package com.beautyhealthapp.PersonHealth.Assistant;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.LocationEntity.AlarmInfo;
import com.beautyhealthapp.PersonHealth.Activity.AlarmActivity;
import com.beautyhealthapp.R;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/1/5.
 */
public class AlarmListAdapter extends BaseAdapter {
    private Context cxt;
    public ArrayList<AlarmInfo> alarmInfoValues=new ArrayList<AlarmInfo>();
    ISqlHelper mysql=null;
    public AlarmListAdapter(Context _cxt) {
        cxt = _cxt;
        mysql=new SqliteHelper(null, cxt);
        mysql.CreateTable("com.LocationEntity.AlarmInfo");
        List<Object> dataTemp=mysql.Query("com.LocationEntity.AlarmInfo",null);
        for(Object obj:dataTemp){
            alarmInfoValues.add((AlarmInfo)obj);
        }
    }

    @Override
    public int getCount() {
        return alarmInfoValues.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmInfoValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView= LayoutInflater.from(cxt).inflate(R.layout.activity_activeable_item, null);
        final TextView text=(TextView)resultView.findViewById(R.id.alertTitle);
        final AlarmInfo alarmInfo=alarmInfoValues.get(position);
        text.setText(alarmInfo.title+"    已提醒："+alarmInfo.sampleCount+"次");
        text.setTextSize(20);
        text.setTextColor(Color.BLACK);
        View indicator = resultView.findViewById(R.id.indicator);
        final ImageView barOnOff = (ImageView) indicator.findViewById(R.id.bar_onoff);
        boolean alarm_enabled=true;
        if(alarmInfo.enabled.equals("0")){
            alarm_enabled=false;
            text.setTextColor(Color.GRAY);
        }
        barOnOff.setImageResource(alarm_enabled ?R.mipmap.button_open : R.mipmap.button_close);
        // Set the initial state of the clock "checkbox"
        final CheckBox clockOnOff = (CheckBox) indicator.findViewById(R.id.clock_onoff);
        clockOnOff.setChecked(alarm_enabled);
        indicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean alarm_enabled = false;
                if (clockOnOff.isChecked()) {
                    alarmInfo.cancelRepeatAlarm(cxt.getApplicationContext(), AlarmActivity.class);
                    clockOnOff.setChecked(false);
                    text.setTextColor(Color.GRAY);
                    text.setText(alarmInfo.title+"    已提醒："+0+"次");
                    alarmInfo.enabled="0";
                    alarmInfo.sampleCount="0";
                }
                else{
                    clockOnOff.setChecked(true);
                    text.setTextColor(Color.BLACK);
                    alarm_enabled=true;
                    alarmInfo.enabled="1";
                    alarmInfo.sampleCount="0";
                    alarmInfo.setRepeatAlarm(cxt.getApplicationContext(), AlarmActivity.class);
                }
                barOnOff.setImageResource(alarm_enabled ? R.mipmap.button_open : R.mipmap.button_close);
                mysql.Update(alarmInfo);
            }
        });
        return resultView;
    }
}
