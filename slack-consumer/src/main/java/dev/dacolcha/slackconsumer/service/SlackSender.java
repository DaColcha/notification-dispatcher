package dev.dacolcha.slackconsumer.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Component
public class SlackSender {
    private final RestClient restClient;

    public SlackSender() {
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
