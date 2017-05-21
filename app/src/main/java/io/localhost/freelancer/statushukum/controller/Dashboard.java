package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.controller.adapter.SearchAdapter;
import io.localhost.freelancer.statushukum.controller.filter.SearchFilter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";

    private CountPerYearAdapter yearAdapter;
    private List<MDM_Data.CountPerYear> yearList;
    private SearchView search;
    private String latestQuery;
    private RecyclerView yearListView;
    private RecyclerView searchListView;
    private SearchAdapter searchAdapter;
    private List<MDM_Data.MetadataSearchable> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_dashboard_wrapper);
        this.setToolbar();
        this.setNavigationSwipe();
        this.setProperty();
    }

    private void setNavigationSwipe()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_dashboard_wrapper_drawerlayout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, (Toolbar) super.findViewById(R.id.activity_dashboard_toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_dashboard_wrapper_navigationview_nav);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        this.setSearchListAdapter();
        this.setYearListAdapter();
        this.setYearList();

        this.search = (SearchView) super.findViewById(R.id.content_dashboard_search_filter);
        this.latestQuery = this.search.getQuery().toString();
        this.search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if((query.trim().length() > 0) && (!Dashboard.this.latestQuery.contentEquals(query)))
                {
                    Dashboard.this.latestQuery = query;
                    Dashboard.this.doSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Dashboard.this.searchAdapter.getFilter().filter(newText);
                return false;
            }
        });
        this.search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean isOnFocus)
            {
                System.out.println(isOnFocus);
                if(isOnFocus)
                {
                    Dashboard.this.yearListView.setVisibility(View.GONE);
                    Dashboard.this.searchListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    Dashboard.this.yearListView.setVisibility(View.VISIBLE);
                    Dashboard.this.searchListView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setSearchListAdapter()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setSearchListAdapter");

        if(this.searchList == null)
        {
            this.searchList = new LinkedList<>();
        }
        else
        {
            this.searchList.clear();
        }
        this.searchListView = (RecyclerView) findViewById(R.id.content_dashboard_recycle_view_container_search);
        this.searchAdapter = new SearchAdapter(new ArrayList<MDM_Data.MetadataSearchable>(0), this);
        this.searchAdapter.setFilter(new SearchFilter(this.searchAdapter, this.searchList));
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getApplicationContext());
        this.searchListView.setLayoutManager(mLayoutManager);
        this.searchListView.setItemAnimator(new DefaultItemAnimator());
        this.searchListView.setAdapter(this.searchAdapter);
    }

    private void doSearch(final String query)
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".doSearch");

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(Dashboard.this);
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(Dashboard.this);
                final MDM_Tag modelTag = MDM_Tag.getInstance(Dashboard.this);
                final List<MDM_Data.MetadataSearchable> dbResultData = modelData.getSearchableList(query);
                final Map<Integer, ME_Tag> dbResultTag = modelTag.getAll();
                for(final MDM_Data.MetadataSearchable result : dbResultData)
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
                Dashboard.this.searchList.clear();
                Dashboard.this.searchList.addAll(dbResultData);
                Dashboard.this.searchAdapter.update(dbResultData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                if(Dashboard.this.searchList.size() == 0)
                {
                    Toast.makeText(Dashboard.this, Dashboard.super.getResources().getString(R.string.activity_search_info_search_empty), Toast.LENGTH_SHORT).show();
                }
                Dashboard.this.searchAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void setYearListAdapter()
    {
        if(this.yearList == null)
        {
            this.yearList = new LinkedList<>();
        }
        else
        {
            this.yearList.clear();
        }
        this.yearListView = (RecyclerView) findViewById(R.id.content_dashboard_recycle_view_container_year);
        this.yearAdapter = new CountPerYearAdapter(new ArrayList<MDM_Data.CountPerYear>(0), this);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getApplicationContext());
        this.yearListView.setLayoutManager(mLayoutManager);
        this.yearListView.setItemAnimator(new DefaultItemAnimator());
        this.yearListView.setAdapter(this.yearAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private synchronized void setYearList()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setYearList");

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(Dashboard.this);
                final List<MDM_Data.CountPerYear> dbResult = modelData.getCountPerYear();
                Dashboard.this.yearList.clear();
                Dashboard.this.yearList.addAll(dbResult);
                Dashboard.this.yearAdapter.update(dbResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                Dashboard.this.yearAdapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onOptionsItemSelected");

        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                Dashboard.this.onBackButtonPressed();
                return true;
            }
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
        this.setYearList();

        super.onPostResume();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackPressed");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_dashboard_wrapper_drawerlayout_container);
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.nav_menu_common_perpu:
            {
                this.startActivity(new Intent(this, GovrnRule.class));
                super.finish();
                return true;
            }
            case R.id.nav_menu_common_setting:
            {
                this.startActivity(new Intent(this, Setting.class));
                return true;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_dashboard_wrapper_drawerlayout_container);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
