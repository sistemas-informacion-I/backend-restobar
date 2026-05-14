
## CU14 – Gestionar Comanda  
**Actor:** Superusuario (SU), Administrador (A), Mesero (M), Cocinero (CN), Bartender (B)
**Prioridad:** Alta  

### Descripción funcional
Crear, modificar, supervisar y cerrar comandas (órdenes de servicio) en cualquier modalidad: mesa, para llevar u online. La comanda agrupa los productos solicitados, su estado de preparación y su posterior facturación.

### Flujo mínimo
1. **Crear comanda**  
   - Seleccionar tipo de servicio: `MESA`, `PARA_LLEVAR` u `ONLINE`. El `ONLINE` se crea automáticamente desde el carrito (CU20/CU21).  
   - **Mesa ocupada**: al crear una comanda en mesa se debe verificar que la mesa no esté `OCUPADA` en otra comanda activa.  
   - Si es `MESA`:
     * Elegir sucursal (automática para Admin/Mesero; manual para SU).  
     * Asociar una reserva existente o seleccionar una mesa libre. Si se toma una mesa libre, cambia a `OCUPADA`.  
     * Asignar cliente si existe; puede ser anónimo (`id_cliente=NULL`).  
   - Si es `PARA_LLEVAR`: no requiere mesa ni reserva; puede asignarse cliente si se conoce.  
   - El sistema genera un `numero_comanda` único automáticamente.  
   - Estado inicial: `ABIERTA` para mesa y para llevar; `PENDIENTE_PAGO` para online hasta confirmar el pago.

2. **Agregar productos a la comanda**  
   - Buscar productos finales con precio y disponibilidad de la sucursal.  
   - Indicar cantidad, notas especiales y observaciones.  
   - El sistema inserta en `detalle_comanda` con estado `PENDIENTE`. Según la categoría del producto se asigna automáticamente la `estacion_preparacion` (`COCINA` o `BARRA`).  
   - Se puede seguir agregando productos mientras la comanda no esté `CERRADA`, `CANCELADA` o, en online, antes de que el primer ítem entre en preparación.

3. **Visualizar comanda**  
   - Mesero/Admin/SU pueden ver la comanda con todos sus ítems y estados de preparacion.  
   - El cocinero/bartender puede listar las comandas activas para su estación (ver CU25).

4. **Modificar comanda**  
   - Cambiar cantidades, agregar/quitar productos, modificar notas.  
   - Cancelar un ítem (`CANCELADO`) solo si todavía está `PENDIENTE`.  
   - Cambiar el estado general de la comanda manualmente a `EN_PREPARACION` cuando ya se envió a cocina/barra.  
   - Cuando todos los ítems estén listos, la comanda pasa a `LISTA`. Para mesa o para llevar, eso habilita facturación (CU15); para online, queda lista para su entrega al cliente (ya fue abonada).

5. **Cerrar comanda**  
   - En mesa o para llevar: el Cajero o Admin, desde CU15, factura la comanda y completa la venta, pasando a estado `CERRADA`.
   - En online: la venta se generó y cobró en CU21. Al entregar el pedido en sucursal, el personal pasa la comanda de `LISTA` directamente a `ENTREGADA` o `CERRADA`.

### Flujos por tipo de servicio

#### 1. Comanda en MESA
1. El mesero selecciona la sucursal y elige entre asociar una reserva existente o seleccionar una mesa libre.  
2. Se genera el `numero_comanda` y el estado inicia en `ABIERTA`.  
3. Se agregan productos del menú de la sucursal y se asigna `estacion_preparacion` automáticamente.  
4. Durante la estancia del cliente, la comanda puede modificarse libremente mientras no haya ítems ya preparados o cerrados por política.  
5. Al pedir la cuenta, la comanda pasa a `LISTA` o `ENTREGADA` según el flujo interno y se factura en CU15.  
6. Tras el pago, la comanda queda `CERRADA` y la mesa se libera automáticamente.

