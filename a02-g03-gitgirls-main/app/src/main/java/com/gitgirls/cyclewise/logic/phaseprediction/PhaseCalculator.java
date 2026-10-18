package com.gitgirls.cyclewise.logic.phaseprediction;

import com.gitgirls.cyclewise.exceptions.PhaseException;
import com.gitgirls.cyclewise.exceptions.ValidationException;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleHistory;
import com.gitgirls.cyclewise.logic.cycleprediction.CyclePredictionMessages;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.Phases;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PhaseCalculator {

    private final CycleHistory cycleHistory;
    private final PeriodCalculator periodCalculator;

    public PhaseCalculator(CycleHistory cycleHistory, PeriodCalculator periodCalculator) {
        if (cycleHistory == null) {
            throw new ValidationException(CyclePredictionMessages.ERROR_NULL_CYCLE_HISTORY);
        }
        if (periodCalculator == null) {
            throw new ValidationException(PhaseCalculatorMessages.ERROR_NULL_PERIOD_CALCULATOR);
        }
        this.cycleHistory = cycleHistory;
        this.periodCalculator = periodCalculator;
    }

    public Phases calculatePhaseToday() {
        LocalDate currentDate = LocalDate.now();
        Cycle lastCycle = cycleHistory.getLastCycle();

        LocalDate nextCycleStart = periodCalculator.calculateNextCycleStart().getDate();

        /*
        ** If today is on or past the predicted next cycle start and there is no ongoing log,
        ** treat it as premenstrual / late rather than menstruation
        */
        if (!currentDate.isBefore(nextCycleStart)) {
            return Phases.PREMENSTRUAL;
        }

        // Days since period ended
        long daysBetween = ChronoUnit.DAYS.between(lastCycle.getEndDate().getDate(), currentDate);

        // If today is same day or before period end -> still menstruating
        if (daysBetween <= 0) {
            return Phases.MENSTRUATION;
        }

        if (daysBetween <= PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS) {
            return Phases.FOLLICULAR;
        } else if (daysBetween <= PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS) {
            return Phases.OVULATION;
        } else if (daysBetween <= PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS) {
            return Phases.LUTEAL;
        } else if (daysBetween <= PhaseConstants.FOLLICULAR_PHASE_DURATION_DAYS
                + PhaseConstants.OVULATION_PHASE_DURATION_DAYS
                + PhaseConstants.LUTEAL_PHASE_DURATION_DAYS
                + PhaseConstants.PREMENSTRUAL_PHASE_DURATION_DAYS) {
            return Phases.PREMENSTRUAL;
        } else {
            throw new PhaseException(PhaseCalculatorMessages.ERROR_PHASE_UNDETERMINED);
        }
    }
}