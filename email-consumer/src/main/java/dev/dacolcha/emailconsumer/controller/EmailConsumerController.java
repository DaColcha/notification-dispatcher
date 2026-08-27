package dev.dacolcha.emailconsumer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmailConsumerController {

    @GetMapping("/email/healthcheck")
    public String sampleNotification() {
        return "Email Consumer up and running";
    }
}
