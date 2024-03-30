package com.example.mainpage.API.Model;

import java.util.List;

public class AllDataSendRequest<T> {
    private List<T> soundValues;
    private List<T> vocValues;
    private List<T> co2Values;

    public AllDataSendRequest(List<T> soundValues, List<T> vocValues, List<T> co2Values) {
        this.soundValues = soundValues;
        this.vocValues = vocValues;
        this.co2Values = co2Values;
    }

    public List<T> getSoundValues() {
        return soundValues;
    }

    public List<T> getVocValues() {
        return vocValues;
    }

    public List<T> getCo2Values() {
        return co2Values;
    }
}
