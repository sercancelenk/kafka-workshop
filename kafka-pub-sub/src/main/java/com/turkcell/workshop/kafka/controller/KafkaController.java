package com.turkcell.workshop.kafka.controller;

import com.turkcell.workshop.kafka.commons.dto.BatchRequest;
import com.turkcell.workshop.kafka.commons.producer.SimpleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KafkaController {
    private final SimpleProducer simpleProducer;

    @GetMapping("/send-single/topic/{topic}/message/{message}")
    public ResponseEntity<String> message(@PathVariable("topic") String topic, @PathVariable("message") String message) {
        simpleProducer.send(message, topic);
        return ResponseEntity.ok(String.format("Message %s received in topic %s", message, topic));
    }

    @PostMapping("/send-batch")
    public ResponseEntity<String> message(@RequestBody BatchRequest request) {
        request
                .getMessages()
                .stream()
                .forEach(m -> simpleProducer.send(m, "topic2"));
        return ResponseEntity.ok(String.format("Messages %s received in topic %s", request, "topic2"));
    }

    @PostMapping("/send-single-manual-ack/topic/{topic}/message/{message}")
    public ResponseEntity<String> singleManualAck(@PathVariable("topic") String topic, @PathVariable("message") String message) {
        simpleProducer.send(message, "topic4");
        return ResponseEntity.ok(String.format("Message %s received in topic %s", message, "topic4"));
    }

}
