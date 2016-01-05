package com.beautyhealthapp.PersonHealth.Assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.LocationEntity.MusicInfo;
import com.beautyhealthapp.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/1/5.
 */
public class MusicListAdapter extends BaseAdapter {

    private ArrayList<MusicInfo> musicList;
    private LayoutInflater inflater;

    public MusicListAdapter(Context context,ArrayList<MusicInfo> list){
        musicList=list;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView , ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.activity_musicselectitem,null);
            viewHolder.name=(TextView)convertView.findViewById(R.id.item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(musicList.get(position).musicName);
        return convertView;
    }

    private static class ViewHolder{
        public TextView name;
    }

}
