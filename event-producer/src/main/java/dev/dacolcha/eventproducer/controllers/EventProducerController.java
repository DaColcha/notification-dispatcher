package dev.dacolcha.eventproducer.controllers;

import dev.dacolcha.eventproducer.dto.NotificationEventDto;
import dev.dacolcha.eventproducer.services.EventProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/event")
public class EventProducerController {

    @Autowired
    private EventProducerService eventProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public String publishEvent(@RequestBody NotificationEventDto event) {
        eventProducerService.sendMessage(event);
        return "Event published";
    }
}
