package com.channeli.aniket.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Handler;

import com.channeli.aniket.noticeboard.Utilities.UtilityFunctions;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        String loginTime = getSharedPreferences("Noticeboard_data", 0).getString("login_time", "not logged in");
        if(loginTime.equals("not logged in")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }
        else
        {

            Date currdate = Calendar.getInstance().getTime();
            Date Logindate;
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            try {
                Logindate = sdf.parse((loginTime));
                long diff = currdate.getTime() - Logindate.getTime();
                diff/=1000;
                diff/=3600;
                diff/=24;
                diff/=180;

                if(diff<6)
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SplashScreen.this, NoticeListScreen.class);
                            startActivity(i);
                            finish();
                        }
                    }, 1000);
                }
                else
                {
                    UtilityFunctions.logout(this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                            startActivity(i);
                            finish();
                        }
                    }, 1000);
                }
            }
            catch (Exception e) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
            }
        }
    }

}
