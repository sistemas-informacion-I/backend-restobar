CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabla: USUARIO (_ACCESO_)
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    ci VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    sexo CHAR(1) NOT NULL CHECK (sexo IN ('M', 'F', 'O')),
    correo VARCHAR(150) UNIQUE,
    direccion TEXT,
    intentos_fallidos INTEGER DEFAULT 0 NOT NULL CHECK (intentos_fallidos >= 0),
    estado_acceso VARCHAR(20) DEFAULT 'HABILITADO' NOT NULL CHECK (estado_acceso IN ('HABILITADO', 'SUSPENDIDO', 'BLOQUEADO')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT chk_correo_format CHECK (correo ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'),
    CONSTRAINT chk_ci_no_vacio CHECK (LENGTH(TRIM(ci)) > 0)
);

-- Tabla: EMPLEADO (_ACCESO_)
CREATE TABLE empleado (
    id_empleado SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL UNIQUE,
    codigo_empleado VARCHAR(20) UNIQUE NOT NULL,
    salario NUMERIC(10,2) NOT NULL CHECK (salario >= 0), 
    turno VARCHAR(2) CHECK (turno IN ('AM', 'PM')),
    fecha_contratacion DATE DEFAULT CURRENT_DATE NOT NULL,
    fecha_finalizacion DATE,  
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_fechas_empleado CHECK (fecha_finalizacion IS NULL OR fecha_finalizacion >= fecha_contratacion)
);


-- Tabla: PROVEEDOR (_ACCESO_)
CREATE TABLE proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    id_usuario INTEGER UNIQUE,
    empresa VARCHAR(200) NOT NULL,
    nit VARCHAR(20) UNIQUE NOT NULL,
    nombre_contacto VARCHAR(150),
    telefono_contacto VARCHAR(20),
    correo_contacto VARCHAR(150),
    categoria_producto VARCHAR(50) CHECK (categoria_producto IN ('BEBIDAS', 'CARNES', 'VEGETALES', 'LACTEOS', 'PANADERIA', 'LIMPIEZA', 'HELADOS','VARIOS')),  
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE SET NULL,
    CONSTRAINT chk_nit_proveedor CHECK (nit ~ '^\d{1,13}$'),
    CONSTRAINT chk_correo_proveedor CHECK (correo_contacto IS NULL OR correo_contacto ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
);


-- Tabla: CLIENTE (_ACCESO_)
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    id_usuario INTEGER,
    nit VARCHAR(20),
    razon_social VARCHAR(200),
    fecha_nacimiento DATE,
    puntos_fidelidad INTEGER DEFAULT 0 NOT NULL CHECK (puntos_fidelidad >= 0),
    nivel_cliente VARCHAR(20) DEFAULT 'REGULAR' NOT NULL CHECK (nivel_cliente IN ('REGULAR', 'FRECUENTE', 'VIP', 'PREMIUM')),
    observaciones TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_nit_valido CHECK (nit IS NULL OR nit ~ '^\d{1,13}$')
);

-- Tabla: ROL (_ACCESO_)
CREATE TABLE rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    nivel_acceso INTEGER DEFAULT 1 NOT NULL CHECK (nivel_acceso > 0),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tabla: ROL_USUARIO (_ACCESO_)
CREATE TABLE rol_usuario (
    id_usuario INTEGER NOT NULL,
    id_rol INTEGER NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_expiracion TIMESTAMP,
    asignado_por INTEGER,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol) ON DELETE CASCADE,
    FOREIGN KEY (asignado_por) REFERENCES usuario(id_usuario),
    PRIMARY KEY (id_usuario, id_rol, fecha_asignacion),
    CONSTRAINT chk_vigencia_rol CHECK (fecha_expiracion IS NULL OR fecha_expiracion >= fecha_asignacion)
);

