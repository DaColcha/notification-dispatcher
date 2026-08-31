package dev.dacolcha.wsgateway.intg;

import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationStatus;
import dev.dacolcha.wsgateway.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketServiceIntgTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebSocketService webSocketService;

    private WebSocketStompClient stompClient;
    private BlockingQueue<Map<String, Object>> blockingQueue;

    public static final String WS_TOPIC = "/event/updates";

    @BeforeEach
    void setUp() {
        blockingQueue = new ArrayBlockingQueue<>(1);

        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @Test
    @DisplayName("Debe conectarse al WebSocket, suscribirse a /event/updates y recibir la notificación de estado")
    void shouldReceiveStatusUpdateFromWebSocketTopic() throws Exception {
        String wsUrl = String.format("ws://localhost:%d/ws", port);

        // Conectar al endpoint de WebSocket
        StompSession session = stompClient
                .connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get();

        session.subscribe(WS_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((Map<String, Object>) payload);
            }
        });

        UUID eventId = UUID.randomUUID();
        webSocketService.sendStatusUpdate(eventId, NotificationStatus.SUCCESS, EventType.EMAIL, "Entregado a Email correctamente");


        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Map<String, Object> receivedPayload = blockingQueue.poll();

            assertNotNull(receivedPayload, "El payload recibido no debe ser nulo");
            assertEquals(eventId.toString(), receivedPayload.get("eventId"));
            assertEquals("SUCCESS", receivedPayload.get("status"));
            assertEquals("EMAIL", receivedPayload.get("channel"));
            assertEquals("Entregado a Email correctamente", receivedPayload.get("detail"));
        });
    }
}
