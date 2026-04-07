-- Procedimiento: sp_crear_comanda
-- - Crea la cabecera de comanda en tabla comanda.
-- - Valida coherencia de tipo de servicio (MESA, PARA_LLEVAR, ONLINE).
-- - Nota: p_id_mesa se mantiene por compatibilidad, pero no se persiste en comanda.
CREATE OR REPLACE PROCEDURE sp_crear_comanda(
	IN p_numero_comanda VARCHAR(50),
	IN p_id_sucursal INTEGER,
	IN p_id_cliente INTEGER,
	IN p_id_empleado INTEGER,
	IN p_numero_personas INTEGER,
	IN p_observaciones TEXT,
	IN p_tipo_servicio VARCHAR(20),
	IN p_id_mesa INTEGER DEFAULT NULL,
	IN p_id_reserva INTEGER DEFAULT NULL,
	IN p_id_carrito INTEGER DEFAULT NULL,
	INOUT p_id_comanda INTEGER DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
	v_existe BOOLEAN;
BEGIN
	IF p_numero_comanda IS NULL OR LENGTH(TRIM(p_numero_comanda)) = 0 THEN
		RAISE EXCEPTION 'El número de comanda es obligatorio';
	END IF;

	SELECT EXISTS(
		SELECT 1 FROM sucursal WHERE id_sucursal = p_id_sucursal AND activo = TRUE
	) INTO v_existe;
	IF NOT v_existe THEN
		RAISE EXCEPTION 'Sucursal % no existe o está inactiva', p_id_sucursal;
	END IF;

	IF p_id_cliente IS NOT NULL THEN
		SELECT EXISTS(SELECT 1 FROM cliente WHERE id_cliente = p_id_cliente)
		INTO v_existe;
		IF NOT v_existe THEN
			RAISE EXCEPTION 'Cliente % no existe', p_id_cliente;
		END IF;
	END IF;

	IF p_id_empleado IS NOT NULL THEN
		SELECT EXISTS(SELECT 1 FROM empleado WHERE id_empleado = p_id_empleado)
		INTO v_existe;
		IF NOT v_existe THEN
			RAISE EXCEPTION 'Empleado % no existe', p_id_empleado;
		END IF;
	END IF;

	IF p_numero_personas IS NOT NULL AND p_numero_personas <= 0 THEN
		RAISE EXCEPTION 'El número de personas debe ser mayor a 0';
	END IF;

	IF p_tipo_servicio NOT IN ('MESA', 'PARA_LLEVAR', 'ONLINE') THEN
		RAISE EXCEPTION 'Tipo de servicio inválido: %', p_tipo_servicio;
	END IF;

	IF p_tipo_servicio = 'MESA' THEN
		IF p_id_reserva IS NULL THEN
			RAISE EXCEPTION 'Servicio MESA requiere reserva';
		END IF;
		IF p_id_carrito IS NOT NULL THEN
			RAISE EXCEPTION 'Servicio MESA no debe tener carrito';
		END IF;
	ELSIF p_tipo_servicio = 'PARA_LLEVAR' THEN
		IF p_id_reserva IS NOT NULL OR p_id_carrito IS NOT NULL THEN
			RAISE EXCEPTION 'Servicio PARA_LLEVAR no debe tener reserva ni carrito';
		END IF;
	ELSIF p_tipo_servicio = 'ONLINE' THEN
		IF p_id_carrito IS NULL THEN
			RAISE EXCEPTION 'Servicio ONLINE requiere carrito';
		END IF;
		IF p_id_reserva IS NOT NULL THEN
			RAISE EXCEPTION 'Servicio ONLINE no debe tener reserva';
		END IF;
	END IF;

	IF p_id_reserva IS NOT NULL THEN
		SELECT EXISTS(SELECT 1 FROM reserva WHERE id_reserva = p_id_reserva)
		INTO v_existe;
		IF NOT v_existe THEN
			RAISE EXCEPTION 'Reserva % no existe', p_id_reserva;
		END IF;
	END IF;

	IF p_id_carrito IS NOT NULL THEN
		SELECT EXISTS(SELECT 1 FROM carrito_compras WHERE id_carrito = p_id_carrito)
		INTO v_existe;
		IF NOT v_existe THEN
			RAISE EXCEPTION 'Carrito % no existe', p_id_carrito;
		END IF;
	END IF;

	IF p_id_mesa IS NOT NULL THEN
		SELECT EXISTS(SELECT 1 FROM mesa WHERE id_mesa = p_id_mesa)
		INTO v_existe;
		IF NOT v_existe THEN
			RAISE EXCEPTION 'Mesa % no existe', p_id_mesa;
		END IF;
	END IF;

	INSERT INTO comanda (
		numero_comanda,
		id_sucursal,
		id_cliente,
		id_empleado,
		id_reserva,
		id_carrito,
		tipo_servicio,
		numero_personas,
		observaciones,
		estado
	) VALUES (
		p_numero_comanda,
		p_id_sucursal,
		p_id_cliente,
		p_id_empleado,
		p_id_reserva,
		p_id_carrito,
		p_tipo_servicio,
		p_numero_personas,
		p_observaciones,
		'ABIERTA'
	)
	RETURNING id_comanda INTO p_id_comanda;
END;
$$;


-- Procedimiento: sp_agregar_detalle_comanda
-- - Agrega ítems a una comanda abierta/en preparación/lista.
-- - Evita agregar en comandas cerradas, canceladas o entregadas.
CREATE OR REPLACE PROCEDURE sp_agregar_detalle_comanda(
	IN p_id_comanda INTEGER,
	IN p_id_producto_final INTEGER,
	IN p_cantidad INTEGER,
	IN p_precio_unitario NUMERIC(10,2),
	IN p_notas_especiales TEXT,
	IN p_estacion_preparacion VARCHAR(30)
)
LANGUAGE plpgsql
AS $$
DECLARE
	v_estado_comanda VARCHAR(30);
	v_producto_activo BOOLEAN;
BEGIN
	IF p_cantidad <= 0 THEN
		RAISE EXCEPTION 'La cantidad debe ser mayor a 0';
	END IF;

	IF p_precio_unitario < 0 THEN
		RAISE EXCEPTION 'El precio unitario no puede ser negativo';
	END IF;

	IF p_estacion_preparacion IS NOT NULL
	   AND p_estacion_preparacion NOT IN ('COCINA', 'BARRA') THEN
		RAISE EXCEPTION 'Estación de preparación inválida: %', p_estacion_preparacion;
	END IF;

	SELECT estado
	INTO v_estado_comanda
	FROM comanda
	WHERE id_comanda = p_id_comanda
	FOR UPDATE;

	IF NOT FOUND THEN
		RAISE EXCEPTION 'Comanda % no existe', p_id_comanda;
	END IF;

	IF v_estado_comanda IN ('CERRADA', 'CANCELADA', 'ENTREGADA') THEN
		RAISE EXCEPTION 'No se pueden agregar ítems a una comanda en estado %', v_estado_comanda;
	END IF;

	SELECT EXISTS(
		SELECT 1
		FROM producto_final
		WHERE id_producto_final = p_id_producto_final
		  AND activo = TRUE
	) INTO v_producto_activo;

	IF NOT v_producto_activo THEN
		RAISE EXCEPTION 'Producto % no existe o no está activo', p_id_producto_final;
	END IF;

	INSERT INTO detalle_comanda (
		id_comanda,
		id_producto_final,
		cantidad,
		precio_unitario,
		notas_especiales,
		estado,
		estacion_preparacion
	) VALUES (
		p_id_comanda,
		p_id_producto_final,
		p_cantidad,
		p_precio_unitario,
		p_notas_especiales,
		'PENDIENTE',
		p_estacion_preparacion
	);

	UPDATE comanda
	SET estado = CASE
		WHEN estado = 'ABIERTA' THEN 'EN_PREPARACION'
		ELSE estado
	END
	WHERE id_comanda = p_id_comanda;
END;
$$;


-- Procedimiento: sp_cambiar_estado_detalle_comanda
-- - Cambia estado de un detalle de comanda con validación de transición.
-- - Recalcula estado general de la comanda.
CREATE OR REPLACE PROCEDURE sp_cambiar_estado_detalle_comanda(
	IN p_id_detalle_comanda INTEGER,
	IN p_nuevo_estado VARCHAR(30)
)
LANGUAGE plpgsql
AS $$
DECLARE
	v_estado_actual VARCHAR(30);
	v_id_comanda INTEGER;
	v_cnt_total INTEGER;
	v_cnt_cancelado INTEGER;
	v_cnt_entregado INTEGER;
	v_cnt_listo INTEGER;
	v_cnt_preparacion INTEGER;
BEGIN
	IF p_nuevo_estado NOT IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO') THEN
		RAISE EXCEPTION 'Estado de detalle inválido: %', p_nuevo_estado;
	END IF;

	SELECT estado, id_comanda
	INTO v_estado_actual, v_id_comanda
	FROM detalle_comanda
	WHERE id_detalle_comanda = p_id_detalle_comanda
	FOR UPDATE;

	IF NOT FOUND THEN
		RAISE EXCEPTION 'Detalle de comanda % no existe', p_id_detalle_comanda;
	END IF;

	IF v_estado_actual = p_nuevo_estado THEN
		RETURN;
	END IF;

	IF v_estado_actual = 'PENDIENTE' AND p_nuevo_estado NOT IN ('EN_PREPARACION', 'CANCELADO') THEN
		RAISE EXCEPTION 'Transición inválida: % -> %', v_estado_actual, p_nuevo_estado;
	ELSIF v_estado_actual = 'EN_PREPARACION' AND p_nuevo_estado NOT IN ('LISTO', 'CANCELADO') THEN
		RAISE EXCEPTION 'Transición inválida: % -> %', v_estado_actual, p_nuevo_estado;
	ELSIF v_estado_actual = 'LISTO' AND p_nuevo_estado NOT IN ('ENTREGADO') THEN
		RAISE EXCEPTION 'Transición inválida: % -> %', v_estado_actual, p_nuevo_estado;
	ELSIF v_estado_actual IN ('ENTREGADO', 'CANCELADO') THEN
		RAISE EXCEPTION 'No se puede cambiar un detalle en estado terminal (%)', v_estado_actual;
	END IF;

	UPDATE detalle_comanda
	SET estado = p_nuevo_estado
	WHERE id_detalle_comanda = p_id_detalle_comanda;

	SELECT
		COUNT(*),
		COUNT(*) FILTER (WHERE estado = 'CANCELADO'),
		COUNT(*) FILTER (WHERE estado = 'ENTREGADO'),
		COUNT(*) FILTER (WHERE estado = 'LISTO'),
		COUNT(*) FILTER (WHERE estado = 'EN_PREPARACION')
	INTO v_cnt_total, v_cnt_cancelado, v_cnt_entregado, v_cnt_listo, v_cnt_preparacion
	FROM detalle_comanda
	WHERE id_comanda = v_id_comanda;

	IF v_cnt_total = v_cnt_cancelado THEN
		UPDATE comanda
		SET estado = 'CANCELADA'
		WHERE id_comanda = v_id_comanda;
	ELSIF v_cnt_total = (v_cnt_entregado + v_cnt_cancelado) THEN
		UPDATE comanda
		SET estado = 'ENTREGADA'
		WHERE id_comanda = v_id_comanda;
	ELSIF v_cnt_preparacion > 0 THEN
		UPDATE comanda
		SET estado = 'EN_PREPARACION'
		WHERE id_comanda = v_id_comanda;
	ELSIF v_cnt_listo > 0 THEN
		UPDATE comanda
		SET estado = 'LISTA'
		WHERE id_comanda = v_id_comanda;
	ELSE
		UPDATE comanda
		SET estado = 'ABIERTA'
		WHERE id_comanda = v_id_comanda
		  AND estado NOT IN ('CERRADA', 'CANCELADA', 'ENTREGADA');
	END IF;
END;
$$;


-- Procedimiento: sp_emitir_nota_venta_desde_comanda
-- - Convierte una comanda en nota de venta (sin facturación fiscal).
-- - Calcula subtotal desde detalle_comanda no cancelado.
-- - Copia el detalle hacia detalle_nota_venta.
-- - Cierra la comanda al emitir la nota.
CREATE OR REPLACE PROCEDURE sp_emitir_nota_venta_desde_comanda(
	IN p_id_comanda INTEGER,
	IN p_id_metodo_pago INTEGER,
	IN p_id_empleado INTEGER,
	IN p_descuento NUMERIC(10,2),
	IN p_impuesto NUMERIC(10,2),
	IN p_propina NUMERIC(10,2),
	IN p_observaciones TEXT,
	INOUT p_id_nota_venta INTEGER DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
	v_id_sucursal INTEGER;
	v_id_cliente INTEGER;
	v_subtotal NUMERIC(10,2);
	v_total NUMERIC(10,2);
	v_estado_comanda VARCHAR(30);
	v_existe BOOLEAN;
BEGIN
	IF COALESCE(p_descuento, 0) < 0 OR COALESCE(p_impuesto, 0) < 0 OR COALESCE(p_propina, 0) < 0 THEN
		RAISE EXCEPTION 'Descuento, impuesto y propina no pueden ser negativos';
	END IF;

	IF p_id_metodo_pago IS NULL THEN
		RAISE EXCEPTION 'Debe indicar método de pago';
	END IF;

	SELECT EXISTS(
		SELECT 1 FROM metodo_pago WHERE id_metodo_pago = p_id_metodo_pago AND activo = TRUE
	) INTO v_existe;
	IF NOT v_existe THEN
		RAISE EXCEPTION 'Método de pago % no existe o está inactivo', p_id_metodo_pago;
	END IF;

	IF p_id_empleado IS NOT NULL THEN
		SELECT EXISTS(SELECT 1 FROM empleado WHERE id_empleado = p_id_empleado)
		INTO v_existe;
		IF NOT v_existe THEN
			RAISE EXCEPTION 'Empleado % no existe', p_id_empleado;
		END IF;
	END IF;

	SELECT id_sucursal, id_cliente, estado
	INTO v_id_sucursal, v_id_cliente, v_estado_comanda
	FROM comanda
	WHERE id_comanda = p_id_comanda
	FOR UPDATE;

	IF NOT FOUND THEN
		RAISE EXCEPTION 'Comanda % no existe', p_id_comanda;
	END IF;

	IF v_estado_comanda IN ('CERRADA', 'CANCELADA') THEN
		RAISE EXCEPTION 'No se puede emitir nota para comanda en estado %', v_estado_comanda;
	END IF;

	SELECT COALESCE(SUM(cantidad * precio_unitario), 0)
	INTO v_subtotal
	FROM detalle_comanda
	WHERE id_comanda = p_id_comanda
	  AND estado <> 'CANCELADO';

	IF v_subtotal <= 0 THEN
		RAISE EXCEPTION 'La comanda % no tiene detalle válido para emitir nota', p_id_comanda;
	END IF;

	IF COALESCE(p_descuento, 0) > v_subtotal THEN
		RAISE EXCEPTION 'El descuento no puede ser mayor al subtotal';
	END IF;

	v_total := v_subtotal - COALESCE(p_descuento, 0) + COALESCE(p_impuesto, 0) + COALESCE(p_propina, 0);

	IF v_total <= 0 THEN
		RAISE EXCEPTION 'El total de la nota de venta debe ser mayor a 0';
	END IF;

	INSERT INTO nota_venta (
		id_comanda,
		id_sucursal,
		id_cliente,
		id_empleado,
		id_metodo_pago,
		subtotal,
		descuento,
		impuesto,
		propina,
		total,
		estado,
		observaciones
	) VALUES (
		p_id_comanda,
		v_id_sucursal,
		v_id_cliente,
		p_id_empleado,
		p_id_metodo_pago,
		v_subtotal,
		COALESCE(p_descuento, 0),
		COALESCE(p_impuesto, 0),
		COALESCE(p_propina, 0),
		v_total,
		'EMITIDA',
		p_observaciones
	)
	RETURNING id_nota_venta INTO p_id_nota_venta;

	INSERT INTO detalle_nota_venta (
		id_nota_venta,
		id_producto_final,
		cantidad,
		precio_unitario,
		costo_unitario,
		descuento,
		subtotal,
		descripcion
	)
	SELECT
		p_id_nota_venta,
		dc.id_producto_final,
		dc.cantidad,
		dc.precio_unitario,
		dc.precio_unitario,
		0,
		(dc.cantidad * dc.precio_unitario),
		dc.notas_especiales
	FROM detalle_comanda dc
	WHERE dc.id_comanda = p_id_comanda
	  AND dc.estado <> 'CANCELADO';

	UPDATE comanda
	SET estado = 'CERRADA',
		fecha_cierre = CURRENT_TIMESTAMP
	WHERE id_comanda = p_id_comanda;
END;
$$;