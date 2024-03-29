package com.channeli.img.noticeboard;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.img.noticeboard.ApiResponseClasses.Filters;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.UserInfo.UserInfo;
import com.channeli.img.noticeboard.Utilities.ApiInterface;
import com.channeli.img.noticeboard.Utilities.EndlessRecyclerViewScrollListener;
import com.channeli.img.noticeboard.Utilities.FilterDialog;
import com.channeli.img.noticeboard.Utilities.GetImage;
import com.channeli.img.noticeboard.Utilities.UtilityFunctions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NoticeListScreen extends AppCompatActivity {

    static Retrofit retrofit;
    private ArrayList<NoticeCardResponse> mlist = new ArrayList<>();
    private ArrayList<NoticeCardResponse> tmpMlist =new ArrayList<>();
    private int tmpPos,tmpCurrPage;
    private int tmpState;
    private String tmpNextPage;
    private FilterDialog filterDialog;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView view;
    private NoticeListAdapter adapter;
    private String category;
    private List<Filters> filters;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private Animation animShow, animHide ;
    private Animation backAnimation;
    private Integer unreadImportantNotices;

    LinearLayoutManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notices);
        unreadImportantNotices = 0;
        filters=new ArrayList<>();
        filterDialog=new FilterDialog(filters,this);
        getFilters();
        category="All";
        UtilityFunctions.tokenRefresh(this);

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
        backAnimation = AnimationUtils.loadAnimation(this,R.anim.view_hide_subfilters);


        findViewById(R.id.nav_complete_menu).setVisibility(View.VISIBLE);

        view = findViewById(R.id.notice_list);
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
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark);

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

        noticeRequest();
    }


    void noticeRequest() {

        UtilityFunctions.tokenRefresh(this);



        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);


        Log.d("","........" +access_token);

        Call<NoticeListResponse> call = api_service.get_notices( (mScrollListener.currentPage+1)+"","Bearer " + access_token);
        findViewById(R.id.filter_selected).setVisibility(View.VISIBLE);

        if(category.equals("All")) {
            String filterid=filterDialog.getFilterId();
            if(filterid.equals("-1")&&(!filterDialog.dateFilterSelected)) {

                unreadImportantNotices = 0;
                unreadImportantNoticesCountUpdate();

                findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.heading)).setText("All Notices");
                call = api_service.get_notices((mScrollListener.currentPage+1) + "","Bearer " + access_token);
            }
            else {

                unreadImportantNotices = 0;

                if (filterid.equals("-1")) {
                    call = api_service.dateFilter(filterDialog.startDate, filterDialog.endDate, (mScrollListener.currentPage+1) + "","Bearer " + access_token);
                } else {
                    if(filterDialog.subFilter.equals("All")) {
                        // send slug here
                        if (!filterDialog.dateFilterSelected) {
                            call = api_service.filteredNoticesAll(filterid, (mScrollListener.currentPage+1) + "","Bearer " + access_token);
                        } else {
                            call = api_service.filterAndDateFilterNoticesAll(filterDialog.startDate, filterDialog.endDate, filterid, (mScrollListener.currentPage+1) + "","Bearer " + access_token);
                        }
                        ((TextView) findViewById(R.id.heading)).setText((String) ("All " + filterDialog.mainFilter + " Notices"));
                    }
                    else {
                        if (!filterDialog.dateFilterSelected) {
                            call = api_service.filteredNotices(filterid, (mScrollListener.currentPage+1) + "","Bearer " + access_token);
                        } else {
                            call = api_service.filterAndDateFilterNotices(filterDialog.startDate, filterDialog.endDate, filterid, (mScrollListener.currentPage+1) + "","Bearer " + access_token);
                        }
                        ((TextView) findViewById(R.id.heading)).setText(((String) filterDialog.subFilter + " Notices"));
                    }

                }
            }
        }
        else {

            unreadImportantNotices = 0;

            if(category.equals("bookmarks"))
            {
                findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                call = api_service.bookmarkedNotices( (mScrollListener.currentPage+1)+"","Bearer " + access_token);
            }
            else {
                if(category.equals("expired"))
                {
                    findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                    call = api_service.expiredNotices( (mScrollListener.currentPage+1)+"","Bearer " + access_token);
                }
                else
                {
                    findViewById(R.id.filter_selected).setVisibility(View.INVISIBLE);
                    call = api_service.importantNotices( (mScrollListener.currentPage+1)+"",true,"Bearer " + access_token);
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
                        if(mlist.size()==0)
                        {
                            if(unreadImportantNotices!=0)
                                mlist.add(new NoticeCardResponse("Important Unread Notices", unreadImportantNotices));
                        }
                        else
                        {
                            if(mlist.get(0).getTitle().equals("Important Unread Notices"))
                            {
                                if(unreadImportantNotices==0)
                                {
                                    mlist.remove(0);
                                }
                                else
                                {
                                    mlist.get(0).unreadCount = unreadImportantNotices;
                                }
                            }
                            else{
                                if(unreadImportantNotices != 0)
                                    mlist.add(0, new NoticeCardResponse("Important Unread Notices", unreadImportantNotices));
                            }
                        }
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
                if(mlist.size()!=0)
                    mlist.remove(mlist.size()-1);
                mScrollListener.nextPage=null;
                adapter.notifyDataSetChanged();
                Toast.makeText(NoticeListScreen.this, "No Internet", Toast.LENGTH_SHORT).show();
                findViewById(R.id.NoNotices).setVisibility(View.INVISIBLE);
                findViewById(R.id.NoInternet).setVisibility(View.VISIBLE);

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
                tmpState=0;
                if(findViewById(R.id.NoNotices).getVisibility()==View.VISIBLE)
                    tmpState=1;
                if(findViewById(R.id.NoInternet).getVisibility()==View.VISIBLE)
                    tmpState=2;
                tmpMlist.addAll(mlist);
                tmpPos=manager.findFirstVisibleItemPosition();
                tmpCurrPage=mScrollListener.currentPage;
                tmpNextPage=mScrollListener.nextPage;
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
                if(tmpState==0)
                {
                    findViewById(R.id.NoNotices).setVisibility(View.INVISIBLE);
                    findViewById(R.id.NoInternet).setVisibility(View.INVISIBLE);
                }
                if(tmpState==1)
                {
                    findViewById(R.id.NoNotices).setVisibility(View.VISIBLE);
                    findViewById(R.id.NoInternet).setVisibility(View.INVISIBLE);
                }

                if(tmpState==2)
                {
                    findViewById(R.id.NoNotices).setVisibility(View.INVISIBLE);
                    findViewById(R.id.NoInternet).setVisibility(View.VISIBLE);
                }

                findViewById(R.id.swipeContainer).startAnimation(backAnimation);

                mlist.clear();
                mlist.addAll(tmpMlist);
                adapter.notifyDataSetChanged();
                Log.d("",tmpPos+" "+mlist.size()+"");
                manager.scrollToPositionWithOffset(tmpPos, 0);
                mScrollListener.currentPage=tmpCurrPage;
                mScrollListener.nextPage=tmpNextPage;
                findViewById(R.id.back).setVisibility(View.INVISIBLE);
                findViewById(R.id.filter_button).setVisibility(View.VISIBLE);
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
                tmpState=0;
                if(findViewById(R.id.NoNotices).getVisibility()==View.VISIBLE)
                    tmpState=1;
                if(findViewById(R.id.NoInternet).getVisibility()==View.VISIBLE)
                    tmpState=2;
                tmpMlist.addAll(mlist);
                tmpPos=manager.findFirstVisibleItemPosition();
                tmpCurrPage=mScrollListener.currentPage;
                tmpNextPage=mScrollListener.nextPage;
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

        findViewById(R.id.nav_important).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category="important";
                String heading="Important Notices";
                tmpMlist.clear();
                tmpState=0;
                if(findViewById(R.id.NoNotices).getVisibility()==View.VISIBLE)
                    tmpState=1;
                if(findViewById(R.id.NoInternet).getVisibility()==View.VISIBLE)
                    tmpState=2;
                tmpMlist.addAll(mlist);
                tmpPos=manager.findFirstVisibleItemPosition();
                tmpCurrPage=mScrollListener.currentPage;
                tmpNextPage=mScrollListener.nextPage;
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
                intent.setData(Uri.parse("market://details?id=com.channeli.img.noticeboard"));
                startActivity(intent);
            }
        });

        findViewById(R.id.nav_notification_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://internet.channeli.in/settings/manage_notifications";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
//                ((DrawerLayout) findViewById(R.id.list_of_notices)).closeDrawer(Gravity.LEFT);
//                Intent intent = new Intent(NoticeListScreen.this,NotificationSettings.class);
//                startActivity(intent);
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


    void unreadImportantNoticesCountUpdate()
    {
        UtilityFunctions.tokenRefresh(this);

        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        if(access_token == null)
        {
            Intent intent = new Intent(NoticeListScreen.this,LoginScreen.class);
            startActivity(intent);
            finish();
        }
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);

        Call<NoticeListResponse> call = api_service.importantUnreadNotices( "1", true, true,"Bearer " + access_token);

        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call,final Response<NoticeListResponse> response) {

               if(response.body()!=null) {
                   unreadImportantNotices = response.body().getCount();
               }
               else
               {
                   unreadImportantNotices = 0;
               }
               if(mlist.size()==0)
               {
                   if(unreadImportantNotices!=0)
                       mlist.add(new NoticeCardResponse("Important Unread Notices", unreadImportantNotices));
               }
               else
               {
                   if(mlist.get(0).getTitle().equals("Important Unread Notices"))
                   {
                       if(unreadImportantNotices==0)
                       {
                           mlist.remove(0);
                       }
                       else
                       {
                           mlist.get(0).unreadCount = unreadImportantNotices;
                       }
                   }
                   else{
                       if(unreadImportantNotices!=0) {
                           mlist.add(0, new NoticeCardResponse("Important Unread Notices", unreadImportantNotices));
                       }
                   }
               }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {

                unreadImportantNotices = 0;

                if(mlist.size()==0)
                {
                    if(unreadImportantNotices!=0)
                        mlist.add(new NoticeCardResponse("Important Unread Notices", unreadImportantNotices));
                }
                else
                {
                    if(mlist.get(0).getTitle().equals("Important Unread Notices"))
                    {
                        if(unreadImportantNotices==0)
                        {
                            mlist.remove(0);
                        }
                        else
                        {
                            mlist.get(0).unreadCount = unreadImportantNotices;
                        }
                    }
                    else{
                        if(unreadImportantNotices!=0) {
                            mlist.add(0, new NoticeCardResponse("Important Unread Notices", unreadImportantNotices));
                        }
                    }
                }

                adapter.notifyDataSetChanged();

            }
        });
    }

    public void applyFilters(View v)
    {
        mlist.clear();
        adapter.notifyDataSetChanged();
        mScrollListener.resetState();
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
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception e)
        {
            Log.d("NoticeListScreen","focus change error "+e.toString());
        }
    }



    private void getFilters()
    {
        String access_token = getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
        retrofit=UtilityFunctions.getRetrofitInstance(getResources().getString(R.string.base_url),retrofit);
        ApiInterface api_service = retrofit.create(ApiInterface.class);

        Call<List<Filters>> call = api_service.getFilters("Bearer " + access_token);
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
            }

            @Override
            public void onFailure(Call<List<Filters>> call, Throwable t) {
                Log.d("NoticeListScreen :","failed to fetch filters");
//                Toast.makeText(NoticeListScreen.this, "Filters not fetched", Toast.LENGTH_SHORT).show();
                filterDialog=new FilterDialog(filters,NoticeListScreen.this);
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        unreadImportantNoticesCountUpdate();
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
            case  "Important Notices":
                findViewById(R.id.back).callOnClick();
                return;
            case "Expired Notices" :
                findViewById(R.id.back).callOnClick();
                return;
            case "Bookmarked Notices" :
                findViewById(R.id.back).callOnClick();
                return;
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
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }



}
