package io.localhost.freelancer.statushukum.model;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

public class AirtableDataFetcherTask extends AsyncTask<Void, Void, AirtableDataFetcher> implements AirtableDataFetcher.InteractionListener {
    private AirtableDataFetcher fetcher;

    public AirtableDataFetcherTask(RequestQueue queue) {
        fetcher = new AirtableDataFetcher(queue, this);
    }

    @Override
    protected AirtableDataFetcher doInBackground(Void... voids) {
        return fetcher.doInBackground(voids);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fetcher.onPreExecute();
    }
}
