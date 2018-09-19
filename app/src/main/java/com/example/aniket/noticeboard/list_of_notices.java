package com.example.aniket.noticeboard;

import android.accessibilityservice.AccessibilityService;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class list_of_notices extends AppCompatActivity {

    static Retrofit retrofit;
    static api_interface api_service;
    ArrayList<notice_card> mlist = new ArrayList<>();
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeContainer;
    String base_url = "http://localhost:8000/get_notices/";
    RecyclerView view;
    private boolean isLoading = false;
    private notices_list_adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api_service = functions.getRetrofitInstance(base_url, retrofit).create(api_interface.class);
        setContentView(R.layout.list_of_notices);
        view = findViewById(R.id.notice_list);
        view.requestFocus();
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(list_of_notices.this, search_notice.class);
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
        swipeContainer.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark);
        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
        Log.d("tag", view + "");
        view.setLayoutManager(manager);
        adapter = new notices_list_adapter(mlist,getApplicationContext());
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
        view.setOnScrollListener(mScrollListener);

    }

    public void focus_change(View v) {
        Log.d("", "focus change" + view + view.requestFocus());
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    void notice_request() {
        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing())
                progressDialog = ProgressDialog.show(list_of_notices.this, "Loading", "please wait", true);
        adapter.notifyData(mlist);
        isLoading = false;
        // confirm the url pattern
        Log.d("", "notice_request");
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        Call<notice_list> call = api_service.get_notices(base_url + mlist.size() + "/", access_token);
        Log.d("tag////////", call.request().header("access_token") + "" + access_token);
        call.enqueue(new Callback<notice_list>() {
            @Override
            public void onResponse(Call<notice_list> call, Response<notice_list> response) {
                for (int i = 0; i < response.body().getNotices().size(); ++i) {
                    mlist.add(response.body().getNotices().get(i));
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
            public void onFailure(Call<notice_list> call, Throwable t) {
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);
                Toast.makeText(list_of_notices.this, "connection error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
        Log.d("", "fad");
        adapter.notifyData(mlist);

    }


}
