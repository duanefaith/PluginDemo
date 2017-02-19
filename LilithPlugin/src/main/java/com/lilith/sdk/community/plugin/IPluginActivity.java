package com.lilith.sdk.community.plugin;

import android.app.Activity;

/**
 * Created by Administrator on 2017/2/19.
 */

public interface IPluginActivity extends IActivity {

    void attachActivity(Activity parent);

    Activity getParentActivity();

}
