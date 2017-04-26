package com.tgb.activiys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tgb.R;
import com.tgb.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void registerAccount(View view){
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
    }

    public void backOnClick(View view) {
        finish();
    }

}
