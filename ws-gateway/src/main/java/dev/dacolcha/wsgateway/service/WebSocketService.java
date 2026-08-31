package dev.dacolcha.wsgateway.service;

import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class WebSocketService {
    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public static final String WS_TOPIC = "/event/updates";

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendStatusUpdate(UUID eventId, NotificationStatus status, EventType channel, String detail) {
        Map<String, Object> updatePayload = Map.of(
                "eventId", eventId.toString(),
                "status", status.toString(),
                "channel", channel.toString(),
                "detail", detail,
                "timestamp", System.currentTimeMillis()
        );

        log.info("📡 [WebSocket] Transmitiendo estado para evento [{}] al canal " + WS_TOPIC, eventId);

        messagingTemplate.convertAndSend( WS_TOPIC, (Object) updatePayload);
    }
}
