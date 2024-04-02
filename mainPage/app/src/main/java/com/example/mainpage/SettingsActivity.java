package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.ActionBar;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.mainpage.API.ThresholdData;

public class SettingsActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected ImageButton infoButton, toggleMode;
    protected Spinner spinnerDBLevels;
    protected Button saveSettingButton;
    protected Integer ThresholdValue;
    private ThresholdData thresholdData;
    private Integer defaultValue = 60;

    public boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toggleMode = findViewById(R.id.toggleDark);
        int defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        setIconButton(defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES);

        try {
    toolbar = findViewById(R.id.settingsPageToolbar);
    spinnerDBLevels = findViewById(R.id.spinner_settings);
    infoButton = findViewById(R.id.infoButton);
    saveSettingButton=findViewById(R.id.saveSettingsbutton);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    thresholdData = new ThresholdData(getBaseContext());
    // Creating an Array adapter and attach to spinner
    ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.DB_levels, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

    spinnerDBLevels.setAdapter(adapter);
    thresholdData.fetchAndSetThreshold(spinnerDBLevels);
//    fetchAndSetThreshold();
    spinnerDBLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Get selected item text
            String selectedItem = parent.getItemAtPosition(position).toString().trim();
            String numericalPart = selectedItem.replace(" dB", "").trim();
            Integer data=Integer.valueOf(numericalPart);
            ThresholdValue=data;


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            ThresholdValue = defaultValue;
        }
    });
    infoButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gotoInfo();
        }
    });
    saveSettingButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            thresholdData.setSoundThreshold(ThresholdValue, new ThresholdData.ThresholdCallback() {
                @Override
                public void onSuccess() {
                    Log.d("HardBLE","SET");
                    finish();
                }

            });


        }
    });


    toggleMode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleDarkMode();
        }
    });



}catch (Exception e){
    Log.d("settingView",e.getMessage());
}

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void gotoInfo(){
        Intent intent = new Intent(this, infoActivity.class);
        startActivity(intent);
    }
   public void gotoMainPageActivity(){
        Intent intent = new Intent(this, MainPageActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void toggleDarkMode() {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            isDarkModeEnabled = false;
            setIconButton(isDarkModeEnabled);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            isDarkModeEnabled = true;
            setIconButton(isDarkModeEnabled);
        }

        SharedPreferences sharedPref = getSharedPreferences("my_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("dark_mode_enabled", isDarkModeEnabled);
        editor.apply();
    }

    public void setIconButton(boolean isOn) {
        if (isOn) {
            toggleMode.setImageResource(R.drawable.moon);
        } else {
            toggleMode.setImageResource(R.drawable.sun);
        }
    }






}