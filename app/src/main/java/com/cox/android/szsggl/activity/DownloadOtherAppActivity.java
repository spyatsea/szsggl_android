/*
 * Copyright (c) 2020 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.StatusBarUtil;

/**
 * 下载相关软件页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class DownloadOtherAppActivity extends DbActivity {
    LinearLayout contentLayout;
    /**
     * 主界面
     */
    ScrollView contentView;
    /**
     * 导航栏名称
     */
    TextView titleBarName;
    // 界面相关参数。开始===============================
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 返回
     */
    private Button goBackBtn;

    private Button btn1_1;
    private Button btn1_2;
    private Button btn2_1;
    private Button btn2_2;
    private Button btn3_1;
    private Button btn3_2;
    // 界面相关参数。结束===============================

    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();

        setContentView(R.layout.download_other_app);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("下载相关软件");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        goBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        btn1_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInMarket_tencent("cn.wps.moffice_eng");
            }
        });
        btn1_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInMarket_huawei("cn.wps.moffice_eng", "C121553");
            }
        });
        btn2_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInMarket_tencent("com.microsoft.office.officehub");
            }
        });
        btn2_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInMarket_huawei("com.microsoft.office.officehub", "C10888510");
            }
        });
        btn3_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInMarket_tencent("com.microsoft.office.word");
            }
        });
        btn3_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInMarket_huawei("com.microsoft.office.word", "C10586094");
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 执行主进程
        mainTask = new MainTask().execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color);
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定 Activity 返回的结果。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == CommonParam.RESULTCODE_EXIT) {
            setResult(CommonParam.RESULTCODE_EXIT);
            goBack();
        }
    }

    /**
     * 返回
     */
    @Override
    public void goBack() {
        super.goBack();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mainTask != null) {
            mainTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                goBack();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * 在菜单显示之前对菜单进行操作
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
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
         * 进度常量：生成信息列表
         */
        private static final int PROGRESS_MAKE_LIST = 1002;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 处理数据。开始============================================================================
            // 处理数据。结束============================================================================

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);
            // 生成信息列表
            publishProgress(PROGRESS_MAKE_LIST);

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
                // 设置字段及按钮

            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
            }
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // 隐藏等待窗口
            unWait();

            if (CommonParam.RESULT_ERROR.equals(result)) {
                show("信息错误！");
                goBack();
            }
        }
    }

    /**
     * 在腾讯市场中打开APP页面
     *
     * @param appPackageName {@code String} APP包名
     */
    public void openAppInMarket_tencent(String appPackageName) {
        if (isAppInstalled(CommonParam.MARKET_PACKAGE_TENCENT)) {
            // 已安装
            // 创建启动 Activity 的 Intent
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(CommonParam.MARKET_PACKAGE_TENCENT);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
        } else {
            // 未安装
            // 调用内置动作
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 将url解析为Uri对象，再传递出去
            intent.setData(Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=" + appPackageName));
            // 启动
            startActivity(intent);
        }
    }

    /**
     * 在华为应用市场中打开APP页面
     *
     * @param appPackageName {@code String} APP包名
     * @param appId          {@code String} APP在市场中的编号
     */
    public void openAppInMarket_huawei(String appPackageName, String appId) {
        if (isAppInstalled(CommonParam.MARKET_PACKAGE_HUAWEI)) {
            // 已安装
            // 创建启动 Activity 的 Intent
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(CommonParam.MARKET_PACKAGE_HUAWEI);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
        } else {
            // 未安装
            // 调用内置动作
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 将url解析为Uri对象，再传递出去
            intent.setData(Uri.parse("https://appgallery.huawei.com/#/app/" + appId));
            // 启动
            startActivity(intent);
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        contentView = (ScrollView) findViewById(R.id.contentView);
        contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        // 界面相关参数。开始===============================
        btn1_1 = (Button) findViewById(R.id.btn1_1);
        btn1_2 = (Button) findViewById(R.id.btn1_2);
        btn2_1 = (Button) findViewById(R.id.btn2_1);
        btn2_2 = (Button) findViewById(R.id.btn2_2);
        btn3_1 = (Button) findViewById(R.id.btn3_1);
        btn3_2 = (Button) findViewById(R.id.btn3_2);
        // 界面相关参数。结束===============================
    }

}
