package com.codepath.imagesearch.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.imagesearch.R;
import com.codepath.imagesearch.models.Settings;

/**
 * Created by litacho on 9/25/15.
 */
public class SettingsDialog extends DialogFragment {
    private Spinner sImageSizeOptions;
    private Spinner sImageColorOptions;
    private Spinner sImageTypeOptions;
    private EditText etSiteType;
    private Settings settings;

    public interface SettingsDialogListener {
        void onFinishSettingsDialog (Settings settings);
    }


    public SettingsDialog() {
    }

    public static SettingsDialog newInstance(Settings settings) {
        SettingsDialog settingsDialog = new SettingsDialog();
        Bundle args = new Bundle();
        args.putParcelable("settings", settings);
        settingsDialog.setArguments(args);
        return settingsDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sImageSizeOptions = (Spinner) view.findViewById(R.id.sImageSizeOptions);
        sImageColorOptions = (Spinner) view.findViewById(R.id.sImageColorOptions);
        sImageTypeOptions = (Spinner) view.findViewById(R.id.sImageTypeOptions);
        etSiteType = (EditText) view.findViewById(R.id.etSiteType);

        Button saveButton = (Button) view.findViewById(R.id.save);
        Button cancelButton = (Button) view.findViewById(R.id.cancel);

        Settings settings = (Settings) getArguments().getParcelable("settings");

        if (settings == null) {
            settings = new Settings();
        }

        String[] sizeOption = getResources().getStringArray(R.array.image_size_options);
        String[] colorOption = getResources().getStringArray(R.array.color_filter_options);
        String[] typeOptions = getResources().getStringArray(R.array.image_type_options);
        sImageSizeOptions.setSelection(
                this.getPositionFromArray(settings.imageSize.viewText, sizeOption));
        sImageColorOptions.setSelection(
                this.getPositionFromArray(settings.imageColor.viewText, colorOption));
        sImageTypeOptions.setSelection(
                this.getPositionFromArray(settings.safeSearch.viewText, typeOptions));

        etSiteType.setText(settings.sitesearch);
        getDialog().setTitle("Advance Search Options");


        // Set up buttons
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                String imageSize = sImageSizeOptions.getSelectedItem().toString();
                String imageColor = sImageColorOptions.getSelectedItem().toString();
                String imageType = sImageTypeOptions.getSelectedItem().toString();
                String imageSiteSearch = etSiteType.getText().toString();
                SettingsDialog.this.settings.setImageSize(imageSize);
                SettingsDialog.this.settings.setImageColor(imageColor);
                SettingsDialog.this.settings.setImageType(imageType);
                // TODO add error handling
                SettingsDialog.this.settings.sitesearch = imageSiteSearch;
                SettingsDialogListener listener = (SettingsDialogListener) getActivity();
                listener.onFinishSettingsDialog(SettingsDialog.this.settings);
                SettingsDialog.this.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsDialog.this.dismiss();
            }
        });

        this.settings = settings;
    }

    protected int getPositionFromArray(String needle, String[] haystack) {
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i].equals(needle)) {
                return i;
            }
        }
        Log.i("WARN", "Settings found an option that did not exist");
        return 0;
    }

}
