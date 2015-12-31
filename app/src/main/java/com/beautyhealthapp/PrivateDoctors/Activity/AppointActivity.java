package com.beautyhealthapp.PrivateDoctors.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.Entity.DoctorInfo;
import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.LoadPicTask;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/26.
 */
public class AppointActivity extends DataRequestActivity implements OnClickListener{
    private String currentNotiName = "AppiontDoctorNotifications";
    private DoctorInfo doctorInfo;
    private TextView tv_ap_doctorName, tv_ap_hospitalName, tv_ap_className;
    private EditText et_ap_address, et_ap_user, et_ap_telNum, et_ap_briefly;
    private Button btn_ap_appoint;
    private RadioGroup rg_ap_time;
    private String AppointTime;
    private ImageView iv_ap_doctorPic;
    private String UserID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint);
        Notifications.add(currentNotiName);
        doctorInfo = (DoctorInfo) getIntent().getSerializableExtra("DoctorInfo");
        initNavBar("预约专家", true,false);
        fetchUIFromLayout();
        setListener();
    }

    private void fetchUIFromLayout() {

        tv_ap_doctorName = (TextView) findViewById(R.id.tv_ap_doctorName);
        tv_ap_doctorName.setText(doctorInfo.DoctorName);
        tv_ap_hospitalName = (TextView) findViewById(R.id.tv_ap_hospitalName);
        tv_ap_hospitalName.setText(getIntent().getStringExtra("HospitalName"));
        tv_ap_className = (TextView) findViewById(R.id.tv_ap_className);
        tv_ap_className.setText(doctorInfo.ClassName);

        et_ap_address = (EditText) findViewById(R.id.et_ap_address);
        et_ap_user = (EditText) findViewById(R.id.et_ap_user);
        et_ap_telNum = (EditText) findViewById(R.id.et_ap_telNum);
        et_ap_briefly = (EditText) findViewById(R.id.et_ap_briefly);

        iv_ap_doctorPic = (ImageView) findViewById(R.id.iv_ap_doctorPic);
        String path = doctorInfo.DoctorImgUrl;
        LoadPicTask lpt = new LoadPicTask(iv_ap_doctorPic);
        lpt.setDefaultPic(R.mipmap.userphoto);
        if (path.length() > 0) {
            String webaddrss = NetworkSetInfo.getServiceUrl()+path.substring(2, doctorInfo.DoctorImgUrl.length());
            lpt.execute(webaddrss);
        }else{
            iv_ap_doctorPic.setImageResource(R.mipmap.userphoto);
        }
        rg_ap_time = (RadioGroup) findViewById(R.id.rg_ap_time);
        AppointTime = "2";
        btn_ap_appoint = (Button) findViewById(R.id.btn_ap_appoint);
    }

    private void setListener() {
        btn_ap_appoint.setOnClickListener((OnClickListener) this);
        rg_ap_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_ap_time1) {
                    AppointTime = "1";
                }
                if (checkedId == R.id.rb_ap_time2) {
                    AppointTime = "2";
                }
                if (checkedId == R.id.rb_ap_time3) {
                    AppointTime = "3";
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ap_appoint) {
            InfoIsNullTip();
        }
    }

    private void InfoIsNullTip() {
        if (AppointTime == null
                || (et_ap_address.getText().toString().equals(""))
                || (et_ap_user.getText().toString().equals(""))
                || (et_ap_telNum.getText().toString().equals(""))
                || (et_ap_briefly.getText().toString().equals(""))){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("亲~请您填写全部信息，以便于联系您")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).setCancelable(false).show();
        } else {
            UserIsLoginTip();
        }
    }

    private void UserIsLoginTip() {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
            UpLoadData();
        } else {
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("您处于离线状态,不能提交数据，请登录再试")
                    .setPositiveButton("确定", null).setCancelable(false)
                    .show();
        }
    }

    private void UpLoadData() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("HandyDoctorService", "appointDoctor");
        Map requestCondition = new HashMap();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String SubmitTime = sDateFormat.format(new java.util.Date());
        String condition[] = { "UserID", "DoctorID", "LinkUserName", "Address", "TelNum", "Briefly", "AppointTime", "SubmitTime" };
        String value[] = { UserID, doctorInfo.DoctorID, et_ap_user.getText().toString(), et_ap_address.getText().toString(),
                et_ap_telNum.getText().toString(), et_ap_briefly.getText().toString(), AppointTime, SubmitTime };
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
                                        finish();
                                        return;
                                    }
                                })
                                .setCancelable(false).show();
                    }
                }else{
                    DefaultTip(AppointActivity.this, "暂无数据");
                }
            } else {
                DefaultTip(AppointActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(AppointActivity.this, "网络获取数据失败");
        }
    }
}
