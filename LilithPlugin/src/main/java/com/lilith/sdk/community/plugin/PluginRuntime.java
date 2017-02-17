package com.lilith.sdk.community.plugin;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duanefaith on 2017/2/16.
 */

public class PluginRuntime {

    private static final String TAG = "PluginRuntime";

    private static PluginRuntime mInstance = null;

    public static PluginRuntime getInstance() {
        return mInstance;
    }

    static void createInstance() {
        if (mInstance == null) {
            synchronized (PluginRuntime.class) {
                if (mInstance == null) {
                    mInstance = new PluginRuntime();
                }
            }
        }
    }

    private WeakReference<Application> mAppRef;
    private String mPackageName;
    private String mProcessName;
    private String mPluginAssetName;

    private final Map<Integer, RemoteCallHandler> mRemoteCallHandlerMap = new HashMap<Integer, RemoteCallHandler>();

    private PluginRuntime() {

    }

    public void init(Application application) {
        if (application == null) {
            return;
        }
        mAppRef = new WeakReference<Application>(application);
        mPackageName = application.getPackageName();
        registerRemoteCallHandler(Constants.CODE_SET_ASSET_NAME, new RemoteCallHandler() {

            @Override
            public Bundle call(Bundle params, IRemoteCallback callback) {
                if (params != null && params.containsKey(Constants.PARAM_ASSET_NAME)) {
                    mPluginAssetName = params.getString(Constants.PARAM_ASSET_NAME);
                    installPlugin();
                }
                return null;
            }
        });
    }

    private void installPlugin() {
        Log.d(TAG, "asset name = " + mPluginAssetName);
    }

    public Application getApplication() {
        if (mAppRef == null) {
            return null;
        }
        return mAppRef.get();
    }

    public void registerRemoteCallHandler(int code, RemoteCallHandler handler) {
        mRemoteCallHandlerMap.put(code, handler);
    }

    public void unRegisterRemoteCallHandler(int code) {
        mRemoteCallHandlerMap.remove(code);
    }

    public RemoteCallHandler getHandler(int code) {
        return mRemoteCallHandlerMap.get(code);
    }

    public boolean containsCode(int code) {
        return mRemoteCallHandlerMap.containsKey(code);
    }

}
