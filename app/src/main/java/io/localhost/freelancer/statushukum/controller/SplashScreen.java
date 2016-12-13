package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.localhost.freelancer.statushukum.R;

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

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                startActivity(new Intent(SplashScreen.this, Dashboard.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }

}
