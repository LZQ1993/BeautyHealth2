package com.beautyhealthapp.PrivateDoctors.Assistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Entity.DoctorInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWDataRequest.NetworkSetInfo;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by lenovo on 2015/12/26.
 */
public class DoctorInfoListAdapter extends ArrayAdapter<DoctorInfo> {
    private LayoutInflater inflater;
    private Context context;
    private Bitmap bitmap;
    private int listViewId;
    public DoctorInfoListAdapter(Context context, int _listViewId,List<DoctorInfo> doctorInfo) {
        super(context, _listViewId, doctorInfo);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    public static class ViewHolder {
        TextView tv_doctorName, tv_ClassName, tv_briefContent;
        ImageView iv_doctorPic;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            // 可以理解为从vlist获取view 之后把view返回给ListView
            convertView = inflater.inflate(R.layout.activity_doctorbrieflyinfo_item, null);
            holder.tv_doctorName = (TextView) convertView
                    .findViewById(R.id.doctorName);
            holder.tv_ClassName = (TextView) convertView
                    .findViewById(R.id.ClassName);
            holder.tv_briefContent = (TextView) convertView
                    .findViewById(R.id.briefContent);
            holder.iv_doctorPic = (ImageView) convertView
                    .findViewById(R.id.doctorPhoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DoctorInfo doctorInfoItem = getItem(position);
        holder.tv_doctorName.setText(doctorInfoItem.getDoctorName());
        holder.tv_ClassName.setText(doctorInfoItem.getClassName());
        holder.tv_briefContent.setText("简介:"+doctorInfoItem.getDoctorDescription());
        if(doctorInfoItem.DoctorImgUrl.length()>0){
            String webaddrss = NetworkSetInfo.getServiceUrl()+doctorInfoItem.DoctorImgUrl.substring(2, doctorInfoItem.DoctorImgUrl.length());
            LoadPicTask lit = new LoadPicTask(convertView, webaddrss);
            lit.execute(webaddrss);
        }else{
            holder.iv_doctorPic.setImageResource(R.mipmap.doctorpic);
        }
        return convertView;
    }
    /**
     * 此方法用来异步加载图片
     *
     * @param //imageview
     * @param //path
     */
    // 加载图片的异步任务
    class LoadPicTask extends AsyncTask<String, Void, Bitmap> {
        private View resultView;
        private String picUrl;

        LoadPicTask(View resultView, String picUrl) {
            this.resultView = resultView;
            this.picUrl = picUrl;
        }

        // doInBackground完成后才会被调用
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // 调用setTag保存图片以便于自动更新图片
            // resultView.setTag(bitmap);
            if (bitmap == null) {
                ((ImageView) resultView.findViewById(R.id.doctorPhoto))
                        .setImageResource(R.mipmap.doctorpic);
            } else {

                ((ImageView) resultView.findViewById(R.id.doctorPhoto))
                        .setImageBitmap(bitmap);
            }
        }

        // 从网上下载图片
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image = null;
            try {
                // new URL对象 把网址传入
                URL url = new URL(params[0]);
                // 取得链接
                URLConnection conn = url.openConnection();
                conn.connect();
                // 取得返回的InputStream
                InputStream is = conn.getInputStream();
                // 将InputStream变为Bitmap
                image = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }
    }
}
