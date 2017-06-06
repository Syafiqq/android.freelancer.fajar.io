package io.localhost.freelancer.statushukum.controller;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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


        setContentView(R.layout.activity_setting);
        this.setToolbar();
        this.registerComponent();
    }

    private void registerComponent()
    {
        final ImageButton sync = (ImageButton) super.findViewById(R.id.content_setting_ib_sync);
        final ProgressBar progress = (ProgressBar) super.findViewById(R.id.content_setting_pb_progress);
        final ImageButton socialFacebook = (ImageButton) super.findViewById(R.id.content_setting_ib_social_facebook);
        final ImageButton socialTwitter = (ImageButton) super.findViewById(R.id.content_setting_ib_social_twitter);
        final ImageButton socialInstagram = (ImageButton) super.findViewById(R.id.content_setting_ib_social_instagram);
        final ImageButton socialGPlus = (ImageButton) super.findViewById(R.id.content_setting_ib_social_gplus);
        socialFacebook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Setting.this.onFacebookSocialPressed(v);
            }
        });
        socialTwitter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Setting.this.onTwitterSocialPressed(v);
            }
        });
        socialInstagram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Setting.this.onInstagramSocialPressed(v);
            }
        });
        socialGPlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Setting.this.onGPlusSocialPressed(v);
            }
        });


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

    private void onGPlusSocialPressed(View view)
    {
        try
        {
            super.startActivity(io.localhost.freelancer.statushukum.model.util.Setting.getInstance(this).social.gPlus.getGPlusIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onInstagramSocialPressed(View view)
    {
        try
        {
            super.startActivity(io.localhost.freelancer.statushukum.model.util.Setting.getInstance(this).social.instagram.getInstagramIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onTwitterSocialPressed(View view)
    {
        try
        {
            super.startActivity(io.localhost.freelancer.statushukum.model.util.Setting.getInstance(this).social.twitter.getTwitterIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFacebookSocialPressed(View view)
    {
        try
        {
            super.startActivity(io.localhost.freelancer.statushukum.model.util.Setting.getInstance(this).social.facebook.getFacebookIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void setToolbar()
    {


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


        super.finish();
    }

}
