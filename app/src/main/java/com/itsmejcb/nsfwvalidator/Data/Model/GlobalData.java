package com.itsmejcb.nsfwvalidator.Data.Model;


import java.util.ArrayList;

public class GlobalData {
    private static GlobalData instance;
    private ArrayList<Image> dataList;

    // Private constructor to prevent instantiation
    private GlobalData() {
        dataList = new ArrayList<Image>();
    }

    // Get a single instance of GlobalData
    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }

    public ArrayList<Image> getDataList() {
        return dataList;
    }

    // Set the ArrayList of data to be passed between activities
    public void setDataList(ArrayList<Image> dataList) {
        this.dataList = dataList;
    }
}
