package com.channeli.img.noticeboard.ApiRequestBody;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenBody {
    @SerializedName("client_identifier")
    @Expose
    private String client_identifier;
    @SerializedName("token")
    @Expose
    private String token;

    public String getToken()
    {
        return token;
    }
    public TokenBody(String token,String client_identifier)
    {
        this.token=token;
        this.client_identifier=client_identifier;
    }

}
