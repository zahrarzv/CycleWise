package com.gitgirls.cyclewise.logic.cycleprediction;

import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.CycleDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CycleHistoryTest {

    private Cycle cycleA;
    private Cycle cycleB;
    private Cycle cycleC;

    @BeforeEach
    void setup() {
        cycleA = mock(Cycle.class);
        cycleB = mock(Cycle.class);
        cycleC = mock(Cycle.class);

        CycleDate dateA = new CycleDate(LocalDate.of(2024, 1, 1));
        CycleDate dateB = new CycleDate(LocalDate.of(2024, 2, 1));
        CycleDate dateC = new CycleDate(LocalDate.of(2024, 3, 1));

        when(cycleA.getStartDate()).thenReturn(dateA);
        when(cycleB.getStartDate()).thenReturn(dateB);
        when(cycleC.getStartDate()).thenReturn(dateC);
    }

    // ---- Constructor ----

    @Test
    @DisplayName("Constructor should throw when cycle list is null")
    void shouldThrowOnNullList() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CycleHistory(null)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_NULL_OR_EMPTY_CYCLE_LIST,
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Constructor should throw when cycle list is empty")
    void shouldThrowOnEmptyList() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CycleHistory(Collections.emptyList())
        );

        assertEquals(
                CyclePredictionMessages.ERROR_NULL_OR_EMPTY_CYCLE_LIST,
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Constructor should succeed with a valid list")
    void shouldConstructSuccessfullyWithValidList() {
        assertDoesNotThrow(() -> new CycleHistory(List.of(cycleA)));
    }

    @Test
    @DisplayName("Constructor should make a defensive copy of the input list")
    void shouldMakeDefensiveCopyOfInputList() {
        List<Cycle> mutableList = new ArrayList<>(List.of(cycleA));
        CycleHistory history = new CycleHistory(mutableList);

        mutableList.clear();

        assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Constructor should sort cycles by start date")
    void shouldSortCyclesInConstructor() {
        CycleHistory history = new CycleHistory(List.of(cycleC, cycleA, cycleB));

        List<Cycle> all = history.getAllCycles();
        assertEquals(cycleA, all.get(0));
        assertEquals(cycleB, all.get(1));
        assertEquals(cycleC, all.get(2));
    }

    // ---- addCycle ----

    @Test
    @DisplayName("addCycle should increase size by one")
    void shouldIncreaseSizeByOne() {
        CycleHistory history = new CycleHistory(List.of(cycleA));
        history.addCycle(cycleB);
        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("addCycle should throw for null cycle")
    void shouldThrowOnNullCycle() {
        CycleHistory history = new CycleHistory(List.of(cycleA));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> history.addCycle(null)
        );

        assertEquals(
                CyclePredictionMessages.ERROR_NULL_CYCLE,
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("addCycle should keep cycles sorted by start date after insertion")
    void shouldMaintainSortOrder() {
        CycleHistory history = new CycleHistory(List.of(cycleC));
        history.addCycle(cycleA);
        history.addCycle(cycleB);

        List<Cycle> all = history.getAllCycles();
        assertEquals(cycleA, all.get(0));
        assertEquals(cycleB, all.get(1));
        assertEquals(cycleC, all.get(2));
    }

    // ---- getAllCycles ----

    @Test
    @DisplayName("getAllCycles should return all cycles")
    void shouldReturnAllCycles() {
        CycleHistory history = new CycleHistory(List.of(cycleA, cycleB));
        assertEquals(2, history.getAllCycles().size());
    }

    @Test
    @DisplayName("getAllCycles should return a defensive copy")
    void shouldReturnDefensiveCopy() {
        CycleHistory history = new CycleHistory(List.of(cycleA));
        history.getAllCycles().clear();
        assertEquals(1, history.size());
    }

    // ---- getLastCycle ----

    @Test
    @DisplayName("getLastCycle should return the most recent cycle by date")
    void shouldReturnMostRecentCycle() {
        CycleHistory history = new CycleHistory(List.of(cycleA, cycleC, cycleB));
        assertEquals(cycleC, history.getLastCycle());
    }

    @Test
    @DisplayName("getLastCycle should return the only cycle when size is one")
    void shouldReturnOnlyCycleWhenSizeIsOne() {
        CycleHistory history = new CycleHistory(List.of(cycleA));
        assertEquals(cycleA, history.getLastCycle());
    }

    // ---- size ----

    @Test
    @DisplayName("size should reflect the correct number of cycles")
    void shouldReturnCorrectCount() {
        CycleHistory history = new CycleHistory(List.of(cycleA, cycleB, cycleC));
        assertEquals(3, history.size());
    }
}