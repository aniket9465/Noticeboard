package com.channeli.img.noticeboard;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.img.noticeboard.ApiResponseClasses.Filters;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.channeli.img.noticeboard.Utilities.ApiInterface;
import com.channeli.img.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.channeli.img.noticeboard.Utilities.UtilityFunctions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.channeli.img.noticeboard.NoticeListScreen.retrofit;

public class ImportantUnreadNotices extends AppCompatActivity {

    ArrayList<NoticeCardResponse> mlist;
    SwipeRefreshLayout swipeContainer;
    RecyclerView view;
    private NoticeListAdapter adapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.important_unread);

        UtilityFunctions.tokenRefresh(this);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setVisibility(View.INVISIBLE);
        findViewById(R.id.NoNotices).setVisibility(View.INVISIBLE);
        findViewById(R.id.NoInternet).setVisibility(View.INVISIBLE);

        mlist = new ArrayList<>();


        ImageView back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                finish();
            }
        });


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                unreadImportantNoticeSearch();
                mlist.clear();

            }
        });
        swipeContainer.setColorScheme(android.R.color.holo_blue_dark);



        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
        mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 0 && totalItemsCount <= mlist.size()) {
                    unreadImportantNoticeSearch();
                }
            }
        };


        view = findViewById(R.id.notice_list);
        view.setLayoutManager(manager);
        adapter = new NoticeListAdapter(mlist, ImportantUnreadNotices.this);
        view.setAdapter(adapter);
        view.addOnScrollListener(mScrollListener);

    }



    void unreadImportantNoticeSearch() {

        UtilityFunctions.tokenRefresh(this);

        ApiInterface api_service;
        api_service = UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
        String access_token=getSharedPreferences("Noticeboard_data", 0).getString("access token", null);


        Call<NoticeListResponse> call;
        call = api_service.importantUnreadNotices( ((int)(mScrollListener.currentPage + 1)) + "" , true , true ,"Bearer " + access_token);

        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call,final Response<NoticeListResponse> response) {


                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(response.body()!=null) {
                            if(mlist.size()!=0)
                                mlist.remove(mlist.size()-1);
                            for (int i = 0; i < response.body().getNotices().size(); ++i) {
                                mlist.add(response.body().getNotices().get(i));
                            }
                            if(response.body().next!=null) {
                                mlist.add(null);
                            }
                            mScrollListener.nextPage=response.body().next;
                        }
                        else
                        {
                            mScrollListener.nextPage=null;
                            if(mlist.size()!=0)
                                mlist.remove(mlist.size()-1);
                        }
                        adapter.notifyData(mlist);
                        adapter.notifyDataSetChanged();
                        if(mlist.size()==0)
                        {
                            findViewById(R.id.NoNotices).setVisibility(View.VISIBLE);
                            findViewById(R.id.NoInternet).setVisibility(View.INVISIBLE);
                        }
                        else{
                            findViewById(R.id.NoNotices).setVisibility(View.INVISIBLE);
                            findViewById(R.id.NoInternet).setVisibility(View.INVISIBLE);
                        }
                    }
                },300);


                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {

                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);

                Toast.makeText(ImportantUnreadNotices.this, "connection error", Toast.LENGTH_SHORT).show();

                mScrollListener.nextPage=null;
                if(mlist.size()!=0)
                    mlist.remove(mlist.size()-1);
                adapter.notifyData(mlist);
                adapter.notifyItemRangeChanged(0, mlist.size());

                findViewById(R.id.NoNotices).setVisibility(View.INVISIBLE);
                findViewById(R.id.NoInternet).setVisibility(View.VISIBLE);
            }
        });
    }

}
