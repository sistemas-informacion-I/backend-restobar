# Guía de Uso de WebSockets Genéricos en Restobar

Se ha implementado una arquitectura genérica de WebSockets utilizando **Spring Boot + STOMP / SockJS** en el backend y un cliente **STOMP genérico** en el frontend.

El gran beneficio de esta implementación es que **sólo tenemos UNA conexión abierta** entre el cliente y el servidor. Todo el tráfico en tiempo real viajará a través de diferentes "canales" (tópicos). Tus componentes no necesitan configurar websockets cada vez. Además, la conexión valida de forma estricta los tokens JWT y aísla los datos por sucursal.

---

## 🛠️ ¿Cómo usar en Backend (Spring Boot)?

### 1. ¿Cómo emitir un evento?

Se creó un servicio singleton llamado `WebSocketService` que expone un método **genérico** `<T>`. 

Si necesitas notificar algo al frontend (ej. un cambio de estado de una comanda), simplemente inyecta el `WebSocketService` en tu componente y el envías el objeto DTO que el frontend espera. **Es imperativo el uso de sucursales para evitar fugas de información.**

**Ejemplo:** Emisión de un objeto de tipo `ComandaDTO`.
```java
package org.restobar.gaira.modulo_operaciones.comanda.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.restobar.gaira.shared.websocket.WebSocketService;

@Service
@RequiredArgsConstructor
public class ComandaActualizacionService {

    // 1. Inyectas el servicio genérico
    private final WebSocketService webSocketService;

    public void notificarComandaLista(Comanda comanda) {
        
        // 2. Construyes tu DTO o usas el mismo objeto
        ComandaResponseDTO dto = new ComandaResponseDTO(comanda.getId(), "LISTA");

        // 3. ¡Lo envías a un "tópico" restringido por sucursal!
        // Cualquier cliente autorizado de esa sucursal recibirá el evento.
        webSocketService.emitirEventoSucursal(comanda.getSucursal().getId(), "comandas", dto);
    }
}
```

### Reglas básicas Backend
- Toda la configuración del Web Socket corre bajo la ruta `/ws`. No necesitas crear nuevos endpoints ni `@Controllers` para WebSockets.
- Siempre usa `emitirEventoSucursal` si la información es privativa de una sucursal para que la seguridad centralizada (ChannelInterceptor) verifique el acceso.


---

## 🌐 ¿Cómo usar en Frontend (React / TypeScript)?

### 1. ¿Cómo escuchar o suscribirse a un evento?

Del lado de React, se creó un servicio que ya se encarga de manejar la reconexión, la actualización del JWT en cada conexión, encolar peticiones tempranas, y deserializar los JSONs. Solo tienes que hacer **2 pasos**: Importar `wsClient` y llamar a `wsClient.subscribe<T>`.

**Ejemplo en un Componente React:**
```typescript
import { useEffect, useState } from 'react';
import { wsClient } from '@/core/api/websocket-client';

// Define qué tipo de dato esperas que envíe el backend
interface IComandaEvento {
  id: number;
  estado: string;
}

export function PantallaCocina({ sucursalId }: { sucursalId: number }) {
  const [ultimaComanda, setUltimaComanda] = useState<IComandaEvento | null>(null);

  useEffect(() => {
    // 1. Te suscribes al tópico por sucursal y le dices QUÉ estructura esperas (<IComandaEvento>)
    const subscripcion = wsClient.subscribe<IComandaEvento>(`/topic/sucursal/${sucursalId}/comandas`, (data) => {
      console.log("Notificación WebSocket Recibida!", data);
      setUltimaComanda(data);
      // Aquí opcionalmente disparas un mutate de SWR o React-Query para invalidar cachés
    });

    // 2. IMPORTANTÍSIMO: Desuscribirse al desmontar el componente 
    // Para que no queden listeners fantasma
    return () => {
      subscripcion.unsubscribe();
    };
  }, [sucursalId]);

  return (
    <div>
      <h3>Último evento de cocina:</h3>
      {ultimaComanda && <p>Comanda #{ultimaComanda.id} - Estado: {ultimaComanda.estado}</p>}
    </div>
  );
}
```

### Reglas básicas Frontend
- Para garantizar un código tipado y sano, siempre crea tu **Interface / Type** de qué dato esperas y úsalo en el genérico de `subscribe<MiEstructura>`.
- Asegúrate de hacer el `.unsubscribe()` del objeto que retorna el `subscribe()` usando el cleanup method de los `useEffect`.
- Todo el manejo de tokens JWT se hace automáticamente; no debes preocuparte si el usuario cierra sesión y abre con otro, el socket reconecta con el nuevo token.

Eso es todo. No tienen que configurar puertos, librerías, STOMP ni SockJS nunca más. Todo ocurre por un mismo pasillo mágico de red gracias al módulo compartido.
