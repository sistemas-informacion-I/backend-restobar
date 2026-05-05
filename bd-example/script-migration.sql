BEGIN;

-- 1. INFRAESTRUCTURA: Columna de Identidad
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS tipo_usuario CHAR(1);

-- 2. PROMOCIÓN: Renombrar ADMIN a SUPERUSER (Global)
UPDATE rol 
SET nombre = 'SUPERUSER', 
    descripcion = 'Rol superusuario (Acceso Global)',
    nivel_acceso = 100
WHERE nombre IN ('ADMIN', 'ADMINISTRADOR');

-- 3. RECREACIÓN: Crear nuevo rol ADMIN (Sucursal)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'ADMIN') THEN
        INSERT INTO rol (nombre, descripcion, nivel_acceso, activo, fecha_creacion) 
        VALUES ('ADMIN', 'Rol dueño de sucursal (Acceso Restringido)', 90, true, CURRENT_TIMESTAMP);
    END IF;
END $$;

-- 4. CLASIFICACIÓN DE IDENTIDADES
-- A. Superusuarios (Acceso Global)
UPDATE usuario 
SET tipo_usuario = 'S' 
WHERE id_usuario IN (
    SELECT ru.id_usuario 
    FROM rol_usuario ru 
    JOIN rol r ON ru.id_rol = r.id_rol 
    WHERE r.nombre = 'SUPERUSER'
);

-- B. Empleados (Acceso de Sucursal)
UPDATE usuario u 
SET tipo_usuario = 'E' 
FROM empleado e 
WHERE u.id_usuario = e.id_usuario 
AND u.tipo_usuario IS NULL;

-- C. El resto (Clientes o sin clasificar)
UPDATE usuario SET tipo_usuario = 'C' WHERE tipo_usuario IS NULL;

-- 5. SINCRONIZACIÓN DE TABLAS DE NEGOCIO (LA CORRECCIÓN)
-- Asegurar que todo usuario 'C' tenga un registro válido en la tabla cliente
INSERT INTO cliente (id_usuario, nivel_cliente, puntos_fidelidad)
SELECT id_usuario, 'REGULAR', 0
FROM usuario
WHERE tipo_usuario = 'C'
AND id_usuario NOT IN (SELECT id_usuario FROM cliente);

-- 6. SEGURIDAD Y RESTRICCIONES
ALTER TABLE usuario ALTER COLUMN tipo_usuario SET NOT NULL;
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'check_tipo_usuario') THEN
        ALTER TABLE usuario ADD CONSTRAINT check_tipo_usuario CHECK (tipo_usuario IN ('S', 'E', 'C'));
    END IF;
END $$;

COMMIT;
