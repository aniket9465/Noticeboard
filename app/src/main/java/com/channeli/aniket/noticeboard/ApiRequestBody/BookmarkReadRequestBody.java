package com.channeli.aniket.noticeboard.ApiRequestBody;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookmarkReadRequestBody {

    @SerializedName("keyword")
    @Expose
    private String keyword;
    @SerializedName("notices")
    @Expose
    private ArrayList<Integer> notices = null;

   public BookmarkReadRequestBody(Integer id, String keyword)
   {
       ArrayList<Integer> l=new ArrayList<>();
       l.add(id);
       setNotices(l);
       setKeyword(keyword);
   }

    public String getKeyword() {
        return keyword;
    }

    private void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getNotices() {
        return notices;
    }

    private void setNotices(ArrayList<Integer> notices) {
        this.notices = notices;
    }

}