package com.example.activeMQ_DEMO;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

@Component
public class MyQueueListener {
    @JmsListener(destination = "${springjms.myQueue}")
    public void message(String message){
        System.out.println("Don't Worry Here is your MyQueue Listener at your service");
        System.out.println(message);
    }
}
