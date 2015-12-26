package com.infrastructure.CWUtilities;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.beautyhealthapp.R;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class LoadPicTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView resultView;
    private int defaultPic = R.mipmap.menu_download_image;

    public void setDefaultPic(int defaultPic) {
        this.defaultPic = defaultPic;
    }

    public LoadPicTask(ImageView resultView) {
        this.resultView = resultView;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            resultView.setImageResource(defaultPic);
        } else {
            resultView.setImageBitmap(bitmap);
        }
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap image = null;
        try {

            URL url = new URL(params[0]);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
