package com.example.kiennhan.when2leave.model.activity;

import android.*;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String NAME = "username";
    int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mEventName = findViewById(R.id.eventName);

        mLocation = findViewById(R.id.event_location);


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
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        String year = String.valueOf(selectedyear);
                        String day = String.valueOf(selectedday);
                        String month = String.valueOf(selectedmonth);
                        if(selectedday < 10){
                            day = "0" + selectedday;
                        }
                        if(selectedmonth < 10){
                            month = "0" + selectedmonth;
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
                        } else {
                            mTime.setText(selectedHour + ":" + selectedMinute);
                        }
                        timeOfmeeting = ( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time of the Event:");
                mTimePicker.show();

            }
        });

        // API KEY AIzaSyDDLeXTz5oZaZA2F1N7NIY_sLqhUhzA3Ok
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
                boolean isReady = checkField(eventName);
                if(isReady) {
                    Meetings meeting = new Meetings(meetingID, eventName, account, timeOfmeeting, dateOfMeeting, "", event_Location, mDescription.getText().toString());
                    mDB.addMeeting(getApplicationContext(), account, meeting);
                    Toast.makeText(getApplicationContext(), "Meeting Data Added", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateEventActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }

            }
        });

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
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
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
