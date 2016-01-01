package com.beautyhealthapp.UserCenter.Activity;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.Entity.CallCenterOfCity;
import com.LocationEntity.UserLocal;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.beautyhealthapp.R;
import com.beautyhealthapp.UserCenter.Assistant.CityListAdapter;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/27.
 */
public class CallCenterActivity extends DataRequestActivity implements AMapLocationListener {
    private String currentNotiName = "CallCenterNotifications";
    private ListView listView;
    private LinearLayout empty_view;
    private CityListAdapter adapter;
    private ArrayList<Boolean> isSelected;
    private ArrayList<CallCenterOfCity> CityInfo;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public void setListItemes(ArrayList<CallCenterOfCity> CityInfo, ArrayList<Boolean> isSelected) {
        this.CityInfo = CityInfo;
        this.isSelected = isSelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callcenter);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_save);
        initNavBar("呼叫中心", true, true);
        isSelected = new ArrayList<Boolean>();
        CityInfo = new ArrayList<CallCenterOfCity>();
        fetchUIFromLayout();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
    }

    private void fetchUIFromLayout() {
        listView = (ListView) findViewById(R.id.lv_city);
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        empty_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        listView.setEmptyView(empty_view);
        if (adapter == null) {
            adapter = new CityListAdapter(CallCenterActivity.this, CityInfo, isSelected);
        }
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        initData();
    }

    private void initData() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("CallCenterService", "queryCallCenter");
        Map requestCondition = new HashMap();
        String condition[] = {"page", "rows"};
        String value[] = {"-1", "18"};
        requestCondition.put("json", JsonDecode.toJson(condition, value));
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    public void updateView() {
        dismissProgressDialog();
        if (result != null) {
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result, "CallCenterOfCity");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    CityInfo.clear();
                    isSelected.clear();
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            CallCenterOfCity callCenterOfCityitem = (CallCenterOfCity) realData.getResult().get(i);
                            CityInfo.add(callCenterOfCityitem);
                            isSelected.add(i, false);
                        }
                        setListItemes(CityInfo, isSelected);
                        adapter.notifyDataSetChanged();
                        Location();
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

    private void Location() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式Hight_Accuracy，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        ConnectivityManager nw = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = nw.getActiveNetworkInfo();
        if (netinfo != null) {
            if (gps && netinfo.isAvailable()) {
                mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            } else if (gps == false && netinfo.isAvailable() == true) {
                mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
            }
        } else {
            Toast.makeText(getApplicationContext(), "请您打开网络后，在进行定位", Toast.LENGTH_SHORT).show();
        }
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(-1);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                String city =amapLocation.getCity();//城市信息
                int index = -1;
                ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                List<Object> ls =  iSqlHelper.Query("com.LocationEntity.UserLocal",null);
                if(ls.size()>0){
                    UserLocal us = (UserLocal) ls.get(0);
                    for (int i = 0; i < CityInfo.size();i++) {
                        if (city.equals(CityInfo.get(i).CityName)) {
                            CityInfo.get(i).CityName= CityInfo.get(i).CityName+ "（当前位置）";
                        }
                        if(us.CityName.equals(CityInfo.get(i).CityName)){
                            index=i;
                        }
                    }

                }else{
                    for (int i = 0; i <CityInfo.size(); i++) {
                        if (city.equals(CityInfo.get(i).CityName)) {
                            CityInfo.get(i).CityName= CityInfo.get(i).CityName+ "（当前位置）";
                            index=i;
                        }
                    }
                }
                if(index==(-1)){
                    isSelected.set(0, true);
                }else{
                    isSelected.set(index, true);
                }
                setListItemes(CityInfo,isSelected);
                adapter.notifyDataSetChanged();
                mLocationClient.stopLocation();//停止定位
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    public void onNavBarRightButtonClick(View view) {
        // 本地存储
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        iSqlHelper.SQLExec("delete from UserLocal");// 删除表中原有的数据，保证只有一条
        UserLocal userLocal = new UserLocal();
        userLocal.CityName = CityInfo.get(adapter.SelectedPosition).CityName;
        userLocal.Tel = CityInfo.get(adapter.SelectedPosition).Tel;
        iSqlHelper.Insert(userLocal);// 插入新的数据，即要保存的
        List<Object> users = iSqlHelper.Query("com.LocationEntity.UserLocal", null);
        for (int i = 0; i < users.size(); i++) {
            UserLocal ecc = (UserLocal) users.get(i);
            ToastUtil.show(getApplicationContext(), " 选择的城市:" + ecc.CityName + " 电话号：" + ecc.Tel + "");
        }
       finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端
    }
}
