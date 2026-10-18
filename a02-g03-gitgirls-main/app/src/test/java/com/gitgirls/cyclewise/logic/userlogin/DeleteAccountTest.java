package com.gitgirls.cyclewise.logic.userlogin;

import com.gitgirls.cyclewise.persistence.UserPersistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteAccountTest {

    @Mock
    private UserPersistence userPersistence;

    private AuthService authenticator;

    @BeforeEach
    void setup() {
        authenticator = new AuthService(userPersistence);
    }

    @Test
    public void shouldDeleteAccountSuccessfully() {
        when(userPersistence.removeUser("username1")).thenReturn(true);

        AuthResult result = authenticator.deleteAccount("username1");

        assertTrue(result.isSuccess());
        assertEquals("Account deleted successfully.", result.getMessage());
        verify(userPersistence).removeUser("username1");
    }

    @Test
    public void shouldFailWhenUserDoesNotExist() {
        when(userPersistence.removeUser("ghostuser")).thenReturn(false);

        AuthResult result = authenticator.deleteAccount("ghostuser");

        assertFalse(result.isSuccess());
        assertEquals("Failed to delete account.", result.getMessage());
        verify(userPersistence).removeUser("ghostuser");
    }

    @Test
    public void shouldFailWhenUsernameIsNull() {
        AuthResult result = authenticator.deleteAccount(null);

        assertFalse(result.isSuccess());
        assertEquals("Username is required.", result.getMessage());
        verify(userPersistence, never()).removeUser(anyString());
    }

    @Test
    public void shouldFailWhenUsernameIsEmpty() {
        AuthResult result = authenticator.deleteAccount("");

        assertFalse(result.isSuccess());
        assertEquals("Username is required.", result.getMessage());
        verify(userPersistence, never()).removeUser(anyString());
    }
}