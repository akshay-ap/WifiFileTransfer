package com.examples.akshay.wififiletranserfer.interfaces;

import com.examples.akshay.wififiletranserfer.ServerDetails;

/**
 * Created by ash on 22/2/18.
 */

public interface AcceptConnectionTaskUpdate {
    void SetIP(String ip);
    void SetPORT(int port);
    void Ready();

}
