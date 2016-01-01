package com.beautyhealthapp.UserCenter.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.Entity.ReturnTransactionMessage;
import com.Entity.UserInfo;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInfoActivity extends DataRequestActivity implements OnClickListener {

	private String currentNotiName = "PersonInfoNotifications";
	private String currentNotiName1 = "GetUUIDNotifications";
	private String currentNotiName2 = "UserInfoUpdateNotifications";
	private Button btn_getregiatercode;
	private Button btn_updatepassword;
	private TextView tv_phone;
	private TextView tv_registercode;
	private EditText et_name;
	private EditText et_birthday;
	private EditText et_tel;
	private EditText et_address;
	private RadioGroup rg_sex;
	private RadioButton rb_male;
	private RadioButton rb_female;
	private String sex;
	private RadioButton checkRadioButton;
	private PopupWindow pop = null;
	private Button copy;
	private LinearLayout parent;
	private View parentView;
	private String PasswordType;
	private List<Object> list;
    private String UserID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = getLayoutInflater().inflate(R.layout.activity_personalinfo, null);
		setContentView(parentView);
		Notifications.add(currentNotiName);
		Notifications.add(currentNotiName1);
		Notifications.add(currentNotiName2);
		setRightpicID(R.mipmap.menu_save);
		initNavBar("个人信息管理", true, true);
		fetchUIFromLayout();
		setListener();
		init();
		dataLoading();
	}

	private void fetchUIFromLayout() {
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_registercode = (TextView) findViewById(R.id.tv_registercode);
		et_name = (EditText) findViewById(R.id.et_name);
		et_tel = (EditText) findViewById(R.id.et_tel);
		et_address = (EditText) findViewById(R.id.et_address);
		et_birthday = (EditText) findViewById(R.id.et_birthday);
		et_birthday.setInputType(InputType.TYPE_NULL);
		rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
		rb_male = (RadioButton) findViewById(R.id.rb_male);
		rb_female = (RadioButton) findViewById(R.id.rb_female);
		btn_getregiatercode = (Button) findViewById(R.id.btn_getregiatercode);
		btn_updatepassword = (Button) findViewById(R.id.btn_updatepassword);
		View view = getLayoutInflater().inflate(R.layout.item_copy_popupwindows, null);
		pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		parent = (LinearLayout) view.findViewById(R.id.parent);
		copy = (Button) view.findViewById(R.id.copy);
		// 背景设置 无阴影
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);// 获取焦点，设置当前窗体为操作窗体
		pop.setOutsideTouchable(true); // 外围点击dismiss
		pop.setContentView(view);
	}

	private void setListener() {
		btn_getregiatercode.setOnClickListener(this);
		btn_updatepassword.setOnClickListener(this);
		rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//点击事件获取的选择对象
				checkRadioButton = (RadioButton) rg_sex.findViewById(checkedId);
				sex = checkRadioButton.getText().toString();
			}
		});
		et_birthday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(PersonalInfoActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								int month = (monthOfYear + 1);
								String strdate = year + "-";
								if (month < 10) {
									strdate = strdate + "0" + month + "-";
								} else {
									strdate = strdate + month + "-";
								}
								if (dayOfMonth < 10) {
									strdate = strdate + "0" + dayOfMonth + " ";
								} else {
									strdate = strdate + dayOfMonth + " ";
								}
								et_birthday.setText(strdate);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		tv_registercode.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				tv_registercode.setBackgroundColor(0xaaaaa);
				pop.showAsDropDown(tv_registercode, 0, 0);
				return false;
			}
		});
		//点击窗体
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		copy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				copy(tv_registercode.getText().toString(), getApplicationContext());
				pop.dismiss();
			}
		});
	}

	private void init() {
		// 从数据库中将信息查出来
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
		if (list.size() > 0) {
			UserMessage userMessage = (UserMessage) list.get(0);
			UserID = userMessage.UserID;
			tv_phone.setText(userMessage.UserID);
			tv_registercode.setText(userMessage.UUID);
			et_name.setText(userMessage.UserRealName);
			et_tel.setText(userMessage.UserTel);
			et_address.setText(userMessage.UserAddress);
			et_birthday.setText(userMessage.UserBirthday);
			PasswordType = userMessage.PasswordType;
			if (userMessage.UserSex.equals("男")) {
				rb_male.setChecked(true);
			} else {
				rb_female.setChecked(true);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			// 重新生成注册码
			case R.id.btn_getregiatercode:
				getNewUUID();
				break;
			// 密码修改
			case R.id.btn_updatepassword:
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),UpdatePasswordActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	private void dataLoading() {
		dismissProgressDialog();
		showProgressDialog(PersonalInfoActivity.this,"加载中");
		RequestUtility myru = new RequestUtility();
		myru.setIP(null);
		myru.setMethod("UserManagerService", "queryUser");
		Map requestCondition = new HashMap();
		String condition[] = { "UserID"};
		String value[] = {UserID};
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		myru.setNotification(currentNotiName);
		setRequestUtility(myru);
		requestData();
	}

	private void getNewUUID() {
		dismissProgressDialog();
		showProgressDialog(PersonalInfoActivity.this,"重新获取中...");
		RequestUtility myru = new RequestUtility();
		myru.setIP(null);
		myru.setMethod("UserManagerService", "createRegisterNumber");
		Map requestCondition = new HashMap();
		String condition[] = { "UserID" };
		String value[] = { tv_phone.getText().toString() };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		myru.setNotification(currentNotiName1);
		setRequestUtility(myru);
		requestData();
	}

	@Override
	public void updateView() {
		dismissProgressDialog();
		if (result != null) {
			if (CurrentAction == currentNotiName) {
				dataResult = dataDecode.decode(result,"UserInfo");
				if (dataResult != null) {
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						UserInfo msg = (UserInfo) realData.getResult().get(0);
						ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
						String sqlStr = "update UserMessage set UserAddress ='"
								+ msg.UserAddress + "',UserSex='" + msg.UserSex
								+ "',UserTel='" + msg.UserTel
								+ "',UserBirthday='" + msg.UserBirthday
								+ "',UserRealName='" + msg.UserRealName
								+ "' where UserID = '" + tv_phone.getText().toString() + "'";
						iSqlHelper.SQLExec(sqlStr);
						handler.sendEmptyMessage(0);
					} else {
						DefaultTip(PersonalInfoActivity.this, "暂无数据");
					}
				} else {
					DefaultTip(PersonalInfoActivity.this, "数据解析失败");
				}
			} else if (CurrentAction == currentNotiName1) {
				dataResult = dataDecode.decode(result, "ReturnTransactionMessage");
				if (dataResult != null) {
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
						if (msg.getResult().equals("1")) {
							tv_registercode.setText(msg.tip);
							ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
							String sqlStr = "update UserMessage set UUID ='" + msg.tip + "'where UserID = '"+UserID+"'";
							iSqlHelper.SQLExec(sqlStr);
							new AlertDialog.Builder(PersonalInfoActivity.this)
									.setTitle("成功")
									.setMessage("新注册码为:" + msg.tip)
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
													return;
												}
									}).setCancelable(false).show();
						} else {
							new AlertDialog.Builder(PersonalInfoActivity.this)
									.setTitle("错误")
									.setMessage("生成失败，请重新生成注册码")
									.setPositiveButton("确定", null).show();
							return;
						}
					} else {
						DefaultTip(PersonalInfoActivity.this, "暂无数据");
					}
				} else {
					DefaultTip(PersonalInfoActivity.this, "数据解析失败");
				}
			} else if (CurrentAction == currentNotiName2) {
				dataResult = dataDecode.decode(result, "ReturnTransactionMessage");
				if (dataResult != null) {
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
						if (msg.getResult().equals("1")) {
							new AlertDialog.Builder(this)
									.setTitle("成功")
									.setMessage(msg.tip)
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											return;
										}
									}).setCancelable(false).show();
						} else {
							new AlertDialog.Builder(this)
									.setTitle("失败")
									.setMessage(msg.tip)
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											return;
										}
									}).setCancelable(false).show();
						}
					} else {
						DefaultTip(PersonalInfoActivity.this, "暂无数据");
					}
				} else {
					DefaultTip(PersonalInfoActivity.this, "数据解析失败");
				}
			}
		} else {
			DefaultTip(PersonalInfoActivity.this, "网络获取数据失败");
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			init();
		}
	};

	/**
	 * 实现文本复制功能
	 *
	 * 
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能
	 * 
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	public void onNavBarRightButtonClick(View view) {
		// 将数据保存到本地
		if (et_name.getText().toString().equals("")
				|| et_tel.getText().toString().equals("")
				|| et_address.getText().toString().equals("")) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("参数不能为空")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
									return;
								}
							}).setCancelable(false).show();

		} else {
			ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
			String sqlStr = "update UserMessage set UserAddress ='"
					+ et_address.getText().toString() + "',UserSex='" + sex
					+ "',UserTel='" + et_tel.getText().toString()
					+ "',UserBirthday='" + et_birthday.getText().toString()
					+ "',UserRealName='" + et_name.getText().toString()
					+ "' where UserID = '"+tv_phone.getText().toString()+"'";
			iSqlHelper.SQLExec(sqlStr);
			userMsgUpdate();
		}

	}

	private void userMsgUpdate() {
		dismissProgressDialog();
		showProgressDialog(PersonalInfoActivity.this,"保存中...");
		RequestUtility myru = new RequestUtility();
		myru.setIP(null);
		myru.setMethod("UserManagerService", "updateUserInfo");
		Map requestCondition = new HashMap();
		String condition[] = { "UserID", "UserRealName", "UserBirthday", "UserTel", "UserSex", "UserAddress" };
		String value[] = { tv_phone.getText().toString(), et_name.getText().toString(), et_birthday.getText().toString(),
				tv_phone.getText().toString(), sex, et_address.getText().toString() };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		myru.setNotification(currentNotiName2);
		setRequestUtility(myru);
		requestData();
	}
}
