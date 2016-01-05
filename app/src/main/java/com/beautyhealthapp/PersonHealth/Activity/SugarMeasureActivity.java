package com.beautyhealthapp.PersonHealth.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by lenovo on 2016/1/4.
 */
public class SugarMeasureActivity extends DataRequestActivity implements OnClickListener {
    private String currentNotiName = "SugarMeasureNotifications";
    private Button btn_dataShow, btn_operGuide;
    private TextView dataValueShow;
    private ImageButton ib_befoedinner, ib_afterdinner, ib_earlymoring,
            ib_bedtime;
    private String bloodSugarValue = "0", measureTime = "xxxx-xx-xx yy:yy:yy",
            conclusion = "FF", UserID;
    private int timeType;
    private Animation animation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugarmeasure);
        Notifications.add(currentNotiName);
        initNavBar("血糖测量", true, false);
        fetchUIFromLayout();
        initValue();
    }

    private void fetchUIFromLayout() {
        ib_befoedinner = (ImageButton) findViewById(R.id.ib_befordinner);
        ib_afterdinner = (ImageButton) findViewById(R.id.ib_afterdinner);
        ib_earlymoring = (ImageButton) findViewById(R.id.ib_earlymorning);
        ib_bedtime = (ImageButton) findViewById(R.id.ib_bedtime);
        animation = AnimationUtils.loadAnimation(this, R.anim.btn_bs_anim);
        btn_dataShow = (Button) findViewById(R.id.btn_bloodSugar_dataShow);
        btn_operGuide = (Button) findViewById(R.id.btn_bloodSugar_operGuide);
        dataValueShow = (TextView) findViewById(R.id.bs_datashow);
        btn_dataShow.setOnClickListener(this);
        btn_operGuide.setOnClickListener(this);
        ib_befoedinner.setOnClickListener(this);
        ib_afterdinner.setOnClickListener(this);
        ib_earlymoring.setOnClickListener(this);
        ib_bedtime.setOnClickListener(this);
    }

    private void initValue() {
        bloodSugarValue = "0";
        measureTime = "xxxx-xx-xx yy:yy:yy";
        conclusion = "FF";
        dataValueShow.setText("血糖值: 00 mmol/l");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bloodSugar_dataShow:
                IsLoginTip(SugarDataShowActivity.class);
                break;
            case R.id.btn_bloodSugar_operGuide:
                Intent _intent = new Intent();
                _intent.setClass(SugarMeasureActivity.this, SugarOperGuideActivity.class);
                startActivity(_intent);
                break;
            case R.id.ib_befordinner:
                ib_befoedinner.startAnimation(animation);
                timeType = 0;
                dataMeasure();
                break;
            case R.id.ib_afterdinner:
                ib_afterdinner.startAnimation(animation);
                timeType = 1;
                dataMeasure();
                break;
            case R.id.ib_earlymorning:
                ib_earlymoring.startAnimation(animation);
                timeType = 2;
                dataMeasure();
                break;
            case R.id.ib_bedtime:
                ib_bedtime.startAnimation(animation);
                timeType = 3;
                dataMeasure();
                break;
            default:
                break;
        }
    }

    private void IsLoginTip(Class<?> cls) {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            jumpActivity(cls);
        } else {
            ToastUtil.show(getApplicationContext(), "亲~，请您先登录");
        }
    }

    private void jumpActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(SugarMeasureActivity.this, cls);
        startActivity(intent);
    }

    private void dataMeasure() {
        bloodSugarValue = launchDevice();
        conclusion = dataEvaluate(bloodSugarValue);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        measureTime = sDateFormat.format(new java.util.Date());
        if ((bloodSugarValue == "0" || bloodSugarValue == null)
                || (conclusion == "FF" || conclusion == null)) {
            ToastUtil.show(getApplicationContext(), "测量数据异常，建议您重新测量");
            return;
        } else {
            dataValueShow.setText("血糖值: " + bloodSugarValue + " mmol/l");
            ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
            List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
            if (list.size() > 0) {
                UserMessage userMessage = (UserMessage) list.get(0);
                UserID = userMessage.UserID;
                dataUpLoading();
            } else {
                ToastUtil.show(getApplicationContext(), "您处于离线状态,不能提交数据，请登录再试");
                return;
            }
        }
    }

    private String launchDevice() {
        showProgressDialog(SugarMeasureActivity.this, "测量中...");
        Random random1 = new Random();
        String ret = "" + (60 + random1.nextInt(10));
        dismissProgressDialog();
        return ret;

    }

    public String dataEvaluate(String Value) {
        String str_ret = null;
        if (Integer.valueOf(Value) > 40)
            str_ret = "严重";
        if (Integer.valueOf(Value) > 0 && Integer.valueOf(Value) < 40)
            str_ret = "正常";
        return str_ret;

    }

    private void dataUpLoading() {
        dismissProgressDialog();
        showProgressDialog(SugarMeasureActivity.this, "加载中");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("BloodService", "uploadBlood");
        Map requestCondition = new HashMap();
        String condition[] = {"Value", "MeasureTime", "Conclusion", "UserID"};
        String value[] = {bloodSugarValue, measureTime, conclusion, UserID};
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
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result, "ReturnTransactionMessage");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.getResult().size() > 0) {
                        ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                        if (msg.getResult().equals("1")) {
                            ToastUtil.show(getApplicationContext(), msg.getTip());
                            return;
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("失败")
                                    .setMessage(msg.getTip() + "请重新上传。")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dataUpLoading();
                                            return;
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    }).setCancelable(false).show();
                        }
                    } else {
                        ToastUtil.show(getApplicationContext(), "暂无数据");
                    }
                } else {
                    DefaultTip(SugarMeasureActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(SugarMeasureActivity.this, "网络获取数据失败");
        }
    }

}
