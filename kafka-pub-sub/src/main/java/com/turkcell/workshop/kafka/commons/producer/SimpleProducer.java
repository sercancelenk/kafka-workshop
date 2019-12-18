package com.turkcell.workshop.kafka.commons.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SimpleProducer {
    private final KafkaTemplate<String, String> simpleProducer;


    public void send(String message, String topic) {
        simpleProducer.send(topic, message);
    }

}
