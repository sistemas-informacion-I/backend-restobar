-- ============================================================
-- MIGRACION CU30 – Gestionar Entregas
-- Agrega modulo ENTREGAS, rol REPARTIDOR y permisos asociados.
-- Ejecutar contra la BD existente (no usa DROP/CREATE TABLE).
-- ============================================================

BEGIN;

-- 1. Modulo de permisos ENTREGAS (create, read, update)
INSERT INTO permiso (nombre, modulo, accion, descripcion, activo, fecha_creacion)
VALUES
    ('entregas:create', 'ENTREGAS', 'CREAR', 'CREAR en el modulo de ENTREGAS', TRUE, CURRENT_TIMESTAMP),
    ('entregas:read',   'ENTREGAS', 'LEER',  'LEER en el modulo de ENTREGAS',  TRUE, CURRENT_TIMESTAMP),
    ('entregas:update', 'ENTREGAS', 'ACTUALIZAR', 'ACTUALIZAR en el modulo de ENTREGAS', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- 2. Rol REPARTIDOR
INSERT INTO rol (nombre, descripcion, nivel_acceso, activo, fecha_creacion)
VALUES ('REPARTIDOR', 'Personal de reparto y entregas a domicilio', 5, TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- 3. Permisos del REPARTIDOR
INSERT INTO rol_permiso (id_rol, id_permiso, fecha_asignacion, activo)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP, TRUE
FROM rol r
JOIN permiso p ON p.nombre IN (
    'entregas:read',
    'entregas:update',
    'branches:read',
    'clients:read',
    'orders:read:kitchen',
    'orders:read:bar',
    'products:read',
    'inventory:read'
)
WHERE r.nombre = 'REPARTIDOR'
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso
);

-- 4. Permisos ENTREGAS para ADMIN
INSERT INTO rol_permiso (id_rol, id_permiso, fecha_asignacion, activo)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP, TRUE
FROM rol r
JOIN permiso p ON p.nombre IN (
    'entregas:create',
    'entregas:read',
    'entregas:update'
)
WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso
);

-- 5. Permisos ENTREGAS para SUPERUSER
INSERT INTO rol_permiso (id_rol, id_permiso, fecha_asignacion, activo)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP, TRUE
FROM rol r
JOIN permiso p ON p.nombre IN (
    'entregas:create',
    'entregas:read',
    'entregas:update'
)
WHERE r.nombre = 'SUPERUSER'
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso
);

-- 6. Tablas IF NOT EXISTS
CREATE TABLE IF NOT EXISTS ubicacion_empleado (
    id_ubicacion SERIAL PRIMARY KEY,
    id_empleado INTEGER NOT NULL,
    latitud NUMERIC(10,8) NOT NULL,
    longitud NUMERIC(11,8) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS entrega (
    id_entrega SERIAL PRIMARY KEY,
    id_comanda INTEGER NOT NULL UNIQUE,
    id_empleado INTEGER,
    direccion_entrega TEXT NOT NULL,
    latitud NUMERIC(10,8) NOT NULL,
    longitud NUMERIC(11,8) NOT NULL,
    latitud_actual NUMERIC(10,8),
    longitud_actual NUMERIC(11,8),
    distancia_km NUMERIC(10,2),
    tiempo_estimado_min INTEGER,
    costo_envio NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (costo_envio >= 0),
    estado VARCHAR(30) DEFAULT 'PENDIENTE' NOT NULL CHECK (estado IN ('PENDIENTE', 'ASIGNADO', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO')),
    fecha_asignacion TIMESTAMP,
    fecha_entrega TIMESTAMP,
    observaciones TEXT,
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda),
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado)
);

-- 7. Columnas latitud/longitud en sucursal
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'sucursal' AND column_name = 'latitud'
    ) THEN
        ALTER TABLE sucursal ADD COLUMN latitud NUMERIC(10,8);
        ALTER TABLE sucursal ADD COLUMN longitud NUMERIC(11,8);
        ALTER TABLE sucursal ADD COLUMN ubicacion VARCHAR(100);
    END IF;
END $$;

-- 8. Empleado REPARTIDOR de prueba (id_usuario = 12 → MES1-AM)
INSERT INTO rol_usuario (id_usuario, id_rol, fecha_asignacion, activo)
SELECT 12, r.id_rol, CURRENT_TIMESTAMP, TRUE
FROM rol r
WHERE r.nombre = 'REPARTIDOR'
AND NOT EXISTS (
    SELECT 1 FROM rol_usuario ru
    WHERE ru.id_usuario = 12 AND ru.id_rol = r.id_rol
);

-- 9. Entregas de prueba (requiere que existan comanda id 1 e id 2)
INSERT INTO entrega (id_comanda, id_empleado, direccion_entrega, latitud, longitud, latitud_actual, longitud_actual, distancia_km, tiempo_estimado_min, costo_envio, estado, observaciones)
SELECT
    c.id_comanda, 12,
    'Av. San Martín #1234, Zona Sur, Santa Cruz',
    -17.79280000, -63.17420000,
    -17.78332700, -63.18214040,
    1.20, 8, 10.00,
    'PENDIENTE',
    'Entrega de prueba — pedido familiar'
FROM comanda c
WHERE c.id_comanda = 1
AND NOT EXISTS (SELECT 1 FROM entrega WHERE id_comanda = c.id_comanda);

INSERT INTO entrega (id_comanda, id_empleado, direccion_entrega, latitud, longitud, latitud_actual, longitud_actual, distancia_km, tiempo_estimado_min, costo_envio, estado, observaciones)
SELECT
    c.id_comanda, NULL,
    'Av. Beni #5678, Equipetrol, Santa Cruz',
    -17.77150000, -63.16900000,
    -17.76475400, -63.17776700,
    0.80, 5, 8.00,
    'PENDIENTE',
    'Entrega de prueba — pedido individual'
FROM comanda c
WHERE c.id_comanda = 2
AND NOT EXISTS (SELECT 1 FROM entrega WHERE id_comanda = c.id_comanda);

-- 10. Coordenadas reales Santa Cruz, Bolivia
UPDATE sucursal SET latitud = -17.78332700, longitud = -63.18214040 WHERE nombre = 'La Gaira San Martín';
UPDATE sucursal SET latitud = -17.76475400, longitud = -63.17776700 WHERE nombre = 'La Gaira Av. Beni';
UPDATE sucursal SET latitud = -17.75881900, longitud = -63.19571300 WHERE nombre = 'La Gaira Av. Piraí';
UPDATE sucursal SET latitud = -17.78601100, longitud = -63.18081700 WHERE nombre = 'La Gaira Centro';

-- 11. Cambiar id_empleado por id_usuario en entrega para que SUPERUSER
--     pueda actuar como repartidor sin registro en tabla empleado
ALTER TABLE entrega ADD COLUMN IF NOT EXISTS id_usuario_repartidor INTEGER;

-- Migrar datos existentes (id_empleado -> empleado.id_usuario -> id_usuario_repartidor)
UPDATE entrega e SET id_usuario_repartidor = emp.id_usuario
FROM empleado emp
WHERE e.id_empleado = emp.id_empleado AND e.id_empleado IS NOT NULL AND e.id_usuario_repartidor IS NULL;

-- FK y FK eliminados (superuser no necesita empleado)
ALTER TABLE entrega DROP CONSTRAINT IF EXISTS entrega_id_empleado_fkey;
ALTER TABLE ubicacion_empleado DROP CONSTRAINT IF EXISTS ubicacion_empleado_id_empleado_fkey;

COMMIT;