#### 2. Comanda PARA LLEVAR
- No requiere mesa ni reserva.  
- Puede asignarse cliente, pero frecuentemente será anónimo.  
- Al terminar la preparación, pasa a `LISTA` y luego se factura en CU15.

#### 3. Comanda ONLINE
- Se crea automáticamente desde CU20/CU21 con `tipo_servicio='ONLINE'` e `id_carrito` relacionado.  
- Estado inicial: `PENDIENTE_PAGO`. Cuando el cliente confirma el pago en CU21, la nota de venta se emite y la comanda pasa a `ABIERTA` (para iniciar preparación en cocina/barra).  
- La modificación de ítems no está permitida una vez que el pago ha sido cursado, debido a que la transacción principal (nota de venta) ya se generó.

### Funcionalidades extras
- Impresión de la comanda en cocina/barra automáticamente al agregar ítems.  
- Fusión de comandas de una misma mesa.  
- Notificación en tiempo real a cocina/barra y mesero cuando cambian los ítems o la comanda.
- Concurrencia: Si dos usuarios agregan productos a la misma comanda simultáneamente. Se deben manejar transacciones para evitar inconsistencias.
- Modificación hasta facturación: la comanda puede editarse mientras no esté `CERRADA`, `CANCELADA` o en un estado terminal. Los ítems en `EN_PREPARACION` no deben cancelarse si la cocina ya los inició.  
- Unicidad de número: generar `numero_comanda` con formato para evitar colisiones.  

### Tablas implicadas
| Tabla              | Operación                                     | Motivo |
|--------------------|-----------------------------------------------|--------|
| `comanda`          | Inserción, actualización, consulta            | Cabecera de la orden |
| `detalle_comanda`  | Inserción, actualización, borrado lógico      | Líneas de productos |
| `producto_final`   | Lectura                                       | Mostrar menú y precios |
| `producto_sucursal`| Lectura (precio, disponibilidad)              | Precio de la sucursal |
| `mesa`             | Lectura y actualización (cambiar estado)      | Ocupar/liberar mesa |
| `cliente`          | Lectura (o inserción si se crea nuevo)        | Asignar cliente a la comanda |
| `sucursal`         | Lectura                                       | Sucursal de la comanda |
| `log_auditoria`    | Inserción                                     | Auditoría |

### Dependencias
- **Ciclo 1:** Sucursales (CU06), Mesas (CU28), Sectores (CU27), Clientes (CU29).  
- **Ciclo 2:** Productos finales (CU09) con precios por sucursal.  
- **Ciclo 3:** La comanda se enlaza con CU15 (Ventas Presenciales) para facturar, con CU20 (Carrito Online) cuando viene de web, y con CU25 (Preparación) para que cocina/barra vean y avancen los ítems.  
- **Ciclo 4:** Las comandas cerradas alimentarán reportes de ventas y dashboard (CU23/CU24).

### Regla multi‑sucursal
- El Administrador y el Mesero solo pueden crear y ver comandas de la sucursal donde están asignados, según `empleado_sucursal`.  
- El SU puede cambiar de sucursal y ver todas.

### Tecnologías
- **WebSockets** (o Server-Sent Events) para notificar a cocina/barra en tiempo real cuando se agregan o actualizan ítems.

---

## CU15 – Gestionar Ventas Presenciales
**Actor:** Superusuario (SU), Administrador (A), Cajero (CJ)
**Prioridad:** Alta

### Objetivo
Convertir una comanda de salón o para llevar en una venta formal, seleccionando método de pago y aplicando descuentos/propina. Al finalizar se libera la mesa y se entrega comprobante.

### Flujo principal
1. **Seleccionar comanda a facturar:**
   - El cajero selecciona la sucursal y ve las comandas en estado `LISTA` o `ENTREGADA` (las que el mesero marcó como listas para cobrar).
   - Se carga la comanda con todos sus detalles y total calculado desde `detalle_comanda`.

