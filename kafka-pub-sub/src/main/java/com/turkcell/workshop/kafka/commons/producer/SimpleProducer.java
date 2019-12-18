package com.turkcell.workshop.kafka.commons.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SimpleProducer {
    private KafkaTemplate<String, String> simpleProducer;

    public SimpleProducer(KafkaTemplate<String, String> simpleProducer) {
        this.simpleProducer = simpleProducer;
    }

    public void send(String message, String topic) {
        simpleProducer.send(topic, message);
    }
}
