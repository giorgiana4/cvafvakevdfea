package com.project.backend.controllers;

import com.project.backend.dtos.RegisterDTO;
import com.project.backend.services.RegisterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/register")
@AllArgsConstructor
@Slf4j
public class RegisterController {
    private RegisterService registerService;

    @PostMapping
    public ResponseEntity<UUID> registerClient(@RequestBody RegisterDTO registerDTO) {
        UUID clientId = registerService.registerClient(registerDTO);
        return new ResponseEntity<>(clientId, HttpStatus.OK);
    }
}
