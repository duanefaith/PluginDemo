package com.liilith.plugin.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by duanefaith on 2017/3/3.
 */

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, "attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }
}
