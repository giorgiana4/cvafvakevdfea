package com.project.backend.validators;

import com.project.backend.dtos.SubscriptionDetailsDTO;
import com.project.backend.entities.Subscription;
import com.project.backend.exceptions.CustomExceptionMessages;
import com.project.backend.repositories.SubscriptionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UniqueSubscriptionValidator {
    private final SubscriptionRepo subscriptionRepo;

    @Autowired
    public UniqueSubscriptionValidator(SubscriptionRepo subscriptionRepo) {
        this.subscriptionRepo = subscriptionRepo;
    }

    public void validateUniqueSubscription(SubscriptionDetailsDTO subscriptionDetailsDTO){
        List<Subscription> subscriptions = subscriptionRepo.findAll();

        for(Subscription subscription:subscriptions){
            if(subscription.getClient().getUser().getUsername().equals(subscriptionDetailsDTO.getUsername()) &&
               subscription.getStartDate().plusMonths(1).isAfter(LocalDate.now())){
                    throw new IllegalArgumentException(CustomExceptionMessages.YOU_CAN_NOT_HAVE_TWO_SUBSCRIPTIONS);
            }
        }
    }
}