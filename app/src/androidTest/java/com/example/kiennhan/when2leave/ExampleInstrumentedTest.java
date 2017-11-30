package com.example.kiennhan.when2leave;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.kiennhan.when2leave.model.Account;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import database.DataBaseHelper;
import database.DataCursorWrapper;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Account account;
    private DataBaseHelper mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = new DataBaseHelper(context);
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.kiennhan.when2leave", appContext.getPackageName());
    }

    @Test
    public void checkAccountTable() throws Exception {
        // Context of the app under test.
       Account account = new Account("111", "Kien", "Nhan",
               "kvnhan", "kvnhan@wpi.edu", "hello", null, null);
        Context context = InstrumentationRegistry.getTargetContext();
        mDb.addAccount(context, account);

        Account retrievedAccount = mDb.getAccount(context, "111");
        assertTrue(retrievedAccount.getFirstName().equals("Kien"));

    }
}
