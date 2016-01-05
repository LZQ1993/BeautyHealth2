package com.beautyhealthapp.PersonHealth.Assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.beautyhealthapp.R;

import java.util.ArrayList;
public class RouteStepListAdapter extends BaseAdapter {
	private ArrayList<String> mList;
	private LayoutInflater mInflater;
	
	
	public RouteStepListAdapter(Context context,ArrayList<String> list){
		mList=list;
		mInflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	

	@Override
	public View getView(int Position, View contentView, ViewGroup arg2) {
		
		
		ViewHolder viewHolder;
		
		if(contentView==null){
			viewHolder=new ViewHolder();
			contentView = mInflater.inflate(R.layout.activity_routestepshow_item, null);
			viewHolder.content = (TextView) contentView.findViewById(R.id.textView);
			contentView.setTag(viewHolder);
		}else{
			
			viewHolder = (ViewHolder) contentView.getTag();
		}
		viewHolder.content.setText((String)mList.get(Position));
		return contentView;
	}
	class ViewHolder{
		public TextView content;
	}

}
