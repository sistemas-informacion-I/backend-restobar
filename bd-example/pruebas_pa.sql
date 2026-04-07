--- EJEMPLO DE USO


/* ============================================================
   1) DEMO: sp_crear_comanda
   Tablas afectadas: comanda
   ============================================================ */

BEGIN;
-- Setup mínimo
INSERT INTO sucursal (nombre, direccion, activo)
VALUES ('DEMO_SUC_CREAR', 'Demo dirección crear', TRUE);

-- ANTES
SELECT id_comanda, numero_comanda, id_sucursal, tipo_servicio, estado
FROM comanda;
--WHERE numero_comanda = 'DEMO-CREAR-001';

-- LLAMADA
DO $$
DECLARE
	v_id_sucursal INTEGER;
	v_id_comanda INTEGER;
BEGIN
	SELECT id_sucursal
	INTO v_id_sucursal
	FROM sucursal
	WHERE nombre = 'DEMO_SUC_CREAR'
	ORDER BY id_sucursal DESC
	LIMIT 1;

	CALL sp_crear_comanda(
		p_numero_comanda := 'DEMO-CREAR-001',
		p_id_sucursal := v_id_sucursal,
		p_id_cliente := NULL,
		p_id_empleado := NULL,
		p_numero_personas := 2,
		p_observaciones := 'Demo crear comanda',
		p_tipo_servicio := 'PARA_LLEVAR',
		p_id_mesa := NULL,
		p_id_reserva := NULL,
		p_id_carrito := NULL,
		p_id_comanda := v_id_comanda
	);

	RAISE NOTICE 'sp_crear_comanda -> id_comanda=%', v_id_comanda;
END $$;

-- DESPUÉS
SELECT id_comanda, numero_comanda, id_sucursal, tipo_servicio, numero_personas, estado, observaciones
FROM comanda;
--WHERE numero_comanda = 'DEMO-CREAR-001';

ROLLBACK;


/* ============================================================
   2) DEMO: sp_agregar_detalle_comanda
   Tablas afectadas: detalle_comanda, comanda
   ============================================================ */

BEGIN;
-- Setup mínimo
INSERT INTO sucursal (nombre, direccion, activo)
VALUES ('DEMO_SUC_DETALLE', 'Demo dirección detalle', TRUE);

INSERT INTO producto_final (codigo, nombre, activo)
VALUES ('DEMO-PROD-DET-001', 'Producto Demo Detalle', TRUE);

INSERT INTO comanda (
	numero_comanda, id_sucursal, tipo_servicio, estado, observaciones
) VALUES (
	'DEMO-DETALLE-001',
	(
		SELECT id_sucursal
		FROM sucursal
		WHERE nombre = 'DEMO_SUC_DETALLE'
		ORDER BY id_sucursal DESC
		LIMIT 1
	),
	'PARA_LLEVAR',
	'ABIERTA',
	'Demo para agregar detalle'
);

-- ANTES
SELECT id_comanda, numero_comanda, estado
FROM comanda;
--WHERE numero_comanda = 'DEMO-DETALLE-001';

SELECT id_detalle_comanda, id_comanda, id_producto_final, cantidad, precio_unitario, estado
FROM detalle_comanda
WHERE id_comanda = (SELECT id_comanda FROM comanda WHERE numero_comanda = 'DEMO-DETALLE-001');

-- LLAMADA
DO $$
DECLARE
	v_id_comanda INTEGER;
	v_id_producto_final INTEGER;
BEGIN
	SELECT id_comanda
	INTO v_id_comanda
	FROM comanda
	WHERE numero_comanda = 'DEMO-DETALLE-001'
	ORDER BY id_comanda DESC
	LIMIT 1;

	SELECT id_producto_final
	INTO v_id_producto_final
	FROM producto_final
	WHERE codigo = 'DEMO-PROD-DET-001'
	ORDER BY id_producto_final DESC
	LIMIT 1;

	CALL sp_agregar_detalle_comanda(
		p_id_comanda := v_id_comanda,
		p_id_producto_final := v_id_producto_final,
		p_cantidad := 2,
		p_precio_unitario := 25.50,
		p_notas_especiales := 'Sin cebolla',
		p_estacion_preparacion := 'COCINA'
	);
