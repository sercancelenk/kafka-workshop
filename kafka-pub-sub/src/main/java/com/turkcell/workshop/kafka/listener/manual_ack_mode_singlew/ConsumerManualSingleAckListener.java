package com.turkcell.workshop.kafka.listener.manual_ack_mode_singlew;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
public class ConsumerManualSingleAckListener implements AcknowledgingMessageListener<String, String> {

    @Override
    @KafkaListener(topics = "topic4", groupId = "ConsumerGroup4", containerFactory = "manualAckKafkaListenerFactory")
    public void onMessage(ConsumerRecord<String, String> message, Acknowledgment acknowledgment) {
        System.out.println("Received but Not Acknowledge Message in group ConsumerGroup1: " + message);
    }
}
