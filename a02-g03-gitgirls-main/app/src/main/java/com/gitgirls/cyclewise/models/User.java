package com.gitgirls.cyclewise.models;

public class User {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String dob;

    public User(String username, String password, String firstName, String lastName, String dob) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(ModelMessages.ERROR_NULL_OR_EMPTY_USERNAME);
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(ModelMessages.ERROR_NULL_OR_EMPTY_PASSWORD);
        }

        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }
}