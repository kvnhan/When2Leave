package com.example.kiennhan.when2leave.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class When2Leave extends JobService {
    public When2Leave() {
    }

    private JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        Toast.makeText(getApplicationContext(), "starting job service...", Toast.LENGTH_SHORT).show();
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
            Log.i("FUCK", "Please Work");
            jobFinished(params, true);
            Toast.makeText(getApplicationContext(), "Finish job", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //TODO: Get current location, calculate distance time, including traffic,etc....
            Log.i("FUCK", "Please Work");
            return null;
        }
    }
}
