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

class EventProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private EventProducerService eventProducerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        String destinationTopic = notificationEventDto.eventType().toString().toLowerCase() + ".notification";
        eventProducerService.sendMessage(notificationEventDto);
        verify(kafkaTemplate).send(eq(destinationTopic), eq(notificationEventDto.eventId().toString()),  eq(message));
    }
}
