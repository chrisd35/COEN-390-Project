package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mainpage.API.Model.DataSendRequest;
import com.example.mainpage.API.Model.ResetPasswordRequest;
import com.example.mainpage.API.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendResetPassword extends AppCompatActivity {

    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_reset_password);
        toolbar = findViewById(R.id.loginPageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button sendButton= findViewById(R.id.button);
        EditText inputText=findViewById(R.id.editTextText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputText.getText().toString();
                if (!email.isEmpty()) {
                    sendForgotPasswordRequest(email);
                } else {
                    Toast.makeText(SendResetPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void sendForgotPasswordRequest(String email) {
        RetrofitClient client = RetrofitClient.getInstance();
        Call<ResponseBody> call = client.getApi().SendForgotPasswordRequest(new ResetPasswordRequest(email));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        String message = jsonObj.getString("message");
                        boolean gologinpage=jsonObj.getBoolean("gotoNewPage");
                        Log.d("HardBLE", String.valueOf(gologinpage));
                        Toast.makeText(SendResetPassword.this, message, Toast.LENGTH_SHORT).show();
                        if(gologinpage)
                            gotoLogin();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                        // Handle the IOException

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    // Request successful, show a success message and navigate to login screen

                } else {
                    // Request failed, show an error message
                    Toast.makeText(SendResetPassword.this, "Failed to send reset password request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Network or API call failure
                Toast.makeText(SendResetPassword.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void gotoLogin(){
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}