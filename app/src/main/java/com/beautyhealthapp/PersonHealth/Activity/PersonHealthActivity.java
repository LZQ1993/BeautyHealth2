package com.beautyhealthapp.PersonHealth.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.List;

/**
 * Created by lenovo on 2015/12/25.
 */
public class PersonHealthActivity extends NavBarActivity implements OnClickListener {
    private ImageButton bloodpressure, bloodsugar, pedometer, abilityfunction, medicalreport, mylocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personhealth);
        initNavBar("个人健康", true, false);
        fetchUIFromLayout();
        setListener();
    }

    private void fetchUIFromLayout() {
        bloodpressure = (ImageButton) findViewById(R.id.btn_bloodpressure);
        bloodsugar = (ImageButton) findViewById(R.id.btn_bloodsugar);
        pedometer = (ImageButton) findViewById(R.id.btn_pedometer);
        abilityfunction = (ImageButton) findViewById(R.id.btn_abilityfunction);
        medicalreport = (ImageButton) findViewById(R.id.btn_medicalreport);
        mylocation = (ImageButton) findViewById(R.id.btn_mylocation);
    }

    private void setListener() {
        bloodpressure.setOnClickListener(this);
        bloodsugar.setOnClickListener(this);
        pedometer.setOnClickListener(this);
        abilityfunction.setOnClickListener(this);
        medicalreport.setOnClickListener(this);
        mylocation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_bloodpressure:
                jumpActivity(PressureMeasureActivity.class);
                break;
            case R.id.btn_bloodsugar:
                jumpActivity(SugarMeasureActivity.class);
                break;
            case R.id.btn_pedometer:
                jumpActivity(PedometerActivity.class);
                break;
            case R.id.btn_abilityfunction:
                jumpActivity(ActiveAbleActivity.class);
                break;
            case R.id.btn_medicalreport:
                jumpActivity(MedicalReportActivity.class);
                break;
            case R.id.btn_mylocation:
                IsLoginTip(MyLocationActivity.class);
                break;
            default:break;
        }
    }

    private void IsLoginTip(Class<?> cls) {
        ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            jumpActivity(cls);
        }else{
            ToastUtil.show(getApplicationContext(), "亲~，请您先登录");
        }
    }

    private void jumpActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(PersonHealthActivity.this, cls);
        startActivity(intent);
    }
}
