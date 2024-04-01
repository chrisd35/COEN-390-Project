package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
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

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
public class loginActivity extends AppCompatActivity {
    protected Button loginButton;
    protected EditText usernameEdit;
    protected EditText passwordEdit;
    protected Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.loginPageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    loginButton=findViewById(R.id.loginButton);

    usernameEdit =findViewById(R.id.usernameLoginEditText);

    passwordEdit =findViewById(R.id.passwordLoginEditText);

        loginButton.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick (View v){
            try{

                userAuthentication();

            }
            catch (Exception e){
                e.printStackTrace();
                Log.e("LoginResponse",e.getMessage());
            }

    }
    });
}
    private void userAuthentication(){
        String username= usernameEdit.getText().toString().trim();
        String password= passwordEdit.getText().toString().trim();
        Log.e("LoginResponse",username);
        try {
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .login(username,password);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            String body = response.body().string();
                            JSONObject jsonObj = new JSONObject(body);
                            boolean isAuthenticated = jsonObj.getBoolean("Authentication");
                            String message=jsonObj.getString("message");
                            Toast.makeText(loginActivity.this,message,Toast.LENGTH_SHORT).show();
                            if(isAuthenticated)
                                gotoMainPageActivity();

                        } catch (IOException e) {
                            Toast.makeText(loginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            // Handle the IOException

                    } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
//                        else {
//                        if(response.body() == null)
//                        Toast.makeText(loginActivity.this,"failure",Toast.LENGTH_LONG).show();
//                        Log.e("LoginResponse","null");
//                        // Handle request failure
//                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("LoginResponse",t.getMessage());
                    Toast.makeText(loginActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e) {
            Log.e("LoginResponse", e.getMessage());
        }


    }
    public void gotoMainPageActivity(){
        Intent intent = new Intent(this, MainPageActivity.class);
        startActivity(intent);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}