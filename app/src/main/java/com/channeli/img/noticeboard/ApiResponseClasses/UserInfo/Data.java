package com.channeli.img.noticeboard.ApiResponseClasses.UserInfo;

import com.channeli.img.noticeboard.ApiResponseClasses.UserInfo.Branch;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("branch")
    @Expose
    private Branch branch;
    @SerializedName("currentYear")
    @Expose
    private Integer currentYear;
    @SerializedName("currentSemester")
    @Expose
    private Integer currentSemester;

    @SerializedName("department")
    @Expose
    private Department department;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getCurrentSemester() {
        return currentSemester;
    }

    public Department getDepartment() {
        return department;
    }

    public void setCurrentSemester(Integer currentSemester) {
        this.currentSemester = currentSemester;
    }

}
