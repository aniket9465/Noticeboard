package com.example.aniket.noticeboard;

import android.app.ProgressDialog;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
on successful login saving data in shared preferences in name : "Noticeboard_data"
with refresh token with key "refresh_token"
with access token with key "access token"
 */

public class Login extends AppCompatActivity {
    EditText UsernameText ;
    EditText PasswordText ;
    Button SubmitButton ;
    static String base_url="http://127.0.0.1:8000";
    static Retrofit retrofit;
    static api_interface api_service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.clear_focus).requestFocus();
        TextView link= (TextView) findViewById(R.id.link);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        TextView img_love= (TextView) findViewById(R.id.made_with_love);
        String text = "Made with <font color=#F50057>"+String.valueOf(Character.toChars(0x2764))+"</font> by IMG";
        img_love.setText(Html.fromHtml(text));
        UsernameText=findViewById(R.id.Username);
        PasswordText=findViewById(R.id.Password);
        SubmitButton=findViewById(R.id.Submit);
        SubmitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                api_service = functions.getRetrofitInstance(base_url,retrofit).create(api_interface.class);
                login_credentials credentials=new login_credentials(UsernameText.getText().toString(),PasswordText.getText().toString());
                Call<login_response> call= api_service.login(credentials);
                call.enqueue(new Callback<login_response>() {
                    @Override
                    public void onResponse(Call<login_response> call, Response<login_response> response) {
                        if(response.body()==null)
                        {
                            Toast.makeText(Login.this, "wrong credentials :(", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            SharedPreferences pref= getApplicationContext().getSharedPreferences("Noticeboard_data",0);
                            SharedPreferences.Editor edit=pref.edit();
                            edit.putString("refresh_token",response.body().getRefresh());
                            edit.putString("access_token",response.body().getAccess());
                            edit.commit();
                            Intent in=new Intent(Login.this,list_of_notices.class);
                            startActivity(in);
                            finish();
                            // new screen intent .......

                        }
                    }

                    @Override
                    public void onFailure(Call<login_response> call, Throwable t) {
                        Toast.makeText(Login.this, "connection issue", Toast.LENGTH_SHORT).show();
                        //remove this
                        Intent in=new Intent(Login.this,list_of_notices.class);
                        startActivity(in);
                        finish();
                        ////
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
