package io.localhost.freelancer.statushukum.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import io.localhost.freelancer.statushukum.R;

public class Setting extends AppCompatActivity
{
    public static final String CLASS_NAME = "Setting";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Setting";

    private io.localhost.freelancer.statushukum.model.util.Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_setting);
        this.setting = io.localhost.freelancer.statushukum.model.util.Setting.getInstance(this);
        this.setToolbar();
        this.registerComponent();
    }

    private void registerComponent()
    {
        final Switch show     = (Switch) super.findViewById(R.id.content_setting_s_show);
        final Switch sync     = (Switch) super.findViewById(R.id.content_setting_s_sync);
        final Switch deepSync = (Switch) super.findViewById(R.id.content_setting_s_deep_sync);
        show.setChecked(this.setting.isAllShowed());
        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Setting.this.setting.setAllShowed(isChecked);
            }
        });

        sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    new AsyncTask<Void, Void, Void>()
                    {
                        @Override
                        protected Void doInBackground(Void... voids)
                        {
                            Setting.this.setting.doSync(null);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid)
                        {
                            if(buttonView instanceof Switch)
                            {
                                buttonView.setChecked(false);
                            }
                        }
                    }.execute();
                }
            }
        });

        deepSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    new AsyncTask<Void, Void, Void>()
                    {
                        @Override
                        protected Void doInBackground(Void... voids)
                        {
                            Setting.this.setting.doDeepSync();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid)
                        {
                            if(buttonView instanceof Switch)
                            {
                                buttonView.setChecked(false);
                            }
                        }
                    }.execute();
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
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.content_toolbar_back));
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
