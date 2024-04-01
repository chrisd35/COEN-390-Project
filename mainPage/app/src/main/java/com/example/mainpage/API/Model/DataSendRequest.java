package com.example.mainpage.API.Model;

import java.util.List;

public class DataSendRequest<T> {
    private List<T> values;

    public DataSendRequest(List<T> value) {
        this.values = value;
    }

    public List<T> getValue() {
        return values;
    }
}
