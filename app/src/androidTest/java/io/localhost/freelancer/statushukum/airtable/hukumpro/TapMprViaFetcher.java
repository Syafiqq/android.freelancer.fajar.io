package io.localhost.freelancer.statushukum.airtable.hukumpro;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.ExecutionException;

import io.localhost.freelancer.statushukum.model.AirtableDataFetcherTask;
import io.localhost.freelancer.statushukum.networking.VolleyUtil;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TapMprViaFetcher {
    @Test
    public void test_it_should_get_list_of_tap_mpr() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AirtableDataFetcherTask x = new AirtableDataFetcherTask(VolleyUtil.getInstance(context).getRequestQueue());
        x.onPreExecute();
        x.doInBackground();
    }
}
