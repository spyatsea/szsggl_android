/*
 * Copyright (c) 2016 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

/**
 * 我-系统参数设置页面
 *
 * @author 乔勇(Jacky Qiao)
 */
public class MeSysParamActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    // DbActivity classThis;
    /**
     * 导航栏名称
     */
    TextView titleBarName;
    /**
     * 返回按钮
     */
    ImageButton backBtn;

    // 界面相关参数。开始===============================
    /**
     * 提交
     */
    private Button submitBtn;

    private EditText remainMessageNumTv;

    // 界面相关参数。结束===============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // classThis = MeSysParamActivity.this;

        setContentView(R.layout.me_sysparam);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        //Intent intent = getIntent();
        // 获取Intent上携带的数据
        //Bundle data = intent.getExtras();

        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        // 界面相关参数。开始===============================
        remainMessageNumTv = (EditText) findViewById(R.id.remainMessageTv);
        // 界面相关参数。结束===============================

        titleBarName.setSingleLine(true);
        titleBarName.setText(R.string.config_sysparam);

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeCancelDialog();
            }
        });
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 执行主进程
        new MainTask().execute();
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                makeCancelDialog();
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

            // 设置字段及按钮
            // publishProgress(PROGRESS_SET_FIELD);

            // 处理数据。开始============================================================================
            // 处理数据。结束============================================================================
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
                remainMessageNumTv.setText(baseApp.remainMessageNum.toString());
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
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
        }
    }

    /**
     * 提交信息
     */
    public void submit() {
        boolean submitFlag = false;
        String errorMsg = "";

        String num = remainMessageNumTv.getText().toString();
        boolean parseFlag = false;

        if (!CommonUtil.checkNB(num)) {
            errorMsg = "请输入消息数量！";
        } else {
            try {
                Integer.parseInt(num);
                parseFlag = true;
            } catch (NumberFormatException e) {
            }
            if (!parseFlag) {
                errorMsg = "消息数量必须为数字！";
            } else {
                submitFlag = true;
            }
        }

        if (!submitFlag) {
            // 不能提交
            if (CommonUtil.checkNB(errorMsg)) {
                makeAlertDialog(errorMsg);
            }
        } else {
            // 可以提交
            new SubmitTask().execute();
        }
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {

        /**
         * 更新参数
         */
        private static final int PROGRESS_SUBMIT = 1001;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("保存，请稍候…");
            submitBtn.setClickable(false);
            submitBtn.setEnabled(false);
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 处理数据。开始============================================================================
            publishProgress(PROGRESS_SUBMIT);
            result = CommonParam.RESULT_SUCCESS;

            // 处理数据。结束============================================================================

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
            if (progress[0] == PROGRESS_SUBMIT) {
                preferEditor.putInt("REMAIN_MESSAGE_NUM", Integer.parseInt(remainMessageNumTv.getText().toString()));
                preferEditor.commit();

                baseApp.remainMessageNum = preferences.getInt("REMAIN_MESSAGE_NUM", 0);
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
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                show("保存成功！");
                goBack();
            }
        }
    }

    /**
     * 显示取消Dialog
     */
    public void makeCancelDialog() {
        Builder dlgBuilder = new Builder(this);

        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage("信息还没有保存，确定退出吗？");
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                goBack();
            }
        });

        AlertDialog dlg = dlgBuilder.create();
        dlg.show();
    }
}
