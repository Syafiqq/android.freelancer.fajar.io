package io.localhost.freelancer.statushukum.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.AirtableDataFetcher;
import io.localhost.freelancer.statushukum.model.util.SyncMessage;

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
        final TextView progressMessage = (TextView) super.findViewById(R.id.progress_message);
        sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(progress.getVisibility() == View.GONE)
                {
                    progressMessage.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.VISIBLE);

                    Observer update = new Observer() {
                        Boolean isIndeterminate = null;
                        @Override
                        public void update(Observable o, Object arg) {
                            if(!(arg instanceof SyncMessage)) return;
                            SyncMessage syncMessage = (SyncMessage) arg;
                            if(isIndeterminate == null || isIndeterminate != syncMessage.isIndeterminate()) {
                                isIndeterminate = syncMessage.isIndeterminate();
                                progress.setIndeterminate(isIndeterminate);
                            }

                            progress.setMax(syncMessage.getMax());
                            progress.setProgress(syncMessage.getCurrent());
                            progressMessage.setText(syncMessage.getMessage());
                        }
                    };
                    AsyncTask<Void, Object, AirtableDataFetcher> task = io.localhost.freelancer.statushukum.model.util.Setting.doSync(
                            null,
                            null,
                            () -> {
                                progress.setVisibility(View.GONE);
                                progressMessage.setVisibility(View.GONE);
                            },
                            update,
                            Setting.this);
                    task.execute();
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
