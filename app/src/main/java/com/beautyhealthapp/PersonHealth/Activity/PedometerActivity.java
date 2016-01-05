package com.beautyhealthapp.PersonHealth.Activity;

import android.os.Bundle;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;

/**
 * Created by lenovo on 2016/1/4.
 */
public class PedometerActivity extends DataRequestActivity {
    private String currentNotiName = "SugarMeasureNotifications";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_upload);
        initNavBar("计步器",true,true);
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {

    }
}
