package com.project.backend.validator;

import com.project.backend.dtos.RegisterDTO;
import com.project.backend.repositories.UserRepo;
import com.project.backend.validators.RegisterValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RegisterValidatorTest {
    private RegisterValidator registerValidator;

    @Mock
    private UserRepo userRepo = mock(UserRepo.class);

    @Before
    public void setup(){
        registerValidator = new RegisterValidator(userRepo);
    }

    @Test
    public void validateName(){
        int numberOfErrors = 0;

        try{
            registerValidator.validateName("");
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            registerValidator.validateName("a");
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            registerValidator.validateName("1331f");
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(3, numberOfErrors);
    }

    @Test
    public void validateAddress(){
        int numberOfErrors = 0;

        try{
            registerValidator.validateAddress("");
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            registerValidator.validateAddress("a");
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            registerValidator.validateAddress("1331f");
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(3, numberOfErrors);
    }

    @Test
    public void validatePassword(){
        int numberOfErrors = 0;

        try{
            registerValidator.validatePassword("");
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            registerValidator.validatePassword("a");
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            registerValidator.validatePassword("1331f");
        }catch(Exception e){
            numberOfErrors++;
        }
        try{
            registerValidator.validatePassword("abasbcuiabus");
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(4, numberOfErrors);
    }

    @Test
    public void validateRegister(){
        RegisterDTO registerDTOEmail = new RegisterDTO("username123", "sTroN18!", "client", "gresit", "Ioachim",
                "Radu", "Tabacarilor 12", LocalDateTime.now(), -10.0, -10.9);
        RegisterDTO registerDTOWalletNegative = new RegisterDTO("username123", "sTroN18!", "client", "email@gmail.com", "Ioachim",
                "Radu", "Tabacarilor 12", LocalDateTime.now(), -10.0, -10.9);

        int numberOfErrors = 0;

        try{
            registerValidator.validateRegistration(registerDTOEmail);
        }catch(Exception e){
           numberOfErrors++;
        }

        try{
            registerValidator.validateRegistration(registerDTOWalletNegative);
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(2, numberOfErrors);
    }
}
