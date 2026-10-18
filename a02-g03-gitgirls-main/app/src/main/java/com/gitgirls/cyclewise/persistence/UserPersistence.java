package com.gitgirls.cyclewise.persistence;

import com.gitgirls.cyclewise.models.User;

/**
 * UserPersistence
 * Interface defining the contract for user account data operations.
 */
public interface UserPersistence {

    boolean addUser(String username, String firstName, String lastName, String password, String dob);

    boolean updateUserInfo(String username, String newPassword);
    boolean removeUser(String username);

    User getByUsername(String username);
}