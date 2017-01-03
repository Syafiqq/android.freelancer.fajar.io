package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;
import java.util.Map;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.localhost.freelancer.statushukum.model.util.ME_TagAdapter;
import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class Detail extends AppCompatActivity
{
    public static final String CLASS_NAME = "Detail";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Detail";
    public static final String EXTRA_ID   = "id";
    private int          id;
    private HtmlTextView no;
    private HtmlTextView description;
    private HtmlTextView status;
    private TagView      tag;
    private TextView     tagLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        this.id = intent.getIntExtra(Detail.EXTRA_ID, -1);

        this.setToolbar();
        this.setProperty();
        this.setDetail();
    }

    private void setProperty()
    {
        this.no = (HtmlTextView) super.findViewById(R.id.content_detail_htv_no);
        this.description = (HtmlTextView) super.findViewById(R.id.content_detail_htv_description);
        this.status = (HtmlTextView) super.findViewById(R.id.content_detail_htv_status);
        this.tag = (TagView) super.findViewById(R.id.content_detail_tv_tag);
        this.tagLabel = (TextView) super.findViewById(R.id.content_detail_tv_tag_label);
        this.tag.setOnTagClickListener(new OnTagClickListener()
        {
            @Override
            public void onTagClick(int i, Tag tag)
            {
                if(tag instanceof ME_TagAdapter)
                {
                    Toast.makeText(Detail.this, ((ME_TagAdapter) tag).description, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setToolbar()
    {


        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_detail_toolbar);
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setContentInsetStartWithNavigation(4);
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.content_toolbar_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Detail.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {


        getMenuInflater().inflate(R.menu.activity_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        switch(item.getItemId())
        {
            case R.id.activity_detail_menu_setting:
            {
                startActivity(new Intent(this, Setting.class));
                return true;
            }
            case android.R.id.home:
                //perhaps use intent if needed but i'm sure there's a specific intent action for up you can use to handle
                Detail.this.onBackButtonPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBackButtonPressed()
    {


        this.onBackPressed();
    }

    @Override
    public void onBackPressed()
    {


        super.finish();
    }

    @SuppressWarnings("ConstantConditions")
    private void setDetail()
    {


        new AsyncTask<Void, Void, Void>()
        {

            List<Integer> dbResultTagID;
            Map<Integer, ME_Tag> dbResultTag;
            ME_Data dbResultData;

            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data    modelData    = MDM_Data.getInstance(Detail.this);
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(Detail.this);
                final MDM_Tag     modelTag     = MDM_Tag.getInstance(Detail.this);
                this.dbResultData = modelData.getFromID(Detail.this.id);
                this.dbResultTag = modelTag.getAll();
                this.dbResultTagID = modelDataTag.getTagFromDataID(Detail.this.id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                Detail.this.tag.removeAllTags();
                if(this.dbResultData != null)
                {
                    Detail.this.no.setHtml(this.dbResultData.getNo());
                    Detail.this.description.setHtml(this.dbResultData.getDescription().equalsIgnoreCase("null") ? "-" : this.dbResultData.getDescription());
                    Detail.this.status.setHtml(this.dbResultData.getStatus().equalsIgnoreCase("null") ? "-" : this.dbResultData.getStatus());

                    if(!dbResultTagID.isEmpty())
                    {
                        Detail.this.tag.setVisibility(View.VISIBLE);
                        Detail.this.tagLabel.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Detail.this.tag.setVisibility(View.GONE);
                        Detail.this.tagLabel.setVisibility(View.GONE);
                    }
                    while(!dbResultTagID.isEmpty())
                    {
                        Detail.this.tag.addTag(new ME_TagAdapter(dbResultTag.get(dbResultTagID.remove(0)), 12f));
                    }
                }
                else
                {
                    Detail.this.no.setText("-");
                    Detail.this.description.setText("-");
                    Detail.this.status.setText("-");
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    protected void onPostResume()
    {

        this.setDetail();

        super.onPostResume();
    }
}
