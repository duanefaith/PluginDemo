// IRemoteCallback.aidl
package com.lilith.sdk.community.plugin;

import android.os.Bundle;

// Declare any non-default types here with import statements

interface IRemoteCallback {
    void onResult(boolean success, int errCode, in Bundle params);
}
