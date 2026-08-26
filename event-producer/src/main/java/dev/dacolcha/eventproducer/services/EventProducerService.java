package dev.dacolcha.eventproducer.services;

import dev.dacolcha.eventproducer.dto.NotificationEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;

@Service
public class EventProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String eventTopic;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventProducerService(KafkaTemplate<String, String> kafkaTemplate,
                              @Value("${kafka.topic.events}") String eventTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventTopic = eventTopic;
    }

    public void sendMessage(NotificationEventDto notificationEventDto) {
        String message = objectMapper.writeValueAsString(notificationEventDto);
        System.out.println("Producing notification event: " + message);
        kafkaTemplate.send(eventTopic, notificationEventDto.eventType().toString(),message);
    }
}
