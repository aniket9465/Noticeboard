package com.channeli.img.noticeboard.ApiResponseClasses;
import com.channeli.img.noticeboard.ApiResponseClasses.Banner;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeContentResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("banner")
    @Expose
    public Banner banner;
    @SerializedName("datetimeModified")
    @Expose
    private String datetimeModified;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public String getDatetimeModified() {
        return datetimeModified;
    }

    public void setDatetimeModified(String datetimeModified) {
        this.datetimeModified = datetimeModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
