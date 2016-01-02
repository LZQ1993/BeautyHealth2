package com.beautyhealthapp.PrivateDoctors.Assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.Entity.MedicalInfo;
import com.Entity.Reply;
import com.beautyhealthapp.R;

import java.util.ArrayList;

public class MedicalInfoListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<MedicalInfo> medicalInfo;
	private ArrayList<ArrayList<Reply>> replyInfo;
	private Context context;
	private OnClickListener mycListener;
	public MedicalInfoListAdapter(Context context, ArrayList<MedicalInfo> medicalInfo,
								ArrayList<ArrayList<Reply>> replyInfo,OnClickListener mycListener) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.medicalInfo = medicalInfo;
		this.replyInfo = replyInfo;
		this.mycListener = mycListener;
	}

	public static class ViewHolder {
		TextView tv_issue, tv_asker,tv_issueNum,tv_mz;
		Button btn_issue_pic;
		ListView reply_area;
		LinearLayout ll_mianze;
	}

	@Override
	public int getCount() {
		return medicalInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return medicalInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 可以理解为从vlist获取view 之后把view返回给ListView
			convertView = inflater.inflate(
					R.layout.activity_medicalinfo_item, null);
			holder.tv_issue = (TextView) convertView
					.findViewById(R.id.tv_pd_mc_item_issue);
			holder.tv_asker = (TextView) convertView
					.findViewById(R.id.tv_pd_mc_item_asker);
			holder.ll_mianze = (LinearLayout) convertView
					.findViewById(R.id.ll_mianze);
			holder.btn_issue_pic = (Button) convertView
					.findViewById(R.id.btn_issue_pic);
			holder.reply_area = (ListView) convertView
					.findViewById(R.id.reply_area);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MedicalInfo medicalInfoItem = (MedicalInfo) medicalInfo.get(position);
		holder.tv_issue.setText(medicalInfoItem.QuestionContent);
		holder.tv_asker.setText(medicalInfoItem.UserID);
		if(medicalInfoItem.Reply.size()>0){
			holder.ll_mianze.setVisibility(View.VISIBLE);
		}
		if (replyInfo.get(position) != null)
			holder.reply_area.setAdapter(new ReplyInfoListAdapter(context,replyInfo.get(position), position ,mycListener));
		holder.btn_issue_pic.setTag(position+":");
		holder.btn_issue_pic.setOnClickListener(mycListener);
		return convertView;
	}

}
