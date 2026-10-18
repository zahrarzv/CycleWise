package e2e.navigation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.presentation.PredictionActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationE2ETest {

    private static final String TAG = "NavigationE2ETest";

    private UiDevice uiDevice;
    private PeriodTracker app;

    @Before
    public void setUp() {
        initApp();

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        unlockDeviceAndDisableAnimations();
        grantNotificationPermission();

        // Ensure a user is logged in for navigation tests
        app.setLoggedInUser(new User("testuser", "Password123", "Test", "User", "01/01/2000"));
    }

    private void initApp() {
        app = (PeriodTracker) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();

        AuthService authService = app.getAuthService();

        try {
            authService.register("testuser", "Password123", "Test", "User", "01/01/2000");
        } catch (Exception e) {
            Log.i(TAG, "User already exists");
        }
    }

    private void unlockDeviceAndDisableAnimations() {
        try {
            uiDevice.wakeUp();
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("settings put global window_animation_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("settings put global transition_animation_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("settings put global animator_duration_scale 0");
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand("wm dismiss-keyguard");
        } catch (Exception e) {
            Log.w(TAG, "Robustness commands failed", e);
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
            Log.d(TAG, "Notification permission granted via shell");
        } catch (Exception e) {
            Log.w(TAG, "Failed to grant notification permission", e);
        }
    }

    @Test
    public void testFullNavigationCycle() {
        try (ActivityScenario<PredictionActivity> scenario =
                     ActivityScenario.launch(PredictionActivity.class)) {

            // Verify we start on the Predict screen
            onView(withId(R.id.phaseTitleText)).check(matches(isDisplayed()));

            // 1. Navigate to Log Period and verify it loaded
            onView(withId(R.id.nav_log)).perform(click());
            onView(withId(R.id.title_log_period)).check(matches(isDisplayed()));

            // 2. Navigate to History and verify it loaded
            onView(withId(R.id.nav_history)).perform(click());
            onView(withId(R.id.savedLogsList)).check(matches(isDisplayed()));

            // 3. Navigate to Notification Settings and verify it loaded
            onView(withId(R.id.nav_settings)).perform(click());
            onView(withId(R.id.switchEnabled)).check(matches(isDisplayed()));

            // 4. Navigate back to Predict and verify it loaded
            onView(withId(R.id.nav_predict)).perform(click());
            onView(withId(R.id.phaseTitleText)).check(matches(isDisplayed()));

            // 5. Navigate to Log Period again to confirm nav is reusable
            onView(withId(R.id.nav_log)).perform(click());
            onView(withId(R.id.title_log_period)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackNavigationConsistency() {
        try (ActivityScenario<PredictionActivity> scenario =
                     ActivityScenario.launch(PredictionActivity.class)) {
            onView(withId(R.id.nav_log)).perform(click());
            onView(withId(R.id.nav_history)).perform(click());
            onView(withId(R.id.nav_predict)).perform(click());

            onView(withId(R.id.phaseTitleText)).check(matches(isDisplayed()));
            onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()));
        }
    }
}