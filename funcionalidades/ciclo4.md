
## CU13 – Gestionar Alertas de Inventario
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Baja  

### Descripción funcional
Permite configurar y visualizar alertas automáticas cuando el stock de un insumo en una sucursal se encuentra por debajo del mínimo definido o cuando un lote está próximo a vencer. Gestiona el ciclo de vida de la alerta (NO_LEIDA, LEIDA, RESUELTA).

### Flujo principal (mínimo viable)
1. **Configuración de umbrales**  
   - La `cantidad_minima` se lee directamente de la tabla `stock_sucursal`.
   - Se debe definir una cantidad de días para alerta de vencimiento de lote (por ejemplo, 7 días antes). Se recomienda agregar este valor como una nueva columna en la tabla `sucursal` (ej. `dias_alerta_vencimiento`).

2. **Generación automática (Cron o Eventos)**
   - El sistema evalúa periódicamente (ej. una vez al día o tras una venta) si `stock_sucursal.cantidad <= stock_sucursal.cantidad_minima` y crea un registro en `alerta_inv` con tipo `STOCK_MINIMO`.
   - Evalúa `lote_inventario` buscando lotes próximos a vencer y crea un registro con tipo `VENCIMIENTO_PROXIMO`.
   - Si una alerta del mismo tipo para ese insumo/lote ya existe y no está resuelta, no se duplica.

3. **Visualización de alertas**  
   - El sistema lista los registros activos de la tabla `alerta_inv` con estado `NO_LEIDA` o `LEIDA`.  
   - **Administrador (A):** Solo ve las alertas filtradas por `id_sucursal` que coincide con su sucursal.  
   - **Superusuario (SU):** Ve las alertas de todas las sucursales, pudiendo filtrar.

4. **Gestión y Acciones rápidas**  
   - El usuario puede marcar la alerta como `LEIDA`.
   - Desde la alerta, el usuario puede navegar al insumo para crear una compra (CU12) o ajustar el stock (CU07).
   - **Resolución automática:** Si el stock vuelve a niveles normales mediante una compra, o el lote vence/se descarta, el sistema debe cambiar el estado de la alerta a `RESUELTA` y registrar la `fecha_resolucion`.

### Funcionalidades extras
- Alertas de stock excesivo (cuando supera `cantidad_maxima`), útil para evitar sobrecompras (crea alerta tipo `STOCK_MAXIMO`).
- Notificaciones WebSocket o Push para mostrar alertas en el panel sin recargar.

### Tablas implicadas
| Tabla              | Operación                      | Motivo |
|--------------------|--------------------------------|--------|
| `alerta_inv`       | CRUD                           | **Tabla Principal.** Almacena la alerta generada. Campos clave: `id_sucursal`, `id_stock`, `id_lote`, `tipo`, `estado`, `fecha_generacion`. |
| `stock_sucursal`   | Lectura                        | Configuración (`cantidad_minima`) y evaluación de alertas de stock. |
| `lote_inventario`  | Lectura                        | Evaluación de alertas de vencimiento (`fecha_vencimiento`). |
| `sucursal`         | Lectura / Actualización        | Filtro por sucursal. Opcionalmente añadir columna `dias_alerta_vencimiento`. |

### Dependencias y Conexiones
- **Ciclo 2:** Inventario (CU07) define los stocks y lotes.  
- **Ciclo 4:** Las alertas pueden desencadenar compras (CU12) o informes (CU23). Las entradas de inventario desde Compras deben intentar resolver alertas pendientes.
- **Ciclo 1:** Sucursales (CU06). La regla multi-sucursal exige filtrar siempre por `id_sucursal`.

### Tecnologías
- **Tareas programadas (cron jobs)** para generar alertas diarias o en tiempo real.  
- **Notificaciones WebSocket** para mostrar alertas en el panel sin recargar.
- **Notificaciones Toast o Push** para notificar las alertas. 

---


## CU17 – Gestionar Notas de Salida
**Actor:** Administrador (A), Cajero (CJ), Superusuario (SU)  
**Prioridad:** Alta  

### Descripción funcional
Registrar egresos no operativos (gastos) de la sucursal, como servicios, alquiler, sueldos, mantenimiento, pérdidas, etc. Permite la rendición de cuentas y se refleja obligatoriamente como un movimiento en la caja de la sucursal.

