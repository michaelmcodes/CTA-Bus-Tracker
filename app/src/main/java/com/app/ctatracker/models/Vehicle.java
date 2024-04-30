package com.app.ctatracker.models;

public class Vehicle {
    private String vid;
    private String tmstmp;
    private String lat;
    private String lon;
    private String hdg;
    private int pid;
    private String rt;
    private String des;
    private int pdist;
    private boolean dly;
    private String tatripid;
    private String origtatripno;
    private String tablockid;
    private String zone;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