CREATE TABLE permiso (
    id_permiso SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    accion VARCHAR(50) NOT NULL CHECK (accion IN ('CREAR', 'LEER', 'ACTUALIZAR', 'ELIMINAR', 'EJECUTAR')),
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE rol_permiso (
    id_rol INTEGER NOT NULL,
    id_permiso INTEGER NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_expiracion TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol) ON DELETE CASCADE,
    FOREIGN KEY (id_permiso) REFERENCES permiso(id_permiso) ON DELETE CASCADE,
    PRIMARY KEY (id_rol, id_permiso),
    CONSTRAINT chk_vigencia_permiso CHECK (fecha_expiracion IS NULL OR fecha_expiracion >= fecha_asignacion)
);

-- Tabla: SESION (_ACCESO_)
CREATE TABLE sesion (
    id_sesion SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    token_sesion VARCHAR(255) UNIQUE NOT NULL,
    refresh_token VARCHAR(255) UNIQUE NOT NULL,
    refresh_expiracion TIMESTAMP,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_expiracion TIMESTAMP,
    ip_origen INET,
    user_agent TEXT,
    fecha_cierre TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_sesion_vigencia CHECK (fecha_expiracion IS NULL OR fecha_expiracion > fecha_inicio),
    CONSTRAINT chk_refresh_vigencia CHECK (refresh_expiracion IS NULL OR refresh_expiracion > fecha_inicio),
    CONSTRAINT chk_sesion_cierre CHECK (fecha_cierre IS NULL OR fecha_cierre >= fecha_inicio)
);

-- Tabla: CATEGORIA (_INVENTARIO_)
CREATE TABLE categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    id_categoria_padre INTEGER,
    nivel INTEGER DEFAULT 1 NOT NULL CHECK (nivel > 0),       
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_categoria_padre) REFERENCES categoria(id_categoria) ON DELETE SET NULL,
    CONSTRAINT chk_categoria_padre CHECK (id_categoria_padre IS NULL OR id_categoria_padre != id_categoria)
);

-- Tabla: PRODUCTO_FINAL (_INVENTARIO_)
CREATE TABLE producto_final (
    id_producto_final SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    id_categoria INTEGER,
    tiempo_preparacion INTEGER CHECK (tiempo_preparacion IS NULL OR tiempo_preparacion > 0),    
    imagen_url TEXT,    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE SET NULL  
);


