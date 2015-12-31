package com.beautyhealthapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.LocationEntity.UserLocal;
import com.beautyhealthapp.PersonHealth.Activity.PersonHealthActivity;
import com.beautyhealthapp.PrivateDoctors.Activity.PrivateDoctorsActivity;
import com.beautyhealthapp.R;
import com.beautyhealthapp.SafeGuardianship.Activity.SafeGuardianshipActivity;
import com.infrastructure.CWFragment.DataRequestFragment;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;
import com.infrastructure.CWUtilities.ToastUtil;

import java.util.List;

/**
 * Created by lenovo on 2015/12/25.
 */
public class MainFragment extends DataRequestFragment implements OnClickListener,OnLongClickListener {
    private Button personHealth, privateDoctors, safeGuardianship, callCenter;
    private Animation scaleAnim;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        personHealth = (Button) view.findViewById(R.id.personHealth);
        privateDoctors = (Button) view.findViewById(R.id.privateDoctors);
        safeGuardianship = (Button) view.findViewById(R.id.safeGuardianship);
        callCenter = (Button) view.findViewById(R.id.callCenter);
        scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale);
        personHealth.setOnClickListener((OnClickListener) this);
        privateDoctors.setOnClickListener((OnClickListener) this);
        safeGuardianship.setOnClickListener((OnClickListener) this);
        callCenter.setOnClickListener((OnClickListener) this);
        callCenter.setOnLongClickListener((OnLongClickListener)this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == personHealth) {
            jumpActivity(personHealth, PersonHealthActivity.class);
        }
        if (v == privateDoctors) {
            jumpActivity(privateDoctors, PrivateDoctorsActivity.class);
        }
        if (v == safeGuardianship) {
            jumpActivity(safeGuardianship, SafeGuardianshipActivity.class);
        }
        if (v == callCenter) {
            callTelephoneTip();
        }
    }

    private void jumpActivity(View _view, Class<?> cls) {
        _view.startAnimation(scaleAnim);
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    private void callTelephoneTip() {
        callCenter.startAnimation(scaleAnim);
        ToastUtil.show(mContext,"为了避免误触，请您长按呼叫中心键");
        return;
    }

    @Override
    public boolean onLongClick(View v) {
        if(v==callCenter){
            callTelephoneAction();
        }
        return true;
    }

    private void callTelephoneAction() {
        ISqlHelper iSqlHelper = new SqliteHelper(null,mContext);
        List<Object> userLocal= iSqlHelper.Query("com.LocationEntity.UserLocal",null);
        if (userLocal.size() > 0) {
            UserLocal retuserLocal = (UserLocal) userLocal.get(0);
            if (retuserLocal.getTel().equals("") || retuserLocal.getTel() == null) {
                ToastUtil.show(mContext, "呼叫中心号码未设置");
            } else {
                Intent _intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + retuserLocal.getTel()));
                startActivity(_intent);
            }
        }else{
            ToastUtil.show(mContext, "呼叫中心未设置");
        }
    }
}
