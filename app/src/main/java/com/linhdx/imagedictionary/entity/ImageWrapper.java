package com.linhdx.imagedictionary.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shine on 01/11/2016.
 */

public class ImageWrapper {
    @SerializedName("value")
    private ImageResult[] imageResults;

    public  ImageResult[] getImageResults(){
        return imageResults;
    }

    private void setImageResults(ImageResult[] imageResults){
        this.imageResults = imageResults;
    }
}
