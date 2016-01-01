package com.beautyhealthapp.CallCenter.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.Entity.CallCenterOfCity;
import com.beautyhealthapp.CallCenter.Assistant.CityListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWAssistant.XListView;
import com.infrastructure.CWAssistant.XListView.IXListViewListener;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/27.
 */
public class CallCenterActivity extends DataRequestActivity implements IXListViewListener {
    private String currentNotiName = "CallCenterNotifications";
    private XListView listview;
    private LinearLayout empty_view;
    private CityListAdapter adapter;
    private ArrayList<Boolean> res1;
    private ArrayList<String[]> params;
    private String[] citynames;
    private String[] phones;
    private Handler mHandler5 = new Handler();
    public void setListItemes(ArrayList<String[]> _params,ArrayList<Boolean> _res1) {
        params=_params;
        res1=_res1;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callcenter);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_save);
        initNavBar("呼叫中心", true, true);
        res1 = new ArrayList<Boolean>();
        params = new ArrayList<String[]>();
        fetchUIFromLayout();

    }

    private void fetchUIFromLayout() {
        listview = (XListView) findViewById(R.id.lv_callCenterCity);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(true);
        empty_view = (LinearLayout)findViewById(R.id.empty_view);
        empty_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissProgressDialog();
                showProgressDialog(CallCenterActivity.this, "加载中，请稍候...");
                initData();
            }
        });
        listview.setEmptyView(empty_view);
        if (adapter == null) {
            adapter = new CityListAdapter(this,params,res1);
        }
        listview.setAdapter(adapter);
        listview.setXListViewListener(this);
        dismissProgressDialog();
        showProgressDialog(CallCenterActivity.this, "加载中，请稍候...");
        initData();
    }

    private void initData() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("CallCenterService", "queryCallCenter");
        Map requestCondition = new HashMap();
        String condition[] = { "page", "rows" };
        String value[] = { "-1", "18" };
        requestCondition.put("json", JsonDecode.toJson(condition, value));
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    @Override
    public void onRefresh() {
        initData();
        mHandler5.postDelayed(new Runnable() {
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

    public void updateView() {
        dismissProgressDialog();
        onLoadStop();
        if (result != null) {
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result, "CallCenterOfCity");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        res1.clear();
                        params.clear();
                        citynames = new String[realData.getResult().size()];
                        phones = new String[realData.getResult().size()];
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            CallCenterOfCity auser = (CallCenterOfCity)realData.getResult().get(i);
                            citynames[i] = auser.CityName;
                            phones[i] = auser.Tel;
                            res1.add(i, false);
                        }
                        params.add(citynames);
                        params.add(phones);
                        setListItemes(params,res1);
                        adapter.notifyDataSetChanged();
                    } else {
                        return;
                    }
                } else {
                    DefaultTip(CallCenterActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(CallCenterActivity.this, "网络数据获取失败");
        }
    }
}
