package com.examples.akshay.wififiletranserfer.interfaces;

import com.examples.akshay.wififiletranserfer.ServerDetails;

/**
 * Created by ash on 22/2/18.
 */

public interface TaskUpdate {
    void TaskCompleted(String message);
    void TaskStarted();
    void TaskProgressPublish(String Update);
    void TaskError(String e);
    void TaskData();
}
