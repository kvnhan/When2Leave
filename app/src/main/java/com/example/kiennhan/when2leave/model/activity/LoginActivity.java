package com.example.kiennhan.when2leave.model.activity;

import android.*;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.AccountTest;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Password;
import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.wrapper.MapWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import database.DataBaseHelper;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    //Id to identity permission request.
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_LOCATION_FINE = 1;
    private static final int REQUEST_LOCATION_COARSE = 2;

    private static final String CURR_LOCATION = "current";
    private static final String SAVE_LOCATION = "saveloc";
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;


    private boolean accExist = false;
    private TextView mRegister;
    private String email;
    private String password;

    //Keys for Sharepreference
    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String UID = "uid";
    private static final String ACC_UID = "accuid";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD_SAFE = "passwordsafe";
    private static final String PW = "peace";
    private static final String USER_NAME_SAVE = "passwordsafe";
    private static final String PASSWORD_SAVE = "passwordsafe";

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    private DataBaseHelper mDb;
    private boolean accountExist = true;
    private DatabaseReference myRef;
    private boolean connected = false;

    private Boolean listenerCompleted = false;
    private FusedLocationProviderClient mFusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        //Make reference to firebase
        myRef = FirebaseDatabase.getInstance().getReference(ACCOUNT);

        mDb = new DataBaseHelper(getApplicationContext());

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mRegister = findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null){
            email = savedInstanceState.getString(USER_NAME_SAVE);
            mEmailView.setText(email);
        }

        //Check for connection to the firebase
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    listenerCompleted = true;
                    System.out.println("connected");
                } else {
                    listenerCompleted = true;
                    System.out.println("not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(USER_NAME_SAVE, mEmailView.getText().toString());

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == 108){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        mDb = new DataBaseHelper(getApplicationContext());
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString().trim();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegister.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    mRegister.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Local Database Account Authentication
            // Store values at the time of the login attempt.
            final String email = mEmailView.getText().toString();
            final String password = mPasswordView.getText().toString();
            final Password hp= new Password();

            //Check if the application establish a connection to the firebase reference
            if(connected) {
                //Check the username/email against firebase
                myRef.addListenerForSingleValueEvent((new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            AccountTest acoount = child.getValue(AccountTest.class);
                            if (acoount.getUserName().equals(email) || acoount.getEmail().equals(email)) {
                                Gson gson = new Gson();
                                MapWrapper wrapper = new MapWrapper();
                                SharedPreferences pref = getApplicationContext().getSharedPreferences(PW, MODE_PRIVATE);
                                String wrapperStr = pref.getString(PASSWORD_SAFE, null);
                                wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
                                HashMap<String, String> HtKpi = wrapper.getMyMap();
                                String hashP = HtKpi.get(acoount.getUserName());
                                //String hashP = pref.getString(PASSWORD_SAFE, null);
                                if (hp.checkPassword(password, hashP)) {
                                    listenerCompleted = true;
                                    accountExist = true;
                                    break;
                                }
                            } else {
                                listenerCompleted = true;
                                accountExist = false;
                            }
                        }
                        checkListenerStatus(email);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Listener was cancelled");
                    }

                }));
            }else {
                //Authenticate with when2leave.db
                Boolean accExist = mDb.checkAccount(getApplicationContext(), email, password);
                String username = mDb.getUsername(email, getApplicationContext());
                Gson gson = new Gson();
                MapWrapper wrapper = new MapWrapper();
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PW, MODE_PRIVATE);
                String wrapperStr = pref.getString(PASSWORD_SAFE, null);
                wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
                HashMap<String, String> HtKpi = wrapper.getMyMap();
                if(accExist){
                    String hashP = HtKpi.get(username);
                    //Check if password is the same
                    if (hp.checkPassword(password, hashP)) {
                        start(email);
                    }
                }

                startDialogBox();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);}

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }

    /**
     * Check if the firebase reference listener is complete before moving on
     * @param username
     */
    private void checkListenerStatus(String username) {
        if (listenerCompleted) {
            if(accountExist){
                start(username);
            }else{
                startDialogBox();
            }
        }
    }

    /**
     * Start an intent to WelcomeActivity
     * @param username
     */
    public void start(String username){
        DataBaseHelper mDB = new DataBaseHelper(getApplicationContext());

        //Get user id
        String uid = mDB.getUUID(username, getApplicationContext());

        //Store current logged in user id
        SharedPreferences mypref = getApplicationContext().getSharedPreferences(UID, MODE_PRIVATE);
        final SharedPreferences.Editor edi = mypref.edit();
        edi.putString(ACC_UID, uid);
        edi.commit();

        //Store currently logged in username
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        String user = mDB.getUsername(username, getApplicationContext());
        editor.putString(KEY, user);
        editor.commit();

        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }

    /**
     * Start Alert Dialog Box
     */
    public void startDialogBox(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
        builder1.setMessage("You have enter an incorrect username or password");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Sign Up",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "Try Again ?",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}

