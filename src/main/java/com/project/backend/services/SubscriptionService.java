package com.project.backend.services;

import com.google.common.collect.Multimap;
import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.SubscriptionDetailsDTO;
import com.project.backend.dtos.builders.SubscriptionBuilder;
import com.project.backend.entities.Client;
import com.project.backend.entities.Court;
import com.project.backend.entities.Subscription;
import com.project.backend.repositories.*;
import com.project.backend.validators.UniqueSubscriptionValidator;
import com.project.backend.validators.UtilsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscriptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);
    private final SubscriptionRepo subscriptionRepo;
    private final CourtRepo courtRepo;
    private final UtilsValidator utilsValidator;
    private final UtilsService utilsService;
    private final UniqueSubscriptionValidator subscriptionValidator;

    @Autowired
    public SubscriptionService(SubscriptionRepo subscriptionRepo,CourtRepo courtRepo,
                               UtilsValidator utilsValidator, UtilsService utilsService) {
        this.subscriptionRepo = subscriptionRepo;
        this.courtRepo=courtRepo;
        this.utilsService=utilsService;
        this.utilsValidator=utilsValidator;
        this.subscriptionValidator = new UniqueSubscriptionValidator(subscriptionRepo);
    }

    public List<SubscriptionDetailsDTO> findSubscriptions() {
        List<Subscription> subscriptionList = subscriptionRepo.findAll();
        if (subscriptionList.isEmpty()) {
            LOGGER.error("No subscription found");
            throw new ResourceNotFoundException(Subscription.class.getSimpleName());
        }
        List<SubscriptionDetailsDTO> subscriptionDetailsDTOList=new ArrayList<>();
        for(Subscription subscription:subscriptionList){
            subscriptionDetailsDTOList.add(SubscriptionBuilder.toSubscriptionDetailsDTO(subscription,subscription.getClient().getUser().getUsername(),subscription.getCourt().getIdCourt()));
        }
        return subscriptionDetailsDTOList;
    }

    public SubscriptionDetailsDTO findSubscriptionById(UUID idSubscription) {
        Optional<Subscription> subscriptionOptional = subscriptionRepo.findById(idSubscription);
        if (!subscriptionOptional.isPresent()) {
            LOGGER.error("Subscription with id {} was not found in db", idSubscription);
            throw new ResourceNotFoundException(Subscription.class.getSimpleName() + " with id: " + idSubscription);
        }
        return SubscriptionBuilder.toSubscriptionDetailsDTO(subscriptionOptional.get(),subscriptionOptional.get().getClient().getUser().getUsername(),subscriptionOptional.get().getCourt().getIdCourt());
    }

    public Multimap<Integer, Integer> availableIntervals(SubscriptionDetailsDTO subscriptionDetailsDTO) {
        Optional<Court> court = courtRepo.findById(subscriptionDetailsDTO.getCourtId());
        if (!court.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", subscriptionDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + subscriptionDetailsDTO.getCourtId());
        }
        return utilsService.findIntervalsAvailableForSubscription(subscriptionDetailsDTO.getStartDate(),
                subscriptionDetailsDTO.getDayOfWeek(),
                court.get());
    }

    public double computePrice(SubscriptionDetailsDTO subscriptionDetailsDTO) {
        Optional<Court> court = courtRepo.findById(subscriptionDetailsDTO.getCourtId());
        if (!court.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", subscriptionDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + subscriptionDetailsDTO.getCourtId());
        }
        return 5 * (85/100.0) * utilsService.computePricePerDay(subscriptionDetailsDTO.getStartDate(), subscriptionDetailsDTO.getStartTime(),
                subscriptionDetailsDTO.getEndTime(), court.get().getLocation(),
                utilsService.stringToDayOfWeekInt(subscriptionDetailsDTO.getDayOfWeek()));
    }

    public UUID insert(SubscriptionDetailsDTO subscriptionDetailsDTO) {
        utilsValidator.validateStartAndEndTime(subscriptionDetailsDTO.getStartTime(), subscriptionDetailsDTO.getEndTime());
        subscriptionValidator.validateUniqueSubscription(subscriptionDetailsDTO);

        HashMap<String, Object> clientAndCourt = utilsService.findClientAndCourt(subscriptionDetailsDTO.getUsername(), subscriptionDetailsDTO.getCourtId());
        double price = computePrice(subscriptionDetailsDTO);
        Client client = (Client)clientAndCourt.get("client");
        utilsService.updateWallet(client, price);

        Subscription newSubscription = SubscriptionBuilder.toEntity(subscriptionDetailsDTO);
        newSubscription.setClient(client);
        newSubscription.setCourt((Court)clientAndCourt.get("court"));
        newSubscription.setTotalPrice(price);

        Subscription subscription = subscriptionRepo.save(newSubscription);
        LOGGER.debug("Subscription with id {} was inserted in db", subscription.getIdSubscription());

        SendBillService sendBillService = new SendBillService();
        sendBillService.generateSubscriptionBill(subscription);
        sendBillService.sendEmailWithBill(subscriptionDetailsDTO.getUsername(), ((Court) clientAndCourt.get("court")).getLocation().getName(),
                ((Client) clientAndCourt.get("client")).getUser().getEmail(), "SubscriptionReceipt");

        return subscription.getIdSubscription();
    }

    public SubscriptionDetailsDTO update(UUID idSubscription, SubscriptionDetailsDTO subscriptionDetailsDTO){
        utilsValidator.validateStartAndEndTime(subscriptionDetailsDTO.getStartTime(), subscriptionDetailsDTO.getEndTime());

        Optional<Subscription> subscriptionOptional = subscriptionRepo.findById(idSubscription);
        if (!subscriptionOptional.isPresent()) {
            LOGGER.error("Subscription with id {} was not found in db", idSubscription);
            throw new ResourceNotFoundException(Subscription.class.getSimpleName() + " with id: " + idSubscription);
        }
        HashMap<String, Object> clientAndCourt = utilsService.findClientAndCourt(subscriptionDetailsDTO.getUsername(),
                subscriptionDetailsDTO.getCourtId());

        Subscription subscription = SubscriptionBuilder.toEntity(subscriptionDetailsDTO);
        subscription.setClient((Client)clientAndCourt.get("client"));
        subscription.setCourt((Court)clientAndCourt.get("court"));
        subscription.setTotalPrice(computePrice(subscriptionDetailsDTO));
        subscription.setIdSubscription(idSubscription);
        subscriptionRepo.save(subscription);

        LOGGER.debug("Subscription with id {} was updated in db", subscription.getIdSubscription());

        return SubscriptionBuilder.toSubscriptionDetailsDTO(subscription, subscriptionDetailsDTO.getUsername(),
                subscriptionDetailsDTO.getCourtId());
    }

    public void deleteSubscriptionById(UUID idSubscription){
        Optional<Subscription> subscriptionOptional = subscriptionRepo.findById(idSubscription);
        if (!subscriptionOptional.isPresent()) {
            LOGGER.error("Subscription with id {} was not found in db", idSubscription);
            throw new ResourceNotFoundException(Subscription.class.getSimpleName() + " with id: " + idSubscription);
        }
        subscriptionRepo.deleteById(idSubscription);
        LOGGER.debug("Subscription with id {} was deleted from db", idSubscription);
    }
}
