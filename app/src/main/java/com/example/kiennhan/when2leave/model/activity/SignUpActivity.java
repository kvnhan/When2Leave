package com.example.kiennhan.when2leave.model.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.AccountTest;
import com.example.kiennhan.when2leave.model.activity.wrapper.MapWrapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import database.DataBaseHelper;

public class SignUpActivity extends AppCompatActivity {

    EditText mFirstName;
    EditText mLastName;
    EditText mEmailAddress;
    EditText mUserName;
    EditText mPassword;
    EditText mPasswordConfirm;
    EditText mPhoneNumber;
    Button mCreateAccount;
    DataBaseHelper mDB;

    private DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;
    private boolean accountExist = true;
    private SignUpActivity.UserLoginTask mAuthTask = null;
    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String FIRST = "first";
    private static final String FIRSTRUN = "firstrun";
    private static final String ACCOUNT = "account";
    private static final String UID = "uid";
    private static final String ACC_UID = "accuid";
    private static final String PASSWORD_SAFE = "passwordsafe";
    private static final String PW = "peace";
    private static final String INBOX = "inbox";
    private static final String INBOX_ID = "inboxID";

    private static final String USER_NAME_KEY= "USER_NAME_KEY";
    private static final String FIRST_NAME_KEY = "FIRST_NAME_KEY";
    private static final String LAST_NAME_KEY = "LAST_NAME_KEY";
    private static final String EMAIL_KEY = "EMAIL_KEY";
    private static final String PHONE_NUM_KEY = "PHONE_NUM_KEY";

    private boolean passwordValid = true;
    private boolean passwordMatch = true;

