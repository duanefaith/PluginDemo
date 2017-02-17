// IRemoteInterface.aidl
package com.lilith.sdk.community.plugin;

import com.lilith.sdk.community.plugin.IRemoteCallback;
import android.os.Bundle;

// Declare any non-default types here with import statements

interface IRemoteInterface {
    Bundle call(int code, in Bundle params, IRemoteCallback callback);
}
