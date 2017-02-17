package com.lilith.sdk.community.plugin;

import android.os.Bundle;

/**
 * Created by duanefaith on 2017/2/16.
 */

public interface RemoteCallHandler {
    Bundle call(Bundle params, IRemoteCallback callback);
}
