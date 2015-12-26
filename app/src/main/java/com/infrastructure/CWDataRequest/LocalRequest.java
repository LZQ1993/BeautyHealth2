package com.infrastructure.CWDataRequest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.infrastructure.CWDomain.GlobalVariables;
import com.infrastructure.CWFileSystem.AbsFileSystem;
import com.infrastructure.CWFileSystem.AssetFileSystem;


@SuppressWarnings("unused")
public class LocalRequest implements IRequest {
	private Context context;
	private String responseResult;

	private String action;
	
	public LocalRequest(Context context) {
		this.context = context;
	}

	public String readFileFromAssets(String filename) {
		AbsFileSystem mabs=new AssetFileSystem(context);
		return mabs.readTxt(filename);
	}

	@Override
	public String responseData() {

		return responseResult;
	}
	
	public void requestData(RequestUtility _requestUtility){
		String fileName_old=_requestUtility.getMethodName();
		String fileName_new=fileName_old;
		if(fileName_old.contains(".")){
			String[] fileNames= fileName_old.split("\\.");
			fileName_new=fileNames[0];
		}
		fileName_new="test_"+fileName_new;
	    action=_requestUtility.getNotificationName();
		responseResult = readFileFromAssets(fileName_new);
		new MyThread().start();
		
	}
	private class MyThread extends Thread{
		   public void run(){
			   try {
				sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   Intent intent = new Intent();
		       intent.setAction(action);
			   intent.putExtra(GlobalVariables.DATA_RESULT, responseResult);
			   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		   }
    }

}
