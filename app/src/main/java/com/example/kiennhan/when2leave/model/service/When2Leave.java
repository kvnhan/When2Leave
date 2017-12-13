package com.example.kiennhan.when2leave.model.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.Location;
import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.ViewEventActivity;
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

import static android.content.ContentValues.TAG;

public class When2Leave extends JobService {
    private  static final String TIME2LEAVE = "time2leave";
    private  static final String LIST = "meetinglist";
    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";



    DataBaseHelper mDB;
    private Location currentLocation;
    private static final String CURR_LOCATION = "current";
    private static final String SAVE_LOCATION = "saveloc";

    public When2Leave() {
    }

    private JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        SharedPreferences locPref = getApplicationContext().getSharedPreferences(CURR_LOCATION, MODE_PRIVATE);
        Gson gson = new Gson();
        if(locPref.getString(SAVE_LOCATION, null) != null) {
            String json = locPref.getString(SAVE_LOCATION, "");
            Location obj = gson.fromJson(json, Location.class);
            Log.d("CURRENT LOCATION", "Long: " + obj.getLong() + ", Lat: " + obj.getLati());
        }
        Log.d("FUCK", "STARTING BACKGROUND WORK");
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
            //TODO: Post notfication

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

            Log.d("FUCK", "FINISH WORK");
            jobFinished(params, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //TODO: Get current location, calculate distance time to a list of meetings retrieved from database, including traffic,etc....
/*
            // Get a list of weekly events
            mDB = new DataBaseHelper(When2Leave.this);
            SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
            String userName = pref.getString(KEY, null);
            String uid = mDB.getUUID(userName, When2Leave.this);
            ArrayList<Meetings> meetingsList = mDB.getWeeklyMeetings(uid, getApplicationContext());


                Compare meeting time to current time, if close get the distance, duration, and arrival time

            ArrayList<Meetings> leavingList = new ArrayList<Meetings>();
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(When2Leave.this);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(leavingList);
            editor.putString(LIST, json);
            editor.commit();
*/
            Log.d("FUCK", "DOING BACKGROUND WORK");
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
                //TODO: Compare current hour to arrrival Time
            }

        }

        return leavingList;
    }
}






