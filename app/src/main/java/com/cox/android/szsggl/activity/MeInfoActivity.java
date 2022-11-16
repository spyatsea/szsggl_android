/*
 * Copyright (c) 2015 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我-个人信息页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MeInfoActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    // 界面相关参数。开始===============================
    /**
     * 取消
     */
    private Button cancelBtn;
    /**
     * 提交
     */
    private Button submitBtn;

    private ImageView userImage;
    private EditText usernameTv;
    private EditText accountTv;
    private TextView genderTv;
    private EditText phoneTv;
    private EditText emailTv;
    private Button switchGenderBtn;

    /**
     * 性别信息
     */
    private ArrayList<String> genderList;

    /**
     * 已选的性别索引
     */
    private int genderIndex;

    // 是否修改了用户头像
    private boolean haveChangePicture;

    // 界面相关参数。结束===============================

    // 图片显示相关参数。开始===============================================
    public static List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    // 图片显示相关参数。结束===============================================
    // 图片加载参数
    DisplayImageOptions displayImageOptions;

    private HashMap<String, Object> fileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MeInfoActivity.this;

        setContentView(R.layout.me_info);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();

        titleText = (TextView) findViewById(R.id.title_text_view);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        // 界面相关参数。开始===============================
        userImage = (ImageView) findViewById(R.id.userImage);
        usernameTv = (EditText) findViewById(R.id.usernameTv);
        accountTv = (EditText) findViewById(R.id.accountTv);
        genderTv = (TextView) findViewById(R.id.genderTv);
        phoneTv = (EditText) findViewById(R.id.phoneTv);
        emailTv = (EditText) findViewById(R.id.emailTv);
        switchGenderBtn = (Button) findViewById(R.id.switchGenderBtn);
        // 界面相关参数。结束===============================

        titleText.setSingleLine(true);
        titleText.setText("个人信息");

        haveChangePicture = false;

        genderList = new ArrayList<String>();
        genderList.add("男");
        genderList.add("女");
        genderIndex = -1;

        if (CommonUtil.checkNB((String) baseApp.loginUser.get("picture"))) {
            fileInfo = new HashMap<String, Object>();
            fileInfo.put("file", (String) baseApp.loginUser.get("picture"));
        }

        cancelBtn.setOnClickListener(new OnClickListener() {

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
        userImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(classThis, MultiImageSelectorActivity.class);

                // 是否显示调用相机拍照
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

                // 最大图片选择数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);

                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);

                startActivityForResult(intent, CommonParam.REQUESTCODE_CAMERA);
            }
        });

        switchGenderBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeChooseGenderDialog();
            }
        });

        displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_head_white) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_head_white) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_head_white) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .displayer(new RoundedBitmapDisplayer(25))// 是否设置为圆角，弧度为多少
                // .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();

        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.default_head_white, userImage,
                displayImageOptions);
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
        if (requestCode == CommonParam.REQUESTCODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> path = intent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (int i = 0, len = path.size(); i < len; i++) {
                    Log.d("----", "file:" + path.get(i));
                    String filePath = path.get(i);
                    Bitmap normalImage = CommonUtil.decodeSampledBitmapFromResource(filePath,
                            CommonParam.RESIZE_IMAGE_WIDTH, CommonParam.RESIZE_IMAGE_WIDTH);

                    String key = CommonUtil.getUUID();
                    String normalImageName = String.format("%s.jpg", key);

                    saveImage(normalImage, normalImageName);

                    HashMap<String, Object> info = new HashMap<String, Object>();

                    info.put("file", normalImageName);

                    fileInfo = info;
                    haveChangePicture = true;

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage("file://" + Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/" + CommonParam.PROJECT_NAME + "/temp/" + normalImageName, userImage,
                            displayImageOptions, new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    ImageView imageView = (ImageView) view;
                                    // imageView.setImageResource(R.drawable.thumbnail);

                                    if (imageView != null) {
                                        ImageLoader.getInstance().displayImage(
                                                "drawable://" + R.drawable.default_head_white, imageView,
                                                displayImageOptions);
                                    }
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    ImageView imageView = (ImageView) view;
                                    if (loadedImage != null) {
                                        boolean firstDisplay = !displayedImages.contains(imageUri);
                                        if (firstDisplay) {
                                            FadeInBitmapDisplayer.animate(imageView, 500);
                                            displayedImages.add(imageUri);
                                        }
                                    } else {
                                        // imageView.setImageResource(R.drawable.thumbnail);
                                        ImageLoader.getInstance().displayImage(
                                                "drawable://" + R.drawable.default_head_white, imageView,
                                                displayImageOptions);
                                    }
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                }
                            });
                }
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
                HashMap<String, Object> user = baseApp.loginUser;
                String username = (String) user.get("nickname");
                String account = (String) user.get("account");
                String genderStr = (String) user.get("gender");
                String gender = null;
                String phone = (String) user.get("mobilephone");
                String email = (String) user.get("email");
                String picture = (String) user.get("picture");

                usernameTv.setText(username);
                accountTv.setText(account);
                if ("0".equals(genderStr)) {
                    gender = "女";
                } else {
                    gender = "男";
                }
                genderTv.setText(gender);
                phoneTv.setText(CommonUtil.N2B(phone));
                emailTv.setText(CommonUtil.N2B(email));

                if (CommonUtil.checkNB(picture)) {

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage("http://" + baseApp.serverAddr + "/"
                                    + CommonParam.URL_UPLOADFILES + "/" + picture, userImage, displayImageOptions,
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    ImageView imageView = (ImageView) view;
                                    // imageView.setImageResource(R.drawable.thumbnail);

                                    if (imageView != null) {
                                        ImageLoader.getInstance().displayImage(
                                                "drawable://" + R.drawable.default_head_white, imageView,
                                                displayImageOptions);
                                    }
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    ImageView imageView = (ImageView) view;
                                    if (loadedImage != null) {
                                        boolean firstDisplay = !displayedImages.contains(imageUri);
                                        if (firstDisplay) {
                                            FadeInBitmapDisplayer.animate(imageView, 500);
                                            displayedImages.add(imageUri);
                                        }
                                    } else {
                                        // imageView.setImageResource(R.drawable.thumbnail);
                                        ImageLoader.getInstance().displayImage(
                                                "drawable://" + R.drawable.default_head_white, imageView,
                                                displayImageOptions);
                                    }
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                }
                            });
                } else {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.default_head_white, userImage,
                            displayImageOptions);
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

        String username = usernameTv.getText().toString();
        String account = accountTv.getText().toString();
        String gender = genderTv.getText().toString();
        String phone = phoneTv.getText().toString();
        String email = emailTv.getText().toString();
        String picture = null;
        if (fileInfo != null) {
            picture = (String) fileInfo.get("file");
        }

        if (!CommonUtil.checkNB(picture)) {
            errorMsg = "请设置用户头像！";
        } else if (!CommonUtil.checkNB(username)) {
            errorMsg = "请输入姓名！";
        } else if (!CommonUtil.checkNB(account)) {
            errorMsg = "请输入登录账号！";
        } else if (account.length() < 6) {
            errorMsg = "登录账号至少6位！";
        } else if (!CommonUtil.checkNB(gender)) {
            errorMsg = "请选择性别！";
        } else if (!CommonUtil.checkNB(phone)) {
            errorMsg = "请输入手机号！";
        } else if (!isMobile(phone)) {
            errorMsg = "手机号格式错误！";
        } else if (!CommonUtil.checkNB(email)) {
            errorMsg = "请输入电子邮箱地址！";
        } else if (!isEmailAddr(email)) {
            errorMsg = "电子邮箱地址格式错误！";
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
     * 手机号验证
     *
     * @param str {@code String} 待验证文本
     * @return {@code boolean} 验证结果，验证成功返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 邮箱地址验证
     *
     * @param str {@code String} 待验证文本
     * @return {@code boolean} 验证结果，验证成功返回true
     */
    public static boolean isEmailAddr(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$"); // 验证电子邮箱地址
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {
        String userId;
        String username;
        String account;
        String genderStr;
        String gender;
        String mobilephone;
        String email;

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
            username = (String) baseApp.loginUser.get("realname");
            account = accountTv.getText().toString();
            genderStr = genderTv.getText().toString();
            gender = null;
            mobilephone = phoneTv.getText().toString();
            email = emailTv.getText().toString();
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
                String picture = null;

                if ("男".equals(genderStr)) {
                    gender = "1";
                } else {
                    gender = "0";
                }
                if (fileInfo != null) {
                    picture = (String) fileInfo.get("file");
                }
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("username", username)
                        .addFormDataPart("account", account)
                        .addFormDataPart("gender", gender)
                        .addFormDataPart("mobilephone", mobilephone)
                        .addFormDataPart("email", email)
                        .addFormDataPart("picture", picture);
                if (haveChangePicture) {
                    String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/temp/";
                    multipartBuilder.addFormDataPart("filedata", picture, RequestBody.create(CommonParam.MEDIA_TYPE_BIN, new File(dirName + picture)));
                }
                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url("http://" + baseApp.serverAddr + "/"
                                + CommonParam.URL_UPDATEUSERINFO)
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
                    if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                        // 请求正确
                        JSONObject data = respJson.getJSONObject("data");
                        HashMap<String, Object> user = CommonUtil.jsonToMap(data);

                        baseApp.loginUser = user;
                        baseApp.isLogged = true;

                        if (baseApp.rememberFlag) {
                            String userStr = JSONObject.toJSONString(data);
                            preferEditor.putString("loginUser", userStr);
                        } else {
                            preferEditor.putString("loginUser", "");
                        }
                        preferEditor.commit();

                        result = CommonParam.RESULT_SUCCESS;
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
                show("信息提交成功！");
                // 返回 Activity 的 Intent
                Intent intent = new Intent(classThis, MeMainActivity.class);
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putBoolean("needReload", true);
                // 将数据存入Intent中
                intent.putExtras(data);
                // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                setResult(CommonParam.RESULTCODE_ME, intent);
                goBack();
            } else {
                submitBtn.setClickable(true);
                submitBtn.setEnabled(true);
                show("无法获取数据，请检查网络连接！");
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

        // 改变一些样式。开始======================================================
        if (customizeAlertDlgFlag) {
            int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId != 0) {
                TextView alertTitle = (TextView) dlg.findViewById(titleId);
                alertTitle.setTextColor(getResources().getColor(R.color.text_color_orange));
            }

            int titleDividerId = dlgBuilder.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            if (titleDividerId != 0) {
                View titleDivider = (View) dlg.findViewById(titleDividerId);
                titleDivider.setBackgroundColor(getResources().getColor(R.color.text_color_orange));
            }
        }
        // 改变一些样式。结束======================================================
    }

    /**
     * 切换性别
     */
    public void makeChooseGenderDialog() {
        Builder dlgBuilder = new Builder(this);

        dlgBuilder.setTitle("选择性别");
        dlgBuilder.setCancelable(true);

        String[] stationNameArray = new String[genderList.size()];
        for (int i = 0, len = genderList.size(); i < len; i++) {
            stationNameArray[i] = genderList.get(i);
        }

        dlgBuilder.setSingleChoiceItems(stationNameArray, genderIndex, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String gender = genderList.get(which);
                genderIndex = which;
                genderTv.setText(gender);
                dialog.cancel();
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // 改变一些样式。开始======================================================
        if (customizeAlertDlgFlag) {
            int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId != 0) {
                TextView alertTitle = (TextView) dlg.findViewById(titleId);
                alertTitle.setTextColor(getResources().getColor(R.color.text_color_orange));
            }

            int titleDividerId = dlgBuilder.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            if (titleDividerId != 0) {
                View titleDivider = (View) dlg.findViewById(titleDividerId);
                titleDivider.setBackgroundColor(getResources().getColor(R.color.text_color_orange));
            }
        }
        // 改变一些样式。结束======================================================
    }

    /**
     * 保存图片
     */
    public void saveImage(Bitmap tempImage, String imageName) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/temp/" + imageName);
            new FileUtil().saveBitmap(tempImage, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除图片
     */
    public void deleteImage(String imageName) {
        String normalImageName = imageName;
        String thumbImageName = String.format("thumb_%s", normalImageName);

        File normalImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + CommonParam.PROJECT_NAME + "/temp/" + normalImageName);
        File thumbImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + CommonParam.PROJECT_NAME + "/temp/" + thumbImageName);

        // Log.d("###1", "#" + normalImageFile.getAbsolutePath() + ":" + normalImageFile.exists());
        // Log.d("###1", "#" + thumbImageFile.getAbsolutePath() + ":" + thumbImageFile.exists());
        if (normalImageFile.exists()) {
            normalImageFile.delete();
        }
        if (thumbImageFile.exists()) {
            thumbImageFile.delete();
        }
        // Log.d("###2", "#" + normalImageFile.getAbsolutePath() + ":" + normalImageFile.exists());
        // Log.d("###2", "#" + thumbImageFile.getAbsolutePath() + ":" + thumbImageFile.exists());
    }
}
