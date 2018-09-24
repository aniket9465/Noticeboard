package com.example.aniket.noticeboard;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

public class notice_view extends AppCompatActivity {
    static api_interface api_service;
    String base_url;
    Retrofit retrofit;
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
        base_url = getResources().getString(R.string.base_url) + "notice/" + id + "/";
        retrofit = functions.getRetrofitInstance(base_url, retrofit);
        api_service = functions.getRetrofitInstance(base_url, retrofit).create(api_interface.class);
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<notice_content> call = api_service.notice_content(base_url, access_token);
        call.enqueue(new Callback<notice_content>() {
            @Override
            public void onResponse(Call<notice_content> call, Response<notice_content> response) {
                if (response.body() != null) {
                    ((TextView) findViewById(R.id.banner_name)).setText(response.body().banner.getName());
                    ((TextView) findViewById(R.id.notice_title)).setText(response.body().getTitle());
                    ((TextView) findViewById(R.id.date_time_notice_view)).setText(response.body().getDatetimeModified());
                    String  yourData="<h2 style=\"text-align: center;\"><u><strong>Presenting ET Campus Stars | Registrations open for Final and Pre-Final year students</strong></u></h2>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p><span style=\"font-size: 20px;\">Greetings from The Economic Times (Digital) team.</span></p>\n<p><span style=\"font-size:20px;\">&nbsp;</span></p>\n<p><span style=\"font-size:20px;\">We are pleased to inform you that the registrations for the second edition of ET Campus Stars (ETCS) are now open and would like to invite students from your institute to register for India&rsquo;s largest program that identifies and rewards the brightest engineering minds in the country.</span></p>\n<p>&nbsp;</p>\n<p><span style=\"font-size:20px;\">About ET Campus Stars 2018-19</span></p>\n<p><span style=\"font-size:20px;\">The program, launched in partnership with Aspiring Minds (Assessment Partner) and Bennett University (Knowledge Partner), is open to participation from engineering students who are in the third or final year of an undergraduate engineering course from any of the AICTE approved colleges. ETCS 2018-19 consists of four phases:</span></p>\n<p><span style=\"font-size:20px;\">Phase 1: Registration and online psychometric test</span></p>\n<p><span style=\"font-size:20px;\">Phase 2: Online test to assess functional knowledge</span></p>\n<p><span style=\"font-size:20px;\">Phase 3: On-ground assessment of group dynamics and leadership &nbsp;</span></p>\n<p><span style=\"font-size:20px;\">Phase 4: One-on-one interaction with Program Jury</span></p>\n<p><span style=\"font-size:20px;\">Each phase would be followed by a shortlisting process. The students who move to Phase 4 will be interviewed by one of the CEOs on the program&rsquo;s Jury Panel. To know more about the Program phases, Process and Jury Panel, please visit www.etcampusstars.com</span></p>\n<p><span style=\"font-size:20px;\">&nbsp;</span></p>\n<p><span style=\"font-size:20px;\">Why participate in ETCS?</span></p>\n<p><span style=\"font-size:20px;\">ETCS is not only the largest but also the most intensive program to identify the best engineering talent. Acknowledged as a &lsquo;life-changing&rsquo; experience by the last year&rsquo;s participants, ETCS offers the following gratifications to students:</span></p>\n<p><span style=\"font-size:20px;\">Self-discovery of core strengths/ limitations on a globally-validated and industry-endorsed process</span></p>\n<p><span style=\"font-size:20px;\">Opportunity to benchmark oneself against other engineering students across the country</span></p>\n<p><span style=\"font-size:20px;\">Inspiring and enriching exclusive interaction with CEOs- once in a life-time opportunity!</span></p>\n<p><span style=\"font-size:20px;\">A chance to be featured in The Economic Times and gain nation-wide recognition</span></p>\n<p><span style=\"font-size:20px;\">Opportunity to jump-start the career early-on</span></p>\n<p><span style=\"font-size:20px;\">&nbsp;</span></p>\n<p><span style=\"font-size:20px;\">How to participate?</span></p>\n<p><span style=\"font-size:20px;\">To participate, students of third and final year engineering have to register on&nbsp; <a href=\"http://www.etcampusstars.com\" target=\"_blank\">http://www.etcampusstars.com</a>&nbsp;and complete the online test (Phase 1) on or before 26 October 2018. They can also engage with ET Campus Stars community on Facebook&nbsp;<a href=\"http://www.facebook.com/ETCampusStar\" target=\"_blank\">http://www.facebook.com/ETCampusStar</a> and Twitter&nbsp;<a href=\"http://www.twitter.com/ETCampusStars\" target=\"_blank\">http://www.twitter.com/ETCampusStars</a></span></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p><span style=\"font-size:20px;\"><a href=\"/media/notices/uploads/Placement%20Office/thakur/ET_Campus_Stars_2.0_Poster.jpg\" target=\"_blank\">/media/notices/uploads/Placement Office/thakur/ET_Campus_Stars_2.0_Poster.jpg</a></span></p>\n\", \"datetime_modified2018-09-22T18:48:15";
                    browser.loadData(yourData, "text/html; charset=utf-8", "UTF-8");
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
            public void onFailure(Call<notice_content> call, Throwable t) {
                ((TextView) findViewById(R.id.banner_name)).setVisibility(View.INVISIBLE);
                findViewById(R.id.no_notice).setVisibility(View.VISIBLE);
                findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);
            }
        });

    }
}