END $$;

-- DESPUÉS
SELECT id_comanda, numero_comanda, estado
FROM comanda;
--WHERE numero_comanda = 'DEMO-DETALLE-001';

SELECT id_detalle_comanda, id_comanda, id_producto_final, cantidad, precio_unitario, estado, notas_especiales
FROM detalle_comanda
WHERE id_comanda = (SELECT id_comanda FROM comanda WHERE numero_comanda = 'DEMO-DETALLE-001');
ROLLBACK;


/* ============================================================
   3) DEMO: sp_cambiar_estado_detalle_comanda
   Tablas afectadas: detalle_comanda, comanda
   ============================================================ */

BEGIN;
-- Setup mínimo
INSERT INTO sucursal (nombre, direccion, activo)
VALUES ('DEMO_SUC_ESTADO', 'Demo dirección estado', TRUE);

INSERT INTO producto_final (codigo, nombre, activo)
VALUES ('DEMO-PROD-EST-001', 'Producto Demo Estado', TRUE);

INSERT INTO comanda (
	numero_comanda, id_sucursal, tipo_servicio, estado, observaciones
) VALUES (
	'DEMO-ESTADO-001',
	(
		SELECT id_sucursal
		FROM sucursal
		WHERE nombre = 'DEMO_SUC_ESTADO'
		ORDER BY id_sucursal DESC
		LIMIT 1
	),
	'PARA_LLEVAR',
	'EN_PREPARACION',
	'Demo cambio estado detalle'
);

INSERT INTO detalle_comanda (
	id_comanda, id_producto_final, cantidad, precio_unitario, estado, estacion_preparacion, notas_especiales
) VALUES (
	(
		SELECT id_comanda
		FROM comanda
		WHERE numero_comanda = 'DEMO-ESTADO-001'
		ORDER BY id_comanda DESC
		LIMIT 1
	),
	(
		SELECT id_producto_final
		FROM producto_final
		WHERE codigo = 'DEMO-PROD-EST-001'
		ORDER BY id_producto_final DESC
		LIMIT 1
	),
	1,
	30,
	'PENDIENTE',
	'COCINA',
	'DEMO-CAMBIO-ESTADO'
);

-- ANTES
SELECT c.id_comanda, c.numero_comanda, c.estado AS estado_comanda,
       d.id_detalle_comanda, d.estado AS estado_detalle
FROM comanda c
JOIN detalle_comanda d ON d.id_comanda = c.id_comanda;
--WHERE c.numero_comanda = 'DEMO-ESTADO-001';

-- LLAMADA
DO $$
DECLARE
	v_id_detalle_comanda INTEGER;
BEGIN
	SELECT id_detalle_comanda
	INTO v_id_detalle_comanda
	FROM detalle_comanda
	WHERE notas_especiales = 'DEMO-CAMBIO-ESTADO'
	ORDER BY id_detalle_comanda DESC
	LIMIT 1;

	CALL sp_cambiar_estado_detalle_comanda(
		p_id_detalle_comanda := v_id_detalle_comanda,
		p_nuevo_estado := 'EN_PREPARACION'
	);
END $$;

-- DESPUÉS
SELECT c.id_comanda, c.numero_comanda, c.estado AS estado_comanda,
       d.id_detalle_comanda, d.estado AS estado_detalle
FROM comanda c
JOIN detalle_comanda d ON d.id_comanda = c.id_comanda;
--WHERE c.numero_comanda = 'DEMO-ESTADO-001';

ROLLBACK;


/* ============================================================
   4) DEMO: sp_emitir_nota_venta_desde_comanda
   Tablas afectadas: nota_venta, detalle_nota_venta, comanda
   ============================================================ */

BEGIN;
-- Setup mínimo
INSERT INTO sucursal (nombre, direccion, activo)
VALUES ('DEMO_SUC_NOTA', 'Demo dirección nota', TRUE);

