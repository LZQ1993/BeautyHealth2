package com.LocationEntity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.infrastructure.CWDomain.EntityBase;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/1/5.
 */
public class AlarmInfo extends EntityBase implements Serializable{
    public String alertID;
    public String enabled;
    public String timespan;
    public String count;
    public String starttime;
    public String title;
    public String sampleCount;
    public String musicfile; //新增的音乐部分

    public  boolean setRepeatAlarm(Context ctx,Class<?> desti_act){
        boolean result = false;
        try {
            Intent _intent = new Intent();
            _intent.setClass(ctx, desti_act);
            _intent.setAction(alertID);
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, _intent, 0);
            long ltimespan = Long.valueOf(timespan) * 60 * 1000;
            AlarmManager aManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
            aManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + ltimespan, ltimespan, pi);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    public  boolean cancelRepeatAlarm(Context ctx,Class<?> desti_act){
        boolean result = false;
        try {
            Intent _intent = new Intent();
            _intent.setClass(ctx, desti_act);
            _intent.setAction(alertID);
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, _intent, 0);
            AlarmManager aManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
            aManager.cancel(pi);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
