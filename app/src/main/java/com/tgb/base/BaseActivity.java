package com.tgb.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void showToast(String text){
		showToast(text, Toast.LENGTH_SHORT);
	}

	public void showToast(String text,int duration){
		Toast.makeText(BaseActivity.this, text, duration).show();
	}

}
