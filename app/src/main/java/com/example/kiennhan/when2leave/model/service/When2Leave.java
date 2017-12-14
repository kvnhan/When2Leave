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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.ViewEventActivity;
import com.example.kiennhan.when2leave.model.activity.wrapper.ListOfMeetings;
import com.example.kiennhan.when2leave.model.adapter.DailyEventAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import database.DataBaseHelper;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

/**
 * Jobscheduler Service
 */
public class When2Leave extends JobService {

    //Keys for storing and retreving data
    private  static final String TIME2LEAVE = "time2leave";
    private  static final String LIST = "meetinglist";
    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String CURR_LOCATION = "current";
    private static final String SAVE_LOCATION = "saveloc";
    private static final String EVENTNAME = "eventname";
    private static final String LOCATION = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";
    private static final String LETSGO = "letsgo";
    private static final String LONG = "long";
    private static final String LAT = "lat";
    private static final String MEETING_ID = "meetingid";
    private static final String LOM = "listofmeeting";
    private static final String LISTOFMEET = "list";


    private RecyclerView mRecyclerView;
    private DailyEventAdapter mEventAdapter;

    //Google API
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;

    DataBaseHelper mDB;
    private com.example.kiennhan.when2leave.model.Location currentLocation;

    public When2Leave() {
    }

    private JobParameters params;
    com.example.kiennhan.when2leave.model.Location obj;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;

        SharedPreferences locPref = getApplicationContext().getSharedPreferences(CURR_LOCATION, MODE_PRIVATE);
        Gson gson = new Gson();

