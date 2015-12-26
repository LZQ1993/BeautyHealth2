package com.infrastructure.CWFileSystem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;

public class RawFileSystem extends AbsFileSystem {
    public RawFileSystem(Context _cxt){
    	setContext(_cxt);
    }
	@Override
	public String readTxt(String inFileName) {
		Resources res = getContext().getResources();
		int fileID = res.getIdentifier(inFileName, "raw", getContext().getPackageName());
		String text = null;
		try {
			InputStream in = getContext().getResources().openRawResource(fileID);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			text = EncodingUtils.getString(buffer, "UTF-8");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public Bitmap getImage(String fileName){
		return null;
	}
}
