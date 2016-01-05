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
public class PressureMeasureActivity extends DataRequestActivity implements OnClickListener{
    private String currentNotiName = "PressureMeasureNotifications";
    private Button  btn_dataShow, btn_operGuide;
    private ImageButton btn_measure;
    private String hightPressure = "0", lowPressure = "0", heartRate = "0",
            measureTime = "xxxx-xx-xx yy:yy:yy", conclusion = "FF";
    private TextView lowpressureValue, highpressureValue, heartrateValue,
            timeshow;
    private String UserID;
    private Animation animation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressuremeasure);
        Notifications.add(currentNotiName);
        initNavBar("血压测量", true, false);
        fetchUIFromLayout();
        initValue();
    }

    private void fetchUIFromLayout() {
        btn_measure = (ImageButton) findViewById(R.id.btn_bloodPressure_measure);
        btn_dataShow = (Button) findViewById(R.id.btn_bloodPressure_dataShow);
        btn_operGuide = (Button) findViewById(R.id.btn_bloodPressure_operGuide);
        lowpressureValue = (TextView) findViewById(R.id.tv_lowpressure);
        highpressureValue = (TextView) findViewById(R.id.tv_highpressure);
        heartrateValue = (TextView) findViewById(R.id.tv_heartrate);
        animation = AnimationUtils.loadAnimation(this, R.anim.btn_bs_anim);
        btn_measure.setOnClickListener(this);
        btn_dataShow.setOnClickListener(this);
        btn_operGuide.setOnClickListener(this);
    }

    private void initValue() {
        hightPressure = "0";
        lowPressure = "0";
        heartRate = "0";
        lowpressureValue.setText("00");
        highpressureValue.setText("00");
        heartrateValue.setText("00");
        measureTime = "xxxx-xx-xx yy:yy:yy";
        conclusion = "FF";
    }

    @Override
    public void onClick(View v) {
        if (v == btn_measure) {
            btn_measure.startAnimation(animation);
            dataMeasure();
        }
        if (v == btn_dataShow) {
            IsLoginTip(PressureDataShowActivity.class);
        }
        if (v == btn_operGuide) {
            Intent _intent = new Intent();
            _intent.setClass(PressureMeasureActivity.this, PressureOperGuideActivity.class);
            startActivity(_intent);
        }
    }

    private void dataMeasure() {
        String[] bloodPressureData = launchDevice();
        hightPressure = bloodPressureData[0];
        lowPressure = bloodPressureData[1];
        heartRate = bloodPressureData[2];
        conclusion = dataEvaluate(hightPressure, lowPressure, heartRate);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        measureTime = sDateFormat.format(new java.util.Date());
        if ((hightPressure == "0" || hightPressure == null) || (lowPressure == "0" || lowPressure == null)
                || (heartRate == "0" || heartRate == null) || (conclusion == "FF" || conclusion == null)) {
            ToastUtil.show(getApplicationContext(), "测量数据异常，建议您重新测量");
            return;
        } else {
            lowpressureValue.setText(lowPressure);
            highpressureValue.setText(hightPressure);
            heartrateValue.setText(heartRate);
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

    private String[] launchDevice() {
        showProgressDialog(PressureMeasureActivity.this,"测量中...");
        Random random1 = new Random();
        String[] ret = { ""+(100+random1.nextInt(10)), ""+(80+random1.nextInt(10)), ""+(80+random1.nextInt(10))};
        dismissProgressDialog();
        return ret;
    }

    private String dataEvaluate(String hightPressure, String lowPressure, String heartRate) {
        int data = Integer.valueOf(hightPressure) - Integer.valueOf(lowPressure);
        String str_ret = null;
        if (data > 60) {
            str_ret = "严重";
        }
        if (data > 40 && data < 60) {
            str_ret = "一般";
        }
        if (data > 0 && data < 40) {
            str_ret = "正常";
        }
        return str_ret;
    }

    private void dataUpLoading() {
        dismissProgressDialog();
        showProgressDialog(PressureMeasureActivity.this, "加载中");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("PressureService", "uploadPressure");
        Map requestCondition = new HashMap();
        String condition[] = { "HightPressure", "LowPressure", "HeartRate", "MeasureTime", "Conclusion", "UserID" };
        String value[] = { hightPressure, lowPressure, heartRate, measureTime, conclusion, UserID };
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
                    DefaultTip(PressureMeasureActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(PressureMeasureActivity.this, "网络获取数据失败");
        }
    }

    private void IsLoginTip(Class<?> cls) {
        ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            jumpActivity(cls);
        }else{
            ToastUtil.show(getApplicationContext(), "亲~，请您先登录");
        }
    }

    private void jumpActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(PressureMeasureActivity.this, cls);
        startActivity(intent);
    }
}
