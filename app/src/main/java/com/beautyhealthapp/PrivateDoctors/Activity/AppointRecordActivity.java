package com.beautyhealthapp.PrivateDoctors.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.Entity.AppointInfo;
import com.Entity.UserMessage;
import com.beautyhealthapp.PrivateDoctors.Assistant.AppointRecordListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWAssistant.XListView;
import com.infrastructure.CWAssistant.XListView.IXListViewListener;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lenovo on 2015/12/26.
 */
public class AppointRecordActivity extends DataRequestActivity implements IXListViewListener,OnItemClickListener {
    private String currentNotiName = "AppointRecordInfoNotifications";
    private XListView listview;
    private Handler mHandler4 = new Handler();
    private LinearLayout empty_view;
    private AppointRecordListAdapter adapter=null;
    private ArrayList<AppointInfo> appointInfo;
    public void setListItemes(ArrayList<AppointInfo> _appointInfo) {
        appointInfo = _appointInfo;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointrecord);
        Notifications.add(currentNotiName);
        initNavBar("预约记录", true, false);
        appointInfo = new ArrayList<AppointInfo>();
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        listview = (XListView) findViewById(R.id.lv_AppointRecord);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(true);
        empty_view = (LinearLayout)findViewById(R.id.empty_view);
        empty_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissProgressDialog();
                showProgressDialog(AppointRecordActivity.this, "加载中，请稍候...");
                initData();
            }
        });
        listview.setEmptyView(empty_view);
        if (adapter == null) {
            adapter = new AppointRecordListAdapter(this,R.layout.activity_appointrecord_item,appointInfo);
        }
        listview.setAdapter(adapter);
        listview.setXListViewListener(this);
        listview.setOnItemClickListener((OnItemClickListener) this);
        dismissProgressDialog();
        showProgressDialog(AppointRecordActivity.this, "加载中，请稍候...");
        initData();
    }
    @Override
    public void onRefresh() {
        initData();
        mHandler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoadStop();
            }
        }, 5000);
    }

    @Override
    public void onLoadMore() {

    }

    private void onLoadStop() {
        listview.stopRefresh();
        listview.stopLoadMore();
        listview.setRefreshTime("刚刚");
    }
    private void initData() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("HandyDoctorService", "searchAppoint");
        Map requestCondition = new HashMap();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
        String Time = sDateFormat.format(new java.util.Date());
        String stime = Time + "-01 " + "00:00:00";
        String etime = Time + "-31 " + "23:59:59";
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.Entity.UserMessage", null);
        UserMessage userMessage = (UserMessage) list.get(0);
        String UserID = userMessage.UserID;
        String condition[] = { "UserID","StartTime", "EndTime", "page", "rows" };
        String value[] = {UserID,stime, etime, "-1", "18" };
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    public void updateView() {
        dismissProgressDialog();
        onLoadStop();
        if (result != null) {
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result,"AppointInfo");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        appointInfo.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            AppointInfo appointInfoitem = (AppointInfo) realData.getResult().get(i);
                            appointInfo.add(appointInfoitem);
                        }
                        setListItemes(appointInfo);
                        adapter.notifyDataSetChanged();
                    } else {
                        return;
                    }
                } else {
                    DefaultTip(AppointRecordActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(AppointRecordActivity.this, "网络数据获取失败");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
