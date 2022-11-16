/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.android.ui.TouchImageView;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;

/**
 * 显示图片
 * <p>仅支持本地图片</p>
 *
 * @author 乔勇(Jacky Qiao)
 */
public class ShowImageActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 关闭按钮
     */
    ImageButton closeBtn;
    // 界面相关参数。开始===============================
    /**
     * 图片
     */
    private TouchImageView picView = null;
    /**
     * 地理位置信息按钮
     */
    private Button locBtn;
    // 界面相关参数。结束===============================
    /**
     * 要传递的信息
     */
    private Bundle data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = ShowImageActivity.this;

        // 获取该Result上的Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        data = intent.getExtras();

        setContentView(R.layout.show_image);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        closeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
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
        StatusBarUtil.setStatusBarMode(this, false, R.color.window_background);
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

            // 处理数据。开始============================================================================

            // 处理数据。结束============================================================================

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
                String title = data.getString("title");
                String filepath = data.getString("filepath");
                Bundle infoBundle = data.getBundle("infoBundle");

                if (title != null) {
                    setTitle(title);
                } else {
                    setTitle(R.string.app_name);
                }

                File file = new File(filepath);
                // 图片
                Bitmap bm = null;
                if (file.exists()) {
                    // 图片文件存在
                    bm = CommonUtil.decodeSampledBitmapFromResource(filepath, CommonParam.SHOW_IMAGE_WIDTH,
                            CommonParam.SHOW_IMAGE_HEIGHT);
                } else {
                    // 图片文件不存在
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.thumbnail);
                }

                picView.setImageBitmap(bm);
                picView.setWillNotCacheDrawing(false);
                picView.setMaxZoom(4F);

                if (infoBundle != null) {
                    Double lon_baidu = infoBundle.getDouble("lon_baidu", 0D);
                    Double lat_baidu = infoBundle.getDouble("lat_baidu", 0D);

                    if (lon_baidu != 0D && lat_baidu != 0D) {
                        locBtn.setVisibility(View.VISIBLE);
                        locBtn.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // 创建启动 ShowImageActivity 的Intent
                                Intent intent = new Intent(classThis, ShowMapActivity.class);
                                // 将数据存入 Intent 中
                                intent.putExtras(data);
                                startActivity(intent);
                            }
                        });
                    }
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

            if (CommonParam.RESULT_ERROR.equals(result)) {
                show("信息出错！");
                goBack();
            }
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        titleText = (TextView) findViewById(R.id.title_text_view);
        closeBtn = (ImageButton) findViewById(R.id.closeBtn);
        // 界面相关参数。开始===============================
        picView = (TouchImageView) findViewById(R.id.picView);
        locBtn = (Button) findViewById(R.id.locBtn);
        // 界面相关参数。结束===============================
    }
}