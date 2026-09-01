package dev.dacolcha.discordconsumer.service;

import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationResult;
import dev.dacolcha.common.dto.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StatusProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationResult> kafkaTemplate;

    @InjectMocks
    private StatusProducer statusProducer;

    private static final String STATUS_TOPIC = "notification-status-updates";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(statusProducer, "statusTopic", STATUS_TOPIC);
    }
    @Test
    void should_sendKafkaMessage_withStatus(){
        UUID eventId = UUID.randomUUID();
        Long timestamp = System.currentTimeMillis();

        NotificationResult notificationStatus= new NotificationResult(
                eventId,
                NotificationStatus.SUCCESS,
                EventType.DISCORD,
                "Entregado",
                0,
                timestamp
        );

        statusProducer.publishStatus(eventId, NotificationStatus.SUCCESS, "Entregado",0);
        verify(kafkaTemplate).send(eq(STATUS_TOPIC),
                eq(notificationStatus.eventId().toString()),  eq(notificationStatus));
    }
}
