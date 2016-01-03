package com.beautyhealthapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.Assistant.MyCountTimer;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/23.
 */
public class LoginActivity extends DataRequestActivity implements
        OnClickListener {
    private Button btn_login;
    private Button btn_sendregistercode;
    private Button btn_registercodelogin;
    private EditText et_phone;
    private EditText et_password;
    private String UserName;
    private String Password;
    private String PasswordType;
    private CheckBox cb_jizhumima;
    private SharedPreferences config;
    private RelativeLayout rl_user;
    private ImageView login_picture;
    private String currentNotiName = "LoginNotifications";
    private String currentNotiName1 = "ReturnCodeNotifications";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Notifications.add(currentNotiName);
        Notifications.add(currentNotiName1);
        initNavBar("登录", false,false);
        fetchUIFromLayout();
        setListener();

    }

    private void fetchUIFromLayout() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_sendregistercode = (Button) findViewById(R.id.btn_sendregistercode);
        btn_registercodelogin = (Button) findViewById(R.id.btn_registercodelogin);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_jizhumima = (CheckBox) findViewById(R.id.cb_jizhumima);
        rl_user = (RelativeLayout) findViewById(R.id.rl_user);
        login_picture = (ImageView) findViewById(R.id.login_picture);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.login_anim);
        anim.setFillAfter(true);
        rl_user.startAnimation(anim);

        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.login_head_anim);
        anim2.setFillAfter(true);
        login_picture.startAnimation(anim2);
    }

    private void setListener() {
        btn_login.setOnClickListener(this);
        btn_sendregistercode.setOnClickListener(this);
        btn_registercodelogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                PasswordType = "2";
                loginTask(PasswordType);
                break;
            case R.id.btn_sendregistercode:
                if (et_phone.getText().toString().equals("")) {
                    ToastUtil.show(getApplicationContext(), "手机号不能为空");
                } else {
                    getNetPassword();
                }
                break;
            case R.id.btn_registercodelogin:
                PasswordType = "1";
                loginTask(PasswordType);
                break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        config = getSharedPreferences("config", MODE_PRIVATE);
        boolean isChecked = config.getBoolean("isChecked", false);
        if (isChecked) {
            et_phone.setText(config.getString("UserName", ""));
            et_password.setText(config.getString("Password", ""));
        }
        cb_jizhumima.setChecked(isChecked);
    }


    private void loginTask(String passwordType) {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "loginUserManager");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID", "Password", "PasswordType"};
        String value[] = {et_phone.getText().toString(),
                et_password.getText().toString(), passwordType};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
        showProgressDialog(LoginActivity.this, "登录中,请稍候...");
    }

    private void getNetPassword() {
        MyCountTimer timeCount = new MyCountTimer(btn_sendregistercode, 0xfff30008, 0xff969696);// ������������ɫֵ
        timeCount.start();
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "returnCode");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID"};
        String value[] = {et_phone.getText().toString()};
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
            dataResult = dataDecode.decode(result,"ReturnTransactionMessage");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (realData.getResultcode().equals("1")&&realData.getResult().size()>0) {
                    ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                    if (CurrentAction == currentNotiName) {
                        if (msg.getResult().equals("1")) {
                            ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                            iSqlHelper.SQLExec("delete from UserMessage");
                            UserMessage userMessage = new UserMessage();
                            userMessage.UserID = et_phone.getText().toString();
                            userMessage.Password = et_password.getText().toString();
                            userMessage.UUID = msg.tip;
                            userMessage.PasswordType = PasswordType;
                            iSqlHelper.Insert(userMessage);
                            UserName = et_phone.getText().toString();
                            Password = et_password.getText().toString();
                            isRemember();
                            if (Password.equals("123456")) {
                                ToastUtil.show(getApplicationContext(), "登录成功,默认密码为：123456，建议您立即修改");
                            } else {
                                ToastUtil.show(getApplicationContext(), "登录成功");
                            }
                            Class<?> gotoClz = null;
                            try {
                                gotoClz = Class.forName(getIntent().getStringExtra("goto"));
                            } catch (Exception e) {
                            }
                            if (gotoClz != null) {
                                Intent intent = new Intent(LoginActivity.this, gotoClz);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                            finish();
                            return;
                        } else {
                            DefaultTip(LoginActivity.this, "登录失败");
                        }
                    }else if(CurrentAction == currentNotiName1){
                        if (msg.getResult().equals("1")) {
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"发送失败，60s后请重新发送", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    DefaultTip(LoginActivity.this, "暂无数据");
                }

            } else {
                DefaultTip(LoginActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(LoginActivity.this, "网络获取数据失败");
        }
    }

    /**
     * 记住密码
     */
    private void isRemember() {
        Editor edit = config.edit();
        boolean isChecked = cb_jizhumima.isChecked();
        edit.putBoolean("isChecked", isChecked);
        if (isChecked) {
            edit.putString("UserName", UserName)
                    .putString("Password", Password);
        } else {
            edit.remove("UserName");
            edit.remove("Password");
        }
        edit.commit();
    }

}