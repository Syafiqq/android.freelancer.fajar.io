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
import io.reactivex.disposables.CompositeDisposable;

public class Setting extends AppCompatActivity
{
    public static final String CLASS_NAME = "Setting";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Setting";

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_setting);
        this.setToolbar();
        this.registerComponent();
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private void registerComponent()
    {
        final ImageButton sync = (ImageButton) super.findViewById(R.id.content_setting_ib_sync);
        final ProgressBar progress = (ProgressBar) super.findViewById(R.id.content_setting_pb_progress);
        final TextView progressMessage = (TextView) super.findViewById(R.id.progress_message);
        final TextView percent = (TextView) super.findViewById(R.id.tv_precent);
        percent.setVisibility(View.GONE);
        final TextView count = (TextView) super.findViewById(R.id.tv_count);
        final View holderProgress = super.findViewById(R.id.progress_holder);
        final View holderPercent = super.findViewById(R.id.percent_holder);
        sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(holderProgress.getVisibility() == View.GONE)
                {
                    holderProgress.setVisibility(View.VISIBLE);

                    Observer update = new Observer() {
                        Boolean isIndeterminate = null;
                        @Override
                        public void update(Observable o, Object arg) {
                            if(!(arg instanceof SyncMessage)) return;
                            SyncMessage syncMessage = (SyncMessage) arg;
                            if(isIndeterminate == null || isIndeterminate != syncMessage.isIndeterminate()) {
                                isIndeterminate = syncMessage.isIndeterminate();
                                progress.setMax(syncMessage.getMax());
                                progress.setProgress(0);
                                progressMessage.setText(syncMessage.getMessage());
                                progress.setIndeterminate(isIndeterminate);
                                if(!isIndeterminate) {
                                    holderPercent.setVisibility(View.VISIBLE);
                                }
                            }
                            progress.setProgress(syncMessage.getCurrent());
                            if(syncMessage.getMax() != 0)
                            {
                                percent.setText((syncMessage.getCurrent() * 100 / syncMessage.getMax()) + " %");
                                count.setText(String.format("%d/%d", syncMessage.getCurrent(), syncMessage.getMax()));
                            }
                            progressMessage.setText(syncMessage.getMessage());
                        }
                    };
                    AsyncTask<Void, Object, Void> task = io.localhost.freelancer.statushukum.model.util.Setting.doSync(
                            Setting.this,
                            null,
                            null,
                            () -> {
                                holderProgress.setVisibility(View.GONE);
                                holderPercent.setVisibility(View.GONE);
                            },
                            update,
                            Setting.this,
                            disposable);
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
