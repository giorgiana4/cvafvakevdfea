package com.project.backend.services;

import com.google.common.collect.Multimap;
import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.dtos.builders.ReservationBuilder;
import com.project.backend.entities.Client;
import com.project.backend.entities.Court;
import com.project.backend.entities.Reservation;
import com.project.backend.repositories.*;
import com.project.backend.validators.UniqueReservationValidator;
import com.project.backend.validators.UtilsValidator;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepo reservationRepo;
    private final CourtRepo courtRepo;
    private final ClientRepo clientRepo;
    private final UtilsValidator utilsValidator;
    private final UtilsService utilsService;
    private final UniqueReservationValidator uniqueReservationValidator;
    private final SendMailBroadcast sendMailBroadcast;
    private final SimpMessagingTemplate template;


    @Autowired
    public ReservationService(ReservationRepo reservationRepo, CourtRepo courtRepo, ClientRepo clientRepo,
                              UtilsValidator utilsValidator, UtilsService utilsService, SimpMessagingTemplate template) {
        this.reservationRepo=reservationRepo;
        this.clientRepo=clientRepo;
        this.courtRepo=courtRepo;
        this.utilsService=utilsService;
        this.utilsValidator=utilsValidator;
        this.uniqueReservationValidator = new UniqueReservationValidator(reservationRepo);
        this.sendMailBroadcast = new SendMailBroadcast();
        this.template = template;
    }

    public List<ReservationDetailsDTO> findReservations() {
        List<Reservation> reservationList = reservationRepo.findAll();
        if (reservationList.isEmpty()) {
            LOGGER.error("No reservation found");
            throw new ResourceNotFoundException(Reservation.class.getSimpleName());
        }
        List<ReservationDetailsDTO> reservationDetailsDTOS=new ArrayList<>();
        for(Reservation reservation:reservationList)
            reservationDetailsDTOS.add(ReservationBuilder.toReservationDetailsDTO(reservation,reservation.getClient().getUser().getUsername(),reservation.getCourt().getIdCourt()));

        return reservationDetailsDTOS;
    }

    public ReservationDetailsDTO findReservationById(UUID idReservation) {
        Optional<Reservation> reservationOptional = reservationRepo.findById(idReservation);
        if (!reservationOptional.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", idReservation);
            throw new ResourceNotFoundException(Reservation.class.getSimpleName() + " with id: " + idReservation);
        }
        return ReservationBuilder.toReservationDetailsDTO(reservationOptional.get(),reservationOptional.get().getClient().getUser().getUsername(),reservationOptional.get().getCourt().getIdCourt());
    }

    public Multimap<Integer, Integer> availableIntervals(ReservationDetailsDTO reservationDetailsDTO) {
        Optional<Court> court = courtRepo.findById(reservationDetailsDTO.getCourtId());
        if (!court.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", reservationDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + reservationDetailsDTO.getCourtId());
        }

        return utilsService.findIntervalsAvailableForReservation(reservationDetailsDTO.getDate(),court.get());
    }

    public double computePrice(ReservationDetailsDTO reservationDetailsDTO) {
        Optional<Court> court = courtRepo.findById(reservationDetailsDTO.getCourtId());
        if (!court.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", reservationDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + reservationDetailsDTO.getCourtId());
        }
        return utilsService.computePricePerDay(reservationDetailsDTO.getDate(), reservationDetailsDTO.getStartTime(),
                reservationDetailsDTO.getEndTime(), court.get().getLocation(),
                reservationDetailsDTO.getDate().getDayOfWeek().getValue());
    }

    public UUID insert(ReservationDetailsDTO reservationDetailsDTO) {
        utilsValidator.validateStartAndEndTime(reservationDetailsDTO.getStartTime(), reservationDetailsDTO.getEndTime());
        uniqueReservationValidator.validateUniqueReservation(reservationDetailsDTO);

        HashMap<String, Object> clientAndCourt = utilsService.findClientAndCourt(reservationDetailsDTO.getUsername(), reservationDetailsDTO.getCourtId());
        double price = computePrice(reservationDetailsDTO);
        Client client = (Client)clientAndCourt.get("client");
        utilsService.updateWallet(client, price);

        Reservation newReservation = ReservationBuilder.toEntity(reservationDetailsDTO);
        newReservation.setClient(client);
        newReservation.setCourt((Court)clientAndCourt.get("court"));
        newReservation.setTotalPrice(price);

        Reservation reservation = reservationRepo.save(newReservation);
        LOGGER.debug("Reservation with id {} was inserted in db", reservation.getIdReservation());

        SendBillService sendBillService = new SendBillService();
        sendBillService.generateReservationBill(reservation);
        sendBillService.sendEmailWithBill(reservationDetailsDTO.getUsername(), ((Court)clientAndCourt.get("court")).getLocation().getName(),
                ((Client) clientAndCourt.get("client")).getUser().getEmail(), "ReservationReceipt");

        return reservation.getIdReservation();
    }

    public ReservationDetailsDTO update(UUID idReservation, ReservationDetailsDTO reservationDetailsDTO){
        utilsValidator.validateStartAndEndTime(reservationDetailsDTO.getStartTime(), reservationDetailsDTO.getEndTime());

        Optional<Reservation> reservationOptional = reservationRepo.findById(idReservation);
        if (!reservationOptional.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", idReservation);
            throw new ResourceNotFoundException(Reservation.class.getSimpleName() + " with id: " + idReservation);
        }
        HashMap<String, Object> clientAndCourt = utilsService.findClientAndCourt(reservationDetailsDTO.getUsername(),
                reservationDetailsDTO.getCourtId());

        Reservation reservation = ReservationBuilder.toEntity(reservationDetailsDTO);
        reservation.setClient((Client)clientAndCourt.get("client"));
        reservation.setCourt((Court)clientAndCourt.get("court"));
        reservation.setTotalPrice(computePrice(reservationDetailsDTO));
        reservation.setIdReservation(idReservation);

        reservationRepo.save(reservation);
        LOGGER.debug("Reservation with id {} was updated in db", reservation.getIdReservation());

        return ReservationBuilder.toReservationDetailsDTO(reservation, reservationDetailsDTO.getUsername(),
                reservationDetailsDTO.getCourtId());
    }

    public void deleteReservationById(UUID idReservation){
        Optional<Reservation> reservationOptional = reservationRepo.findById(idReservation);
        if (!reservationOptional.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", idReservation);
            throw new ResourceNotFoundException(Reservation.class.getSimpleName() + " with id: " + idReservation);
        }
        reservationRepo.deleteById(idReservation);
        LOGGER.debug("Reservation with id {} was deleted from db", idReservation);
    }

    public String searchPlayer(ReservationDetailsDTO reservationDetailsDTO) {
        Optional<Court> optional = courtRepo.findById(reservationDetailsDTO.getCourtId());
        if (!optional.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", reservationDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + reservationDetailsDTO.getCourtId());
        }
        String header = reservationDetailsDTO.getUsername().length() + reservationDetailsDTO.getUsername();
        String code = sendMailBroadcast.sendEmail(reservationDetailsDTO.getUsername(), optional.get().getLocation().getAddress(), reservationDetailsDTO.getDate(),
                reservationDetailsDTO.getStartTime());
        return header+code;
    }

    public String transferReservation(ReservationDetailsDTO reservationDetailsDTO) {
        if (reservationDetailsDTO.getIdReservation() == null) {
            LOGGER.error("Received invalid RezervationDetailsDto: idReservation is null");
            throw new ResourceNotFoundException("Received invalid RezervationDetailsDto: idReservation is null");
        }
        if(findReservationById(reservationDetailsDTO.getIdReservation()) == null) {
            LOGGER.error("Reservation with id {} was not found in db", reservationDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Reservation.class.getSimpleName() + " with id: " + reservationDetailsDTO.getIdReservation() + " was not found in db");
        }
        Optional<Court> courtOptional = courtRepo.findById(reservationDetailsDTO.getCourtId());
        if (!courtOptional.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", reservationDetailsDTO.getCourtId());
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + reservationDetailsDTO.getCourtId() + " was not found in db");
        }
        String code = RandomStringUtils.randomAlphanumeric(20);
        JSONObject notification = UtilsService.constructNotificationMessage(reservationDetailsDTO.getUsername(),
                reservationDetailsDTO.getIdReservation(), code);
        template.convertAndSend("/notifications", notification);
        return code;
    }

    public ReservationDetailsDTO changeClient(ReservationDetailsDTO reservationDetailsDTO){
        UUID idReservation=reservationDetailsDTO.getIdReservation();
        UUID idCourt=reservationDetailsDTO.getCourtId();
        String username=reservationDetailsDTO.getUsername();
        Optional<Client> newClient = clientRepo.findByUsername(username);
        if (!newClient.isPresent()) {
            LOGGER.error("Client with username {} was not found in db", username);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with username: " + username);
        }
        ReservationDetailsDTO reservationDetailsDTO2 =  findReservationById(idReservation);
        Reservation reservation=ReservationBuilder.toEntity(reservationDetailsDTO2);
        reservation.setIdReservation(idReservation);
        reservation.setClient(newClient.get());
        update(reservation.getIdReservation(),ReservationBuilder.toReservationDetailsDTO(
                reservation,
                username,
                idCourt));
        return ReservationBuilder.toReservationDetailsDTO(reservation,username,idCourt);
    }
}