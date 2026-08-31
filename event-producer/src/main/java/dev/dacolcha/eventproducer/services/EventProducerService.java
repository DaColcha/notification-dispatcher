package dev.dacolcha.eventproducer.services;

import dev.dacolcha.common.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    public EventProducerService(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(NotificationEvent notificationEvent) {
        String destinationTopic = notificationEvent.eventType().toString().toLowerCase() + ".notification";

        System.out.println("Producing notification event: " + notificationEvent.toString());

        kafkaTemplate.send(destinationTopic, notificationEvent.eventId().toString(), notificationEvent);
    }
}
