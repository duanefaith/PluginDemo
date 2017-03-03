package com.lilith.sdk.community.plugin;

import android.content.res.Configuration;

/**
 * Created by duanefaith on 2017/3/3.
 */

interface IApplication {
    void onTerminate();
    void onConfigurationChanged(Configuration newConfig);
    void onLowMemory();
    void onTrimMemory(int level);
}
