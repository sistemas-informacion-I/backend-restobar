## CU01 – Gestionar Sesión  
**Actor:** Superusuario (SU), Administrador (A), Cajero, Mesero, Cocinero, Bartender, Cliente, Proveedor  
**Prioridad:** Alta  

### Descripción funcional
Permite a cualquier actor autenticarse en el sistema mediante un **Portal Único**. Tras el inicio de sesión exitoso, el sistema identifica la **Identidad del Usuario** mediante el campo `tipo_usuario` (S, E, C) y redirige al dashboard correspondiente.

**Reglas de Redirección e Identidad:**
- **Superusuario (S):** Identidad puramente administrativa. Acceso total al Panel Global. Puede previsualizar la Vista de Cliente. **No requiere registro en tablas de extensión (`empleado`/`cliente`)**.
- **Empleado (E):** Identidad operativa/administrativa de sucursal. Requiere obligatoriamente un registro vinculado en la tabla **`empleado`**. Redirige al Dashboard de Gestión.
- **Cliente (C):** Identidad de consumidor. Requiere obligatoriamente un registro vinculado en la tabla **`cliente`**. Redirige a la Vista de Cliente (E-commerce/Reservas).

### Flujo principal (mínimo viable)
1. **Login**  
   - El usuario envía `username` y `password`.  
   - El sistema busca en `usuario` por `username`, valida que `activo = TRUE` y `estado_acceso = 'HABILITADO'`.  
   - Verifica la contraseña con `pgcrypto`.  
   - Si es incorrecta: incrementa `intentos_fallidos`.  
   - Si es exitosa:
     - Reinicia `intentos_fallidos` y genera `token_sesion` y `refresh_token`.
     - **Determinación de Identidad:** El sistema lee el campo `tipo_usuario`.
     - **Carga de Contexto:** 
       * Si `tipo = 'E'`, busca la sucursal activa en `empleado_sucursal`.
       * Si `tipo = 'S'`, establece contexto global.
       * Si `tipo = 'C'`, carga perfil.
   - Inserta registro en `sesion` y retorna tokens + `tipo_usuario` + datos de contexto (sucursal).

2. **Validar sesión**  
   - El sistema recibe el `token_sesion` en cada petición, busca la sesión activa (sin `fecha_cierre`) y verifica que no esté expirada.  
   - Devuelve el `id_usuario` y los roles/perfiles asociados.

3. **Refrescar token**  
   - El cliente envía el `refresh_token`. Se valida su vigencia, se invalida el anterior y se emite un nuevo par de tokens, manteniendo la sesión.

4. **Logout**  
   - Se recibe el `token_sesion` (o el `refresh_token`).  
   - Se actualiza el registro de sesión estableciendo `fecha_cierre = NOW()`.  
   - A partir de ese momento, el token se considera inválido.

### Funcionalidades extras
- Política configurable de intentos máximos y tiempo de bloqueo.  
- Envío de correo electrónico para desbloqueo o recuperación de contraseña (esto podría ir a un sub‑caso de uso de “recuperar acceso”).  
- Historial de sesiones para el propio usuario (visible en su perfil).  

### Tablas implicadas
| Tabla      | Lectura/Escritura | Motivo |
|------------|-------------------|--------|
| `usuario`  | Lectura y actualización | Validar credenciales y campo `tipo_usuario` |
| `empleado` | Lectura           | Extensión para tipo 'E' |
| `cliente`  | Lectura           | Extensión para tipo 'C' |
| `sesion`   | CRUD              | Gestión de sesiones |

### Dependencias y conexiones
- **Ciclo 1:** Necesita que los usuarios existan (CU02, CU26, CU29). Los roles (CU03) y la tabla `rol_usuario` no se tocan directamente aquí; la autorización se hace en una capa superior usando la sesión.  
- **Todos los ciclos:** Es la puerta de entrada a cualquier operación protegida. El middleware de autorización validará el token y cargará los permisos (CU03).  
- **Para el futuro:** Asegurarse de que el token pueda transmitirse por header HTTP y que el sistema de logs (CU05) registre cada inicio de sesión (exitoso/fallido) como evento de auditoría.

---

