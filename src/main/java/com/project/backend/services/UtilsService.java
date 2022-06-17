package com.project.backend.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.entities.*;
import com.project.backend.exceptions.CustomExceptionMessages;
import com.project.backend.repositories.ClientRepo;
import com.project.backend.repositories.CourtRepo;
import com.project.backend.repositories.TariffRepo;
import com.project.backend.repositories.*;
import net.minidev.json.JSONObject;
import com.project.backend.validators.UtilsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UtilsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepo subscriptionRepo;
    private final CourtRepo courtRepo;
    private final ClientRepo clientRepo;
    private final TariffRepo tariffRepo;
    private final ReservationRepo reservationRepo;

    @Autowired
    public UtilsService(SubscriptionRepo subscriptionRepo, CourtRepo courtRepo, ClientRepo clientRepo, UtilsValidator utilsValidator, TariffRepo tariffRepo, ReservationRepo reservationRepo) {
        this.subscriptionRepo = subscriptionRepo;
        this.courtRepo = courtRepo;
        this.clientRepo = clientRepo;
        this.tariffRepo = tariffRepo;
        this.reservationRepo = reservationRepo;
    }

    public HashMap<String, Object> findClientAndCourt(String username, UUID courtId) {
        HashMap<String, Object> hashMap = new HashMap<>();

        List<Client> clients=clientRepo.findAll();
        boolean clientFound = false;
        for(Client client:clients){
            String currentUsername = client.getUser().getUsername();
            if(currentUsername.equals(username)) {
                clientFound = true;
                hashMap.put("client", client);
            }
        }
        if (!clientFound) {
            LOGGER.error("Client with username {} was not found in db", username);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with username: " + username);
        }

        Optional<Court> courtOptional = courtRepo.findById(courtId);
        if (!courtOptional.isPresent()) {
            LOGGER.error("Court with number {} was not found in db", courtId);
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with number: " + courtId);
        }
        hashMap.put("court", courtOptional.get());

        return hashMap;
    }

    private Multimap<Integer,Integer> generateAllIntervals(int start, int end){
        Multimap<Integer,Integer> intervals= ArrayListMultimap.create();
        for(int h=start;h<end;h++){
            intervals.put(h,h+1);
            if(h!=end-1)
                intervals.put(h,h+2);
        }
        return intervals;
    }

    private void removeIntersectedIntervals(Multimap<Integer,Integer> intervals, int start, int end) {
        intervals.remove(start, end);                     //se scoate intervalul rezervarii
        switch (end-start){
            case 1:
                intervals.remove(start-1,end);         //se scot intervalele de 2h care se suprapun
                intervals.remove(start,end+1);
                break;
            case 2:
                intervals.remove(start,start+1);      //se scot intervalele de 1h care se suprapun
                intervals.remove(start+1,end);
                intervals.remove(start-1,start+1); //se scot intervalele de 2h care se suprapun
                intervals.remove(start+1,end+1);
            default:
                break;
        }
    }

    public Multimap<Integer,Integer> findIntervalsAvailableForReservation(LocalDate date, Court court) {
        Multimap<Integer, Integer> intervals = generateAllIntervals(court.getLocation().getStartHour(), court.getLocation().getEndHour());
        List<Subscription> subscriptions = subscriptionRepo.findByDayOfWeekAndCourt(String.valueOf(date.getDayOfWeek()).toLowerCase(), court.getIdCourt());
        List<Reservation> reservations = reservationRepo.findByDateAndCourt(date, court.getIdCourt());
        for (Reservation r : reservations) {
            removeIntersectedIntervals(intervals, r.getStartTime(), r.getEndTime());
        }
        for (Subscription s : subscriptions) {
            if (s.getStartDate().plusMonths(1).minusDays(1).isAfter(date))        //abonamentul existent acopera si data dorita de client
                removeIntersectedIntervals(intervals, s.getStartTime(), s.getEndTime());
        }
        return intervals;
    }

    public Multimap<Integer,Integer> findIntervalsAvailableForSubscription(LocalDate startDate, String dayOfWeek, Court court) {
        LocalDate endDate=startDate.plusMonths(1).minusDays(1);
        Multimap<Integer, Integer> intervals = generateAllIntervals(court.getLocation().getStartHour(), court.getLocation().getEndHour());
        List<Subscription> subscriptions = subscriptionRepo.findByDayOfWeekAndCourt(dayOfWeek, court.getIdCourt());
        List<Reservation> reservations = reservationRepo.findByCourt(court.getIdCourt());
        for (Reservation r : reservations) {
            if((dayOfWeek.equals(String.valueOf(r.getDate().getDayOfWeek()).toLowerCase())) &&
                    (r.getDate().isAfter(startDate) && r.getDate().isBefore(endDate)
                            || r.getDate().equals(startDate)
                            || r.getDate().equals(endDate)))
                removeIntersectedIntervals(intervals, r.getStartTime(), r.getEndTime());
        }
        for (Subscription s : subscriptions) {
            if((s.getStartDate().isBefore(startDate) && s.getStartDate().plusMonths(1).minusDays(1).isAfter(startDate))
            || (s.getStartDate().isBefore(endDate) && s.getStartDate().plusMonths(1).minusDays(1).isAfter(endDate))
            || s.getStartDate().equals(startDate) && s.getStartDate().plusMonths(1).minusDays(1).equals(endDate))
                removeIntersectedIntervals(intervals, s.getStartTime(), s.getEndTime());
        }
        return intervals;
    }

    public int stringToDayOfWeekInt(String dayOfWeek) {
        if(dayOfWeek.equalsIgnoreCase("monday"))
            return 1;
        if(dayOfWeek.equalsIgnoreCase("tuesday"))
            return 2;
        if(dayOfWeek.equalsIgnoreCase("wednesday"))
            return 3;
        if(dayOfWeek.equalsIgnoreCase("thursday"))
            return 4;
        if(dayOfWeek.equalsIgnoreCase("friday"))
            return 5;
        if(dayOfWeek.equalsIgnoreCase("saturday"))
            return 6;
        else
            return 7;
    }

    public double computePricePerDay(LocalDate date, int startTime, int endTime, Location location, int dayOfWeek) {

        List<Tariff> tariffs = tariffRepo.findAllByLocation(location);
        if (tariffs.size() != 8)
            throw new ResourceNotFoundException("The number of tariffs asociated with this location is invalid");
        Predicate<Tariff> predicate;
        int month = date.getMonthValue();
        if(month >= 4 && month <= 10)
            predicate = t -> t.getSeason().equalsIgnoreCase("Summer");
        else
            predicate = t -> t.getSeason().equalsIgnoreCase("Winter");
        if(dayOfWeek >= 6)
            predicate = predicate.and(t -> t.getDayOfWeek().equalsIgnoreCase("Weekend"));
        else
            predicate = predicate.and(t -> t.getDayOfWeek().equalsIgnoreCase("Workweek"));
        tariffs = tariffs.stream().filter(predicate).collect(Collectors.toList());
        double totalPrice = 0.0;
        for (int hour = startTime; hour < endTime; hour++) {
            List<Tariff> selectedTariffs;
            if (hour < 21) {
                selectedTariffs = tariffs.stream().filter(t -> !t.isNight()).collect(Collectors.toList());
                if(selectedTariffs.isEmpty())
                    throw new ResourceNotFoundException("Requested tariff not found in database");
                totalPrice += selectedTariffs.get(0).getPrice();
            }
            else {
                selectedTariffs = tariffs.stream().filter(t -> t.isNight()).collect(Collectors.toList());
                if(selectedTariffs.isEmpty())
                    throw new ResourceNotFoundException("Requested tariff not found in database");
                totalPrice += selectedTariffs.get(0).getPrice();
            }
        }

        return totalPrice;
    }

    public static JSONObject constructNotificationMessage(String username, UUID idReservation, String code) {
        JSONObject notification = new JSONObject();
        notification.put("title", "Take someone's reservation");
        notification.put("details",
                username+ " has a reservation but he/she cannot go. If you wanna play you can access the code in the bottom. " +
                "If you are not interested just ignore this message. YOUR CODE: " + code);
        notification.put("idReservation", idReservation);
        notification.put("username", username);
        return notification;
    }

    public void updateWallet(Client client, double price){
        if(client.getWallet() >= price) {
            client.setWallet(client.getWallet() - price);
            clientRepo.save(client);
        }
        else{
            throw new IllegalArgumentException(CustomExceptionMessages.INSUFFICIENT_FUNDS);
        }
    }
}