package com.project.backend.validator;

import com.project.backend.dtos.LocationDTO;
import com.project.backend.validators.UtilsValidator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsValidatorTest {
    private final UtilsValidator utilsValidator = new UtilsValidator();

    @Test
    public void validateDoublePositive(){
        int numberOfErrors = 0;

        try{
            utilsValidator.validateDoublePositive(-20.1);
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(1, numberOfErrors);
    }

    @Test
    public void validateIntPositive(){
        int numberOfErrors = 0;

        try{
            utilsValidator.validateIntPositive(-20);
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(1, numberOfErrors);
    }

    @Test
    public void validateStartEndTime(){
        int numberOfErrors = 0;

        try{
            utilsValidator.validateStartAndEndTime(12, 11);
        }catch (Exception e){
            numberOfErrors++;
        }

        try{
            utilsValidator.validateStartAndEndTime(11, 20);
        }catch (Exception e){
            numberOfErrors++;
        }

        assertEquals(2, numberOfErrors);
    }

    @Test
    public void validateDetails(){
        int numberOfErrors = 0;

        try{
            utilsValidator.validateDetails("");
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(1, numberOfErrors);
    }

    @Test
    public void validateLocation(){
        LocationDTO locationDTOLL = new LocationDTO(null, -190, 190, "Alexandru 13", "Teren Sportiv", 10, 12);
        LocationDTO locationDTOTime = new LocationDTO(null, 77, 51, "Alexandru 13", "Teren Sportiv", 5, 25);
        int numberOfErrors = 0;

        try{
            utilsValidator.validateLocation(locationDTOLL);
        }catch(Exception e){
            numberOfErrors++;
        }

        try{
            utilsValidator.validateLocation(locationDTOTime);
        }catch(Exception e){
            numberOfErrors++;
        }

        assertEquals(2, numberOfErrors);
    }
}