2. **Configurar la venta / Aplicar ajustes:**
   - Cliente nulo: la nota de venta permite `id_cliente=NULL`. Las ventas anónimas no acumulan puntos de fidelidad.
   - Mostrar subtotal (suma de los items de `detalle_comanda`).
   - **Aplicar ajustes:**
     - Descuento manual (porcentaje o monto fijo) sobre el subtotal.
     - Propina (monto o porcentaje, opcional).
     - Impuesto (ya configurado en el sistema, por defecto IVA).
     - Total = subtotal - descuento + impuesto + propina.
   - **Asignar cliente:** puede ser el de la comanda o uno nuevo. Si se asigna, se puede registrar el NIT para factura. Puede ser nulo (venta anónima).
   - **Seleccionar método de pago:** el sistema lista todos los métodos activos de `metodo_pago`. Desde el frontend de caja se ocultan los métodos no presenciales (por ejemplo, no mostrar “PayPal” si no aplica en local). El cajero elige uno (efectivo, tarjeta, QR, etc.).

3. **Emitir nota de venta:**
   - Se inserta `nota_venta` y sus detalles, copiando los datos de `detalle_comanda` (incluido el `costo_unitario` que debe venir de un campo calculado en `producto_sucursal`).
   - La comanda se marca como `CERRADA` y, si era en mesa, la mesa se libera (`DISPONIBLE`).
   - Se genera un comprobante (ticket) y opcionalmente una factura electrónica (extra).

### Funcionalidades extras
- **Cupones/vouchers (extras):** preparar el modelo para que en ciclo 4 se pueda aplicar un cupón (descuento adicional) antes de emitir la venta. Por ahora se puede dejar un campo `codigo_cupon` opcional en la nota de venta, pero sin lógica de validación.

### Tablas implicadas
| Tabla                | Operación                           | Motivo |
|----------------------|-------------------------------------|--------|
| `comanda`            | Lectura, actualización (estado)     | Obtener datos, cerrar |
| `detalle_comanda`    | Lectura                             | Obtener items |
| `nota_venta`         | Inserción                           | Cabecera de la venta |
| `detalle_nota_venta` | Inserción                           | Líneas de la venta |
| `metodo_pago`        | Lectura                             | Selección de método |
| `cliente`            | Lectura (y actualización de puntos) | Cliente para factura |
| `producto_sucursal`  | Lectura                             | Precios y costos |
| `mesa`               | Actualización (liberar)             | Si la comanda era en mesa |
| `log_auditoria`      | Inserción                           | Auditoría |

### Dependencias
- **Ciclo 2:** Recetas (para obtener costo unitario).
- **Ciclo 3:** Comandas (CU14), Preparación (CU25, para saber estado).
- **Ciclo 4:** Caja (CU22) – se conecta directamente. Promociones (CU16) se validan aquí.
- **Reportes (CU23):** datos de ventas.

### Regla multi‑sucursal
- El Cajero y Admin solo operan sobre comandas y ventas de su sucursal.
- El SU puede cambiar de sucursal.

### Tecnologías
- **Impresión térmica:** usar librerías como `escpos` o servicio de impresión local para generar el comprobante.
- **WebSockets:** para notificar al mesero que la mesa ya fue facturada y liberada en tiempo real.

---

## CU19 – Gestionar Catálogo Online
**Actor:** Administrador (A), Cliente (C)
**Prioridad:** Media

### Objetivo
Permitir que el administrador configure qué productos se muestran al cliente en el portal de pedidos, y que el cliente pueda navegar el menú digital.

### Flujo – Vista Admin 
1. **Acceso y Listado:** El administrador accede a la sección “Catálogo Online” de su sucursal.
2. **Visibilidad de Productos:** Ve la lista de todos los productos de la sucursal (de `producto_sucursal`) con un indicador de visibilidad.
   - **Regla de negocio:** Se usa el campo `disponible` de `producto_sucursal` como “visible en online”. Si el producto tiene `disponible = TRUE`, se muestra en el catálogo online de esa sucursal; si `FALSE`, no. (No se crean campos extra como `publicado_online`).
