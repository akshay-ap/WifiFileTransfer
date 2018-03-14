package com.examples.akshay.wififiletranserfer.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.examples.akshay.wififiletranserfer.ConnectionDetails;
import com.examples.akshay.wififiletranserfer.SocketHolder;
import com.examples.akshay.wififiletranserfer.interfaces.ConnectTaskUpdate;

import java.io.IOException;
import java.net.Socket;


/**
 * Created by ash on 20/2/18.
 *
 */

public class ConnectTask extends AsyncTask {
    private static final String TAG = "===ConnectTask";
    Socket mSocket;
    ConnectTaskUpdate connectTaskUpdate;
    private Context context;
    public ConnectTask(Context context,ConnectTaskUpdate connectTaskUpdate) {
        this.context = context;
        this.connectTaskUpdate = connectTaskUpdate;
        logd("Object created");
    }


    @Override
    protected Object doInBackground(Object[] objects)
    {
        connectTaskUpdate.connectTaskStarted();
        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();

        String ip = connectionDetails.getIp();
        int port = connectionDetails.getPort();

        try {
            logd("Creating socket object with IP: " + ip + " PORT : " + port);
            mSocket = new Socket(ip,port);

            SocketHolder.setSocket(mSocket);
            connectTaskUpdate.connectTaskStartDataTransfer();
            logd("Socket object created");
        } catch (IOException e) {
            logd("exception " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logd("exception " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.e(TAG, "onPostExecute()");
        connectTaskUpdate.connectTaskCompleted("Completed");
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        connectTaskUpdate.connectTaskCancelled();
    }
    private void logd(String log) {
        Log.d(ConnectTask.TAG,log);
    }

}