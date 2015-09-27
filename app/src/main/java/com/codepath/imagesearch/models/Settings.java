package com.codepath.imagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by litacho on 9/23/15.
 */
public class Settings implements Parcelable {
    public Setting safeSearch;
    public Setting imageColor;
    public Setting imageSize;
    public String sitesearch;
    public String query;

    public Settings() {
        this.imageColor = new Setting();
        this.safeSearch = new Setting();
        this.imageSize = new Setting();
    }

    public void setImageType(String imageType) {
        this.safeSearch.viewText = imageType;
        if (imageType.equals("On")) {
            this.safeSearch.urlText= "active";
        } else {
            this.safeSearch.urlText = "moderate";
        }
    }

    public void setImageColor(String imageColor) {
        this.imageColor.viewText = imageColor;
        this.imageColor.urlText = imageColor.toLowerCase();
    }

    public void setImageSize(String imageSize) {
        this.imageSize.viewText = imageSize;
        this.imageSize.urlText = imageSize.toLowerCase();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.safeSearch, 0);
        dest.writeParcelable(this.imageColor, 0);
        dest.writeParcelable(this.imageSize, 0);
        dest.writeString(this.sitesearch);
        dest.writeString(this.query);
    }

    protected Settings(Parcel in) {
        this.safeSearch = in.readParcelable(Setting.class.getClassLoader());
        this.imageColor = in.readParcelable(Setting.class.getClassLoader());
        this.imageSize = in.readParcelable(Setting.class.getClassLoader());
        this.sitesearch = in.readString();
        this.query = in.readString();
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        public Settings createFromParcel(Parcel source) {
            return new Settings(source);
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
