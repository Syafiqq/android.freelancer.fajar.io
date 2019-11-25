package io.localhost.freelancer.statushukum.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

@SuppressLint("StaticFieldLeak")
public class SearchFragment extends Fragment
{
    public static final String CLASS_NAME = "SearchFragment";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.SearchFragment";
    public static int CATEGORY = -1;

    private SearchView search;
    private String latestQuery;
    private RecyclerView searchListView;
    private SearchAdapter searchAdapter;
    private List<MDM_Data.MetadataSearchable> searchList;
    private View root;

    private OnFragmentInteractionListener listener;
    private View contentRoot;
    private View progress;
    private boolean isLoading = false;

    public SearchFragment()
    {
        // Required empty public constructor
    }

    public static SearchFragment newInstance()
    {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        contentRoot = view.findViewById(R.id.content_root);
        progress = view.findViewById(R.id.content_progress);
        setProperty();
        super.onViewCreated(view, savedInstanceState);
    }


    private void setLoading(boolean loading) {
        if(isLoading == loading) return;
        searchListView.setVisibility(loading ? View.GONE : View.VISIBLE);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        isLoading = loading;
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        setSearchListAdapter();

        search = (SearchView) root.findViewById(R.id.content_constitution_search_filter);
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
            }
        });
        View searchRoot = root.findViewById(R.id.search_root);
        searchRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
                search.performClick();
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
        searchListView = (RecyclerView) root.findViewById(R.id.content_constitution_recycle_view_container_search);
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
            protected void onPreExecute() {
                super.onPreExecute();
                setLoading(true);
            }

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
                setLoading(false);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.listener.onFragmentChangeForTitle(R.string.content_setting_s_search);
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

    public synchronized void updateContent() {
        doSearch(latestQuery);
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentChangeForTitle(int string);
    }
}
