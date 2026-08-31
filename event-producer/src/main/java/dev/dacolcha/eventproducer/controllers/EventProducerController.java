package dev.dacolcha.eventproducer.controllers;

import dev.dacolcha.common.dto.NotificationEvent;
import dev.dacolcha.eventproducer.services.EventProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class EventProducerController {

    @Autowired
    private EventProducerService eventProducerService;

    @PostMapping
    public String publishEvent(@RequestBody NotificationEvent event) {
        eventProducerService.sendMessage(event);
        return "Event published";
    }
}
