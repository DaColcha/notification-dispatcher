package dev.dacolcha.slackconsumer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SlackConsumerController {

    @GetMapping("/slack/healthcheck")
    public String healthcheck() {
        return "Slack Consumer up and running";
    }
}
