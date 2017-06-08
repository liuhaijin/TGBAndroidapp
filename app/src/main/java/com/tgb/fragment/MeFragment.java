package com.tgb.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tgb.R;
import com.tgb.activiys.AvatarActivity;
import com.tgb.activiys.EditInfoActivity;
import com.tgb.activiys.FollowActivity;
import com.tgb.activiys.LoginActivity;
import com.tgb.activiys.MyFindActivity;
import com.tgb.app.AppProfile;
import com.tgb.app.AppState;
import com.tgb.model.User;
import com.tgb.model.UserInfo;
import com.tgb.service.FollowService;
import com.tgb.service.UserService;
import com.tgb.utils.PreferencesUtils;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lenovo on 2017/4/17.
 */

public class MeFragment extends Fragment implements MeMessage{

    //头像，性别，昵称
    @InjectView(R.id.iv_user_avatar)
    ImageView iv_user_avatar;
    @InjectView(R.id.iv_user_gender)
    ImageView iv_user_gender;
    @InjectView(R.id.tv_nickname)
    TextView tv_nickname;

    //所在地，微信，个性签名
    @InjectView(R.id.tv_loaction)
    TextView tv_loaction;
    @InjectView(R.id.tv_wechat)
    TextView tv_wechat;
    @InjectView(R.id.tv_signature)
    TextView tv_signature;


    //关注，粉丝，动态
    @InjectView(R.id.ll_focus)
    LinearLayout ll_focus;
    @InjectView(R.id.tv_focusNum)
    TextView tv_focusNum;

    @InjectView(R.id.ll_fans)
    RelativeLayout ll_fans;
    @InjectView(R.id.tv_fansNum)
    TextView tv_fansNum;
    @InjectView(R.id.rl_red_dot)
    RelativeLayout rl_red_dot;
    @InjectView(R.id.tv_new_fans)
    TextView tv_new_fans;

    @InjectView(R.id.ll_find)
    LinearLayout ll_find;
    @InjectView(R.id.tv_findNum)
    TextView tv_findNum;



    //关于通告帮，检查更新
    @InjectView(R.id.tv_about_tgb)
    TextView tv_about_tgb;
    @InjectView(R.id.tv_check_update)
    TextView tv_check_update;

    //登录/登出 按钮
    @InjectView(R.id.btn_login)
    Button btn_login;

    ProgressDialog progressDialog;

    @InjectView(R.id.rl_location)
    RelativeLayout rl_location;
    @InjectView(R.id.rl_wechat)
    RelativeLayout rl_wechat;
    @InjectView(R.id.rl_signature)
    RelativeLayout rl_signature;

    public static final int DEFAULT_TIMEOUT = 5;
    Retrofit retrofit;

