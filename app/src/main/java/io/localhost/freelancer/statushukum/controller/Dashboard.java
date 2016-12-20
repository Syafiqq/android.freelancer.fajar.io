package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

public class Dashboard extends AppCompatActivity
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";

    private CountPerYearAdapter         recycleViewAdapter;
    private List<MDM_Data.CountPerYear> entryList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_dashboard);
        this.setToolbar();
        this.setProperty();
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        if(this.entryList == null)
        {
            this.entryList = new LinkedList<>();
        }
        else
        {
            this.entryList.clear();
        }
        this.setCenturyListAdapter();
        this.setCenturyList();
    }

    private void setCenturyListAdapter()
    {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_dashboard_recycle_view_container);
        this.recycleViewAdapter = new CountPerYearAdapter(new ArrayList<MDM_Data.CountPerYear>(0), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this.recycleViewAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private synchronized void setCenturyList()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setCenturyList");

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data                    modelData = MDM_Data.getInstance(Dashboard.this);
                final List<MDM_Data.CountPerYear> dbResult  = modelData.getCountPerYear();
                Dashboard.this.entryList.clear();
                Dashboard.this.entryList.addAll(dbResult);
                Dashboard.this.recycleViewAdapter.update(dbResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                Dashboard.this.recycleViewAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void setToolbar()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

        final Toolbar toolbar = (Toolbar) super.findViewById(R.id.activity_dashboard_toolbar);
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setContentInsetStartWithNavigation(4);
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
            case R.id.activity_dashboard_menu_refresh:
            {
                this.setCenturyList();
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
    protected void onPostResume()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onPostResume");
        this.setCenturyList();

        super.onPostResume();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackPressed");

        super.finish();
    }
}
