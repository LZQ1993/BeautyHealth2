package com.beautyhealthapp.PrivateDoctors.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.Entity.ReturnTransactionMessage;
import com.LocationEntity.UserMessage;
import com.beautyhealthapp.PrivateDoctors.Entity.Bimp;
import com.beautyhealthapp.PrivateDoctors.Entity.ImageItem;
import com.beautyhealthapp.PrivateDoctors.Entity.PublicWay;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.DataRequestActivity;
import com.infrastructure.CWDataDecoder.DataResult;
import com.infrastructure.CWDataDecoder.JsonDecode;
import com.infrastructure.CWDataRequest.RequestUtility;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.Res;
import com.infrastructure.CWUtilities.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lenovo on 2016/1/2.
 */
public class AddIssueActivity extends DataRequestActivity implements OnItemClickListener {
    private String currentNotiName = "IssueSubmitNotifications";
    private String UserID;
    private String medicalType;
    private EditText etcontent;
    private RadioGroup rg_contenttype;
    private String IsShared;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private String picpath;
    private Uri picuri;
    private File picfile;
    private String picname;
    public static Bitmap bimap;
    private static final int TAKE_PICTURE = 0x000001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PublicWay.activityList.add(this);
        Res.init(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_addissue, null);
        setContentView(parentView);
        Notifications.add(currentNotiName);
        setRightpicID(R.mipmap.menu_save);
        initNavBar("添加问题", true, true);
        fetchUIFromLayout();
        setListener();
        ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
        List<Object> list = iSqlHelper.Query("com.LocationEntity.UserMessage", null);
        if (list.size() > 0) {
            UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
        } else {
            UserID = "0000000";
        }
        medicalType = getIntent().getStringExtra("Type");
    }

    private void fetchUIFromLayout() {
        rg_contenttype = (RadioGroup) findViewById(R.id.rg_pd_contenttype);
        etcontent = (EditText) findViewById(R.id.content);
        // 改变默认选项
        rg_contenttype.check(R.id.rg_pd_contentissue_public);
        // 获取默认被被选中值
        if (rg_contenttype.getCheckedRadioButtonId() == R.id.rg_pd_contentissue_public) {
            IsShared = "1";
        } else {
            IsShared = "0";
        }
        initpop();
        noScrollgridview = (GridView) findViewById(R.id.Scrollgridview);
        // 选中背景
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);

    }

    private void initpop() {
        pop = new PopupWindow(AddIssueActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        // 背景设置 无阴影
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);// 获取焦点，设置当前窗体为操作窗体
        pop.setOutsideTouchable(true); // 外围点击dismiss
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        // 点击窗体
        parent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        // 点击拍照
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        // 点击相册
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AddIssueActivity.this, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        // 点击退出
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    private void setListener() {
        rg_contenttype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rg_contenttype.getCheckedRadioButtonId() == R.id.rg_pd_contentissue_public) {
                    IsShared = "1";
                } else {
                    IsShared = "0";
                }
            }
        });
        noScrollgridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == Bimp.tempSelectBitmap.size()) {
            popTip();
        } else {
            Intent intent = new Intent(AddIssueActivity.this, GalleryActivity.class);
            intent.putExtra("position", "1");
            intent.putExtra("ID", position);
            startActivity(intent);
        }
    }

    private void popTip() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(parentView.getWindowToken(), 0); // 强制隐藏键盘
        ll_popup.startAnimation(AnimationUtils.loadAnimation(AddIssueActivity.this, R.anim.activity_translate_in));
        pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }

    public void photo() {
        Intent openCameraIntent = new Intent();
        openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        picname = "IMG_" + DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        picpath = "/sdcard/DCIM/Camera/" + picname;
        picfile = new File(picpath);
        picuri = Uri.fromFile(picfile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picuri); // 设置拍照的照片存储在哪个位置。
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            ToastUtil.show(getApplicationContext(), "SD卡错误!");
            return;
        }
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            photoResult(resultCode);
        }
    }

    private void photoResult(int resultCode) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, picuri));
        if (Bimp.tempSelectBitmap.size() < 8 && resultCode == RESULT_OK) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(picuri), null, options);
                options.inSampleSize = 4;
                options.inJustDecodeBounds = false;
                Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(picuri), null, options);
                ImageItem takePhoto = new ImageItem();
                takePhoto.setBitmap(photo);
                takePhoto.setImagePath(picpath);
                takePhoto.setName(picname);
                Bimp.tempSelectBitmap.add(takePhoto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNavBarLeftButtonClick(View view) {
        etcontent.setText("");
        rg_contenttype.check(R.id.rg_pd_contentissue_public);
        Bimp.tempSelectBitmap.clear();
        Bimp.max = 0;
        adapter.update();
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }

    @Override
    public void onNavBarRightButtonClick(View view) {
        if (etcontent.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请填写您的病情简例!")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).setCancelable(false).show();
        } else {
            submitIssue();
        }
    }

    private void submitIssue() {
        dismissProgressDialog();
        showProgressDialog(AddIssueActivity.this,"上传中...");
        RequestUtility myru = new RequestUtility();
        myru.setIP(null);
        myru.setMethod("PrivateDoctor", "uploadQuestion");
        Map requestCondition = new HashMap();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String QuestionTime = sDateFormat.format(new java.util.Date());
        String condition[] = {"UserID", "QuestionContent", "QuestionTime", "IsShared", "DoctorType"};
        String value[] = {UserID, etcontent.getText().toString(),
                QuestionTime, IsShared, medicalType};
        String strJson = JsonDecode.toJson(condition, value);
        requestCondition.put("json", strJson);
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            picfile = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
            requestCondition.put("image" + i, picfile);
        }
        myru.setParams(requestCondition);
        myru.setNotification(currentNotiName);
        setRequestUtility(myru);
        requestData();
    }

    @Override
    public void updateView() {
        dismissProgressDialog();
        if (result != null) {
            dataResult = dataDecode.decode(result, "ReturnTransactionMessage");
            if (dataResult != null) {
                DataResult realData = (DataResult) dataResult;
                if (CurrentAction == currentNotiName) {
                    if (realData.getResultcode().equals("1") && realData.getResult().size() > 0) {
                        ReturnTransactionMessage msg = (ReturnTransactionMessage) realData.getResult().get(0);
                        if(msg.result.equals("1")) {
                            new AlertDialog.Builder(AddIssueActivity.this)
                                    .setTitle("提示")
                                    .setMessage(msg.tip)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            etcontent.setText("");
                                            Bimp.tempSelectBitmap.clear();
                                            Bimp.max = 0;
                                            adapter.update();
                                            return;
                                        }
                                    }).setCancelable(false).show();
                        }else{
                            ToastUtil.show(getApplicationContext(),msg.tip);
                            return;
                        }
                    } else {
                        DefaultTip(AddIssueActivity.this, "暂无数据");
                    }
                }
            } else {
                DefaultTip(AddIssueActivity.this, "数据解析失败");
            }
        } else {
            DefaultTip(AddIssueActivity.this, "网络获取数据失败");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)
                && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            etcontent.setText("");
            rg_contenttype.check(R.id.rg_pd_contentissue_public);
            Bimp.tempSelectBitmap.clear();
            Bimp.max = 0;
            adapter.update();
            Intent intent = new Intent();
            setResult(1, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 4) {
                return 4;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_unfocused));
                if (position == 4) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
                        .getBitmap());
            }
            return convertView;

        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

}
