package dev.dacolcha.eventproducer.dto;

import java.util.UUID;

public record NotificationEventDto(
        UUID Event_id,
        EventType eventType,
        String destination,
        String message
){
}
