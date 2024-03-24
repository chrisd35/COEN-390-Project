package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.content.Context;
import android.util.Log;

import com.example.mainpage.API.Model.SoundDataSendRequest;
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
        ArrayList<Integer> SounddataToSend = new ArrayList<>(MainPageActivity.Sounddata);
        ArrayList<Integer> VOCdataToSend = new ArrayList<>(MainPageActivity.VOCdata);
        ArrayList<Integer> CO2dataToSend = new ArrayList<>(MainPageActivity.Co2data);

        // Log the size of data before sending
        if (sendDatatoServer(SounddataToSend) && sendVOCDatatoServer(VOCdataToSend)&& sendCO2DatatoServer(CO2dataToSend)) {
            // If data sent successfully, clear the original data list
            synchronized (MainPageActivity.Sounddata) {
                MainPageActivity.Sounddata.clear();
                MainPageActivity.VOCdata.clear();
                MainPageActivity.Co2data.clear();
            }
            Log.d(TAG, "Data sent and cleared successfully.");
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private boolean sendCO2DatatoServer(List<Integer> data) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddCO2Data(new SoundDataSendRequest(data));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending data", e);
            return false;
        }
    }


    private boolean sendDatatoServer(List<Integer> data) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddSoundData(new SoundDataSendRequest(data));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending data", e);
            return false;
        }
    }
    private boolean sendVOCDatatoServer(List<Integer> data) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddVOCData(new SoundDataSendRequest(data));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending data", e);
            return false;
        }
    }

}