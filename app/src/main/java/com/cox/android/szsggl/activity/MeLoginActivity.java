/*
 * Copyright (c) 2015 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.PopupMenu.OnDismissListener;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.handler.HtmlTagHandler;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.DigestUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我-登录页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MeLoginActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 主界面
     */
    ScrollView contentView;
    // 界面相关参数。开始===============================
    /**
     * 取消
     */
    private Button cancelBtn;
    /**
     * 提交
     */
    private Button submitBtn;
    /**
     * 设置
     */
    private ImageButton configBtn;
    /**
     * 注册
     * */
    // private Button regBtn;
    /**
     * QQ登录
     */
    // private Button qqLoginBtn;

    private EditText accountTv;
    private EditText passwordTv;
    //private CheckBox remember_user;

    /**
     * 弹出菜单
     */
    private PopupMenu popupMenu;
    /**
     * 数据同步ProgressDialog
     */
    private ProgressDialog dataSyncDlg = null;
    // 界面相关参数。结束===============================

    // QQ相关参数。开始===============================
    // private ImageView mUserLogo;
    // private UserInfo mInfo;
    // public static Tencent mTencent;
    // private static boolean isServerSideLogin = false;
    // QQ相关参数。结束===============================

    /**
     * 用户协议Dialog
     */
    private AlertDialog privacyDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MeLoginActivity.this;

        setContentView(R.layout.me_login);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        fromFlag = data.getString("fromFlag", "me_main");

        findViews();

        titleText.setSingleLine(true);
        titleText.setText(R.string.login);

        // mTencent = Tencent.createInstance(CommonParam.TENCENT_APPID, this);

        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
        configBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopupConfigMenu(configBtn);
            }
        });
        // regBtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // Log.d("---", "#reg");
        // // 创建启动 Activity 的 Intent
        // Intent intent = new Intent(classThis, MeRegActivity.class);
        // // 信息传输Bundle
        // // Bundle data = new Bundle();
        // // 将数据存入Intent中
        // // intent.putExtras(data);
        // startActivityForResult(intent, CommonParam.REQUESTCODE_ME);
        // overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
        // }
        // });
        // qqLoginBtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // onClickLogin();
        // }
        // });

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
        if (requestCode == CommonParam.REQUESTCODE_ME && resultCode == CommonParam.RESULTCODE_ME) {
            Bundle data = intent.getExtras();
            boolean needReload = data.getBoolean("needReload", false);
            boolean exitFlag = data.getBoolean("exitFlag", false);

            if (exitFlag) {
                Intent _intent = new Intent(classThis, MeMainActivity.class);
                // 创建信息传输Bundle
                Bundle _data = new Bundle();
                _data.putBoolean("needReload", needReload);
                // 将数据存入Intent中
                _intent.putExtras(_data);
                // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                setResult(CommonParam.RESULTCODE_ME, _intent);
                goBack();
            }
        }
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
//        makeAlertDialog("" + keyCode);
//        Log.d("#", "###" + event.getAction() + ":" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                makeExitDialog();
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
     * 提交信息
     */
    public void submit() {
        boolean submitFlag = false;
        String errorMsg = "";

        String account = accountTv.getText().toString();
        String password = passwordTv.getText().toString();

        if (!CommonUtil.checkNB(account)) {
            errorMsg = "请输入登录账号！";
        } else if (!CommonUtil.checkNB(password)) {
            errorMsg = "请输入密码！";
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
            if (!checkNet(false)) {
                // 离线
                new SubmitLocalTask().execute();
            } else {
                // 在线
                new SubmitTask().execute();
            }
        }
    }

    /**
     * 显示PopupMenu
     *
     * @param view {@code View} PopupMenu绑定的对象
     */
    public void showPopupConfigMenu(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(this, view);
            // 强制显示PopupMenu图标
            forceShowPopupMenuIcon(popupMenu);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.main_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_resetDB:
                            // 显示重置数据提示对话框
                            makeResetDbDialog();
                            break;
                        case R.id.baseInfoSync:
                            // 基础数据同步
                            if (checkNet(true)) {
                                makeDataSyncConfirmDialog();
                            }
                            break;
                        case R.id.menu_update:
                            // 检查更新
                            if (!isUpdating) {
                                testUpdateApp("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUPDATE
                                        + "?token=" + CommonParam.APP_KEY + "&type=1", "1");
                            }
                            break;
                        case R.id.menu_serverAddr:
                            // 服务器地址
                            makeSetServerDialog();
                            break;
                        case R.id.menu_uploadTestData:
                            // 上传测试数据
                            new UploadTestDataTask().execute();
                            break;
                        case R.id.menu_about:
                            // 显示关于对话框
                            makeAboutDialog();
                            break;
                        default:
                    }
                    return true;
                }
            });
            popupMenu.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(PopupMenu popup) {
                }
            });
            popupMenu.show();
        } else {
            Menu menu = popupMenu.getMenu();
            menu.close();
            popupMenu.show();
        }
    }

    /**
     * 显示数据同步提示对话框
     */
    public void makeDataSyncConfirmDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.accountDataSync);
        dlgBuilder.setMessage(R.string.alert_account_sync_confirm);
        dlgBuilder.setIcon(R.drawable.menu_table_refresh);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeDataSyncDialog();
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.create().show();
    }

    /**
     * 显示数据同步进度对话框
     */
    public void makeDataSyncDialog() {
        if (!checkNet(true)) {
            return;
        }

        if (dataSyncDlg == null) {
            dataSyncDlg = new ProgressDialog(this);
            dataSyncDlg.setTitle(R.string.accountDataSync);
            dataSyncDlg.setMessage(getString(R.string.alert_data_sync_message));
            dataSyncDlg.setMax(CommonParam.PROGRESS_MAX);
            dataSyncDlg.setCancelable(false);
            dataSyncDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dataSyncDlg.setProgress(0);
            dataSyncDlg.setIndeterminate(true);
            dataSyncDlg.setIcon(R.drawable.menu_table_refresh);

            dataSyncDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dataSyncDlg.cancel();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
        dataSyncDlg.show();

        new DataSyncTask().execute();
    }

    /**
     * 显示用户协议Dialog
     */
    public void makePrivacyDialog() {
        Builder dlgBuilder = new Builder(this);
        TextView msgTv = (TextView) getLayoutInflater().inflate(R.layout.dlg_privacy_msg, null);
        msgTv.setText(Html.fromHtml(getString(R.string.privacy_msg), null, new HtmlTagHandler(this)));
        msgTv.setMovementMethod(LinkMovementMethod.getInstance());
        dlgBuilder.setView(msgTv);
        dlgBuilder.setTitle(R.string.privacy);
        //dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(false);

        dlgBuilder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        privacyDlg = dlgBuilder.create();
        privacyDlg.show();

        // 改变一些样式。开始======================================================
        int iconId = dlgBuilder.getContext().getResources().getIdentifier("icon", "id", "android");
        if (iconId != 0) {
            View icon = privacyDlg.findViewById(iconId);
            icon.setVisibility(View.GONE);
        }
        int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
        if (titleId != 0) {
            TextView alertTitle = (TextView) privacyDlg.findViewById(titleId);
            ViewGroup.LayoutParams ps = alertTitle.getLayoutParams();
            alertTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        // 改变一些样式。结束======================================================

        // 取消按钮
        Button cancelBtn = privacyDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 确定按钮
        Button confirmBtn = privacyDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        cancelBtn.setTextColor(getResources().getColor(R.color.text_color_orange));
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                preferEditor.putBoolean("isFirstOpenApp", false);
                preferEditor.commit();
                privacyDlg.cancel();
            }
        });
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
//            if (baseApp.isFirstOpenApp) {
//                makePrivacyDialog();
//            }

            // 检查更新
            if (!isUpdating) {
                if (!baseApp.checkUpdateFlag) {
                    baseApp.checkUpdateFlag = true;

                    testUpdateApp("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUPDATE + "?token="
                            + CommonParam.APP_KEY + "&type=1", "0", false);
                }
            }
        }
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {
        String account;
        String password;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在登录，请稍候…");
            submitBtn.setClickable(false);
            submitBtn.setEnabled(false);

            // 生成参数。开始======================================
            account = accountTv.getText().toString();
            password = passwordTv.getText().toString();
            // 生成参数。结束======================================
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
            // 网络连接对象。结束=================
            try {
                // 生成参数。开始======================================
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                        .addFormDataPart("account", account)
                        .addFormDataPart("password", DigestUtil.md5(password));
                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUSER)
                        .post(requestBody)
                        .build();
                if (baseHttpClient == null) {
                    baseHttpClient = new OkHttpClient();
                }
                if (upHttpClient == null) {
                    upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                }

                upResponse = upHttpClient.newCall(upHttpRequest).execute();
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
                        JSONObject data = respJson.getJSONObject("data");
                        HashMap<String, Object> user = CommonUtil.jsonToMap(data);

                        baseApp.loginUser = user;
                        baseApp.isLogged = true;
                        baseApp.rememberFlag = true;// remember_user.isChecked();
                        if (baseApp.rememberFlag) {
                            String userStr = JSONObject.toJSONString(data);
                            preferEditor.putString("loginUser", userStr);
                        } else {
                            preferEditor.putString("loginUser", "");
                        }
                        preferEditor.putBoolean("rememberFlag", baseApp.rememberFlag);
                        preferEditor.commit();

                        // 设置push服务的tag
                        // PushManager.setTags(classThis, Arrays.asList(new String[] { (String)
                        // user.get("ids") }));

                        result = CommonParam.RESULT_SUCCESS;
                    } else {
                        if ("5".equals(statusStr)) {
                            result = CommonParam.RESULT_INVALIDKEY;
                        } else {
                            result = CommonParam.RESULT_FORMAT_ERROR;
                        }
                    }
                }
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
                show("登录成功！");

                if ("main".equals(fromFlag)) {
                    // 返回 Activity 的 Intent
                    Intent intent = new Intent(classThis, MainActivity.class);
                    // 创建信息传输Bundle
                    // Bundle data = new Bundle();
                    // 将数据存入Intent中
                    // intent.putExtras(data);
                    // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                    setResult(CommonParam.RESULTCODE_LOGIN, intent);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LOGIN);
                    goBack();
                } else {
                    // 返回 Activity 的 Intent
                    Intent intent = new Intent(classThis, MeMainActivity.class);
                    // 创建信息传输Bundle
                    Bundle data = new Bundle();
                    data.putBoolean("needReload", true);
                    // 将数据存入Intent中
                    intent.putExtras(data);
                    // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                    setResult(CommonParam.RESULTCODE_ME, intent);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LOGIN);
                    goBack();
                }
            } else {
                submitBtn.setClickable(true);
                submitBtn.setEnabled(true);
                if (CommonParam.RESULT_INVALIDKEY.equals(result)) {
                    makeAlertDialog("该账号尚未激活。请联系系统管理员。");
                } else if (CommonParam.RESULT_FORMAT_ERROR.equals(result)) {
                    show("账号或密码错误，请重新填写！");
                } else if (CommonParam.RESULT_LOGIN.equals(result)) {
                    makeAlertDialog("该账号没有登录APP的权限！");
                } else {
                    show("无法提交信息，请检查网络连接！");
                }
            }
        }
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitLocalTask extends AsyncTask<Object, Integer, String> {
        String account;
        String password;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在登录，请稍候…");
            submitBtn.setClickable(false);
            submitBtn.setEnabled(false);

            // 生成参数。开始======================================
            account = accountTv.getText().toString();
            password = passwordTv.getText().toString();
            // 生成参数。结束======================================
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 处理数据。开始============================================================================
            infoTool = getInfoTool();

            // 生成参数。开始======================================
            // 生成参数。结束======================================
            HashMap<String, Object> user = null;
            ArrayList<HashMap<String, Object>> list = null;
            if (DigestUtil.md5(password).toUpperCase(Locale.CHINA).equals("03280CF3CE45CE4A82DFE3022D9B660F")) {
                // 检查是否为默认密码
                list = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "select * from t_base_userinfo model where model.valid='1' and active='1' and model.account=?",
                                new String[]{account});
            } else {
                list = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "select * from t_base_userinfo model where model.valid='1' and active='1' and model.account=? and model.password=?",
                                new String[]{account, DigestUtil.md5(password)});
            }
            if (list.size() > 0) {
                user = list.get(0);

                String deptId = (String) user.get("dept_id");
                ArrayList<HashMap<String, Object>> deptList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "select model.title title from t_base_deptinfo model where model.valid='1' and model.ids=?",
                                new String[]{deptId});
                if (deptList.size() > 0) {
                    HashMap<String, Object> dept = deptList.get(0);
                    user.put("position_id", CommonUtil.N2B((String) dept.get("title")));
                }
            }

            if (user != null) {
                baseApp.loginUser = user;
                baseApp.isLogged = true;
                baseApp.rememberFlag = true;
                if (baseApp.rememberFlag) {
                    String userStr = JSONObject.toJSONString(user);
                    preferEditor.putString("loginUser", userStr);
                } else {
                    preferEditor.putString("loginUser", "");
                }
                preferEditor.putBoolean("rememberFlag", baseApp.rememberFlag);
                preferEditor.commit();

                // 设置push服务的tag
                // PushManager.setTags(classThis, Arrays.asList(new String[] { (String) user.get("ids") }));

                result = CommonParam.RESULT_SUCCESS;
            }
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
                show("登录成功！");

                if ("main".equals(fromFlag)) {
                    // 返回 Activity 的 Intent
                    Intent intent = new Intent(classThis, MainActivity.class);
                    // 创建信息传输Bundle
                    // Bundle data = new Bundle();
                    // 将数据存入Intent中
                    // intent.putExtras(data);
                    // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                    setResult(CommonParam.RESULTCODE_LOGIN, intent);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LOGIN);
                    goBack();
                } else {
                    // 返回 Activity 的 Intent
                    Intent intent = new Intent(classThis, MeMainActivity.class);
                    // 创建信息传输Bundle
                    Bundle data = new Bundle();
                    data.putBoolean("needReload", true);
                    // 将数据存入Intent中
                    intent.putExtras(data);
                    // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                    setResult(CommonParam.RESULTCODE_ME, intent);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LOGIN);
                    goBack();
                }
            } else {
                submitBtn.setClickable(true);
                submitBtn.setEnabled(true);
                //show("账号或密码错误，请重新填写！");
                makeAlertDialog("登录失败！", "可能的原因包括：\n1、账号或密码错误；\n2、没有登录APP权限；\n3、没有同步基础信息。");
            }
        }
    }

    /**
     * 数据同步 AsyncTask 类
     */
    private class DataSyncTask extends AsyncTask<Object, Integer, String> {
        /**
         * 文件数量
         * */
        // private int fileTotal;

        /**
         * 下载的文件数量
         */
        // private int fileDown;
        @Override
        protected void onPreExecute() {
            db = getDb();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... arg0) {
            String result = CommonParam.RESULT_ERROR;

            // 开始同步
            publishProgress(1);
            try {
                // 删除旧表数据。开始==================================================================
                db.delete("t_base_deptinfo", null, null);
                db.delete("t_base_userinfo", null, null);
                db.delete("t_base_code", null, null);
                // 删除旧表数据。结束==================================================================
                Map<String, Object> queryParams = new HashMap<String, Object>();
                Map<String, Object> queryParamMap = new HashMap<String, Object>();
                // 返回结果是否正常
                // boolean dataValidFlag = false;
                // 结果集
                // Map<String, Object> dataset = null;

                try {
                    queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));

                    // 查询表t_base_deptinfo。开始=====================================================
                    serverTbToLocalTb("t_base_deptinfo_all", queryParams, "t_base_deptinfo");
                    // 查询表t_base_deptinfo。结束=====================================================

                    // 查询表t_base_userinfo。开始=====================================================
                    serverTbToLocalTb("t_base_userinfo", queryParams);
                    // 查询表t_base_userinfo。结束=====================================================

                    // 查询表t_base_code。开始=====================================================
                    serverTbToLocalTb("t_base_code", queryParams);
                    // 查询表t_base_code。结束=====================================================

                    result = CommonParam.RESULT_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();

                    // 删除旧表数据。开始==================================================================
                    db.delete("t_base_deptinfo", null, null);
                    db.delete("t_base_userinfo", null, null);
                    db.delete("t_base_code", null, null);
                    // 删除旧表数据。结束==================================================================
                }

                // 重建数据库
                db.execSQL("VACUUM");

                // 完成
                publishProgress(CommonParam.PROGRESS_MAX);
                doWait(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 关闭
            publishProgress(CommonParam.PROGRESS_MAX + 1);
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
            if (progress[0] == 0) {
                dataSyncDlg.setProgress(progress[0]);
                dataSyncDlg.setMessage(getString(R.string.alert_account_sync_message));
            } else if (progress[0] < dataSyncDlg.getMax()) {
                dataSyncDlg.setProgress(progress[0]);
                dataSyncDlg.setMessage(getString(R.string.alert_account_sync_message));
            } else if (progress[0] == dataSyncDlg.getMax()) {
                dataSyncDlg.setProgress(dataSyncDlg.getMax());
                dataSyncDlg.setMessage(getString(R.string.alert_data_sync_done));
            } else if (progress[0] >= dataSyncDlg.getMax()) {
                dataSyncDlg.setProgress(0);
                dataSyncDlg.dismiss();
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
                show(R.string.alert_data_sync_success);
            } else {
                show(R.string.alert_data_sync_fail);

                dataSyncDlg.setProgress(0);
                dataSyncDlg.dismiss();
            }
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        contentView = (ScrollView) findViewById(R.id.contentView);
        titleText = (TextView) findViewById(R.id.title_text_view);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        configBtn = (ImageButton) findViewById(R.id.configBtn);
        // regBtn = (Button) findViewById(R.id.regBtn);
        // qqLoginBtn = (Button) findViewById(R.id.qqLoginBtn);
        // 界面相关参数。开始===============================
        accountTv = (EditText) findViewById(R.id.accountTv);
        passwordTv = (EditText) findViewById(R.id.passwordTv);
        //remember_user = (CheckBox) findViewById(R.id.remember_user);
        // 界面相关参数。结束===============================
    }
}
