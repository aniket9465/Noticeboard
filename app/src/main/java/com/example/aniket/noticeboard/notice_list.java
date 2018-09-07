package com.example.aniket.noticeboard;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class notice_list {

    @SerializedName("notices")
    @Expose
    private List<notice_card> notices = null;

    public List<notice_card> getNotices() {
        return notices;
    }

    public void setNotices(List<notice_card> notices) {
        this.notices = notices;
    }

}
