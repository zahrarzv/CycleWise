package e2e.phases;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.Flow;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Symptoms;
import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.presentation.PredictionActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PhasesDashboardE2ETest {

    private static final String TAG = "PhasesDashboardE2ETest";
    private PeriodTracker app;

    @Before
    public void setUp() {
        initApp();
        unlockDeviceAndDisableAnimations();

        // Clear all existing period logs before each test
        clearAllPeriodLogs();

        // Ensure the user is logged in for the test
        app.setLoggedInUser(new User("testuser", "password123", "Test", "User", "01/01/2000"));
    }

    @After
    public void tearDown() {
        // Clean up after each test to prevent test pollution
        clearAllPeriodLogs();
    }

    private void initApp() {
        app = (PeriodTracker) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();

        AuthService authService = app.getAuthService();

        try {
            // Register the test user if they don't exist
            authService.register("testuser", "password123", "Test", "User", "01/01/2000");
        } catch (Exception e) {
            Log.i(TAG, "User already exists");
        }
    }

    private void clearAllPeriodLogs() {
        try {
            List<PeriodLog> allLogs = app.getPeriodLogService().getAllLogs();
            for (PeriodLog log : allLogs) {
                app.getPeriodLogService().deleteLog(log.getID());
            }
            Log.i(TAG, "Cleared " + allLogs.size() + " period logs");
        } catch (Exception e) {
            Log.w(TAG, "Failed to clear period logs", e);
        }
    }

    private void unlockDeviceAndDisableAnimations() {
        try {
            UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            uiDevice.wakeUp();

            // Disable animations to prevent RootViewWithoutFocusException and long-press bugs
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("settings put global window_animation_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("settings put global transition_animation_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("settings put global animator_duration_scale 0");

            // Dismiss keyguard
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("wm dismiss-keyguard");
        } catch (Exception e) {
            Log.w(TAG, "Robustness commands failed", e);
        }
    }

    private void injectPeriodLog(LocalDate start, LocalDate end) {
        PeriodLog log = new PeriodLog(
                PeriodLog.UNASSIGNED_ID,
                new CycleDate(start),
                end == null ? null : new CycleDate(end),
                Flow.MEDIUM,
                new Symptoms(false, false, false, false, false, false, false, false, false, false, false, false),
                "Dashboard test log"
        );
        app.getPeriodLogService().addLog(log);
    }

    @Test
    public void testNoDataState() {
        // No logs injected - should show no data state
        try (ActivityScenario<PredictionActivity> scenario = ActivityScenario.launch(PredictionActivity.class)) {
            onView(withId(R.id.phaseTitleText)).check(matches(withText(R.string.prediction_no_phase)));
            onView(withId(R.id.nextPeriodCardText)).check(matches(withText(R.string.prediction_no_data)));
        }
    }

    @Test
    public void testMenstruationPhaseDashboard() {
        // Log an ongoing period starting today
        injectPeriodLog(LocalDate.now(), null);

        try (ActivityScenario<PredictionActivity> scenario = ActivityScenario.launch(PredictionActivity.class)) {
            onView(withId(R.id.phaseTitleText)).check(matches(withText(R.string.phase_menstruation)));
            onView(withId(R.id.tipOneText)).check(matches(withText(R.string.tip_menstruation_1)));
            onView(withId(R.id.cycleDayText)).check(matches(withText(containsString("1"))));
        }
    }

    @Test
    public void testOvulationPhaseDashboard() {
        // Set up for ovulation phase: period ended 9 days ago, lasting 5 days
        // This puts the user around day 14 of their cycle (ovulation)
        LocalDate end = LocalDate.now().minusDays(9);
        LocalDate start = end.minusDays(5);
        injectPeriodLog(start, end);

        try (ActivityScenario<PredictionActivity> scenario = ActivityScenario.launch(PredictionActivity.class)) {
            onView(withId(R.id.phaseTitleText)).check(matches(withText(R.string.phase_ovulation)));
            onView(withId(R.id.tipOneText)).check(matches(withText(R.string.tip_ovulation_1)));
        }
    }

    @Test
    public void testLutealPhaseDashboard() {
        // Set up for luteal phase: period ended 15 days ago
        LocalDate end = LocalDate.now().minusDays(15);
        LocalDate start = end.minusDays(5);
        injectPeriodLog(start, end);

        try (ActivityScenario<PredictionActivity> scenario = ActivityScenario.launch(PredictionActivity.class)) {
            onView(withId(R.id.phaseTitleText)).check(matches(withText(R.string.phase_luteal)));
            onView(withId(R.id.tipOneText)).check(matches(withText(R.string.tip_luteal_1)));
        }
    }

    @Test
    public void testPremenstrualPhaseDashboard() {
        // Set up for premenstrual phase: period ended 22 days ago
        LocalDate end = LocalDate.now().minusDays(22);
        LocalDate start = end.minusDays(5);
        injectPeriodLog(start, end);

        try (ActivityScenario<PredictionActivity> scenario = ActivityScenario.launch(PredictionActivity.class)) {
            onView(withId(R.id.phaseTitleText)).check(matches(withText(R.string.phase_premenstrual)));
            onView(withId(R.id.tipOneText)).check(matches(withText(R.string.tip_premenstrual_1)));
        }
    }
}