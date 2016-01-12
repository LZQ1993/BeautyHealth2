package com.beautyhealthapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.LocationEntity.GPSStatueAndTime;
import com.LocationEntity.ServiceState;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.CWAlarmManager;

import java.util.List;

public class GpsService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ISqlHelper iSqlHelper = new SqliteHelper(null,this);
		List<Object> list = iSqlHelper.Query("com.LocationEntity.GPSStatueAndTime", null);
		GPSStatueAndTime gPSStatueAndTime = (GPSStatueAndTime) list.get(0);
		CWAlarmManager.stopSystemAlarm(getApplicationContext(), LocationService.class, "com.chinawit.locationuploading");
		if(gPSStatueAndTime.Time.equals("")||gPSStatueAndTime.Time==null) {
			Toast.makeText(getApplicationContext(), "时间为空", Toast.LENGTH_SHORT).show();
		}else{
			int Time =Integer.valueOf(gPSStatueAndTime.Time);
			CWAlarmManager.startSystemAlarm(getApplicationContext(), LocationService.class, Time, "com.chinawit.locationuploading");
		}
		Toast.makeText(this, "服务 启动1", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		ServiceState aService=new ServiceState();
		aService.ServiceName="LocationUploading";
		if(aService.requireStart(getApplicationContext())){
			Intent intentService = new Intent(getApplicationContext(),GpsService.class);
        	intentService.setAction(aService.ServiceName);
        	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	getApplicationContext().startService(intentService);
		}
		else{
			CWAlarmManager.stopSystemAlarm(getApplicationContext(), LocationService.class, "com.chinawit.locationuploading");
			super.onDestroy();
		}
	}
	

}
