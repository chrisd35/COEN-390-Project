package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button signUpButton, loginButton, homePageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    private void setup() {
        signUpButton = findViewById(R.id.goToSignUpButton);
        loginButton = findViewById(R.id.goToLoginButton);
        homePageButton = findViewById(R.id.goToHomePageButton);

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

        homePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHomePage();
            }
        });
    }

    private void goToHomePage() {
        Intent intent = new Intent(this, MainPageActivity.class);
        startActivity(intent);
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