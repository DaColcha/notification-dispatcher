package dev.dacolcha.common.dto;

import java.io.Serializable;
import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        EventType eventType,
        String destination,
        String message
) implements Serializable {
}
