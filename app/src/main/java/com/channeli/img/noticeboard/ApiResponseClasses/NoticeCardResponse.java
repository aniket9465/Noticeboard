package com.channeli.img.noticeboard.ApiResponseClasses;
import com.channeli.img.noticeboard.ApiResponseClasses.Banner;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeCardResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("banner")
    @Expose
    private Banner banner;
    @SerializedName("datetimeModified")
    @Expose
    private String datetimeModified;
    @SerializedName("isDraft")
    @Expose
    private Boolean isDraft;
    @SerializedName("read")
    @Expose
    private Boolean read;
    @SerializedName("starred")
    @Expose
    public Boolean bookmark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(Boolean bookmark) {
        this.bookmark = bookmark;
    }

}