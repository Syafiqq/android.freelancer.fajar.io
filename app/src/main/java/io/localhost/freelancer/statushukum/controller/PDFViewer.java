package io.localhost.freelancer.statushukum.controller;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import io.localhost.freelancer.statushukum.R;

public class PDFViewer extends AppCompatActivity
{
    public static final String CLASS_NAME = "PDFViewer";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.PDFViewer";
    public static final String EXTRA_URI = "data";


    private PDFView view;
    private byte[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        final Intent intent = getIntent();
        this.data = intent.getByteArrayExtra(PDFViewer.EXTRA_URI);

        this.setToolbar();
        this.setProperty();
        this.setPDF();
    }

    private void setPDF()
    {
        if(data != null)
        {
            this.view.fromBytes(data)
                     .enableSwipe(true) // allows to block changing pages using swipe
                     .swipeHorizontal(false)
                     .enableDoubletap(true)
                     .defaultPage(0)// called after document is rendered for the first time
                     .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                     .password(null)
                     .scrollHandle(null)
                     .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                     .load();
        } else {
            Toast.makeText(this, "Cannot open file", Toast.LENGTH_SHORT).show();
        }
    }

    private void setToolbar()
    {


        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_pdf_viewer_toolbar);
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setContentInsetStartWithNavigation(4);
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_white_24));
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    PDFViewer.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {


        getMenuInflater().inflate(R.menu.activity_pdf_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        switch(item.getItemId())
        {
            case R.id.activity_pdf_viewer_menu_setting:
            {
                startActivity(new Intent(this, Setting.class));
                return true;
            }
            case android.R.id.home:
                //perhaps use intent if needed but i'm sure there's a specific intent action for up you can use to handle
                PDFViewer.this.onBackButtonPressed();
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

    private void setProperty()
    {
        this.view = (PDFView) super.findViewById(R.id.content_pdfviewer_pdfview_view);
    }
}
