package com.gitgirls.cyclewise.logic.userlogin;

import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.persistence.UserPersistence;

public class AuthService {

    private final UserPersistence userPersistence;

    public AuthService(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    public AuthResult register(String userName, String pass, String firstName, String lastName, String dob) {
        String validationError = AuthValidator.validateRegistration(userName, pass, firstName, lastName, dob);
        if (validationError != null) {
            return AuthResult.failure(validationError);
        }

        if (userPersistence.getByUsername(userName) != null) {
            return AuthResult.failure(AuthMessages.USERNAME_EXISTS);
        }

        boolean added = userPersistence.addUser(userName, firstName, lastName, pass, dob);

        if (added) {
            return AuthResult.success(AuthMessages.REGISTRATION_SUCCESS);
        }
        else {
            return AuthResult.failure(AuthMessages.REGISTRATION_FAILED);
        }
    }

    public AuthResult login(String userName, String pass) {
        if (userName == null || userName.trim().isEmpty()) {
            return AuthResult.failure(AuthMessages.USERNAME_REQUIRED);
        }

        if (pass == null || pass.trim().isEmpty()) {
            return AuthResult.failure(AuthMessages.PASSWORD_REQUIRED);
        }

        User user = userPersistence.getByUsername(userName);
        if (user == null) {
            return AuthResult.failure(AuthMessages.USER_NOT_FOUND);
        }

        if (!user.getPassword().equals(pass)) {
            return AuthResult.failure(AuthMessages.PASSWORD_MISMATCH);
        }

        return AuthResult.success(AuthMessages.LOGIN_SUCCESS);
    }

    public AuthResult deleteAccount(String username) {
        if (username == null || username.trim().isEmpty()) {
            return AuthResult.failure(AuthMessages.USERNAME_REQUIRED);
        }

        boolean removed = userPersistence.removeUser(username);
        if (removed) {
            return AuthResult.success(AuthMessages.ACCOUNT_DELETE_SUCCESS);
        } else {
            return AuthResult.failure(AuthMessages.ACCOUNT_DELETE_FAILED);
        }
    }

    public User getUserByUsername(String username) {
        return userPersistence.getByUsername(username);
    }
}