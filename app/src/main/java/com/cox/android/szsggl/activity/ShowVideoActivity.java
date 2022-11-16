/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 显示视频
 * <p>使用<i>GSYVideoPlayer</i></p>
 *
 * @author 乔勇(Jacky Qiao)
 */
public class ShowVideoActivity extends DbActivity {
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
     * 视频View
     */
    private StandardGSYVideoPlayer videoPlayer;
    // 界面相关参数。结束===============================
    /**
     * 要传递的信息
     */
    private Bundle data;

    // 视频相关参数。开始=====================================
    private OrientationUtils videoOrientationUtils;
    private boolean videoIsPlay;
    /**
     * 是否有视频
     */
    private boolean videoFlag;

    /**
     * 页面Handler
     */
    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 10:
                    if (videoFlag && videoPlayer != null) {
                        // 开始播放
                        videoPlayer.startPlayLogic();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    // 视频相关参数。开始=====================================


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = ShowVideoActivity.this;

        // 获取该Result上的Intent
        Intent cerIntent = getIntent();
        // 获取Intent上携带的数据
        data = cerIntent.getExtras();

        setContentView(R.layout.show_video);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        closeBtn.setOnClickListener(new View.OnClickListener() {

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
        if (videoFlag && videoPlayer != null) {
            videoPlayer.getCurrentPlayer().onVideoResume(false);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (videoFlag && videoPlayer != null) {
            videoPlayer.getCurrentPlayer().onVideoPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (videoFlag && videoPlayer != null) {
            if (videoIsPlay) {
                videoPlayer.getCurrentPlayer().release();
            }
            if (videoOrientationUtils != null) {
                videoOrientationUtils.releaseListener();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(this.getClass().getName() + ":" + "log", "onBackPressed()");
        super.onBackPressed();
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(this.getClass().getName() + ":" + "log", "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        if (videoFlag && videoPlayer != null) {
            videoPlayer.onConfigurationChanged(this, newConfig, videoOrientationUtils, true, true);
        }
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

                if (!CommonUtil.checkNB(title)) {
                    title = getString(R.string.app_name);
                }

                File file = new File(filepath);
                if (file.exists()) {
                    videoFlag = true;

                    // 增加封面
                    ImageView imageView = new ImageView(classThis);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageResource(R.drawable.bg_portrait_0);

                    // 外部辅助的旋转，帮助全屏
                    videoOrientationUtils = new OrientationUtils(classThis, videoPlayer);
                    // 初始化不打开外部的旋转
                    videoOrientationUtils.setEnable(false);

                    GSYVideoOptionBuilder videoOption = new GSYVideoOptionBuilder();
                    // String videoUrl = "http://yjgl.sxcox.com/download/1.mp4";
                    videoOption.setThumbImageView(imageView)
                            .setIsTouchWiget(true)
                            .setRotateViewAuto(false)
                            .setLockLand(true)
                            .setAutoFullWithSize(false)
                            .setShowFullAnimation(false)
                            .setNeedLockFull(true)
                            .setUrl(Uri.fromFile(file).toString())
                            .setCacheWithPlay(true)
                            .setSeekRatio(1.0F)
                            .setBottomShowProgressBarDrawable(getResources().getDrawable(R.drawable.video_progress), getResources().getDrawable(R.drawable.video_seek_thumb))
                            .setBottomProgressBarDrawable(getResources().getDrawable(R.drawable.video_progress))
                            .setShrinkImageRes(R.drawable.video_shrink)
                            .setEnlargeImageRes(R.drawable.video_enlarge)
                            .setDialogProgressColor(R.color.solid_green, R.color.ade_dark_green)
                            .setVideoTitle(title)
                            .setVideoAllCallBack(new GSYSampleCallBack() {
                                @Override
                                public void onPrepared(String url, Object... objects) {
                                    Log.d("##onPrepared", objects[0] + ":" + objects[1]);
                                    super.onPrepared(url, objects);
                                    videoIsPlay = true;
                                }

                                @Override
                                public void onQuitFullscreen(String url, Object... objects) {
                                    Log.d("##onQuitFullscreen", objects[0] + ":" + objects[1]);
                                    super.onQuitFullscreen(url, objects);
                                    if (videoOrientationUtils != null) {
                                        videoOrientationUtils.backToProtVideo();
                                    }
                                    (new Timer()).schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            pageHandler.sendEmptyMessage(20);
                                        }
                                    }, 100L);
                                }

                                @Override
                                public void onEnterFullscreen(String url, Object... objects) {
                                    Log.d("##onEnterFullscreen", objects[0] + ":" + objects[1]);
                                    super.onEnterFullscreen(url, objects);
                                }
                            }).setLockClickListener(new LockClickListener() {
                        @Override
                        public void onClick(View view, boolean lock) {
                            if (videoOrientationUtils != null) {
                                //配合下方的onConfigurationChanged
                                videoOrientationUtils.setEnable(!lock);
                            }
                        }
                    }).build(videoPlayer);

                    // 全屏按键
                    videoPlayer.getFullscreenButton().setVisibility(View.GONE);
                    // 返回按键
                    videoPlayer.getBackButton().setVisibility(View.GONE);
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
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (videoFlag && videoPlayer != null) {
                    (new Timer()).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            pageHandler.sendEmptyMessage(10);
                        }
                    }, 100L);
                }
            } else if (CommonParam.RESULT_ERROR.equals(result)) {
                show("信息出错！");
                goBack();
            }
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        closeBtn = (ImageButton) findViewById(R.id.closeBtn);
        // 界面相关参数。开始===============================
        videoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.videoPlayer);
        // 界面相关参数。结束===============================
    }
}