package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import java.sql.SQLException;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.util.Setting;

public class SplashScreen extends AppCompatActivity
{
    public static final String CLASS_NAME = "SplashScreen";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_splash_screen);
        JodaTimeAndroid.init(this);
        Setting.getInstance(this);

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data model_data = MDM_Data.getInstance(SplashScreen.this);
                try
                {
                    model_data.openWrite();
                    model_data.close();
                }
                catch(SQLException e)
                {
                    Log.i(CLASS_NAME, CLASS_PATH + ".SQLException");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                int secondsDelayed = 2;
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        SplashScreen.super.startActivity(new Intent(SplashScreen.this, Constitution.class));
                        SplashScreen.super.finish();
                    }
                }, secondsDelayed * 1000);
            }
        }.execute();
    }

}
