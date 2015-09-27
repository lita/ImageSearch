package com.codepath.imagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by litacho on 9/21/15.
 */
public class ImageResult implements Parcelable {
    public String fullUrl;
    public String thumbUrl;
    public String title;
    public int height;
    public int width;

    public ImageResult(JSONObject json) throws JSONException {
        try {
            this.fullUrl = json.getString("url");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
            this.height =   json.getInt("height");
            this.width = json.getInt("width");
        } catch (JSONException e) {
            throw e;
        }
    }

    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullUrl);
        dest.writeString(this.thumbUrl);
        dest.writeString(this.title);
        dest.writeInt(this.height);
        dest.writeInt(this.width);
    }

    protected ImageResult(Parcel in) {
        this.fullUrl = in.readString();
        this.thumbUrl = in.readString();
        this.title = in.readString();
        this.height = in.readInt();
        this.width = in.readInt();
    }

    public static final Parcelable.Creator<ImageResult> CREATOR = new Parcelable.Creator<ImageResult>() {
        public ImageResult createFromParcel(Parcel source) {
            return new ImageResult(source);
        }

        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };

    public int getAdjustedHeight(int width) {
        float ratio = (float) this.height/this.width;
        return Math.round(width * ratio);
    }
}
