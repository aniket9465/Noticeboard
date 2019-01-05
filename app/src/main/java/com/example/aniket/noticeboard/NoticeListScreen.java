package com.example.aniket.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.ApiResponseClasses.FiltersList;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.example.aniket.noticeboard.Utilities.ApiInterface;
import com.example.aniket.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.example.aniket.noticeboard.Utilities.FilterDialog;
import com.example.aniket.noticeboard.Utilities.UtilityFunctions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NoticeListScreen extends AppCompatActivity {

    static Retrofit retrofit;
    private ArrayList<NoticeCardResponse> mlist = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView view;
    private NoticeListAdapter adapter;
    private String category;
    private ArrayList<Filters> filters;
    private FilterDialog filterDialog;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private Animation animShow, animHide ,animShowSubFilters,animHideSubFilters;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notices);
        filters=new ArrayList<>();
        getFilters();
        category="All";
        progressDialog=new ProgressDialog(this);

        UtilityFunctions.tokenRefresh(this);

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
        animShowSubFilters = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animShowSubFilters.setDuration(200);
        animHideSubFilters = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animShowSubFilters.setDuration(200);



        findViewById(R.id.nav_complete_menu).setVisibility(View.VISIBLE);

        view = findViewById(R.id.notice_list);
        view.requestFocus();
        setUpOnClicks();


        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                cancelFilters(v);
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
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mlist.clear();
                mScrollListener.resetState();
                noticeRequest();

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark);

        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
         mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 0 && totalItemsCount <= mlist.size()) {
                    noticeRequest();
                }
            }
        };


        view.setLayoutManager(manager);
        adapter = new NoticeListAdapter(mlist,NoticeListScreen.this);
        view.setAdapter(adapter);
        view.addOnScrollListener(mScrollListener);



    }


    void noticeRequest() {

        UtilityFunctions.tokenRefresh(this);

        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing()) {
                progressDialog=ProgressDialog.show(this, "Loading", "please wait", true);
            }


        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);




        Call<NoticeListResponse> call = api_service.get_notices( (mlist.size()/10 + 1)+"", access_token);
        if(category.equals("All")) {
            String filterid=filterDialog.getFilterId();
            if(filterid.equals("-1")&&(!filterDialog.dateFilterSelected)) {
                call = api_service.get_notices((mlist.size() / 10 + 1) + "", access_token);
            }
            else {
                if (filterid.equals("-1")) {
                    call = api_service.dateFilter(filterDialog.startDate, filterDialog.endDate, (mlist.size() / 10) + "", access_token);
                } else {
                    if (!filterDialog.dateFilterSelected) {
                        call = api_service.filteredNotices(filterid, (mlist.size() / 10 + 1) + "", access_token);
                    } else {
                        call = api_service.filterAndDateFilterNotices(filterDialog.startDate, filterDialog.endDate, filterid, (mlist.size() / 10) + "", access_token);
                    }
                }
            }
        }
        else {

            if(category.equals("bookmarks"))
            {
                    call = api_service.bookmarkedNotices( (mlist.size()/10+1)+"", access_token);
            }
            else
                {
                    if(category.equals("expired"))
                    {
                        call = api_service.expiredNotices( (mlist.size()/10+1)+"", access_token);
                    }
                }
        }


        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call, Response<NoticeListResponse> response) {

                if(response.body()!=null) {

                    for (int i = 0; i < response.body().getNotices().size(); ++i) {
                        mlist.add(response.body().getNotices().get(i));
                    }

                }
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                },300);
                if (swipeContainer != null) {
                        swipeContainer.setRefreshing(false);
                }

                adapter.notifyData(mlist);

            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);

                Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                },300);

            }
        });

    }

    private void setUpOnClicks()
    {

        findViewById(R.id.nav_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category="All";
                String heading="All Notices";
                if(findViewById(R.id.swipeContainer).getVisibility()==View.INVISIBLE)
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                cancelFilters(v);

                findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(NoticeListScreen.this, SearchNoticeScreen.class);
                        startActivityForResult(in, 1);
                    }
                });

                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                mScrollListener.resetState();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.START);



                filterDialog.resetFilters();
                applyFilters(v);

            }
        });

        findViewById(R.id.filter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.setOriginal();
                if(findViewById(R.id.filter_menu).getVisibility()==View.INVISIBLE) {
                    findViewById(R.id.swipeContainer).setVisibility(View.INVISIBLE);
                    findViewById(R.id.filter_menu).setVisibility(View.VISIBLE);
                    findViewById(R.id.filter_menu).startAnimation(animShow);
                }
                else
                {

                    findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                    findViewById(R.id.filter_menu).startAnimation(animHide);
                    cancelFilters(v);
                }
            }
        });

        findViewById(R.id.nav_expired).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category="expired";
                String heading="Expired Notices";
                findViewById(R.id.back).setVisibility(View.VISIBLE);
                findViewById(R.id.filter_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(NoticeListScreen.this, ExpiredSearch.class);
                        startActivityForResult(in, 1);
                    }
                });
                findViewById(R.id.drawer_opener).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                mScrollListener.resetState();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();

                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                cancelFilters(v);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category="All";
                String heading="All Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                mScrollListener.resetState();
                findViewById(R.id.back).setVisibility(View.INVISIBLE);
                findViewById(R.id.filter_button).setVisibility(View.VISIBLE);
                findViewById(R.id.content_frame).startAnimation(animHideSubFilters);
                findViewById(R.id.content_frame).startAnimation(animShowSubFilters);
                findViewById(R.id.search).setVisibility(View.VISIBLE);
                findViewById(R.id.drawer_opener).setVisibility(View.VISIBLE);
                noticeRequest();

                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                cancelFilters(v);

            }
        });

        findViewById(R.id.nav_bookmarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category="bookmarks";
                String heading="Bookmarked Notices";
                findViewById(R.id.back).setVisibility(View.VISIBLE);
                findViewById(R.id.filter_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.search).setVisibility(View.INVISIBLE);
                findViewById(R.id.drawer_opener).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                mScrollListener.resetState();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();


                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                cancelFilters(v);
            }
        });

        findViewById(R.id.nav_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.channeli.noticeboard"));
                startActivity(intent);
            }
        });

        findViewById(R.id.nav_notification_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeListScreen.this,NotificationSettings.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.nav_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.logout(NoticeListScreen.this);
                Intent i = new Intent(NoticeListScreen.this,LoginScreen.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void applyFilters(View v)
    {
        mlist.clear();
        filterDialog.setNew();
        findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
        if(findViewById(R.id.filter_menu).getVisibility()==View.VISIBLE) {
            findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
            findViewById(R.id.filter_menu).startAnimation(animHide);
        }
        noticeRequest();
    }

    public void cancelFilters(View v)
    {
        if(findViewById(R.id.swipeContainer).getVisibility()==View.INVISIBLE) {
            findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
        }
        if(findViewById(R.id.filter_menu).getVisibility()==View.VISIBLE) {
            findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
            findViewById(R.id.filter_menu).startAnimation(animHide);
        }

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



    private void getFilters()
    {
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);

        Call<FiltersList> call = api_service.getFilters( access_token);
        call = api_service.getFilters( access_token);

        call.enqueue(new Callback<FiltersList>() {
            @Override
            public void onResponse(Call<FiltersList> call, Response<FiltersList> response) {

                filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                if(response.body()!=null) {

                    filters=response.body().getResult();
                    filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                }
                noticeRequest();
            }

            @Override
            public void onFailure(Call<FiltersList> call, Throwable t) {

                Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();
                filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                noticeRequest();
            }
        });

    }

}
