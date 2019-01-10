package com.channeli.aniket.noticeboard;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.aniket.noticeboard.ApiRequestBody.BookmarkReadRequestBody;
import com.channeli.aniket.noticeboard.ApiResponseClasses.NoticeContentResponse;
import com.channeli.aniket.noticeboard.Utilities.ApiInterface;
import com.channeli.aniket.noticeboard.Utilities.UtilityFunctions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/*
    implement download button and shareable link button
*/

public class NoticeViewScreen extends AppCompatActivity {
    static Retrofit retrofit;
    Boolean bookmarked;
    Intent returnIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_view);


        final WebView browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDisplayZoomControls(false);
        browser.getSettings().setJavaScriptEnabled(true);
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
        Boolean expired =  i.getBooleanExtra("Expired",true);
        final Integer id = i.getIntExtra("id",-1);
        bookmarked = i.getBooleanExtra("bookmarked",false);
        final Integer position = i.getIntExtra("position",-1);
        Log.d("",id+ " " + bookmarked +" "+ position);
        returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);

        returnIntent.putExtra("position",position);
        returnIntent.putExtra("bookmarked",bookmarked);

        if(expired)
        {
            findViewById(R.id.bookmark).setVisibility(View.INVISIBLE);
        }

        if(bookmarked)
        {
            ((ImageView)findViewById(R.id.bookmark)).setImageResource(R.drawable.bookmarked);
        }
        else
        {
            ((ImageView)findViewById(R.id.bookmark)).setImageResource(R.drawable.bookmark);
        }


        retrofit = UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);


        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<NoticeContentResponse> call = api_service.noticeContent(id,"Bearer "+ access_token);
        if(expired)
            call = api_service.noticeContentExpired(id,"Bearer "+ access_token);
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Notice shareable link", getResources().getString(R.string.base_url)+"notice/"+id);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(NoticeViewScreen.this, "Notice link copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

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
                            if (url.contains("android_asset") ){
                                return false;
                            }

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


        findViewById(R.id.bookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // make call for bookmarking notice
                ApiInterface api_service;
                retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
                api_service = retrofit.create(ApiInterface.class);
                String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
                Call<Void> call = api_service.bookmark_read("Bearer "+access_token,new BookmarkReadRequestBody(id,"star"));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if(response.code()==201||response.code()==200) {
                            if (bookmarked) {
                                ((ImageView) v).setImageResource(R.drawable.bookmark);
                                bookmarked=!bookmarked;
                                returnIntent.putExtra("bookmarked",bookmarked);
                                setResult(Activity.RESULT_OK, returnIntent);
                                Toast.makeText(getApplicationContext(), "notice unmarked", Toast.LENGTH_SHORT).show();
                            } else {
                                ((ImageView) v).setImageResource(R.drawable.bookmarked);
                                bookmarked=!bookmarked;
                                returnIntent.putExtra("bookmarked",bookmarked);
                                setResult(Activity.RESULT_OK, returnIntent);
                                Toast.makeText(getApplicationContext(), "notice bookmarked", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "connection error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "connection error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