### Flujo principal (mínimo viable)
1. **Listar notas de salida** 
   - Muestra registros filtrados por tipo de gasto, fecha y estado (`REGISTRADO`, `ANULADO`).
   - **Administrador (A) y Cajero (CJ):** Solo ven y operan sobre su propia sucursal (`id_sucursal`).
   - **Superusuario (SU):** Acceso global con filtro de sucursal.

2. **Crear nota de salida**  
   - **Selección de Sucursal:** Automática para A/CJ; elegible para SU.
   - **Datos de cabecera:** Se define el `tipo_gasto` (ej. `SERVICIOS`, `PERDIDA`), la `fecha` y el empleado responsable (`id_empleado`).
   - **Detalle de ítems (`detalle_nota_salida`):** Se agrega descripción, `cantidad` y `monto`. 
     - *Caso Pérdida:* Si corresponde a una merma o pérdida de inventario, se selecciona obligatoriamente el `id_stock_sucursal` afectado.
   - **Cálculo de Totales y Guardado:** El `monto_total` se calcula sumando el monto de los detalles. Estado inicial: `REGISTRADO`.

3. **Relación con inventario (Solo por Pérdidas)**  
   - Si el detalle está vinculado a un `id_stock_sucursal`, el sistema debe descontar del stock físico. Esto implica actualizar `stock_sucursal` y, si maneja lotes, descontar usando lógica FIFO (primero en entrar, primero en salir) en `lote_inventario`.

4. **Anular nota de salida** 
   - Cambia el estado a `ANULADO` (borrado lógico).
   - Revertir el ajuste de inventario (restaurando cantidades a lotes y stock general) si hubo bajas previas.

### Funcionalidades extras
- Programar notas de salida recurrentes (por ejemplo, alquiler fijo).

### Tablas implicadas
| Tabla                  | Operación                                   | Motivo |
|------------------------|---------------------------------------------|--------|
| `nota_salida`          | Inserción, actualización, consulta          | Cabecera del gasto. Campos clave: `id_sucursal`, `tipo_gasto`, `estado`, `monto_total`. |
| `detalle_nota_salida`  | Inserción, consulta                         | Líneas de la nota. Clave: `id_stock_sucursal` (opcional). |
| `stock_sucursal`       | Lectura / Actualización                     | Disminución o restitución (si anulación) de cantidades por pérdida. |
| `lote_inventario`      | Actualización                               | Descuento FIFO si hay lotes registrados para el insumo dañado. |

### Dependencias y Conexiones
- **Ciclo 4 (Caja):** Estrictamente dependiente de CU22 (Caja). No se puede emitir una salida de dinero si la caja está cerrada.
- **Ciclo 2 (Inventario):** Depende de CU07 para inventario y efectuar bajas por pérdida.
- **Ciclo 1 (Sucursales y Empleados):** CU06 y CU26 para ligar el gasto a una sucursal y empleado.

### Tecnologías
- **Transacciones de BD (ACID):** Uso intensivo de `BEGIN`, `COMMIT`, `ROLLBACK` para asegurar que el registro de la nota, la deducción de caja y el descuento de inventario sucedan atómicamente.
- **WebSockets / Eventos:** Para notificar a la terminal de caja sobre un nuevo egreso registrado desde el backoffice (si A/SU genera la nota fuera del módulo de TPV).

---

## CU18 – Gestionar Reservas de Mesas
**Actor:** Administrador (A), Mesero (M), Cliente (C)  
**Prioridad:** Baja  

### Descripción funcional
Permite a los clientes reservar mesas para una fecha y hora específica, y al personal del restaurante gestionar el ciclo de vida de esas reservas (confirmar, cancelar, marcar asistencia).

### Flujo principal (mínimo viable)
**Vista Cliente (Portal público):**
1. **Búsqueda:** El cliente selecciona sucursal, fecha, hora deseada y número de personas.  
2. **Selección Visual Cliente:** En lugar de una lista plana, el sistema presenta un **Plano Interactivo** que simula la vista superior del local.
   - Las mesas se colorean según su estado: *Disponible* (Blanco), *Seleccionado* (Verde), *Ocupado/Reservado* (Rojo), *No Disponible* (Negro).
   - Solo se pueden seleccionar mesas cuya capacidad sumada alcance para el número de personas.
3. **Confirmación:** El cliente escoge la(s) mesa(s) haciendo clic en el plano y llena sus datos. Si está autenticado, sus datos (CU04) se asocian automáticamente.  
4. **Registro:** La reserva se guarda en la base de datos con estado `PENDIENTE`.

