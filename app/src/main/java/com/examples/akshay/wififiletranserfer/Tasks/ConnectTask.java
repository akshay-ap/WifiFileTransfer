package com.examples.akshay.wififiletranserfer.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.examples.akshay.wififiletranserfer.ServerDetails;
import com.examples.akshay.wififiletranserfer.interfaces.AcceptConnectionTaskUpdate;

import java.io.IOException;
import java.net.Socket;


/**
 * Created by ash on 20/2/18.
 *
 */

public class ConnectTask extends AsyncTask {
    private static final String TAG = "===ConnectTask";
    Socket mSocket;
    AcceptConnectionTaskUpdate acceptConnectionTaskUpdate;
    private Context context;
    public ConnectTask(Context context,AcceptConnectionTaskUpdate acceptConnectionTask) {
        this.context = context;
        this.acceptConnectionTaskUpdate = acceptConnectionTaskUpdate;
    }


    @Override
    protected Object doInBackground(Object[] objects)
    {

        try {
            logd("Creating socket object");
            mSocket = new Socket(ServerDetails.IP,ServerDetails.PORT);
            acceptConnectionTaskUpdate.StartDataTransfer();
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

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
    }
    private void logd(String log) {
        Log.d(ConnectTask.TAG,log);
    }

}