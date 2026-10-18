package com.gitgirls.cyclewise.logic.cycleprediction;

import com.gitgirls.cyclewise.models.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PeriodCalculatorTest {

    private CycleHistory cycleHistory;
    private PeriodCalculator calculator;

    @BeforeEach
    void setUp() {
        cycleHistory = new CycleHistory(createTestCycles());
        calculator = new PeriodCalculator(cycleHistory);
    }

    @Test
    @DisplayName("Constructor should throw when CycleHistory is null")
    void shouldThrowWhenCycleHistoryNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodCalculator(null));
    }

    @Test
    @DisplayName("Should calculate average cycle length with multiple cycles")
    void shouldCalculateAverageCycleLength() {
        int avg = calculator.calculateAvgCycleLength();

        // Jan 1 → Feb 1 = 31
        // Feb 1 → Mar 1 = 29 (leap year)
        // avg = (31 + 29) / 2 = 30
        assertEquals(30, avg);
    }

    @Test
    @DisplayName("Should return default when less than 2 cycles")
    void shouldReturnDefaultAvgWithOneCycle() {
        CycleHistory history = new CycleHistory(
                Collections.singletonList(createCycle(LocalDate.of(2024, 1, 1)))
        );

        PeriodCalculator calc = new PeriodCalculator(history);

        assertEquals(CycleConstants.DEFAULT_CYCLE_LENGTH,
                calc.calculateAvgCycleLength());
    }

    @Test
    @DisplayName("Should calculate next cycle start correctly")
    void shouldCalculateNextCycleStart() {
        CycleDate next = calculator.calculateNextCycleStart();

        // avg = 30, last = Mar 1 → Mar 1 + 30 = Mar 31
        assertEquals(LocalDate.of(2024, 3, 31), next.getDate());
    }

    @Test
    @DisplayName("Should update prediction after adding new cycle")
    void shouldUpdateAfterAddingCycle() {
        cycleHistory.addCycle(createCycle(LocalDate.of(2024, 4, 1)));

        CycleDate next = calculator.calculateNextCycleStart();

        // New diffs: Jan→Feb=31, Feb→Mar=29, Mar→Apr=31
        // avg = (31+29+31)/3 = 30
        // last = Apr 1 → Apr 1 + 30 = May 1
        assertEquals(LocalDate.of(2024, 5, 1), next.getDate());
    }

    @Test
    @DisplayName("Should handle unsorted input cycles")
    void shouldHandleUnsortedCycles() {
        List<Cycle> unsorted = Arrays.asList(
                createCycle(LocalDate.of(2024, 3, 1)),
                createCycle(LocalDate.of(2024, 1, 1)),
                createCycle(LocalDate.of(2024, 2, 1))
        );

        PeriodCalculator calc = new PeriodCalculator(new CycleHistory(unsorted));

        int avg = calc.calculateAvgCycleLength();

        // Sorted internally → same result
        assertEquals(30, avg);
    }

    // ---------- Helper Methods ----------

    private List<Cycle> createTestCycles() {
        return Arrays.asList(
                createCycle(LocalDate.of(2024, 1, 1)),
                createCycle(LocalDate.of(2024, 2, 1)),
                createCycle(LocalDate.of(2024, 3, 1))
        );
    }

    private Cycle createCycle(LocalDate startDate) {
        // Using DEFAULT_CYCLE_LENGTH = 28 for the cycle duration
        // End date = start date + (DEFAULT_CYCLE_LENGTH - 1) days
        // This creates a cycle that lasts 28 days (inclusive of start date)
        LocalDate endDate = startDate.plusDays(CycleConstants.DEFAULT_CYCLE_LENGTH - 1);

        return Cycle.builder()
                .withStartDate(new CycleDate(startDate))
                .withEndDate(new CycleDate(endDate))
                .build();
    }
}