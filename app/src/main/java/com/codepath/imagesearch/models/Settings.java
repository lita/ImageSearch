package com.codepath.imagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by litacho on 9/23/15.
 */
public class Settings implements Parcelable {
    public Setting imageType;
    public Setting imageColor;
    public Setting imageSize;
    public String sitesearch;
    public String query;

    public Settings() {
        this.imageColor = new Setting();
        this.imageType = new Setting();
        this.imageSize = new Setting();
    }

    public void setImageType(String imageType) {
        this.imageType.viewText = imageType;
        if (imageType == "Clip art") {
            this.imageType.urlText= "clipart";
        } else if (imageType == "Line drawing") {
            this.imageType.urlText = "lineart";
        } else {
            this.imageType.urlText = imageType.toLowerCase();
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
        dest.writeParcelable(this.imageType, 0);
        dest.writeParcelable(this.imageColor, 0);
        dest.writeParcelable(this.imageSize, 0);
        dest.writeString(this.sitesearch);
        dest.writeString(this.query);
    }

    protected Settings(Parcel in) {
        this.imageType = in.readParcelable(Setting.class.getClassLoader());
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
