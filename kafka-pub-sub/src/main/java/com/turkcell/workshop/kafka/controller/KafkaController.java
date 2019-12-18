package com.turkcell.workshop.kafka.controller;

import com.turkcell.workshop.kafka.commons.producer.SimpleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {
    private final SimpleProducer simpleProducer;

    @GetMapping("/topic/{topic}/message/{message}")
    public ResponseEntity<String> message(@PathVariable("topic") String topic, @PathVariable("message") String message) {
        simpleProducer.send(message, topic);
        return ResponseEntity.ok(String.format("Message %s received in topic %s", message, topic));
    }
}
