package com.example.kiennhan.when2leave.model.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Date;
import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import database.DataBaseHelper;

import static android.provider.Contacts.SettingsColumns.KEY;

public class CreateEventActivity extends AppCompatActivity {


    EditText mEventName, mEventStreetNum, mEventStreetName, mEventState, mEventCity, mEventZipCode,
            mDefaultStreetName, mDefaultStreetNum, mDefaultCity, mDefaultState, mDefaultZipCode,
            mDescription;
    TextView mTime, mDate;
    Button mCreateEvent;
    String dateOfMeeting;
    String timeOfmeeting;
    DataBaseHelper mDB;

    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String NAME = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mEventName = findViewById(R.id.eventName);
        mEventStreetNum = findViewById(R.id.DESstreetNum);
        mEventStreetName = findViewById(R.id.DESstreetName);
        mEventState  = findViewById(R.id.DESstate);
        mEventCity = findViewById(R.id.DEScity);
        mEventZipCode = findViewById(R.id.DESzipCode);
        mDefaultStreetName  = findViewById(R.id.DEFAULTstreetName);
        mDefaultStreetNum= findViewById(R.id.DEFAULTstreetNum);
        mDefaultCity= findViewById(R.id.DEFAULTcity);
        mDefaultState= findViewById(R.id.DEFAULTstate);
        mDefaultZipCode= findViewById(R.id.DEFAULTzipCode);
        mDescription= findViewById(R.id.description);

        mDate= findViewById(R.id.datePicker);
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        String myFormat = "dd/MMM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        mcurrentDate.set(Calendar.YEAR, selectedyear);
                        mcurrentDate.set(Calendar.MONTH, selectedmonth);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH,  selectedday);
                        mDate.setText(sdf.format(mcurrentDate.getTime()));
                        dateOfMeeting = (sdf.format(mcurrentDate.getTime()));

                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select a date of the event:");
                mDatePicker.show();
            }
        });

        mTime= findViewById(R.id.timePicker);
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTime.setText( selectedHour + ":" + selectedMinute);
                        timeOfmeeting = ( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time of the Event:");
                mTimePicker.show();

            }
        });

        mCreateEvent = findViewById(R.id.createEvent);
        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                String userName = pref.getString(KEY, null);
                mDB = new DataBaseHelper(getApplicationContext());
                Account account = mDB.getAccountWithUserName(getApplicationContext(), userName);

                String meetingID = UUID.randomUUID().toString() + "_" + userName;
                String eventName = mEventName.getText().toString();
                String eventStreetName =  mEventStreetName.getText().toString();
                String eventStreetNum =  mEventStreetNum.getText().toString();
                String eventCity =  mEventCity.getText().toString();
                String eventZipcode =  mEventZipCode.getText().toString();
                String eventState =  mEventState.getText().toString();
                Address destination = new Address(meetingID, eventStreetNum, eventStreetName, eventZipcode, eventState, eventCity);

                String eventStreetName2 =  mDefaultStreetName.getText().toString();
                String eventStreetNum2 =  mDefaultStreetNum.getText().toString();
                String eventCity2 =  mDefaultCity.getText().toString();
                String eventZipcode2 =  mDefaultZipCode.getText().toString();
                String eventState2 =  mDefaultState.getText().toString();
                Address userLocation = new Address(meetingID, eventStreetNum2, eventStreetName2, eventZipcode2, eventState2, eventCity2);

                Meetings meeting = new Meetings(meetingID, eventName, account,timeOfmeeting, dateOfMeeting, userLocation, destination, mDescription.getText().toString());
                mDB.addMeeting(getApplicationContext(), account, meeting, userLocation, destination);
                Toast.makeText(getApplicationContext(), "Meeting Data Added", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateEventActivity.this, WelcomeActivity.class);
                startActivity(intent);

            }
        });
    }

}
