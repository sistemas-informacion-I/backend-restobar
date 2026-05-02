

## CU07 – Gestionar Inventario por Sucursal  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Alta  

### Descripción funcional
Administrar el catálogo de insumos (inventario) y el stock en cada sucursal, incluyendo la trazabilidad por lotes. El SU opera sobre cualquier sucursal; el A solo sobre la suya.

### Flujo mínimo
1. **Listar insumos**  
   - Mostrar todos los registros de `inventario` (código, nombre, unidad de medida, marca, si es reutilizable, activo).  
   - Para cada insumo, mostrar el stock disponible en la sucursal seleccionada (del SU) o la del A se cargara automaticamene solo la de él, consultando `stock_sucursal`.

2. **Crear insumo**  
   - Formulario con código único, nombre, descripción, unidad de medida (KG, GRAMO, LITRO, ML), marca, si es reutilizable.  
   - El insumo se crea a nivel global (sin sucursal).  

3. **Gestionar stock por sucursal**  
   - Desde la pantalla del insumo, el administrador de sucursal o el SU pueden:  
     * **Establecer stock inicial:** insertar en `stock_sucursal` (id_inventario, id_sucursal) con cantidad, cantidades mínima/máxima, precio unitario, precio promedio, ubicación.  
     * **Agregar lote:** insertar en `lote_inventario` un nuevo lote (número de lote, cantidad, fecha ingreso, vencimiento, precio compra). Esto incrementa la cantidad del `stock_sucursal` automáticamente (o al recibir la compra, pero aquí puede ser manual).  
     * **Ajustar stock:** para correcciones manuales (crear un lote de ajuste positivo/negativo) o registrar bajas por vencimiento/daño (cambiar estado del lote a ‘VENCIDO’ o ‘DAÑADO’ y liberar la cantidad).  
     * **Ver historial de lotes:** todos los lotes asociados a ese stock, con sus cantidades y estados.

4. **Ver alertas tempranas** (básico):  
   - En el listado, marcar en rojo los insumos cuyo stock actual esté por debajo de la cantidad mínima (esto servirá para el futuro CU13).

### Funcionalidades extras
- Transferencia de stock entre sucursales (para el SU).  
- Vista consolidada de inventario de todas las sucursales (para el SU).  

### Tablas implicadas
| Tabla             | Operación                           | Motivo |
|-------------------|-------------------------------------|--------|
| `inventario`      | CRUD                                | Catálogo maestro de insumos |
| `stock_sucursal`  | Inserción, actualización, consulta  | Stock por sucursal |
| `lote_inventario` | Inserción, actualización (estado)   | Manejo de lotes y movimientos |
| `sucursal`        | Lectura                             | Para seleccionar/validar sucursal |
| `log_auditoria`   | Inserción                           | Auditar cambios |

### Dependencias y conexiones
- **Ciclo 1:** Necesita que existan sucursales (CU06) y que el usuario tenga el rol adecuado.  
- **Ciclo 2:** Los insumos que aquí se crean serán usados en las recetas (CU10) y en las compras (CU12).  
- **Ciclo 3/4:** Los movimientos de stock por ventas se harán automáticamente al preparar comandas (CU25). Las alertas se formalizarán en CU13, que ya podrá apoyarse en los datos de `cantidad_minima` y los lotes.

### Regla multi‑sucursal
- El Administrador **solo puede ver y modificar** el stock de su propia sucursal. El frontend debe ocultar el selector de sucursal para él.  
- El Superusuario puede elegir sucursal y operar en cualquiera.

---

## CU08 – Gestionar Categorías de Productos  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Baja  

### Descripción funcional
Mantener la jerarquía de categorías para organizar los productos finales (platos, bebidas, etc) del menú. Es una tabla maestra global, no depende de la sucursal.

### Flujo mínimo
1. **Listar categorías**  
   - Vista de árbol o tabla con sangrado, mostrando nombre, nivel, estado.  
2. **Crear categoría**  
   - Nombre único, descripción, categoría padre (opcional, para anidar).  
   - El sistema calcula automáticamente el `nivel`.  
