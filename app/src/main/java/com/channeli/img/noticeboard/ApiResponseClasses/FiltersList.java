package com.channeli.img.noticeboard.ApiResponseClasses;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FiltersList {

    @SerializedName("results")
    @Expose
    private ArrayList<Filters> result = null;

    public ArrayList<Filters> getResult() {
        if(result==null)
        {
            result=new ArrayList<>();
        }
        return result;
    }

    public void setResult(ArrayList<Filters> result) {
        this.result = result;
    }

}
