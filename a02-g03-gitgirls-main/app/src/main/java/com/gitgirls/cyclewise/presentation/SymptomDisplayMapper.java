package com.gitgirls.cyclewise.presentation;

import android.content.Context;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.models.SymptomConstants;

public final class SymptomDisplayMapper {
    private SymptomDisplayMapper() {}
    public static String toDisplayString(Context context, String symptomKey) {
        switch (symptomKey) {
            case SymptomConstants.CRAMPS:
                return context.getString(R.string.symptom_cramps);
            case SymptomConstants.HEADACHE:
                return context.getString(R.string.symptom_headache);
            case SymptomConstants.FATIGUE:
                return context.getString(R.string.symptom_fatigue);
            case SymptomConstants.BLOATING:
                return context.getString(R.string.symptom_bloating);
            case SymptomConstants.LOWER_BACK_PAIN:
                return context.getString(R.string.symptom_lower_back_pain);
            case SymptomConstants.NAUSEA:
                return context.getString(R.string.symptom_nausea);
            case SymptomConstants.DIZZINESS:
                return context.getString(R.string.symptom_dizziness);
            case SymptomConstants.RESTLESSNESS:
                return context.getString(R.string.symptom_restlessness);
            case SymptomConstants.FOOD_CRAVINGS:
                return context.getString(R.string.symptom_food_cravings);
            case SymptomConstants.BRAIN_FOG:
                return context.getString(R.string.symptom_brain_fog);
            case SymptomConstants.ANXIETY:
                return context.getString(R.string.symptom_anxiety);
            case SymptomConstants.IRRITABILITY:
                return context.getString(R.string.symptom_irritability);
            default:
                throw new IllegalArgumentException(
                        context.getString(R.string.error_unknown_symptom_value, symptomKey)
                );
        }
    }
}