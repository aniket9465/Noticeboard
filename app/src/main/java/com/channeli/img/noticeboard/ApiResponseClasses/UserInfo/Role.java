package com.channeli.img.noticeboard.ApiResponseClasses.UserInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Role {

    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("activeStatus")
    @Expose
    private String activeStatus;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}