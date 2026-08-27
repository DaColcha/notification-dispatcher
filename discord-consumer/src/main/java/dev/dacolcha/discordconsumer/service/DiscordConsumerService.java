package dev.dacolcha.discordconsumer.service;

import dev.dacolcha.discordconsumer.dto.NotificationEvent;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class DiscordConsumerService {
    private static final Logger log = LoggerFactory.getLogger(DiscordConsumerService.class);

    @Value("${discord.webhook-url}")
    private String fallbackWebhookUrl;

    @Autowired
    private DiscordWebhookSender webhookSender;

    @KafkaListener(topics = "${kafka.topic.discord}")
    public void consumeDiscord(String message) {

        NotificationEvent event = new ObjectMapper().readValue(message, NotificationEvent.class);
        log.info("💬 [DiscordConsumer] Consumiendo evento [{}] para Discord", event.eventId());

        try {
            String webhookUrl = (event.destination() != null && event.destination().startsWith("https://discord.com"))
                    ? event.destination()
                    : fallbackWebhookUrl;

            Map<String, Object> discordPayload = formatEventContent(event);

            webhookSender.send(webhookUrl, discordPayload);

            log.info("✅ [DiscordConsumer] Evento [{}] enviado con éxito a Discord", event.eventId());


        } catch (Exception e) {
            log.error("❌ [DiscordConsumer] Fallo al enviar notificación a Discord", e);
            throw e;
        }
    }

    private static @NonNull Map<String, Object> formatEventContent(NotificationEvent event) {
        Map<String, Object> embed = Map.of(
                "title", " Notification Dispatcher",
                "description", event.message()
        );

        return Map.of(
                "username", "Event Pulse",
                "embeds", List.of(embed)
        );
    }
}
