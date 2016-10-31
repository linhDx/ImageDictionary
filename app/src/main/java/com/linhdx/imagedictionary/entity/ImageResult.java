package com.linhdx.imagedictionary.entity;

import com.google.gson.annotations.SerializedName;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by shine on 31/10/2016.
 */

public class ImageResult implements Parcelable{
    @SerializedName(value = "name")
    String name;
    @SerializedName(value = "webSearchUrl")
    String webSearchUrl;
    @SerializedName(value = "thumbnailUrl")
    String thumbnailUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.getThumbnailUrl());
        dest.writeString(this.webSearchUrl);
    }

    public ImageResult() {
    }

    protected ImageResult(Parcel in){
        this.name = in.readString();
        this.thumbnailUrl = in.readString();
        this.webSearchUrl = in.readString();
    }

    public  static final Creator<ImageResult> CREATOR = new Creator<ImageResult>() {
        @Override
        public ImageResult createFromParcel(Parcel source) {
            return new ImageResult(source);
        }

        @Override
        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };
}
