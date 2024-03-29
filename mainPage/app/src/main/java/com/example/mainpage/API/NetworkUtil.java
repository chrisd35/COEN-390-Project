package com.example.mainpage.API;

import android.content.Context;
import android.widget.Toast;

import com.example.mainpage.API.Model.DataSendRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUtil {
    public static <T> void sendDatatoServer(Context context, List<T> data) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .AddSoundData(new DataSendRequest(data));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        String message = jsonObj.getString("message");
                        // Use passed context for Toast
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Use passed context for Toast
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
