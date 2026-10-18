package com.gitgirls.cyclewise.models;

import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CycleDateTest {

    @Nested
    @DisplayName("CycleDate Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create CycleDate with valid date")
        void shouldCreateWithValidDate() {
            LocalDate date = LocalDate.of(2024, 1, 1);
            CycleDate cycleDate = new CycleDate(date);

            assertEquals(date, cycleDate.getDate());
        }

        @Test
        @DisplayName("Should throw exception when date is null")
        void shouldThrowWhenDateNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> new CycleDate(null),
                    "Date cannot be null");
        }
    }

    @Nested
    @DisplayName("CycleDate Operations Tests")
    class OperationTests {

        private CycleDate jan1;
        private CycleDate jan15;

        @BeforeEach
        void setUp() {
            jan1 = new CycleDate(LocalDate.of(2024, 1, 1));
            jan15 = new CycleDate(LocalDate.of(2024, 1, 15));
        }

        @Test
        @DisplayName("Should add days correctly")
        void shouldAddDays() {
            CycleDate result = jan1.plusDays(14);
            assertEquals(LocalDate.of(2024, 1, 15), result.getDate());
        }

        @Test
        @DisplayName("Should subtract days correctly")
        void shouldSubtractDays() {
            CycleDate result = jan15.minusDays(14);
            assertEquals(LocalDate.of(2024, 1, 1), result.getDate());
        }

        @Test
        @DisplayName("Should handle month boundaries when adding days")
        void shouldHandleMonthBoundariesWhenAdding() {
            CycleDate jan31 = new CycleDate(LocalDate.of(2024, 1, 31));
            CycleDate result = jan31.plusDays(1);
            assertEquals(LocalDate.of(2024, 2, 1), result.getDate());
        }

        @Test
        @DisplayName("Should handle year boundaries when adding days")
        void shouldHandleYearBoundariesWhenAdding() {
            CycleDate dec31 = new CycleDate(LocalDate.of(2024, 12, 31));
            CycleDate result = dec31.plusDays(1);
            assertEquals(LocalDate.of(2025, 1, 1), result.getDate());
        }

        @Test
        @DisplayName("Should handle negative days when adding")
        void shouldHandleNegativeDaysWhenAdding() {
            CycleDate result = jan15.plusDays(-14);
            assertEquals(LocalDate.of(2024, 1, 1), result.getDate());
        }

        @Test
        @DisplayName("Should handle negative days when subtracting")
        void shouldHandleNegativeDaysWhenSubtracting() {
            CycleDate result = jan1.minusDays(-14);
            assertEquals(LocalDate.of(2024, 1, 15), result.getDate());
        }

        @Test
        @DisplayName("Should compare dates correctly with isBefore")
        void shouldCompareDates() {
            assertTrue(jan1.isBefore(jan15));
            assertFalse(jan15.isBefore(jan1));
            assertFalse(jan1.isBefore(jan1));
        }

        @Test
        @DisplayName("Should return string representation")
        void shouldReturnStringRepresentation() {
            assertEquals("2024-01-01", jan1.toString());
        }
    }
}