package com.example.aniket.noticeboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aniket.noticeboard.ApiRequestBody.LoginRequestBody;
import com.example.aniket.noticeboard.ApiResponseClasses.LoginResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeContentResponse;
import com.example.aniket.noticeboard.Utilities.ApiInterface;
import com.example.aniket.noticeboard.Utilities.UtilityFunctions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NoticeViewScreen extends AppCompatActivity {
    static Retrofit retrofit;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_view);
        final WebView browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDisplayZoomControls(false);
        browser.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        ((ImageView) (findViewById(R.id.back_button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent i = getIntent();
        id = i.getStringExtra("id");
        retrofit = UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<NoticeContentResponse> call = api_service.notice_content(id, access_token);
        call.enqueue(new Callback<NoticeContentResponse>() {
            @Override
            public void onResponse(Call<NoticeContentResponse> call, Response<NoticeContentResponse> response) {
                if (response.body() != null) {
                    ((TextView) findViewById(R.id.banner_name)).setText(response.body().banner.getName());
                    if(response.body().getTitle()!=null)
                    ((TextView) findViewById(R.id.notice_title)).setText(response.body().getTitle());
                    if(response.body().getDatetimeModified()!=null)
                    ((TextView) findViewById(R.id.date_time_notice_view)).setText(response.body().getDatetimeModified());
                    if(response.body().getContent()!=null)
                    browser.loadData(response.body().getContent(), "text/html; charset=utf-8", "UTF-8");
                    browser.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            // open in Webview
                            if (url.contains("android_asset") ){
                                // Can be clever about it like so where myshost is defined in your strings file
                                // if (Uri.parse(url).getHost().equals(getString(R.string.myhost)))
                                return false;
                            }
                            // open rest of URLS in default browser
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NoticeContentResponse> call, Throwable t) {
                ((TextView) findViewById(R.id.banner_name)).setVisibility(View.INVISIBLE);
                findViewById(R.id.no_notice).setVisibility(View.VISIBLE);
                findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);
            }
        });

    }

}
