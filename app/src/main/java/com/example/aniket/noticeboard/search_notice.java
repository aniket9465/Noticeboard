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
    String searched="";
    private boolean isLoading=false;
    ProgressDialog progressDialog;
    String base_url;
    SwipeRefreshLayout swipeContainer;
    private search_notice.notices_list_adapter adapter;
    RecyclerView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_notice);
        base_url="http://localhost:8000/get_notices/search_notices/2/";
        mlist=new ArrayList<>();
        api_service = functions.getRetrofitInstance(base_url,retrofit).create(api_interface.class);
        view = findViewById(R.id.notice_list);
        view.requestFocus();
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
        final LinearLayoutManager manager=new LinearLayoutManager(this.getApplicationContext());
        Log.d("tag",view+"");
        view.setLayoutManager(manager);
        adapter=new search_notice.notices_list_adapter(mlist);
        view.setAdapter(adapter);
        notice_search(searched);
        EndlessRecyclerViewScrollListener mScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                if(totalItemsCount>0 && totalItemsCount<=mlist.size() ){
                    Log.d("","onloadmore");
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
        Log.d("",searchView+"");
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                Log.d(" ",newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Log.d("",query);
                notice_search(query);
                searched=query;
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    public void focus_change(View v)
    {
        Log.d("","focus change"+view+view.requestFocus());
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void notice_search(String search_query)
    {
        if(!search_query.equals(searched));
        mlist.clear();
        if(swipeContainer!=null)
            if(!swipeContainer.isRefreshing())
                progressDialog= ProgressDialog.show(search_notice.this,"Loading","please wait",true);
        adapter.notifyData(mlist);
        isLoading=false;
        // confirm the url pattern
        Log.d("","notice_request");
        Call<notice_list> call=api_service.get_notices(base_url,getSharedPreferences("Noticeboard_data",0).getString("access token",null));
        call.enqueue(new Callback<notice_list>() {
            @Override
            public void onResponse(Call<notice_list> call, Response<notice_list> response) {
                if(response.body()!=null)
                for(int i=0;i<response.body().getNotices().size();++i)
                {
                    mlist.add(response.body().getNotices().get(i));
                }
                progressDialog.dismiss();
                if(swipeContainer!=null) {
                    swipeContainer.setRefreshing(false);
                    Log.d("......",swipeContainer.isRefreshing()+"");
                }
                Log.d("f","onrespmpse"+mlist.size()+swipeContainer);
                adapter.notifyData(mlist);
            }

            @Override
            public void onFailure(Call<notice_list> call, Throwable t) {
                if(swipeContainer!=null)
                    swipeContainer.setRefreshing(false);
                Toast.makeText(search_notice.this, "connection error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
        Log.d("","fad");
        adapter.notifyData(mlist);
    }

    public class notices_list_adapter extends RecyclerView.Adapter<search_notice.notices_list_adapter.notice_view_holder>
    {

        private ArrayList<notice_card> list;

        class notice_view_holder extends RecyclerView.ViewHolder
        {
            TextView subject;
            TextView banner;
            TextView date;
            ImageView star;
            notice_view_holder(View parent)
            {
                super(parent);
                this.subject=parent.findViewById(R.id.subject);
                this.star=parent.findViewById(R.id.star);
                this.banner=parent.findViewById(R.id.banner);
                this.date=parent.findViewById(R.id.date);
            }
        }

        notices_list_adapter(ArrayList<notice_card> list)
        {
            this.list=list;
        }

        public void notifyData(ArrayList<notice_card> myList) {
            this.list = myList;
            notifyDataSetChanged();
        }

        @Override
        public search_notice.notices_list_adapter.notice_view_holder onCreateViewHolder(ViewGroup parent,
                                                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notice_card, parent, false);
            search_notice.notices_list_adapter.notice_view_holder vh = new search_notice.notices_list_adapter.notice_view_holder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(search_notice.notices_list_adapter.notice_view_holder holder, int position) {
            holder.subject.setText(list.get(position).getBanner());
            holder.subject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send information of notice to next activity
                    Intent i=new Intent( search_notice.this,notice_view.class);
                    startActivity(i);
                }
            });
            holder.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // make call for staring notice
                    Toast.makeText(search_notice.this, "notice starred", Toast.LENGTH_SHORT).show();
                }
            });
            holder.date.setText(list.get(position).getDatertimeModified());
            holder.subject.setText(list.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

}