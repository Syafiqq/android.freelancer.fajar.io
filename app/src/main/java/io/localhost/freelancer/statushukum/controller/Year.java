package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.YearListAdapter;
import io.localhost.freelancer.statushukum.controller.filter.YearListFilter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;

public class Year extends AppCompatActivity
{
    public static final String CLASS_NAME      = "Year";
    public static final String CLASS_PATH      = "io.localhost.freelancer.statushukum.controller.Year";
    public static final String EXTRA_YEAR      = "year";
    public static final String EXTRA_YEAR_SIZE = "count";
    private int             year;
    private int             yearSize;
    private YearListAdapter yearListAdapter;
    private boolean isSyncOperated = false;
    private List<MDM_Data.YearMetadata> entryList;
    private SearchView                  search;


    private void doSync()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".doSync");
        if(!this.isSyncOperated)
        {
            this.isSyncOperated = true;
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
                            Year.super.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    switch((Integer) o)
                                    {
                                        case io.localhost.freelancer.statushukum.model.util.Setting.SYNC_FAILED:
                                        {
                                            Toast.makeText(Year.this, Year.super.getString(R.string.system_setting_server_version_error), Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                        case io.localhost.freelancer.statushukum.model.util.Setting.SYNC_SUCCESS:
                                        {
                                            Toast.makeText(Year.this, Year.super.getString(R.string.system_setting_server_version_success), Toast.LENGTH_SHORT).show();
                                            Year.this.setYearList();
                                        }
                                        break;
                                        case io.localhost.freelancer.statushukum.model.util.Setting.SYNC_EQUAL:
                                        {
                                            Toast.makeText(Year.this, Year.super.getString(R.string.system_setting_server_version_equal), Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    }
                                }
                            });
                        }
                    };
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... voids)
                {
                    io.localhost.freelancer.statushukum.model.util.Setting.getInstance(Year.this).doSync(this.callback);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid)
                {
                    Year.this.isSyncOperated = false;
                }
            }.execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_year);
        Intent intent = getIntent();
        this.year = intent.getIntExtra(Year.EXTRA_YEAR, -1);
        this.yearSize = intent.getIntExtra(Year.EXTRA_YEAR_SIZE, -1);

        this.setToolbar();
        this.setProperty();
        this.setYearListAdapter();
        this.setYearList();
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
        this.setYearListAdapter();
        this.setYearList();
        this.search = (SearchView) super.findViewById(R.id.content_year_search_filter);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Year.this.yearListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void setToolbar()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_year_toolbar);
        super.setSupportActionBar(toolbar);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    Year.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.activity_year_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onOptionsItemSelected");

        switch(item.getItemId())
        {
            case R.id.activity_year_menu_setting:
            {
                startActivity(new Intent(this, Setting.class));
                return true;
            }
            case R.id.activity_year_menu_sync:
            {
                this.doSync();
                return true;
            }
            case R.id.activity_year_menu_refresh:
            {
                this.setYearList();
                return true;
            }
            case android.R.id.home:
                //perhaps use intent if needed but i'm sure there's a specific intent action for up you can use to handle
                Year.this.onBackButtonPressed();
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

    private void setYearListAdapter()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setYearListAdapter");

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_year_recycle_view_container);
        this.yearListAdapter = new YearListAdapter(new ArrayList<MDM_Data.YearMetadata>(0), this);
        this.yearListAdapter.setFilter(new YearListFilter(this.yearListAdapter, this.entryList));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this.yearListAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private void setYearList()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setYearList");

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data                    modelData    = MDM_Data.getInstance(Year.this);
                final MDM_DataTag                 modelDataTag = MDM_DataTag.getInstance(Year.this);
                final MDM_Tag                     modelTag     = MDM_Tag.getInstance(Year.this);
                final List<MDM_Data.YearMetadata> dbResultData = modelData.getYearList(Year.this.year, Year.this.yearSize);
                final Map<Integer, ME_Tag>        dbResultTag  = modelTag.getAll();
                for(final MDM_Data.YearMetadata result : dbResultData)
                {
                    if(result.getTagSize() > 0)
                    {
                        final List<Integer> dbResultTagID = modelDataTag.getTagFromDataID(result.getId());
                        for(int tagId : dbResultTagID)
                        {
                            result.add(dbResultTag.get(tagId));
                        }
                    }
                }
                Year.this.entryList.clear();
                Year.this.entryList.addAll(dbResultData);
                Year.this.yearListAdapter.update(dbResultData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                Year.this.search.setQuery(Year.this.search.getQuery(), true);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
}
