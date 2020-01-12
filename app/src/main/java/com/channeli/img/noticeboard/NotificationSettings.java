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
    CheckBox centres;
    CheckBox bhawans;
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
        bhawans=findViewById(R.id.bhawans_subscription);
        centres=findViewById(R.id.centres_subscription);

        String subStatusString = getSharedPreferences("Noticeboard_data", 0).getString("Subscription", "----");
        subStatus[0]=subStatusString.charAt(0);
        subStatus[1]=subStatusString.charAt(1);
        subStatus[2]=subStatusString.charAt(2);
        subStatus[3]=subStatusString.charAt(3);
        Log.d("",subStatusString);
        if(subStatusString.equals("----"))
        {
            subStatus[0]='1';
            subStatus[1]='1';
            subStatus[2]='1';
            subStatus[3]='1';
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
            bhawans.setChecked(true);
        }
        else
        {
            bhawans.setChecked(false);
        }

        if(subStatus[3]=='1')
        {
            centres.setChecked(true);
        }
        else
        {
            centres.setChecked(false);
        }
        centres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
        bhawans.setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.Centres_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centres.callOnClick();
                if(centres.isChecked())
                    centres.setChecked(false);
                else
                    centres.setChecked(true);
            }
        });
        findViewById(R.id.Bhawans_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bhawans.callOnClick();
                if(bhawans.isChecked())
                    bhawans.setChecked(false);
                else
                    bhawans.setChecked(true);
            }
        });
        findViewById(R.id.Authorities_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authority.callOnClick();
                if(authority.isChecked())
                    authority.setChecked(false);
                else
                    authority.setChecked(true);
            }
        });
        findViewById(R.id.Departments_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                department.callOnClick();
                if(department.isChecked())
                    department.setChecked(false);
                else
                    department.setChecked(true);
            }
        });
    }
    void changeStatus()
    {if (FirebaseMessaging.getInstance()!=null) {
        FirebaseMessaging.getInstance().subscribeToTopic("Bhawans");
        FirebaseMessaging.getInstance().subscribeToTopic("Centres");
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
        if(bhawans.isChecked()) {
            FirebaseMessaging.getInstance().subscribeToTopic("Bhawans");
            subStatus[2] = '1';
        }
        else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Bhwanas");
            subStatus[2] = '0';
        }
        if(centres.isChecked()) {
            FirebaseMessaging.getInstance().subscribeToTopic("Centres");
            subStatus[3] = '1';
        }
        else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Centres");
            subStatus[3] = '0';
        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("Subscription", subStatus[0]+""+subStatus[1]+""+subStatus[2]+"");
        edit.apply();
    }
}
