package dev.dacolcha.wsgateway.service;

import dev.dacolcha.common.dto.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatusEventListener {
    private static final Logger log = LoggerFactory.getLogger(StatusEventListener.class);
    private static final String WS_DESTINATION = "/event/updates";

    private final SimpMessagingTemplate messagingTemplate;

    public StatusEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics ="${kafka.topic.notification-status}")
    public void handleStatusUpdate(NotificationResult statusEvent) {
        log.info("📡 [WS Gateway] Recibido evento de Kafka [{}] para canal [{}]. Reenviando a WebSocket STOMP: {}",
                statusEvent.eventId(), statusEvent.eventType(), WS_DESTINATION);

        messagingTemplate.convertAndSend(WS_DESTINATION, statusEvent);
    }
}
