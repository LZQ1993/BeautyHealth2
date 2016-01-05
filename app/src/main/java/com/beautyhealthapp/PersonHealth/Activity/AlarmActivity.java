package com.beautyhealthapp.PersonHealth.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.Entity.FamilyNumberMessage;
import com.LocationEntity.AlarmInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWMobileDevice.AbsMobilePhone;
import com.infrastructure.CWMobileDevice.MobilePhone422;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by lenovo on 2016/1/5.
 */
public class AlarmActivity extends Activity {
    private WakeLock mWakelock;
    private MediaPlayer alarmMusic;
    private ISqlHelper iSqlHelper;
    private AbsMobilePhone smssender;
    private String AlarmID;
    private AlarmInfo alarm;
    private MyCount mc;
    private int count = 0;//总数
    private int sampleCount = 0;//一提醒次数
    private Button stop;
    private TextView title, times;
    private String message="您的亲人长时间处于非活动状态，请您多加留意。";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setWinWake();
        iSqlHelper = new SqliteHelper(null, getApplicationContext());
        smssender = new MobilePhone422();
        getAlarmSample();
        setTimer();
        if (alarm != null) {
            loadMustic();
            //设置计数器加1
            count = Integer.parseInt(alarm.count);
            sampleCount = Integer.parseInt(alarm.sampleCount);
            sampleCount++;
            alarm.sampleCount = String.valueOf(sampleCount);
            iSqlHelper.Update(alarm);// 更新数据库samplecount
            title = (TextView) findViewById(R.id.title);
            title.setText(alarm.title);
            times = (TextView) findViewById(R.id.times);
            times.setText("已提醒：" + alarm.sampleCount + "次");
        } else {
            AlarmActivity.this.finish();
        }
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mc.cancel();
                alarmMusic.stop();
                alarmMusic.release();
                alarm.sampleCount = "0";
                sampleCount = 0;
                alarm.enabled = "1";
                iSqlHelper.Update(alarm);// 更新数据库samplecount
                alarm.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
                AlarmActivity.this.finish();
            }
        });
    }

    private void setWinWake() {
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }


    private void getAlarmSample() {
        Intent _intent = getIntent();
        AlarmID = _intent.getAction();
        if (AlarmID != null) {
            //取数据库值
            String wheres = " alertID='" + AlarmID + "'";
            List<Object> alarmList = iSqlHelper.Query("com.LocationEntity.AlarmInfo", wheres);
            if (alarmList.size() > 0) {
                alarm = (AlarmInfo) alarmList.get(0);
            }
        }
    }

    private void setTimer() {
        mc = new MyCount(60000, 60000);  //////warning
        mc.start();
    }

    private void loadMustic() {
        try {
			/* 重置MediaPlayer */
            alarmMusic = new MediaPlayer();
            if (alarm.musicfile.equals("") || alarm.musicfile.equals("默认铃声")) {
                // 加载指定音乐，并为之创建MediaPlayer对象
                alarmMusic = MediaPlayer.create(this, R.raw.alarm);
            } else {
				/* 设置要播放的文件的路径 */
                alarmMusic.setDataSource(alarm.musicfile);
				/* 准备播放 */
                alarmMusic.prepare();
                alarmMusic.setLooping(true);
            }
			/* 开始播放 */
            alarmMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakelock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();
    }

    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            alarmMusic.stop(); // 停止
            alarmMusic.release();
            if (alarm != null) {
                if (count <= sampleCount) {
                    List<Object> numberMessage = iSqlHelper.Query("com.LocationEntity.LocalFamilyNum", null);
                    if (numberMessage.size() > 0) {
                        for (int i = 0; i < numberMessage.size(); i++) {
                            FamilyNumberMessage fnm = (FamilyNumberMessage) numberMessage.get(i);
                            if (!fnm.Tel.equals("")) {
                                smssender.sendMessage(fnm.Tel, message + "(" + alarm.title + "  已提醒：" + alarm.sampleCount + "次" + ")");
                            }
                        }
                        mc.cancel();
                        sampleCount = 0;
                        alarm.enabled = "1";
                        alarm.sampleCount = "0";
                        alarm.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
                        iSqlHelper.Update(alarm);// 更新数据库samplecount
                        AlarmActivity.this.finish();
                    }
                    alarm.sampleCount = "0";
                }
                iSqlHelper.Update(alarm);// 更新数据库samplecount
            }
            AlarmActivity.this.finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

}