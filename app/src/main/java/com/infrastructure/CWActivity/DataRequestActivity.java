package com.infrastructure.CWActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyhealthapp.R;
import com.infrastructure.CWBroadcastReceiver.ResultBroadcastReceiver;
import com.infrastructure.CWDataDecoder.DataDecode;
import com.infrastructure.CWDataDecoder.IDataDecode;
import com.infrastructure.CWDataRequest.IRequest;
import com.infrastructure.CWDataRequest.LocalRequest;
import com.infrastructure.CWDataRequest.NetRequest;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWDomain.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

public class DataRequestActivity extends NavBarActivity {

	public IDataDecode dataDecode=null;
	public Object dataResult = null;
	private Context cxt;
	public  String result = null;
	private LocalBroadcastManager broadcastFragment;
	private boolean hasregister=false; //是否已经注册
	private RequestUtility requestUtility=null;
	public List<String> Notifications=new ArrayList<String>();
	public  String CurrentAction="";
	Dialog progressDialog;

	private ResultBroadcastReceiver mydatareceive = new ResultBroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
		}

		@Override
		public void dataReload(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(GlobalVariables.ACTION_DATA_RELOAD_ERROR)){
				errorDataReload(context,intent);
			}else{
				result = intent.getStringExtra(GlobalVariables.DATA_RESULT);
				CurrentAction=action;
				updateView();
			}
		}
		private void errorDataReload(Context context, Intent intent) {
			errorTip(context,intent);
		}
	};

	public void DefaultTip(Context context,String tip){
		new AlertDialog.Builder(context)
				.setTitle("提示")
				.setMessage(tip)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								return;
							}
						}).setCancelable(false).show();
		return;
	}

	//重写这个函数用于当数据访问错误时显示或提示
	public void errorTip(Context context, Intent intent){
		dismissProgressDialog();
		Toast.makeText(context, "亲~，请保持网络畅通",
				Toast.LENGTH_SHORT).show();

	}

	//重写这个函数用于更新视图上的数据（ 显示）
	public void updateView() {
	}

	@Override
	public void onPause() {
		super.onPause();
		removeBroadcast();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void registeBroadcast() {
		//注册接收广播
		if(!hasregister){
			for(String notiAction :Notifications){
				IntentFilter filter=new IntentFilter(notiAction);
				broadcastFragment.registerReceiver(mydatareceive, filter);
			}
			hasregister=true;
		}
	}
	private void removeBroadcast() {
		if(hasregister){
			broadcastFragment.unregisterReceiver(mydatareceive);
			hasregister=false;
		}
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cxt=getApplicationContext();
		if(dataDecode==null){
			dataDecode=new DataDecode();
			dataResult = new Object();
		}
		broadcastFragment=LocalBroadcastManager.getInstance(cxt);
		Notifications.add(GlobalVariables.ACTION_DATA_RELOAD_OK);
		Notifications.add(GlobalVariables.ACTION_DATA_RELOAD_ERROR);
	}


	public void setDataDecoder(IDataDecode _dataDecode)//,Object _dataResult)
	{
		dataDecode = _dataDecode;
	}

	public void setRequestUtility(RequestUtility _requestUtility){
		requestUtility=_requestUtility;
	}

	public void requestData(){
		registeBroadcast();
		IRequest mdataRequest=null;
		if (GlobalVariables.ISLOCALDATA) {
			mdataRequest = new LocalRequest(cxt);
		} else {
			mdataRequest = new NetRequest(cxt);
		}
		mdataRequest.requestData(requestUtility);
	}
	/**
	 * 得到自定义的progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.layout_progressdialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.progressdialog_anim);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;

	}

	protected void showProgressDialog(Context context,String content) {
		progressDialog = createLoadingDialog(context,content);
		progressDialog.show();
		progressDialog.setOnKeyListener(onKeyListener);

	}
	private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (progressDialog.isShowing()&&keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				removeBroadcast();
				dismissProgressDialog();
			}
			return false;
		}
	};
	protected void dismissProgressDialog() {
		if (isFinishing()) {
			return;
		}
		if (null != progressDialog && progressDialog.isShowing()) {
			removeBroadcast();
			progressDialog.dismiss();

		}
	}
}

