package com.wrw.graduation.mysqlmonitor;

import org.litepal.crud.DataSupport;

public class Monitor extends DataSupport{
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String port;

    private String name;

    private String usr;

    private String pw;

    private String dbname;

    private String svstatus;

    private String dbstatus;



    public String getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getUsr() {
        return usr;
    }

    public String getPw() {
        return pw;
    }

    public String getDbname() {
        return dbname;
    }

    public String getSvstatus() {
        return svstatus;
    }

    public String getDbstatus() {
        return dbstatus;
    }



    public void setPort(String port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public void setSvstatus(String svstatus) {
        this.svstatus = svstatus;
    }

    public void setDbstatus(String dbstatus) {
        this.dbstatus = dbstatus;
    }
}
