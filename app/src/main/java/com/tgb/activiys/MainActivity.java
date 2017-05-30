package com.tgb.activiys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tgb.R;
import com.tgb.app.AppState;
import com.tgb.base.BaseActivity;
import com.tgb.fragment.FindFragment;
import com.tgb.fragment.MeFragment;
import com.tgb.fragment.NoticeFragment;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tv_title)
    TextView tv_title;
    @InjectView(R.id.tv_release)
    TextView tv_release;
    @InjectView(R.id.rb_notice)
    RadioButton rb_notice;
    @InjectView(R.id.rb_chat)
    RadioButton rb_chat;
    @InjectView(R.id.rb_me)
    RadioButton rb_me;
    @InjectView(R.id.ll_fifter)
    LinearLayout ll_filter;
    @InjectView(R.id.tv_fifter)
    TextView tv_filter;
    @InjectView(R.id.arraw_fifter)
    ImageView arrow_filter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initView();
    }

    public void initView() {
        setSupportActionBar(toolbar);

        rb_notice.setOnClickListener(new TabBarButton_Click());
        rb_chat.setOnClickListener(new TabBarButton_Click());
        rb_me.setOnClickListener(new TabBarButton_Click());

        fragment_notice = (NoticeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_notice);
        fragment_chat = (FindFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        fragment_me = (MeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_me);

        fragment_list.add(fragment_notice);
        fragment_list.add(fragment_chat);
        fragment_list.add(fragment_me);

        rb_notice.setChecked(true);
        activeFragment("notice");

        ll_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopupWindow();
            }
        });
    }

    private List<Fragment> fragment_list = new LinkedList<>();
    private NoticeFragment fragment_notice = null;
    private FindFragment fragment_chat = null;
    private MeFragment fragment_me = null;
    private Fragment mCurrentFragment = null;

    private class TabBarButton_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String tag = v.getTag().toString();
            activeFragment(tag);
        }
    }

    public void releaseOnClick(View view){
//        new AlertDialog.Builder(this)
//                .setTitle("通告帮")
//                .setMessage("公测版暂未开放发布通告功能，如有问题请联系邮箱1007705984@qq.com")
//                .setNegativeButton("确定", null).show();
        if(AppState.isLogin) {
            Intent intent = new Intent(this, ImageSelectorMainActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void activeFragment(String tag) {

        Fragment nFragment = null;
        String releaseText = "";

        switch (tag) {
            case "notice":
                nFragment = fragment_notice;
                tv_title.setText("通告帮");
                ll_filter.setVisibility(View.INVISIBLE);
                releaseText = "发布";
                break;

            case "chat":
                nFragment = fragment_chat;
                tv_title.setText("发现");
                ll_filter.setVisibility(View.VISIBLE);
                releaseText = "✚";
                break;

            case "me":
                nFragment = fragment_me;
                tv_title.setText("个人信息");
                ll_filter.setVisibility(View.INVISIBLE);
                releaseText = "封面";
                break;

        }

        if(mCurrentFragment == nFragment)
            return;
        tv_release.setText(releaseText);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(mCurrentFragment == fragment_notice){
            fragmentTransaction.setCustomAnimations(R.anim.slide_right_to_screen, R.anim.slide_screen_to_left);
        }else if(mCurrentFragment == fragment_me){
            fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_screen, R.anim.slide_screen_to_right);
        }else{
            if(tag.equals("notice")){
                fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_screen, R.anim.slide_screen_to_right);
            }else{
                fragmentTransaction.setCustomAnimations(R.anim.slide_right_to_screen, R.anim.slide_screen_to_left);
            }
        }

        for(Fragment f : fragment_list){
            if(f == nFragment){
                fragmentTransaction.show(f);
            }else{
                fragmentTransaction.hide(f);
            }
        }

        mCurrentFragment = nFragment;

        fragmentTransaction.commit();

    }

    private PopupWindow filterPopupWindow;

    // 显示筛选条件弹出框
    protected void showFilterPopupWindow() {
        // 判断popupWindow存在则不再初始化
        if (filterPopupWindow != null) {
            return;
        }
        // 获取popupwindow自定义布局
        View popupWindowView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_find, (ViewGroup) null);
        // 创建PopupWindow实例--获取焦点
        filterPopupWindow = new PopupWindow(popupWindowView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // 第一个参数---父view-这里是位置显示方式
//        filterPopupWindow.showAtLocation(ll_filter, Gravity.CENTER, 0, 0);

        filterPopupWindow.showAsDropDown(ll_filter, -ll_filter.getWidth() / 2, 0);

        arrow_filter.setImageResource(R.mipmap.fifter_selector_reverse);

        popupWindowView.findViewById(R.id.tv_filter_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("popupWindowView","tv_filter_all");
                tv_filter.setText("所有人");
                hidefilterPopupWindow();
            }
        });
        popupWindowView.findViewById(R.id.tv_filter_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("popupWindowView","tv_filter_me");
                tv_filter.setText("我的关注");
                hidefilterPopupWindow();
            }
        });

        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                filterPopupWindow = null;
                arrow_filter.setImageResource(R.mipmap.fifter_selector);
            }
        });
    }

    // 关闭筛选条件弹出框
    protected void hidefilterPopupWindow() {
        if (filterPopupWindow != null && filterPopupWindow.isShowing()) {
            // 关闭popupwindow
            filterPopupWindow.dismiss();
            filterPopupWindow = null;
        }
    }

}
