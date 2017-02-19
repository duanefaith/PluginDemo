package com.lilith.sdk.community.plugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by duanefaith on 2017/2/16.
 */

public abstract class BasePluginContainerActivity extends Activity implements IActivity {

    private static final String TAG = "BasePluginContainerActivity";

    public static final String PARAM_PLUGIN_ACTIVITY = "plugin_activity";

    private IPluginActivity mPluginActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(PARAM_PLUGIN_ACTIVITY)) {
            DexClassLoader classLoader = PluginRuntime.getInstance().getPluginClassLoader();
            if (classLoader != null) {
                try {
                    Class<?> clazz = classLoader.loadClass(intent.getStringExtra(PARAM_PLUGIN_ACTIVITY));
                    if (clazz != null) {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        Object obj = constructor.newInstance();
                        mPluginActivity = (IPluginActivity) obj;
                        mPluginActivity.attachActivity(this);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mPluginActivity == null) {
            finish();
            return;
        }
        mPluginActivity.onCreate(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mPluginActivity != null) {
            mPluginActivity.onCreate(savedInstanceState);
        }
    }

    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mPluginActivity != null) {
            mPluginActivity.onPostCreate(savedInstanceState);
        }
    }

    public void onStart() {
        super.onStart();
        if (mPluginActivity != null) {
            mPluginActivity.onStart();
        }
    }

    public void onRestart() {
        super.onRestart();
        if (mPluginActivity != null) {
            mPluginActivity.onRestart();
        }
    }

    public void onStateNotSaved() {
        super.onStateNotSaved();
        if (mPluginActivity != null) {
            mPluginActivity.onStateNotSaved();
        }
    }

    public void onResume() {
        super.onResume();
        if (mPluginActivity != null) {
            mPluginActivity.onResume();
        }
    }

    public void onPostResume() {
        super.onPostResume();
        if (mPluginActivity != null) {
            mPluginActivity.onPostResume();
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mPluginActivity != null) {
            mPluginActivity.onNewIntent(intent);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPluginActivity != null) {
            mPluginActivity.onSaveInstanceState(outState);
        }
    }

    public void onPause() {
        super.onPause();
        if (mPluginActivity != null) {
            mPluginActivity.onPause();
        }
    }

    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (mPluginActivity != null) {
            mPluginActivity.onUserLeaveHint();
        }
    }

    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        boolean ret = super.onCreateThumbnail(outBitmap, canvas);
        if (mPluginActivity != null) {
            return mPluginActivity.onCreateThumbnail(outBitmap, canvas);
        }
        return ret;
    }

    public CharSequence onCreateDescription() {
        CharSequence ret = super.onCreateDescription();
        if (mPluginActivity != null) {
            return mPluginActivity.onCreateDescription();
        }
        return ret;
    }

    public void onProvideAssistData(Bundle data) {
        super.onProvideAssistData(data);
        if (mPluginActivity != null) {
            mPluginActivity.onProvideAssistData(data);
        }
    }

    public void onStop() {
        super.onStop();
        if (mPluginActivity != null) {
            mPluginActivity.onStop();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mPluginActivity != null) {
            mPluginActivity.onDestroy();
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        if (mPluginActivity != null) {
            mPluginActivity.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (mPluginActivity != null) {
            mPluginActivity.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPluginActivity != null) {
            mPluginActivity.onConfigurationChanged(newConfig);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        Object ret = super.onRetainNonConfigurationInstance();
        if (mPluginActivity != null) {
            return mPluginActivity.onRetainNonConfigurationInstance();
        }
        return ret;
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (mPluginActivity != null) {
            mPluginActivity.onLowMemory();
        }
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (mPluginActivity != null) {
            mPluginActivity.onTrimMemory(level);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyDown(keyCode, event);
        if (mPluginActivity != null) {
            mPluginActivity.onKeyDown(keyCode, event);
        }
        return ret;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyLongPress(keyCode, event);
        if (mPluginActivity != null) {
            mPluginActivity.onKeyLongPress(keyCode, event);
        }
        return ret;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyUp(keyCode, event);
        if (mPluginActivity != null) {
            mPluginActivity.onKeyUp(keyCode, event);
        }
        return ret;
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        boolean ret = super.onKeyMultiple(keyCode, repeatCount, event);
        if (mPluginActivity != null) {
            mPluginActivity.onKeyMultiple(keyCode, repeatCount, event);
        }
        return ret;
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (mPluginActivity != null) {
            mPluginActivity.onBackPressed();
        }
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyShortcut(keyCode, event);
        if (mPluginActivity != null) {
            mPluginActivity.onKeyShortcut(keyCode, event);
        }
        return ret;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        if (mPluginActivity != null) {
            mPluginActivity.onTouchEvent(event);
        }
        return ret;
    }

    public boolean onTrackballEvent(MotionEvent event) {
        boolean ret = super.onTrackballEvent(event);
        if (mPluginActivity != null) {
            mPluginActivity.onTrackballEvent(event);
        }
        return ret;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean ret = super.onGenericMotionEvent(event);
        if (mPluginActivity != null) {
            mPluginActivity.onGenericMotionEvent(event);
        }
        return ret;
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        if (mPluginActivity != null) {
            mPluginActivity.onUserInteraction();
        }
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
        if (mPluginActivity != null) {
            mPluginActivity.onWindowAttributesChanged(params);
        }
    }

    public void onContentChanged() {
        super.onContentChanged();
        if (mPluginActivity != null) {
            mPluginActivity.onContentChanged();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mPluginActivity != null) {
            mPluginActivity.onWindowFocusChanged(hasFocus);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPluginActivity != null) {
            mPluginActivity.onAttachedToWindow();
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPluginActivity != null) {
            mPluginActivity.onDetachedFromWindow();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean ret = super.dispatchKeyEvent(event);
        if (mPluginActivity != null) {
            mPluginActivity.dispatchKeyEvent(event);
        }
        return ret;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        boolean ret = super.dispatchKeyShortcutEvent(event);
        if (mPluginActivity != null) {
            mPluginActivity.dispatchKeyShortcutEvent(event);
        }
        return ret;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        if (mPluginActivity != null) {
            mPluginActivity.dispatchTouchEvent(ev);
        }
        return ret;
    }

    public boolean dispatchTrackballEvent(MotionEvent ev) {
        boolean ret = super.dispatchTrackballEvent(ev);
        if (mPluginActivity != null) {
            mPluginActivity.dispatchTrackballEvent(ev);
        }
        return ret;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        boolean ret = super.dispatchGenericMotionEvent(ev);
        if (mPluginActivity != null) {
            mPluginActivity.dispatchGenericMotionEvent(ev);
        }
        return ret;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean ret =  super.dispatchPopulateAccessibilityEvent(event);
        if (mPluginActivity != null) {
            mPluginActivity.dispatchPopulateAccessibilityEvent(event);
        }
        return ret;
    }

    public View onCreatePanelView(int featureId) {
        View ret = super.onCreatePanelView(featureId);
        if (mPluginActivity != null) {
            return mPluginActivity.onCreatePanelView(featureId);
        }
        return ret;
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        boolean ret =  super.onCreatePanelMenu(featureId, menu);
        if (mPluginActivity != null) {
            return mPluginActivity.onCreatePanelMenu(featureId, menu);
        }
        return ret;
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        boolean ret = super.onPreparePanel(featureId, view, menu);
        if (mPluginActivity != null) {
            return mPluginActivity.onPreparePanel(featureId, view, menu);
        }
        return ret;
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        boolean ret = super.onMenuOpened(featureId, menu);
        if (mPluginActivity != null) {
            return mPluginActivity.onMenuOpened(featureId, menu);
        }
        return ret;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        boolean ret = super.onMenuItemSelected(featureId, item);
        if (mPluginActivity != null) {
            return mPluginActivity.onMenuItemSelected(featureId, item);
        }
        return ret;
    }

    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
        if (mPluginActivity != null) {
            mPluginActivity.onPanelClosed(featureId, menu);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean ret = super.onCreateOptionsMenu(menu);
        if (mPluginActivity != null) {
            return mPluginActivity.onCreateOptionsMenu(menu);
        }
        return ret;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean ret = super.onPrepareOptionsMenu(menu);
        if (mPluginActivity != null) {
            return mPluginActivity.onPrepareOptionsMenu(menu);
        }
        return ret;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = super.onOptionsItemSelected(item);
        if (mPluginActivity != null) {
            return mPluginActivity.onOptionsItemSelected(item);
        }
        return ret;
    }

    public boolean onNavigateUp() {
        boolean ret = super.onNavigateUp();
        if (mPluginActivity != null) {
            return mPluginActivity.onNavigateUp();
        }
        return ret;
    }

    public boolean onNavigateUpFromChild(Activity child) {
        boolean ret = super.onNavigateUpFromChild(child);
        if (mPluginActivity != null) {
            return mPluginActivity.onNavigateUpFromChild(child);
        }
        return ret;
    }

    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        if (mPluginActivity != null) {
            mPluginActivity.onOptionsMenuClosed(menu);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (mPluginActivity != null) {
            mPluginActivity.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        boolean ret = super.onContextItemSelected(item);
        if (mPluginActivity != null) {
            return mPluginActivity.onContextItemSelected(item);
        }
        return ret;
    }

    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        if (mPluginActivity != null) {
            mPluginActivity.onContextMenuClosed(menu);
        }
    }

    public void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
        if (mPluginActivity != null) {
            mPluginActivity.onApplyThemeResource(theme, resid, first);
        }
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPluginActivity != null) {
            mPluginActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(name, context, attrs);
        if (mPluginActivity != null) {
            return mPluginActivity.onCreateView(name, context, attrs);
        }
        return ret;
    }

    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(parent, name, context, attrs);
        if (mPluginActivity != null) {
            return mPluginActivity.onCreateView(parent, name, context, attrs);
        }
        return ret;
    }

    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
        if (mPluginActivity != null) {
            mPluginActivity.onVisibleBehindCanceled();
        }
    }

}
