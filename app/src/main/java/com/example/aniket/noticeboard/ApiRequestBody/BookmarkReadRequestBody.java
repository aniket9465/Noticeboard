package com.example.aniket.noticeboard.ApiRequestBody;

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
    private ArrayList<String> notices = null;

   public BookmarkReadRequestBody(String id, String keyword)
   {
       ArrayList<String> l=new ArrayList<>();
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

    public List<String> getNotices() {
        return notices;
    }

    private void setNotices(ArrayList<String> notices) {
        this.notices = notices;
    }

}