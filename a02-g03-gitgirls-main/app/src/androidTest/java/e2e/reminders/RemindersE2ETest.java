package e2e.reminders;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.Flow;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Symptoms;
import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.presentation.notification.NotificationSettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RemindersE2ETest {

    private static final String TAG = "RemindersE2ETest";
    private static final String TEST_DB = "e2e_reminders_test.db";
    private PeriodTracker app;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.POST_NOTIFICATIONS
    );

    @Before
    public void setUp() {
        // 1. Get app instance
        app = (PeriodTracker) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();

        // 2. Wipe and point to isolated test DB — must happen before any DB operation
        app.getApplicationContext().deleteDatabase(TEST_DB);
        app.setTestDatabase(TEST_DB);

        // 3. Register user into the test DB
        try {
            app.getAuthService().register("testuser", "Password123", "Test", "User", "01/01/2000");
        } catch (Exception e) {
            Log.i(TAG, "User already exists");
        }

        // 4. Set logged in user and seed a period log
        app.setLoggedInUser(new User("testuser", "Password123", "Test", "User", "01/01/2000"));
        addTestPeriodLog(app);
    }

    private void addTestPeriodLog(PeriodTracker app) {
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(25);

        PeriodLog log = new PeriodLog(
                PeriodLog.UNASSIGNED_ID,
                new CycleDate(start),
                new CycleDate(end),
                Flow.MEDIUM,
                new Symptoms(false, false, false, false, false, false, false, false, false, false, false, false),
                "Test log for reminders"
        );

        try {
            app.getPeriodLogService().addLog(log);
        } catch (Exception e) {
            Log.i(TAG, "Test log might already exist");
        }
    }

    @Test
    public void testToggleAndSaveReminders() {
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).perform(click());
            onView(withId(R.id.editDaysBefore)).perform(clearText(), typeText("3"), closeSoftKeyboard());
            onView(withId(R.id.timePicker)).perform(PickerActions.setTime(8, 30));
            onView(withId(R.id.btnSave)).perform(click());
        }

        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).check(matches(isChecked()));
            onView(withId(R.id.editDaysBefore)).check(matches(withText("3")));
        }
    }

    @Test
    public void testDisableRemindersPersistence() {
        // Enable and save
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).perform(click());
            onView(withId(R.id.editDaysBefore)).perform(replaceText("2"), closeSoftKeyboard());
            onView(withId(R.id.btnSave)).perform(click());
        }

        // Disable and save
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).check(matches(isChecked()));
            onView(withId(R.id.switchEnabled)).perform(click());
            onView(withId(R.id.switchEnabled)).check(matches(isNotChecked()));
            onView(withId(R.id.btnSave)).perform(click());
        }

        // Verify disabled persisted
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).check(matches(isNotChecked()));
        }
    }

    @Test
    public void testValidationEmptyDays() {
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.editDaysBefore)).perform(clearText(), closeSoftKeyboard());
            onView(withId(R.id.btnSave)).perform(click());
            // Activity stays open because validation failed
            onView(withId(R.id.editDaysBefore)).check(matches(withText("")));
            onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testZeroDaysBefore() {
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).perform(click());
            onView(withId(R.id.editDaysBefore)).perform(replaceText("0"), closeSoftKeyboard());
            onView(withId(R.id.btnSave)).perform(click());
        }

        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.editDaysBefore)).check(matches(withText("0")));
        }
    }

    @Test
    public void testMultipleUpdates() {
        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.switchEnabled)).perform(click());
            onView(withId(R.id.editDaysBefore)).perform(replaceText("5"), closeSoftKeyboard());
            onView(withId(R.id.btnSave)).perform(click());
        }

        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.editDaysBefore)).perform(replaceText("1"), closeSoftKeyboard());
            onView(withId(R.id.btnSave)).perform(click());
        }

        try (ActivityScenario<NotificationSettingsActivity> scenario =
                     ActivityScenario.launch(NotificationSettingsActivity.class)) {
            onView(withId(R.id.editDaysBefore)).check(matches(withText("1")));
        }
    }
}