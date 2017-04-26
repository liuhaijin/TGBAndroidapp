package com.tgb.activiys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tgb.R;
import com.tgb.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class VerifyActivity extends BaseActivity {

    @InjectView(R.id.username)
    EditText et_username;
    @InjectView(R.id.verify_code)
    EditText et_verifyCode;

    @InjectView(R.id.btn_get_verify_code)
    Button btn_get_verify_code;
    @InjectView(R.id.btn_next)
    Button btn_next;

    Long sendSmsTime;
    Thread smsTimeThread;

    EventHandler eh;
    String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.inject(this);
        initView();
        initData();
    }

    private void initData() {
        eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //用户手机验证码校验成功
                        Log.i("afterEvent","用户手机验证码校验成功");
                        if(data != null){
                            for(String key : ((HashMap<String,Object>)data).keySet()){
                                Log.i("-----key-----"+key, ((HashMap<String,Object>)data).get(key).toString());
                            }
                        }
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //发送验证码成功
                        Log.i("afterEvent","发送验证码成功 "+data.toString());
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                        Log.i("afterEvent","返回支持发送验证码的国家列表");
                        if(data != null){
                            for(HashMap<String,Object> hm : (ArrayList<HashMap<String,Object>>) data){
                                for(String key : hm.keySet()){
                                    Log.i("---------"+key, hm.get(key).toString());
                                }
                            }
                        }
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    public void getVerifyCode(View view){
        phoneNumber = et_username.getText().toString();
        // 手机号验证规则
        String regEx = "^1[3|4|5|8][0-9]\\d{8}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phoneNumber);
        // 字符串是否与正则表达式相匹配
        if(matcher.matches()){
            SMSSDK.getVerificationCode("86", phoneNumber);
            showToast("验证码已发送");
            sendSmsTime = System.currentTimeMillis();
            smsTimeThread = new Thread(new SmsVerificationTimeRunnable());
            smsTimeThread.start();
        }else{
            showToast("手机号格式错误");
        }
    }

    private static final int REFRESH_SMS_VERIFICATION_TIME = 1;
    private static final int FINISH_SMS_VERIFICATION_TIME = 2;

    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_SMS_VERIFICATION_TIME:
                    btn_get_verify_code.setEnabled(false);
                    btn_get_verify_code.setText("("+ msg.arg1 +"s)");
                    break;
                case FINISH_SMS_VERIFICATION_TIME:
                    btn_get_verify_code.setEnabled(true);
                    btn_get_verify_code.setText("获取验证码");
                    break;
            }
        }
    };

    private class SmsVerificationTimeRunnable implements Runnable {

        @Override
        public void run() {
            while(true){
                try {
                    Message msg = Message.obtain();
                    int time = (int) (System.currentTimeMillis() - sendSmsTime) / 1000;
                    if(time < 10){
                        msg.what = REFRESH_SMS_VERIFICATION_TIME;
                        msg.arg1 = time;
                        handler.sendMessage(msg);
                    }else{
                        msg.what = FINISH_SMS_VERIFICATION_TIME;
                        handler.sendMessage(msg);
                        break;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void backOnClick(View view) {
        finish();
    }

    public void backNextClick(View view) {
        SMSSDK.submitVerificationCode("86", phoneNumber, et_verifyCode.getText().toString());
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
    }
}
