package com.gitgirls.cyclewise.logic.periodlogging;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.Flow;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Symptoms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.END_DATE_BEFORE_START;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.END_DATE_FUTURE;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.FLOW_REQUIRED;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.INVALID_ID;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.LOG_REQUIRED;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.NOTES_TOO_LONG;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.START_DATE_FUTURE;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.START_DATE_REQUIRED;
import static com.gitgirls.cyclewise.logic.periodlogging.PeriodLogMessages.SYMPTOMS_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;

class PeriodLogValidatorTest {

    private LocalDate today;
    private CycleDate validStartDate;
    private CycleDate validEndDate;
    private Flow validFlow;
    private Symptoms validSymptoms;
    private String validNotes;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        validStartDate = new CycleDate(today.minusDays(5));
        validEndDate = new CycleDate(today.minusDays(1));
        validFlow = Flow.LIGHT;
        validSymptoms = new Symptoms(
                false, false, false, false, false, false,
                false, false, false, false, false, false
        );
        validNotes = "Test notes";
    }

    @Nested
    @DisplayName("Create and Update Validation Tests")
    class CreateAndUpdateValidationTests {

        @Test
        @DisplayName("Should validate a valid create log")
        void shouldValidateValidCreateLog() {
            PeriodLog validLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, validStartDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForCreate(validLog));
        }

        @Test
        @DisplayName("Should validate a valid update log")
        void shouldValidateValidUpdateLog() {
            PeriodLog validLog = new PeriodLog(
                    1, validStartDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(validLog));
        }

        @Test
        @DisplayName("Should throw exception when create log is null")
        void shouldThrowWhenCreateLogNull() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateForCreate(null)
            );
            assertEquals(LOG_REQUIRED, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when update log has invalid ID")
        void shouldThrowWhenUpdateLogIdInvalid() {
            PeriodLog invalidIdLog = new PeriodLog(
                    -1, validStartDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateForUpdate(invalidIdLog)
            );
            assertEquals(INVALID_ID, exception.getMessage());
        }

        @Test
        @DisplayName("Should propagate date validation exceptions")
        void shouldPropagateDateValidation() {
            CycleDate futureDate = new CycleDate(today.plusDays(5));
            PeriodLog futureStartLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, futureDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateForCreate(futureStartLog)
            );
            assertEquals(START_DATE_FUTURE, exception.getMessage());
        }

        @Test
        @DisplayName("Should propagate details validation exceptions")
        void shouldPropagateDetailsValidation() {
            PeriodLog nullFlowLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, validStartDate, validEndDate, null, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateForCreate(nullFlowLog)
            );
            assertEquals(FLOW_REQUIRED, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Validate ID Tests")
    class ValidateIDTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10, 100, 9999, Integer.MAX_VALUE})
        @DisplayName("Should accept valid IDs (>= 0)")
        void shouldAcceptValidIds(int validId) {
            assertDoesNotThrow(() -> PeriodLogValidator.validateID(validId));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -5, -10, -100, Integer.MIN_VALUE})
        @DisplayName("Should throw exception for negative IDs")
        void shouldThrowForNegativeIds(int negativeId) {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateID(negativeId)
            );
            assertEquals(INVALID_ID, exception.getMessage());
        }

        @Test
        @DisplayName("Should accept UNASSIGNED_ID (0)")
        void shouldAcceptUnassignedId() {
            assertDoesNotThrow(() -> PeriodLogValidator.validateID(PeriodLog.UNASSIGNED_ID));
        }
    }

    @Nested
    @DisplayName("Validate Dates Tests")
    class ValidateDatesTests {

        @Test
        @DisplayName("Should validate with both start and end dates")
        void shouldValidateWithBothDates() {
            PeriodLog log = new PeriodLog(
                    1, validStartDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }

        @Test
        @DisplayName("Should validate with null end date")
        void shouldValidateWithNullEndDate() {
            PeriodLog log = new PeriodLog(
                    1, validStartDate, null, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }

        @Test
        @DisplayName("Should throw exception when start date is null")
        void shouldThrowWhenStartDateNull() {
            PeriodLog log = new PeriodLog(
                    1, null, validEndDate, validFlow, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDates(log)
            );
            assertEquals(START_DATE_REQUIRED, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when start date is in the future")
        void shouldThrowWhenStartDateInFuture() {
            CycleDate futureDate = new CycleDate(today.plusDays(1));
            PeriodLog log = new PeriodLog(
                    1, futureDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDates(log)
            );
            assertEquals(START_DATE_FUTURE, exception.getMessage());
        }

        @Test
        @DisplayName("Should accept today as start date")
        void shouldAcceptTodayAsStartDate() {
            CycleDate todayDate = new CycleDate(today);
            PeriodLog log = new PeriodLog(
                    1, todayDate, null, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }

        @Test
        @DisplayName("Should accept start date today and end date today")
        void shouldAcceptStartDateTodayAndEndDateToday() {
            CycleDate todayDate = new CycleDate(today);
            PeriodLog log = new PeriodLog(
                    1, todayDate, todayDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }

        @Test
        @DisplayName("Should accept past dates as start date")
        void shouldAcceptPastDates() {
            CycleDate pastDate = new CycleDate(today.minusYears(1));
            PeriodLog log = new PeriodLog(
                    1, pastDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }

        @Test
        @DisplayName("Should throw exception when end date is before start date")
        void shouldThrowWhenEndDateBeforeStartDate() {
            CycleDate start = new CycleDate(today.minusDays(5));
            CycleDate end = new CycleDate(today.minusDays(10));
            PeriodLog log = new PeriodLog(
                    1, start, end, validFlow, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDates(log)
            );
            assertEquals(END_DATE_BEFORE_START, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when end date is in the future")
        void shouldThrowWhenEndDateInFuture() {
            CycleDate futureEndDate = new CycleDate(today.plusDays(1));
            PeriodLog log = new PeriodLog(
                    1, validStartDate, futureEndDate, validFlow, validSymptoms, validNotes
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDates(log)
            );
            assertEquals(END_DATE_FUTURE, exception.getMessage());
        }

        @Test
        @DisplayName("Should accept end date equal to start date")
        void shouldAcceptEndDateEqualToStartDate() {
            CycleDate sameDate = new CycleDate(today.minusDays(5));
            PeriodLog log = new PeriodLog(
                    1, sameDate, sameDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }

        @Test
        @DisplayName("Should accept very old start dates")
        void shouldAcceptVeryOldStartDates() {
            CycleDate oldDate = new CycleDate(LocalDate.of(1900, 1, 1));
            PeriodLog log = new PeriodLog(
                    1, oldDate, null, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateDates(log));
        }
    }

    @Nested
    @DisplayName("Validate Details Tests")
    class ValidateDetailsTests {

        @Test
        @DisplayName("Should validate with all valid parameters")
        void shouldValidateWithValidParameters() {
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, validNotes)
            );
        }

        @Test
        @DisplayName("Should validate with null notes")
        void shouldValidateWithNullNotes() {
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, null)
            );
        }

        @Test
        @DisplayName("Should validate with empty notes")
        void shouldValidateWithEmptyNotes() {
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, "")
            );
        }

        @Test
        @DisplayName("Should validate with whitespace notes")
        void shouldValidateWithWhitespaceNotes() {
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, "   ")
            );
        }

        @Test
        @DisplayName("Should throw exception when flow is null")
        void shouldThrowWhenFlowNull() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDetails(null, validSymptoms, validNotes)
            );
            assertEquals(FLOW_REQUIRED, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when symptoms is null")
        void shouldThrowWhenSymptomsNull() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDetails(validFlow, null, validNotes)
            );
            assertEquals(SYMPTOMS_REQUIRED, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when both flow and symptoms are null")
        void shouldThrowWhenBothFlowAndSymptomsNull() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDetails(null, null, validNotes)
            );
            assertEquals(FLOW_REQUIRED, exception.getMessage());
        }

        @Test
        @DisplayName("Should accept notes up to 500 characters")
        void shouldAcceptNotesUpTo500Chars() {
            String notes500 = "a".repeat(500);
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, notes500)
            );
        }

        @Test
        @DisplayName("Should throw exception when notes exceed 500 characters")
        void shouldThrowWhenNotesExceed500Chars() {
            String notes501 = "a".repeat(501);

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateDetails(validFlow, validSymptoms, notes501)
            );
            assertEquals(NOTES_TOO_LONG, exception.getMessage());
        }

        @ParameterizedTest
        @MethodSource("provideValidFlowValues")
        @DisplayName("Should accept all Flow enum values")
        void shouldAcceptAllFlowValues(Flow flow) {
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(flow, validSymptoms, validNotes)
            );
        }

        private static Stream<Arguments> provideValidFlowValues() {
            return Stream.of(
                    Arguments.of(Flow.LIGHT),
                    Arguments.of(Flow.MEDIUM),
                    Arguments.of(Flow.HEAVY)
            );
        }

        @Test
        @DisplayName("Should accept any Symptoms object regardless of symptom values")
        void shouldAcceptAnySymptoms() {
            Symptoms allTrueSymptoms = new Symptoms(
                    true, true, true, true, true, true,
                    true, true, true, true, true, true
            );

            Symptoms allFalseSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );

            Symptoms mixedSymptoms = new Symptoms(
                    true, false, true, false, true, false,
                    true, false, true, false, true, false
            );

            assertAll(
                    () -> assertDoesNotThrow(() ->
                            PeriodLogValidator.validateDetails(validFlow, allTrueSymptoms, validNotes)
                    ),
                    () -> assertDoesNotThrow(() ->
                            PeriodLogValidator.validateDetails(validFlow, allFalseSymptoms, validNotes)
                    ),
                    () -> assertDoesNotThrow(() ->
                            PeriodLogValidator.validateDetails(validFlow, mixedSymptoms, validNotes)
                    )
            );
        }

        @Test
        @DisplayName("Should handle notes with special characters")
        void shouldHandleNotesWithSpecialCharacters() {
            String specialNotes = "!@#$%^&*()_+{}|:\"<>?~`-=[]\\;',./";
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, specialNotes)
            );
        }

        @Test
        @DisplayName("Should handle notes with Unicode characters")
        void shouldHandleNotesWithUnicodeCharacters() {
            String unicodeNotes = "Hello 世界 🌍 ¡Hola! öäüß";
            assertDoesNotThrow(() ->
                    PeriodLogValidator.validateDetails(validFlow, validSymptoms, unicodeNotes)
            );
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle maximum valid ID")
        void shouldHandleMaxId() {
            PeriodLog log = new PeriodLog(
                    Integer.MAX_VALUE, validStartDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(log));
        }

        @Test
        @DisplayName("Should handle ID 0 (UNASSIGNED_ID)")
        void shouldHandleUnassignedId() {
            PeriodLog log = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, validStartDate, validEndDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForCreate(log));
        }

        @Test
        @DisplayName("Should handle very old dates")
        void shouldHandleVeryOldDates() {
            CycleDate oldDate = new CycleDate(LocalDate.of(1900, 1, 1));
            PeriodLog log = new PeriodLog(
                    1, oldDate, null, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(log));
        }

        @Test
        @DisplayName("Should handle zero-length period (same start and end date)")
        void shouldHandleZeroLengthPeriod() {
            CycleDate sameDate = new CycleDate(today.minusDays(1));
            PeriodLog log = new PeriodLog(
                    1, sameDate, sameDate, validFlow, validSymptoms, validNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(log));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should validate complete update log with all fields present")
        void shouldValidateCompleteLog() {
            Symptoms symptoms = new Symptoms(
                    true,
                    true,
                    true,
                    true,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
            );

            PeriodLog completeLog = new PeriodLog(
                    42,
                    new CycleDate(today.minusDays(3)),
                    new CycleDate(today),
                    Flow.HEAVY,
                    symptoms,
                    "Severe cramps and headache today."
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(completeLog));
        }

        @Test
        @DisplayName("Should validate minimal create log (only required fields)")
        void shouldValidateMinimalLog() {
            Symptoms noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );

            PeriodLog minimalLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID,
                    new CycleDate(today.minusDays(1)),
                    null,
                    Flow.LIGHT,
                    noSymptoms,
                    null
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForCreate(minimalLog));
        }

        @Test
        @DisplayName("Should reject log with multiple validation errors")
        void shouldRejectLogWithMultipleErrors() {
            Symptoms symptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );

            PeriodLog invalidLog = new PeriodLog(
                    -1,
                    new CycleDate(today.plusDays(1)),
                    new CycleDate(today.minusDays(5)),
                    null,
                    symptoms,
                    "a".repeat(501)
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> PeriodLogValidator.validateForUpdate(invalidLog)
            );

            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should validate log with all symptoms true")
        void shouldValidateLogWithAllSymptoms() {
            Symptoms allSymptoms = new Symptoms(
                    true, true, true, true, true, true,
                    true, true, true, true, true, true
            );

            PeriodLog log = new PeriodLog(
                    1,
                    new CycleDate(today.minusDays(2)),
                    new CycleDate(today),
                    Flow.MEDIUM,
                    allSymptoms,
                    "Experiencing all symptoms"
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(log));
        }

        @Test
        @DisplayName("Should validate log with notes at maximum length")
        void shouldValidateLogWithMaxLengthNotes() {
            String maxNotes = "a".repeat(500);

            PeriodLog log = new PeriodLog(
                    1,
                    validStartDate,
                    validEndDate,
                    validFlow,
                    validSymptoms,
                    maxNotes
            );

            assertDoesNotThrow(() -> PeriodLogValidator.validateForUpdate(log));
        }
    }

    @Nested
    @DisplayName("Flow Enum Tests")
    class FlowEnumTests {

        @Test
        @DisplayName("Should have all expected Flow values")
        void shouldHaveAllExpectedFlowValues() {
            Flow[] flows = Flow.values();
            assertEquals(3, flows.length);
            assertTrue(containsFlow(flows, Flow.LIGHT));
            assertTrue(containsFlow(flows, Flow.MEDIUM));
            assertTrue(containsFlow(flows, Flow.HEAVY));
        }

        @Test
        @DisplayName("Should preserve enum names")
        void shouldPreserveEnumNames() {
            assertEquals("LIGHT", Flow.LIGHT.name());
            assertEquals("MEDIUM", Flow.MEDIUM.name());
            assertEquals("HEAVY", Flow.HEAVY.name());
        }

        private boolean containsFlow(Flow[] flows, Flow flow) {
            for (Flow f : flows) {
                if (f == flow) {
                    return true;
                }
            }
            return false;
        }
    }
}