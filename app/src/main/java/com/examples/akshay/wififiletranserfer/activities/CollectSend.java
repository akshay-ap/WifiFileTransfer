package com.examples.akshay.wififiletranserfer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.Constants;
import com.examples.akshay.wififiletranserfer.InstanceProviderAPI;
import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.Tasks.MultiFileSenderTask;
import com.examples.akshay.wififiletranserfer.adapters.FormInstanceAdapter;
import com.examples.akshay.wififiletranserfer.dto.Instance;
import com.examples.akshay.wififiletranserfer.interfaces.InstanceSelectionUpdate;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectSend extends AppCompatActivity implements View.OnClickListener,InstanceSelectionUpdate,TaskUpdate {

    RecyclerView recyclerViewPairedDevices;
    AlertDialog alertDialog;

    MultiFileSenderTask multiFileSenderTask;
    Button buttonSend;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    FormInstanceAdapter formInstanceAdapter;
    ArrayList<Instance> arrayListInstances;
    ArrayList<Boolean> arrayListSelectedInstances;
    Cursor mCursor;
    public static final String TAG = "===CollectSend";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_send);

        formInstanceAdapter = new FormInstanceAdapter(new ArrayList<Instance>(),CollectSend.this,this);
        mCursor = getCompletedInstancesCursor();

        setupUI();

        broadcastReceiver = getBroadCastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DATA_TRANSFER_ACTION);

        arrayListInstances = new ArrayList<>();
        arrayListSelectedInstances = new ArrayList<>();
        while (mCursor.moveToNext()) {

            Instance.Builder builder = new Instance.Builder();
            String displayName = mCursor.getString(mCursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
            String instancePath = mCursor.getString(mCursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
            builder.displayName(displayName);
            builder.instanceFilePath(instancePath);

            Log.d(CollectSend.TAG,"Instance displayname : "+displayName +" path : "+instancePath);
            arrayListInstances.add(builder.build());
            arrayListSelectedInstances.add(false);
        }

        formInstanceAdapter.setArrayListInstanceAdapter(arrayListInstances);
        recyclerViewPairedDevices.setAdapter(formInstanceAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    private void setupUI() {

        alertDialog = getAlertDialog();


        recyclerViewPairedDevices = findViewById(R.id.activity_collect_send_recycler_view_from_instances);
        RecyclerView.LayoutManager layoutManagerPairedDevices = new LinearLayoutManager(getApplicationContext());
        recyclerViewPairedDevices.setLayoutManager(layoutManagerPairedDevices);
        recyclerViewPairedDevices.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPairedDevices.setAdapter(formInstanceAdapter);

        buttonSend = findViewById(R.id.activity_collect_send_button);
        buttonSend.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connecting...");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(CollectSend.TAG,"Cancel button Clicked");
            }
        });

    }

    public Cursor getCompletedInstancesCursor() {
        String selection = InstanceProviderAPI.InstanceColumns.STATUS + "=? or " + InstanceProviderAPI.InstanceColumns.STATUS + "=?";
        String[] selectionArgs = {InstanceProviderAPI.STATUS_COMPLETE};
        String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
        Cursor mCursor= getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI,null,selection,selectionArgs,sortOrder);
        return mCursor;
    }


    private Cursor getDataFromContentProvider() {
        //String [] arr = new String[] {};
        Cursor mCursor= getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI,null,null,null,null);

        if (null == mCursor) {
            Log.d(CollectSend.TAG," mCursor is null");
            Toast.makeText(CollectSend.this,"Cursor is null",Toast.LENGTH_SHORT).show();
        } else if (mCursor.getCount() < 1) {

            Log.d(CollectSend.TAG," mCursor is empty");
            Toast.makeText(CollectSend.this,"Cursor is empty",Toast.LENGTH_SHORT).show();
        } else {
            Log.d(CollectSend.TAG," mCursor is non-empty");
            Toast.makeText(CollectSend.this,"Cursor is non-empty",Toast.LENGTH_SHORT).show();
            Log.d(CollectSend.TAG," mCursor no. of cloumns : " +mCursor.getColumnCount());
            String cloumnNames [] = mCursor.getColumnNames();
            Log.d(CollectSend.TAG," mCursor cloumns : " +  Arrays.toString(cloumnNames));
        }
        return  mCursor;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_collect_send_button:
                Log.d(CollectSend.TAG,"Send button Clicked");
                if(arrayListSelectedInstances.size() != arrayListInstances.size()) {
                    Log.d(CollectSend.TAG,"Implementation error sizes of arraylist differ...please solve this bug");
                    Toast.makeText(this,"Implementation error sizes of arraylist differ...please solve this bug",Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> formPathsToSend = new ArrayList<>();
                for(int i=0;i < arrayListSelectedInstances.size();i++) {
                    if(arrayListSelectedInstances.get(i)== true) {
                        Instance instance = arrayListInstances.get(i);
                        Log.d(CollectSend.TAG,instance.getInstanceFilePath());
                        Log.d(CollectSend.TAG,"To send  :" + i);
                        formPathsToSend.add(instance.getInstanceFilePath());
                    }
                }

                if(multiFileSenderTask == null || multiFileSenderTask.getStatus() == AsyncTask.Status.FINISHED) {
                    multiFileSenderTask = new MultiFileSenderTask(CollectSend.this,formPathsToSend);
                    multiFileSenderTask.execute();
                }  else {
                    Toast.makeText(CollectSend.this,"Already in sending task is running",Toast.LENGTH_SHORT).show();
                    Log.d(CollectSend.TAG,"already sending task is running");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void select(int position) {
        arrayListSelectedInstances.set(position,true);
        Log.d(CollectSend.TAG,"selected instance : "+ position);
    }

    @Override
    public void unSelect(int position) {
        arrayListSelectedInstances.set(position,false);
        Log.d(CollectSend.TAG,"unselected instance : "+ position);
    }

    @Override
    public void TaskCompleted(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(CollectSend.TAG,"TaskCompleted()");
                if(alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                String toShow;
                toShow = "File sent : "+ message;
                Toast.makeText(CollectSend.this,toShow,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void TaskStarted() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(CollectSend.TAG,"TaskStarted()");
                String toShow;
                toShow = "Waiting for receiver...";
                if(!alertDialog.isShowing()) {
                    alertDialog = getAlertDialog();
                    alertDialog.setMessage(toShow);
                    alertDialog.show();
                }
            }
        });

    }

    @Override
    public void TaskProgressPublish(final String  update) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(CollectSend.TAG,"TaskProgressPublish()");
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
                Toast.makeText(CollectSend.this,e,Toast.LENGTH_SHORT).show();
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
    private BroadcastReceiver getBroadCastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(CollectSend.TAG,"broadCastReceived...");
            }
        };
    }
}
