/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.dtr.zxing.camera.CameraManager;
import com.dtr.zxing.decode.DecodeThread;
import com.dtr.zxing.utils.BeepManager;
import com.dtr.zxing.utils.CaptureActivityHandler;
import com.dtr.zxing.utils.InactivityTimer;
import com.google.zxing.Result;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a viewfinder to help the
 * user place the barcode correctly, shows feedback as the image processing is happening, and then overlays the results
 * when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends DbActivity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private Rect mCropRect = null;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private boolean isHasSurface = false;

    /**
     * ????????????
     */
    ImageButton backBtn;
    /**
     * ???????????????
     */
    TextView titleBarName;

    /**
     * ???????????????????????????????????????????????????
     * */
    // private GeoCoder mSearch = null;

    /**
     * ????????????
     */
    private int scan_type;
    // ????????????????????????============================
    private int _i = 0;
    private boolean canScan = true;
    private AlertDialog confirmDlg;
    public boolean isProcessing = false;

    // ????????????????????????============================
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_capture);

        // ??????ActionBar
        actionBar = getSupportActionBar();
        // ??????ActionBar
        actionBar.hide();

        // ??????Intent
        Intent intent = getIntent();
        // ??????Intent??????????????????
        Bundle data = intent.getExtras();
        scan_type = data.getInt("scan_type", CommonParam.SCAN_TYPE_INFO);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(2500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        titleBarName = (TextView) findViewById(R.id.title_text_view);

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ??????
                goBack();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // ???????????????
        new MainTask().execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color_black);
    }

    /**
     * ??????
     */
    @Override
    public void goBack() {
        super.goBack();
    }

    @Override
    public void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (confirmDlg != null && confirmDlg.isShowing()) {
                    confirmDlg.cancel();
                    canScan = true;
                } else {
                    goBack();
                }
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ??????????????????
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * ??????????????????????????????????????????
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        // ???????????????
        // mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude_baidu, longitude_baidu)));

        bundle.putInt("width", mCropRect.width());
        bundle.putInt("height", mCropRect.height());

        if (canScan) {
            canScan = false;

            String sn = rawResult.getText();
            if (!isProcessing) {
                isProcessing = true;
                if (CommonUtil.checkNB(sn) && sn.length() > 1) {
                    // ??????
                    showInfo(sn);
                } else {
                    // ??????
                    restartPreviewAfterDelay(2000);
                }
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(R.string.alert_can_not_open_camera);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * ??????????????????????????????
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** ??????????????????????????????????????? */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** ??????????????????????????? */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** ?????????????????????????????????????????????x?????? */
        int x = cropLeft * cameraWidth / containerWidth;
        /** ?????????????????????????????????????????????y?????? */
        int y = cropTop * cameraHeight / containerHeight;

        /** ???????????????????????????????????? */
        int width = cropWidth * cameraWidth / containerWidth;
        /** ???????????????????????????????????? */
        int height = cropHeight * cameraHeight / containerHeight;

        /** ?????????????????????????????? */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * ????????????
     *
     * @param sn {@code String} ??????
     */
    public void showInfo(String sn) {
        // ??????????????????Bundle
        Bundle data = new Bundle();
        data.putString("sn", sn);
        Log.d("####", sn);
        // ???????????? Activity ??? Intent
        Intent intent = new Intent(CaptureActivity.this, MainActivity.class);

        // ???????????????Intent???
        intent.putExtras(data);
        // ????????? Activity ????????????????????????????????????????????? Activity
        setResult(CommonParam.RESULTCODE_OPENCARD, intent);
        goBack();
    }

    /**
     * ?????????????????????
     *
     * @param msg {@code String} ???????????????
     */
    public void makeAlertDialog(String msg) {
        // ?????????????????????
        int readCardType_old = readCardType;
        // ????????????????????????
        readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(msg);
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        confirmDlg = dlgBuilder.create();
        confirmDlg.show();

        // ???????????????????????????======================================================
        if (customizeAlertDlgFlag) {
            int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId != 0) {
                TextView alertTitle = (TextView) confirmDlg.findViewById(titleId);
                alertTitle.setTextColor(getResources().getColor(R.color.text_color_orange));
            }

            int titleDividerId = dlgBuilder.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            if (titleDividerId != 0) {
                View titleDivider = (View) confirmDlg.findViewById(titleDividerId);
                titleDivider.setBackgroundColor(getResources().getColor(R.color.text_color_orange));
            }
        }
        // ???????????????????????????======================================================

        // ????????????
        Button confirmBtn = confirmDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmBtn.setTag(readCardType_old);

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ????????????
                Button confirmBtn = (Button) v;
                // ?????????????????????
                int readCardType_old = (Integer) confirmBtn.getTag();
                // ??????????????????????????????
                readCardType = readCardType_old;
                canScan = true;
                confirmDlg.cancel();
            }
        });

    }

    /**
     * ????????? AsyncTask ???
     */
    private class MainTask extends AsyncTask<Object, Integer, String> {
        /**
         * ????????????????????????????????????
         */
        private static final int PROGRESS_SET_FIELD = 1001;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // ??????????????????
            // makeWaitDialog();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // ?????????????????????============================================================================
            infoTool = getInfoTool();
            // ?????????????????????============================================================================

            // ?????????????????????
            publishProgress(PROGRESS_SET_FIELD);

            result = CommonParam.RESULT_SUCCESS;
            return result;
        }

        /**
         * invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is
         * undefined. This method is used to display any form of progress in the user interface while the background
         * computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text
         * field.
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == PROGRESS_SET_FIELD) {
                // ?????????????????????
                titleBarName.setSingleLine(true);
                switch (scan_type) {
                    case CommonParam.SCAN_TYPE_INFO:
                        titleBarName.setText("????????????");
                        break;
                    default:
                        break;
                }
            }

        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // ??????????????????
            // unWait();
        }
    }

    /**
     * ?????????????????? AsyncTask ???
     */
    private class ProcessSnTask extends AsyncTask<Object, Integer, String> {
        /**
         * ???????????????sn??????
         */
        private static final int PROCESS_SN_INVALID = 1001;
        /**
         * ?????????????????????
         */
        private static final String RESULT_SHOW_INFO = "show_info";
        /**
         * ??????
         */
        private String sn;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_SUCCESS;

            // ?????????????????????============================================================================
            sn = (String) params[0];
            if (CommonUtil.checkNB(sn) && sn.length() > 1) {
                // ??????
                result = RESULT_SHOW_INFO;
            } else {
                // ??????
                publishProgress(PROCESS_SN_INVALID);
            }
            // ?????????????????????============================================================================

            // result = CommonParam.RESULT_SUCCESS;
            return result;
        }

        /**
         * invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is
         * undefined. This method is used to display any form of progress in the user interface while the background
         * computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text
         * field.
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == PROCESS_SN_INVALID) {
                // sn??????
                makeAlertDialog("??????????????????");
            }
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // ??????????????????
            Log.d("###3", "#");
            if (RESULT_SHOW_INFO.equals(result)) {
                // ????????????
                showInfo(sn);
            } else {
                restartPreviewAfterDelay(2000);
            }
        }
    }
}