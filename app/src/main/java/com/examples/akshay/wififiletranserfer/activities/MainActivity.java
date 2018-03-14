package com.examples.akshay.wififiletranserfer.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.ConnectionDetails;
import com.examples.akshay.wififiletranserfer.Constants;
import com.examples.akshay.wififiletranserfer.HotSpotManager;
import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.Tasks.AcceptConnectionTask;
import com.examples.akshay.wififiletranserfer.Tasks.ConnectTask;
import com.examples.akshay.wififiletranserfer.Utils;
import com.examples.akshay.wififiletranserfer.interfaces.AcceptConnectionTaskUpdate;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    WifiManager wifiManager;
    AlertDialog alertDialog;

    HotSpotManager hotSpotManager;
    Button buttonScanQRCode;
    Button buttonGenerateQRCode;

    Thread hotspotCreationThread;
    Handler mHandler;
    //AcceptConnectionTask acceptConnectionTask;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    private static final String TAG = "===MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        checkPermissions();

        //acceptConnectionTask = new AcceptConnectionTask(this,this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mReceiver = getBroadCastRecevier();
        mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");

        mHandler = getHandler();

        hotSpotManager = new HotSpotManager(this,wifiManager,mHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (mNSDHelper != null && mNSDHelper.isDiscovering()) {
            mNSDHelper.stopDiscovery();
        }*/
        unregisterReceiver(mReceiver);
        logd("onPause");
    }

    @Override
    protected void onDestroy() {
        //mNSDHelper.tearDown();
        super.onDestroy();
        if(HotSpotManager.isApOn(this)) {
            HotSpotManager.configApState(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        logd("onResume");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.main_activity_button_create_hotspot:
                Log.d(MainActivity.TAG,"main_activity_button_create_hotspot CLICK");
                createHotSpot();
                break;
            case R.id.main_activity_button_connect_to_hotspot:
                Log.d(MainActivity.TAG,"main_activity_button_connect_to_hotspot CLICK");
                connectToHotSpot();
                break;
            case R.id.main_activity_button_test1:
                Log.d(MainActivity.TAG,"main_activity_button_test1 CLICK");
                    //mNSDHelper.registerService(ServerDetails.PORT,ServerDetails.IP);
                break;
            case R.id.main_activity_button_test2:
                Log.d(MainActivity.TAG,"main_activity_button_test2 CLICK");
             *//*   if(!mNSDHelper.isDiscovering()) {
                    logd("trying to start discovery");
                    mNSDHelper.discoverServices();
                } else {
                    logd("already discovering");
                    makeToast("Already discovering");
                }*//*
                break;
            case R.id.main_activity_button_test3:
                if(!(acceptConnectionTask.getStatus() == AsyncTask.Status.RUNNING)) {
                    acceptConnectionTask.execute();
                } else {
                    logd("acceptConnectionTaskAlready running");
                }
                break;
            case R.id.main_activity_button_test4:
                if(!(connectTask.getStatus() == AsyncTask.Status.RUNNING)) {
                    connectTask.execute();
                } else {
                    logd("connectTask running");
                }
                break;*/
            case  R.id.main_activity_button_scan_qrcode:

                if(HotSpotManager.isApOn(this)) {
                    HotSpotManager.configApState(this);
                }

                IntentIntegrator intentIntegrator= new IntentIntegrator(this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.initiateScan();
                break;
            case R.id.main_activity_button_generate_qrcode:


                Log.d(MainActivity.TAG,"main_activity_button_create_hotspot CLICK");
                hotspotCreationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message;
                        if(HotSpotManager.isApOn(MainActivity.this)) {
                            HotSpotManager.configApState(MainActivity.this);
                            try {
                                message = new Message();
                                message.obj = Constants.HOTSPOT_TURN_OFF_REQUEST;
                                mHandler.sendMessage(message);
                                Thread.sleep(Constants.HOTSPOT_TURN_OFF_WAIT_PERIOD);
                                message = new Message();
                                message.obj = Constants.HOTSPOT_TURN_OFF_SUCCESS;
                                mHandler.sendMessage(message);
                            } catch (InterruptedException e) {
                                message = new Message();
                                message.obj = Constants.HOTSPOT_TURN_OFF_FAILED;
                                mHandler.sendMessage(message);
                                e.printStackTrace();
                                logd(e.toString());
                            }
                        }
                        hotSpotManager.createHotSpot();
                    }
                });

                hotspotCreationThread.start();
                break;

            default:
                makeToast("Yet to do...");
                break;
        }
    }

    private void setupUI() {

        buttonScanQRCode = findViewById(R.id.main_activity_button_scan_qrcode);
        buttonScanQRCode.setOnClickListener(this);

        buttonGenerateQRCode = findViewById(R.id.main_activity_button_generate_qrcode);
        buttonGenerateQRCode.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Creating hotspot");
        builder.setCancelable(false);
        alertDialog = builder.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                makeToast("Cancelled");
            } else {
                makeToast( "Scanned: " + result.getContents());
                logd(result.getContents());

                Utils.parseString(result.getContents());
                ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
                logd( "Parsed contents : " + connectionDetails.getIp() + " " + connectionDetails.getSsid() + " " + connectionDetails.getPassword() + " " + connectionDetails.getPort());

                if(connectionDetails.getIp() != null && connectionDetails.getPort() != 0 && connectionDetails.getPassword() !=null && connectionDetails.getSsid()!= null) {
                    logd("Valid data received...");
                    hotSpotManager.connectToHotSpot();

                } else {
                    makeToast("Invalid data received...");
                    logd("QRCode scan returned invalid data");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void checkPermissions() {

        boolean read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        boolean write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        boolean settings = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            settings = Settings.System.canWrite(getApplication());
        } else {
           settings = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PERMISSION_GRANTED;
        }
        ArrayList<String> arrayListPermissions = new ArrayList<>();

        if (!read) {
            arrayListPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.d(MainActivity.TAG, "READ_EXTERNAL_STORAGE permission needed");
        }
        if (!write) {
            arrayListPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.d(MainActivity.TAG, "WRITE_EXTERNAL_STORAGE permission needed");

        }
        if (!settings) {
            arrayListPermissions.add(Manifest.permission.WRITE_SETTINGS);
            Log.d(MainActivity.TAG, "WRITE_SETTINGS permission needed");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }

        if (arrayListPermissions.size() != 0) {

            String[] permissions = new String[arrayListPermissions.size()];
            for (int i = 0; i < arrayListPermissions.size(); i++) {
                permissions[i] = arrayListPermissions.get(i);
            }

            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSIONS_REQUEST);
            Log.d(MainActivity.TAG, "Requesting permissions");
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int all = 0;
        if(requestCode == Constants.PERMISSIONS_REQUEST) {
            Log.d(MainActivity.TAG,"Number of permissions : "+ permissions.length);
            for (int res : grantResults) {
                if(res == PERMISSION_GRANTED) all++;
            }
            if(all == grantResults.length) {
                Log.d(MainActivity.TAG,"All permissions granted");
            } else {
                Log.d(MainActivity.TAG,"permissions denied : " + (grantResults.length-all));
            }
        }
    }


    private void logd(String tolog) {
        Log.d(MainActivity.TAG,tolog);
    }

    private void makeToast(String x) {
        if (x == null) {
            x = "null";
        }
        Toast.makeText(getApplicationContext(),x,Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver getBroadCastRecevier() {

        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*String action = intent.getAction();
                if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                        // Wifi is enabled
                        logd("Wifi enabled");
                        makeToast("Wifi enabled");
                    } else  {
                        makeToast("Wifi disabled");
                        logd("Wifi enabled");

                    }

                }*/
            }
        };
    }

    private Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                int status = (int)inputMessage.obj;
                switch (status) {
                    case Constants.HOTSPOT_CREATION_STARTED:
                        makeToast("Hotspot Creation Started");
                        if(!alertDialog.isShowing()) {
                        alertDialog.show();
                        }
                        break;
                    case Constants.HOTSPOT_CREATION_FAILED:
                        if(alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        makeToast("Hotspot creation failed");

                        break;
                    case Constants.HOTSPOT_CREATION_SUCCESS:
                        if(alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        Intent intentShowQRCode = new Intent(MainActivity.this, ShowQRCode.class);
                        startActivity(intentShowQRCode);

/*                        makeToast("Hotspot created");
                        if(acceptConnectionTask.getStatus() == AsyncTask.Status.RUNNING) {
                            acceptConnectionTask.cancel();
                            acceptConnectionTask = null;
                            acceptConnectionTask = new AcceptConnectionTask(MainActivity.this,MainActivity.this);
                        }
                        acceptConnectionTask.execute();*/
                        break;
                    case Constants.WIFI_CONNECTION_SUCCESS:
                        startActivity(new Intent(MainActivity.this,ConnectToServer.class));
                        logd("Connected to hotspot");

                        break;
                    case Constants.WIFI_CONNECTION_FAILURE:
                        logd("Connection to hotspot failed");
                        makeToast("Try again..connection attempt failed");
                        break;

                    case Constants.HOTSPOT_TURN_OFF_FAILED:
                        if(alertDialog.isShowing()) {
                            alertDialog.dismiss();
                            makeToast("Hotspot termination failed");
                        }
                        break;
                    case Constants.HOTSPOT_TURN_OFF_REQUEST:
                        if(!alertDialog.isShowing()) {
                            alertDialog.show();
                        }
                        break;
                    case Constants.HOTSPOT_TURN_OFF_SUCCESS:
                        if(alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        break;
                    default:
                        makeToast("Unhandled message" + status);
                        break;
                }
            }
        };
    }
}
