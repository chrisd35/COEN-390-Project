package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.content.Context;
import android.util.Log;

import com.example.mainpage.API.Model.DataSendRequest;
import com.example.mainpage.API.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DataSendWorker extends Worker {
    private String TAG = "HardBLE";
    public DataSendWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Your data sending code here
        ArrayList<Integer> soundDataToSend = new ArrayList<>(MainPageActivity.Sounddata);
        ArrayList<Integer> vocDataToSend = new ArrayList<>(MainPageActivity.VOCdata);
        ArrayList<Integer> co2DataToSend = new ArrayList<>(MainPageActivity.Co2data);

        boolean soundDataSent = false, vocDataSent = false, co2DataSent = false;

        // Send Sound data and clear if successful
        if (sendDatatoServer(soundDataToSend)) {
            synchronized (MainPageActivity.Sounddata) {
                MainPageActivity.Sounddata.clear();
            }
            soundDataSent = true;
            Log.d(TAG, "Sound data sent and cleared successfully.");
        }

        // Send VOC data and clear if successful
        if (sendVOCDatatoServer(vocDataToSend)) {
            synchronized (MainPageActivity.VOCdata) {
                MainPageActivity.VOCdata.clear();
            }
            vocDataSent = true;
            Log.d(TAG, "VOC data sent and cleared successfully.");
        }

        // Send CO2 data and clear if successful
        if (sendCO2DatatoServer(co2DataToSend)) {
            synchronized (MainPageActivity.Co2data) {
                MainPageActivity.Co2data.clear();
            }
            co2DataSent = true;
            Log.d(TAG, "CO2 data sent and cleared successfully.");
        }

        // Check if all data types were sent successfully
        if (soundDataSent && vocDataSent && co2DataSent) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private boolean sendCO2DatatoServer(List<Integer> data) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddCO2Data(new DataSendRequest(data));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending data", e);
            Log.e(TAG,  e.getMessage());
            return false;
        }
    }


    private boolean sendDatatoServer(List<Integer> data) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddSoundData(new DataSendRequest(data));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending data", e);
            Log.e(TAG,  e.getMessage());
            return false;
        }
    }
    private boolean sendVOCDatatoServer(List<Integer> data) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddVOCData(new DataSendRequest(data));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending data", e);
            Log.e(TAG,  e.getMessage());
            return false;
        }
    }

}