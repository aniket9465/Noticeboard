package com.channeli.img.noticeboard;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

public class FCMIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        sendRegistrationToServer(getApplicationContext());

    }


    public void sendRegistrationToServer(Context context) {

        FirebaseApp.initializeApp(context);
        String token = FirebaseInstanceId.getInstance().getToken();
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        if(access_token==null)
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
            return;
        }
        String subStatusString = getSharedPreferences("Noticeboard_data", 0).getString("Subscription", "---");
        char[] subStatus=new char[3];
        subStatus[0]=subStatusString.charAt(0);
        subStatus[1]=subStatusString.charAt(1);
        subStatus[2]=subStatusString.charAt(2);
        Log.d("",subStatusString);
        if(subStatusString.equals("---"))
        {
            subStatus[0]='1';
            subStatus[1]='1';
            subStatus[2]='1';
        }
        if(subStatus[0]=='1')
        {
            FirebaseMessaging.getInstance().subscribeToTopic("Authorities");
        }
        else
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
        }
        if(subStatus[1]=='1')
        {
            FirebaseMessaging.getInstance().subscribeToTopic("Departments");
        }
        else
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
        }
        if(subStatus[2]=='1')
        {
            FirebaseMessaging.getInstance().subscribeToTopic("Placement%20Office");
        }
        else
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
        }
    }
}