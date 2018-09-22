package com.example.aniket.noticeboard;

import android.content.SharedPreferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface api_interface {

    @POST("/token_auth/obtain_pair/")
    Call<login_response> login(@Body login_credentials credentials);

    @GET
    Call<notice_list> get_notices(@Url String url, @Header("access_token") String access_token);

    @GET
    Call<notice_list> search_notices(@Url String url, @Header("access_token") String access_token);

    @POST
    Call<Void> bookmark_read(@Url String url, @Header("access_token") String access_token, @Body bookmark_read_body body);

    @GET
    Call<notice_content> notice_content(@Url String url,@Header("access_token") String access_token);
}