        //Get saved user location, if there is one
        if(locPref.getString(SAVE_LOCATION, null) != null) {
            String json = locPref.getString(SAVE_LOCATION, "");
            obj = gson.fromJson(json, com.example.kiennhan.when2leave.model.Location.class);
            Log.d("LOCATION", "Long: " + obj.getLong() + ", Lat: " + obj.getLati());
        }

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
            Log.i("tester", "FINISH WORK");
            jobFinished(params, false);
        }

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... params) {
            Meetings meeting = null;
            Date date = null;

            Gson gson = new Gson();
            SharedPreferences listpref = getApplicationContext().getSharedPreferences(LISTOFMEET, MODE_PRIVATE);
            String listStr = listpref.getString(LOM, null);
            ListOfMeetings lom = new ListOfMeetings();
            lom = gson.fromJson(listStr, ListOfMeetings.class);
            if(lom != null) {
                mEventAdapter = new DailyEventAdapter(getApplicationContext(), lom.getMeetings());
                mEventAdapter.removeCompletedMeetings();
                mEventAdapter.notifyDataSetChanged();
            }

            // Get a list of weekly events
            mDB = new DataBaseHelper(When2Leave.this);
            SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
            String userName = pref.getString(KEY, null);
            String uid;
            if(!userName.equals("only_guest")){
                uid = mDB.getUUID(userName, When2Leave.this);
            }else{
                uid = "just_guest";
            }
            ArrayList<Meetings> meetingsList = mDB.getWeeklyMeetings(uid, getApplicationContext());

            //select the next upcoming meeting
            for(Meetings m: meetingsList) {
                String dateTime = m.getDateOfMeeting() + " " + m.getTimeOfMeeting();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                try {
                    date = sdf.parse(dateTime);

                    //if the current time is before the next upcoming meeting
                    if (new Date().before(date)) {
                        meeting = m;
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //get the current location
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();
            }
            mGoogleApiClient.connect();


            @SuppressLint("MissingPermission") PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            final Meetings finalMeeting = meeting;
            final Date finalDate = date;
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

                @Override
                public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {

                    PlaceLikelihood place = placeLikelihoods.get(0);
                    String placeID = place.getPlace().getId();

                    com.example.kiennhan.when2leave.model.Location likelyLocation = new com.example.kiennhan.when2leave.model.Location(place.getPlace().getLatLng().longitude,
                            place.getPlace().getLatLng().latitude);

                    //Save user location
                    SharedPreferences locPref = getApplicationContext().getSharedPreferences(CURR_LOCATION, MODE_PRIVATE);
                    SharedPreferences.Editor editor = locPref.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(likelyLocation);
                    editor.putString(SAVE_LOCATION, json);
                    editor.commit();

                    placeLikelihoods.release();
                    if(finalMeeting != null) {
                        //Start a background task to get the direction
                        new getDirectionsTask(finalMeeting, finalDate, placeID).execute();
                    }

                }
            });

            Log.i("tester", "DOING BACKGROUND WORK");
            return null;
        }

        private class getDirectionsTask extends AsyncTask<Void, Void, Void> {

            Meetings meeting;
            Date date;
            String placeID;

            getDirectionsTask(Meetings meeting, Date date, String placeID) {
                this.meeting = meeting;
                this.date = date;
                this.placeID = placeID;
            }

            //get the directions to the location
            @Override
            protected Void doInBackground(Void... voids) {
                URL url = null;
                String origin = placeID;
                String destination = meeting.getDestination().replaceAll(" ", "+");
                String key = "AIzaSyBbWG82CGJS1t6RT5DoCV5cjKV8cHrLHNk";
                String arrivalTime = date.getTime()+"";
                try {
                    url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:"+origin+"&destination="+destination+"&arrival_time="+arrivalTime+"&key="+key);
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

                        //parse the JSON reply
                        try {
                            JSONObject directions = new JSONObject(str);
                            int travelSeconds = directions.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
                            double longtitude = directions.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lng");
                            double latitude = directions.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lat");
                            Log.d("tester", travelSeconds+" seconds to arrive");

                            //show the notification if it's time to leave
                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                            Date meetingDate = null;
                            Calendar today = Calendar.getInstance();
                            try {
                                meetingDate = format.parse(meeting.getDateOfMeeting() + " " + meeting.getTimeOfMeeting());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //Calculate the remaining time user have left before notifying
                            long timeDiff = (meetingDate.getTime() - today.getTime().getTime()) / 1000;
                            long leftoverTime = timeDiff - (travelSeconds + 900);
                            Log.d("FUCK", leftoverTime+" seconds leftover");

                            if(leftoverTime < 60*15) {
                                Intent resultIntent = new Intent(When2Leave.this, ViewEventActivity.class);

                                Gson gson = new Gson();
                                SharedPreferences listpref = getApplicationContext().getSharedPreferences(LISTOFMEET, MODE_PRIVATE);
                                String listStr = listpref.getString(LOM, null);
                                ListOfMeetings lom = new ListOfMeetings();
                                lom = gson.fromJson(listStr, ListOfMeetings.class);
                                if(lom != null) {
                                    mEventAdapter = new DailyEventAdapter(getApplicationContext(), lom.getMeetings());
                                    mEventAdapter.setCompleteMeeting(meeting);
                                    mEventAdapter.notifyDataSetChanged();
                                }

                                //Store meeting info in resultIntent
                                String name = meeting.getTitle();
                                String location = meeting.getDestination();
                                String time = meeting.getTimeOfMeeting();
                                String date = meeting.getDateOfMeeting();
                                String desc = meeting.getDescription();
                                resultIntent.putExtra(EVENTNAME, name);
                                resultIntent.putExtra(LOCATION, location);
                                resultIntent.putExtra(TIME, time);
                                resultIntent.putExtra(DATE, date);
                                resultIntent.putExtra(DESC, desc);
                                resultIntent.putExtra(LONG, longtitude);
                                resultIntent.putExtra(LAT, latitude);
                                resultIntent.putExtra(MEETING_ID, meeting.getId());
                                resultIntent.putExtra(LETSGO, true);

                                PendingIntent resultPendingIntent =
                                        PendingIntent.getActivity(
                                                When2Leave.this,
                                                0,
                                                resultIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );

                                //Build a notification
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(When2Leave.this, TIME2LEAVE)
                                                .setSmallIcon(R.drawable.ic_launcher_background)
                                                .setTicker("TIME TO LEAVE!!!")
                                                .setContentTitle("When2Leave")
                                                .setContentIntent(resultPendingIntent)
                                                .setDefaults(Notification.DEFAULT_SOUND)
                                                .setAutoCancel(true)
                                                .setContentText("Leave now for " + meeting.getTitle());

                                int mNotificationId = 001;
                                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

    }
}