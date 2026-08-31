package dev.dacolcha.eventproducer.services;
import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class EventProducerServiceTest {

    @Mock
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @InjectMocks
    private EventProducerService eventProducerService;

    @Test
    void should_sendKafkaMessage_withEvent(){
        NotificationEvent notificationEvent = new NotificationEvent(
                UUID.randomUUID(),
                EventType.EMAIL,
                "da.colcha@gmail.com",
                "Random event notification"
        );

        String destinationTopic = notificationEvent.eventType().toString().toLowerCase() + ".notification";
        eventProducerService.sendMessage(notificationEvent);
        verify(kafkaTemplate).send(eq(destinationTopic), eq(notificationEvent.eventId().toString()),  eq(notificationEvent));
    }
}
