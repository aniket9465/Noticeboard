package com.channeli.img.noticeboard;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.img.noticeboard.ApiRequestBody.BookmarkReadRequestBody;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeContentResponse;
import com.channeli.img.noticeboard.Utilities.ApiInterface;
import com.channeli.img.noticeboard.Utilities.UtilityFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class NoticeViewScreen extends AppCompatActivity {
    static Retrofit retrofit;
    Boolean bookmarked;
    Intent returnIntent;
    String dataString;
    Boolean loginPage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_view);


        final WebView browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDisplayZoomControls(false);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });


        ((ImageView) (findViewById(R.id.back_button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calling = getIntent();
                if(calling.getBooleanExtra("notification",false) || dataString != null || loginPage)
                {
                    Intent i = new Intent(NoticeViewScreen.this,NoticeListScreen.class);
                    startActivity(i);
                    finish();
                }
                finish();
            }
        });


        Intent i = getIntent();
        dataString = i.getDataString();
        Boolean expired =  i.getBooleanExtra("Expired",false);
        loginPage =  i.getBooleanExtra("loginPage",false);
        final Integer id;
        Integer tempId;
        if(dataString != null)
        {
            if(dataString.charAt(dataString.length()-1) == '/')
                dataString=dataString.substring(0,dataString.length()-2);
            try {
                tempId = Integer.parseInt(dataString.substring(dataString.lastIndexOf('/') + 1));
            }
            catch (Exception e)
            {
                tempId = -1;
            }
        }
        else
            tempId = i.getIntExtra("id",-1);
        id=tempId;
        markRead(id);
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

        if(access_token == null)
        {
            Intent intent = new Intent(NoticeViewScreen.this,LoginScreen.class);
            intent.putExtra("id", id);
            startActivity(intent);
            finish();
        }

        Call<NoticeContentResponse> call = api_service.noticeContent(id,"Bearer "+ access_token);

        if(expired)
            call = api_service.noticeContentExpired(id,"Bearer " + access_token);

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.base_url_frontend) + "/noticeboard/notice/" + id);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        call.enqueue(new Callback<NoticeContentResponse>() {
            @Override
            public void onResponse(Call<NoticeContentResponse> call, Response<NoticeContentResponse> response) {

                if (response.code() == 200 && response.body() != null) {

                    ((TextView) findViewById(R.id.banner_name)).setText(response.body().banner.getName());

                    if(response.body().getTitle()!=null)
                        ((TextView) findViewById(R.id.notice_title)).setText(response.body().getTitle());

                    if(response.body().getDatetimeModified()!=null) {
                        try {
                            String date = response.body().getDatetimeModified();
                            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date newDate = spf.parse(date);
                            spf = new SimpleDateFormat("dd MMM yyyy");
                            date = spf.format(newDate);
                            ((TextView) findViewById(R.id.date_time_notice_view)).setText(date);
                        }
                        catch (Exception e)
                        {
                            ((TextView) findViewById(R.id.date_time_notice_view)).setText(response.body().getDatetimeModified());
                        }
                    }

                    if(response.body().getContent()!=null) {
                        Log.d("",""+response.body().getContent());
                        String html = response.body().getContent();
                        browser.loadDataWithBaseURL(getResources().getString(R.string.base_url),html, "text/html", null,null);
                    }
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


                    browser.getSettings().setLoadWithOverviewMode(true);
                    browser.getSettings().setUseWideViewPort(true);

                }
                else
                {
                    ((TextView) findViewById(R.id.banner_name)).setVisibility(View.INVISIBLE);
                    findViewById(R.id.no_notice).setVisibility(View.VISIBLE);
                    findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);
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
                if(bookmarked)
                    call = api_service.bookmark_read("Bearer "+access_token,new BookmarkReadRequestBody(id,"unstar"));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if(response.code()==201||response.code()==200) {
                            if (bookmarked) {
                                ((ImageView) v).setImageResource(R.drawable.bookmark);
                                bookmarked=!bookmarked;
                                returnIntent.putExtra("bookmarked",bookmarked);
                                setResult(Activity.RESULT_OK, returnIntent);
                                Toast.makeText(getApplicationContext(), "Notice Unmarked", Toast.LENGTH_SHORT).show();
                            } else {
                                ((ImageView) v).setImageResource(R.drawable.bookmarked);
                                bookmarked=!bookmarked;
                                returnIntent.putExtra("bookmarked",bookmarked);
                                setResult(Activity.RESULT_OK, returnIntent);
                                Toast.makeText(getApplicationContext(), "Notice Bookmarked", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    public void markRead(int id)
    {
        ApiInterface api_service;
        retrofit = UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit);
        api_service = retrofit.create(ApiInterface.class);
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<Void> call = api_service.bookmark_read("Bearer " + access_token, new BookmarkReadRequestBody(id, "read"));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("", response.code() + "");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent calling= getIntent();
        if(calling.getBooleanExtra("notification",false) || dataString != null || loginPage)
        {
            Intent i = new Intent(NoticeViewScreen.this,NoticeListScreen.class);
            startActivity(i);
            finish();
        }
        super.onBackPressed();
    }
}
