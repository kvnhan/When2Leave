package com.example.kiennhan.when2leave;

/**
 * Created by Kien Nhan on 12/12/2017.
 */

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.kiennhan.when2leave.model.activity.LoginActivity;
import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.SignUpActivity;
import com.example.kiennhan.when2leave.model.activity.WelcomeActivity;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest

public class RegisterActivtyTest {

    private String firstName, lastName, Username, password, confirmPassword, email, phone;

    @Rule
    public IntentsTestRule<SignUpActivity> mActivityRule = new IntentsTestRule<SignUpActivity>(
            SignUpActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
       firstName = "Kien";
       lastName = "Nhan";
       Username = "Cool";
       password = "kkkkkkkk";
       confirmPassword = "kkkkkkkk";
       email = "cool@yahoo.com";
       phone = "8888888888";
    }

    @Test
    public void SignUp_Field_Test() {
        // Type text and then press the button.
        onView(withId(R.id.firstName))
                .perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastName))
                .perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.userName))
                .perform(typeText(Username), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.confirmPassword))
                .perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.emailAddress))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.phoneNumber))
                .perform(typeText(phone), closeSoftKeyboard());


        onView(withId(R.id.firstName))
                .check(matches(withText(firstName)));
        onView(withId(R.id.lastName))
                .check(matches(withText(lastName)));
        onView(withId(R.id.userName))
                .check(matches(withText(Username)));
        onView(withId(R.id.password))
                .check(matches(withText(password)));
        onView(withId(R.id.confirmPassword))
                .check(matches(withText(confirmPassword)));
        onView(withId(R.id.emailAddress))
                .check(matches(withText(email)));
        onView(withId(R.id.phoneNumber))
                .check(matches(withText(phone)));


    }

}
