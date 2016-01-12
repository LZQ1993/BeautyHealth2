package com.infrastructure.CWUtilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CWAlarmManager {
	
	public static void startSystemAlarm(Context context,Class<?>targetService,long timeMinute,String actionIntent){
		PendingIntent alarmSender = null;  
	    Intent startIntent = new Intent(context, targetService);  
	    startIntent.setAction(actionIntent);  
	    try {  
	        alarmSender = PendingIntent.getService(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
	    } catch (Exception e) {  
	    	Toast.makeText(context, "服务启动失败", Toast.LENGTH_SHORT).show();
	    }  
	    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
	    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),timeMinute*60*1000, alarmSender);
	}
	
	public static void stopSystemAlarm(Context context,Class<?>targetService,String actionIntent){
		PendingIntent alarmSender = null;  
        Intent startIntent = new Intent(context,targetService); 
        startIntent.setAction(actionIntent);  
        context.stopService(startIntent);
        try {  
            alarmSender = PendingIntent.getService(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
        } catch (Exception e) {  
        	Toast.makeText(context, "服务关系失败", Toast.LENGTH_SHORT).show();
        }  
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
        am.cancel(alarmSender);
	}
	
	public static void startSystemAlarmByReceive(Context context,Long timeMinute,String actionIntent){
		PendingIntent alarmSender = null;  
	    Intent startIntent = new Intent();  
	    startIntent.setAction(actionIntent);  
	    alarmSender = PendingIntent.getBroadcast(context,0,startIntent,PendingIntent.FLAG_UPDATE_CURRENT);  
	    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
	    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),timeMinute*60*1000, alarmSender);
	}
	
	public static void stopSystemAlarmByReceive(Context context,String actionIntent){
		PendingIntent alarmSender = null;  
        Intent startIntent = new Intent(); 
        startIntent.setAction(actionIntent);  
        alarmSender = PendingIntent.getBroadcast(context,0,startIntent,PendingIntent.FLAG_UPDATE_CURRENT);  
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
        am.cancel(alarmSender);
        alarmSender.cancel();
	}

}
