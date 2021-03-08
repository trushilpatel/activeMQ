package com.example.activeMQ_DEMO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {
    @Autowired
    MessageSender messageSender;

    @GetMapping("/myQueue/{message}")
    String sendMessage(@PathVariable String message) {
        messageSender.send(message);
        return message;
    }
}
