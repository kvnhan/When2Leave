package com.example.kiennhan.when2leave.model.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewEventActivity extends AppCompatActivity {

    private static final String EVENTNAME = "eventname";
    private static final String LOCATIN = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";
    private static final String MEETING_ID = "meetingid";

    private static final String MY_PREF = "myPref";
    private static final String TWO_CLICK = "twoclick";




    EditText mName;
    EditText mLocation;
    EditText mTime;
    EditText mDate;
    EditText mDesc;
    Button mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event);

        Intent intent = getIntent();
        final String name = intent.getStringExtra(EVENTNAME);
        final String loc = intent.getStringExtra(LOCATIN);
        final String time = intent.getStringExtra(TIME);
        final String date = intent.getStringExtra(DATE);
        final String desc = intent.getStringExtra(DESC);
        final String id = intent.getStringExtra(MEETING_ID);

        mName = findViewById(R.id.event_name_field);
        mLocation = findViewById(R.id.event_location_feld);
        mTime = findViewById(R.id.event_Time);
        mDate = findViewById(R.id.event_Date);
        mDesc = findViewById(R.id.event_Description);
        mEdit = findViewById(R.id.edit);

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


        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewEventActivity.this, CreateEventActivity.class);
                intent.putExtra(EVENTNAME, name);
                intent.putExtra(LOCATIN, loc);
                intent.putExtra(TIME, time);
                intent.putExtra(DATE, date);
                intent.putExtra(DESC, desc);
                intent.putExtra(MEETING_ID, id);
                intent.putExtra(TWO_CLICK, 1);
                startActivity(intent);
            }
        });



    }
}
