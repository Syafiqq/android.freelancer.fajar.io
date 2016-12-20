package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

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
        final Switch      sync   = (Switch) super.findViewById(R.id.content_setting_s_sync);
        final ImageButton button = (ImageButton) super.findViewById(R.id.content_setting_ib_mailto);
        button.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_mail_send)
                .color(ContextCompat.getColor(this, R.color.grey_700))
                .sizeDp(24));
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Resources resources = Setting.this.getResources();
                final Intent    mailto    = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                mailto.putExtra(Intent.EXTRA_EMAIL, new String[] {resources.getString(R.string.activity_setting_mailto_receipt)});
                mailto.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.activity_setting_mailto_subject));
                mailto.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.activity_setting_mailto_content));
                Setting.super.startActivity(Intent.createChooser(mailto, "Send Feedback:"));
            }
        });
        sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    if(buttonView instanceof Switch)
                    {
                        buttonView.setEnabled(false);
                    }
                    Setting.this.doSync(buttonView);
                }
            }
        });
    }

    private synchronized void doSync(final CompoundButton buttonView)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".doSync");
        new AsyncTask<Void, Void, Void>()
        {
            private Observer callback;

            @Override
            protected void onPreExecute()
            {
                callback = new Observer()
                {
                    @Override
                    public void update(final Observable observable, final Object o)
                    {
                        Setting.super.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                switch((Integer) o)
                                {
                                    case io.localhost.freelancer.statushukum.model.util.Setting.SYNC_FAILED:
                                    {
                                        Toast.makeText(Setting.this, Setting.super.getString(R.string.system_setting_server_version_error), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case io.localhost.freelancer.statushukum.model.util.Setting.SYNC_SUCCESS:
                                    {
                                        Toast.makeText(Setting.this, Setting.super.getString(R.string.system_setting_server_version_success), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case io.localhost.freelancer.statushukum.model.util.Setting.SYNC_EQUAL:
                                    {
                                        Toast.makeText(Setting.this, Setting.super.getString(R.string.system_setting_server_version_equal), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                                buttonView.setChecked(false);
                                buttonView.setEnabled(true);
                            }
                        });
                    }
                };
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids)
            {
                io.localhost.freelancer.statushukum.model.util.Setting.getInstance(Setting.this).doSync(this.callback);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
            }
        }.execute();
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
