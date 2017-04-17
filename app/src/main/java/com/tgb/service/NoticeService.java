package com.tgb.service;

import com.tgb.model.Notice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by lenovo on 2017/4/13.
 */

public interface NoticeService {
    @GET("notice")
    Call<List<Notice>> listNotices();

    @GET("noticeMaxID")
    Call<Integer> getNoticeMaxID();
}
