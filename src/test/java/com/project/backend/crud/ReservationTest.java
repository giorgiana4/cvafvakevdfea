package com.project.backend.crud;

import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.dtos.builders.ReservationBuilder;
import com.project.backend.entities.*;
import com.project.backend.repositories.*;
import com.project.backend.services.ReservationService;
import com.project.backend.services.UtilsService;
import com.project.backend.validators.UtilsValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReservationTest {
    private final List<ReservationDetailsDTO> reservations = new ArrayList<>();
    private final UtilsValidator utilsValidator = new UtilsValidator();

    @Mock
    private ReservationService reservationService = mock(ReservationService.class);
    private UtilsService utilsService = mock(UtilsService.class);
    private ReservationRepo reservationRepo = mock(ReservationRepo.class);
    private ReservationDetailsDTO reservationDetailsDTO = mock(ReservationDetailsDTO.class);
    private CourtRepo courtRepo = mock(CourtRepo.class);
    private ClientRepo clientRepo = mock(ClientRepo.class);
    private TariffRepo tariffRepo = mock(TariffRepo.class);
    private SubscriptionRepo subscriptionRepo = mock(SubscriptionRepo.class);
    private SimpMessagingTemplate template = mock(SimpMessagingTemplate.class);

    @Before
    public void setupFindAll() {
        ReservationDetailsDTO reservation1 = new ReservationDetailsDTO();
        reservation1.setTotalPrice(200);

        ReservationDetailsDTO reservation2 = new ReservationDetailsDTO();
        reservation2.setTotalPrice(300);

        reservations.add(reservation1);
        reservations.add(reservation2);
    }

    @Before
    public void setupFindById() {
        reservationDetailsDTO.setDate(LocalDate.of(2022, 2, 12));
        reservationDetailsDTO.setStartTime(12);
        reservationDetailsDTO.setEndTime(14);
        reservationDetailsDTO.setTotalPrice(400);
    }

    @Test
    public void findAllTest() {
        Mockito.when(reservationService.findReservations()).thenReturn(reservations);

        List<ReservationDetailsDTO> reservationsFindAll = reservationService.findReservations();
        assertNotNull(reservationsFindAll);
        assertEquals(2, reservationsFindAll.size());
    }

    @Test
    public void findByIdReservation() {
        Mockito.when(reservationService.findReservationById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(reservationDetailsDTO);

        ReservationDetailsDTO reservationDTOTest = reservationService.findReservationById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        assertEquals(reservationDetailsDTO, reservationDTOTest);
    }

    private void addTariffs(List<Tariff> tariffs){
        tariffs.add(new Tariff("Summer", "Workweek", true, 80.0));
        tariffs.add(new Tariff("Summer", "Workweek", false, 70.0));
        tariffs.add(new Tariff("Summer", "Weekend", true, 100.0));
        tariffs.add(new Tariff("Summer", "Weekend", false, 90.0));
        tariffs.add(new Tariff("Winter", "Workweek", true, 100.0));
        tariffs.add(new Tariff("Winter", "Workweek", false, 90.0));
        tariffs.add(new Tariff("Winter", "Weekend", true, 120.0));
        tariffs.add(new Tariff("Winter", "Weekend", false, 110.0));
    }

    @Test
    public void computePriceTest() {
        Court court = new Court(1, "Clay", "details");
        Location location = new Location(46.77089056276414, 23.63603604531141,
                "Strada Alexandru Vaida Voievod", "Baza Sportiva Gheorgheni", 10, 22);
        court.setLocation(location);
        Mockito.when(courtRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(court));

        List<Tariff> tariffs = new ArrayList<>();
        addTariffs(tariffs);

        Mockito.when(tariffRepo.findAllByLocation(location)).thenReturn(tariffs);

        UtilsService utilsBL=new UtilsService(subscriptionRepo,courtRepo,clientRepo,utilsValidator,tariffRepo,reservationRepo);
        ReservationService myReservationService = new ReservationService(reservationRepo, courtRepo,
                clientRepo, utilsValidator, utilsBL, template);

        ReservationDetailsDTO reservationDetailsDTO = new ReservationDetailsDTO(null,
                LocalDate.of(2022, 3, 22), 10, 12, 0.0, "test",
                UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        double price = myReservationService.computePrice(reservationDetailsDTO);
        assertEquals(180.0, price, 0.01);

        ReservationDetailsDTO reservationDetailsDTO2 = new ReservationDetailsDTO(null,
                LocalDate.of(2022, 3, 22), 20, 22, 0.0, "test",
                UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        double price2 = myReservationService.computePrice(reservationDetailsDTO2);
        assertEquals(190.0, price2, 0.01);
    }

    @Test
    public void testInsertReservation() {
        assertNotNull(reservationRepo);
        Reservation insertReservation = new Reservation(LocalDate.of(2022,10,10), 10, 12, 400);

        Mockito.when(reservationService.insert(ReservationBuilder.toReservationDetailsDTO(insertReservation,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"))))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(reservationService.findReservationById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(ReservationBuilder.toReservationDetailsDTO(insertReservation,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")));

        UUID id = reservationService.insert(ReservationBuilder.toReservationDetailsDTO(insertReservation,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")));
        Assert.assertNotNull(id);

        ReservationDetailsDTO reservationDTOTest = reservationService.findReservationById(id);

        assertEquals(10, reservationDTOTest.getStartTime());
        assertEquals(12, reservationDTOTest.getEndTime());
    }

    @Test
    public void testDeleteReservation() {
        assertNotNull(reservationRepo);
        Reservation reservation1 = new Reservation(LocalDate.of(2022,10,10), 10, 13, 400);

        Mockito.when(reservationService.insert(ReservationBuilder.toReservationDetailsDTO(reservation1,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"))))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(reservationRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(reservation1));

        ReservationService customerBL = new ReservationService(reservationRepo, courtRepo, clientRepo, utilsValidator, utilsService, template);

        UUID id = UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5");
        customerBL.deleteReservationById(id);
        verify(reservationRepo, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateReservation() {
        assertNotNull(reservationRepo);
        assertNotNull(clientRepo);
        assertNotNull(courtRepo);

        User user=new User("username","passW1!","client","user@yahoo.com");
        user.setIdUser(UUID.fromString("723eced5-7d06-4b86-b6c2-42918473110b"));
        Client client=new Client("pop","ana","address", LocalDateTime.now(),200,200);
        client.setIdClient(UUID.fromString("b8f1ee40-319c-493e-b67a-e99dc42fe3bd"));
        client.setUser(user);
        Location location=new Location(20,20,"address","location",2,4);
        Court court=new Court(2,"Clay", "Very good field");
        court.setIdCourt(UUID.fromString("4493cbef-2366-49a9-9098-44a43339f155"));
        court.setLocation(location);

        Reservation reservation1 = new Reservation(LocalDate.of(2022,10,10), 10, 11, 400);
        reservation1.setClient(client);
        reservation1.setCourt(court);
        Reservation reservationUp = new Reservation(LocalDate.of(2022,10,12), 10, 11, 400);
        reservationUp.setClient(client);
        reservationUp.setCourt(court);

        Mockito.when(reservationRepo.findById(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b")))
                .thenReturn(Optional.of(reservation1));
        List<Client> clients=new ArrayList<>();
        clients.add(client);
        Mockito.when(clientRepo.findAll()).thenReturn(clients);
        Mockito.when(courtRepo.findById(UUID.fromString("4493cbef-2366-49a9-9098-44a43339f155"))).thenReturn(Optional.of(court));

        Mockito.when(reservationRepo.save(Mockito.any(Reservation.class))).thenReturn(reservationUp);

        ReservationService reservationBL = new ReservationService(reservationRepo, courtRepo, clientRepo, utilsValidator, utilsService, template);
        ReservationDetailsDTO updateReservation = reservationBL.update(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"),
                ReservationBuilder.toReservationDetailsDTO(reservationUp,"username",UUID.fromString("4493cbef-2366-49a9-9098-44a43339f155")));

        assertEquals(10, updateReservation.getStartTime());
        assertEquals(11, updateReservation.getEndTime());
    }
}