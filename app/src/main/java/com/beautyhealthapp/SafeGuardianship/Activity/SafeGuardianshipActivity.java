package com.beautyhealthapp.SafeGuardianship.Activity;

import android.os.Bundle;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2015/12/25.
 */
public class SafeGuardianshipActivity extends NavBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safeguardianship);
        initNavBar("安全监护",true,false);
    }
}
