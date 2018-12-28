package com.myapps.compilerfarsi;

import com.google.gson.annotations.SerializedName;

public class ImageRes {
    @SerializedName("file")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }
}
