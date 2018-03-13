package com.examples.akshay.wififiletranserfer;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.util.Log;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;

public class NSDHelper {

    Context mContext;

    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;
    public static final String SERVICE_TYPE = "_http._tcp.";

    public static final String TAG = "===NSDHelper";
    public String mServiceName = "NsdChat";

    boolean isDiscovering;
    NsdServiceInfo mService;

    TaskUpdate taskUpdate;
    public NSDHelper(Context context,TaskUpdate taskUpdate) {
        mContext = context;
        this.taskUpdate = taskUpdate;
        logd("before getting NSDManager object");
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        logd("after getting NSDManager object");

    }

    public void initializeNsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                isDiscovering = true;
                logd("Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                logd("Service discovery success");
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    logd("Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    logd("Same machine: " + mServiceName);
                    mNsdManager.resolveService(service, mResolveListener);
                } else //if (service.getServiceName().contains(mServiceName))
                    {
                    logd("Found service : "+ service.getServiceName());
                    makeToast("Found service :" + service.getServiceName());
                    //mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
               logd("service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
                logd("Discovery stopped: " + serviceType);
                isDiscovering = false;

            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                logd("Discovery failed: Error code:" + errorCode);
                isDiscovering = false;
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                isDiscovering = true;

                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                logd("Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                logd("Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    logd("Same IP.");
                    return;
                }
                mService = serviceInfo;
            }
        };
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
                logd("mRegistrationListener onServiceRegistered()");
                makeToast("mRegistrationListener onServiceRegistered()");
            }
            
            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
                logd("mRegistrationListener onRegistrationFailed()" + arg0.toString());
                makeToast("mRegistrationListener onRegistrationFailed()");

            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                logd("mRegistrationListener onServiceUnregistered()");
                makeToast("mRegistrationListener onServiceUnregistered()");

            }
            
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                logd("mRegistrationListener onUnregistrationFailed()");
                makeToast("mRegistrationListener onUnregistrationFailed()");

            }
            
        };
    }

    public void registerService(int port,String ip) {
        logd("registerService() " + port);

        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        
    }

    public void discoverServices() {
        logd("discoverServices()");

        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public boolean isDiscovering() {
        return isDiscovering;
    }
    
    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }
    
    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
    }

    private void logd(String log) {
        Log.d(NSDHelper.TAG,log);
    }

    private void makeToast(String toast) {
        Toast.makeText(mContext, toast , Toast.LENGTH_SHORT).show();
    }
}
