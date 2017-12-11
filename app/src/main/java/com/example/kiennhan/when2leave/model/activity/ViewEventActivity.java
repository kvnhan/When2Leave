package com.example.kiennhan.when2leave.model.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ViewEventActivity extends AppCompatActivity {

    private static final String EVENTNAME = "eventname";
    private static final String LOCATION = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";


    EditText mName;
    EditText mLocation;
    EditText mTime;
    EditText mDate;
    EditText mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event);

        //TODO: Check for notification and spawn the correct event

        Intent intent = getIntent();
        String name = intent.getStringExtra(EVENTNAME);
        String loc = intent.getStringExtra(LOCATION);
        String time = intent.getStringExtra(TIME);
        String date = intent.getStringExtra(DATE);
        String desc = intent.getStringExtra(DESC);

        mName = findViewById(R.id.event_name_field);
        mLocation = findViewById(R.id.event_location_feld);
        mTime = findViewById(R.id.event_Time);
        mDate = findViewById(R.id.event_Date);
        mDesc = findViewById(R.id.event_Description);

        mName.setText(name);
        mName.setEnabled(false);
        mLocation.setText(loc);
        mLocation.setEnabled(false);
        mTime.setText(time);
        mTime.setEnabled(false);
        mDate.setText(date);
        mDate.setEnabled(false);
        mDesc.setText(desc);
        mDesc.setEnabled(false);


    }
}
