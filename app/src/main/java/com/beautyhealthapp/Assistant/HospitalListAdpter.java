package com.beautyhealthapp.Assistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Entity.HospitalInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWUtilities.LoadPicTask;

import java.util.List;

public class HospitalListAdpter extends ArrayAdapter<HospitalInfo> {
	private LayoutInflater inflater;
	private Context context;
	private Bitmap bitmap;
	private int listViewId;
	private OnClickListener mycListener;

	public HospitalListAdpter(Context context, int _listViewId,List<HospitalInfo> hospitalInfo,OnClickListener _mycListener) {
		super(context, _listViewId, hospitalInfo);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.mycListener = _mycListener;
	}

	public static class ViewHolder {
		TextView tv_hospitalName, tv_hospitalLevel, tv_hospitalBriefly;
		ImageView iv_hospitalPhoto;
		Button btn_seeall;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 可以理解为从vlist获取view 之后把view返回给ListView
			convertView = inflater.inflate(R.layout.fragment_searchhospital_item, null);
			holder.tv_hospitalName = (TextView) convertView
					.findViewById(R.id.hospitalName);
			holder.tv_hospitalLevel = (TextView) convertView
					.findViewById(R.id.hospitalLevel);
			holder.tv_hospitalBriefly = (TextView) convertView
					.findViewById(R.id.hospitalBriefly);
			holder.iv_hospitalPhoto = (ImageView) convertView
					.findViewById(R.id.hospitalPhoto);
			holder.btn_seeall = (Button) convertView
					.findViewById(R.id.btn_see_all);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HospitalInfo hospitalitem = getItem(position);
		holder.tv_hospitalName.setText(hospitalitem.HospitalName);
		holder.tv_hospitalLevel.setText(hospitalitem.HospitalLevel);
		holder.tv_hospitalBriefly.setText(hospitalitem.HospitalBriefly);
		holder.iv_hospitalPhoto.setImageResource(R.mipmap.doctorpic);
		LoadPicTask hospitallpt = new LoadPicTask(holder.iv_hospitalPhoto);
		if(hospitalitem.HospitalImgUrl.length()>0){
			String webaddrss = NetworkSetInfo.getServiceUrl()+hospitalitem.HospitalImgUrl.substring(2,
					hospitalitem.HospitalImgUrl.length());
			hospitallpt.execute(webaddrss);
		}else{
			holder.iv_hospitalPhoto.setImageResource(R.mipmap.doctorpic);
		}
		holder.btn_seeall.setTag(position);
		holder.btn_seeall.setOnClickListener(mycListener);
		return convertView;
	}
}
