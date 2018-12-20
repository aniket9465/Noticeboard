package com.example.aniket.noticeboard.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.aniket.noticeboard.LoginScreen;
import com.example.aniket.noticeboard.NoticeListScreen;
import com.example.aniket.noticeboard.SplashScreen;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UtilityFunctions {
    public static Retrofit getRetrofitInstance(String base_url,Retrofit retrofit) {
       if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static void tokenRefresh()
    {

    }

    public static void logout(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences("Noticeboard_data", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("login_time");
        editor.remove("token_time");
        editor.remove("access_token");
        editor.remove("refresh_token");
        editor.apply();
    }
}
