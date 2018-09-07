package com.example.aniket.noticeboard;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class functions {
    public static Retrofit getRetrofitInstance(String base_url,Retrofit retrofit) {
       if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
