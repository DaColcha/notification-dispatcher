package dev.dacolcha.eventproducer.services;
import dev.dacolcha.eventproducer.dto.EventType;
import dev.dacolcha.eventproducer.dto.NotificationEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.eq;

class EventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private EventProducerService eventProducerService;

    private final String eventTopic = "event-notification";
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        try {
            java.lang.reflect.Field topicField = EventProducerService.class.getDeclaredField("eventTopic");
            topicField.setAccessible(true);
            topicField.set(eventProducerService, eventTopic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void should_sendKafkaMessage_withEvent(){
        NotificationEventDto notificationEventDto = new NotificationEventDto(
                UUID.randomUUID(),
                EventType.EMAIL,
                "da.colcha@gmail.com",
                "Random event notification"
        );

        String message = objectMapper.writeValueAsString(notificationEventDto);
        eventProducerService.sendMessage(notificationEventDto);
        verify(kafkaTemplate).send(eq(eventTopic), eq(notificationEventDto.eventType().toString()),  eq(message));
    }
}
