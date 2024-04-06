package com.example.mainpage.API.Model;

public class ResetPasswordRequest {




    String email;
    public ResetPasswordRequest(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

}
