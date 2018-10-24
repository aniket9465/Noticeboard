package com.example.aniket.noticeboard.Utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.NoticeListScreen;
import com.example.aniket.noticeboard.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FilterDialog implements DatePickerDialog.OnDateSetListener{

    boolean filterSelected=false;
    boolean dateFilterSelected=false;
    Date startDate;
    Date endDate;
    String mainFilter;
    ArrayList<Filters> filters;
    String subFilter;
    String filterId;
    Activity activity;


    public FilterDialog(ArrayList<Filters> filters,Activity activity)
    {
         this.filters=filters;
         this.activity=activity;
         setUpOnClicks();
    }

    void setUpOnClicks()
    {
        activity.findViewById(R.id.date_filter_not_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        FilterDialog.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(activity.getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Toast.makeText(activity, ""+year+"-"+monthOfYear+"-"+dayOfMonth+"...... "+yearEnd+"- "+monthOfYearEnd+"- "+dayOfMonthEnd, Toast.LENGTH_SHORT).show();
    }
}