INSERT INTO metodo_pago (nombre, activo)
VALUES ('DEMO_MP_NOTA', TRUE);

INSERT INTO producto_final (codigo, nombre, activo)
VALUES ('DEMO-PROD-NOTA-001', 'Producto Demo Nota', TRUE);

INSERT INTO comanda (
	numero_comanda, id_sucursal, tipo_servicio, estado, observaciones
) VALUES (
	'DEMO-NOTA-001',
	(
		SELECT id_sucursal
		FROM sucursal
		WHERE nombre = 'DEMO_SUC_NOTA'
		ORDER BY id_sucursal DESC
		LIMIT 1
	),
	'PARA_LLEVAR',
	'EN_PREPARACION',
	'Demo emitir nota'
);

INSERT INTO detalle_comanda (
	id_comanda, id_producto_final, cantidad, precio_unitario, estado, estacion_preparacion, notas_especiales
) VALUES (
	(
		SELECT id_comanda
		FROM comanda
		WHERE numero_comanda = 'DEMO-NOTA-001'
		ORDER BY id_comanda DESC
		LIMIT 1
	),
	(
		SELECT id_producto_final
		FROM producto_final
		WHERE codigo = 'DEMO-PROD-NOTA-001'
		ORDER BY id_producto_final DESC
		LIMIT 1
	),
	2,
	40,
	'EN_PREPARACION',
	'COCINA',
	'DETALLE PARA NOTA'
);

-- ANTES
SELECT id_comanda, numero_comanda, estado, fecha_cierre
FROM comanda;
--WHERE numero_comanda = 'DEMO-NOTA-001';

SELECT id_nota_venta, id_comanda, total, estado
FROM nota_venta
WHERE id_comanda = (SELECT id_comanda FROM comanda WHERE numero_comanda = 'DEMO-NOTA-001');

-- LLAMADA
DO $$
DECLARE
	v_id_comanda INTEGER;
	v_id_metodo_pago INTEGER;
	v_id_nota_venta INTEGER;
BEGIN
	SELECT id_comanda
	INTO v_id_comanda
	FROM comanda
	WHERE numero_comanda = 'DEMO-NOTA-001'
	ORDER BY id_comanda DESC
	LIMIT 1;

	SELECT id_metodo_pago
	INTO v_id_metodo_pago
	FROM metodo_pago
	WHERE nombre = 'DEMO_MP_NOTA'
	ORDER BY id_metodo_pago DESC
	LIMIT 1;

	CALL sp_emitir_nota_venta_desde_comanda(
		p_id_comanda := v_id_comanda,
		p_id_metodo_pago := v_id_metodo_pago,
		p_id_empleado := NULL,
		p_descuento := 5,
		p_impuesto := 2,
		p_propina := 3,
		p_observaciones := 'Demo emisión nota',
		p_id_nota_venta := v_id_nota_venta
	);

	RAISE NOTICE 'sp_emitir_nota_venta_desde_comanda -> id_nota_venta=%', v_id_nota_venta;
END $$;

-- DESPUÉS
SELECT id_comanda, numero_comanda, estado, fecha_cierre
FROM comanda;
--WHERE numero_comanda = 'DEMO-NOTA-001';

SELECT id_nota_venta, id_comanda, subtotal, descuento, impuesto, propina, total, estado
FROM nota_venta
WHERE id_comanda = (SELECT id_comanda FROM comanda WHERE numero_comanda = 'DEMO-NOTA-001');

SELECT dnv.id_detalle_nota_venta, dnv.id_nota_venta, dnv.id_producto_final, dnv.cantidad, dnv.precio_unitario, dnv.subtotal
FROM detalle_nota_venta dnv
JOIN nota_venta nv ON nv.id_nota_venta = dnv.id_nota_venta
WHERE nv.id_comanda = (SELECT id_comanda FROM comanda WHERE numero_comanda = 'DEMO-NOTA-001');

ROLLBACK;