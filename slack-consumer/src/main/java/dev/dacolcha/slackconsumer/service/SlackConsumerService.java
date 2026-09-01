package dev.dacolcha.slackconsumer.service;

import dev.dacolcha.common.dto.NotificationEvent;
import dev.dacolcha.common.dto.NotificationStatus;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SlackConsumerService {
    private static final Logger log = LoggerFactory.getLogger(SlackConsumerService.class);

    @Value("${slack.webhook-url}")
    private String fallbackWebhookUrl;

    @Autowired
    private SlackSender slackSender;

    @Autowired
    private StatusProducer statusProducer;

    @KafkaListener(topics = "${kafka.topic.slack}")
    public void consumeSlack(NotificationEvent event,
                             @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("💬 [SlackConsumer] Consumiendo evento [{}] para Slack", event.eventId());

        try {
            String webhookUrl = (event.destination() != null && event.destination().startsWith("https://hooks.slack.com"))
                    ? event.destination()
                    : fallbackWebhookUrl;

            Map<String, Object> slackPayload = formatEventContent(event);

            slackSender.send(webhookUrl, slackPayload);

            log.info("✅ [SlackConsumer] Evento [{}] enviado con éxito a Slack", event.eventId());

            statusProducer.publishStatus(
                    event.eventId(),
                    NotificationStatus.SUCCESS,
                    event.message(),
                    partition
            );
        } catch (Exception e) {
            log.error("❌ [SlackConsumer] Fallo al enviar notificación a Slack", e);
            statusProducer.publishStatus(
                    event.eventId(),
                    NotificationStatus.FAILED,
                    "Fallo al entregar a Slack: " + e.getMessage(),
                    partition
            );
        }
    }

    private static @NonNull Map<String, Object> formatEventContent(NotificationEvent event) {
        Map<String, Object> headerBlock = Map.of(
                "type", "header",
                "text", Map.of(
                        "type", "plain_text",
                        "text", "🚨 Notification Dispatcher",
                        "emoji", true
                )
        );

        Map<String, Object> messageBlock = Map.of(
                "type", "section",
                "text", Map.of(
                        "type", "mrkdwn",
                        "text", event.message()
                )
        );

        return Map.of(
                "blocks", List.of(headerBlock, messageBlock)
        );
    }
}
