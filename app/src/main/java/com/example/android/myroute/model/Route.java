package com.example.android.myroute.model;

public class Route {
    private String id;
    private String description;
    private String bigchar;
    private String arrayvalue;
    private String date;
    private String time;




    public String getArrayvalue() {
        return arrayvalue;
    }

    public void setArrayvalue(String arrayvalue) {
        this.arrayvalue = arrayvalue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBigchar() {
        return bigchar;
    }

    public void setBigchar(String bigchar) {
        this.bigchar = bigchar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
