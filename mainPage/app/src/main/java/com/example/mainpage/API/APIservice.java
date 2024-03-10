package com.example.mainpage.API;

import com.example.mainpage.API.Model.SoundDataSendRequest;
import com.example.mainpage.API.Model.SoundRetrieveData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface APIservice {

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signup(
            @Field("username") String username,
            @Field("password") String password
    );
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
        );
    @FormUrlEncoded
    @GET("isAuthorized")
    Call<ResponseBody> isAuthorized();

    @POST("AddSoundData")
    Call<ResponseBody> AddSoundData(@Body SoundDataSendRequest soundDataSendRequest);
    @POST("retrieveSoundData")
    Call<ResponseBody> retrieveSoundData(@Body SoundRetrieveData soundRetrieveRequest);



}