3. **Editar categoría**  
   - Modificar nombre, descripción, padre (evitando ciclos).  
4. **Desactivar categoría** (`activo = FALSE`), no se borra físicamente para no afectar productos que la referencien.
- Restricción: no permitir borrar una categoría padre si tiene hijos.

### Funcionalidades extras
- Reordenar categorías manualmente (si se agrega un campo de orden).  
- Vista previa de cuántos productos finales hay en cada categoría.  

### Tablas implicadas
| Tabla      | Operación | Motivo |
|------------|-----------|--------|
| `categoria`| CRUD      | Datos de categorías |
| `log_auditoria` | Inserción | Auditoría |

### Dependencias
- **Ciclo 2:** Es prerequisito para CU09 (Productos Finales).  
- **Ciclo 3:** El catálogo online (CU19) usará las categorías para filtrar y navegar.

### Regla multi‑sucursal
Como las categorías son globales, cualquier Administrador con permiso puede modificarlas. Si se desea limitar, se podría restringir al SU, pero por prioridad baja es aceptable que el Admin también pueda.

---

## CU09 – Gestionar Productos Finales  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Alta  

### Descripción funcional
Administrar el catálogo de productos que se venden (platos, bebidas, menús). Tiene dos caras:
- **Maestro de producto** (global): código, nombre, descripción, categoría, tiempo de preparación, imagen.  
- **Precio y disponibilidad por sucursal**: cada sucursal puede tener un precio distinto y decidir si el producto está disponible.

El Administrador solo puede gestionar la oferta de su sucursal; el SU puede manejar todo.

### Flujo mínimo
**Como Superusuario:**
1. **Listar productos** → con filtro por categoría, activo.  
2. **Crear producto** → Código único, nombre, descripción, categoría, tiempo prep., URL imagen.  
3. **Editar/desactivar** producto.  
4. **Asignar a sucursales** → Para un producto, elegir una sucursal e indicar precio y si está disponible. Esto inserta en `producto_sucursal`.  
5. **Listar productos por sucursal** → ver qué productos están asignados y sus precios.

**Como Administrador:**  
- Solo ve la pantalla de “Mis productos” que lista todos los productos existentes, pero muestra su propio precio y disponibilidad.  
- Puede **activar/desactivar** el producto para su sucursal y **modificar el precio** (actualiza `producto_sucursal`).  
- No puede editar los datos maestros del producto (nombre, descripción, etc.) – eso lo hace el SU.

### Funcionalidades extras
- Subida de imágenes 
- Historial de cambios de precio.  

### Tablas implicadas
| Tabla               | Operación                          | Motivo |
|---------------------|------------------------------------|--------|
| `producto_final`    | CRUD (maestro)                     | Productos globales |
| `producto_sucursal` | Inserción, actualización, consulta | Precio/disponibilidad por sucursal |
| `categoria`         | Lectura                            | Para asociar categoría |
| `sucursal`          | Lectura                            | Para asignar/validar sucursal |
| `log_auditoria`     | Inserción                          | Auditoría |

### Dependencias
- **Ciclo 1:** Sucursales (CU06).  
- **Ciclo 2:** Categorías (CU08) y Recetas (CU10).  
- **Ciclo 3:** Los productos aparecerán en el catálogo online (CU19) y en las comandas (CU14).  
- **Ciclo 4:** Promociones (CU16) se basarán en productos.

### Regla multi‑sucursal
- El Administrador **no puede ver ni modificar** los precios o disponibilidad de otras sucursales.  
- La creación de productos maestros puede reservarse al SU, o bien permitirse al Admin pero informando que es global. Recomiendo que solo el SU cree productos para mantener control.

---

## CU10 – Gestionar Recetas y Composición  
**Actor:** Superusuario (SU), Administrador (A), Cocinero (CN), Bartender (B)  
**Prioridad:** Media  

