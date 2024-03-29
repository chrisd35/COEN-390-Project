package com.example.mainpage.API;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mainpage.API.Model.SetThresholdRequest;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThresholdData {


    private Context context;
private int defaultValue=60;
    public ThresholdData(Context context) {
        this.context = context;
    }

    public void fetchAndSetThreshold(Spinner spinner) {
        Call<ResponseBody> call = RetrofitClient.getInstance().getApi().getSoundThreshold();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        int currentThreshold = jsonObj.getInt("data");
                        boolean dataExist = jsonObj.getBoolean("dataExist");
                        SharedPreferences sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("ThresholdValue", currentThreshold);
                        editor.putBoolean("DataExist", dataExist);
                        editor.apply();
                        spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition(String.valueOf(currentThreshold) + " dB"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch current settings", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void fetchAndSetThreshold() {
        Call<ResponseBody> call = RetrofitClient.getInstance().getApi().getSoundThreshold();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        int currentThreshold = jsonObj.getInt("data");
                        boolean dataExist = jsonObj.getBoolean("dataExist");
                        SharedPreferences sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("ThresholdValue", currentThreshold);
                        editor.putBoolean("DataExist", dataExist);
                        editor.apply();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch current settings", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void setSoundThreshold(Integer ThresholdValue, ThresholdData.ThresholdCallback callback) {
        Call<ResponseBody> call = RetrofitClient.getInstance().getApi().setSoundThreshold(new SetThresholdRequest(ThresholdValue));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d("HardBLE", "Response received");
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        String message = jsonObj.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        Log.d("HardBLE", "Successful: " + message);
                        SharedPreferences sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("ThresholdValue", ThresholdValue);
                        editor.putBoolean("DataExist", true);
                        editor.apply();
                        callback.onSuccess();
                    } else {
                        Log.d("HardBLE", "Response not successful");
                    }
                } catch (Exception e) {
                    Log.e("HardBLE", "Error in onResponse", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
    public int getSavedThreshold() {
        SharedPreferences sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        // Return the saved threshold or a default value if no threshold is saved
        return sharedPref.getInt("ThresholdValue", defaultValue);
    }
    public boolean ThresholdExist() {
        SharedPreferences sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        // Return the saved threshold or a default value if no threshold is saved
        return sharedPref.getBoolean("DataExist", false);
    }
    public interface ThresholdCallback {
        void onSuccess();
    }
}
