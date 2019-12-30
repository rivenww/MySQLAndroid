package com.wrw.graduation.mysqlmonitor;

public class Dbinfo {
    private String IP;
    private String Status;
    public Dbinfo (String IP,String Status){
        this.IP=IP;
        this.Status=Status;
    }

    public String getIP() {
        return IP;
    }

    public String getStatus() {
        return Status;
    }
}
