package com.beautyhealthapp.UserCenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWMobileDevice.AbsMobilePhone;
import com.infrastructure.CWMobileDevice.MobilePhone422;
import com.infrastructure.CWUtilities.ToastUtil;

/**
 * Created by lenovo on 2015/12/30.
 */
public class OperateNumberActivity extends NavBarActivity implements OnClickListener {
    private ImageButton btn_sendMessage;
    private ImageButton btn_callPhone;
    private ImageButton btn_updateNumber;
    private String phone;
    private String  message[];
    private AbsMobilePhone mobilePhone=new MobilePhone422();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operatenumber);
        initNavBar("亲情号操作", true, false);
        fetchUIFromLayout();
        setListener();
        message = new String[]{getResources().getString(R.string.message1),
                        getResources().getString(R.string.message2),
                        getResources().getString(R.string.message3),
                        getResources().getString(R.string.message4),
                        getResources().getString(R.string.message5),
                        getResources().getString(R.string.message6)
        };
    }

    private void fetchUIFromLayout() {
        btn_sendMessage = (ImageButton) findViewById(R.id.btn_sendMessage);
        btn_callPhone = (ImageButton) findViewById(R.id.btn_callPhone);
        btn_updateNumber = (ImageButton) findViewById(R.id.btn_updateNumber);
    }

    private void setListener() {
        btn_sendMessage.setOnClickListener(this);
        btn_callPhone.setOnClickListener(this);
        btn_updateNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendMessage:
                sendMessageAction();
                break;
            case R.id.btn_callPhone:
                callPhoneNumAction();
                break;
            case R.id.btn_updateNumber:
                updateFamilyNum();
                break;
            default:
                break;
        }
    }

    private void updateFamilyNum() {
        Bundle bundleun = getIntent().getExtras();
        if (bundleun != null) {
            Intent intent=new Intent(OperateNumberActivity.this,AddFamilyNumberActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("indexmy",bundleun.getStringArrayList("key").get(0));
            bundle.putString("name", bundleun.getStringArrayList("key").get(1));
            bundle.putString("phone", bundleun.getStringArrayList("key").get(2));
            bundle.putString("address", bundleun.getStringArrayList("key").get(3));
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            ToastUtil.show(getApplicationContext(), "当前页值未获取到");
            finish();
        }
    }

    private void sendMessageAction() {
        Bundle bundlesm = getIntent().getExtras();
        if (bundlesm != null) {
            phone = bundlesm.getStringArrayList("key").get(2);
            if (phone.equals("") || phone == null) {
                ToastUtil.show(getApplicationContext(), "号码为空，不能进行操作");
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("选择要发送的消息：")
                        .setItems(message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[arg1]);
                                Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null).show();
            }
        } else {
            ToastUtil.show(getApplicationContext(), "当前页值未获取到");
            finish();
        }
    }

    private void callPhoneNumAction() {
        Bundle bundlecp = getIntent().getExtras();
        if (bundlecp != null) {
            phone = bundlecp.getStringArrayList("key").get(2);
            if (phone.equals("") || phone == null) {
                ToastUtil.show(getApplicationContext(), "号码为空，不能进行操作");
            } else {
                Intent _intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(_intent);
            }
        } else {
            ToastUtil.show(getApplicationContext(), "当前页值未获取到");
            finish();
        }
    }

}
