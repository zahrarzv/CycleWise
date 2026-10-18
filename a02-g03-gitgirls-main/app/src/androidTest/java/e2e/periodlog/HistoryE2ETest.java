package e2e.periodlog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.*;
import com.gitgirls.cyclewise.presentation.SavedLogsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class HistoryE2ETest {

    private PeriodLogService periodLogService;
    private PeriodTracker app;
    private ActivityScenario<SavedLogsActivity> scenario;

    private final Symptoms noSymptoms = new Symptoms(
            false, false, false, false, false,
            false, false, false, false, false, false, false
    );

    @Before
    public void setUp() {
        app = (PeriodTracker)
                InstrumentationRegistry.getInstrumentation()
                        .getTargetContext().getApplicationContext();

        AuthService authService = app.getAuthService();

        try {
            authService.register("testuser", "password123", "Test", "User", "01/01/2000");
        } catch (Exception e) {
            // Already exists, fine
        }

        authService.login("testuser", "password123");

        User loggedInUser = authService.getUserByUsername("testuser");
        app.setLoggedInUser(loggedInUser);

        periodLogService = app.getPeriodLogService();
        clearAllLogs();

        launchActivity();
    }

    @After
    public void tearDown() {
        clearAllLogs();
        if (scenario != null) {
            scenario.close();
        }
        if (app != null) {
            app.setLoggedInUser(null);
        }
    }

    private void launchActivity() {
        if (scenario != null) {
            scenario.close();
        }
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                SavedLogsActivity.class);
        scenario = ActivityScenario.launch(intent);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearAllLogs() {
        try {
            List<PeriodLog> logs = periodLogService.getAllLogs();
            for (PeriodLog log : logs) {
                periodLogService.deleteLog(log.getID());
            }
        } catch (Exception e) {
            // Nothing to clear
        }
    }

    private void addTestLog(LocalDate start, LocalDate end) {
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
            // ignore
        }
    }

    // ✅ HAPPY PATH — saved log appears in history
    @Test
    public void testSavedLogAppearsInHistory() {
        addTestLog(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 6));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Database should have 1 log",
                1, periodLogService.getAllLogs().size());

        scenario.recreate();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.savedLogsList)).check(matches(isDisplayed()));

        onView(withId(R.id.emptyText))
                .check(matches(
                        androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(
                                androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE)));
    }

    // ✅ HAPPY PATH — delete a log
    @Test
    public void testDeleteLog() {
        addTestLog(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 6));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scenario.recreate();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Should have 1 log before delete",
                1, periodLogService.getAllLogs().size());

        // Click Delete button
        onView(withText(R.string.delete_button)).perform(click());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Confirm deletion
        onView(withText(R.string.delete_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Database should be empty after delete",
                0, periodLogService.getAllLogs().size());

        scenario.recreate();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.emptyText)).check(matches(isDisplayed()));
    }

    // ✅ HAPPY PATH — sort earliest to latest
    @Test
    public void testSortEarliestToLatest() {
        // Add 2 logs in reverse order
        addTestLog(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 6));
        addTestLog(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 6));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scenario.recreate();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Should have 2 logs", 2, periodLogService.getAllLogs().size());

        // Click sort button
        onView(withId(R.id.sortButton)).perform(click());

        // Wait for popup menu to appear
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click on sort option using the EXACT text from the menu
        onView(withText("Earliest to latest")).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify the list is still displayed
        onView(withId(R.id.savedLogsList)).check(matches(isDisplayed()));
    }

    // ✅ HAPPY PATH — sort latest to earliest
    @Test
    public void testSortLatestToEarliest() {
        addTestLog(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 6));
        addTestLog(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 6));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scenario.recreate();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click sort button
        onView(withId(R.id.sortButton)).perform(click());

        // Wait for popup menu to appear
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click on sort option using the EXACT text from the menu
        onView(withText("Latest to earliest")).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.savedLogsList)).check(matches(isDisplayed()));
    }

    // ❌ EDGE CASE — empty history shows empty state text
    @Test
    public void testEmptyHistoryShowsEmptyText() {
        assertEquals("Database should be empty", 0, periodLogService.getAllLogs().size());

        scenario.recreate();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.emptyText)).check(matches(isDisplayed()));
        onView(withId(R.id.sortButton)).check(matches(isDisplayed()));
    }
}
