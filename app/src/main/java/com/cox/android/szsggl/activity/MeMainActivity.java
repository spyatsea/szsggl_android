/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.tool.DbTool;
import com.cox.android.szsggl.tool.InsTool;
import com.cox.android.uhf.Reader;
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
import com.rfid.PowerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 栏目“我”的主页面
 *
 * @author 乔勇(Jacky Qiao)
 */
public class MeMainActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    // 界面相关参数。开始===============================
    /**
     * 刷新
     */
    private ImageButton refreshBtn;
    /**
     * 导航按钮：首页
     */
    private LinearLayout nav_main_layout;
    /**
     * 导航按钮：设置
     */
    private LinearLayout nav_config_layout;

    private ImageView userImage;
    private TextView newsItemTitle;
    private TextView newsItemAccount;
    // private TextView newsItemPro;

    // Layout=======================================
    // private LinearLayout me_feedback_layout;
    // private LinearLayout me_info_layout;
    // private LinearLayout me_sysParam_layout;
    // private LinearLayout me_changePwd_layout;
    // private LinearLayout me_checkUpdate_layout;
    // private LinearLayout me_resetDb_layout;
    // private LinearLayout me_serverAddr_layout;
    // private LinearLayout me_uploadTestData_layout;
    // private LinearLayout me_about_layout;
    private LinearLayout me_logout_layout;
    private LinearLayout me_login_layout;

    // 按钮=================================
    // private TextView me_feedback_btn;
    // private TextView me_info_btn;
    // private TextView me_sysParam_btn;
    private TextView me_baseInfoSync_btn;
    private TextView me_dataDownload_btn;
    private TextView me_dataUpload_btn;
    private Switch me_autoDownloadAtta_switch;
    private Switch me_autoPlayInsAudio_switch;
    private Switch me_reverseRotate_switch;
    private TextView me_uhfPower_btn;
    private TextView me_autoDkOverTime_btn;
    private TextView me_autoDkStayTime_btn;
    private TextView me_uhfKeyCode_btn;
    //private TextView me_changePwd_btn;
    private TextView me_clearCache_btn;
    private TextView me_resetDb_btn;
    private TextView me_serverAddr_btn;
    private TextView me_checkUpdate_btn;
    private TextView me_uploadTestData_btn;
    private TextView me_downloadOtherApp_btn;
    private TextView me_about_btn;
    private TextView me_logout_btn;
    private TextView me_login_btn;
    // 界面相关参数。结束===============================
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    /**
     * 数据下载ProgressDialog
     */
    private ProgressDialog dataDownloadDlg = null;
    /**
     * 数据上传ProgressDialog
     */
    private ProgressDialog dataUploadDlg = null;
    /**
     * 数据同步ProgressDialog
     */
    private ProgressDialog dataSyncDlg = null;
    /**
     * UHF功率Dialog
     */
    private AlertDialog uhfPowerDlg;
    /**
     * UHF扫卡快签信息保存间隔Dialog
     */
    private AlertDialog audoDkOverTimeDlg;
    /**
     * UHF扫卡快签信息保留时间Dialog
     */
    private AlertDialog audoDkStayTimeDlg;
    /**
     * UHF按键设置Dialog
     */
    private AlertDialog uhfKeyCodeDlg;
    // 图片显示相关参数。开始===============================================
    public static List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    // 图片显示相关参数。结束===============================================
    // 图片加载参数
    DisplayImageOptions displayImageOptions;

    private boolean needGetUser;
    private boolean needReload;

    boolean[] showArray;

    // layout 数组
    LinearLayout[] LAYOUT_ARRAY;
    boolean[] SHOW_LOGIN = {true, false};
    boolean[] SHOW_LOGOUT = {false, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MeMainActivity.this;

        // 获取Intent
        // Intent intent = getIntent();
        // 获取Intent上携带的数据
        // Bundle data = intent.getExtras();

        setContentView(R.layout.me_main_x);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        // titleText.setSingleLine(true);
        // titleText.setText(R.string.config);

        needGetUser = false;
        needReload = true;
        showArray = SHOW_LOGOUT;
        LAYOUT_ARRAY = new LinearLayout[]{me_logout_layout, me_login_layout};

        nav_main_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MainActivity.class);
                // 信息传输Bundle
                // Bundle data = new Bundle();
                // data.putString("fromFlag", "me_main");
                // // 将数据存入Intent中
                // intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_CONFIG);
                finish();
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.activity_slide_right_out);
            }
        });
        nav_config_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        me_baseInfoSync_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    makeDataSyncConfirmDialog();
                }
            }
        });
        me_dataDownload_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    makeDataDownloadConfirmDialog();
                }
            }
        });
        me_dataUpload_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    new CheckBeforeDataUpTask_ok().execute();
                }
            }
        });
        me_autoDownloadAtta_switch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                baseApp.isAutoDownloadAtta = me_autoDownloadAtta_switch.isChecked();
                preferEditor.putBoolean("isAutoDownloadAtta", baseApp.isAutoDownloadAtta);
                preferEditor.commit();

                me_autoDownloadAtta_switch.setChecked(baseApp.isAutoDownloadAtta);
            }
        });
        me_autoPlayInsAudio_switch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                baseApp.isAutoPlayInsAudio = me_autoPlayInsAudio_switch.isChecked();
                preferEditor.putBoolean("isAutoPlayInsAudio", baseApp.isAutoPlayInsAudio);
                preferEditor.commit();

                me_autoPlayInsAudio_switch.setChecked(baseApp.isAutoPlayInsAudio);
            }
        });
        me_reverseRotate_switch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                baseApp.isReverseRotate = me_reverseRotate_switch.isChecked();
                preferEditor.putBoolean("isReverseRotate", baseApp.isReverseRotate);
                preferEditor.commit();

                me_reverseRotate_switch.setChecked(baseApp.isReverseRotate);
            }
        });
        me_uhfPower_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.isUhfPda) {
                    makeSetUhfPowerDialog();
                } else {
                    makeAlertDialog("您的设备不支持UHF功能！");
                }
            }
        });
        me_autoDkOverTime_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.isUhfPda) {
                    makeSetAutoDkOverTimeDialog();
                } else {
                    makeAlertDialog("您的设备不支持UHF功能！");
                }
            }
        });
        me_autoDkStayTime_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.isUhfPda) {
                    makeSetAutoDkStayTimeDialog();
                } else {
                    makeAlertDialog("您的设备不支持UHF功能！");
                }
            }
        });
        me_uhfKeyCode_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.isUhfPda) {
                    makeSetUhfKeyCodeDialog();
                } else {
                    makeAlertDialog("您的设备不支持UHF功能！");
                }
            }
        });
//        me_changePwd_btn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 创建启动 Activity 的 Intent
//                Intent intent = new Intent(classThis, MeChangePwdActivity.class);
//                // 信息传输Bundle
//                // Bundle data = new Bundle();
//                // 将数据存入Intent中
//                // intent.putExtras(data);
//                startActivityForResult(intent, CommonParam.REQUESTCODE_ME);
//                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
//            }
//        });
        me_clearCache_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清理缓存
                makeClearCacheDialog();
            }
        });
        me_resetDb_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 复位数据
                // 显示重置数据提示对话框
                makeResetDbDialog();
            }
        });
        me_serverAddr_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 服务器地址
                makeSetServerDialog();
            }
        });
        me_checkUpdate_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 检查更新
                if (!isUpdating) {
                    testUpdateApp("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUPDATE + "?token="
                            + CommonParam.APP_KEY + "&type=1", CommonParam.YES);
                }
            }
        });
        me_uploadTestData_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 上传系统测试数据
                new UploadTestDataTask().execute();
            }
        });
        me_downloadOtherApp_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, DownloadOtherAppActivity.class);
                // 信息传输Bundle
                // Bundle data = new Bundle();
                // 将数据存入Intent中
                // intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_ME);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });
        me_about_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("---", "#about");
                makeAboutDialog();
//
//                // 创建信息传输Bundle
//                Bundle data = new Bundle();
//                // 创建启动 Activity 的 Intent
//                Intent intent = new Intent(MeMainActivity.this, TestAnimaActivity.class);
//                // 将数据存入 Intent 中
//                intent.putExtras(data);
//                startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
//                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);


//                HashMap<String, Object> info = new HashMap<String, Object>();
//                info.put("type", CommonParam.ATTA_TYPE_PHOTO);
//                info.put("name", "10#隧道现场图.jpg");
//                info.put("alias", "10#隧道现场图片.jpg");
//                info.put("size", "2.3M");
//
                // 打开Doodle。开始====================================
//                // 创建启动 Activity 的 Intent
//                Intent intent = new Intent(classThis, PicEditActivity.class);
//                // 信息传输Bundle
//                Bundle data = new Bundle();
//                data.putSerializable("info", info);
//                // 将数据存入Intent中
//                intent.putExtras(data);
//                startActivity(intent);
//                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
//
//                File imageFileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
//                        + CommonParam.PROJECT_NAME + "/ins");
//                if (!imageFileDir.exists()) {
//                    imageFileDir.mkdir();
//                }
//                String srcFileName = "2.jpg";//CommonUtil.getDT("yyyyMMddHHmmss") + ".jpg";
//                String postfix = CommonUtil.getPostfix(srcFileName);
//                String dstFileName = srcFileName.substring(0, srcFileName.lastIndexOf(".")) + "_" + CommonUtil.getDT("yyyyMMddHHmmss") + "." + postfix;//CommonUtil.getDT("yyyyMMddHHmmss") + ".jpg";
//                String srcFilePath = imageFileDir.getAbsolutePath() + "/" + srcFileName;
//                String dstFilePath = imageFileDir.getAbsolutePath() + "/" + dstFileName;
//                // 涂鸦参数
//                DoodleParams params = new DoodleParams();
//                params.mIsFullScreen = true;
//                // 源图片路径
//                params.mImagePath = srcFilePath;
//                // 图片保存路径
//                params.mSavePath = dstFilePath;
//                // 初始画笔大小
//                params.mPaintUnitSize = DoodleView.DEFAULT_SIZE;
//                // 画笔颜色
//                params.mPaintColor = Color.RED;
//                // 是否支持缩放item
//                params.mSupportScaleItem = true;
//                // 启动涂鸦页面
//                QDoodleActivity.startActivityForResult(classThis, params, CommonParam.REQUESTCODE_DOODLE);
//                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                // 打开Doodle。结束====================================

                // 打开会议系统。开始====================================
