package com.beautyhealthapp.CallCenter.Assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.beautyhealthapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2015/12/27.
 */
public class CityListAdapter extends BaseAdapter {

    public int SelectedPosition=0;
    private Context context;
    List<Boolean> res1;
    private List<String[]> params;
    // 用于记录每个RadioButton的状态，并保证只可选一个
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();
    public static class ViewHolder {
        TextView tvName;
        RadioButton rb_state;
    }

    public CityListAdapter(Context context, List<String[]> _params,List<Boolean> res1) {
        this.params = _params;
        this.context = context;
        this.res1=res1;
    }

    @Override
    public int getCount() {
        return params.get(0).length;
    }

    @Override
    public Object getItem(int position) {
        return params.get(0)[position];
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
        String citynames = params.get(0)[position];
        String phone = params.get(1)[position];
        holder.tvName.setText(citynames);
        holder.rb_state.setChecked(res1.get(position));

        if(res1.get(position)==true){
            SelectedPosition=position;
        }

        holder.rb_state.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                res1.set(position, true);
                for(int i=0;i<res1.size();i++){
                    if(i!=position){
                        res1.set(i, false);
                    }
                }
                SelectedPosition=position;
                CityListAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
