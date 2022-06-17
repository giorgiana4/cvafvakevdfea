package com.project.backend.services;

import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.ClientDTO;
import com.project.backend.dtos.ClientDetailsDTO;
import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.dtos.builders.ClientBuilder;
import com.project.backend.entities.Client;
import com.project.backend.entities.Reservation;
import com.project.backend.entities.User;
import com.project.backend.repositories.ClientRepo;
import com.project.backend.repositories.UserRepo;
import com.project.backend.validators.RegisterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepo clientRepo;
    private final UserRepo userRepo;
    private final RegisterValidator registerValidator;

    @Autowired
    public ClientService(ClientRepo clientRepo, UserRepo userRepo, RegisterValidator registerValidator) {
        this.clientRepo = clientRepo;
        this.userRepo = userRepo;
        this.registerValidator = registerValidator;
    }

    public List<ClientDTO> findClients() {
        List<Client> clientList = clientRepo.findAll();
        if (clientList.isEmpty()) {
            LOGGER.error("No client found");
            throw new ResourceNotFoundException(Client.class.getSimpleName());
        }
        return clientList.stream()
                .map(ClientBuilder::toClientDTO)
                .collect(Collectors.toList());
    }

    public List<ClientDetailsDTO> findClientsDetails() {
        List<Client> clientList = clientRepo.findAll();
        if (clientList.isEmpty()) {
            LOGGER.error("No client found");
            throw new ResourceNotFoundException(Client.class.getSimpleName());
        }
        List<ClientDetailsDTO> clientDetailsDTOList=new ArrayList<>();
        for(Client client: clientList)
            clientDetailsDTOList.add(ClientBuilder.toClientDetailsDTO(client,client.getUser().getUsername()));
        return clientDetailsDTOList;
    }

    public ClientDetailsDTO findClientById(UUID id) {
        Optional<Client> clientOptional = clientRepo.findById(id);
        if (!clientOptional.isPresent()) {
            LOGGER.error("Client with id {} was not found in db", id);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with id: " + id);
        }
        return ClientBuilder.toClientDetailsDTO(clientOptional.get(),clientOptional.get().getUser().getUsername());
    }

    public ClientDetailsDTO findClientByUsername(String username) {
        Optional<Client> clientOptional = clientRepo.findByUsername(username);
        if (!clientOptional.isPresent()) {
            LOGGER.error("Client with username {} was not found in db", username);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with username: " + username);
        }
        return ClientBuilder.toClientDetailsDTO(clientOptional.get(),clientOptional.get().getUser().getUsername());
    }

    //nu se foloseste
    public UUID insert(UUID userId, ClientDetailsDTO clientDetailsDTO) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", userId);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userId);
        }
        User user = userOptional.get();
        Client client = ClientBuilder.toEntity(clientDetailsDTO);
        client.setUser(user);
        client = clientRepo.save(client);
        LOGGER.debug("Client with id {} was inserted in db", client.getIdClient());
        return client.getIdClient();
    }

    public ClientDetailsDTO update(UUID clientId, ClientDetailsDTO clientDetailsDTO){
        registerValidator.validateName(clientDetailsDTO.getLastName());
        registerValidator.validateAddress(clientDetailsDTO.getAddress());

        Optional<Client> clientOptional = clientRepo.findById(clientId);
        if (!clientOptional.isPresent()) {
            LOGGER.error("Client with id {} was not found in db", clientId);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with id: " + clientId);
        }
        Client client = ClientBuilder.toEntity(clientDetailsDTO);
        client.setIdClient(clientId);
        List<User> users=userRepo.findAll();
        for(User user:users){
            if(user.getUsername().equals(clientDetailsDTO.getUsername()))
                client.setUser(user);
        }
        client = clientRepo.save(client);
        LOGGER.debug("Client with id {} was updated in db", client.getIdClient());
        return ClientBuilder.toClientDetailsDTO(client, client.getUser().getUsername());
    }

    public void deleteClientById(UUID clientId){
        Optional<Client> clientOptional = clientRepo.findById(clientId);
        if (!clientOptional.isPresent()) {
            LOGGER.error("Client with id {} was not found in db", clientId);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with id: " + clientId);
        }
        clientRepo.deleteById(clientId);
        LOGGER.debug("Client with id {} was deleted from db", clientId);
    }

    public double returnMoney(ReservationDetailsDTO reservationDetailsDTO){
        String username=reservationDetailsDTO.getUsername();
        Optional<Client> client = clientRepo.findByUsername(username);
        if (!client.isPresent()) {
            LOGGER.error("Client with username {} was not found in db", username);
            throw new ResourceNotFoundException(Client.class.getSimpleName() + " with username: " + username);
        }
        Period period= Period.between(LocalDate.now(), reservationDetailsDTO.getDate());
        if(period.getDays()>1 || (period.getDays()==1 && reservationDetailsDTO.getStartTime()> LocalDateTime.now().getHour())) {
            double amount = reservationDetailsDTO.getTotalPrice();
            ClientDetailsDTO clientDetailsDTO = ClientBuilder.toClientDetailsDTO(
                    client.get(),
                    username
            );

            clientDetailsDTO.setWallet(clientDetailsDTO.getWallet()+amount);
            update(client.get().getIdClient(), clientDetailsDTO);
        }
        return client.get().getWallet();

    }
}
