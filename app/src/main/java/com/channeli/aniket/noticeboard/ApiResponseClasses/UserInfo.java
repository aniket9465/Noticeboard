package com.channeli.aniket.noticeboard.ApiResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserInfo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("displayPicture")
    @Expose
    private String displayPicture;
    @SerializedName("roles")
    @Expose
    private List<Role> roles = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}