    private int new_fans_num = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_me, container);
        ButterKnife.inject(this, view);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppProfile.getBaseAddress)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        initView(true, "登录中");
        setListener();

        return view;
    }

    private void setListener(){

        iv_user_avatar.setOnClickListener(new MainInfoOnClickListener());
        iv_user_gender.setOnClickListener(new MainInfoOnClickListener());
        tv_nickname.setOnClickListener(new MainInfoOnClickListener());

        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(AppState.isLogin) {
                    new AlertDialog.Builder(getActivity()).setTitle("确定退出当前账号？")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                PreferencesUtils.putSharePre("isLogin", false);
                                AppState.user = new User();
                                initView(true, "正在登出...");

                                }
                            }).setPositiveButton("取消", null).show();

                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
                }
            }
        });

        rl_location.setOnClickListener(new InfoOnClickListener());
        rl_wechat.setOnClickListener(new InfoOnClickListener());
        rl_signature.setOnClickListener(new InfoOnClickListener());

        ll_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppState.isLogin) {
                    Intent intent = new Intent(getActivity(), FollowActivity.class);
                    intent.putExtra("followType", true);
                    startActivityForResult(intent, FOLLOW_DATA_CHANGE);
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
                }
            }
        });

        ll_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppState.isLogin) {
                    Intent intent = new Intent(getActivity(), FollowActivity.class);
                    intent.putExtra("followType", false);
                    intent.putExtra("new_fans_num", new_fans_num);
                    startActivityForResult(intent, FOLLOW_DATA_CHANGE);
                    new_fans_num = 0;
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
                }
            }
        });

        ll_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppState.isLogin) {
                    startActivityForResult(new Intent(getActivity(), MyFindActivity.class), FOLLOW_DATA_CHANGE);
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
                }
            }
        });
    }

    private void initView(final boolean showDialog, String message){

        AppState.isLogin = PreferencesUtils.getSharePreBoolean("isLogin");
        final String username = PreferencesUtils.getSharePreStr("username");
        final String password = PreferencesUtils.getSharePreStr("password");

        if(AppState.isLogin) {
            if(showDialog){
                showProgressDialog(getResources().getString(R.string.app_name), message);
            }


            Call call = retrofit.create(UserService.class).getUser(username, password);
            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    hideProgressDialog();
                    if(response.code() == 200){
                        AppState.user = response.body();
                        Log.i("AppState.user", AppState.user.toString());
                        if(AppState.user.getAvatarPic() == null){
                            iv_user_avatar.setImageResource(R.mipmap.avatar);
                        }else{
                            Glide.with(MeFragment.this)
                                    .load(AppProfile.getBaseAddress + "/user/getPic/" + AppState.user.getAvatarPic())
                                    .error(R.mipmap.avatar)
                                    .into(iv_user_avatar);
                        }

                        iv_user_gender.setImageResource(AppState.user.getGender() ? R.mipmap.ic_profile_male : R.mipmap.ic_profile_female);

                        tv_nickname.setText(AppState.user.getNickname());

                        tv_loaction.setText(AppState.user.getLocation() == null ? "未设置" : AppState.user.getLocation());

                        tv_wechat.setText(AppState.user.getWechat() == null ? "未设置" : AppState.user.getWechat());

                        tv_signature.setText(AppState.user.getSignature().equals("") ? "这个人很懒，什么都没有写" : AppState.user.getSignature());

                        int localFollowNum = PreferencesUtils.getSharePreInt("followNum");
                        int localFansNum = PreferencesUtils.getSharePreInt("fansNum");
                        int localFindsNum = PreferencesUtils.getSharePreInt("findNum");

                        tv_focusNum.setText(localFollowNum+"");
                        tv_fansNum.setText(localFansNum+"");
                        tv_findNum.setText(localFindsNum+"");

                    }else{
                        PreferencesUtils.putSharePre("isLogin", false);
                        initView(true, "请重新登录");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    hideProgressDialog();
                    PreferencesUtils.putSharePre("isLogin", false);
                    initView(false, "");
                }
            });

            btn_login.setBackgroundResource(R.drawable.selector_button_logout);
            btn_login.setText("退出登录");

        }else{
            iv_user_avatar.setImageResource(R.mipmap.icon);
            iv_user_gender.setImageResource(R.mipmap.ic_profile_male);
            tv_nickname.setText("未注册");

            tv_loaction.setText("未设置");
            tv_wechat.setText("未设置");
            tv_signature.setText("这个人很懒，什么都没有写");

            btn_login.setBackgroundResource(R.drawable.selector_button);
            btn_login.setText("点击登录");
        }
    }

    private static final int LOAD_AVATAR_COMPLETE = 1;
    private static final int FINISH_SMS_VERIFICATION_TIME = 2;

    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case LOAD_AVATAR_COMPLETE:

                    break;
                case FINISH_SMS_VERIFICATION_TIME:

                    break;
            }
        }
    };

    private void showProgressDialog(String title, String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgressDialog(){
        if(progressDialog != null){
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    private static final int EDIT_MAIN_INFO = 11;
    private static final int EDIT_NORMAL_INFO = 12;
    private static final int LOGIN_OR_REGISTER_ACCOUNT = 13;
    private static final int FOLLOW_DATA_CHANGE = 14;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDIT_MAIN_INFO:
                if(resultCode == 1){
                    initView(true, "");
                }
                break;
            case EDIT_NORMAL_INFO:
                if(resultCode == 1){
                    initView(false, null);
                }
                break;
            case LOGIN_OR_REGISTER_ACCOUNT:
                if(resultCode == 1){
                    Log.i("onActivityResult", "LOGIN_OR_REGISTER_ACCOUNT");
                    initView(true, "正在登录...");
                }
                break;
            case FOLLOW_DATA_CHANGE:
                loadNew();
                break;
        }
    }

    @Override
    public void loadNew() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNewestUserInfo();
            }
        }, 500);
    }

    public void loadNewestUserInfo(){//获取用户最新关注，粉丝，动态统计，并更新UI

        if(!AppState.isLogin){//未登录取消
            return;
        }

        int DEFAULT_TIMEOUT = 5;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppProfile.getBaseAddress)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Call<UserInfo> call = retrofit.create(UserService.class).getUserInfo(AppState.user.getIdUser());

        call.enqueue(new retrofit2.Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                Log.i("onResponse", response.code()+"");
                if(response.code() == 200){
                    Log.i("UserInfo", "get newest info");
                    int localFollowNum = PreferencesUtils.getSharePreInt("followNum");
                    int localFansNum = PreferencesUtils.getSharePreInt("fansNum");
                    int localFindsNum = PreferencesUtils.getSharePreInt("findNum");

                    int remoteFollowNum = response.body().getFollowNum();
                    int remoteFansNum = response.body().getFansNum();
                    int remoteFindsNum = response.body().getFindNum();

                    if(remoteFollowNum != localFollowNum){
                        tv_focusNum.setText(remoteFollowNum+"");
                        PreferencesUtils.putSharePre("followNum", remoteFollowNum);
                    }else{
                        tv_focusNum.setText(localFollowNum+"");
                    }

                    if(remoteFansNum > localFansNum){
                        rl_red_dot.setVisibility(View.VISIBLE);
                        new_fans_num = remoteFansNum - localFansNum;
                        tv_new_fans.setText(new_fans_num + "");
                        PreferencesUtils.putSharePre("fansNum", remoteFansNum);
                        tv_fansNum.setText(remoteFansNum+"");
                    }else if(remoteFansNum < localFansNum){
                        rl_red_dot.setVisibility(View.GONE);
                        PreferencesUtils.putSharePre("fansNum", remoteFansNum);
                        tv_fansNum.setText(remoteFansNum+"");
                    }else{
                        tv_fansNum.setText(localFansNum+"");
                        rl_red_dot.setVisibility(View.GONE);
                    }

                    if(remoteFindsNum != localFindsNum){
                        PreferencesUtils.putSharePre("findNum", remoteFindsNum);
                        tv_findNum.setText(remoteFindsNum+"");
                    }else{
                        tv_findNum.setText(localFindsNum+"");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.i("Notice Throwable", t.toString());
                Toast.makeText(getActivity(), "服务器开小差啦(⊙ˍ⊙)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class InfoOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(AppState.isLogin) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                switch (v.getTag().toString()){
                    case "location":
                        intent.putExtra("typeId", 1);
                        break;
                    case "wechat":
                        intent.putExtra("typeId", 2);
                        break;
                    case "signature":
                        intent.putExtra("typeId", 3);
                        break;
                }
                startActivityForResult(intent, EDIT_MAIN_INFO);
            }else{
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
            }
        }
    }

    private class MainInfoOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(AppState.isLogin) {
                Intent intent = new Intent(getActivity(), AvatarActivity.class);
                startActivityForResult(intent, EDIT_MAIN_INFO);
            }else{
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
            }
        }
    }
}
