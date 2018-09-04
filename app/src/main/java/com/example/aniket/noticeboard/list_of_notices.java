package com.example.aniket.noticeboard;

import android.accessibilityservice.AccessibilityService;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class list_of_notices extends AppCompatActivity {

    ArrayList<notice_card> mlist=new ArrayList<>();
    private boolean isLoading=false;
    private notices_list_adapter adapter;
    ProgressDialog progressDialog;
    RecyclerView view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notices);
        view=(RecyclerView) findViewById(R.id.notice_list);
        view.requestFocus();
        final LinearLayoutManager manager=new LinearLayoutManager(this.getApplicationContext());
        Log.d("tag",view+"");
        view.setLayoutManager(manager);
        adapter=new notices_list_adapter(mlist);
        view.setAdapter(adapter);
        notice_request();
        view.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = manager.getItemCount();
                int lastVisibleItem = manager.findLastVisibleItemPosition()+1;
                Log.d("tag",totalItemCount+"  "+lastVisibleItem);
                if (!isLoading && totalItemCount <= (lastVisibleItem)) {
                    isLoading=true;
                    notice_request();
                    }
                }

        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_bar);
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                Log.d(" ",newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Log.d("",query);
                notice_search();
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


    void notice_request()
    {
        //progressDialog=ProgressDialog.show(list_of_notices.this,"Loading","please wait",true);
        for(int i=0;i<10;++i)
        mlist.add(new notice_card());
        adapter.notifyData(mlist);
        isLoading=false;
        //make api call change list accordingly and on response progressDialog.dismiss()
    }

    void notice_search()
    {
        view.requestFocus();
        //progressDialog=ProgressDialog.show(list_of_notices.this,"Loading","please wait",true);
        mlist.clear();
        for(int i=0;i<10;++i)
            mlist.add(new notice_card());
        adapter.notifyData(mlist);
        isLoading=false;
        //make api call change list accordingly and on response progressDialog.dismiss()
    }

    public class notices_list_adapter extends RecyclerView.Adapter<notices_list_adapter.notice_view_holder>
    {

        private ArrayList<notice_card> list;

        class notice_view_holder extends RecyclerView.ViewHolder
        {
            TextView subject;
            Button star;
            notice_view_holder(View parent)
            {
                super(parent);
                this.subject=parent.findViewById(R.id.subject);
                this.star=parent.findViewById(R.id.star);
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
        public notices_list_adapter.notice_view_holder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notice_card, parent, false);
            notice_view_holder vh = new notice_view_holder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(notice_view_holder holder, int position) {
           holder.subject.setText(list.get(position).subject+"notice "+(position+1));
           holder.subject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send information of notice to next activity
                    Intent i=new Intent(list_of_notices.this,notice_view.class);
                    startActivity(i);
                }
            });
           holder.star.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // make call for staring notice
                   Toast.makeText(list_of_notices.this, "notice starred", Toast.LENGTH_SHORT).show();
               }
           });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }
}
