package com.channeli.img.noticeboard;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.channeli.img.noticeboard.ApiRequestBody.RefreshTokenBody;
import com.channeli.img.noticeboard.ApiRequestBody.TokenBody;
import com.channeli.img.noticeboard.ApiResponseClasses.accessToken;
import com.channeli.img.noticeboard.ApiResponseClasses.notificationResponse;
import com.channeli.img.noticeboard.Utilities.ApiInterface;
import com.channeli.img.noticeboard.Utilities.UtilityFunctions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FCMIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        sendRegistrationToServer(getApplicationContext());

    }


    public void sendRegistrationToServer(Context context) {

        FirebaseApp.initializeApp(context);
        final String token = FirebaseInstanceId.getInstance().getToken();
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        if(access_token==null)
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
            return;
        }

        Retrofit retrofit=null;
        ApiInterface api_service = UtilityFunctions.getRetrofitInstance(this.getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
        String notificationIdentifier = getSharedPreferences("Noticeboard_data", 0).getString("notificationIdentifier", null);
        if(notificationIdentifier!=null)
            return;
        StringBuffer rand = new StringBuffer();
        for(int i=0;i<50;++i) {
            rand.append(((char)('a'+(Math.floor(26*Math.random()))%26)));
        }
        final String generatedString=rand.toString();
        TokenBody tokenBody = new TokenBody(token,generatedString);
        Call<notificationResponse> call = api_service.notificationToken(tokenBody,"Bearer " + access_token);
        call.enqueue(new Callback<notificationResponse>() {
            @Override
            public void onResponse( Call<notificationResponse> call, Response<notificationResponse> response) {
                Log.d("...", token + response.message() + response.code());
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("notificationIdentifier",generatedString);
                edit.apply();
            }

            @Override
            public void onFailure(Call<notificationResponse> call, Throwable t) {
                Log.d("...", token + "fail");
            }
        });


//        String subStatusString = getSharedPreferences("Noticeboard_data", 0).getString("Subscription", "---");
//        char[] subStatus=new char[3];
//        subStatus[0]=subStatusString.charAt(0);
//        subStatus[1]=subStatusString.charAt(1);
//        subStatus[2]=subStatusString.charAt(2);
//        Log.d("",subStatusString);
//        if(subStatusString.equals("---"))
//        {
//            subStatus[0]='1';
//            subStatus[1]='1';
//            subStatus[2]='1';
//        }
//        if(subStatus[0]=='1')
//        {
//            FirebaseMessaging.getInstance().subscribeToTopic("Authorities");
//        }
//        else
//        {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
//        }
//        if(subStatus[1]=='1')
//        {
//            FirebaseMessaging.getInstance().subscribeToTopic("Departments");
//        }
//        else
//        {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
//        }
//        if(subStatus[2]=='1')
//        {
//            FirebaseMessaging.getInstance().subscribeToTopic("Placement%20Office");
//        }
//        else
//        {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
//        }
    }
}