package com.infrastructure.CWComponent;

import android.app.Application;

public class App extends Application {
	private AppInfo appInfo;
	@Override
	public void onCreate() {
		super.onCreate();
		appInfo = new AppInfo(getApplicationContext());

	}

}
