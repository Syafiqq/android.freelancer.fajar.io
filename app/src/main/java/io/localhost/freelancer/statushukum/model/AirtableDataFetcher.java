package io.localhost.freelancer.statushukum.model;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.localhost.freelancer.statushukum.model.converter.JsonObjectToMeData;
import io.localhost.freelancer.statushukum.model.proceeder.AirTableResponseProceeder;

public class AirtableDataFetcher {
    private final InteractionListener listener;
    private RequestQueue queue;
    private List<JSONObject> data;
    public Exception ex;
    private JsonObjectToMeData converter;
    private AirTableResponseProceeder proceeder;

    public AirtableDataFetcher(RequestQueue queue, InteractionListener listener) {
        this.queue = queue;
        this.listener = listener;
    }

    public void onPreExecute() {
        data = new LinkedList<>();
        converter = new JsonObjectToMeData(0);
        proceeder = new AirTableResponseProceeder(converter, data);
    }

    public AirtableDataFetcher doInBackground(Void... voids) {
        String[] tables = {"TAP MPR", "UU", "UU DARURAT", "PERPU", "PP", "PERPRES"};
        for (int i = 0; i < tables.length; i++) {
            String table = tables[i];
            LinkedList<JSONObject> tempStorage = new LinkedList<>();

            converter.setCategory(i + 1);
            proceeder.setCollector(tempStorage);

            for (int r1 = 0; r1 < 3; r1++) {
                tempStorage.clear();

                if (this.listener.isCancelled()) return this;
                try {
                    callRequestRecursive(table, null);
                    Thread.sleep(400);
                    break;
                } catch (Exception e) {
                    if(this.listener.isCancelled()) {
                        return this;
                    }
                    if (r1 == 2) {
                        ex = e;
                        return this;
                    }
                }
            }
            data.addAll(tempStorage);
            tempStorage.clear();
        }
        int c = 0;
        for (JSONObject datum: data) {
            try {
                datum.put("id", ++c);
            } catch (JSONException e) {
                return this;
            }
        }
        return this;
    }

    private void callRequestRecursive(String table, String offset) throws ExecutionException, InterruptedException, JSONException {
        String url = generateUrl(table, offset);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);

        queue.add(request);

        JSONObject response = future.get(); // this will block
        proceeder.proceed(response);
        if (response.has("offset")) {
            Thread.sleep(400);
            if(this.listener.isCancelled())
                throw new InterruptedException();
            callRequestRecursive(table, response.getString("offset"));
        }
    }

    public JSONArray getData() {
        return new JSONArray(data);
    }

    private String generateUrl(String table, String offset) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.airtable.com")
                .appendPath("v0")
                .appendPath("appCjxqN70qCRThpX")
                .appendPath(table)
                .appendQueryParameter("fields", "NOMOR")
                .appendQueryParameter("fields", "TENTANG")
                .appendQueryParameter("fields", "STATUS")
                .appendQueryParameter("fields", "DOWNLOAD")
                .appendQueryParameter("view", "Grid view")
                .appendQueryParameter("api_key", "keypy7sFHBs2xvvSe");
        if (offset != null) {
            builder.appendQueryParameter("offset", offset);
        }
        return builder.build().toString();
    }

    public static interface InteractionListener {
        boolean isCancelled();
    }
}