### Descripción funcional
Definir para cada producto final su receta estándar: lista de insumos (del inventario) con sus cantidades, unidad de medida y notas. Esto permite calcular el costo teórico y, más adelante, hacer el descuento automático del inventario al preparar una comanda.

### Flujo mínimo
1. **Listar recetas** → asociadas a un producto final, con su costo total calculado.  
2. **Crear/Editar receta**  
   - Seleccionar el producto final.  
   - Agregar ingredientes: buscar insumo (autocomplete), indicar cantidad, unidad (debe ser compatible), notas.  
   - El sistema calcula el `costo_total` de la receta sumando el `precio_promedio` de cada insumo en la sucursal actual (requiere elegir sucursal de referencia).  
   - Guardar la cabecera (`receta`) y los detalles (`ingrediente_receta`).  
3. **Ver/Editar ingredientes** de una receta existente.  
4. **Desactivar receta** (si un producto cambia).
- Comparar costo teórico con compras reales para identificar desvíos.  

### Funcionalidades extras
- Múltiples versiones de receta (p.ej., “receta clásica”, “receta ligera”) con fechas de vigencia.  
- Impresión de receta en formato cocina.
- Duplicar receta para crear una nueva version de la receta.

### Tablas implicadas
| Tabla                  | Operación      | Motivo |
|------------------------|----------------|--------|
| `producto_final`       | Lectura        | Producto al que se asocia |
| `inventario`           | Lectura        | Insumos disponibles |
| `receta`               | CRUD           | Cabecera de receta |
| `ingrediente_receta`   | CRUD           | Detalle de ingredientes |
| `stock_sucursal`       | Lectura        | Para obtener precio promedio por sucursal y calcular costo |
| `log_auditoria`        | Inserción      | Auditoría |

### Dependencias
- **Ciclo 2:** Requiere que existan productos finales (CU09) e insumos en inventario (CU07).  
- **Ciclo 3:** La preparación de comandas (CU25) usará la receta para deducir automáticamente el inventario.  

### Regla multi‑sucursal
- La receta es global (un producto tiene una sola receta oficial), pero el costo se puede calcular con los precios de una sucursal de referencia elegida. El SU puede seleccionar la sucursal; el Admin o Cocinero calculan con los precios de su sucursal.

---

## CU11 – Gestionar Proveedores  
**Actor:** Superusuario (SU), Administrador (A), Proveedor (P) 
**Prioridad:** Media  

### Descripción funcional
Mantener los datos de los proveedores. El Administrador y el SU los crean y editan. El proveedor será únicamente una entidad de registro interno para nuestras compras e inventario, por lo que no tendrá acceso ni cuenta en el sistema.

### Flujo mínimo
1. **Listar proveedores** con filtros (empresa, NIT, categoría).  
2. **Crear proveedor**  
   - Datos a registrar: empresa, NIT, nombre del contacto, teléfono, correo, dirección, categoría de productos que suministra.  
3. **Editar datos** del proveedor.  
4. **Desactivar** (no borrar) el registro.

### Tablas implicadas
| Tabla       | Operación                         | Motivo |
|-------------|-----------------------------------|--------|
| `proveedor` | CRUD                              | Datos maestros del proveedor |
| `log_auditoria` | Inserción                     | Auditoría |

### Dependencias 
- **Ciclo 2:** Esencial para CU12 (Compras).  
- **Ciclo 4:** Los reportes de compras por proveedor (CU23) se basarán en estos datos.

### Regla multi‑sucursal
Los proveedores son globales, no pertenecen a una sucursal. Cualquier Administrador puede verlos y crear nuevos, compartidos entre sucursales.