3. **Gestión:** El admin activa/desactiva productos para su sucursal (modifica `disponible`). También puede editar el precio (el mismo de `producto_sucursal`).
4. **Configuración de Contenido:** Permite configurar unicamente precio y disponibilidad. `producto_sucursal`
5. **Vista Previa:** Existe un modo vista previa que muestra exactamente lo que verá el cliente.

### Flujo – Vista Cliente
1. **Selección de Sucursal:** El cliente elige la sucursal. El catálogo muestra solo los productos de esa sucursal.
2. **Navegación:** Navega por categorías y productos, con búsqueda por texto (filtros por categoría y búsqueda).
3. **Detalle de Producto:** Ve fotos, descripción, precio y tiempo de preparación.
4. **Acción:** Desde aquí añade productos al carrito (conecta con CU20).

### Funcionalidades Extras
- **Disponibilidad y stock:** Aunque un producto esté `disponible=TRUE`, el stock real puede estar agotado. Por producto debe mostrar si hay stock disponible, sino automomaticamente marcarlo como no disponible y no permitir edicion de disponibilidad de ese producto hasta que se reponga el stock.
- **Caché de catálogo:** Cachear la respuesta de categorías/productos con **Redis** (TTL de 15 min) para aligerar la carga en la base de datos.

### Tablas implicadas
| Tabla              | Operación          | Motivo |
|--------------------|--------------------|--------|
| `producto_final`   | Lectura, actualización (Admin) | Datos del producto |
| `producto_sucursal`| Lectura, actualización (Admin) | Precio, disponibilidad |
| `categoria`        | Lectura            | Navegación y filtros |

### Dependencias
- **Ciclo 2:** Productos finales (CU09) y Categorías (CU08).
- **Ciclo 3:** Carrito (CU20) usa sus productos.
- **Ciclo 4:** Promociones (CU16) pueden resaltarse aquí.

### Regla multi‑sucursal
- El cliente elige la sucursal; el catálogo se filtra por `producto_sucursal` asociada.
- El Administrador configura solo su sucursal; el SU puede hacerlo para cualquiera.

### Tecnologías
- **Búsqueda y filtros:** Usar **índices de texto completo** de PostgreSQL (`tsvector`) para búsqueda avanzada.


---

## CU20 – Gestionar Carrito de Compras Online
**Actor:** Cliente (C) (autenticado o anónimo con sesión temporal)
**Prioridad:** Media

### Objetivo
Permitir al cliente acumular productos de una sucursal y luego convertirlos en un pedido (comanda) cuando decida comprar.

### Flujo principal
1. **Agregar al carrito:**
   - El cliente pulsa “Añadir al carrito”.
   - El frontend envía `id_producto_final`, cantidad y notas.
   - El backend valida que el producto exista y esté `disponible` en la sucursal.
    - **Almacenamiento con Redis:** Se usa Redis (clave `cart:{session_id}` o `cart:{user_id}`) para rapidez, atomización y expiración automática (TTL 30 min).
   - Si el producto ya existe, se incrementa la cantidad.

2. **Ver y Editar carrito:**
   - Se recupera el carrito desde Redis.
   - Se recalculan subtotales y total con los precios actuales de `producto_sucursal` (no fiarse de precios cacheados).
   - Permite actualizar cantidades o eliminar items.

3. **Cambio de Sucursal:**
   - Si el cliente cambia de sucursal, el backend verifica disponibilidad.
   - Los productos no disponibles en la nueva sucursal se marcan o notifican. **No se permite el pedido** hasta corregir el carrito.

4. **Realizar pedido (Checkout):**
   - El cliente **debe estar autenticado**. Si es anónimo, se redirige al login y se migra el carrito en Redis al id del usuario.
   - Se crea una comanda `ONLINE` en la BD con `estado = 'PENDIENTE_PAGO'`.
   - Los ítems se copian a `detalle_comanda`.
   - El carrito en Redis se elimina cuando se confirma la compra.

### Funcionalidades extras
- **Recalcular precios:** Siempre obtener precios actualizados de la BD durante el checkout.
- **Sincronización:** Carrito persistente para usuario anonimo. Si el cliente se autentica después de agregar productos, el carrito se asocia a su cuenta.

