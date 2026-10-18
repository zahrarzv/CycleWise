package com.gitgirls.cyclewise.logic.userlogin;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class AuthValidatorTest {

    @Test
    void shouldFailWhenUsernameIsNull() {
        String result = AuthValidator.validateUserName(null);
        assertEquals("Username is required.", result);
    }

    @Test
    void shouldFailWhenUsernameIsEmpty() {
        String result = AuthValidator.validateUserName("");
        assertEquals("Username is required.", result);
    }

    @Test
    void shouldFailWhenUsernameIsWhitespaceOnly() {
        String result = AuthValidator.validateUserName("   ");
        assertEquals("Username is required.", result);
    }

    @Test
    void shouldFailWhenUsernameIsTooShort() {
        String result = AuthValidator.validateUserName("user1");
        assertEquals("Username must be at least 8 characters long.", result);
    }

    @Test
    void shouldPassWhenUsernameIsExactlyEightCharacters() {
        String result = AuthValidator.validateUserName("username");
        assertNull(result);
    }

    @Test
    void shouldPassWhenUsernameIsValid() {
        String result = AuthValidator.validateUserName("username1");
        assertNull(result);
    }

    @Test
    void shouldFailWhenPasswordIsNull() {
        String result = AuthValidator.validatePassword(null);
        assertEquals("Please enter a password.", result);
    }

    @Test
    void shouldFailWhenPasswordIsEmpty() {
        String result = AuthValidator.validatePassword("");
        assertEquals("Please enter a password.", result);
    }

    @Test
    void shouldFailWhenPasswordIsTooShort() {
        String result = AuthValidator.validatePassword("Pass1");
        assertEquals("Password must be at least 8 characters long.", result);
    }

    @Test
    void shouldFailWhenPasswordContainsSpaces() {
        String result = AuthValidator.validatePassword("Pass word1");
        assertEquals("Password cannot contain spaces.", result);
    }

    @Test
    void shouldPassWhenPasswordIsValid() {
        String result = AuthValidator.validatePassword("Password1");
        assertNull(result);
    }

    @Test
    void shouldFailWhenFirstNameIsNull() {
        String result = AuthValidator.validateFirstName(null);
        assertEquals("Please enter your first name.", result);
    }

    @Test
    void shouldFailWhenFirstNameIsEmpty() {
        String result = AuthValidator.validateFirstName("");
        assertEquals("Please enter your first name.", result);
    }

    @Test
    void shouldFailWhenFirstNameHasInvalidCharacters() {
        String result = AuthValidator.validateFirstName("Jane3");
        assertEquals("First name can only contain letters (A–Z).", result);
    }

    @Test
    void shouldPassWhenFirstNameIsValid() {
        String result = AuthValidator.validateFirstName("Jane");
        assertNull(result);
    }

    @Test
    void shouldFailWhenLastNameIsNull() {
        String result = AuthValidator.validateLastName(null);
        assertEquals("Please enter your last name.", result);
    }

    @Test
    void shouldFailWhenLastNameIsEmpty() {
        String result = AuthValidator.validateLastName("");
        assertEquals("Please enter your last name.", result);
    }

    @Test
    void shouldFailWhenLastNameHasInvalidCharacters() {
        String result = AuthValidator.validateLastName("Doe9");
        assertEquals("Last name can only contain letters (A–Z).", result);
    }

    @Test
    void shouldPassWhenLastNameIsValid() {
        String result = AuthValidator.validateLastName("Doe");
        assertNull(result);
    }

    @Test
    void shouldFailWhenBirthDateIsNull() {
        String result = AuthValidator.validateBirthDate(null);
        assertEquals("Please enter your birthdate.", result);
    }

    @Test
    void shouldFailWhenBirthDateIsEmpty() {
        String result = AuthValidator.validateBirthDate("");
        assertEquals("Please enter your birthdate.", result);
    }

    @Test
    void shouldFailWhenBirthDateFormatIsInvalid() {
        String result = AuthValidator.validateBirthDate("2001-11-11");
        assertEquals("Please enter a valid birth date.", result);
    }

    @Test
    void shouldFailWhenBirthDateIsInFuture() {
        String futureDate = LocalDate.now().plusDays(1)
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        String result = AuthValidator.validateBirthDate(futureDate);
        assertEquals("Please choose a birth date that is before today.", result);
    }

    @Test
    void shouldFailWhenUserIsTooYoung() {
        String tooYoungDate = LocalDate.now().minusYears(3)
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        String result = AuthValidator.validateBirthDate(tooYoungDate);
        assertEquals("User must be more than 5 years old.", result);
    }

    @Test
    void shouldPassWhenBirthDateIsValid() {
        String validDate = LocalDate.now().minusYears(10)
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        String result = AuthValidator.validateBirthDate(validDate);
        assertNull(result);
    }

    @Test
    void shouldFailRegistrationWhenUsernameIsInvalid() {
        String result = AuthValidator.validateRegistration("usr", "Password1", "Jane", "Doe", "11/11/2001");
        assertEquals("Username must be at least 8 characters long.", result);
    }

    @Test
    void shouldFailRegistrationWhenFirstNameIsInvalid() {
        String result = AuthValidator.validateRegistration("username1", "Password1", "Jane3", "Doe", "11/11/2001");
        assertEquals("First name can only contain letters (A–Z).", result);
    }

    @Test
    void shouldFailRegistrationWhenLastNameIsInvalid() {
        String result = AuthValidator.validateRegistration("username1", "Password1", "Jane", "Doe9", "11/11/2001");
        assertEquals("Last name can only contain letters (A–Z).", result);
    }

    @Test
    void shouldFailRegistrationWhenPasswordIsInvalid() {
        String result = AuthValidator.validateRegistration("username1", "Pass1", "Jane", "Doe", "11/11/2001");
        assertEquals("Password must be at least 8 characters long.", result);
    }

    @Test
    void shouldFailRegistrationWhenBirthDateIsInvalid() {
        String result = AuthValidator.validateRegistration("username1", "Password1", "Jane", "Doe", "invalid-date");
        assertEquals("Please enter a valid birth date.", result);
    }

    @Test
    void shouldPassRegistrationWhenAllFieldsAreValid() {
        String result = AuthValidator.validateRegistration("username1", "Password1", "Jane", "Doe", "11/11/2001");
        assertNull(result);
    }
}