package com.beautyhealthapp.UserCenter.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.BindingMessage;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/28.
 */
public class BindUserActivity extends DataRequestActivity implements OnClickListener {
    private String currentNotiName = "BindUserNotifications";
    private String currentNotiName1 = "CancelBindNotifications";
    private String currentNotiName2 = "UpdateBindNotifications";
    private EditText et_registercode;
    private Button btn_addbinding;
    private Button btn_cancelbinding;
    private Button btn_synchronousbinding;
    private String UserID;
    private String cancelCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binduser);
        Notifications.add(currentNotiName);
        Notifications.add(currentNotiName1);
        Notifications.add(currentNotiName2);
        initNavBar("绑定被监护人", true, false);
        fetchUIFromLayout();
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
        } else {
            UserID = "0000000";
        }
    }

    private void fetchUIFromLayout() {
        et_registercode = (EditText) findViewById(R.id.et_registercode);
        btn_addbinding = (Button) findViewById(R.id.btn_addbinding);
        btn_cancelbinding = (Button) findViewById(R.id.btn_cancelbinding);
        btn_synchronousbinding = (Button) findViewById(R.id.btn_synchronousbinding);
        btn_addbinding.setOnClickListener(this);
        btn_cancelbinding.setOnClickListener(this);
        btn_synchronousbinding.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addbinding:
                codeIsEmptyTip();
                break;
            case R.id.btn_cancelbinding:
                cancelBindAction();
                break;
            //在服务器端将绑定的信息同步到本地
            case R.id.btn_synchronousbinding:
                bindUpdateOnNetwork();
                break;
            default:
                break;
        }

    }

    private void codeIsEmptyTip() {
        if (et_registercode.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请填写完整信息！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).setCancelable(false).show();
        } else {
            bindUserNetwork();
        }
    }

    private void bindUserNetwork() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "bindUnderGuargeUser");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID", "UnderGuardUserID"};
        String value[] = {UserID, et_registercode.getText().toString()};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
        dismissProgressDialog();
        showProgressDialog(BindUserActivity.this, "正在绑定...");
    }

    private void cancelBindAction() {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> ls = iSqlHelper.Query("com.LocationEntity.BindingMessage", "UserID='" + UserID + "'");
        if (ls.size() > 0) {
            String[] message = new String[ls.size()];
            final String[] UnderGuardUserIDTip = new String[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                BindingMessage bm = (BindingMessage) ls.get(i);
                message[i] = bm.UserName;
                UnderGuardUserIDTip[i] = bm.UnderGuardUserID;
            }
            new AlertDialog.Builder(this)
                    .setTitle("选择要取消绑定的姓名：")
                    .setItems(message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            cancelCode = UnderGuardUserIDTip[arg1];
                            cancelBindNetwork(cancelCode);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            new AlertDialog.Builder(BindUserActivity.this)
                    .setTitle("提示")
                    .setMessage("无绑定数据")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }
    }

    private void cancelBindNetwork(String tip) {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "bindCancelUnderGuargeUser");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID", "UnderGuardUserID"};
        String value[] = {UserID, tip};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName1);
        setRequestUtility(myru);
        requestData();
        dismissProgressDialog();
        showProgressDialog(BindUserActivity.this, "正在解除绑定...");
    }

    private void bindUpdateOnNetwork() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "bindUpdateUnderGuargeUser");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID"};
        String value[] = {UserID};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName2);
        setRequestUtility(myru);
        requestData();
        dismissProgressDialog();
        showProgressDialog(BindUserActivity.this, "同步中...");
    }

    @Override
    public void updateView() {
        dismissProgressDialog();
        if (result != null) {
            dataResult = dataDecode.decode(result, "ReturnTransactionMessage");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (realData.getResultcode().equals("1")&&realData.getResult().size()>0) {
                    if (CurrentAction == currentNotiName) {
                        ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                        if (!msg.getResult().equals("0")) {
                            BindingMessage bindingMessage = new BindingMessage();
                            bindingMessage.UnderGuardUserID = msg.result;
                            bindingMessage.UserID = UserID;
                            bindingMessage.UserName = msg.tip;
                            ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                            List<Object> ls = iSqlHelper.Query("com.LocationEntity.BindingMessage", "UnderGuardUserID = '" + msg.result + "' and UserID ='"+UserID+"'");
                            if (ls.size() == 0) {
                                iSqlHelper.Insert(bindingMessage);
                            } else {
                                iSqlHelper.SQLExec("update BindingMessage set UserName = '" + msg.tip + "' where UnderGuardUserID = '" + msg.result + "'");
                            }
                            new AlertDialog.Builder(this)
                                    .setTitle("成功")
                                    .setMessage("绑定成功")
                                    .setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    et_registercode.setText("");
                                                    return;
                                                }
                                            })
                                    .setCancelable(false).show();
                            return;
                        }else{
                            new AlertDialog.Builder(this)
                                    .setTitle("提示")
                                    .setMessage(msg.tip)
                                    .setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    et_registercode.setText("");
                                                    return;
                                                }
                                            })
                                    .setCancelable(false).show();
                        }
                    } else if (CurrentAction == currentNotiName1) {
                        ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                        if (msg.getResult().equals("1")) {
                            ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                            iSqlHelper.SQLExec("delete from BindingMessage where UnderGuardUserID = '" + cancelCode + "'");
                            new AlertDialog.Builder(BindUserActivity.this)
                                    .setTitle("提示")
                                    .setMessage(msg.tip)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    }).setCancelable(false).show();
                        } else {
                            new AlertDialog.Builder(BindUserActivity.this)
                                    .setTitle("错误")
                                    .setMessage(msg.tip)
                                    .setPositiveButton("确定", null)
                                    .show();
                            return;
                        }

                    } else if (CurrentAction == currentNotiName2) {
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(i);
                            BindingMessage bindingMessage = new BindingMessage();
                            bindingMessage.UnderGuardUserID = msg.result;
                            bindingMessage.UserID = UserID;
                            bindingMessage.UserName = msg.tip;
                            ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                            List<Object> ls = iSqlHelper.Query("com.LocationEntity.BindingMessage", "UnderGuardUserID = '" + msg.result + "'");
                            if (ls.size() == 0) {
                                iSqlHelper.Insert(bindingMessage);
                            } else {
                                iSqlHelper.SQLExec("update BindingMessage set UserName = '" + msg.tip + "' where UnderGuardUserID = '" + msg.result + "'");
                            }
                        }
                        new AlertDialog.Builder(BindUserActivity.this)
                                .setTitle("成功")
                                .setMessage("绑定数据同步成功")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                return;
                                            }
                                        })
                                .setCancelable(false).show();
                    }
                } else {
                    DefaultTip(BindUserActivity.this, "暂无数据");
                }
            } else {
                DefaultTip(BindUserActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(BindUserActivity.this, "网络获取数据失败");
        }
    }

}
