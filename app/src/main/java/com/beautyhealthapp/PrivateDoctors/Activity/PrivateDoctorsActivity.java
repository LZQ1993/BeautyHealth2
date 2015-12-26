package com.beautyhealthapp.PrivateDoctors.Activity;

import android.os.Bundle;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2015/12/25.
 */
public class PrivateDoctorsActivity extends NavBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personhealth);
        initNavBar("医疗咨询", true,false);
    }
}
