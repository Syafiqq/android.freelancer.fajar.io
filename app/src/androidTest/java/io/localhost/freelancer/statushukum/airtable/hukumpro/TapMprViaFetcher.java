package io.localhost.freelancer.statushukum.airtable.hukumpro;


import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.localhost.freelancer.statushukum.model.AirtableDataFetcher;
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
        AirtableDataFetcher x = new AirtableDataFetcher(VolleyUtil.getInstance(context).getRequestQueue());
        x.onPreExecute();
        x.doInBackground();
    }
}
