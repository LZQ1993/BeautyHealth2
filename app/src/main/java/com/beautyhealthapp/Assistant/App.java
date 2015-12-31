package com.beautyhealthapp.Assistant;

import android.app.Application;

import com.infrastructure.CWComponent.AppInfo;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

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
		}
	}

}
