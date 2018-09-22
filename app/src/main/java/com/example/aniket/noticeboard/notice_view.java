package com.example.aniket.noticeboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class notice_view extends AppCompatActivity
{
    String base_url;
    Retrofit retrofit;
    String id;
    static api_interface api_service;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_view);
        final WebView browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDisplayZoomControls(false);
        browser.setWebViewClient(new WebViewClient()
        {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
       ((ImageView)(findViewById(R.id.back_button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       Intent i=getIntent();
       id=i.getStringExtra("id");
        base_url=getResources().getString(R.string.base_url)+"notice/"+id+"/";
        retrofit=(new functions()).getRetrofitInstance(base_url,retrofit);
        api_service = functions.getRetrofitInstance(base_url, retrofit).create(api_interface.class);
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<notice_content> call = api_service.notice_content(base_url , access_token);
        call.enqueue(new Callback<notice_content>() {
            @Override
            public void onResponse(Call<notice_content> call, Response<notice_content> response) {
                if(response.body()!=null)
                {
                    ((TextView)findViewById(R.id.banner_name)).setText(response.body().banner.getName());
                    ((TextView)findViewById(R.id.notice_title)).setText(response.body().getTitle());
                    ((TextView)findViewById(R.id.date_time_notice_view)).setText(response.body().getDatetimeModified());
                    browser.loadUrl(response.body().getContent());
                }
            }

            @Override
            public void onFailure(Call<notice_content> call, Throwable t) {
                ((TextView)findViewById(R.id.banner_name)).setVisibility(View.INVISIBLE);
                findViewById(R.id.no_notice).setVisibility(View.VISIBLE);
                findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);
            }
        });

    }
}
