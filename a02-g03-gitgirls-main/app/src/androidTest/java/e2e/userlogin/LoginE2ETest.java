package e2e.userlogin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.platform.app.InstrumentationRegistry;
import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.presentation.LoginActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginE2ETest {

    // This launches the LoginActivity fresh before every test
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);


    @Before
    public void setUp() {
        // Register a test user before each test
        // so login tests always have valid credentials to work with
        AuthService authService = ((PeriodTracker)
                InstrumentationRegistry.getInstrumentation()
                        .getTargetContext().getApplicationContext())
                .getAuthService();

        try {
            authService.register("Testuser", "password123", "Testtest", "Testtest", "03/10/2003");
        } catch (Exception e) {
            // User might already exist — that's fine, ignore it
        }
    }

    @Test
    public void testSuccessfulLogin() {
        // Type a valid username
        onView(withId(R.id.usernameEditText))
                .perform(typeText("Testuser"), closeSoftKeyboard());

        // Type a valid password
        onView(withId(R.id.passwordEditText))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Tap the Login button
        onView(withId(R.id.loginButton))
                .perform(click());

        // After login → PeriodLogActivity should open
        // We verify something visible on that screen is shown
        onView(withId(R.id.saveLogButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithEmptyUsername() {
        // Leave username empty, type only password
        onView(withId(R.id.passwordEditText))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Tap Login
        onView(withId(R.id.loginButton))
                .perform(click());

        // Username field should show an error
        onView(withId(R.id.usernameEditText))
                .check(matches(hasErrorText("Username is required")));
    }

    @Test
    public void testLoginWithEmptyPassword() {
        // Type username but leave password empty
        onView(withId(R.id.usernameEditText))
                .perform(typeText("testuser"), closeSoftKeyboard());

        // Tap Login
        onView(withId(R.id.loginButton))
                .perform(click());

        // Password field should show an error
        onView(withId(R.id.passwordEditText))
                .check(matches(hasErrorText("Password is required")));
    }

    @Test
    public void testLoginWithWrongPassword() {
        // Type valid username but wrong password
        onView(withId(R.id.usernameEditText))
                .perform(typeText("testuser"), closeSoftKeyboard());

        onView(withId(R.id.passwordEditText))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());

        // Tap Login
        onView(withId(R.id.loginButton))
                .perform(click());

        // Should stay on Login screen — login button still visible
        onView(withId(R.id.loginButton))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testLoginWithBothFieldsEmpty() {
        // Tap Login without typing anything
        onView(withId(R.id.loginButton))
                .perform(click());

        // Username field should show error first (it's checked first in your code)
        onView(withId(R.id.usernameEditText))
                .check(matches(hasErrorText("Username is required")));
    }
}
