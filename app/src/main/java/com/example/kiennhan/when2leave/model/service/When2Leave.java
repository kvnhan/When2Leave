package com.example.kiennhan.when2leave.model.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.ViewEventActivity;

public class When2Leave extends JobService {
    private  static final String TIME2LEAVE = "time2leave";
    public When2Leave() {
    }

    private JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
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
            //TODO: Get current location, calculate distance time, including traffic,etc....
            Log.d("FUCK", "DOING BACKGROUND WORK");
            return null;
        }
    }
}
