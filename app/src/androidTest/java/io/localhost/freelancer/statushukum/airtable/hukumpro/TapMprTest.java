package io.localhost.freelancer.statushukum.airtable.hukumpro;

import android.content.Context;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import io.localhost.freelancer.statushukum.networking.VolleyUtil;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TapMprTest {
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

        callRequestRecursive(context, null);
    }

    private void callRequestRecursive(Context context, String offset) {
        String url = generateUrl(offset);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, future, future);

        VolleyUtil.getInstance(context).addToRequestQueue(request);

        try {
            JSONObject response = future.get(); // this will block
            if(response.has("offset")){
                callRequestRecursive(context, response.getString("offset"));
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
}