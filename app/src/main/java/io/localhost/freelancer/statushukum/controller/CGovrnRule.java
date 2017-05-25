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

public class CGovrnRule extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String CLASS_NAME = "CGovrnRule";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.CGovrnRule";
    public static final int CATEGORY = 2;

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

        setContentView(R.layout.activity_govrn_rule_wrapper);
        this.setToolbar();
        this.setNavigationSwipe();
        this.setProperty();
    }

    private void setNavigationSwipe()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_govrn_rule_wrapper_drawerlayout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, (Toolbar) super.findViewById(R.id.activity_govrn_rule_toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_govrn_rule_wrapper_navigationview_nav);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        this.setSearchListAdapter();
        this.setYearListAdapter();
        this.setYearList();

        this.search = (SearchView) super.findViewById(R.id.content_govrn_rule_search_filter);
        this.latestQuery = this.search.getQuery().toString();
        this.search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if((query.trim().length() > 0) && (!CGovrnRule.this.latestQuery.contentEquals(query)))
                {
                    CGovrnRule.this.latestQuery = query;
                    CGovrnRule.this.doSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                CGovrnRule.this.searchAdapter.getFilter().filter(newText);
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
                    CGovrnRule.this.yearListView.setVisibility(View.GONE);
                    CGovrnRule.this.searchListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    CGovrnRule.this.yearListView.setVisibility(View.VISIBLE);
                    CGovrnRule.this.searchListView.setVisibility(View.GONE);
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
        this.searchListView = (RecyclerView) findViewById(R.id.content_govrn_rule_recycle_view_container_search);
        this.searchAdapter = new SearchAdapter(new ArrayList<MDM_Data.MetadataSearchable>(0), this, CATEGORY);
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
                final MDM_Data modelData = MDM_Data.getInstance(CGovrnRule.this);
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(CGovrnRule.this);
                final MDM_Tag modelTag = MDM_Tag.getInstance(CGovrnRule.this);
                final List<MDM_Data.MetadataSearchable> dbResultData = modelData.getSearchableList(query, CATEGORY);
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
                CGovrnRule.this.searchList.clear();
                CGovrnRule.this.searchList.addAll(dbResultData);
                CGovrnRule.this.searchAdapter.update(dbResultData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                if(CGovrnRule.this.searchList.size() == 0)
                {
                    Toast.makeText(CGovrnRule.this, CGovrnRule.super.getResources().getString(R.string.activity_search_info_search_empty), Toast.LENGTH_SHORT).show();
                }
                CGovrnRule.this.searchAdapter.notifyDataSetChanged();
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
        this.yearListView = (RecyclerView) findViewById(R.id.content_govrn_rule_recycle_view_container_year);
        this.yearAdapter = new CountPerYearAdapter(new ArrayList<MDM_Data.CountPerYear>(0), this, CATEGORY);
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
                final MDM_Data modelData = MDM_Data.getInstance(CGovrnRule.this);
                final List<MDM_Data.CountPerYear> dbResult = modelData.getCountPerYear(CATEGORY);
                CGovrnRule.this.yearList.clear();
                CGovrnRule.this.yearList.addAll(dbResult);
                CGovrnRule.this.yearAdapter.update(dbResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                CGovrnRule.this.yearAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void setToolbar()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

        final Toolbar toolbar = (Toolbar) super.findViewById(R.id.activity_govrn_rule_toolbar);
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
                CGovrnRule.this.onBackButtonPressed();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_govrn_rule_wrapper_drawerlayout_container);
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
            case R.id.nav_menu_dashboard_rule_constitution:
            {
                this.startActivity(new Intent(this, CConstitution.class));
                super.finish();
                return true;
            }
            case R.id.nav_menu_dashboard_setting:
            {
                this.startActivity(new Intent(this, Setting.class));
                return true;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_govrn_rule_wrapper_drawerlayout_container);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
