package com.turkcell.workshop.kafka.listener.withautocommitsingle;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerWithAutocommitListener {

    @KafkaListener(topics = "topic1", groupId = "ConsumerGroup1")
    public void listen(String message) {
        System.out.println("Received but not processed Message in group ConsumerGroup1: " + message);

        if("throw".equals(message))
            throw new RuntimeException("Throwing known exception. Kafka will be retry with limit..");

        System.out.println("Received Message in group ConsumerGroup1: " + message);
    }

}
