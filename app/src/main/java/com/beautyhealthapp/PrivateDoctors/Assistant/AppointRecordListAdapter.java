package com.beautyhealthapp.PrivateDoctors.Assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Entity.AppointInfo;
import com.beautyhealthapp.R;

import java.util.List;

/**
 * Created by lenovo on 2015/12/26.
 */
public class AppointRecordListAdapter extends ArrayAdapter<AppointInfo> {
    private LayoutInflater inflater;
    private Context context;
    private int listViewId;
    public AppointRecordListAdapter(Context context, int _listViewId,List<AppointInfo> appointInfo) {
        super(context, _listViewId, appointInfo);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    public static class ViewHolder {
        TextView tv_hospitalName,tv_class, tv_doctorName, tv_submitTime,tv_appointTime;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            // 可以理解为从vlist获取view 之后把view返回给ListView
            convertView = inflater.inflate(
                    R.layout.activity_appointrecord_item, null);
            holder.tv_hospitalName = (TextView) convertView
                    .findViewById(R.id.ap_info_hospitalName);
            holder.tv_doctorName = (TextView) convertView
                    .findViewById(R.id.ap_info_doctorName);
            holder.tv_class = (TextView) convertView
                    .findViewById(R.id.ap_info_doctorClass);
            holder.tv_submitTime = (TextView) convertView
                    .findViewById(R.id.ap_info_submittime);
            holder.tv_appointTime = (TextView) convertView
                    .findViewById(R.id.ap_info_appointtime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppointInfo appointInfoitem = getItem(position);
        holder.tv_hospitalName.setText(appointInfoitem.HospitalName);
        holder.tv_doctorName.setText(appointInfoitem.DoctorName);
        holder.tv_class.setText(appointInfoitem.DoctorClass);
        holder.tv_submitTime.setText(appointInfoitem.SubmitTime);
        if(appointInfoitem.AppointTime.equals("1")){
            holder.tv_appointTime.setText("越快越好");
        }else if(appointInfoitem.AppointTime.equals("2")){
            holder.tv_appointTime.setText("一周内");
        }else {
            holder.tv_appointTime.setText("一月内");
        }
        return convertView;
    }
}
