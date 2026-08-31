package dev.dacolcha.slackconsumer;

import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationEvent;
import dev.dacolcha.common.dto.NotificationStatus;
import dev.dacolcha.slackconsumer.service.SlackConsumerService;
import dev.dacolcha.slackconsumer.service.SlackSender;
import dev.dacolcha.slackconsumer.service.StatusProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SlackConsumerServiceTest {
    private static final String FALLBACK_WEBHOOK = "https://hooks.slack.com/api/webhooks/fallback-token";

    @Mock
    private SlackSender slackSender;

    @Mock
    private StatusProducer statusProducerMock;

    private SlackConsumerService slackConsumerService;

    @BeforeEach
    void setUp() {
        slackConsumerService = new SlackConsumerService();

        ReflectionTestUtils.setField(slackConsumerService, "slackSender", slackSender);
        ReflectionTestUtils.setField(slackConsumerService, "fallbackWebhookUrl", FALLBACK_WEBHOOK);
        ReflectionTestUtils.setField(slackConsumerService, "statusProducer", statusProducerMock);
    }


    @Test
    void shouldPostEmbedToFallbackWebhookWhenDestinationIsBlank() throws Exception {
        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        NotificationEvent event = new NotificationEvent(
                eventId,
                EventType.SLACK,
                null,
                "Service X deployed"
        );

        slackConsumerService.consumeSlack(event);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(slackSender).send(uriCaptor.capture(), payloadCaptor.capture());

        assertThat(uriCaptor.getValue()).isEqualTo(FALLBACK_WEBHOOK);

        Map<String, Object> payload = payloadCaptor.getValue();
        List<Map<String, Object>> blocks = ((List<Map<String, Object>>) payload.get("blocks"));
        assertThat(blocks).hasSize(2);
        Map<String, Object> content = (Map<String, Object>) blocks.get(1).get("text");
        assertThat(content.get("text")).isEqualTo(event.message());
        verify(statusProducerMock, times(1))
                .publishStatus(
                        eq(event.eventId()),
                        eq(NotificationStatus.SUCCESS),
                        contains("entregado")
                );
    }

    @Test
    void shouldPostEmbedToProvidedDiscordWebhookWhenDestinationIsAWebhookUrl() throws Exception {
        String perEventWebhook = "https://hooks.slack.com/api/webhooks/per-event-token";
        NotificationEvent event = new NotificationEvent(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                EventType.SLACK,
                perEventWebhook,
                "Deploy failed"
        );

        slackConsumerService.consumeSlack(event);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackSender).send(uriCaptor.capture(), ArgumentMatchers.any(Map.class));

        assertThat(uriCaptor.getValue()).isEqualTo(perEventWebhook);
        verify(statusProducerMock, times(1))
                .publishStatus(
                        eq(event.eventId()),
                        eq(NotificationStatus.SUCCESS),
                        contains("entregado")
                );
    }

    @Test
    void shouldRethrowWhenWebhookSendFails() throws Exception {
        doThrow(new RuntimeException("connection refused"))
                .when(slackSender)
                .send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Map.class));

        NotificationEvent event = new NotificationEvent(
                UUID.randomUUID(),
                EventType.SLACK,
                null,
                "this will fail to send"
        );

        slackConsumerService.consumeSlack(event);

        verify(statusProducerMock, times(1))
                .publishStatus(
                        eq(event.eventId()),
                        eq(NotificationStatus.FAILED),
                        contains("Fallo")
                );
    }
}
