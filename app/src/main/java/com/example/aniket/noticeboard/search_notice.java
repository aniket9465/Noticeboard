package com.example.aniket.noticeboard;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aniket.noticeboard.list_of_notices.api_service;
import static com.example.aniket.noticeboard.list_of_notices.retrofit;

public class search_notice extends AppCompatActivity {

    ArrayList<notice_card> mlist;
    String searched = "";
    ProgressDialog progressDialog;
    String base_url;
    ImageView back_button;
    SwipeRefreshLayout swipeContainer;
    RecyclerView view;
    private boolean isLoading = false;
    private notices_list_adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_notice);
        base_url = "http://localhost:8000/get_notices/search_notices/2/";
        mlist = new ArrayList<>();
        api_service = functions.getRetrofitInstance(base_url, retrofit).create(api_interface.class);
        view = findViewById(R.id.notice_list);
        view.requestFocus();
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Log.d("f", "click");
                finish();
            }
        });
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notice_search(searched);
                mlist.clear();

            }
        });
        swipeContainer.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark);
        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
        Log.d("tag", view + "");
        view.setLayoutManager(manager);
        adapter = new notices_list_adapter(mlist,search_notice.this);
        view.setAdapter(adapter);
        EndlessRecyclerViewScrollListener mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 0 && totalItemsCount <= mlist.size()) {
                    Log.d("", "onloadmore");
                    notice_search(searched);
                }
            }
        };
        view.setOnScrollListener(mScrollListener);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_bar);
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        Log.d("", searchView + "");
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                Log.d(" ", newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Log.d("/////////////////////", query);
                notice_search(query);
                searched = query;
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    public void focus_change(View v) {
        Log.d("", "focus change" + view + view.requestFocus());
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void notice_search(String search_query) {
        if (!search_query.equals(searched)) ;
        mlist.clear();
        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing())
                progressDialog = ProgressDialog.show(search_notice.this, "Loading", "please wait", true);
        adapter.notifyData(mlist);
        isLoading = false;
        // confirm the url pattern
        Log.d("", "notice_request");
        Call<notice_list> call = api_service.get_notices(base_url, getSharedPreferences("Noticeboard_data", 0).getString("access token", null));
        call.enqueue(new Callback<notice_list>() {
            @Override
            public void onResponse(Call<notice_list> call, Response<notice_list> response) {
                if (response.body() != null)
                    for (int i = 0; i < response.body().getNotices().size(); ++i) {
                        mlist.add(response.body().getNotices().get(i));
                    }
                if (progressDialog != null)
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
                Toast.makeText(search_notice.this, "connection error", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();

            }
        });
        Log.d("", "fad");
        adapter.notifyData(mlist);
    }

}
