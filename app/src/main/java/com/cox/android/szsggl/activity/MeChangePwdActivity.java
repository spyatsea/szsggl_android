/*
 * Copyright (c) 2016 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.DigestUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我-修改密码页面
 *
 * @author 乔勇(Jacky Qiao)
 */
public class MeChangePwdActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    // DbActivity classThis;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    // 界面相关参数。开始===============================
    /**
     * 提交
     */
    private Button submitBtn;

    private EditText pwdOldTv;
    private EditText pwdNewTv;
    private EditText pwdNewConfirmTv;

    // 界面相关参数。结束===============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // classThis = MeChangePwdActivity.this;

        setContentView(R.layout.me_changepwd);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();

        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        // 界面相关参数。开始===============================
        pwdOldTv = (EditText) findViewById(R.id.pwdOldTv);
        pwdNewTv = (EditText) findViewById(R.id.pwdNewTv);
        pwdNewConfirmTv = (EditText) findViewById(R.id.pwdNewConfirmTv);
        // 界面相关参数。结束===============================

        titleText.setSingleLine(true);
        titleText.setText(R.string.changePwd);

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
        super.onActivityResult(requestCode, resultCode, intent);
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

        String pwdOld = pwdOldTv.getText().toString();
        String pwdNew = pwdNewTv.getText().toString();
        String pwdNewConfirm = pwdNewConfirmTv.getText().toString();

        if (!CommonUtil.checkNB(pwdOld)) {
            errorMsg = "请输入旧密码！";
            // } else if
            // (DigestUtil.md5(pwdOld).toUpperCase().equals(((String)baseApp.loginUser.get("password")).toUpperCase()))
            // {
            // errorMsg = "旧密码错误！";
        } else if (pwdOld.length() < 6) {
            errorMsg = "旧密码至少6位！";
        } else if (!CommonUtil.checkNB(pwdNew)) {
            errorMsg = "请输入新密码！";
        } else if (pwdNew.length() < 6) {
            errorMsg = "新密码至少6位！";
        } else if (!CommonUtil.checkNB(pwdNewConfirm)) {
            errorMsg = "请再次输入新密码！";
        } else if (!pwdNew.equals(pwdNewConfirm)) {
            errorMsg = "两次输入的新密码不一致，请检查！";
        } else {
            submitFlag = true;
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

        private static final int PROGRESS_4 = 1001;

        String userId;
        String pwdOld;
        String pwdNew;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在提交，请稍候…");
            submitBtn.setClickable(false);
            submitBtn.setEnabled(false);

            userId = (String) baseApp.loginUser.get("ids");
            pwdOld = DigestUtil.md5(pwdOldTv.getText().toString());
            pwdNew = pwdNewTv.getText().toString();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 服务器返回的文本
            String respStr = "";
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。开始=================
            try {
                // 查询信息。开始====================================================================
                // 生成参数。开始======================================
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("password_old", pwdOld)
                        .addFormDataPart("password_new", pwdNew);
                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url("http://" + baseApp.serverAddr + "/"
                                + CommonParam.URL_CHANGEUSERPWD)
                        .post(requestBody)
                        .build();
                if (baseHttpClient == null) {
                    baseHttpClient = new OkHttpClient();
                }
                if (upHttpClient == null) {
                    upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                }

                upResponse = upHttpClient.newCall(upHttpRequest).execute();
                Log.d("#succ", "#" + upResponse.isSuccessful());
                if (upResponse.code() == 200) {
                    // 获取成功
                    byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                    // 如果返回的xml中有BOM头，要将其删除
                    if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0]
                            && respBytes[1] == FileUtil.UTF8BOM[1] && respBytes[2] == FileUtil.UTF8BOM[2]) {
                        respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                    } else {
                        respStr = new String(respBytes, "UTF-8");
                    }
                    Log.d("##", "#" + respStr);

                    JSONObject respJson = JSONObject.parseObject(respStr);
                    String resultStr = respJson.getString("result");
                    String statusStr = respJson.getString("status");
                    boolean resultFlag = false;
                    if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                        // 请求正确
                        if ("1".equals(statusStr)) {
                            resultFlag = true;
                        }
                    }

                    if (resultFlag) {
                        baseApp.loginUser.put("password", DigestUtil.md5(pwdNew));

                        if (baseApp.rememberFlag) {
                            JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(baseApp.loginUser));
                            String userStr = JSONObject.toJSONString(data);
                            preferEditor.putString("loginUser", userStr);
                        } else {
                            preferEditor.putString("loginUser", "");
                        }
                        preferEditor.commit();

                        result = CommonParam.RESULT_SUCCESS;
                    } else {
                        if ("4".equals(statusStr)) {
                            result = CommonParam.RESULT_INVALIDKEY;
                        }
                    }

                }
                // 查询信息。结束====================================================================
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

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
                show("密码提交成功！");
                goBack();
            } else {
                submitBtn.setClickable(true);
                submitBtn.setEnabled(true);
                if (CommonParam.RESULT_INVALIDKEY.equals(result)) {
                    makeAlertDialog("旧密码不正确，请重新填写！");
                } else {
                    show("无法提交数据，请重新提交！");
                }
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
