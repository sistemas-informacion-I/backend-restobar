-- ============================================================
-- MÓDULO: ACCESO
-- ============================================================

-- TRIGGER 1: trg_bloquear_cuenta
-- Tabla: usuario (UPDATE)
-- Si los intentos fallidos llegan a 5, bloquea la cuenta
-- automáticamente cambiando estado_acceso a BLOQUEADO.
-- Si se resetean los intentos, vuelve a HABILITADO.

CREATE OR REPLACE FUNCTION fn_bloquear_cuenta()
RETURNS TRIGGER AS $$
BEGIN
   
    IF NEW.intentos_fallidos >= 5 AND OLD.estado_acceso <> 'BLOQUEADO' THEN
        NEW.estado_acceso := 'BLOQUEADO';
    END IF;

    IF NEW.estado_acceso = 'HABILITADO' AND OLD.estado_acceso = 'BLOQUEADO' THEN
        NEW.intentos_fallidos := 0;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bloquear_cuenta
BEFORE UPDATE ON usuario
FOR EACH ROW
EXECUTE FUNCTION fn_bloquear_cuenta();

-- ============================================================
-- MÓDULO: INVENTARIO
-- ============================================================

-- TRIGGER 2: trg_actualizar_stock_por_lote
-- Tabla: lote_inventario (INSERT, UPDATE)
-- Cuando se agrega un lote nuevo, suma la cantidad al stock.
-- Cuando se modifica un lote, ajusta la diferencia.

CREATE OR REPLACE FUNCTION fn_actualizar_stock_por_lote()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE stock_sucursal
        SET cantidad = cantidad + NEW.cantidad
        WHERE id_stock = NEW.id_stock;

    ELSIF TG_OP = 'UPDATE' THEN
        UPDATE stock_sucursal
        SET cantidad = cantidad + (NEW.cantidad - OLD.cantidad)
        WHERE id_stock = NEW.id_stock;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_actualizar_stock_por_lote
AFTER INSERT OR UPDATE ON lote_inventario
FOR EACH ROW
EXECUTE FUNCTION fn_actualizar_stock_por_lote();

-- TRIGGER 3: trg_recalcular_costo_receta
-- Tabla: ingrediente_receta (INSERT, UPDATE, DELETE)
-- Cada vez que se agrega, modifica o elimina un ingrediente,
-- recalcula el costo total de la receta sumando
-- cantidad * precio_unitario de cada ingrediente en stock.

CREATE OR REPLACE FUNCTION fn_recalcular_costo_receta()
RETURNS TRIGGER AS $$
DECLARE
    v_id_receta   INTEGER;
    v_costo_total NUMERIC(10,2);
BEGIN
    v_id_receta := CASE WHEN TG_OP = 'DELETE' 
                   THEN OLD.id_receta 
                   ELSE NEW.id_receta END;

    SELECT COALESCE(SUM(ir.cantidad * ss.precio_unitario), 0)
    INTO v_costo_total
    FROM ingrediente_receta ir
    JOIN stock_sucursal ss ON ss.id_inventario = ir.id_inventario
    WHERE ir.id_receta = v_id_receta
      AND ss.id_sucursal = 1;

    UPDATE receta
    SET costo_total = v_costo_total
    WHERE id_receta = v_id_receta;

    RETURN CASE WHEN TG_OP = 'DELETE' THEN OLD ELSE NEW END;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_recalcular_costo_receta
AFTER INSERT OR UPDATE OR DELETE ON ingrediente_receta
FOR EACH ROW
EXECUTE FUNCTION fn_recalcular_costo_receta();


-- ============================================================
-- MÓDULO: COMERCIAL
-- ============================================================

-- TRIGGER 4: trg_recalcular_total_nota_venta
-- Tabla: detalle_nota_venta (INSERT, UPDATE, DELETE)
-- Cada vez que se agrega, modifica o elimina un detalle,
-- recalcula el subtotal y total de la nota de venta

CREATE OR REPLACE FUNCTION fn_recalcular_total_nota_venta()
RETURNS TRIGGER AS $$
DECLARE
    v_id_nota_venta INTEGER;
    v_subtotal      NUMERIC(10,2);
    v_descuento     NUMERIC(10,2);
    v_impuesto      NUMERIC(10,2);
    v_propina       NUMERIC(10,2);
