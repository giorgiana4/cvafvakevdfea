package com.project.backend.dtos.builders;

import com.project.backend.dtos.SubscriptionDetailsDTO;
import com.project.backend.entities.Subscription;

import java.util.UUID;

public class SubscriptionBuilder {

    public SubscriptionBuilder() {
    }

    public static SubscriptionDetailsDTO toSubscriptionDetailsDTO(Subscription subscription, String username, UUID courtId){
        return new SubscriptionDetailsDTO(subscription.getIdSubscription(),
                subscription.getStartDate(),
                subscription.getStartTime(),
                subscription.getEndTime(),
                subscription.getDayOfWeek(),
                subscription.getTotalPrice(),
                username,
                courtId);
    }

    public static Subscription toEntity(SubscriptionDetailsDTO subscriptionDetailsDTO){
        return new Subscription(subscriptionDetailsDTO.getStartDate(),
                subscriptionDetailsDTO.getStartTime(),
                subscriptionDetailsDTO.getEndTime(),
                subscriptionDetailsDTO.getDayOfWeek().toLowerCase(),
                subscriptionDetailsDTO.getTotalPrice());
    }
}
