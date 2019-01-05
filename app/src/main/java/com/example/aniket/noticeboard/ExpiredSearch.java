package com.example.aniket.noticeboard;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.example.aniket.noticeboard.Utilities.ApiInterface;
import com.example.aniket.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.example.aniket.noticeboard.Utilities.UtilityFunctions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aniket.noticeboard.NoticeListScreen.retrofit;
/*
    set up calls for filters
*/
public class ExpiredSearch extends AppCompatActivity {

    ArrayList<NoticeCardResponse> mlist;
    String searched = "";
    View recent_searches;
        static ProgressDialog progressDialog;
    ArrayList<String> listItems = new ArrayList<String>();
    ListView search_list;
    SwipeRefreshLayout swipeContainer;
    ArrayAdapter<String> recent_adapter;
    android.support.v7.widget.SearchView searchView;
    private Animation animShow, animHide ,animShowSubFilters,animHideSubFilters;
    RecyclerView view;
    private NoticeListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.expired_search);

        UtilityFunctions.tokenRefresh(this);

        search_list = findViewById(R.id.recent_searches_list);
        recent_searches = findViewById(R.id.recent_searches);
        swipeContainer = findViewById(R.id.swipeContainer);
        recent_searches.setVisibility(View.VISIBLE);
        swipeContainer.setVisibility(View.INVISIBLE);

        progressDialog=new ProgressDialog(this);

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
        animShowSubFilters = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animShowSubFilters.setDuration(200);
        animHideSubFilters = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animShowSubFilters.setDuration(200);


        listItems = new ArrayList<>();
        String recents = getSharedPreferences("Noticeboard_data", 0).getString("recent_searches", "");
        String[] recent = recents.split(";;;");
        for (int i = 0; i < recent.length; ++i) {
            listItems.add(recent[i]);
        }
        recent_adapter = new MyListAdapter(this, listItems);
        search_list.setAdapter(recent_adapter);
        recent_adapter.notifyDataSetChanged();


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
        EndlessRecyclerViewScrollListener mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 0 && totalItemsCount <= mlist.size()) {
                    Log.d("", "onloadmore");
                    notice_search(searched);
                }
            }
        };


        view = findViewById(R.id.notice_list);
        view.setLayoutManager(manager);
        adapter = new NoticeListAdapter(mlist, ExpiredSearch.this);
        view.setAdapter(adapter);
        view.addOnScrollListener(mScrollListener);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_bar);
        View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        final EditText editText = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                findViewById(R.id.recent_searches).setVisibility(View.VISIBLE);
            }
        });

        editText.setHintTextColor(getResources().getColor(R.color.white));
        editText.setHint(" Search expired notices");
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        editText.setTextColor(Color.parseColor("#ffffff"));
        editText.setHintTextColor(Color.parseColor("#8EB2E7"));
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(editText, R.drawable.cursor);
        } catch (Exception e) {
            Log.d("tag",e.toString());
        }
        ImageView searchClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_clear_white_24dp);

        if (null != searchView) {
            try {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
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

                return true;
            }


            public boolean onQueryTextSubmit(String query) {

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

                if (!Arrays.asList(recent).contains(query)) {
                    String recent_string = query + ";;;";
                    for (int i = 0; i < 4 && i < recent.length; ++i) {
                        recent_string = recent_string.concat(recent[i] + ";;;");
                    }
                    edit.putString("recent_searches", recent_string);
                    edit.apply();
                } else {
                    String recent_string = query + ";;;";
                    for (int i = 0; i < 5 && i < recent.length; ++i) {
                        if (!recent[i].equals(query + ""))
                            recent_string = recent_string.concat(recent[i] + ";;;");
                    }
                    edit.putString("recent_searches", recent_string);
                    edit.commit();
                }

                recent_searches.setVisibility(View.INVISIBLE);
                swipeContainer.setVisibility(View.VISIBLE);
                mlist.clear();
                notice_search(query);
                searched = query;

                return false;
            }

        };
        searchView.setOnQueryTextListener(queryTextListener);
    }



    void notice_search(String search_query) {

        UtilityFunctions.tokenRefresh(this);

        if (!search_query.equals(searched))
            mlist.clear();
        searched = search_query;
        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing()) {
                progressDialog = ProgressDialog.show(ExpiredSearch.this, "Loading", "please wait", true);
            }
        adapter.notifyData(mlist);
        if (search_query.equals("")) {
            if (swipeContainer != null)
                swipeContainer.setRefreshing(false);
            if (progressDialog != null) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                },300);
            }
            return;
        }

        ApiInterface api_service;
        api_service = UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
        String access_token=getSharedPreferences("Noticeboard_data", 0).getString("access token", null);


        Call<NoticeListResponse> call;
        call = api_service.searchExpiredNotices((mlist.size() / 10 + 1) + "" , search_query ,  access_token);

        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call, Response<NoticeListResponse> response) {

                if (response.body() != null) {
                    if (response.body().getNotices() != null)
                        for (int i = 0; i < response.body().getNotices().size(); ++i) {
                            mlist.add(response.body().getNotices().get(i));
                        }
                }

                if (progressDialog != null) {
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                             progressDialog.dismiss();
                        }
                    },300);
                }

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                adapter.notifyData(mlist);
            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {

                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);

                Toast.makeText(ExpiredSearch.this, "connection error", Toast.LENGTH_SHORT).show();

                if (progressDialog != null){
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                             progressDialog.dismiss();
                        }
                    },300);
                }

                adapter.notifyData(mlist);
            }
        });
    }

    private void focus_change(View v) {
        Log.d("", "focus change" + view + view.requestFocus());
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class MyListAdapter extends ArrayAdapter<String> {

        private Activity context;
        private ArrayList<String> maintitle;

        private MyListAdapter(Activity context, ArrayList<String> maintitle) {
            super(context, 0, maintitle);
            this.context = context;
            this.maintitle = new ArrayList<>();
            this.maintitle = maintitle;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.recent_search_view, null, true);

            final TextView titleText = (TextView) rowView.findViewById(R.id.recent_search_text);
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
                            recent_string = recent_string.concat(recent[i] + ";;;");
                    }
                    edit.putString("recent_searches", recent_string);
                    edit.apply();
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
