package com.app.ctatracker.models;

public class Prediction {
    private String tmstmp;
    private String typ;
    private String stpnm;
    private String stpid;
    private String vid;
    private int dstp;
    private String rt;
    private String rtdd;
    private String rtdir;
    private String des;
    private String prdtm;
    private String tablockid;
    private String tatripid;
    private String origtatripno;
    private boolean dly;
    private String prdctdn;
    private String zone;

    public String getVid() {
        return vid;
    }

    public String getRtdir() {
        return rtdir;
    }

    public String getDes() {
        return des;
    }

    public String getPrdtm() {
        return prdtm;
    }

    public boolean isDly() {
        return dly;
    }

    public String getPrdctdn() {
        return prdctdn;
    }
}
