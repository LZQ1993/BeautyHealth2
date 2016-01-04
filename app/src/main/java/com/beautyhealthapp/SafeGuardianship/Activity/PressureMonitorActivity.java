package com.beautyhealthapp.SafeGuardianship.Activity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.Entity.BloodPressureInfo;
import com.LocationEntity.BindingMessage;
import com.beautyhealthapp.R;
import com.beautyhealthapp.SafeGuardianship.Assistant.SplineChartView;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWAssistant.CWChartLine2D;
import com.infrastructure.CWAssistant.CWChartPoint2D;
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
 * Created by lenovo on 2016/1/3.
 */
public class PressureMonitorActivity extends DataRequestActivity{
    private String currentNotiName = "PressureMonitorDataNotifications";
    private SplineChartView mspchar;
    private List<BloodPressureInfo> testDataPressure= new ArrayList<BloodPressureInfo>();
    private List<CWChartLine2D> Lines = new ArrayList<CWChartLine2D>();
    private List<Integer> colors = new ArrayList<Integer>();
    private double YAxistMax = 0;
    private double YAxistMin = 0;
    private EditText startTime,endTime;
    private Spinner spUserName;
    private String UUID;
    private String[] strsUserName = null;
    private String[] strsUUID = null;
    private ArrayAdapter<String> adapter;

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
        if (testDataPressure.size() > 0) {
            CWChartLine2D heartValueLine = new CWChartLine2D();
            heartValueLine.LineTitle = "心率";
            heartValueLine.XUnit = "时间";
            heartValueLine.YUnit = "次数";

            CWChartLine2D hightValueLine = new CWChartLine2D();
            hightValueLine.LineTitle = "高压";
            hightValueLine.XUnit = "时间";
            hightValueLine.YUnit = "mms/l";

            CWChartLine2D lowValueLine = new CWChartLine2D();
            lowValueLine.LineTitle = "低压";
            lowValueLine.XUnit = "时间";
            lowValueLine.YUnit = "mms/l";

            for (int i = 0; i < testDataPressure.size(); i++) {
                BloodPressureInfo ap = testDataPressure.get(i);
                CWChartPoint2D heartValuePoint = new CWChartPoint2D();
                heartValuePoint.AY = Double.valueOf(ap.HeartRate);
                heartValuePoint.AX = i * 10;
                heartValuePoint.AXLabel = ap.MeasureTime;

                CWChartPoint2D hightValuePoint = new CWChartPoint2D();
                hightValuePoint.AY = Double.valueOf(ap.HightPressure);
                hightValuePoint.AX = i * 10;
                hightValuePoint.AXLabel = ap.MeasureTime;

                CWChartPoint2D lowValuePoint = new CWChartPoint2D();
                lowValuePoint.AY = Double.valueOf(ap.LowPressure);
                lowValuePoint.AX = i * 10;
                lowValuePoint.AXLabel = ap.MeasureTime;

                if (i == 0) {
                    if (Double.valueOf(ap.HightPressure)> Double.valueOf(ap.HeartRate) ){
                        YAxistMax = Double.valueOf(ap.HightPressure);
                    } else {
                        YAxistMax = Double.valueOf(ap.HeartRate);
                    }
                    if (Double.valueOf(ap.LowPressure) < Double.valueOf(ap.HeartRate)) {
                        YAxistMin = Double.valueOf(ap.LowPressure);
                    } else {
                        YAxistMin = Double.valueOf(ap.HeartRate);
                    }

                } else {
                    setMaxValue(Double.valueOf(ap.HeartRate));
                    setMaxValue(Double.valueOf(ap.HightPressure));
                    setMaxValue(Double.valueOf(ap.LowPressure));
                    setMinValue(Double.valueOf(ap.HeartRate));
                    setMinValue(Double.valueOf(ap.HightPressure));
                    setMinValue(Double.valueOf(ap.LowPressure));
                }
                heartValueLine.ChartPoints.add(heartValuePoint);
                hightValueLine.ChartPoints.add(hightValuePoint);
                lowValueLine.ChartPoints.add(lowValuePoint);
            }
            Lines.add(heartValueLine);
            Lines.add(hightValueLine);
            Lines.add(lowValueLine);
        }
    }

    // 修改这里
    private void setChart() {
        colors.add(Color.rgb(54, 141, 238));
        colors.add(Color.rgb(255, 165, 132));
        colors.add(Color.rgb(84, 206, 231));
        mspchar = new SplineChartView(this, Lines, colors); // 平滑曲线图
        mspchar.getChart().setTitle("");
        // 数据轴最大值
        mspchar.getChart().getDataAxis().setAxisMax(Math.floor(YAxistMax * 1.2));
        mspchar.getChart().getDataAxis().setAxisMin(Math.floor(YAxistMin * 0.8));
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
        int scrHeight = (int) ((dm.heightPixels)/8*7+5);
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
        View convertView = inflater.inflate(R.layout.activity_bloodpressure_navbar, null);
        DisplayMetrics dms = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((int)dms.widthPixels,(int)((dms.heightPixels)/8*1)); // 设置按钮的宽度和高度
        btParams.leftMargin = 0; // 横坐标定位
        btParams.topMargin = 0; // 纵坐标定位
        ((ViewGroup) content).addView(convertView, btParams);// 将按钮放入layout组件

    }

    private void InitData() {
        ISqlHelper isqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> ls = isqlHelper.Query("com.LocationEntity.BindingMessage", null);
        if (ls.size() > 0) {
            BindingMessage um = (BindingMessage) ls.get(0);
            UUID = um.UnderGuardUserID;
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
            String Time = sDateFormat.format(new java.util.Date());
            String stime = Time +"-01 "+"00:00:00";
            String etime = Time+"-31 "+"23:59:59";
            Toast.makeText(getApplicationContext(), "被监护人：" + um.UserName, Toast.LENGTH_SHORT).show();
            dataUpLoading(stime,etime,UUID);
        }else{
            ToastUtil.show(getApplicationContext(), "暂无绑定信息");
        }
    }

    private void dataUpLoading(String starttime,String endtime,String id) {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("PressureService", "queryPressure");
        Map requestCondition = new HashMap();
        String condition[] = { "StartTime", "EndTime", "UserID", "page", "rows" };
        String value[] = { starttime, endtime, UUID, "-1", "18" };
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    @Override
    public void updateView(){
        if (result != null) {
            dataResult = dataDecode.decode(result, "BloodPressureInfo");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (CurrentAction == currentNotiName) {
                    if (realData.getResultcode().equals("1")) {
                        testDataPressure.clear();
                        Lines.clear();
                        colors.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            BloodPressureInfo msg = (BloodPressureInfo) realData.getResult().get(i);
                            testDataPressure.add(msg);
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

    private void showDialog(){
        View itemview = getLayoutInflater().inflate(R.layout.activity_sg_dialog, null);
        startTime = (EditText) itemview.findViewById(R.id.et_startTime);
        endTime = (EditText)  itemview.findViewById(R.id.et_endTime);
        spUserName = (Spinner) itemview.findViewById(R.id.sp_sg_user);
        ISqlHelper isqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> ls = isqlHelper.Query("com.LocationEntity.BindingMessage", null);
        if (ls.size() > 0) {
            strsUserName = new String[ls.size()];
            strsUUID = new String[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                BindingMessage um = (BindingMessage) ls.get(i);
                if (um.UserName.equals("")) {
                    strsUserName[i] = "被监护人" + i;
                } else {
                    strsUserName[i] = um.UserName;
                }
                strsUUID[i] = um.UnderGuardUserID;
            }
        } else {
            strsUserName = new String[1];
            strsUUID = new String[1];
            strsUserName[0] = "无";
            strsUUID[0] = "FF";
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strsUserName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserName.setAdapter(adapter);
        spUserName.setPrompt(" 请选择： ");
        spUserName.setSelection(0);
        spUserName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UUID = strsUUID[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        startTime.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PressureMonitorActivity.this,
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
                                startTime.setText(strdate + "00:00:00");
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PressureMonitorActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                        endTime.setText(strdate + "23:59:59");
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //对话框
        new AlertDialog.Builder(PressureMonitorActivity.this)
                .setView(itemview)
                .setTitle("提示：输入条件")
                .setPositiveButton("确定", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (startTime.getText().toString().equals("")||endTime.getText().toString().equals("")) {
                            ToastUtil.show(PressureMonitorActivity.this, "查询信息不能为空");

                        } else {
                            dataUpLoading(startTime.getText().toString(), endTime.getText().toString(), UUID);
                        }
                        return;
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false) //触摸不消失
                .show();
        return;
    }
}
