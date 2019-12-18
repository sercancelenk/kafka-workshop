package com.turkcell.workshop.kafka;

import com.turkcell.workshop.kafka.dto.IntegerMessage;
import com.turkcell.workshop.kafka.dto.StringMessage;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(id = "multiGroup", topics = {"itopic", "stopic"})
public class MultiMethodListener {

    @KafkaHandler
    public void integerMessage(IntegerMessage integerMessage) {
        System.out.println("Received: " + integerMessage);
    }

    @KafkaHandler
    public void stringMessage(StringMessage stringMessage) {
        System.out.println("Received: " + stringMessage);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        System.out.println("Received unknown: " + object);
    }

}
