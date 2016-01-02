package com.beautyhealthapp.PrivateDoctors.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.Entity.PictureMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWUtilities.ReadNetPicture;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/2.
 */
public class MedicalpicShowActivity extends DataRequestActivity implements OnClickListener, OnTouchListener {
    private String currentNotiName = "MedicalPicNotifications";
    private ImageView mImageView;
    private List<String> mImageUrl;
    private ImageButton leftArrow, rightArrow;
    private LinearLayout linearLayout;
    private int currentPosition = 0;
    private float downX;
    private Bitmap bitmap;
    Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalpicshow);
        Notifications.add(currentNotiName);
        initNavBar("查看图片", true, false);
        fetchUIFromLayout();
        setListener();
        initPic();
    }

    private void fetchUIFromLayout() {
        leftArrow = (ImageButton) findViewById(R.id.pdmc_left_arrow);
        rightArrow = (ImageButton) findViewById(R.id.pdmc_right_arrow);
        // 实例化ImageView
        mImageView = (ImageView) findViewById(R.id.pdmc_imageView);
        // 设置OnTouchListener，我们通过Touch事件来切换图片
        mImageView.setOnTouchListener(this);
        mImageView.setImageResource(R.mipmap.no_pictures);
        leftArrow.setVisibility(View.INVISIBLE);
    }

    private void setListener() {
        rightArrow.setOnClickListener(this);
        leftArrow.setOnClickListener(this);
    }


    private void initPic() {
        dismissProgressDialog();
        showProgressDialog(MedicalpicShowActivity.this, "加载中...");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("PrivateDoctor", "queryPic");
        Map requestCondition = new HashMap();
        String condition[] = {"QAID", "TypeID", "page", "rows"};
        String value[] = {getIntent().getStringExtra("QAID"),
                getIntent().getStringExtra("TypeID"), "-1", "18"};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    @Override
    public void updateView() {
        dismissProgressDialog();
        if (result != null) {
            dataResult = dataDecode.decode(result, "PictureMessage");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (CurrentAction == currentNotiName) {
                    mImageUrl = new ArrayList<String>();
                    if (realData.getResultcode().equals("1") && realData.getResult().size() > 0) {
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            PictureMessage msg = (PictureMessage) realData.getResult().get(i);
                            String webaddrss = NetworkSetInfo.getServiceUrl() + msg.PathAndFileName.substring(2, msg.PathAndFileName.length());
                            mImageUrl.add(webaddrss);
                        }
                        if (mImageUrl.size() > 1) {
                            rightArrow.setVisibility(View.VISIBLE);
                        } else {
                            rightArrow.setVisibility(View.INVISIBLE);
                        }
                        downloadpic(0);
                    } else {
                        rightArrow.setVisibility(View.INVISIBLE);
                        ToastUtil.show(getApplicationContext(), "暂无图片");
                        return;
                    }
                }
            } else {
                DefaultTip(MedicalpicShowActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(MedicalpicShowActivity.this, "网络获取数据失败");
        }
    }

    private void downloadpic(final int position) {
        dismissProgressDialog();
        showProgressDialog(MedicalpicShowActivity.this, "加载中...");
        new Thread() {
            public void run() {
                bitmap = ReadNetPicture.getHttpBitmap(mImageUrl.get(position));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap == null) {
                            mImageView.setImageResource(R.mipmap.no_pictures);
                        } else {
                            mImageView.setImageBitmap(bitmap);
                        }
                        dismissProgressDialog();
                    }
                });
            };

        }.start();
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
            leftArrow.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow));
            prev(v);
        }
        if (v == rightArrow) {
            rightArrow.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow));
            next(v);
        }

    }

    public void prev(View source) {
        if (mImageUrl.size() > 0) {
            if (mImageUrl.size() > 1) {
                rightArrow.setVisibility(View.VISIBLE);
            } else {
                rightArrow.setVisibility(View.INVISIBLE);
            }
            // 显示上一个组件
            if (currentPosition > 0) {
                // 设置动画，这里的动画比较简单，不明白的去网上看看相关内容
                currentPosition--;
                if (currentPosition == 0) {
                    leftArrow.setVisibility(View.INVISIBLE);
                }
                downloadpic(currentPosition % mImageUrl.size());
            } else {
                ToastUtil.show(getApplicationContext(), "已经是第一张");
                leftArrow.setVisibility(View.INVISIBLE);
            }
        } else {
            ToastUtil.show(getApplicationContext(), "暂无图片");
        }
    }

    public void next(View source) {
        if (mImageUrl.size() > 0) {
            if (mImageUrl.size() > 1) {
                leftArrow.setVisibility(View.VISIBLE);
            } else {
                leftArrow.setVisibility(View.INVISIBLE);
            }
            // 显示下一个组件。
            if (currentPosition < (mImageUrl.size() - 1)) {
                currentPosition++;
                if (currentPosition == (mImageUrl.size() - 1)) {
                    rightArrow.setVisibility(View.INVISIBLE);
                }
                downloadpic(currentPosition % mImageUrl.size());
            } else {
                ToastUtil.show(getApplicationContext(), "已经是最后一张了");
                rightArrow.setVisibility(View.INVISIBLE);
            }
        } else {
            ToastUtil.show(getApplicationContext(), "暂无图片");
        }
    }
}
