package com.lilith.sdk.community.plugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.app.assist.AssistContent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashMap;
import java.util.List;

/**
 * Created by duanefaith on 2017/2/16.
 */

public interface IActivity {
    void onCreate(Bundle savedInstanceState);
    void onRestoreInstanceState(Bundle savedInstanceState);
    void onPostCreate(Bundle savedInstanceState);
    void onStart();
    void onRestart();
    void onStateNotSaved();
    void onResume();
    void onPostResume();
    void onNewIntent(Intent intent);
    void onSaveInstanceState(Bundle outState);
    void onPause();
    void onUserLeaveHint();
    boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas);
    CharSequence onCreateDescription();
    void onProvideAssistData(Bundle data);
    void onStop();
    void onDestroy();
    void onMultiWindowModeChanged(boolean isInMultiWindowMode);
    void onPictureInPictureModeChanged(boolean isInPictureInPictureMode);
    void onConfigurationChanged(Configuration newConfig);
    void onLowMemory();
    void onTrimMemory(int level);
    boolean onKeyDown(int keyCode, KeyEvent event);
    boolean onKeyLongPress(int keyCode, KeyEvent event);
    boolean onKeyUp(int keyCode, KeyEvent event);
    boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event);
    void onBackPressed();
    boolean onKeyShortcut(int keyCode, KeyEvent event);
    boolean onTouchEvent(MotionEvent event);
    boolean onTrackballEvent(MotionEvent event);
    boolean onGenericMotionEvent(MotionEvent event);
    void onUserInteraction();
    void onWindowAttributesChanged(WindowManager.LayoutParams params);
    void onContentChanged();
    void onWindowFocusChanged(boolean hasFocus);
    void onAttachedToWindow();
    void onDetachedFromWindow();
    boolean dispatchKeyEvent(KeyEvent event);
    boolean dispatchKeyShortcutEvent(KeyEvent event);
    boolean dispatchTouchEvent(MotionEvent ev);
    boolean dispatchTrackballEvent(MotionEvent ev);
    boolean dispatchGenericMotionEvent(MotionEvent ev);
    boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event);
    View onCreatePanelView(int featureId);
    boolean onCreatePanelMenu(int featureId, Menu menu);
    boolean onPreparePanel(int featureId, View view, Menu menu);
    boolean onMenuOpened(int featureId, Menu menu);
    void onPanelClosed(int featureId, Menu menu);
    boolean onCreateOptionsMenu(Menu menu);
    boolean onPrepareOptionsMenu(Menu menu);
    boolean onOptionsItemSelected(MenuItem item);
    boolean onNavigateUp();
    boolean onNavigateUpFromChild(Activity child);
    void onOptionsMenuClosed(Menu menu);
    void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);
    boolean onContextItemSelected(MenuItem item);
    void onContextMenuClosed(Menu menu);
    void onApplyThemeResource(Resources.Theme theme, int resid, boolean first);
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    View onCreateView(String name, Context context, AttributeSet attrs);
    View onCreateView(View parent, String name, Context context, AttributeSet attrs);
    void onVisibleBehindCanceled();

}