### Nueva Tabla Proveedores

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| `id` | BIGINT | PK, Auto-incremental | Identificador único del proveedor |
| `empresa` | VARCHAR | NOT NULL | Nombre comercial o razón social de la empresa |
| `nit` | VARCHAR | UNIQUE | Número de identificación tributaria o documento de la empresa |
| `nombre_contacto` | VARCHAR | NOT NULL | Nombre de la persona encargada |
| `telefono` | VARCHAR | NOT NULL | Teléfono de contacto |
| `correo` | VARCHAR | UNIQUE | Correo electrónico de contacto |
| `direccion` | VARCHAR | NULL | Dirección física de la empresa proveedora |
| `categoria_productos`| VARCHAR | NULL | Categoría de los insumos o productos que suministra | (Opcional)
| `activo` | BOOLEAN | DEFAULT TRUE | Estado lógico del proveedor para permitir o no nuevas compras |
| `creado_por` | BIGINT | FK (usuario) | Usuario (SU o Admin) que registró al proveedor |
| `created_at` | TIMESTAMP | | Fecha de creación del registro |
| `updated_at` | TIMESTAMP | | Fecha de última modificación |


---

## CU12 – Gestionar Compras e Insumos  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Media  

### Descripción funcional
Realizar pedidos de insumos a proveedores y registrar la recepción, actualizando automáticamente el stock y la información de lotes. También manejar el estado de pago.

### Flujo mínimo
1. **Crear una compra**  
   - Seleccionar sucursal (automática para el Admin).  
   - Elegir proveedor (de los activos).  
   - Fecha de compra, fecha de entrega programada, observaciones.  
   - **Agregar items:** buscar insumo (del inventario), el `stock_sucursal` correspondiente a la sucursal destino. Se muestra el stock actual y la cantidad mínima/máxima para referencia.  
     * Indicar cantidad, precio unitario, descuento, número de lote, fecha de vencimiento.  
     * El subtotal se calcula automáticamente.  
   - El sistema calcula subtotal, descuento, impuesto y total de la compra.  
   - Estado de pago inicial: `PENDIENTE`.  

2. **Listar compras** con filtros (proveedor, estado, fecha).  

3. **Ver detalle de compra** y poder editar (antes de la recepción).  

4. **Registrar recepción de la compra**  
   - Para cada detalle, confirmar la cantidad recibida realmente (por defecto la solicitada) y actualizar `fecha_entrega_real`.  
   - El sistema **crea un lote en `lote_inventario`** con la cantidad recibida, usando el número de lote y fecha de vencimiento indicados, y suma la cantidad al `stock_sucursal` correspondiente.  
   - Si el lote ya existía (mismo número), opcionalmente se suma.  

5. **Registrar pago**  
   - Cambiar `estado_pago` a `PAGADO` o `PARCIAL`, registrando `fecha_pago` y observaciones.  
   - (Para el ciclo 4 se enlazará con la caja y el movimiento correspondiente).

### Funcionalidades extras  
- Integración futura con la caja (CU22) para registrar los egresos.

### Tablas implicadas
| Tabla              | Operación                                  | Motivo |
|--------------------|--------------------------------------------|--------|
| `compra`           | Inserción, actualización, consulta         | Cabecera de compra |
| `detalle_compra`   | Inserción, actualización                   | Líneas de compra |
| `proveedor`        | Lectura                                    | Seleccionar proveedor |
| `stock_sucursal`   | Lectura y actualización (cantidad)         | Actualizar stock al recibir |
| `lote_inventario`  | Inserción (al recibir)                     | Trazabilidad |
| `sucursal`         | Lectura                                    | Sucursal destino |
| `log_auditoria`    | Inserción                                  | Auditoría |

### Dependencias
- **Ciclo 1:** Sucursales (CU06), empleados (opcional, `creado_por`).  
- **Ciclo 2:** Proveedores (CU11) e Inventario (CU07).  
- **Ciclo 4:** Las compras impactarán en caja (CU22) y reportes (CU23). El movimiento de caja `EGRESO` se generará cuando se pague la compra (en CU22 se podrá tomar la compra pendiente de pago y completar la operación). Esto debe tenerse en cuenta para que el CU12 no cierre completamente el pago; mejor que solo maneje el estado de pago y luego la caja enlace el movimiento.

### Regla multi‑sucursal
- El Administrador solo puede comprar para su sucursal (el `id_sucursal` en `compra` se fija al del Admin).  
- El SU puede elegir la sucursal de destino.

---

