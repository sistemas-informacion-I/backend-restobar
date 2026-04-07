BEGIN;
-- Simular 5 intentos fallidos al usuario 1
UPDATE usuario
SET intentos_fallidos = 5
WHERE id_usuario = 1;

-- Verificar que se bloqueó
SELECT id_usuario, nombre, intentos_fallidos, estado_acceso
FROM usuario
WHERE id_usuario = 1;
-- Debe mostrar: estado_acceso = 'BLOQUEADO'

-- Desbloquear manualmente y verificar que se resetean los intentos
UPDATE usuario
SET estado_acceso = 'HABILITADO'
WHERE id_usuario = 1;

SELECT id_usuario, nombre, intentos_fallidos, estado_acceso
FROM usuario
WHERE id_usuario = 1;
-- Debe mostrar: intentos_fallidos = 0, estado_acceso = 'HABILITADO'
ROLLBACK;




BEGIN;
-- Ver stock actual de Papa (id_stock = 1) antes
SELECT id_stock, cantidad
FROM stock_sucursal
WHERE id_stock = 1;

-- Insertar un lote nuevo de Papa
INSERT INTO lote_inventario 
    (id_stock, numero_lote, cantidad, fecha_ingreso, precio_compra, estado)
VALUES 
    (1, 'LOT-TEST-001', 20.000, CURRENT_DATE, 7.50, 'DISPONIBLE');

-- Ver stock después — debe haber sumado 20
SELECT *
FROM lote_inventario
WHERE id_stock = 1;
ROLLBACK;



--UPDATE INTO stock_sucursal
--SET cantidad = 50.000
--WHERE id_stock = 1;




BEGIN;
-- Ver costo actual de la receta 1 (Empanadas Colombianas)
SELECT id_receta, nombre, costo_total
FROM receta
WHERE id_receta = 1;

-- Agregar un ingrediente nuevo a la receta
INSERT INTO ingrediente_receta 
    (id_receta, id_inventario, cantidad, unidad_medida, notas)
VALUES 
    (1, 9, 0.050, 'KG', 'Limón para decorar');

-- Ver si el costo se recalculó automáticamente
SELECT id_receta, nombre, costo_total
FROM receta
WHERE id_receta = 1;
-- El costo debe haber cambiado
ROLLBACK;




BEGIN;
-- Ver subtotal y total actual de la nota de venta 1
SELECT id_nota_venta, subtotal, descuento, impuesto, propina, total
FROM nota_venta
WHERE id_nota_venta = 1;

-- Agregar un detalle nuevo a esa nota
INSERT INTO detalle_nota_venta 
    (id_nota_venta, id_producto_final, cantidad, precio_unitario,
     costo_unitario, descuento, subtotal, descripcion)
VALUES 
    (1, 4, 1, 15.00, 10.00, 0.00, 15.00, 'Papas fritas extra');

-- Ver si el subtotal y total se actualizaron solos
SELECT id_nota_venta, subtotal, descuento, impuesto, propina, total
FROM nota_venta
WHERE id_nota_venta = 1;
-- El subtotal y total deben haber aumentado
ROLLBACK;




BEGIN;
-- Ver movimientos actuales en caja 1
SELECT id_movimiento, concepto, tipo, monto, observaciones
FROM movimiento_caja
WHERE id_caja = 1;

-- Cambiar la nota de venta 1 a PAGADA
UPDATE nota_venta
SET estado = 'PAGADA',
    fecha_pago = CURRENT_TIMESTAMP
WHERE id_nota_venta = 1;

-- Ver si se registró automáticamente el movimiento
SELECT id_movimiento, concepto, tipo, monto, observaciones
FROM movimiento_caja
WHERE id_caja = 1;
-- Debe aparecer un nuevo registro con concepto = 'VENTA'
ROLLBACK;




BEGIN;
-- Ver disponibilidad actual de la mesa 1
SELECT id_mesa, numero_mesa, disponibilidad
FROM mesa
WHERE id_mesa = 1;

-- Insertar una comanda nueva tipo MESA usando la reserva 1
-- (reserva 1 tiene asignada la mesa 12 en reserva_mesa)
INSERT INTO comanda 
    (numero_comanda, id_sucursal, id_cliente, id_empleado,
     id_reserva, tipo_servicio, estado)
VALUES 
    ('COM-TEST-01', 1, 1, 25, 1, 'MESA', 'ABIERTA');

-- Ver si la mesa 12 cambió a OCUPADA
SELECT id_mesa, numero_mesa, disponibilidad
FROM mesa
WHERE id_mesa = 12;
-- Debe mostrar: disponibilidad = 'OCUPADA'

-- Cerrar la comanda y ver si la mesa vuelve a DISPONIBLE
UPDATE comanda
SET estado = 'CERRADA'
WHERE numero_comanda = 'COM-TEST-01';

SELECT id_mesa, numero_mesa, disponibilidad
FROM mesa
WHERE id_mesa = 12;
-- Debe mostrar: disponibilidad = 'DISPONIBLE'
ROLLBACK;