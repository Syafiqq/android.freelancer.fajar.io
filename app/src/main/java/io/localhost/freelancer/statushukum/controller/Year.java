package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.YearListAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;

public class Year extends AppCompatActivity
{
    public static final String CLASS_NAME = "Year";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Year";
    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_CATEGORY = "count";
    public static final String EXTRA_TITLE = "title";
    private int year;
    private YearListAdapter yearListAdapter;
    private List<MDM_Data.YearMetadata> entryList;
    private Integer category;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_year);
        Intent intent = getIntent();
        this.year = intent.getIntExtra(Year.EXTRA_YEAR, -1);
        if (intent.hasExtra(Year.EXTRA_CATEGORY)) {
            this.category = intent.getIntExtra(Year.EXTRA_CATEGORY, -1);
        } else {
            this.category = null;
        }
        this.title = intent.hasExtra(Year.EXTRA_TITLE) ? intent.getStringExtra(Year.EXTRA_TITLE) : getResources().getString(R.string.activity_year_toolbar_logo_title);

        this.setToolbar();
        this.setProperty();
        this.setYearListAdapter();
        this.setYearList();
    }

    private void setProperty()
    {


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
    }

    private void setToolbar()
    {


        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_year_toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(title);
        super.setSupportActionBar(toolbar);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    Year.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        switch(item.getItemId())
        {
            case R.id.activity_year_menu_setting:
            {
                startActivity(new Intent(this, Setting.class));
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


        this.onBackPressed();
    }

    @Override
    public void onBackPressed()
    {


        super.finish();
    }

    private void setYearListAdapter()
    {


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_year_recycle_view_container);
        this.yearListAdapter = new YearListAdapter(new ArrayList<MDM_Data.YearMetadata>(0), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this.yearListAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private synchronized void setYearList()
    {


        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(Year.this);
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(Year.this);
                final MDM_Tag modelTag = MDM_Tag.getInstance(Year.this);
                final List<MDM_Data.YearMetadata> dbResultData = Year.this.category == null ? modelData.getYearList(Year.this.year) : modelData.getYearList(Year.this.year, Year.this.category);
                final Map<Integer, ME_Tag> dbResultTag = modelTag.getAll();
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
                Year.this.yearListAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    protected void onPostResume()
    {

        this.setYearList();

        super.onPostResume();
    }
}
