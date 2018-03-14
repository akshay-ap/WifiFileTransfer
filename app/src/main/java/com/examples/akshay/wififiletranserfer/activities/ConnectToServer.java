package com.examples.akshay.wififiletranserfer.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.Tasks.ConnectTask;
import com.examples.akshay.wififiletranserfer.interfaces.AcceptConnectionTaskUpdate;
import com.examples.akshay.wififiletranserfer.interfaces.ConnectTaskUpdate;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;

public class ConnectToServer extends AppCompatActivity implements View.OnClickListener, ConnectTaskUpdate {

    private static final String TAG = "===ConnectToServer";

    ConnectTask connectTask;
    AlertDialog alertDialog;

    Button buttonConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_server);
        setupUI();

    }
    private void setupUI() {

        buttonConnect = findViewById(R.id.connect_to_server_activity_button_connect);
        buttonConnect.setOnClickListener(this);

        alertDialog = getAlertDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_to_server_activity_button_connect:

                if(!alertDialog.isShowing()) {
                    alertDialog.show();
                }
                logd("Click connect_to_server_activity_button_connect");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeToast("Initiating socket connection");
                        if(connectTask != null ) {
                            logd("connect task is not null");
                            if(connectTask.getStatus() == AsyncTask.Status.RUNNING) {
                                connectTask.cancel();
                                }
                                connectTask =null;
                        }
                        connectTask = new ConnectTask(ConnectToServer.this,ConnectToServer.this);
                        connectTask.execute();
                    }
                }, 3000);
                break;
            default:
                break;
        }
    }
    private void makeToast(String message) {
        if(message == null) {
            message = "null";
        }
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }
    private void logd(String logMessage) {
        Log.d(ConnectToServer.TAG,logMessage);
    }

    @Override
    public void connectTaskCompleted(String message) {
        logd("connectTaskCompleted()");

    }

    @Override
    public void connectTaskStartDataTransfer() {
        logd("connectTaskStartDataTransfer()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ConnectToServer.this, DataTransfer.class));
                finish();
            }
        });
    }

    @Override
    public void connectTaskStarted() {
        logd("connectTaskStarted()");

    }

    @Override
    public void connectTaskCancelled() {
        logd("connectTaskCancelled()");

    }

    @Override
    public void connectTaskProgressPublish(String Update) {
        logd("connectTaskProgressPublish()");

    }

    @Override
    public void connectTaskError(String e) {
        logd("connectTaskError()");

    }

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Transferring data");
        alertDialog = builder.create();
        return alertDialog;
    }

}
