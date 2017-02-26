package com.lilith.sdk.community.plugin;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duanefaith on 2017/2/16.
 */

public class PluginSDK {

    private static final String TAG = "PluginSDK";

    private static PluginSDK mInstance = null;

    public static PluginSDK getInstance() {
        if (mInstance == null) {
            synchronized (PluginSDK.class) {
                if (mInstance == null) {
                    mInstance = new PluginSDK();
                }
            }
        }
        return mInstance;
    }

    private WeakReference<Application> mAppRef;
    private String mPackageName;
    private String mProcessName;
    private final Map<Class<? extends BasePluginRemoteService>, RemoteServiceInfo> mRemoteServiceMap = new HashMap<Class<? extends BasePluginRemoteService>, RemoteServiceInfo>();

    private PluginSDK() {

    }

    public void registerPluginRemoteService(Class<? extends BasePluginRemoteService> serviceClass
            , Class<? extends BasePluginContainerActivity> activityClass, String assetName) {
        mRemoteServiceMap.put(serviceClass, new RemoteServiceInfo(serviceClass, activityClass, assetName));
    }

    public void init(Application application) {
        if (application == null) {
            return;
        }
        mAppRef = new WeakReference<Application>(application);
        mPackageName = application.getPackageName();
        if (isMainProcess()) {
            if (!mRemoteServiceMap.isEmpty()) {
                for (Class<? extends BasePluginRemoteService> clazz : mRemoteServiceMap.keySet()) {
                    if (clazz != null) {
                        Intent intent = new Intent(application, clazz);
                        application.bindService(intent, new PluginServiceConnection(), Context.BIND_AUTO_CREATE);
                    }
                }
            }
        } else {
            PluginRuntime.createInstance();
            PluginRuntime.getInstance().init(getApplication());
        }
    }

    public void terminate() {
        Application application = getApplication();
        if (isMainProcess()) {
            if (application != null && !mRemoteServiceMap.isEmpty()) {
                for (RemoteServiceInfo info : mRemoteServiceMap.values()) {
                    if (info != null && info.getConnection() != null) {
                        application.unbindService(info.getConnection());
                    }
                }
            }
        }
    }

    public Application getApplication() {
        if (mAppRef == null) {
            return null;
        }
        return mAppRef.get();
    }

    public String getProcessName() {
        if (TextUtils.isEmpty(mProcessName)) {
            Application application = getApplication();
            if (application == null) {
                return null;
            }
            int pid = android.os.Process.myPid();
            ActivityManager am = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                if (processInfo != null && processInfo.pid == pid) {
                    mProcessName = processInfo.processName;
                    break;
                }
            }
        }
        return mProcessName;
    }

    public boolean isMainProcess() {
        String processName = getProcessName();
        return !TextUtils.isEmpty(processName)
                && processName.trim().equals(mPackageName);
    }

    private class PluginServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (name != null && name.getClassName() != null) {
                Class clazz = null;
                try {
                    clazz = Class.forName(name.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null && mRemoteServiceMap.containsKey(clazz)) {
                    RemoteServiceInfo remoteServiceInfo = mRemoteServiceMap.get(clazz);
                    if (remoteServiceInfo != null) {
                        remoteServiceInfo.setRemoteInterface(IRemoteInterface.Stub.asInterface(service));
                        remoteServiceInfo.setConnection(this);

                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.PARAM_ASSET_NAME, remoteServiceInfo.getAssetPluginName());
                        bundle.putSerializable(Constants.PARAM_ACTIVITY_CLASS, remoteServiceInfo.activityClass);
                        bundle.putSerializable(Constants.PARAM_SERVICE_CLASS, remoteServiceInfo.serviceClass);

                        try {
                            remoteServiceInfo.getRemoteInterface().call(Constants.CODE_SET_PLUIN_INIT, bundle, null);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name != null && name.getClassName() != null) {
                Class clazz = null;
                try {
                    clazz = Class.forName(name.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null && mRemoteServiceMap.containsKey(clazz)) {
                    RemoteServiceInfo remoteServiceInfo = mRemoteServiceMap.get(clazz);
                    if (remoteServiceInfo != null) {
                        remoteServiceInfo.setConnection(null);
                        remoteServiceInfo.setRemoteInterface(null);
                    }
                }
            }
        }
    };


    private class RemoteServiceInfo {

        private Class<? extends BasePluginRemoteService> serviceClass;
        private Class<? extends BasePluginContainerActivity> activityClass;
        private String assetPluginName;
        private IRemoteInterface remoteInterface;
        private PluginServiceConnection connection;

        private RemoteServiceInfo(Class<? extends BasePluginRemoteService> serviceClass
                , Class<? extends BasePluginContainerActivity> activityClass, String assetPluginName) {
            this.serviceClass = serviceClass;
            this.activityClass = activityClass;
            this.assetPluginName = assetPluginName;
        }

        public Class<? extends BasePluginRemoteService> getServiceClass() {
            return serviceClass;
        }

        public String getAssetPluginName() {
            return assetPluginName;
        }

        public IRemoteInterface getRemoteInterface() {
            return remoteInterface;
        }

        public void setRemoteInterface(IRemoteInterface remoteInterface) {
            this.remoteInterface = remoteInterface;
        }

        public PluginServiceConnection getConnection() {
            return connection;
        }

        public void setConnection(PluginServiceConnection connection) {
            this.connection = connection;
        }
    }
}
