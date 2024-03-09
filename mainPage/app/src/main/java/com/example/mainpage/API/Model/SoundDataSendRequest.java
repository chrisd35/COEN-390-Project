package com.example.mainpage.API.Model;

import java.util.List;

public class SoundDataSendRequest<T> {
    private List<T> values;

    public SoundDataSendRequest(List<T> value) {
        this.values = value;
    }

    public List<T> getValue() {
        return values;
    }
}
