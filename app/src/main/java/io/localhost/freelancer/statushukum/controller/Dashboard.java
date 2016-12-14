package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
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
import android.widget.ArrayAdapter;

import com.jaredrummler.materialspinner.MaterialSpinner;

import net.danlew.android.joda.JodaTimeAndroid;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

public class Dashboard extends AppCompatActivity
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";
    private CountPerYearAdapter recycleViewAdapter;

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
        this.setYearList();
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

    private void setYearList()
    {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_dashboard_recycle_view_container);
        this.recycleViewAdapter = new CountPerYearAdapter(new ArrayList<MDM_Data.CountPerYear>(0), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this.recycleViewAdapter);
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
                try
                {
                    final MDM_Data                    modelData = MDM_Data.getInstance(Dashboard.this);
                    final int                         yearStart = NumberFormat.getInstance(Locale.getDefault()).parse(item.toString()).intValue() * 100;
                    final int                         yearEnd   = yearStart + 99;
                    final List<MDM_Data.CountPerYear> dbResult  = modelData.getCountPerYear(yearStart, yearEnd);
                    final List<MDM_Data.CountPerYear> result    = new ArrayList<>(yearEnd - yearStart + 1);
                    int                               iYear     = yearStart - 1;
                    while(!dbResult.isEmpty())
                    {
                        final MDM_Data.CountPerYear tmpCPY = dbResult.remove(0);
                        for(int isYear = tmpCPY.getYear(); ++iYear < isYear; )
                        {
                            result.add(new MDM_Data.CountPerYear(iYear, 0));
                        }

                        result.add(tmpCPY);
                    }
                    for(int isYear = yearEnd + 1; ++iYear < isYear; )
                    {
                        result.add(new MDM_Data.CountPerYear(iYear, 0));
                    }
                    Dashboard.this.recycleViewAdapter.update(result);
                }
                catch(ParseException ignored)
                {
                    Log.i(CLASS_NAME, "ParseException");
                }
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
