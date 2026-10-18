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
public class AccountCreationTest {

    @Mock
    private UserPersistence userPersistence;

    private AuthService authenticator;

    @BeforeEach
    void setup() {
        authenticator = new AuthService(userPersistence);
    }

    @Test
    public void shouldRegisterSuccessfully() {
        when(userPersistence.getByUsername("username1")).thenReturn(null);
        when(userPersistence.addUser("username1", "first", "last", "Password1", "11/11/2001"))
                .thenReturn(true);

        AuthResult result = authenticator.register("username1", "Password1", "first", "last", "11/11/2001");

        assertTrue(result.isSuccess());
        assertEquals("Registered successfully.", result.getMessage());
        verify(userPersistence).addUser("username1", "first", "last", "Password1", "11/11/2001");
    }

    @Test
    public void shouldSaveNewUserToPersistenceAfterCreation() {
        when(userPersistence.getByUsername("username1")).thenReturn(null);
        when(userPersistence.addUser("username1", "first", "last", "Password1", "11/11/2001"))
                .thenReturn(true);

        authenticator.register("username1", "Password1", "first", "last", "11/11/2001");

        verify(userPersistence).addUser("username1", "first", "last", "Password1", "11/11/2001");
    }

    @Test
    public void shouldFailWhenPersistenceFailsToSaveUser() {
        when(userPersistence.getByUsername("username1")).thenReturn(null);
        when(userPersistence.addUser("username1", "first", "last", "Password1", "11/11/2001"))
                .thenReturn(false);

        AuthResult result = authenticator.register("username1", "Password1", "first", "last", "11/11/2001");

        assertFalse(result.isSuccess());
        assertEquals("Registration failed.", result.getMessage());
    }

    @Test
    public void shouldFailWhenUsernameAlreadyExists() {
        when(userPersistence.getByUsername("username1")).thenReturn(
                new com.gitgirls.cyclewise.models.User("username1", "Password1", "first", "last", "11/11/2001")
        );

        AuthResult result = authenticator.register("username1", "Password1", "first", "last", "11/11/2001");

        assertFalse(result.isSuccess());
        assertEquals("Username already exists. Try another name.", result.getMessage());
        verify(userPersistence, never()).addUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void shouldFailWithNullUsername() {
        AuthResult result = authenticator.register(null, "Password1", "first", "last", "11/11/2001");

        assertFalse(result.isSuccess());
        verify(userPersistence, never()).addUser(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userPersistence, never()).getByUsername(anyString());
    }

    @Test
    public void shouldFailWithTooShortUsername() {
        AuthResult result = authenticator.register("usr", "Password1", "first", "last", "11/11/2001");

        assertFalse(result.isSuccess());
        verify(userPersistence, never()).addUser(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userPersistence, never()).getByUsername(anyString());
    }

    @Test
    public void shouldFailWithSevenCharUsername() {
        AuthResult result = authenticator.register("usernam", "Password1", "first", "last", "11/11/2001");

        assertFalse(result.isSuccess());
        verify(userPersistence, never()).addUser(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userPersistence, never()).getByUsername(anyString());
    }

    @Test
    public void shouldSucceedWithExactlyEightCharUsername() {
        when(userPersistence.getByUsername("username")).thenReturn(null);
        when(userPersistence.addUser("username", "first", "last", "Password1", "11/11/2001"))
                .thenReturn(true);

        AuthResult result = authenticator.register("username", "Password1", "first", "last", "11/11/2001");

        assertTrue(result.isSuccess());
        assertEquals("Registered successfully.", result.getMessage());
    }

    @Test
    public void shouldFailWithTooShortPassword() {
        AuthResult result = authenticator.register("username1", "Pass1", "first", "last", "11/11/2001");

        assertFalse(result.isSuccess());
        verify(userPersistence, never()).addUser(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userPersistence, never()).getByUsername(anyString());
    }

    @Test
    public void shouldSucceedWithExactlyEightCharPassword() {
        when(userPersistence.getByUsername("username1")).thenReturn(null);
        when(userPersistence.addUser("username1", "first", "last", "Password1", "11/11/2001"))
                .thenReturn(true);

        AuthResult result = authenticator.register("username1", "Password1", "first", "last", "11/11/2001");

        assertTrue(result.isSuccess());
        assertEquals("Registered successfully.", result.getMessage());
    }

    @Test
    public void shouldReturnUserByUsername() {
        com.gitgirls.cyclewise.models.User mockUser =
                new com.gitgirls.cyclewise.models.User("username1", "Password1", "first", "last", "11/11/2001");
        when(userPersistence.getByUsername("username1")).thenReturn(mockUser);

        com.gitgirls.cyclewise.models.User result = authenticator.getUserByUsername("username1");

        assertEquals(mockUser, result);
        verify(userPersistence).getByUsername("username1");
    }
}