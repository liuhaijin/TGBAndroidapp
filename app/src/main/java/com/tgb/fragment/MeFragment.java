package com.tgb.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tgb.R;
import com.tgb.activiys.LoginActivity;
import com.tgb.utils.PreferencesUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lenovo on 2017/4/17.
 */

public class MeFragment extends Fragment {

    @InjectView(R.id.iv_user_avatar)
    ImageView iv_user_avatar;
    @InjectView(R.id.iv_user_gender)
    ImageView iv_user_gender;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_me, container);
        ButterKnife.inject(this, view);
        initView();

        return view;
    }

    private void initView(){
        iv_user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PreferencesUtils.getSharePreBoolean("isLogin")){

                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
