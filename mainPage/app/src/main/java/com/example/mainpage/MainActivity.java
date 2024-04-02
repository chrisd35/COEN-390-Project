package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button signUpButton, loginButton;
    SettingsActivity settings = new SettingsActivity();

    private boolean isDarkModeEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();

        SharedPreferences sharedPref = getSharedPreferences("my_settings", Context.MODE_PRIVATE);
        isDarkModeEnabled = sharedPref.getBoolean("dark_mode_enabled", false);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    private void setup() {
        signUpButton = findViewById(R.id.goToSignUpButton);
        loginButton = findViewById(R.id.goToLoginButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpPage();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
            }
        });



    }



    private void goToLoginPage() {
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
    }

    private void goToSignUpPage() {
        Intent intent = new Intent(this, signUpActivity.class);
        startActivity(intent);
    }

}