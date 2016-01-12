package com.beautyhealthapp.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.Entity.AdvertisementInfo;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWUtilities.LoadPicTask;

/**
 * Created by lenovo on 2015/12/25.
 */
public class FavorableInfoActivity extends DataRequestActivity {
    private TextView tv_fi_briefly,tv_adTitle,tv_publicTime;
    private ImageView iv_brieflypic;
    private AdvertisementInfo advertisementInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorableinfo);
        initNavBar("优惠详情",true,false);
        initData();
        fetchUIFromLayout();

    }
    private void initData() {
        advertisementInfo = (AdvertisementInfo) getIntent().getSerializableExtra("advertisementItem");
    }
    private void fetchUIFromLayout() {
        tv_fi_briefly = (TextView) findViewById(R.id.tv_fi_briefly);
        if (advertisementInfo.AdBriefly.equals("")||advertisementInfo.AdBriefly==null) {
            tv_fi_briefly.setText("暂无介绍");
        } else {
            tv_fi_briefly.setText(advertisementInfo.AdBriefly);
        }
        tv_adTitle = (TextView) findViewById(R.id.tv_adTitle);
        tv_adTitle.setText(advertisementInfo.AdTitle);
        tv_publicTime = (TextView) findViewById(R.id.tv_publicTime);
        tv_publicTime.setText(advertisementInfo.PublishTime);
        iv_brieflypic = (ImageView) findViewById(R.id.iv_brieflypic);
        iv_brieflypic.setImageResource(R.mipmap.hy_info_item_pic);
        String path = advertisementInfo.brieflyImgUrl;
        LoadPicTask advertisementDetiallpt = new LoadPicTask(iv_brieflypic);
        advertisementDetiallpt.setDefaultPic(R.mipmap.hy_info_item_pic);
        if (path.length() > 0) {
            String webaddrss = NetworkSetInfo.getServiceUrl()+path.substring(2, advertisementInfo.brieflyImgUrl.length());
            advertisementDetiallpt.execute(webaddrss);
        }else{
            iv_brieflypic.setImageResource(R.mipmap.hy_info_item_pic);
        }
    }

}
