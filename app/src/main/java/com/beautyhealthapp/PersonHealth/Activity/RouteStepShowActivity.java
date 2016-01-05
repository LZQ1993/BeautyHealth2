package com.beautyhealthapp.PersonHealth.Activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.LocationEntity.BusRouteStepInfo;
import com.LocationEntity.RounteStepInfo;
import com.beautyhealthapp.PersonHealth.Assistant.RouteStepListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/1/5.
 */
public class RouteStepShowActivity extends NavBarActivity{
    private ListView listView;
    private TextView startpoint,stoppoint;
    private ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routestepshow);
        initNavBar("经过道路", true, false);
        list=new ArrayList<String>();
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        listView=(ListView) findViewById(R.id.lv_routestep);
        startpoint=(TextView) findViewById(R.id.startpoint);
        stoppoint=(TextView) findViewById(R.id.stoppoint);
        String routeType=getIntent().getBundleExtra("routeType").getString("routeType") ;
        startpoint.setText(getIntent().getStringExtra("start"));
        stoppoint.setText(getIntent().getStringExtra("end"));
        ArrayList<RounteStepInfo> driveSteps=(ArrayList<RounteStepInfo>)getIntent().getBundleExtra("routeStep").getSerializable("routeStep");
        //将下面的数据再整理后添加到LIST列表中即可
        if(routeType.equals("1")){
            for(int i=0;i<driveSteps.size();i++){
                BusRouteStepInfo aRSI=(BusRouteStepInfo)driveSteps.get(i);
                list.add("公交车名:"+aRSI.BustLineName);
            }
        }else if(routeType.equals("2")){
            for(int i=0;i<driveSteps.size();i++){
                RounteStepInfo aRSI=driveSteps.get(i);
                list.add("道路名称："+aRSI.StepName +"\n"+"距离（米）："+aRSI.Distance);
            }
        }else if(routeType.equals("3")){
            for(int i=0;i<driveSteps.size();i++){
                RounteStepInfo aRSI=driveSteps.get(i);
                list.add("道路名称："+aRSI.StepName +"\n"+"距离（米）："+aRSI.Distance);
            }
        }
        listView.setAdapter(new RouteStepListAdapter(this, list));
    }
}
