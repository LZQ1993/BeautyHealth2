package com.beautyhealthapp.PrivateDoctors.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Entity.DoctorInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.LoadPicTask;

import java.util.List;

/**
 * Created by lenovo on 2015/12/26.
 */
public class DoctorDetailInfoActivity extends DataRequestActivity implements OnClickListener{
    private DoctorInfo doctorInfo;
    private TextView tv_doctorName, tv_hospitalName, tv_className, tv_mainCute,tv_doctorInfo;
    private Button btnAppoint, btnPrice;
    private ImageView iv_db_docotrPic;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctordetailinfo);
        setRightpicID(R.mipmap.appointrecord);
        initNavBar("专家", true,true);
        doctorInfo = (DoctorInfo) getIntent().getSerializableExtra("DoctorInfo");
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        tv_doctorName = (TextView) findViewById(R.id.tv_db_doctorName);
        tv_doctorName.setText(doctorInfo.DoctorName);
        tv_hospitalName = (TextView) findViewById(R.id.tv_hospitalName);
        tv_hospitalName.setText(getIntent().getStringExtra("HospitalName"));
        tv_className = (TextView) findViewById(R.id.tv_className);
        tv_className.setText(doctorInfo.ClassName);
        tv_mainCute = (TextView) findViewById(R.id.tv_mainCute);
        tv_mainCute.setText(doctorInfo.MainCute);
        tv_doctorInfo = (TextView) findViewById(R.id.tv_doctorInfo);
        tv_doctorInfo.setText(doctorInfo.DoctorDescription);
        iv_db_docotrPic = (ImageView) findViewById(R.id.iv_db_docotrPic);
        String path = doctorInfo.DoctorImgUrl;
        LoadPicTask lpt = new LoadPicTask(iv_db_docotrPic);
        lpt.setDefaultPic(R.mipmap.userphoto);
        if (path.length() > 0) {
            String webaddrss = NetworkSetInfo.getServiceUrl()+path.substring(2, doctorInfo.DoctorImgUrl.length());
            lpt.execute(webaddrss);
        }else{
            iv_db_docotrPic.setImageResource(R.mipmap.userphoto);
        }
        btnPrice = (Button) findViewById(R.id.btn_price);
        btnPrice.setText(Html.fromHtml("<u>关于价格</u>"));// 下划线
        btnAppoint = (Button) findViewById(R.id.btn_appoint);
        btnAppoint.setOnClickListener((OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAppoint) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("DoctorInfo",doctorInfo);
            intent.putExtras(bundle);
            intent.putExtra("HospitalName", getIntent().getStringExtra("HospitalName"));
            intent.setClass(getApplicationContext(), AppointActivity.class);
            startActivity(intent);
        }

    }

    public void showPriceTip(View view) {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("亲~具体价格，callcenter会电话联系您，根据具体病情确定哦~")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setCancelable(false).show();
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setClass(DoctorDetailInfoActivity.this,AppointRecordActivity.class);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("您处于离线状态,请登录再试").setPositiveButton("确定", null)
                    .setCancelable(false).show();
            return;
        }
    }

}
