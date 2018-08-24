package com.example.aniket.noticeboard;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface api_interface {
    @POST("/token_auth/obtain_pair/")
    Call<login_response> login(@Body login_credentials credentials);
}
