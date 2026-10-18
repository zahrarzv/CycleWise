package com.gitgirls.cyclewise.logic.periodlogging;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.Flow;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Symptoms;
import com.gitgirls.cyclewise.persistence.PeriodLogPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeriodLogServiceTest {

    @Mock
    private PeriodLogPersistence mockPersistence;

    @Mock
    private PeriodLog mockLog;

    private PeriodLogService service;


    private static final String TEST_USER = "testuser";

    @BeforeEach
    void setUp() {
        service = new PeriodLogService(mockPersistence, TEST_USER);
    }

    @Test
    @DisplayName("Should add valid log through persistence")
    void shouldAddValidLog() {
        PeriodLog validLog = validLogForCreate();
        when(mockPersistence.add(validLog, TEST_USER)).thenReturn(true);

        assertDoesNotThrow(() -> service.addLog(validLog));

        verify(mockPersistence, times(1)).add(validLog, TEST_USER);
    }

    @Test
    @DisplayName("Should throw InvalidPeriodLogException for invalid add input")
    void shouldThrowInvalidPeriodLogExceptionOnAdd() {
        PeriodLog invalidLog = new PeriodLog(
                PeriodLog.UNASSIGNED_ID,
                null,
                null,
                Flow.LIGHT,
                validSymptoms(),
                ""
        );

        assertThrows(InvalidPeriodLogException.class, () -> service.addLog(invalidLog));
        verify(mockPersistence, never()).add(any(), any());
    }

    @Test
    @DisplayName("Should throw PeriodLogException when persistence add fails")
    void shouldThrowPeriodLogExceptionOnAddFailure() {
        PeriodLog validLog = validLogForCreate();
        when(mockPersistence.add(validLog, TEST_USER)).thenReturn(false);

        assertThrows(PeriodLogException.class, () -> service.addLog(validLog));
    }

    @Test
    @DisplayName("Should return log by ID")
    void shouldReturnLogById() {
        when(mockPersistence.getByID(42, TEST_USER)).thenReturn(Optional.of(mockLog));

        PeriodLog result = service.getLogById(42);

        assertEquals(mockLog, result);
        verify(mockPersistence, times(1)).getByID(42, TEST_USER);
    }

    @Test
    @DisplayName("Should throw InvalidPeriodLogException for negative ID")
    void shouldThrowInvalidPeriodLogExceptionOnGetLogById() {
        assertThrows(InvalidPeriodLogException.class, () -> service.getLogById(-1));
        verify(mockPersistence, never()).getByID(anyInt(), any());
    }

    @Test
    @DisplayName("Should throw PeriodLogException when log is not found")
    void shouldThrowPeriodLogExceptionWhenLogNotFound() {
        when(mockPersistence.getByID(1, TEST_USER)).thenReturn(Optional.empty());

        assertThrows(PeriodLogException.class, () -> service.getLogById(1));
    }

    @Test
    @DisplayName("Should return all logs")
    void shouldReturnAllLogs() {
        List<PeriodLog> expected = Arrays.asList(mockLog, mockLog);
        when(mockPersistence.getAllPeriodLogs(TEST_USER)).thenReturn(expected);

        List<PeriodLog> result = service.getAllLogs();

        assertEquals(expected, result);
        verify(mockPersistence, times(1)).getAllPeriodLogs(TEST_USER);
    }

    @Test
    @DisplayName("Should return empty list when there are no logs")
    void shouldReturnEmptyListWhenNoLogs() {;
        when(mockPersistence.getAllPeriodLogs(TEST_USER)).thenReturn(List.of());

        List<PeriodLog> result = service.getAllLogs();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should update valid log")
    void shouldUpdateValidLog() {
        PeriodLog validLog = validLogForUpdate();
        when(mockPersistence.update(validLog, TEST_USER)).thenReturn(true);

        assertDoesNotThrow(() -> service.updateLog(validLog));

        verify(mockPersistence, times(1)).update(validLog, TEST_USER);
    }

    @Test
    @DisplayName("Should throw InvalidPeriodLogException for invalid update input")
    void shouldThrowInvalidPeriodLogExceptionOnUpdate() {
        PeriodLog invalidLog = new PeriodLog(
                -1,
                null,
                null,
                Flow.LIGHT,
                validSymptoms(),
                ""
        );

        assertThrows(InvalidPeriodLogException.class, () -> service.updateLog(invalidLog));
        verify(mockPersistence, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should throw PeriodLogException when persistence update fails")
    void shouldThrowPeriodLogExceptionOnUpdateFailure() {
        PeriodLog validLog = validLogForUpdate();
        when(mockPersistence.update(validLog, TEST_USER)).thenReturn(false);

        assertThrows(PeriodLogException.class, () -> service.updateLog(validLog));
    }

    @Test
    @DisplayName("Should delete log by ID")
    void shouldDeleteLog() {
        when(mockPersistence.delete(7, TEST_USER)).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteLog(7));

        verify(mockPersistence, times(1)).delete(7, TEST_USER);
    }

    @Test
    @DisplayName("Should throw InvalidPeriodLogException for negative delete ID")
    void shouldThrowInvalidPeriodLogExceptionOnDelete() {
        assertThrows(InvalidPeriodLogException.class, () -> service.deleteLog(-1));
        verify(mockPersistence, never()).delete(anyInt(), any());
    }

    @Test
    @DisplayName("Should throw PeriodLogException when persistence delete fails")
    void shouldThrowPeriodLogExceptionOnDeleteFailure() {
        when(mockPersistence.delete(1, TEST_USER)).thenReturn(false);

        assertThrows(PeriodLogException.class, () -> service.deleteLog(1));
    }

    private Symptoms validSymptoms() {
        return new Symptoms(
                false, false, false, false, false, false,
                false, false, false, false, false, false
        );
    }

    private PeriodLog validLogForCreate() {
        return new PeriodLog(
                PeriodLog.UNASSIGNED_ID,
                new CycleDate(LocalDate.now().minusDays(3)),
                new CycleDate(LocalDate.now().minusDays(1)),
                Flow.LIGHT,
                validSymptoms(),
                "notes"
        );
    }

    private PeriodLog validLogForUpdate() {
        return new PeriodLog(
                1,
                new CycleDate(LocalDate.now().minusDays(3)),
                new CycleDate(LocalDate.now().minusDays(1)),
                Flow.LIGHT,
                validSymptoms(),
                "notes"
        );
    }
}
