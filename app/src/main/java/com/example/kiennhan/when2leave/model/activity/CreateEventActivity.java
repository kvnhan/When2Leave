package com.example.kiennhan.when2leave.model.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CreateEventActivity extends AppCompatActivity {


    EditText mEventName, mEventStreetNum, mEventStreetName, mEventState, mEventCity, mEventZipCode,
            mDefaultStreetName, mDefaultStreetNum, mDefaultCity, mDefaultState, mDefaultZipCode,
            mDate, mTime, mDescription;
    Button mCreateEvent;

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

            }
        });
    }
}
