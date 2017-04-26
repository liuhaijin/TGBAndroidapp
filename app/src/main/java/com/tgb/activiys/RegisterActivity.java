package com.tgb.activiys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tgb.R;
import com.tgb.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        finish();
    }


    public void backOnClick(View view) {
        finish();
    }
}
