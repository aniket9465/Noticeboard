package com.channeli.img.noticeboard.ApiResponseClasses;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Filters {

    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("banner")
    @Expose
    private ArrayList<Banner> banner = null;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(ArrayList<Banner> banner) {
        this.banner = banner;
    }

}