package com.channeli.aniket.noticeboard.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.channeli.aniket.noticeboard.ApiRequestBody.LoginRequestBody;
import com.channeli.aniket.noticeboard.ApiRequestBody.RefreshTokenBody;
import com.channeli.aniket.noticeboard.ApiResponseClasses.LoginResponse;
import com.channeli.aniket.noticeboard.ApiResponseClasses.accessToken;
import com.channeli.aniket.noticeboard.LoginScreen;
import com.channeli.aniket.noticeboard.NoticeListScreen;
import com.channeli.aniket.noticeboard.R;
import com.channeli.aniket.noticeboard.SplashScreen;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    public static void tokenRefresh(final Activity activity)
    {

        String loginTime = activity.getSharedPreferences("Noticeboard_data", 0).getString("token_time", "not logged in");

        Date currdate = Calendar.getInstance().getTime();
        Date Logindate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String refreshToken=activity.getSharedPreferences("Noticeboard_data", 0).getString("refresh_token", "not logged in");
        try {
                Logindate = sdf.parse((loginTime));
                double diff = currdate.getTime() - Logindate.getTime();
                diff/=1000;
                diff/=60;
                if(diff<4)
                {
                    return ;
                }
            }
            catch (Exception e) {}

        Retrofit retrofit=null;
        ApiInterface api_service = UtilityFunctions.getRetrofitInstance(activity.getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
        RefreshTokenBody refreshTokenBody = new RefreshTokenBody(refreshToken);
        Call<accessToken> call = api_service.refreshToken(refreshTokenBody);
        call.enqueue(new Callback<accessToken>() {
            @Override
            public void onResponse( Call<accessToken> call, Response<accessToken> response) {
                if (response.body() == null) {
                    Toast.makeText(activity, "could not fetch token", Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
                    SharedPreferences.Editor edit = pref.edit();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    edit.putString("access_token", response.body().getAccess());
                    edit.putString("token_time", sdf.format(Calendar.getInstance().getTime()));
                    edit.apply();
                }
            }

            @Override
            public void onFailure(Call<accessToken> call, Throwable t) {

                Toast.makeText(activity, "connection issue", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public static void logout(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences("Noticeboard_data", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("login_time");
        editor.remove("token_time");
        editor.remove("access_token");
        editor.remove("refresh_token");
        editor.remove("Subscription");
//        if (FirebaseMessaging.getInstance()!=null) {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
//        }
        editor.apply();
    }
}
