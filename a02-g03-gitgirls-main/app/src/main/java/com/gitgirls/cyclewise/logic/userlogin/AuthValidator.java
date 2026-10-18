package com.gitgirls.cyclewise.logic.userlogin;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class AuthValidator {
    private static final int MIN_USERNAME_LENGTH = 8;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_AGE = 5;
    private static final String NAME_PATTERN = "[a-zA-Z]+";
    private static final String BIRTHDATE_PATTERN = "MM/dd/yyyy";
    private static final DateTimeFormatter BIRTHDATE_FORMATTER =
            DateTimeFormatter.ofPattern(BIRTHDATE_PATTERN);

    private AuthValidator() {}

    public static String validateRegistration(String userName, String pass, String firstName, String lastName, String dob) {

        String userNameError = validateUserName(userName);
        if (userNameError != null) return userNameError;

        String firstNameError = validateFirstName(firstName);
        if (firstNameError != null) return firstNameError;

        String lastNameError = validateLastName(lastName);
        if (lastNameError != null) return lastNameError;

        String passwordError = validatePassword(pass);
        if (passwordError != null) return passwordError;

        String birthDateError = validateBirthDate(dob);
        if (birthDateError != null) return birthDateError;

        return null;
    }

    public static String validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return AuthMessages.USERNAME_REQUIRED;
        }
        if (userName.length() < MIN_USERNAME_LENGTH) {
            return AuthMessages.USERNAME_TOO_SHORT;
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return AuthMessages.PASSWORD_REQUIRED;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return AuthMessages.PASSWORD_TOO_SHORT;
        }
        if (password.contains(" ")) {
            return AuthMessages.PASSWORD_SPACES;
        }
        return null;
    }

    public static String validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return AuthMessages.FIRST_NAME_REQUIRED;
        }
        if (!firstName.matches(NAME_PATTERN)) {
            return AuthMessages.FIRST_NAME_INVALID;
        }
        return null;
    }

    public static String validateLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return AuthMessages.LAST_NAME_REQUIRED;
        }
        if (!lastName.matches(NAME_PATTERN)) {
            return AuthMessages.LAST_NAME_INVALID;
        }
        return null;
    }

    public static String validateBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.isEmpty()) {
            return AuthMessages.BIRTHDATE_REQUIRED;
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr, BIRTHDATE_FORMATTER);
            LocalDate today = LocalDate.now();

            if (birthDate.isAfter(today)) {
                return AuthMessages.BIRTHDATE_BEFORE_TODAY;
            }

            if (Period.between(birthDate, today).getYears() < MIN_AGE) {
                return AuthMessages.USER_TOO_YOUNG;
            }

            return null;
        } catch (DateTimeParseException e) {
            return AuthMessages.BIRTHDATE_INVALID;
        }
    }
}