package com.channeli.aniket.noticeboard.ApiRequestBody;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefreshTokenBody {
    @SerializedName("refresh")
    @Expose
    private String refresh;

    public String getRefresh()
    {
        return refresh;
    }
    public RefreshTokenBody(String refresh)
    {
        this.refresh=refresh;
    }

}
