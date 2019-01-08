package com.channeli.aniket.noticeboard.Utilities;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.channeli.aniket.noticeboard.ApiResponseClasses.Banner;
import com.channeli.aniket.noticeboard.ApiResponseClasses.Filters;
import com.channeli.aniket.noticeboard.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FilterDialog implements DatePickerDialog.OnDateSetListener{

    boolean filterSelected=false;
    public boolean dateFilterSelected=false;
    public String startDate="";
    public String endDate="";
    public String mainFilter="";
    private String filterId="";
    public String subFilter="";

    private RadioButton checked;
    private RadioButton pchecked;

    private ArrayList<Filters> filters;
    private Activity activity;

    private String currmainFilter="";

    boolean tmpfilterSelected=false;
    private boolean tmpdateFilterSelected=false;
    private String tmpstartDate="";
    private String tmpendDate="";
    private String tmpmainFilter="";
    String tmpfilterId="";
    private String tmpsubFilter="";

    private MyListAdapter subfilterAdapter;
    private ArrayList<Banner> subfilterListItems;

    public FilterDialog(ArrayList<Filters> filters,Activity activity)
    {
         this.filters=filters;
         this.activity=activity;
         currmainFilter="Authorities";
         tmpmainFilter="";
         tmpsubFilter="";
         setUpOnClicks();
        subfilterListItems = new ArrayList<>();
        subfilterAdapter = new MyListAdapter(activity, subfilterListItems);
        ListView subfilterList = activity.findViewById(R.id.subfilters);
        subfilterList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        subfilterList.setAdapter(subfilterAdapter);
        subfilterAdapter.notifyDataSetChanged();
        subfilterListItems.clear();
        for(int i=0;i<filters.size();++i)
        {
            if(filters.get(i).getName().equals("Authorities")) {

                for (int j = 0; j < filters.get(i).getBanner().size(); ++j) {
                    subfilterListItems.add(filters.get(i).getBanner().get(j));
                }
            }
        }
        subfilterAdapter.notifyDataSetChanged();
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
                dpd.setAccentColor(Color.parseColor("#5288DA"));
                dpd.show(activity.getFragmentManager(), "Datepickerdialog");
            }
        });

        activity.findViewById(R.id.reset_date_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpdateFilterSelected=false;
                activity.findViewById(R.id.date_filter_not_set).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.date_filter_set).setVisibility(View.INVISIBLE);
            }
        });

        activity.findViewById(R.id.authority).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currmainFilter.equals("Authorities"))
                {
                    ((RadioButton)activity.findViewById(R.id.all_subcategories_bullet)).setChecked(false);
                }
                if(tmpmainFilter.equals("Authorities"))
                {
                    if(tmpsubFilter.equals("All"))
                    {
                        ((RadioButton)activity.findViewById(R.id.all_subcategories_bullet)).setChecked(true);
                    }

                }
                currmainFilter="Authorities";
                activity.findViewById(R.id.authority).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.departments).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.placements).setBackgroundColor(Color.parseColor("#EDF4FF"));
                v.setBackgroundColor(Color.parseColor("#ffffff"));
                subfilterListItems.clear();
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Authorities")) {

                        for (int j = 0; j < filters.get(i).getBanner().size(); ++j) {
                            subfilterListItems.add(filters.get(i).getBanner().get(j));
                        }
                    }
                }
                subfilterAdapter.notifyDataSetChanged();
            }
        });

        activity.findViewById(R.id.departments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currmainFilter.equals("Departments"))
                {
                    ((RadioButton)activity.findViewById(R.id.all_subcategories_bullet)).setChecked(false);
                }
                if(tmpmainFilter.equals("Departments"))
                {
                    if(tmpsubFilter.equals("All"))
                    {
                        ((RadioButton)activity.findViewById(R.id.all_subcategories_bullet)).setChecked(true);
                    }

                }
                currmainFilter="Departments";
                activity.findViewById(R.id.authority).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.departments).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.placements).setBackgroundColor(Color.parseColor("#EDF4FF"));
                v.setBackgroundColor(Color.parseColor("#ffffff"));
                subfilterListItems.clear();
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Departments")) {

                        for (int j = 0; j < filters.get(i).getBanner().size(); ++j) {
                            subfilterListItems.add(filters.get(i).getBanner().get(j));
                        }
                    }
                }
                subfilterAdapter.notifyDataSetChanged();
            }
        });

        activity.findViewById(R.id.placements).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currmainFilter.equals("Placements"))
                {
                    ((RadioButton)activity.findViewById(R.id.all_subcategories_bullet)).setChecked(false);
                }
                if(tmpmainFilter.equals("Placements"))
                {
                    if(tmpsubFilter.equals("All"))
                    {
                        ((RadioButton)activity.findViewById(R.id.all_subcategories_bullet)).setChecked(true);
                    }

                }
                currmainFilter="Placements";
                activity.findViewById(R.id.authority).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.departments).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.placements).setBackgroundColor(Color.parseColor("#EDF4FF"));
                v.setBackgroundColor(Color.parseColor("#ffffff"));
                subfilterListItems.clear();
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Placements")) {

                        for (int j = 0; j < filters.get(i).getBanner().size(); ++j) {
                            subfilterListItems.add(filters.get(i).getBanner().get(j));
                        }
                    }
                }
                subfilterAdapter.notifyDataSetChanged();
            }
        });

        activity.findViewById(R.id.all_subcategories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pchecked!=null)
                {
                    pchecked.setChecked(false);
                }
                activity.findViewById(R.id.authority_bullet).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.placements_bullet).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.departments_bullet).setVisibility(View.INVISIBLE);
                tmpsubFilter="All";
                tmpmainFilter=currmainFilter;
                if(tmpmainFilter.equals("Authorities"))
                {
                    activity.findViewById(R.id.authority_bullet).setVisibility(View.VISIBLE);
                }
                if(tmpmainFilter.equals("Placements"))
                {
                    activity.findViewById(R.id.placements_bullet).setVisibility(View.VISIBLE);
                }
                if(tmpmainFilter.equals("Departments"))
                {
                    activity.findViewById(R.id.departments_bullet).setVisibility(View.VISIBLE);
                }
                ((RadioButton)v.findViewById(R.id.all_subcategories_bullet)).setChecked(true);
                pchecked=((RadioButton)v.findViewById(R.id.all_subcategories_bullet));

            }
        });
        activity.findViewById(R.id.filter_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpmainFilter="";
                tmpsubFilter="";
                if(pchecked!=null)
                pchecked.setChecked(false);
                pchecked=null;
                activity.findViewById(R.id.authority_bullet).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.placements_bullet).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.departments_bullet).setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        tmpstartDate=""+year+"-"+monthOfYear+"-"+dayOfMonth;
        tmpendDate=yearEnd+"-"+monthOfYearEnd+"-"+dayOfMonthEnd;
        tmpdateFilterSelected=true;
        activity.findViewById(R.id.date_filter_not_set).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.date_filter_set).setVisibility(View.VISIBLE);
        ((TextView)activity.findViewById(R.id.starting_date)).setText(tmpstartDate);
        ((TextView)activity.findViewById(R.id.ending_date)).setText(tmpendDate);
    }

    public void setOriginal() {

        if(dateFilterSelected)
        {
            activity.findViewById(R.id.date_filter_not_set).setVisibility(View.INVISIBLE);
            activity.findViewById(R.id.date_filter_set).setVisibility(View.VISIBLE);
        }
        else {
            activity.findViewById(R.id.date_filter_not_set).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.date_filter_set).setVisibility(View.INVISIBLE);
            ((TextView) activity.findViewById(R.id.starting_date)).setText(startDate);
            ((TextView) activity.findViewById(R.id.ending_date)).setText(endDate);
        }

            tmpdateFilterSelected=dateFilterSelected;
            tmpstartDate=startDate;
            tmpendDate=endDate;
            tmpsubFilter=subFilter;
            tmpmainFilter=mainFilter;

            if(pchecked!=checked)
            {
                if(pchecked!=null)
                    pchecked.setChecked(false);
                if(checked!=null)
                checked.setChecked(true);
            }
            if(mainFilter.equals("Authorities"))
            {
                activity.findViewById(R.id.authority).callOnClick();
            }
            if(mainFilter.equals("Departments"))
            {
                activity.findViewById(R.id.departments).callOnClick();
            }
            if(mainFilter.equals("Placements"))
            {
                activity.findViewById(R.id.placements).callOnClick();
            }
            activity.findViewById(R.id.authority_bullet).setVisibility(View.INVISIBLE);
            activity.findViewById(R.id.placements_bullet).setVisibility(View.INVISIBLE);
            activity.findViewById(R.id.departments_bullet).setVisibility(View.INVISIBLE);
            if(mainFilter.equals("Authorities"))
            {
                activity.findViewById(R.id.authority_bullet).setVisibility(View.VISIBLE);
            }
            if(mainFilter.equals("Placements"))
            {
                activity.findViewById(R.id.placements_bullet).setVisibility(View.VISIBLE);
            }
            if(mainFilter.equals("Departments"))
            {
                activity.findViewById(R.id.departments_bullet).setVisibility(View.VISIBLE);
            }

    }

    public void setNew()
    {
        startDate=tmpstartDate;
        endDate=tmpendDate;
        dateFilterSelected=tmpdateFilterSelected;
        mainFilter=tmpmainFilter;
        currmainFilter=mainFilter;
        subFilter=tmpsubFilter;
    }

    public class MyListAdapter extends ArrayAdapter<Banner> {

        private Activity context;
        private ArrayList<Banner> list;

        private MyListAdapter(Activity context, ArrayList<Banner> subfilters) {
            super(context, 0, subfilters);
            this.context = context;
            this.list=new ArrayList<>();
            list=subfilters;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.subcategory_view, null, true);

            final TextView titleText = (TextView) rowView.findViewById(R.id.subcategory_text);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pchecked!=null)
                    {
                        pchecked.setChecked(false);
                    }
                    ((RadioButton)v.findViewById(R.id.subcategory_bullet)).setChecked(true);
                    pchecked=((RadioButton)v.findViewById(R.id.subcategory_bullet));

                    activity.findViewById(R.id.authority_bullet).setVisibility(View.INVISIBLE);
                    activity.findViewById(R.id.placements_bullet).setVisibility(View.INVISIBLE);
                    activity.findViewById(R.id.departments_bullet).setVisibility(View.INVISIBLE);
                    if(currmainFilter.equals("Authorities"))
                    {
                        activity.findViewById(R.id.authority_bullet).setVisibility(View.VISIBLE);
                    }
                    if(currmainFilter.equals("Placements"))
                    {
                        activity.findViewById(R.id.placements_bullet).setVisibility(View.VISIBLE);
                    }
                    if(currmainFilter.equals("Departments"))
                    {
                        activity.findViewById(R.id.departments_bullet).setVisibility(View.VISIBLE);
                    }
                    tmpmainFilter=currmainFilter;
                    tmpsubFilter=list.get(position).getName();
                }
            });
            if(tmpsubFilter.equals(list.get(position).getName()))
            {
                ((RadioButton)rowView.findViewById(R.id.subcategory_bullet)).setChecked(true);
                pchecked=((RadioButton)rowView.findViewById(R.id.subcategory_bullet));
            }
            titleText.setText(list.get(position).getName());
            return rowView;

        }
    }

    public String getFilterId()
    {
        String filterID="-1";
        if(mainFilter.equals("")) {
            return "-1";
        }
        if(mainFilter.equals("Placements"))
        {
            if(subFilter.equals("All"))
            {
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Placements"))
                    {
                        filterID=filters.get(i).getId();
                        break;
                    }
                }
            }
            else
            {
                for(int i=0;i<filters.size();++i)
                {
                    for(int j=0;j<filters.get(i).getBanner().size();++j)
                    {
                        if(filters.get(i).getBanner().get(j).getName().equals(subFilter))
                        {
                            filterID=filters.get(i).getBanner().get(j).getId();
                        }
                    }
                }
            }
        }
        if(mainFilter.equals("Authorities"))
        {
            if(subFilter.equals("All"))
            {
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Authorities"))
                    {
                        filterID=filters.get(i).getId();
                        break;
                    }
                }
            }
            else
            {
                for(int i=0;i<filters.size();++i)
                {
                    for(int j=0;j<filters.get(i).getBanner().size();++j)
                    {
                        if(filters.get(i).getBanner().get(j).getName().equals(subFilter))
                        {
                            filterID=filters.get(i).getBanner().get(j).getId();
                        }
                    }
                }
            }
        }
        if(mainFilter.equals("Departments"))
        {
            if(subFilter.equals("All"))
            {
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Departments"))
                    {
                        filterID=filters.get(i).getId();
                        break;
                    }
                }
            }
            else
            {
                for(int i=0;i<filters.size();++i)
                {
                    for(int j=0;j<filters.get(i).getBanner().size();++j)
                    {
                        if(filters.get(i).getBanner().get(j).getName().equals(subFilter))
                        {
                            filterID=filters.get(i).getBanner().get(j).getId();
                        }
                    }
                }
            }
        }
        return filterID;

    }

    public void resetFilters()
    {
        tmpdateFilterSelected=false;
        activity.findViewById(R.id.date_filter_not_set).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.date_filter_set).setVisibility(View.INVISIBLE);

        tmpmainFilter="";
        tmpsubFilter="";
        if(pchecked!=null)
            pchecked.setChecked(false);
        pchecked=null;
        activity.findViewById(R.id.authority_bullet).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.placements_bullet).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.departments_bullet).setVisibility(View.INVISIBLE);

    }
}
