package com.beautyhealthapp.PrivateDoctors.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.Entity.MedicalInfo;
import com.Entity.Reply;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.PrivateDoctors.Assistant.MedicalInfoListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/2.
 */
public class MedicalInfoActivity extends DataRequestActivity implements OnClickListener {
    private String currentNotiName = "MedicalInfoNotifications";
    private String medicalType;
    private ListView listview;
    private View headerView;
    private Button btn_addIssue, btn_myIssue ,btn_allIssue;
    private MedicalInfoListAdapter medicalInfoListAdapter;
    private ArrayList<MedicalInfo> medicalInfo;
    private ArrayList<ArrayList<Reply>> replyInfo;
    private ArrayList<Reply> replysItem;
    private String UserID;
    private EditText startTimeEd, endTimeEd;
    private String startTime, endTime;
    private Animation scaleAnim;
    public void setListItemes(ArrayList<MedicalInfo> medicalInfo, ArrayList<ArrayList<Reply>> replyInfo) {
        this.medicalInfo = medicalInfo;
        this.replyInfo = replyInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalinfo);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_search);
        initNavBar("医疗咨询", true, true);
        medicalInfo = new ArrayList<MedicalInfo>();
        replyInfo = new ArrayList<ArrayList<Reply>>();
        fetchUIFromLayout();
        setListener();
        medicalType = getIntent().getStringExtra("Type");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
        String Time = sDateFormat.format(new java.util.Date());
        startTime = Time + "-01 " + "00:00:00";
        endTime = Time + "-31 " + "23:59:59";
        UserID = "999";
        dataUpLoading(startTime, endTime, UserID);
    }

    private void fetchUIFromLayout() {
        listview = (ListView) findViewById(R.id.lv_medicalinfo);
        headerView = View.inflate(this, R.layout.mi_listview_header, null);
        listview.addHeaderView(headerView);//ListView条目中的悬浮部分 添加到头部
        btn_addIssue = (Button) findViewById(R.id.btn_pd_addIssue);
        btn_myIssue = (Button) findViewById(R.id.btn_pd_myIssue);
        btn_allIssue = (Button) findViewById(R.id.btn_pd_allIssue);
        btn_addIssue.setText("提问");
        btn_myIssue.setText("@我的问题");
        btn_allIssue.setText("全部问题");
        scaleAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);
    }

    private void setListener() {
        btn_addIssue.setOnClickListener(this);
        btn_myIssue.setOnClickListener(this);
        btn_allIssue.setOnClickListener(this);
        if (medicalInfoListAdapter == null) {
            medicalInfoListAdapter = new MedicalInfoListAdapter(this, medicalInfo, replyInfo, myListener);
        }
        listview.setAdapter(medicalInfoListAdapter);
    }

    private void dataUpLoading(String _startTime, String _endTime, String _userID) {
        dismissProgressDialog();
        showProgressDialog(MedicalInfoActivity.this, "加载中...");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("PrivateDoctor", "queryQuestion");
        Map requestCondition = new HashMap();
        String condition[] = {"StartTime", "EndTime", "UserID", "page", "rows", "DoctorType"};
        String value[] = {_startTime, _endTime, _userID, "-1", "18", medicalType};
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
            dataResult = dataDecode.decode(result, "MedicalInfo");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (CurrentAction == currentNotiName) {
                    if (realData.getResultcode().equals("1")&&realData.getResult().size()>0) {
                        medicalInfo.clear();
                        replyInfo.clear();
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            MedicalInfo medicalInfoitem = (MedicalInfo) realData.getResult().get(i);
                            replysItem = new ArrayList<Reply>();
                            replysItem.clear();
                            for (int j = 0; j < medicalInfoitem.Reply.size(); j++) {
                                Reply reply = (Reply) medicalInfoitem.Reply.get(j);
                                replysItem.add(reply);
                            }
                            replyInfo.add(replysItem);
                            medicalInfo.add(medicalInfoitem);
                        }
                        setListItemes(medicalInfo,replyInfo);
                        medicalInfoListAdapter.notifyDataSetChanged();
                    }else{
                        medicalInfo.clear();
                        replyInfo.clear();
                        setListItemes(medicalInfo,replyInfo);
                        medicalInfoListAdapter.notifyDataSetChanged();
                        ToastUtil.show(getApplicationContext(),"暂无数据");
                        return;
                    }
                }
            } else {
                DefaultTip(MedicalInfoActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(MedicalInfoActivity.this, "网络获取数据失败");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pd_addIssue) {
            btn_addIssue.startAnimation(scaleAnim);
            IsLoginTip(btn_addIssue);
        }
        if (v.getId() == R.id.btn_pd_myIssue) {
            btn_myIssue.startAnimation(scaleAnim);
            IsLoginTip(btn_myIssue);
        }
        if (v.getId() == R.id.btn_pd_allIssue) {
            btn_allIssue.startAnimation(scaleAnim);
            UserID="999";
            dataUpLoading(startTime, endTime, UserID);
        }
    }

    private void IsLoginTip(Button btn) {
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            if(btn==btn_addIssue){
                Intent _intent = new Intent();
                _intent.setClass(getApplicationContext(), AddIssueActivity.class);
                _intent.putExtra("Type", medicalType);
                startActivityForResult(_intent, 1);
            }
            if(btn==btn_myIssue){
                UserMessage userMessage = (UserMessage) list.get(0);
                UserID = userMessage.UserID;
                dataUpLoading(startTime, endTime, UserID);
            }
        } else {
            ToastUtil.show(this, "亲~，请您先登录");
        }
    }

    /**
     * 右按钮监听函数
     */
    public void onNavBarRightButtonClick(View view) {
        showTimeDialog();
    }

    private void showTimeDialog() {
        View itemview = getLayoutInflater().inflate(R.layout.showtimeseacherdialog, null);
        startTimeEd = (EditText) itemview.findViewById(R.id.ed_startTime);
        endTimeEd = (EditText) itemview.findViewById(R.id.ed_endTime);
        startTimeEd.setInputType(InputType.TYPE_NULL);
        endTimeEd.setInputType(InputType.TYPE_NULL);
        startTimeEd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(MedicalInfoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int month = (monthOfYear + 1);
                                String strdate = year + "-";
                                if (month < 10) {
                                    strdate = strdate + "0" + month + "-";
                                } else {
                                    strdate = strdate + month + "-";
                                }
                                if (dayOfMonth < 10) {
                                    strdate = strdate + "0" + dayOfMonth + " ";
                                } else {
                                    strdate = strdate + dayOfMonth + " ";
                                }
                                startTimeEd.setText(strdate + "00:00:00");
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endTimeEd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(MedicalInfoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int month = (monthOfYear + 1);
                                String strdate = year + "-";
                                if (month < 10) {
                                    strdate = strdate + "0" + month + "-";
                                } else {
                                    strdate = strdate + month + "-";
                                }
                                if (dayOfMonth < 10) {
                                    strdate = strdate + "0" + dayOfMonth + " ";
                                } else {
                                    strdate = strdate + dayOfMonth + " ";
                                }
                                endTimeEd.setText(strdate + "23:59:59");
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // 对话框
        new AlertDialog.Builder(MedicalInfoActivity.this).setView(itemview)
                .setTitle("提示：输入条件")
                .setPositiveButton("确定", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startTime = startTimeEd.getText().toString();
                        endTime = endTimeEd.getText().toString();
                        dataUpLoading(startTime, endTime, UserID);
                    }
                }).setNegativeButton("取消", null).setCancelable(false) // 触摸不消失
                .show();
        return;
    }

    private OnClickListener myListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = 0,parentposition = 0;
            String pos = (String) v.getTag();
            String temppos[] = pos.split(":");
            if (temppos.length == 1) {
                position = Integer.valueOf(temppos[0]);
            } else {
                position = Integer.valueOf(temppos[0]);
                parentposition = Integer.valueOf(temppos[1]);
            }
            Intent _intent = new Intent();
            if (v.getId() == R.id.btn_reply_pic) {
                _intent.putExtra("QAID", replyInfo.get(parentposition).get(position).auto_id);
                _intent.putExtra("TypeID", "1");
                jumpActivity(MedicalpicShowActivity.class, _intent);
            }
            if (v.getId() == R.id.btn_issue_pic) {
                _intent.putExtra("QAID", medicalInfo.get(position).auto_id);
                _intent.putExtra("TypeID", "0");
                jumpActivity(MedicalpicShowActivity.class,_intent);
            }
        }
    };

    private void jumpActivity(Class<?> cls,Intent _intent) {
        _intent.setClass(MedicalInfoActivity.this, cls);
        startActivity(_intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
            String Time = sDateFormat.format(new java.util.Date());
            startTime = Time + "-01 " + "00:00:00";
            endTime = Time + "-31 " + "23:59:59";
            dataUpLoading(startTime, endTime, UserID);
        }
    }
}
