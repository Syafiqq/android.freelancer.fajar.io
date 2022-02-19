package io.localhost.freelancer.statushukum.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.controller.adapter.SearchAdapter;
import io.localhost.freelancer.statushukum.controller.filter.SearchFilter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("StaticFieldLeak")
public class LawAndSearch extends Fragment
{
    public static final String CLASS_NAME = "Law";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Law";
    public Integer CATEGORY = -1;

    private CountPerYearAdapter yearAdapter;
    private List<MDM_Data.CountPerYear> yearList;
    private RecyclerView yearListView;
    private View root;

    private SearchView search;
    private RecyclerView searchListView;
    private SearchAdapter searchAdapter;
    private List<MDM_Data.MetadataSearchable> searchList;
    PublishRelay<Boolean> searchTextProducer = PublishRelay.create();
    private Disposable searchTextDisposable;

    private OnFragmentInteractionListener listener;
    private View progress;
    private boolean isLoading = false;
    private String title;

    public LawAndSearch()
    {
        // Required empty public constructor
    }

    public static LawAndSearch newInstance(int title)
    {
        LawAndSearch fragment = new LawAndSearch();
        Bundle args = new Bundle();
        args.putInt("title", title);
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
        return inflater.inflate(R.layout.fragment_constitution_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        progress = view.findViewById(R.id.content_progress);
        searchListView = (RecyclerView) root.findViewById(R.id.content_constitution_recycle_view_container_search);
        yearListView = (RecyclerView) root.findViewById(R.id.content_constitution_recycle_view_container_year);
        search = (SearchView) root.findViewById(R.id.content_constitution_search_filter);
        setProperty();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Integer category = null;
            if (bundle.containsKey("category")) {
                category = bundle.getInt("category", -1);
            }

            Integer finalCategory = category;
            new Handler().postDelayed(() -> updateCategory(
                    finalCategory,
                    bundle.getInt("title", R.string.activity_dashboard_toolbar_logo_title)
            ), 250);
        }

        // Search consumer
        if (searchTextDisposable != null) {
            searchTextDisposable.dispose();
        }
        searchTextDisposable = searchTextProducer.subscribeOn(Schedulers.io())
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        String textToSearch = LawAndSearch.this.search.getQuery().toString();
                        if((textToSearch.trim().length() > 0)) {
                            LawAndSearch.this.doSearch(textToSearch);
                        }
                    }
                });
        super.onViewCreated(view, savedInstanceState);
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        setLoading(true);
        setListVisibility(false);
        setSearchListAdapter();
        setYearListAdapter();

        // Search Property
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
                searchTextProducer.accept(true);
                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                LawAndSearch.this.setListVisibility(false);
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
                LawAndSearch.this.setListVisibility(true);
                search.setIconified(false);
                search.performClick();
            }
        });
    }

    private void setLoading(boolean loading) {
        if(isLoading == loading) return;
        searchListView.setVisibility(loading ? View.GONE : View.VISIBLE);
        yearListView.setVisibility(loading ? View.GONE : View.VISIBLE);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        isLoading = loading;
    }

    private void setListVisibility(boolean isSearchActive) {
        searchListView.setVisibility(isSearchActive ? View.VISIBLE : View.GONE);
        yearListView.setVisibility(isSearchActive ? View.GONE : View.VISIBLE);
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
            protected void onPreExecute() {
                setLoading(true);
            }

            @Override
            protected Void doInBackground(Void... voids)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                final MDM_Data modelData = MDM_Data.getInstance(getContext());
                final List<MDM_Data.CountPerYear> dbResult = CATEGORY == null ? modelData.getCountPerYear() : modelData.getCountPerYear((int) CATEGORY);
                yearList.clear();
                yearList.addAll(dbResult);
                yearAdapter.update(dbResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                yearAdapter.notifyDataSetChanged();
                yearAdapter.setTitle(title);
                setLoading(false);
                setListVisibility(false);
                super.onPostExecute(aVoid);
            }
        }.execute();
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
                setListVisibility(true);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public synchronized void updateCategoryAndTitle(Integer category, int title) {
        if(CATEGORY.equals(category)) return;
        CATEGORY = category;
        yearAdapter.setCategory(CATEGORY);
        this.title = listener.getTitle(title);
        listener.onFragmentChangeForTitle(this.title);
    }

    public synchronized void updateCategory(Integer category, int title) {
        updateCategoryAndTitle(category, title);
        updateCategory();
    }

    public synchronized void updateCategory() {
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
        String getTitle(int title);
        void onFragmentChangeForTitle(String title);
    }
}
