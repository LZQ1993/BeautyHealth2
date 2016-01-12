package com.beautyhealthapp.Assistant;

import android.app.Application;
import android.content.Intent;

import com.LocationEntity.GPSStatueAndTime;
import com.LocationEntity.ServiceState;
import com.beautyhealthapp.Service.GpsService;
import com.beautyhealthapp.Service.LocationService;
import com.infrastructure.CWComponent.AppInfo;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.List;

public class App extends Application {
	private AppInfo appInfo;
	@Override
	public void onCreate() {
		super.onCreate();
		appInfo = new AppInfo(getApplicationContext());
		ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
		if (appInfo.isNewVersion()) {
			iSqlHelper.CreateTable("com.LocationEntity.UserMessage");
			iSqlHelper.CreateTable("com.LocationEntity.UserLocal");
			iSqlHelper.CreateTable("com.LocationEntity.BluetoothState");
			iSqlHelper.CreateTable("com.LocationEntity.BindingMessage");
			iSqlHelper.CreateTable("com.LocationEntity.LocalFamilyNum");
			iSqlHelper.CreateTable("com.LocationEntity.GPSStatueAndTime");
			iSqlHelper.CreateTable("com.LocationEntity.ServiceState");
		}else{
			List<Object> ls1 = iSqlHelper.Query("com.LocationEntity.GPSStatueAndTime",null);
			Intent intentService ;
			ServiceState aService=new ServiceState();
			aService.ServiceName="LocationUploading";
			Intent _Intent = new Intent(this,LocationService.class);
			stopService(_Intent);
			if (ls1.size() > 0) {
				GPSStatueAndTime gat = (GPSStatueAndTime) ls1.get(0);
				if (gat.isUpload.equals("1")) {
					//停止这个后台的服务
					aService.setCurrentServiceStateInDB("0", this);
					//停止这个后台的服务
					intentService = new Intent(this,GpsService.class);
					intentService.setAction(aService.ServiceName);
					intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					stopService(intentService);
					aService.setCurrentServiceStateInDB("1",this);
					//启动一个后台的服务
					intentService = new Intent(this,GpsService.class);
					intentService.setAction(aService.ServiceName);
					intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startService(intentService);

				} else {
					//停止这个后台的服务
					aService.setCurrentServiceStateInDB("0", this);
					//停止这个后台的服务
					intentService = new Intent(this,GpsService.class);
					intentService.setAction(aService.ServiceName);
					intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					stopService(intentService);

				}
			} else {
				//停止这个后台的服务
				aService.setCurrentServiceStateInDB("0", this);
				//停止这个后台的服务
				intentService = new Intent(this,GpsService.class);
				intentService.setAction(aService.ServiceName);
				intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				stopService(intentService);

			}

		}
	}

}
