package com.lilith.sdk.community.plugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/19.
 */

public class BasePluginActivity extends AppCompatActivity implements IPluginActivity {

    private Activity mParent;

    private void setFields(Class clazz, Object injecter) {
        if (clazz != null
                && injecter != null
                && clazz.isAssignableFrom(this.getClass())
                && clazz.isAssignableFrom(injecter.getClass())) {
            Field[] fields= clazz.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (field != null) {
                        field.setAccessible(true);
                        try {
                            field.set(this, field.get(injecter));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void attachActivity(Activity parent) {
        if (parent != null) {
            attachBaseContext(parent.getBaseContext());

            setFields(AppCompatActivity.class, parent);
            setFields(Activity.class, parent);
            setFields(ContextThemeWrapper.class, parent);

            mParent = parent;
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    private boolean isPluginIntent(Intent intent) {
        if (intent != null
                && intent.getComponent() != null
                && intent.getComponent().getClassName() != null) {
            Class clazz = null;
            try {
                clazz = PluginRuntime.getInstance().getPluginClassLoader().loadClass(intent.getComponent().getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null
                    && clazz.getClassLoader() == PluginRuntime.getInstance().getPluginClassLoader()) {
                return true;
            }
        }
        return false;
    }

    private Intent parsePluginIntent(Intent intent) {
        Intent newIntent = new Intent(mParent, PluginRuntime.getInstance().getActivityClass());
        newIntent.putExtra(BasePluginContainerActivity.PARAM_PLUGIN_ACTIVITY, intent.getComponent().getClassName());
        if (intent.getAction() != null) {
            newIntent.setAction(intent.getAction());
        }
        if (intent.getData() != null) {
            newIntent.setData(intent.getData());
        }
        Set<String> categories = intent.getCategories();
        if (categories != null && !categories.isEmpty()) {
            for (String category : categories) {
                newIntent.addCategory(category);
            }
        }
        return newIntent;
    }

    @Override
    public void startActivity(Intent intent) {
        if (isPluginIntent(intent)) {
            mParent.startActivity(parsePluginIntent(intent));
        } else {
            mParent.startActivity(intent);
        }
    }

    @TargetApi(16)
    @Override
    public void startActivity(Intent intent, Bundle options) {
        if (isPluginIntent(intent)) {
            mParent.startActivity(parsePluginIntent(intent), options);
        } else {
            mParent.startActivity(intent, options);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (isPluginIntent(intent)) {
            mParent.startActivityForResult(parsePluginIntent(intent), requestCode);
        } else {
            mParent.startActivityForResult(intent, requestCode);
        }
    }

    @TargetApi(16)
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (isPluginIntent(intent)) {
            mParent.startActivityForResult(parsePluginIntent(intent), requestCode, options);
        } else {
            mParent.startActivityForResult(intent, requestCode, options);
        }
    }

    @Override
    public boolean startActivityIfNeeded(Intent intent, int requestCode) {
        if (isPluginIntent(intent)) {
            return mParent.startActivityIfNeeded(parsePluginIntent(intent), requestCode);
        } else {
            return mParent.startActivityIfNeeded(intent, requestCode);
        }
    }

    @TargetApi(16)
    @Override
    public boolean startActivityIfNeeded(Intent intent, int requestCode, Bundle options) {
        if (isPluginIntent(intent)) {
            return mParent.startActivityIfNeeded(parsePluginIntent(intent), requestCode, options);
        } else {
            return mParent.startActivityIfNeeded(intent, requestCode, options);
        }
    }

    @Override
    public Resources getResources() {
        return PluginRuntime.getInstance().getPluginResources();
    }

    @Override
    public AssetManager getAssets() {
        Resources resources = getResources();
        if (resources != null) {
            return resources.getAssets();
        }
        return null;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return PluginRuntime.getInstance().getPluginLayoutInflater();
    }

    @Override
    public Activity getParentActivity() {
        return mParent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onStateNotSaved() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPostResume() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onUserLeaveHint() {

    }

    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return false;
    }

    @Override
    public CharSequence onCreateDescription() {
        return null;
    }

    @Override
    public void onProvideAssistData(Bundle data) {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onUserInteraction() {

    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {

    }

    @Override
    public void onContentChanged() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    @Override
    public View onCreatePanelView(int featureId) {
        return null;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return false;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return false;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return false;
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onNavigateUp() {
        return false;
    }

    @Override
    public boolean onNavigateUpFromChild(Activity child) {
        return false;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onContextMenuClosed(Menu menu) {

    }

    @Override
    public void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public void onVisibleBehindCanceled() {

    }

}
