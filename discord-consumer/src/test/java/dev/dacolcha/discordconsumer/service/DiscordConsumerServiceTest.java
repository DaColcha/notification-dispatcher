package dev.dacolcha.discordconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationEvent;
import dev.dacolcha.common.dto.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordConsumerServiceTest {

    private static final String FALLBACK_WEBHOOK = "https://discord.com/api/webhooks/fallback-token";

    @Mock
    private DiscordWebhookSender webhookSender;

    @Mock
    private StatusProducer statusProducerMock;

    private DiscordConsumerService discordConsumerService;

    @BeforeEach
    void setUp() {
        discordConsumerService = new DiscordConsumerService();

        ReflectionTestUtils.setField(discordConsumerService, "webhookSender", webhookSender);
        ReflectionTestUtils.setField(discordConsumerService, "fallbackWebhookUrl", FALLBACK_WEBHOOK);
        ReflectionTestUtils.setField(discordConsumerService, "statusProducer", statusProducerMock);
    }


    @Test
    void shouldPostEmbedToFallbackWebhookWhenDestinationIsBlank() throws Exception {
        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        NotificationEvent event = new NotificationEvent(
                eventId,
                EventType.DISCORD,
                null,
                "Service X deployed"
        );

        discordConsumerService.consumeDiscord(event);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webhookSender).send(uriCaptor.capture(), payloadCaptor.capture());

        assertThat(uriCaptor.getValue()).isEqualTo(FALLBACK_WEBHOOK);

        Map<String, Object> payload = payloadCaptor.getValue();
        assertThat(payload.get("username")).isEqualTo("Event Pulse");


        List<Map<String, Object>> embeds = ((List<Map<String, Object>>) payload.get("embeds"));
        assertThat(embeds).hasSize(1);
        assertThat(embeds.get(0).get("description")).isEqualTo(event.message());
    }

    @Test
    void shouldPostEmbedToProvidedDiscordWebhookWhenDestinationIsAWebhookUrl() throws Exception {
        String perEventWebhook = "https://discord.com/api/webhooks/per-event-token";
        NotificationEvent event = new NotificationEvent(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                EventType.DISCORD,
                perEventWebhook,
                "Deploy failed"
        );

        discordConsumerService.consumeDiscord(event);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(webhookSender).send(uriCaptor.capture(), ArgumentMatchers.any(Map.class));

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
                .when(webhookSender)
                .send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Map.class));

        NotificationEvent event = new NotificationEvent(
                UUID.randomUUID(),
                EventType.DISCORD,
                null,
                "this will fail to send"
        );

        discordConsumerService.consumeDiscord(event);

        verify(statusProducerMock, times(1))
                .publishStatus(
                        eq(event.eventId()),
                        eq(NotificationStatus.FAILED),
                        contains("Fallo")
                );
    }
}
