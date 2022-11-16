/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.android.ui.TouchImageView;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 显示图片
 * <p>使用<code>universalimageloader</code>，支持本地图片和网络图片</p>
 *
 * @author 乔勇(Jacky Qiao)
 */
public class ShowImageCacheActivity extends DbActivity {
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

    private String imageUri;

    // 图片显示相关参数。开始===============================================
    public static List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    // 图片加载参数
    DisplayImageOptions displayImageOptions;
    // 图片显示相关参数。结束===============================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = ShowImageCacheActivity.this;

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

        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });

        displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.thumbnail) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.thumbnail) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.thumbnail) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                //.displayer(new RoundedBitmapDisplayer(8))// 是否设置为圆角，弧度为多少
                // .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();
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
                imageUri = data.getString("imageUri");
                Bundle infoBundle = data.getBundle("infoBundle");

                if (title != null) {
                    setTitle(title);
                } else {
                    setTitle(R.string.app_name);
                }

                picView.setWillNotCacheDrawing(false);
                picView.setMaxZoom(4F);

                if (infoBundle != null) {
                    Double lon_baidu = infoBundle.getDouble("lon_baidu", 0D);
                    Double lat_baidu = infoBundle.getDouble("lat_baidu", 0D);

                    if (lon_baidu != 0D && lat_baidu != 0D) {
                        locBtn.setVisibility(View.VISIBLE);
                        locBtn.setOnClickListener(new View.OnClickListener() {

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

                if (CommonUtil.checkNB(imageUri)) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(imageUri, picView, displayImageOptions,
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view,
                                                            FailReason failReason) {
                                    ImageView imageView = (ImageView) view;

                                    if (imageView != null) {
                                        ImageLoader.getInstance().displayImage(
                                                "drawable://" + R.drawable.thumbnail, imageView,
                                                displayImageOptions);
                                    }
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    ImageView imageView = (ImageView) view;
                                    if (loadedImage != null) {
                                        boolean firstDisplay = !displayedImages.contains(imageUri);
                                        if (firstDisplay) {
                                            FadeInBitmapDisplayer.animate(imageView, 500);
                                            displayedImages.add(imageUri);
                                        }
                                    } else {
                                        ImageLoader.getInstance().displayImage(
                                                "drawable://" + R.drawable.thumbnail, imageView,
                                                displayImageOptions);
                                    }
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                }
                            });
                } else {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.thumbnail,
                            picView, displayImageOptions);
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