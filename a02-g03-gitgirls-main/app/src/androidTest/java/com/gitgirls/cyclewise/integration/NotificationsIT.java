package com.gitgirls.cyclewise.integration;

import static org.junit.Assert.*;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.gitgirls.cyclewise.exceptions.InvalidCycleDateException;
import com.gitgirls.cyclewise.exceptions.NotEnabledException;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleHistory;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.logic.notifications.NotificationScheduler;
import com.gitgirls.cyclewise.logic.notifications.NotificationValidator;
import com.gitgirls.cyclewise.logic.notifications.SchedulerConstants;
import com.gitgirls.cyclewise.logic.notifications.SchedulerService;
import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.persistence.NotificationSettingsPersistence;
import com.gitgirls.cyclewise.presentation.notification.NotificationHelper;
import com.gitgirls.cyclewise.presentation.notification.NotificationMapper;
import com.gitgirls.cyclewise.presentation.notification.NotificationReceiver;

import androidx.test.rule.GrantPermissionRule;
import org.junit.Rule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class NotificationsIT {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);
    private Context context;
    private NotificationManager notificationManager;

    // -----------------------------------------------------------------------
    // Minimal in-memory persistence — stands in for the real DB-backed one
    // -----------------------------------------------------------------------
    static class inMemoryNotificationPersistence implements NotificationSettingsPersistence {
        private final Map<String, NotificationSettings> store = new HashMap<>();

        @Override
        public NotificationSettings getSettings(String username) {
            return store.get(username);
        }

        @Override
        public boolean save(NotificationSettings settings, String username) {
            if (settings == null || username == null) return false;
            store.put(username, settings);
            return true;
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Returns a PeriodCalculator whose next-cycle start is `daysFromNow` days away. */
    private PeriodCalculator calculatorWithNextCycle(int daysFromNow) {
        LocalDate lastStart   = LocalDate.now().minusDays(28);
        LocalDate lastEnd     = lastStart.plusDays(5);
        LocalDate secondStart = lastStart.minusDays(28);
        LocalDate secondEnd   = secondStart.plusDays(5);

        List<Cycle> cycles = new ArrayList<>();
        cycles.add(new Cycle(new CycleDate(secondStart), new CycleDate(secondEnd)));  // ← wrap in CycleDate
        cycles.add(new Cycle(new CycleDate(lastStart),   new CycleDate(lastEnd)));    // ← wrap in CycleDate

        CycleHistory history = new CycleHistory(cycles);
        return new PeriodCalculator(history) {
            @Override
            public CycleDate calculateNextCycleStart() {
                return new CycleDate(LocalDate.now().plusDays(daysFromNow));
            }
        };
    }

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        notificationManager = context.getSystemService(NotificationManager.class);
        NotificationHelper.createChannel(context);
        notificationManager.cancelAll();
    }

    @After
    public void tearDown() {
        notificationManager.cancelAll();
    }

    // -----------------------------------------------------------------------
    // NotificationScheduler + NotificationValidator integration
    // -----------------------------------------------------------------------

    @Test
    public void testSchedulerReturnsCorrectNotificationDate() {
        int daysUntilCycle  = 7;
        int daysBeforeReminder = 3;
        LocalDate expectedNotifDate = LocalDate.now().plusDays(daysUntilCycle - daysBeforeReminder);

        NotificationSettings settings = new NotificationSettings(
                daysBeforeReminder, true, LocalTime.of(9, 0)
        );
        NotificationValidator validator = new NotificationValidator();
        PeriodCalculator calculator = calculatorWithNextCycle(daysUntilCycle);

        NotificationScheduler scheduler = new NotificationScheduler(calculator, settings, validator);
        LocalDate notifDate = scheduler.getNotificationDate();

        assertEquals("Notification date should be daysBeforeReminder before next cycle",
                expectedNotifDate, notifDate);
    }

    @Test
    public void testSchedulerThrowsWhenNotificationsDisabled() {
        NotificationSettings settings = new NotificationSettings(3, false, LocalTime.of(9, 0));
        NotificationValidator validator = new NotificationValidator();
        PeriodCalculator calculator = calculatorWithNextCycle(10);

        NotificationScheduler scheduler = new NotificationScheduler(calculator, settings, validator);

        try {
            scheduler.getNotificationDate();
            fail("Expected NotEnabledException");
        } catch (NotEnabledException e) {
            assertEquals(SchedulerConstants.NOTI_NOT_ENABLED, e.getMessage());
        }
    }

    @Test
    public void testSchedulerConstructorRejectsNullArguments() {
        NotificationValidator validator = new NotificationValidator();
        NotificationSettings settings  = new NotificationSettings(3, true, LocalTime.of(9, 0));
        PeriodCalculator calculator    = calculatorWithNextCycle(7);

        try {
            new NotificationScheduler(null, settings, validator);
            fail("Expected IllegalArgumentException for null calculator");
        } catch (IllegalArgumentException e) {
            assertEquals(SchedulerConstants.NULL_PERIOD_CALC, e.getMessage());
        }

        try {
            new NotificationScheduler(calculator, null, validator);
            fail("Expected IllegalArgumentException for null settings");
        } catch (IllegalArgumentException e) {
            assertEquals(SchedulerConstants.NULL_NOTI_SETTING, e.getMessage());
        }

        try {
            new NotificationScheduler(calculator, settings, null);
            fail("Expected IllegalArgumentException for null validator");
        } catch (IllegalArgumentException e) {
            assertEquals(SchedulerConstants.NULL_NOTI_VALIDATOR, e.getMessage());
        }
    }

    @Test
    public void testValidatorThrowsOnNullCycleDate() {
        NotificationValidator validator = new NotificationValidator();
        try {
            validator.validateDate(null);
            fail("Expected InvalidCycleDateException for null CycleDate");
        } catch (InvalidCycleDateException e) {
            assertEquals(SchedulerConstants.INVALID_DATES, e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // SchedulerService + InMemory Persistence integration
    // -----------------------------------------------------------------------

    @Test
    public void testSaveAndScheduleWithEnabledSettingsPersistsCorrectly() {
        inMemoryNotificationPersistence persistence = new inMemoryNotificationPersistence();
        NotificationMapper mapper = new NotificationMapper(context);
        SchedulerService service  = new SchedulerService(persistence, mapper);

        NotificationSettings settings = new NotificationSettings(3, true, LocalTime.of(8, 0));
        PeriodCalculator calculator   = calculatorWithNextCycle(10);

        boolean result = service.saveAndSchedule("user1", settings, new CycleDate(LocalDate.now().plusDays(10)), calculator);

        assertTrue("saveAndSchedule should return true on success", result);
        NotificationSettings saved = service.getSettings("user1");
        assertNotNull("Saved settings should be retrievable", saved);
        assertTrue("Saved settings should be enabled", saved.isEnabled());
        assertEquals("Days before reminder should match", 3, saved.getDaysBeforeReminder());
    }

    @Test
    public void testSaveAndScheduleWithDisabledSettingsCancelsAlarm() {
        inMemoryNotificationPersistence persistence = new inMemoryNotificationPersistence();
        NotificationMapper mapper = new NotificationMapper(context);
        SchedulerService service  = new SchedulerService(persistence, mapper);

        // First enable
        NotificationSettings enabled = new NotificationSettings(3, true, LocalTime.of(8, 0));
        service.saveAndSchedule("user2", enabled, new CycleDate(LocalDate.now().plusDays(10)), calculatorWithNextCycle(10));

        // Then disable — should cancel without throwing
        NotificationSettings disabled = new NotificationSettings(3, false, LocalTime.of(8, 0));
        boolean result = service.saveAndSchedule("user2", disabled, new CycleDate(LocalDate.now().plusDays(10)), calculatorWithNextCycle(10));

        assertTrue("saveAndSchedule should still return true when disabling", result);
        assertFalse("Persisted settings should now be disabled",
                service.getSettings("user2").isEnabled());
    }

    @Test
    public void testSaveAndScheduleWithNoCycleDataThrowsIllegalState() {
        inMemoryNotificationPersistence persistence = new inMemoryNotificationPersistence();
        NotificationMapper mapper = new NotificationMapper(context);
        SchedulerService service  = new SchedulerService(persistence, mapper);

        NotificationSettings settings = new NotificationSettings(3, true, LocalTime.of(8, 0));

        try {
            service.saveAndSchedule("user3", settings, null, calculatorWithNextCycle(7));
            fail("Expected IllegalStateException when cycle date is null");
        } catch (IllegalStateException e) {
            // Expected — service throws when scheduling fails with invalid cycle date
        }

        // Settings should still have been persisted before the exception was thrown
        assertNotNull("Settings should be saved even if scheduling fails",
                service.getSettings("user3"));
    }

    @Test
    public void testGetSettingsReturnsNullWhenNoneExist() {
        inMemoryNotificationPersistence persistence = new inMemoryNotificationPersistence();
        SchedulerService service = new SchedulerService(persistence, new NotificationMapper(context));

        assertNull("Should return null for user with no saved settings",
                service.getSettings("unknown_user"));
    }

    @Test
    public void testSettingsPersistAcrossServiceInstances() {
        inMemoryNotificationPersistence persistence = new inMemoryNotificationPersistence();
        SchedulerService firstService = new SchedulerService(persistence, new NotificationMapper(context));

        NotificationSettings settings = new NotificationSettings(5, true, LocalTime.of(10, 0));
        firstService.saveAndSchedule("user4", settings, new CycleDate(LocalDate.now().plusDays(10)), calculatorWithNextCycle(10));

        // New service instance sharing the same persistence
        SchedulerService secondService = new SchedulerService(persistence, new NotificationMapper(context));
        NotificationSettings retrieved  = secondService.getSettings("user4");

        assertNotNull("Settings should survive across service instances", retrieved);
        assertEquals("Days before reminder should be preserved", 5, retrieved.getDaysBeforeReminder());
    }

    // -----------------------------------------------------------------------
    // NotificationReceiver integration (real BroadcastReceiver + real helper)
    // -----------------------------------------------------------------------

    @Test
    public void testReceiverSendsNotificationWhenPeriodDueToday() throws InterruptedException{
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_PREDICTED_DATE, LocalDate.now().toString());
        intent.putExtra(NotificationReceiver.EXTRA_DAYS_BEFORE_REMINDER, 3);

        NotificationReceiver receiver = new NotificationReceiver();
        receiver.onReceive(context, intent);

        Thread.sleep(500);

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        boolean found = false;
        for (StatusBarNotification sbn : active) {
            if (sbn.getId() == SchedulerConstants.NOTIF_ID) {
                found = true;
                break;
            }
        }
        assertTrue("A notification should be posted when period is due today", found);
    }

    @Test
    public void testReceiverSendsLateNotificationWhenPeriodOverdue() throws InterruptedException {
        LocalDate overdueDate = LocalDate.now().minusDays(3);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_PREDICTED_DATE, overdueDate.toString());
        intent.putExtra(NotificationReceiver.EXTRA_DAYS_BEFORE_REMINDER, 3);

        NotificationReceiver receiver = new NotificationReceiver();
        receiver.onReceive(context, intent);

        Thread.sleep(500); // give NotificationManager time to post

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        boolean found = false;
        for (StatusBarNotification sbn : active) {
            if (sbn.getId() == SchedulerConstants.NOTIF_ID) {
                found = true;
                break;
            }
        }
        assertTrue("A late notification should be posted when period is overdue", found);
    }

    @Test
    public void testReceiverDoesNotSendNotificationWhenTooEarly() {
        // Period is 30 days away and reminder is only 3 days before — too early
        LocalDate farFutureDate = LocalDate.now().plusDays(30);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_PREDICTED_DATE, farFutureDate.toString());
        intent.putExtra(NotificationReceiver.EXTRA_DAYS_BEFORE_REMINDER, 3);

        NotificationReceiver receiver = new NotificationReceiver();
        receiver.onReceive(context, intent);

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        for (StatusBarNotification sbn : active) {
            assertNotEquals("No notification should be posted when it is too early",
                    SchedulerConstants.NOTIF_ID, sbn.getId());
        }
    }

    @Test
    public void testReceiverDoesNotSendNotificationWhenTooLate() {
        // Period is MAX_DAYS_LATE + 1 days overdue
        LocalDate veryOverdueDate = LocalDate.now().minusDays(SchedulerConstants.MAX_DAYS_LATE + 1);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_PREDICTED_DATE, veryOverdueDate.toString());
        intent.putExtra(NotificationReceiver.EXTRA_DAYS_BEFORE_REMINDER, 3);

        NotificationReceiver receiver = new NotificationReceiver();
        receiver.onReceive(context, intent);

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        for (StatusBarNotification sbn : active) {
            assertNotEquals("No notification should be posted when period is too far overdue",
                    SchedulerConstants.NOTIF_ID, sbn.getId());
        }
    }

    @Test
    public void testReceiverHandlesMissingPredictedDateGracefully() {
        // No EXTRA_PREDICTED_DATE in intent — receiver should return silently
        Intent intent = new Intent(context, NotificationReceiver.class);

        NotificationReceiver receiver = new NotificationReceiver();
        try {
            receiver.onReceive(context, intent); // must not throw
        } catch (Exception e) {
            fail("Receiver should handle a missing predicted date without crashing: " + e.getMessage());
        }

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        for (StatusBarNotification sbn : active) {
            assertNotEquals("No notification should be posted when predicted date is missing",
                    SchedulerConstants.NOTIF_ID, sbn.getId());
        }
    }

    // -----------------------------------------------------------------------
    // NotificationHelper integration
    // -----------------------------------------------------------------------

    @Test
    public void testNotificationChannelIsCreated() {
        NotificationHelper.createChannel(context);
        assertNotNull("Notification channel should exist after creation",
                notificationManager.getNotificationChannel(SchedulerConstants.CHANNEL_ID));
    }

    @Test
    public void testHelperSendsReminderNotification() throws InterruptedException {
        NotificationHelper helper = new NotificationHelper(context);
        helper.sendPeriodReminder(2, false);

        Thread.sleep(500);

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        boolean found = false;
        for (StatusBarNotification sbn : active) {
            if (sbn.getId() == SchedulerConstants.NOTIF_ID) {
                found = true;
                break;
            }
        }
        assertTrue("A reminder notification should be posted", found);
    }

    @Test
    public void testHelperSendsLateNotification() throws InterruptedException {
        NotificationHelper helper = new NotificationHelper(context);
        helper.sendPeriodReminder(4, true);

        Thread.sleep(500);

        StatusBarNotification[] active = notificationManager.getActiveNotifications();
        boolean found = false;
        for (StatusBarNotification sbn : active) {
            if (sbn.getId() == SchedulerConstants.NOTIF_ID) {
                found = true;
                break;
            }
        }
        assertTrue("A late notification should be posted", found);
    }
}