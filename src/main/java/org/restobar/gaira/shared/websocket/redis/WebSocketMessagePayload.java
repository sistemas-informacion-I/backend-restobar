package org.restobar.gaira.shared.websocket.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessagePayload {
    private String topic;
    private Object payload;
}
