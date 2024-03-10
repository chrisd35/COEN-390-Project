package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mainpage.API.DataCallback;
import com.example.mainpage.API.Model.SoundDataSendRequest;
import com.example.mainpage.API.Model.SoundRetrieveData;
import com.example.mainpage.API.RetrofitClient;
import com.example.mainpage.API.SendDataCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity<T> extends AppCompatActivity {
    protected ImageView Stats;
    protected ImageView Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Stats = findViewById(R.id.imageView3);
        Settings = findViewById(R.id.imageButton);

        Stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoStatsPage();
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingsPage();
            }
        });



        sendDatatoServer(( new ArrayList<Integer>(Arrays.asList(5))), new SendDataCallback() {
                    @Override
                    public void onDataSent() {

                        receiveDatafromServer("2024-03-09", new DataCallback() {
                            @Override
                            public void onDataLoaded(List<Double> data) {
                                String DataLine="";
                                for (int i = 0; i < data.size(); i++){
                                    Log.d("MainPageActivity",String.valueOf(data.get(i)));

                                    DataLine+=String.valueOf(data.get(i))+", ";
                                }
                                Toast.makeText(MainPageActivity.this, DataLine, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
//        sendDatatoServer(( new ArrayList<Double>(Arrays.asList(2.5,1.5))));

    }

    public void receiveDatafromServer(String date, DataCallback callback){
        Call<ResponseBody>call =RetrofitClient
                .getInstance()
                .getApi()
                .retrieveSoundData(new SoundRetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                {
                    try {
                        if (response != null && response.body() != null) {
                            String body = response.body().string();
                            JSONObject jsonObj = new JSONObject(body);
                            JSONArray dataArray = jsonObj.getJSONArray("data");
                            ArrayList<Double> dataList = new ArrayList<>();
                            String message = jsonObj.getString("message");
                            Toast.makeText(MainPageActivity.this, message, Toast.LENGTH_LONG).show();
                            for (int i = 0; i < dataArray.length(); i++) {
                                double value = dataArray.getDouble(i); // Get the double value at the current position
                                dataList.add(value); // Add the double value to the ArrayList
                            }
                            callback.onDataLoaded(dataList);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    catch (Exception e) {
                        Log.e("MainPageActivity", "Error ", e);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


    public <T> void sendDatatoServer(List<T>data){
Call<ResponseBody>call =RetrofitClient
        .getInstance()
        .getApi()
        .AddSoundData(new SoundDataSendRequest(data));
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
       try {
           String body=response.body().string();
           JSONObject jsonObj = new JSONObject(body);
           String message=jsonObj.getString("message");
           Toast.makeText(MainPageActivity.this,message,Toast.LENGTH_LONG).show();
       } catch (JSONException e) {
           throw new RuntimeException(e);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
    }
});
    }
    public <T> void sendDatatoServer(List<T>data,SendDataCallback callback ){
        Call<ResponseBody>call =RetrofitClient
                .getInstance()
                .getApi()
                .AddSoundData(new SoundDataSendRequest(data));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body=response.body().string();
                    JSONObject jsonObj = new JSONObject(body);
                    String message=jsonObj.getString("message");
                    Toast.makeText(MainPageActivity.this,message,Toast.LENGTH_LONG).show();
                    callback.onDataSent();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void  verifyAuthentication() {

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .isAuthorized();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean isAuthenticated;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        isAuthenticated= jsonObj.getBoolean("Authentication");
                        if(!isAuthenticated)
                            gotoMainActivity();
                        Toast.makeText(MainPageActivity.this,String.valueOf(isAuthenticated),Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainPageActivity.this,"NullResponse",Toast.LENGTH_LONG).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(MainPageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }
        public void gotoMainActivity(){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        public void gotoStatsPage(){
            Intent intent = new Intent(this, StatActivity.class);
            startActivity(intent);
        }

    public void gotoSettingsPage(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}