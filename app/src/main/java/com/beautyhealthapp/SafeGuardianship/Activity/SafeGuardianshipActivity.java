package com.beautyhealthapp.SafeGuardianship.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.List;

/**
 * Created by lenovo on 2015/12/25.
 */
public class SafeGuardianshipActivity extends NavBarActivity implements OnClickListener{
    private ImageButton bloodpressureBtn,bloodsugarBtn,abilityBtn,mylocationBtn;
    private Animation scaleAnim;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safeguardianship);
        initNavBar("安全监护", true, false);
        fetchUIFromLayout();
        setListener();
    }

    private void fetchUIFromLayout() {
        bloodpressureBtn = (ImageButton) findViewById(R.id.btn_pressure_guardianship);
        bloodsugarBtn = (ImageButton) findViewById(R.id.btn_sugar_guardianship);
        abilityBtn = (ImageButton) findViewById(R.id.btn_action_guardianship);
        mylocationBtn = (ImageButton) findViewById(R.id.btn_location_guardianship);
        scaleAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);
    }

    private void setListener() {
        bloodpressureBtn.setOnClickListener(this);
        bloodsugarBtn.setOnClickListener(this);
        abilityBtn.setOnClickListener(this);
        mylocationBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_pressure_guardianship:
                IsLoginTip(PressureMonitorActivity.class, v);
                break;
            case R.id.btn_sugar_guardianship:
                IsLoginTip(SugarMonitorActivity.class, v);
                break;
            case R.id.btn_action_guardianship:
                IsLoginTip(ActionMonitorActivity.class, v);
                break;
            case R.id.btn_location_guardianship:
                IsLoginTip(LocationMonitorActivity.class, v);
                break;
            default:break;
        }
    }

    private void IsLoginTip(Class<?> cls, View v) {
        ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
            IsBindUserTip(cls,v);
        }else{
            UserID = "0000000";
            ToastUtil.show(getApplicationContext(), "亲~，请您先登录");
        }
    }

    private void IsBindUserTip(Class<?> cls, View v) {
        ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.BindingMessage", "UserID='"+UserID+"'");
        if (list.size() > 0) {
            jumpActivity(cls, v);
        }else{
            ToastUtil.show(getApplicationContext(), "亲~，请您先绑定被监护人");
        }
    }

    private void jumpActivity(Class<?> cls,View view) {
        view.startAnimation(scaleAnim);
        Intent _intent = new Intent();
        _intent.setClass(SafeGuardianshipActivity.this, cls);
        startActivity(_intent);
    }
}
