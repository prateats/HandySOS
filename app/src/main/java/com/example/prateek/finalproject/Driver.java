package com.example.prateek.finalproject;

public class Driver {

    String driverid;
    String plat;
    String plong;
    String time;

    public Driver()
    {}

    public Driver(String driveri,String pilat, String pilot, String time)
    {
        this.driverid = driveri;
        this.plat = pilat;
        this.plong = pilot;
        this.time = time;

    }

    public String getDriverid() {
        return driverid;
    }

    public String getPlat() {
        return plat;
    }

    public String getPlong() {
        return plong;
    }

    public String getTime(){return time;}
}
