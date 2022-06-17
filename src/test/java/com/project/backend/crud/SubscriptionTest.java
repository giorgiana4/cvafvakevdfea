package com.project.backend.crud;

import com.project.backend.dtos.SubscriptionDetailsDTO;
import com.project.backend.dtos.builders.SubscriptionBuilder;
import com.project.backend.entities.*;
import com.project.backend.repositories.*;
import com.project.backend.services.SubscriptionService;
import com.project.backend.services.UtilsService;
import com.project.backend.validators.UtilsValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class SubscriptionTest {
    private final List<SubscriptionDetailsDTO> subscriptions = new ArrayList<>();
    private final UtilsValidator utilsValidator = new UtilsValidator();

    @Mock
    private SubscriptionService subscriptionService = mock(SubscriptionService.class);
    private UtilsService utilsService = mock(UtilsService.class);
    private SubscriptionRepo subscriptionRepo = mock(SubscriptionRepo.class);
    private SubscriptionDetailsDTO subscriptionDetailsDTO = mock(SubscriptionDetailsDTO.class);
    private CourtRepo courtRepo = mock(CourtRepo.class);
    private ClientRepo clientRepo = mock(ClientRepo.class);
    private TariffRepo tariffRepo = mock(TariffRepo.class);
    private ReservationRepo reservationRepo = mock(ReservationRepo.class);

    @Before
    public void setupFindAll() {
        SubscriptionDetailsDTO subscription1 = new SubscriptionDetailsDTO();
        subscription1.setIdSubscription(UUID.randomUUID());
        subscription1.setStartDate(LocalDate.now());
        subscription1.setEndTime(12);
        subscription1.setStartTime(10);
        subscription1.setDayOfWeek("L-V");
        subscription1.setTotalPrice(200);

        SubscriptionDetailsDTO subscription2 = new SubscriptionDetailsDTO();
        subscription2.setIdSubscription(UUID.randomUUID());
        subscription2.setStartDate(LocalDate.now());
        subscription2.setStartTime(10);
        subscription2.setEndTime(12);
        subscription2.setDayOfWeek("S-D");
        subscription2.setTotalPrice(300);

        subscriptions.add(subscription1);
        subscriptions.add(subscription1);
    }

    @Before
    public void setupFindById(){
        subscriptionDetailsDTO.setStartDate(LocalDate.now());
        subscriptionDetailsDTO.setStartTime(12);
        subscriptionDetailsDTO.setEndTime(14);
        subscriptionDetailsDTO.setDayOfWeek("L-V");
        subscriptionDetailsDTO.setTotalPrice(400);
    }

    @Test
    public void findAllTest(){
        Mockito.when(subscriptionService.findSubscriptions()).thenReturn(subscriptions);

        List<SubscriptionDetailsDTO> subscriptionsFindAll = subscriptionService.findSubscriptions();
        assertNotNull(subscriptionsFindAll);
        assertEquals( 2, subscriptionsFindAll.size());
    }

    @Test
    public void findByIdSubscription(){
        Mockito.when(subscriptionService.findSubscriptionById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(subscriptionDetailsDTO);

        SubscriptionDetailsDTO subscriptionDTOTest = subscriptionService.findSubscriptionById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        assertEquals(subscriptionDetailsDTO, subscriptionDTOTest);
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

        UtilsService utilsBL = new UtilsService(subscriptionRepo,courtRepo,clientRepo,utilsValidator,tariffRepo,reservationRepo);
        SubscriptionService mySubscriptionService = new SubscriptionService(subscriptionRepo, courtRepo, utilsValidator, utilsBL);

        SubscriptionDetailsDTO subscriptionDetailsDTO = new SubscriptionDetailsDTO(null,
                LocalDate.of(2022, 3, 22), 10, 12, "tuesday",
                0.0, "test", UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        double price = mySubscriptionService.computePrice(subscriptionDetailsDTO);
        assertEquals(765.0, price, 0.01);

        SubscriptionDetailsDTO subscriptionDetailsDTO2 = new SubscriptionDetailsDTO(null,
                LocalDate.of(2022, 3, 22), 20, 22, "tuesday",
                0.0, "test", UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        double price2 = mySubscriptionService.computePrice(subscriptionDetailsDTO2);
        assertEquals(807.5, price2, 0.01);
    }

    @Test
    public void testInsertSubscription(){
        assertNotNull(subscriptionRepo);
        Subscription insertSubscription = new Subscription(LocalDate.of(2022,2,2),10,12,"L-V");

        Mockito.when(subscriptionService.insert(SubscriptionBuilder.toSubscriptionDetailsDTO(insertSubscription,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"))))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(subscriptionService.findSubscriptionById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(SubscriptionBuilder.toSubscriptionDetailsDTO(insertSubscription,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")));

        UUID id = subscriptionService.insert(SubscriptionBuilder.toSubscriptionDetailsDTO(insertSubscription,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")));
        Assert.assertNotNull(id);

        SubscriptionDetailsDTO subscriptionDTOTest = subscriptionService.findSubscriptionById(id);

        assertEquals("L-V", subscriptionDTOTest.getDayOfWeek());
        assertEquals(LocalDate.of(2022,2,2), subscriptionDTOTest.getStartDate());
    }

    @Test
    public void testDeleteSubscription(){
        assertNotNull(subscriptionRepo);
        Subscription subscription1 = new Subscription(LocalDate.of(2022,2,2),12,14,"S-D",2);

        Mockito.when(subscriptionService.insert(SubscriptionBuilder.toSubscriptionDetailsDTO(subscription1,"username",UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"))))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(subscriptionRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(subscription1));

        SubscriptionService subscriptionBL=new SubscriptionService(subscriptionRepo,courtRepo, utilsValidator, utilsService);
        UUID id = UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5");
        subscriptionBL.deleteSubscriptionById(id);
        verify(subscriptionRepo, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateSubscription(){
        assertNotNull(subscriptionRepo);
        assertNotNull(clientRepo);
        assertNotNull(courtRepo);

        User user=new User("username","passW1!","client","user@yahoo.com");
        user.setIdUser(UUID.fromString("723eced5-7d06-4b86-b6c2-42918473110b"));
        Client client=new Client("pop","ana","adresa", LocalDateTime.now(),200,200);
        client.setIdClient(UUID.fromString("b8f1ee40-319c-493e-b67a-e99dc42fe3bd"));
        client.setUser(user);
        Location location=new Location(20,20,"address","location",2,4);
        Court court=new Court(2,"Clay", "Medium size");
        court.setIdCourt(UUID.fromString("4493cbef-2366-49a9-9098-44a43339f155"));
        court.setLocation(location);

        Subscription subscription1 = new Subscription(LocalDate.of(2022,2,2), 10, 11, "monday", 2);
        subscription1.setClient(client);
        subscription1.setCourt(court);
        Subscription subscriptionUp = new Subscription(LocalDate.of(2022,2,2), 10, 11, "monday",2);
        subscriptionUp.setClient(client);
        subscriptionUp.setCourt(court);

        Mockito.when(subscriptionRepo.findById(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b")))
                .thenReturn(Optional.of(subscription1));
        List<Client> clients=new ArrayList<>();
        clients.add(client);
        Mockito.when(clientRepo.findAll()).thenReturn(clients);
        Mockito.when(courtRepo.findById(UUID.fromString("4493cbef-2366-49a9-9098-44a43339f155"))).thenReturn(Optional.of(court));

        Mockito.when(subscriptionRepo.save(Mockito.any(Subscription.class))).thenReturn(subscriptionUp);

        SubscriptionService subscriptionBL=new SubscriptionService(subscriptionRepo,courtRepo, utilsValidator, utilsService);
        SubscriptionDetailsDTO updateSubscription = subscriptionBL.update(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"),
                SubscriptionBuilder.toSubscriptionDetailsDTO(subscriptionUp,"username",UUID.fromString("4493cbef-2366-49a9-9098-44a43339f155")));

        assertEquals("monday", updateSubscription.getDayOfWeek());
        assertEquals(10, updateSubscription.getStartTime());
    }
}
