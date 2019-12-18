package com.turkcell.workshop.kafka.listener.withautocommit;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerWithAutocommitListener {

    @KafkaListener(topics = "topic1", groupId = "ConsumerGroup1")
    public void listen(String message) {
        System.out.println(1/0);
        System.out.println("Received Message in group ConsumerGroup1: " + message);
    }

}
