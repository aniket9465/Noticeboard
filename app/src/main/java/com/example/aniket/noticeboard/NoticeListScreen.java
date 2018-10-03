package com.example.aniket.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.ApiResponseClasses.FiltersList;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.example.aniket.noticeboard.Utilities.ApiInterface;
import com.example.aniket.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.example.aniket.noticeboard.Utilities.UtilityFunctions;

import java.nio.file.Path;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
    private String filter,subfilter;
    private String filterValue;
    private ArrayList<Filters> filters;
    private int filterid;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notices);
        getFilters();
        filter="All";
        filters=new ArrayList<>();
        filterValue=null;
        progressDialog=new ProgressDialog(this);

        findViewById(R.id.nav_complete_menu).setVisibility(View.VISIBLE);
        findViewById(R.id.drawer_subfilters).setVisibility(View.INVISIBLE);

        view = findViewById(R.id.notice_list);
        view.requestFocus();
        findViewById(R.id.date_time_filter).setVisibility(View.INVISIBLE);
        findViewById(R.id.cover_view).setVisibility(View.INVISIBLE);
        setUpFilters();


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
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noticeRequest();
                mlist.clear();

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark);

        final LinearLayoutManager manager = new LinearLayoutManager(this.getApplicationContext());
        EndlessRecyclerViewScrollListener mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 0 && totalItemsCount <= mlist.size()) {
                    Log.d("", "onloadmore");
                    noticeRequest();
                }
            }
        };


        view.setLayoutManager(manager);
        adapter = new NoticeListAdapter(mlist,NoticeListScreen.this);
        view.setAdapter(adapter);
        view.addOnScrollListener(mScrollListener);

        noticeRequest();

    }


    void noticeRequest() {


        if (swipeContainer != null)
            if (!swipeContainer.isRefreshing()) {
                progressDialog=ProgressDialog.show(this, "Loading", "please wait", true);
            }


        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);




        Call<NoticeListResponse> call = api_service.get_notices( (mlist.size()/10)+"", access_token);
        if(filter.equals("All"))
            call = api_service.get_notices( (mlist.size()/10)+"", access_token);
        else {
            if (filter.equals("Date_filter")) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String formattedDateStart = "";
                String formattedDateEnd = "";
                if (subfilter.equals("A week ago")) {
                    c.add(Calendar.DAY_OF_YEAR, -7);
                    formattedDateStart = sd.format(c.getTime());
                    c.add(Calendar.DAY_OF_YEAR, -14);
                    formattedDateEnd = sd.format(c.getTime());
                }
                if (subfilter.equals("Two week ago")) {
                    c.add(Calendar.DAY_OF_YEAR, -14);
                    formattedDateStart = sd.format(c.getTime());
                    c.add(Calendar.DAY_OF_YEAR, -21);
                    formattedDateEnd = sd.format(c.getTime());
                }
                if (subfilter.equals("A month ago")) {
                    c.add(Calendar.DAY_OF_YEAR, -21);
                    formattedDateStart = sd.format(c.getTime());
                    c.add(Calendar.DAY_OF_YEAR, -31);
                    formattedDateEnd = sd.format(c.getTime());
                }
                if (subfilter.equals("More than a month ago")) {
                    c.add(Calendar.DAY_OF_YEAR, -31);
                    formattedDateStart = sd.format(c.getTime());
                    c.add(Calendar.DAY_OF_YEAR, -365);
                    formattedDateEnd = sd.format(c.getTime());
                }
                if(subfilter.equals("Recent First"))
                    call = api_service.get_notices( (mlist.size()/10)+"", access_token);
                else
                    call = api_service.dateFilter(formattedDateStart, formattedDateEnd, (mlist.size() / 10) + "", access_token);

            }
            else
            {
                if(filter.equals("bookmarks"))
                {
                    call = api_service.bookmarkedNotices( (mlist.size()/10)+"", access_token);
                }
                else
                {
                    if(filter.equals("expired"))
                    {
                        call = api_service.expiredNotices( (mlist.size()/10)+"", access_token);
                    }
                    else
                    {
                        call = api_service.filteredNotices( filterid+"",(mlist.size()/10)+"", access_token);
                    }
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

                progressDialog.dismiss();
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

                progressDialog.dismiss();

            }
        });

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

                if(response.body()!=null) {

                    filters=response.body().getResult();

                }

            }

            @Override
            public void onFailure(Call<FiltersList> call, Throwable t) {

                Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setUpFilters()
    {
        findViewById(R.id.cover_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","cover view");
                findViewById(R.id.date_time_filter).setVisibility(View.INVISIBLE);
                findViewById(R.id.cover_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.time_filter_spinner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.date_time_filter).setVisibility(View.VISIBLE);
                findViewById(R.id.cover_view).setVisibility(View.VISIBLE);
            }
        });
        View.OnClickListener dateSelected=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="Date_filter";
                subfilter=((TextView)v).getText().toString();
                findViewById(R.id.recent_first).setBackgroundColor(Color.parseColor("#ffffff"));
                findViewById(R.id.a_week_ago).setBackgroundColor(Color.parseColor("#ffffff"));
                findViewById(R.id.a_month_ago).setBackgroundColor(Color.parseColor("#ffffff"));
                findViewById(R.id.more_than_month).setBackgroundColor(Color.parseColor("#ffffff"));
                findViewById(R.id.two_week_ago).setBackgroundColor(Color.parseColor("#ffffff"));
                v.setBackgroundColor(Color.parseColor("#EDF4FF"));
                ((TextView)findViewById(R.id.textView)).setText(subfilter);
                findViewById(R.id.cover_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.date_time_filter).setVisibility(View.INVISIBLE);
                mlist.clear();
                adapter.notifyData(mlist);
                noticeRequest();
            }
        };
        findViewById(R.id.recent_first).setOnClickListener(dateSelected);
        findViewById(R.id.a_week_ago).setOnClickListener(dateSelected);
        findViewById(R.id.two_week_ago).setOnClickListener(dateSelected);
        findViewById(R.id.a_month_ago).setOnClickListener(dateSelected);
        findViewById(R.id.more_than_month).setOnClickListener(dateSelected);

        findViewById(R.id.nav_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="All";
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.date_time_bar);
                findViewById(R.id.swipeContainer).setLayoutParams(params);
                String heading="All Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                findViewById(R.id.date_time_bar).setVisibility(View.VISIBLE);
                mlist.clear();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                findViewById(R.id.recent_first).callOnClick();
            }
        });

        findViewById(R.id.nav_expired).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="expired";
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.lay);
                findViewById(R.id.swipeContainer).setLayoutParams(params);
                String heading="Expired Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                findViewById(R.id.date_time_bar).setVisibility(View.INVISIBLE);
                mlist.clear();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();
            }
        });

        findViewById(R.id.nav_bookmarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="bookmarks";
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.lay);
                findViewById(R.id.swipeContainer).setLayoutParams(params);
                String heading="Bookmarked Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                findViewById(R.id.date_time_bar).setVisibility(View.INVISIBLE);
                mlist.clear();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();
            }
        });

        findViewById(R.id.nav_placement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="placement";
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.lay);
                findViewById(R.id.swipeContainer).setLayoutParams(params);
                findViewById(R.id.date_time_bar).setVisibility(View.INVISIBLE);
                String heading="Placement Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                filterid=-1;
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("placement"))
                    {
                        filterid=filters.get(i).getId();
                        break;
                    }
                    for(int j=0;j<filters.get(i).getBanner().size();++j)
                    {
                        if(filters.get(i).getBanner().get(j).getName().equals("placement"))
                        {
                            filterid=filters.get(i).getBanner().get(j).getId();
                        }
                    }
                }
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();
            }
        });

        findViewById(R.id.nav_authorities).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="placement";
                findViewById(R.id.nav_complete_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.drawer_subfilters).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.main_filter)).setText("Authorities");
            }
        });
        findViewById(R.id.nav_departments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="placement";
                findViewById(R.id.nav_complete_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.drawer_subfilters).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.main_filter)).setText("Departments");
            }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.nav_complete_menu).setVisibility(View.VISIBLE);
                findViewById(R.id.drawer_subfilters).setVisibility(View.INVISIBLE);
            }
        });



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


}
