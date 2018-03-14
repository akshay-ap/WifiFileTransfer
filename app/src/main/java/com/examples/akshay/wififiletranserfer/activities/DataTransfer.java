package com.examples.akshay.wififiletranserfer.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.Constants;
import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.Tasks.FileReceiverTask;
import com.examples.akshay.wififiletranserfer.Tasks.FileSenderTask;
import com.examples.akshay.wififiletranserfer.Tasks.MultiFileReceiverTask;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class DataTransfer extends AppCompatActivity implements View.OnClickListener,TaskUpdate {
    private final static String TAG = "===DataTransfer";

    AlertDialog alertDialog;
    BroadcastReceiver broadcastReceiver;
    Button buttonTest1;
    Button buttonTest2;
    Button buttonFromSend;
    Button buttonFromReceive;

    FileReceiverTask fileReceiverTask;
    MultiFileReceiverTask multiFileReceiverTask;
    IntentFilter intentFilter;
    FileSenderTask fileSenderTask;
    String toDisplay;
    boolean state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        setupUI();

        broadcastReceiver = getBroadCastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DATA_TRANSFER_ACTION);
    }

    private void setupUI() {

        alertDialog = getAlertDialog();

        buttonTest1 = findViewById(R.id.activity_data_transfer_test_1);
        buttonTest1.setOnClickListener(this);

        buttonTest2 = findViewById(R.id.activity_data_transfer_test_2);
        buttonTest2.setOnClickListener(this);

        buttonFromSend = findViewById(R.id.activity_data_transfer_from_send);
        buttonFromSend.setOnClickListener(this);

        buttonFromReceive = findViewById(R.id.activity_data_transfer_form_receive);
        buttonFromReceive.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_data_transfer_test_1:
                toDisplay = "Received : ";
                state = false;
                if(fileReceiverTask == null || fileReceiverTask.getStatus() == AsyncTask.Status.FINISHED) {
                    fileReceiverTask = new FileReceiverTask(DataTransfer.this);
                    fileReceiverTask.execute();
                } else {
                    Toast.makeText(DataTransfer.this,"Already running another task",Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.activity_data_transfer_test_2:
                toDisplay = "Sent    : ";
                state = true;

                String path = String.valueOf(Environment.getExternalStorageDirectory());
                new ChooserDialog().with(this)
                        .withStartFile(path)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(DataTransfer.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                                if(fileSenderTask == null || fileSenderTask.getStatus() == AsyncTask.Status.FINISHED) {
                                    fileSenderTask = new FileSenderTask(path,DataTransfer.this);
                                    fileSenderTask.execute();
                                }  else {
                                    Toast.makeText(DataTransfer.this,"Already in sending task is running",Toast.LENGTH_SHORT).show();
                                    Log.d(DataTransfer.TAG,"already sending task is running");
                                }
                            }
                        })
                        .build()
                        .show();
                break;

            case  R.id.activity_data_transfer_from_send:
                startActivity(new Intent(this,CollectSend.class));
                break;
            case  R.id.activity_data_transfer_form_receive:

                toDisplay = "Received : ";
                state = false;
                if(multiFileReceiverTask == null || multiFileReceiverTask.getStatus() == AsyncTask.Status.FINISHED) {
                    multiFileReceiverTask = new MultiFileReceiverTask(DataTransfer.this);
                    multiFileReceiverTask.execute();
                } else {
                    Toast.makeText(DataTransfer.this,"Already running another task",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver getBroadCastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(DataTransfer.TAG,"broadCastReceived...");
            }
        };
    }

    @Override
    public void TaskCompleted(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(DataTransfer.TAG,"TaskCompleted()");
                if(alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                Toast.makeText(DataTransfer.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void TaskStarted() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(DataTransfer.TAG,"TaskStarted()");
                String toShow;
                if (state) {
                    toShow = "Waiting for receiver...";
                }
                else {
                    toShow = "Waiting for sender...";
                }

                if(!alertDialog.isShowing()) {
                    alertDialog= getAlertDialog();
                    alertDialog.setMessage(toShow);
                    alertDialog.show();
                }
            }
        });

    }

    @Override
    public void TaskProgressPublish(final String update) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(DataTransfer.TAG,"TaskProgressPublish()");
                if(!alertDialog.isShowing()) {
                    alertDialog.show();
                }
                alertDialog.setMessage(update);
            }
        });
    }

    @Override
    public void TaskError(final String e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                Toast.makeText(DataTransfer.this,e,Toast.LENGTH_SHORT).show();
            }
        });
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
