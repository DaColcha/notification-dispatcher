package dev.dacolcha.wsgateway.service;

import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Captor
    private ArgumentCaptor<Map<String, Object>> payloadCaptor;

    private WebSocketService webSocketService;

    public static final String WS_TOPIC = "/event/updates";

    @BeforeEach
    void setUp() {
        webSocketService = new WebSocketService(messagingTemplate);
    }

    @Test
    @DisplayName("Debe construir el payload correcto y enviarlo a /event/updates")
    void sendStatusUpdate_ShouldSendCorrectPayloadToTopic() {
        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String detail = "Mensaje entregado con éxito";

        webSocketService.sendStatusUpdate(eventId, NotificationStatus.SUCCESS, EventType.EMAIL, detail);

        verify(messagingTemplate, times(1))
                .convertAndSend(eq(WS_TOPIC), (Object) payloadCaptor.capture());

        Map<String, Object> capturedPayload = payloadCaptor.getValue();

        assertNotNull(capturedPayload);
        assertEquals(eventId.toString(), capturedPayload.get("eventId"));
        assertEquals(NotificationStatus.SUCCESS.toString(), capturedPayload.get("status"));
        assertEquals(EventType.EMAIL.toString(), capturedPayload.get("channel"));
        assertEquals(detail, capturedPayload.get("detail"));
        assertNotNull(capturedPayload.get("timestamp"), "El timestamp debe ser generado automáticamente");
    }
}
