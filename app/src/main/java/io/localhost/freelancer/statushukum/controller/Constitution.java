package io.localhost.freelancer.statushukum.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Constitution extends Fragment
{
    public static final String CLASS_NAME = "Constitution";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Constitution";
    public static final int CATEGORY = 1;

    private CountPerYearAdapter yearAdapter;
    private List<MDM_Data.CountPerYear> yearList;
    private SearchView search;
    private String latestQuery;
    private RecyclerView yearListView;
    private RecyclerView searchListView;
    private SearchAdapter searchAdapter;
    private List<MDM_Data.MetadataSearchable> searchList;
    private View root;

    private OnFragmentInteractionListener listener;

    public Constitution()
    {
        // Required empty public constructor
    }

    public static Constitution newInstance()
    {
        Constitution fragment = new Constitution();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.root = inflater.inflate(R.layout.fragment_constitution, container, false);
        this.setProperty();
        this.listener.onFragmentChangeForTitle(R.string.nav_header_dashboard_drawer_rule_constitution);
        return root;
    }

    private void setProperty()
    {


        this.setSearchListAdapter();
        this.setYearListAdapter();

        this.search = (SearchView) this.root.findViewById(R.id.content_constitution_search_filter);
        this.latestQuery = this.search.getQuery().toString();
        this.search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if((query.trim().length() > 0) && (!Constitution.this.latestQuery.contentEquals(query)))
                {
                    Constitution.this.latestQuery = query;
                    Constitution.this.doSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Constitution.this.searchAdapter.getFilter().filter(newText);
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
                    Constitution.this.yearListView.setVisibility(View.GONE);
                    Constitution.this.searchListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    Constitution.this.yearListView.setVisibility(View.VISIBLE);
                    Constitution.this.searchListView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setSearchListAdapter()
    {


        if(this.searchList == null)
        {
            this.searchList = new LinkedList<>();
        }
        else
        {
            this.searchList.clear();
        }
        this.searchListView = (RecyclerView) this.root.findViewById(R.id.content_constitution_recycle_view_container_search);
        this.searchAdapter = new SearchAdapter(new ArrayList<MDM_Data.MetadataSearchable>(0), super.getContext(), CATEGORY);
        this.searchAdapter.setFilter(new SearchFilter(this.searchAdapter, this.searchList));
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getContext());
        this.searchListView.setLayoutManager(mLayoutManager);
        this.searchListView.setItemAnimator(new DefaultItemAnimator());
        this.searchListView.setAdapter(this.searchAdapter);
    }

    private void doSearch(final String query)
    {


        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(Constitution.super.getContext());
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(Constitution.super.getContext());
                final MDM_Tag modelTag = MDM_Tag.getInstance(Constitution.super.getContext());
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
                Constitution.this.searchList.clear();
                Constitution.this.searchList.addAll(dbResultData);
                Constitution.this.searchAdapter.update(dbResultData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                if(Constitution.this.searchList.size() == 0)
                {
                    Toast.makeText(Constitution.super.getContext(), Constitution.super.getResources().getString(R.string.activity_search_info_search_empty), Toast.LENGTH_SHORT).show();
                }
                Constitution.this.searchAdapter.notifyDataSetChanged();
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
        this.yearListView = (RecyclerView) this.root.findViewById(R.id.content_constitution_recycle_view_container_year);
        this.yearAdapter = new CountPerYearAdapter(new ArrayList<MDM_Data.CountPerYear>(0), super.getContext(), CATEGORY);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getContext());
        this.yearListView.setLayoutManager(mLayoutManager);
        this.yearListView.setItemAnimator(new DefaultItemAnimator());
        this.yearListView.setAdapter(this.yearAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private synchronized void setYearList()
    {


        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(Constitution.super.getContext());
                final List<MDM_Data.CountPerYear> dbResult = modelData.getCountPerYear(CATEGORY);
                Constitution.this.yearList.clear();
                Constitution.this.yearList.addAll(dbResult);
                Constitution.this.yearAdapter.update(dbResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                Constitution.this.yearAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.setYearList();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener)
        {
            listener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentChangeForTitle(int string);
    }
}
