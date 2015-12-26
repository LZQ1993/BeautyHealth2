package com.infrastructure.CWFileSystem;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class LocalFileSystem extends AbsFileSystem{

    public LocalFileSystem(Context _cxt){
    	setContext(_cxt);
    }
    
    @Override
	public String readTxt(String inFileName)
	{
		try
		{

			FileInputStream fis = getContext().openFileInput(inFileName);
			//FileInputStream fis = new FileInputStream(inFileName);
			byte[] buff = new byte[1024];
			int hasRead = 0;
			StringBuilder sb = new StringBuilder("");

			while ((hasRead = fis.read(buff)) > 0)
			{
				sb.append(new String(buff, 0, hasRead));
			}
			// �ر��ļ�������
			fis.close();
			return sb.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean write(String content,String outFileName)
	{
		boolean result=false;
		try
		{
			// ��׷��ģʽ���ļ������
			FileOutputStream fos = getContext().openFileOutput(outFileName, getContext().MODE_APPEND);
			// ��FileOutputStream��װ��PrintStream
			PrintStream ps = new PrintStream(fos);
			// ����ļ�����
			ps.println(content);
			// �ر��ļ������
			ps.close();
			result=true;
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
		// TODO Auto-generated method stub
		return null;
	}


	public Bitmap getImage(String fileName){
		return null;
	}


}
