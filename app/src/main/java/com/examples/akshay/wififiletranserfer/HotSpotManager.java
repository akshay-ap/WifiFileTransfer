package com.examples.akshay.wififiletranserfer;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
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
                }
            }
        }
    }

    public void connectToHotSpot() {
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
                    //hotspotUpdate.connectedToHotspot();
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void stopHotspot() {

    }

    public boolean isHotspotEnabled() {

        Method method = null;
        try {
            //wifiManager.getWifiState();
            method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
            method.setAccessible(true);
            int actualState = (Integer) method.invoke(wifiManager, (Object[]) null);

            if(actualState == WifiManager.WIFI_STATE_ENABLED ) {
                return true;
            }

        } catch (NoSuchMethodException e) {
            logd(e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logd(e.toString());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            logd(e.toString());
        }

        return false;
    }
    private void logd(String logMessage) {
        Log.d(HotSpotManager.TAG,logMessage);
    }
}
