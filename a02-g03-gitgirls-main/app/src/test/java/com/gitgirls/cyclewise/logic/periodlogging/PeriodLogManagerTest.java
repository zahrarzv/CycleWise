package com.gitgirls.cyclewise.logic.periodlogging;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.models.*;
import com.gitgirls.cyclewise.persistence.stub.PeriodLogPersistenceStub;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PeriodLogManagerTest {

    private PeriodLogManager periodLogManager;
    private PeriodLogPersistenceStub persistence;
    private PeriodLog validPeriodLog;
    private CycleDate startDate;
    private CycleDate endDate;
    private Flow flow;
    private Symptoms symptoms;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        startDate = new CycleDate(today.minusDays(30));
        endDate = new CycleDate(today.minusDays(1));
        flow = Flow.LIGHT;

        symptoms = new Symptoms(
                false, false, false, false, false, false,
                false, false, false, false, false, false
        );

        validPeriodLog = new PeriodLog(
                PeriodLog.UNASSIGNED_ID, startDate, endDate, flow, symptoms, "Test notes"
        );

        persistence = new PeriodLogPersistenceStub();
        periodLogManager = new PeriodLogManager(persistence);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create PeriodLogManager with valid persistence")
        void shouldCreateWithValidPersistence() {
            PeriodLogManager manager = new PeriodLogManager(new PeriodLogPersistenceStub());
            assertNotNull(manager);
        }

        @Test
        @DisplayName("Should throw exception when persistence is null")
        void shouldThrowWhenPersistenceNull() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PeriodLogManager(null)
            );
            assertEquals("PeriodLogPersistence cannot be null.", exception.getMessage());
            assertEquals("PeriodLogPersistence cannot be null.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Username Constructor Tests")
    class UsernameConstructorTests {

        private PeriodLogManager usernameManager;
        private PeriodLogPersistenceStub usernamePersistence;

        @BeforeEach
        void setUp() {
            usernamePersistence = new PeriodLogPersistenceStub();
            usernameManager = new PeriodLogManager(usernamePersistence, "username1");
        }

        @Test
        @DisplayName("Should create PeriodLogManager with valid persistence and username")
        void shouldCreateWithValidPersistenceAndUsername() {
            PeriodLogManager manager = new PeriodLogManager(new PeriodLogPersistenceStub(), "username1");
            assertNotNull(manager);
        }

        @Test
        @DisplayName("Should add valid PeriodLog successfully with username constructor")
        void shouldAddValidPeriodLogWithUsername() throws InvalidPeriodLogException, PeriodLogException {
            assertDoesNotThrow(() -> usernameManager.add(validPeriodLog));
            assertEquals(1, usernamePersistence.getAllPeriodLogs().size());
            assertTrue(usernamePersistence.getByID(1).isPresent());
        }

        @Test
        @DisplayName("Should get log by ID with username constructor")
        void shouldGetLogByIdWithUsername() throws InvalidPeriodLogException, PeriodLogException {
            usernameManager.add(validPeriodLog);

            PeriodLog retrieved = usernameManager.getLogById(1);

            assertNotNull(retrieved);
            assertEquals(1, retrieved.getID());
            assertEquals("Test notes", retrieved.getNotes());
        }

        @Test
        @DisplayName("Should return all logs with username constructor")
        void shouldReturnAllLogsWithUsername() throws InvalidPeriodLogException, PeriodLogException {
            usernameManager.add(validPeriodLog);

            List<PeriodLog> logs = usernameManager.getAll();

            assertEquals(1, logs.size());
            assertEquals("Test notes", logs.get(0).getNotes());
        }

        @Test
        @DisplayName("Should update existing log with username constructor")
        void shouldUpdateLogWithUsername() throws InvalidPeriodLogException, PeriodLogException {
            usernameManager.add(validPeriodLog);

            PeriodLog updatedLog = new PeriodLog(
                    1, startDate, endDate, Flow.HEAVY, symptoms, "Updated notes"
            );

            usernameManager.updateLog(updatedLog);

            PeriodLog retrieved = usernamePersistence.getByID(1).orElseThrow();
            assertEquals(Flow.HEAVY, retrieved.getFlow());
            assertEquals("Updated notes", retrieved.getNotes());
        }

        @Test
        @DisplayName("Should delete existing log with username constructor")
        void shouldDeleteLogWithUsername() throws InvalidPeriodLogException, PeriodLogException {
            usernameManager.add(validPeriodLog);

            usernameManager.deleteLog(1);

            assertTrue(usernamePersistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw PeriodLogException when updating non-existent log with username constructor")
        void shouldThrowWhenUpdatingNonExistentLogWithUsername() {
            PeriodLog nonExistentLog = new PeriodLog(
                    999, startDate, endDate, flow, symptoms, "Non-existent"
            );

            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> usernameManager.updateLog(nonExistentLog)
            );

            assertEquals("Failed to update period log. Log not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw PeriodLogException when deleting non-existent log with username constructor")
        void shouldThrowWhenDeletingNonExistentLogWithUsername() {
            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> usernameManager.deleteLog(999)
            );

            assertEquals("Failed to delete period log. Log not found.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Add Method Tests")
    class AddMethodTests {

        @Test
        @DisplayName("Should add valid PeriodLog successfully")
        void shouldAddValidPeriodLog() throws InvalidPeriodLogException, PeriodLogException {
            assertDoesNotThrow(() -> periodLogManager.add(validPeriodLog));
            assertEquals(1, persistence.getAllPeriodLogs().size());
            assertTrue(persistence.getByID(1).isPresent());
            assertEquals("Test notes", persistence.getByID(1).orElseThrow().getNotes());
        }

        @Test
        @DisplayName("Should assign ID automatically for UNASSIGNED_ID")
        void shouldAssignIdAutomatically() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);
            PeriodLog added = persistence.getByID(1).orElseThrow();
            assertEquals(1, added.getID());
        }

        @Test
        @DisplayName("Should add multiple PeriodLogs successfully")
        void shouldAddMultiplePeriodLogs() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);

            PeriodLog secondLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, Flow.MEDIUM, symptoms, "Second log"
            );
            periodLogManager.add(secondLog);

            PeriodLog thirdLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, Flow.HEAVY, symptoms, "Third log"
            );
            periodLogManager.add(thirdLog);

            assertEquals(3, persistence.getAllPeriodLogs().size());
            assertEquals(1, persistence.getByID(1).orElseThrow().getID());
            assertEquals(2, persistence.getByID(2).orElseThrow().getID());
            assertEquals(3, persistence.getByID(3).orElseThrow().getID());
        }

        @Test
        @DisplayName("Should add log with specific ID")
        void shouldAddLogWithSpecificId() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog logWithId = new PeriodLog(
                    42, startDate, endDate, flow, symptoms, "Specific ID"
            );
            assertDoesNotThrow(() -> periodLogManager.add(logWithId));
            assertEquals(42, persistence.getByID(42).orElseThrow().getID());
        }

        @Test
        @DisplayName("Should sanitize notes before adding")
        void shouldSanitizeNotesBeforeAdd() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog logWithUntrimmedNotes = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, flow, symptoms, "  Test notes with spaces  "
            );

            periodLogManager.add(logWithUntrimmedNotes);
            PeriodLog addedLog = persistence.getByID(1).orElseThrow();
            assertEquals("Test notes with spaces", addedLog.getNotes());
        }

        @Test
        @DisplayName("Should convert null notes to empty string")
        void shouldConvertNullNotesToEmpty() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog logWithNullNotes = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, flow, symptoms, null
            );

            periodLogManager.add(logWithNullNotes);
            PeriodLog addedLog = persistence.getByID(1).orElseThrow();
            assertEquals("", addedLog.getNotes());
        }

        @Test
        @DisplayName("Should throw exception when adding null PeriodLog")
        void shouldThrowWhenAddingNull() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(null)
            );
            assertEquals("Period log cannot be null.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with negative ID")
        void shouldThrowWhenAddingNegativeId() {
            PeriodLog invalidLog = new PeriodLog(
                    -1, startDate, endDate, flow, symptoms, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("ID must be >= 0.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with null start date")
        void shouldThrowWhenAddingNullStartDate() {
            PeriodLog invalidLog = new PeriodLog(
                    1, null, endDate, flow, symptoms, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("Start date is required.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with future start date")
        void shouldThrowWhenAddingFutureStartDate() {
            CycleDate futureDate = new CycleDate(today.plusDays(5));
            PeriodLog invalidLog = new PeriodLog(
                    1, futureDate, endDate, flow, symptoms, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("Start date cannot be in the future.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with end date before start date")
        void shouldThrowWhenAddingEndDateBeforeStartDate() {
            CycleDate invalidEndDate = new CycleDate(today.minusDays(50));
            PeriodLog invalidLog = new PeriodLog(
                    1, startDate, invalidEndDate, flow, symptoms, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("End date cannot be before start date.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with future end date")
        void shouldThrowWhenAddingFutureEndDate() {
            CycleDate futureEndDate = new CycleDate(today.plusDays(5));
            PeriodLog invalidLog = new PeriodLog(
                    1, startDate, futureEndDate, flow, symptoms, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("End date cannot be in the future.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with null flow")
        void shouldThrowWhenAddingNullFlow() {
            PeriodLog invalidLog = new PeriodLog(
                    1, startDate, endDate, null, symptoms, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("Flow is required.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with null symptoms")
        void shouldThrowWhenAddingNullSymptoms() {
            PeriodLog invalidLog = new PeriodLog(
                    1, startDate, endDate, flow, null, "Test notes"
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("Symptoms are required.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding PeriodLog with notes > 500 chars")
        void shouldThrowWhenAddingNotesTooLong() {
            String longNotes = "a".repeat(501);
            PeriodLog invalidLog = new PeriodLog(
                    1, startDate, endDate, flow, symptoms, longNotes
            );
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.add(invalidLog)
            );
            assertEquals("Notes cannot exceed 500 characters.", exception.getMessage());
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw PeriodLogException when persistence add fails")
        void shouldThrowWhenPersistenceAddFails() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);

            PeriodLog duplicateLog = new PeriodLog(
                    1, startDate, endDate, flow, symptoms, "Duplicate"
            );

            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> periodLogManager.add(duplicateLog)
            );
            assertEquals("Failed to add period log.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Log By ID Tests")
    class GetLogByIdTests {

        @BeforeEach
        void setUp() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);
        }

        @Test
        @DisplayName("Should get log by existing ID")
        void shouldGetLogByExistingId() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog retrieved = periodLogManager.getLogById(1);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.getID());
            assertEquals(startDate, retrieved.getStartDate());
            assertEquals(endDate, retrieved.getEndDate());
            assertEquals(flow, retrieved.getFlow());
        }

        @Test
        @DisplayName("Should throw PeriodLogException when ID not found")
        void shouldThrowPeriodLogExceptionWhenIdNotFound() {
            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> periodLogManager.getLogById(999)
            );
            assertEquals("Period log not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw InvalidPeriodLogException for negative ID")
        void shouldThrowInvalidPeriodLogExceptionForNegativeId() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.getLogById(-5)
            );
            assertEquals("ID must be >= 0.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get All Logs Tests")
    class GetAllLogsTests {

        @Test
        @DisplayName("Should return empty list when no logs exist")
        void shouldReturnEmptyListWhenNoLogs() {
            List<PeriodLog> logs = periodLogManager.getAll();
            assertNotNull(logs);
            assertTrue(logs.isEmpty());
        }

        @Test
        @DisplayName("Should return all added logs")
        void shouldReturnAllLogs() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);

            PeriodLog secondLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, Flow.MEDIUM, symptoms, "Second"
            );
            periodLogManager.add(secondLog);

            PeriodLog thirdLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, Flow.HEAVY, symptoms, "Third"
            );
            periodLogManager.add(thirdLog);

            List<PeriodLog> logs = periodLogManager.getAll();
            assertEquals(3, logs.size());
        }

        @Test
        @DisplayName("Should return logs in correct order")
        void shouldReturnLogsInCorrectOrder() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog log1 = new PeriodLog(1, startDate, endDate, Flow.LIGHT, symptoms, "First");
            PeriodLog log2 = new PeriodLog(2, startDate, endDate, Flow.MEDIUM, symptoms, "Second");

            periodLogManager.add(log1);
            periodLogManager.add(log2);

            List<PeriodLog> logs = periodLogManager.getAll();
            assertEquals(2, logs.size());
            assertEquals("First", logs.get(0).getNotes());
            assertEquals("Second", logs.get(1).getNotes());
        }
    }

    @Nested
    @DisplayName("Update Log Tests")
    class UpdateLogTests {

        @BeforeEach
        void setUp() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);
        }

        @Test
        @DisplayName("Should update existing log")
        void shouldUpdateExistingLog() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog updatedLog = new PeriodLog(
                    1, startDate, endDate, Flow.HEAVY, symptoms, "Updated notes"
            );

            assertDoesNotThrow(() -> periodLogManager.updateLog(updatedLog));

            PeriodLog retrieved = persistence.getByID(1).orElseThrow();
            assertEquals(Flow.HEAVY, retrieved.getFlow());
            assertEquals("Updated notes", retrieved.getNotes());
        }

        @Test
        @DisplayName("Should sanitize notes during update")
        void shouldSanitizeNotesDuringUpdate() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog updatedLog = new PeriodLog(
                    1, startDate, endDate, flow, symptoms, "  Updated with spaces  "
            );

            periodLogManager.updateLog(updatedLog);

            PeriodLog retrieved = persistence.getByID(1).orElseThrow();
            assertEquals("Updated with spaces", retrieved.getNotes());
        }

        @Test
        @DisplayName("Should throw PeriodLogException when updating non-existent log")
        void shouldThrowPeriodLogExceptionWhenUpdatingNonExistentLog() {
            PeriodLog nonExistentLog = new PeriodLog(
                    999, startDate, endDate, flow, symptoms, "Non-existent"
            );

            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> periodLogManager.updateLog(nonExistentLog)
            );
            assertEquals("Failed to update period log. Log not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw InvalidPeriodLogException for invalid log during update")
        void shouldThrowInvalidPeriodLogExceptionForInvalidLogDuringUpdate() {
            PeriodLog invalidLog = new PeriodLog(
                    1, null, endDate, flow, symptoms, "Invalid"
            );

            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.updateLog(invalidLog)
            );
            assertEquals("Start date is required.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Log Tests")
    class DeleteLogTests {

        @BeforeEach
        void setUp() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);
        }

        @Test
        @DisplayName("Should delete existing log")
        void shouldDeleteExistingLog() throws InvalidPeriodLogException, PeriodLogException {
            assertDoesNotThrow(() -> periodLogManager.deleteLog(1));
            assertTrue(persistence.getAllPeriodLogs().isEmpty());
        }

        @Test
        @DisplayName("Should throw PeriodLogException when deleting non-existent log")
        void shouldThrowPeriodLogExceptionWhenDeletingNonExistentLog() {
            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> periodLogManager.deleteLog(999)
            );
            assertEquals("Failed to delete period log. Log not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw InvalidPeriodLogException for negative ID during delete")
        void shouldThrowInvalidPeriodLogExceptionForNegativeIdDuringDelete() {
            InvalidPeriodLogException exception = assertThrows(
                    InvalidPeriodLogException.class,
                    () -> periodLogManager.deleteLog(-5)
            );
            assertEquals("ID must be >= 0.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should perform complete CRUD operations")
        void shouldPerformCompleteCrudOperations() throws InvalidPeriodLogException, PeriodLogException {
            periodLogManager.add(validPeriodLog);
            assertEquals(1, periodLogManager.getAll().size());

            PeriodLog retrieved = periodLogManager.getLogById(1);
            assertNotNull(retrieved);
            assertEquals("Test notes", retrieved.getNotes());

            PeriodLog updated = new PeriodLog(
                    1, startDate, endDate, Flow.HEAVY, symptoms, "Updated"
            );
            periodLogManager.updateLog(updated);

            retrieved = periodLogManager.getLogById(1);
            assertEquals(Flow.HEAVY, retrieved.getFlow());
            assertEquals("Updated", retrieved.getNotes());

            periodLogManager.deleteLog(1);
            assertTrue(periodLogManager.getAll().isEmpty());

            PeriodLogException exception = assertThrows(
                    PeriodLogException.class,
                    () -> periodLogManager.getLogById(1)
            );
            assertEquals("Period log not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle multiple operations sequentially")
        void shouldHandleMultipleOperations() throws InvalidPeriodLogException, PeriodLogException {
            for (int i = 0; i < 5; i++) {
                PeriodLog log = new PeriodLog(
                        PeriodLog.UNASSIGNED_ID,
                        startDate,
                        endDate,
                        Flow.values()[i % 3],
                        symptoms,
                        "Log " + i
                );
                periodLogManager.add(log);
            }

            assertEquals(5, periodLogManager.getAll().size());

            PeriodLog updateLog = new PeriodLog(
                    2, startDate, endDate, Flow.HEAVY, symptoms, "Updated Log 2"
            );
            periodLogManager.updateLog(updateLog);

            periodLogManager.deleteLog(1);
            periodLogManager.deleteLog(3);

            assertEquals(3, periodLogManager.getAll().size());

            assertTrue(persistence.getByID(2).isPresent());
            assertTrue(persistence.getByID(4).isPresent());
            assertTrue(persistence.getByID(5).isPresent());

            assertEquals("Updated Log 2", persistence.getByID(2).get().getNotes());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle maximum ID value")
        void shouldHandleMaxId() throws InvalidPeriodLogException, PeriodLogException {
            PeriodLog maxIdLog = new PeriodLog(
                    Integer.MAX_VALUE, startDate, endDate, flow, symptoms, "Max ID"
            );

            periodLogManager.add(maxIdLog);
            assertEquals(Integer.MAX_VALUE, periodLogManager.getLogById(Integer.MAX_VALUE).getID());
        }

        @Test
        @DisplayName("Should handle very old dates")
        void shouldHandleVeryOldDates() throws InvalidPeriodLogException, PeriodLogException {
            CycleDate oldDate = new CycleDate(LocalDate.of(1900, 1, 1));
            PeriodLog oldLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, oldDate, null, flow, symptoms, "Old log"
            );

            periodLogManager.add(oldLog);
            assertEquals(oldDate, periodLogManager.getLogById(1).getStartDate());
        }

        @Test
        @DisplayName("Should handle zero-length periods")
        void shouldHandleZeroLengthPeriods() throws InvalidPeriodLogException, PeriodLogException {
            CycleDate sameDate = new CycleDate(today);
            PeriodLog zeroLengthLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, sameDate, sameDate, flow, symptoms, "Zero length"
            );

            periodLogManager.add(zeroLengthLog);
            PeriodLog retrieved = periodLogManager.getLogById(1);
            assertEquals(sameDate, retrieved.getStartDate());
            assertEquals(sameDate, retrieved.getEndDate());
        }

        @Test
        @DisplayName("Should handle notes with special characters")
        void shouldHandleNotesWithSpecialCharacters() throws InvalidPeriodLogException, PeriodLogException {
            String specialNotes = "!@#$%^&*()_+{}|:\"<>?~`-=[]\\;',./";
            PeriodLog specialLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, flow, symptoms, specialNotes
            );

            periodLogManager.add(specialLog);
            assertEquals(specialNotes, periodLogManager.getLogById(1).getNotes());
        }

        @Test
        @DisplayName("Should handle notes with Unicode characters")
        void shouldHandleNotesWithUnicodeCharacters() throws InvalidPeriodLogException, PeriodLogException {
            String unicodeNotes = "Hello 世界 🌍 ¡Hola! öäüß";
            PeriodLog unicodeLog = new PeriodLog(
                    PeriodLog.UNASSIGNED_ID, startDate, endDate, flow, symptoms, unicodeNotes
            );

            periodLogManager.add(unicodeLog);
            assertEquals(unicodeNotes, periodLogManager.getLogById(1).getNotes());
        }
    }
}
