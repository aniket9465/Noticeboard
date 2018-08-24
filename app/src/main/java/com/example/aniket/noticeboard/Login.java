package com.example.aniket.noticeboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
To add this to your app :
- add internet permission to your gradle
- copy this class in your app
- copy activity_login.xml to your app
 */

public class Login extends AppCompatActivity {

    EditText UsernameText ;//= findViewById(R.id.Username);
    EditText PasswordText ;//= findViewById(R.id.Password);
    Button SubmitButton ;//= findViewById(R.id.Submit);
    static String base_url="http://127.0.0.1:8000";
    static Retrofit retrofit;
    static api_interface api_service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UsernameText=findViewById(R.id.Username);
        PasswordText=findViewById(R.id.Password);
        SubmitButton=findViewById(R.id.Submit);
        SubmitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make api call
                //save token in shared preferences
                //on fail of login show login  fail
                //check for internet connection (on fail of api call either internet not available or server down)
                api_service = getRetrofitInstance().create(api_interface.class);
                login_credentials credentials=new login_credentials(UsernameText.getText().toString(),PasswordText.getText().toString());
                Call<login_response> call= api_service.login(credentials);
                call.enqueue(new Callback<login_response>() {
                    @Override
                    public void onResponse(Call<login_response> call, Response<login_response> response) {
                        Toast.makeText(Login.this, "success  "+response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<login_response> call, Throwable t) {
                        Toast.makeText(Login.this, "connection issue", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
