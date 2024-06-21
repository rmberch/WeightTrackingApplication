package com.rmberch.weighttrackingapplication;

// DataModel.java
//Class for Weight records. Has weight and date variables
public class DataModel {
    private String date;
    private float weight;

    public DataModel(String date, float weight) {
        this.date = date;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public float getWeight() {
        return weight;
    }
}

