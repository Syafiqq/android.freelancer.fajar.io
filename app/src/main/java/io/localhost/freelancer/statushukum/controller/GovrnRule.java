package io.localhost.freelancer.statushukum.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
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

public class GovrnRule extends Fragment
{
    public static final String CLASS_NAME = "GovrnRule";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.GovrnRule";
    public static final int CATEGORY = 2;

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

    public GovrnRule()
    {
        // Required empty public constructor
    }

    public static GovrnRule newInstance()
    {
        GovrnRule fragment = new GovrnRule();
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
        root = inflater.inflate(R.layout.fragment_govrn_rule, container, false);
        setProperty();
        listener.onFragmentChangeForTitle(R.string.nav_header_dashboard_drawer_rule_govrn_rule);
        return root;
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        setSearchListAdapter();
        setYearListAdapter();

        search = (SearchView) root.findViewById(R.id.content_govrn_rule_search_filter);
        latestQuery = search.getQuery().toString();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if((query.trim().length() > 0) && (!latestQuery.contentEquals(query)))
                {
                    latestQuery = query;
                    doSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean isOnFocus)
            {
                System.out.println(isOnFocus);
                if(isOnFocus)
                {
                    yearListView.setVisibility(View.GONE);
                    searchListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    yearListView.setVisibility(View.VISIBLE);
                    searchListView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setSearchListAdapter()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setSearchListAdapter");

        if(searchList == null)
        {
            searchList = new LinkedList<>();
        }
        else
        {
            searchList.clear();
        }
        searchListView = (RecyclerView) root.findViewById(R.id.content_govrn_rule_recycle_view_container_search);
        searchAdapter = new SearchAdapter(new ArrayList<MDM_Data.MetadataSearchable>(0), getContext(), CATEGORY);
        searchAdapter.setFilter(new SearchFilter(searchAdapter, searchList));
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        searchListView.setLayoutManager(mLayoutManager);
        searchListView.setItemAnimator(new DefaultItemAnimator());
        searchListView.setAdapter(searchAdapter);
    }

    private void doSearch(final String query)
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".doSearch");

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(getContext());
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(getContext());
                final MDM_Tag modelTag = MDM_Tag.getInstance(getContext());
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
                searchList.clear();
                searchList.addAll(dbResultData);
                searchAdapter.update(dbResultData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                if(searchList.size() == 0)
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.activity_search_info_search_empty), Toast.LENGTH_SHORT).show();
                }
                searchAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void setYearListAdapter()
    {
        if(yearList == null)
        {
            yearList = new LinkedList<>();
        }
        else
        {
            yearList.clear();
        }
        yearListView = (RecyclerView) root.findViewById(R.id.content_govrn_rule_recycle_view_container_year);
        yearAdapter = new CountPerYearAdapter(new ArrayList<MDM_Data.CountPerYear>(0), getContext(), CATEGORY);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        yearListView.setLayoutManager(mLayoutManager);
        yearListView.setItemAnimator(new DefaultItemAnimator());
        yearListView.setAdapter(yearAdapter);
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
                final MDM_Data modelData = MDM_Data.getInstance(getContext());
                final List<MDM_Data.CountPerYear> dbResult = modelData.getCountPerYear(CATEGORY);
                yearList.clear();
                yearList.addAll(dbResult);
                yearAdapter.update(dbResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                yearAdapter.notifyDataSetChanged();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setYearList();
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
