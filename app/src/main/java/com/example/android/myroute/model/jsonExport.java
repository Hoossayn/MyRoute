package com.example.android.myroute.model;

import com.fasterxml.jackson.core.JsonFactory;

public class jsonExport extends JsonFactory {

    private String  coordinate;
    private String cordValue;

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getCordValue() {
        return cordValue;
    }

    public void setCordValue(String cordValue) {
        this.cordValue = cordValue;
    }
}