**Vista Admin/Mesero (Portal negocio):**
1. **Panel de Reservas:** Visualización de todas las reservas del día en un calendario o lista, filtrado por sucursal.  
2. **Confirmar reserva:** El Admin/Mesero revisa la reserva `PENDIENTE` y la pasa a `CONFIRMADA`, registrando la `fecha_confirmacion` y el `id_empleado`.
3. **Recepción (Check-in):** Cuando el cliente llega, se marca su asistencia. La reserva pasa a `EN_CURSO`, y el sistema permite generar automáticamente la **Comanda** (CU14) asociada a esa mesa.  
4. **Cancelación / No Show:** 
   - El cliente puede anular vía portal (estado `CANCELADA`).
   - El personal puede marcarla como `NO_ASISTIO` si pasa la hora límite de tolerancia sin que el cliente llegue.

### Funcionalidades extras
- Envío automático de recordatorio o confirmación por correo electrónico o WhatsApp.  
- Historial de reservas en el perfil del cliente.

### Tablas implicadas
| Tabla          | Operación                          | Motivo |
|----------------|------------------------------------|--------|
| `reserva`      | CRUD                               | Cabecera de la reserva. Campos clave: `estado`, `fecha_reserva`, `hora_inicio`. |
| `reserva_mesa` | Inserción, eliminación             | Tabla puente para asignar una o más mesas a la misma reserva. |
| `mesa`         | Lectura, actualización             | Determinar capacidad y disponibilidad para el renderizado del mapa visual. |
| `cliente`      | Lectura / Inserción                | Vincular la reserva al perfil del usuario. |
| `sucursal`     | Lectura                            | Filtro de disponibilidad. |

### Dependencias y Conexiones
- **Ciclo 1:** Mesas (CU28), Sectores (CU27), Sucursales (CU06). Son la base para renderizar el plano del restaurante.
- **Ciclo 3:** Comandas (CU14). Una reserva `EN_CURSO` es el punto de inicio natural para abrir una mesa.

### Tecnologías
- **Librería de Canvas o CSS Grid Interactivo** para el frontend, permitiendo dibujar el plano de mesas y manejar eventos de clic (ej. `Fabric.js`, `Konva` o Flexbox/Grid).
- **Calendario interactivo** en el panel de administración (FullCalendar o similar) para el admin.  
- **WebSockets:** Para bloquear temporalmente las mesas en tiempo real mientras alguien las está seleccionando (evitar dobles reservas concurrentes).

---

## CU22 – Gestionar Caja
**Actor:** Cajero (CJ), Administrador (A), Superusuario (SU)  
**Prioridad:** Alta  

### Descripción funcional
Este CU es el **único responsable** de la contabilidad operativa en la sucursal (apertura, movimientos, arqueo y cierre). Centraliza y registra absolutamente todos los ingresos y egresos de dinero (por ventas, compras, notas de salida, etc.) asegurando la consistencia financiera.

### Flujo principal (mínimo viable)
1. **Abrir caja**  
   - El Cajero/Admin ingresa el `monto_inicial` de efectivo en su sucursal.
   - El sistema inserta un registro en `caja` con `estado='ABIERTA'`. Solo puede haber una caja abierta por sucursal.

2. **Gestión Centralizada de Movimientos (`movimiento_caja`)**  
   - **Ingresos Automáticos (Ventas):** Al confirmar una `nota_venta` (CU15), este módulo recibe la orden e inserta un `movimiento_caja` (`tipo='INGRESO'`, `concepto='VENTA'`).
   - **Egresos Automáticos (Compras y Gastos):** Al registrar una compra pagada (CU12) o una `nota_salida` (CU17), este módulo inserta un `movimiento_caja` (`tipo='EGRESO'`). *Si la nota de salida se anula, CU22 genera un movimiento compensatorio.*
   - **Movimientos Manuales:** El cajero puede insertar manualmente un `INGRESO_EXTRA` o un `RETIRO` (ej. retiro de efectivo hacia bóveda), registrando el monto y la observación.
   - **Validación Estricta:** Ningún otro CU puede insertar en `movimiento_caja`. Si la caja está cerrada, el sistema debe rechazar transacciones de dinero.

