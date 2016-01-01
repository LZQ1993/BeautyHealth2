package com.beautyhealthapp.UserCenter.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.LocationEntity.LocalFamilyNum;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.List;
/**
 * Created by lenovo on 2015/12/30.
 */
public class AddFamilyNumberActivity extends NavBarActivity {
    private EditText et_name;
    private EditText et_phone;
    private EditText et_address;
    private Bundle bundle;
    private String indexmy;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfamilynumber);
        setRightpicID(R.mipmap.menu_save);
        initNavBar("亲情号设置", true, true);
        fetchUIFromLayout();
        Intent intent=getIntent();
        bundle=intent.getExtras();
        indexmy=bundle.getString("indexmy");
        ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
        List<Object> list=iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        UserMessage userMessage=(UserMessage) list.get(0);
        UserID = userMessage.UserID;
        if(bundle.getString("name")!=null&&bundle.getString("phone")!=null&&bundle.getString("address")!=null){
            et_name.setText(bundle.getString("name"));
            et_phone.setText(bundle.getString("phone"));
            et_address.setText(bundle.getString("address"));
        }
    }

    private void fetchUIFromLayout() {
        et_name=(EditText) findViewById(R.id.et_name);
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_address=(EditText) findViewById(R.id.et_address);
    }

    /**
     * 左按钮监听函数,返回
     */
    public void onNavBarLeftButtonClick(View view) {
        Bundle bundle=new Bundle();
        String  str = "未指定联系人点击添加";
        bundle.putString("ret", str);
        Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        if((et_name.getText().toString().equals("")||et_name.getText().toString()==null)
                ||(et_phone.getText().toString().equals("")||et_phone.getText().toString()==null)
                ||(et_address.getText().toString().equals("")||et_address.getText().toString()==null)){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("参数不能为空")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    return;
                        }
                    }).setCancelable(false).show();

        }else{
            ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
            LocalFamilyNum localFamilyNum=new LocalFamilyNum();
            //如果数据不为空，则将数据添加到本地内存
            if(bundle.getString("name")!=null){
                localFamilyNum.Indexmy=indexmy;
                localFamilyNum.PeopleName=et_name.getText().toString();
                localFamilyNum.Tel=et_phone.getText().toString();
                localFamilyNum.Address=et_address.getText().toString();
                localFamilyNum.UserID=UserID;
                String sqlstr = "update LocalFamilyNum set PeopleName='"+et_name.getText().toString()+"',Tel='"+et_phone.getText().toString()+"',Address='"+et_address.getText().toString()+"'  where UserID = '"+UserID+"'"+"and Indexmy='"+indexmy+"'";
                iSqlHelper.SQLExec(sqlstr);
                Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                    localFamilyNum.Indexmy=indexmy;
                    localFamilyNum.PeopleName=et_name.getText().toString();
                    localFamilyNum.Tel=et_phone.getText().toString();
                    localFamilyNum.Address=et_address.getText().toString();
                    localFamilyNum.UserID=UserID;
                    iSqlHelper.Insert(localFamilyNum);
                    valueReturn();
            }

        }
    }

    private void valueReturn() {
        //在AddFamilyNumber和FamilyNumberActivity之间传递信息，即修改“未指定联系人点击添加”为联系人信息
        //将数据放入bundle中在放入intent中
        Bundle bundlev=new Bundle();
        String str = "";
        if(et_name.getText().toString()!=null&&et_phone.getText().toString()!=null&&et_address.getText().toString()!=null){
            str = et_name.getText().toString()+"\n"+et_phone.getText().toString()+"\n"+et_address.getText().toString();
        }else{
            //将未指定联系人点击添加传回去，以免为空
            str = str+getApplicationContext().getString(R.string.family_unadded);
        }
        bundlev.putString("ret", str);
        Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
        intent.putExtras(bundlev);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)//主要是对这个函数的复写
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Bundle bundle=new Bundle();
            String  str = "未指定联系人点击添加";
            bundle.putString("ret", str);
            Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