    private Boolean listenerCompleted = false;
    private Boolean signUpComplete = false;
    SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mUserName = findViewById(R.id.userName);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.confirmPassword);
        mEmailAddress = findViewById(R.id.emailAddress);
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mCreateAccount = findViewById(R.id.createAccount);

        pref = getApplicationContext().getSharedPreferences(FIRST, MODE_PRIVATE);
        /*
        SharedPreferences spref = getApplicationContext().getSharedPreferences(INBOX, MODE_PRIVATE);
        String uniqueINBOX = null;
        if(spref.getString(INBOX_ID, null) == null){
            uniqueINBOX = UUID.randomUUID().toString();
            spref.edit().putString(INBOX_ID, null).commit();
        }else{
            uniqueINBOX = spref.getString(INBOX_ID, null);
        }
        */
        myRef = FirebaseDatabase.getInstance().getReference(ACCOUNT);

        if(savedInstanceState != null){
            mFirstName.setText(savedInstanceState.getString(FIRST_NAME_KEY));
            mLastName.setText(savedInstanceState.getString(LAST_NAME_KEY));
            mUserName.setText(savedInstanceState.getString(USER_NAME_KEY));
            mEmailAddress.setText(savedInstanceState.getString(EMAIL_KEY));
            mPhoneNumber.setText(savedInstanceState.getString(PHONE_NUM_KEY));
        }
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uniqueId = UUID.randomUUID().toString();
                String streetId = UUID.randomUUID().toString() + uniqueId;
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String userName = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                String confirmPassword = mPasswordConfirm.getText().toString();
                String emailAddress = mEmailAddress.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();

                if(!checkField(firstName,lastName,userName,password,confirmPassword, emailAddress)){
                    return;
                }

                Account newAccount = new Account(uniqueId, firstName, lastName, userName, emailAddress, password);
                String hashPassord = newAccount.hashPassword(password);
                mDB = new DataBaseHelper(getApplicationContext());

                //accountExist = mDB.checkAccount(getApplicationContext(), userName, password);

                if(password.length() < 7){
                    View focusView1 = null;
                    mPassword.setError(getString(R.string.Password));
                    focusView1 = mPassword;
                    focusView1.requestFocus();
                    passwordValid = false;
                }

                if(!password.equals(confirmPassword)){
                    View focusView1 = null;
                    View focusView2 = null;
                    mPassword.setError(getString(R.string.PasswordNotMatch));
                    focusView1 = mPassword;
                    focusView1.requestFocus();
                    mPasswordConfirm.setError(getString(R.string.PasswordNotMatch));
                    focusView2 = mPasswordConfirm;
                    focusView2.requestFocus();
                    passwordMatch = false;
                }
                if(passwordMatch && passwordValid) {
                    attemptLogin(newAccount, userName, emailAddress, hashPassord);
                }

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(USER_NAME_KEY, mUserName.getText().toString());
        savedInstanceState.putString(FIRST_NAME_KEY, mFirstName.getText().toString());
        savedInstanceState.putString(LAST_NAME_KEY, mLastName.getText().toString());
        savedInstanceState.putString(PHONE_NUM_KEY, mPhoneNumber.getText().toString());
        savedInstanceState.putString(EMAIL_KEY, mEmailAddress.getText().toString());

    }

    public boolean checkField(String firstname, String lastname, String username, String password, String cpassword, String email){
        boolean isready = true;
        if(firstname.equals("")){
            View focusView1 = null;
            mFirstName.setError(getString(R.string.error_field_required));
            focusView1 =  mFirstName;
            focusView1.requestFocus();
            isready = false;
        }
        if(lastname.equals("")){
            View focusView1 = null;
            mLastName.setError(getString(R.string.error_field_required));
            focusView1 =  mLastName;
            focusView1.requestFocus();
            isready = false;
        }
        if(username.equals("")){
            View focusView1 = null;
            mUserName.setError(getString(R.string.error_field_required));
            focusView1 =  mUserName;
            focusView1.requestFocus();
            isready = false;
        }
        if(password.equals("")){
            View focusView1 = null;
            mPassword.setError(getString(R.string.error_field_required));
            focusView1 =  mPassword;
            focusView1.requestFocus();
            isready = false;
        }
        if(cpassword.equals("")){
            View focusView1 = null;
            mPasswordConfirm.setError(getString(R.string.error_field_required));
            focusView1 =  mPasswordConfirm;
            focusView1.requestFocus();
            isready = false;
        }
        if(email.equals("")){
            View focusView1 = null;
            mEmailAddress.setError(getString(R.string.error_field_required));
            focusView1 =  mEmailAddress;
            focusView1.requestFocus();
            isready = false;
        }else if(!email.contains("@")){
            View focusView1 = null;
            mEmailAddress.setError(getString(R.string.error_invalid_email));
            focusView1 =  mEmailAddress;
            focusView1.requestFocus();
            isready = false;
        }

        return isready;
    }

    private boolean saveUserInfo(Account account, String Hashpw){
        String id = account.getUid();

        Gson gson = new Gson();
        MapWrapper wrapper = new MapWrapper();
        SharedPreferences mypref = getApplicationContext().getSharedPreferences(PW, MODE_PRIVATE);
        //Get a Serialize HashMap
        String wrapperStr = mypref.getString(PASSWORD_SAFE, null);
        wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
        if(wrapper == null){
            wrapper = new MapWrapper();
            wrapper.setMyMap(new HashMap<String, String>());
        }
        HashMap<String, String> HtKpi = wrapper.getMyMap();
        // Put username and hash password in the hashmap
        HtKpi.put(account.getUserName(), Hashpw);
        // Set the new map
        wrapper.setMyMap(HtKpi);
        String serializedMap = gson.toJson(wrapper);
        final SharedPreferences.Editor editor = mypref.edit();
        editor.putString(PASSWORD_SAFE, serializedMap);
        editor.commit();
        /*
        SharedPreferences pw = getApplicationContext().getSharedPreferences(PW, MODE_PRIVATE);
        final SharedPreferences.Editor edi = pw.edit();
        edi.putString(PASSWORD_SAFE, Hashpw);
        edi.commit();
        */
        account.setUid("");
        account.setPassword("");
        myRef.child(id).setValue(account);
        return true;
    }

    private void getUserData(final String username, final String email, final Account newAccount, final String hashPassord){
        myRef.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    mUserName.setError(null);
                    mEmailAddress.setError(null);
                    for (DataSnapshot child : children) {
                        AccountTest acoount = child.getValue(AccountTest.class);
                        if (acoount.getUserName().equals(username) && acoount.getEmail().equals(email)) {
                            View focusView = null;
                            View focusView2 = null;
                            mUserName.setError(getString(R.string.UsedUsername));
                            mEmailAddress.setError("You have already signed up with this email address");
                            focusView = mUserName;
                            focusView2 = mEmailAddress;
                            focusView.requestFocus();
                            focusView2.requestFocus();
                            listenerCompleted = true;
                            accountExist = true;
                            break;
                        } else if (acoount.getUserName().equals(username)) {
                            View focusView = null;
                            mUserName.setError(getString(R.string.UsedUsername));
                            focusView = mUserName;
                            focusView.requestFocus();
                            listenerCompleted = true;
                            accountExist = true;
                            break;
                        } else if (acoount.getEmail().equals(email)) {
                            View focusView2 = null;
                            mEmailAddress.setError("You have already signed up with this email address");
                            focusView2 = mEmailAddress;
                            focusView2.requestFocus();
                            listenerCompleted = true;
                            accountExist = true;
                            break;
                        } else {
                            listenerCompleted = true;
                            accountExist = false;
                        }
                    }
                }else{
                    listenerCompleted = true;
                    accountExist = false;
                    checkListenerStatus(username, newAccount, hashPassord);
                }
                checkListenerStatus(username, newAccount, hashPassord);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        }));

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private Account acc;
        private String username;
        private String emailAddress;
        private String hashPw;

        public UserLoginTask(Account acc, String username, String emailAddress, String hashPw) {
            this.acc = acc;
            this.username = username;
            this.emailAddress = emailAddress;
            this.hashPw = hashPw;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            getUserData(username, emailAddress, acc , hashPw);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void attemptLogin(Account acc, String username, String emailAddress, String hashPw) {
        if (mAuthTask != null) {
            return;
        }
        mAuthTask = new SignUpActivity.UserLoginTask(acc, username, emailAddress, hashPw);
        mAuthTask.execute((Void) null);
        }

    private void checkListenerStatus(String username, Account acc,String hashPw) {
        if (listenerCompleted) {
            if(!accountExist){
                mDB.addAccount(getApplicationContext(), acc, hashPw);
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref.edit();
                editor.putString(KEY, username);
                editor.commit();
                saveUserInfo(acc, hashPw);
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        }
    }

}
