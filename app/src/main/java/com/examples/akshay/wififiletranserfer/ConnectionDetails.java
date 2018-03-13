package com.examples.akshay.wififiletranserfer;

/**
 * Created by ash on 13/3/18.
 */

public class ConnectionDetails {
    private String ssid;
    private String ip;
    private String password;
    private int port;

    public ConnectionDetails() {
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ConnectionDetails(String ssid, String ip, String password, int port) {
        this.ssid = ssid;
        this.ip = ip;
        this.password = password;
        this.port = port;
    }

}
