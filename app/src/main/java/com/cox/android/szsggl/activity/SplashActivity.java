/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.application.BaseApplication;
import com.cox.android.szsggl.tool.InfoTool;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;

import java.io.File;

/**
 * 闪屏页面
 *
 * @author 乔勇(Jacky Qiao)
 */
public class SplashActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 信息代码：隐藏进度条
     */
    private static final int MESSAGE_HIDE_PROBAR = 0x020;
    /**
     * 信息代码：显示进度条
     */
    private static final int MESSAGE_SHOW_PROBAR = 0x021;
    /**
     * 信息代码：显示登录窗口
     */
    private static final int MESSAGE_SHOW_LOGIN = 0x022;
    // 进度条相关的参数。开始===========================================
    /**
     * 信息代码：更新进度信息
     */
    private static final int MESSAGE_PROGRESS = 0x111;
    /**
     * 最大进度值
     */
    private static final int STATUS_MAX = 10;
    /**
     * 进度值
     */
    private static int status = 0;
    /**
     * 负责更新的进度的Handler
     */
    private final Handler proHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            if (msg.what == MESSAGE_PROGRESS) {
                if (status > STATUS_MAX) {
                    // proBar.setProgress(STATUS_MAX);
                } else {
                    // proBar.setProgress(status);
                }
            }
        }
    };
    /**
     * 显示或隐藏组件的 Handler
     */
    private final Handler showHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_HIDE_PROBAR:
                    // proBar.setVisibility(View.GONE);
                    break;
                case MESSAGE_SHOW_PROBAR:
                    // proBar.setVisibility(View.VISIBLE);
                    break;
                case MESSAGE_SHOW_LOGIN:
                    status = STATUS_MAX + 1;
                    // 到主页面
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            baseApp.setSplashProgressOver(true);

                            new MainTask().execute();
                        }
                    }, 100);
                    break;
                default:
                    break;
            }
        }
    };

    // 进度条相关的参数。结束===========================================
    /**
     * 进度条
     */
    // private ProgressBar proBar;

    // 程序之前的版本号
    int ver_int;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        classThis = SplashActivity.this;

        boolean continueFlag = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    continueFlag = true;
                }
            }
        } else {
            continueFlag = true;
        }
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("myAppName", "Error: external storage is unavailable");
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("myAppName", "Error: external storage is read only.");
        }
        Log.d("myAppName", "External storage is not read only or unavailable");

        if (!((BaseApplication) getApplication()).isSplashProgressOver()) {
            // 检查是否有额外的存储设备（如 SD 卡）。开始=====================================
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                show("找不到SD卡，程序将退出！");
                finish();
            } else {
                File sdcard = Environment.getExternalStorageDirectory();
                File pkgFile = new File(sdcard.getAbsolutePath() + "/" + CommonParam.PROJECT_NAME);
                File dbFile = new File(pkgFile.getAbsolutePath() + "/db/sys.db");
                FileUtil.copyDefaultDB(this);
                if (!dbFile.exists()) {
                } else {
                    // 清空临时文件
                    File tempDir = new File(pkgFile.getAbsolutePath() + "/temp");
                    File[] files = tempDir.listFiles();
                    try {
                        for (File file : files) {
                            if (file.isFile()) {
                                file.delete();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // 检查是否有额外的存储设备（如 SD 卡）。结束=====================================
        }
        super.onCreate(savedInstanceState);
        // 获得ActionBar
        // actionBar = getSupportActionBar();
        // 隐藏ActionBar
        // actionBar.hide();
        // 不显示标题
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //infoTool = getInfoTool();

        // 提示动画
        // Animation animation_fadeIn = AnimationUtils.loadAnimation(classThis, R.anim.view_fadein);
        // animation_fadeIn.setAnimationListener(new AnimationListener() {
        //
        // @Override
        // public void onAnimationStart(Animation animation) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void onAnimationRepeat(Animation animation) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void onAnimationEnd(Animation animation) {
        //
        // }
        // });
        // // 运行动画
        // findViewById(R.id.splashLayout).startAnimation(animation_fadeIn);

        // 升级服务器地址。开始====================================
//        if (CommonUtil.checkNB(baseApp.serverAddr) && baseApp.serverAddr.equals("39.107.231.163:8080/zb")) {
//            // 设置默认服务器地址
//            baseApp.serverAddr = getString(R.string.url_upload_default);
//            // 写入服务器地址
//            preferEditor.putString("SERVER_ADDR", baseApp.serverAddr);
//            preferEditor.commit();
//        }
        // 升级服务器地址。结束====================================

        // 检查版本号。开始====================================
        // 程序之前的版本号
        String ver = getSysconfigValue(CommonParam.SYSCONFIG_VER);
        if (!CommonUtil.checkNB(ver)) {
            ver = "1";
        }
        ver_int = Integer.parseInt(ver);
        // 当前版本号
        int version_code_int = baseApp.versionCodeInt;
        //Log.d("##version_code_int", "" + version_code_int);
        //Log.d("##ver_int", "" + ver_int);
        if (version_code_int > ver_int) {
            // 如果当前版本号大于数据库版本号，就要对数据库进行升级
            // FileUtil.copyUpdateDB(classThis);
            // ★☆★判断需要升级的版本号
            // if (version_code_int > 1 && ver_int <= 1 && ver_int > 0 || ver_int == 0) {
            // dbTool.closeDb();
            // FileUtil.copyDefaultDB(this, true);
            // dbTool.regetDb();
            // }
            // 升级。开始=========================
            new UpdateDbTask().execute();
            // 升级。结束=========================
        } else {
            new updateProTask().execute();
        }
        // 检查版本号。结束====================================

        // Log.d("#status", "" + status);

        // proBar = (ProgressBar) findViewById(R.id.splashProBar);

        // proBar.setMax(STATUS_MAX);

        // if (baseApp.getReceiveNofifyFlag()) {
        // // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,"api_key")
        // // 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
        // if (!PNUtil.hasBind(getApplicationContext())) {
        // PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
        // PNUtil.getMetaValue(getApplicationContext(), PNUtil.API_KEY_NAME));
        // // PushManager.setTags(getApplicationContext(), Arrays.asList(new String[] { "AAA" }));
        // // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // // PushManager.enableLbs(getApplicationContext());
        // } else {
        // // 恢复push服务
        // PushManager.resumeWork(getApplicationContext());
        // // PushManager.setTags(getApplicationContext(), Arrays.asList(new String[] { "AAA" }));
        // }
        // } else {
        // PushManager.stopWork(getApplicationContext());
        // }

        if (baseApp.isSplashProgressOver()) {
            // 进度条已经结束
            status = STATUS_MAX + 1;
        } else {
            status = 0;
            // 进度条需要显示
            // proBar.setVisibility(View.VISIBLE);
            // 外部扫描的 IC 卡 ID
            // postCardId = getPostCardId();
        }

        baseApp.setSplashProgressOver(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        status = STATUS_MAX + 1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 用来升级的 AsyncTask
     */
    private class updateProTask extends AsyncTask<Object, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... arg0) {
            String result = CommonParam.RESULT_ERROR;

            try {
                while (status <= STATUS_MAX) {
                    // 获取耗时操作的完成百分比
                    // Log.d("#", "" + status);
                    if (status < STATUS_MAX) {
                        status++;
                        doWait(100);
                    } else {
                        status++;
                        doWait(100);
                    }
                    // 发送消息到 Handler
                    // proHandler.sendEmptyMessage(MESSAGE_PROGRESS);
                }
                baseApp.setSplashProgressOver(true);
                showHandler.sendEmptyMessage(MESSAGE_SHOW_LOGIN);
                result = CommonParam.RESULT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        /**
         * The system calls this to perform work in the UI thread and delivers the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {

        }

    }

    /**
     * 更新数据库 AsyncTask 类
     */
    private class UpdateDbTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：正在上传
         */
        private static final int PROGRESS_UPDATING = 1;

        /**
         * 上传的数据库
         */
        private SQLiteDatabase updateDb;
        /**
         * 操作巡视信息的工具类
         */
        private InfoTool updateTool;

        @Override
        protected void onPreExecute() {
            infoTool = getInfoTool();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;
            publishProgress(PROGRESS_UPDATING);

            // // 升级数据库文件
            // File updateFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
            // + CommonParam.PROJECT_NAME + "/update/update.db");
            // try {
            // updateDb = DbTool.getDb(updateFile);
            // if (updateDb != null) {
            // updateTool = new InfoTool(classThis, new DbTool(classThis, updateDb));
            //
            // 启动事务
            //db.beginTransaction();
            try {
//                if (ver_int == 104) {
//                    db.execSQL("ALTER TABLE main.t_base_userinfo ADD COLUMN picture TEXT;");
//                    db.execSQL("ALTER TABLE main.t_base_userinfo ADD COLUMN memo TEXT;");
//                } else if (ver_int > 1 && ver_int <= 113) {
//                    db.execSQL("ALTER TABLE main.t_base_deptinfo ADD COLUMN types TEXT;");
//                }

                // // 写入“code”表。开始================================================================
                // ArrayList<HashMap<String, Object>> codeList = updateTool.getInfoMapList(
                // "select * from \"code\" model", new String[] {});
                // // 删除旧数据
                // db.delete("code", null, null);
                // for (HashMap<String, Object> map : codeList) {
                // // 键值对
                // ContentValues cv = CommonUtil.mapToCv(map);
                // // ★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆
                // infoTool.insert("code", cv);
                // }
                // // 写入“region”表。结束================================================================

                // 提交事务
                //db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 结束事务
                //db.endTransaction();
            }

            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // } finally {
            // if (updateDb != null) {
            // DbTool.closeDb(updateDb);
            // }
            // }
            updateSysconfigValue(CommonParam.SYSCONFIG_VER, baseApp.versionCode);

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
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            Log.d("###", "更新成功！");
            new updateProTask().execute();
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
            // 显示等待窗口
            // makeWaitDialog();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            result = CommonParam.RESULT_SUCCESS;

            try {
                // 优化数据库
                db = getDb();
                db.execSQL("VACUUM");
            } catch (SQLException e) {
                e.printStackTrace();
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
            // 隐藏等待窗口
            unWait();
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (baseApp.isLogged) {
                    // 如果已经登录
                    // 创建信息传输Bundle
                    // Bundle data = new Bundle();
                    // 监控点id List
                    // data.putSerializable("monitoryIdList", monitoryIdList);
                    // 创建启动 Activity 的 Intent
                    Intent intent = new Intent(classThis, MainActivity.class);
                    // 将数据存入 Intent 中
                    // intent.putExtras(data);
                    startActivity(intent);
                    finish();
                    // overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                } else {
                    // 如果没有登录
                    // 创建启动 Activity 的 Intent
                    Intent intent = new Intent(classThis, MeLoginActivity.class);
                    // 信息传输Bundle
                    Bundle data = new Bundle();
                    data.putString("fromFlag", "main");
                    // 将数据存入Intent中
                    intent.putExtras(data);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}