package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mainpage.API.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class signUpActivity extends AppCompatActivity {
    protected Button saveButton;
    protected EditText usernameEdit;
    protected EditText passwordEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        saveButton = findViewById(R.id.saveButton);
        usernameEdit = findViewById(R.id.usernameLoginEditText);
        passwordEdit = findViewById(R.id.passwordLoginEditText);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();

            }
        });
    }

    private void createUser(){
        String username= usernameEdit.getText().toString().trim();
        String password= passwordEdit.getText().toString().trim();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .signup(username,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String body=response.body().string();
                    JSONObject jsonObj = new JSONObject(body);
                    boolean isAccountCreated = jsonObj.getBoolean("AccountCreated");
                    String message=jsonObj.getString("Message");
                    Toast.makeText(signUpActivity.this,message,Toast.LENGTH_LONG).show();
                    if(isAccountCreated)
                        gotoMainPageActivity();

                    Log.e("SignupResponse",body);
                }catch(IOException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SignupResponse",t.getMessage());
                Toast.makeText(signUpActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public void gotoMainPageActivity(){
        Intent intent = new Intent(this, MainPageActivity.class);
        startActivity(intent);
    }

}