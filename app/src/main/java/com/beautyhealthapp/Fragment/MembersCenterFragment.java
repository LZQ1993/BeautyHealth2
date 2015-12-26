package com.beautyhealthapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.Entity.AdvertisementInfo;
import com.beautyhealthapp.Activity.FavorableInfoActivity;
import com.beautyhealthapp.Assistant.AdvertisementLisrAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWAssistant.XListView;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWFragment.DataRequestFragment;
import com.infrastructure.CWAssistant.XListView.IXListViewListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/25.
 */
public class MembersCenterFragment extends DataRequestFragment implements IXListViewListener, OnItemClickListener {
    private Context mContext;
    private String currentNotiName = "AcceptVipInfoNotifications";
    private XListView listview;
    private LinearLayout empty_view;
    private AdvertisementLisrAdapter adapter;
    private ArrayList<AdvertisementInfo> advertisementInfo;
    private Handler mHandler = new Handler();
    public void setListItemes(ArrayList<AdvertisementInfo> _advertisementInfo) {
        advertisementInfo = _advertisementInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        Notifications.add(currentNotiName);
        advertisementInfo = new ArrayList<AdvertisementInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memberscenter, container, false);
        listview = (XListView) view.findViewById(R.id.lv_AcceptVipInfo);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(true);
        empty_view = (LinearLayout) view.findViewById(R.id.empty_view);
        empty_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        listview.setEmptyView(empty_view);
        if (adapter == null) {
            adapter = new AdvertisementLisrAdapter(getActivity(), R.layout.fragment_memberscenter_item, advertisementInfo);
        }
        listview.setAdapter(adapter);
        listview.setXListViewListener(this);
        listview.setOnItemClickListener((OnItemClickListener) this);
        initData();
        return view;
    }

    @Override
    public void onRefresh() {
        initData();
        mHandler.postDelayed(new Runnable() {
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
        myru.setMethod("MembersService", "searchAd");
        Map requestCondition = new HashMap();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
        String Time = sDateFormat.format(new java.util.Date());
        String stime = Time + "-01 " + "00:00:00";
        String etime = Time + "-31 " + "23:59:59";
        String condition[] = {"StartTime", "EndTime", "page", "rows"};
        String value[] = {stime, etime, "-1", "18"};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    public void updateView() {
        onLoadStop();
        if (result != null) {
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result, "AdvertisementInfo");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        advertisementInfo.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            AdvertisementInfo advertisementitem = (AdvertisementInfo) realData.getResult().get(i);
                            advertisementInfo.add(advertisementitem);
                        }
                        setListItemes(advertisementInfo);
                        adapter.notifyDataSetChanged();
                    } else {
                        return;
                    }
                } else {
                    DefaultTip(getActivity(), "数据解析失败");
                }
            }
        } else {
            DefaultTip(getActivity(), "网络数据获取失败");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdvertisementInfo advertisementItem = advertisementInfo.get(position-1);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("advertisementItem", advertisementItem);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), FavorableInfoActivity.class);
        startActivity(intent);
    }

}
