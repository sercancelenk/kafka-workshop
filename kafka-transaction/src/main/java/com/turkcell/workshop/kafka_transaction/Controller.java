package com.turkcell.workshop.kafka_transaction;

import com.turkcell.workshop.kafka_transaction.dto.Step1Data;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final KafkaTemplate<Object, Object> template;

    @GetMapping(path = "/send/foos/{what}")
    public void sendStep1(@PathVariable String what) {
        this.template.executeInTransaction(kafkaTemplate -> {
            StringUtils.commaDelimitedListToSet(what).stream()
                    .map(s -> new Step1Data(s))
                    .forEach(step1 -> kafkaTemplate.send("step1topic", step1));
            return null;
        });
    }
}
