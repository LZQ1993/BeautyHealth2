package com.beautyhealthapp.SafeGuardianship.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.Entity.SpotInfo;
import com.LocationEntity.BindingMessage;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.beautyhealthapp.R;
import com.beautyhealthapp.SafeGuardianship.Assistant.AMapUtil;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/3.
 */
public class LocationMonitorActivity extends DataRequestActivity implements OnInfoWindowClickListener,OnMarkerClickListener,OnGeocodeSearchListener {
    private String currentNotiName = "CurrentSpotInfoNotifications";
    private MapView mapView;
    private String addressName;
    private AMap aMap;
    private Spinner selectMonitorSpn;
    private ArrayAdapter<String> adapter;
    private String[] itemMonitor;
    private String[] UserID;
    private Marker geoMarker;
    private Marker regeoMarker;
    private GeocodeSearch geocoderSearch;
    private LatLonPoint latLonPoint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationmonitor);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.monitorpath);
        initNavBar("位置监护", true, true);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        if (aMap == null) {
            aMap = mapView.getMap();
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        selectMonitorSpn = (Spinner) findViewById(R.id.selectMonitorSpn);
        ISqlHelper isqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> ls = isqlHelper.Query("com.LocationEntity.BindingMessage", null);
        if (ls.size() > 0) {
            itemMonitor = new String[ls.size()];
            UserID = new String[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                BindingMessage um = (BindingMessage) ls.get(i);
                if(um.UserName.equals("")){
                    itemMonitor[i] = "被监护人" + i;
                }else{
                    itemMonitor[i] = um.UserName;
                }
                UserID[i] = um.UnderGuardUserID;
            }
        }else{
            itemMonitor = new String[1];
            UserID = new String[1];
            itemMonitor[0] ="未绑定";
            UserID[0] = "FF";
        }
        adapter = new ArrayAdapter<String>(this,R.layout.monitor_spinner_item,itemMonitor);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        selectMonitorSpn.setAdapter(adapter);
        selectMonitorSpn.setPrompt(" 请选择： ");
        selectMonitorSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (UserID[position].equals("FF")) {
                    ToastUtil.show(getApplicationContext(), "此用户无效，请重新绑定");
                } else {
                    getCurrentPosData(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
       if( marker.isInfoWindowShown()){
           marker.hideInfoWindow();
       }
    }

    private void getCurrentPosData(int position) {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("SpotService", "getCurrentSpot");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID"};
        String value[] = {UserID[position]};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    public void updateView() {
        if (result != null) {
            if (CurrentAction == currentNotiName) {
                dataResult = dataDecode.decode(result,"SpotInfo");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1") && realData.result.size() > 0) {
                        SpotInfo msg = (SpotInfo)realData.getResult().get(0);
                        addMarkersToMap(msg.Longtitude, msg.Latitude);
                    } else {
                        ToastUtil.show(getApplicationContext(), "暂无数据");
                    }
                } else {
                    DefaultTip(LocationMonitorActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(LocationMonitorActivity.this, "网络数据获取失败");
        }
    }

    private void addMarkersToMap(String Longtitude, String Latitude) {
        latLonPoint = new LatLonPoint(Double.valueOf(Latitude),Double.valueOf(Longtitude));
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(latLonPoint), 15));
        regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(String name) {
        GeocodeQuery query = new GeocodeQuery(name, "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(LatLonPoint _latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(_latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getAddress(latLonPoint);
        return false;
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int resulCode) {
        if (resulCode == 0) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address.getLatLonPoint()));
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:" + address.getFormatAddress();
            } else {
                ToastUtil.show(getApplicationContext(),"对不起，没有搜索到相关数据");
            }
        }else{
            ToastUtil.show(getApplicationContext(),"搜索失败");
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int resulCode) {
        if (resulCode == 0) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(latLonPoint), 15));
                addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress() + "附近";
                regeoMarker.setTitle(addressName);
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
            } else {
                ToastUtil.show(getApplicationContext(),"对不起，没有搜索到相关数据");
            }
        }else{
            ToastUtil.show(getApplicationContext(),"搜索失败");
        }
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        if(UserID[selectMonitorSpn.getSelectedItemPosition()].toString().equals("FF")){
            ToastUtil.show(getApplicationContext(), "此用户无效，请重新绑定后，再进行查询");
        }else {
            Intent intent = new Intent();
            intent.putExtra("UsedID",UserID[selectMonitorSpn.getSelectedItemPosition()]);
            intent.setClass(LocationMonitorActivity.this, MonitorPathActivity.class);
            startActivity(intent);
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
