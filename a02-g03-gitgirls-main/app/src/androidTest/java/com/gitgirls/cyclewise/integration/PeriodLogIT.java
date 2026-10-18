package com.gitgirls.cyclewise.integration;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogManager;
import com.gitgirls.cyclewise.logic.userlogin.AuthResult;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.*;
import com.gitgirls.cyclewise.persistence.UserPersistence;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;
import com.gitgirls.cyclewise.persistence.real.periodlogrepository.PeriodLogPersistenceReal;
import com.gitgirls.cyclewise.persistence.real.userrepository.UserPersistenceReal;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PeriodLogIT {

    private DatabaseHelper dbHelper;
    private UserPersistence userPersistence;
    private AuthService authenticator;

    private CycleDate today;
    private CycleDate yesterday;
    private CycleDate twoDaysAgo;
    private CycleDate threeDaysAgo;
    private CycleDate fourDaysAgo;
    private CycleDate tomorrow;

    private static final String USER1          = "validuser1";
    private static final String USER2          = "validuser2";
    private static final String VALID_PASSWORD = "Password1";
    private static final String VALID_DOB = "01/01/2001";
    private static final Symptoms NO_SYMPTOMS  = new Symptoms(
            false, false, false, false, false, false,
            false, false, false, false, false, false
    );
    private static final Symptoms ALL_SYMPTOMS = new Symptoms(
            true, true, true, true, true, true,
            true, true, true, true, true, true
    );

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        // Clean DB so each test is deterministic
        context.deleteDatabase("test.db");
        dbHelper        = new DatabaseHelper(context, "test.db");
        userPersistence = new UserPersistenceReal(dbHelper);
        authenticator   = new AuthService(userPersistence);

        today        = new CycleDate(LocalDate.now());
        yesterday    = new CycleDate(LocalDate.now().minusDays(1));
        twoDaysAgo   = new CycleDate(LocalDate.now().minusDays(2));
        threeDaysAgo = new CycleDate(LocalDate.now().minusDays(3));
        fourDaysAgo  = new CycleDate(LocalDate.now().minusDays(4));
        tomorrow     = new CycleDate(LocalDate.now().plusDays(1));
    }

    @After
    public void teardown() {
        dbHelper.close();
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private PeriodLogManager managerFor(String username) {
        User user = userPersistence.getByUsername(username);

        if (user == null) {
            throw new InvalidPeriodLogException("User does not exist.");
        }

//        // Changed this for PeriodLogManager
//        return new PeriodLogManager(new PeriodLogPersistenceReal(dbHelper, user), username);

        // Changed this for PeriodLogManager
        return new PeriodLogManager(new PeriodLogPersistenceReal(dbHelper), username);
    }

    private void registerAndLogin(String username) {
        AuthResult registerResult = authenticator.register(
                username, VALID_PASSWORD, "first", "last", VALID_DOB
        );
        assertTrue("Registration should succeed for " + username, registerResult.isSuccess());

        AuthResult loginResult = authenticator.login(username, VALID_PASSWORD);
        assertTrue("Login should succeed for " + username, loginResult.isSuccess());
    }

    private PeriodLog buildLog(int id, CycleDate start, CycleDate end, Flow flow, Symptoms symptoms) {
        return new PeriodLog(id, start, end, flow, symptoms, "notes");
    }

    private PeriodLog buildValidLog() {
        return buildLog(PeriodLog.UNASSIGNED_ID, yesterday, today, Flow.MEDIUM, NO_SYMPTOMS);
    }


    // =======================================================
    // SEAM: Logic → User Persistence
    // AuthService → UserPersistenceReal → SQLite
    // =======================================================

    @Test
    public void shouldAllowUserToLogPeriodAfterRegistration() throws InvalidPeriodLogException, PeriodLogException {
        authenticator.register(USER1, VALID_PASSWORD, "first", "last", VALID_DOB);
        managerFor(USER1).add(buildValidLog());
    }

    @Test(expected = InvalidPeriodLogException.class)
    public void shouldPreventUnregisteredUserFromLoggingPeriod() throws InvalidPeriodLogException, PeriodLogException {
        managerFor("ghost").add(buildValidLog());
    }

    @Test
    public void shouldAllowUserToLogPeriodAfterLogin() throws InvalidPeriodLogException, PeriodLogException {
        authenticator.register(USER1, VALID_PASSWORD, "first", "last", VALID_DOB);
        AuthResult loginResult = authenticator.login(USER1, VALID_PASSWORD);

        assertTrue(loginResult.isSuccess());
        managerFor(USER1).add(buildValidLog());
    }

    @Test
    public void shouldFailLoginWithWrongPassword() {
        authenticator.register(USER1, VALID_PASSWORD, "first", "last", VALID_DOB);
        AuthResult result = authenticator.login(USER1, "WrongPassword1");
        assertFalse(result.isSuccess());
    }

    @Test
    public void shouldFailLoginForNonExistentUser() {
        AuthResult result = authenticator.login("ghostuser", VALID_PASSWORD);
        assertFalse(result.isSuccess());
    }

    @Test
    public void shouldSuccessfullyDeleteAccount() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).add(buildValidLog());

        AuthResult deleteResult = authenticator.deleteAccount(USER1);
        assertTrue(deleteResult.isSuccess());
    }

    @Test
    public void shouldHaveNoLogsAfterAccountIsDeleted() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        authenticator.deleteAccount(USER1);
        assertTrue(manager.getAll().isEmpty());
    }


    // =======================================================
    // SEAM: Logic → PeriodLog Persistence
    // PeriodLogManager → PeriodLogPersistenceReal → SQLite
    // =======================================================

    // --- Add ---

    @Test
    public void shouldPersistPeriodLogToDatabase() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());
        assertFalse(manager.getAll().isEmpty());
    }

    @Test
    public void shouldAssignIdToLogAfterPersistence() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        PeriodLog saved = manager.getAll().get(0);
        assertNotEquals(PeriodLog.UNASSIGNED_ID, saved.getID());
    }

    // --- Read ---

    @Test
    public void shouldReadBackCorrectStartDate() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        PeriodLog saved = manager.getAll().get(0);
        assertEquals(yesterday.getDate(), saved.getStartDate().getDate());
    }

    @Test
    public void shouldReadBackCorrectEndDate() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        PeriodLog saved = manager.getAll().get(0);
        assertEquals(today.getDate(), saved.getEndDate().getDate());
    }

    @Test
    public void shouldReadBackCorrectFlowLevel() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday, today, Flow.HEAVY, NO_SYMPTOMS));

        assertEquals(Flow.HEAVY, manager.getAll().get(0).getFlow());
    }

    @Test
    public void shouldReadBackAllFlowTypes() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo,  threeDaysAgo, Flow.LIGHT,  NO_SYMPTOMS));
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, twoDaysAgo,   yesterday,    Flow.MEDIUM, NO_SYMPTOMS));
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday,    today,        Flow.HEAVY,  NO_SYMPTOMS));

        List<PeriodLog> logs = manager.getAll();
        assertEquals(Flow.LIGHT,  logs.get(0).getFlow());
        assertEquals(Flow.MEDIUM, logs.get(1).getFlow());
        assertEquals(Flow.HEAVY,  logs.get(2).getFlow());
    }

    @Test
    public void shouldReadBackAllSymptomsWhenAllActive() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday, today, Flow.MEDIUM, ALL_SYMPTOMS));

        PeriodLog saved = manager.getAll().get(0);
        assertTrue(saved.getSymptoms().hasCramps());
        assertTrue(saved.getSymptoms().hasHeadache());
        assertTrue(saved.getSymptoms().hasFatigue());
        assertTrue(saved.getSymptoms().hasBloating());
        assertTrue(saved.getSymptoms().hasNausea());
        assertTrue(saved.getSymptoms().hasDizziness());
    }

    @Test
    public void shouldReadBackNoSymptomsWhenNoneActive() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday, today, Flow.MEDIUM, NO_SYMPTOMS));

        PeriodLog saved = manager.getAll().get(0);
        assertFalse(saved.getSymptoms().hasCramps());
        assertFalse(saved.getSymptoms().hasHeadache());
        assertFalse(saved.getSymptoms().hasFatigue());
    }

    @Test
    public void shouldRetrievePeriodLogById() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        PeriodLog saved = manager.getAll().get(0);
        assertEquals(saved.getID(), manager.getLogById(saved.getID()).getID());
    }

    @Test(expected = PeriodLogException.class)
    public void shouldThrowWhenRetrievingNonExistentLogById() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).getLogById(99999);
    }

    // --- Update ---

    @Test
    public void shouldPersistUpdatedFlowToDatabase() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        PeriodLog saved = manager.getAll().get(0);
        manager.updateLog(buildLog(saved.getID(), yesterday, today, Flow.HEAVY, NO_SYMPTOMS));

        assertEquals(Flow.HEAVY, manager.getLogById(saved.getID()).getFlow());
    }

    @Test
    public void shouldPersistUpdatedSymptomsToDatabase() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday, today, Flow.MEDIUM, NO_SYMPTOMS));

        PeriodLog saved = manager.getAll().get(0);
        manager.updateLog(buildLog(saved.getID(), yesterday, today, Flow.MEDIUM, ALL_SYMPTOMS));

        assertTrue(manager.getLogById(saved.getID()).getSymptoms().hasCramps());
    }

    @Test(expected = PeriodLogException.class)
    public void shouldThrowWhenUpdatingAnotherUsersLog() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager user1Manager = managerFor(USER1);
        user1Manager.add(buildValidLog());
        PeriodLog user1Log = user1Manager.getAll().get(0);

        registerAndLogin(USER2);
        managerFor(USER2).updateLog(buildLog(user1Log.getID(), yesterday, today, Flow.HEAVY, NO_SYMPTOMS));
    }

    // --- Delete ---

    @Test
    public void shouldRemoveLogFromDatabaseAfterDeletion() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        manager.deleteLog(manager.getAll().get(0).getID());
        assertTrue(manager.getAll().isEmpty());
    }

    @Test(expected = PeriodLogException.class)
    public void shouldNotBeAbleToRetrieveLogAfterDeletion() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());

        int savedId = manager.getAll().get(0).getID();
        manager.deleteLog(savedId);
        manager.getLogById(savedId);
    }

    @Test
    public void shouldReduceCountByOneAfterDeletion() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo, threeDaysAgo, Flow.LIGHT, NO_SYMPTOMS));
        manager.add(buildValidLog());

        int countBefore = manager.getAll().size();
        manager.deleteLog(manager.getAll().get(0).getID());

        assertEquals(countBefore - 1, manager.getAll().size());
    }

    @Test(expected = PeriodLogException.class)
    public void shouldThrowWhenDeletingNonExistentLog() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).deleteLog(99999);
    }

    @Test(expected = InvalidPeriodLogException.class)
    public void shouldThrowWhenDeletingWithInvalidId() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).deleteLog(-1);
    }

    @Test(expected = PeriodLogException.class)
    public void shouldThrowWhenDeletingAnotherUsersLog() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager user1Manager = managerFor(USER1);
        user1Manager.add(buildValidLog());
        PeriodLog user1Log = user1Manager.getAll().get(0);

        registerAndLogin(USER2);
        managerFor(USER2).deleteLog(user1Log.getID());
    }

    @Test
    public void shouldDeleteAllLogsOneByOne() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo, threeDaysAgo, Flow.LIGHT, NO_SYMPTOMS));
        manager.add(buildValidLog());

        for (PeriodLog log : manager.getAll()) {
            manager.deleteLog(log.getID());
        }
        assertTrue(manager.getAll().isEmpty());
    }


    // =======================================================
    // SEAM: PeriodLogRepo internal
    // PeriodLogMapper → PeriodLogQueries → SQLite
    // =======================================================

    @Test
    public void shouldCorrectlyMapMultipleLogsFromDatabase() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo,  threeDaysAgo, Flow.LIGHT,  NO_SYMPTOMS));
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, twoDaysAgo,   yesterday,    Flow.MEDIUM, NO_SYMPTOMS));
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday,    today,        Flow.HEAVY,  ALL_SYMPTOMS));

        assertEquals(3, manager.getAll().size());
    }

    @Test
    public void shouldReturnLogsInChronologicalOrder() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        // Added out of order intentionally to verify query sorts correctly
        manager.add(buildValidLog());
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo, threeDaysAgo, Flow.LIGHT, NO_SYMPTOMS));

        List<PeriodLog> logs = manager.getAll();
        assertTrue(logs.get(0).getStartDate().isBefore(logs.get(1).getStartDate()));
    }

    @Test
    public void shouldAssignUniqueIdsToEachPersistedLog() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo, threeDaysAgo, Flow.LIGHT, NO_SYMPTOMS));
        manager.add(buildValidLog());

        List<PeriodLog> logs = manager.getAll();
        assertNotEquals(logs.get(0).getID(), logs.get(1).getID());
    }

    @Test
    public void shouldCorrectlyMapMixedSymptomsAcrossMultipleLogs() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo, threeDaysAgo, Flow.LIGHT, NO_SYMPTOMS));
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, yesterday,   today,        Flow.HEAVY, ALL_SYMPTOMS));

        List<PeriodLog> logs = manager.getAll();
        assertFalse(logs.get(0).getSymptoms().hasCramps());
        assertTrue(logs.get(1).getSymptoms().hasCramps());
    }

    @Test
    public void shouldCorrectlyQueryLogByIdFromDatabase() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildLog(PeriodLog.UNASSIGNED_ID, fourDaysAgo, threeDaysAgo, Flow.LIGHT, NO_SYMPTOMS));
        manager.add(buildValidLog());

        PeriodLog second = manager.getAll().get(1);
        PeriodLog fetched = manager.getLogById(second.getID());

        assertEquals(second.getID(), fetched.getID());
        assertEquals(second.getFlow(), fetched.getFlow());
        assertEquals(second.getStartDate().getDate(), fetched.getStartDate().getDate());
    }


    // =======================================================
    // SEAM: Data isolation between users
    // =======================================================

    @Test
    public void shouldIsolatePeriodLogsBetweenUsers() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).add(buildValidLog());

        registerAndLogin(USER2);
        assertTrue(managerFor(USER2).getAll().isEmpty());
    }

    @Test
    public void shouldOnlyShowUserTheirOwnLogs() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager user1Manager = managerFor(USER1);
        user1Manager.add(buildValidLog());
        int user1Count = user1Manager.getAll().size();

        registerAndLogin(USER2);
        managerFor(USER2).add(buildValidLog());

        assertEquals(user1Count, managerFor(USER1).getAll().size());
    }




    // =======================================================
    // SEAM: Validation (Logic → Exception boundary)
    // =======================================================

    @Test(expected = InvalidPeriodLogException.class)
    public void shouldThrowWhenEndDateIsBeforeStartDate() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).add(buildLog(PeriodLog.UNASSIGNED_ID, today, yesterday, Flow.MEDIUM, NO_SYMPTOMS));
    }

    @Test(expected = InvalidPeriodLogException.class)
    public void shouldThrowWhenEndDateIsInFuture() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        managerFor(USER1).add(buildLog(PeriodLog.UNASSIGNED_ID, today, tomorrow, Flow.MEDIUM, NO_SYMPTOMS));
    }




    // =======================================================
    // SEAM: Persistence across sessions (DB durability)
    // =======================================================

    @Test
    public void shouldPersistLogAfterRelogin() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());
        int countBeforeRelogin = manager.getAll().size();

        authenticator.login(USER1, VALID_PASSWORD);
        assertEquals(countBeforeRelogin, managerFor(USER1).getAll().size());
    }

    @Test
    public void shouldStillRetrieveLogByIdAfterRelogin() throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin(USER1);
        PeriodLogManager manager = managerFor(USER1);
        manager.add(buildValidLog());
        PeriodLog saved = manager.getAll().get(0);

        authenticator.login(USER1, VALID_PASSWORD);
        assertEquals(saved.getID(), managerFor(USER1).getLogById(saved.getID()).getID());
    }
}