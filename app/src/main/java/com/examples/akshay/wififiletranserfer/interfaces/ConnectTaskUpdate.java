package com.examples.akshay.wififiletranserfer.interfaces;

/**
 * Created by ash on 22/2/18.
 */

public interface ConnectTaskUpdate {
    void connectTaskCompleted(String message);
    void connectTaskStartDataTransfer();
    void connectTaskStarted();
    void connectTaskCancelled();
    void connectTaskProgressPublish(String Update);
    void connectTaskError(String e);
}
