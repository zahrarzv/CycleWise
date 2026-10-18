package com.gitgirls.cyclewise.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SymptomConstants - Central definition of all possible symptoms
 * This is the SINGLE SOURCE OF TRUTH for symptom names
 */
public final class SymptomConstants {
    private SymptomConstants() {}

    // Define all symptoms as constants
    public static final String CRAMPS = "cramps";
    public static final String HEADACHE = "headache";
    public static final String FATIGUE = "fatigue";
    public static final String BLOATING = "bloating";
    public static final String LOWER_BACK_PAIN = "lower_back_pain";
    public static final String NAUSEA = "nausea";
    public static final String DIZZINESS = "dizziness";
    public static final String RESTLESSNESS = "restlessness";
    public static final String FOOD_CRAVINGS = "food_cravings";
    public static final String BRAIN_FOG = "brain_fog";
    public static final String ANXIETY = "anxiety";
    public static final String IRRITABILITY = "irritability";

    public static final List<String> ALL_SYMPTOMS = Collections.unmodifiableList(Arrays.asList(
            CRAMPS, HEADACHE, FATIGUE, BLOATING, LOWER_BACK_PAIN,
            NAUSEA, DIZZINESS, RESTLESSNESS, FOOD_CRAVINGS, BRAIN_FOG,
            ANXIETY, IRRITABILITY
    ));

    public static final Set<String> ALL_SYMPTOMS_SET =
            Collections.unmodifiableSet(new HashSet<>(ALL_SYMPTOMS));

    public static boolean isValidSymptom(String symptomName) {
        return ALL_SYMPTOMS_SET.contains(symptomName);
    }
}