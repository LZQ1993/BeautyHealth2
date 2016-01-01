package com.beautyhealthapp.UserCenter.Activity;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Switch;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2016/1/1.
 */
public class GPSSettingActivity extends NavBarActivity{
    private Switch sw_isupload;
    private Spinner spinner_min;
    private boolean gpsEnabled;
    private Switch sw_gpsEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpssetting);
        initNavBar("GPS设置", true, false);
        fetchUIFromLayout();
        setListener();

    }

    private void fetchUIFromLayout() {
        sw_isupload = (Switch) findViewById(R.id.sw_isupload);
        spinner_min = (Spinner) findViewById(R.id.spinner_min);
        sw_gpsEnabled = (Switch) findViewById(R.id.sw_gpsEnabled);
    }


    private void setListener() {
    }

}
