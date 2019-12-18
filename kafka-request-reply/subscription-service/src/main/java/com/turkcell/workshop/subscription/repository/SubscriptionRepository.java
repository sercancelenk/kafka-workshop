package com.turkcell.workshop.subscription.repository;

import com.turkcell.workshop.requestreplycommon.dto.Subscription;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SubscriptionRepository {
    Map<String, Subscription> subscriptionsMap = new HashMap<>();

    @PostConstruct
    private void initCars() {
        subscriptionsMap.put("5302008502", Subscription.builder().msisdn("5302008502").offerId("12").status("ACTIVE").build());
        subscriptionsMap.put("5302223333", Subscription.builder().msisdn("5302223333").offerId("13").status("DEACTIVE").build());
    }

    public Subscription getSubscription(String msisdn) {
        return subscriptionsMap.get(msisdn);
    }
}