## CU02 – Gestionar Usuarios y Roles  
**Actor:** Superusuario (SU) 
**Prioridad:** Alta  

### Descripción funcional
El Superusuario gestiona todas las cuentas del sistema, incluyendo la asignación de su Identidad Global (`tipo_usuario`).

### Flujo principal (mínimo viable)
1. **Listar usuarios**  
   - **Superusuario:** Ve todos los usuarios del sistema.
   - Paginado, con filtros por nombre, username, correo y **Tipo de Usuario (S, E, C)**.
   - Muestra los roles actuales y el tipo de usuario de cada uno.

2. **Crear usuario**  
   - Ingresa CI, nombre, apellido, username, contraseña inicial, teléfono, correo, sexo.  
   - **Selecciona Tipo de Usuario (Identidad):** Superusuario, Empleado o Cliente.
   - Asigna `estado_acceso = 'HABILITADO'` y `activo = TRUE`.  
   - Si se van a seleccionar roles, inserta filas en `rol_usuario`.  
   - Nota: Al elegir 'E' o 'C', este CU solo crea la base del usuario; la vinculación operativa (sucursal, legajo, etc.) se realiza en sus respectivos módulos.
   - La contraseña se guarda encriptada.

3. **Editar usuario**  
   - Permite modificar datos personales, cambiar la **Identidad (tipo_usuario)**, restablecer contraseña y cambiar estado (HABILITADO, SUSPENDIDO).

4. **Asignar/quitar roles**  
   - Agrega o desactiva filas en `rol_usuario`. Solo los Superusuarios pueden asignar el rol `SUPERUSER`.

5. **Desactivar usuario**  
   - Pone `activo = FALSE` en `usuario`. No se borra físicamente.

### Funcionalidades extras
- Importación masiva de usuarios desde CSV.  
- Vista previa de los permisos efectivos que hereda un usuario (resolviendo todos sus roles).  
- Forzar cierre de sesiones activas de un usuario al cambiar su estado.

### Tablas implicadas
| Tabla          | Operación | Motivo |
|----------------|-----------|--------|
| `usuario`      | CRUD      | Entidad principal |
| `rol`          | Lectura   | Mostrar lista de roles disponibles |
| `rol_usuario`  | Inserción, actualización (desactivar), borrado lógico | Asignación de roles a usuarios |
| `sesion`       | Actualización (cierre forzado) | Opcional, para cerrar sesiones |

### Dependencias y conexiones
- **Ciclo 1:** Muy relacionado con CU03 (Roles y Permisos) — se necesita que existan roles para poder asignarlos. También con CU05 (Auditoría) porque cada cambio en `usuario` y `rol_usuario` debe quedar registrado.  
- **Futuro:**
  - Los actores especializados (empleado, proveedor, cliente) se apoyan en que ya exista el `usuario`. En CU26 (Gestionar Personal) se debe considerar un flujo integrado (ver más adelante).  
  - La tabla `usuario` tiene `intentos_fallidos` y `estado_acceso`; cualquier cambio aquí impacta directamente en CU01 (Sesión).

---

## CU03 – Gestionar Roles y Permisos  
**Actor:** Superusuario (SU) 
**Prioridad:** Alta  

### Descripción funcional
El Superusuario define los roles del sistema (por ejemplo “Administrador”, “Cajero”, “Mesero”) y determina exactamente qué acciones pueden realizar en cada módulo.

### Flujo principal (mínimo viable)
1. **Listar roles**  
   - Muestra nombre, descripción, nivel de acceso, estado.

2. **Crear/Editar rol**  
   - Nombre único, descripción, nivel de acceso (numérico), activo/inactivo.

3. **Gestionar permisos de un rol**  
   - Se muestra una matriz o lista de todos los permisos disponibles agrupados por módulo.  
   - El Superusuario activa/desactiva las asociaciones en `rol_permiso` (insertando o desactivando filas).  
   - Se puede establecer fecha de expiración a un permiso en ese rol.

### Funcionalidades extras
- Copiar un rol existente para crear uno similar.  
- Vista de “permisos efectivos” que muestra todos los permisos que otorga un rol.  

