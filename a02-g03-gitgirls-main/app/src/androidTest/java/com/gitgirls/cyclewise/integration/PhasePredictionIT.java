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
import com.gitgirls.cyclewise.logic.phaseprediction.PhaseCalculator;
import com.gitgirls.cyclewise.logic.phaseprediction.PhaseConstants;
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
public class PhasePredictionIT {

    private DatabaseHelper dbHelper;
    private UserPersistence userPersistence;
    private AuthService authenticator;

    private static final String USER1 = "phaseuser";
    private static final String VALID_PASSWORD = "Password1";
    private static final String VALID_DOB = "01/01/2001";

    private static final Symptoms NO_SYMPTOMS = new Symptoms(
            false, false, false, false, false, false,
            false, false, false, false, false, false
    );

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("phase_test.db");

        dbHelper = new DatabaseHelper(context, "phase_test.db");
        userPersistence = new UserPersistenceReal(dbHelper);
        authenticator = new AuthService(userPersistence);
    }

    @After
    public void teardown() {
        dbHelper.close();
    }

    // ===== Helper Methods =====

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
                "test notes"
        );
    }

    private PhaseCalculator buildPhaseCalculator(PeriodLogManager manager)
            throws InvalidPeriodLogException, PeriodLogException {
        List<PeriodLog> logs = manager.getAll();
        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(logs);
        CycleHistory history = new CycleHistory(cycles);
        PeriodCalculator periodCalculator = new PeriodCalculator(history);
        return new PhaseCalculator(history, periodCalculator);
    }

    /**
     * Creates a log that ended a specific number of days ago
     * Period duration: 5 days
     */
    private void addLogEndingDaysAgo(int daysAgo, PeriodLogManager manager)
            throws InvalidPeriodLogException, PeriodLogException {
        LocalDate endDate = LocalDate.now().minusDays(daysAgo);
        LocalDate startDate = endDate.minusDays(4); // 5 day period

        manager.add(buildLog(
                new CycleDate(startDate),
                new CycleDate(endDate)
        ));
    }

    // ===== MENSTRUATION PHASE TESTS =====


    @Test
    public void shouldDetectMenstruationPhaseOnPeriodEndDate()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended today
        addLogEndingDaysAgo(0, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.MENSTRUATION, phase);
    }



    // ===== FOLLICULAR PHASE TESTS =====

    @Test
    public void shouldDetectFollicularPhaseOnFirstDayAfterPeriod()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended yesterday (1 day ago)
        addLogEndingDaysAgo(1, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.FOLLICULAR, phase);
    }

    @Test
    public void shouldDetectFollicularPhaseInMiddleOfPhase()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended 4 days ago (middle of 8-day follicular phase)
        addLogEndingDaysAgo(4, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.FOLLICULAR, phase);
    }

    @Test
    public void shouldDetectFollicularPhaseOnLastDayOfPhase()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended exactly FOLLICULAR_PHASE_DURATION_DAYS ago
        addLogEndingDaysAgo(PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.FOLLICULAR, phase);
    }

    // ===== OVULATION PHASE TESTS =====

    @Test
    public void shouldDetectOvulationPhaseOnFirstDay()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended FOLLICULAR + 1 days ago
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS + 1;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.OVULATION, phase);
    }

    @Test
    public void shouldDetectOvulationPhaseOnLastDay()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended FOLLICULAR + OVULATION days ago
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.OVULATION, phase);
    }

    // ===== LUTEAL PHASE TESTS =====

    @Test
    public void shouldDetectLutealPhaseOnFirstDay()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended FOLLICULAR + OVULATION + 1 days ago
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS + 1;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.LUTEAL, phase);
    }

    @Test
    public void shouldDetectLutealPhaseInMiddle()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended FOLLICULAR + OVULATION + 5 days ago (middle of luteal)
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS + 5;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.LUTEAL, phase);
    }

    @Test
    public void shouldDetectLutealPhaseOnLastDay()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended FOLLICULAR + OVULATION + LUTEAL days ago
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.LUTEAL, phase);
    }

    // ===== PREMENSTRUAL PHASE TESTS =====

    @Test
    public void shouldDetectPremenstrualPhaseOnFirstDay()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended FOLLICULAR + OVULATION + LUTEAL + 1 days ago
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS + 1;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.PREMENSTRUAL, phase);
    }

    @Test
    public void shouldDetectPremenstrualPhaseOnLastDay()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended at the end of all phases
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        assertEquals(Phases.PREMENSTRUAL, phase);
    }

    @Test
    public void shouldDetectPremenstrualWhenPeriodIsLate()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Period ended way past all phases (cycle is late)
        int daysAgo = PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS + 5;
        addLogEndingDaysAgo(daysAgo, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        // When period is late, should still return PREMENSTRUAL
        assertEquals(Phases.PREMENSTRUAL, phase);
    }

    // ===== PHASE TRANSITIONS TESTS =====

    @Test
    public void shouldTransitionThroughAllPhasesCorrectly()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Test each phase boundary
        Phases[] expectedPhases = {
                Phases.MENSTRUATION,  // Day 0 (period end)
                Phases.FOLLICULAR,    // Day 1
                Phases.FOLLICULAR,    // Day 8 (last day of follicular)
                Phases.OVULATION,     // Day 9 (first day of ovulation)
                Phases.OVULATION,     // Day 9 (last day of ovulation)
                Phases.LUTEAL,        // Day 10 (first day of luteal)
                Phases.LUTEAL,        // Day 20 (last day of luteal)
                Phases.PREMENSTRUAL,  // Day 21 (first day of premenstrual)
                Phases.PREMENSTRUAL   // Day 23 (last day of premenstrual)
        };

        int[] daysAfterPeriod = {0, 1, 8, 9, 9, 10, 20, 21, 23};

        for (int i = 0; i < expectedPhases.length; i++) {
            // Clear previous logs
            context().deleteDatabase("phase_test.db");
            dbHelper = new DatabaseHelper(context(), "phase_test.db");
            userPersistence = new UserPersistenceReal(dbHelper);
            authenticator = new AuthService(userPersistence);
            registerAndLogin();
            PeriodLogManager freshManager = manager();

            addLogEndingDaysAgo(daysAfterPeriod[i], freshManager);

            PhaseCalculator calculator = buildPhaseCalculator(freshManager);
            Phases phase = calculator.calculatePhaseToday();

            assertEquals(
                    "Failed at day " + daysAfterPeriod[i] + " after period",
                    expectedPhases[i],
                    phase
            );
        }
    }

    // ===== MULTIPLE CYCLES TESTS =====

    @Test
    public void shouldUseMostRecentCycleForPhaseCalculation()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Add old cycle (60 days ago)
        addLogEndingDaysAgo(60, manager);

        // Add recent cycle (5 days ago - should be in follicular phase)
        addLogEndingDaysAgo(5, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        // 5 days after period = follicular phase
        assertEquals(Phases.FOLLICULAR, phase);
    }

    @Test
    public void shouldHandleIrregularCyclesCorrectly()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Add cycles with varying lengths
        LocalDate now = LocalDate.now();

        // Cycle 1: 35 days ago
        manager.add(buildLog(
                new CycleDate(now.minusDays(39)),
                new CycleDate(now.minusDays(35))
        ));

        // Cycle 2: 60 days ago (short cycle)
        manager.add(buildLog(
                new CycleDate(now.minusDays(64)),
                new CycleDate(now.minusDays(60))
        ));

        // Most recent cycle ended 10 days ago (luteal phase)
        addLogEndingDaysAgo(10, manager);

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        // 10 days after period = luteal phase
        assertEquals(Phases.LUTEAL, phase);
    }

    // ===== EDGE CASES =====


    @Test
    public void shouldHandleShortPeriodsCorrectly()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Very short period (1 day)
        LocalDate endDate = LocalDate.now().minusDays(5);
        LocalDate startDate = endDate; // Same day

        manager.add(buildLog(
                new CycleDate(startDate),
                new CycleDate(endDate)
        ));

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        // 5 days after period = follicular phase
        assertEquals(Phases.FOLLICULAR, phase);
    }

    @Test
    public void shouldHandleLongPeriodsCorrectly()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Long period (10 days)
        LocalDate endDate = LocalDate.now().minusDays(3);
        LocalDate startDate = endDate.minusDays(9);

        manager.add(buildLog(
                new CycleDate(startDate),
                new CycleDate(endDate)
        ));

        PhaseCalculator calculator = buildPhaseCalculator(manager);
        Phases phase = calculator.calculatePhaseToday();

        // 3 days after period = follicular phase
        assertEquals(Phases.FOLLICULAR, phase);
    }

    // ===== INTEGRATION WITH PREDICTION =====

    @Test
    public void shouldIntegrateWithPeriodPredictionCorrectly()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Add two cycles to establish average
        addLogEndingDaysAgo(56, manager); // ~8 weeks ago
        addLogEndingDaysAgo(28, manager); // ~4 weeks ago

        List<PeriodLog> logs = manager.getAll();
        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(logs);
        CycleHistory history = new CycleHistory(cycles);
        PeriodCalculator periodCalculator = new PeriodCalculator(history);

        // Verify average cycle length
        int avgCycleLength = periodCalculator.calculateAvgCycleLength();
        assertEquals(28, avgCycleLength);

        // Verify next cycle prediction
        CycleDate nextCycle = periodCalculator.calculateNextCycleStart();
        LocalDate expectedNext = logs.get(1).getStartDate().getDate().plusDays(28);
        assertEquals(expectedNext, nextCycle.getDate());

        // Verify current phase
        PhaseCalculator phaseCalculator = new PhaseCalculator(history, periodCalculator);
        Phases currentPhase = phaseCalculator.calculatePhaseToday();

        // 28 days after last period = premenstrual/next cycle imminent
        assertEquals(Phases.PREMENSTRUAL, currentPhase);
    }

    @Test
    public void shouldProvideConsistentPhaseAcrossDatabaseReloads()
            throws InvalidPeriodLogException, PeriodLogException {
        registerAndLogin();
        PeriodLogManager manager = manager();

        // Add a period that ended 5 days ago
        addLogEndingDaysAgo(5, manager);

        // Calculate phase
        PhaseCalculator calculator1 = buildPhaseCalculator(manager);
        Phases phase1 = calculator1.calculatePhaseToday();

        // Close and reopen database
        dbHelper.close();
        dbHelper = new DatabaseHelper(context(), "phase_test.db");

        // Recreate manager and calculator
        PeriodLogManager manager2 = manager();
        PhaseCalculator calculator2 = buildPhaseCalculator(manager2);
        Phases phase2 = calculator2.calculatePhaseToday();

        // Phases should be identical
        assertEquals(phase1, phase2);
        assertEquals(Phases.FOLLICULAR, phase2);
    }

    // ===== Helper =====

    private Context context() {
        return ApplicationProvider.getApplicationContext();
    }
}