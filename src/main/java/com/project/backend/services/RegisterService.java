package com.project.backend.services;

import com.project.backend.dtos.RegisterDTO;
import com.project.backend.entities.Client;
import com.project.backend.entities.User;
import com.project.backend.repositories.ClientRepo;
import com.project.backend.validators.RegisterValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @Transactional @Slf4j
public class RegisterService {
    private final ClientRepo clientRepo;
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final RegisterValidator registerValidator;

    @Autowired
    public RegisterService(ClientRepo clientRepo, RegisterValidator registerValidator) {
        this.clientRepo = clientRepo;
        this.registerValidator = registerValidator;
    }

    public UUID registerClient(RegisterDTO registerDTO) {
        registerValidator.validateRegistration(registerDTO);

        User newUser = new User(registerDTO.getUsername(), registerDTO.getPassword(), registerDTO.getRole(), registerDTO.getEmail());
        Client newClient = new Client(registerDTO.getFirstName(), registerDTO.getLastName(), registerDTO.getAddress(), registerDTO.getBirthday(), registerDTO.getWallet(), registerDTO.getCryptoWallet());
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword())); // encrypting user password before saving to database
        newClient.setUser(newUser);
        Client insertedClient = clientRepo.save(newClient);

        return insertedClient.getIdClient();
    }
}