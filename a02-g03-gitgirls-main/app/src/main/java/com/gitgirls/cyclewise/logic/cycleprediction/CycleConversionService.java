package com.gitgirls.cyclewise.logic.cycleprediction;

import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CycleConversionService {
    private CycleConversionService() {}

    // Converts a list of PeriodLogs to a list of Cycles (invalid logs are skipped)
    public static List<Cycle> fromPeriodLogs(List<PeriodLog> logs) {
        List<Cycle> cycles = new ArrayList<>();

        if (logs == null || logs.isEmpty()) {
            return Collections.emptyList();
        }

        for (PeriodLog log : logs) {
            if (!isValidLog(log)) {
                continue;
            }

            cycles.add(toCycle(log));
        }

        return Collections.unmodifiableList(cycles);
    }

    private static boolean isValidLog(PeriodLog log) {
        if (log == null) return false;
        if (log.getStartDate() == null) return false;
        if (log.getEndDate() == null) return false;
        if (log.getStartDate().getDate() == null) return false;
        if (log.getEndDate().getDate() == null) return false;

        // Ensure end date is not before start date
        return !log.getEndDate().getDate()
                .isBefore(log.getStartDate().getDate());
    }

    // Converts a single PeriodLog to a Cycle
    public static Cycle toCycle(PeriodLog log) {
        if (!isValidLog(log)) {
            throw new InvalidPeriodLogException(
                    CyclePredictionMessages.ERROR_INVALID_PERIOD_LOG_FOR_CYCLE_CONVERSION
            );
        }
        return new Cycle(log.getStartDate(), log.getEndDate());
    }
}