### Tablas implicadas
| Tabla          | Operación | Motivo |
|----------------|-----------|--------|
| `rol`          | CRUD      | Gestión de roles |
| `permiso`      | Lectura  | Lista de módulos/acciones disponibles |
| `rol_permiso`  | Inserción, actualización (desactivar) | Asociación permisos ↔ rol |

### Dependencias y conexiones
- **Ciclo 1:** CU02 usa los roles creados aquí. CU01 usará los permisos en el backend para autorizar cada endpoint.  
- **Para todos los ciclos:** Es crucial que el equipo defina **desde ya** los permisos que van a necesitar los módulos de inventario, compras, ventas, caja, etc. Por ejemplo:  
  - Inventario: `LEER`, `CREAR`, `ACTUALIZAR`  
  - Ventas: `CREAR_VENTA`, `ANULAR_VENTA`, `VER_HISTORIAL`  
  - Caja: `ABRIR_CAJA`, `CERRAR_CAJA`, `VER_MOVIMIENTOS`  
  Si no se definen ahora, el desarrollo de los siguientes ciclos quedará trabado.

---

## CU04 – Configurar Perfil Personal  
**Actor:** Todos los actores autenticados  
**Prioridad:** Baja  

### Descripción funcional
Cada usuario puede ver y modificar sus propios datos personales y cambiar su contraseña.

### Flujo mínimo
1. **Ver perfil**  
   - Muestra nombre, apellido, CI, username, correo, teléfono, dirección, sexo.

2. **Editar perfil**  
   - Permite modificar los campos anteriores (no CI, no username).  
   - Verifica formato de correo y teléfono.

3. **Cambiar contraseña**  
   - Pide contraseña actual, nueva y confirmación.  
   - Verifica la actual y actualiza `password_hash`.

### Funcionalidades extras
- Subir foto de perfil (requiere agregar una columna en `usuario` o tabla aparte).  
- Ver las sesiones activas y cerrar otras remotamente.

### Tablas implicadas
| Tabla     | Operación          | Motivo |
|-----------|--------------------|--------|
| `usuario` | Lectura y actualización | Datos propios del usuario |

### Dependencias
- Depende de que la sesión esté activa (CU01).  
- No afecta a otros CU. Puede implementarse en cualquier momento.

---

## CU05 – Auditoría de Usuarios  
**Actor:** Superusuario (Global), Administrador (Sucursal)  
**Prioridad:** Alta  

### Descripción funcional
Permite consultar el registro histórico de todas las operaciones realizadas en la base de datos. La visibilidad de los datos depende del tipo de usuario.

### Flujo principal
1. **Consultar logs (Visibilidad Jerárquica)**  
   - **Superusuario (S):** Tiene acceso total. Puede filtrar logs por cualquier sucursal o ver eventos globales del sistema.
   - **Administrador (E):** Acceso restringido. Solo puede ver logs de su propia sucursal (eventos generados por él mismo o sus empleados subordinados).
   - Filtros comunes: Usuario, Tabla, Operación (INSERT, UPDATE, DELETE), Rango de fechas.
   - Muestra datos anteriores y nuevos en formato JSON.

2. **Ver detalle de un registro**  
   - Expande la fila o abre modal mostrando completamente los JSONB de `datos_anteriores` y `datos_nuevos`.

### Funcionalidades extras
- Exportar resultados a CSV/Excel.  
- Gráfico de actividad por usuario/día.  

### Tablas implicadas
| Tabla            | Operación | Motivo |
|------------------|-----------|--------|
| `log_auditoria`  | Lectura   | Consulta de auditoría |
| `usuario`        | Lectura   | Para mostrar nombre del usuario responsable |

### Dependencias y conexiones
- **Fuerte dependencia transversal:** Todos los demás casos de uso deben insertar filas en `log_auditoria` cuando realicen operaciones de escritura. Por tanto, hay que implementar un mecanismo centralizado (triggers o un servicio de auditoría) **antes** de dar por terminado cualquier otro CU.  
- Si no se hace desde el principio, en el ciclo 2 el caos será aún mayor.

---

## CU06 – Gestionar Sucursales  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Alta  

### Descripción funcional
Mantener el catálogo de sucursales. El Superusuario (SU) tiene control total. El Administrador (A) solo puede ver y editar la información de su propia sucursal.

