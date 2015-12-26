package com.infrastructure.CWFileSystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SDCardFileSystem extends AbsFileSystem {
 
    public SDCardFileSystem(Context _cxt){
    	setContext(_cxt);
    }
    
    @Override
	public String readTxt(String inFileName) {
    	try
		{
			// ����ֻ�������SD��������Ӧ�ó�����з���SD��Ȩ��
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				File sdCardDir = Environment.getExternalStorageDirectory();
				FileInputStream fis = new FileInputStream(sdCardDir.getCanonicalPath() + inFileName);
				BufferedReader br = new BufferedReader(new 
					InputStreamReader(fis));
				StringBuilder sb = new StringBuilder("");
				String line = null;
				while ((line = br.readLine()) != null)
				{
					sb.append(line);
				}
				br.close();
				return sb.toString();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean write(String content, String outFileName) {
		boolean result=false;
		try
		{
			// ����ֻ�������SD��������Ӧ�ó�����з���SD��Ȩ��
			if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			{
				File sdCardDir = Environment.getExternalStorageDirectory();
				File targetFile = new File(sdCardDir.getCanonicalPath() + outFileName);
				RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
				raf.seek(targetFile.length());
				raf.write(content.getBytes());
				raf.close();
				result=true;
			}
		}
		catch (Exception e)
		{
			result=false;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getFileList() {
		File root=new File("/mnt/sdcard/");
		File[] currentFiles;
		List<Map<String,Object>> filesAndFolders=new ArrayList<Map<String,Object>>();
		
		if(root.exists()){
			currentFiles=root.listFiles();		
			super.addFile(currentFiles,filesAndFolders);
		}
		return filesAndFolders;
	}
	
	
	public Bitmap getImage(String fileName){
		return null;
	}
}