-- Tabla: INVENTARIO (_INVENTARIO_)
CREATE TABLE inventario (
    id_inventario SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    unidad_medida VARCHAR(20) NOT NULL CHECK (unidad_medida IN ('KG', 'GRAMO', 'LITRO', 'ML')),
    marca VARCHAR(100),
    es_rehutilizable BOOLEAN DEFAULT FALSE NOT NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tabla: RECETA (_INVENTARIO_)
CREATE TABLE receta (
    id_receta SERIAL PRIMARY KEY,
    id_producto_final INTEGER NOT NULL,
    nombre VARCHAR(150),
    descripcion TEXT,
    tiempo_preparacion INTEGER CHECK (tiempo_preparacion IS NULL OR tiempo_preparacion > 0), 
    instrucciones TEXT,
    costo_total NUMERIC(10,2) NOT NULL CHECK (costo_total >= 0),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (id_producto_final) REFERENCES producto_final(id_producto_final) ON DELETE CASCADE
);

-- Tabla: INGREDIENTE_RECETA (_INVENTARIO_)
CREATE TABLE ingrediente_receta (
    id_ingrediente_receta SERIAL PRIMARY KEY,
    id_receta INTEGER NOT NULL,
    id_inventario INTEGER NOT NULL,
    cantidad NUMERIC(10,3) NOT NULL CHECK (cantidad > 0),
    unidad_medida VARCHAR(20) NOT NULL CHECK (unidad_medida IN ('KG', 'GRAMO', 'LITRO', 'ML')),
    notas TEXT,
    FOREIGN KEY (id_receta) REFERENCES receta(id_receta) ON DELETE CASCADE,
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE RESTRICT
);

-- Tabla: METODO_PAGO (_COMERCIAL_)
CREATE TABLE metodo_pago (
    id_metodo_pago SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    comision_porcentaje NUMERIC(5,2) CHECK (comision_porcentaje IS NULL OR (comision_porcentaje >= 0 AND comision_porcentaje <= 100)),
    comision_fija NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (comision_fija >= 0),
    activo BOOLEAN DEFAULT TRUE NOT NULL
);

-- Tabla: SUCURSAL (_OPERACIONES_)
CREATE TABLE sucursal (
    id_sucursal SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(150),
    horario_apertura TIME,
    horario_cierre TIME,
    ciudad VARCHAR(100),
    ubicacion VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT chk_horario_sucursal CHECK (horario_cierre IS NULL OR horario_apertura IS NULL OR horario_cierre > horario_apertura),
    CONSTRAINT chk_correo_sucursal CHECK (correo IS NULL OR correo ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
);

-- Tabla: PRODUCTO_SUCURSAL (_OPERACIONES_)
CREATE TABLE producto_sucursal (
    id_producto_final INTEGER NOT NULL,
    id_sucursal INTEGER NOT NULL,
    precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    disponible BOOLEAN DEFAULT TRUE NOT NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    PRIMARY KEY (id_producto_final, id_sucursal),
    FOREIGN KEY (id_producto_final) REFERENCES producto_final(id_producto_final) ON DELETE CASCADE,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON DELETE CASCADE
);

-- Tabla: LOG_AUDITORIA (_ACCESO_)
CREATE TABLE log_auditoria (
    id_log SERIAL PRIMARY KEY,
    tabla VARCHAR(100) NOT NULL,
    operacion VARCHAR(20) NOT NULL CHECK (operacion IN ('INSERT', 'UPDATE', 'DELETE')),
    id_registro VARCHAR(50),
    datos_anteriores JSONB,
    datos_nuevos JSONB,
    id_usuario INTEGER,
    id_sucursal INTEGER,
    ip_origen INET,
    user_agent TEXT,
    fecha_operacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal)
);

-- Tabla: CARRITO_COMPRAS (_ECOMMERCE_)
CREATE TABLE carrito_compras (
    id_carrito SERIAL PRIMARY KEY,
    id_cliente INTEGER,
    session_id TEXT, 
    id_sucursal INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO' NOT NULL CHECK (estado IN ('ACTIVO', 'ABANDONADO', 'CONVERTIDO')),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE SET NULL,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal),
    CONSTRAINT chk_carrito_cliente_session CHECK (id_cliente IS NOT NULL OR session_id IS NOT NULL)
);

-- Tabla: ITEM_CARRITO (_ECOMMERCE_)
CREATE TABLE item_carrito (
    id_item_carrito SERIAL PRIMARY KEY,
    id_carrito INTEGER NOT NULL,
    id_producto_final INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    notas_especiales TEXT,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (id_carrito) REFERENCES carrito_compras(id_carrito) ON DELETE CASCADE,
    FOREIGN KEY (id_producto_final) REFERENCES producto_final(id_producto_final)
);

-- Tabla: STOCK_SUCURSAL (_INVENTARIO_)
CREATE TABLE stock_sucursal (
    id_stock SERIAL PRIMARY KEY,
    id_inventario INTEGER NOT NULL,
    id_sucursal INTEGER NOT NULL,
    cantidad NUMERIC(10,3) DEFAULT 0 NOT NULL CHECK (cantidad >= 0),
    cantidad_minima NUMERIC(10,3) DEFAULT 0 NOT NULL CHECK (cantidad_minima >= 0),
    cantidad_maxima NUMERIC(10,3) CHECK (cantidad_maxima IS NULL OR cantidad_maxima >= 0),
    precio_unitario NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (precio_unitario >= 0),
    precio_promedio NUMERIC(10,4) DEFAULT 0 NOT NULL CHECK (precio_promedio >= 0),
    ubicacion_almacen VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE CASCADE,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
    UNIQUE (id_inventario, id_sucursal),
    CONSTRAINT chk_stock_limites CHECK (cantidad_maxima IS NULL OR cantidad_maxima >= cantidad_minima)
);


-- Tabla: LOTE_INVENTARIO (_INVENTARIO_)
CREATE TABLE lote_inventario (
    id_lote SERIAL PRIMARY KEY,
    id_stock INTEGER NOT NULL,
    numero_lote VARCHAR(50),
    cantidad NUMERIC(10,3) NOT NULL CHECK (cantidad > 0),
    fecha_ingreso DATE DEFAULT CURRENT_DATE NOT NULL,
    fecha_vencimiento DATE,
    precio_compra NUMERIC(10,2) NOT NULL CHECK (precio_compra >= 0),
    estado VARCHAR(20) DEFAULT 'DISPONIBLE' NOT NULL CHECK (estado IN ('DISPONIBLE', 'VENCIDO', 'AGOTADO', 'DAÑADO')),
    FOREIGN KEY (id_stock) REFERENCES stock_sucursal(id_stock) ON DELETE CASCADE,
    CONSTRAINT chk_lote_vencimiento CHECK (fecha_vencimiento IS NULL OR fecha_vencimiento >= fecha_ingreso)
);


-- Tabla: COMPRA (_COMERCIAL_)
CREATE TABLE compra (
    id_compra SERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE,
    id_proveedor INTEGER NOT NULL,
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_entrega_programada DATE,
    fecha_entrega_real DATE,
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    descuento NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (descuento >= 0 AND descuento <= subtotal),
    impuesto NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (impuesto >= 0),
    total NUMERIC(10,2) NOT NULL CHECK (total = (subtotal - descuento + impuesto) AND total > 0),
    estado_pago VARCHAR(20) NOT NULL CHECK  (estado_pago IN ('PENDIENTE', 'PAGADO', 'PARCIAL')),
    fecha_limite_pago DATE,
    fecha_pago DATE,
    observaciones TEXT,
    creado_por INTEGER,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    FOREIGN KEY (creado_por) REFERENCES empleado(id_empleado),
    CONSTRAINT chk_compra_fechas CHECK (
        (fecha_entrega_programada IS NULL OR fecha_entrega_programada >= fecha_compra::date)
        AND (fecha_entrega_real IS NULL OR fecha_entrega_real >= fecha_compra::date)
        AND (fecha_pago IS NULL OR fecha_pago >= fecha_compra::date)
        AND (fecha_entrega_real IS NULL OR fecha_entrega_programada IS NULL OR fecha_entrega_real >= fecha_entrega_programada)
    )    
);


-- Tabla: DETALLE_COMPRA (_COMERCIAL_)
CREATE TABLE detalle_compra (
    id_detalle_compra SERIAL PRIMARY KEY,
    id_compra INTEGER NOT NULL,
    id_stock INTEGER NOT NULL,
    cantidad NUMERIC(10,3) NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0) ,
    descuento NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (descuento >= 0),
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal = (cantidad * precio_unitario - COALESCE(descuento, 0)) AND subtotal >= 0),
    numero_lote VARCHAR(50),
    fecha_vencimiento DATE,
    FOREIGN KEY (id_compra) REFERENCES compra(id_compra) ON DELETE CASCADE,
    FOREIGN KEY (id_stock) REFERENCES stock_sucursal(id_stock) ON DELETE RESTRICT
);