### Flujo mínimo
1. **Listar sucursales:**
   - **Superusuario (SU):** Lista todas las sucursales.
   - **Administrador (A):** Solo ve el registro de su propia sucursal.
2. **Crear sucursal:** Reservado para el Superusuario (SU). Solo podra asignar sucursal si el usuario a asignar tiene el rol de Administrador(A) y es tipo Empleado(E).
3. **Editar sucursal:** 
   - El Superusuario edita cualquier sucursal.
   - El Administrador edita datos de contacto/horarios de la suya.

### Funcionalidades extras
- Visualizar en mapa (si se guardan coordenadas en `ubicacion`).  
- Ver estadísticas asociadas más adelante (ventas por sucursal en dashboard).

### Tablas implicadas
| Tabla      | Operación | Motivo |
|------------|-----------|--------|
| `sucursal` | CRUD      | Gestión de sucursales |

### Dependencias y conexiones
- **Ciclo 1:** Sirve de base para CU27 (Sectores) y CU28 (Mesas).  
- **Ciclo 2 y 3:** Prácticamente todos los demás módulos (inventario por sucursal, comandas, caja, reservas) dependen de que al menos una sucursal esté creada y activa.  
- Es vital que el CU06 quede **completo y estable** porque será la referencia de muchas FK.

---

## CU26 – Gestionar Personal  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Media  

### Descripción funcional
Administrar el registro de empleados. El Administrador (A) solo puede gestionar empleados de su propia sucursal. El Superusuario (SU) gestiona todo el personal y sus asignaciones.

### Flujo mínimo
1. **Listar empleados**
   - **Administrador (A):** Solo ve empleados de su sucursal.
   - **Superusuario (SU):** Ve todos los empleados, con filtro por sucursal.

2. **Crear empleado**  
   - El Administrador crea empleados asignándolos automáticamente a su sucursal.
   - El Superusuario elige la sucursal de destino.
   - Se debe crear un `usuario` (si no existe) a través de un form integrado.
   - Internamente: inserta en `usuario`, luego en `empleado` con el `id_usuario` recién creado.  
   - Asigna sucursal(opcional) mediante `empleado_sucursal` (puede hacerse en el mismo paso o en uno posterior).  
3. **Editar empleado**  
   - Permite modificar salario, turno, fechas, y también los datos de usuario (nombre, etc.).  
4. **Desvincular/remover**  
   - Establece `fecha_finalizacion` en `empleado` o desactiva el usuario.  
   - Opcionalmente, finaliza la asignación a sucursal con `fecha_fin`.

### Funcionalidades extras
- Reasignar empleado entre sucursales conservando histórico de fechas.  
- Ver historial de sucursales por donde pasó.  
- Generar carnet con código QR.

### Tablas implicadas
| Tabla                | Operación                        | Motivo |
|----------------------|----------------------------------|--------|
| `usuario`            | Inserción, actualización         | Datos personales y acceso |
| `empleado`           | Inserción, actualización         | Datos laborales |
| `empleado_sucursal`  | Inserción, actualización (fin)   | Asignación a sucursales |
| `sucursal`           | Lectura                          | Para elegir sucursal al asignar |

### Dependencias y conexiones
- **Ciclo 1:**  
  - Necesita que exista CU02 (Usuarios) para la creación de usuarios, pero lo recomendable es que **el propio CU26 gestione la creación del usuario** para no duplicar flujos. El administrador no debería tener que ir primero a “Crear Usuario” y luego a “Crear Empleado”. Puedes simplificarlo haciendo que CU26 invoque internamente la lógica de registro de `usuario` (la misma que usa CU02).  
  - También depende de CU06 (Sucursales).  
- **Futuro:**  
  - Los empleados van a ser los Cajeros, Meseros, Cocineros… que aparecen como actores. Cuando se abra una Caja (CU22) o se asigne una Comanda (CU14), se seleccionará un empleado. Por tanto, el `id_empleado` debe estar correctamente vinculado a un usuario con el rol apropiado (Cajero, Mesero…). Conviene comprobar esa coherencia desde YA.

---

## CU27 – Gestionar Sectores  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Alta  

### Descripción funcional
Configurar las áreas en las que se divide una sucursal. Estos datos son específicos de cada sucursal.