BEGIN
    v_id_nota_venta := CASE WHEN TG_OP = 'DELETE' 
                       THEN OLD.id_nota_venta 
                       ELSE NEW.id_nota_venta END;

    SELECT COALESCE(SUM(subtotal), 0)
    INTO v_subtotal
    FROM detalle_nota_venta
    WHERE id_nota_venta = v_id_nota_venta;

    SELECT descuento, impuesto, propina
    INTO v_descuento, v_impuesto, v_propina
    FROM nota_venta
    WHERE id_nota_venta = v_id_nota_venta;

    UPDATE nota_venta
    SET subtotal = v_subtotal,
        total    = v_subtotal - v_descuento + v_impuesto + v_propina
    WHERE id_nota_venta = v_id_nota_venta;

    RETURN CASE WHEN TG_OP = 'DELETE' THEN OLD ELSE NEW END;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_recalcular_total_nota_venta
AFTER INSERT OR UPDATE OR DELETE ON detalle_nota_venta
FOR EACH ROW
EXECUTE FUNCTION fn_recalcular_total_nota_venta();

-- TRIGGER 5: trg_movimiento_caja_por_venta
-- Tabla: nota_venta (INSERT, UPDATE)
-- Cuando una nota de venta cambia a PAGADA, registra
-- un INGRESO en movimiento_caja buscando la caja
-- abierta de esa sucursal.

CREATE OR REPLACE FUNCTION fn_movimiento_caja_por_venta()
RETURNS TRIGGER AS $$
DECLARE
    v_id_caja INTEGER;
BEGIN
    IF NEW.estado = 'PAGADA' AND 
       (TG_OP = 'INSERT' OR OLD.estado <> 'PAGADA') THEN
        SELECT id_caja INTO v_id_caja
        FROM caja
        WHERE id_sucursal = NEW.id_sucursal
          AND estado = 'ABIERTA'
        LIMIT 1;

        IF v_id_caja IS NOT NULL THEN
            INSERT INTO movimiento_caja (
                id_caja,
                id_nota_venta,
                tipo,
                concepto,
                monto,
                fecha,
                observaciones
            ) VALUES (
                v_id_caja,
                NEW.id_nota_venta,
                'INGRESO',
                'VENTA',
                NEW.total,
                CURRENT_TIMESTAMP,
                'Venta registrada automáticamente'
            );
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_movimiento_caja_por_venta
AFTER INSERT OR UPDATE ON nota_venta
FOR EACH ROW
EXECUTE FUNCTION fn_movimiento_caja_por_venta();


-- ============================================================
-- MÓDULO: OPERACIONES
-- ============================================================

-- TRIGGER 6: trg_mesa_ocupada_por_comanda
-- Tabla: comanda (INSERT, UPDATE)
-- Cuando se abre una comanda tipo MESA, busca la mesa
-- en reserva_mesa y la marca como OCUPADA.
-- Cuando se cierra o cancela, la libera a DISPONIBLE.

CREATE OR REPLACE FUNCTION fn_mesa_ocupada_por_comanda()
RETURNS TRIGGER AS $$
DECLARE
    v_id_mesa INTEGER;
BEGIN
    IF NEW.tipo_servicio = 'MESA' THEN

        SELECT rm.id_mesa INTO v_id_mesa
        FROM reserva_mesa rm
        WHERE rm.id_reserva = NEW.id_reserva
        LIMIT 1;

        IF v_id_mesa IS NOT NULL THEN
            IF NEW.estado IN ('ABIERTA', 'EN_PREPARACION', 
                              'LISTA', 'ENTREGADA') THEN
                UPDATE mesa
                SET disponibilidad = 'OCUPADA'
                WHERE id_mesa = v_id_mesa;
				
            ELSIF NEW.estado IN ('CERRADA', 'CANCELADA') THEN
                UPDATE mesa
                SET disponibilidad = 'DISPONIBLE'
                WHERE id_mesa = v_id_mesa;
            END IF;

        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_mesa_ocupada_por_comanda
AFTER INSERT OR UPDATE ON comanda
FOR EACH ROW
EXECUTE FUNCTION fn_mesa_ocupada_por_comanda();