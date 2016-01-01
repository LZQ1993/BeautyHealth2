package com.infrastructure.CWMobileDevice;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class MobilePhone422 extends AbsMobilePhone {

		public void SMSSend(Activity act,String address,String content){
			SmsManager sms=SmsManager.getDefault();
			PendingIntent sendIntent=PendingIntent.getBroadcast(act, 0, new Intent(), 0);
			sms.sendTextMessage(address, null, content, sendIntent, null);
		}

		public void sendMessage(String number, String message) {  		  
			SmsManager smsManager =SmsManager.getDefault();  
			smsManager.sendTextMessage(number, null,message, null, null);  		 
		}

		public  Intent DialToNumber(String PhoneNumber){
			Intent _intent=new Intent();
			_intent.setAction(Intent.ACTION_DIAL);
			String data="tel:"+PhoneNumber;
			Uri auri=Uri.parse(data);
			_intent.setData(auri);
			return _intent;
		}
		
		//HOME
		public Intent BackToSysHom(){
			Intent _intent=new Intent();
			_intent.setAction(Intent.ACTION_MAIN);
			_intent.addCategory(Intent.CATEGORY_HOME);
			return _intent;
		}
		
		//HOME
		public  String Orientation(){

			return null;
		}
	


		
}