### Tablas implicadas
| Tabla              | Operación                                        | Motivo |
|--------------------|--------------------------------------------------|--------|
| `carrito_compras`  | Inserción, actualización (historial)             | Registro físico solo al convertir |
| `item_carrito`     | Inserción (historial)                            | Detalle físico solo al convertir |
| `producto_final`   | Lectura                                          | Validar existencia y mostrar datos |
| `producto_sucursal`| Lectura (precio, disponibilidad)                 | Validación y precio actual |
| `comanda`          | Inserción (al convertir)                         | Generar pedido online |
| `detalle_comanda`  | Inserción (copiar items)                         | Detalle de la comanda |
| `cliente`          | Lectura                                          | Cliente autenticado |

### Dependencias
- **Ciclo 2:** Productos (CU09).
- **Ciclo 3:** Catálogo online (CU19), Pago (CU21) que tomará la comanda generada.
- **Ciclo 3/4:** Preparación (CU25) y reportes.

### Regla multi‑sucursal
- El carrito es exclusivo de la sucursal seleccionada. Al cambiar de sucursal se debe re-validar el contenido.

### Tecnologías
- **Redis** (`ioredis` o similar) para almacenamiento temporal.
- **TTL:** Expiración de 30 minutos (`EXPIRE`).

---

## CU21 – Gestionar Métodos de Pago y Pasarela de Pago
**Actor:** Cliente (C), Administrador (A)
**Prioridad:** Media

### División del caso de uso
Este CU se divide en dos partes:
1. **Administración de métodos de pago:** Configuración de los métodos disponibles.
2. **Pasarela de pago para el cliente (Online):** Proceso de pago electrónico.

### Parte 1 – Administración de métodos de pago (Admin)
- El Admin visualiza la lista de métodos de pago en `metodo_pago`.
- Puede editar comisión, descripción y activar/desactivar cada método.
- **No puede crear nuevos métodos**, ya que cada uno requiere una integración técnica predefinida (PayPal, Stripe, QR, etc.).

### Parte 2 – Pasarela de pago (Cliente Online)
1. **Selección:** Tras confirmar el carrito (CU20) y generarse la comanda en `PENDIENTE_PAGO`, se redirige al cliente a la selección de métodos online (filtrados para excluir efectivo).

2. **Inicio de Transacción:**
   - Se crea la `nota_venta` provisional en estado `EMITIDA` (pero no pagada).
   - Se crea un registro en `transaccion_online` con estado `PENDIENTE`.
   - Se invoca al SDK de la pasarela (Stripe/PayPal).
3. **Confirmación (Webhook/Callback):**
   - **Pago Exitoso:** `transaccion_online` → `APROBADA`, `nota_venta` → `PAGADA`, `comanda` → `ABIERTA` (para cocina).
   - **Pago Rechazado:** `transaccion_online` → `RECHAZADA`, se notifica al cliente para reintentar.

### Funcionalidades extras
- **Idempotencia:**  Para evitar cobros dobles por reintentos o fallos de red. Simular pagos por sandbox (cuentas de prueba).
- **Seguridad:** Validar las firmas de los webhooks de la pasarela.
- **Factura electrónica:** Preparar la estructura de `nota_venta` para que en ciclo 4 se pueda generar factura electrónica con los datos del cliente (NIT, dirección, etc.) si se proporcionan.

### Tablas implicadas
| Tabla                | Operación                           | Motivo |
|----------------------|-------------------------------------|--------|
| `nota_venta`         | Inserción, actualización            | Registro de la venta online |
| `detalle_nota_venta` | Inserción                           | Detalle de la venta |
| `transaccion_online` | Inserción, actualización            | Control y estado del pago electrónico |
| `metodo_pago`        | Lectura, actualización (Admin)      | Configuración de pasarelas |
| `comanda`            | Lectura, actualización (estado)     | Origen del pedido |
| `cliente`            | Lectura                             | Datos del pagador |

