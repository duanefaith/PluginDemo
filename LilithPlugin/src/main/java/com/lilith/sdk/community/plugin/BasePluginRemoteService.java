package com.lilith.sdk.community.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by duanefaith on 2017/2/16.
 */

public abstract class BasePluginRemoteService extends Service {

    private final IRemoteInterface.Stub mRemoteInterface = new IRemoteInterface.Stub() {

        @Override
        public Bundle call(int code, Bundle params, IRemoteCallback callback) throws RemoteException {
            if (PluginRuntime.getInstance().containsCode(code)) {
                RemoteCallHandler handler = PluginRuntime.getInstance().getHandler(code);
                if (handler != null) {
                    return handler.call(params, callback);
                }
            }
            return null;
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return mRemoteInterface;
    }

}
