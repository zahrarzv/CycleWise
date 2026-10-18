package com.gitgirls.cyclewise.logic.cycleprediction;

import com.gitgirls.cyclewise.models.Cycle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CycleHistory {

    private final List<Cycle> cycles;

    public CycleHistory(List<Cycle> cycles) {
        validateCycles(cycles);
        this.cycles = new ArrayList<>(cycles);
        sortCycles();
    }

    public void addCycle(Cycle cycle) {
        if (cycle == null) {
            throw new IllegalArgumentException(CyclePredictionMessages.ERROR_NULL_CYCLE);
        }

        cycles.add(cycle);
        sortCycles();
    }

    public List<Cycle> getAllCycles() {
        return new ArrayList<>(cycles);
    }

    public Cycle getLastCycle() {
        return cycles.get(cycles.size() - 1);
    }

    public int size() {
        return cycles.size();
    }

    private void sortCycles() {
        cycles.sort(Comparator.comparing(cycle ->
                cycle.getStartDate().getDate()));
    }

    private void validateCycles(List<Cycle> cycles) {
        if (cycles == null || cycles.isEmpty()) {
            throw new IllegalArgumentException(
                    CyclePredictionMessages.ERROR_NULL_OR_EMPTY_CYCLE_LIST);
        }
    }
}