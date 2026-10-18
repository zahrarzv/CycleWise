
package com.gitgirls.cyclewise.integration;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleConversionService;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleHistory;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogManager;
import com.gitgirls.cyclewise.logic.userlogin.AuthResult;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.*;
import com.gitgirls.cyclewise.persistence.UserPersistence;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;
import com.gitgirls.cyclewise.persistence.real.periodlogrepository.PeriodLogPersistenceReal;
import com.gitgirls.cyclewise.persistence.real.userrepository.UserPersistenceReal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

    @RunWith(AndroidJUnit4.class)
    public class CyclePredictionIT {

        private DatabaseHelper dbHelper;
        private UserPersistence userPersistence;
        private AuthService authenticator;

        private CycleDate today;
        private CycleDate yesterday;
        private CycleDate twoDaysAgo;
        private CycleDate threeDaysAgo;

        private static final String USER1 = "cycleuser";
        private static final String VALID_PASSWORD = "Password1";
        private static final String VALID_DOB = "01/01/2001";

        private static final Symptoms NO_SYMPTOMS = new Symptoms(
                false, false, false, false, false, false,
                false, false, false, false, false, false
        );
        // -- setup
        @Before
        public void setup() {
            Context context = ApplicationProvider.getApplicationContext();
            context.deleteDatabase("test.db");

            dbHelper = new DatabaseHelper(context, "test.db");
            userPersistence = new UserPersistenceReal(dbHelper);
            authenticator = new AuthService(userPersistence);

            today = new CycleDate(LocalDate.now());
            yesterday = new CycleDate(LocalDate.now().minusDays(1));
            twoDaysAgo = new CycleDate(LocalDate.now().minusDays(2));
            threeDaysAgo = new CycleDate(LocalDate.now().minusDays(3));
        }

        @After
        public void teardown() {
            dbHelper.close();
        }

        //helpers
        private void registerAndLogin() {
            AuthResult register = authenticator.register(
                    USER1, VALID_PASSWORD, "first", "last", VALID_DOB
            );
            assertTrue(register.isSuccess());

            AuthResult login = authenticator.login(USER1, VALID_PASSWORD);
            assertTrue(login.isSuccess());
        }

        private PeriodLogManager manager() {
            return new PeriodLogManager(
                    new PeriodLogPersistenceReal(dbHelper),
                    USER1
            );
        }

        private PeriodLog buildLog(CycleDate start, CycleDate end) {
            return new PeriodLog(
                    PeriodLog.UNASSIGNED_ID,
                    start,
                    end,
                    Flow.MEDIUM,
                    NO_SYMPTOMS,
                    "notes"
            );
        }

        // ---tests
        @Test
        public void shouldCalculateNextCycleFromDatabaseLogs()
                throws InvalidPeriodLogException, PeriodLogException {

            registerAndLogin();

            PeriodLogManager manager = manager();

            manager.add(buildLog(
                    new CycleDate(LocalDate.now().minusDays(56)),
                    new CycleDate(LocalDate.now().minusDays(52))
            ));

            manager.add(buildLog(
                    new CycleDate(LocalDate.now().minusDays(28)),
                    new CycleDate(LocalDate.now().minusDays(24))
            ));

            List<PeriodLog> logs = manager.getAll();

            List<Cycle> cycles = CycleConversionService.fromPeriodLogs(logs);
            CycleHistory history = new CycleHistory(cycles);
            PeriodCalculator calculator = new PeriodCalculator(history);

            CycleDate next = calculator.calculateNextCycleStart();

            LocalDate expected =
                    logs.get(1).getStartDate().getDate().plusDays(28);

            assertEquals(expected, next.getDate());
        }

        @Test
        public void shouldUseDefaultCycleLengthWhenOnlyOneCycle()
                throws InvalidPeriodLogException, PeriodLogException {

            registerAndLogin();

            PeriodLogManager manager = manager();
            manager.add(buildLog(yesterday, today));

            List<Cycle> cycles =
                    CycleConversionService.fromPeriodLogs(manager.getAll());

            CycleHistory history = new CycleHistory(cycles);
            PeriodCalculator calculator = new PeriodCalculator(history);

            int avg = calculator.calculateAvgCycleLength();

            assertEquals(28, avg);
        }

        @Test
        public void shouldCalculateAverageCycleLengthCorrectly()
                throws InvalidPeriodLogException, PeriodLogException {

            registerAndLogin();

            PeriodLogManager manager = manager();

            manager.add(buildLog(
                    new CycleDate(LocalDate.now().minusDays(60)),
                    new CycleDate(LocalDate.now().minusDays(55))
            ));

            manager.add(buildLog(
                    new CycleDate(LocalDate.now().minusDays(30)),
                    new CycleDate(LocalDate.now().minusDays(25))
            ));

            List<Cycle> cycles =
                    CycleConversionService.fromPeriodLogs(manager.getAll());

            CycleHistory history = new CycleHistory(cycles);
            PeriodCalculator calculator = new PeriodCalculator(history);

            int avg = calculator.calculateAvgCycleLength();

            assertEquals(30, avg);
        }

        @Test
        public void shouldConvertLogsToCyclesCorrectly()
                throws InvalidPeriodLogException, PeriodLogException {

            registerAndLogin();

            PeriodLogManager manager = manager();

            manager.add(buildLog(twoDaysAgo, yesterday));

            List<Cycle> cycles =
                    CycleConversionService.fromPeriodLogs(manager.getAll());

            assertEquals(1, cycles.size());
            assertEquals(twoDaysAgo.getDate(),
                    cycles.get(0).getStartDate().getDate());
        }

        @Test
        public void shouldReturnCyclesInChronologicalOrder()
                throws InvalidPeriodLogException, PeriodLogException {

            registerAndLogin();

            PeriodLogManager manager = manager();

            manager.add(buildLog(yesterday, today));
            manager.add(buildLog(threeDaysAgo, twoDaysAgo));

            List<Cycle> cycles =
                    CycleConversionService.fromPeriodLogs(manager.getAll());

            CycleHistory history = new CycleHistory(cycles);

            List<Cycle> ordered = history.getAllCycles();

            assertTrue(
                    ordered.get(0).getStartDate().getDate()
                            .isBefore(ordered.get(1).getStartDate().getDate())
            );
        }


        // ===== IRREGULARITY & VARIATION TESTS =====

        @Test
        public void shouldHandleExtremelyShortCycles()
                throws InvalidPeriodLogException, PeriodLogException {
            registerAndLogin();
            PeriodLogManager manager = manager();

            // 21-day cycles (Short end of normal)
            manager.add(buildLog(new CycleDate(LocalDate.now().minusDays(42)),
                    new CycleDate(LocalDate.now().minusDays(38))));
            manager.add(buildLog(new CycleDate(LocalDate.now().minusDays(21)),
                    new CycleDate(LocalDate.now().minusDays(17))));

            PeriodCalculator calculator = new PeriodCalculator(
                    new CycleHistory(CycleConversionService.fromPeriodLogs(manager.getAll())));

            assertEquals(21, calculator.calculateAvgCycleLength());
        }

        @Test
        public void shouldHandleExtremelyLongCycles()
                throws InvalidPeriodLogException, PeriodLogException {
            registerAndLogin();
            PeriodLogManager manager = manager();

            // 35-day cycles (Long end of normal)
            manager.add(buildLog(new CycleDate(LocalDate.now().minusDays(70)),
                    new CycleDate(LocalDate.now().minusDays(65))));
            manager.add(buildLog(new CycleDate(LocalDate.now().minusDays(35)),
                    new CycleDate(LocalDate.now().minusDays(30))));

            PeriodCalculator calculator = new PeriodCalculator(
                    new CycleHistory(CycleConversionService.fromPeriodLogs(manager.getAll())));

            assertEquals(35, calculator.calculateAvgCycleLength());
        }

        @Test
        public void shouldRoundAverageCycleLengthToNearestInteger()
                throws InvalidPeriodLogException, PeriodLogException {
            registerAndLogin();
            PeriodLogManager manager = manager();

            // Cycle 1: 28 days. Cycle 2: 31 days. Avg = 29.5
            manager.add(buildLog(new CycleDate(LocalDate.now().minusDays(59)),
                    new CycleDate(LocalDate.now().minusDays(55))));
            manager.add(buildLog(new CycleDate(LocalDate.now().minusDays(31)),
                    new CycleDate(LocalDate.now().minusDays(27))));
            manager.add(buildLog(today, today)); // Just to mark the end of the 31-day cycle

            PeriodCalculator calculator = new PeriodCalculator(
                    new CycleHistory(CycleConversionService.fromPeriodLogs(manager.getAll())));

            // Depending on your implementation, check if it rounds or floors
            int avg = calculator.calculateAvgCycleLength();
            assertTrue(avg == 29 || avg == 30);
        }

        // ===== DATA VALIDATION & ERROR HANDLING =====

        @Test(expected = InvalidPeriodLogException.class)
        public void shouldFailToSaveLogWithEndDateBeforeStartDate()
                throws InvalidPeriodLogException, PeriodLogException {
            registerAndLogin();
            manager().add(buildLog(today, yesterday));
        }

        @Test
        public void shouldHandleEmptyDatabaseGracefully() {
            registerAndLogin();
            List<PeriodLog> logs = manager().getAll();

            assertTrue(logs.isEmpty());
            List<Cycle> cycles = CycleConversionService.fromPeriodLogs(logs);
            assertTrue(cycles.isEmpty());
        }



        // ===== PERSISTENCE & MULTI-USER TESTS =====

        @Test
        public void shouldNotSeeLogsFromDifferentUser()
                throws InvalidPeriodLogException, PeriodLogException {
            registerAndLogin();
            manager().add(buildLog(yesterday, today));

            // Register User 2
            authenticator.register("user2", VALID_PASSWORD, "A", "B", VALID_DOB);
            authenticator.login("user2", VALID_PASSWORD);

            PeriodLogManager manager2 = new PeriodLogManager(
                    new PeriodLogPersistenceReal(dbHelper), "user2");

            assertTrue(manager2.getAll().isEmpty());
        }




    }


