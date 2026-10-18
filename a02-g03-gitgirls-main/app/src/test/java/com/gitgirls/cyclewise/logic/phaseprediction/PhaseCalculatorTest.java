package com.gitgirls.cyclewise.logic.phaseprediction;

import com.gitgirls.cyclewise.exceptions.PhaseException;
import com.gitgirls.cyclewise.exceptions.ValidationException;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleHistory;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.Phases;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhaseCalculatorTest {

    private static final LocalDate PERIOD_END_DATE = LocalDate.of(2024, 1, 10);
    private static final int PHASE_EXCEPTION_BUFFER = 60;

    private static final LocalDate NEXT_CYCLE_START = PERIOD_END_DATE.plusDays(
            PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                    + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                    + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                    + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS
                    + PHASE_EXCEPTION_BUFFER);


    private PhaseCalculator buildCalculator() {
        CycleHistory mockCycleHistory = mock(CycleHistory.class);
        PeriodCalculator mockPeriodCalculator = mock(PeriodCalculator.class);
        Cycle mockLastCycle = mock(Cycle.class);

        when(mockCycleHistory.getLastCycle()).thenReturn(mockLastCycle);
        when(mockLastCycle.getEndDate()).thenReturn(new CycleDate(PERIOD_END_DATE));
        when(mockPeriodCalculator.calculateNextCycleStart())
                .thenReturn(new CycleDate(NEXT_CYCLE_START));

        return new PhaseCalculator(mockCycleHistory, mockPeriodCalculator);
    }

    private PhaseCalculator buildCalculatorForPastNextCycle() {
        CycleHistory mockCycleHistory = mock(CycleHistory.class);
        PeriodCalculator mockPeriodCalculator = mock(PeriodCalculator.class);

        when(mockPeriodCalculator.calculateNextCycleStart())
                .thenReturn(new CycleDate(NEXT_CYCLE_START));

        return new PhaseCalculator(mockCycleHistory, mockPeriodCalculator);
    }

    // -------------------------------------------------------------------------
    // Constructor Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Constructor cases")
    class ConstructorCases {

        @Test
        @DisplayName("Invalid: CycleHistory is null throws ValidationException")
        void throwWhenCycleHistoryIsNull() {
            PeriodCalculator mockPeriodCalculator = mock(PeriodCalculator.class);
            assertThrows(ValidationException.class,
                    () -> new PhaseCalculator(null, mockPeriodCalculator));
        }

        @Test
        @DisplayName("Invalid: PeriodCalculator is null throws ValidationException")
        void throwWhenPeriodCalculatorIsNull() {
            CycleHistory mockCycleHistory = mock(CycleHistory.class);
            assertThrows(ValidationException.class,
                    () -> new PhaseCalculator(mockCycleHistory, null));
        }

        @Test
        @DisplayName("Invalid: PeriodCalculator and CycleHistory are null throws ValidationException")
        void throwWhenPeriodCalculatorAndCycleHistoryIsNull() {
            assertThrows(ValidationException.class,
                    () -> new PhaseCalculator(null, null));
        }

        @Test
        @DisplayName("Valid: both args provided constructs without throwing")
        void constructorSuccessWithValidArgs() {
            CycleHistory mockCycleHistory = mock(CycleHistory.class);
            PeriodCalculator mockPeriodCalculator = mock(PeriodCalculator.class);
            assertDoesNotThrow(
                    () -> new PhaseCalculator(mockCycleHistory, mockPeriodCalculator));
        }
    }

    // -------------------------------------------------------------------------
    // MENSTRUATION PHASE TEST
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("MENSTRUATION and predicted-late boundary cases")
    class MenstruationCases {

        @Test
        @DisplayName("Edge: today is exactly the predicted next cycle start date with no ongoing log")
        void returnPremenstrualWhenTodayIsNextCycleStart() {
            PhaseCalculator calculator = buildCalculatorForPastNextCycle();
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(NEXT_CYCLE_START);
                assertEquals(Phases.PREMENSTRUAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: today is several days past the predicted next cycle start with no ongoing log")
        void returnPremenstrualWhenTodayIsPastNextCycleStart() {
            PhaseCalculator calculator = buildCalculatorForPastNextCycle();
            LocalDate date = NEXT_CYCLE_START.plusDays(3);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.PREMENSTRUAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Edge: today is exactly the period end date (daysBetween = 0)")
        void returnMenstruationWhenTodayIsPeriodEndDate() {
            PhaseCalculator calculator = buildCalculator();
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(PERIOD_END_DATE);
                assertEquals(Phases.MENSTRUATION, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Edge: today is before the period end date (daysBetween < 0)")
        void returnMenstruationWhenTodayIsBeforePeriodEnd() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.minusDays(1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.MENSTRUATION, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: today is well before period end")
        void returnMenstruationWhenTodayIsWellBeforePeriodEnd() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.minusDays(10);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.MENSTRUATION, calculator.calculatePhaseToday());
            }
        }
    }

    // -------------------------------------------------------------------------
    // FOLLICULAR
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("FOLLICULAR phase cases")
    class FollicularCases {

        @Test
        @DisplayName("Edge: first day after period ends (daysBetween = 1)")
        void returnFollicularOnFirstDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.FOLLICULAR, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: middle of follicular phase")
        void returnFollicularInMiddleOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS / 2);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.FOLLICULAR, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: three quarters through follicular phase")
        void returnFollicularAtThreeQuartersOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    (PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS * 3) / 4);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.FOLLICULAR, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Edge: last day of follicular phase")
        void returnFollicularOnLastDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.FOLLICULAR, calculator.calculatePhaseToday());
            }
        }
    }

    // -------------------------------------------------------------------------
    // OVULATION
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("OVULATION phase cases")
    class OvulationCases {

        @Test
        @DisplayName("Edge: first day of ovulation phase")
        void returnOvulationOnFirstDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.OVULATION, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: middle of ovulation phase")
        void returnOvulationInMiddlePhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + (PhaseConstants.OVULATION_PHASE_DURATION_DAYS / 2) + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.OVULATION, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: one quarter through ovulation phase")
        void returnOvulationAtOneQuarterOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            +  (PhaseConstants.OVULATION_PHASE_DURATION_DAYS / 4) + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.OVULATION, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Edge: last day of ovulation phase")
        void returnOvulationOnLastDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.OVULATION, calculator.calculatePhaseToday());
            }
        }
    }

    // -------------------------------------------------------------------------
    // LUTEAL
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("LUTEAL phase cases")
    class LutealCases {

        @Test
        @DisplayName("Edge: first day of luteal phase")
        void returnLutealOnFirstDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.LUTEAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: middle of luteal phase")
        void returnLutealInMiddleOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + (PhaseConstants.LUTEAL_PHASE_DURATION_DAYS / 2) + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.LUTEAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: three quarters through luteal phase")
        void returnLutealAtThreeQuartersOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + (PhaseConstants.LUTEAL_PHASE_DURATION_DAYS * 3) / 4);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.LUTEAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Edge: last day of luteal phase")
        void returnLutealOnLastDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.LUTEAL, calculator.calculatePhaseToday());
            }
        }
    }

    // -------------------------------------------------------------------------
    // PREMENSTRUAL
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("PREMENSTRUAL phase cases")
    class PremenstrualCases {

        @Test
        @DisplayName("Edge: first day of premenstrual phase")
        void returnPremenstrualOnFirstDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.PREMENSTRUAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: middle of premenstrual phase")
        void returnPremenstrualInMiddleOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                            + (PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS / 2) + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.PREMENSTRUAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Normal: three quarters through premenstrual phase")
        void returnPremenstrualAtThreeQuartersOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                            + (PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS * 3) / 4);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.PREMENSTRUAL, calculator.calculatePhaseToday());
            }
        }

        @Test
        @DisplayName("Edge: last day of premenstrual phase")
        void returnPremenstrualOnLastDayOfPhase() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                            + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertEquals(Phases.PREMENSTRUAL, calculator.calculatePhaseToday());
            }
        }
    }

    // -------------------------------------------------------------------------
    // PhaseException
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("PhaseException invalid cases")
    class PhaseExceptionCases {

        @Test
        @DisplayName("Invalid: one day beyond all phases throws PhaseException")
        void throwPhaseExceptionOneDayBeyondAllPhases() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                            + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS + 1);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertThrows(PhaseException.class, calculator::calculatePhaseToday);
            }
        }

        @Test
        @DisplayName("Invalid: two days beyond all phases throws PhaseException")
        void throwPhaseExceptionTwoDaysBeyondAllPhases() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                            + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS + 2);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertThrows(PhaseException.class, calculator::calculatePhaseToday);
            }
        }

        @Test
        @DisplayName("Invalid: far beyond all phases but before next cycle start throws PhaseException")
        void throwPhaseExceptionFarBeyondAllPhases() {
            PhaseCalculator calculator = buildCalculator();
            LocalDate date = PERIOD_END_DATE.plusDays(
                    PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                            + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                            + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                            + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS + 30);
            try (var mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                mock.when(LocalDate::now).thenReturn(date);
                assertThrows(PhaseException.class, calculator::calculatePhaseToday);
            }
        }
    }
}