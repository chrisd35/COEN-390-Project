package com.example.mainpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mainpage.API.NetworkUtil;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Trigger your data sending logic here or start a service
        List<Integer> dummyData = MainPageActivity.Sounddata;
        NetworkUtil.sendDatatoServer(context, dummyData);
        dummyData.clear();
    }

    private void sendDataToServer(Context context) {
        // Your data sending logic here
    }
}