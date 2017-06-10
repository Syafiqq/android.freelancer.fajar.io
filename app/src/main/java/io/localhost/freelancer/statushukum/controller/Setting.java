package io.localhost.freelancer.statushukum.controller;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.Observable;
import java.util.Observer;

import io.localhost.freelancer.statushukum.R;

public class Setting extends AppCompatActivity
{
    public static final String CLASS_NAME = "Setting";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Setting";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_setting);
        this.setToolbar();
        this.registerComponent();
    }

    private void registerComponent()
    {
        final ImageButton sync = (ImageButton) super.findViewById(R.id.content_setting_ib_sync);
        final ProgressBar progress = (ProgressBar) super.findViewById(R.id.content_setting_pb_progress);
        sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(progress.getVisibility() == View.GONE)
                {
                    progress.setVisibility(View.VISIBLE);
                    io.localhost.freelancer.statushukum.model.util.Setting.doSync(new Observer()
                    {
                        @Override
                        public void update(Observable o, Object arg)
                        {
                            progress.setVisibility(View.GONE);
                        }
                    }, Setting.this);
                }
            }
        });
    }

    private void setToolbar()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

        final Toolbar toolbar = (Toolbar) super.findViewById(R.id.activity_setting_toolbar);
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setContentInsetStartWithNavigation(4);
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_white_24));
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Setting.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackPressed");

        super.finish();
    }

}
