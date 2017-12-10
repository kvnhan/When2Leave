package com.example.kiennhan.when2leave.model.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.AccountTest;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Meetings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.DataBaseHelper;
import database.FirebaseLogger;

public class SignUpActivity extends AppCompatActivity {

    EditText mFirstName;
    EditText mLastName;
    EditText mEmailAddress;
    EditText mUserName;
    EditText mPassword;
    EditText mPasswordConfirm;
    EditText mPhoneNumber;
    EditText mStreetNumber;
    EditText mStreetName;
    EditText mCity;
    EditText mState;
    EditText mZipCode;
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

    private boolean passwordValid = true;
    private boolean passwordMatch = true;

    private Boolean listenerCompleted = false;
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
        mStreetName = findViewById(R.id.streetName);
        mStreetNumber = findViewById(R.id.streetNum);
        mCity = findViewById(R.id.city);
        mState =  findViewById(R.id.state);
        mZipCode = findViewById(R.id.zipCode);
        mCreateAccount = findViewById(R.id.createAccount);

        pref = getApplicationContext().getSharedPreferences(FIRST, MODE_PRIVATE);


        myRef = FirebaseDatabase.getInstance().getReference(ACCOUNT);

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
                String streetName = mStreetName.getText().toString();
                String streetNumber = mStreetNumber.getText().toString();
                String city = mCity.getText().toString();
                String state = mState.getText().toString();
                String zipCode = mZipCode.getText().toString();

                if(!checkField(firstName,lastName,userName,password,confirmPassword, emailAddress)){
                    return;
                }

                Address homeAddress = new Address("", streetNumber, streetName,zipCode,state,city);
                Account newAccount = new Account(uniqueId, firstName, lastName, userName, emailAddress, password, homeAddress);
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

                if(pref.getBoolean(FIRSTRUN, true)){
                    pref.edit().putBoolean(FIRSTRUN, false).commit();
                    mDB.addAddress(getApplicationContext(), homeAddress, newAccount, false, null);
                    mDB.addAccount(getApplicationContext(), newAccount, hashPassord);
                    SharedPreferences mypref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = mypref.edit();
                    editor.putString(KEY, userName);
                    editor.commit();
                    saveUserInfo(newAccount, hashPassord);
                    Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
                attemptLogin(newAccount, userName, emailAddress, hashPassord, homeAddress);

            }
        });
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
        }

        return isready;
    }

    private boolean saveUserInfo(Account account, String Hashpw){
        SharedPreferences mypref = getApplicationContext().getSharedPreferences(UID, MODE_PRIVATE);
        final SharedPreferences.Editor editor = mypref.edit();
        editor.putString(ACC_UID, account.getUid());
        editor.commit();
        SharedPreferences pw = getApplicationContext().getSharedPreferences(PW, MODE_PRIVATE);
        final SharedPreferences.Editor edi = pw.edit();
        edi.putString(PASSWORD_SAFE, Hashpw);
        edi.commit();
        account.setUid("");
        account.setPassword("");
        myRef.child(account.getUid()).push().setValue(account);
        return true;
    }

    private void getUserData(final String username, final String email, final Account newAccount, final Address homeAddress, final String hashPassord){
        myRef.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                        checkListenerStatus(username, newAccount, homeAddress, hashPassord);
                        break;
                    } else if (acoount.getUserName().equals(username)) {
                        View focusView = null;
                        mUserName.setError(getString(R.string.UsedUsername));
                        focusView = mUserName;
                        focusView.requestFocus();
                        listenerCompleted = true;
                        accountExist = true;
                        checkListenerStatus(username, newAccount, homeAddress, hashPassord);
                        break;
                    } else if (acoount.getEmail().equals(email)) {
                        View focusView2 = null;
                        mEmailAddress.setError("You have already signed up with this email address");
                        focusView2 = mEmailAddress;
                        focusView2.requestFocus();
                        listenerCompleted = true;
                        accountExist = true;
                        checkListenerStatus(username, newAccount, homeAddress, hashPassord);
                        break;
                    }else {
                        listenerCompleted = true;
                        accountExist = false;
                        checkListenerStatus(username, newAccount, homeAddress, hashPassord);
                        break;
                    }
                }
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
        private Address addr;

        public UserLoginTask(Account acc, String username, String emailAddress, String hashPw, Address addr) {
            this.acc = acc;
            this.username = username;
            this.emailAddress = emailAddress;
            this.hashPw = hashPw;
            this.addr = addr;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            getUserData(username, emailAddress, acc ,addr , hashPw);

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

    private void attemptLogin(Account acc, String username, String emailAddress, String hashPw, Address addr) {
        if (mAuthTask != null) {
            return;
        }
        mAuthTask = new SignUpActivity.UserLoginTask(acc, username, emailAddress, hashPw, addr);
        mAuthTask.execute((Void) null);
        }

    private void checkListenerStatus(String username, Account acc, Address addr, String hashPw) {
        if (listenerCompleted) {
            if(!accountExist){
                if (passwordValid && passwordMatch) {
                    mDB.addAddress(getApplicationContext(), addr, acc, false, null);
                    mDB.addAccount(getApplicationContext(), acc, hashPw);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY, username);
                    editor.commit();
                    saveUserInfo(acc, hashPw);
                    Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

}
