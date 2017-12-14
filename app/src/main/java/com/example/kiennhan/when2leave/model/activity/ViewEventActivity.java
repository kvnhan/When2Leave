package com.example.kiennhan.when2leave.model.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.activity.wrapper.ListOfMeetings;
import com.example.kiennhan.when2leave.model.activity.wrapper.MapWrapper;
import com.example.kiennhan.when2leave.model.adapter.DailyEventAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import database.DataBaseHelper;

public class ViewEventActivity extends AppCompatActivity {


    private static final String EVENTNAME = "eventname";
    private static final String LOCATIN = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";
    private static final String MEETING_ID = "meetingid";
    private static final String LETSGO = "letsgo";
    private static final String LONG = "long";
    private static final String LAT = "lat";
    private static final String LIST = "list";
    private static final String LOM = "listofmeeting";


    private static final String MY_PREF = "myPref";
    private static final String TWO_CLICK = "twoclick";

    private static final String UID = "uid";
    private static final String ACC_UID = "accuid";




    EditText mName;
    EditText mLocation;
    EditText mTime;
    EditText mDate;
    EditText mDesc;
    Button mEdit;

    private RecyclerView mRecyclerView;
    private DailyEventAdapter mEventAdapter;
    com.example.kiennhan.when2leave.model.Location obj;

    private static final String CURR_LOCATION = "current";
    private static final String SAVE_LOCATION = "saveloc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event);

        final Intent intent = getIntent();
        final String name = intent.getStringExtra(EVENTNAME);
        final String loc = intent.getStringExtra(LOCATIN);
        final String time = intent.getStringExtra(TIME);
        final String date = intent.getStringExtra(DATE);
        final String desc = intent.getStringExtra(DESC);
        final String id = intent.getStringExtra(MEETING_ID);

        SharedPreferences locPref = getApplicationContext().getSharedPreferences(CURR_LOCATION, MODE_PRIVATE);
        Gson gso = new Gson();
        String json = locPref.getString(SAVE_LOCATION, "");
        obj = gso.fromJson(json, com.example.kiennhan.when2leave.model.Location.class);

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


        if (intent.getBooleanExtra(LETSGO, false)) {
            mEdit.setText("LET'S GO");
        }
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.getBooleanExtra(LETSGO, false)) {
                    new AlertDialog.Builder(ViewEventActivity.this)
                            .setTitle("Use GoogleMaps")
                            .setMessage("Do you really want to use Google Maps to navigate ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    Meetings meeting = new Meetings(id, name, null, time, date, "", loc, desc, true);
                                    updateDatabase(meeting);
                                    double latitude = intent.getDoubleExtra(LAT, 0);
                                    double longitude = intent.getDoubleExtra(LONG, 0);
                                    String uri = "http://maps.google.com/maps?saddr=" + obj.getLati() + "," + obj.getLong() + "&daddr=" +
                                            latitude + "," + longitude;
                                    Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent3.setPackage("com.google.android.apps.maps");
                                    startActivity(intent3);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Meetings meeting = new Meetings(id, name, null, time, date, "", loc, desc, true);
                                    updateDatabase(meeting);
                                    finish();
                                }
                            }).show();

                }

                Intent intent2 = new Intent(ViewEventActivity.this, CreateEventActivity.class);
                intent2.putExtra(EVENTNAME, name);
                intent2.putExtra(LOCATIN, loc);
                intent2.putExtra(TIME, time);
                intent2.putExtra(DATE, date);
                intent2.putExtra(DESC, desc);
                intent2.putExtra(MEETING_ID, id);
                intent2.putExtra(TWO_CLICK, 1);
                startActivity(intent2);

            }
        });
    }
        public void updateDatabase(Meetings meeting){
        Gson gson = new Gson();
        SharedPreferences listpref = getApplicationContext().getSharedPreferences(LIST, MODE_PRIVATE);
        String listStr = listpref.getString(LOM, null);
        ListOfMeetings lom = new ListOfMeetings();
        lom = gson.fromJson(listStr, ListOfMeetings.class);

        DataBaseHelper mDB = new DataBaseHelper(getApplicationContext());
        mDB.deleteEvent(getApplicationContext(), meeting);
        mRecyclerView = (RecyclerView) findViewById(R.id.daily_event);
        mEventAdapter = new DailyEventAdapter(getApplicationContext(), lom.getMeetings());
        mEventAdapter.setMeetings(lom.getMeetings());
        mEventAdapter.notifyDataSetChanged();
        mEventAdapter.removeMeetings(meeting);
        mEventAdapter.notifyDataSetChanged();

        SharedPreferences mypref = getApplicationContext().getSharedPreferences(UID, MODE_PRIVATE);
        String uid = mypref.getString(ACC_UID, null);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("account" + "/" + uid);
        myRef.child(meeting.getId()).setValue(meeting);
    }
}
