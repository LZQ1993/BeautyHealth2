package com.beautyhealthapp.PrivateDoctors.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import com.Entity.DoctorInfo;
import com.beautyhealthapp.PrivateDoctors.Assistant.DoctorInfoListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWAssistant.XListView;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWAssistant.XListView.IXListViewListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lenovo on 2015/12/26.
 */
public class DoctorBrieflyInfoActivity extends DataRequestActivity implements IXListViewListener,OnItemClickListener{
    private String currentNotiName = "DoctorInfoNotifications";
    private XListView listview;
    private LinearLayout empty_view;
    private DoctorInfoListAdapter adapter=null;
    private ArrayList<DoctorInfo> doctorInfo;
    private String hospitalName;
    private Handler mHandler2 = new Handler();
    public void setListItemes(ArrayList<DoctorInfo> _doctorInfo) {
        doctorInfo = _doctorInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorbrieflyinfo);
        Notifications.add(currentNotiName);
        hospitalName = getIntent().getStringExtra("HospitalName");
        setRightpicID(R.mipmap.appointrecord);
        initNavBar(hospitalName, true,true);
        doctorInfo = new ArrayList<DoctorInfo>();
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        listview = (XListView) findViewById(R.id.lv_DoctorInfo);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(true);
        empty_view = (LinearLayout)findViewById(R.id.empty_view);
        empty_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissProgressDialog();
                showProgressDialog(DoctorBrieflyInfoActivity.this, "加载中，请稍候...");
                initData();
            }
        });
        listview.setEmptyView(empty_view);
        if (adapter == null) {
            adapter = new DoctorInfoListAdapter(this,R.layout.activity_doctorbrieflyinfo_item,doctorInfo);
        }
        listview.setAdapter(adapter);
        listview.setXListViewListener(this);
        listview.setOnItemClickListener((OnItemClickListener) this);
        dismissProgressDialog();
        showProgressDialog(DoctorBrieflyInfoActivity.this, "加载中，请稍候...");
        initData();
    }

    @Override
    public void onRefresh() {
        initData();
        mHandler2.postDelayed(new Runnable() {
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
        myru.setMethod("HandyDoctorService", "queryHandyDoctor");
        Map requestCondition = new HashMap();
        String condition[] = { "HospitalID","rows","page" };
        String value[] = {getIntent().getStringExtra("HospitalID"),"18","-1" };
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
                dataResult = dataDecode.decode(result, "DoctorInfo");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        doctorInfo.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            DoctorInfo doctorInfoitem = (DoctorInfo) realData.getResult().get(i);
                            doctorInfo.add(doctorInfoitem);
                        }
                        setListItemes(doctorInfo);
                        adapter.notifyDataSetChanged();
                    } else {
                        return;
                    }
                } else {
                    DefaultTip(DoctorBrieflyInfoActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(DoctorBrieflyInfoActivity.this, "网络数据获取失败");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("DoctorInfo",doctorInfo.get(position-1));
        intent.putExtras(bundle);
        intent.putExtra("HospitalName", getIntent().getStringExtra("HospitalName"));
        intent.setClass(getApplicationContext(), DoctorDetailInfoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setClass(DoctorBrieflyInfoActivity.this,AppointRecordActivity.class);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("您处于离线状态,请登录再试").setPositiveButton("确定", null)
                    .setCancelable(false).show();
            return;
        }
    }
}
