package com.gitgirls.cyclewise.models;

import com.gitgirls.cyclewise.logic.cycleprediction.CycleConstants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CycleTest {

    @Nested
    @DisplayName("Cycle Constructor Tests")
    class ConstructorTests {

        private CycleDate validStart;
        private CycleDate validEnd;

        @BeforeEach
        void setUp() {
            validStart = new CycleDate(LocalDate.of(2024, 1, 1));
            validEnd = new CycleDate(LocalDate.of(2024, 1, 29)); // 29 days later (diff = 28 + 1)
        }

        @Test
        @DisplayName("Should create Cycle with valid dates")
        void shouldCreateWithValidDates() {
            Cycle cycle = new Cycle(validStart, validEnd);

            assertEquals(validStart, cycle.getStartDate());
            assertEquals(validEnd, cycle.getEndDate());
        }

        @Test
        @DisplayName("Should throw exception when end date is before start date")
        void shouldThrowWhenEndBeforeStart() {
            CycleDate endBeforeStart = new CycleDate(LocalDate.of(2023, 12, 31));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Cycle(validStart, endBeforeStart)
            );
            assertTrue(exception.getMessage().contains("End date cannot be before start date"));
        }

        @Test
        @DisplayName("Should accept exactly 28 day cycle")
        void shouldAccept28DayCycle() {
            // Jan 1 to Jan 28 = 28 days total (diff = 27 + 1 = 28)
            CycleDate exactly28Days = new CycleDate(LocalDate.of(2024, 1, 28));
            assertDoesNotThrow(() -> new Cycle(validStart, exactly28Days));
        }

        @Test
        @DisplayName("Should accept 29 day cycle")
        void shouldAccept29DayCycle() {
            // Jan 1 to Jan 29 = 29 days total (diff = 28 + 1 = 29)
            CycleDate exactly29Days = new CycleDate(LocalDate.of(2024, 1, 29));
            assertDoesNotThrow(() -> new Cycle(validStart, exactly29Days));
        }
    }

    @Nested
    @DisplayName("Cycle Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build cycle using builder pattern")
        void shouldBuildCycleWithBuilder() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 28)); // 28 day cycle

            Cycle cycle = Cycle.builder()
                    .withStartDate(start)
                    .withEndDate(end)
                    .build();

            assertEquals(start, cycle.getStartDate());
            assertEquals(end, cycle.getEndDate());
        }

        @Test
        @DisplayName("Builder should throw exception for invalid dates")
        void builderShouldValidateDates() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate invalidEnd = new CycleDate(LocalDate.of(2023, 12, 31));

            assertThrows(IllegalArgumentException.class,
                    () -> Cycle.builder()
                            .withStartDate(start)
                            .withEndDate(invalidEnd)
                            .build()
            );
        }

        @Test
        @DisplayName("Builder should accept short valid duration")
        void builderShouldAcceptShortDuration() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate shortEnd = new CycleDate(LocalDate.of(2024, 1, 6));

            assertDoesNotThrow(() -> Cycle.builder()
                    .withStartDate(start)
                    .withEndDate(shortEnd)
                    .build()
            );
        }

        @Test
        @DisplayName("Builder should accept 28 day cycle")
        void builderShouldAccept28DayCycle() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 28)); // 28 days

            assertDoesNotThrow(() -> Cycle.builder()
                    .withStartDate(start)
                    .withEndDate(end)
                    .build()
            );
        }
    }

    @Nested
    @DisplayName("Cycle Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @MethodSource("provideValidCycleDates")
        @DisplayName("Should accept valid cycle durations")
        void shouldAcceptValidCycleDurations(LocalDate start, LocalDate end) {
            CycleDate startDate = new CycleDate(start);
            CycleDate endDate = new CycleDate(end);

            assertDoesNotThrow(() -> new Cycle(startDate, endDate));
        }

        private static Stream<Arguments> provideValidCycleDates() {
            return Stream.of(
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 28)), // 28 days
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 29)), // 29 days
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1)),  // 31 days
                    Arguments.of(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)), // 29 days (leap year)
                    Arguments.of(LocalDate.of(2023, 2, 1), LocalDate.of(2023, 3, 1)),  // 28 days (non-leap year)
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 30)), // 29 days
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 5))   // 35 days
            );
        }

        private static Stream<Arguments> provideInvalidCycleDates() {
            return Stream.of(
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1)),  // 1 day
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2)),  // 2 days
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 6)),  // 6 days
                    // REMOVE these - they should be valid (7+ days)
                    // Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 26)), // 26 days - VALID
                    // Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 27)), // 27 days - VALID
                    Arguments.of(LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 1)),  // End before start
                    Arguments.of(LocalDate.of(2024, 1, 1), LocalDate.of(2023, 12, 31)) // End before start
            );
        }
    }

    @Nested
    @DisplayName("Cycle Date Comparison Tests")
    class DateComparisonTests {

        @Test
        @DisplayName("Should correctly identify valid and invalid date order")
        void shouldIdentifyValidAndInvalidDateOrder() throws Exception {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 7));

            Cycle cycle = new Cycle(start, end);

            java.lang.reflect.Method validateMethod = Cycle.class.getDeclaredMethod("validateCycleDates", CycleDate.class, CycleDate.class);
            validateMethod.setAccessible(true);

            boolean result = (boolean) validateMethod.invoke(cycle, start, end);
            assertTrue(result);

            CycleDate endBeforeStart = new CycleDate(LocalDate.of(2023, 12, 31));
            boolean invalidResult = (boolean) validateMethod.invoke(cycle, start, endBeforeStart);
            assertFalse(invalidResult);
        }
    }

    @Nested
    @DisplayName("Cycle Constants Tests")
    class CycleConstantsTests {

        @Test
        @DisplayName("DEFAULT_CYCLE_LENGTH should be 28")
        void defaultCycleLengthShouldBe28() {
            assertEquals(28, CycleConstants.DEFAULT_CYCLE_LENGTH);
        }

        @Test
        @DisplayName("Validation should allow short valid durations")
        void validationShouldAllowShortValidDurations() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 7));

            assertDoesNotThrow(() -> new Cycle(start, end));

            CycleDate shortEnd = new CycleDate(LocalDate.of(2024, 1, 6));
            assertDoesNotThrow(() -> new Cycle(start, shortEnd));
        }
    }

    @Nested
    @DisplayName("Cycle Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Start date should be immutable")
        void startDateShouldBeImmutable() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 28));

            Cycle cycle = new Cycle(start, end);

            CycleDate retrievedStart = cycle.getStartDate();
            retrievedStart.plusDays(1); // This returns a new CycleDate, doesn't modify original

            assertEquals(LocalDate.of(2024, 1, 1), cycle.getStartDate().getDate());
        }

        @Test
        @DisplayName("End date should be immutable")
        void endDateShouldBeImmutable() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 28));

            Cycle cycle = new Cycle(start, end);

            CycleDate retrievedEnd = cycle.getEndDate();
            retrievedEnd.minusDays(1); // This returns a new CycleDate, doesn't modify original

            assertEquals(LocalDate.of(2024, 1, 28), cycle.getEndDate().getDate());
        }

        @Test
        @DisplayName("Cycle should be immutable - no setters for dates")
        void cycleShouldBeImmutable() {
            CycleDate start = new CycleDate(LocalDate.of(2024, 1, 1));
            CycleDate end = new CycleDate(LocalDate.of(2024, 1, 28));

            Cycle cycle = new Cycle(start, end);

            // Verify there are no setters for startDate or endDate
            assertThrows(NoSuchMethodException.class,
                    () -> Cycle.class.getMethod("setStartDate", CycleDate.class)
            );
            assertThrows(NoSuchMethodException.class,
                    () -> Cycle.class.getMethod("setEndDate", CycleDate.class)
            );
        }
    }
}