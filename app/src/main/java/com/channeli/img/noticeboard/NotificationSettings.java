package com.channeli.img.noticeboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationSettings extends AppCompatActivity {

    CheckBox department;
    CheckBox authority;
    CheckBox placement;
    char[] subStatus=new char[3];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        authority=findViewById(R.id.authority_subscription);
        department=findViewById(R.id.department_subscription);
        placement=findViewById(R.id.placement_subscription);

        String subStatusString = getSharedPreferences("Noticeboard_data", 0).getString("Subscription", "---");
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
            authority.setChecked(true);
        }
        else
        {
            authority.setChecked(false);
        }

        if(subStatus[1]=='1')
        {
            department.setChecked(true);
        }
        else
        {
            department.setChecked(false);
        }

        if(subStatus[2]=='1')
        {
            placement.setChecked(true);
        }
        else
        {
            placement.setChecked(false);
        }
        placement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
        department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
        authority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
    }
    void changeStatus()
    {if (FirebaseMessaging.getInstance()!=null) {
        FirebaseMessaging.getInstance().subscribeToTopic("Placement%20Office");
        FirebaseMessaging.getInstance().subscribeToTopic("Authorities");
        FirebaseMessaging.getInstance().subscribeToTopic("Departments");
    }
        if(authority.isChecked()) {
            FirebaseMessaging.getInstance().subscribeToTopic("Authorities");
            subStatus[0] = '1';
        }
        else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Authorities");
            subStatus[0] = '0';
        }
        if(department.isChecked()) {
            FirebaseMessaging.getInstance().subscribeToTopic("Departments");
            subStatus[1] = '1';
        }
        else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Departments");
            subStatus[1] = '0';
        }
        if(placement.isChecked()) {
            FirebaseMessaging.getInstance().subscribeToTopic("Placement%20Office");
            subStatus[2] = '1';
        }
        else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Placement%20Office");
            subStatus[2] = '0';
        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("Subscription", subStatus[0]+""+subStatus[1]+""+subStatus[2]+"");
        edit.apply();
    }
}
