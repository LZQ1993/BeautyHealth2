package com.beautyhealthapp.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.Entity.HospitalInfo;
import com.beautyhealthapp.Assistant.HospitalListAdpter;
import com.beautyhealthapp.PrivateDoctors.Activity.DoctorBrieflyInfoActivity;
import com.beautyhealthapp.R;
import com.infrastructure.CWAssistant.XListView;
import com.infrastructure.CWAssistant.XListView.IXListViewListener;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWFragment.DataRequestFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by lenovo on 2015/12/25.
 */
public class SearchHospitalFragment extends DataRequestFragment implements IXListViewListener, OnItemClickListener {
    private Context mContext;
    private String currentNotiName = "HospitalInfoNotifications";
    private XListView listview;
    private LinearLayout empty_view;
    private HospitalListAdpter adapter;
    private ArrayList<HospitalInfo> hospitalInfo;
    private Handler mHandler1 = new Handler();
    public void setListItemes(ArrayList<HospitalInfo> _hospitalInfo) {
        hospitalInfo = _hospitalInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        Notifications.add(currentNotiName);
        hospitalInfo = new ArrayList<HospitalInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchhospital, container, false);
        listview = (XListView) view.findViewById(R.id.lv_SearchHospital);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(true);
        empty_view = (LinearLayout) view.findViewById(R.id.empty_view);
        empty_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        listview.setEmptyView(empty_view);
        if (adapter == null) {
            adapter = new HospitalListAdpter(getActivity(), R.layout.fragment_searchhospital_item,
                    hospitalInfo, myListener);
        }
        listview.setAdapter(adapter);
        listview.setXListViewListener(this);
        listview.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        initData();
        return view;
    }

    @Override
    public void onRefresh() {
        initData();
        mHandler1.postDelayed(new Runnable() {
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
        myru.setMethod("HandyDoctorService", "searchHospital");
        Map requestCondition = new HashMap();
        String condition[] = {"page", "rows"};
        String value[] = {"-1", "18"};
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
                dataResult = dataDecode.decode(result, "HospitalInfo");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        hospitalInfo.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            HospitalInfo hospitalInfoitem = (HospitalInfo) realData.getResult().get(i);
                            hospitalInfo.add(hospitalInfoitem);
                        }
                        setListItemes(hospitalInfo);
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
        HospitalInfo hospitalItem = hospitalInfo.get(position-1);
        Intent intent = new Intent();
        intent.putExtra("HospitalID", hospitalItem.HospitalID);
        intent.putExtra("HospitalName", hospitalItem.HospitalName);
        intent.setClass(getActivity(), DoctorBrieflyInfoActivity.class);
        startActivity(intent);
    }

    private OnClickListener myListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            new AlertDialog.Builder(getActivity())
                    .setTitle("简介：")
                    .setMessage(hospitalInfo.get(position).HospitalBriefly)
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).setCancelable(false).show();
        }
    };



}
