package com.examples.akshay.wififiletranserfer.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by ash on 20/2/18.
 *
 */

public class ConnectTask extends AsyncTask {
    private static final String TAG = "===ConnectTask";

    private Context context;
    public ConnectTask(Context context) {
        this.context = context;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
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

}