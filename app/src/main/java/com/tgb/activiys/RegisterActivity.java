package com.tgb.activiys;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tgb.R;
import com.tgb.app.AppProfile;
import com.tgb.app.AppSQLiteHelper;
import com.tgb.app.AppState;
import com.tgb.base.BaseActivity;
import com.tgb.model.User;
import com.tgb.service.UserService;
import com.tgb.utils.PreferencesUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.password)
    EditText et_password;
    @InjectView(R.id.password_repeat)
    EditText et_password_repeat;

    @InjectView(R.id.btn_register)
    Button btn_register;

    public static final int DEFAULT_TIMEOUT = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {

    }

    public void doRegister(View view){
        String password = et_password.getText().toString();
        if(password == null){
            showToast("密码不能为空");
            return;
        }
        // 手机号验证规则
        String regEx = "^[a-zA-Z0-9]{6,21}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        // 字符串是否与正则表达式相匹配
        if(matcher.matches()){
            if(et_password_repeat.getText().toString().equals(password)){
                AppState.user.setPassword(password);
                //register account and put preferences
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

                Call call = retrofit.create(UserService.class).createUser(AppState.user);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.i("response.code", response.code()+"");
                        if(response.code() == 409){
                            showToast("手机号已存在，请登录或重新注册");
                        }else if(response.code() == 201){
                            showToast("注册成功");
                            PreferencesUtils.putSharePre("username", AppState.user.getUsername());
                            PreferencesUtils.putSharePre("password", AppState.user.getPassword());
                            PreferencesUtils.putSharePre("isLogin", true);
                            setResult(1);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.i("createUser throwable", t.toString());
                        Toast.makeText(RegisterActivity.this, "网络错误(⊙ˍ⊙)", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                showToast("两次输入不一致");
            }
        }else{
            showToast("密码格式错误");
        }
    }


    public void backOnClick(View view) {
        finish();
    }

}
