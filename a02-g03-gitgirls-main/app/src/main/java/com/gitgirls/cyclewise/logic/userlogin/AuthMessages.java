package com.gitgirls.cyclewise.logic.userlogin;

public final class AuthMessages {

    private AuthMessages() {}

    public static final String ALL_FIELDS_REQUIRED = "All fields are required.";
    public static final String USERNAME_REQUIRED = "Username is required.";
    public static final String USERNAME_TOO_SHORT = "Username must be at least 8 characters long.";
    public static final String PASSWORD_REQUIRED = "Please enter a password.";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least 8 characters long.";
    public static final String PASSWORD_SPACES = "Password cannot contain spaces.";
    public static final String FIRST_NAME_REQUIRED = "Please enter your first name.";
    public static final String FIRST_NAME_INVALID = "First name can only contain letters (A–Z).";
    public static final String LAST_NAME_REQUIRED = "Please enter your last name.";
    public static final String LAST_NAME_INVALID = "Last name can only contain letters (A–Z).";
    public static final String BIRTHDATE_REQUIRED = "Please enter your birthdate.";
    public static final String BIRTHDATE_BEFORE_TODAY = "Please choose a birth date that is before today.";
    public static final String USER_TOO_YOUNG = "User must be more than 5 years old.";
    public static final String BIRTHDATE_INVALID = "Please enter a valid birth date.";
    public static final String USERNAME_EXISTS = "Username already exists. Try another name.";
    public static final String REGISTRATION_SUCCESS = "Registered successfully.";
    public static final String REGISTRATION_FAILED = "Registration failed.";
    public static final String USERNAME_AND_PASSWORD_REQUIRED = "Username and password required.";
    public static final String USER_NOT_FOUND = "Cannot find a user with that username.";
    public static final String PASSWORD_MISMATCH = "Password doesn't match. Try again.";
    public static final String LOGIN_SUCCESS = "Login successful.";
    public static final String ACCOUNT_DELETE_SUCCESS = "Account deleted successfully.";
    public static final String ACCOUNT_DELETE_FAILED = "Failed to delete account.";
}