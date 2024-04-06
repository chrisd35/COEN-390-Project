package com.example.mainpage.API;

import com.example.mainpage.API.Model.AllDataSendRequest;
import com.example.mainpage.API.Model.DataSendRequest;
import com.example.mainpage.API.Model.ResetPasswordRequest;
import com.example.mainpage.API.Model.RetrieveData;
import com.example.mainpage.API.Model.SetThresholdRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    @GET("isAuthorized")
    Call<ResponseBody> isAuthorized();

    @DELETE("logOut")
    Call<ResponseBody> loggedOut();

    @POST("AddSoundData")
    Call<ResponseBody> AddSoundData(@Body DataSendRequest dataSendRequest);
    @POST("retrieveSoundData")
    Call<ResponseBody> retrieveSoundData(@Body RetrieveData soundRetrieveRequest);
    @POST("AddVOCData")
    Call<ResponseBody> AddVOCData(@Body DataSendRequest dataSendRequest);
    @POST("retrieveVOCData")
    Call<ResponseBody> retrieveVOCData(@Body RetrieveData soundRetrieveRequest);
    @POST("AddCO2Data")
    Call<ResponseBody> AddCO2Data(@Body DataSendRequest dataSendRequest);
    @POST("retrieveCO2Data")
    Call<ResponseBody> retrieveCO2Data(@Body RetrieveData soundRetrieveRequest);
    @POST("setSoundThreshold")
    Call<ResponseBody> setSoundThreshold(@Body SetThresholdRequest setThresholdRequest);
    @GET("getSoundThreshold")
    Call<ResponseBody> getSoundThreshold();
    @POST("AddData")
    Call<ResponseBody> AddData(@Body AllDataSendRequest allDataSendRequest);
    @POST("forgotpassword")
    Call<ResponseBody> SendForgotPasswordRequest(@Body ResetPasswordRequest resetPasswordRequest);

}
