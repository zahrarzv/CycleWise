package com.gitgirls.cyclewise.persistence.real.periodlogrepository;

import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.*;

public final class PeriodLogQueries {

    private PeriodLogQueries() {}

    private static String baseSelect() {
        return String.format(
                "SELECT d.%s, d.%s, d.%s, d.%s, c.%s, c.%s " +
                        "FROM %s d JOIN %s c ON d.%s = c.%s ",
                COL_LOG_ID, COL_FLOW_LEVEL, COL_NOTES, COL_DATE,
                COL_START_DATE, COL_END_DATE,
                TABLE_DAILY_LOG, TABLE_CYCLE, COL_CYCLE_ID, COL_CYCLE_ID
        );
    }

    private static String baseSelectWhere(String col) {
        return baseSelect() + String.format("WHERE d.%s = ?", col);
    }


    public static String allPeriodLogsForUser() {
        return baseSelectWhere(COL_USERNAME) +
                " ORDER BY c." + COL_START_DATE + " ASC, d." + COL_DATE + " ASC";
    }

    public static String periodLogById() {
        return baseSelect() + String.format(
                "WHERE d.%s = ? AND d.%s = ?",
                COL_LOG_ID, COL_USERNAME
        );
    }

    public static String symptomsForLog() {
        return String.format(
                "SELECT t.%s FROM %s ls JOIN %s t ON ls.%s = t.%s WHERE ls.%s = ?",
                COL_SYMPTOM_NAME,
                TABLE_LOG_SYMPTOM, TABLE_SYMPTOM_TYPE,
                COL_SYMPTOM_ID, COL_SYMPTOM_ID,
                COL_LOG_ID
        );
    }

    public static String symptomIdByName() {
        return String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                COL_SYMPTOM_ID, TABLE_SYMPTOM_TYPE, COL_SYMPTOM_NAME
        );
    }

    public static String findCycleByStartDate() {
        return String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = ?",
                COL_CYCLE_ID, TABLE_CYCLE, COL_USERNAME, COL_START_DATE
        );
    }
}