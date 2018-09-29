package com.example.aniket.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.aniket.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.example.aniket.noticeboard.Utilities.ApiInterface;
import com.example.aniket.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.example.aniket.noticeboard.Utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NoticeListScreen extends AppCompatActivity {

    static Retrofit retrofit;
    static ApiInterface api_service;
    ArrayList<NoticeCardResponse> mlist = new ArrayList<>();
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeContainer;
    String base_url ;
    RecyclerView view;
    private NoticeListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notices);
        base_url = getResources().getString(R.string.base_url);
        Log.d("........",base_url);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit);
        api_service = retrofit.create(ApiInterface.class);
        view = findViewById(R.id.notice_list);
        view.requestFocus();
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(NoticeListScreen.this, SearchNoticeScreen.class);
                startActivityForResult(in, 1);
            }
        });
        findViewById(R.id.drawer_opener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) findViewById(R.id.list_of_notices)).openDrawer(Gravity.LEFT);
            }
        });
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("", "refresh");
                notice_request();
                mlist.clear();

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark);
        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
        Log.d("tag", view + "");
        view.setLayoutManager(manager);
        adapter = new NoticeListAdapter(mlist,NoticeListScreen.this);
        view.setAdapter(adapter);
        notice_request();
        EndlessRecyclerViewScrollListener mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 0 && totalItemsCount <= mlist.size()) {
                    Log.d("", "onloadmore");
                    notice_request();
                }
            }
        };
        view.addOnScrollListener(mScrollListener);

    }

    public void focus_change(View v) {
        Log.d("", "focus change" + view + view.requestFocus());
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception e)
        {
            Log.d("Exception",e.toString());
        }
    }


    void notice_request() {
        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing())
                progressDialog = ProgressDialog.show(NoticeListScreen.this, "Loading", "please wait", true);
        adapter.notifyData(mlist);
        // confirm the url pattern
        Log.d("", "notice_request");
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<NoticeListResponse> call = api_service.get_notices( (mlist.size()/10)+"", access_token);
        Log.d("tag",call.request().url()+"");
        Log.d("tag////////", call.request().header("access_token") + "" + access_token);
        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call, Response<NoticeListResponse> response) {
                if(response.body()!=null) {
                    for (int i = 0; i < response.body().getNotices().size(); ++i) {
                        mlist.add(response.body().getNotices().get(i));
                    }
                }
                    progressDialog.dismiss();
                    if (swipeContainer != null) {
                        swipeContainer.setRefreshing(false);
                        Log.d("......", swipeContainer.isRefreshing() + "");
                    }
                    Log.d("f", "onrespmpse" + mlist.size() + swipeContainer);
                    adapter.notifyData(mlist);

            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {
                Log.d("<<<<<<<<<",t+"");
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);
                Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
        Log.d("", "fad");
        adapter.notifyData(mlist);

    }


}
