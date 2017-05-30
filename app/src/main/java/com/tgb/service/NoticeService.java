package com.tgb.service;

import com.tgb.model.Notice;
import com.tgb.model.NoticeCustom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lenovo on 2017/4/13.
 */

public interface NoticeService {
    @GET("notice")
    Call<List<NoticeCustom>> listNotices(@Query("start_id") int start_id);

    @GET("noticeMaxID")
    Call<Integer> getNoticeMaxID();
}
