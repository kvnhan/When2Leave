package com.example.kiennhan.when2leave;

/**
 * Created by Kien on 12/12/2017.
 */
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.kiennhan.when2leave.model.activity.LoginActivity;
import com.example.kiennhan.when2leave.model.activity.R;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;


@RunWith(AndroidJUnit4.class)
@LargeTest

public class LoginActivityTest {
    private String mEmail;
    private String vaidUser;
    private String mPassword;
    private String validPassword;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        mEmail = "Espresso";
        mPassword = "Hello";
        vaidUser = "pole";
        validPassword = "kkkkkkkk";
    }

    @Test
    public void Login_Test() {
        // Type text and then press the button.
        onView(withId(R.id.email))
                .perform(typeText(vaidUser), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(validPassword), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.email))
                .check(matches(withText(vaidUser)));
        onView(withId(R.id.password))
                .check(matches(withText(validPassword)));

        onView(withId(R.id.email_sign_in_button)).perform(click());
        intended(allOf(
                hasComponent(hasShortClassName(".WelcomeActivity")),
                toPackage("com.example.kiennhan.when2leave.model.activity")));

    }

    @Test
    public void Register_Test() {
        onView(withId(R.id.register)).perform(click());
        intended(allOf(
                hasComponent(hasShortClassName(".SignUpActivity")),
                toPackage("com.example.kiennhan.when2leave.model.activity"), hasExtra(LoginActivity.EXTRA_MESSAGE, MESSAGE)));

    }
}
