package com.infrastructure.CWMobileDevice;

import android.app.Activity;
import android.content.Intent;

public interface IMobileDevice {
	    
	    //发送短信
		public void SMSSend(Activity act, String address, String content);
		public void sendMessage(String number, String message);

		//号码Dial
		public  Intent DialToNumber(String PhoneNumber);
		
		//HOME
		public  Intent BackToSysHom();
		
		//HOME
		public  String Orientation();
		
		

	
}
