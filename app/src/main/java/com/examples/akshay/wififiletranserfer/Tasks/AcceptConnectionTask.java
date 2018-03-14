package com.examples.akshay.wififiletranserfer.Tasks;

import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import com.examples.akshay.wififiletranserfer.ConnectionDetails;
import com.examples.akshay.wififiletranserfer.SocketHolder;
import com.examples.akshay.wififiletranserfer.Utils;
import com.examples.akshay.wififiletranserfer.interfaces.AcceptConnectionTaskUpdate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by ash on 20/2/18.
 */

public class AcceptConnectionTask extends AsyncTask {
    private static final String TAG = "===AccpetThread";
    ServerSocket serverSocket;
    private Context context;
    AcceptConnectionTaskUpdate acceptConnectionTaskUpdate;
    public AcceptConnectionTask(Context context,AcceptConnectionTaskUpdate acceptConnectionTaskUpdate) {
        this.context = context;
        this.acceptConnectionTaskUpdate = acceptConnectionTaskUpdate;
    }

    public void cancel() {
        try {
            logd("in cancel() trying to close Server socket ");
            serverSocket.close();
            logd("after cancel() ");
            if (serverSocket.isClosed()) {
                logd("after cancel() Server socket is closed");
            }
        } catch (IOException e) {
            Log.d(TAG, "Could not close the connect socket", e);
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.e(TAG, "onPostExecute()");
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            serverSocket = new ServerSocket(0);

            if (serverSocket.isClosed()) {
                logd("doInBackground() "+"Server socket is closed");
            }

            ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
            connectionDetails.setIp(Utils.getIPAddress(true));
            connectionDetails.setPort(serverSocket.getLocalPort());


            logd("listening on port: " + serverSocket.getLocalPort());
            logd("ipv4 address current: " + Utils.getIPAddress(true) + " saved : " + connectionDetails.getIp());

            acceptConnectionTaskUpdate.Ready();



            logd("trying to accept connection");
            Socket client = serverSocket.accept();
            SocketHolder.setSocket(client);
            acceptConnectionTaskUpdate.StartDataTransfer();
            logd("Accepted connection");

        } catch (IOException e) {
            e.printStackTrace();
            logd(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logd(e.toString());
        }
        return null;
    }

    private void logd(String log) {
        Log.d(AcceptConnectionTask.TAG,log);
    }
}