package com.beautyhealthapp.PersonHealth.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.LocationEntity.AlarmInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2016/1/5.
 */
public class SetAlarmActivity extends NavBarActivity implements OnClickListener {
    private ISqlHelper iSqlHelper;
    private Button alarmSaveBtn, alarmStopBtn, alarmDeleteBtn, musicSelectBtn;
    private EditText tipTitleEdt, timeSpanEdt, tipCountEdt;
    private String musicPath="";
    private final String tag_intent = "HAVE_ALERMID";
    private final String tag_alarmobj = "ALERMOBJ";
    private AlarmInfo alarmInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setalarm);
        setRightpicID(R.mipmap.menu_help);
        initNavBar("提醒设置", true, true);
        iSqlHelper = new SqliteHelper(null, getApplicationContext());
        fetchUIFromLayout();
        setListener();
        loadAlarm();
    }

    private void fetchUIFromLayout() {
        alarmSaveBtn = (Button) findViewById(R.id.alarm_save);
        alarmStopBtn = (Button) findViewById(R.id.alarm_stop);
        alarmDeleteBtn = (Button) findViewById(R.id.alarm_delete);
        tipTitleEdt = (EditText) findViewById(R.id.tipTitle);
        timeSpanEdt = (EditText) findViewById(R.id.timespan);
        tipCountEdt = (EditText) findViewById(R.id.tipCount);
        musicSelectBtn = (Button) findViewById(R.id.btn_selectMusic);
    }

    private void setListener() {
        alarmSaveBtn.setOnClickListener(this);
        alarmStopBtn.setOnClickListener(this);
        alarmDeleteBtn.setOnClickListener(this);
        musicSelectBtn.setOnClickListener(this);
    }

    private void loadAlarm() {
        Intent intent = this.getIntent();
        boolean haveAlarm = intent.getBooleanExtra(tag_intent, false);
        if (haveAlarm) {
            alarmInfo = (AlarmInfo) intent.getSerializableExtra(tag_alarmobj);
            tipTitleEdt.setText(alarmInfo.title);
            timeSpanEdt.setText(alarmInfo.timespan);
            tipCountEdt.setText(alarmInfo.count);
            if (alarmInfo.enabled.equals("0"))
                alarmStopBtn.setText("启用");
            else alarmStopBtn.setText("停用");
            if (alarmInfo.musicfile.equals(""))
                musicSelectBtn.setText("默认铃声（点击设置）");
            else musicSelectBtn.setText(getfileName(alarmInfo.musicfile) + "（点击设置）");
        } else musicSelectBtn.setText("默认铃声（点击设置）");
    }

    @Override
    public void onClick(View v) {
        if (v == musicSelectBtn) {
            Intent _intent = new Intent();
            _intent.setClass(getApplicationContext(), MusicSelectActivity.class);
            startActivityForResult(_intent, 1);
        }else if (v == alarmSaveBtn) {
            alarmSaveAction();
        }else if (v == alarmStopBtn) {
            alarmStopAction();
        }else if (v == alarmDeleteBtn) {
            alarmDeleteAction();
        }
    }

    private void alarmSaveAction() {
        if(tipTitleEdt.getText().toString().equals("")||timeSpanEdt.getText().toString().equals("")
                ||tipCountEdt.getText().toString().equals("")){
            ToastUtil.show(getApplicationContext(), "请填写闹钟信息");
            return;
        }else {
            if(!(0<Integer.valueOf(timeSpanEdt.getText().toString().trim())
                    &&Integer.valueOf(timeSpanEdt.getText().toString().trim())<=2048)){
                Toast.makeText(getApplicationContext(), "提醒间隔输入不合法", Toast.LENGTH_LONG).show();
                return;
            }else if(!(0<Integer.valueOf(tipCountEdt.getText().toString().trim())
                    &&Integer.valueOf(tipCountEdt.getText().toString().trim())<=2048)){
                Toast.makeText(getApplicationContext(), "提醒次数输入不合法", Toast.LENGTH_LONG).show();
                return;
            }else {
                iSqlHelper.CreateTable("com.LocationEntity.AlarmInfo");
                String identity = java.util.UUID.randomUUID().toString();
                AlarmInfo alarmInfoitem = new AlarmInfo();
                alarmInfoitem.title = tipTitleEdt.getText().toString();
                alarmInfoitem.timespan = timeSpanEdt.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String StartTime = formatter.format(curDate);
                alarmInfoitem.starttime = StartTime;
                alarmInfoitem.count = tipCountEdt.getText().toString();
                alarmInfoitem.enabled = "1";
                alarmInfoitem.sampleCount = "0";
                alarmInfoitem.musicfile = musicPath;
                if (alarmInfo != null) {
                    alarmInfoitem.AutoID = alarmInfo.AutoID;
                    alarmInfoitem.alertID = alarmInfo.alertID;
                    String strsqlValue = "enabled='"+alarmInfoitem.enabled+"',timespan='"+alarmInfoitem.timespan+"',"
                            +"count='"+alarmInfoitem.count+"',starttime='"+alarmInfoitem.starttime+"',"
                            +"title='"+alarmInfoitem.title+"',sampleCount='"+alarmInfoitem.sampleCount+"',musicfile='"+alarmInfoitem.musicfile+"'";
                    iSqlHelper.SQLExec("update AlarmInfo set " + strsqlValue + "where alertID = '" + alarmInfoitem.alertID+"'");
                    //iSqlHelper.Update(alarmInfoitem);
                }else{
                    alarmInfoitem.alertID = identity;
                    iSqlHelper.Insert(alarmInfoitem);
                }
                alarmInfoitem.cancelRepeatAlarm(getApplicationContext(),AlarmActivity.class);
                alarmInfoitem.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
                ToastUtil.show(getApplicationContext(), "闹铃设置成功");
                finish();
            }
        }
    }

    private void alarmStopAction() {
        String result="";
        if (alarmInfo != null) {
            if(alarmStopBtn.getText().equals("停用")){
                alarmInfo.enabled="0";
                alarmStopBtn.setText("启用");
                alarmInfo.cancelRepeatAlarm(getApplicationContext(), AlarmActivity.class);
                result="闹钟暂停";
            }else if(alarmStopBtn.getText().equals("启用")){
                alarmInfo.enabled="1";
                alarmInfo.sampleCount="0";
                alarmStopBtn.setText("停用");
                alarmInfo.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
                result="闹钟已恢复";
            }
            //iSqlHelper.Update(alarmInfo);
            iSqlHelper.SQLExec("update AlarmInfo set enabled='"+alarmInfo.enabled+"',sampleCount='"+alarmInfo.sampleCount+"' where alertID='"+alarmInfo.alertID+"'");
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "您没有设置闹铃",Toast.LENGTH_LONG).show();
        }
    }

    private void alarmDeleteAction() {
        if (alarmInfo != null) {
            iSqlHelper.CreateTable("com.LocationEntity.AlarmInfo");
            alarmInfo.cancelRepeatAlarm(getApplicationContext(), AlarmActivity.class);
           // iSqlHelper.Delete(alarmInfo);
            iSqlHelper.SQLExec("delete from AlarmInfo where alertID='"+alarmInfo.alertID+"'");
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "您没有设置闹铃", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == -1) {
            String filePath = data.getStringExtra("fileName");
            musicSelectBtn.setText(getfileName(filePath) + "（点击设置）");
            musicPath = filePath;
        }
    }

    private String getfileName(String path) {
        String filename = "";
        String[] filenames = path.split("/");
        filename = filenames[filenames.length - 1];
        String[] names_load = filename.split("\\.");  // “.”和“|”都是转义字符，必须得加"\\"     如果有多个分隔符，可以用"|"作为连字符
        filename = names_load[0];
        if (filename.length() > 10)
            filename = filename.substring(0, 7) + "...";
        return filename;
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        new AlertDialog.Builder(this).setTitle("帮助")
                .setMessage(R.string.activeablehelp)
                .setPositiveButton("我知道了", null)
                .setCancelable(false)
                .show();
    }
}
