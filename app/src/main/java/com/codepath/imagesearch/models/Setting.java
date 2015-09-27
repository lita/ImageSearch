package com.codepath.imagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Setting implements Parcelable {
    public String urlText;
    public String viewText;

    public Setting() {
        urlText = "any";
        viewText = "Any";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.urlText);
        dest.writeString(this.viewText);
    }

    protected Setting(Parcel in) {
        this.urlText = in.readString();
        this.viewText = in.readString();
    }

    public static final Creator<Setting> CREATOR = new Creator<Setting>() {
        public Setting createFromParcel(Parcel source) {
            return new Setting(source);
        }

        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };
}