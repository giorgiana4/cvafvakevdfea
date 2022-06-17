package com.project.backend.validators;

import com.project.backend.dtos.RegisterDTO;
import com.project.backend.entities.User;
import com.project.backend.exceptions.CustomExceptionMessages;
import com.project.backend.repositories.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegisterValidator {
    private final UserRepo userRepo;

    @Autowired
    public RegisterValidator(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void validateRegistration(RegisterDTO registerDTO){
        validateName(registerDTO.getFirstName());
        validateName(registerDTO.getLastName());
        validateAddress(registerDTO.getAddress());
        validateBirthday(registerDTO.getBirthday());
        validateUsername(registerDTO.getUsername());
        usernameAlreadyExists(registerDTO.getUsername());
        validateEmail(registerDTO.getEmail());
        emailAlreadyExists(registerDTO.getEmail());
        validatePassword(registerDTO.getPassword());
    }

    public void validateName(String name){
        if (name == null || name.equals("") || name.length() < 3) {
            throw new IllegalArgumentException(CustomExceptionMessages.NAME_IS_NULL);
        }

        String regex = "^[\\p{L} -]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);

        if(!m.matches()){
            throw new IllegalArgumentException(CustomExceptionMessages.FORMAT_NAME_INVALID);
        }
    }

    public void validateAddress(String address){
        if (address == null || address.equals("")) {
            throw new IllegalArgumentException(CustomExceptionMessages.ADDRESS_IS_NULL);
        }

        String regex = "[a-zA-Z0-9\\s]{3,}(\\.)? (\\d+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(address);

        if(!m.matches()) {
            throw new IllegalArgumentException(CustomExceptionMessages.FORMAT_ADDRESS_INVALID);
        }
    }

    private void validateBirthday(LocalDateTime birthday){
        LocalDate currentTime = LocalDate.now();
        LocalDate birthdayDate = birthday.toLocalDate();
        if (birthdayDate != null) {
            if (Period.between(birthdayDate, currentTime).getYears() < 18) {
                throw new IllegalArgumentException(CustomExceptionMessages.INVALID_AGE);
            }
        }
    }

    private void validateUsername(String username){
        if (username == null || username.equals("") || username.length() < 5) {
            throw new IllegalArgumentException(CustomExceptionMessages.USERNAME_IS_NULL);
        }

        String regex = "^[a-zA-Z]+[a-zA-Z0-9_.*!@#$%^&?-]{4,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);

        if(!m.matches()) {
            throw new IllegalArgumentException(CustomExceptionMessages.FORMAT_USERNAME_INVALID);
        }
    }

    private void usernameAlreadyExists(String username){
        Optional<User> userOptional = userRepo.findByUsername(username);
        if(userOptional.isPresent()){
            throw new IllegalArgumentException(CustomExceptionMessages.USERNAME_ALREADY_EXISTS);
        }
    }

    private void validateEmail(String email){
        if (email == null || email.equals("") || email.length() < 4) {
            throw new IllegalArgumentException(CustomExceptionMessages.EMAIL_IS_NULL);
        }

        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);

        if(!m.matches()) {
            throw new IllegalArgumentException(CustomExceptionMessages.FORMAT_EMAIL_INVALID);
        }
    }

    private void emailAlreadyExists(String email){
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isPresent()){
            throw new IllegalArgumentException(CustomExceptionMessages.EMAIL_ALREADY_EXISTS);
        }
    }

    public void validatePassword(String password){
        if (password == null || password.equals("") || password.length() < 5) {
            throw new IllegalArgumentException(CustomExceptionMessages.PASSWORD_IS_NULL);
        }

        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{5,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);

        if(!m.matches()) {
            throw new IllegalArgumentException(CustomExceptionMessages.FORMAT_PASSWORD_INVALID);
        }
    }
}