package com.beautyhealthapp.PrivateDoctors.Activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

/**
 * Created by lenovo on 2016/1/2.
 */
public class AppointRegistrateActivity extends NavBarActivity {
    private WebView wv_appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointregistrate);
        initNavBar("预约挂号", true, false);
        fetchUIFromLayout();
    }

    private void fetchUIFromLayout() {
        // 实例化WebView对象
        wv_appointment = (WebView) findViewById(R.id.wv_pd_appointment);
        wv_appointment.getSettings().setAllowFileAccess(true);
        wv_appointment.getSettings().setJavaScriptEnabled(true);
        wv_appointment.getSettings().setBlockNetworkImage(false);
        wv_appointment.getSettings().setBlockNetworkLoads(false);
        wv_appointment.getSettings().setBuiltInZoomControls(true);
        wv_appointment.getSettings().setJavaScriptCanOpenWindowsAutomatically(
                true);
        wv_appointment.getSettings().setLoadsImagesAutomatically(true);
        wv_appointment.getSettings().setDomStorageEnabled(true);
        wv_appointment.requestFocusFromTouch();
        wv_appointment.getSettings().setBuiltInZoomControls(true);

        wv_appointment.loadUrl("http://meinian.cn/LookHealth.aspx");
        wv_appointment.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_appointment.canGoBack()) {
            wv_appointment.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
