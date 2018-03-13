package com.examples.akshay.wififiletranserfer.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.ConnectionDetails;
import com.examples.akshay.wififiletranserfer.Constants;
import com.examples.akshay.wififiletranserfer.NSDHelper;
import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.ServerDetails;
import com.examples.akshay.wififiletranserfer.Tasks.AcceptConnectionTask;
import com.examples.akshay.wififiletranserfer.Tasks.ConnectTask;
import com.examples.akshay.wififiletranserfer.Utils;
import com.examples.akshay.wififiletranserfer.interfaces.AcceptConnectionTaskUpdate;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,TaskUpdate, AcceptConnectionTaskUpdate {

    WifiManager wifiManager;
    //NSDHelper mNSDHelper;

    Button buttonCreateHotSpot;
    Button buttonConnectToHotSpot;
    Button buttonTest2;
    Button buttonTest3;
    Button buttonTest4;
    Button buttonScanQRCode;
    Button buttonGenerateQRCode;

    AcceptConnectionTask acceptConnectionTask;
    ConnectTask connectTask;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    private static final String TAG = "===MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        checkPermissions();
        //mNSDHelper = new NSDHelper(this,this);
        //mNSDHelper.initializeNsd();
        acceptConnectionTask = new AcceptConnectionTask(this,this);
        connectTask = new ConnectTask(this,this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mReceiver = getBroadCastRecevier();
        mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");

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
            case R.id.main_activity_button_create_hotspot:
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
             /*   if(!mNSDHelper.isDiscovering()) {
                    logd("trying to start discovery");
                    mNSDHelper.discoverServices();
                } else {
                    logd("already discovering");
                    makeToast("Already discovering");
                }*/
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
                break;
            case  R.id.main_activity_button_scan_qrcode:
                IntentIntegrator intentIntegrator= new IntentIntegrator(this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
                break;
            case R.id.main_activity_button_generate_qrcode:
                Intent intentShowQRCode = new Intent(this, ShowQRCode.class);
                intentShowQRCode.putExtra(Constants.KEY_SSID,Constants.SSID);
                intentShowQRCode.putExtra(Constants.KEY_PASSWORD,Constants.PASSWORD);
                intentShowQRCode.putExtra(Constants.KEY_SERVER_PORT,Constants.SERVER_PORT);
                intentShowQRCode.putExtra(Constants.KEY_IP,Constants.KEY_IP);

                startActivity(intentShowQRCode);
                break;
            default:
                makeToast("Yet to do...");
                break;
        }
    }

    private void setupUI() {

        buttonCreateHotSpot = findViewById(R.id.main_activity_button_create_hotspot);
        buttonCreateHotSpot.setOnClickListener(this);

        buttonConnectToHotSpot = findViewById(R.id.main_activity_button_connect_to_hotspot);
        buttonConnectToHotSpot.setOnClickListener(this);

        buttonTest2 = findViewById(R.id.main_activity_button_test2);
        buttonTest2.setOnClickListener(this);

        buttonTest3 = findViewById(R.id.main_activity_button_test3);
        buttonTest3.setOnClickListener(this);

        buttonTest4 = findViewById(R.id.main_activity_button_test4);
        buttonTest4.setOnClickListener(this);

        buttonScanQRCode = findViewById(R.id.main_activity_button_scan_qrcode);
        buttonScanQRCode.setOnClickListener(this);

        buttonGenerateQRCode = findViewById(R.id.main_activity_button_generate_qrcode);
        buttonGenerateQRCode.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                logd(result.getContents());

                ConnectionDetails connectionDetails = parseString(result.getContents());
                logd( "Parsed contents : " + connectionDetails.getIp() + " " + connectionDetails.getSsid() + " " + connectionDetails.getPassword() + " " + connectionDetails.getPort());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ConnectionDetails parseString(String qrString) {

        ConnectionDetails connectionDetails = new ConnectionDetails();
        try {
            JSONObject jsonObject = new JSONObject(qrString);
            connectionDetails.setIp(jsonObject.getString(Constants.KEY_IP));
            connectionDetails.setPassword(jsonObject.getString(Constants.KEY_PASSWORD));
            connectionDetails.setPort(jsonObject.getInt(Constants.KEY_SERVER_PORT));
            connectionDetails.setSsid(jsonObject.getString(Constants.KEY_SSID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  connectionDetails;
    }

    private void createHotSpot() {
        WifiConfiguration netConfig = new WifiConfiguration();

        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }

        netConfig.SSID = Constants.SSID;
        //netConfig.preSharedKey = Constants.PASSWORD;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);


        try{
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, netConfig,true);

            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate=(Integer)getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig=(WifiConfiguration)getWifiApConfigurationMethod.invoke(wifiManager);
            Log.d("CLIENT", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");

        } catch (Exception e) {
            Log.d(this.getClass().toString(), "", e);
        }
    }

    private void connectToHotSpot() {

        try {
            if(!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
            }

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" +Constants.SSID + "\"";
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            int netId = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.disconnect();
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + Constants.SSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG,e.toString());
            Toast.makeText(getApplicationContext(),"Exception :" + e.toString(),Toast.LENGTH_LONG).show();
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

        buttonConnectToHotSpot.setEnabled(true);
        buttonCreateHotSpot.setEnabled(true );
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
                buttonConnectToHotSpot.setEnabled(true);
                buttonCreateHotSpot.setEnabled(true);
                Log.d(MainActivity.TAG,"All permissions granted");
            } else {
                Log.d(MainActivity.TAG,"permissions denied : " + (grantResults.length-all));
                buttonConnectToHotSpot.setEnabled(false);
                buttonCreateHotSpot.setEnabled(false);
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

    @Override
    public void TaskCompleted(String message) {

    }

    @Override
    public void TaskStarted() {

    }

    @Override
    public void TaskProgressPublish(String Update) {

    }

    @Override
    public void TaskError(String e) {

    }

    @Override
    public void TaskData(String ip, int PORT) {
        makeToast(ip+" "+ PORT);
        ServerDetails.IP = ip;
        ServerDetails.PORT = PORT;

    }

    @Override
    public void SetIP(String ip) {
        ServerDetails.IP = ip;
    }

    @Override
    public void SetPORT(int port) {
        ServerDetails.PORT = port;
    }

    @Override
    public void Ready() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeToast("Ready to accept connection");
                //mNSDHelper.registerService(ServerDetails.PORT,ServerDetails.IP);

            }
        });

    }

    @Override
    public void StartDataTransfer() {
        Intent intent = new Intent(this, DataTransfer.class);
        startActivity(intent);
    }

    private BroadcastReceiver getBroadCastRecevier() {

        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {

                    // get Wi-Fi Hotspot state here
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                    if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                        // Wifi is enabled
                        logd("Wifi enabled");
                        makeToast("Wifi enabled");
                    } else  {
                        makeToast("Wifi disabled");
                        logd("Wifi enabled");

                    }

                }
            }
        };
    }
}
