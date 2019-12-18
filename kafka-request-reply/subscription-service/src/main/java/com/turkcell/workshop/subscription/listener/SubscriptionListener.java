package com.turkcell.workshop.subscription.listener;

import com.turkcell.workshop.requestreplycommon.dto.Subscription;
import com.turkcell.workshop.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SubscriptionListener {
    private final SubscriptionRepository subscriptionRepository;

    @KafkaListener(topics = "${kafka.topic.subscription.request}", containerFactory = "requestReplyListenerContainerFactory")
    @SendTo()
    public Subscription receive(String msisdn) {
        log.info("received request for MSISDN {} ", msisdn);
        Subscription subscription = subscriptionRepository.getSubscription(msisdn);
        log.info("sending reply {} ", subscription);
        return subscription;
    }
}
