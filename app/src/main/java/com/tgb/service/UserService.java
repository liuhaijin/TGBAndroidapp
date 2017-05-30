package com.tgb.service;

import com.tgb.model.User;

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

/**
 * Created by lenovo on 2017/4/13.
 */

public interface UserService {


    @POST("user")
    Call<Void> createUser(@Body User user);

    @PUT("user")
    Call<ResponseBody> updateUser(@Body User user);


    @GET("user/login/{username}/{password}")
    Call<User> getUser(@Path("username") String username, @Path("password") String password);


    @Multipart
    @POST("user/upLoadPic/{id}")
    Call<ResponseBody> upLoadPic(@Path("id") int id, @Part("filename") String description,
                                 @Part("user_pic\"; filename=\"image.png") RequestBody imgs);


}
