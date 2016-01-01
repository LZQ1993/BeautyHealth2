package com.beautyhealthapp.UserCenter.Assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.Entity.CallCenterOfCity;
import com.beautyhealthapp.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2015/12/27.
 */
public class CityListAdapter extends BaseAdapter{
    public int SelectedPosition=0;
    private Context context;
    ArrayList<CallCenterOfCity> CityInfo;
    ArrayList<Boolean> isSelected;
    public static class ViewHolder {
        TextView tvName;
        RadioButton rb_state;
    }
    public CityListAdapter(Context context,ArrayList<CallCenterOfCity> CityInfo, ArrayList<Boolean> isSelected) {
        this.CityInfo = CityInfo;
        this.context = context;
        this.isSelected=isSelected;
    }

    @Override
    public int getCount() {
        return CityInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return CityInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_callcenter_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_device_name);
            holder.rb_state=(RadioButton)convertView.findViewById(R.id.rb_light);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CallCenterOfCity callCenterOfCity = (CallCenterOfCity) getItem(position);
        String citynames =callCenterOfCity.CityName;
        String phone =callCenterOfCity.Tel;
        holder.tvName.setText(citynames);
        holder.rb_state.setChecked(isSelected.get(position));

        if(isSelected.get(position)==true){
            SelectedPosition=position;
        }

        holder.rb_state.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isSelected.set(position, true);
                for(int i=0;i<isSelected.size();i++){
                    if(i!=position){
                        isSelected.set(i, false);
                    }
                }
                SelectedPosition=position;
                CityListAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
