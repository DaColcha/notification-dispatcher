package dev.dacolcha.discordconsumer.dto;

import dev.dacolcha.discordconsumer.dto.EventType;

import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        EventType eventType,
        String destination,
        String message
){
}
