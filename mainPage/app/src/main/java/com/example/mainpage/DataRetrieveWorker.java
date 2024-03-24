package com.example.mainpage;

import android.util.Log;

import com.example.mainpage.API.Model.SoundRetrieveData;
import com.example.mainpage.API.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DataRetrieveWorker {
    private static final String TAG = "DataRetrieveHelper";

    public interface DataCallback {
        void onDataLoaded(ArrayList<Double> dataList);

        void onFailure(String errorMessage);
    }

    public static void retrieveSoundDataFromServer(String date, DataCallback callback) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .retrieveSoundData(new SoundRetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray dataArray = jsonObj.getJSONArray("data");
                        ArrayList<Double> dataList = new ArrayList<>();
                        String message = jsonObj.getString("message");
                        for (int i = 0; i < dataArray.length(); i++) {
                            double value = dataArray.getDouble(i);
                            dataList.add(value);
                        }
                        // Log the retrieved data
                        Log.d(TAG, "Retrieved sound data: " + dataList.toString());
                        callback.onDataLoaded(dataList);
                    } else {
                        callback.onFailure("Response body is null");
                    }
                } catch (IOException | JSONException e) {
                    callback.onFailure(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public static void retrieveVOCDataFromServer(String date, DataCallback callback) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .retrieveVOCData(new SoundRetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray dataArray = jsonObj.getJSONArray("data");
                        ArrayList<Double> dataList = new ArrayList<>();
                        String message = jsonObj.getString("message");
                        for (int i = 0; i < dataArray.length(); i++) {
                            double value = dataArray.getDouble(i);
                            dataList.add(value);
                        }
                        // Log the retrieved data
                        Log.d(TAG, "Retrieved VOC data: " + dataList.toString());
                        callback.onDataLoaded(dataList);
                    } else {
                        callback.onFailure("Response body is null");
                    }
                } catch (IOException | JSONException e) {
                    callback.onFailure(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public static void retrieveCO2DataFromServer(String date, DataCallback callback) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .retrieveCO2Data(new SoundRetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray dataArray = jsonObj.getJSONArray("data");
                        ArrayList<Double> dataList = new ArrayList<>();
                        String message = jsonObj.getString("message");
                        for (int i = 0; i < dataArray.length(); i++) {
                            double value = dataArray.getDouble(i);
                            dataList.add(value);
                        }
                        // Log the retrieved data
                        Log.d(TAG, "Retrieved CO2 data: " + dataList.toString());
                        callback.onDataLoaded(dataList);
                    } else {
                        callback.onFailure("Response body is null");
                    }
                } catch (IOException | JSONException e) {
                    callback.onFailure(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }



}


