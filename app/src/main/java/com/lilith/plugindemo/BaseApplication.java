package com.lilith.plugindemo;

import android.app.Application;

import com.lilith.sdk.community.plugin.PluginSDK;

/**
 * Created by duanefaith on 2017/2/16.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PluginSDK.getInstance().registerPluginRemoteService(PluginRemoteService.class, "plugin_test.apk");
        PluginSDK.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PluginSDK.getInstance().terminate();
    }

}
