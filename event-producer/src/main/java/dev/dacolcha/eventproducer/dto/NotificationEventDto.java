package dev.dacolcha.eventproducer.dto;

import java.util.UUID;

public record NotificationEventDto(
        UUID eventId,
        EventType eventType,
        String destination,
        String message
){
}
