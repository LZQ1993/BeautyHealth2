package com.beautyhealthapp.Assistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Entity.AdvertisementInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWUtilities.LoadPicTask;

import java.util.List;


public class AdvertisementLisrAdapter extends ArrayAdapter<AdvertisementInfo> {
	private LayoutInflater inflater;
	private Context context;
	private int listViewId;
	private Bitmap bitmap;
	public AdvertisementLisrAdapter(Context context, int _listViewId, List<AdvertisementInfo> _advertisementInfoItem) {
		super(context, _listViewId, _advertisementInfoItem);
		this.context = context;
		listViewId=_listViewId;
		this.inflater = LayoutInflater.from(context);
	}

	public static class ViewHolder {
		TextView tv_ad_content,tv_mc_time;
		ImageView iv_ad;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 可以理解为从vlist获取view 之后把view返回给ListView
			convertView = inflater.inflate(
					R.layout.fragment_memberscenter_item, null);
			holder.tv_ad_content = (TextView) convertView
					.findViewById(R.id.tv_ad_content);
			holder.tv_mc_time= (TextView) convertView
					.findViewById(R.id.tv_mc_time);
			holder.iv_ad = (ImageView) convertView
					.findViewById(R.id.iv_ad);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AdvertisementInfo advertisementitem = getItem(position);
		holder.tv_ad_content.setText(advertisementitem.AdTitle);
		holder.tv_mc_time.setText(advertisementitem.PublishTime);
		holder.iv_ad.setImageResource(R.mipmap.menu_download_image);
		if(advertisementitem.AdImgUrl.length()>0){
			LoadPicTask advertisementlpt = new LoadPicTask(holder.iv_ad);
			String webaddrss = NetworkSetInfo.getServiceUrl()+advertisementitem.AdImgUrl.substring(2, advertisementitem.AdImgUrl.length());
			advertisementlpt.execute(webaddrss);
		}else{
			holder.iv_ad.setImageResource(R.mipmap.menu_download_image);
		}
		return convertView;
	}
}
