package com.examples.akshay.wififiletranserfer;

/**
 * Created by ash on 13/3/18.
 */

public class ConnectionDetails {
    private String ssid;
    private String ip;
    private String password;
    private int port;

    private static volatile ConnectionDetails sConnectionDetails;

    //private constructor.
    private ConnectionDetails() {
        if (sConnectionDetails != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static  ConnectionDetails getInstance() {
        if (sConnectionDetails == null) {

            synchronized (ConnectionDetails.class) {
                if (sConnectionDetails == null) sConnectionDetails = new ConnectionDetails();
            }
        }

        return sConnectionDetails;
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



}
