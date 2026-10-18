package com.gitgirls.cyclewise.persistence.real;

/**
 * SeedData
 *
 * Default data inserted into the database on first creation.
 * Symptom types are seeded here as a fixed lookup list.
 * Five team member accounts are included with cycles, logs, and symptoms.
 */
public class SeedData {

    public static final String[] SEED_STATEMENTS = {


            "INSERT INTO " + CycleWiseSchema.TABLE_USER_ACCOUNT +
                    " (" + CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_PASSWORD   + ", " +
                    CycleWiseSchema.COL_FIRST_NAME + ", " +
                    CycleWiseSchema.COL_LAST_NAME  + ", " +
                    CycleWiseSchema.COL_DOB        + ") " +
                    "VALUES ('alice_p', 'hashed_pw_1', 'Alice', 'Pham', '2000-01-01');",

            "INSERT INTO " + CycleWiseSchema.TABLE_USER_ACCOUNT +
                    " (" + CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_PASSWORD   + ", " +
                    CycleWiseSchema.COL_FIRST_NAME + ", " +
                    CycleWiseSchema.COL_LAST_NAME  + ", " +
                    CycleWiseSchema.COL_DOB        + ") " +
                    "VALUES ('khadijat_f', 'hashed_pw_2', 'Khadijat', 'Folarin', '2000-01-01');",

            "INSERT INTO " + CycleWiseSchema.TABLE_USER_ACCOUNT +
                    " (" + CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_PASSWORD   + ", " +
                    CycleWiseSchema.COL_FIRST_NAME + ", " +
                    CycleWiseSchema.COL_LAST_NAME  + ", " +
                    CycleWiseSchema.COL_DOB        + ") " +
                    "VALUES ('krissy_t', 'hashed_pw_3', 'Krissy', 'Tran', '2000-01-01');",

            "INSERT INTO " + CycleWiseSchema.TABLE_USER_ACCOUNT +
                    " (" + CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_PASSWORD   + ", " +
                    CycleWiseSchema.COL_FIRST_NAME + ", " +
                    CycleWiseSchema.COL_LAST_NAME  + ", " +
                    CycleWiseSchema.COL_DOB        + ") " +
                    "VALUES ('sujana_i', 'hashed_pw_4', 'Sujana', 'Islam', '2000-01-01');",

            "INSERT INTO " + CycleWiseSchema.TABLE_USER_ACCOUNT +
                    " (" + CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_PASSWORD   + ", " +
                    CycleWiseSchema.COL_FIRST_NAME + ", " +
                    CycleWiseSchema.COL_LAST_NAME  + ", " +
                    CycleWiseSchema.COL_DOB        + ") " +
                    "VALUES ('zahra_r', 'hashed_pw_5', 'Zahra', 'Rizvi', '2000-01-01');",

            "INSERT INTO " + CycleWiseSchema.TABLE_USER_ACCOUNT +
                    " (" + CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_PASSWORD   + ", " +
                    CycleWiseSchema.COL_FIRST_NAME + ", " +
                    CycleWiseSchema.COL_LAST_NAME  + ", " +
                    CycleWiseSchema.COL_DOB        + ") " +
                    "VALUES ('alice123', 'hashed_pw_6', 'Alice', 'Smith', '1998-04-12');",

            // Fixed list - symptom IDs are  referenced by LogSymptom below

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('irritability');",   // 1

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('bloating');",       // 2

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('restlessness');",   // 3

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('anxiety');",        // 4

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('headache');",       // 5

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('nausea');",         // 6

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('food_cravings');",  // 7

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('cramps');",         // 8

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('dizziness');",      // 9

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('fatigue');",        // 10

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('brain_fog');",      // 11

            "INSERT INTO " + CycleWiseSchema.TABLE_SYMPTOM_TYPE +
                    " (" + CycleWiseSchema.COL_SYMPTOM_NAME + ") VALUES ('lower_back_pain');",// 12


            // Cycles
            // Cycle start = first day of period, end = start of next period (~28 days later)
            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (1, 'alice_p', '2025-12-05', '2025-12-10');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (2, 'alice_p', '2026-01-05', '2026-01-10');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (3, 'khadijat_f', '2025-12-10', '2025-12-15');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (4, 'khadijat_f', '2026-01-10', '2026-01-15');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (5, 'krissy_t', '2026-01-08', '2026-01-13');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (6, 'sujana_i', '2026-01-12', '2026-01-17');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (7, 'zahra_r', '2026-01-20', '2026-01-25');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (8, 'alice123', '2025-12-15', '2025-12-20');",

            "INSERT INTO " + CycleWiseSchema.TABLE_CYCLE +
                    " (" + CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_START_DATE + ", " +
                    CycleWiseSchema.COL_END_DATE   + ") " +
                    "VALUES (9, 'alice123', '2026-01-15', '2026-01-20');",


            //  Daily logs - each log has its own date (the specific day logged)
            // cycle_id links to the cycle it belongs to; start/end dates come from that cycle

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (1, 1, 'alice_p', '2025-12-05', 'heavy', 'first day, lots of cramps');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (2, 1, 'alice_p', '2025-12-06', 'heavy', 'feeling exhausted');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (3, 2, 'alice_p', '2026-01-05', 'medium', 'started lighter than usual');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (4, 3, 'khadijat_f', '2025-11-10', 'heavy', 'terrible cramps');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (5, 3, 'khadijat_f', '2025-12-11', 'medium', 'nausea today');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (6, 4, 'khadijat_f', '2026-01-10', 'light', 'much easier this cycle');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (7, 5, 'krissy_t', '2026-01-08', 'medium', 'mild headache');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (8, 6, 'sujana_i', '2026-01-12', 'heavy', 'back pain and bloating');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (9, 7, 'zahra_r', '2026-01-20', 'medium', 'restless and anxious');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (10, 8, 'alice123', '2025-12-15', 'medium', 'mild cramps in the morning');",

            "INSERT INTO " + CycleWiseSchema.TABLE_DAILY_LOG +
                    " (" + CycleWiseSchema.COL_LOG_ID     + ", " +
                    CycleWiseSchema.COL_CYCLE_ID   + ", " +
                    CycleWiseSchema.COL_USERNAME   + ", " +
                    CycleWiseSchema.COL_DATE       + ", " +
                    CycleWiseSchema.COL_FLOW_LEVEL + ", " +
                    CycleWiseSchema.COL_NOTES      + ") " +
                    "VALUES (11, 9, 'alice123', '2026-01-15', 'light', 'feeling better this cycle');",

            // log_id, symptom_id, notes
            // symptom IDs match the SymptomType insert order above

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (1, 8, NULL);",   // alice log 1: cramps

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (1, 10, NULL);",  // alice log 1: fatigue

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (2, 10, NULL);",  // alice log 2: fatigue

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (2, 9, NULL);",   // alice log 2: dizziness

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (3, 7, NULL);",   // alice log 3: food_cravings

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (4, 8, NULL);",   // khadijat log 1: cramps

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (4, 2, NULL);",   // khadijat log 1: bloating

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (5, 6, NULL);",   // khadijat log 2: nausea

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (6, 4, NULL);",   // khadijat log 3: anxiety

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (7, 5, NULL);",   // krissy log 1: headache

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (8, 12, NULL);",  // sujana log 1: lower_back_pain

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (8, 2, NULL);",   // sujana log 1: bloating

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (9, 3, NULL);",   // zahra log 1: restlessness

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (9, 4, NULL);",   // zahra log 1: anxiety

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (10, 8, NULL);",  // alice123 log 1: cramps

            "INSERT INTO " + CycleWiseSchema.TABLE_LOG_SYMPTOM +
                    " (" + CycleWiseSchema.COL_LOG_ID + ", " + CycleWiseSchema.COL_SYMPTOM_ID + ", " + CycleWiseSchema.COL_NOTES + ") " +
                    "VALUES (10, 10, NULL);"  // alice123 log 1: fatigue
    };


    private SeedData() {}
}