package dev.dacolcha.discordconsumer.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Component
public class DiscordWebhookSender {

    private final RestClient restClient;

    public DiscordWebhookSender() {
        this.restClient = RestClient.create();
    }

    public void send(String webhookUrl, Map<String, Object> payload) {
        restClient.post()
                .uri(URI.create(webhookUrl))
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
