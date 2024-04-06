package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.content.Context;
import android.util.Log;

import com.example.mainpage.API.Model.AccessTime;
import com.example.mainpage.API.Model.AllDataSendRequest;
import com.example.mainpage.API.Model.DataSendRequest;
import com.example.mainpage.API.RetrofitClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DataSendWorker extends Worker {
    private String TAG = "HardBLE";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public DataSendWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        ArrayList<Integer> soundDataToSend = new ArrayList<>(MainPageActivity.Sounddata);
        ArrayList<Integer> vocDataToSend = new ArrayList<>(MainPageActivity.VOCdata);
        ArrayList<Integer> co2DataToSend = new ArrayList<>(MainPageActivity.Co2data);
        ArrayList<AccessTime> soundTimeDataToSend = new ArrayList<>(MainPageActivity.SounddataTime);
        ArrayList<AccessTime> vocTimeDataToSend = new ArrayList<>(MainPageActivity.VOCdataTIme);
        ArrayList<AccessTime> co2TimeDataToSend = new ArrayList<>(MainPageActivity.Co2dataTime);

        sdf.setTimeZone(TimeZone.getDefault()); // Use the client's default timezone
        String currentDate = sdf.format(new Date());
        // Send all data and clear if successful
        if (sendAlldata(soundDataToSend, vocDataToSend, co2DataToSend,soundTimeDataToSend,vocTimeDataToSend,co2TimeDataToSend ,currentDate)) {
            synchronized (MainPageActivity.Sounddata) {
                MainPageActivity.Sounddata.clear();
                MainPageActivity.SounddataTime.clear();
            }
            synchronized (MainPageActivity.VOCdata) {
                MainPageActivity.VOCdata.clear();
                MainPageActivity.VOCdataTIme.clear();
            }
            synchronized (MainPageActivity.Co2data) {
                MainPageActivity.Co2data.clear();
                MainPageActivity.Co2dataTime.clear();
            }
            Log.d(TAG, "All data sent and cleared successfully.");
            return Result.success();
        } else {
            return Result.retry();
        }
    }
//    @Override
//    public Result doWork() {
//        // Synchronize the copy and clear operations to ensure data consistency
//        synchronized (MainPageActivity.Sounddata) {
//            ArrayList<Integer> soundDataToSend = new ArrayList<>(MainPageActivity.Sounddata);
//            MainPageActivity.Sounddata.clear();  // Clear the data after copying
//        }
//
//        synchronized (MainPageActivity.VOCdata) {
//            ArrayList<Integer> vocDataToSend = new ArrayList<>(MainPageActivity.VOCdata);
//            MainPageActivity.VOCdata.clear();  // Clear the data after copying
//        }
//
//        synchronized (MainPageActivity.Co2data) {
//            ArrayList<Integer> co2DataToSend = new ArrayList<>(MainPageActivity.Co2data);
//            MainPageActivity.Co2data.clear();  // Clear the data after copying
//        }
//
//        // Now send the copied data
//        if (sendAlldata(soundDataToSend, vocDataToSend, co2DataToSend)) {
//            Log.d(TAG, "All data sent and cleared successfully.");
//            return Result.success();
//        } else {
//            return Result.retry();
//        }
//    }


    private boolean sendAlldata(ArrayList<Integer> sound, ArrayList<Integer> voc, ArrayList<Integer> co2,ArrayList<AccessTime> soundTime, ArrayList<AccessTime> vocTime, ArrayList<AccessTime> co2Time,String currentDate) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().AddData(new AllDataSendRequest(sound, voc, co2,soundTime,vocTime,co2Time,currentDate));

        try {
            Response<ResponseBody> response = call.execute(); // Execute the call synchronously
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending all data", e);
            return false;
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