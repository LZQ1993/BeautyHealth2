package com.beautyhealthapp.PersonHealth.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.beautyhealthapp.PersonHealth.Assistant.AlarmListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
/**
 * Created by lenovo on 2016/1/4.
 */
public class ActiveAbleActivity extends NavBarActivity implements OnItemClickListener {
    private ListView myAlarmsList;
    private AlarmListAdapter adapter;
    private final String tag_intent = "HAVE_ALERMID";
    private final String tag_alarmobj = "ALERMOBJ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activeable);
        setRightpicID(R.mipmap.menu_add);
        initNavBar("活动能力",true,true);
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        myAlarmsList =(ListView) findViewById(R.id.alarms_list);
        myAlarmsList.setOnItemClickListener(this);
        adapter=new AlarmListAdapter(this);
        myAlarmsList.setAdapter(adapter);
    }

    private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    adapter.notifyDataSetChanged(); //发送消息通知ListView更新
                    adapter=new AlarmListAdapter(getApplicationContext());
                    myAlarmsList.setAdapter(adapter); // 重新设置ListView的数据适配器
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        loadDataInList();
    }

    private void loadDataInList() {
        hander.sendEmptyMessage(0);
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        Intent _intent=new Intent();
        _intent.setClass(this, SetAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("tag_alarmobj", null);
        bundle.putBoolean("tag_intent", false);
        _intent.putExtras(bundle);
        startActivity(_intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent _intent=new Intent();
        _intent.setClass(this, SetAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(tag_alarmobj, adapter.alarmInfoValues.get(position));
        bundle.putBoolean(tag_intent, true);
        _intent.putExtras(bundle);
        startActivity(_intent);
    }

}
