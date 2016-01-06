package com.beautyhealthapp.PersonHealth.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.PersonHealth.Assistant.Circlebar;
import com.beautyhealthapp.PersonHealth.Assistant.StepDetector;
import com.beautyhealthapp.PersonHealth.Assistant.StepService;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/4.
 */
public class PedometerActivity extends DataRequestActivity implements OnClickListener{
    private String currentNotiName = "StepSubmitNotifications";
    private Circlebar circleBar;
    private Button btn_start,btn_stop,btn_stepdatashow;
    private TextView tv_timer;// 运行时间
    private int total_step;
    private String Value = null;
    private String MeasureTime = null;
    private String TimeSpan = null;
    private Calendar c = Calendar.getInstance();
    private int myear,mmonth, mday,mHour, mMinute,msecond;
    private String month,day, Hour,Minute,second;
    private Thread thread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_upload);
        initNavBar("计步器", true, true);
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        circleBar = (Circlebar) this.findViewById(R.id.progress_pedometer);
        btn_start = (Button) findViewById(R.id.start);
        btn_stop = (Button) findViewById(R.id.stop);
        tv_timer = (TextView) findViewById(R.id.usetime);
        btn_stepdatashow = (Button) findViewById(R.id.btn_stepdatashow);
        total_step = StepDetector.CURRENT_SETP;
        circleBar.setProgress(total_step);
        circleBar.startCustomAnimation();// 开启动画
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_stepdatashow.setOnClickListener(this);
        if (StepService.flag) {
            btn_start.setEnabled(false);
            btn_stop.setEnabled(true);
        }
        if (!StepService.flag & StepDetector.CURRENT_SETP > 0) {
            btn_start.setEnabled(true);
            btn_stop.setBackground(getResources().getDrawable(R.mipmap.reset_enable));
            btn_stop.setEnabled(true);
            tv_timer.setText(timeRun(StepDetector.timer));
        }
        mThread();
    }

    @Override
    public void onClick(View v) {
        Intent service = new Intent(PedometerActivity.this, StepService.class);
        switch (v.getId()) {
            case R.id.start:
                startService(service);
                StepDetector.starttime = System.currentTimeMillis();
                StepDetector.tempTime = StepDetector.timer;
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                btn_stop.setBackground(getResources().getDrawable(R.drawable.btn_step_stop));
                break;
            case R.id.stop:
                stopService(service);
                if (StepService.flag && StepDetector.CURRENT_SETP > 0) {
                    btn_stop.setBackground(getResources().getDrawable(R.mipmap.reset_enable));
                } else {
                    StepDetector.CURRENT_SETP = 0;
                    StepDetector.starttime = 0;
                    StepDetector.tempTime = StepDetector.timer = 0;
                    circleBar.setProgress(0);
                    tv_timer.setText(timeRun(StepDetector.timer));
                    btn_stop.setBackground(getResources().getDrawable(R.drawable.btn_step_stop));
                    btn_stop.setEnabled(false);
                }
                btn_start.setEnabled(true);
                break;
            case R.id.btn_stepdatashow:
                IsLoginTip(PedometerDataShowActivity.class);
                break;
            default:break;
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
        intent.setClass(PedometerActivity.this, cls);
        startActivity(intent);
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            uploadStepData(view, userMessage.UserID);
        }else{
            ToastUtil.show(getApplicationContext(), "亲~，请您先登录");
        }
    }

    private void uploadStepData(View view, String userID) {
        if (StepService.flag) {
            ToastUtil.show(getApplicationContext(), "请先暂停");
        } else if (StepDetector.CURRENT_SETP == 0) {
            ToastUtil.show(getApplicationContext(), "步数为0，无效信息");
        } else {
            dataSubmitNetwork(userID);
        }

    }

    private void dataSubmitNetwork(String userID) {
        dismissProgressDialog();
        showProgressDialog(PedometerActivity.this, "加载中");
        Value = StepDetector.CURRENT_SETP + "";
        MeasureTime = getFormatTime();
        TimeSpan = getTimeSpan(StepDetector.timer);
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("WalkActionService", "uploadWalkAction");
        Map requestCondition = new HashMap();
        String condition[] = { "Value", "MeasureTime", "TimeSpan", "UserID" };
        String value[] = { Value, MeasureTime, TimeSpan, userID };
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
                            StepDetector.CURRENT_SETP = 0;
                            circleBar.setProgress(0);
                            btn_stop.setEnabled(false);
                            btn_stop.setBackground(getResources().getDrawable(R.drawable.btn_step_stop));
                            StepDetector.tempTime = StepDetector.timer = 0;
                            tv_timer.setText(timeRun(StepDetector.timer));
                            ToastUtil.show(getApplicationContext(), msg.getTip());
                            return;
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("失败")
                                    .setMessage(msg.getTip() + "请重新上传。")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
                    DefaultTip(PedometerActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(PedometerActivity.this, "网络获取数据失败");
        }
    }

    public String getFormatTime() {
        c.setTimeInMillis(System.currentTimeMillis());
        myear = c.get(Calendar.YEAR);
        mmonth = c.get(Calendar.MONTH) + 1;
        if (mmonth < 10) {
            month = "0" + mmonth + "";
        } else {
            month = mmonth + "";
        }
        mday = c.get(Calendar.DATE);
        if (mday < 10) {
            day = "0" + mday + "";
        } else {
            day = mday + "";
        }
        mHour = c.get(Calendar.HOUR_OF_DAY);
        if (mHour < 10) {
            Hour = "0" + mHour + "";
        } else {
            Hour = mHour + "";
        }
        mMinute = c.get(Calendar.MINUTE);
        if (mMinute < 10) {
            Minute = "0" + mMinute + "";
        } else {
            Minute = mMinute + "";
        }
        msecond = c.get(Calendar.SECOND);
        if (msecond < 10) {
            second = "0" + msecond + "";
        } else {
            second = msecond + "";
        }
        String time = myear + "-" + month + "-" + day + " " + Hour + ":"
                + Minute + ":" + second;
        return time;
    }

    private String timeRun(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;
        // 秒显示两位
        String strSecond = ("00" + second).substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute).substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);
        return strHour + ":" + strMinute + ":" + strSecond;
    }

    private String getTimeSpan(long time) {
        time = time / 1000;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;
        int TimeSpan = (int) (minute + hour * 60);
        String timespan = Integer.toString(TimeSpan);
        return timespan;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            total_step = StepDetector.CURRENT_SETP;
            System.out.println(total_step);
            circleBar.setProgress(total_step);
            tv_timer.setText(timeRun(StepDetector.timer));

        }
    };

    private void mThread() {
        if (thread == null) {
            thread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (StepService.flag) {
                            Message msg = new Message();
                            if (StepDetector.starttime != System.currentTimeMillis()) {
                                StepDetector.timer = StepDetector.tempTime + System.currentTimeMillis() - StepDetector.starttime;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
            });
            thread.start();
        }
    }


}