-- Tabla: NOTA_SALIDA (_COMERCIAL_)
CREATE TABLE nota_salida (
    id_nota_salida SERIAL PRIMARY KEY,
    id_sucursal INTEGER NOT NULL,
    id_empleado INTEGER,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    descripcion TEXT,
    monto_total NUMERIC(10,2) NOT NULL CHECK (monto_total > 0),
    tipo_gasto VARCHAR(50) NOT NULL CHECK (tipo_gasto IN ('SERVICIOS', 'ALQUILER', 'SUELDOS', 'MANTENIMIENTO', 'TRANSPORTE', 'IMPUESTOS', 'PERDIDA', 'OTROS')),
    estado VARCHAR(20) DEFAULT 'REGISTRADO' NOT NULL CHECK (estado IN ('REGISTRADO', 'ANULADO')),
    observaciones TEXT,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal),
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado)
);


-- Tabla: DETALLE_NOTA_SALIDA (_COMERCIAL_)
CREATE TABLE detalle_nota_salida (
    id_detalle SERIAL PRIMARY KEY,
    id_nota_salida INTEGER NOT NULL,
    id_stock_sucursal INTEGER,
    descripcion TEXT NOT NULL,
    cantidad NUMERIC(10,2) NOT NULL CHECK (cantidad > 0),
    monto NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    FOREIGN KEY (id_nota_salida) REFERENCES nota_salida(id_nota_salida) ON DELETE CASCADE,
    FOREIGN KEY (id_stock_sucursal) REFERENCES stock_sucursal(id_stock) ON DELETE SET NULL 
);


