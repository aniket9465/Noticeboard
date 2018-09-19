package com.example.aniket.noticeboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class notices_list_adapter extends RecyclerView.Adapter<notices_list_adapter.notice_view_holder> {

    private ArrayList<notice_card> list;
    private Context context;
    private Retrofit retrofit_bookmark;
    private Retrofit retrofit_read;
    static api_interface api_service;
    String base_url_bookmark="http://127.0.0.1:8000/bookmark_notice/";
    String base_url_read="http://127.0.0.1:8000/read_notice/";
    notices_list_adapter(ArrayList<notice_card> list,Context c) {
        this.list = list;
        this.context=c;
        retrofit_bookmark=functions.getRetrofitInstance(base_url_bookmark,this.retrofit_bookmark);
        retrofit_read=functions.getRetrofitInstance(base_url_read,this.retrofit_read);
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
        notices_list_adapter.notice_view_holder vh = new notices_list_adapter.notice_view_holder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(notices_list_adapter.notice_view_holder holder, final int position) {
        holder.subject.setText(list.get(position).getBanner());
        holder.subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send information of notice to next activity
                //make call for read
                Intent i = new Intent(context , notice_view.class);
                ((Activity)context).startActivityForResult(i,0);
            }
        });
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // make call for bookmarking notice
                api_service = functions.getRetrofitInstance(base_url_bookmark, retrofit_bookmark).create(api_interface.class);
                String access_token = context.getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
                Call<Void> call = api_service.bookmark(base_url_bookmark , access_token,"{ notice_id : "+list.get(position).getId()+" }");
                //Log.d("......", list.get(position).getId()+"////");
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("",response+"");
                        if(response.code()==200)
                        if(list.get(position).getBookmark())
                        {
                            ((ImageView)v).setImageResource(R.drawable.bookmark);
                            list.get(position).bookmark=!list.get(position).getBookmark();
                            Toast.makeText(context, "notice unmarked", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            ((ImageView)v).setImageResource(R.drawable.bookmarked);
                            list.get(position).bookmark=!list.get(position).getBookmark();
                            Toast.makeText(context, "notice bookmarked", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "network issue", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        if(list.get(position).getBookmark())
        {
            holder.bookmark.setImageResource(R.drawable.bookmarked);
        }
        holder.date.setText(list.get(position).getDatertimeModified());
        holder.subject.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class notice_view_holder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView banner;
        TextView date;
        ImageView bookmark;

        notice_view_holder(View parent) {
            super(parent);
            this.subject = parent.findViewById(R.id.subject);
            this.bookmark = parent.findViewById(R.id.bookmark);
            this.banner = parent.findViewById(R.id.banner);
            this.date = parent.findViewById(R.id.date);
        }
    }

}