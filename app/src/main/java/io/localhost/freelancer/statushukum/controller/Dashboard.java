package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.jaredrummler.materialspinner.MaterialSpinner;

import net.danlew.android.joda.JodaTimeAndroid;

import java.sql.SQLException;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

public class Dashboard extends AppCompatActivity
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";

    private static void doSync()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_dashboard);

        this.setToolbar();
        this.setCenturyList();

        final MDM_Data model_data = MDM_Data.getInstance(this);
        try
        {
            /*model_data.openWrite();
            model_data.close();*/
            model_data.openRead();
        }
        catch(SQLException e)
        {
            Log.i(CLASS_NAME, CLASS_PATH + ".SQLException");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setCenturyList()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setCenturyList");

        final MaterialSpinner            spinner     = (MaterialSpinner) findViewById(R.id.content_dashboard_spinner_century_chooser);
        final ArrayAdapter<CharSequence> adapter     = ArrayAdapter.createFromResource(this, R.array.content_dashboard_spinner_observable_century, android.R.layout.simple_spinner_item);
        final String[]                   spinnerItem = new String[adapter.getCount()];
        for(int i = -1, is = spinnerItem.length; ++i < is; )
        {
            try
            {
                spinnerItem[i] = adapter.getItem(i).toString();
            }
            catch(NullPointerException ignored)
            {

            }
        }
        spinner.setItems(spinnerItem);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<CharSequence>()
        {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, CharSequence item)
            {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setToolbar()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

        final Toolbar toolbar = (Toolbar) super.findViewById(R.id.activity_dashboard_toolbar);
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
                    Dashboard.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.activity_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onOptionsItemSelected");

        switch(item.getItemId())
        {
            case R.id.activity_dashboard_menu_setting:
            {
                startActivity(new Intent(this, Setting.class));
                return true;
            }
            case R.id.activity_dashboard_menu_sync:
            {
                Dashboard.doSync();
                return true;
            }
            case android.R.id.home:
                //perhaps use intent if needed but i'm sure there's a specific intent action for up you can use to handle
                Dashboard.this.onBackButtonPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBackButtonPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackButtonPressed");

        this.onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackPressed");

        super.finish();
    }
}
