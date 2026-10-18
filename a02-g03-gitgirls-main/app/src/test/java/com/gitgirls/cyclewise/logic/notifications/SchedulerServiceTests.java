package com.gitgirls.cyclewise.logic.notifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.persistence.NotificationSettingsPersistence;
import com.gitgirls.cyclewise.presentation.notification.NotificationMapper;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;

import java.time.LocalTime;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
// This fixes the "Unnecessary stubbings detected" error by being more flexible
@MockitoSettings(strictness = Strictness.LENIENT)
public class SchedulerServiceTests {

    @Mock
    private NotificationSettingsPersistence persistence;
    @Mock
    private NotificationMapper mapper;
    @Mock
    private PeriodCalculator periodCalculator;
    @Mock
    private CycleDate mockCycleDate;

    private SchedulerService schedulerService;
    private NotificationSettings settings;
    private LocalDate today;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        settings = new NotificationSettings(2, true, LocalTime.of(9, 0));
        schedulerService = new SchedulerService(persistence, mapper);
    }

    @Test
    @DisplayName("getSettings should delegate to persistence")
    void testGetSettings() {
        when(persistence.getSettings(username)).thenReturn(settings);
        assertEquals(settings, schedulerService.getSettings(username));
    }

    @Test
    @DisplayName("execute should successfully schedule when settings are valid")
    void execute_shouldScheduleCorrectly() throws Exception {
        // 1. Use the mockCycleDate you already defined at the top of the class
        // or ensure the real one has the date set.
        CycleDate nextCycle = new CycleDate(today);

        // 2. Stub the persistence call (The service NEEDS this to progress)
        when(persistence.getSettings(username)).thenReturn(settings);

        // 3. Stub the periodCalculator (The internal NotificationScheduler uses this)
        when(periodCalculator.calculateNextCycleStart()).thenReturn(nextCycle);

        // 4. Run the method
        schedulerService.execute(username, nextCycle, periodCalculator);

        // 5. Verify the mapper was called with the exact date from our 'today' variable
        verify(mapper).scheduleNotification(
                any(),         // notificationDate (calculated internally)
                any(),         // reminderTime
                eq(today),     // nextCycle.getDate() <--- THIS must match today
                anyInt()       // daysBefore
        );
    }
    @Test
    @DisplayName("saveAndSchedule should return false when persistence fails")
    void saveAndSchedule_shouldReturnFalseWhenSaveFails() {
        when(persistence.save(any(), anyString())).thenReturn(false);
        boolean result = schedulerService.saveAndSchedule(username, settings, mockCycleDate, periodCalculator);
        assertFalse(result);
    }

    @Test
    @DisplayName("saveAndSchedule should cancel when settings are disabled")
    void saveAndSchedule_shouldCancelWhenDisabled() {
        NotificationSettings disabled = new NotificationSettings(2, false, LocalTime.NOON);
        when(persistence.save(disabled, username)).thenReturn(true);

        boolean result = schedulerService.saveAndSchedule(username, disabled, mockCycleDate, periodCalculator);

        assertTrue(result);
        verify(mapper).cancelNotification();
    }

    // --- COVERAGE FIXES ---

    @Test
    void testCatchInvalidDate() {
        when(persistence.save(any(), anyString())).thenReturn(true);
        assertThrows(IllegalStateException.class, () ->
                schedulerService.saveAndSchedule(username, settings, null, periodCalculator)
        );
    }

    @Test
    void testCatchGenericException() {
        when(persistence.save(any(), anyString())).thenReturn(true);
        when(persistence.getSettings(username)).thenThrow(new RuntimeException("Unexpected"));
        assertThrows(IllegalStateException.class, () ->
                schedulerService.saveAndSchedule(username, settings, mockCycleDate, periodCalculator)
        );
    }

    @Test
    void testCatchIllegalArgument() {
        when(persistence.save(any(), anyString())).thenReturn(true);
        when(persistence.getSettings(username)).thenThrow(new IllegalArgumentException("Illegal"));
        assertThrows(IllegalStateException.class, () ->
                schedulerService.saveAndSchedule(username, settings, mockCycleDate, periodCalculator)
        );
    }

}