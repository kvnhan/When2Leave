package com.example.kiennhan.when2leave.model.activity;

import android.*;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import database.DataBaseHelper;

import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

public class CreateEventActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    EditText mEventName,
            mDescription;
    TextView mTime, mDate, mLocation;
    Button mCreateEvent;
    String dateOfMeeting;
    String timeOfmeeting;
    DataBaseHelper mDB;
    String event_Location = "";
    String eventName, meetingID;

    private DatabaseReference myRef;
    private static final String ACCOUNT = "account";


    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String NAME = "username";
    private static final String UID = "uid";
    private static final String ACC_UID = "accuid";
    int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;

    private static final String EVENT_NAME = "EVENT_NAME";
    private static final String DES = "DES";
    private static final String TIME_KEY = "TIME_KEY";
    private static final String DATE_KEY = "DATE_KEY";
    private static final String LOCATION = "LOCATION";

    private static final String EVENTNAME = "eventname";
    private static final String LOCATIN = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";
    private static final String MEETING_ID = "meetingid";


    private static final String MY_PREF = "myPref";
    private static final String TWO_CLICK = "twoclick";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EVENT_NAME, mEventName.getText().toString());
        savedInstanceState.putString(DES, mDescription.getText().toString());
        savedInstanceState.putString(TIME_KEY, mTime.getText().toString());
        savedInstanceState.putString(DATE_KEY, mDate.getText().toString());
        savedInstanceState.putString(LOCATION, mLocation.getText().toString());

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mDB = new DataBaseHelper(getApplicationContext());

        mEventName = findViewById(R.id.eventName);

        mLocation = findViewById(R.id.event_location);

        myRef = FirebaseDatabase.getInstance().getReference(ACCOUNT);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(CreateEventActivity.this)
                    .addOnConnectionFailedListener(CreateEventActivity.this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }



        final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        mDescription = findViewById(R.id.description);
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(builder.build(CreateEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        mDate= findViewById(R.id.datePicker);
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH) + 1;
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        String year = String.valueOf(selectedyear);
                        String day = String.valueOf(selectedday);
                        String month = String.valueOf(selectedmonth + 1);
                        if(selectedday < 10){
                            day = "0" + selectedday;
                        }
                        mDate.setText(month + "/" + day + "/" + year);
                        dateOfMeeting = (month + "/" + day + "/" + year);

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
                        if (selectedMinute < 10) {
                            mTime.setText(selectedHour + ":0" + selectedMinute);
                            timeOfmeeting = ( selectedHour + ":" + selectedMinute);
                        } else {
                            mTime.setText(selectedHour + ":" + selectedMinute);
                            timeOfmeeting = ( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time of the Event:");
                mTimePicker.show();

            }
        });

        String id = "";

        mCreateEvent = findViewById(R.id.createEvent);
        final Intent intent = getIntent();
        if(intent.getIntExtra(TWO_CLICK, 0) == 1){
            mCreateEvent.setText("Update Event");
            eventName = intent.getStringExtra(EVENTNAME);
            event_Location = intent.getStringExtra(LOCATIN);
            timeOfmeeting = intent.getStringExtra(TIME);
            dateOfMeeting = intent.getStringExtra(DATE);
            String desc = intent.getStringExtra(DESC);
            id = intent.getStringExtra(MEETING_ID);

            mEventName.setText(eventName);
            mLocation.setText(event_Location);
            mTime.setText(timeOfmeeting);
            mDate.setText(dateOfMeeting);
            mDescription.setText(desc);

        }
        final String finalId = id;
        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                String userName = pref.getString(KEY, null);
                Account account = mDB.getAccountWithUserName(getApplicationContext(), userName);

                meetingID = UUID.randomUUID().toString() + "_" + userName;
                eventName = mEventName.getText().toString();
                boolean isReady = checkField(eventName);
                Meetings meeting;
                if(isReady) {
                    if(intent.getIntExtra(TWO_CLICK, 0) == 1){
                        meeting = new Meetings(finalId, eventName, account, timeOfmeeting, dateOfMeeting, "", event_Location, mDescription.getText().toString(), false);
                        mDB.updateEvent(getApplicationContext(), meeting);
                        Intent intent = new Intent(CreateEventActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }else {
                        meeting = new Meetings(meetingID, eventName, account, timeOfmeeting, dateOfMeeting, "", event_Location, mDescription.getText().toString(), false);
                        mDB.addMeeting(getApplicationContext(), account, meeting);
                        Toast.makeText(getApplicationContext(), "Meeting Data Added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateEventActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }

                    saveMeetingInfo(account, meeting);

                }

            }
        });

        if(savedInstanceState != null){
            mEventName.setText(savedInstanceState.getString(EVENT_NAME));
            mDescription.setText(savedInstanceState.getString(DES));
            mTime.setText(savedInstanceState.getString(TIME_KEY));
            mDate.setText(savedInstanceState.getString(DATE_KEY));
            mLocation.setText(savedInstanceState.getString(LOCATION));
        }


    }

    private boolean saveMeetingInfo(Account account, Meetings meetings){
        SharedPreferences mypref = getApplicationContext().getSharedPreferences(UID, MODE_PRIVATE);
        String uid = mypref.getString(ACC_UID, null);
        myRef = FirebaseDatabase.getInstance().getReference(ACCOUNT + "/" + uid);
        myRef.child(meetings.getId()).setValue(meetings);
        return true;
    }

    public boolean checkField(String name){
        boolean isready = true;
        if(name.equals("")){
            View focusView1 = null;
            mEventName.setError(getString(R.string.error_field_required));
            focusView1 =  mEventName;
            focusView1.requestFocus();
            isready = false;
        }
        if(mTime.getText().equals("")){
            View focusView1 = null;
            mTime.setError(getString(R.string.error_field_required));
            focusView1 =  mTime;
            focusView1.requestFocus();
            isready = false;
        }
        if(mDate.getText().equals("")){
            View focusView1 = null;
            mDate.setError(getString(R.string.error_field_required));
            focusView1 =  mDate;
            focusView1.requestFocus();
            isready = false;
        }
        if(mLocation.getText().equals("")){
            View focusView1 = null;
            mLocation.setError(getString(R.string.error_field_required));
            focusView1 =  mLocation;
            focusView1.requestFocus();
            isready = false;
        }

        return isready;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = getPlace(CreateEventActivity.this, data);
                event_Location = String.valueOf(place.getAddress());
                mLocation.setText(event_Location);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
