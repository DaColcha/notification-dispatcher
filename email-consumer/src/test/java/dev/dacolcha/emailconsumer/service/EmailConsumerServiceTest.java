package dev.dacolcha.emailconsumer.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import dev.dacolcha.emailconsumer.dto.EventType;
import dev.dacolcha.emailconsumer.dto.NotificationEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConsumerServiceTest {

    private static final String FROM_EMAIL = "notifications@dispatcher.test";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockedConstruction<Resend> resendConstruction;
    private Emails emailsMock;
    private EmailConsumerService emailConsumerService;

    @BeforeEach
    void setUp() {
        emailsMock = Mockito.mock(Emails.class);
        resendConstruction = mockConstruction(Resend.class, (mock, context) -> {
            when(mock.emails()).thenReturn(emailsMock);
        });
        emailConsumerService = new EmailConsumerService("test-api-key", FROM_EMAIL);
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
    }
}
