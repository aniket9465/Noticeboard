package com.example.aniket.noticeboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aniket.noticeboard.ApiResponseClasses.Banner;
import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.ApiResponseClasses.FiltersList;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.example.aniket.noticeboard.Utilities.ApiInterface;
import com.example.aniket.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.example.aniket.noticeboard.Utilities.FilterDialog;
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
    private String category;
    private ArrayList<Filters> filters;
    private FilterDialog filterDialog;
    private String filterid;
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




        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
        animShowSubFilters = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animShowSubFilters.setDuration(200);
        animHideSubFilters = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animShowSubFilters.setDuration(200);


        /*subfilterListItems = new ArrayList<>();
        subfilterAdapter = new NoticeListScreen.MyListAdapter(this, subfilterListItems);
        ListView subfilterList = findViewById(R.id.subfilters);
        subfilterList.setAdapter(subfilterAdapter);
        subfilterAdapter.notifyDataSetChanged();
        */

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
        if(category.equals("All")) {
            call = api_service.get_notices((mlist.size() / 10) + "", access_token);

            /*if (filter.equals("Date_filter")) {
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

            }*/
        }
        else {

            if(category.equals("bookmarks"))
            {
                    call = api_service.bookmarkedNotices( (mlist.size()/10)+"", access_token);
            }
            else
                {
                    if(category.equals("expired"))
                    {
                        call = api_service.expiredNotices( (mlist.size()/10)+"", access_token);
                    }
                    else
                    {
                        call = api_service.get_notices((mlist.size() / 10) + "", access_token);
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
                    filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                }
                filterDialog=new FilterDialog(filters,NoticeListScreen.this);

            }

            @Override
            public void onFailure(Call<FiltersList> call, Throwable t) {

                Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();
                filterDialog=new FilterDialog(filters,NoticeListScreen.this);
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
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();


                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                cancelFilters(v);
            }
        });

        findViewById(R.id.filter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                findViewById(R.id.search).setVisibility(View.INVISIBLE);
                findViewById(R.id.drawer_opener).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
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
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();


                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                cancelFilters(v);
            }
        });

        /*

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
                filterid="-1";
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
                filter="authorities";
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.lay);
                findViewById(R.id.swipeContainer).setLayoutParams(params);
                findViewById(R.id.date_time_bar).setVisibility(View.INVISIBLE);
                String heading="Authorities Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                filterid="-1";
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("authorities"))
                    {
                        filterid=filters.get(i).getId();
                        break;
                    }
                    for(int j=0;j<filters.get(i).getBanner().size();++j)
                    {
                        if(filters.get(i).getBanner().get(j).getName().equals("authorities"))
                        {
                            filterid=filters.get(i).getBanner().get(j).getId();
                        }
                    }
                }
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();
            }
        });

        findViewById(R.id.nav_departments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="departments";
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.lay);
                findViewById(R.id.swipeContainer).setLayoutParams(params);
                findViewById(R.id.date_time_bar).setVisibility(View.INVISIBLE);
                String heading="Departments Notices";
                ((TextView)findViewById(R.id.heading)).setText(heading);
                mlist.clear();
                filterid="-1";
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("departments"))
                    {
                        filterid=filters.get(i).getId();
                        break;
                    }
                    for(int j=0;j<filters.get(i).getBanner().size();++j)
                    {
                        if(filters.get(i).getBanner().get(j).getName().equals("departments"))
                        {
                            filterid=filters.get(i).getBanner().get(j).getId();
                        }
                    }
                }
                ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
                noticeRequest();
            }
        });

        findViewById(R.id.authorities_subfilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="authorities";
                findViewById(R.id.nav_complete_menu).startAnimation(animHideSubFilters);
                findViewById(R.id.nav_complete_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.drawer_subfilters).setVisibility(View.VISIBLE);
                findViewById(R.id.drawer_subfilters).startAnimation(animShowSubFilters);
                ((TextView)findViewById(R.id.main_filter)).setText("Authorities");
                subfilterListItems.clear();
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Authorities")) {

                        for (int j = 0; j < filters.get(i).getBanner().size(); ++j) {
                            subfilterListItems.add(filters.get(i).getBanner().get(j));
                        }
                    }
                }
                subfilterAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.departments_subfilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter="departments";
                findViewById(R.id.nav_complete_menu).startAnimation(animHideSubFilters);
                findViewById(R.id.nav_complete_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.drawer_subfilters).setVisibility(View.VISIBLE);
                findViewById(R.id.drawer_subfilters).startAnimation(animShowSubFilters);
                ((TextView)findViewById(R.id.main_filter)).setText("Departments");
                subfilterListItems.clear();
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Departments")) {

                        for (int j = 0; j < filters.get(i).getBanner().size(); ++j) {
                            subfilterListItems.add(filters.get(i).getBanner().get(j));
                        }
                    }
                }
                subfilterAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.nav_complete_menu).setVisibility(View.VISIBLE);
                findViewById(R.id.nav_complete_menu).startAnimation(animShowSubFilters);
                findViewById(R.id.drawer_subfilters).startAnimation(animHideSubFilters);
                findViewById(R.id.drawer_subfilters).setVisibility(View.INVISIBLE);

            }
        });

    */

    }

    public void applyFilters(View v)
    {
        filterDialog.setNew();
        findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
        findViewById(R.id.filter_menu).startAnimation(animHide);

    }

    public void cancelFilters(View v)
    {
        filterDialog.setOriginal();
        findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
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



}
