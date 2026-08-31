package dev.dacolcha.emailconsumer.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import dev.dacolcha.common.dto.EventType;
import dev.dacolcha.common.dto.NotificationEvent;
import dev.dacolcha.common.dto.NotificationStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailConsumerServiceTest {

    private static final String FROM_EMAIL = "notifications@dispatcher.test";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockedConstruction<Resend> resendConstruction;
    private EmailConsumerService emailConsumerService;

    @Mock
    private Emails emailsMock;

    @Mock
    private StatusProducer statusProducerMock;

    @BeforeEach
    void setUp() {
        resendConstruction = mockConstruction(Resend.class, (mock, context) -> {
            when(mock.emails()).thenReturn(emailsMock);
        });
        emailConsumerService = new EmailConsumerService("test-api-key", FROM_EMAIL, statusProducerMock);
    }

    @AfterEach
    void tearDown() {
        resendConstruction.close();
    }

    @Test
    void should_sendEmail_withEventInformation() throws Exception {
        when(emailsMock.send(ArgumentMatchers.any(CreateEmailOptions.class)))
                .thenReturn(new CreateEmailResponse("email-id-123"));

        NotificationEvent notificationEvent = new NotificationEvent(
                UUID.randomUUID(),
                EventType.EMAIL,
                "da.colcha@gmail.com",
                "Random event notification"
        );

        String message = objectMapper.writeValueAsString(notificationEvent);

        emailConsumerService.consumeEvent(message);

        ArgumentCaptor<CreateEmailOptions> optionsCaptor = ArgumentCaptor.forClass(CreateEmailOptions.class);
        verify(emailsMock, times(1)).send(optionsCaptor.capture());

        CreateEmailOptions sentOptions = optionsCaptor.getValue();
        assertEquals(FROM_EMAIL, sentOptions.getFrom());
        assertEquals(1, sentOptions.getTo().size());
        assertEquals(notificationEvent.destination(), sentOptions.getTo().get(0));
        assertEquals("<p>" + notificationEvent.message() + "</p>", sentOptions.getHtml());
        assertTrue(sentOptions.getSubject().startsWith("NotificationDispatcher - "));

        verify(statusProducerMock, times(1))
                .publishStatus(
                        eq(notificationEvent.eventId()),
                        eq(NotificationStatus.SUCCESS),
                        contains("entregado")
                );
    }

    @Test
    void should_swallowResendException_whenSendFails() throws Exception {
        when(emailsMock.send(ArgumentMatchers.any(CreateEmailOptions.class)))
                .thenThrow(new ResendException("boom"));

        NotificationEvent notificationEvent = new NotificationEvent(
                UUID.randomUUID(),
                EventType.EMAIL,
                "da.colcha@gmail.com",
                "Random event notification"
        );

        String message = objectMapper.writeValueAsString(notificationEvent);

        emailConsumerService.consumeEvent(message);

        verify(emailsMock, times(1)).send(ArgumentMatchers.any(CreateEmailOptions.class));
        verify(statusProducerMock, times(1))
                .publishStatus(
                        eq(notificationEvent.eventId()),
                        eq(NotificationStatus.FAILED),
                        contains("Fallo")
                );
    }
}
