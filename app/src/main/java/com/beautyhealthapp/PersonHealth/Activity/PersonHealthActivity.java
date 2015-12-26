package com.beautyhealthapp.PersonHealth.Activity;

import android.os.Bundle;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2015/12/25.
 */
public class PersonHealthActivity extends NavBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personhealth);
        initNavBar("个人健康",true,false);
    }

}
