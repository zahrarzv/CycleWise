package com.gitgirls.cyclewise.logic.notifications;

import com.gitgirls.cyclewise.exceptions.InvalidCycleDateException;
import com.gitgirls.cyclewise.exceptions.NotEnabledException;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.NotificationSettings;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private PeriodCalculator periodCalculator;

    @Mock
    private CycleDate mockCycleDate;

    private NotificationSettings settings;
    private NotificationValidator validator;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        settings = new NotificationSettings(2, true, LocalTime.of(9, 0));
        validator = new NotificationValidator();
    }

    // helper to reduce boilerplate
    private NotificationScheduler makeScheduler(NotificationSettings s) {
        return new NotificationScheduler(periodCalculator, s, validator);
    }


    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create NotificationScheduler with valid arguments")
        void shouldCreateWithValidArguments() {
            assertNotNull(new NotificationScheduler(periodCalculator, settings, validator));
        }

        @Test
        @DisplayName("Should throw when PeriodCalculator is null")
        void shouldThrowWhenPeriodCalculatorNull() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationScheduler(null, settings, validator)
            );
            assertEquals(SchedulerConstants.NULL_PERIOD_CALC, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw when NotificationSettings is null")
        void shouldThrowWhenSettingsNull() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationScheduler(periodCalculator, null, validator)
            );
            assertEquals(SchedulerConstants.NULL_NOTI_SETTING, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw when validator is null")
        void shouldThrowWhenValidatorNull() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationScheduler(periodCalculator, settings, null)
            );
        }

        @Test
        @DisplayName("Should throw when all arguments are null")
        void shouldThrowWhenAllNull() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationScheduler(null, null, null)
            );
        }
    }


    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should return notification date daysBeforeReminder days before predicted period")
        void shouldReturnCorrectNotificationDate() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            LocalDate result = makeScheduler(settings).getNotificationDate();

            assertEquals(today.minusDays(2), result);
            verify(periodCalculator).calculateNextCycleStart();
        }

        @Test
        @DisplayName("Should return period date itself when daysBeforeReminder is 0")
        void shouldReturnPeriodDateWhenZeroDaysBefore() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            NotificationSettings zeroDaySettings = new NotificationSettings(0, true, LocalTime.of(9, 0));
            LocalDate result = makeScheduler(zeroDaySettings).getNotificationDate();

            assertEquals(today, result);
        }

        @Test
        @DisplayName("Should return correct date when daysBeforeReminder is 1")
        void shouldReturnCorrectDateForOneDayBefore() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            NotificationSettings oneDaySettings = new NotificationSettings(1, true, LocalTime.of(9, 0));
            LocalDate result = makeScheduler(oneDaySettings).getNotificationDate();

            assertEquals(today.minusDays(1), result);
        }

        @Test
        @DisplayName("Should return correct date when daysBeforeReminder is 7")
        void shouldReturnCorrectDateForSevenDaysBefore() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            NotificationSettings sevenDaySettings = new NotificationSettings(7, true, LocalTime.of(9, 0));
            LocalDate result = makeScheduler(sevenDaySettings).getNotificationDate();

            assertEquals(today.minusDays(7), result);
        }

        @Test
        @DisplayName("Should call calculateNextCycleStart exactly once")
        void shouldCallCalculatorOnce() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            makeScheduler(settings).getNotificationDate();

            verify(periodCalculator, times(1)).calculateNextCycleStart();
        }

        @Test
        @DisplayName("Should handle future predicted date correctly")
        void shouldHandleFuturePredictedDate() {
            LocalDate futureDate = today.plusDays(14);
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(futureDate));

            LocalDate result = makeScheduler(settings).getNotificationDate();

            assertEquals(futureDate.minusDays(2), result);
        }

        @Test
        @DisplayName("Should produce different dates for different daysBeforeReminder values")
        void shouldProduceDifferentDatesForDifferentSettings() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            NotificationSettings twoDays  = new NotificationSettings(2, true, LocalTime.of(9, 0));
            NotificationSettings fiveDays = new NotificationSettings(5, true, LocalTime.of(9, 0));

            LocalDate twoResult  = makeScheduler(twoDays).getNotificationDate();
            LocalDate fiveResult = makeScheduler(fiveDays).getNotificationDate();

            assertNotEquals(twoResult, fiveResult);
            assertEquals(3, java.time.temporal.ChronoUnit.DAYS.between(fiveResult, twoResult));
        }

        @Test
        @DisplayName("Should ignore reminder time when calculating notification date")
        void shouldIgnoreReminderTimeForDateCalculation() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            NotificationSettings morningSettings = new NotificationSettings(2, true, LocalTime.of(8,  0));
            NotificationSettings eveningSettings = new NotificationSettings(2, true, LocalTime.of(20, 0));

            LocalDate morningResult = makeScheduler(morningSettings).getNotificationDate();
            LocalDate eveningResult = makeScheduler(eveningSettings).getNotificationDate();

            assertEquals(morningResult, eveningResult);
        }
    }

    @Nested
    @DisplayName("Unhappy Path Tests")
    class UnhappyPathTests {

        @Test
        @DisplayName("Should throw NotEnabledException when notifications are disabled")
        void shouldThrowWhenDisabled() {
            NotificationSettings disabledSettings = new NotificationSettings(2, false, LocalTime.of(9, 0));
            NotificationScheduler scheduler = makeScheduler(disabledSettings);

            assertThrows(NotEnabledException.class, scheduler::getNotificationDate);
            verifyNoInteractions(periodCalculator);
        }

        @Test
        @DisplayName("Should throw NotEnabledException when notifications toggled off after creation")
        void shouldThrowAfterDisabling() {
            NotificationScheduler scheduler = makeScheduler(settings);
            settings.setEnabled(false);

            assertThrows(NotEnabledException.class, scheduler::getNotificationDate);
            verifyNoInteractions(periodCalculator);
        }

        @Test
        @DisplayName("Should not call calculator when notifications are disabled")
        void shouldNotCallCalculatorWhenDisabled() {
            NotificationSettings disabledSettings = new NotificationSettings(2, false, LocalTime.of(9, 0));

            try {
                makeScheduler(disabledSettings).getNotificationDate();
            } catch (NotEnabledException ignored) {}

            verifyNoInteractions(periodCalculator);
        }

        @Test
        @DisplayName("Should propagate exception when calculator throws")
        void shouldPropagateCalculatorException() {
            when(periodCalculator.calculateNextCycleStart())
                    .thenThrow(new IllegalStateException("No cycles available"));

            assertThrows(IllegalStateException.class, makeScheduler(settings)::getNotificationDate);
        }

        @Test
        @DisplayName("Should return a past date when daysBeforeReminder exceeds cycle length")
        void shouldHandleLargeDaysBeforeReminder() {
            when(periodCalculator.calculateNextCycleStart()).thenReturn(new CycleDate(today));

            NotificationSettings largeDaySettings = new NotificationSettings(100, true, LocalTime.of(9, 0));
            LocalDate result = makeScheduler(largeDaySettings).getNotificationDate();

            assertNotNull(result);
            assertTrue(result.isBefore(today));
        }
    }


    @Nested
    @DisplayName("Validator Tests")
    class ValidatorTests {

        @Mock
        private CycleDate mockDate;

        @Test
        @DisplayName("checkEnable should pass when notifications are enabled")
        void checkEnable_shouldPass_whenEnabled() {
            assertDoesNotThrow(() -> validator.checkEnable(settings));
        }

        @Test
        @DisplayName("checkEnable should throw NotEnabledException when disabled")
        void checkEnable_shouldThrow_whenDisabled() {
            NotificationSettings disabledSettings = new NotificationSettings(2, false, LocalTime.of(9, 0));
            assertThrows(NotEnabledException.class, () -> validator.checkEnable(disabledSettings));
        }

        @Test
        @DisplayName("checkEnable should throw IllegalArgumentException when settings is null")
        void checkEnable_shouldThrow_whenNull() {
            assertThrows(IllegalArgumentException.class, () -> validator.checkEnable(null));
        }

        @Test
        @DisplayName("validateDate should pass when date is valid")
        void validateDate_shouldPass_whenValid() {
            CycleDate validDate = mock(CycleDate.class);
            when(validDate.getDate()).thenReturn(LocalDate.now());
            assertDoesNotThrow(() -> validator.validateDate(validDate));
        }

        @Test
        @DisplayName("validateDate should throw InvalidCycleDateException when date object is null")
        void validateDate_shouldThrow_whenNullObject() {
            assertThrows(InvalidCycleDateException.class, () -> validator.validateDate(null));
        }

        @Test
        @DisplayName("validateDate should throw InvalidCycleDateException when getDate() returns null")
        void validateDate_shouldThrow_whenGetDateReturnsNull() {
            when(mockDate.getDate()).thenReturn(null);
            assertThrows(InvalidCycleDateException.class, () -> validator.validateDate(mockDate));
        }
    }
}