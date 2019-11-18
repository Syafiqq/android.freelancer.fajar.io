package io.localhost.freelancer.statushukum.airtable.hukumpro;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.localhost.freelancer.statushukum.model.entity.ME_Data;
import io.localhost.freelancer.statushukum.networking.VolleyUtil;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TapMprTest {
    private Pattern yearPattern = Pattern.compile("Tahun ([0-9]{4})", Pattern.CASE_INSENSITIVE);
    @Test
    public void test_it_should_get_list_of_tap_mpr() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String url = generateUrl(null);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, future, future);

        VolleyUtil.getInstance(context).addToRequestQueue(request);

        try {
            JSONObject response = future.get(); // this will block
            int a= 10;
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        }
    }


    @Test
    public void test_it_should_get_list_of_tap_mpr_recursively() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LinkedList<ME_Data> list = new LinkedList<>();
        callRequestRecursive(context, null, list);
        list = list;
    }

    private void callRequestRecursive(Context context, String offset, List<ME_Data> collector) {
        String url = generateUrl(offset);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, future, future);

        VolleyUtil.getInstance(context).addToRequestQueue(request);

        try {
            JSONObject response = future.get(); // this will block
            if(response.has("records")){
                JSONArray records = response.optJSONArray("records");
                if(records != null)
                    for (int i = -1, is = records.length(); ++i < is;) {
                        JSONObject record = records.optJSONObject(i);
                        if(record != null) {
                            JSONObject fields = record.optJSONObject("fields");
                            if(fields != null) {
                                String rawYear = parseSingle(fields.optString("NOMOR", ""), yearPattern);
                                int year = parseInt(rawYear, -1);
                                if(year == -1) continue;
                                collector.add(new ME_Data(
                                        0,
                                        year,
                                        sanitizeString(fields.optString("NOMOR")),
                                        sanitizeString(fields.optString("TENTANG")),
                                        sanitizeString(fields.optString("STATUS")),
                                        1,
                                        sanitizeString(fields.optString("DOWNLOAD"))
                                ));
                            }
                        }
                    }
            }
            if(response.has("offset")){
                Thread.sleep(400);
                callRequestRecursive(context, response.getString("offset"), collector);
            }
        } catch (InterruptedException e) {
            int a = 10;
        } catch (ExecutionException e) {
            int a = 10;
        } catch (JSONException e) {
            int a = 10;
        }

    }

    private String generateUrl(String offset) {
        //":///v0/appCjxqN70qCRThpX/TAP%20MPR"
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.airtable.com")
                .appendPath("v0")
                .appendPath("appCjxqN70qCRThpX")
                .appendPath("TAP MPR")
                .appendQueryParameter("fields", "NOMOR")
                .appendQueryParameter("fields", "TENTANG")
                .appendQueryParameter("fields", "STATUS")
                .appendQueryParameter("fields", "DOWNLOAD")
                .appendQueryParameter("view", "Grid view")
                .appendQueryParameter("api_key", "keypy7sFHBs2xvvSe");
        if(offset != null) {
            builder.appendQueryParameter("offset", offset);
        }
        return builder.build().toString();
    }

    private String parseSingle(String value, Pattern pattern) {
        final Matcher m = pattern.matcher(value);

        if (m.find()) {
            try {
                return m.group(1);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private int parseInt(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return defaultVal;
        }
    }

    private String sanitizeString(String s) {
        if(s == null)
            return null;
        else {
            String t = s.trim();
            if(TextUtils.isEmpty(t))
                return null;
            return t;
        }
    }
}