package com.example.prateek.finalproject;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.prateek.finalproject.Driver;
import com.example.prateek.finalproject.R;

import java.util.List;

public class DriverList extends ArrayAdapter<Driver> {

    private Activity context;
    private List<Driver> driverList;

    public DriverList(Activity context, List<Driver> driverList)
    {
        super(context, R.layout.list_layout,driverList);
        this.context = context;
        this.driverList = driverList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = context.getLayoutInflater();


        View listViewItem  = layoutInflater.inflate(R.layout.list_layout,null,true);
        View listViewDetail = layoutInflater.inflate(R.layout.activity_detailhistory,null,true);

        TextView dri = (TextView) listViewItem.findViewById(R.id.drivernum);

        Driver driver = driverList.get(position);

        dri.setText(driver.getDriverid());

      /*  TextView drimob = (TextView) listViewDetail.findViewById(R.id.textView25);

        drimob.setText(driver.getDriverid()); */

        return listViewItem;
    }
}

