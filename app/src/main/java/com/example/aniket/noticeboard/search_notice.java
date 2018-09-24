package com.example.aniket.noticeboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aniket.noticeboard.list_of_notices.api_service;
import static com.example.aniket.noticeboard.list_of_notices.retrofit;

public class search_notice extends AppCompatActivity {

    ArrayList<notice_card> mlist;
    String searched = "";
    View recent_searches;
    ProgressDialog progressDialog;
    String base_url;
    ImageView back_button;
    ArrayList<String> listItems = new ArrayList<String>();
    ListView search_list;
    SwipeRefreshLayout swipeContainer;
    ArrayAdapter<String> recent_adapter;
    android.support.v7.widget.SearchView searchView;
    RecyclerView view;
    private notices_list_adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_notice);
        search_list = findViewById(R.id.recent_searches_list);
        recent_searches = findViewById(R.id.recent_searches);
        swipeContainer = findViewById(R.id.swipeContainer);
        recent_searches.setVisibility(View.VISIBLE);
        swipeContainer.setVisibility(View.INVISIBLE);

        listItems = new ArrayList<>();
        String recents = getSharedPreferences("Noticeboard_data", 0).getString("recent_searches", "");
        String[] recent = recents.split(";;;");
        for (int i = 0; i < recent.length; ++i) {
            listItems.add(recent[i]);
        }
        recent_adapter = new MyListAdapter(this,
                listItems);
        search_list.setAdapter(recent_adapter);
        recent_adapter.notifyDataSetChanged();

        mlist = new ArrayList<>();


        base_url = getResources().getString(R.string.base_url);
        api_service = functions.getRetrofitInstance(base_url, retrofit).create(api_interface.class);


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


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (searched.equals("")) {
                    swipeContainer.setRefreshing(false);
                    return;
                }
                notice_search(searched);
                mlist.clear();

            }
        });
        swipeContainer.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark);


        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
        view = findViewById(R.id.notice_list);
        Log.d("tag", view + "");
        view.setLayoutManager(manager);
        adapter = new notices_list_adapter(mlist, search_notice.this);
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
        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_bar);
         if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        android.support.v7.widget.SearchView.OnQueryTextListener queryTextListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                String recents = getSharedPreferences("Noticeboard_data", 0).getString("recent_searches", "");
                String[] recent = recents.split(";;;");
                listItems.clear();
                for (int i = 0; i < recent.length; ++i) {
                    listItems.add(recent[i]);
                }
                recent_adapter.notifyDataSetChanged();
                recent_searches.setVisibility(View.VISIBLE);
                swipeContainer.setVisibility(View.INVISIBLE);
                Log.d(" ", newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Log.d("/////////////////////", query);
                focus_change(findViewById(R.id.notice_list));
                if (query.equals("")) {
                    recent_searches.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.INVISIBLE);
                    return false;
                }
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
                SharedPreferences.Editor edit = pref.edit();
                String recents = getSharedPreferences("Noticeboard_data", 0).getString("recent_searches", "");
                String[] recent = recents.split(";;;");
                if(!Arrays.asList(recent).contains(query)) {
                    String recent_string = query + ";;;";
                    for (int i = 0; i < 4 && i < recent.length; ++i) {
                        recent_string += recent[i] + ";;;";
                    }
                    edit.putString("recent_searches", recent_string);
                    edit.commit();
                }
                else
                {
                    String recent_string = query + ";;;";
                    for (int i = 0; i < 5 && i < recent.length; ++i) {
                        if (!recent[i].equals(query + ""))
                            recent_string += recent[i] + ";;;";
                    }
                    edit.putString("recent_searches", recent_string);
                    edit.commit();
                }
                recent_searches.setVisibility(View.INVISIBLE);
                swipeContainer.setVisibility(View.VISIBLE);
                notice_search(query);
                searched = query;
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }


    void notice_search(String search_query) {

        if (!search_query.equals(searched)) ;
        mlist.clear();
        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing())
                progressDialog = ProgressDialog.show(search_notice.this, "Loading", "please wait", true);
        adapter.notifyData(mlist);
        if (search_query.equals("")) {
            if (swipeContainer != null)
                swipeContainer.setRefreshing(false);
            if (progressDialog != null)
                progressDialog.dismiss();
            return;
        }
        // confirm the url pattern
        Log.d("", "notice_request");
        Call<notice_list> call = api_service.search_notices(base_url + "search?keyword=" + search_query + "&" + mlist.size() + "-" + (mlist.size() + 9), getSharedPreferences("Noticeboard_data", 0).getString("access token", null));
        call.enqueue(new Callback<notice_list>() {
            @Override
            public void onResponse(Call<notice_list> call, Response<notice_list> response) {
                if (response.body() != null) {
                    for (int i = 0; i < response.body().getNotices().size(); ++i) {
                        mlist.add(response.body().getNotices().get(i));
                    }
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


    public void focus_change(View v) {
        Log.d("", "focus change" + view + view.requestFocus());
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public class MyListAdapter extends ArrayAdapter<String> {

        private Activity context;
        private ArrayList<String> maintitle;

        public MyListAdapter(Activity context, ArrayList<String> maintitle) {
            super(context, 0, maintitle);
            this.context = context;
            this.maintitle = new ArrayList<>();
            this.maintitle = maintitle;
            Log.d(";;;;;;;;", "" + this.maintitle.size());
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.recent_search_view, null, true);

            final TextView titleText = (TextView) rowView.findViewById(R.id.recent_search_text);
            Log.d(".........", titleText + " " + position + " " + maintitle + " ");
            titleText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setQuery(titleText.getText() + "", false);
                    focus_change(findViewById(R.id.notice_list));
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("Noticeboard_data", 0);
                    SharedPreferences.Editor edit = pref.edit();
                    String recents = getSharedPreferences("Noticeboard_data", 0).getString("recent_searches", "");
                    String[] recent = recents.split(";;;");
                    String recent_string = titleText.getText() + ";;;";
                    for (int i = 0; i < 5 && i < recent.length; ++i) {
                        if (!recent[i].equals(titleText.getText() + ""))
                            recent_string += recent[i] + ";;;";
                    }
                    edit.putString("recent_searches", recent_string);
                    edit.commit();
                    recent_searches.setVisibility(View.INVISIBLE);
                    swipeContainer.setVisibility(View.VISIBLE);
                    searched = "";
                    notice_search(titleText.getText() + "");
                    searched = titleText.getText() + "";
                }
            });
            titleText.setText(maintitle.get(position));
            return rowView;

        }
    }

}
