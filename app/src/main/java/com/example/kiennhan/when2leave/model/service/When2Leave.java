package com.example.kiennhan.when2leave.model.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.activity.CreateEventActivity;
import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.ViewEventActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import database.DataBaseHelper;

import static android.content.ContentValues.TAG;

public class When2Leave extends JobService {
    private  static final String TIME2LEAVE = "time2leave";
    private  static final String LIST = "meetinglist";
    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;

    DataBaseHelper mDB;

    public When2Leave() {
    }

    private JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        Log.i("tester", "STARTING BACKGROUND WORK");
        new Time2Leave().execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private class Time2Leave extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //TODO: Post notification

            /*
            Get a list of upcoming meetings and create notification for ea
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(When2Leave.this);
            Gson gson = new Gson();
            String json = sharedPrefs.getString(LIST, null);
            Type type = new TypeToken<ArrayList<Meetings>>() {}.getType();
            ArrayList<Meetings> arrayList = gson.fromJson(json, type);
            */
            Intent resultIntent = new Intent(When2Leave.this, ViewEventActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            When2Leave.this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(When2Leave.this, TIME2LEAVE)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setTicker("TIME TO LEAVE!!!")
                            .setContentTitle("When2Leave")
                            .setContentIntent(resultPendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentText("You Should Leave for Your Meeting");

            int mNotificationId = 001;
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            Log.i("tester", "FINISH WORK");
            jobFinished(params, false);
        }

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... params) {
            //TODO: Get current location, calculate distance time to a list of meetings retrieved from database, including traffic,etc....

            Meetings meeting;


            // Get a list of weekly events
            mDB = new DataBaseHelper(When2Leave.this);
            SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
            String userName = pref.getString(KEY, null);
            String uid = mDB.getUUID(userName, When2Leave.this);
            ArrayList<Meetings> meetingsList = mDB.getWeeklyMeetings(uid, getApplicationContext());
            Log.i("tester", meetingsList.size()+" meetings");
            for(Meetings m: meetingsList){
                Log.i("tester", "   "+m.getTitle());
            }

            //select the next upcoming meeting
            String time = DateFormat.getDateTimeInstance().format(new Date());
            Log.i("tester", time);
            for(Meetings m: meetingsList) {
                String dateTime = m.getDateOfMeeting() + " " + m.getTimeOfMeeting();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                try {
                    Date d = sdf.parse(dateTime);

                    //if the current time is before the next upcoming meeting
                    if (new Date().before(d)) {
                        meeting = m;
                        Log.i("tester", "next meeting: " + m.getTitle());
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();
                mGoogleApiClient.connect();
                Log.i("tester", "got location");

                @SuppressLint("MissingPermission") PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(mGoogleApiClient, null);
                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {

                        PlaceLikelihood place = placeLikelihoods.get(0);
                        Log.i("tester", String.format("Place '%s' has lat: %g and long: %g",
                                place.getPlace().getName(),
                                place.getPlace().getLatLng().latitude,
                                place.getPlace().getLatLng().longitude));

                        placeLikelihoods.release();

                        getArrivalTime();
                    }

                    //make the request to get the time to travel to the next meeting
                    public void getArrivalTime() {
                        URL url = null;
                        try {
                            url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=AIzaSyBbWG82CGJS1t6RT5DoCV5cjKV8cHrLHNk");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            try {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                                int data = in.read();
                                String str = "";
                                while (data != -1) {
                                    str += (char) data;
                                    data = in.read();
                                }
                                in.close();
                                Log.i("tester", str);
                            } finally {
                                urlConnection.disconnect();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }



            Log.i("tester", "DOING BACKGROUND WORK");
            return null;
        }
    }

    public ArrayList<Meetings> timeToLeave(ArrayList<Meetings> lom) throws ParseException {
        ArrayList<Meetings> leavingList = new ArrayList<Meetings>();

        final Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        for(Meetings m: lom){
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate =  df.parse(m.getDateOfMeeting());
            Calendar todayDate = Calendar.getInstance();
            todayDate.setTime(startDate);
            if(todayDate.get(Calendar.DAY_OF_MONTH) == mDay && todayDate.get(Calendar.MONTH) == mMonth){
                //TODO: Compare current hour to arrival Time
            }

        }

        return leavingList;
    }
}