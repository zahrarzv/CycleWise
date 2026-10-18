package com.gitgirls.cyclewise.logic.cycleprediction;

import org.junit.jupiter.api.Test;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

import com.gitgirls.cyclewise.models.SymptomConstants;

class SymptomConstantsTest {

    // --- Constant Value Tests ---

    @Test
    void shouldMatchExpectedStringValuesForAllConstants() {
        assertEquals("cramps", SymptomConstants.CRAMPS);
        assertEquals("headache", SymptomConstants.HEADACHE);
        assertEquals("fatigue", SymptomConstants.FATIGUE);
        assertEquals("bloating", SymptomConstants.BLOATING);
        assertEquals("lower_back_pain", SymptomConstants.LOWER_BACK_PAIN);
        assertEquals("nausea", SymptomConstants.NAUSEA);
        assertEquals("dizziness", SymptomConstants.DIZZINESS);
        assertEquals("restlessness", SymptomConstants.RESTLESSNESS);
        assertEquals("food_cravings", SymptomConstants.FOOD_CRAVINGS);
        assertEquals("brain_fog", SymptomConstants.BRAIN_FOG);
        assertEquals("anxiety", SymptomConstants.ANXIETY);
        assertEquals("irritability", SymptomConstants.IRRITABILITY);
    }

    // --- ALL_SYMPTOMS List Tests ---

    @Test
    void shouldContainAllTwelveSymptomsInList() {
        assertEquals(12, SymptomConstants.ALL_SYMPTOMS.size());
    }

    @Test
    void shouldContainEveryDefinedConstantInList() {
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.CRAMPS));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.HEADACHE));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.FATIGUE));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.BLOATING));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.LOWER_BACK_PAIN));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.NAUSEA));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.DIZZINESS));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.RESTLESSNESS));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.FOOD_CRAVINGS));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.BRAIN_FOG));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.ANXIETY));
        assertTrue(SymptomConstants.ALL_SYMPTOMS.contains(SymptomConstants.IRRITABILITY));
    }

    @Test
    void shouldBeUnmodifiable_allSymptomsList() {
        assertThrows(UnsupportedOperationException.class,
                () -> SymptomConstants.ALL_SYMPTOMS.add("new_symptom"));
        assertThrows(UnsupportedOperationException.class,
                () -> SymptomConstants.ALL_SYMPTOMS.remove(0));
        assertThrows(UnsupportedOperationException.class,
                () -> SymptomConstants.ALL_SYMPTOMS.clear());
    }

    @Test
    void shouldHaveNoDuplicatesInList() {
        long distinctCount = SymptomConstants.ALL_SYMPTOMS.stream().distinct().count();
        assertEquals(SymptomConstants.ALL_SYMPTOMS.size(), distinctCount);
    }

    @Test
    void shouldContainNoNullOrBlankEntriesInList() {
        for (String symptom : SymptomConstants.ALL_SYMPTOMS) {
            assertNotNull(symptom);
            assertFalse(symptom.isBlank());
        }
    }

    // --- ALL_SYMPTOMS_SET Tests ---

    @Test
    void shouldMatchListContents_allSymptomsSet() {
        assertEquals(new HashSet<>(SymptomConstants.ALL_SYMPTOMS), SymptomConstants.ALL_SYMPTOMS_SET);
    }

    @Test
    void shouldBeUnmodifiable_allSymptomsSet() {
        assertThrows(UnsupportedOperationException.class,
                () -> SymptomConstants.ALL_SYMPTOMS_SET.add("new_symptom"));
        assertThrows(UnsupportedOperationException.class,
                () -> SymptomConstants.ALL_SYMPTOMS_SET.remove(SymptomConstants.CRAMPS));
        assertThrows(UnsupportedOperationException.class,
                () -> SymptomConstants.ALL_SYMPTOMS_SET.clear());
    }

    @Test
    void shouldHaveSameSizeAsList_allSymptomsSet() {
        assertEquals(SymptomConstants.ALL_SYMPTOMS.size(), SymptomConstants.ALL_SYMPTOMS_SET.size());
    }

    // --- isValidSymptom Tests ---

    @Test
    void shouldReturnTrueForAllKnownSymptoms() {
        for (String symptom : SymptomConstants.ALL_SYMPTOMS) {
            assertTrue(SymptomConstants.isValidSymptom(symptom),
                    "Expected valid: " + symptom);
        }
    }

    @Test
    void shouldReturnFalseForUnknownSymptom() {
        assertFalse(SymptomConstants.isValidSymptom("fever"));
        assertFalse(SymptomConstants.isValidSymptom("mood_swings"));
    }

    @Test
    void shouldReturnFalseForEmptyString() {
        assertFalse(SymptomConstants.isValidSymptom(""));
    }

    @Test
    void shouldReturnFalseForNull() {
        assertFalse(SymptomConstants.isValidSymptom(null));
    }

    @Test
    void shouldBeCaseSensitive() {
        assertFalse(SymptomConstants.isValidSymptom("Cramps"));
        assertFalse(SymptomConstants.isValidSymptom("CRAMPS"));
        assertFalse(SymptomConstants.isValidSymptom("Fatigue"));
    }

    @Test
    void shouldReturnFalseForWhitespaceVariants() {
        assertFalse(SymptomConstants.isValidSymptom(" cramps"));
        assertFalse(SymptomConstants.isValidSymptom("cramps "));
        assertFalse(SymptomConstants.isValidSymptom(" cramps "));
    }
}