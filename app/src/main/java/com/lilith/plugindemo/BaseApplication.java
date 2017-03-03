package com.lilith.plugindemo;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.res.Configuration;

import com.lilith.sdk.community.plugin.PluginSDK;

/**
 * Created by duanefaith on 2017/2/16.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PluginSDK.getInstance().registerPluginRemoteService(PluginRemoteService.class
                , PluginContainerActivity.class, "plugin_test.apk");
        PluginSDK.getInstance().onCreate(this);
    }

    @Override
    public void onTerminate() {
        PluginSDK.getInstance().onTerminate();
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PluginSDK.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        PluginSDK.getInstance().onLowMemory();
    }

    @TargetApi(14)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        PluginSDK.getInstance().onTrimMemory(level);
    }

}
