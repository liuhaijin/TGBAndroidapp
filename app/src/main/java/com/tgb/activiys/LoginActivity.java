package com.tgb.activiys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.tgb.R;
import com.tgb.app.AppProfile;
import com.tgb.app.AppState;
import com.tgb.base.BaseActivity;
import com.tgb.fragment.MeFragment;
import com.tgb.model.User;
import com.tgb.service.UserService;
import com.tgb.utils.PreferencesUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.smssdk.SMSSDK;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.et_username)
    EditText et_username;
    @InjectView(R.id.et_password)
    EditText et_password;

    public static final int DEFAULT_TIMEOUT = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        et_username.setText(PreferencesUtils.getSharePreStr("username"));
        et_password.setText(PreferencesUtils.getSharePreStr("password"));
    }

    public void loginAccount(View view){

        final String username = et_username.getText().toString();
        if(username == null){
            showToast("账号不能为空");
            return;
        }
        // 手机号验证规则
        String regEx = "^1[3|4|5|8][0-9]\\d{8}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(username);
        // 字符串是否与正则表达式相匹配
        if(!matcher.matches()){
            showToast("账号格式错误");
            return;
        }

        final String password = et_password.getText().toString();
        if(password == null){
            showToast("密码不能为空");
            return;
        }

        // 密码验证规则
        regEx = "^[a-zA-Z0-9]{6,21}$";
        // 编译正则表达式
        pattern = Pattern.compile(regEx);
        // 字符串是否与正则表达式相匹配
        if(!pattern.matcher(password).matches()){
            showToast("密码格式错误");
            return;
        }

        showProgressDialog(getResources().getString(R.string.app_name), "登录中");

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

        Call call = retrofit.create(UserService.class).getUser(username, password);
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                hideProgressDialog();
                if(response.code() == 200){
                    PreferencesUtils.putSharePre("username", username);
                    PreferencesUtils.putSharePre("password", password);
                    PreferencesUtils.putSharePre("isLogin", true);
                    setResult(1);
                    finish();
                }else{
                    showToast("密码错误");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                hideProgressDialog();
                showToast("网络错误");
            }
        });
    }

    public void registerAccount(View view){
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
    }

    public void forgetPassword(View view){
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivityForResult(intent, LOGIN_OR_REGISTER_ACCOUNT);
    }

    public void backOnClick(View view) {
        finish();
    }

    private static final int LOGIN_OR_REGISTER_ACCOUNT = 13;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case LOGIN_OR_REGISTER_ACCOUNT:
                if(resultCode == 1) {
                    setResult(1);
                    finish();
                }
                break;
        }
    }

}
