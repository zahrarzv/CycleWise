package com.gitgirls.cyclewise.logic.cycleprediction;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.PeriodLog;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CycleConversionServiceTest {

    @Test
    void shouldReturnEmptyList_whenLogsAreNullOrEmpty() {
        List<Cycle> result1 = CycleConversionService.fromPeriodLogs(null);
        List<Cycle> result2 = CycleConversionService.fromPeriodLogs(Arrays.asList());

        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }

    @Test
    void shouldConvertValidPeriodLogsToCycles() {
        PeriodLog log1 = new PeriodLog(1,
                new CycleDate(LocalDate.of(2024, 1, 1)),
                new CycleDate(LocalDate.of(2024, 1, 30)),
                null, null, null);

        PeriodLog log2 = new PeriodLog(2,
                new CycleDate(LocalDate.of(2024, 2, 1)),
                new CycleDate(LocalDate.of(2024, 3, 1)),
                null, null, null);

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(log1, log2));

        assertEquals(2, cycles.size());
        assertEquals(LocalDate.of(2024, 1, 1), cycles.get(0).getStartDate().getDate());
        assertEquals(LocalDate.of(2024, 1, 30), cycles.get(0).getEndDate().getDate());
        assertEquals(LocalDate.of(2024, 2, 1), cycles.get(1).getStartDate().getDate());
        assertEquals(LocalDate.of(2024, 3, 1), cycles.get(1).getEndDate().getDate());
    }

    @Test
    void shouldSkipInvalidLogsAndOnlyReturnValidCycles() {
        PeriodLog validLog = new PeriodLog(1,
                new CycleDate(LocalDate.of(2024, 1, 1)),
                new CycleDate(LocalDate.of(2024, 1, 30)),
                null, null, null);

        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(new CycleDate(LocalDate.of(2024, 2, 1)));
        Mockito.when(invalidLog.getEndDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 28)));

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(validLog, invalidLog));

        assertEquals(1, cycles.size());
        assertEquals(LocalDate.of(2024, 1, 1), cycles.get(0).getStartDate().getDate());
        assertEquals(LocalDate.of(2024, 1, 30), cycles.get(0).getEndDate().getDate());
    }

    @Test
    void shouldSkipNullLog() {
        PeriodLog validLog = new PeriodLog(1,
                new CycleDate(LocalDate.of(2024, 1, 1)),
                new CycleDate(LocalDate.of(2024, 1, 30)),
                null, null, null);

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(null, validLog));

        assertEquals(1, cycles.size());
        assertEquals(LocalDate.of(2024, 1, 1), cycles.get(0).getStartDate().getDate());
        assertEquals(LocalDate.of(2024, 1, 30), cycles.get(0).getEndDate().getDate());
    }

    @Test
    void shouldSkipLogWithNullStartDate() {
        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(null);
        Mockito.when(invalidLog.getEndDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 30)));

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(invalidLog));

        assertNotNull(cycles);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void shouldSkipLogWithNullEndDate() {
        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 1)));
        Mockito.when(invalidLog.getEndDate()).thenReturn(null);

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(invalidLog));

        assertNotNull(cycles);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void shouldSkipLogWithNullStartLocalDate() {
        CycleDate startDate = Mockito.mock(CycleDate.class);
        Mockito.when(startDate.getDate()).thenReturn(null);

        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(startDate);
        Mockito.when(invalidLog.getEndDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 30)));

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(invalidLog));

        assertNotNull(cycles);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void shouldSkipLogWithNullEndLocalDate() {
        CycleDate endDate = Mockito.mock(CycleDate.class);
        Mockito.when(endDate.getDate()).thenReturn(null);

        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 1)));
        Mockito.when(invalidLog.getEndDate()).thenReturn(endDate);

        List<Cycle> cycles = CycleConversionService.fromPeriodLogs(Arrays.asList(invalidLog));

        assertNotNull(cycles);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void shouldReturnCycle_whenPeriodLogIsValid() {
        PeriodLog log = new PeriodLog(1,
                new CycleDate(LocalDate.of(2024, 3, 1)),
                new CycleDate(LocalDate.of(2024, 3, 30)),
                null, null, null);

        Cycle cycle = CycleConversionService.toCycle(log);

        assertNotNull(cycle);
        assertEquals(LocalDate.of(2024, 3, 1), cycle.getStartDate().getDate());
        assertEquals(LocalDate.of(2024, 3, 30), cycle.getEndDate().getDate());
    }

    @Test
    void shouldThrowInvalidPeriodLogException_whenPeriodLogIsInvalid() {
        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(new CycleDate(LocalDate.of(2024, 5, 1)));
        Mockito.when(invalidLog.getEndDate()).thenReturn(new CycleDate(LocalDate.of(2024, 4, 28)));

        InvalidPeriodLogException exception = assertThrows(
                InvalidPeriodLogException.class,
                () -> CycleConversionService.toCycle(invalidLog)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowInvalidPeriodLogException_whenPeriodLogIsNull() {
        InvalidPeriodLogException exception = assertThrows(
                InvalidPeriodLogException.class,
                () -> CycleConversionService.toCycle(null)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowInvalidPeriodLogException_whenStartDateIsNull() {
        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(null);
        Mockito.when(invalidLog.getEndDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 30)));

        InvalidPeriodLogException exception = assertThrows(
                InvalidPeriodLogException.class,
                () -> CycleConversionService.toCycle(invalidLog)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowInvalidPeriodLogException_whenEndDateIsNull() {
        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 1)));
        Mockito.when(invalidLog.getEndDate()).thenReturn(null);

        InvalidPeriodLogException exception = assertThrows(
                InvalidPeriodLogException.class,
                () -> CycleConversionService.toCycle(invalidLog)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowInvalidPeriodLogException_whenStartLocalDateIsNull() {
        CycleDate startDate = Mockito.mock(CycleDate.class);
        Mockito.when(startDate.getDate()).thenReturn(null);

        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(startDate);
        Mockito.when(invalidLog.getEndDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 30)));

        InvalidPeriodLogException exception = assertThrows(
                InvalidPeriodLogException.class,
                () -> CycleConversionService.toCycle(invalidLog)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowInvalidPeriodLogException_whenEndLocalDateIsNull() {
        CycleDate endDate = Mockito.mock(CycleDate.class);
        Mockito.when(endDate.getDate()).thenReturn(null);

        PeriodLog invalidLog = Mockito.mock(PeriodLog.class);
        Mockito.when(invalidLog.getStartDate()).thenReturn(new CycleDate(LocalDate.of(2024, 1, 1)));
        Mockito.when(invalidLog.getEndDate()).thenReturn(endDate);

        InvalidPeriodLogException exception = assertThrows(
                InvalidPeriodLogException.class,
                () -> CycleConversionService.toCycle(invalidLog)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION,
                exception.getMessage()
        );
    }
}