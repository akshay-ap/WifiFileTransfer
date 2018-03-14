package com.examples.akshay.wififiletranserfer;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.List;


public class HotSpotManager {

    private static final String TAG = "===HotSpotManager";
    private WifiManager wifiManager;
    private Handler mHandler;
    private Message o;
    private Context context;
    public HotSpotManager(Context context,WifiManager wifiManager,Handler mHandler) {
        this.context = context;
        this.wifiManager = wifiManager;
        this.mHandler = mHandler;
    }
    public void createHotSpot() {

/*        if(HotSpotManager.isApOn(context)) {
            HotSpotManager.configApState(context);
        }*/

        o = new Message();
        o.obj = Constants.HOTSPOT_CREATION_STARTED;
        mHandler.sendMessage(o);
        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class
        boolean methodFound=false;
        for(Method method: wmMethods){
            if(method.getName().equals("setWifiApEnabled")){
                methodFound=true;
                WifiConfiguration netConfig = new WifiConfiguration();
                connectionDetails.setSsid(Utils.getRandomString());
                connectionDetails.setPassword(Utils.getRandomString());
                netConfig.SSID = connectionDetails.getSsid();
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                try {
                    boolean apstatus=(Boolean) method.invoke(wifiManager, netConfig,true);
                    for (Method isWifiApEnabledmethod: wmMethods) {
                        if(isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {
                            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)) {

                            };

                            for(Method method1: wmMethods){
                                if(method1.getName().equals("getWifiApState")){
                                    int apstate;
                                    apstate=(Integer)method1.invoke(wifiManager);
                                }
                            }

                            logd("======= "+Utils.getIPAddress(true));
                           /* WifiInfo wifiinfo = wifiManager.getConnectionInfo();
                            byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
// you must reverse the byte array before conversion. Use Apache's commons library
                            Utils.reverse(myIPAddress);
                            InetAddress myInetIP = InetAddress.getByAddress(myIPAddress);
                            String myIP = myInetIP.getHostAddress();
                            logd("=============== "+ myIP);*/
                        }
                    }
                    if(apstatus) {
                        o = new Message();
                        o.obj = Constants.HOTSPOT_CREATION_SUCCESS;
                        mHandler.sendMessage(o);
                       logd("SUCCESS");
                    }else {
                        o = new Message();
                        o.obj = Constants.HOTSPOT_CREATION_FAILED;
                        mHandler.sendMessage(o);
                       logd("FAILED");
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } /*catch (UnknownHostException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    public void connectToHotSpot() {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration conf = new WifiConfiguration();
        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
        String ssid = "\"" +connectionDetails.getSsid()+"\"";
        conf.SSID = ssid;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if(list == null ) {
            logd("list is null");
            Message o = new Message();
            o.obj = Constants.WIFI_CONNECTION_FAILURE;
            mHandler.sendMessage(o);
            return;
        }

        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals(ssid)) {
                try {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    logd("i.networkId " + i.networkId + "\n");
                    wifiManager.reconnect();
                    Message o = new Message();
                    o.obj = Constants.WIFI_CONNECTION_SUCCESS;
                    mHandler.sendMessage(o);
                    //hotspotUpdate.connectedToHotspot();
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Message o = new Message();
                    o.obj = Constants.WIFI_CONNECTION_FAILURE;
                    mHandler.sendMessage(o);
                }

            }
        }
    }

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            if(isApOn(context)) {
                if (wifimanager != null) {
                    wifimanager.setWifiEnabled(false);
                }
            }
            Method method = null;
            if (wifimanager != null) {
                method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            }
            if (method != null) {
                method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void logd(String logMessage) {
        Log.d(HotSpotManager.TAG,logMessage);
    }
}
