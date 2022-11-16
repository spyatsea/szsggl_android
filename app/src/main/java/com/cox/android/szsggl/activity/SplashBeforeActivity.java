/*
 * Copyright (c) 2014 山西考科思 版权所有
 */
package com.cox.android.szsggl.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.application.BaseApplication;
import com.cox.utils.CommonParam;

import java.util.HashMap;

//import android.util.Log;

/**
 * 闪屏之前的页面
 * <p>在加载主程序之前，在本页面做一些初始化工作，如授权等。</p>
 *
 * @author 乔勇(Jacky Qiao)
 */
public class SplashBeforeActivity extends AppCompatActivity {
    /**
     * 当前类对象
     * */
    AppCompatActivity classThis;
    /**
     * 存放变量的Application
     */
    BaseApplication baseApp = null;

    public BaseApplication getBaseApp() {
        if (baseApp == null) {
            baseApp = (BaseApplication) getApplication();
        }
        return baseApp;
    }

    /**
     * 信息代码：显示闪屏窗口
     */
    private static final int MESSAGE_SHOW_SPLASH = 0x020;

    /**
     * 授予权限按钮
     */
    private Button permissionBtn;

    /**
     * 获取权限对话框
     * */
    private AlertDialog permissionDlg;

    /**
     * 显示或隐藏组件的 Handler
     */
    private final Handler showHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_SPLASH:
                    // 到主页面
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new MainTask().execute();
                        }
                    }, 100);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * APP获得了所有需要的权限
     */
