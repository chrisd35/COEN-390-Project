package com.example.mainpage.API.Model;

import java.util.List;

public class AllDataSendRequest<T> {
    private List<T> soundValues;
    private List<T> vocValues;
    private List<T> co2Values;
    private List<AccessTime> soundTime;
    private List<AccessTime> vocTime;
    private List<AccessTime> co2Time;



    private String Date;
    public String getDate() {
        return Date;
    }

    public AllDataSendRequest(List<T> soundValues, List<T> vocValues, List<T> co2Values, List<AccessTime> soundTime, List<AccessTime> vocTime, List<AccessTime> co2Time, String date) {
        this.soundValues = soundValues;
        this.vocValues = vocValues;
        this.co2Values = co2Values;
        this.soundTime = soundTime;
        this.vocTime = vocTime;
        this.co2Time = co2Time;
        Date = date;
    }

    public AllDataSendRequest(List<T> soundValues, List<T> vocValues, List<T> co2Values, List<AccessTime> soundTime, List<AccessTime> vocTime, List<AccessTime> co2Time) {
        this.soundValues = soundValues;
        this.vocValues = vocValues;
        this.co2Values = co2Values;
        this.soundTime = soundTime;
        this.vocTime = vocTime;
        this.co2Time = co2Time;
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

    public List<AccessTime> getSoundTime() {
        return soundTime;
    }

    public List<AccessTime> getVocTime() {
        return vocTime;
    }

    public List<AccessTime> getCo2Time() {
        return co2Time;
    }

    public AllDataSendRequest(List<T> soundValues, List<T> vocValues, List<T> co2Values) {
        this.soundValues = soundValues;
        this.vocValues = vocValues;
        this.co2Values = co2Values;
    }


}
