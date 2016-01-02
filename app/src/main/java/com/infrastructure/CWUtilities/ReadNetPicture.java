package com.infrastructure.CWUtilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ReadNetPicture {


    public static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(true);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            Options opt = new Options();
            opt.inJustDecodeBounds = true;
            //设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
            opt.inDither = false;
            opt.inSampleSize = 2;
            opt.inJustDecodeBounds = false;//最后把标志复原
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is, null, opt);
            //关闭数据流
            is.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

    }
}