//    boolean havePermission;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = SplashBeforeActivity.this;

        // 获得ActionBar
        // actionBar = getSupportActionBar();
        // 隐藏ActionBar
        // actionBar.hide();
        // 不显示标题
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        findViews();

        baseApp = getBaseApp();

        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取权限的结果信息
                HashMap<String, Object> permissionResultMap = baseApp.checkPermissions(classThis);
                // 是否已经获得了所需的权限
                boolean havePermission = (Boolean) permissionResultMap.get("havePermission");
                if (havePermission) {
                    showSplash();
                } else {
                    permissionBtn.setVisibility(View.VISIBLE);
                    // 是否需要在应用信息中设置权限
                    boolean needShowDetailsSettings = (Boolean) permissionResultMap.get("needShowDetailsSettings");
                    // 需要获得的权限数组
                    String[] permissions = (String[]) permissionResultMap.get("permissions");
                    // 获得APP需要的权限
                    getPermissions(permissions, needShowDetailsSettings);
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 处理APP需要的权限
        processPermissions();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定 Activity 返回的结果。
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Log.d(this.getClass().getName() + ":" + "log", "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CommonParam.REQUESTCODE_PERMISSION || requestCode == CommonParam.REQUESTCODE_PERMISSION_ALL_FILES) {
            // 这里要判断从应用信息中设置权限的结果
            //Log.d(this.getClass().getName() + ":" + "result", "#" + resultCode);
            HashMap<String, Object> permissionResultMap = baseApp.checkPermissions(classThis);
            // 是否已经获得了所需的权限
            boolean havePermission = (Boolean) permissionResultMap.get("havePermission");
            if (havePermission) {
                showSplash();
            } else {
                // 如果发现需要获取权限，那么就仅提示用户需要权限。由用户点击“手工设置权限”按钮来进行授权
                permissionBtn.setVisibility(View.VISIBLE);
                Toast toast = Toast.makeText(classThis, R.string.alert_need_permissions, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    /**
     * 主进程 AsyncTask 类
     */
    private class MainTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：设置字段及按钮
         */
        private static final int PROGRESS_SET_FIELD = 1001;

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
            String result = CommonParam.RESULT_ERROR;

            result = CommonParam.RESULT_SUCCESS;

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);

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
                // 设置字段及按钮
            }

        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
            }
        }
    }

    /**
     * 显示闪屏页面
     */
    private void showSplash() {
        permissionBtn.setVisibility(View.GONE);
        startActivity(new Intent(classThis, SplashActivity.class));
        finish();
    }

    /**
     * 查找view
     */
    public void findViews() {
        permissionBtn = (Button) findViewById(R.id.permissionBtn);
    }

    // 权限相关的属性与方法。开始=============================================

    /**
     * 获得APP需要的权限
     *
     * @param permissions             {@code String[]} 需要获得的权限数组
     * @param needShowDetailsSettings {@code boolean} 是否需要在应用信息中设置权限
     */
    @TargetApi(23)
    public void getPermissions(String[] permissions, boolean needShowDetailsSettings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissions != null && permissions.length > 0) {
                // 需要获得权限
                if (needShowDetailsSettings) {
                    // 需要打开应用信息界面
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_PERMISSION);
                } else {
                    // 打开授权窗口
                    requestPermissions(permissions, CommonParam.REQUESTCODE_PERMISSION);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // 访问所有存储空间权限(
                    if (!Environment.isExternalStorageManager()) {
                        makeGetPermissionDialog();
                    }
                }
            }
        }
    }

    /**
     * 获取权限的回调方法
     */
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Log.d(this.getClass().getName() + ":" + "log", "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == CommonParam.REQUESTCODE_PERMISSION) {
                // 是否拒绝了权限
                boolean deniedFlag = false;
                for (int i = 0, len = permissions.length; i < len; i++) {
                    // String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        //Log.d("###", permission + ": 申请权限成功");
                    } else if (grantResult == PackageManager.PERMISSION_DENIED) {
                        //Log.d("###", permission + ": 申请权限失败");
                        deniedFlag = true;
                    }
                }
                if (deniedFlag) {
                    // 如果拒绝过权限，那么就仅提示用户需要权限。由用户点击“手工设置权限”按钮来进行授权
                    permissionBtn.setVisibility(View.VISIBLE);
                    Toast toast = Toast.makeText(classThis, R.string.alert_need_permissions, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // 访问所有存储空间权限(
                        if (!Environment.isExternalStorageManager()) {
                            makeGetPermissionDialog();
                        }
                    } else {
                        Log.d("####", "开始");
                        showSplash();
                    }
                }
            }
        }
    }

    /**
     * 显示获取权限对话框
     */
    public void makeGetPermissionDialog() {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage("APP运行时需要读写文件，请授予APP文件访问权限。");
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(false);

        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        permissionDlg = dlgBuilder.create();
        permissionDlg.show();

        // 确定按钮
        Button confirmBtn = permissionDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = permissionDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, CommonParam.REQUESTCODE_PERMISSION_ALL_FILES);
                permissionDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionDlg.cancel();
            }
        });
    }

    /**
     * 处理APP需要的权限
     */
    public void processPermissions() {
        HashMap<String, Object> permissionResultMap = baseApp.checkPermissions(classThis);
        // 是否已经获得了所需的权限
        boolean havePermission = (Boolean) permissionResultMap.get("havePermission");
        if (havePermission) {
            Log.d("####", "开始");
            showSplash();
        } else {
            permissionBtn.setVisibility(View.VISIBLE);
            boolean isFirstOpenApp = baseApp.preferences.getBoolean("isFirstOpenApp", true);
            //Log.d("#isFirstOpenApp", "#" + isFirstOpenApp);
            // 是否需要在应用信息中设置权限
            boolean needShowDetailsSettings = (Boolean) permissionResultMap.get("needShowDetailsSettings");
            if (needShowDetailsSettings && !isFirstOpenApp) {
                // 如果一启动APP就发现需要打开应用信息界面来获取权限，那么就仅提示用户需要权限。由用户点击“手工设置权限”按钮来进行授权
                Toast toast = Toast.makeText(classThis, R.string.alert_need_permissions, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                // 需要获得的权限数组
                String[] permissions = (String[]) permissionResultMap.get("permissions");
                if (needShowDetailsSettings && isFirstOpenApp) {
                    needShowDetailsSettings = false;
                }
                // 获得APP需要的权限
                getPermissions(permissions, needShowDetailsSettings);
            }
        }
    }
    // 权限相关的属性与方法。结束=============================================
}