//				Intent intent = new Intent();
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.setAction(android.content.Intent.ACTION_VIEW);
//				intent.setClassName("com.rhubcom.turbomeeting80", "com.rhubcom.turbomeeting80.PSplashScreen");
//				intent.putExtra("meeting_id", 11061831);
//				intent.putExtra("password", "123456");
//				intent.putExtra("name", "我是APP");
//				intent.putExtra("server_address", "demo.rhubcom.cn");
//				intent.putExtra("start_meeting", true);
//
//				startActivity(intent);
                // 打开会议系统。结束====================================
            }
        });
        me_logout_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("---", "#logout");
                checkLogoutDialog();
            }
        });
        me_login_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("---", "#login");
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MeLoginActivity.class);
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putString("fromFlag", "me_main");
                // 将数据存入Intent中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_ME);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });
        // me_feedback_btn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // Log.d("---", "#feedback");
        // // 创建启动 Activity 的 Intent
        // Intent intent = new Intent(classThis, MeFeedbackEditActivity.class);
        // // 信息传输Bundle
        // // Bundle data = new Bundle();
        // // 将数据存入Intent中
        // // intent.putExtras(data);
        // startActivityForResult(intent, CommonParam.REQUESTCODE_ME);
        // overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
        // }
        // });


        refreshBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // if (requestCode == CommonParam.REQUESTCODE_ME && resultCode == CommonParam.RESULTCODE_ME) {
        // Bundle data = intent.getExtras();
        // needReload = data.getBoolean("needReload", false);
        // needGetUser = data.getBoolean("needGetUser", false);
        // if (needReload) {
        // reloadData();
        // }
        // if (needGetUser) {
        // new GetUserTask().execute();
        // }
        // }
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

            // ============================================================================

            // ============================================================================

            // 设置字段及按钮
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
                // 设置字段及按钮
                processUserFields();
                reloadData();
                me_autoDownloadAtta_switch.setChecked(baseApp.isAutoDownloadAtta);
                me_autoPlayInsAudio_switch.setChecked(baseApp.isAutoPlayInsAudio);
                me_reverseRotate_switch.setChecked(baseApp.isReverseRotate);
                if (baseApp.isUhfPda) {
                    me_uhfPower_btn.setBackgroundResource(R.drawable.custom_btn_transparent_blue);
                    me_uhfPower_btn.setText("" + baseApp.uhfPower);

                    me_autoDkOverTime_btn.setBackgroundResource(R.drawable.custom_btn_transparent_blue);
                    me_autoDkOverTime_btn.setText("" + baseApp.autoDkOverTime);

                    me_autoDkStayTime_btn.setBackgroundResource(R.drawable.custom_btn_transparent_blue);
                    me_autoDkStayTime_btn.setText("" + baseApp.autoDkStayTime);

                    me_uhfKeyCode_btn.setBackgroundResource(R.drawable.custom_btn_transparent_blue);
                    me_uhfKeyCode_btn.setText("" + baseApp.uhfKeyCode);
                } else {
                    me_uhfPower_btn.setBackgroundResource(R.drawable.border_grey_readonly);
                    me_uhfPower_btn.setText("设备不支持");

                    me_autoDkOverTime_btn.setBackgroundResource(R.drawable.border_grey_readonly);
                    me_autoDkOverTime_btn.setText("设备不支持");

                    me_autoDkStayTime_btn.setBackgroundResource(R.drawable.border_grey_readonly);
                    me_autoDkStayTime_btn.setText("设备不支持");

                    me_uhfKeyCode_btn.setBackgroundResource(R.drawable.border_grey_readonly);
                    me_uhfKeyCode_btn.setText("设备不支持");
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
     * 显示退出程序提示对话框
     */
    @Override
    public void makeExitDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.alert_askquit);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(CommonParam.RESULTCODE_EXIT);
                // 退出程序
                finishApp();
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dlg = dlgBuilder.create();
        dlg.show();
    }

    public void processUserFields() {
        String nickname = null;
        String account = null;
        String picture = null;
        String type = null;
        String deptTitle = null;

        HashMap<String, Object> user = baseApp.loginUser;
        if (user != null) {
            // 已登录
            nickname = (String) user.get("realname");
            account = (String) user.get("account");
            picture = (String) user.get("picture");
            Log.d("---", "#" + picture);
            type = (String) user.get("type");
            deptTitle = CommonUtil.N2B((String) user.get("position_id"));

            // if ("0".equals(type)) {
            // newsItemPro.setVisibility(View.GONE);
            // } else {
            // newsItemPro.setVisibility(View.VISIBLE);
            // }

        } else {
            // 未登录
            nickname = "未登录";
            account = "";
            picture = null;
            deptTitle = "";
            // newsItemPro.setVisibility(View.GONE);
        }
        newsItemTitle.setText(nickname);
        newsItemAccount.setText(deptTitle);

        if (CommonUtil.checkNB(picture)) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(
                    "http://" + baseApp.serverAddr + "/" + CommonParam.URL_UPLOADFILES + "/" + picture, userImage,
                    displayImageOptions, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            ImageView imageView = (ImageView) view;
                            // imageView.setImageResource(R.drawable.thumbnail);

                            if (imageView != null) {
                                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.default_head_white,
                                        imageView, displayImageOptions);
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
                                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.default_head_white,
                                        imageView, displayImageOptions);
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

    public void reloadData() {
        if (needReload) {
            HashMap<String, Object> user = baseApp.loginUser;
            if (user != null) {
                // 已登录
                showArray = SHOW_LOGIN;
            } else {
                // 未登录
                showArray = SHOW_LOGOUT;
            }
            reloadListData();
            processUserFields();

            needReload = false;
        }
    }

    public void reloadListData() {
        if (showArray != null) {
            for (int i = 0, len = showArray.length; i < len; i++) {
                LinearLayout layout = LAYOUT_ARRAY[i];
                boolean showFlag = showArray[i];
                if (showFlag) {
                    layout.setVisibility(View.VISIBLE);
                } else {
                    layout.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 获得用户信息的 AsyncTask 类
     */
    private class GetUserTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：设置字段及按钮
         */
        private static final int PROGRESS_SET_FIELD = 1001;

        /**
         * 收藏标志
         */
        private boolean isFav = false;

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
                HashMap<String, Object> loginUser = baseApp.loginUser;
                String userId = (String) loginUser.get("ids");
                String account = (String) loginUser.get("account");
                String password = (String) loginUser.get("password");
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("account", account)
                        .addFormDataPart("password", password);
                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUSERDETAIL)
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

                        if (resultFlag) {
                            needGetUser = false;

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
                    } else {
                        result = CommonParam.RESULT_INVALIDKEY;
                    }

                }
                // 查询信息。结束====================================================================
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
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
                if (needGetUser || needReload) {
                    if (needReload) {
                        needReload = false;
                    }
                    processUserFields();
                }
            } else if (CommonParam.RESULT_INVALIDKEY.equals(result)) {
                makeAlertDialog("用户信息过期，请重新登录");
            } else {
                makeAlertDialog("无法获取信息，请检查网络连接！");
            }
        }
    }

    /**
     * 注销用户信息
     *
     * @param needAlert         {@code boolean} 是否需要发出提示信息
     * @param gotoLoginPageFlag {@code boolean} 是否需要返回登录界面
     */
    @Override
    public void makeLogout(boolean needAlert, boolean gotoLoginPageFlag) {
        baseApp.loginUser = null;
        baseApp.rememberFlag = true;
        baseApp.isLogged = false;

        preferEditor.putString("loginUser", "");
        preferEditor.putBoolean("rememberFlag", baseApp.rememberFlag);
        preferEditor.commit();

        needReload = true;

        // baseApp.delBdPushTags();

        reloadData();

        // 创建启动 Activity 的 Intent
        Intent intent = new Intent(classThis, MeLoginActivity.class);
        // 信息传输Bundle
        Bundle data = new Bundle();
        data.putString("fromFlag", "me_main");
        // 将数据存入Intent中
        intent.putExtras(data);
        startActivityForResult(intent, CommonParam.REQUESTCODE_ME);
        finish();
        overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
    }

    /**
     * 显示数据同步提示对话框
     */
    public void makeDataSyncConfirmDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.baseDataSync);
        dlgBuilder.setMessage(R.string.alert_data_sync_confirm);
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
        if (!checkNet()) {
            return;
        }

        insTool = getInsTool();

        if (dataSyncDlg == null) {
            dataSyncDlg = new ProgressDialog(this);
            dataSyncDlg.setTitle(R.string.baseDataSync);
            dataSyncDlg.setMessage(getString(R.string.alert_data_sync_message));
            dataSyncDlg.setMax(CommonParam.PROGRESS_MAX);
            dataSyncDlg.setCancelable(false);
            dataSyncDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dataSyncDlg.setProgress(0);
            dataSyncDlg.setIndeterminate(true);
            dataSyncDlg.setIcon(R.drawable.item_icon_update);

            dataSyncDlg.setOnKeyListener(new OnKeyListener() {

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
     * 数据同步 AsyncTask 类
     */
    private class DataSyncTask extends AsyncTask<Object, Integer, String> {
        /**
         * 文件数量
         */
        private int fileTotal;
        /**
         * 下载的文件数量
         */
        private int fileDown;

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
                db.delete("t_szfgs_sgcategory", null, null);
                db.delete("t_szfgs_sgres", null, null);
                db.delete("t_szfgs_sgresareasign", null, null);
                db.delete("t_szfgs_sgwxstage", null, null);
                db.delete("t_szfgs_sgwxcat", null, null);
                // 删除旧表数据。结束==================================================================
                Map<String, Object> queryParams = new HashMap<String, Object>();
                Map<String, Object> queryParamMap = new HashMap<String, Object>();
                // 返回结果是否正常
                boolean dataValidFlag = false;
                // 结果集
                Map<String, Object> dataset = null;

                try {
                    // 查询表t_base_deptinfo。开始=====================================================
                    queryParams.put("userId", (String) baseApp.getLoginUser().get("ids"));
                    queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));

                    serverTbToLocalTb("t_base_deptinfo_all", queryParams, "t_base_deptinfo");
                    // 查询表t_base_deptinfo。结束=====================================================

                    // 查询表t_base_userinfo。开始=====================================================
                    serverTbToLocalTb("t_base_userinfo", queryParams);
                    // 查询表t_base_userinfo。结束=====================================================

                    // 查询表t_base_code。开始=====================================================
                    serverTbToLocalTb("t_base_code", queryParams);
                    // 查询表t_base_code。结束=====================================================

                    serverTbToLocalTb("t_szfgs_sgcategory", queryParams);
                    serverTbToLocalTb("t_szfgs_sgres", queryParams);
                    serverTbToLocalTb("t_szfgs_sgresareasign", queryParams);
                    serverTbToLocalTb("t_szfgs_sgwxstage", queryParams);
                    serverTbToLocalTb("t_szfgs_sgwxcat", queryParams);

                    result = CommonParam.RESULT_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();

                    // 删除旧表数据。开始==================================================================
                    db.delete("t_base_deptinfo", null, null);
                    db.delete("t_base_userinfo", null, null);
                    db.delete("t_base_code", null, null);
                    db.delete("t_szfgs_sgcategory", null, null);
                    db.delete("t_szfgs_sgres", null, null);
                    db.delete("t_szfgs_sgresareasign", null, null);
                    db.delete("t_szfgs_sgwxstage", null, null);
                    db.delete("t_szfgs_sgwxcat", null, null);
                    // 删除旧表数据。结束==================================================================
                }

                // 重建数据库
                db.execSQL("VACUUM");

                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    // 更新当前用户信息。开始==================================================================
                    HashMap<String, Object> user = null;
                    ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                            "select * from t_base_userinfo model where model.valid='1' and model.ids=?",
                            new String[]{(String) baseApp.loginUser.get("ids")});
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
                        baseApp.rememberFlag = true;// remember_user.isChecked();
                        if (baseApp.rememberFlag) {
                            String userStr = JSONObject.toJSONString(user);
                            preferEditor.putString("loginUser", userStr);
                        } else {
                            preferEditor.putString("loginUser", "");
                        }
                        preferEditor.putBoolean("rememberFlag", baseApp.rememberFlag);
                        preferEditor.commit();
                        result = CommonParam.RESULT_SUCCESS;
                    } else {
                        result = CommonParam.RESULT_LOGIN;
                    }
                    // 更新当前用户信息。结束==================================================================

                    // 更新其他信息。开始==================================================================
                    String ins_distance_str = infoTool.getSingleVal(
                            "select model.zdname from t_base_code model where model.valid='1' and model.type='偏关分公司_水工巡视参数' and model.zdcode='水工巡视定位有效距离'",
                            new String[]{});
                    if (CommonUtil.checkNB(ins_distance_str)) {
                        int ins_distance = -1;
                        try {
                            ins_distance = Integer.parseInt(ins_distance_str);
                        } catch (Exception e) {
                            ins_distance = -1;
                        }
                        if (ins_distance != -1) {
                            CommonParam.SYSCONFIG_VALUE_INS_DISTANCE = ins_distance;
                            preferEditor.putInt("SYSCONFIG_VALUE_INS_DISTANCE", ins_distance);
                            preferEditor.commit();
                        }
                    }
                    Log.d("#dis", ins_distance_str + ":" + CommonParam.SYSCONFIG_VALUE_INS_DISTANCE);
                    // 更新其他信息。结束==================================================================
                }

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
                dataSyncDlg.setMessage(getString(R.string.alert_data_sync_message));
            } else if (progress[0] < dataSyncDlg.getMax()) {
                dataSyncDlg.setProgress(progress[0]);
                dataSyncDlg.setMessage(getString(R.string.alert_data_sync_message));
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
            } else if (CommonParam.RESULT_LOGIN.equals(result)) {
                dataSyncDlg.setProgress(0);
                dataSyncDlg.dismiss();
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MeLoginActivity.class);
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putString("fromFlag", "me_main");
                // 将数据存入Intent中
                intent.putExtras(data);
                startActivity(intent);
                finish();
            } else {
                show(R.string.alert_data_sync_fail);

                dataSyncDlg.setProgress(0);
                dataSyncDlg.dismiss();
            }
        }
    }

    /**
     * 显示数据下载提示对话框
     */
    public void makeDataDownloadConfirmDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.title_bar_m3);
        dlgBuilder.setMessage(R.string.alert_data_download_confirm);
        dlgBuilder.setIcon(R.drawable.item_icon_download);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeDataDownloadDialog();
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
     * 显示数据下载进度对话框
     */
    public void makeDataDownloadDialog() {
        if (!checkNet()) {
            return;
        }

        insTool = getInsTool();

        if (dataDownloadDlg == null) {
            dataDownloadDlg = new ProgressDialog(this);
            dataDownloadDlg.setTitle(R.string.title_bar_m3);
            dataDownloadDlg.setMessage(getString(R.string.alert_data_download_message));
            dataDownloadDlg.setMax(CommonParam.PROGRESS_MAX);
            dataDownloadDlg.setCancelable(false);
            dataDownloadDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dataDownloadDlg.setProgress(0);
            dataDownloadDlg.setIndeterminate(true);
            dataDownloadDlg.setIcon(R.drawable.item_icon_download);

            dataDownloadDlg.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dataDownloadDlg.cancel();
                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });
        }
        dataDownloadDlg.show();

        new DataDownloadTask_ok().execute();
    }

    /**
     * 数据下载 AsyncTask 类
     */
    private class DataDownloadTask_ok extends AsyncTask<Object, Integer, String> {
        /**
         * 文件数量
         */
        private int fileTotal;
        /**
         * 下载的文件数量
         */
        private int fileDown;
        /**
         * 当前下载的文件名称
         */
        private String attaName = "";

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
                db.delete("t_szfgs_sgcategory", null, null);
                db.delete("t_szfgs_sgres", null, null);
                db.delete("t_szfgs_sgresareasign", null, null);
                db.delete("t_szfgs_sgwxstage", null, null);
                db.delete("t_szfgs_sgwxcat", null, null);
                db.delete("t_szfgs_sgwxinfo", "quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                db.delete("t_szfgs_sgwxrec", "quid=? and up='1'", new String[]{(String) baseApp.getLoginUser().get("ids")});
                // 删除旧表数据。结束==================================================================
                Map<String, Object> queryParams = new HashMap<String, Object>();
                Map<String, Object> queryParamMap = new HashMap<String, Object>();
                // 返回结果是否正常
                boolean dataValidFlag = false;
                // 结果集
                Map<String, Object> dataset = null;
                // 结果数据
                List<HashMap<String, Object>> data = null;
                // 数据写入是否正常
                boolean insertResultFlag = false;
                // 服务器上当前可以巡视的巡视业务编号List
                List<String> serverBizIdList = new ArrayList<String>();
                // 服务器上当前可以巡视的巡视业务编号
                StringBuffer serverBizIdSb = new StringBuffer();
                // APP中当前用户可见的巡视业务编号List
                List<String> appBizIdList = new ArrayList<String>();
                // APP中当前用户可见的巡视业务编号
                StringBuffer appBizIdSb = new StringBuffer();
                // 不需要下载的巡视业务编号List
                List<String> notDownloadBizIdList = new ArrayList<String>();
                // 不需要下载的巡视业务编号
                StringBuffer notDownloadBizIdSb = new StringBuffer();
                // 实际下载的巡视业务编号
                StringBuffer realityDownloadBizIdSb = new StringBuffer();

                try {
                    // 查询表t_base_deptinfo。开始=====================================================
                    queryParams.put("userId", (String) baseApp.getLoginUser().get("ids"));
                    queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));

                    serverTbToLocalTb("t_base_deptinfo_all", queryParams, "t_base_deptinfo");
                    // 查询表t_base_deptinfo。结束=====================================================

                    // 查询表t_base_userinfo。开始=====================================================
                    serverTbToLocalTb("t_base_userinfo", queryParams);
                    // 查询表t_base_userinfo。结束=====================================================

                    // 查询表t_base_code。开始=====================================================
                    serverTbToLocalTb("t_base_code", queryParams);
                    // 查询表t_base_code。结束=====================================================

                    serverTbToLocalTb("t_szfgs_sgcategory", queryParams);
                    serverTbToLocalTb("t_szfgs_sgres", queryParams);
                    serverTbToLocalTb("t_szfgs_sgresareasign", queryParams);
                    serverTbToLocalTb("t_szfgs_sgwxstage", queryParams);
                    serverTbToLocalTb("t_szfgs_sgwxcat", queryParams);

                    // 查询表t_szfgs_sgwxinfo。开始=====================================================
                    // serverTbToLocalTb("t_szfgs_sgwxinfo", queryParams);
                    dataset = getTbFromServer_ok("t_szfgs_sgwxinfo", queryParams);
                    dataValidFlag = (Boolean) dataset.get("dataValidFlag");
                    if (!dataValidFlag) {
                        throw new Exception();
                    }
                    data = (ArrayList<HashMap<String, Object>>) dataset
                            .get("data");
                    for (HashMap<String, Object> m : data) {
                        m.put("quid", (String) baseApp.getLoginUser().get("ids"));
                    }
                    insertResultFlag = insertToTable(dataset, "t_szfgs_sgwxinfo", false);
                    if (!insertResultFlag) {
                        throw new Exception();
                    }
                    // 查询表t_szfgs_sgwxinfo。结束=====================================================

                    // 查询表t_szfgs_sgwxrec。开始=====================================================
                    queryParams.clear();
                    queryParamMap.clear();
                    // 当前APP中已经有了的基本信息
                    ArrayList<String> existInfoList = (ArrayList<String>) infoTool
                            .getValList(
                                    "select model.ids ids from t_szfgs_sgwxinfo model where model.valid='1' and model.quid=?",
                                    new String[]{(String) baseApp.getLoginUser().get("ids")});
                    StringBuffer infoIdSb = new StringBuffer();
                    for (String info_id : existInfoList) {
                        infoIdSb.append("," + info_id);
                    }
                    if (infoIdSb.length() > 0) {
                        infoIdSb.delete(0, 1);
                    }
                    queryParams.put("userId", (String) baseApp.getLoginUser().get("ids"));
                    queryParams.put("infoIds", infoIdSb.toString());
                    queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));
                    // serverTbToLocalTb("t_szfgs_sgwxrec", queryParams);
                    dataset = getTbFromServer_ok("t_szfgs_sgwxrec", queryParams);
                    dataValidFlag = (Boolean) dataset.get("dataValidFlag");
                    if (!dataValidFlag) {
                        throw new Exception();
                    }
                    data = (ArrayList<HashMap<String, Object>>) dataset
                            .get("data");
                    for (HashMap<String, Object> m : data) {
                        m.put("quid", (String) baseApp.getLoginUser().get("ids"));
                    }
                    insertResultFlag = insertToTable(dataset, "t_szfgs_sgwxrec", false);
                    if (!insertResultFlag) {
                        throw new Exception();
                    }
                    // 查询表t_szfgs_sgwxrec。结束=====================================================

                    queryParams.clear();
                    queryParamMap.clear();
                    // APP中当前用户可见的巡视业务信息
                    ArrayList<HashMap<String, Object>> appExistBizList = (ArrayList<HashMap<String, Object>>) infoTool
                            .getInfoMapList(
                                    "select model.ids ids, model.realatime realatime, model.realbtime realbtime from t_biz_sgxuns model where model.valid='1' and (model.fzr=? or INSTR(model.ryap, ?)>0) and model.quid=?",
                                    new String[]{(String) baseApp.getLoginUser().get("ids"),
                                            (String) baseApp.getLoginUser().get("ids"),
                                            (String) baseApp.getLoginUser().get("ids")});
                    for (HashMap<String, Object> biz : appExistBizList) {
                        String biz_id = (String) biz.get("ids");
                        if (!appBizIdList.contains(biz_id)) {
                            appBizIdList.add(biz_id);
                        }
                    }
                    for (String s : appBizIdList) {
                        appBizIdSb.append("," + s);
                    }
                    if (appBizIdSb.length() > 0) {
                        appBizIdSb.delete(0, 1);
                    }

                    queryParams.clear();
                    queryParamMap.clear();
                    queryParams.put("userId", (String) baseApp.getLoginUser().get("ids"));
                    queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));
                    // 这里查询所有可以下载的任务编号
                    dataset = getTbFromServer_ok("sql_biz_sgxuns_lite", queryParams);
                    // 检查本地的业务信息。开始=====================================================
                    // 如果APP中的任务已经不能上传到服务器（不在第2环节），那么根据条件来处理：
                    // 如果APP中的任务还没有巡视，就直接删除。
                    // 如果APP中的任务已经开始巡视，无论是否已完成，都不做处理。
                    dataValidFlag = (Boolean) dataset.get("dataValidFlag");
                    if (dataValidFlag) {
                        data = (ArrayList<HashMap<String, Object>>) dataset
                                .get("data");
                        for (HashMap<String, Object> m : data) {
                            serverBizIdList.add((String) m.get("id"));
                        }
                    }
                    for (String s : serverBizIdList) {
                        serverBizIdSb.append(",'" + s + "'");
                    }
                    if (serverBizIdSb.length() > 0) {
                        serverBizIdSb.delete(0, 1);
                    }
                    // 遍历APP中当前用户可见的业务编号，如果该业务不属于服务器上当前可以巡视的巡视业务，则需要判断是否要从APP删除该业务
                    for (HashMap<String, Object> biz : appExistBizList) {
                        String biz_id = (String) biz.get("ids");
                        if (!serverBizIdList.contains(biz_id)) {
                            // 如果APP中的业务不能上传，就要根据情况处理
                            // 实际开始时间
                            String realatime = (String) biz.get("realatime");
                            // 实际结束时间
                            String realbtime = (String) biz.get("realbtime");
                            if (!CommonUtil.checkNB(realatime) && !CommonUtil.checkNB(realbtime)) {
                                // 还没有开始巡视，就要删除该业务
                                db.delete("t_biz_sgxuns", "ids=? and quid=?", new String[]{biz_id, (String) baseApp.getLoginUser().get("ids")});
                            }
                        }
                    }
                    // 遍历服务器上当前可以巡视的巡视业务编号，如果APP中有该业务编号，则不需要从服务器下载该业务
                    for (String serverBizId : serverBizIdList) {
                        if (appBizIdList.contains(serverBizId)) {
                            notDownloadBizIdList.add(serverBizId);
                        }
                    }
                    for (String s : notDownloadBizIdList) {
                        notDownloadBizIdSb.append("," + s);
                    }
                    if (notDownloadBizIdSb.length() > 0) {
                        notDownloadBizIdSb.delete(0, 1);
                    }
                    // 检查本地的业务信息。结束=====================================================

                    queryParams.clear();
                    queryParamMap.clear();
                    queryParams.put("userId", (String) baseApp.getLoginUser().get("ids"));
                    queryParams.put("bizIds", notDownloadBizIdSb.toString());
                    queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));
                    // dataset = serverTbToLocalTb("sql_biz_sgxuns", queryParams, "t_biz_sgxuns", false);
                    dataset = getTbFromServer_ok("sql_biz_sgxuns", queryParams);
                    dataValidFlag = (Boolean) dataset.get("dataValidFlag");
                    if (!dataValidFlag) {
                        throw new Exception();
                    }
                    data = (ArrayList<HashMap<String, Object>>) dataset
                            .get("data");
                    for (HashMap<String, Object> m : data) {
                        m.put("quid", (String) baseApp.getLoginUser().get("ids"));
                    }
                    insertResultFlag = insertToTable(dataset, "t_biz_sgxuns", false);
                    if (!insertResultFlag) {
                        throw new Exception();
                    }
                    // 下载巡视附件。开始======================================================================================
                    dataValidFlag = (Boolean) dataset.get("dataValidFlag");
                    if (dataValidFlag) {
                        data = (ArrayList<HashMap<String, Object>>) dataset.get("data");
                        for (HashMap<String, Object> m : data) {
                            String id = (String) m.get("ids");
                            realityDownloadBizIdSb.append("," + id);
                        }
                        if (realityDownloadBizIdSb.length() > 0) {
                            realityDownloadBizIdSb.delete(0, 1);
                        }
                    }

                    if (realityDownloadBizIdSb.length() > 0) {
                        queryParams.clear();
                        queryParamMap.clear();
                        queryParams.put("userId", (String) baseApp.getLoginUser().get("ids"));
                        queryParams.put("bizIds", realityDownloadBizIdSb.toString());
                        queryParams.put("queryParams", JSONObject.toJSONString(queryParamMap));

                        // 这里用来查询该业务对应的模板绑定表（t_bind_templateinfo）编号，因为该业务信息对应的模板和附件也保存在该目录下。
                        dataset = getTbFromServer_ok("sql_bind_templateinfo", queryParams);
                        dataValidFlag = (Boolean) dataset.get("dataValidFlag");
                        if (dataValidFlag) {
                            data = (ArrayList<HashMap<String, Object>>) dataset
                                    .get("data");

                            for (HashMap<String, Object> m : data) {
                                // 业务编号
                                String biz_id = (String) m.get("i");
                                // 业务信息保存目录
                                String temp_save = (String) m.get("s");

                                if (CommonUtil.checkNB(biz_id) && CommonUtil.checkNB(temp_save)) {
                                    ContentValues cv = new ContentValues();
                                    cv.put("temp_save", temp_save);

                                    infoTool.update("t_biz_sgxuns", cv, "ids=? and quid=?", new String[]{biz_id, (String) baseApp.getLoginUser().get("ids")});

                                    if (CommonUtil.checkNB(temp_save)) {
                                        File temp_dir = new File(Environment.getExternalStorageDirectory()
                                                .getAbsolutePath()
                                                + "/"
                                                + CommonParam.PROJECT_NAME
                                                + "/ins/"
                                                + temp_save);
                                        if (!temp_dir.exists()) {
                                            temp_dir.mkdir();
                                        }

                                        ArrayList<HashMap<String, Object>> bizList = (ArrayList<HashMap<String, Object>>) infoTool
                                                .getInfoMapList(
                                                        "select * from t_biz_sgxuns model where model.valid='1' and model.ids=? and model.quid=?",
                                                        new String[]{biz_id, (String) baseApp.getLoginUser().get("ids")});

                                        if (bizList.size() > 0) {
                                            HashMap<String, Object> bizInfo = bizList.get(0);
                                            String attachment = (String) bizInfo.get("attachment");
                                            if (CommonUtil.checkNB(attachment)) {
                                                JSONArray ps = JSONArray.parseArray(attachment);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    JSONObject o = ps.getJSONObject(i);
                                                    String _file = o.getString("file");
                                                    String _name = o.getString("name");
                                                    File attaFile = new File(temp_dir, _file);

                                                    if (!attaFile.exists()) {
                                                        attaName = _name;
                                                        publishProgress(dataDownloadDlg.getMax() - 1);
                                                        Map<String, Object> downloadResult = downloadFile("http://"
                                                                        + baseApp.serverAddr + "/UploadFiles/" + temp_save
                                                                        + "/" + _file,
                                                                temp_dir.getAbsolutePath() + "/" + _file, null);
                                                        String r = (String) downloadResult.get("result");
                                                        if (!CommonParam.RESULT_SUCCESS.equals(r)) {
                                                            throw new Exception("文件" + _file + "下载出错");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 下载巡视附件。结束======================================================================================

                    publishProgress(1);

                    result = CommonParam.RESULT_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();

                    // 删除旧表数据。开始==================================================================
                    db.delete("t_base_deptinfo", null, null);
                    db.delete("t_base_userinfo", null, null);
                    db.delete("t_base_code", null, null);
                    db.delete("t_szfgs_sgcategory", null, null);
                    db.delete("t_szfgs_sgres", null, null);
                    db.delete("t_szfgs_sgresareasign", null, null);
                    db.delete("t_szfgs_sgwxstage", null, null);
                    db.delete("t_szfgs_sgwxcat", null, null);
                    db.delete("t_szfgs_sgwxinfo", "quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                    db.delete("t_szfgs_sgwxrec", "quid=? and up='1'", new String[]{(String) baseApp.getLoginUser().get("ids")});
                    // 删除旧表数据。结束==================================================================
                }

                // 重建数据库
                db.execSQL("VACUUM");

                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    // 更新当前用户信息。开始==================================================================
                    HashMap<String, Object> user = null;
                    ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                            "select * from t_base_userinfo model where model.valid='1' and model.ids=?",
                            new String[]{(String) baseApp.loginUser.get("ids")});
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
                        baseApp.rememberFlag = true;// remember_user.isChecked();
                        if (baseApp.rememberFlag) {
                            String userStr = JSONObject.toJSONString(user);
                            preferEditor.putString("loginUser", userStr);
                        } else {
                            preferEditor.putString("loginUser", "");
                        }
                        preferEditor.putBoolean("rememberFlag", baseApp.rememberFlag);
                        preferEditor.commit();
                        result = CommonParam.RESULT_SUCCESS;
                    } else {
                        result = CommonParam.RESULT_LOGIN;
                    }
                    // 更新当前用户信息。结束==================================================================

                    // 更新其他信息。开始==================================================================
                    String ins_distance_str = infoTool.getSingleVal(
                            "select model.zdname from t_base_code model where model.valid='1' and model.type='偏关分公司_水工巡视参数' and model.zdcode='水工巡视定位有效距离'",
                            new String[]{});
                    if (CommonUtil.checkNB(ins_distance_str)) {
                        int ins_distance = -1;
                        try {
                            ins_distance = Integer.parseInt(ins_distance_str);
                        } catch (Exception e) {
                            ins_distance = -1;
                        }
                        if (ins_distance != -1) {
                            CommonParam.SYSCONFIG_VALUE_INS_DISTANCE = ins_distance;
                            preferEditor.putInt("SYSCONFIG_VALUE_INS_DISTANCE", ins_distance);
                            preferEditor.commit();
                        }
                    }
                    Log.d("#dis", ins_distance_str + ":" + CommonParam.SYSCONFIG_VALUE_INS_DISTANCE);
                    // 更新其他信息。结束==================================================================
                }

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
                dataDownloadDlg.setProgress(progress[0]);
                dataDownloadDlg.setMessage(getString(R.string.alert_data_download_message));
            } else if (progress[0] < (dataDownloadDlg.getMax() - 1)) {
                dataDownloadDlg.setProgress(progress[0]);
                dataDownloadDlg.setMessage(getString(R.string.alert_data_download_message));
            } else if (progress[0] == (dataDownloadDlg.getMax() - 1)) {
                dataDownloadDlg.setProgress(progress[0]);
                dataDownloadDlg.setMessage(getString(R.string.alert_data_download_message_file, attaName));
            } else if (progress[0] == dataDownloadDlg.getMax()) {
                dataDownloadDlg.setProgress(dataDownloadDlg.getMax());
                dataDownloadDlg.setMessage(getString(R.string.alert_data_download_done));
            } else if (progress[0] >= dataDownloadDlg.getMax()) {
                dataDownloadDlg.setProgress(0);
                dataDownloadDlg.dismiss();
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
                show(R.string.alert_data_download_success);
            } else if (CommonParam.RESULT_LOGIN.equals(result)) {
                dataDownloadDlg.setProgress(0);
                dataDownloadDlg.dismiss();
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MeLoginActivity.class);
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putString("fromFlag", "me_main");
                // 将数据存入Intent中
                intent.putExtras(data);
                startActivity(intent);
                finish();
            } else {
                show(R.string.alert_data_download_fail);

                dataDownloadDlg.setProgress(0);
                dataDownloadDlg.dismiss();
            }
        }
    }

    /**
     * 显示数据上传提示对话框
     */
    public void makeDataUploadConfirmDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.title_bar_m4);
        dlgBuilder.setMessage(R.string.alert_data_upload_confirm);
        dlgBuilder.setIcon(R.drawable.item_icon_upload);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeDataUploadDialog();
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
     * 显示数据上传进度对话框
     */
    public void makeDataUploadDialog() {
        if (!checkNet()) {
            return;
        }

        insTool = getInsTool();

        if (dataUploadDlg == null) {
            dataUploadDlg = new ProgressDialog(this);
            dataUploadDlg.setTitle(R.string.title_bar_m4);
            dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message));
            dataUploadDlg.setMax(CommonParam.PROGRESS_MAX);
            dataUploadDlg.setCancelable(false);
            dataUploadDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dataUploadDlg.setProgress(0);
            dataUploadDlg.setIndeterminate(true);
            dataUploadDlg.setIcon(R.drawable.item_icon_upload);

            dataUploadDlg.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dataUploadDlg.cancel();
                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });
        }
        dataUploadDlg.show();

        new DataUploadTask_ok().execute();
    }

    /**
     * 数据上传 AsyncTask 类
     */
    private class DataUploadTask_ok extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：生成巡视结果数据包
         */
        private static final int PROGRESS_MAKE_INS_DATA = 0;
        /**
         * 进度常量：正在上传
         */
        private static final int PROGRESS_UPLOADING = 1;
        /**
         * 进度常量：正在上传数据库
         */
        private static final int PROGRESS_UPLOADING_DB = 2;
        /**
         * 进度常量：正在上传多媒体附件
         */
        private static final int PROGRESS_UPLOADING_MULTIMEDIA = 3;
        /**
         * 进度常量：更新本机数据
         */
        private static final int PROGRESS_UPDATE_LOCAL_DATA = 4;
        /**
         * 进度常量：正在删除业务附件
         */
        private static final int PROGRESS_DELETING_BIZ_MULTIMEDIA = 5;
        /**
         * 进度常量：正在删除多媒体附件
         */
        private static final int PROGRESS_DELETING_MULTIMEDIA = 6;

        /**
         * 上传的数据库
         */
        private SQLiteDatabase uploadDb;
        /**
         * 操作巡视信息的工具类
         */
        private InsTool uploadInsTool;

        /**
         * 文件数量
         */
        private int fileTotal = 0;
        /**
         * 当前的文件序号
         */
        private int fileNow = 0;
        /**
         * 当前上传的记录类型名称
         */
        private String infoTypeName = "";

        @Override
        protected void onPreExecute() {
            isUploading = true;
            infoTool = getInfoTool();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... arg0) {
            String result = CommonParam.RESULT_ERROR;

            // 生成巡视结果数据包
            publishProgress(PROGRESS_MAKE_INS_DATA);

            // 上传数据库的id
            String dbId = CommonUtil.GetNextID();

            // 巡视附件目录
            String insDirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/ins";
            // 上传数据库模板文件
            File modelFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/model/result.db");
            // 上传数据库文件
            File uploadFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/upload/" + dbId + ".db");

            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。开始=================
            try {
                // 将模板拷贝到update目录
                new FileUtil().copyFile(modelFile.getAbsolutePath(), uploadFile.getAbsolutePath());
                uploadDb = DbTool.getDb(uploadFile);
                // 保存上传数据库是否成功
                boolean saveUploadDbFlag = false;
                // 上传附件是否成功
                boolean uploadAttaFlag = true;
                // 巡视业务结果信息
                ArrayList<HashMap<String, Object>> bizInsList = null;
                // 巡视检查记录信息
                ArrayList<HashMap<String, Object>> jcList = null;
                // 巡视检查记录子表信息
                ArrayList<HashMap<String, Object>> jcSonList = null;
                // 巡视打卡记录信息
                ArrayList<HashMap<String, Object>> dkList = null;
                // 项目记录信息
                ArrayList<HashMap<String, Object>> recList = null;
                // 资源采集定位记录信息
                ArrayList<HashMap<String, Object>> locList = null;
                // 区域标识定位记录信息
                ArrayList<HashMap<String, Object>> areaSignList = null;
                // 进行操作的巡视业务信息编号
                StringBuffer insBizIdsSb = new StringBuffer();
                // 进行操作的检查信息编号
                StringBuffer jcIdsSb = new StringBuffer();
                // 业务涉及到的资源编号List
                List<String> resIdList = new ArrayList<String>();
                StringBuffer resIdSb = new StringBuffer();

                if (uploadDb != null) {
                    uploadInsTool = new InsTool(classThis, new DbTool(classThis, uploadDb));

                    // 启动事务
                    db.beginTransaction();

                    try {
                        // 查询表t_biz_sgxuns。开始=====================================================
                        // 已经巡视完成的线路编号
                        List<String> eBizIdsList = new ArrayList<String>();

                        // 当前APP中已经完成，但尚未上传的业务信息
                        ArrayList<HashMap<String, Object>> existBizList = (ArrayList<HashMap<String, Object>>) infoTool
                                .getInfoMapList(
                                        "select model.ids ids, model.res_id res_id from t_biz_sgxuns model where model.valid='1' and (model.fzr=? or INSTR(model.ryap, ?)>0) and IFNULL(model.realatime,'')<>'' and IFNULL(model.realbtime,'')<>'' and model.up='0' and model.quid=?",
                                        new String[]{(String) baseApp.getLoginUser().get("ids"),
                                                (String) baseApp.getLoginUser().get("ids"),
                                                (String) baseApp.getLoginUser().get("ids")});
                        for (HashMap<String, Object> o : existBizList) {
                            String biz_id = (String) o.get("ids");
                            eBizIdsList.add(biz_id);
                        }
                        for (String s : eBizIdsList) {
                            insBizIdsSb.append(",'" + s + "'");
                        }
                        if (insBizIdsSb.length() > 0) {
                            insBizIdsSb.delete(0, 1);
                        }
                        // 查询表t_biz_sgxuns。结束=====================================================

                        // 写入"t_biz_sgxuns"表。开始================================================================
                        bizInsList = insTool.getInfoMapList(
                                "select * from t_biz_sgxuns model where model.valid='1' and model.ids in ("
                                        + insBizIdsSb.toString() + ") and model.quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                        for (HashMap<String, Object> map : bizInsList) {
                            // 键值对
                            ContentValues cv = CommonUtil.mapToCv(map);
                            // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                            uploadInsTool.insert("t_biz_sgxuns", cv);
                        }
                        // 写入"t_biz_sgxuns"表。结束================================================================

                        // 写入"t_szfgs_sgxunsjcjl"表。开始================================================================
                        jcList = insTool
                                .getInfoMapList(
                                        "select * from t_szfgs_sgxunsjcjl model where model.valid='1' and model.biz_id in ("
                                                + insBizIdsSb.toString()
                                                + ") and model.quid=? order by model.biz_id asc, DATETIME(model.atime) asc",
                                        new String[]{(String) baseApp.getLoginUser().get("ids")});
                        for (HashMap<String, Object> map : jcList) {
                            // 键值对
                            ContentValues cv = CommonUtil.mapToCv(map);
                            // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                            uploadInsTool.insert("t_szfgs_sgxunsjcjl", cv);

                            jcIdsSb.append(",'" + cv.getAsString("ids") + "'");
                        }

                        if (jcIdsSb.length() > 0) {
                            jcIdsSb.deleteCharAt(0);
                        }
                        // 写入"t_szfgs_sgxunsjcjl"表。结束================================================================

                        // 写入"t_szfgs_sgxunsjcjl_son"表。开始================================================================
                        jcSonList = insTool
                                .getInfoMapList(
                                        "select * from t_szfgs_sgxunsjcjl_son model where model.valid='1' and model.jcjl_id in ("
                                                + jcIdsSb.toString()
                                                + ") and model.quid=? order by xh asc",
                                        new String[]{(String) baseApp.getLoginUser().get("ids")});
                        for (HashMap<String, Object> map : jcSonList) {
                            // 键值对
                            ContentValues cv = CommonUtil.mapToCv(map);
                            // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                            uploadInsTool.insert("t_szfgs_sgxunsjcjl_son", cv);
                        }
                        // 写入"t_szfgs_sgxunsjcjl_son"表。结束================================================================

                        // 写入"t_szfgs_sgxunsqdjl"表。开始================================================================
                        dkList = insTool.getInfoMapList(
                                "select * from t_szfgs_sgxunsqdjl model where model.valid='1' and model.biz_id in ("
                                        + insBizIdsSb.toString()
                                        + ") and model.quid=? order by model.biz_id asc, DATETIME(model.ctime) asc",
                                new String[]{(String) baseApp.getLoginUser().get("ids")});
                        for (HashMap<String, Object> map : dkList) {
                            // 键值对
                            ContentValues cv = CommonUtil.mapToCv(map);
                            // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                            uploadInsTool.insert("t_szfgs_sgxunsqdjl", cv);
                        }
                        // 写入"t_szfgs_sgxunsqdjl"表。结束================================================================

                        // 写入"t_szfgs_sgwxrec"表。开始================================================================
                        recList = insTool.getInfoMapList(
                                "select * from t_szfgs_sgwxrec model where model.valid='1' and model.up='0' and model.quid=? order by DATETIME(model.ctime) asc",
                                new String[]{(String) baseApp.getLoginUser().get("ids")});
                        for (HashMap<String, Object> map : recList) {
                            // 键值对
                            ContentValues cv = CommonUtil.mapToCv(map);
                            // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                            uploadInsTool.insert("t_szfgs_sgwxrec", cv);
                        }
                        // 写入"t_szfgs_sgwxrec"表。结束================================================================

                        // 写入"t_szfgs_sgresloc"表。开始================================================================
                        for (HashMap<String, Object> o : existBizList) {
                            // 资源编号
                            String res_id = (String) o.get("res_id");
                            if (CommonUtil.checkNB(res_id)) {
                                String[] res_array = res_id.split(",");
                                for (String _id : res_array) {
                                    if (CommonUtil.checkNB(_id)) {
                                        if (!resIdList.contains(_id)) {
                                            resIdList.add(_id);
                                        }
                                    }
                                }
                            }
                        }
                        if (resIdList.size() > 0) {
                            for (String _id : resIdList) {
                                resIdSb.append(",'" + _id + "'");
                            }
                            if (resIdSb.length() > 0) {
                                resIdSb.deleteCharAt(0);
                            }
                            locList = insTool.getInfoMapList(
                                    "select * from t_szfgs_sgresloc model where model.valid='1' and model.res_id in ("
                                            + resIdSb.toString() + ") and model.uid=?",
                                    new String[]{(String) baseApp.getLoginUser().get("ids")});
                            for (HashMap<String, Object> map : locList) {
                                // 键值对
                                ContentValues cv = CommonUtil.mapToCv(map);
                                // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                                uploadInsTool.insert("t_szfgs_sgresloc", cv);
                            }
                        }
                        // 写入"t_szfgs_sgresloc"表。结束================================================================

                        // 写入"t_szfgs_sgresareasignloc"表。开始================================================================
                        areaSignList = insTool.getInfoMapList(
                                "select * from t_szfgs_sgresareasignloc model where model.valid='1'",
                                new String[]{});
                        for (HashMap<String, Object> map : areaSignList) {
                            // 键值对
                            ContentValues cv = CommonUtil.mapToCv(map);
                            // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                            uploadInsTool.insert("t_szfgs_sgresareasignloc", cv);
                        }
                        // 写入"t_szfgs_sgresareasignloc"表。结束================================================================

                        // 提交事务
                        db.setTransactionSuccessful();
                        saveUploadDbFlag = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // 结束事务
                        db.endTransaction();
                    }

                    // 关闭上传库
                    DbTool.closeDb(uploadDb);

                    if (saveUploadDbFlag) {
                        // 数据库生成成功
                        // 正在上传数据库
                        publishProgress(PROGRESS_UPLOADING_DB);

                        // 设置post值。开始=========================
                        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM);
                        multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                                .addFormDataPart("uploadType", "insdb")
                                .addFormDataPart("filedata", uploadFile.getName(), RequestBody.create(CommonParam.MEDIA_TYPE_BIN, uploadFile));
                        RequestBody requestBody = multipartBuilder.build();
                        // 设置post值。结束=========================

                        Request.Builder requestBuilder = new Request.Builder();
                        requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                        upHttpRequest = requestBuilder
                                .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_UPLOAD_INS_DB)
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
                            // 数据库上传成功
                            String respStr = upResponse.body().string();
                            Log.d("##", "#" + respStr);
                            JSONObject respJson = JSONObject.parseObject(respStr);
                            // 数据库是否上传成功
                            boolean dbUploadFlag = respJson.getBoolean("flag");
                            // 上传的数据库文件名
                            String dbUploadFileName = respJson.getString("fileName");
                            if (dbUploadFlag && uploadFile.getName().equals(dbUploadFileName)) {
                                // 上传成功，且服务器收到的是本机刚上传的数据库

                                // [{"ct":"2017-03-22 15:02:13",
                                // "lat":"0.0",
                                // "latb":"37.832922",
                                // "lng":"0.0",
                                // "lngb":"112.564733",
                                // "memo":"",
                                // "name":"170322150212740000387.jpg",
                                // "size":"123.4 KB","type":"PHOTO"
                                // },{"ct":"2017-03-22 15:02:23","lat":"0.0","latb":"37.832885","lng":"0.0","lngb":"112.564717","memo":"","name":"170322150222960000417.jpg","size":"338.0 KB","type":"PHOTO"}]

                                List<String> attaAllList, attaList;// , photoList, videoList, audioList;
                                attaAllList = new ArrayList<String>();
                                attaList = new ArrayList<String>();
                                // photoList = new ArrayList<String>();
                                // videoList = new ArrayList<String>();
                                // audioList = new ArrayList<String>();
                                // ＠＠＠上传巡视检查记录附件。开始===========================================================
                                if (bizInsList != null) {
                                    for (HashMap<String, Object> map : bizInsList) {
                                        String attachment = (String) map.get("atta");
                                        if (CommonUtil.checkNB(attachment)) {
                                            JSONObject atta = JSONObject.parseObject(attachment);
                                            if (atta != null) {
                                                String fileName = atta.getString("name");
                                                attaList.add(fileName);
                                            }
                                        }
                                    }
                                }
                                if (jcSonList != null) {
                                    infoTypeName = "〖现场检查记录〗";
                                    for (HashMap<String, Object> m : jcSonList) {
                                        // 图片
                                        String photo = (String) m.get("photo");
                                        // 视频
                                        String video = (String) m.get("video");
                                        // 音频
                                        String audio = (String) m.get("audio");

                                        if (CommonUtil.checkNB(photo)) {
                                            JSONArray ps = JSONArray.parseArray(photo);
                                            for (int i = 0, len = ps.size(); i < len; i++) {
                                                String fileName = ps.getJSONObject(i).getString("name");
                                                if (CommonUtil.checkNB(fileName)) {
                                                    attaList.add(fileName);
                                                }
                                            }
                                        }

                                        if (CommonUtil.checkNB(video)) {
                                            JSONArray ps = JSONArray.parseArray(video);
                                            for (int i = 0, len = ps.size(); i < len; i++) {
                                                String fileName = ps.getJSONObject(i).getString("name");
                                                if (CommonUtil.checkNB(fileName)) {
                                                    attaList.add(fileName);
                                                }
                                            }
                                        }

                                        if (CommonUtil.checkNB(audio)) {
                                            JSONArray ps = JSONArray.parseArray(audio);
                                            for (int i = 0, len = ps.size(); i < len; i++) {
                                                String fileName = ps.getJSONObject(i).getString("name");
                                                if (CommonUtil.checkNB(fileName)) {
                                                    attaList.add(fileName);
                                                }
                                            }
                                        }

                                        attaAllList.addAll(attaList);
                                    }

                                    fileTotal = attaList.size();
                                    for (int i = 0, len = attaList.size(); i < len; i++) {
                                        fileNow = i + 1;
                                        // 正在上传附件
                                        publishProgress(PROGRESS_UPLOADING_MULTIMEDIA);
                                        // 附件文件
                                        File upFile = new File(insDirName + "/" + attaList.get(i));
                                        // Log.d("file:", upFile.getAbsolutePath());
                                        if (upFile.exists()) {
                                            // 设置post值。开始=========================
                                            multipartBuilder = new MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM);
                                            multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                                                    .addFormDataPart("uploadType", "insattach")
                                                    .addFormDataPart("filedata", upFile.getName(), RequestBody.create(CommonParam.MEDIA_TYPE_BIN, upFile));
                                            requestBody = multipartBuilder.build();
                                            // 设置post值。结束=========================

                                            requestBuilder = new Request.Builder();
                                            requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                                            upHttpRequest = requestBuilder
                                                    .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_UPLOAD_INS_ATTACHMENT)
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
                                                String upRespStr = upResponse.body().string();
                                                Log.d("##", "#" + upRespStr);
                                                JSONObject upRespJson = JSONObject.parseObject(upRespStr);
                                                // 附件是否上传成功
                                                boolean attaUploadFlag = upRespJson.getBoolean("flag");
                                                if (attaUploadFlag) {
                                                    // 上传成功
                                                } else {
                                                    uploadAttaFlag = false;
                                                    break;
                                                }
                                            } else {
                                                uploadAttaFlag = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                // 上传巡视检查记录附件。结束===========================================================

                                // ＠＠＠上传巡视打卡记录附件。开始===========================================================
                                attaList.clear();
                                fileTotal = 0;
                                fileNow = 0;
                                if (uploadAttaFlag) {
                                    if (dkList != null) {
                                        infoTypeName = "〖签到打卡记录〗";
                                        // photoList = new ArrayList<String>();
                                        // videoList = new ArrayList<String>();
                                        // audioList = new ArrayList<String>();
                                        for (HashMap<String, Object> m : dkList) {
                                            // 图片
                                            String photo = (String) m.get("photo");
                                            // 视频
                                            String video = (String) m.get("video");
                                            // 音频
                                            String audio = (String) m.get("audio");

                                            if (CommonUtil.checkNB(photo)) {
                                                JSONArray ps = JSONArray.parseArray(photo);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    String fileName = ps.getJSONObject(i).getString("name");
                                                    if (CommonUtil.checkNB(fileName)) {
                                                        attaList.add(fileName);
                                                    }
                                                }
                                            }

                                            if (CommonUtil.checkNB(video)) {
                                                JSONArray ps = JSONArray.parseArray(video);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    String fileName = ps.getJSONObject(i).getString("name");
                                                    if (CommonUtil.checkNB(fileName)) {
                                                        attaList.add(fileName);
                                                    }
                                                }
                                            }

                                            if (CommonUtil.checkNB(audio)) {
                                                JSONArray ps = JSONArray.parseArray(audio);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    String fileName = ps.getJSONObject(i).getString("name");
                                                    if (CommonUtil.checkNB(fileName)) {
                                                        attaList.add(fileName);
                                                    }
                                                }
                                            }

                                            attaAllList.addAll(attaList);
                                        }

                                        fileTotal = attaList.size();
                                        for (int i = 0, len = attaList.size(); i < len; i++) {
                                            fileNow = i + 1;
                                            // 正在上传附件
                                            publishProgress(PROGRESS_UPLOADING_MULTIMEDIA);
                                            // 附件文件
                                            File upFile = new File(insDirName + "/" + attaList.get(i));
                                            // Log.d("file:", upFile.getAbsolutePath());
                                            if (upFile.exists()) {
                                                // 设置post值。开始=========================
                                                multipartBuilder = new MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM);
                                                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                                                        .addFormDataPart("uploadType", "insattach")
                                                        .addFormDataPart("filedata", upFile.getName(), RequestBody.create(CommonParam.MEDIA_TYPE_BIN, upFile));
                                                requestBody = multipartBuilder.build();
                                                // 设置post值。结束=========================

                                                requestBuilder = new Request.Builder();
                                                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                                                upHttpRequest = requestBuilder
                                                        .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_UPLOAD_INS_ATTACHMENT)
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
                                                    String upRespStr = upResponse.body().string();
                                                    // Log.d("##", "#" + upRespStr);
                                                    JSONObject upRespJson = JSONObject.parseObject(upRespStr);
                                                    // 附件是否上传成功
                                                    boolean attaUploadFlag = upRespJson.getBoolean("flag");
                                                    if (attaUploadFlag) {
                                                        // 上传成功
                                                    } else {
                                                        uploadAttaFlag = false;
                                                        break;
                                                    }
                                                } else {
                                                    uploadAttaFlag = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                // 上传巡视打卡记录附件。结束===========================================================

                                // ＠＠＠上传项目记录附件。开始===========================================================
                                attaList.clear();
                                fileTotal = 0;
                                fileNow = 0;
                                if (uploadAttaFlag) {
                                    if (recList != null) {
                                        infoTypeName = "〖水工维修项目记录〗";
                                        // photoList = new ArrayList<String>();
                                        // videoList = new ArrayList<String>();
                                        // audioList = new ArrayList<String>();
                                        for (HashMap<String, Object> m : recList) {
                                            // 图片
                                            String photo = (String) m.get("photo");
                                            // 视频
                                            String video = (String) m.get("video");
                                            // 音频
                                            String audio = (String) m.get("audio");

                                            if (CommonUtil.checkNB(photo)) {
                                                JSONArray ps = JSONArray.parseArray(photo);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    String fileName = ps.getJSONObject(i).getString("name");
                                                    if (CommonUtil.checkNB(fileName)) {
                                                        attaList.add(fileName);
                                                    }
                                                }
                                            }

                                            if (CommonUtil.checkNB(video)) {
                                                JSONArray ps = JSONArray.parseArray(video);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    String fileName = ps.getJSONObject(i).getString("name");
                                                    if (CommonUtil.checkNB(fileName)) {
                                                        attaList.add(fileName);
                                                    }
                                                }
                                            }

                                            if (CommonUtil.checkNB(audio)) {
                                                JSONArray ps = JSONArray.parseArray(audio);
                                                for (int i = 0, len = ps.size(); i < len; i++) {
                                                    String fileName = ps.getJSONObject(i).getString("name");
                                                    if (CommonUtil.checkNB(fileName)) {
                                                        attaList.add(fileName);
                                                    }
                                                }
                                            }

                                            attaAllList.addAll(attaList);
                                        }

                                        fileTotal = attaList.size();
                                        for (int i = 0, len = attaList.size(); i < len; i++) {
                                            fileNow = i + 1;
                                            // 正在上传附件
                                            publishProgress(PROGRESS_UPLOADING_MULTIMEDIA);
                                            // 附件文件
                                            File upFile = new File(insDirName + "/" + attaList.get(i));
                                            // Log.d("file:", upFile.getAbsolutePath());
                                            if (upFile.exists()) {
                                                // 设置post值。开始=========================
                                                multipartBuilder = new MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM);
                                                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                                                        .addFormDataPart("uploadType", "insattach")
                                                        .addFormDataPart("filedata", upFile.getName(), RequestBody.create(CommonParam.MEDIA_TYPE_BIN, upFile));
                                                requestBody = multipartBuilder.build();
                                                // 设置post值。结束=========================

                                                requestBuilder = new Request.Builder();
                                                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                                                upHttpRequest = requestBuilder
                                                        .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_UPLOAD_INS_ATTACHMENT)
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
                                                    String upRespStr = upResponse.body().string();
                                                    // Log.d("##", "#" + upRespStr);
                                                    JSONObject upRespJson = JSONObject.parseObject(upRespStr);
                                                    // 附件是否上传成功
                                                    boolean attaUploadFlag = upRespJson.getBoolean("flag");
                                                    if (attaUploadFlag) {
                                                        // 上传成功
                                                    } else {
                                                        uploadAttaFlag = false;
                                                        break;
                                                    }
                                                } else {
                                                    uploadAttaFlag = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                // 上传项目记录附件。结束===========================================================

                                if (uploadAttaFlag) {
                                    // 正在更新本机数据
                                    publishProgress(PROGRESS_UPDATE_LOCAL_DATA);
                                    // 发出完成上传指令
                                    // 设置post值。开始=========================
                                    multipartBuilder = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM);
                                    multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                                            .addFormDataPart("cmd", CommonParam.URLCOMMAND_FINISH_UP_INS)
                                            .addFormDataPart("fileName", uploadFile.getName())
                                            .addFormDataPart("userId", (String) baseApp.loginUser.get("ids"));
                                    requestBody = multipartBuilder.build();
                                    // 设置post值。结束=========================

                                    requestBuilder = new Request.Builder();
                                    requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                                    upHttpRequest = requestBuilder
                                            .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_COMMAND)
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
                                        String impCmdRespStr = upResponse.body().string();
                                        Log.d("##", "#" + impCmdRespStr);

                                        JSONObject impCmdRespJson = JSONObject.parseObject(impCmdRespStr);
                                        // 指令是否执行
                                        String impCmdResult = impCmdRespJson.getString("result");

                                        if (CommonParam.RESPONSE_SUCCESS.equals(impCmdResult)) {
                                            // 请求正确
                                            // 指令是否执行成功
                                            boolean impCmdResultFlag = impCmdRespJson.getBoolean("data");

                                            if (impCmdResultFlag) {
                                                // 指令执行成功
                                                Log.d("##", "开始更新表格=============");
                                                // 更新表格数据。开始==================================================================
                                                ContentValues cv = new ContentValues();
                                                cv.put("up", "1");

                                                infoTool.update("t_biz_sgxuns", cv,
                                                        "ids in (" + insBizIdsSb.toString() + ") and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                // 更新表格数据。结束==================================================================

                                                // 删除旧表数据。开始==================================================================
                                                db.delete("t_biz_sgxuns", "ids in (" + insBizIdsSb.toString() + ") and quid=?",
                                                        new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                db.delete("t_szfgs_sgxunsjcjl", "biz_id in (" + insBizIdsSb.toString()
                                                        + ") and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                db.delete("t_szfgs_sgxunsjcjl_son", "jcjl_id in (" + jcIdsSb.toString()
                                                        + ") and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                db.delete("t_szfgs_sgxunsqdjl", "biz_id in (" + insBizIdsSb.toString()
                                                        + ") and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                db.delete("t_szfgs_sgwxrec", "up='0' and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                db.delete("t_szfgs_sgxunsloc", "biz_id in (" + insBizIdsSb.toString()
                                                        + ") and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
                                                if (resIdList.size() > 0) {
                                                    db.delete("t_szfgs_sgresloc", "res_id in ("
                                                            + resIdSb.toString() + ")", null);
                                                }
                                                db.delete("t_szfgs_sgresareasignloc", "", new String[]{});
                                                // 删除旧表数据。结束==================================================================
                                                // 重建数据库
                                                db.execSQL("VACUUM");

                                                Log.d("##", "开始删除附件=============");
                                                // 删除巡视业务附件。开始==================================================================
                                                if (bizInsList != null) {
                                                    for (HashMap<String, Object> map : bizInsList) {
                                                        String attachment = (String) map.get("attachment");
                                                        if (CommonUtil.checkNB(attachment)) {
                                                            JSONArray ps = JSONArray.parseArray(attachment);
                                                            String temp_save = (String) map.get("temp_save");
                                                            File temp_save_dir = new File(Environment
                                                                    .getExternalStorageDirectory().getAbsolutePath()
                                                                    + "/"
                                                                    + CommonParam.PROJECT_NAME
                                                                    + "/ins/"
                                                                    + temp_save);
                                                            if (temp_save_dir.exists() && temp_save_dir.isDirectory()) {
                                                                // 正在删除业务附件
                                                                publishProgress(PROGRESS_DELETING_BIZ_MULTIMEDIA);
                                                                File[] attaFiles = temp_save_dir.listFiles();
                                                                for (File attaFile : attaFiles) {
                                                                    if (attaFile.exists() && attaFile.isFile()) {
                                                                        attaFile.delete();
                                                                    }
                                                                }
                                                                temp_save_dir.delete();
                                                            }
                                                        }
                                                    }
                                                }
                                                // 删除巡视业务附件。结束==================================================================

                                                // 删除附件。开始==================================================================
                                                fileTotal = attaAllList.size();
                                                fileNow = 0;
                                                for (int i = 0, len = attaAllList.size(); i < len; i++) {
                                                    fileNow = i + 1;
                                                    // 正在删除附件
                                                    publishProgress(PROGRESS_DELETING_MULTIMEDIA);
                                                    // 附件文件
                                                    File upFile = new File(insDirName + "/" + attaAllList.get(i));
                                                    if (upFile.exists() && upFile.isFile()) {
                                                        upFile.delete();
                                                    }
                                                }
                                                // 删除附件。结束==================================================================

                                                publishProgress(CommonParam.PROGRESS_MAX);
                                                doWait(500);
                                                publishProgress(CommonParam.PROGRESS_MAX + 1);
                                                result = CommonParam.RESULT_SUCCESS;
                                            } else {
                                                // 指令执行失败
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // 数据库上传失败
                        }
                    } else {
                        // 数据库生成失败
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (uploadDb != null) {
                    DbTool.closeDb(uploadDb);
                }
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
            if (progress[0] == PROGRESS_MAKE_INS_DATA) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message_package));
            } else if (progress[0] == PROGRESS_UPLOADING) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message));
            } else if (progress[0] == PROGRESS_UPLOADING_DB) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message_db));
            } else if (progress[0] == PROGRESS_UPLOADING_MULTIMEDIA) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message_atta_type_n, infoTypeName,
                        fileNow, fileTotal));
            } else if (progress[0] == PROGRESS_UPDATE_LOCAL_DATA) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_update_local_data));
            } else if (progress[0] == PROGRESS_DELETING_BIZ_MULTIMEDIA) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message_delete_biz_atta));
            } else if (progress[0] == PROGRESS_DELETING_MULTIMEDIA) {
                dataUploadDlg.setProgress(progress[0]);
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_message_delete_atta_n, fileNow,
                        fileTotal));
            } else if (progress[0] == dataUploadDlg.getMax()) {
                dataUploadDlg.setProgress(dataUploadDlg.getMax());
                dataUploadDlg.setMessage(getString(R.string.alert_data_upload_done));
            } else if (progress[0] >= dataUploadDlg.getMax()) {
                dataUploadDlg.setProgress(0);
                dataUploadDlg.dismiss();
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
                show(R.string.alert_data_upload_success);
                makeDataDownloadDialog();
            } else {
                show(R.string.alert_data_upload_fail);
                dataUploadDlg.setProgress(0);
                dataUploadDlg.dismiss();
            }
            isUploading = false;
        }
    }

    /**
     * 检查业务是否可以上传 AsyncTask 类
     */
    private class CheckBeforeDataUpTask_ok extends AsyncTask<Object, Integer, String> {
        JSONObject result_data = null;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog();
            infoTool = getInfoTool();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 处理数据。开始============================================================================
            // 进行操作的巡视业务信息编号
            StringBuffer insBizIdsSb = new StringBuffer();

            // 查询表t_biz_sgxuns。开始=====================================================
            // 已经巡视完成的巡视业务编号
            List<String> eBizIdsList = new ArrayList<String>();

            // 当前APP中已经完成，但尚未上传的业务信息
            ArrayList<HashMap<String, Object>> existBizList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "select model.ids ids from t_biz_sgxuns model where model.valid='1' and (model.fzr=? or INSTR(model.ryap, ?)>0) and IFNULL(model.realatime,'')<>'' and IFNULL(model.realbtime,'')<>'' and model.up='0' and model.quid=?",
                            new String[]{(String) baseApp.getLoginUser().get("ids"),
                                    (String) baseApp.getLoginUser().get("ids"),
                                    (String) baseApp.getLoginUser().get("ids")});
            for (HashMap<String, Object> o : existBizList) {
                String biz_id = (String) o.get("ids");
                eBizIdsList.add(biz_id);
            }
            for (String s : eBizIdsList) {
                insBizIdsSb.append("," + s);
            }
            if (insBizIdsSb.length() > 0) {
                insBizIdsSb.delete(0, 1);
            }
            // 查询表t_biz_sgxuns。结束=====================================================
            // 处理数据。结束============================================================================

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
                // String userId = (String) baseApp.loginUser.get("ids");

                JSONObject queryParams = new JSONObject();
                queryParams.put("xsIds", insBizIdsSb.toString());
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                        .addFormDataPart("infoType", "m_check_before_ins_up")
                        .addFormDataPart("queryParams", queryParams.toJSONString());
                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_SEARCHTABLE)
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
                        result_data = respJson.getJSONObject("data");

                        if ("1".equals(result_data.getString("xs_status"))) {
                            result = CommonParam.RESULT_SUCCESS;
                        }
                    }
                } else {
                    // 服务器连接失败
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
            // {
            // "result" : "1",
            // "data" : {
            // "e" :
            // "★ 10kV下分线 T4050（2017-09-15 08:00，线路所_朱继科）［已办结］\n★ 110kV方南二Ⅰ线  119-163（2018-09-10 08:00，线路所_付贵虎）［在第1环节］\n",
            // "status" : "0",
            // "ec" : 2
            // }
            // }
            // 隐藏等待窗口
            unWait();

            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                // makeAlertDialog("开始上传");
                makeDataUploadConfirmDialog();
            } else {
                makeAlertDialog("有" + (result_data.getIntValue("xs_ec") + result_data.getIntValue("jx_ec"))
                        + "个任务有错误，导致无法上传。请先解决错误后再上传。\n\n" + result_data.getString("e"));
            }
        }
    }

    /**
     * 显示设置UHF功率对话框
     */
    public void makeSetUhfPowerDialog() {
        Builder dlgBuilder = new Builder(this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_uhf_power, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("UHF天线功率");
        // dlgBuilder.setIcon(R.drawable.ic_dialog_uhf_v);
        dlgBuilder.setCancelable(true);

        // 当前功率
        final TextView currentNumTv_nav = (TextView) layout.findViewById(R.id.currentNumTv);
        // 最大功率
        final TextView totalNumTv_nav = (TextView) layout.findViewById(R.id.totalNumTv);
        // 拖动条
        SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
        int currentNum = (int) baseApp.uhfPdaInfo.get("power");

        currentNumTv_nav.setText("" + currentNum);
        totalNumTv_nav.setText("" + CommonParam.UHF_ANTENNA_POWER_MAX);
        seekBar.setMax(CommonParam.UHF_ANTENNA_POWER_MAX);
        seekBar.setProgress(currentNum);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentNumTv_nav.setText("" + progress);
            }
        });

        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        uhfPowerDlg = dlgBuilder.create();
        uhfPowerDlg.show();

        // 确定按钮
        Button confirmBtn = uhfPowerDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 绑定数据
        // confirmBtn.setTag();

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                uhfPowerDlg.cancel();
                int preNum = Integer.parseInt(me_uhfPower_btn.getText().toString());
                int currentNum = Integer.parseInt(currentNumTv_nav.getText().toString());
                Log.d("###power:", "" + currentNum);
                if (preNum == currentNum) {
                    // 如果值和修改前一样，就不用修改
                    show("功率修改成功");
                } else {
                    // 如果值和修改前不一样，就需要真正修改
                    try {
                        PowerUtil.power("1");
                        connectUhf();
                        Reader.rrlib.SetRfPower(currentNum);
                        getUhfInfo();
                        baseApp.uhfPower = (int) baseApp.uhfPdaInfo.get("power");
                        preferEditor.putInt("uhfPower", baseApp.uhfPower);
                        preferEditor.commit();
                        me_uhfPower_btn.setText("" + baseApp.uhfPower);
                        disconnectUhf();
                        PowerUtil.power("0");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 显示设置UHF扫卡快签信息保存间隔对话框
     */
    public void makeSetAutoDkOverTimeDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_autodk_overtime, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.autoDkOverTimeTitle);
        // dlgBuilder.setIcon(R.drawable.ic_dialog_uhf_v);
        dlgBuilder.setCancelable(true);

        // 附件名称
        final EditText info_tv = (EditText) layout.findViewById(R.id.info_tv);
        info_tv.setText("" + baseApp.autoDkOverTime);

        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        audoDkOverTimeDlg = dlgBuilder.create();
        audoDkOverTimeDlg.show();

        // 确定按钮
        Button confirmBtn = audoDkOverTimeDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 绑定数据
        // confirmBtn.setTag();

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean submitFlag = false;
                String errorMsg = "";

                String num_str = info_tv.getText().toString();
                int num = -1;
                try {
                    num = Integer.parseInt(num_str);
                } catch (Exception e) {
                    num = -1;
                }

                if (!CommonUtil.checkNB(num_str)) {
                    errorMsg = "请输入数字！";
                } else if (num == -1) {
                    errorMsg = "请输入正整数！";
                } else if (num < 10) {
                    errorMsg = "值不能小于10！";
                } else {
                    submitFlag = true;
                }

                if (!submitFlag) {
                    // 不能提交
                    if (CommonUtil.checkNB(errorMsg)) {
                        show(errorMsg);
                    }
                } else {
                    baseApp.autoDkOverTime = num;
                    preferEditor.putInt("autoDkOverTime", baseApp.autoDkOverTime);
                    preferEditor.commit();
                    me_autoDkOverTime_btn.setText("" + baseApp.autoDkOverTime);
                    audoDkOverTimeDlg.cancel();
                }
            }
        });
    }

    /**
     * 显示设置UHF扫卡快签信息保留时间对话框
     */
    public void makeSetAutoDkStayTimeDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_autodk_staytime, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.autoDkStayTimeTitle);
        // dlgBuilder.setIcon(R.drawable.ic_dialog_uhf_v);
        dlgBuilder.setCancelable(true);

        // 附件名称
        final EditText info_tv = (EditText) layout.findViewById(R.id.info_tv);
        info_tv.setText("" + baseApp.autoDkStayTime);

        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        audoDkStayTimeDlg = dlgBuilder.create();
        audoDkStayTimeDlg.show();

        // 确定按钮
        Button confirmBtn = audoDkStayTimeDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 绑定数据
        // confirmBtn.setTag();

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean submitFlag = false;
                String errorMsg = "";

                String num_str = info_tv.getText().toString();
                int num = -1;
                try {
                    num = Integer.parseInt(num_str);
                } catch (Exception e) {
                    num = -1;
                }

                if (!CommonUtil.checkNB(num_str)) {
                    errorMsg = "请输入数字！";
                } else if (num == -1) {
                    errorMsg = "请输入正整数！";
                } else if (num < 5) {
                    errorMsg = "值不能小于5！";
                } else {
                    submitFlag = true;
                }

                if (!submitFlag) {
                    // 不能提交
                    if (CommonUtil.checkNB(errorMsg)) {
                        show(errorMsg);
                    }
                } else {
                    baseApp.autoDkStayTime = num;
                    preferEditor.putInt("autoDkStayTime", baseApp.autoDkStayTime);
                    preferEditor.commit();
                    me_autoDkStayTime_btn.setText("" + baseApp.autoDkStayTime);
                    audoDkStayTimeDlg.cancel();
                }
            }
        });
    }

    /**
     * 显示设置UHF开关按键对话框
     */
    public void makeSetUhfKeyCodeDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_uhf_keycode, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.uhfKeyCodeTitle);
        // dlgBuilder.setIcon(R.drawable.ic_dialog_uhf_v);
        dlgBuilder.setCancelable(true);

        // 当前按键编码
        final TextView info_tv = (TextView) layout.findViewById(R.id.info_tv);

        info_tv.setText("" + baseApp.uhfKeyCode);

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

        uhfKeyCodeDlg = dlgBuilder.create();
        uhfKeyCodeDlg.show();

        // 确定按钮
        Button confirmBtn = uhfKeyCodeDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = uhfKeyCodeDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean submitFlag = false;
                String errorMsg = "";

                String num_str = info_tv.getText().toString();
                int num = -1;
                try {
                    num = Integer.parseInt(num_str);
                } catch (Exception e) {
                    num = -1;
                }

                if (num == -1) {
                    errorMsg = "请点击按键！";
                } else {
                    submitFlag = true;
                }

                if (!submitFlag) {
                    // 不能提交
                    if (CommonUtil.checkNB(errorMsg)) {
                        show(errorMsg);
                    }
                } else {
                    baseApp.uhfKeyCode = num;
                    preferEditor.putInt("uhfKeyCode", baseApp.uhfKeyCode);
                    preferEditor.commit();
                    me_uhfKeyCode_btn.setText("" + baseApp.uhfKeyCode);
                    uhfKeyCodeDlg.cancel();
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uhfKeyCodeDlg.cancel();
            }
        });

        uhfKeyCodeDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                boolean flag = false;
                // 点击PDA上的红色按钮会开始扫描/停止扫描
                // 这里加上了event.getAction() == KeyEvent.ACTION_UP。如果不加，会响应两次
                // 开关的监听设置在这里，是因为弹出AlertDialog后，由AlertDialog来监听按键，Activity是监听不到的。
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    info_tv.setText("" + keyCode);
                    flag = true;
                }
                return flag;
            }
        });
    }

    // UHF相关方法。开始============================================

    /**
     * 获得UHF基本信息
     */
    public Map<String, Object> getUhfInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        byte[] Version = new byte[2];
        byte[] Power = new byte[1];
        byte[] band = new byte[1];
        byte[] MaxFre = new byte[1];
        byte[] MinFre = new byte[1];
        byte[] BeepEn = new byte[1];
        byte[] Ant = new byte[1];

        if (baseApp.isUhfPda) {
            int result = Reader.rrlib.GetUHFInformation(Version, Power, band, MaxFre, MinFre, BeepEn, Ant);
            if (result == 0) {
                String hvn = String.valueOf(Version[0]);
                if (hvn.length() == 1) hvn = "0" + hvn;
                String lvn = String.valueOf(Version[1]);
                if (lvn.length() == 1) lvn = "0" + lvn;
                int bandindex = band[0];
                if (bandindex == 8) {
                    bandindex = bandindex - 4;
                } else {
                    bandindex = bandindex - 1;
                }

                map.put("version", hvn + "." + lvn);
                map.put("power", (int) Power[0]);
                map.put("fre", (int) band[0]);
                map.put("band", bandindex);
                map.put("minFrm", (int) MinFre[0]);
                map.put("maxFrm", (int) MaxFre[0]);
            }
        }
        baseApp.uhfPdaInfo = map;

        return map;
    }

    /**
     * 连接UHF模块
     */
    private void connectUhf() {
        try {
            int result = Reader.rrlib.Connect("/dev/ttyS2", CommonParam.UHF_BAUD);
            if (result == 0) {
            } else {
                String result_hex = Integer.toHexString(result).toUpperCase(Locale.CHINA);
                result_hex = String.format(result_hex.length() > 1 ? "RV_%s" : "RV_0%s", result_hex);
                int result_string_int = CommonUtil.getFieldValue("string", result_hex, this);
                if (result_string_int != -1) {

                } else {

                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 断开UHF模块
     */
    private void disconnectUhf() {
        try {
            if (Reader.rrlib.IsConnected()) {
                int result = Reader.rrlib.DisConnect();
                if (result == 0) {

                } else {
                    String result_hex = Integer.toHexString(result).toUpperCase(Locale.CHINA);
                    result_hex = String.format(result_hex.length() > 1 ? "RV_%s" : "RV_0%s", result_hex);
                    int result_string_int = CommonUtil.getFieldValue("string", result_hex, this);
                    if (result_string_int != -1) {

                    } else {

                    }
                }
            }
        } catch (Exception e) {

        }
    }
    // UHF相关方法。结束============================================

    /**
     * 查找view
     */
    public void findViews() {
        titleText = (TextView) findViewById(R.id.title_text_view);
        refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
        // 界面相关参数。开始===============================
        userImage = (ImageView) findViewById(R.id.userImage);
        newsItemTitle = (TextView) findViewById(R.id.newsItemTitle);
        newsItemAccount = (TextView) findViewById(R.id.newsItemAccount);
        // newsItemPro = (TextView) findViewById(R.id.newsItemPro);

        nav_main_layout = (LinearLayout) findViewById(R.id.nav_main_layout);
        nav_config_layout = (LinearLayout) findViewById(R.id.nav_config_layout);
        // me_feedback_layout = (LinearLayout) findViewById(R.id.me_feedback_layout);
        // me_info_layout = (LinearLayout) findViewById(R.id.me_info_layout);
        // me_sysParam_layout = (LinearLayout) findViewById(R.id.me_sysParam_layout);
        // me_changePwd_layout = (LinearLayout) findViewById(R.id.me_changePwd_layout);
        // me_checkUpdate_layout = (LinearLayout) findViewById(R.id.me_checkUpdate_layout);
        // me_resetDb_layout = (LinearLayout) findViewById(R.id.me_resetDb_layout);
        // me_serverAddr_layout = (LinearLayout) findViewById(R.id.me_serverAddr_layout);
        // me_uploadTestData_layout = (LinearLayout) findViewById(R.id.me_uploadTestData_layout);
        // me_about_layout = (LinearLayout) findViewById(R.id.me_about_layout);
        me_logout_layout = (LinearLayout) findViewById(R.id.me_logout_layout);
        me_login_layout = (LinearLayout) findViewById(R.id.me_login_layout);

        // me_feedback_btn = (TextView) findViewById(R.id.me_feedback_btn);
        // me_info_btn = (TextView) findViewById(R.id.me_info_btn);
        // me_sysParam_btn = (TextView) findViewById(R.id.me_sysParam_btn);
        me_baseInfoSync_btn = (TextView) findViewById(R.id.me_baseInfoSync_btn);
        me_dataDownload_btn = (TextView) findViewById(R.id.me_dataDownload_btn);
        me_dataUpload_btn = (TextView) findViewById(R.id.me_dataUpload_btn);
        me_autoDownloadAtta_switch = (Switch) findViewById(R.id.me_autoDownloadAtta_switch);
        me_autoPlayInsAudio_switch = (Switch) findViewById(R.id.me_autoPlayInsAudio_switch);
        me_reverseRotate_switch = (Switch) findViewById(R.id.me_reverseRotate_switch);
        me_uhfPower_btn = (TextView) findViewById(R.id.me_uhfPower_btn);
        me_autoDkOverTime_btn = (TextView) findViewById(R.id.me_autoDkOverTime_btn);
        me_autoDkStayTime_btn = (TextView) findViewById(R.id.me_autoDkStayTime_btn);
        me_uhfKeyCode_btn = (TextView) findViewById(R.id.me_uhfKeyCode_btn);
        //me_changePwd_btn = (TextView) findViewById(R.id.me_changePwd_btn);
        me_clearCache_btn = (TextView) findViewById(R.id.me_clearCache_btn);
        me_resetDb_btn = (TextView) findViewById(R.id.me_resetDb_btn);
        me_serverAddr_btn = (TextView) findViewById(R.id.me_serverAddr_btn);
        me_checkUpdate_btn = (TextView) findViewById(R.id.me_checkUpdate_btn);
        me_uploadTestData_btn = (TextView) findViewById(R.id.me_uploadTestData_btn);
        me_downloadOtherApp_btn = (TextView) findViewById(R.id.me_downloadOtherApp_btn);
        me_about_btn = (TextView) findViewById(R.id.me_about_btn);
        me_logout_btn = (TextView) findViewById(R.id.me_logout_btn);
        me_login_btn = (TextView) findViewById(R.id.me_login_btn);
        // 界面相关参数。结束===============================
    }
}
