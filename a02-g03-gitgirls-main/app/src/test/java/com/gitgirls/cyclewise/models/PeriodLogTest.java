package com.gitgirls.cyclewise.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PeriodLogTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        private CycleDate startDate;
        private CycleDate endDate;
        private Flow flow;
        private Symptoms symptoms;
        private String notes;

        @BeforeEach
        void setUp() {
            startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            endDate = new CycleDate(LocalDate.of(2024, 1, 5));
            flow = Flow.HEAVY;

            // Create Symptoms with no symptoms (all false)
            symptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
            notes = "Test notes";
        }

        @Test
        @DisplayName("Should create PeriodLog with all valid parameters")
        void shouldCreateWithValidParameters() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, flow, symptoms, notes);

            assertEquals(1, periodLog.getID());
            assertEquals(startDate, periodLog.getStartDate());
            assertEquals(endDate, periodLog.getEndDate());
            assertEquals(flow, periodLog.getFlow());

            // Compare symptoms by their values since getSymptoms returns a defensive copy
            Symptoms returnedSymptoms = periodLog.getSymptoms();
            assertEquals(symptoms.hasCramps(), returnedSymptoms.hasCramps());
            assertEquals(symptoms.hasHeadache(), returnedSymptoms.hasHeadache());
            assertEquals(symptoms.hasFatigue(), returnedSymptoms.hasFatigue());
            assertEquals(symptoms.hasBloating(), returnedSymptoms.hasBloating());
            assertEquals(symptoms.hasLowerBackPain(), returnedSymptoms.hasLowerBackPain());
            assertEquals(symptoms.hasNausea(), returnedSymptoms.hasNausea());
            assertEquals(symptoms.hasDizziness(), returnedSymptoms.hasDizziness());
            assertEquals(symptoms.hasRestlessness(), returnedSymptoms.hasRestlessness());
            assertEquals(symptoms.hasFoodCravings(), returnedSymptoms.hasFoodCravings());
            assertEquals(symptoms.hasBrainFog(), returnedSymptoms.hasBrainFog());
            assertEquals(symptoms.hasAnxiety(), returnedSymptoms.hasAnxiety());
            assertEquals(symptoms.hasIrritability(), returnedSymptoms.hasIrritability());

            assertEquals("Test notes", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should create PeriodLog with UNASSIGNED_ID")
        void shouldCreateWithUnassignedId() {
            PeriodLog periodLog = new PeriodLog(PeriodLog.UNASSIGNED_ID, startDate, endDate, flow, symptoms, notes);

            assertEquals(PeriodLog.UNASSIGNED_ID, periodLog.getID());
        }

        @Test
        @DisplayName("Should create PeriodLog with null notes")
        void shouldCreateWithNullNotes() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, flow, symptoms, null);

            assertEquals("", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should create PeriodLog with empty notes")
        void shouldCreateWithEmptyNotes() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, flow, symptoms, "");

            assertEquals("", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should create PeriodLog with different Flow values")
        void shouldCreateWithDifferentFlowValues() {
            for (Flow flowValue : Flow.values()) {
                PeriodLog periodLog = new PeriodLog(1, startDate, endDate, flowValue, symptoms, notes);
                assertEquals(flowValue, periodLog.getFlow());
            }
        }

        @Test
        @DisplayName("Should create PeriodLog with different Symptoms combinations")
        void shouldCreateWithDifferentSymptomsCombinations() {
            // Test with all false
            Symptoms noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
            PeriodLog log1 = new PeriodLog(1, startDate, endDate, flow, noSymptoms, notes);
            Symptoms returnedSymptoms1 = log1.getSymptoms();

            // Compare symptom values
            assertEquals(noSymptoms.hasCramps(), returnedSymptoms1.hasCramps());
            assertEquals(noSymptoms.hasHeadache(), returnedSymptoms1.hasHeadache());
            assertEquals(noSymptoms.hasFatigue(), returnedSymptoms1.hasFatigue());

            // Test with all true
            Symptoms allSymptoms = new Symptoms(
                    true, true, true, true, true, true,
                    true, true, true, true, true, true
            );
            PeriodLog log2 = new PeriodLog(2, startDate, endDate, flow, allSymptoms, notes);
            Symptoms returnedSymptoms2 = log2.getSymptoms();

            assertEquals(allSymptoms.hasCramps(), returnedSymptoms2.hasCramps());
            assertEquals(allSymptoms.hasHeadache(), returnedSymptoms2.hasHeadache());
            assertEquals(allSymptoms.hasFatigue(), returnedSymptoms2.hasFatigue());

            // Test with mixed symptoms
            Symptoms mixedSymptoms = new Symptoms(
                    true, false, true, false, true, false,
                    true, false, true, false, true, false
            );
            PeriodLog log3 = new PeriodLog(3, startDate, endDate, flow, mixedSymptoms, notes);
            Symptoms returnedSymptoms3 = log3.getSymptoms();

            assertEquals(mixedSymptoms.hasCramps(), returnedSymptoms3.hasCramps());
            assertEquals(mixedSymptoms.hasHeadache(), returnedSymptoms3.hasHeadache());
            assertEquals(mixedSymptoms.hasFatigue(), returnedSymptoms3.hasFatigue());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        private PeriodLog periodLog;
        private CycleDate startDate;
        private CycleDate endDate;
        private Symptoms testSymptoms;

        @BeforeEach
        void setUp() {
            startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            endDate = new CycleDate(LocalDate.of(2024, 1, 5));

            // Create Symptoms with headache and fatigue
            testSymptoms = new Symptoms(
                    false, true, true, false, false, false,
                    false, false, false, false, false, false
            );

            periodLog = new PeriodLog(42, startDate, endDate, Flow.MEDIUM, testSymptoms, "Test notes");
        }

        @Test
        @DisplayName("Should get correct ID")
        void shouldGetId() {
            assertEquals(42, periodLog.getID());
        }

        @Test
        @DisplayName("Should get correct start date")
        void shouldGetStartDate() {
            assertEquals(startDate, periodLog.getStartDate());
            assertEquals(LocalDate.of(2024, 1, 1), periodLog.getStartDate().getDate());
        }

        @Test
        @DisplayName("Should get correct end date")
        void shouldGetEndDate() {
            assertEquals(endDate, periodLog.getEndDate());
            assertEquals(LocalDate.of(2024, 1, 5), periodLog.getEndDate().getDate());
        }

        @Test
        @DisplayName("Should get correct flow")
        void shouldGetFlow() {
            assertEquals(Flow.MEDIUM, periodLog.getFlow());
        }

        @Test
        @DisplayName("Should get correct symptoms")
        void shouldGetSymptoms() {
            Symptoms returnedSymptoms = periodLog.getSymptoms();

            // Verify symptom values
            assertTrue(returnedSymptoms.hasHeadache());
            assertTrue(returnedSymptoms.hasFatigue());
            assertFalse(returnedSymptoms.hasCramps());

            // Verify that it's a defensive copy (different object)
            assertNotSame(testSymptoms, returnedSymptoms);
        }

        @Test
        @DisplayName("Should get notes")
        void shouldGetNotes() {
            assertEquals("Test notes", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should return empty string for null notes")
        void shouldReturnEmptyForNullNotes() {
            Symptoms noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
            PeriodLog nullNotesLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, null);
            assertEquals("", nullNotesLog.getNotes());
        }

        @Test
        @DisplayName("Should return null for null symptoms")
        void shouldReturnNullForNullSymptoms() {
            PeriodLog nullSymptomsLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, null, "notes");
            assertNull(nullSymptomsLog.getSymptoms());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        private PeriodLog periodLog;

        @BeforeEach
        void setUp() {
            CycleDate startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate endDate = new CycleDate(LocalDate.of(2024, 1, 5));
            Symptoms noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
            periodLog = new PeriodLog(PeriodLog.UNASSIGNED_ID, startDate, endDate, Flow.LIGHT, noSymptoms, "Initial notes");
        }

        @Test
        @DisplayName("Should set ID")
        void shouldSetId() {
            assertEquals(PeriodLog.UNASSIGNED_ID, periodLog.getID());

            // Since setId is package-private, we need to access it from the same package
            // This test assumes we're in the same package as PeriodLog
            periodLog.setId(99);
            assertEquals(99, periodLog.getID());
        }

        @Test
        @DisplayName("Should override existing ID")
        void shouldOverrideExistingId() {
            periodLog.setId(100);
            assertEquals(100, periodLog.getID());

            periodLog.setId(200);
            assertEquals(200, periodLog.getID());
        }

        @Test
        @DisplayName("Should set ID to UNASSIGNED_ID")
        void shouldSetIdToUnassigned() {
            periodLog.setId(PeriodLog.UNASSIGNED_ID);
            assertEquals(PeriodLog.UNASSIGNED_ID, periodLog.getID());
        }
    }

    @Nested
    @DisplayName("Sanitize Notes Tests")
    class SanitizeNotesTests {

        private CycleDate startDate;
        private CycleDate endDate;
        private Symptoms noSymptoms;

        @BeforeEach
        void setUp() {
            startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            endDate = new CycleDate(LocalDate.of(2024, 1, 5));
            noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
        }

        @Test
        @DisplayName("Should trim leading whitespace from notes")
        void shouldTrimLeadingWhitespace() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "  Leading spaces");
            periodLog.sanitizeNotes();

            assertEquals("Leading spaces", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should trim trailing whitespace from notes")
        void shouldTrimTrailingWhitespace() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "Trailing spaces  ");
            periodLog.sanitizeNotes();

            assertEquals("Trailing spaces", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should trim both leading and trailing whitespace")
        void shouldTrimBothEnds() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "  Both ends  ");
            periodLog.sanitizeNotes();

            assertEquals("Both ends", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should handle tabs and other whitespace")
        void shouldHandleTabsAndWhitespace() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "\t\n  Mixed whitespace\t\n  ");
            periodLog.sanitizeNotes();

            assertEquals("Mixed whitespace", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should set null notes to empty string")
        void shouldSetNullNotesToEmpty() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, null);
            periodLog.sanitizeNotes();

            assertEquals("", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should handle empty string notes")
        void shouldHandleEmptyString() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "");
            periodLog.sanitizeNotes();

            assertEquals("", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should handle whitespace-only notes")
        void shouldHandleWhitespaceOnly() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "   \t\n  ");
            periodLog.sanitizeNotes();

            assertEquals("", periodLog.getNotes());
        }

        @Test
        @DisplayName("Should preserve internal whitespace")
        void shouldPreserveInternalWhitespace() {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, "  Internal   spaces  ");
            periodLog.sanitizeNotes();

            assertEquals("Internal   spaces", periodLog.getNotes());
        }

        @ParameterizedTest
        @MethodSource("provideNotesForSanitization")
        @DisplayName("Should correctly sanitize various note formats")
        void shouldCorrectlySanitizeVariousFormats(String input, String expected) {
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, input);
            periodLog.sanitizeNotes();

            assertEquals(expected, periodLog.getNotes());
        }

        private static Stream<Arguments> provideNotesForSanitization() {
            return Stream.of(
                    Arguments.of(null, ""),
                    Arguments.of("", ""),
                    Arguments.of("   ", ""),
                    Arguments.of("\t\n", ""),
                    Arguments.of("NoWhitespace", "NoWhitespace"),
                    Arguments.of("  Leading", "Leading"),
                    Arguments.of("Trailing  ", "Trailing"),
                    Arguments.of("  Both  ", "Both"),
                    Arguments.of("Multiple   spaces", "Multiple   spaces"),
                    Arguments.of("\tTabbed\t", "Tabbed"),
                    Arguments.of("\nNewLine\n", "NewLine"),
                    Arguments.of("  Mixed \t whitespace \n ", "Mixed \t whitespace")
            );
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        private Symptoms noSymptoms;

        @BeforeEach
        void setUp() {
            noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
        }

        @Test
        @DisplayName("Start date should be immutable")
        void startDateShouldBeImmutable() {
            CycleDate originalDate = new CycleDate(LocalDate.of(2024, 1, 1));
            PeriodLog periodLog = new PeriodLog(1, originalDate,
                    new CycleDate(LocalDate.of(2024, 1, 5)), Flow.LIGHT, noSymptoms, "Notes");

            CycleDate retrievedDate = periodLog.getStartDate();
            retrievedDate.plusDays(1); // This returns a new CycleDate, doesn't modify original

            assertEquals(LocalDate.of(2024, 1, 1), periodLog.getStartDate().getDate());
        }

        @Test
        @DisplayName("End date should be immutable")
        void endDateShouldBeImmutable() {
            CycleDate originalDate = new CycleDate(LocalDate.of(2024, 1, 5));
            PeriodLog periodLog = new PeriodLog(1,
                    new CycleDate(LocalDate.of(2024, 1, 1)), originalDate, Flow.LIGHT, noSymptoms, "Notes");

            CycleDate retrievedDate = periodLog.getEndDate();
            retrievedDate.minusDays(1); // This returns a new CycleDate, doesn't modify original

            assertEquals(LocalDate.of(2024, 1, 5), periodLog.getEndDate().getDate());
        }

        @Test
        @DisplayName("Symptoms should be immutable via defensive copy")
        void symptomsShouldBeImmutable() {
            Symptoms originalSymptoms = new Symptoms(
                    true, false, false, false, false, false,
                    false, false, false, false, false, false
            );

            PeriodLog periodLog = new PeriodLog(1,
                    new CycleDate(LocalDate.of(2024, 1, 1)),
                    new CycleDate(LocalDate.of(2024, 1, 5)),
                    Flow.LIGHT, originalSymptoms, "Notes");

            Symptoms retrievedSymptoms = periodLog.getSymptoms();

            // Verify it's a different object
            assertNotSame(originalSymptoms, retrievedSymptoms);

            // Verify values are the same
            assertEquals(originalSymptoms.hasCramps(), retrievedSymptoms.hasCramps());
        }
    }

    @Nested
    @DisplayName("Symptoms Access Tests")
    class SymptomsAccessTests {

        @Test
        @DisplayName("Should correctly access individual symptom flags")
        void shouldAccessIndividualSymptomFlags() {
            Symptoms symptoms = new Symptoms(
                    true,   // cramps
                    false,  // headache
                    true,   // fatigue
                    false,  // bloating
                    true,   // lowerBackPain
                    false,  // nausea
                    true,   // dizziness
                    false,  // restlessness
                    true,   // foodCravings
                    false,  // brainFog
                    true,   // anxiety
                    false   // irritability
            );

            CycleDate startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate endDate = new CycleDate(LocalDate.of(2024, 1, 5));
            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.MEDIUM, symptoms, "Test");

            Symptoms retrievedSymptoms = periodLog.getSymptoms();

            assertTrue(retrievedSymptoms.hasCramps());
            assertFalse(retrievedSymptoms.hasHeadache());
            assertTrue(retrievedSymptoms.hasFatigue());
            assertFalse(retrievedSymptoms.hasBloating());
            assertTrue(retrievedSymptoms.hasLowerBackPain());
            assertFalse(retrievedSymptoms.hasNausea());
            assertTrue(retrievedSymptoms.hasDizziness());
            assertFalse(retrievedSymptoms.hasRestlessness());
            assertTrue(retrievedSymptoms.hasFoodCravings());
            assertFalse(retrievedSymptoms.hasBrainFog());
            assertTrue(retrievedSymptoms.hasAnxiety());
            assertFalse(retrievedSymptoms.hasIrritability());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        private Symptoms noSymptoms;

        @BeforeEach
        void setUp() {
            noSymptoms = new Symptoms(
                    false, false, false, false, false, false,
                    false, false, false, false, false, false
            );
        }

        @Test
        @DisplayName("Should handle very long notes")
        void shouldHandleVeryLongNotes() {
            String longNotes = "A".repeat(10000);
            CycleDate startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate endDate = new CycleDate(LocalDate.of(2024, 1, 5));

            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, longNotes);

            assertEquals(longNotes, periodLog.getNotes());
        }

        @Test
        @DisplayName("Should handle special characters in notes")
        void shouldHandleSpecialCharacters() {
            String specialNotes = "!@#$%^&*()_+{}|:\"<>?~`-=[]\\;',./";
            CycleDate startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate endDate = new CycleDate(LocalDate.of(2024, 1, 5));

            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, specialNotes);

            assertEquals(specialNotes, periodLog.getNotes());
        }

        @Test
        @DisplayName("Should handle Unicode characters in notes")
        void shouldHandleUnicodeCharacters() {
            String unicodeNotes = "Hello 世界 🌍 ¡Hola! öäüß";
            CycleDate startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate endDate = new CycleDate(LocalDate.of(2024, 1, 5));

            PeriodLog periodLog = new PeriodLog(1, startDate, endDate, Flow.LIGHT, noSymptoms, unicodeNotes);

            assertEquals(unicodeNotes, periodLog.getNotes());
        }
    }

    @Nested
    @DisplayName("Constructor Parameter Order Tests")
    class ConstructorParameterOrderTests {

        @Test
        @DisplayName("Should maintain correct parameter order")
        void shouldMaintainCorrectParameterOrder() {
            CycleDate startDate = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate endDate = new CycleDate(LocalDate.of(2024, 1, 5));
            Flow flow = Flow.HEAVY;
            Symptoms symptoms = new Symptoms(
                    true, false, true, false, true, false,
                    true, false, true, false, true, false
            );
            String notes = "Test notes";
            int id = 123;

            PeriodLog periodLog = new PeriodLog(id, startDate, endDate, flow, symptoms, notes);

            assertEquals(id, periodLog.getID());
            assertEquals(startDate, periodLog.getStartDate());
            assertEquals(endDate, periodLog.getEndDate());
            assertEquals(flow, periodLog.getFlow());

            Symptoms returnedSymptoms = periodLog.getSymptoms();
            assertEquals(symptoms.hasCramps(), returnedSymptoms.hasCramps());
            assertEquals(symptoms.hasHeadache(), returnedSymptoms.hasHeadache());

            assertEquals(notes, periodLog.getNotes());
        }
    }

    @Nested
    @DisplayName("Flow Enum Tests")
    class FlowEnumTests {

        @Test
        @DisplayName("Should preserve enum names")
        void shouldPreserveEnumNames() {
            assertEquals("LIGHT", Flow.LIGHT.name());
            assertEquals("MEDIUM", Flow.MEDIUM.name());
            assertEquals("HEAVY", Flow.HEAVY.name());
        }

        @Test
        @DisplayName("Should have all expected Flow values")
        void shouldHaveAllExpectedFlowValues() {
            Flow[] flows = Flow.values();
            assertEquals(3, flows.length);
            assertTrue(containsFlow(flows, Flow.LIGHT));
            assertTrue(containsFlow(flows, Flow.MEDIUM));
            assertTrue(containsFlow(flows, Flow.HEAVY));
        }

        private boolean containsFlow(Flow[] flows, Flow flow) {
            for (Flow f : flows) {
                if (f == flow) {
                    return true;
                }
            }
            return false;
        }
    }
}