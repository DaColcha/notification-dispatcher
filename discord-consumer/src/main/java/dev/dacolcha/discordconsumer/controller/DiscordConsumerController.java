package dev.dacolcha.discordconsumer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiscordConsumerController {
    @GetMapping("/discord/healthcheck")
    public String sampleNotification() {
        return "Discord Consumer up and running";
    }
}
