package com.beautyhealthapp.UserCenter;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.Entity.FamilyNumberMessage;
import com.LocationEntity.LocalFamilyNum;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/30.
 */
public class FamilyNumberActivity extends DataRequestActivity implements OnClickListener {
    private String currentNotiName = "FamilyNumberUpLoadNotifications";
    private String currentNotiName1 = "FamilyNumberDownLoadNotifications";
    private Button familyNumBtn1;
    private Button familyNumBtn2;
    private Button familyNumBtn3;
    private Button upLoadBtn;
    private Button downLoadBtn;
    private String UserID;
    private ISqlHelper iSqlHelper;
    private String[] indexmys = new String[3];
    private String[] names = new String[3];
    private String[] phones = new String[3];
    private String[] addresses = new String[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familynumber);
        Notifications.add(currentNotiName);
        Notifications.add(currentNotiName1);
        initNavBar("亲情号", true, false);
        fetchUIFromLayout();
        setListener();
        iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
        } else {
            UserID = "0000000";
        }

    }

    private void fetchUIFromLayout() {
        familyNumBtn1 = (Button) findViewById(R.id.btn_1);
        familyNumBtn2 = (Button) findViewById(R.id.btn_2);
        familyNumBtn3 = (Button) findViewById(R.id.btn_3);
        upLoadBtn = (Button) findViewById(R.id.btn_upload);
        downLoadBtn = (Button) findViewById(R.id.btn_download);
    }

    //设置监听器
    private void setListener() {
        familyNumBtn1.setOnClickListener(this);
        familyNumBtn2.setOnClickListener(this);
        familyNumBtn3.setOnClickListener(this);
        upLoadBtn.setOnClickListener(this);
        downLoadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                redirectIntent(1, familyNumBtn1);
                break;
            case R.id.btn_2:
                redirectIntent(2, familyNumBtn2);
                break;
            case R.id.btn_3:
                redirectIntent(3, familyNumBtn3);
                break;
            case R.id.btn_upload:
                upLoad();
                break;
            case R.id.btn_download:
                downLoad();
                break;
            default:
                break;
        }
    }

    private void redirectIntent(int flag, Button btn) {
        String familyUnadded = getApplicationContext().getString(R.string.family_unadded);
        if (btn.getText().toString().equals(familyUnadded)) {
            Intent intent = new Intent(FamilyNumberActivity.this, AddFamilyNumberActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("indexmy", String.valueOf(flag));
            bundle.putString("UserID", UserID);
            intent.putExtras(bundle);
            startActivityForResult(intent, flag);
        } else {
//			将从AddFamilyNumber中的消息传到号码操作界面OperateNumberActivity
            List<String> sendMessage = new ArrayList<String>();
            sendMessage.add(indexmys[flag - 1]);
            sendMessage.add(names[flag - 1]);
            sendMessage.add(phones[flag - 1]);
            sendMessage.add(addresses[flag - 1]);
            Intent intent = new Intent(FamilyNumberActivity.this, OperateNumberActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("key", (ArrayList<String>) sendMessage);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void upLoad() {


    }

    private void downLoad() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("FamilyService", "downloadFamily");
        Map requestCondition = new HashMap();
        String condition[] = {"UserID", "page", "rows"};
        String value[] = {UserID, "1", "3"};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName1);
        setRequestUtility(myru);
        requestData();
        dismissProgressDialog();
        showProgressDialog(FamilyNumberActivity.this, "正在同步...");
    }

    @Override
    public void updateView() {
        dismissProgressDialog();
        if (result != null) {
            if (CurrentAction == currentNotiName1) {
                dataResult = dataDecode.decode(result, "FamilyNumberMessage");
                if (dataResult != null) {
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1")) {
                        // 删除表中原有的数据，保证只有一条
                        iSqlHelper.SQLExec("delete from FamilyNumberMessage where UserID='" + UserID + "'");
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            FamilyNumberMessage msg = (FamilyNumberMessage) realData.getResult().get(i);
                            // 将读到的数据逐条插入到相应的表中
                            iSqlHelper.Insert(msg);
                            if (msg.Indexmy.equals("1")) {
                                indexmys[0] = msg.Indexmy;
                                names[0] = msg.PeopleName;
                                phones[0] = msg.Tel;
                                addresses[0] = msg.Address;
                                familyNumBtn1.setText(msg.PeopleName + "\n" + msg.Tel + "\n" + msg.Address);
                            } else if (msg.Indexmy.equals("2")) {
                                indexmys[1] = msg.Indexmy;
                                names[1] = msg.PeopleName;
                                phones[1] = msg.Tel;
                                addresses[1] = msg.Address;
                                familyNumBtn2.setText(msg.PeopleName + "\n" + msg.Tel + "\n" + msg.Address);
                            } else if (msg.Indexmy.equals("3")) {
                                indexmys[2] = msg.Indexmy;
                                names[2] = msg.PeopleName;
                                phones[2] = msg.Tel;
                                addresses[2] = msg.Address;
                                familyNumBtn3.setText(msg.PeopleName + "\n" + msg.Tel + "\n" + msg.Address);
                            }
                        }
                    } else {
                        DefaultTip(FamilyNumberActivity.this, "暂无数据");
                    }
                } else {
                    DefaultTip(FamilyNumberActivity.this, "数据解析失败");
                }
            }
        } else {
            DefaultTip(FamilyNumberActivity.this, "网络获取数据失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            //接收AddFamilyNumber中传过来的数据
            Bundle b = data.getExtras();
            familyNumBtn1.setText(b.getString("ret"));
        }
        if (requestCode == 2) {
            Bundle b = data.getExtras();
            familyNumBtn2.setText(b.getString("ret"));

        }
        if (requestCode == 3) {
            //接收数据
            Bundle b = data.getExtras();
            familyNumBtn3.setText(b.getString("ret"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isEmpty();
    }

    //从本地读取数据用来显示是联系人的信息还是“未指定联系人点击添加”
    private void isEmpty() {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        String whereString = "UserID='" + UserID + "'";
        List<Object> fnls = iSqlHelper.Query("com.LocationEntity.LocalFamilyNum", whereString);
        if (fnls.size() > 0) {
            for (int i = 0; i < fnls.size(); i++) {
                LocalFamilyNum localFamilyNum = (LocalFamilyNum) fnls.get(i);
                if (localFamilyNum.Indexmy.equals("1")) {
                    if (localFamilyNum.PeopleName != null
                            && localFamilyNum.Tel != null
                            && localFamilyNum.Address != null) {
                        familyNumBtn1.setText(localFamilyNum.PeopleName + "\n"
                                + localFamilyNum.Tel + "\n"
                                + localFamilyNum.Address);
                    } else {
                        familyNumBtn1.setText(R.string.family_unadded);
                    }
                    indexmys[0] = localFamilyNum.Indexmy;
                    names[0] = localFamilyNum.PeopleName;
                    phones[0] = localFamilyNum.Tel;
                    addresses[0] = localFamilyNum.Address;
                }
                if (localFamilyNum.Indexmy.equals("2")) {
                    if (localFamilyNum.PeopleName != null
                            && localFamilyNum.Tel != null
                            && localFamilyNum.Address != null) {
                        familyNumBtn2.setText(localFamilyNum.PeopleName + "\n"
                                + localFamilyNum.Tel + "\n"
                                + localFamilyNum.Address);
                    } else {
                        familyNumBtn2.setText(R.string.family_unadded);
                    }
                    indexmys[1] = localFamilyNum.Indexmy;
                    names[1] = localFamilyNum.PeopleName;
                    phones[1] = localFamilyNum.Tel;
                    addresses[1] = localFamilyNum.Address;
                }
                if (localFamilyNum.Indexmy.equals("3")) {
                    if (localFamilyNum.PeopleName != null
                            && localFamilyNum.Tel != null
                            && localFamilyNum.Address != null) {
                        familyNumBtn3.setText(localFamilyNum.PeopleName + "\n"
                                + localFamilyNum.Tel + "\n"
                                + localFamilyNum.Address);
                    } else {
                        familyNumBtn3.setText(R.string.family_unadded);
                    }
                    indexmys[2] = localFamilyNum.Indexmy;
                    names[2] = localFamilyNum.PeopleName;
                    phones[2] = localFamilyNum.Tel;
                    addresses[2] = localFamilyNum.Address;
                }
            }
        }else{
            familyNumBtn1.setText(R.string.family_unadded);
            familyNumBtn2.setText(R.string.family_unadded);
            familyNumBtn3.setText(R.string.family_unadded);
        }
    }
}
