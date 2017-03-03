package com.lilith.sdk.community.plugin;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

/**
 * Created by duanefaith on 2017/2/16.
 */

public class PluginRuntime implements IApplication {

    private static final String TAG = "PluginRuntime";

    private static final List<String> sNativeLibPaths = new ArrayList<String>();

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            if (supportedAbis != null
                    && supportedAbis.length > 0) {
                for (String supportedAbi : supportedAbis) {
                    if (supportedAbi != null) {
                        sNativeLibPaths.add(("lib/" + supportedAbi + "/").toLowerCase());
                    }
                }
            }
        } else {
            if (Build.CPU_ABI != null) {
                sNativeLibPaths.add(("lib/" + Build.CPU_ABI + "/").toLowerCase());
            }
            if (Build.CPU_ABI2 != null) {
                sNativeLibPaths.add(("lib/" + Build.CPU_ABI2 + "/").toLowerCase());
            }
        }
    }

    private static PluginRuntime sInstance = null;

    public static PluginRuntime getInstance() {
        return sInstance;
    }

    static void createInstance() {
        if (sInstance == null) {
            synchronized (PluginRuntime.class) {
                if (sInstance == null) {
                    sInstance = new PluginRuntime();
                }
            }
        }
    }

    private WeakReference<Application> mAppRef;
    private String mPackageName;
    private String mProcessName;
    private String mPluginAssetName;
    private File mPluginFile;
    private File mPluginExtractedDir;
    private File mPluginNativeDir;
    private File mPluginOptimizeDir;
    private DexClassLoader mDexClassLoader;
    private Resources mPluginResources;
    private LayoutInflater mInflater;
    private PackageInfo mArchiveInfo;
    private Application mPluginApplication;

    private Class<? extends BasePluginContainerActivity> mActivityClass;
    private Class<? extends BasePluginRemoteService> mServiceClass;

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
        registerRemoteCallHandler(Constants.CODE_SET_PLUIN_INIT, new RemoteCallHandler() {

            @Override
            public Bundle call(Bundle params, IRemoteCallback callback) {
                if (params != null && params.containsKey(Constants.PARAM_ASSET_NAME)) {
                    mPluginAssetName = params.getString(Constants.PARAM_ASSET_NAME);
                    mActivityClass = (Class<? extends BasePluginContainerActivity>) params.getSerializable(Constants.PARAM_ACTIVITY_CLASS);
                    mServiceClass = (Class<? extends BasePluginRemoteService>) params.getSerializable(Constants.PARAM_SERVICE_CLASS);
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
        extractSoFiles();
        createClassLoader();
        createAndInjectResources();
        retrievePackageInfo();
        Log.i(TAG, "Plugin load succeed, consumption = " + (System.currentTimeMillis() - mStartTimeStamp));
    }

    private void retrievePackageInfo() {
        mArchiveInfo = PluginRuntime.getInstance().getApplication()
                .getPackageManager().getPackageArchiveInfo(mPluginFile.getAbsolutePath()
                        , PackageManager.GET_ACTIVITIES
                                | PackageManager.GET_SERVICES
                                | PackageManager.GET_RECEIVERS
                                | PackageManager.GET_PROVIDERS
                                | PackageManager.GET_INTENT_FILTERS
                                | PackageManager.GET_INSTRUMENTATION
                                | PackageManager.GET_CONFIGURATIONS
                                | PackageManager.GET_META_DATA
                                | PackageManager.GET_SHARED_LIBRARY_FILES
                                | PackageManager.GET_PERMISSIONS);
        if (mArchiveInfo != null) {
            if (mArchiveInfo.applicationInfo.name != null) {
                try {
                    Class clazz = mDexClassLoader.loadClass(mArchiveInfo.applicationInfo.name);
                    Constructor constructor = clazz.getConstructor();
                    constructor.setAccessible(true);
                    mPluginApplication = (Application) constructor.newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        if (mPluginApplication != null) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Method attach = Application.class.getDeclaredMethod("attach", Context.class);
                        attach.setAccessible(true);
                        attach.invoke(mPluginApplication, getApplication());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPluginApplication.onCreate();
                }
            });
        }
    }

    private boolean isAvailableNativeEntry(ZipEntry entry) {
        if (entry == null || entry.isDirectory()) {
            return false;
        }
        String name = entry.getName();
        if (name == null) {
            return false;
        }
        for (String availPath : sNativeLibPaths) {
            if (name.startsWith(availPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean writeFileFromZip(ZipInputStream zis, File output) {
        if (output == null) {
            return false;
        }
        if (!output.exists()) {
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            boolean created = false;
            try {
                created = output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (created) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(output);
                    byte[] buffer = new byte[8096];
                    int readLen = 0;
                    while ((readLen = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, readLen);
                    }
                    fos.flush();
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean extractSoFiles() {
        mPluginExtractedDir = new File(mPluginFile.getParent()
                , "/extracted_" + mPluginFile.getName() + "/");

        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(mPluginFile));
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (isAvailableNativeEntry(entry)) {
                    File extractedNativeFile = new File(mPluginExtractedDir, entry.getName());
                    writeFileFromZip(zis, extractedNativeFile);
                }
                zis.closeEntry();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (String availPath : sNativeLibPaths) {
            File nativeLibPath = new File(mPluginExtractedDir, availPath);
            if (nativeLibPath.exists() && nativeLibPath.isDirectory()) {

                File[] files = nativeLibPath.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name != null && name.endsWith(".so")){
                            return true;
                        }
                        return false;
                    }
                });

                if (files != null && files.length > 0) {
                    mPluginNativeDir = nativeLibPath;
                    break;
                }
            }
        }

        return true;
    }

    private boolean createClassLoader() {
        if (!mPluginOptimizeDir.exists()) {
            mPluginOptimizeDir.mkdirs();
        }

        String pluginPath = mPluginFile.getAbsolutePath();
        String pluginOptPath = mPluginOptimizeDir.getAbsolutePath();
        String pluginNativePath = mPluginNativeDir == null ? null : mPluginNativeDir.getAbsolutePath();

        Log.i(TAG, "pluginPath = " + pluginPath + ", pluginOptPath = " + pluginOptPath + ", pluginNativePath = " + pluginNativePath);

        mDexClassLoader = new DexClassLoader(pluginPath
                , pluginOptPath
                , pluginNativePath
                , this.getClass().getClassLoader());
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

    public Class<? extends BasePluginContainerActivity> getActivityClass() {
        return mActivityClass;
    }

    public Class<? extends BasePluginRemoteService> getServiceClass() {
        return mServiceClass;
    }

    @Override
    public void onTerminate() {
        if (mPluginApplication != null) {
            mPluginApplication.onTerminate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mPluginApplication != null) {
            mPluginApplication.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (mPluginApplication != null) {
            mPluginApplication.onLowMemory();
        }
    }

    @TargetApi(14)
    @Override
    public void onTrimMemory(int level) {
        if (mPluginApplication != null) {
            mPluginApplication.onTrimMemory(level);
        }
    }
}
