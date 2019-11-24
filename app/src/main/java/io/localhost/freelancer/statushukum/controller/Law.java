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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

@SuppressLint("StaticFieldLeak")
public class Law extends Fragment
{
    public static final String CLASS_NAME = "Law";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Law";
    public static int CATEGORY = -1;

    private CountPerYearAdapter yearAdapter;
    private List<MDM_Data.CountPerYear> yearList;
    private String latestQuery;
    private RecyclerView yearListView;
    private View root;

    private OnFragmentInteractionListener listener;
    private View contentRoot;
    private View progress;
    private boolean isLoading = false;

    public Law()
    {
        // Required empty public constructor
    }

    public static Law newInstance()
    {
        Law fragment = new Law();
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
        return inflater.inflate(R.layout.fragment_constitution, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        contentRoot = view.findViewById(R.id.content_root);
        progress = view.findViewById(R.id.content_progress);
        setProperty();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setProperty()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setProperty");

        setLoading(true);
        setYearListAdapter();
        latestQuery = "";
    }

    private void setLoading(boolean loading) {
        if(isLoading == loading) return;
        contentRoot.setVisibility(loading ? View.GONE : View.VISIBLE);
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
        yearListView = (RecyclerView) root.findViewById(R.id.content_constitution_recycle_view_container_year);
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

    public synchronized void updateCategory(int category, int title) {
        if(CATEGORY == category) return;
        CATEGORY = category;
        yearAdapter.setCategory(CATEGORY);
        listener.onFragmentChangeForTitle(title);
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
        void onFragmentChangeForTitle(int string);
    }
}
