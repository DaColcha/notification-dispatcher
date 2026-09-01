package dev.dacolcha.emailconsumer.service;

import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationStatus;
import dev.dacolcha.common.dto.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StatusProducer {
    private static final Logger log = LoggerFactory.getLogger(StatusProducer.class);

    @Value("${kafka.topic.notification-status}")
    private String statusTopic;

    private final KafkaTemplate<String, NotificationResult> kafkaTemplate;

    public StatusProducer(KafkaTemplate<String, NotificationResult> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStatus(UUID eventId, NotificationStatus status, String detail, int partition) {
        NotificationResult statusEvent = new NotificationResult(
                eventId,
                status,
                EventType.EMAIL,
                detail,
                partition,
                System.currentTimeMillis()
        );

        log.info("📢 [Producer Estado] Enviando evento de estado a Kafka para eventId: [{}] - Status: [{}]", eventId, status);

        kafkaTemplate.send(statusTopic, eventId.toString(), statusEvent);
    }
}
