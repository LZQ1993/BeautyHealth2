package com.beautyhealthapp.PersonHealth.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.LocationEntity.BusRouteStepInfo;
import com.LocationEntity.LocalFamilyNum;
import com.LocationEntity.RounteStepInfo;
import com.LocationEntity.UserMessage;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.Doorway;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.beautyhealthapp.R;
import com.beautyhealthapp.SafeGuardianship.Assistant.AMapUtil;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/1/4.
 */
public class MyLocationActivity extends NavBarActivity implements OnClickListener,OnRouteSearchListener,
        LocationSource,OnGeocodeSearchListener,OnMarkerClickListener,AMapLocationListener,OnInfoWindowClickListener {
    private AMap aMap;
    private MapView mapView;
    private Button drivebutton,walkbutton,busbutton;
    private Button familynumbreoneButton,familynumbretwoButton,familynumbrethreeButton;
    private String UserID,city,startaddressName,endAddressName;
    private String[] addresses = new String[3];
    private String[] names = new String[3];
    private String[] tels = new String[3];
    private Marker geoMarker;
    private Marker regeoMarker;
    private RouteSearch routeSearch;
    private GeocodeSearch geocoderSearch;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean isFirst = true;
    private MarkerOptions markerOption;
    private LatLonPoint latLonPoint = new LatLonPoint(0,0);
    private LatLonPoint latStartPoint_myLocation = null;
    private LatLonPoint latEndtPoint_family = null;
    private int routeType = 2;// 1代表公交模式，2代表驾车模式，3代表步行模式
    private int busMode = RouteSearch.BusDefault;// 公交默认模式
    private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
    private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
    private BusRouteResult busRouteResult;// 公交模式查询结果
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private List<RounteStepInfo> driveSteps = new ArrayList<RounteStepInfo>();// 用于记录所经过路线

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylocation);
        setRightpicID(R.mipmap.road);
        initNavBar("我的位置", true, true);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
        List<Object> list=iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if(list.size()>0){
            UserMessage userMessage=(UserMessage) list.get(0);
            UserID = userMessage.UserID;
        }else{
            UserID="00000";
        }
        fetchUIFromLayout();
        initData();
        initMap();
    }

    private void fetchUIFromLayout() {
        drivebutton = (Button) findViewById(R.id.drivebutton);
        walkbutton = (Button) findViewById(R.id.walkbutton);
        busbutton = (Button) findViewById(R.id.busbutton);
        familynumbreoneButton = (Button) findViewById(R.id.familynumbreoneButton);
        familynumbretwoButton = (Button) findViewById(R.id.familynumbretwoButton);
        familynumbrethreeButton = (Button) findViewById(R.id.familynumbrethreeButton);
        familynumbreoneButton.setText("未添加");
        familynumbretwoButton.setText("未添加");
        familynumbrethreeButton.setText("未添加");
        familynumbreoneButton.setOnClickListener(this);
        familynumbretwoButton.setOnClickListener(this);
        familynumbrethreeButton.setOnClickListener(this);
        busbutton.setOnClickListener(this);
        drivebutton.setOnClickListener(this);
        walkbutton.setOnClickListener(this);
    }

    private void initData() {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> user = iSqlHelper.Query("com.LocationEntity.LocalFamilyNum", "UserID='" + UserID + "'");
        if (user.size() > 0) {
            for (int i = 0; i < user.size(); i++) {
                LocalFamilyNum localFamilyNum = (LocalFamilyNum) user.get(i);
                addresses[i] = localFamilyNum.Address;
                names[i] = localFamilyNum.PeopleName;
                tels[i] = localFamilyNum.Tel;
                if (i == 0) {
                    familynumbreoneButton.setText(names[i]);
                } else if (i == 1) {
                    familynumbretwoButton.setText(names[i]);
                } else if (i == 2) {
                    familynumbrethreeButton.setText(names[i]);
                }
            }
        }
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setMapListener();
        geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    private void setMapListener() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            if (isFirst) {
                isFirst = false;
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aLocation.getLatitude(), aLocation.getLongitude())));
                CameraUpdateFactory.zoomTo(16);
                markerOption = new MarkerOptions();
                markerOption.position(new LatLng(aLocation.getLatitude(), aLocation.getLongitude()));
                city = aLocation.getCity();
                latLonPoint.setLatitude(aLocation.getLatitude());
                latLonPoint.setLongitude(aLocation.getLongitude());
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
                latStartPoint_myLocation = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());
                getAddress(latStartPoint_myLocation);
            }
        } else {
            ToastUtil.show(getApplicationContext(), "定位失败");
            String errText = "定位失败," + aLocation.getErrorCode()+ ": " + aLocation.getErrorInfo();
            Log.e("AmapErr", errText);
            aMap.clear();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 响应逆地理编码
     */

    public void getAddress(LatLonPoint _latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(_latLonPoint, 200,GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                startaddressName = result.getRegeocodeAddress().getFormatAddress() + "附近";
                regeoMarker.setTitle(startaddressName);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(latLonPoint), 15));
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
            }else{
                ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
                aMap.clear();
            }
        } else {
            ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
            aMap.clear();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.busbutton:
                busbutton.setBackgroundResource(R.color.wathet_color);
                drivebutton.setBackgroundResource(R.color.blue_color);
                walkbutton.setBackgroundResource(R.color.blue_color);
                routeType = 1;// 标识为公交模式
                searchRouteResult(latStartPoint_myLocation, latEndtPoint_family);

                break;
            case R.id.drivebutton:
                drivebutton.setBackgroundResource(R.color.wathet_color);
                busbutton.setBackgroundResource(R.color.blue_color);
                walkbutton.setBackgroundResource(R.color.blue_color);
                routeType = 2;// 标识为驾车模式
                searchRouteResult(latStartPoint_myLocation, latEndtPoint_family);

                break;
            case R.id.walkbutton:
                walkbutton.setBackgroundResource(R.color.wathet_color);
                busbutton.setBackgroundResource(R.color.blue_color);
                drivebutton.setBackgroundResource(R.color.blue_color);
                routeType = 3;// 标识为步行模式
                searchRouteResult(latStartPoint_myLocation, latEndtPoint_family);
                break;
            case R.id.familynumbreoneButton:
                if (!familynumbreoneButton.getText().equals("未添加")) {
                    getLatlon(addresses[0]);
                    endAddressName = addresses[0];
                    routeType = 2;
                    drivebutton.setBackgroundResource(R.color.wathet_color);
                    busbutton.setBackgroundResource(R.color.blue_color);
                    walkbutton.setBackgroundResource(R.color.blue_color);
                    familynumbrethreeButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_default));
                    familynumbretwoButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_default));
                    familynumbreoneButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_pressed));
                }
                break;
            case R.id.familynumbretwoButton:
                if (!familynumbretwoButton.getText().equals("未添加")) {
                    getLatlon(addresses[1]);
                    endAddressName = addresses[1];
                    routeType = 2;
                    drivebutton.setBackgroundResource(R.color.wathet_color);
                    busbutton.setBackgroundResource(R.color.blue_color);
                    walkbutton.setBackgroundResource(R.color.blue_color);
                    familynumbrethreeButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_default));
                    familynumbretwoButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_pressed));
                    familynumbreoneButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_default));
                }
                break;
            case R.id.familynumbrethreeButton:
                if (!familynumbrethreeButton.getText().equals("未添加")) {
                    getLatlon(addresses[2]);
                    endAddressName = addresses[2];
                    routeType = 2;
                    drivebutton.setBackgroundResource(R.color.wathet_color);
                    busbutton.setBackgroundResource(R.color.blue_color);
                    walkbutton.setBackgroundResource(R.color.blue_color);
                    familynumbrethreeButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_pressed));
                    familynumbretwoButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_default));
                    familynumbreoneButton.setBackground(getResources().getDrawable(R.drawable.btn_fammilynum_default));
                }
                break;
            default:break;
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(String name) {
        GeocodeQuery query = new GeocodeQuery(name, null);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }
    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                geoMarker.setTitle(endAddressName + "附近");
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address.getLatLonPoint()));
                latEndtPoint_family = new LatLonPoint(address.getLatLonPoint().getLatitude(),address.getLatLonPoint().getLongitude());
                searchRouteResult(latStartPoint_myLocation, latEndtPoint_family);
            } else {
                latEndtPoint_family = null;
                ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
                aMap.clear();
            }
        } else{
            latEndtPoint_family = null;
            ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
            aMap.clear();
        }
    }

    private void setRoute() {
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        searchRouteResult(latStartPoint_myLocation, latEndtPoint_family);
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        if (routeType == 1) {// 公交路径规划
            BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, city, 0);
            // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == 2) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == 3) {// 步行路径规划
            WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    /**
     * 公交路线查询回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                busRouteResult = result;
                BusPath busPath = busRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                getRouteStepInfo(busPath);
                BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(),
                        busRouteResult.getTargetPos());
                routeOverlay.removeFromMap();
                routeOverlay.addToMap();
                routeOverlay.zoomToSpan();
            } else {
                driveSteps.clear();
                aMap.clear();
                ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
            }
        } else {
            driveSteps.clear();
            aMap.clear();
            ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
        }
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                driveRouteResult = result;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                getRouteStepInfo(drivePath);
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath, driveRouteResult.getStartPos(),
                        driveRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
                driveSteps.clear();
                aMap.clear();
                ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
            }
        } else {
            driveSteps.clear();
            aMap.clear();
            ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
        }
    }

    /**
     * 步行路线结果回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                getRouteStepInfo(walkPath);
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(),
                        walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            } else {
                driveSteps.clear();
                aMap.clear();
                ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
            }
        } else {
            driveSteps.clear();
            aMap.clear();
            ToastUtil.show(MyLocationActivity.this,"对不起，没有搜索到相关数据！");
        }
    }
    // 添加经过的路线
    private void getRouteStepInfo(Object _drivePath) {
        driveSteps.clear();
        if (routeType == 1) {// 公交
            BusPath ds = (BusPath) _drivePath;
            List<BusStep> steps = ds.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                BusStep astep = steps.get(i);
                RouteBusLineItem arouteBusLineItem = astep.getBusLine();
                BusRouteStepInfo arountSI = new BusRouteStepInfo();
                if (arouteBusLineItem != null) {
                    arountSI.BustLineName = arouteBusLineItem.getBusLineName();
                }
                Doorway entrancedw = astep.getEntrance();
                if (entrancedw != null) {
                    arountSI.EntranceDoorName = entrancedw.getName();
                }
                Doorway exitdw = astep.getExit();
                if (exitdw != null) {
                    arountSI.ExitDoorName = exitdw.getName();
                }
                RouteBusWalkItem arouteBusWalkItem = astep.getWalk();
                if (arouteBusWalkItem != null) {
                    List<WalkStep> walksteps = arouteBusWalkItem.getSteps();
                    for (int j = 0; j < walksteps.size(); j++) {
                        WalkStep awalkstep = walksteps.get(j);
                        RounteStepInfo awalkrountSI = new RounteStepInfo();
                        awalkrountSI.Distance = awalkstep.getDistance();
                        awalkrountSI.StepName = awalkstep.getRoad();
                        arountSI.walkSteps.add(awalkrountSI);
                    }
                }
                driveSteps.add(arountSI);
            }
        } else if (routeType == 2) {// 驾车
            DrivePath ds = (DrivePath) _drivePath;
            List<DriveStep> steps = ds.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                DriveStep astep = steps.get(i);
                RounteStepInfo arountSI = new RounteStepInfo();
                arountSI.Distance = astep.getDistance();
                arountSI.StepName = astep.getRoad();
                driveSteps.add(arountSI);
            }
        } else if (routeType == 3) {// 步行
            WalkPath ds = (WalkPath) _drivePath;
            List<WalkStep> steps = ds.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                WalkStep astep = steps.get(i);
                RounteStepInfo arountSI = new RounteStepInfo();
                arountSI.Distance = astep.getDistance();
                arountSI.StepName = astep.getRoad();
                driveSteps.add(arountSI);
            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if( marker.isInfoWindowShown()){
            marker.hideInfoWindow();
        }
    }

    /**
     * 右按钮监听函数
     */
    public void onNavBarRightButtonClick(View view) {
        if (driveSteps.size() > 0) {
            Intent _intent = new Intent(MyLocationActivity.this, RouteStepShowActivity.class);
            Bundle routeTypeBundle = new Bundle();
            routeTypeBundle.putString("routeType", String.valueOf(routeType));
            Bundle routeStepBundle = new Bundle();
            routeStepBundle.putSerializable("routeStep", (Serializable) driveSteps);
            _intent.putExtra("routeType", routeTypeBundle);
            _intent.putExtra("routeStep", routeStepBundle);
            _intent.putExtra("start", startaddressName);
            _intent.putExtra("end", endAddressName);
            startActivity(_intent);
        }else{
            ToastUtil.show(MyLocationActivity.this, "未检索到经过路线");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getAddress(latLonPoint);
        return false;
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
        deactivate();
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
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }
}
