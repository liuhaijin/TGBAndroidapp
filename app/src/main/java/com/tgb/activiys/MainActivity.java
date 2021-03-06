package com.tgb.activiys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tgb.R;
import com.tgb.adapter.RecyclerViewAdapter;
import com.tgb.app.AppProfile;
import com.tgb.base.BaseActivity;
import com.tgb.model.Notice;
import com.tgb.service.ConnectService;
import com.tgb.service.NoticeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    boolean isLoading;
    private List<Notice> data = new ArrayList<Notice>();
    private RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, data);
    private Call<List<Notice>> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.inject(this);
        initView();
        initData();
    }

    public void initView() {
        setSupportActionBar(toolbar);

        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear();
                getData();
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });

        //添加点击事件
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("test", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    public void initData() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppProfile.getBaseAddress)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        call = retrofit.create(NoticeService.class).listNotices();
        getData();

//        startService(new Intent(this, ConnectService.class));

//        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.i("androidId", androidId);
    }

    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyItemRemoved(adapter.getItemCount());
        }
    };

    /**
     * 获取测试数据
     */
    private void getData() {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        Call<List<Notice>> cur_call = call.clone();
        cur_call.enqueue(new retrofit2.Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                data.clear();
                data.addAll(response.body());
                for(Notice n : data){
                    Log.i("Notice", n.toString());
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyItemRemoved(adapter.getItemCount());
            }

            @Override
            public void onFailure(Call<List<Notice>> call, Throwable t) {
                Log.i("Notice Throwable", t.toString());
            }
        });

    }

}
