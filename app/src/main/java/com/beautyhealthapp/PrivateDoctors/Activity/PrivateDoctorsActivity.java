package com.beautyhealthapp.PrivateDoctors.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2015/12/25.
 */
public class PrivateDoctorsActivity extends NavBarActivity implements View.OnClickListener {
    private Button herbalistdoctor,westerndoctor,appointment;
    private Animation scaleAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privatedoctors);
        initNavBar("医疗咨询", true, false);
        fetchUIFromLayout();
        setListener();
    }

    private void fetchUIFromLayout() {
        herbalistdoctor = (Button) findViewById(R.id.btn_pd_herbalistdoctor);
        westerndoctor = (Button) findViewById(R.id.btn_pd_westerndoctor);
        appointment = (Button) findViewById(R.id.btn_pd_appointment);
        scaleAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);
    }

    private void setListener() {
        herbalistdoctor.setOnClickListener(this);
        westerndoctor.setOnClickListener(this);
        appointment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.btn_pd_herbalistdoctor:
                herbalistdoctor.startAnimation(scaleAnim);
                intent.putExtra("Type","1");
                jumpActivity(MedicalInfoActivity.class,intent);
                break;
            case R.id.btn_pd_westerndoctor:
                westerndoctor.startAnimation(scaleAnim);
                intent.putExtra("Type","0");
                jumpActivity(MedicalInfoActivity.class,intent);
                break;
            case R.id.btn_pd_appointment:
                appointment.startAnimation(scaleAnim);
                jumpActivity(AppointRegistrateActivity.class,intent);
                break;
            default: break;
        }
    }

    private void jumpActivity(Class<?> cls,Intent _intent) {
        _intent.setClass(PrivateDoctorsActivity.this, cls);
        startActivity(_intent);
    }
}
