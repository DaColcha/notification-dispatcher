package dev.dacolcha.slackconsumer.dto;

import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        EventType eventType,
        String destination,
        String message
){
}
