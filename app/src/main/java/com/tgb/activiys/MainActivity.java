package com.tgb.activiys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;

import com.tgb.R;
import com.tgb.base.BaseActivity;
import com.tgb.fragment.ChatFragment;
import com.tgb.fragment.MeFragment;
import com.tgb.fragment.NoticeFragment;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.rb_notice)
    RadioButton rb_notice;
    @InjectView(R.id.rb_chat)
    RadioButton rb_chat;
    @InjectView(R.id.rb_me)
    RadioButton rb_me;

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
        fragment_chat = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        fragment_me = (MeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_me);

        fragment_list.add(fragment_notice);
        fragment_list.add(fragment_chat);
        fragment_list.add(fragment_me);

        rb_notice.setChecked(true);
        activeFragment("notice");
    }


    private List<Fragment> fragment_list = new LinkedList<>();
    private NoticeFragment fragment_notice = null;
    private ChatFragment fragment_chat = null;
    private MeFragment fragment_me = null;
    private Fragment mCurrentFragment = null;

    private class TabBarButton_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String tag = v.getTag().toString();
            activeFragment(tag);
        }
    }

    private void activeFragment(String tag) {

        Fragment nFragment = null;

        switch (tag) {
            case "notice":
                nFragment = fragment_notice;
                break;

            case "chat":
                nFragment = fragment_chat;
                break;

            case "me":
                nFragment = fragment_me;
                break;

        }

        if(mCurrentFragment == nFragment)
            return;

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

}
