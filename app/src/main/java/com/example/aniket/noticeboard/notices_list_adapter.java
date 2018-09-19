package com.example.aniket.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class notices_list_adapter extends RecyclerView.Adapter<notices_list_adapter.notice_view_holder> {

    private ArrayList<notice_card> list;
    private Context context;
    notices_list_adapter(ArrayList<notice_card> list,Context c) {
        this.list = list;
        this.context=c;
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
                Intent i = new Intent(context, notice_view.class);
                context.startActivity(i);
            }
        });
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make call for bookmarking notice
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