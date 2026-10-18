package e2e.periodlog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.Flow;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.presentation.PeriodLogActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class LogPeriodE2ETest {

    private static final String TAG = "LogPeriodE2ETest";

    private PeriodLogService periodLogService;
    private PeriodTracker app;
    private User loggedInUser;
    private ActivityScenario<PeriodLogActivity> scenario;
    private UiDevice uiDevice;

    private void initApp() {
        app = (PeriodTracker)
                InstrumentationRegistry.getInstrumentation()
                        .getTargetContext().getApplicationContext();

        AuthService authService = app.getAuthService();

        try {
            authService.register("testuser", "password123", "Test", "User", "01/01/2000");
        } catch (Exception e) {
            Log.i(TAG, "User already exists");
        }

        authService.login("testuser", "password123");
        loggedInUser = authService.getUserByUsername("testuser");
        app.setLoggedInUser(loggedInUser);

        periodLogService = app.getPeriodLogService();
    }

    private void clearAllLogs() {
        try {
            List<PeriodLog> logs = periodLogService.getAllLogs();
            for (PeriodLog log : logs) {
                periodLogService.deleteLog(log.getID());
            }
        } catch (Exception e) {
            Log.w(TAG, "Nothing to clear");
        }
    }

    private void launchActivity() {
        if (scenario != null) {
            scenario.close();
        }
        scenario = ActivityScenario.launch(PeriodLogActivity.class);
    }

    private void waitForSave() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void grantNotificationPermission() {
        try {
            String pkg = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext().getPackageName();
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand(
                            "pm grant " + pkg + " android.permission.POST_NOTIFICATIONS"
                    );
        } catch (Exception e) {
            Log.w(TAG, "Failed to grant notification permission", e);
        }
    }

    private void disableAnimations() {
        try {
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("settings put global window_animation_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("settings put global transition_animation_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("settings put global animator_duration_scale 0");
        } catch (Exception e) {
            Log.w(TAG, "Failed to disable animations", e);
        }
    }

    @Before
    public void setUp() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            uiDevice.wakeUp();
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("wm dismiss-keyguard");
        } catch (Exception e) {
            Log.w(TAG, "Wake/unlock failed", e);
        }
        disableAnimations();
        grantNotificationPermission();
        initApp();
        clearAllLogs();
        launchActivity();
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
        if (periodLogService != null) {
            clearAllLogs();
        }
        if (app != null) {
            app.setLoggedInUser(null);
        }
    }

    @Test
    public void testLogPeriodWithStartDateOnly() {
        int countBefore = periodLogService.getAllLogs().size();

        onView(withId(R.id.startDateTextView)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.noEndDateCheck)).perform(scrollTo(), click());
        onView(withId(R.id.flowLight)).perform(scrollTo(), click());
        onView(withId(R.id.saveLogButton)).perform(scrollTo(), click());

        waitForSave();

        periodLogService = app.getPeriodLogService();
        assertEquals("A new log should have been saved",
                countBefore + 1, periodLogService.getAllLogs().size());

        // Scroll back to top before asserting title is visible
        onView(withId(R.id.title_log_period)).perform(scrollTo());
        onView(withId(R.id.title_log_period)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogPeriodWithAllFields() {
        int countBefore = periodLogService.getAllLogs().size();

        onView(withId(R.id.startDateTextView)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.endDateTextView)).perform(scrollTo(), click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.flowLight)).perform(scrollTo(), click());

        // Use replaceText instead of typeText to bypass soft keyboard IME issues
        onView(withId(R.id.notesInput))
                .perform(scrollTo(), click(),
                        replaceText("Feeling tired today"),
                        closeSoftKeyboard());

        onView(withId(R.id.saveLogButton)).perform(scrollTo(), click());

        waitForSave();

        periodLogService = app.getPeriodLogService();
        int countAfter = periodLogService.getAllLogs().size();
        assertEquals("A new log should have been saved", countBefore + 1, countAfter);

        String savedNotes = periodLogService.getAllLogs()
                .get(periodLogService.getAllLogs().size() - 1)
                .getNotes();
        assertEquals("Notes should be saved", "Feeling tired today", savedNotes);
    }

    @Test
    public void testLogPeriodWithHeavyFlow() {
        int countBefore = periodLogService.getAllLogs().size();

        onView(withId(R.id.startDateTextView)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.flowHeavy)).perform(scrollTo(), click());
        onView(withId(R.id.saveLogButton)).perform(scrollTo(), click());

        waitForSave();

        periodLogService = app.getPeriodLogService();
        assertEquals("A new log should have been saved",
                countBefore + 1, periodLogService.getAllLogs().size());

        Flow savedFlow = periodLogService.getAllLogs()
                .get(periodLogService.getAllLogs().size() - 1)
                .getFlow();
        assertEquals("Flow should be HEAVY", Flow.HEAVY, savedFlow);
    }

    @Test
    public void testLogPeriodWithNoDateSelected() {
        int countBefore = periodLogService.getAllLogs().size();

        onView(withId(R.id.saveLogButton)).perform(scrollTo(), click());

        waitForSave();

        periodLogService = app.getPeriodLogService();
        assertEquals("No log should be saved when no date selected",
                countBefore, periodLogService.getAllLogs().size());

        // Scroll back to top before asserting title is visible
        onView(withId(R.id.title_log_period)).perform(scrollTo());
        onView(withId(R.id.title_log_period)).check(matches(isDisplayed()));
    }
}