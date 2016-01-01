package com.infrastructure.CWMobileDevice;

import android.app.Activity;
import android.content.Intent;

public abstract class AbsMobilePhone implements IMobileDevice {

	@Override
	public void SMSSend(Activity act, String address, String content) {
		// TODO Auto-generated method stub

	}
	public void sendMessage(String number, String message){
		
	}

	@Override
	public Intent DialToNumber(String PhoneNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Intent BackToSysHom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String Orientation() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
