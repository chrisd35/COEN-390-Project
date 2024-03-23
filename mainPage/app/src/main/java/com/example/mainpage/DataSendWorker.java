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
    private String Tag = "HardBLE";
    public DataSendWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Your data sending code here

        ArrayList<Integer> DummyData = MainPageActivity.Sounddata;

        sendDatatoServer(DummyData); // Implement this method to send data
        return Result.success();
    }

    private Result sendDatatoServer(List<Integer> data) {
        try {
            RetrofitClient client = RetrofitClient.getInstance();
            Call<ResponseBody> call = client.getApi().AddSoundData(new SoundDataSendRequest(data));
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously

            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                // Process the response as needed
                Log.d(Tag, String.valueOf(data.size()));
                Log.d(Tag, "Data sent successfully");
                data.clear();
                Log.d(Tag, String.valueOf(data.size()));
                return Result.success();
            } else {
                Log.e(Tag, "Failed to send data");
                return Result.failure();
            }
        } catch (IOException e) {
            Log.e(Tag, "Network error", e);
            return Result.retry();
        }
    }
}