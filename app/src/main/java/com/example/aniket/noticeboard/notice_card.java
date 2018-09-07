package com.example.aniket.noticeboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class notice_card {

    @SerializedName("banner")
    @Expose
    private String banner;
    @SerializedName("datetime_modified")
    @Expose
    private String datetimeModified;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("main_category")
    @Expose
    private String mainCategory;
    @SerializedName("read")
    @Expose
    private Boolean read;
    @SerializedName("starred")
    @Expose
    private Boolean starred;
    @SerializedName("title")
    @Expose
    private String title;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDatertimeModified() {
        return datetimeModified;
    }

    public void setDatertimeModified(String datertimeModified) {
        this.datetimeModified = datertimeModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getStarred() {
        return starred;
    }

    public void setStarred(Boolean starred) {
        this.starred = starred;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}