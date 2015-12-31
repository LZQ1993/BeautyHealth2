package com.beautyhealthapp.UserCenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/31.
 */
public class UpdatePasswordActivity extends DataRequestActivity implements OnClickListener {

    private String currentNotiName = "UpdatePasswordNotifications";
    private String currentNotiName1 = "UpdateGetCodeNotifications";
    private Button btn_updatepassword;
    private Button btn_sendregistercode;
    private Button btn_registercodeupdate;
    private EditText et_oldpassword;
    private EditText et_newpassword;
    private EditText et_passwordok;
    private String PasswordType;
    private String UserID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepassword);
        Notifications.add(currentNotiName);
        Notifications.add(currentNotiName1);
        initNavBar("密码修改", true, false);
        fetchUIFromLayout();
        setListener();
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
        }else{
            UserID="0000000";
        }
    }

    private void fetchUIFromLayout() {
        btn_updatepassword=(Button) findViewById(R.id.btn_updatepassword);
        btn_sendregistercode=(Button) findViewById(R.id.btn_sendregistercode);
        btn_registercodeupdate=(Button) findViewById(R.id.btn_registercodeupdate);
        et_oldpassword=(EditText) findViewById(R.id.et_oldpassword);
        et_newpassword=(EditText) findViewById(R.id.et_newpassword);
        et_passwordok=(EditText) findViewById(R.id.et_passwordok);
    }

    private void setListener() {
        btn_updatepassword.setOnClickListener(this);
        btn_sendregistercode.setOnClickListener(this);
        btn_registercodeupdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //密码修改
            case R.id.btn_updatepassword:
                PasswordType = "2";
                isEmpty();
                break;
            //发送验证码
            case R.id.btn_sendregistercode:
                getUUID();
                break;
            //验证码修改密码
            case R.id.btn_registercodeupdate:
                PasswordType="1";
                isEmpty();
                break;
            default:
                break;
        }
    }

    private void isEmpty() {
        if(et_oldpassword.getText().toString().equals("")||et_passwordok.getText().toString().equals("")||et_newpassword.getText().toString().equals("")){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请填写完整信息！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    return;
                        }
                    }).setCancelable(false).show();
        }else{
            updatePasswordNetwork();
        }
    }

    private void updatePasswordNetwork() {
        dismissProgressDialog();
        showProgressDialog(UpdatePasswordActivity.this,"保存中...");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "passwordUpdate");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID","OldPassword","NewPassword","PasswordType"};
        String value[] = {UserID,et_oldpassword.getText().toString(),et_passwordok.getText().toString(),PasswordType};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    private void getUUID() {
        dismissProgressDialog();
        showProgressDialog(UpdatePasswordActivity.this,"正在获取中...");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "returnCode");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID"};
        String value[] = {UserID};
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
            dataResult = dataDecode.decode(result, "ReturnTransactionMessage");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (realData.getResultcode().equals("1")) {
                    ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                    if(CurrentAction == currentNotiName){
                        if (msg.getResult().equals("1")) {
                            new AlertDialog.Builder(this)
                                    .setTitle("成功")
                                    .setMessage(msg.tip)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            et_oldpassword.setText("");
                                            et_passwordok.setText("");
                                            et_newpassword.setText("");
                                            return;
                                        }
                                    }).setCancelable(false).show();
                        }else{
                            new AlertDialog.Builder(this)
                                    .setTitle("失败")
                                    .setMessage("更改失败")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    }).setCancelable(false).show();
                        }
                    }else if(CurrentAction == currentNotiName1){
                        if (msg.getResult().equals("1")) {
                            ToastUtil.show(getApplicationContext(),msg.tip);
                        }else{
                            ToastUtil.show(getApplicationContext(),msg.tip+"，请重新发送");
                        }
                    }
                }else{
                    DefaultTip(UpdatePasswordActivity.this, "暂无数据");
                }
            } else {
                DefaultTip(UpdatePasswordActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(UpdatePasswordActivity.this, "网络获取数据失败");
        }
    }


}
