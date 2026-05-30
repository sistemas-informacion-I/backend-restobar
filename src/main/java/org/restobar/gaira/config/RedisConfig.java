package org.restobar.gaira.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.restobar.gaira.shared.websocket.WebSocketService;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Serializadores para claves y valores
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
    MessageListenerAdapter messageListener(WebSocketService webSocketService) {
        // Le indicamos que llame al método handleRedisMessage cuando reciba un evento
        MessageListenerAdapter adapter = new MessageListenerAdapter(webSocketService, "handleRedisMessage");
        // Aseguramos que se use el mismo deserializador que para escribir
        adapter.setSerializer(new GenericJackson2JsonRedisSerializer());
        return adapter;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // Nos suscribimos al canal "websocket-events"
        container.addMessageListener(messageListener, new ChannelTopic(WebSocketService.REDIS_WEBSOCKET_CHANNEL));
        return container;
    }
}
