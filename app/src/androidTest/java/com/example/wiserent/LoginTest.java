package com.example.wiserent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import android.content.Intent;
//
//import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<Login> activityRule =
            new ActivityScenarioRule<>(Login.class);

    // Test successful login with valid credentials
    @Test
    public void testSuccessfulLogin() {
        // Enter valid email
        onView(withId(R.id.Email)).perform(typeText("Hulk@gmail.com"), ViewActions.closeSoftKeyboard());

        // Enter valid password
        onView(withId(R.id.Password)).perform(typeText("123456"), ViewActions.closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.LoginBtn)).perform(click());

        // Check that one of the buttons in PremissionType activity is displayed (e.g., RenterBtn)
        onView(withId(R.id.RenterBtn))
                .check(matches(withText("בעל דירה")));
    }


    // Test login with invalid credentials
    @Test
    public void testInvalidLogin() {
        // Enter invalid email
        onView(withId(R.id.Email)).perform(typeText("Hulk@gmail.com"), ViewActions.closeSoftKeyboard());

        // Enter invalid password
        onView(withId(R.id.Password)).perform(typeText("wrongpassword"), ViewActions.closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.LoginBtn)).perform(click());

        // Check error message
        onView(withText("שגיאה! שם המשתמש או הסיסמה אינם נכונים."))
                .check(matches(withText("שגיאה! שם המשתמש או הסיסמה אינם נכונים.")));
    }

    // Test login with empty fields
    @Test
    public void testEmptyFields() {
        // Leave email and password empty and click login
        onView(withId(R.id.LoginBtn)).perform(click());

        // Check email field error
        onView(withId(R.id.Email)).check(matches(withText("שדה אימייל ריק.")));

        // Check password field error
        onView(withId(R.id.Password)).check(matches(withText("שדה סיסמא ריק.")));
    }
    @Test
    public void testNavigateToRegister() {
        // Click on the "אין לך משתמש? הרשם כאן!" text to navigate to the Register activity
        onView(withId(R.id.createText)).perform(click());

        // Check if the Register activity is displayed by verifying the presence of the "Register" button
        onView(withId(R.id.RegisterBtn))
                .check(matches(isDisplayed()));
    }


}
