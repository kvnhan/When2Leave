package com.example.kiennhan.when2leave.model.activity;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.OnItemClickListener;
import com.example.kiennhan.when2leave.model.adapter.DailyEventAdapter;
import com.example.kiennhan.when2leave.model.service.When2Leave;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import database.DataBaseHelper;

public class WelcomeActivity extends AppCompatActivity {

    TextView mWelcome;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String[] items;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    ArrayList<Meetings> meetingsList = new ArrayList<Meetings>();

    private RecyclerView mRecyclerView;
    private DailyEventAdapter mEventAdapter;

    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private boolean onWelcome = false;

    private FusedLocationProviderClient mFusedLocationClient;
    DataBaseHelper mDB;

    private static final String EVENTNAME = "eventname";
    private static final String LOCATION = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";
    private static final String MEETING_ID = "meetingid";

    private static final String CURR_LOCATION = "current";
    private static final String SAVE_LOCATION = "saveloc";
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");

        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(getApplicationContext().JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), When2Leave.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000)
                .setPersisted(true)
                .build();
        int ret = jobScheduler.schedule(jobInfo);
        if (ret == JobScheduler.RESULT_SUCCESS) Log.d("FUCK", "Job scheduled successfully!");

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.daily_event);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (!onWelcome) {
                        Intent intent = new Intent(WelcomeActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                } else if (position == 1) {
                    Intent intent = new Intent(WelcomeActivity.this, CreateEventActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(WelcomeActivity.this, EventListActivity.class);
                    startActivity(intent);
                }
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
        String userName = pref.getString(KEY, null);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.Welcome), userName);
        mWelcome = (TextView)findViewById(R.id.welcome);
        mWelcome.setText(text);
        updateUI();
        clickItem();

        //check for permissions
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.inflateMenu(R.menu.top_bar);
        myToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            if(id == R.id.signOut) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void addDrawerItems() {
        items = getResources().getStringArray(R.array.menus);
        mAdapter = new ArrayAdapter<String>(this, R.layout.nav_menu, items);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void updateUI() {
        mDB = new DataBaseHelper(getApplicationContext());
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
        String userName = pref.getString(KEY, null);
        String uid = mDB.getUUID(userName, getApplicationContext());

        meetingsList = mDB.getWeeklyMeetings(uid, getApplicationContext());
        try {
            if (mEventAdapter == null) {
                mEventAdapter = new DailyEventAdapter(getApplication(), meetingsList);
                mRecyclerView.setAdapter(mEventAdapter);
            } else {
                mEventAdapter.setMeetings(meetingsList);
                mEventAdapter.notifyDataSetChanged();
            }

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }catch (Exception i){
            i.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void clickItem(){
        mEventAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(WelcomeActivity.this, ViewEventActivity.class);
                Meetings meeting =meetingsList.get(position);
                String name = meeting.getTitle();
                String location = meeting.getDestination();
                String time = meeting.getTimeOfMeeting();
                String date = meeting.getDateOfMeeting();
                String desc = meeting.getDescription();
                intent.putExtra(EVENTNAME, name);
                intent.putExtra(LOCATION, location);
                intent.putExtra(TIME, time);
                intent.putExtra(DATE, date);
                intent.putExtra(DESC, desc);
                startActivity(intent);
            }
        });
    }

}
