package com.codepath.imagesearch.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.imagesearch.R;
import com.codepath.imagesearch.adapters.ImageResultsAdapter;
import com.codepath.imagesearch.fragments.SettingsDialog;
import com.codepath.imagesearch.helpers.DialogHelpers;
import com.codepath.imagesearch.helpers.EndlessScrollListener;
import com.codepath.imagesearch.models.ImageResult;
import com.codepath.imagesearch.models.Settings;
import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {
    private EditText etQuery;
    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private static int SETTINGS_CODE = 20;
    private Settings settings;
    private static int RETRY_ATTEMPTS = 3;
    private MaterialDialog progessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                queryImage(settings.query, page, false);
            }
        });
        settings = new Settings();

        //Set up the action bar
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_actionbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settings = getIntent().getParcelableExtra("settings");
        queryImage(settings.query, 0, true);
    }

    private void setupView() {
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ImageDisplayActivty.class);
                ImageResult result = imageResults.get(position);
                i.putExtra("imageResult", result);
                startActivity(i);
            }
        });
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
                queryImage(query, 0, true);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void queryImage(String query, int currentPage, boolean clearPage) {

        progessDialog = DialogHelpers.showProgressDialog(this);

        if (query == null) {
            progessDialog.dismiss();
            DialogHelpers.showAlert(this, "Search field cannot be empty", "Search cannot be empty");
            return;
        }

        if (currentPage >= 8) {
            currentPage = (currentPage * 8) % 64;
            imageResults.addAll(imageResults.subList(currentPage, currentPage+8));
            aImageResults.notifyDataSetChanged();
            progessDialog.dismiss();
            return;
        }

        if (!isNetworkAvailable()) {
            progessDialog.dismiss();
            DialogHelpers.showAlert(this, "Network is not available", "Please connect to the internet.");
            return;
        }

        if (clearPage) {
            this.imageResults.clear();
            this.aImageResults.notifyDataSetChanged();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&q=" + query;

        if (settings.imageType.urlText != "any") searchUrl += "&imgtyp=" + settings.imageType.urlText;
        if (settings.imageColor.urlText != "any") searchUrl += "&imgcolorp=" + settings.imageColor.urlText;
        if (settings.imageSize.urlText != "any") searchUrl += "&imgsz=" + settings.imageSize.urlText;

        if (settings.sitesearch != null) {
            if (!settings.sitesearch.isEmpty()) {
                searchUrl += "&as_sitesearch=" + settings.sitesearch;
            }
        }

        searchUrl += "&start=" + Integer.toString(currentPage*8);

        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;

                int retries = 0;
                while (retries < MainActivity.RETRY_ATTEMPTS) {
                    try {
                        imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                        ArrayList<ImageResult> results = ImageResult.fromJSONArray(imageResultsJson);
                        if (results == null) {
                            retries += 1;
                            continue;
                        }
                        imageResults.addAll(results);
                        aImageResults.notifyDataSetChanged();
                        progessDialog.dismiss();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        retries += 1;
                    }
                }
                progessDialog.dismiss();

                DialogHelpers.showAlert(MainActivity.this,
                        "Network Error",
                        "Couldn't connect to Google. Please check internet connection.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progessDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to get response from server 2", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progessDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to get response from server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSettings(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialog settingsDialog = SettingsDialog.newInstance(this.settings);
        settingsDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialogTheme);
        settingsDialog.show(fm, "settings");
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return isNetworkAvailable;
    }

    @Override
    public void onFinishSettingsDialog(Settings settings) {
        this.settings = settings;
    }
}
