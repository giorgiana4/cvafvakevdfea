package com.project.backend.exceptions;

public class CustomExceptionMessages {
    public static final String NAME_IS_NULL = "Name is null or it's length is smaller than 3!";
    public static final String FORMAT_NAME_INVALID = "The name must contains only letters and the minimum length is 3!";
    public static final String ADDRESS_IS_NULL = "Address is null!";
    public static final String FORMAT_ADDRESS_INVALID = "The address must contains at least one digit, one space and the minimum length is 6!";
    public static final String FORMAT_ADDRESS_INVALID_LOCATION = "The address is null or it's length is lower than 3. The address can contains letters or digit (optional)!";
    public static final String INVALID_AGE = "You must be at least 18 years old!";
    public static final String USERNAME_IS_NULL = "Username is null or it's length is smaller than 4!";
    public static final String FORMAT_USERNAME_INVALID = "Username must start with a letter!";
    public static final String EMAIL_IS_NULL = "Email is null or it's length is smaller than 4!";
    public static final String FORMAT_EMAIL_INVALID = "Email format is invalid!";
    public static final String PASSWORD_IS_NULL = "Password is null or it's length is smaller than 4!";
    public static final String FORMAT_PASSWORD_INVALID = "Password must contains at least one digit, one special character and one uppercase letter!";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists!";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists!";
    public static final String PRICE_MUST_BE_BIGGER_THAN_0 = "Price for tariff must be bigger than 0!";
    public static final String START_TIME_AFTER_END_TIME = "End time must be after start time";
    public static final String PERIOD_OF_TIME_TO_BIG = "Period of time must be lower than 4!";
    public static final String COURT_NUMBER_MUST_BE_BIGGER_THAN_0 = "Court number must be bigger than 0!";
    public static final String DETAILS_IS_NULL = "Details are incomplete!";
    public static final String LATITUDE_OR_LONGITUDE = "Latitude or longitude is incorrect!";
    public static final String HOURS_INVALID = "Start or end hour is outside of the interval!";
    public static final String YOU_CAN_NOT_HAVE_TWO_RESERVATIONS = "You can not have two reservations in future!";
    public static final String YOU_CAN_NOT_HAVE_TWO_SUBSCRIPTIONS = "You can not have two subscriptions in future!";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds in the wallet!";
}
