package com.example.mainpage;

import android.util.Log;

import com.example.mainpage.API.Model.AccessTime;
import com.example.mainpage.API.Model.RetrieveData;
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
        void onDataLoaded(ArrayList<Double> dataList, ArrayList<AccessTime> accessTimeList);

        void onFailure(String errorMessage);
    }

    public static void retrieveSoundDataFromServer(String date, DataCallback callback) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .retrieveSoundData(new RetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray dataArray = jsonObj.getJSONArray("data");
                        JSONArray accessTimeArray = jsonObj.getJSONArray("DataAccessTime");
                        ArrayList<AccessTime> accessTimeList = new ArrayList<>();
                        ArrayList<Double> dataList = new ArrayList<>();
                        String message = jsonObj.getString("message");
                        for (int i = 0; i < dataArray.length(); i++) {
                            double value = dataArray.getDouble(i);
                            dataList.add(value);
                        }
                        for (int i = 0; i < accessTimeArray.length(); i++) {
                            JSONObject timeObj = accessTimeArray.getJSONObject(i);
                            int hour = timeObj.getInt("hour");
                            int minute = timeObj.getInt("minute");
                            int second = timeObj.getInt("second");
                            AccessTime accessTime = new AccessTime(hour, minute, second);
                            accessTimeList.add(accessTime);
                        }
                        // Log the retrieved data
                        Log.d(TAG, "Retrieved sound data: " + dataList.toString());
                        callback.onDataLoaded(dataList,accessTimeList);
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
                .retrieveVOCData(new RetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray dataArray = jsonObj.getJSONArray("data");
                        JSONArray accessTimeArray = jsonObj.getJSONArray("DataAccessTime");
                        ArrayList<AccessTime> accessTimeList = new ArrayList<>();
                        ArrayList<Double> dataList = new ArrayList<>();
                        String message = jsonObj.getString("message");
                        for (int i = 0; i < dataArray.length(); i++) {
                            double value = dataArray.getDouble(i);
                            dataList.add(value);
                        }
                        for (int i = 0; i < accessTimeArray.length(); i++) {
                            JSONObject timeObj = accessTimeArray.getJSONObject(i);
                            int hour = timeObj.getInt("hour");
                            int minute = timeObj.getInt("minute");
                            int second = timeObj.getInt("second");
                            AccessTime accessTime = new AccessTime(hour, minute, second);
                            accessTimeList.add(accessTime);
                        }
                        // Log the retrieved data
                        Log.d(TAG, "Retrieved VOC data: " + dataList.toString());
                        callback.onDataLoaded(dataList, accessTimeList);
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
                .retrieveCO2Data(new RetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray dataArray = jsonObj.getJSONArray("data");
                        JSONArray accessTimeArray = jsonObj.getJSONArray("DataAccessTime");
                        ArrayList<AccessTime> accessTimeList = new ArrayList<>();
                        ArrayList<Double> dataList = new ArrayList<>();
                        String message = jsonObj.getString("message");
                        for (int i = 0; i < dataArray.length(); i++) {
                            double value = dataArray.getDouble(i);
                            dataList.add(value);
                        }
                        for (int i = 0; i < accessTimeArray.length(); i++) {
                            JSONObject timeObj = accessTimeArray.getJSONObject(i);
                            int hour = timeObj.getInt("hour");
                            int minute = timeObj.getInt("minute");
                            int second = timeObj.getInt("second");
                            AccessTime accessTime = new AccessTime(hour, minute, second);
                            accessTimeList.add(accessTime);
                        }
                        // Log the retrieved data
                        Log.d(TAG, "Retrieved CO2 data: " + dataList.toString());
                        callback.onDataLoaded(dataList, accessTimeList);
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


