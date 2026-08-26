package dev.dacolcha.eventproducer.services;

import dev.dacolcha.eventproducer.dto.NotificationEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class EventProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(NotificationEventDto notificationEventDto) {
        String message = objectMapper.writeValueAsString(notificationEventDto);
        String destinationTopic = notificationEventDto.eventType().toString().toLowerCase() + ".notification";

        System.out.println("Producing notification event: " + message);

        kafkaTemplate.send(destinationTopic, notificationEventDto.eventId().toString(),message);
    }
}
