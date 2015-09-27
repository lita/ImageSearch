package com.codepath.imagesearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.imagesearch.R;
import com.codepath.imagesearch.helpers.DialogHelpers;
import com.codepath.imagesearch.helpers.TouchImageView;
import com.codepath.imagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDisplayActivty extends AppCompatActivity {
    MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display_activty);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_actionbar));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView tvTitle = (TextView) findViewById(R.id.tvAction);
        tvTitle.setText("Full Image");

        ImageResult imageResult = getIntent().getParcelableExtra("imageResult");
        TouchImageView ivImageResult = (TouchImageView) findViewById(R.id.ivImageResult);

        progressDialog = DialogHelpers.showProgressDialog(this);

        ivImageResult.setImageResource(0);
        Picasso.with(this).load(imageResult.fullUrl).placeholder(R.drawable.placeholder).into(ivImageResult, new Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                DialogHelpers.showAlert(ImageDisplayActivty.this, "Error loading image", "We are having trouble loading image. Please try a different search.");
                progressDialog.dismiss();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display_activty, menu);
        return true;
    }

    public void onShareImage(MenuItem item) {
        ImageView ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
        Uri bmpUri = getLocalBitmapUri(ivImageResult);
        if (bmpUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(this, "Unable to share this image. You might be out of memory", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Given an ImageView, store the image into external storage and return a URI path.
     * @param ivImageResult
     * @return Uri|null
     */
    private Uri getLocalBitmapUri(ImageView ivImageResult) {
        Drawable drawable = ivImageResult.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) ivImageResult.getDrawable()).getBitmap();
        } else {
            return null;
        }

        Uri bmpUri = null;
        try {
            String filename = "share_image_result_" + System.currentTimeMillis() + ".png";
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), filename);
            file.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, output);
            output.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
