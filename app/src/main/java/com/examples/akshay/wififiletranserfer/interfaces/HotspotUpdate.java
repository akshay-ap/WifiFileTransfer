package com.examples.akshay.wififiletranserfer.interfaces;

/**
 * Created by ash on 13/3/18.
 */

public interface HotspotUpdate {
    void hotspotCreated();
    void hotspotCreationFailed();
    void connectedToHotspot();
    void wifiConnectionFailed();
}
