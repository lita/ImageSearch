package com.codepath.imagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.imagesearch.R;
import com.codepath.imagesearch.fragments.SettingsDialog;
import com.codepath.imagesearch.models.Settings;

public class SplashActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings =  new Settings();
        setContentView(R.layout.activity_splash);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_actionbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
    }

    @Override
    public void onFinishSettingsDialog(Settings settings) {
        this.settings = settings;
    }

    public void onSettings(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialog settingsDialog = SettingsDialog.newInstance(this.settings);
        settingsDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialogTheme);
        settingsDialog.show(fm, "settings");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                settings.query = query;

                if (query == null || query.isEmpty()) {
                    return true;
                }

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.putExtra("settings", settings);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
}
