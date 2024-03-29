package com.channeli.img.noticeboard.ApiResponseClasses;

import java.util.ArrayList;

import com.channeli.img.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeListResponse {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("next")
    @Expose
    public String next;
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("results")
    @Expose
    private ArrayList<NoticeCardResponse> results = new ArrayList<>() ;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public ArrayList<NoticeCardResponse> getNotices() {
        return results;
    }

    public void setNotices(ArrayList<NoticeCardResponse> results) {
        this.results = results;
    }

}
