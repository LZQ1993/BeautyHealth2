package com.beautyhealthapp.UserCenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.LocationEntity.UserMessage;
import com.beautyhealthapp.Activity.LoginActivity;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.List;

public class UserManagerActivity extends NavBarActivity implements
		OnClickListener {

	private Button btn1,btn2;
	private TextView userid;
	private ISqlHelper iSqlHelper;
	private List<Object> list ;
	private UserMessage userMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usermanager);
		initNavBar("账户管理", true, false);
		init();
		setListener();
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		iSqlHelper = new SqliteHelper(null, getApplicationContext());
	    list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
	    if(list.size()>0){
	    	userMessage=(UserMessage) list.get(0);
		    userid.setText(userMessage.UserID);
	    }
	    
	}


	private void init() {
		btn1 = (Button) findViewById(R.id.change);
		userid=(TextView)findViewById(R.id.userid);
	}

	private void setListener() {
		btn1.setOnClickListener((OnClickListener) this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.change:
			intent.setClass(getApplicationContext(), LoginActivity.class);
			intent.putExtra("goto", UserManagerActivity.class.getName());
			startActivity(intent);
			break;
		default:
			ToastUtil.show(getApplicationContext(), "输入有误!");
		}

	}

}
