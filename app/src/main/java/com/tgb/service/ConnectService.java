package com.tgb.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telecom.ConnectionService;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tgb.app.AppProfile;
import com.tgb.model.Notice;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ConnectService extends Service {
	
	private static ConnectService mInstance = null;
    private Thread pullDataThread;
    private Call<List<Notice>> call;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static ConnectService getInstance(){
    	return mInstance;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ConnectService", "onStartCommand");
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppProfile.getBaseAddress)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        call = retrofit.create(NoticeService.class).listNotices(-1);

        pullDataThread = new Thread(new PullDataRunnable());
        pullDataThread.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pullDataThread = null;
    }

    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Call<List<Notice>> cur_call = call.clone();
            cur_call.enqueue(new retrofit2.Callback<List<Notice>>() {
                @Override
                public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                    List<Notice> list = response.body();
                    for(Notice n : list){
                        Log.i("Notice", n.toString());
                    }
                }

                @Override
                public void onFailure(Call<List<Notice>> call, Throwable t) {
                    Log.i("Notice", t.toString());
                }
            });
        }
    };

    private class PullDataRunnable implements Runnable {

        @Override
        public void run() {
            while(true){
                try {
                    Log.i("thread", "callData");
                    handler.sendEmptyMessage(1);
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
