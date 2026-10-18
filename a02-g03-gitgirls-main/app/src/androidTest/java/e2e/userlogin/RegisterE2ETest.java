package e2e.userlogin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static org.junit.Assert.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.presentation.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterE2ETest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityRule =
            new ActivityScenarioRule<>(RegisterActivity.class);
    @Test
    public void testSuccessfulRegistration() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01/01/2000"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        // Wait for activity to finish after successful registration
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        // NEW - checks lifecycle state directly, no need to enter the destroyed activity
        assertEquals(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }
    // ❌ SAD PATH — empty first name
    // strings.xml: register_first_name_required = "First name is required"
    @Test
    public void testRegisterWithEmptyFirstName() {
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01/01/2000"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.firstNameEditText))
                .check(matches(hasErrorText("First name is required")));
    }

    // ❌ SAD PATH — empty last name
    // strings.xml: register_last_name_required = "Last name is required"
    @Test
    public void testRegisterWithEmptyLastName() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01/01/2000"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.lastNameEditText))
                .check(matches(hasErrorText("Last name is required")));
    }

    // ❌ SAD PATH — empty username
    // strings.xml: register_username_required = "Username is required"
    @Test
    public void testRegisterWithEmptyUsername() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01/01/2000"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.usernameEditText))
                .check(matches(hasErrorText("Username is required")));
    }

    // ❌ SAD PATH — empty birthdate
    // strings.xml: register_birthdate_required = "Birthdate is required"
    @Test
    public void testRegisterWithEmptyBirthdate() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.birthdateEditText))
                .check(matches(hasErrorText("Birthdate is required")));
    }

    // ❌ SAD PATH — empty password
    // strings.xml: register_password_required = "Password is required"
    @Test
    public void testRegisterWithEmptyPassword() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01/01/2000"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.passwordEditText))
                .check(matches(hasErrorText("Password is required")));
    }

    // ❌ SAD PATH — wrong date format
    // RegisterActivity sets error via getString(R.string.register_birthdate_required)
    // BUT AuthValidator returns AuthMessages.BIRTHDATE_INVALID for bad format
    // The activity catches this via Toast NOT setError — so we just verify we stay on screen
    @Test
    public void testRegisterWithWrongDateFormat() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01.01.2000"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        // Activity stays open — register button still visible
        onView(withId(R.id.registerButton))
                .check(matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
    }

    // ❌ SAD PATH — user is too young
    // Same as above — AuthValidator returns USER_TOO_YOUNG via Toast, not setError
    @Test
    public void testRegisterWithAgeTooYoung() {
        onView(withId(R.id.firstNameEditText))
                .perform(typeText("Jane"), closeSoftKeyboard());
        onView(withId(R.id.lastNameEditText))
                .perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText))
                .perform(typeText("janedoe123"), closeSoftKeyboard());
        onView(withId(R.id.birthdateEditText))
                .perform(typeText("01/01/2026"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("securepass"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        // Activity stays open — register button still visible
        onView(withId(R.id.registerButton))
                .check(matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
    }
}
