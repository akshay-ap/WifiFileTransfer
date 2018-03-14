package com.examples.akshay.wififiletranserfer;

/**
 * Created by ash on 1/3/18.
 */

public class Constants {


    public static final String KEY_SERVER_PORT = "KEY_SERVER_PORT";
    public static  final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_SSID = "KEY_SSID ap";
    public static final String KEY_IP = "SERVER_IP";

    public static final int PERMISSIONS_REQUEST = 22;
    //Service Info
    //public static final String INSTANCE_NAME= "_wifi_file";
    //public static final String SERVICE_TYPE = "_presence._tcp";

    //milli seconds
    public static final int HOTSPOT_TURN_OFF_WAIT_PERIOD = 3000;

    public static final int HOTSPOT_CREATION_STARTED = 1;
    public static final int HOTSPOT_CREATION_SUCCESS = 2;
    public static final int HOTSPOT_CREATION_FAILED = 3;
    public static final int HOTSPOT_TURN_OFF_REQUEST = 4;
    public static final int HOTSPOT_TURN_OFF_FAILED = 5;
    public static final int HOTSPOT_TURN_OFF_SUCCESS = 6;

    public static final int WIFI_CONNECTION_SUCCESS = 7;
    public static final int WIFI_CONNECTION_FAILURE = 8;

    public static final String DATA_TRANSFER_ACTION = "DATA_TRANSFER_ACTION";
    public static final String DATA_TRANSFER_DATA = "DATA_TRANSFER_DATA";
    public static final String  DATA_TRANSFER_SOCKET = "DATA_TRANSFER_SOCKET";
    public static final int DISCOVERABLE_DURATION = 120;
    public static final String RECEIVED_FORM_SAVE_PATH = "odk/instances";
}
