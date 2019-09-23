package com.channeli.img.noticeboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.channeli.img.noticeboard.ApiRequestBody.BookmarkReadRequestBody;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeCardResponse;
import com.channeli.img.noticeboard.Utilities.ApiInterface;
import com.channeli.img.noticeboard.Utilities.UtilityFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.notice_view_holder> {

    private ArrayList<NoticeCardResponse> list;
    private Context context;
    private static Retrofit retrofit;

    NoticeListAdapter(ArrayList<NoticeCardResponse> list, Context c) {
        this.list=new ArrayList<>();
        if(list!=null)
        this.list = list;
        this.context=c;

    }

    public void notifyData(ArrayList<NoticeCardResponse> myList) {
        list=myList;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public NoticeListAdapter.notice_view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0)
        {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loader, parent, false);
            return new NoticeListAdapter.notice_view_holder(v);
        }
        if(viewType==2)
        {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.important_notices_card, parent, false);
            return new NoticeListAdapter.notice_view_holder(v);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_card, parent, false);
        return new NoticeListAdapter.notice_view_holder(v);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onBindViewHolder(@NonNull final NoticeListAdapter.notice_view_holder holder, final int position) {
        if(list.get(position)==null)
            return;
        Log.d("tag","........................"+list.get(position).getTitle()+".........."+getItemViewType(position));
        if(getItemViewType(position)==2)
            return;
        View.OnClickListener open_notice = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(position).getBookmark() != null) {
                    Intent i = new Intent(context, NoticeViewScreen.class);
                    i.putExtra("id", list.get(position).getId());
                    i.putExtra("Expired",false);
                    i.putExtra("bookmarked", list.get(position).getBookmark());
                    list.get(position).setRead(true);
                    i.putExtra("position", position);
                    ((Activity) context).startActivityForResult(i, 0);
                    ApiInterface api_service;
                    retrofit = UtilityFunctions.getRetrofitInstance(context.getResources().getString(R.string.base_url), retrofit);
                    api_service = retrofit.create(ApiInterface.class);
                    String access_token = context.getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
                    Call<Void> call = api_service.bookmark_read("Bearer " + access_token, new BookmarkReadRequestBody(list.get(position).getId(), "read"));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("", response.code() + "");
                            if (response.code() == 201 || response.code() == 200)
                                holder.card.setBackgroundColor(Color.parseColor("#4dc4c4c4"));
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
                else
                {
                    Intent i = new Intent(context, NoticeViewScreen.class);
                    i.putExtra("id", list.get(position).getId());
                    i.putExtra("position", position);
                    i.putExtra("bookmarked", false);
                    i.putExtra("Expired",true);
                    ((Activity) context).startActivityForResult(i, 1);
                }
            }
        };

        holder.open.setOnClickListener(open_notice);
        holder.subject.setOnClickListener(open_notice);
        holder.banner.setOnClickListener(open_notice);
        holder.date.setOnClickListener(open_notice);

        if(list.get(position).getBookmark()==null)
        {
            holder.bookmark.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.bookmark.setVisibility(View.VISIBLE);
        }


        if(list.get(position).getBookmark()!=null)
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // make call for bookmarking notice
                ApiInterface api_service;
                retrofit=UtilityFunctions.getRetrofitInstance(context.getResources().getString(R.string.base_url),retrofit);
                api_service = retrofit.create(ApiInterface.class);
                String access_token = context.getSharedPreferences("Noticeboard_data", 0).getString("access_token", null);
                Log.d("..",access_token);
                Call<Void> call = api_service.bookmark_read("Bearer "+access_token,new BookmarkReadRequestBody(list.get(position).getId(),"star"));
                if(list.get(position).getBookmark())
                    call = api_service.bookmark_read("Bearer "+access_token,new BookmarkReadRequestBody(list.get(position).getId(),"unstar"));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(".......",list.get(position).getId().toString()+" ");
                        if(response.code()==201||response.code()==200) {
                            if (list.get(position).getBookmark()) {
                                ((ImageView) v).setImageResource(R.drawable.bookmark);
                                list.get(position).bookmark = !list.get(position).getBookmark();
                                Toast.makeText(context, "notice unmarked", Toast.LENGTH_SHORT).show();
                            } else {
                                ((ImageView) v).setImageResource(R.drawable.bookmarked);
                                list.get(position).bookmark = !list.get(position).getBookmark();
                                Toast.makeText(context, "notice bookmarked", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });

            }
        });

        Log.d("",position+""+list.get(position).getBookmark()+list.get(position).getIsDraft()+list.get(position).getRead());
        if(list.get(position).getBookmark()!=null)
        if(list.get(position).getBookmark())
        {
            holder.bookmark.setImageResource(R.drawable.bookmarked);
        }
        else
        {
            holder.bookmark.setImageResource(R.drawable.bookmark);
        }


        if(list.get(position).getBookmark()!=null) {
            if (list.get(position).getRead()) {
                holder.card.setBackgroundColor(Color.parseColor("#4dc4c4c4"));
            } else {
                holder.card.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
        else
        {
            holder.card.setBackgroundColor(Color.parseColor("#4dc4c4c4"));
        }


        try {
            String date = list.get(position).getDatetimeModified();
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("dd MMM yyyy");
            date = spf.format(newDate);
            holder.date.setText(date);
        }
        catch (Exception e)
        {
            holder.date.setText(list.get(position).getDatetimeModified());
        }

        holder.subject.setText(list.get(position).getBanner().getName());

        holder.subject.setText(list.get(position).getTitle());

    }



    @Override
    public int getItemViewType(int position) {
        if(list.get(position)==null)
            return 0;
        if(list.get(position).getTitle().equals("Important Unread Notices"))
            return 2;
        return 1;
    }

    class notice_view_holder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView banner;
        TextView date;
        LinearLayout open;
        ImageView bookmark;
        View card;

        notice_view_holder(View parent) {
            super(parent);
            this.card=parent;
            this.open=parent.findViewById(R.id.open);

            this.subject = parent.findViewById(R.id.subject);
            this.bookmark = parent.findViewById(R.id.bookmark);
            this.banner = parent.findViewById(R.id.banner);
            this.date = parent.findViewById(R.id.date);
        }
    }

}