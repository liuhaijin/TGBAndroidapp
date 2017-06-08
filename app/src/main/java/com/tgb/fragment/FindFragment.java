package com.tgb.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tgb.R;
import com.tgb.activiys.LoginActivity;
import com.tgb.adapter.FindAdapter;
import com.tgb.adapter.NoticeAdapter;
import com.tgb.app.AppProfile;
import com.tgb.app.AppState;
import com.tgb.model.FindCustom;
import com.tgb.service.FindService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lenovo on 2017/4/17.
 */

public class FindFragment extends Fragment implements FindMessage {


    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    boolean isLoading;
    private List<FindCustom> data = new ArrayList();
    private FindAdapter adapter;
    private Retrofit retrofit;
    public static final int DEFAULT_TIMEOUT = 5;

    private int type = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find, container);
        ButterKnife.inject(this, view);
        initView();
        initData();
        return view;
    }

    public void initView() {

        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear();
                getData(type, -1);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FindAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("test", "StateChanged = " + newState);


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.d("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount() && adapter.getFooterType() == NoticeAdapter.FOOTER_LOAD_MORE) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }

                    if (!isLoading) {
                        isLoading = true;
                        if(data.size() > 15){
                            isLoading = false;
                            adapter.setFooterType(NoticeAdapter.FOOTER_NO_ITEM);
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.notifyItemRemoved(adapter.getItemCount());
                            return;
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData(type, data.get(data.size()-1).getIdFind()-1);
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
    }


    public void initData() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        //retrofit底层用的okHttp,所以设置超时还需要okHttp
        //然后设置5秒超时
        //其中DEFAULT_TIMEOUT是我这边定义的一个常量
        //TimeUnit为java.util.concurrent包下的时间单位
        //TimeUnit.SECONDS这里为秒的单位
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppProfile.getBaseAddress)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

//        getData(-1);

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
    private void getData(int type, int start_id) {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        int uid = -1;
        //默认为所有人，未登录
        Call call = retrofit.create(FindService.class).listFinds(uid, start_id);

        if(type == 1){//我的关注
            if(AppState.isLogin){//已登录
                uid = AppState.user.getIdUser();
                call = retrofit.create(FindService.class).listMyFollows(uid, start_id);
            }else{//未登录
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }else{//所有人
            if(AppState.isLogin){//已登录
                uid = AppState.user.getIdUser();
                call = retrofit.create(FindService.class).listFinds(uid, start_id);
            }
        }

        call.enqueue(new retrofit2.Callback<List<FindCustom>>() {
            @Override
            public void onResponse(Call<List<FindCustom>> call, Response<List<FindCustom>> response) {
                if(response.body() == null){
                    Log.i("Find", "reponse is null");
                    adapter.setFooterType(NoticeAdapter.FOOTER_NO_ITEM);
                }else{
                    adapter.setFooterType(NoticeAdapter.FOOTER_LOAD_MORE);
                    data.addAll(response.body());
                    for(FindCustom fc : response.body()){
                        Log.i("FindCustom", fc.toString());
                    }
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyItemRemoved(adapter.getItemCount());
            }

            @Override
            public void onFailure(Call<List<FindCustom>> call, Throwable t) {
                Log.i("Notice Throwable", t.toString());
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "服务器开小差啦(⊙ˍ⊙)", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void loadData(int type) {
        this.type = type;
        data.clear();
        getData(type, -1);
    }
}
