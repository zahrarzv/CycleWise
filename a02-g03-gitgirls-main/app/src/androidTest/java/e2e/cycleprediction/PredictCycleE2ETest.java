package e2e.cycleprediction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.*;
import com.gitgirls.cyclewise.presentation.PredictionActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PredictCycleE2ETest {

    private static final String TAG = "PredictCycleE2ETest";

    private PeriodLogService periodLogService;
    private PeriodTracker app;
    private ActivityScenario<PredictionActivity> scenario;
    private CountingIdlingResource dbIdlingResource;

    private final Symptoms noSymptoms = new Symptoms(
            false, false, false, false, false,
            false, false, false, false, false, false, false
    );

    @Before
    public void setUp() throws Exception {
        dbIdlingResource = new CountingIdlingResource("DatabaseOperations");
        IdlingRegistry.getInstance().register(dbIdlingResource);

        // Force disable all animations on the emulator (critical for API 36)
        disableAnimations();

        // Unlock the emulator completely
        UiDevice uiDevice = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        uiDevice.wakeUp();
        Thread.sleep(500);

        // Multiple methods to ensure keyguard is dismissed
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("wm dismiss-keyguard");

        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("input keyevent KEYCODE_MENU");

        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("input keyevent KEYCODE_BACK");

        Thread.sleep(1000);

        app = (PeriodTracker)
                InstrumentationRegistry.getInstrumentation()
                        .getTargetContext().getApplicationContext();

        AuthService authService = app.getAuthService();

        try {
            authService.register("testuser", "password123", "Test", "User", "01/01/2000");
        } catch (Exception e) {
            Log.i(TAG, "User already exists, proceeding with login");
        }

        authService.login("testuser", "password123");

        User loggedInUser = authService.getUserByUsername("testuser");
        app.setLoggedInUser(loggedInUser);

        periodLogService = app.getPeriodLogService();
        clearAllLogs();

        launchActivity();

        // CRITICAL: Wait for window focus after launch
        waitForWindowFocus();
    }

    @After
    public void tearDown() {
        if (dbIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(dbIdlingResource);
        }
        clearAllLogs();
        if (scenario != null) {
            scenario.close();
        }
        if (app != null) {
            app.setLoggedInUser(null);
        }
    }

    private void disableAnimations() {
        try {
            InstrumentationRegistry.getInstrumentation()
                    .getUiAutomation()
                    .executeShellCommand("settings put global window_animation_scale 0");
            InstrumentationRegistry.getInstrumentation()
                    .getUiAutomation()
                    .executeShellCommand("settings put global transition_animation_scale 0");
            InstrumentationRegistry.getInstrumentation()
                    .getUiAutomation()
                    .executeShellCommand("settings put global animator_duration_scale 0");
            Log.i(TAG, "Animations disabled");
        } catch (Exception e) {
            Log.w(TAG, "Failed to disable animations", e);
        }
    }

    private void launchActivity() {
        if (scenario != null) {
            scenario.close();
        }

        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                PredictionActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            );
            activity.getWindow().getDecorView().requestFocus();
        });
    }

    private void waitForWindowFocus() {
        long startTime = System.currentTimeMillis();
        long timeout = 5000;
        boolean hasFocus = false;

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                final boolean[] focusResult = new boolean[1];
                scenario.onActivity(activity -> {
                    focusResult[0] = activity.hasWindowFocus();
                });
                hasFocus = focusResult[0];
                if (hasFocus) {
                    Log.i(TAG, "Window focus acquired");
                    Thread.sleep(500);
                    break;
                }
            } catch (Exception e) {
                Log.w(TAG, "Error checking window focus", e);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }

        if (!hasFocus) {
            Log.w(TAG, "Window focus not acquired within timeout, attempting force focus");
            try {
                InstrumentationRegistry.getInstrumentation()
                        .getUiAutomation()
                        .executeShellCommand("input keyevent KEYCODE_APP_SWITCH");
                Thread.sleep(300);
                InstrumentationRegistry.getInstrumentation()
                        .getUiAutomation()
                        .executeShellCommand("input keyevent KEYCODE_APP_SWITCH");
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e(TAG, "Failed to force focus", e);
            }
        }
    }

    private void clearAllLogs() {
        List<PeriodLog> logs = periodLogService.getAllLogs();
        for (PeriodLog log : logs) {
            periodLogService.deleteLog(log.getID());
        }
    }

    private void addTestLog(LocalDate start, LocalDate end) {
        dbIdlingResource.increment();
        try {
            periodLogService.addLog(new PeriodLog(
                    PeriodLog.UNASSIGNED_ID,
                    new CycleDate(start),
                    new CycleDate(end),
                    Flow.LIGHT,
                    noSymptoms,
                    ""
            ));
        } catch (Exception e) {
            Log.e(TAG, "Failed to add test log: " + start + " to " + end, e);
            throw new RuntimeException("Test setup failed: could not add period log", e);
        } finally {
            dbIdlingResource.decrement();
        }
    }

    private void recreateAndWait() {
        scenario.recreate();
        waitForWindowFocus();
    }

    // HAPPY PATH - prediction shown after logging 2 cycles
    @Test
    public void testPredictionShownAfterTwoCycles() {
        addTestLog(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 6));
        addTestLog(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 6));

        assertEquals("Should have 2 logs", 2, periodLogService.getAllLogs().size());

        recreateAndWait();

        // Check that next period card shows a date containing 2026
        // The actual format from toString() is "2026-02-01" or similar
        onView(withId(R.id.nextPeriodCardText)).check(matches(isDisplayed()));
        onView(withId(R.id.nextPeriodCardText))
                .check(matches(withText(containsString("2026"))));
    }

    // HAPPY PATH - key views are always visible
    @Test
    public void testPredictionTextIsAlwaysVisible() {
        waitForWindowFocus();

        onView(withId(R.id.phaseTitleText)).check(matches(isDisplayed()));
        onView(withId(R.id.cycleDayText)).check(matches(isDisplayed()));
        onView(withId(R.id.nextPeriodCardText)).check(matches(isDisplayed()));
        onView(withId(R.id.avgCycleCardText)).check(matches(isDisplayed()));
    }

    // EDGE CASE - no logs shows "no data" message
    @Test
    public void testPredictionWithNoLogsShowsNoDataMessage() {
        assertEquals("Database should be empty", 0, periodLogService.getAllLogs().size());

        recreateAndWait();

        // With no logs, showNoDataState() sets nextPeriodCardText to "No data"
        onView(withId(R.id.nextPeriodCardText))
                .check(matches(withText(R.string.prediction_no_data)));
    }

    // EDGE CASE - only 1 completed log shows prediction UI (not "current cycle")
    // Based on actual app behavior: with 1 cycle, cycles.size() = 1,
    // goes to showPredictedCycleState() which shows predicted data
    @Test
    public void testPredictionWithOnlyOneCompletedLog() {
        addTestLog(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 6));

        List<PeriodLog> logs = periodLogService.getAllLogs();
        assertEquals("Should have 1 log", 1, logs.size());

        PeriodLog log = logs.get(0);
        assertEquals("Log start should be 2026-03-01",
                LocalDate.of(2026, 3, 1), log.getStartDate().getDate());
        assertEquals("Log end should be 2026-03-06",
                LocalDate.of(2026, 3, 6), log.getEndDate().getDate());

        recreateAndWait();

        // With 1 completed cycle, the app shows predicted data (contains a date)
        // NOT "current cycle" message
        onView(withId(R.id.nextPeriodCardText)).check(matches(isDisplayed()));
        onView(withId(R.id.nextPeriodCardText))
                .check(matches(withText(containsString("2026"))));
    }
}