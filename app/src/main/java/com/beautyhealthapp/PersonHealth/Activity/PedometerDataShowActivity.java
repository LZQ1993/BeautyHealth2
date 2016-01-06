package com.beautyhealthapp.PersonHealth.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.Entity.ActionInfo;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.beautyhealthapp.SafeGuardianship.Assistant.SafeActionLine;
import com.beautyhealthapp.SafeGuardianship.Assistant.SafeActionPoint;
import com.beautyhealthapp.SafeGuardianship.Assistant.SafeActionView;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/6.
 */
public class PedometerDataShowActivity extends DataRequestActivity {
    private String currentNotiName = "PedometerDataShowNotifications";
    private SafeActionView mspchar;
    private List<ActionInfo> ActionData = new ArrayList<ActionInfo>();
    private List<SafeActionLine> Lines = new ArrayList<SafeActionLine>();
    private List<Integer> colors = new ArrayList<Integer>();
    private double YAxistMax = 0;
    private double YAxistMin = 0;
    private EditText startTimeEd, endTimeEd;
    private String UUID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifications.add(currentNotiName);
        setLandscape();
        set2DLines();
        setChart();
        initActivity();
        InitData();
    }

    private void setLandscape() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
    }

    // 修改这里
    private void set2DLines() {
        if (ActionData.size() > 0) {
            SafeActionLine heartValueLine = new SafeActionLine();
            heartValueLine.LineTitle = "步数";
            heartValueLine.XUnit = "时间";
            heartValueLine.YUnit = "步";
            heartValueLine.TimeSpan = "时长";
            for (int i = 0; i < ActionData.size(); i++) {
                ActionInfo ap = ActionData.get(i);
                SafeActionPoint heartValuePoint = new SafeActionPoint();
                heartValuePoint.AY = Double.valueOf(ap.Value);
                heartValuePoint.AX = i * 10;
                heartValuePoint.AXLabel = ap.MeasureTime;
                heartValuePoint.TimeSpan = ap.TimeSpan;

                if (i == 0) {
                    YAxistMax = Double.valueOf(ap.Value);
                    YAxistMin = Double.valueOf(ap.Value);
                } else {
                    setMaxValue(Double.valueOf(ap.Value));
                    setMinValue(Double.valueOf(ap.Value));
                }
                heartValueLine.SafeActionPoint.add(heartValuePoint);
            }
            Lines.add(heartValueLine);
        }
    }

    // 修改这里
    private void setChart() {
        colors.add(Color.rgb(54, 141, 238));
        colors.add(Color.rgb(255, 165, 132));
        colors.add(Color.rgb(84, 206, 231));
        mspchar = new SafeActionView(this, Lines, colors); // 平滑曲线图
        mspchar.getChart().setTitle("");
        // 数据轴最大值
        mspchar.getChart().getDataAxis().setAxisMax(Math.floor(YAxistMax * 1.4));
        mspchar.getChart().getDataAxis().setAxisMin(Math.floor(YAxistMin * 0.6));
        // 数据轴刻度间隔
        mspchar.getChart().getDataAxis().setAxisSteps(20);
        mspchar.getChart().getPlotGrid().getHorizontalLinePaint().setColor(Color.rgb(179, 147, 197));
    }

    private void setMinValue(double value) {
        if (YAxistMin > value) {
            YAxistMin = value;
        }
    }

    private void setMaxValue(double value) {
        if (YAxistMax < value) {
            YAxistMax = value;
        }
    }

    public void onResume() {
        super.onResume();
    }

    private void initActivity() {
        FrameLayout content = new FrameLayout(this);
        // 缩放控件放置在FrameLayout的上层，用于放大缩小图表
        FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameParm.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // 图表显示范围在占屏幕大小的98%的区域内
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scrWidth = (int) (dm.widthPixels);
        int scrHeight = (int) ((dm.heightPixels) / 8 * 7 + 5);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);
        // 居中显示
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        // 图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
        final RelativeLayout chartLayout = new RelativeLayout(this);
        chartLayout.addView(mspchar, layoutParams);
        // 增加控件
        ((ViewGroup) content).addView(chartLayout);
        // 增加一个按钮用于打开时间选择框
        setTimeSelected(content);
        setContentView(content);
    }

    private void setTimeSelected(FrameLayout content) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View convertView = inflater.inflate(R.layout.activity_action_navbar, null);
        DisplayMetrics dms = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((int) dms.widthPixels, (int) ((dms.heightPixels) / 8 * 1)); // 设置按钮的宽度和高度
        btParams.leftMargin = 0; // 横坐标定位
        btParams.topMargin = 0; // 纵坐标定位
        ((ViewGroup) content).addView(convertView, btParams);// 将按钮放入layout组件
    }

    private void InitData() {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UUID = userMessage.UUID;
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
            String Time = sDateFormat.format(new java.util.Date());
            String stime = Time + "-01 " + "00:00:00";
            String etime = Time + "-31 " + "23:59:59";
            dataUpLoading(stime, etime);
        } else {
            ToastUtil.show(getApplicationContext(), "亲~，请您先登录");
        }
    }

    private void dataUpLoading(String starttime, String endtime) {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("WalkActionService", "queryWalkAction");
        Map requestCondition = new HashMap();
        String condition[] = {"StartTime", "EndTime", "UserID", "page", "rows"};
        String value[] = {starttime, endtime, UUID, "-1", "18"};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    @Override
    public void updateView() {
        if (result != null) {
            dataResult = dataDecode.decode(result, "ActionInfo");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (CurrentAction == currentNotiName) {
                    if (realData.getResultcode().equals("1")) {
                        ActionData.clear();
                        Lines.clear();
                        colors.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            ActionInfo msg = (ActionInfo) realData.getResult().get(i);
                            ActionData.add(msg);
                        }
                        set2DLines();
                        setChart();
                        initActivity();
                    } else {
                        ToastUtil.show(getApplicationContext(), "暂无数据");
                    }
                }
            } else {
                ToastUtil.show(getApplicationContext(), "数据解析失败");
            }
        } else {
            ToastUtil.show(getApplicationContext(), "网络获取数据失败");
        }

    }

    public void onNavBarRightButtonClick(View view) {
        showDialog();
    }

    public void onNavBarLeftButtonClick(View view) {
        finish();
    }

    private void showDialog() {
        View itemview = getLayoutInflater().inflate(R.layout.showtimeseacherdialog, null);
        startTimeEd = (EditText) itemview.findViewById(R.id.ed_startTime);
        endTimeEd = (EditText) itemview.findViewById(R.id.ed_endTime);
        startTimeEd.setInputType(InputType.TYPE_NULL);
        endTimeEd.setInputType(InputType.TYPE_NULL);
        startTimeEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PedometerDataShowActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int month = (monthOfYear + 1);
                                String strdate = year + "-";
                                if (month < 10) {
                                    strdate = strdate + "0" + month + "-";
                                } else {
                                    strdate = strdate + month + "-";
                                }
                                if (dayOfMonth < 10) {
                                    strdate = strdate + "0" + dayOfMonth + " ";
                                } else {
                                    strdate = strdate + dayOfMonth + " ";
                                }
                                startTimeEd.setText(strdate + "00:00:00");
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endTimeEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PedometerDataShowActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int month = (monthOfYear + 1);
                                String strdate = year + "-";
                                if (month < 10) {
                                    strdate = strdate + "0" + month + "-";
                                } else {
                                    strdate = strdate + month + "-";
                                }
                                if (dayOfMonth < 10) {
                                    strdate = strdate + "0" + dayOfMonth + " ";
                                } else {
                                    strdate = strdate + dayOfMonth + " ";
                                }
                                endTimeEd.setText(strdate + "23:59:59");
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // 对话框
        new AlertDialog.Builder(PedometerDataShowActivity.this).setView(itemview)
                .setTitle("提示：输入条件")
                .setPositiveButton("确定", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String startTime = startTimeEd.getText().toString();
                        String endTime = endTimeEd.getText().toString();
                        dataUpLoading(startTime, endTime);
                    }
                }).setNegativeButton("取消", null).setCancelable(false) // 触摸不消失
                .show();
        return;
    }
}

