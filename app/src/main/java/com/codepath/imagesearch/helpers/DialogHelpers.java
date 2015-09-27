package com.codepath.imagesearch.helpers;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.imagesearch.R;

/**
 * Created by litacho on 9/26/15.
 */
public class DialogHelpers {
    static public MaterialDialog showProgressDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .title("Loading image...")
                .content("Please wait.")
                .titleColor(ContextCompat.getColor(context, R.color.primary))
                .widgetColor(ContextCompat.getColor(context, R.color.primary))
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    static public void showAlert(Context context, String title, String content) {
         new MaterialDialog.Builder(context)
                 .title(title)
                 .content(content)
                 .titleColor(ContextCompat.getColor(context, R.color.primary))
                 .widgetColor(ContextCompat.getColor(context, R.color.primary))
                 .neutralColor(ContextCompat.getColorStateList(context, R.color.button_text))
                 .neutralText(R.string.neutral)
                 .buttonRippleColor(ContextCompat.getColor(context, R.color.accent))
                 .show();
    }
}
