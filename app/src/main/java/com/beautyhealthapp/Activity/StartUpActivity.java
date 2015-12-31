package com.beautyhealthapp.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.Entity.Ad;
import com.LocationEntity.BluetoothState;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWComponent.AppInfo;
import com.infrastructure.CWComponent.ImageCal;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.LoadImage;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWFileSystem.IFileSystem;
import com.infrastructure.CWFileSystem.LocalFileSystem;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/12/23.
 */
public class StartUpActivity extends DataRequestActivity {
    private int reqWidth;
    private int reqHeight;
    private AppInfo appInfo;
    private View parentView;
    private ImageView ad;
    private IFileSystem myfilesystem;
    public String localPath;
    private String currentNotiName = "LoadingPicNotifications";
    private LoadImage load;
    private String webaddress;
    private String filename;
    private long size;
    private int resultflag;
    private BluetoothAdapter bluetoothAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifications.add(currentNotiName);
        WindowManager wm = this.getWindowManager();
        reqWidth = wm.getDefaultDisplay().getWidth();
        reqHeight = wm.getDefaultDisplay().getHeight();
        appInfo = new AppInfo(getApplicationContext());
        parentView = getLayoutInflater().inflate(R.layout.activity_startup,null);
        ad = (ImageView) parentView.findViewById(R.id.imageView1);
        myfilesystem = new LocalFileSystem(getApplicationContext());
        localPath = myfilesystem.getLocalPath();
        if (!appInfo.isNewVersion()) {
            getFiles(this, localPath);
            getadimage();
        }
        setContentView(parentView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> ls = iSqlHelper.Query("com.LocationEntity.BluetoothState", null);
        if (ls.size() > 0) {
            BluetoothState bs = (BluetoothState) ls.get(0);
            if (bs.State.equals("1")) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        } else {
            bluetoothAdapter.disable();
        }
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                if (appInfo.isNewVersion()) {
                    intent.setClass(StartUpActivity.this, IntroActivity.class);
                    intent.putExtra("goto", LoginActivity.class.getName());
                    AppInfo.markCurrentVersion();
                    startActivity(intent);
                    finish();
                } else {
                    intent.setClass(StartUpActivity.this, LoginActivity.class);
                    intent.putExtra("goto", MainActivity.class.getName());
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }

    public void getadimage() {
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("PicService", "queryPic");
        Map requestCondition = new HashMap();
        String condition[] = {"page", "rows", "MobileCode", "TypeID"};
        String value[] = {"-1", "3", "1", "1"};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    @Override
    public void updateView() {
        if (result != null) {
            dataResult = dataDecode.decode(result, "Ad");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (CurrentAction.equals(currentNotiName)) {
                    if (realData.getResultcode().equals("1")) {
                        load = new LoadImage(localPath);
                        for (int i = 0; i < realData.getResult().size(); i++) {
                            Ad msg = (Ad) realData.getResult().get(i);
                            webaddress = msg.getWebAddress();
                            filename = msg.getPathAndFileName();
                            size = Long.parseLong(msg.getSize());
                        }
                        updateImageShow();
                    } else {
                        ad.setImageResource(R.mipmap.app_startup);
                    }
                }
            } else {
                DefaultTip(StartUpActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(StartUpActivity.this, "网络获取数据失败");
        }
    }

    private void updateImageShow() {
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            loadImageOnView();
        }
    };

    private void loadImageOnView() {
        // TODO Auto-generated method stub
        try {
            resultflag = load.download(webaddress, filename, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String webfilename = filename.substring(filename.lastIndexOf("/") + 1);
        if (resultflag == 1) {
            File files = new File(localPath);
            File[] file = files.listFiles();
            for (int iFileLength = 0; iFileLength < file.length; iFileLength++) {
                if (!file[iFileLength].isDirectory()) {
                    String name = file[iFileLength].getName();
                    if ((name.trim().toLowerCase().endsWith(".jpg") || name
                            .trim().toLowerCase().endsWith(".png"))
                            & !name.equals(webfilename)) {
                        file[iFileLength].delete();
                    }
                }

            }
        }
    }

    private void getFiles(StartUpActivity startUpActivity, String url) {
        File files = new File(url);
        File[] file = files.listFiles();
        int fileLength = file.length;

        try {
            for (int iFileLength = 0; iFileLength < fileLength; iFileLength++) {
                if (!file[iFileLength].isDirectory()) {
                    String filename = file[iFileLength].getName();
                    if (filename.trim().toLowerCase().endsWith(".jpg")
                            || filename.trim().toLowerCase()
                            .endsWith(".png")) {
                        ad.setImageBitmap(ImageCal.decodeSampledBitmapFromResource(localPath + File.separator + filename,
                                reqWidth, reqHeight));
                    } else {
                        ad.setImageResource(R.mipmap.app_startup);
                    }
                } else {
                    ad.setImageResource(R.mipmap.app_startup);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // ����쳣��Ϣ
        }
    }


    @Override
    public void onBackPressed() {
        bluetoothAdapter.disable();
        finish();
    }

}
