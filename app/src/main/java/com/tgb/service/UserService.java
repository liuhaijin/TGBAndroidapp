package com.tgb.service;

import com.tgb.model.User;
import com.tgb.model.UserInfo;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lenovo on 2017/4/13.
 */

public interface UserService {


    @POST("user")
    Call<Void> createUser(@Body User user);

    @PUT("user")
    Call<ResponseBody> updateUser(@Body User user);

    @PUT("user/useDefaultCover")
    Call<Void> useDefaultCover(@Body User user);

    @POST("user/loadUserInfo")
    Call<User> loadUserInfo(@Body User user);

    @GET("user/login/{username}/{password}")
    Call<User> getUser(@Path("username") String username, @Path("password") String password);

    @GET("user/getInfo/{id}")
    Call<UserInfo> getUserInfo(@Path("id") int id);

    @Multipart
    @POST("user/upLoadPic/{id}")
    Call<ResponseBody> upLoadPic(@Path("id") int id, @Part("filename") String description,
                                 @Part("pic\"; filename=\"image.png") RequestBody imgs);

    @Multipart
    @POST("user/upLoadPic/{id}")
    Call<ResponseBody> upLoadPic(@Path("id") int id, @Query("type") String type,
                                 @Part("filename") String description,
                                 @Part("pic\"; filename=\"image.png") RequestBody imgs);
}
