package com.lilith.sdk.community.plugin;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

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
    private File mPluginFile;
    private File mPluginOptimizeDir;
    private DexClassLoader mDexClassLoader;
    private Resources mPluginResources;
    private LayoutInflater mInflater;

    private long mStartTimeStamp;

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

    public DexClassLoader getPluginClassLoader() {
        return mDexClassLoader;
    }

    public Resources getPluginResources() {
        return mPluginResources;
    }

    public LayoutInflater getPluginLayoutInflater() {
        return mInflater;
    }

    private void installPlugin() {
        Log.d(TAG, "asset name = " + mPluginAssetName);
        mStartTimeStamp = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                Application application = getApplication();
                if (application != null) {
                    mPluginFile = new File(application.getFilesDir(), "/plugins/" + mPluginAssetName);
                    mPluginOptimizeDir = new File(application.getFilesDir(), "/plugins_opt/");
                    if (mPluginFile.exists()) {
                        onPluginInstalled();
                        return;
                    }

                    mPluginFile.getParentFile().mkdirs();

                    boolean fileCreated = false;

                    try {
                        fileCreated = mPluginFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!fileCreated) {
                        onPluginInstallFailed();
                        return;
                    }

                    BufferedInputStream inputStream = null;
                    FileOutputStream outputStream = null;
                    try {
                        inputStream = new BufferedInputStream(application.getAssets().open(mPluginAssetName));
                        outputStream = new FileOutputStream(mPluginFile, false);
                        byte[] buffer = new byte[8096];
                        int length = 0;
                        while ((length = inputStream.read(buffer)) > -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        outputStream.flush();
                        onPluginInstalled();
                    } catch (IOException e) {
                        e.printStackTrace();
                        onPluginInstallFailed();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    private void onPluginInstalled() {
        Log.i(TAG, "onPluginInstalled...");
        createClassLoader();
        createAndInjectResources();
        Log.i(TAG, "Plugin load succeed, consumption = " + (System.currentTimeMillis() - mStartTimeStamp));
    }

    private boolean createClassLoader() {
        if (!mPluginOptimizeDir.exists()) {
            mPluginOptimizeDir.mkdirs();
        }
        mDexClassLoader = new DexClassLoader(mPluginFile.getAbsolutePath()
                , mPluginOptimizeDir.getAbsolutePath(), null, this.getClass().getClassLoader());
        return true;
    }

    private boolean createAndInjectResources() {
        Application application = getApplication();
        if (application == null) {
            return false;
        }
        try {
            Constructor<AssetManager> constructor = AssetManager.class.getDeclaredConstructor();
            if (constructor != null) {
                AssetManager assetManager = constructor.newInstance();
                Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
                if (addAssetPath != null) {
                    addAssetPath.setAccessible(true);
                    addAssetPath.invoke(assetManager, mPluginFile.getAbsolutePath());

                    mPluginResources = new Resources(assetManager, application.getResources().getDisplayMetrics(),
                            application.getResources().getConfiguration());
                    Context baseContext = application.getBaseContext();
                    Class<?> clazz = Class.forName("android.app.ContextImpl");
                    if (clazz != null) {
                        Field mResources = clazz.getDeclaredField("mResources");
                        if (mResources != null) {
                            mResources.setAccessible(true);
                            mResources.set(baseContext, mPluginResources);

                            mInflater = LayoutInflater.from(baseContext);
                            return true;
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void onPluginInstallFailed() {
        Log.i(TAG, "onPluginInstallFailed...");
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
