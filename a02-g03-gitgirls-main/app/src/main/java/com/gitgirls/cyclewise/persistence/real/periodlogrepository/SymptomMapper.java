package com.gitgirls.cyclewise.persistence.real.periodlogrepository;

import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_LOG_ID;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_SYMPTOM_ID;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_SYMPTOM_NAME;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.TABLE_LOG_SYMPTOM;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gitgirls.cyclewise.models.SymptomConstants;
import com.gitgirls.cyclewise.models.Symptoms;

public final class SymptomMapper {
    private SymptomMapper() {}
    public static Symptoms getSymptomsForLog(int periodLogID, SQLiteDatabase db) {
        boolean[] symptomFlags = new boolean[SymptomConstants.ALL_SYMPTOMS.size()];

        try (Cursor cursor = db.rawQuery(
                PeriodLogQueries.symptomsForLog(), new String[]{String.valueOf(periodLogID)})) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_SYMPTOM_NAME));
                int index = SymptomConstants.ALL_SYMPTOMS.indexOf(name);
                if (index != -1) symptomFlags[index] = true;
            }
        }

        return new Symptoms(
                symptomFlags[0],  symptomFlags[1],  symptomFlags[2],  symptomFlags[3],
                symptomFlags[4],  symptomFlags[5],  symptomFlags[6],  symptomFlags[7],
                symptomFlags[8],  symptomFlags[9],  symptomFlags[10], symptomFlags[11]
        );
    }

    public static void insertLogSymptoms(SQLiteDatabase db, int periodLogID, Symptoms symptoms) {
        for (String symptomName : SymptomConstants.ALL_SYMPTOMS) {
            if (checkActiveSymptoms(symptomName, symptoms)) {
                insertActiveSymptoms(db, periodLogID, symptomName);
            }
        }
    }

    public static void updateLogSymptoms(SQLiteDatabase db, int periodLogID, Symptoms symptoms) {
        db.delete(TABLE_LOG_SYMPTOM, COL_LOG_ID + "=?", new String[]{String.valueOf(periodLogID)});
        insertLogSymptoms(db, periodLogID, symptoms);
    }

    private static boolean checkActiveSymptoms(String symptomName, Symptoms symptoms) {
        return switch (symptomName) {
            case SymptomConstants.CRAMPS -> symptoms.hasCramps();
            case SymptomConstants.HEADACHE -> symptoms.hasHeadache();
            case SymptomConstants.FATIGUE -> symptoms.hasFatigue();
            case SymptomConstants.BLOATING -> symptoms.hasBloating();
            case SymptomConstants.LOWER_BACK_PAIN -> symptoms.hasLowerBackPain();
            case SymptomConstants.NAUSEA -> symptoms.hasNausea();
            case SymptomConstants.DIZZINESS -> symptoms.hasDizziness();
            case SymptomConstants.RESTLESSNESS -> symptoms.hasRestlessness();
            case SymptomConstants.FOOD_CRAVINGS -> symptoms.hasFoodCravings();
            case SymptomConstants.BRAIN_FOG -> symptoms.hasBrainFog();
            case SymptomConstants.ANXIETY -> symptoms.hasAnxiety();
            case SymptomConstants.IRRITABILITY -> symptoms.hasIrritability();
            default -> false;
        };
    }

    private static void insertActiveSymptoms(SQLiteDatabase db, int periodLogID, String symptomName) {
        int symptomId = getSymptomIdByName(db, symptomName);
        if (symptomId == -1) return;

        ContentValues values = new ContentValues();
        values.put(COL_LOG_ID, periodLogID);
        values.put(COL_SYMPTOM_ID, symptomId);
        db.insert(TABLE_LOG_SYMPTOM, null, values);
    }

    private static int getSymptomIdByName(SQLiteDatabase db, String name) {
        int symptomId = -1;
        try (Cursor cursor = db.rawQuery(PeriodLogQueries.symptomIdByName(), new String[]{name})) {
            if (cursor.moveToFirst()) symptomId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SYMPTOM_ID));
        }
        return symptomId;
    }
}
