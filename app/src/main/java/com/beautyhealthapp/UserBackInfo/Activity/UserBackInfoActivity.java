package com.beautyhealthapp.UserBackInfo.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.Entity.ReturnTransactionMessage;
import com.Entity.UserMessage;
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
 * Created by lenovo on 2015/12/27.
 */
public class UserBackInfoActivity extends DataRequestActivity implements OnClickListener{
    private String currentNotiName = "UserBackInfoNotifications";
    private EditText userbackinfoEdt;
    private Button dataSubmitBtn;
    private String UserID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userbackinfo);
        Notifications.add(currentNotiName);
        initNavBar("用户反馈", true, false);
        fetchUIFromLayout();
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.Entity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
        }else{
            UserID="0000000";
        }
    }

    private void fetchUIFromLayout() {
        userbackinfoEdt = (EditText) findViewById(R.id.ed_userbackinfo);
        dataSubmitBtn = (Button) findViewById(R.id.btn_dataSubmit);
        dataSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == dataSubmitBtn) {
            InfoIsNullTip();
        }
    }

    private void InfoIsNullTip() {
        if(userbackinfoEdt.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("亲，您还未填写任何反馈信息！")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        } else {
            dataSubmit();
        }
    }

    private void dataSubmit() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("UserManagerService", "uploadUserSuggest");
        Map requestCondition = new HashMap();
        String condition[] = { "Description","UserID" };
        String value[] = { userbackinfoEdt.getText().toString(), UserID };
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
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
                    if (msg.getResult().equals("0")) {
                        new AlertDialog.Builder(this)
                                .setTitle("失败")
                                .setMessage(msg.getTip() + "请重新上传。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                }).setCancelable(false).show();
                    }else{
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage(msg.getTip())
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userbackinfoEdt.setText("");
                                        return;
                                    }
                                }).setCancelable(false).show();
                    }
                }else{
                    DefaultTip(UserBackInfoActivity.this, "暂无数据");
                }
            } else {
                DefaultTip(UserBackInfoActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(UserBackInfoActivity.this, "网络获取数据失败");
        }
    }

}