-- Tabla: CAJA (_COMERCIAL_)
CREATE TABLE caja (
    id_caja SERIAL PRIMARY KEY,
    id_sucursal INTEGER NOT NULL,
    id_empleado INTEGER,    
    fecha_apertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP,
    monto_inicial NUMERIC(10,2) NOT NULL CHECK (monto_inicial >= 0),
    monto_final NUMERIC(10,2) CHECK (monto_final IS NULL OR monto_final >= 0),
    estado VARCHAR(20) DEFAULT 'ABIERTA' NOT NULL CHECK (estado IN ('ABIERTA', 'CERRADA')),
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal),
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    CONSTRAINT chk_caja_fechas CHECK (fecha_cierre IS NULL OR fecha_cierre >= fecha_apertura)
);


-- Tabla: SECTOR (_OPERACIONES_)
CREATE TABLE sector (
    id_sector SERIAL PRIMARY KEY,
    id_sucursal INTEGER NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    tipo_sector VARCHAR(20) NOT NULL CHECK (tipo_sector IN ('TERRAZA', 'SALON', 'VIP', 'BARRA', 'PRIVADO')),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
    UNIQUE (id_sucursal, nombre)    
);


-- Tabla: MESA (_OPERACIONES_)
CREATE TABLE mesa (
    id_mesa SERIAL PRIMARY KEY,
    id_sector INTEGER NOT NULL,   
    numero_mesa VARCHAR(20) NOT NULL,
    capacidad_personas INTEGER NOT NULL CHECK (capacidad_personas > 0),
    disponibilidad VARCHAR(20) DEFAULT 'DISPONIBLE' NOT NULL CHECK (disponibilidad IN ('DISPONIBLE', 'OCUPADA', 'RESERVADA', 'FUERA_SERVICIO')),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_sector) REFERENCES sector(id_sector) ON DELETE CASCADE,
    UNIQUE (id_sector, numero_mesa)    
);


-- Tabla: RESERVA (_ECOMMERCE_)
CREATE TABLE reserva (
    id_reserva SERIAL PRIMARY KEY,
    id_cliente INTEGER,
    fecha_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME,
    numero_personas INTEGER NOT NULL CHECK (numero_personas > 0),
    observaciones TEXT,
    estado VARCHAR(30) DEFAULT 'PENDIENTE' NOT NULL CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_CURSO', 'COMPLETADA', 'CANCELADA', 'NO_ASISTIO')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_confirmacion TIMESTAMP,
    confirmado_por INTEGER,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE SET NULL,
    FOREIGN KEY (confirmado_por) REFERENCES empleado(id_empleado),
    CONSTRAINT chk_reserva_fechas CHECK (fecha_reserva >= CURRENT_DATE),
    CONSTRAINT chk_reserva_horas CHECK (hora_fin IS NULL OR hora_fin > hora_inicio),
    CONSTRAINT chk_reserva_confirmacion CHECK (fecha_confirmacion IS NULL OR (estado = 'CONFIRMADA' AND fecha_confirmacion >= fecha_creacion))
);


