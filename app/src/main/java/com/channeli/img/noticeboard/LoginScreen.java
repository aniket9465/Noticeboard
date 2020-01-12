package com.channeli.img.noticeboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.img.noticeboard.Utilities.ApiInterface;
import com.channeli.img.noticeboard.ApiRequestBody.LoginRequestBody;
import com.channeli.img.noticeboard.ApiResponseClasses.LoginResponse;
import com.channeli.img.noticeboard.Utilities.UtilityFunctions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



public class LoginScreen extends AppCompatActivity {


    static Retrofit retrofit;
    EditText UsernameText;
    EditText PasswordText;
    Button SubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        findViewById(R.id.clear_focus).requestFocus();

        UsernameText = findViewById(R.id.Username);
        PasswordText = findViewById(R.id.Password);
        SubmitButton = findViewById(R.id.Submit);
        TextView link = (TextView) findViewById(R.id.link);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        TextView img_love = (TextView) findViewById(R.id.made_with_love);
        String text = "Made with <font color=#F50057>" + String.valueOf(Character.toChars(0x2764)) + "</font> by IMG";
        img_love.setText(Html.fromHtml(text));


        SubmitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface api_service = UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
                LoginRequestBody credentials = new LoginRequestBody(UsernameText.getText().toString(), PasswordText.getText().toString());
                Call<LoginResponse> call = api_service.login(credentials);
                Log.d("",call.request().url()+"");
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse( Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.body() == null) {
                            Toast.makeText(LoginScreen.this, "Wrong Credentials :(", Toast.LENGTH_SHORT).show();


                        } else {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
                            SharedPreferences.Editor edit = pref.edit();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                            edit.putString("refresh_token", response.body().getRefresh());
                            edit.putString("access_token", response.body().getAccess());
                            edit.putString("token_time", sdf.format(Calendar.getInstance().getTime()));
                            edit.putString("login_time", sdf.format(Calendar.getInstance().getTime()));
                            edit.putString("Subscription", "1111");
                            edit.apply();
                            if (FirebaseMessaging.getInstance()!=null) {
                                FirebaseMessaging.getInstance().subscribeToTopic("Bhawans");
                                FirebaseMessaging.getInstance().subscribeToTopic("Centres");
                                FirebaseMessaging.getInstance().subscribeToTopic("Authorities");
                                FirebaseMessaging.getInstance().subscribeToTopic("Departments");
                            }
                            Intent in = new Intent(LoginScreen.this, NoticeListScreen.class);
                            startActivity(in);
                            finish();

                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginScreen.this, "Connection Issue", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        UsernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!UsernameText.getText().toString().trim().isEmpty() && !PasswordText.getText().toString().trim().isEmpty()) {
                    SubmitButton.setEnabled(true);

                } else {
                    SubmitButton.setEnabled(false);
                }

            }
        });


        PasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!UsernameText.getText().toString().trim().isEmpty() && !PasswordText.getText().toString().trim().isEmpty()) {
                    SubmitButton.setEnabled(true);

                } else {
                    SubmitButton.setEnabled(false);
                }

            }
        });
    }

}