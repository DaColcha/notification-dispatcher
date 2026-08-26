package dev.dacolcha.eventproducer.controllers;

import dev.dacolcha.eventproducer.dto.EventType;
import dev.dacolcha.eventproducer.dto.NotificationEventDto;
import dev.dacolcha.eventproducer.services.EventProducerService;
import jdk.jfr.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(EventProducerController.class)
public class EventProducerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventProducerService eventProducerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createOrder_shouldReturnOrderCreatedAndSendKafkaMessage() throws Exception {
        NotificationEventDto notificationEventDto = new NotificationEventDto(
                UUID.randomUUID(),
                EventType.EMAIL,
                "da.colcha@gmail.com",
                "Random event notification"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationEventDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Event published")));

        verify(eventProducerService, times(1)).sendMessage(any(NotificationEventDto.class));
    }
}
