package io.localhost.freelancer.statushukum.controller;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.MenuModelType;
import io.localhost.freelancer.statushukum.model.util.Setting;
import io.localhost.freelancer.statushukum.model.util.SyncMessage;
import io.reactivex.disposables.CompositeDisposable;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LawAndSearch.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";

    public DrawerLayout drawer;
    public Toolbar toolbar;
    private TextView toolbarTitle;
    private ProgressDialog progressBar;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_wrapper);

        this.setToolbar();
        this.setNavigationSwipe();
        this.setProgressView();
        this.updateLawMenuVisibility();

        if(savedInstanceState == null)
        {
            LawAndSearch fragment = LawAndSearch.newInstance(R.string.title_application_name);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_dashboard_root, fragment, LawAndSearch.CLASS_PATH)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void onGPlusSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.gPlus.getGPlusIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void onInstagramSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.instagram.getInstagramIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onTwitterSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.twitter.getTwitterIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFacebookSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.facebook.getFacebookIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void setProgressView()
    {
        this.progressBar = new ProgressDialog(Dashboard.this);
        progressBar.setMessage(super.getResources().getString(R.string.content_setting_s_sync_label_desc));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgressPercentFormat(null);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(false);
    }

    @Override
    public void onBackPressed()
    {
        if(this.drawer.isDrawerOpen(GravityCompat.START))
        {
            this.drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onOptionsItemSelected");

        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                Dashboard.this.onBackButtonPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        item.setCheckable(false);
        switch(id)
        {
            case R.id.nav_menu_dashboard_search:
            {
                updateSearchContent();
            }
            break;
            case R.id.nav_menu_dashboard_sync:
            {
                this.onBackPressed();

                Observer update = new Observer() {
                    Boolean isIndeterminate = null;
                    @Override
                    public void update(Observable o, Object arg) {
                        if(!(arg instanceof SyncMessage)) return;
                        SyncMessage syncMessage = (SyncMessage) arg;
                        if(isIndeterminate == null || isIndeterminate != syncMessage.isIndeterminate()) {
                            progressBar.dismiss();
                            progressBar = new ProgressDialog(Dashboard.this);
                            progressBar.setProgressPercentFormat(null);
                            isIndeterminate = syncMessage.isIndeterminate();
                            if(isIndeterminate) {
                                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            }
                            else {
                                progressBar.setMax(syncMessage.getMax());
                                progressBar.setProgress(0);
                                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                            }
                            progressBar.setMessage(syncMessage.getMessage());
                            progressBar.setIndeterminate(isIndeterminate);
                            progressBar.show();
                        }
                        progressBar.setProgress(syncMessage.getCurrent());
                        progressBar.setMessage(syncMessage.getMessage());
                    }
                };
                AsyncTask<Void, Object, Void> task = Setting.doSync(
                        this,
                        () -> new Handler().postDelayed(this::updateContent, 500),
                        null,
                        () -> Dashboard.this.progressBar.dismiss(),
                        update,
                        Dashboard.this,
                        disposable);
                task.execute();
                return true;
            }
            case R.id.nav_menu_dashboard_about:
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hukumpro.com/about"));
                startActivity(browserIntent);
            }
            break;
        }

        this.onBackPressed();
        return true;
    }

    private void setToolbar()
    {
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

        this.toolbar = (Toolbar) super.findViewById(R.id.activity_dashboard_toolbar);
        this.toolbarTitle = (TextView) this.toolbar.findViewById(R.id.activity_dashboard_toolbar_title);
        super.setSupportActionBar(this.toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false);
            this.toolbar.setContentInsetStartWithNavigation(4);
        }
    }

    private void updateContent() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_dashboard_root);
        if(fragment instanceof LawAndSearch)
            ((LawAndSearch) fragment).updateCategory();
        else if(fragment instanceof SearchFragment)
            ((SearchFragment) fragment).updateContent();
    }

    private void updateSearchContent() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_dashboard_root);
        if(!(fragment instanceof SearchFragment)) {
            fragment = getSupportFragmentManager().findFragmentByTag(SearchFragment.CLASS_PATH);
            if(fragment == null) {
                fragment = SearchFragment.newInstance();
            } else {
                ((SearchFragment) fragment).updateContent();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_dashboard_root, fragment, SearchFragment.CLASS_PATH)
                    .addToBackStack(null)
                    .commit();
        } else {
            ((SearchFragment) fragment).updateContent();
        }
    }

    private void setNavigationSwipe()
    {
        this.drawer = (DrawerLayout) findViewById(R.id.activity_dashboard_wrapper_drawerlayout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_dashboard_wrapper_navigationview_nav);
        ImageButton button = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.nav_header_dashboard_imagebutton_back);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dashboard.this.onBackPressed();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void onBackButtonPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackButtonPressed");

        this.onBackPressed();
    }

    @Override
    public void onFragmentChangeForTitle(String title)
    {
        this.toolbarTitle.setText(title);
    }

    @Override
    public String getTitle(int title) {
        return super.getResources().getString(title);
    }

    private void updateLawMenuVisibility() {
    }
}