-- Tabla: RESERVA_MESA (_ECOMMERCE_)
CREATE TABLE reserva_mesa (
    id_reserva INTEGER NOT NULL,
    id_mesa INTEGER NOT NULL,
    FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa),
    PRIMARY KEY (id_reserva, id_mesa)
);


-- Tabla: EMPLEADO_SUCURSAL (_OPERACIONES_)
CREATE TABLE empleado_sucursal (
    id_empleado INTEGER NOT NULL,
    id_sucursal INTEGER NOT NULL,
    fecha_asignacion DATE DEFAULT CURRENT_DATE NOT NULL, 
    fecha_fin DATE,    
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
    PRIMARY KEY (id_empleado, id_sucursal, fecha_asignacion),
    CONSTRAINT chk_empleado_sucursal_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_asignacion)
);


-- Tabla: COMANDA (_OPERACIONES_)
CREATE TABLE comanda (
    id_comanda SERIAL PRIMARY KEY,
    numero_comanda VARCHAR(50) UNIQUE NOT NULL,
    id_sucursal INTEGER NOT NULL,
    id_cliente INTEGER,
    id_empleado INTEGER, 
    id_reserva INTEGER,
    id_carrito INTEGER,
    tipo_servicio VARCHAR(20) NOT NULL CHECK (tipo_servicio IN ('MESA', 'PARA_LLEVAR', 'ONLINE')),
    fecha_apertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP,
    numero_personas INTEGER CHECK (numero_personas IS NULL OR numero_personas > 0),
    estado VARCHAR(30) DEFAULT 'ABIERTA' NOT NULL CHECK (estado IN ('ABIERTA', 'EN_PREPARACION', 'LISTA', 'ENTREGADA', 'CERRADA', 'CANCELADA')),
    observaciones TEXT,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE SET NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva),
    FOREIGN KEY (id_carrito) REFERENCES carrito_compras(id_carrito),
    CONSTRAINT chk_comanda_fechas CHECK (fecha_cierre IS NULL OR fecha_cierre >= fecha_apertura),
    CONSTRAINT chk_comanda_contexto_coherencia CHECK (
        (tipo_servicio = 'MESA' AND id_reserva IS NOT NULL AND id_carrito IS NULL) OR
        (tipo_servicio = 'PARA_LLEVAR' AND id_reserva IS NULL AND id_carrito IS NULL) OR
        (tipo_servicio = 'ONLINE' AND id_reserva IS NULL AND id_carrito IS NOT NULL)
    )
);


-- Tabla: NOTA_VENTA (_COMERCIAL_)
CREATE TABLE nota_venta (
    id_nota_venta SERIAL PRIMARY KEY,
    id_comanda INTEGER NOT NULL,
    id_sucursal INTEGER NOT NULL,
    id_cliente INTEGER,
    id_empleado INTEGER, 
    id_metodo_pago INTEGER NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    descuento NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (descuento >= 0 AND descuento <= subtotal),
    impuesto NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (impuesto >= 0),
    propina NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (propina >= 0),
    total NUMERIC(10,2) NOT NULL CHECK (total = (subtotal - descuento + impuesto + propina) AND total > 0),
    estado VARCHAR(30) DEFAULT 'EMITIDA' NOT NULL CHECK (estado IN ('EMITIDA', 'PAGADA', 'ANULADA', 'DEVUELTA')),
    observaciones TEXT,
	fecha_pago TIMESTAMP,
    nit_cliente VARCHAR(20) CHECK (nit_cliente ~ '^\d{1,13}$'),
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda),
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago),
    UNIQUE (id_comanda),
    CONSTRAINT chk_nota_venta_fechas CHECK (
        fecha_pago IS NULL OR (estado = 'PAGADA' AND fecha_pago >= fecha_emision)
    )    
);


