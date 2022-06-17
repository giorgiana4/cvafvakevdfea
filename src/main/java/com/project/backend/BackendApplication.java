package com.project.backend;

import com.project.backend.dtos.*;
import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.UUID;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // do not use hardcoded secret key in production! This is just as an exercise.
    // It should be securely stored somewhere (encrypted) and used here after it's decrypted
    Algorithm algorithm() { return Algorithm.HMAC256("secret_key".getBytes()); }

    @Bean
    public CommandLineRunner mappingDemo(UserService userService) {
        return args -> {
            try {
                userService.findUserByUsername("admin");
            }catch(ResourceNotFoundException e){
                UserDTO userDTO = new UserDTO(UUID.randomUUID(),
                        "admin", "admin", "admin", "admin@gmail.com");
                userService.insert(userDTO);
            }
        };
    }
}