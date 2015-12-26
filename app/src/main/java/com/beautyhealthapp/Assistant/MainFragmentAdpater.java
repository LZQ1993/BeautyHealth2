package com.beautyhealthapp.Assistant;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.beautyhealthapp.PrivateDoctors.Activity.AppointRecordActivity;
import com.beautyhealthapp.R;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.List;

public class MainFragmentAdpater implements OnCheckedChangeListener,OnClickListener{

    private List<Fragment> fragments; // 一个tab页面对应一个Fragment
    private List<String> title;
    private List<Boolean> rightVisibility;
    private List<Integer> rightPicId;
    private RadioGroup rgs; // 用于切换tab
    private FragmentActivity fragmentActivity; // Fragment所属的Activity
    private int fragmentContentId; // Activity中所要被替换的区域的id
    private int currentTab = 0; // 当前Tab页面索引
    private TextView titleBtn;
    private ImageButton rightBtn;
    public MainFragmentAdpater(FragmentActivity fragmentActivity,List<String> title,List<Boolean> rightVisibility,
                               List<Integer> rightPicId, List<Fragment> fragments,int fragmentContentId, RadioGroup rgs,
                               TextView titleBtn,ImageButton rightBtn) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.rightBtn = rightBtn;
        this.title = title;
        this.rightVisibility=rightVisibility;
        this.rightPicId=rightPicId;
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;
        this.titleBtn = titleBtn;
        rightBtn.setOnClickListener(this);
      /*getSupportFragmentManager()获取一个FragmentManager
        FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作。
        从 FragmentManager 获得一个FragmentTransaction的实例 :*/
        titleBtn.setText(title.get(0));
        if(rightVisibility.get(0)&&rightPicId.get(0)!=null){
            rightBtn.setVisibility(View.VISIBLE);
            rightBtn.setImageResource(rightPicId.get(0));
        }else{
            rightBtn.setVisibility(View.INVISIBLE);
        }
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(0));
        ft.commit();
        this.rgs.setOnCheckedChangeListener(this);
    }
    /**
     * 监听radiobutton改变时间，当选择了不同的radiobutton会出发这个函数
     */
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < rgs.getChildCount(); i++) {
            if (rgs.getChildAt(i).getId() == checkedId) {
                Fragment fragment = fragments.get(i);

                FragmentTransaction ft = obtainFragmentTransaction(i);
                getCurrentFragment().onPause(); // 暂停当前tab
                if (fragment.isAdded()) {
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    ft.add(fragmentContentId, fragment);
                }
                showTab(i); // 显示目标tab
                ft.commit();
            }
        }
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }

    private void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if (idx == i) {
                titleBtn.setText(title.get(i));
                if(rightVisibility.get(i)&&rightPicId.get(i)!=null){
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(rightPicId.get(i));
                }else{
                    rightBtn.setVisibility(View.INVISIBLE);
                }
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager()
                .beginTransaction();
        // 设置切换动画
        if (index > currentTab) {

            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        } else {
            ft.setCustomAnimations(R.anim.slide_right_in,
                    R.anim.slide_right_out);
        }
        return ft;
    }

    @Override
    public void onClick(View v) {
        if(v==rightBtn){
            switch(currentTab){
                case 2:
                    if(rightVisibility.get(currentTab)&&rightPicId.get(currentTab)!=null){
                        ISqlHelper iSqlHelper = new SqliteHelper(null,fragmentActivity);
                        List<Object> list = iSqlHelper.Query("com.Entity.UserMessage", null);
                        if (list.size() > 0) {
                            Intent intent = new Intent();
                            intent.setClass(fragmentActivity,AppointRecordActivity.class);
                            fragmentActivity.startActivity(intent);
                        } else {
                            new AlertDialog.Builder(fragmentActivity).setTitle("提示")
                                    .setMessage("您处于离线状态,请登录再试").setPositiveButton("确定", null)
                                    .setCancelable(false).show();
                            return;
                        }
                    }else{
                        break;
                    }
                    break;
                default: break;
            }
        }
    }
}
