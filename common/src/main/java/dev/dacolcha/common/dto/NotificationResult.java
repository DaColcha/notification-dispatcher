package dev.dacolcha.common.dto;

import java.io.Serializable;
import java.util.UUID;

public record NotificationResult(
        UUID eventId,
        NotificationStatus status,
        EventType eventType,
        String detail,
        int partition,
        Long timestamp
) implements Serializable {
}