### Flujo mínimo
1. **Listar sectores:**
   - El sistema filtra automáticamente por la sucursal activa del Administrador o la seleccionada por el Superusuario.
2. **Crear sector:** El Administrador lo crea para su sucursal; el Superusuario para la activa.
3. **Editar sector** y desactivarlo.

### Funcionalidades extras
- Configurar colores o íconos para ayudar a la vista de planta (aunque no se guarde en BD).  

### Tablas implicadas
| Tabla      | Operación | Motivo |
|------------|-----------|--------|
| `sucursal` | Lectura   | Para mostrar/validar la sucursal |
| `sector`   | CRUD      | Gestión de sectores |

### Dependencias
- Depende de CU06 (Sucursales).  
- **Ciclo siguiente cercano:** CU28 (Mesas) se ancla directamente en `sector`.

---

## CU28 – Gestionar Mesas  
**Actor:** Superusuario (SU), Administrador (A)  
**Prioridad:** Alta  

### Descripción funcional
Manejar el inventario de mesas dentro de cada sector. Proceso restringido a la sucursal activa.

### Flujo mínimo
1. **Listar mesas:** Filtrado por sector de la sucursal activa.
2. **Crear mesa:** El Administrador lo crea para su sucursal.
3. **Editar mesa** (capacidad, número) y cambiar manualmente el estado (por ejemplo, a `FUERA_SERVICIO`).  
4. **Desactivar** (`activo = FALSE`).

### Funcionalidades extras
- Plano gráfico de mesas (drag & drop). No es requisito mínimo pero es un plus visual.  
- Cambiar estado de varias mesas a la vez (por ejemplo, al iniciar jornada).

### Tablas implicadas
| Tabla    | Operación | Motivo |
|----------|-----------|--------|
| `sector` | Lectura   | Para asociar la mesa a un sector |
| `mesa`   | CRUD      | Gestión de mesas |

### Dependencias
- Depende de CU27 (Sectores).  
- **Futuro ciclo 4:** El estado `disponibilidad` será manipulado por CU18 (Reservas) y CU14 (Comandas). Por eso, el CU28 debe **permitir al administrador forzar el estado** pero también asegurarse de que otros procesos puedan leer y modificar ese campo mediante API.

---

## CU29 – Registrarse en el Sistema  
**Actor:** Cliente (Público General)  
**Prioridad:** Media  

### Descripción funcional
Permite a un usuario externo crear su propia cuenta desde la opción de "Registrarse" del **Portal Único**. Este flujo está restringido a la creación de perfiles de tipo **Cliente**.

### Flujo principal
1. **Acceso:** El usuario selecciona "Crear Cuenta" en la pantalla principal.
2. **Formulario de registro:** CI, nombre, apellido, username, contraseña, correo, teléfono.
3. **Creación de cuenta**  
   - Inserta en `usuario` con `activo = TRUE` y `estado_acceso = 'HABILITADO'`.  
   - Inserta en `cliente` vinculando al mismo `id_usuario`, con valores por defecto: `puntos_fidelidad = 0`, `nivel_cliente = 'REGULAR'`.  
4. **Confirmación** (opcional) – puede enviarse un correo de bienvenida o un enlace de activación.  
5. **Inicio de sesión automático** (o redirigir a login).

### Funcionalidades extras
- Registro con redes sociales (requiere columnas adicionales o tabla aparte).  
- Verificación de correo electrónico para activar la cuenta.  
- Política de aceptación de términos y condiciones.

### Tablas implicadas
| Tabla      | Operación                  | Motivo |
|------------|----------------------------|--------|
| `usuario`  | Inserción                  | Cuenta de acceso del cliente |
| `cliente`  | Inserción                  | Perfil específico de cliente |

### Dependencias y conexiones
- **Ciclo 1:** Se apoya en el mismo mecanismo de encriptación de contraseña y validaciones que CU02.  
- **Futuro:**
  - El cliente registrado puede iniciar sesión con CU01.  
  - La tabla `cliente` será esencial en el e‑commerce (carrito de compras, transacciones online) y en las reservas de mesas.  
  - Asegurarse de que el `cliente` tenga un `nit` opcional para facturación (campo ya existe).  

---
