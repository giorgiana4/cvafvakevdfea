package com.project.backend.controllers;

import com.project.backend.dtos.ResetPasswordDTO;
import com.project.backend.services.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/reset")
public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;

    @Autowired
    public ResetPasswordController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @PostMapping(value = "/{email}")
    public ResponseEntity<String> generateCode(@PathVariable("email") String email) {
        String code = resetPasswordService.sendEmail(email);
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @PostMapping(value = "/confirmation/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable("email") String email, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO){
        String status = resetPasswordService.resetPassword(email, resetPasswordDTO);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
