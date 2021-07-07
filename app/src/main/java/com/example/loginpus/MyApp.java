package com.example.loginpus;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {

    private static MyApp singleton;

    private List<Location> BaseLocation;

    public List<Location> getBaseLocation() {
        return BaseLocation;
    }

    public void setBaseLocation(List<Location> baseLocation) {
        BaseLocation = baseLocation;
    }

    public MyApp getInstance(){
        return singleton;
    }

    public void onCreate(){
        super.onCreate();
        singleton = this;
        BaseLocation = new ArrayList<>();
    }
}
