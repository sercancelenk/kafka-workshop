package com.turkcell.workshop.kafka.listener.manual_ack_mode_singlew;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerManualSingleAckListener implements AcknowledgingMessageListener<String, String> {

    @Override
    @KafkaListener(topics = "topic4", groupId = "ConsumerGroup4", containerFactory = "manualAckKafkaListenerFactory")
    public void onMessage(ConsumerRecord<String, String> message, Acknowledgment acknowledgment) {
        try{
            System.out.println("Received but Not Acknowledge Message in group ConsumerGroup1: " + message);

            System.out.println("Received Message acknowledged in group ConsumerGroup1: " + message);
            System.out.println("------------------------------------");
        }catch (Exception ex){
            System.out.println("Received Message in group ConsumerGroup1: " + message + " exception occurred");
        }
    }
}
