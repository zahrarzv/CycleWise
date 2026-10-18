package com.gitgirls.cyclewise.logic.userlogin;

import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.persistence.UserPersistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginTest {

    @Mock
    private UserPersistence userPersistence;

    private AuthService authenticator;

    @BeforeEach
    void setup() {
        authenticator = new AuthService(userPersistence);
    }

    @Test
    void shouldLoginSuccessfullyWithCorrectCredentials() {
        User mockUser = new User("username1", "Password1", "first", "last", "11/11/2001");
        when(userPersistence.getByUsername("username1")).thenReturn(mockUser);

        AuthResult result = authenticator.login("username1", "Password1");

        assertTrue(result.isSuccess());
        assertEquals("Login successful.", result.getMessage());
        verify(userPersistence).getByUsername("username1");
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        User mockUser = new User("username1", "Password1", "first", "last", "11/11/2001");
        when(userPersistence.getByUsername("username1")).thenReturn(mockUser);

        AuthResult result = authenticator.login("username1", "wrongpassword");

        assertFalse(result.isSuccess());
        assertEquals("Password doesn't match. Try again.", result.getMessage());
        verify(userPersistence).getByUsername("username1");
    }

    @Test
    void shouldFailLoginWithNonExistentUsername() {
        when(userPersistence.getByUsername("ghostuser")).thenReturn(null);

        AuthResult result = authenticator.login("ghostuser", "Password1");

        assertFalse(result.isSuccess());
        assertEquals("Cannot find a user with that username.", result.getMessage());
        verify(userPersistence).getByUsername("ghostuser");
    }

    @Test
    void shouldFailLoginWithNullUsername() {
        AuthResult result = authenticator.login(null, "Password1");

        assertFalse(result.isSuccess());
        verifyNoInteractions(userPersistence);
    }

    @Test
    void shouldFailLoginWithNullPassword() {
        AuthResult result = authenticator.login("username1", null);

        assertFalse(result.isSuccess());
        verifyNoInteractions(userPersistence);
    }

    @Test
    void shouldFailLoginWithEmptyUsername() {
        AuthResult result = authenticator.login("", "Password1");

        assertFalse(result.isSuccess());
        verifyNoInteractions(userPersistence);
    }

    @Test
    void shouldFailLoginWithEmptyPassword() {
        AuthResult result = authenticator.login("username1", "");

        assertFalse(result.isSuccess());
        verifyNoInteractions(userPersistence);
    }

    @Test
    void shouldFailLoginWithWhitespaceAroundUsername() {
        when(userPersistence.getByUsername("  username1  ")).thenReturn(null);

        AuthResult result = authenticator.login("  username1  ", "Password1");

        assertFalse(result.isSuccess());
        assertEquals("Cannot find a user with that username.", result.getMessage());
        verify(userPersistence).getByUsername("  username1  ");
    }

    @Test
    void shouldFailLoginWithWhitespaceOnlyUsername() {
        AuthResult result = authenticator.login("   ", "Password1");

        assertFalse(result.isSuccess());
        verifyNoInteractions(userPersistence);
    }

    @Test
    void shouldFailLoginWithWhitespaceOnlyPassword() {
        AuthResult result = authenticator.login("username1", "   ");

        assertFalse(result.isSuccess());
        verifyNoInteractions(userPersistence);
    }

    @Test
    void shouldLoginAfterAccountCreation() {
        User mockUser = new User("newuser1", "Password1", "Jane", "Doe", "11/11/2001");
        when(userPersistence.getByUsername("newuser1")).thenReturn(mockUser);

        AuthResult result = authenticator.login("newuser1", "Password1");

        assertTrue(result.isSuccess());
        assertEquals("Login successful.", result.getMessage());
        verify(userPersistence).getByUsername("newuser1");
    }

    @Test
    void shouldFailLoginWithShortUsername() {
        when(userPersistence.getByUsername("usr")).thenReturn(null);

        AuthResult result = authenticator.login("usr", "Password1");

        assertFalse(result.isSuccess());
        assertEquals("Cannot find a user with that username.", result.getMessage());
        verify(userPersistence).getByUsername("usr");
    }

    @Test
    void shouldFailLoginWithSevenCharacterUsername() {
        when(userPersistence.getByUsername("usernam")).thenReturn(null);

        AuthResult result = authenticator.login("usernam", "Password1");

        assertFalse(result.isSuccess());
        assertEquals("Cannot find a user with that username.", result.getMessage());
        verify(userPersistence).getByUsername("usernam");
    }

    @Test
    void shouldFailLoginWithShortPassword() {
        User mockUser = new User("username1", "Password1", "first", "last", "11/11/2001");
        when(userPersistence.getByUsername("username1")).thenReturn(mockUser);

        AuthResult result = authenticator.login("username1", "Pass1");

        assertFalse(result.isSuccess());
        assertEquals("Password doesn't match. Try again.", result.getMessage());
        verify(userPersistence).getByUsername("username1");
    }

    @Test
    void shouldFailLoginWithPasswordContainingSpaces() {
        User mockUser = new User("username1", "Password1", "first", "last", "11/11/2001");
        when(userPersistence.getByUsername("username1")).thenReturn(mockUser);

        AuthResult result = authenticator.login("username1", "Pass word1");

        assertFalse(result.isSuccess());
        assertEquals("Password doesn't match. Try again.", result.getMessage());
        verify(userPersistence).getByUsername("username1");
    }

    @Test
    void shouldFailLoginWithExactlySevenCharacterPassword() {
        User mockUser = new User("username1", "Password1", "first", "last", "11/11/2001");
        when(userPersistence.getByUsername("username1")).thenReturn(mockUser);

        AuthResult result = authenticator.login("username1", "Pass123");

        assertFalse(result.isSuccess());
        assertEquals("Password doesn't match. Try again.", result.getMessage());
        verify(userPersistence).getByUsername("username1");
    }
}