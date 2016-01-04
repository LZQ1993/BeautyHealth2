package com.beautyhealthapp.SafeGuardianship.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.Entity.SpotInfo;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWUtilities.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/4.
 */
public class MonitorPathActivity extends DataRequestActivity implements OnSeekBarChangeListener {
    private String currentNotiName = "PathSearchNotifications";
    private AMap aMap;
    private MapView mapView;
    private Button replayButton;
    private SeekBar processbar;
    private String starttime, endtime,UserID;
    private EditText startTimeEdt, endTimeEdt;
    public Handler timer = new Handler();// 定时器
    public Runnable runnable = null;
    private ArrayList<LatLng> latlngList;
    private ArrayList<LatLng> latlngPathList;
    private Marker marker = null;// 当前轨迹点图案

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorpath);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_search);
        initNavBar("轨迹监测", true, true);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        latlngList = new ArrayList<LatLng>();
        latlngPathList = new ArrayList<LatLng>();
        UserID=getIntent().getStringExtra("UsedID");
        fetchUIFromLayout();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
        String Time = sDateFormat.format(new java.util.Date());
        String starttime = Time +"-01 "+"00:00:00";
        String endtime = Time+"-31 "+"23:59:59";
        dataUpLoading(starttime,endtime);
    }

    private void fetchUIFromLayout() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        replayButton = (Button) findViewById(R.id.btn_replay);
        processbar = (SeekBar) findViewById(R.id.process_bar);
        processbar.setSelected(false);
        processbar.setOnSeekBarChangeListener(this);
        // 初始化runnable开始
        runnable = new Runnable() {
            @Override
            public void run() {
                // 要做的事情
                handler.sendMessage(Message.obtain(handler, 1));
            }
        };
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        latlngPathList.clear();
        if (progress != 0) {
            for (int i = 0; i < seekBar.getProgress(); i++) {
                latlngPathList.add(latlngList.get(i));
            }
            drawLine(latlngPathList, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        latlngPathList.clear();
        int current = seekBar.getProgress();
        if (current != 0) {
            for (int i = 0; i < seekBar.getProgress(); i++) {
                latlngPathList.add(latlngList.get(i));
            }
            drawLine(latlngPathList, current);
        }
    }

    private void drawLine(ArrayList<LatLng> list, int current) {
        aMap.clear();
        LatLng replayGeoPoint = latlngList.get(current - 1);
        if (marker != null) {
            marker.destroy();
        }
        // 添加位置
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(replayGeoPoint)
                .title("起点")
                .snippet(" ")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.man)))
                .anchor(0.5f, 0.5f);
        marker = aMap.addMarker(markerOptions);
        // 增加起点开始
        aMap.addMarker(new MarkerOptions()
                .position(latlngList.get(0))
                .title("起点")
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.start))));
        // 增加起点结束
        if (latlngPathList.size() > 1) {
            PolylineOptions polylineOptions = (new PolylineOptions())
                    .addAll(latlngPathList)
                    .color(Color.rgb(9, 129, 240)).width(6.0f);
            aMap.addPolyline(polylineOptions);
        }
        if (latlngPathList.size() == latlngList.size()) {
            aMap.addMarker(new MarkerOptions()
                    .position(latlngList.get(latlngList.size() - 1))
                    .title("终点")
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.end))));
        }
    }

    // 根据定时器线程传递过来指令执行任务
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int curpro = processbar.getProgress();
                if (curpro != processbar.getMax()) {
                    processbar.setProgress(curpro + 1);
                    timer.postDelayed(runnable, 500);// 延迟0.5秒后继续执行
                } else {
                    Button button = (Button) findViewById(R.id.btn_replay);
                    button.setText(" 回放 ");// 已执行到最后一个坐标 停止任务
                }
            }
        }
    };

    @Override
    public void onNavBarRightButtonClick(View view) {
        showDialog();
    }

    private void showDialog() {
        View itemview = getLayoutInflater().inflate(R.layout.showtimeseacherdialog, null);
        startTimeEdt = (EditText) itemview.findViewById(R.id.ed_startTime);
        endTimeEdt = (EditText) itemview.findViewById(R.id.ed_endTime);
        startTimeEdt.setInputType(InputType.TYPE_NULL);
        endTimeEdt.setInputType(InputType.TYPE_NULL);
        startTimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(MonitorPathActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        startTimeEdt.setText(strdate + "00:00:00");
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endTimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(MonitorPathActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
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
                                endTimeEdt.setText(strdate + "23:59:59");
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //对话框
        new AlertDialog.Builder(MonitorPathActivity.this)
                .setView(itemview)
                .setTitle("提示：输入条件")
                .setPositiveButton("确定", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        starttime = startTimeEdt.getText().toString();
                        endtime = endTimeEdt.getText().toString();
                        dataUpLoading(starttime, endtime);
                        return;
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false) //触摸不消失
                .show();
        return;
    }

    private void dataUpLoading(String starttime, String endtime) {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("SpotService", "querySpot");
        Map requestCondition = new HashMap();
        String condition[] = {"StartTime", "EndTime", "UserID", "page", "rows"};
        String value[] = {starttime, endtime,UserID, "-1", "-1"};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    public void updateView() {
        aMap.clear();
        if (result != null) {
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result, "SpotInfo");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        latlngList.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            SpotInfo msg = (SpotInfo) realData.getResult().get(i);
                            LatLng marker = new LatLng(Double.valueOf(msg.Latitude), Double.valueOf(msg.Longtitude));
                            latlngList.add(marker);
                        }
                        setUpMap();
                    } else {
                        ToastUtil.show(getApplicationContext(), "暂无数据");
                    }
                } else {
                    DefaultTip(MonitorPathActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(MonitorPathActivity.this, "网络数据获取失败");
        }
    }

    private void setUpMap() {
        // 设置进度条最大长度为数组长度
        processbar.setMax(latlngList.size());
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngList.get(0), 18));
    }

    public void replayclick(View v) {
        // 根据按钮上的字判断当前是否在回放
        if (replayButton.getText().toString().trim().equals("回放")) {
            if (latlngList.size() > 0) {
                // 假如当前已经回放到最后一点 置0
                if (processbar.getProgress() == processbar.getMax()) {
                    processbar.setProgress(0);
                }
                // 将按钮上的字设为"停止" 开始调用定时器回放
                replayButton.setText(" 停止 ");
                timer.postDelayed(runnable, 10);
            }
        } else {
            // 移除定时器的任务
            timer.removeCallbacks(runnable);
            replayButton.setText(" 回放 ");
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
