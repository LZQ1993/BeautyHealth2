package com.beautyhealthapp.PersonHealth.Activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2016/1/5.
 */
public class SugarOperGuideActivity extends NavBarActivity implements ViewFactory,
        OnTouchListener, OnClickListener {
    /**
     * ImagaSwitcher 的引用
     */
    private ImageSwitcher mImageSwitcher;
    /**
     * 图片id数组
     */
    private int[] imgIds;
    /**
     * 当前选中的图片id序号
     */
    private int currentPosition = 0;
    /**
     * 按下点的X坐标
     */
    private float downX;
    private String[] content;
    private int[] method;
    private ImageButton leftArrow, rightArrow;
    private TextView tv_data, tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugaroperationguide);
        initNavBar("操作指南", true, false);
        fetchUIFromLayout();
        setListener();
    }

    private void setListener() {
        rightArrow.setOnClickListener((OnClickListener) this);
        leftArrow.setOnClickListener((OnClickListener) this);

    }

    @SuppressWarnings("deprecation")
    private void fetchUIFromLayout() {
        leftArrow = (ImageButton) findViewById(R.id.bs_left_arrow);
        rightArrow = (ImageButton) findViewById(R.id.bs_right_arrow);
        tv_content = (TextView) findViewById(R.id.bs_tv_usercontent_show);
        tv_data = (TextView) findViewById(R.id.bs_tv_data);

        imgIds = new int[]{R.mipmap.sugar_pic1, R.mipmap.sugar_pic2, R.mipmap.sugar_pic3, R.mipmap.sugar_pic4, R.mipmap.sugar_pic5
                , R.mipmap.sugar_pic6, R.mipmap.sugar_pic7, R.mipmap.sugar_pic8, R.mipmap.sugar_pic9
                , R.mipmap.sugar_pic10, R.mipmap.sugar_pic11, R.mipmap.sugar_pic12, R.mipmap.sugar_pic13, R.mipmap.sugar_pic14, R.mipmap.sugar_pic15
                , R.mipmap.sugar_pic16, R.mipmap.sugar_pic17, R.mipmap.sugar_pic18};

        content = new String[]{"使用步骤1", "使用步骤2", "使用步骤3", "使用步骤4", "使用步骤5",
                "使用步骤6", "使用步骤7", "使用步骤8", "使用步骤9", "使用步骤10", "使用步骤11", "使用步骤12"
                , "使用步骤13", "使用步骤14", "使用步骤15", "使用步骤16", "使用步骤17", "使用步骤18"};

        method = new int[]{R.string.sugarMethod1, R.string.sugarMethod2, R.string.sugarMethod3,
                R.string.sugarMethod4, R.string.sugarMethod5, R.string.sugarMethod6, R.string.sugarMethod7,
                R.string.sugarMethod8, R.string.sugarMethod9, R.string.sugarMethod10, R.string.sugarMethod11,
                R.string.sugarMethod12, R.string.sugarMethod13, R.string.sugarMethod14, R.string.sugarMethod15,
                R.string.sugarMethod16, R.string.sugarMethod17, R.string.sugarMethod18};

        // 实例化ImageSwitcher
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.bs_imageSwitcher1);
        // 设置Factory
        mImageSwitcher.setFactory(this);
        // 设置OnTouchListener，我们通过Touch事件来切换图片
        mImageSwitcher.setOnTouchListener(this);
        mImageSwitcher.setImageResource(imgIds[currentPosition]);
        setContent(currentPosition);
        leftArrow.setVisibility(View.INVISIBLE);
    }


    private void setContent(int selectItems) {
        for (int i = 0; i < content.length; i++) {
            if (i == selectItems) {
                tv_data.setText(content[i]);
                tv_content.setText(getResources().getString(method[i]));
            }
        }
    }

    @Override
    public View makeView() {
        final ImageView i = new ImageView(this);
        i.setBackgroundColor(0xff000000);
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        return i;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 手指按下的X坐标
                downX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float lastX = event.getX();
                // 抬起的时候的X坐标大于按下的时候就显示上一张图片
                if (lastX > downX) {
                    prev(v);
                }
                if (lastX < downX) {
                    next(v);
                }
            }
            break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == leftArrow) {
            leftArrow.setAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.arrow));
            prev(v);
        }
        if (v == rightArrow) {
            rightArrow.setAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.arrow));
            next(v);
        }
    }

    public void prev(View source) {
        rightArrow.setVisibility(View.VISIBLE);
        // 显示上一个组件
        if (currentPosition > 0) {
            // 设置动画，这里的动画比较简单，不明白的去网上看看相关内容
            mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
                    getApplication(), R.anim.left_in));
            mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
                    getApplication(), R.anim.right_out));
            currentPosition--;
            if (currentPosition == 0) {
                leftArrow.setVisibility(View.INVISIBLE);
            }
            mImageSwitcher.setImageResource(imgIds[currentPosition % imgIds.length]);
            setContent(currentPosition);
        } else {
            Toast.makeText(getApplication(), "已经是第一张", Toast.LENGTH_SHORT).show();
            leftArrow.setVisibility(View.INVISIBLE);
        }

    }

    public void next(View source) {
        leftArrow.setVisibility(View.VISIBLE);
        // 显示下一个组件。
        if (currentPosition < imgIds.length - 1) {

            mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));
            mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));
            currentPosition++;
            if (currentPosition == (imgIds.length - 1)) {
                rightArrow.setVisibility(View.INVISIBLE);
            }
            mImageSwitcher.setImageResource(imgIds[currentPosition]);
            setContent(currentPosition);

        } else {
            Toast.makeText(getApplication(), "到了最后一张", Toast.LENGTH_SHORT).show();
            rightArrow.setVisibility(View.INVISIBLE);
        }
    }
}