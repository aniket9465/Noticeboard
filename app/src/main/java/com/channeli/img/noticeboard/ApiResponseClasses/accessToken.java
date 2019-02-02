package com.channeli.img.noticeboard.ApiResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class accessToken {
    @SerializedName("access")
    @Expose
    private String access;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
