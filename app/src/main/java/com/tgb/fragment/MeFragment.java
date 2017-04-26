package com.tgb.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tgb.R;
import com.tgb.activiys.LoginActivity;

import butterknife.ButterKnife;

/**
 * Created by lenovo on 2017/4/17.
 */

public class MeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_me, container);
        ButterKnife.inject(this, view);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        return view;
    }

}
