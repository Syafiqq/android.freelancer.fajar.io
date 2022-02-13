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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

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
        yearListView = (RecyclerView) root.findViewById(R.id.content_constitution_recycle_view_container_year);
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
        super.onViewCreated(view, savedInstanceState);
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        setLoading(true);
        setYearListAdapter();
    }

    private void setLoading(boolean loading) {
        if(isLoading == loading) return;
        yearListView.setVisibility(loading ? View.GONE : View.VISIBLE);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        isLoading = loading;
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
