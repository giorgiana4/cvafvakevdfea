package com.project.backend.crud;

import com.project.backend.dtos.ClientDTO;
import com.project.backend.dtos.ClientDetailsDTO;
import com.project.backend.dtos.RegisterDTO;
import com.project.backend.dtos.builders.ClientBuilder;
import com.project.backend.entities.Client;
import com.project.backend.entities.User;
import com.project.backend.repositories.ClientRepo;
import com.project.backend.repositories.UserRepo;
import com.project.backend.services.ClientService;
import com.project.backend.validators.RegisterValidator;
import com.project.backend.services.RegisterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ClientTest {
    private final List<ClientDTO> clients = new ArrayList<>();
    private RegisterValidator registerValidator;

    @Mock
    private ClientService clientService = mock(ClientService.class);
    private ClientRepo clientRepo = mock(ClientRepo.class);
    private UserRepo userRepo = mock(UserRepo.class);
    private ClientDetailsDTO clientDTO = mock(ClientDetailsDTO.class);
    private RegisterService registerService = mock(RegisterService.class);

    @Before
    public void setup(){
        registerValidator = new RegisterValidator(userRepo);
    }

    @Before
    public void setupFindAll() {
        ClientDTO client1 = new ClientDTO();
        client1.setFirstName("Ionut");
        client1.setLastName("Gabigol");

        ClientDTO client2 = new ClientDTO();
        client1.setFirstName("Ionutdoi");
        client1.setLastName("Gabigoldoi");

        clients.add(client1);
        clients.add(client2);
    }

    @Before
    public void setupFindById(){
        clientDTO.setFirstName("Ion");
        clientDTO.setLastName("Irinca");
        clientDTO.setAddress("Ion Sugariu");
        clientDTO.setBirthday(LocalDateTime.now());
    }

    @Test
    public void findAllTest(){
        Mockito.when(clientService.findClients()).thenReturn(clients);

        List<ClientDTO> clientsFindAll = clientService.findClients();
        assertNotNull(clientsFindAll);
        assertEquals(2, clientsFindAll.size());
    }

    @Test
    public void findByIdClient() {
        Mockito.when(clientService.findClientById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(clientDTO);

        ClientDetailsDTO clientDetailsDTO = clientService.findClientById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        assertEquals(clientDTO, clientDetailsDTO);
    }

    @Test
    public void testDeleteClient(){
        assertNotNull(clientRepo);
        Client insertClient = new Client("ana", "matei", "client", LocalDateTime.now(), 10.0, 10.2);

        Mockito.when(clientService.insert(UUID.randomUUID(), ClientBuilder.toClientDetailsDTO(insertClient,"username")))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(clientRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(insertClient));

        ClientService customerBL=new ClientService(clientRepo,userRepo,registerValidator);
        UUID id = UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5");
        customerBL.deleteClientById(id);
        verify(clientRepo, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateClient(){
        assertNotNull(clientRepo);
        User user=new User("raulpop","Parola1!","client","raulpop@yahoo.com");
        Client client1 = new Client("ana", "matei", "Tabacarilor 12", LocalDateTime.now(), 10.0, 10.2);
        client1.setUser(user);
        Client clientUp = new Client("anaUpdate", "anaUpdateLast", "Tabacarilor 12", LocalDateTime.now(), 10.0, 10.2);
        clientUp.setUser(user);
        RegisterDTO registerDTO=new RegisterDTO("raulpop","Parola1!","client","raulpop@yahoo.com","ana","matei","Tabacarilor 12",LocalDateTime.now(),10.0,10.2);
        Mockito.when(registerService.registerClient(registerDTO)).thenReturn(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"));
        Mockito.when(clientRepo.findById(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b")))
                .thenReturn(Optional.of(client1));
        Mockito.when(clientRepo.save(Mockito.any(Client.class))).thenReturn(clientUp);
        ClientService clientBL=new ClientService(clientRepo,userRepo,registerValidator);
        ClientDetailsDTO updateClient = clientBL.update(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"),
                ClientBuilder.toClientDetailsDTO(clientUp,"username"));

        assertEquals("anaUpdate", updateClient.getFirstName());
        assertEquals("anaUpdateLast", updateClient.getLastName());
    }
}
