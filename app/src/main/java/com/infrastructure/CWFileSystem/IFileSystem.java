package com.infrastructure.CWFileSystem;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;



public interface IFileSystem {

	public void setContext(Context _cxt);
	
	public Context getContext();
	
	public String readTxt(String inFileName);

	public boolean write(String content,String outFileName);
	
	public List<Map<String,Object>> getFileList();
	
	public Bitmap getImage(String fileName);

    public String getLocalPath();
}