3. **Arqueo y cierre de caja**  
   - El sistema totaliza todos los `movimiento_caja` de la sesión actual y calcula el **saldo esperado** (`monto_inicial + total_ingresos - total_egresos`).
   - El cajero ingresa el **saldo real** (dinero físico en caja).
   - Se cierra la caja (`estado='CERRADA'`, actualiza `fecha_cierre` y `monto_final`). Se registra la diferencia (sobrante/faltante) como observación.

### Funcionalidades extras
- Alertas si el descuadre (faltante) supera un porcentaje configurado.

### Tablas implicadas
| Tabla             | Operación                           | Motivo |
|-------------------|-------------------------------------|--------|
| `caja`            | Inserción, actualización, consulta  | Gestión del ciclo de vida de la caja por sucursal. |
| `movimiento_caja` | CRUD (Solo este CU inserta aquí)    | Tabla core de flujos de dinero. Campos clave: `tipo`, `concepto`, `monto`, llaves foráneas a otras operaciones. |
| `nota_venta`      | Lectura                             | Validar ingresos generados por ventas. |
| `compra`          | Lectura                             | Validar egresos por compras. |
| `nota_salida`     | Lectura                             | Validar egresos por gastos (servicios, alquiler, etc.). |

### Dependencias y Conexiones
- **Transversal:** CU12 (Compras), CU15 (Ventas) y CU17 (Notas de Salida) dependen de este CU para concretar pagos o ingresos.
- **Ciclo 1:** CU06 (Sucursales) y CU26 (Personal) para saber qué sucursal tiene caja y qué empleado la opera.

### Tecnologías
- **Transacciones BD (ACID):** Uso intensivo de Commit/Rollback en operaciones conjuntas (Venta+Caja, Nota+Caja) para evitar inconsistencias.
- **WebSockets:** Para que la terminal del Cajero vea en tiempo real egresos o ingresos automáticos generados por el Administrador desde el backoffice.

---

## CU24 – Dashboard Analítico
**Actor:** Administrador (A), Superusuario (SU)  
**Prioridad:** Media  

### Descripción funcional
Pantalla principal que muestra indicadores clave (KPIs) en forma de gráficos interactivos, permitiendo una visión rápida del estado del negocio y el rendimiento de la sucursal.

### Flujo principal (mínimo viable)
1. **Selección de Contexto:** 
   - **Administrador (A):** Ingresa y ve automáticamente los datos de su sucursal.
   - **Superusuario (SU):** Puede ver métricas globales o filtrar por una sucursal específica.
2. **Visualización de KPIs (Tarjetas Superiores):**  
   - Ventas del día (monto total, número de transacciones).  
   - Ticket promedio.  
   - Productos más vendidos (top 10).  
   - Nivel de inventario crítico (alertas de stock bajo).  
   - Estado de comandas/entregas activas.  
   - Reservas del día.  
3. **Gráficos Interactivos:**  
   - Evolución de ventas (línea de tiempo).  
   - Distribución de ventas por categoría (gráfico de pastel/dona).  
   - Comparativa de ventas mes actual vs mes anterior (barras).  
   - Ranking de empleados (ventas generadas por mozo/cajero).  
4. **Filtros Dinámicos:** 
   - El usuario puede cambiar el rango de fechas (hoy, esta semana, este mes, rango personalizado) y los gráficos y KPIs deben recalcularse al instante.

### Funcionalidades extras
- Arrastrar y soltar (drag & drop) de widgets para personalizar el dashboard.  
- Actualización en tiempo real: los KPIs de ventas del día se actualizan sin recargar al cerrar una cuenta.  
- Proyecciones de ventas basadas en histórico.

### Tablas implicada
- Todos los módulos anteriores. Similar a reportes, lectura de todas las tablas de ventas, inventario, caja, etc., y posiblemente una capa de cache (Redis) para no sobrecargar la base de datos.


### Tecnologías e Implementación
- **Backend / Microservicios:** Se sugiere implementar toda la lógica para el Dashboard desde **el servicio FastAPI ya implementado**. También se puede exponer desde **Spring Boot**.
- **Frontend:** **Chart.js / ApexCharts / ECharts** para los gráficos interactivos.  
- **Caché:** **Redis** para almacenar métricas precalculadas (ej. resumen del mes pasado) y evitar sobrecargar PostgreSQL con lecturas pesadas.  
- **WebSockets:** Opcional, para actualizar KPIs en tiempo real si hay ventas nuevas.
