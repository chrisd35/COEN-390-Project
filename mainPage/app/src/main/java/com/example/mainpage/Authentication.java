package com.example.mainpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.mainpage.API.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Authentication {
    public interface DataCallback {
        void onDataLoaded(ArrayList<Double> dataList);

        void onFailure(String errorMessage);
    }
    private static final String SHARED_PREFS_NAME = "AppPrefs";
    private static final String AUTH_KEY = "Authentication";
    private Context context;

    public Authentication(Context context) {
        this.context = context;
    }
    public void logout(){
        Log.d("HardBLE","FHdbfjd");
        try{
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .loggedOut();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    boolean isAuthenticated;
                    String message;
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String body = response.body().string();
                            JSONObject jsonObj = new JSONObject(body);
                            isAuthenticated = jsonObj.getBoolean("Authentication");
                            message=jsonObj.getString("message");
                            setAuthenticated(isAuthenticated);
                            if (!isAuthenticated)
                                gotoMainActivity();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "NullResponse", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        Log.d("HardBLE",e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            Log.d("HardBLE",e.getMessage());
        }


    }
    public boolean isAuthenticated() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(AUTH_KEY, false);  // Default to false if not found
    }

    private void setAuthenticated(boolean isAuthenticated) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(AUTH_KEY, isAuthenticated);
        editor.apply();
    }
    public void verifyAuthentication() {
        Log.d("HardBLE","FHfdfsdfsdffdbfjd");
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .isAuthorized();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean isAuthenticated;
                String message;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        isAuthenticated = jsonObj.getBoolean("Authentication");
                        setAuthenticated(isAuthenticated);
                        if (!isAuthenticated) {
                            gotoMainActivity();
                            Toast.makeText(context, "You need to log in again", Toast.LENGTH_LONG).show();
                        }
                    } else {
//                        Toast.makeText(context, "NullResponse", Toast.LENGTH_LONG).show();

                    }


                } catch (Exception e) {
                    Log.d("HardBLE",e.getMessage());
//                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
    public void gotoMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);

        // Set flags to clear all existing activities and start a new task with MainActivity as the root
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);

        // If the context is an instance of Activity, finish it
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }}
