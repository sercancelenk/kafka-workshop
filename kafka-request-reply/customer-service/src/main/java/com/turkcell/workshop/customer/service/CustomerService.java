package com.turkcell.workshop.customer.service;

import com.turkcell.workshop.requestreplycommon.dto.Subscription;
import com.turkcell.workshop.requestreplycommon.request_reply_util.CompletableFutureReplyingKafkaOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CompletableFutureReplyingKafkaOperations<String, String, Subscription> requestReplyKafkaTemplate;

    @Value("${kafka.topic.subscription.request}")
    private String requestTopic;

    public Subscription getCustomerSubscription(String msisdn) {
        try {
            return getCustomerSubscriptionAsync(msisdn).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get Subscription", e);
        }
    }

    public CompletableFuture<Subscription> getCustomerSubscriptionAsync(String msisdn) {
        return requestReplyKafkaTemplate.requestReply(requestTopic, msisdn);
    }

}
