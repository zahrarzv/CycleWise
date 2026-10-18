/**
 * PeriodLogValidator contains the rules to validates period log data
 * before it is processed or saved.
 */

package com.gitgirls.cyclewise.logic.periodlogging;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.Symptoms;
import com.gitgirls.cyclewise.models.Flow;

import java.time.LocalDate;

public final class PeriodLogValidator {
    private static final int MAX_NOTES_LENGTH = 500;

    private PeriodLogValidator() {}

    public static void validateForCreate(PeriodLog log) throws InvalidPeriodLogException {
        validateLogRequired(log);
        validateID(log.getID());
        validateDates(log);
        validateDetails(log.getFlow(), log.getSymptoms(), log.getNotes());
    }

    public static void validateForUpdate(PeriodLog log) throws InvalidPeriodLogException {
        validateLogRequired(log);
        validateID(log.getID());
        validateDates(log);
        validateDetails(log.getFlow(), log.getSymptoms(), log.getNotes());
    }

    private static void validateLogRequired(PeriodLog log) throws InvalidPeriodLogException {
        if (log == null) {
            throw new InvalidPeriodLogException(PeriodLogMessages.LOG_REQUIRED);
        }
    }

    public static void validateDates(PeriodLog log) throws InvalidPeriodLogException {
        CycleDate start = log.getStartDate();
        CycleDate end = log.getEndDate();

        if (start == null) {
            throw new InvalidPeriodLogException(PeriodLogMessages.START_DATE_REQUIRED);
        }

        LocalDate today = LocalDate.now();

        // Only current or past logs are allowed
        if (start.getDate().isAfter(today)) {
            throw new InvalidPeriodLogException(PeriodLogMessages.START_DATE_FUTURE);
        }

        if (end != null) {
            if (end.getDate().isBefore(start.getDate())) {
                throw new InvalidPeriodLogException(PeriodLogMessages.END_DATE_BEFORE_START);
            }

            if (end.getDate().isAfter(today)) {
                throw new InvalidPeriodLogException(PeriodLogMessages.END_DATE_FUTURE);
            }
        }
    }

    public static void validateDetails(Flow flow, Symptoms symptoms, String notes) throws InvalidPeriodLogException {
        if (flow == null) {
            throw new InvalidPeriodLogException(PeriodLogMessages.FLOW_REQUIRED);
        }
        if (symptoms == null) {
            throw new InvalidPeriodLogException(PeriodLogMessages.SYMPTOMS_REQUIRED);
        }

        // notes is optional, but if present keep it reasonable
        if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
            throw new InvalidPeriodLogException(PeriodLogMessages.NOTES_TOO_LONG);
        }
    }

    public static void validateID (int id) throws InvalidPeriodLogException {
        if (id < 0) {
            throw new InvalidPeriodLogException(PeriodLogMessages.INVALID_ID);
        }
    }
}
