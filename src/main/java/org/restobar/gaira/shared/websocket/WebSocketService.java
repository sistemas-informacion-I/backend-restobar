package org.restobar.gaira.shared.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.shared.websocket.redis.WebSocketMessagePayload;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio centralizado para emitir eventos por WebSocket a los clientes
 * conectados a través del clúster usando Redis Pub/Sub.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public static final String REDIS_WEBSOCKET_CHANNEL = "websocket-events";

    /**
     * Emite un evento a los clientes suscritos al tópico especificado.
     * En lugar de enviarlo localmente, lo publica en Redis para que todos los nodos lo reciban.
     * 
     * @param topic   El canal de suscripción (ej: "/topic/comandas")
     * @param payload El objeto estructurado que representa la información a enviar.
     * @param <T>     El tipo del payload.
     */
    public <T> void emitirEvento(String topic, T payload) {
        try {
            WebSocketMessagePayload message = new WebSocketMessagePayload(topic, payload);
            redisTemplate.convertAndSend(REDIS_WEBSOCKET_CHANNEL, message);
            log.debug("Evento publicado a Redis en canal '{}' para tópico '{}'", REDIS_WEBSOCKET_CHANNEL, topic);
        } catch (Exception e) {
            log.warn("Redis no disponible, enviando directo al WebSocket tópico '{}': {}", topic, e.getMessage());
            try {
                messagingTemplate.convertAndSend(topic, payload);
            } catch (Exception ex) {
                log.error("Error enviando directo al WebSocket tópico '{}': {}", topic, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Emite un evento específicamente a una sucursal para garantizar el aislamiento.
     * El tópico resultante será: /topic/sucursal/{sucursalId}/{subTopic}
     */
    public <T> void emitirEventoSucursal(Long sucursalId, String subTopic, T payload) {
        if (sucursalId == null) {
            log.warn("Intento de emitir evento de sucursal con ID nulo para tema '{}'.", subTopic);
            return;
        }
        String topic = String.format("/topic/sucursal/%d/%s", sucursalId, subTopic);
        emitirEvento(topic, payload);
    }

    /**
     * Emite un evento especifico a los suscriptores de una entrega.
     * El topico resultante sera: /topic/entrega/{idEntrega}/{subTopic}
     */
    public <T> void emitirEventoEntrega(Long idEntrega, String subTopic, T payload) {
        if (idEntrega == null) {
            log.warn("Intento de emitir evento de entrega con ID nulo para tema '{}'.", subTopic);
            return;
        }
        String topic = String.format("/topic/entrega/%d/%s", idEntrega, subTopic);
        emitirEvento(topic, payload);
    }

    /**
     * Emite un evento a todos los repartidores conectados para notificar
     * nuevas entregas o cambios globales (broadcast).
     * El topico resultante sera: /topic/repartidores/{subTopic}
     */
    public <T> void emitirEventoRepartidores(String subTopic, T payload) {
        String topic = String.format("/topic/repartidores/%s", subTopic);
        emitirEvento(topic, payload);
    }

    /**
     * Metodo manejador que se invoca cuando Redis emite un mensaje en el canal.
     * Recibe el mensaje y lo despacha a los clientes conectados LOCALMENTE a este nodo.
     */
    public void handleRedisMessage(WebSocketMessagePayload message) {
        try {
            messagingTemplate.convertAndSend(message.getTopic(), message.getPayload());
            log.debug("Evento recibido desde Redis y despachado al WebSocket tópico '{}'", message.getTopic());
        } catch (Exception e) {
            log.error("Error despachando evento desde Redis al WebSocket tópico '{}': {}", message.getTopic(), e.getMessage(), e);
        }
    }
}
