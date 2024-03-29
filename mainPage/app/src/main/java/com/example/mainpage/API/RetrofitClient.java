package com.example.mainpage.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
//    private static final String BASE_URL= "https://coen390backend.nn.r.appspot.com";
    //Testing locally
private static final String BASE_URL= "http://192.168.0.11:3000";
    private static  RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }
    public static synchronized RetrofitClient getInstance(){
        if(mInstance==null){
            mInstance=new RetrofitClient();
        }
        return  mInstance;
    }
    public APIservice getApi(){
        return retrofit.create(APIservice.class);
    }


}