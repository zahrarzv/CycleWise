package com.gitgirls.cyclewise.integration;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gitgirls.cyclewise.logic.userlogin.AuthResult;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;
import com.gitgirls.cyclewise.persistence.real.userrepository.UserPersistenceReal;

@RunWith(AndroidJUnit4.class)
public class LoginIntegrationTest {
    private static final String DB_NAME = "auth_integration_test.db";

    private AuthService authService;
    private DatabaseHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(DB_NAME);

        dbHelper = new DatabaseHelper(context, DB_NAME);
        dbHelper.getWritableDatabase();

        UserPersistenceReal persistence = new UserPersistenceReal(dbHelper);
        authService = new AuthService(persistence);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        context.deleteDatabase(DB_NAME);
    }

    @Test
    public void testRegisterAndLoginSuccess() {
        AuthResult regResult = authService.register(
                "test_user_123", "Pass1234", "Test",
                "User", "01/01/2000"
        );

        assertTrue("Registration failed: " + regResult.getMessage(), regResult.isSuccess());

        AuthResult loginResult = authService.login("test_user_123", "Pass1234");

        assertTrue("Login failed: " + loginResult.getMessage(), loginResult.isSuccess());
    }

    @Test
    public void testDataPersistsAcrossInstances() {
        String user = "persistent_user";
        String pass = "Pass1234";

        AuthResult regResult = authService.register(user, pass, "Alice", "Smith", "05/05/1995");
        assertTrue("Registration failed: " + regResult.getMessage(), regResult.isSuccess());

        dbHelper.close();

        DatabaseHelper newDbHelper = new DatabaseHelper(context, DB_NAME);
        UserPersistenceReal newPersistence = new UserPersistenceReal(newDbHelper);
        AuthService newService = new AuthService(newPersistence);

        AuthResult result = newService.login(user, pass);

        assertTrue("Login after reopening DB failed: " + result.getMessage(), result.isSuccess());

        newDbHelper.close();
        dbHelper = null;
    }

    @Test
    public void testAccountDeletionRemovesData() {
        String user = "delete_me";
        String pass = "Pass1234";

        AuthResult regResult = authService.register(user, pass, "Delete", "User", "01/01/1990");
        assertTrue("Registration failed: " + regResult.getMessage(), regResult.isSuccess());

        AuthResult deleteResult = authService.deleteAccount(user);
        assertTrue("Delete failed: " + deleteResult.getMessage(), deleteResult.isSuccess());

        AuthResult loginResult = authService.login(user, pass);

        assertFalse("Deleted user should not log in", loginResult.isSuccess());
        assertEquals("Cannot find a user with that username.", loginResult.getMessage());
    }

    @Test
    public void testLoginWithWrongPasswordFails() {
        String user = "wrong_pass_user";
        String correctPass = "Pass1234";
        String wrongPass = "Incorrect999";

        AuthResult regResult = authService.register(user, correctPass, "First", "Last", "01/01/2000");
        assertTrue("Registration failed: " + regResult.getMessage(), regResult.isSuccess());

        AuthResult result = authService.login(user, wrongPass);

        assertFalse("Login should fail with wrong password", result.isSuccess());
        assertEquals("Password doesn't match. Try again.", result.getMessage());
    }

    @Test
    public void testRegisterDuplicateUsernameFails() {
        String user = "duplicate_me";

        AuthResult firstResult = authService.register(user, "Pass1234", "User", "One", "01/01/2000");
        assertTrue("First registration failed: " + firstResult.getMessage(), firstResult.isSuccess());

        AuthResult result = authService.register(
                user, "DifferentPass56", "User", "Two", "01/01/1999"
        );

        assertFalse("Should not be able to register a duplicate username", result.isSuccess());
        assertEquals("Username already exists. Try another name.", result.getMessage());
    }

    @Test
    public void testLoginNonExistentUserFails() {
        AuthResult result = authService.login("no_one_123", "SomePass1");

        assertFalse("Login should fail for non-existent user", result.isSuccess());
        assertEquals("Cannot find a user with that username.", result.getMessage());
    }
}