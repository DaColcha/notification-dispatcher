package dev.dacolcha.emailconsumer.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import dev.dacolcha.emailconsumer.dto.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailConsumerService {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumerService.class);

    private final Resend resend;
    private final String fromEmail;

    public EmailConsumerService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
    }

    @KafkaListener(topics = "${kafka.topic.email}")
    public void consumeEvent(String message) {
        NotificationEvent event = new ObjectMapper().readValue(message, NotificationEvent.class);
        log.info("📧 [EmailConsumer] Procesando evento [{}] para {}", event.eventId(), event.destination());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(event.destination())
                    .subject("NotificationDispatcher - " + formattedDate)
                    .html("<p>" + event.message() + "</p>")
                    .build();

            CreateEmailResponse response = resend.emails().send(params);

            log.info("✅ [EmailConsumer] Email enviado correctamente para [{}]", event.eventId());


        } catch (Exception e) {
            log.error("❌ [EmailConsumer] Error enviando email para evento [{}]", event.eventId(), e);
        }
    }
}
