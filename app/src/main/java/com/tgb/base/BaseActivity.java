package com.tgb.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tgb.R;

public abstract class BaseActivity extends AppCompatActivity {

	ProgressDialog progressDialog;

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

	public void showProgressDialog(String message){
		showProgressDialog(getResources().getString(R.string.app_name), message);
	}

	public void showProgressDialog(String title, String message){
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void hideProgressDialog(){
		if(progressDialog != null){
			progressDialog.cancel();
			progressDialog = null;
		}
	}

}
