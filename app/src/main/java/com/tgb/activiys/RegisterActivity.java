package com.tgb.activiys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tgb.R;
import com.tgb.base.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.password)
    EditText et_password;
    @InjectView(R.id.password_repeat)
    EditText et_password_repeat;

    @InjectView(R.id.btn_register)
    Button btn_register;

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

            }
        }else{
            showToast("密码格式错误");
        }
    }


    public void backOnClick(View view) {
        finish();
    }
}
