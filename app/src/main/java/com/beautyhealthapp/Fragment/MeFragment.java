package com.beautyhealthapp.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.Entity.BluetoothState;
import com.beautyhealthapp.R;
import com.infrastructure.CWFragment.DataRequestFragment;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.List;

/**
 * Created by lenovo on 2015/12/25.
 */
public class MeFragment extends DataRequestFragment implements OnClickListener{
    private TableRow tr_bluetooth, tr_gps, tr_callCerten,
            tr_userManger, tr_personInfo, tr_userBackInfo, tr_familyNum,tr_bindedPeople;
    private TextView bluetoothstate;
    private BluetoothAdapter bluetoothAdapter;
    private ISqlHelper iSqlHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        tr_bluetooth= (TableRow) view.findViewById(R.id.tr_bluetooth);
        tr_gps = (TableRow) view.findViewById(R.id.tr_gps);
        tr_callCerten = (TableRow) view.findViewById(R.id.tr_callCerten);
        tr_userManger = (TableRow) view.findViewById(R.id.tr_userManger);
        tr_personInfo = (TableRow) view.findViewById(R.id.tr_personInfo);
        tr_userBackInfo = (TableRow) view.findViewById(R.id.tr_userBackInfo);
        tr_familyNum = (TableRow) view.findViewById(R.id.tr_familyNum);
        tr_bindedPeople = (TableRow) view.findViewById(R.id.tr_bindedPeople);
        bluetoothstate=(TextView) view.findViewById(R.id.bluetoothstate);
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        setListener();
        initUIData();
        return view;
    }

    private void setListener() {
        tr_bluetooth.setOnClickListener((OnClickListener) this);
        tr_gps.setOnClickListener((OnClickListener) this);
        tr_callCerten.setOnClickListener((OnClickListener) this);
        tr_userManger.setOnClickListener((OnClickListener) this);
        tr_personInfo.setOnClickListener((OnClickListener) this);
        tr_userBackInfo.setOnClickListener((OnClickListener) this);
        tr_familyNum.setOnClickListener((OnClickListener) this);
        tr_bindedPeople.setOnClickListener((OnClickListener) this);
    }

    private void initUIData() {
        iSqlHelper= new SqliteHelper(null,getActivity());
        List<Object> list = iSqlHelper.Query("com.Entity.BluetoothState", null);
        if(list.size()>0){
            BluetoothState bs =  (BluetoothState)list.get(0);
            if(bs.State.equals("1")){
                bluetoothAdapter.enable();
                bluetoothstate.setText("蓝牙已打开");

            }else{
                bluetoothAdapter.disable();
                bluetoothstate.setText("蓝牙已关闭");
            }
        }else{
            bluetoothAdapter.disable();
            bluetoothstate.setText("蓝牙已关闭");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tr_bluetooth:
                BluetoothIsEnable();
                break;
            case R.id.tr_gps:
                break;
            case R.id.tr_callCerten:
                break;
            case R.id.tr_userManger:
                break;
            case R.id.tr_personInfo:
                break;
            case R.id.tr_userBackInfo:
                break;
            case R.id.tr_familyNum:
                break;
            case R.id.tr_bindedPeople:
                break;
            default:
                ToastUtil.show(getActivity(), "输入有误!");
                break;
        }
    }

    private void BluetoothIsEnable() {
        BluetoothState bluetoothState =  new BluetoothState();
        if(bluetoothAdapter.getState()==10){
            //打开蓝牙
            bluetoothAdapter.enable();
            bluetoothState.AutoID=1;
            bluetoothState.State="1";
            iSqlHelper.Delete(bluetoothState);
            iSqlHelper.Insert(bluetoothState);
            bluetoothstate.setText("蓝牙已打开");
        }else {
            //关闭蓝牙
            bluetoothAdapter.disable();
            bluetoothState.AutoID=1;
            bluetoothState.State="0";
            iSqlHelper.Delete(bluetoothState);
            iSqlHelper.Insert(bluetoothState);
            bluetoothstate.setText("蓝牙已关闭");
        }
    }
}
