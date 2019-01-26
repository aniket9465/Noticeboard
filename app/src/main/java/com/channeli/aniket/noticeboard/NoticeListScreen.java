package com.channeli.aniket.noticeboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.aniket.noticeboard.ApiResponseClasses.Filters;
import com.channeli.aniket.noticeboard.ApiResponseClasses.FiltersList;
import com.channeli.aniket.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.channeli.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.channeli.aniket.noticeboard.ApiResponseClasses.UserInfo;
import com.channeli.aniket.noticeboard.Utilities.ApiInterface;
import com.channeli.aniket.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.channeli.aniket.noticeboard.Utilities.FilterDialog;
import com.channeli.aniket.noticeboard.Utilities.GetImage;
import com.channeli.aniket.noticeboard.Utilities.UtilityFunctions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NoticeListScreen extends AppCompatActivity {

    static Retrofit retrofit;
    private ArrayList<NoticeCardResponse> mlist = new ArrayList<>();
    private ArrayList<NoticeCardResponse> tmpMlist =new ArrayList<>();
    private int tmpPos,tmpCurrPage;
    private FilterDialog filterDialog;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView view;
    private NoticeListAdapter adapter;
    private String category;
    private List<Filters> filters;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private Animation animShow, animHide ,animShowSubFilters,animHideSubFilters;
    LinearLayoutManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notices);
        filters=new ArrayList<>();
        filterDialog=new FilterDialog(filters,this);
        getFilters();
        category="All";

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

        manager = new LinearLayoutManager(this.getApplicationContext());
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

        getUserInfo();

    }


    void noticeRequest() {

        UtilityFunctions.tokenRefresh(this);



        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);

        

        Call<NoticeListResponse> call = api_service.get_notices( (mScrollListener.currentPage+1)+"","Bearer" + access_token);
        findViewById(R.id.filter_selected).setVisibility(View.VISIBLE);

        if(category.equals("All")) {
            String filterid=filterDialog.getFilterId();
            if(filterid.equals("-1")&&(!filterDialog.dateFilterSelected)) {
                findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.heading)).setText("All Notices");
                call = api_service.get_notices((mScrollListener.currentPage+1) + "","Bearer" + access_token);
            }
            else {
                if (filterid.equals("-1")) {
                    call = api_service.dateFilter(filterDialog.startDate+" 00:00", filterDialog.endDate+" 23:59", (mScrollListener.currentPage+1) + "","Bearer" + access_token);
                } else {
                    if(filterDialog.subFilter.equals("All"))
                        ((TextView)findViewById(R.id.heading)).setText((String)("All "+filterDialog.mainFilter+" Notices"));
                    else
                        ((TextView)findViewById(R.id.heading)).setText(((String)filterDialog.subFilter + " Notices"));

                    if (!filterDialog.dateFilterSelected) {
                        call = api_service.filteredNotices(filterid, (mScrollListener.currentPage+1) + "","Bearer" + access_token);
                    } else {
                        call = api_service.filterAndDateFilterNotices(filterDialog.startDate+" 00:00", filterDialog.endDate+" 23:59", filterid, (mScrollListener.currentPage+1) + "","Bearer" + access_token);
                    }
                }
            }
        }
        else {

            if(category.equals("bookmarks"))
            {
                findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                call = api_service.bookmarkedNotices( (mScrollListener.currentPage+1)+"","Bearer" + access_token);
            }
            else
                {
                    if(category.equals("expired"))
                    {
                        findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                        call = api_service.expiredNotices( (mScrollListener.currentPage+1)+"","Bearer" + access_token);
                    }
                }
        }


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
                            if(mlist.size()!=0)
                                mlist.remove(mlist.size()-1);
                            mScrollListener.nextPage=null;
                        }
                        adapter.notifyData(mlist);
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
                if(mlist.size()!=0)
                    mlist.remove(mlist.size()-1);
                mScrollListener.nextPage=null;
                adapter.notifyData(mlist);
                Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                    findViewById(R.id.authority).callOnClick();
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
                tmpMlist.clear();
                tmpMlist.addAll(mlist);
                tmpPos=manager.findFirstVisibleItemPosition();
                tmpCurrPage=mScrollListener.currentPage;
                Log.d("//",tmpPos+" "+tmpMlist.size());
                manager.scrollToPosition(0);
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
                mlist.addAll(tmpMlist);
                adapter.notifyData(mlist);
                Log.d("",tmpPos+" "+mlist.size()+"");
                manager.scrollToPositionWithOffset(tmpPos, 0);
                mScrollListener.currentPage=tmpCurrPage;
                findViewById(R.id.back).setVisibility(View.INVISIBLE);
                findViewById(R.id.filter_button).setVisibility(View.VISIBLE);
                findViewById(R.id.content_frame).startAnimation(animHideSubFilters);
                findViewById(R.id.content_frame).startAnimation(animShowSubFilters);
                findViewById(R.id.search).setVisibility(View.VISIBLE);
                findViewById(R.id.drawer_opener).setVisibility(View.VISIBLE);

                findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                findViewById(R.id.swipeContainer).setVisibility(View.VISIBLE);
                cancelFilters(v);

                findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.filter_menu).setVisibility(View.INVISIBLE);
                        cancelFilters(v);
                        Intent in = new Intent(NoticeListScreen.this, SearchNoticeScreen.class);
                        startActivityForResult(in, 1);
                    }
                });

                if(!filterDialog.getFilterId().equals("-1"))
                    if(filterDialog.subFilter.equals("All"))
                        ((TextView)findViewById(R.id.heading)).setText((String)("All "+filterDialog.mainFilter+" Notices"));
                    else
                        ((TextView)findViewById(R.id.heading)).setText(((String)filterDialog.subFilter + " Notices"));

                if (filterDialog.getFilterId().equals("-1") && (!filterDialog.dateFilterSelected)) {
                    findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                } else
                    findViewById(R.id.filter_selected).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.nav_bookmarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category="bookmarks";
                String heading="Bookmarked Notices";
                tmpMlist.clear();
                tmpMlist.addAll(mlist);
                tmpPos=manager.findFirstVisibleItemPosition();
                tmpCurrPage=mScrollListener.currentPage;
                manager.scrollToPosition(0);
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

        Call<List<Filters>> call = api_service.getFilters("Bearer" + access_token);
        call = api_service.getFilters("Bearer " + access_token);
        Log.d("",""+call.request().url());
        call.enqueue(new Callback<List<Filters>>() {
            @Override
            public void onResponse(Call<List<Filters>> call, Response<List<Filters>> response) {

                filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                if(response.body()!=null) {
                    filters=response.body();
                    filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                }
                noticeRequest();
            }

            @Override
            public void onFailure(Call<List<Filters>> call, Throwable t) {
                Log.d("",t.getMessage()+"....."+t.getCause());
                Toast.makeText(NoticeListScreen.this, "connection error !!", Toast.LENGTH_SHORT).show();
                filterDialog=new FilterDialog(filters,NoticeListScreen.this);
                noticeRequest();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(((DrawerLayout)findViewById(R.id.list_of_notices)).isDrawerOpen(Gravity.START))
        {
            ((DrawerLayout)findViewById(R.id.list_of_notices)).closeDrawer(Gravity.START);
            return;
        }
        if(findViewById(R.id.filter_menu).getVisibility()==View.VISIBLE)
        {
            cancelFilters(findViewById(R.id.filter_menu));
            return;
        }
        switch ((String)((TextView)findViewById(R.id.heading)).getText())
        {
            case "Expired Notices" :
                findViewById(R.id.nav_all).callOnClick();
                break;
            case "Bookmarked Notices" :
                findViewById(R.id.nav_all).callOnClick();
                break;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                Integer position=data.getIntExtra("position",-1);
                boolean bookmarked=data.getBooleanExtra("bookmarked",false);
                if(position!=-1)
                {
                    mlist.get(position).setBookmark(bookmarked);
                    adapter.notifyData(mlist);
                }
            }
        }
    }

    void getUserInfo()
    {
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);
        Call<UserInfo> call = api_service.getUserInfo( "Bearer " + access_token);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.body()!=null)
                {
                    ((TextView)findViewById(R.id.username)).setText(response.body().getFullName());
                    Log.d("" , " "+response.body().getDisplayPicture());
                    if(response.body().getDisplayPicture()!=null)
                    {
                         String url = (getResources().getString(R.string.base_url) + response.body().getDisplayPicture());
                         GetImage getImage=new GetImage(NoticeListScreen.this);
                         getImage.execute(url);
                    }
                }
                else
                {
                    Log.d("",call.request().url()+" " + response.message());
                    Toast.makeText(NoticeListScreen.this, "connection error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Toast.makeText(NoticeListScreen.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
