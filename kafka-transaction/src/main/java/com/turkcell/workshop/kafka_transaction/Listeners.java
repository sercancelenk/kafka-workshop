package com.turkcell.workshop.kafka_transaction;

import com.turkcell.workshop.kafka_transaction.dto.Step1Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class Listeners {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(id = "step1Group", topics = "step1topic")
    public void listen1(List<Step1Data> step1DataList) throws IOException {
        log.info("Received: " + step1DataList);
        step1DataList.forEach(f -> kafkaTemplate.send("step2topic", f.getData().toUpperCase()));
        log.info("Messages sent, hit Enter to commit tx");
        System.in.read();
    }

    @KafkaListener(id = "step2Group", topics = "step2topic")
    public void listen2(List<String> in) {
        log.info("Received: " + in);
    }
}
