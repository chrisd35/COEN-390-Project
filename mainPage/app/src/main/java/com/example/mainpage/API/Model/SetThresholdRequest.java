package com.example.mainpage.API.Model;

import java.util.List;

public class SetThresholdRequest {

    private int thresholdValue;

    public SetThresholdRequest(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public int getThresholdValue() {
        return thresholdValue;
    }
}
