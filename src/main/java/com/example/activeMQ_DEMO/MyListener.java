package com.example.activeMQ_DEMO;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MyListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println("MESSAGE LISTENER : ");

        try {
            System.out.println(message.getBody(String.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
