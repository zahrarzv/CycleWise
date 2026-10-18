package com.gitgirls.cyclewise.logic.cycleprediction;

import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.CycleDate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PeriodCalculator {
    private static final int MIN_CYCLES_FOR_AVERAGE = 2;
    private static final int INTERVAL_OFFSET = 1;

    private final CycleHistory cycleHistory;

    public PeriodCalculator(CycleHistory cycleHistory) {
        if (cycleHistory == null) {
            throw new IllegalArgumentException(CyclePredictionMessages.ERROR_NULL_CYCLE_HISTORY);
        }

        this.cycleHistory = cycleHistory;
    }

    public CycleDate calculateNextCycleStart() {

        int cycleLength = calculateAvgCycleLength();

        Cycle lastCycle = cycleHistory.getLastCycle();

        return lastCycle.getStartDate().plusDays(cycleLength);
    }

    public int calculateAvgCycleLength() {

        List<Cycle> cycles = cycleHistory.getAllCycles();

        if (cycles.size() < MIN_CYCLES_FOR_AVERAGE) {
            return CycleConstants.DEFAULT_CYCLE_LENGTH;
        }

        long total = 0;

        for (int i = 1; i < cycles.size(); i++) {

            LocalDate prev = cycles.get(i - INTERVAL_OFFSET)
                    .getStartDate()
                    .getDate();

            LocalDate curr = cycles.get(i)
                    .getStartDate()
                    .getDate();

            long diff = ChronoUnit.DAYS.between(prev, curr);

            total += diff;
        }

        return (int) (total / (cycles.size() - 1));
    }
}