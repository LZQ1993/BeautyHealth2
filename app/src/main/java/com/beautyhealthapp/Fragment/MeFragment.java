package com.beautyhealthapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beautyhealthapp.R;
import com.infrastructure.CWFragment.DataRequestFragment;

/**
 * Created by lenovo on 2015/12/25.
 */
public class MeFragment extends DataRequestFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }

}
