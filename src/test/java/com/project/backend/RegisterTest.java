package com.project.backend;

import com.project.backend.dtos.ClientDetailsDTO;
import com.project.backend.dtos.RegisterDTO;
import com.project.backend.services.ClientService;
import com.project.backend.services.RegisterService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class RegisterTest {
    @Mock
    private RegisterService registerService = mock(RegisterService.class);
    private ClientService clientService = mock(ClientService.class);
    private RegisterDTO registerDTO = mock(RegisterDTO.class);
    private ClientDetailsDTO clientDetailsDTO = mock(ClientDetailsDTO.class);

    @Test
    public void registerTest() {
        registerDTO = new RegisterDTO("test", "Test123!", "client", "test@gmail.com", "test", "testlast", "address", LocalDateTime.parse("1999-01-17T20:00:00"), 0.0, 0.0);
        Mockito.when(registerService.registerClient(registerDTO))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        clientDetailsDTO = new ClientDetailsDTO(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"), "test", "testlast", "address", LocalDateTime.parse("1999-01-17T20:00:00"), 0.0, 0.0,"username");
        Mockito.when(clientService.findClientById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(clientDetailsDTO);

        UUID registeredClientId = registerService.registerClient(registerDTO);
        Assert.assertNotNull(registeredClientId);

        ClientDetailsDTO returnedClientDto = clientService.findClientById(registeredClientId);
        Assert.assertNotNull(returnedClientDto);
        Assert.assertEquals(returnedClientDto.getFirstName(), "test");
        Assert.assertEquals(returnedClientDto.getLastName(), "testlast");
        Assert.assertEquals(returnedClientDto.getAddress(), "address");
        Assert.assertEquals(returnedClientDto.getBirthday(), LocalDateTime.parse("1999-01-17T20:00:00"));
        Assert.assertEquals(returnedClientDto.getWallet(), 0.0, 0.001);
        Assert.assertEquals(returnedClientDto.getCryptoWallet(), 0.0, 0.001);
    }
}