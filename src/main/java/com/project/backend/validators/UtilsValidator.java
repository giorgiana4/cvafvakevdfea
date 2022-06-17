package com.project.backend.validators;

import com.project.backend.dtos.LocationDTO;
import com.project.backend.exceptions.CustomExceptionMessages;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UtilsValidator {
    public void validateLocation(LocationDTO locationDTO){
        validateCoordinates(locationDTO.getLatitude(), locationDTO.getLongitude());
        validateAddressLocation(locationDTO.getAddress());
        validateTimes(locationDTO.getStartHour(), locationDTO.getEndHour());
    }

    public void validateDoublePositive(double price){
        if (price <= 0) {
            throw new IllegalArgumentException(CustomExceptionMessages.PRICE_MUST_BE_BIGGER_THAN_0);
        }
    }

    public void validateStartAndEndTime(int startTime, int endTime){
        if(endTime < startTime) {
            throw new IllegalArgumentException(CustomExceptionMessages.START_TIME_AFTER_END_TIME);
        }else if(endTime - startTime > 2){
            throw new IllegalArgumentException(CustomExceptionMessages.PERIOD_OF_TIME_TO_BIG);
        }
    }

    public void validateIntPositive(int courtNr){
        if (courtNr <= 0) {
            throw new IllegalArgumentException(CustomExceptionMessages.COURT_NUMBER_MUST_BE_BIGGER_THAN_0);
        }
    }

    public void validateDetails(String details){
        if (details == null || details.equals("") || details.length() < 2) {
            throw new IllegalArgumentException(CustomExceptionMessages.DETAILS_IS_NULL);
        }
    }

    private void validateCoordinates(double latitude, double longitude){
        if(latitude < -90 || latitude > 90 || longitude > 180 || longitude < -180)
            throw new IllegalArgumentException(CustomExceptionMessages.LATITUDE_OR_LONGITUDE);
    }

    private void validateAddressLocation(String address){
        if (address == null || address.equals("")) {
            throw new IllegalArgumentException(CustomExceptionMessages.ADDRESS_IS_NULL);
        }

        String regex = "[a-zA-Z0-9\\s]{3,}(\\.)? (\\d*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(address);

        if(!m.matches()) {
            throw new IllegalArgumentException(CustomExceptionMessages.FORMAT_ADDRESS_INVALID_LOCATION);
        }
    }

    private void validateTimes(int startHour, int endHour) {
        if(startHour < 6 || endHour > 23)
            throw new IllegalArgumentException(CustomExceptionMessages.HOURS_INVALID);
    }
}
