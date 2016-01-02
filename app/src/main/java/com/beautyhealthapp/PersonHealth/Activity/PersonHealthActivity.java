package com.beautyhealthapp.PersonHealth.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWUtilities.ToastUtil;

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
                jumpActivity(MedicalReportActivity.class);
                break;
            case R.id.btn_bloodsugar:
                jumpActivity(MedicalReportActivity.class);
                break;
            case R.id.btn_pedometer:
                jumpActivity(MedicalReportActivity.class);
                break;
            case R.id.btn_abilityfunction:
                jumpActivity(MedicalReportActivity.class);
                break;
            case R.id.btn_medicalreport:
                jumpActivity(MedicalReportActivity.class);
                break;
            case R.id.btn_mylocation:
                jumpActivity(MedicalReportActivity.class);
                break;
            default:
                ToastUtil.show(getApplicationContext(), "输入有误!");
        }
    }

    private void jumpActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(PersonHealthActivity.this, cls);
        startActivity(intent);
    }
}
