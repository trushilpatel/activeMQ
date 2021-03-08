package com.example.activeMQ_DEMO;

import java.io.Serializable;

public class Message implements Serializable {
    String name;
    String body;

    public Message() {
    }

    public Message(String name, String body) {
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
