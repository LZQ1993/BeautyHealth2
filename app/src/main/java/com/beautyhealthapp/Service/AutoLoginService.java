package com.beautyhealthapp.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.UserMessage;
import com.infrastructure.CWDataDecoder.DataDecode;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.IDataDecode;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.HttpUtil;
import com.infrastructure.CWDataRequest.NetworkSetInfo;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by lenovo on 2016/1/3.
 */
public class AutoLoginService extends Service {

    private String UserID,PasswordType,Password;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        autologin();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void autologin() {
        String url = NetworkSetInfo.getServiceUrl() + "/UserManagerService/loginUserManager";
        RequestParams params = new RequestParams();
        String condition[] = {"UserID", "Password", "PasswordType"};
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        PasswordType = config.getString("PasswordType", "1");
        Password = config.getString("Password", "");
        UserID = config.getString("UserName", "");
        Log.e("111111111","类型"+PasswordType+"密码"+Password+"用户名"+UserID);
        String value[] = {UserID, Password, PasswordType};
        String strJson = JsonDecode.toJson(condition, value);
        params.put("json", strJson);
        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error) {
                Log.e("22222","类型"+PasswordType+"密码"+Password+"用户名"+UserID);
                ToastUtil.show(getApplicationContext(), "亲~,请保持网络畅通");
                Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                stopService(service);
                Log.e("333", "类型" + PasswordType + "密码" + Password + "用户名" + UserID);
                return;
            }
            @Override
            public void onSuccess(String content) {
                if (content != null) {
                    Log.e("444444","类型"+PasswordType+"密码"+Password+"用户名"+UserID);
                    IDataDecode dataDecode=new DataDecode();
                    Object dataResult = new Object();
                    dataResult = dataDecode.decode(content,"ReturnTransactionMessage");
                    DataResult realData = (DataResult) dataResult;
                    if (realData.getResultcode().equals("1")&&realData.getResult().size()>0) {
                        ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                        if (msg.getResult().equals("1")) {
                            ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
                            iSqlHelper.SQLExec("delete from UserMessage");
                            UserMessage userMessage = new UserMessage();
                            userMessage.UserID =UserID;
                            userMessage.Password = Password;
                            userMessage.UUID = msg.tip;
                            userMessage.PasswordType = PasswordType;
                            iSqlHelper.Insert(userMessage);
                            if (Password.equals("123456")) {
                                ToastUtil.show(getApplicationContext(), "登录成功,默认密码为：123456，建议您立即修改");
                                Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                                stopService(service);
                            } else {
                                ToastUtil.show(getApplicationContext(), "登录成功");
                                Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                                stopService(service);
                            }
                            Log.e("555555","类型"+PasswordType+"密码"+Password+"用户名"+UserID);
                        }else {
                            Toast.makeText(getApplicationContext(),"登录失败", Toast.LENGTH_SHORT).show();
                            Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                            stopService(service);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"登录失败", Toast.LENGTH_SHORT).show();
                        Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                        stopService(service);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"登录失败", Toast.LENGTH_SHORT).show();
                    Intent service = new Intent(getApplicationContext(), AutoLoginService.class);
                    stopService(service);
                }
            }
        });
    }
}