### Dependencias
- **Ciclo 3:** Comanda online (CU20), Ventas (CU15) para la estructura de la nota.
- **Externas:** APIs de Stripe/PayPal.

### Regla multi‑sucursal
- El pago se procesa bajo el contexto de la sucursal de la comanda.

### Tecnologías
- **Pasarela:** SDK Stripe / PayPal REST API.
- **Webhooks:** Endpoints seguros con validación de firma.

---

## CU25 – Gestionar Preparación de Comanda
**Actor:** Cocinero (CN), Bartender (B), Superusuario (SU)
**Prioridad:** Alta

### Objetivo
Pantalla de cocina/barra que muestra los ítems pendientes, permite avanzar su estado y descuenta automáticamente el inventario cuando un ítem se marca como “LISTO”.

### Flujo principal
1. **Vista de cola:** Se muestran todos los `detalle_comanda` con estado `PENDIENTE` o `EN_PREPARACION`, filtrados por `estacion_preparacion` correspondiente al rol (Cocinero ve `COCINA`, Bartender ve `BARRA`).
   - Se ordenan por tiempo de solicitud (FIFO de pedidos).
   - Se agrupan por comanda (número de comanda, mesa si aplica).
   - Se actualiza en **tiempo real** vía WebSockets cuando un mesero agrega un nuevo ítem.

2. **Tomar ítem:** El cocinero/bartender selecciona un ítem y lo pasa a `EN_PREPARACION`. Esto notifica al mesero y evita que otros cocineros tomen el mismo ítem.

3. **Marcar como LISTO (Descuento de Inventario):**
   - El cocinero confirma que el producto está terminado.
   - **Búsqueda de Receta:** El sistema busca la receta asociada al producto final (`receta` e `ingrediente_receta`). Todo producto en cocina DEBE tener receta para este proceso.
   - **Ajuste de Stock:** Por cada ingrediente, se llama a la función `ajuste_stock(id_stock, -cantidad)` (o equivalente). Esta función se encarga de:
     - Restar la cantidad del lote más antiguo disponible (**FIFO de lotes**).
     - Actualizar el total del `stock_sucursal`.
   - **Validación de Stock:** Si no hay stock suficiente, el sistema debe rechazar la operación, mostrar una alerta y notificar al administrador. El ítem no puede pasar a `LISTO` sin insumos.
   - Si el descuento es exitoso, el ítem pasa a estado `LISTO`.

4. **Actualización de Comanda:**
   - Cuando todos los ítems de una comanda están en `LISTO`, la comanda pasa automáticamente a estado `LISTA`.
   - Queda disponible para facturación en mesa/llevar (CU15) o para entrega directa al cliente en el mostrador si es online.

### Funcionalidades extras
- **WebSockets:** Los cambios de estado deben transmitirse inmediatamente a la pantalla de cocina y al mesero.
- Sonido al caer nueva comanda.

### Tablas implicadas
| Tabla                | Operación                            | Motivo |
|----------------------|--------------------------------------|--------|
| `comanda`            | Lectura, actualización (estado)      | Cabecera de la orden |
| `detalle_comanda`    | Lectura, actualización               | Cambio de estado de items |
| `receta` / `ingrediente_receta` | Lectura                      | Obtener insumos a descontar |
| `stock_sucursal`     | Actualización (cantidad)             | Descontar inventario real |
| `lote_inventario`    | Lectura / Actualización              | Aplicar FIFO de lotes |
| `log_auditoria`      | Inserción                            | Auditoría de movimientos |

### Dependencias
- **Ciclo 2:** Recetas (CU10) e Inventario (CU07).
- **Ciclo 3:** Comandas (CU14) para el origen de datos.
- **Ciclo 4:** Reportes de consumo y merma (CU23).

### Tecnologías
- **WebSockets:** Para notificaciones en tiempo real (nuevos pedidos y cambios de estado).
- **FIFO con lotes:** Lógica de selección del lote más antiguo con estado `DISPONIBLE` y cantidad > 0.

---


