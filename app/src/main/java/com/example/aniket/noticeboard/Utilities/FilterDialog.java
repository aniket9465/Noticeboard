package com.example.aniket.noticeboard.Utilities;

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

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.aniket.noticeboard.ApiResponseClasses.Banner;
import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FilterDialog implements DatePickerDialog.OnDateSetListener{

    boolean filterSelected=false;
    boolean dateFilterSelected=false;
    String startDate;
    String endDate;
    String mainFilter;
    String filterId;
    String subFilter;

    RadioButton pchecked;

    ArrayList<Filters> filters;
    Activity activity;

    boolean tmpfilterSelected=false;
    boolean tmpdateFilterSelected=false;
    String tmpstartDate;
    String tmpendDate;
    String tmpmainFilter;
    String tmpfilterId;
    String tmpsubFilter;

    private MyListAdapter subfilterAdapter;
    private ArrayList<Banner> subfilterListItems;

    public FilterDialog(ArrayList<Filters> filters,Activity activity)
    {
         this.filters=filters;
         this.activity=activity;
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
                activity.findViewById(R.id.authority).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.departments).setBackgroundColor(Color.parseColor("#EDF4FF"));
                activity.findViewById(R.id.placements).setBackgroundColor(Color.parseColor("#EDF4FF"));
                v.setBackgroundColor(Color.parseColor("#ffffff"));
                subfilterListItems.clear();
                for(int i=0;i<filters.size();++i)
                {
                    if(filters.get(i).getName().equals("Placement")) {

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
                ((RadioButton)v.findViewById(R.id.all_subcategories_bullet)).setChecked(true);
                pchecked=((RadioButton)v.findViewById(R.id.all_subcategories_bullet));

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
        else
        {
            activity.findViewById(R.id.date_filter_not_set).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.date_filter_set).setVisibility(View.INVISIBLE);
            ((TextView)activity.findViewById(R.id.starting_date)).setText(startDate);
            ((TextView)activity.findViewById(R.id.ending_date)).setText(endDate);
        }
    }

    public void setNew()
    {
        startDate=tmpstartDate;
        endDate=tmpendDate;
        dateFilterSelected=tmpdateFilterSelected;
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
                    Log.d("////////////////",""+v);
                    if(pchecked!=null)
                    {
                        pchecked.setChecked(false);
                    }
                    ((RadioButton)v.findViewById(R.id.subcategory_bullet)).setChecked(true);
                    pchecked=((RadioButton)v.findViewById(R.id.subcategory_bullet));
                }
            });
            titleText.setText(list.get(position).getName());
            return rowView;

        }
    }

}
