package com.beautyhealthapp.Activity;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyhealthapp.Assistant.MainFragmentAdpater;
import com.beautyhealthapp.Fragment.MainFragment;
import com.beautyhealthapp.Fragment.MeFragment;
import com.beautyhealthapp.Fragment.MembersCenterFragment;
import com.beautyhealthapp.Fragment.SearchHospitalFragment;
import com.beautyhealthapp.R;
import com.beautyhealthapp.Service.AutoLoginService;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private RadioGroup rg;
    private TextView titleBtn;
    private ImageButton rightBtn;
    private List<Fragment> fragments;
    private List<Boolean> rightVisibility;
    private List<Integer> rightPicId;
    private List<String> title;
    private long firstBackKeyTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleBtn = (TextView) findViewById(R.id.nav_bar_tv_title);
        rightBtn = (ImageButton) findViewById(R.id.nav_bar_btn_right);
        rg = (RadioGroup) findViewById(R.id.radioGroup);// 实例化radiogroup
        fragments = new ArrayList<Fragment>();
        title = new ArrayList<String>();
        rightVisibility = new ArrayList<Boolean>();
        rightPicId = new ArrayList<Integer>();
        // 分别添加4个fragment
        fragments.add(new MainFragment());
        title.add("首页");
        rightVisibility.add(false);
        rightPicId.add(null);
        fragments.add(new MembersCenterFragment());
        title.add("会员中心");
        rightVisibility.add(false);
        rightPicId.add(null);
        fragments.add(new SearchHospitalFragment());
        title.add("绿色就医");
        rightVisibility.add(true);
        rightPicId.add(R.mipmap.appointrecord);
        fragments.add(new MeFragment());
        title.add("我");
        rightVisibility.add(false);
        rightPicId.add(null);
        new MainFragmentAdpater(this,title,rightVisibility,rightPicId,fragments,R.id.id_content, rg, titleBtn,rightBtn);// 设置适配器

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)// 主要是对这个函数的复写
    {
        // TODO Auto-generated method stub

        if ((keyCode == KeyEvent.KEYCODE_BACK)
                && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            if (System.currentTimeMillis() - firstBackKeyTime > 2000) // 2s内再次选择back键有效
            {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstBackKeyTime = System.currentTimeMillis();
            } else {
                ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                iSqlHelper.SQLExec("delete from UserMessage");// 删除表中原有的数据，保证只有一条
                BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.disable();
                Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                stopService(service);
                finish();
                System.exit(0); // 凡是非零都表示异常退出!0表示正常退出!
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}