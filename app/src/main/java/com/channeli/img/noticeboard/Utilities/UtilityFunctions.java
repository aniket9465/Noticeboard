package com.channeli.img.noticeboard.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.img.noticeboard.ApiRequestBody.LoginRequestBody;
import com.channeli.img.noticeboard.ApiRequestBody.RefreshTokenBody;
import com.channeli.img.noticeboard.ApiResponseClasses.LoginResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.UserInfo.UserInfo;
import com.channeli.img.noticeboard.ApiResponseClasses.accessToken;
import com.channeli.img.noticeboard.LoginScreen;
import com.channeli.img.noticeboard.NoticeListScreen;
import com.channeli.img.noticeboard.R;
import com.channeli.img.noticeboard.SplashScreen;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.WeakHashMap;

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

        final String loginTime = activity.getSharedPreferences("Noticeboard_data", 0).getString("token_time", "not logged in");

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
                    Log.d("refresh token Utility ","token fetch fail logging out");
                    logout(activity);
                }
                else {
                    SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
                    SharedPreferences.Editor edit = pref.edit();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    edit.putString("access_token", response.body().getAccess());
                    edit.putString("token_time", sdf.format(Calendar.getInstance().getTime()));
                    edit.apply();
                    UtilityFunctions.getUserInfo(activity);
                }
            }

            @Override
            public void onFailure(Call<accessToken> call, Throwable t) {

            }
        });

    }
    static void getUserInfo(final Activity activity)
    {
        String access_token = activity.getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Retrofit retrofit=null;
        retrofit=UtilityFunctions.getRetrofitInstance(activity.getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);
        Call<UserInfo> call = api_service.getUserInfo( "Bearer " + access_token);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.code()==401)
                {
                    Intent i = new Intent(activity,LoginScreen.class);
                    UtilityFunctions.logout(activity);
                    activity.startActivity(i);
                    activity.finish();
                }
                if(response.body()!=null)
                {
                    ((TextView)activity.findViewById(R.id.username)).setText(response.body().getFullName());
                    String branch="";
                    for(int i=0;i<response.body().getRoles().size();++i)
                    {
                        if(response.body().getRoles().get(i).getRole().equals("Student"))
                        {
                            branch=response.body().getRoles().get(i).getData().getBranch().getName();
                        }

                        if(response.body().getRoles().get(i).getRole().equals("FacultyMember"))
                        {
                            branch=response.body().getRoles().get(i).getData().getDepartment().getName();
                        }
                    }
                    ((TextView)activity.findViewById(R.id.branch)).setText(branch);
                    if(response.body().getDisplayPicture()!=null)
                    {
                        String url = (activity.getResources().getString(R.string.base_url) + response.body().getDisplayPicture());
                        GetImage getImage=new GetImage(activity);
                        getImage.execute(url);
                    }
                }
                else
                {
                    Log.d("noticeListScreen : ","could not fetch user information (repose.body()==null)");
                }

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("noticeListScreen : ","could not fetch user information (Failure)");

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
        if (FirebaseMessaging.getInstance()!=null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
        }


        editor.apply();
    }

}
