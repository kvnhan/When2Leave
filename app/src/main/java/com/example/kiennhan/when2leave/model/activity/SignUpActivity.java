package com.example.kiennhan.when2leave.model.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Meetings;

import java.util.ArrayList;
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
    EditText mStreetNumber;
    EditText mStreetName;
    EditText mCity;
    EditText mState;
    EditText mZipCode;
    Button mCreateAccount;
    DataBaseHelper mDB;

    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";

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

                //TODO: Need to check for username/email in the database or server and check if confirm password

                Address homeAddress = new Address("", streetNumber, streetName,zipCode,state,city);
                Account newAccount = new Account(uniqueId, firstName, lastName, userName, emailAddress, password, homeAddress, new ArrayList<Meetings>());
                String hashPassord = newAccount.hashPassword(password);
                mDB = new DataBaseHelper(getApplicationContext());

                boolean accountExist = mDB.checkAccount(getApplicationContext(), userName, password);

                if(password.length() < 7){
                    View focusView1 = null;
                    mPassword.setError(getString(R.string.Password));
                    focusView1 = mPassword;
                    focusView1.requestFocus();
                }else if(!password.equals(confirmPassword)){
                    View focusView1 = null;
                    View focusView2 = null;
                    mPassword.setError(getString(R.string.UsedUsername));
                    focusView1 = mPassword;
                    focusView1.requestFocus();
                    mPasswordConfirm.setError(getString(R.string.UsedUsername));
                    focusView2 = mPasswordConfirm;
                    focusView2.requestFocus();
                }

                if(!accountExist) {
                    mDB.addAddress(getApplicationContext(), homeAddress, newAccount, false, null);
                    mDB.addAccount(getApplicationContext(), newAccount, hashPassord);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY, userName);
                    editor.commit();
                    Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }else{
                    View focusView = null;
                    mUserName.setError(getString(R.string.UsedUsername));
                    focusView = mUserName;
                    focusView.requestFocus();
                }
            }
        });
    }
}