-- Tabla: DETALLE_NOTA_VENTA (_COMERCIAL_)
CREATE TABLE detalle_nota_venta (
    id_detalle_nota_venta SERIAL PRIMARY KEY,
    id_nota_venta INTEGER NOT NULL,
    id_producto_final INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    costo_unitario NUMERIC(10,2) NOT NULL CHECK (costo_unitario >= 0 AND costo_unitario <= precio_unitario),
    descuento NUMERIC(10,2) DEFAULT 0 NOT NULL CHECK (descuento >= 0 AND descuento <= subtotal),
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal = (cantidad * precio_unitario - COALESCE(descuento, 0)) AND subtotal >= 0),
    descripcion TEXT,
    FOREIGN KEY (id_nota_venta) REFERENCES nota_venta(id_nota_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto_final) REFERENCES producto_final(id_producto_final)
);



-- Tabla: TRANSACCION_ONLINE (_ECOMMERCE_)
CREATE TABLE transaccion_online (
    id_transaccion SERIAL PRIMARY KEY,
    id_nota_venta INTEGER,
    numero_transaccion VARCHAR(100) UNIQUE NOT NULL,
    monto NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    moneda VARCHAR(10) DEFAULT 'BOB' NOT NULL,
    estado VARCHAR(30) NOT NULL CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'APROBADA', 'RECHAZADA', 'REEMBOLSADA', 'CANCELADA')),
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_completado TIMESTAMP,
    codigo_autorizacion VARCHAR(100),
    codigo_error VARCHAR(50),
    datos_adicionales JSONB,
    FOREIGN KEY (id_nota_venta) REFERENCES nota_venta(id_nota_venta),
    CONSTRAINT chk_transaccion_fechas CHECK (fecha_completado IS NULL OR fecha_completado >= fecha_inicio)
);


-- Tabla: MOVIMIENTO_CAJA (_COMERCIAL_)
CREATE TABLE movimiento_caja (
    id_movimiento SERIAL PRIMARY KEY,
    id_caja INTEGER NOT NULL,
    id_nota_venta integer,
    id_compra integer,
    id_nota_salida integer,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INGRESO', 'EGRESO')),
    concepto VARCHAR(50) NOT NULL CHECK (concepto IN ('VENTA', 'COMPRA', 'GASTO', 'RETIRO', 'INGRESO_EXTRA')),
    monto NUMERIC(10,2) NOT NULL CHECK (monto >= 0),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    referencia_id INTEGER, 
    observaciones TEXT,
    FOREIGN KEY (id_caja) REFERENCES caja(id_caja) ON DELETE cascade,
    FOREIGN KEY (id_nota_venta) REFERENCES nota_venta(id_nota_venta),
    FOREIGN KEY (id_compra) references compra(id_compra),
    FOREIGN KEY (id_nota_salida) references  nota_salida(id_nota_salida),
    UNIQUE (id_nota_venta),
    UNIQUE (id_compra),
    UNIQUE (id_nota_salida),
    CONSTRAINT chk_movimiento_coherencia CHECK (
        (tipo = 'INGRESO' AND concepto IN ('VENTA', 'INGRESO_EXTRA')) OR
        (tipo = 'EGRESO' AND concepto IN ('COMPRA', 'GASTO', 'RETIRO'))
    )
);



-- Tabla: DETALLE_COMANDA (_OPERACIONES_)
CREATE TABLE detalle_comanda (
    id_detalle_comanda SERIAL PRIMARY KEY,
    id_comanda INTEGER NOT NULL,
    id_producto_final INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    notas_especiales TEXT,
    estado VARCHAR(30) DEFAULT 'PENDIENTE' NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO')),
    estacion_preparacion VARCHAR(30) CHECK (estacion_preparacion IN ('COCINA', 'BARRA')),
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda) ON DELETE CASCADE,
    FOREIGN KEY (id_producto_final) REFERENCES producto_final(id_producto_final)
);


