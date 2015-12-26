package com.infrastructure.CWActivity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beautyhealthapp.R;

public class NavBarActivity extends Activity {

    private TextView tvTitle;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private int rightpicID = R.mipmap.little_pic_info;
    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 初始化navbar
     */
    protected void initNavBar(String title, Boolean leftVisible, Boolean rightVisible) {
        tvTitle = (TextView) findViewById(R.id.nav_bar_tv_title);
        tvTitle.setText(title);
        btnLeft = (ImageButton) findViewById(R.id.nav_bar_btn_left);
        if (leftVisible) {
            btnLeft.setVisibility(View.VISIBLE);
        } else{
            btnLeft.setVisibility(View.INVISIBLE);
        }
        if (title == null) tvTitle.setVisibility(View.INVISIBLE);
        btnRight = (ImageButton) findViewById(R.id.nav_bar_btn_right);
        if (rightVisible){
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setImageResource(rightpicID);
        }else{
            btnRight.setVisibility(View.INVISIBLE);

        }
    }

    /**
     * 设置标题
     */
    protected void setNavBarTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 获取标题
     */
    protected String getNavBarTitle() {
        return tvTitle.getText().toString();
    }

    /**
     * 设置左按钮监听
     */
    public void onNavBarLeftButtonClick(View view) {
        finish();
    }

    /**
     * 设置右按钮监听
     */
    public void onNavBarRightButtonClick(View view) {

    }

    /**
     * 设置标题监听
     */
    public void onNavBarTitleClick(View view) {
    }

    /**
     * 设置左按钮可见性
     */
    protected void setNavBarLeftButtonVisible(boolean visible) {
        if (visible) btnLeft.setVisibility(View.VISIBLE);
        else btnLeft.setVisibility(View.INVISIBLE);
    }
    /**
     * 设置右按钮图片id
     */
    public void setRightpicID(int _rightpicID){
        this.rightpicID = _rightpicID;
    }
    /**
     * 设置右按钮可见性
     */
    protected void setNavBarRightButtonVisible(boolean visible) {
        if (visible) btnRight.setVisibility(View.VISIBLE);
        else btnRight.setVisibility(View.INVISIBLE);
    }

}
