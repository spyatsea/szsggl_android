/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.utils.SnackbarUtil;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.github.johnkil.print.PrintView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * 水工维修_项目记录信息_查阅页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MaintenanceRecShowActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 主界面
     */
    ScrollView contentView;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 回到主页按钮
     */
    ImageButton homeBtn;
    /**
     * 返回
     */
    private Button goBackBtn;
    /**
     * 列表名称区
     */
    LinearLayout listTitleLayout;
    /**
     * 列表名称
     */
    TextView listTitleTv;
    // 界面相关参数。开始===============================
    private TextView titleTv;
    private TextView stage1Tv;
    private TextView stage2Tv;
    private TextView cat1Tv;
    private TextView cat2Tv;
    private TextView lyTv;
    private TextView skTv;
    private TextView infoTv;
    private TextView ctimeTv;
    private TextView unameTv;
    private LinearLayout photoTitleLayout;
    private LinearLayout videoTitleLayout;
    private LinearLayout audioTitleLayout;
    private LinearLayout photoContentLayout;
    private LinearLayout videoContentLayout;
    private LinearLayout audioContentLayout;
    /**
     * 图片数量
     */
    private TextView photoNumTv;
    /**
     * 视频数量
     */
    private TextView videoNumTv;
    /**
     * 音频数量
     */
    private TextView audioNumTv;
    /**
     * 图片列表
     */
    private Gallery photoGallery;
    /**
     * 视频列表
     */
    private Gallery videoGallery;
    /**
     * 音频列表
     */
    private Gallery audioGallery;
    /**
     * 选中图片的说明
     */
    private TextView photoDescTv;
    /**
     * 选中视频的说明
     */
    private TextView videoDescTv;
    /**
     * 选中音频的说明
     */
    private TextView audioDescTv;
    private LinearLayout attaTitleLayout;
    private TextView attaNumTv;
    private Button continueDownloadAllBtn;
    private Button reDownloadAllBtn;
    private PrintView attaHelpBtn;
    private LinearLayout attaListLayout;
    /**
     * 弹出菜单
     */
    private PopupMenu attaPopupMenu;
    // 界面相关参数。结束===============================

    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;

    // 附件相关参数。开始===============================
    /**
     * 附件 List
     */
    private List<HashMap<String, Object>> attaList;
    /**
     * 存放下载附件任务的Map
     */
    HashMap<String, AsyncTask<Object, Integer, String>> downloadAttaTaskMap;
    /**
     * 待下载附件的List
     */
    ArrayList<HashMap<String, Object>> downloadAttaList;
    /**
     * 当前正在下载的文件
     */
    File currentDownloadingFile;
    // 附件相关参数。结束===============================

    /**
     * 信息
     */
    private HashMap<String, Object> infoObj;
    /**
     * 信息编号
     */
    private String infoId;
    /**
     * 弹出菜单
     */
    private PopupMenu popupMenu;
    /**
     * 播音Dialog
     */
    private AlertDialog playVoiceDlg;
    /**
     * 继续播放音频标记
     */
    private boolean playVoiceContinueFlag;
    /**
     * 图片附件 List
     */
    private List<HashMap> photoList;
    /**
     * 视频附件 List
     */
    private List<HashMap> videoList;
    /**
     * 音频附件 List
     */
    private List<HashMap> audioList;
    /**
     * 选中图片的索引
     */
    private int photoSelPos = -1;
    /**
     * 选中视频的索引
     */
    private int videoSelPos = -1;
    /**
     * 选中音频的索引
     */
    private int audioSelPos = -1;
    /**
     * 选中附件的类型
     */
    private String currentAttaType;
    /**
     * 选中附件的索引
     */
    private int currentAttaIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MaintenanceRecShowActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");

        setContentView(R.layout.maintenance_rec_show);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("项目记录：查阅");

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        homeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(CommonParam.RESULTCODE_EXIT);
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
        continueDownloadAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                continueDownloadAllBtn.setClickable(false);
                continueDownloadAllBtn.setEnabled(false);
                AsyncTask<Object, Integer, String> checkUnDownloadAttaListTask = new CheckUnDownloadAttaListTask();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        checkUnDownloadAttaListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        checkUnDownloadAttaListTask.execute();
                    }
                } catch (RejectedExecutionException ree) {
                }
            }
        });
        reDownloadAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reDownloadAllBtn.setClickable(false);
                reDownloadAllBtn.setEnabled(false);
                AsyncTask<Object, Integer, String> reDownloadAllAttaListTask = new ReDownloadAllAttaListTask();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        reDownloadAllAttaListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        reDownloadAllAttaListTask.execute();
                    }
                } catch (RejectedExecutionException ree) {
                }
            }
        });
        attaHelpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeHelpDialog(R.layout.dlg_help_download_all);
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
        StatusBarUtil.setStatusBarMode(this, false, R.color.background_title_green_dark);
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
        if (downloadAttaTaskMap != null) {
            for (Map.Entry<String, AsyncTask<Object, Integer, String>> e : downloadAttaTaskMap.entrySet()) {
                AsyncTask<Object, Integer, String> task = e.getValue();
                task.cancel(true);
            }
        }
        downloadAttaList.clear();
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
         * 进度常量：显示图片
         */
        private static final int PROGRESS_SHOW_PHOTO = 1003;
        /**
         * 进度常量：显示视频
         */
        private static final int PROGRESS_SHOW_VIDEO = 1004;
        /**
         * 进度常量：显示音频
         */
        private static final int PROGRESS_SHOW_AUDIO = 1005;

        /**
         * 是否已上传到服务器
         */
        private String upSign;

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

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            downloadAttaTaskMap = new HashMap<String, AsyncTask<Object, Integer, String>>();
            downloadAttaList = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT * FROM t_szfgs_sgwxrec model WHERE model.valid='1' and model.ids=? and model.quid=?",
                            new String[]{infoId, (String) baseApp.getLoginUser().get("ids")});
            if (recList.size() > 0) {
                infoObj = recList.get(0);
            }
            if (infoObj == null) {
                return result;
            }
            upSign = (String) infoObj.get("up");

            // 图片信息
            String photo = (String) infoObj.get("photo");
            if (CommonUtil.checkNB(photo)) {
                photoList = (List<HashMap>) JSONArray.parseArray(photo, HashMap.class);
            }
            if (photoList == null) {
                photoList = new ArrayList<HashMap>();
            }
            // 视频信息
            String video = (String) infoObj.get("video");
            if (CommonUtil.checkNB(video)) {
                videoList = (List<HashMap>) JSONArray.parseArray(video, HashMap.class);
            }
            if (videoList == null) {
                videoList = new ArrayList<HashMap>();
            }
            // 音频信息
            String audio = (String) infoObj.get("audio");
            if (CommonUtil.checkNB(audio)) {
                audioList = (List<HashMap>) JSONArray.parseArray(audio, HashMap.class);
            }
            if (audioList == null) {
                audioList = new ArrayList<HashMap>();
            }

            // 下载附件。开始================================================================
//            // 待下载的附件List
//            List<String> attaFileList = new ArrayList<String>();
//            // 附件
//            String _attachment = null;
//            if ("1".equals(upSign)) {
//                _attachment = (String) infoObj.get("attachment");
//            }
//
//            if (CommonUtil.checkNB(_attachment)) {
//                JSONArray _aArray = JSONArray.parseArray(_attachment);
//                for (int i = 0, len = _aArray.size(); i < len; i++) {
//                    String fileName = _aArray.getJSONObject(i).getString("file");
//                    if (CommonUtil.checkNB(fileName)) {
//                        attaFileList.add(fileName);
//                    }
//                }
//            }
//            // 附件保存目录
//            File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
//                    + CommonParam.PROJECT_NAME + "/atta");
//            for (int i = 0, len = attaFileList.size(); i < len; i++) {
//                String _file = attaFileList.get(i);
//                File attaFile = new File(saveDir_atta, _file);
//
//                if (!attaFile.exists()) {
//                    Map<String, Object> downloadResult = downloadFile("http://" + baseApp.serverAddr
//                            + "/UploadFiles/SgBizAtta/" + _file, attaFile.getAbsolutePath(), null);
//                }
//            }
            // 下载附件。结束================================================================
            // 处理数据。结束============================================================================

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);
            if ("0".equals(upSign)) {
                // 显示图片
                publishProgress(PROGRESS_SHOW_PHOTO);
                // 显示视频
                publishProgress(PROGRESS_SHOW_VIDEO);
                // 显示音频
                publishProgress(PROGRESS_SHOW_AUDIO);
            }
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
                homeBtn.setVisibility(View.VISIBLE);
                if ("0".equals(upSign)) {
                    listTitleLayout.setVisibility(View.VISIBLE);
                    listTitleTv.setText("本记录尚未上传到服务器，请及时上传。");
                } else {
                    listTitleLayout.setVisibility(View.GONE);
                }

                titleTv.setText(CommonUtil.N2B((String) infoObj.get("title")));
                stage1Tv.setText(CommonUtil.N2B((String) infoObj.get("stage1")));
                stage2Tv.setText(CommonUtil.N2B((String) infoObj.get("stage2")));
                cat1Tv.setText(CommonUtil.N2B((String) infoObj.get("cat1")));
                cat2Tv.setText(CommonUtil.N2B((String) infoObj.get("cat2")));
                lyTv.setText(CommonUtil.N2B((String) infoObj.get("ly")));
                skTv.setText(CommonUtil.N2B((String) infoObj.get("sk")).equals("1") ? "是" : "否");
                infoTv.setText(CommonUtil.N2B((String) infoObj.get("info")));
                ctimeTv.setText(CommonUtil.N2B((String) infoObj.get("ctime")));
                unameTv.setText(CommonUtil.N2B((String) infoObj.get("uname")));
                if ("0".equals(upSign)) {
                    photoTitleLayout.setVisibility(View.VISIBLE);
                    videoTitleLayout.setVisibility(View.VISIBLE);
                    audioTitleLayout.setVisibility(View.VISIBLE);
                    photoContentLayout.setVisibility(View.VISIBLE);
                    videoContentLayout.setVisibility(View.VISIBLE);
                    audioContentLayout.setVisibility(View.VISIBLE);
                } else {
                    photoTitleLayout.setVisibility(View.GONE);
                    videoTitleLayout.setVisibility(View.GONE);
                    audioTitleLayout.setVisibility(View.GONE);
                    photoContentLayout.setVisibility(View.GONE);
                    videoContentLayout.setVisibility(View.GONE);
                    audioContentLayout.setVisibility(View.GONE);
                }
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
            } else if (progress[0] == PROGRESS_SHOW_PHOTO) {
                // 生成图片 Gallery 列表
                // 设置图片数量
                photoNumTv.setText("(" + photoList.size() + ")");
                makePhotoGallery();
            } else if (progress[0] == PROGRESS_SHOW_VIDEO) {
                // 生成视频 Gallery 列表
                // 设置视频数量
                videoNumTv.setText("(" + videoList.size() + ")");
                makeVideoGallery();
            } else if (progress[0] == PROGRESS_SHOW_AUDIO) {
                // 生成音频 Gallery 列表
                // 设置音频数量
                audioNumTv.setText("(" + audioList.size() + ")");
                makeAudioGallery();
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
                new MakeAttaListTask().execute();
            } else {
                show("信息错误！");
                goBack();
            }
        }
    }

    /**
     * 生成附件列表的 AsyncTask 类
     */
    private class MakeAttaListTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：显示附件列表
         */
        private static final int PROGRESS_SHOW_ATTA_LIST = 1001;

        /**
         * 进度常量：建立附件列表
         */
        private static final int PROGRESS_MAKE_ATTA_LIST = 1002;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在生成附件列表，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            attaList = new ArrayList<HashMap<String, Object>>();
            String attachment = null;
            // 是否上传到服务器
            String upSign = (String) infoObj.get("up");
            if ("1".equals(upSign)) {
                attachment = (String) infoObj.get("attachment");
            }
            if (CommonUtil.checkNB(attachment)) {
                JSONArray ps = JSONArray.parseArray(attachment);
                for (int i = 0, len = ps.size(); i < len; i++) {
                    JSONObject o = ps.getJSONObject(i);
                    attaList.add(CommonUtil.jsonToMap(o));
                }
            }

            publishProgress(PROGRESS_SHOW_ATTA_LIST);

            if (attaList.size() > 0) {
                // 有附件
                publishProgress(PROGRESS_MAKE_ATTA_LIST);
            }

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
            if (progress[0] == PROGRESS_SHOW_ATTA_LIST) {
                // 显示附件列表
                if (attaList.size() == 0) {
                    attaTitleLayout.setVisibility(View.GONE);
                    attaListLayout.setVisibility(View.GONE);
                } else {
                    attaTitleLayout.setVisibility(View.VISIBLE);
                    attaListLayout.setVisibility(View.VISIBLE);
                    attaNumTv.setText("(" + attaList.size() + ")");
                }
            } else if (progress[0] == PROGRESS_MAKE_ATTA_LIST) {
                // 建立附件列表
                // 附件保存目录
                File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/atta");
                for (int i = 0, len = attaList.size(); i < len; i++) {
                    HashMap<String, Object> o = attaList.get(i);

                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.atta_list_item, null);
                    // 附件名称
                    TextView infoName = (TextView) layout.findViewById(R.id.infoName);
                    // 附件大小
                    TextView infoSize = (TextView) layout.findViewById(R.id.infoSize);
                    // 行分隔符
                    View splitterView = (View) layout.findViewById(R.id.splitterView);
                    // 图标
                    // ImageView infoIcon = (ImageView) layout.findViewById(R.id.infoIcon);
                    // 附件状态
                    TextView infoStatus = (TextView) layout.findViewById(R.id.infoStatus);
                    // 下载状态
                    ProgressBar infoStatusBar = (ProgressBar) layout.findViewById(R.id.infoStatusBar);

                    infoName.setText((String) o.get("name"));
                    infoSize.setText((String) o.get("size"));
                    // 信息Tag
                    HashMap<String, Object> tag = new HashMap<String, Object>();
                    tag.put("i", i);
                    tag.put("o", o);
                    // 是否强制下载
                    tag.put("f", false);
                    layout.setTag("ATTA_" + i);
                    infoName.setTag(tag);
                    infoStatusBar.setVisibility(View.GONE);

                    layout.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> tag = (HashMap<String, Object>) v.findViewById(R.id.infoName).getTag();
                            HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");

                            String fileName = (String) o.get("file");
                            String fileAlias = (String) o.get("name");
                            if (CommonUtil.checkNB(fileName)) {
                                File attaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/" + CommonParam.PROJECT_NAME + "/atta/" + fileName);
                                if (!(currentDownloadingFile != null && currentDownloadingFile.getAbsolutePath().equals(attaFile.getAbsolutePath()))) {
                                    // 如果没有正在下载该文件，才可以继续后面的操作
                                    if (CommonUtil.checkEndsWithInStringArray(fileName,
                                            getResources().getStringArray(R.array.fileEndingWps))
                                            || CommonUtil.checkEndsWithInStringArray(fileName, getResources()
                                            .getStringArray(R.array.fileEndingEt))
                                            || CommonUtil.checkEndsWithInStringArray(fileName, getResources()
                                            .getStringArray(R.array.fileEndingDps))) {
                                        CommonUtil.editWpsFile(attaFile, classThis);
                                    } else {
                                        CommonUtil.openAttaFile(attaFile, fileAlias, classThis);
                                    }
                                }
                            }
                        }
                    });
                    layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showPopupMenuAtta(v);
                            return true;
                        }
                    });

                    if (i < (len - 1)) {
                        splitterView.setVisibility(View.VISIBLE);
                    }
                    attaListLayout.addView(layout);

                    // 处理附件相关信息。开始=====================================
                    String file = (String) o.get("file");
                    File attaFile = new File(saveDir_atta, file);
                    if (attaFile.exists()) {
                        // 如果附件已经存在
                        infoStatus.setText(R.string.status_already_downloaded);
                        infoStatus.setTextColor(getResources().getColor(R.color.text_green_darker));
                    } else {
                        infoStatus.setText(R.string.status_not_download);
                        infoStatus.setTextColor(getResources().getColor(R.color.text_brown));

                        // 将待下载的附件添加到列表中
                        downloadAttaList.add(tag);
                    }
                    // 处理附件相关信息。结束=====================================
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

            // 自动下载附件
            if (baseApp.isAutoDownloadAtta) {
                // 下载下一个附件
                downloadNextAtta();
            } else {
                downloadAttaList.clear();
            }
        }
    }

    /**
     * 下载下一个附件
     */
    private void downloadNextAtta() {
        if (downloadAttaList.size() > 0) {
            isDownloading = true;
            // 信息Tag
            HashMap<String, Object> tag = (HashMap<String, Object>) downloadAttaList.get(0);
            HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
            int o_index = (int) tag.get("i");
            boolean o_force = (boolean) tag.get("f");
            // 创建附件下载任务
            createDownloadAttaTask(o, o_index, o_force);
        } else {
            isDownloading = false;
        }
    }

    /**
     * 创建附件下载任务
     *
     * @param o                 {@code HashMap<String, Object>} 附件对象
     * @param o_index           {@code int} 附件索引
     * @param forceDownloadFlag {@code boolean} 强制下载标志
     */
    private void createDownloadAttaTask(HashMap<String, Object> o, int o_index, boolean forceDownloadFlag) {
        createDownloadAttaTask(o, o_index, forceDownloadFlag, false);
    }

    /**
     * 创建附件下载任务
     *
     * @param o                 {@code HashMap<String, Object>} 附件对象
     * @param o_index           {@code int} 附件索引
     * @param forceDownloadFlag {@code boolean} 强制下载标志
     * @param needShowNetAlert  {@code boolean} 是否需要发出网络提示信息
     */
    private void createDownloadAttaTask(HashMap<String, Object> o, int o_index, boolean forceDownloadFlag, boolean needShowNetAlert) {
        DownloadAttaTask downloadAttaTask = new DownloadAttaTask();
        downloadAttaTaskMap.put((String) o.get("file"), downloadAttaTask);
        downloadAttaTask.execute(o, o_index, forceDownloadFlag, needShowNetAlert);
    }

    /**
     * 下载附件 AsyncTask 类
     */
    private class DownloadAttaTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：开始下载
         */
        private static final int PROGRESS_START = 1001;

        /**
         * 附件对象
         */
        HashMap<String, Object> o;
        /**
         * 附件索引
         */
        int o_index;
        /**
         * 强制下载标志
         */
        boolean forceDownloadFlag = false;
        /**
         * 是否需要发出网络提示信息
         */
        boolean needShowNetAlert = false;
        /**
         * 是否需要下载附件
         */
        boolean needDownloadFlag = false;
        /**
         * 本地保存的附件文件
         */
        File attaFile;
        /**
         * 下载连接
         */
        HttpURLConnection conn;
        InputStream is;
        FileOutputStream fs;

        LinearLayout atta_layout;
        TextView infoStatus;
        ProgressBar infoStatusBar;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            currentDownloadingFile = null;
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 处理数据。开始============================================================================
            o = (HashMap<String, Object>) params[0];
            o_index = (int) params[1];
            if (params.length > 2) {
                forceDownloadFlag = (boolean) params[2];
            }
            if (params.length > 3) {
                needShowNetAlert = (boolean) params[3];
            }

            String remoteFileName = null;
            if (o != null && o_index > -1) {
                atta_layout = (LinearLayout) attaListLayout.findViewWithTag("ATTA_" + o_index);
                if (atta_layout != null) {
                    // 附件状态
                    infoStatus = (TextView) atta_layout.findViewById(R.id.infoStatus);
                    // 下载状态
                    infoStatusBar = (ProgressBar) atta_layout.findViewById(R.id.infoStatusBar);
                }

                // 附件保存目录
                File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/atta");
                remoteFileName = (String) o.get("file");
                attaFile = new File(saveDir_atta, remoteFileName);
                currentDownloadingFile = attaFile;
                if (forceDownloadFlag) {
                    // 强制下载，先删除后下载
                    if (attaFile.exists()) {
                        attaFile.delete();
                    }
                    needDownloadFlag = true;
                } else {
                    // 非强制下载
                    if (!attaFile.exists()) {
                        needDownloadFlag = true;
                    } else {
                        result = CommonParam.RESULT_SUCCESS;
                    }
                }
            }

            if (needDownloadFlag) {
                // 需要下载
                publishProgress(PROGRESS_START);
                // 下载文件。开始===============================================
                if (checkNet(needShowNetAlert)) {
                    // 读取超时（毫秒）
                    int readTimeout = 3000;
                    // 连接超时（毫秒）
                    int connectTimeout = 3000;
                    // 下载文件名
                    String urlString = "http://" + baseApp.serverAddr + "/UploadFiles/SgBizAtta/" + remoteFileName;

                    try {
                        URL url = new URL(urlString);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                        conn.setReadTimeout(readTimeout);
                        conn.setConnectTimeout(connectTimeout);
                        conn.setDoInput(true);
                        conn.connect();
                        int response = conn.getResponseCode();
                        if (response == 200) {
                            is = conn.getInputStream();
                            if (is != null) {
                                fs = new FileOutputStream(attaFile);
                                byte[] buffer = new byte[1444];
                                int byteRead = 0;
                                while ((byteRead = is.read(buffer)) != -1) {
                                    fs.write(buffer, 0, byteRead);
                                }
                                fs.flush();
                                result = CommonParam.RESULT_SUCCESS;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                            conn = null;
                        }
                        if (fs != null) {
                            try {
                                fs.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("#successFlag", result + ":" + urlString);
                }
                // 下载文件。结束===============================================
//                Map<String, Object> downloadResult = downloadFile("http://" + baseApp.serverAddr
//                        + "/UploadFiles/SgBizAtta/" + remoteFileName, attaFile.getAbsolutePath(), null, needShowNetAlert);
//                result = (String) downloadResult.get("result");
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
            if (progress[0] == PROGRESS_START) {
                // 开始下载
                if (atta_layout != null) {
                    infoStatus.setText(R.string.status_downloading);
                    infoStatus.setTextColor(getResources().getColor(R.color.text_color_subline_blue));
                    infoStatusBar.setVisibility(View.VISIBLE);
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
            if (atta_layout != null) {
                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    // 下载成功
                    infoStatus.setText(R.string.status_already_downloaded);
                    infoStatus.setTextColor(getResources().getColor(R.color.text_green_darker));
                } else {
                    // 下载失败
                    infoStatus.setText(R.string.status_not_download);
                    infoStatus.setTextColor(getResources().getColor(R.color.text_brown));
                }
                infoStatusBar.setVisibility(View.GONE);
            }

            // 去掉该附件的下载计划
            downloadAttaTaskMap.remove((String) o.get("file"));
            if (downloadAttaList.size() > 0) {
                downloadAttaList.remove(0);
            }
            currentDownloadingFile = null;

            // 下载下一个附件
            downloadNextAtta();
        }

        @Override
        protected void onCancelled(String result) {
            currentDownloadingFile = null;
            if (needDownloadFlag && isCancelled() && CommonParam.RESULT_ERROR.equals(result)) {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
                if (fs != null) {
                    try {
                        fs.flush();
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 删除没下载完成的文件
                if (attaFile.exists()) {
                    attaFile.delete();
                }
            }

            super.onCancelled(result);
        }
    }

    /**
     * 显示附件PopupMenu
     *
     * @param view {@code View} PopupMenu绑定的对象
     */
    public void showPopupMenuAtta(View view) {
        if (attaPopupMenu != null) {
            Menu menu = attaPopupMenu.getMenu();
            menu.close();
            attaPopupMenu = null;
        }
        attaPopupMenu = new PopupMenu(this, view);
        // 强制显示PopupMenu图标
        forceShowPopupMenuIcon(attaPopupMenu);
        MenuInflater inflater = attaPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.sgres_atta_menu, attaPopupMenu.getMenu());
        attaPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_download:
                        // 下载附件
                        // 附件名称
                        TextView infoName = (TextView) view.findViewById(R.id.infoName);
                        // 信息Tag
                        HashMap<String, Object> tag = (HashMap<String, Object>) infoName.getTag();
                        HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
                        int o_index = (int) tag.get("i");
                        boolean o_force = (boolean) tag.get("f");
                        String file = (String) o.get("file");

                        if (downloadAttaTaskMap.containsKey(file)) {
                            // 正在下载
                            show("正在下载该文件，请稍后再试…");
                        } else {
                            // 是否需要将附件添加到下载队列中
                            boolean needAddToListFlag = true;
                            for (HashMap<String, Object> downloadAtta : downloadAttaList) {
                                int download_index = (int) downloadAtta.get("i");
                                if (o_index == download_index) {
                                    needAddToListFlag = false;
                                    downloadAtta.put("f", true);
                                    break;
                                }
                            }
                            if (needAddToListFlag) {
                                // 将待下载的附件添加到列表中
                                // 强制下载
                                tag.put("f", true);
                                downloadAttaList.add(tag);
                                if (!isDownloading) {
                                    downloadNextAtta();
                                }
                            }
                            SnackbarUtil.ShortSnackbar(contentView, "附件已添加到下载计划中！", SnackbarUtil.Info).show();
                        }

                        break;
                    default:
                }
                return true;
            }
        });
        attaPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu popup) {
            }
        });
        attaPopupMenu.show();
    }


    /**
     * 显示附件信息
     *
     * @param type {@code String} 附件类型
     * @param pos  {@code int} 附件在列表中的索引
     */
    public void showAttaDetail(String type, int pos) {
        HashMap<String, Object> atta = null;
        if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
            // 图片
            atta = photoList.get(pos);
        } else if (CommonParam.ATTA_TYPE_VIDEO.equals(type)) {
            // 视频
            atta = videoList.get(pos);
        } else if (CommonParam.ATTA_TYPE_AUDIO.equals(type)) {
            // 音频
            atta = audioList.get(pos);
        }
        makeAttaDetailDialog(atta);
    }

    /**
     * 显示编辑附件详情对话框
     *
     * @param atta {@code HashMap<String, Object>} 绑定的信息
     */
    public void makeAttaDetailDialog(HashMap<String, Object> atta) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
        ScrollView layout = null;
        // 附件类型
        String type = (String) atta.get("type");
        layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_atta_detail, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.ins_column_atta_detail);
        dlgBuilder.setIcon(R.drawable.menu_document_info);
        dlgBuilder.setCancelable(true);

        // 附件图片
        ImageView imageView = (ImageView) layout.findViewById(R.id.atta_image);
        // 附件名称
        TextView attaFileName = (TextView) layout.findViewById(R.id.atta_filename);
        // 附件类型
        TextView attaFileType = (TextView) layout.findViewById(R.id.atta_filetype);
        // 附件大小
        TextView attaFileSize = (TextView) layout.findViewById(R.id.atta_filesize);
        // 创建时间
        // TextView attaCreatedtime = (TextView) layout.findViewById(R.id.atta_createdtime);

        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CommonParam.PROJECT_NAME
                + "/ins/" + atta.get("name");

        File attaFile = new File(filepath);
        if (type.equals(CommonParam.ATTA_TYPE_PHOTO)) {
            // 图片
            Bitmap bm = null;
            if (attaFile.exists()) {
                // 图片文件存在
                bm = CommonUtil.decodeSampledBitmapFromResource(filepath,
                        getResources().getInteger(R.integer.gallery_thumbnail_width),
                        getResources().getInteger(R.integer.gallery_thumbnail_height));
            } else {
                // 图片文件不存在
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.thumbnail);
            }
            attaFileSize.setText((String) atta.get("size"));
            // attaCreatedtime.setText((String) atta.get("ct"));
            imageView.setImageBitmap(bm);
        } else if (type.equals(CommonParam.ATTA_TYPE_VIDEO)) {
            // 视频
            if (attaFile.exists()) {
                attaFileSize.setText((String) atta.get("size"));
            }
            // 视频图片
            imageView.setImageResource(R.drawable.video_icon);
        } else if (type.equals(CommonParam.ATTA_TYPE_AUDIO)) {
            // 音频
            if (attaFile.exists()) {
                attaFileSize.setText((String) atta.get("size"));
            }
            // 音频图片
            imageView.setImageResource(R.drawable.audio_icon);
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = getResources().getInteger(R.integer.gallery_thumbnail_height);
        layoutParams.width = getResources().getInteger(R.integer.gallery_thumbnail_width);
        imageView.setWillNotCacheDrawing(false);

        attaFileName.setText((String) atta.get("alias"));
        attaFileType.setText(CommonUtil.N2B(CommonParam.CODE_ATTA_TYPE().get(type)));

        dlgBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // 确定按钮
        Button confirmBtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmBtn.setTag(dlg);

        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dlg = (AlertDialog) v.getTag();
                dlg.cancel();
            }
        });
    }

    /**
     * 生成图片 Gallery 列表
     */
    public void makePhotoGallery() {
        // 为 Gallery 设置 adapter
        BaseAdapter adapter = new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 图片的 ImageView
                ImageView imageView = new ImageView(classThis);
                imageView.setTag(CommonParam.ATTA_TYPE_PHOTO);
                HashMap<String, Object> atta = photoList.get(position);
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");

                File attaFile = new File(filepath);
                // 图片
                Bitmap bm = null;
                if (attaFile.exists()) {
                    // 图片文件存在
                    bm = CommonUtil.decodeSampledBitmapFromResource(filepath,
                            getResources().getInteger(R.integer.gallery_thumbnail_width),
                            getResources().getInteger(R.integer.gallery_thumbnail_height));
                } else {
                    // 图片文件不存在
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.thumbnail);
                }

                imageView.setImageBitmap(bm);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new Gallery.LayoutParams(getResources().getInteger(
                        R.integer.gallery_thumbnail_width), getResources().getInteger(
                        R.integer.gallery_thumbnail_height)));
                TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
                imageView.setBackgroundResource(typedArray.getResourceId(
                        R.styleable.Gallery_android_galleryItemBackground, 0));
                imageView.setWillNotCacheDrawing(false);
                return imageView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public int getCount() {
                return photoList.size();
            }
        };
        photoGallery.setAdapter(adapter);
        photoGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                photoSelPos = position;
                HashMap<String, Object> atta = photoList.get(position);
                String name = (String) atta.get("alias");
                photoDescTv.setText(name);
                photoDescTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                photoSelPos = -1;
                photoDescTv.setText("");
                photoDescTv.setVisibility(View.INVISIBLE);
            }
        });
        photoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了选中的图片
                if (photoSelPos == position) {
                    HashMap<String, Object> atta = photoList.get(position);
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");
                    openPicByFilename((String) atta.get("alias"), filepath, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
        photoGallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupConfigMenu(view, CommonParam.ATTA_TYPE_PHOTO, position);
                return false;
            }
        });
    }

    /**
     * 生成视频 Gallery 列表
     */
    public void makeVideoGallery() {
        // 为 Gallery 设置 adapter
        BaseAdapter adapter = new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 视频的 ImageView
                ImageView imageView = new ImageView(classThis);
                imageView.setTag(CommonParam.ATTA_TYPE_VIDEO);
                // 视频图片
                imageView.setImageResource(R.drawable.video_icon);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new Gallery.LayoutParams(getResources().getInteger(
                        R.integer.gallery_thumbnail_width), getResources().getInteger(
                        R.integer.gallery_thumbnail_height)));
                TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
                imageView.setBackgroundResource(typedArray.getResourceId(
                        R.styleable.Gallery_android_galleryItemBackground, 0));
                imageView.setWillNotCacheDrawing(false);
                return imageView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public int getCount() {
                return videoList.size();
            }
        };
        videoGallery.setAdapter(adapter);
        // 注册监听器：选中item
        videoGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                videoSelPos = position;
                Map<String, Object> atta = videoList.get(position);
                String name = (String) atta.get("alias");
                videoDescTv.setText(name);
                videoDescTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                videoSelPos = -1;
                videoDescTv.setText("");
                videoDescTv.setVisibility(View.INVISIBLE);
            }

        });
        videoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了选中的视频
                if (videoSelPos == position) {
                    Map<String, Object> atta = videoList.get(position);
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");
                    openVideoByFilename((String) atta.get("alias"), filepath, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
        videoGallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupConfigMenu(view, CommonParam.ATTA_TYPE_VIDEO, position);
                return false;
            }
        });
    }

    /**
     * 生成音频 Gallery 列表
     */
    public void makeAudioGallery() {
        // 为 Gallery 设置 adapter
        BaseAdapter adapter = new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 音频的 ImageView
                ImageView imageView = new ImageView(classThis);
                imageView.setTag(CommonParam.ATTA_TYPE_AUDIO);
                // 音频图片
                imageView.setImageResource(R.drawable.audio_icon);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new Gallery.LayoutParams(getResources().getInteger(
                        R.integer.gallery_thumbnail_width), getResources().getInteger(
                        R.integer.gallery_thumbnail_height)));
                TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
                imageView.setBackgroundResource(typedArray.getResourceId(
                        R.styleable.Gallery_android_galleryItemBackground, 0));
                imageView.setWillNotCacheDrawing(false);
                return imageView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public int getCount() {
                return audioList.size();
            }
        };
        audioGallery.setAdapter(adapter);
        // 注册监听器：选中item
        audioGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                audioSelPos = position;
                Map<String, Object> atta = audioList.get(position);
                String name = (String) atta.get("alias");
                audioDescTv.setText(name);
                audioDescTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                audioSelPos = -1;
                audioDescTv.setText("");
                audioDescTv.setVisibility(View.INVISIBLE);
            }

        });
        // 注册监听器：点击item
        audioGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了选中的音频
                if (audioSelPos == position) {
                    HashMap<String, Object> atta = audioList.get(position);
                    // String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    // + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");
                    makePlayVoiceDialog(atta);
                }
            }
        });
        audioGallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupConfigMenu(view, CommonParam.ATTA_TYPE_AUDIO, position);
                return false;
            }
        });
    }

    /**
     * 显示音频播放对话框
     *
     * @param atta {@code HashMap<String, Object>>) 附件信息
     */
    public void makePlayVoiceDialog(HashMap<String, Object> atta) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_play_voice, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle((String) atta.get("alias"));
        dlgBuilder.setIcon(R.drawable.menu_speaker);
        dlgBuilder.setCancelable(false);

        dlgBuilder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // 播放按钮
        ImageButton startBtn = (ImageButton) layout.findViewById(R.id.play_voice_start);
        // 暂停播放按钮
        ImageButton pauseBtn = (ImageButton) layout.findViewById(R.id.play_voice_pause);
        // 停止播放按钮
        ImageButton stopBtn = (ImageButton) layout.findViewById(R.id.play_voice_stop);

        // 存放Dialog所需信息的Map
        Map<String, Object> tag = new HashMap<String, Object>();
        tag.put("atta", atta);
        startBtn.setTag(tag);

        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!playVoiceContinueFlag) {
                    // 重新开始播放
                    HashMap<String, Object> tag = (HashMap<String, Object>) v.getTag();
                    HashMap<String, Object> atta = (HashMap<String, Object>) tag.get("atta");

                    if (atta != null) {
                        if (mediaPlayer == null) {
                            mediaPlayer = getMediaplayer();
                        }
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mediaPlayer.reset();
                                    }
                                });

                                playVoiceStop();
                            }
                        });
                        classThis.playAudio(Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/" + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name"));
                        playVoiceStart();
                    } else {
                        playVoiceContinueFlag = false;
                        playVoiceStop();
                    }
                } else {
                    // 继续播放
                    playVoiceContinueFlag = false;
                    mediaPlayer.start();
                    playVoiceStart();
                }
            }
        });

        pauseBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                playVoiceContinueFlag = true;
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playVoicePause();
                } else {
                    playVoiceStop();
                }
            }
        });

        stopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                playVoiceContinueFlag = false;

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                playVoiceStop();
            }
        });

        playVoiceDlg = dlgBuilder.create();
        playVoiceDlg.show();

        // 取消按钮
        Button cancelBtn = playVoiceDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                playVoiceContinueFlag = false;

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                playVoiceStop();

                playVoiceDlg.cancel();
            }
        });
    }

    /**
     * 开始播放音频
     */
    public void playVoiceStart() {
        try {
            LinearLayout play_voice_start_layout = (LinearLayout) playVoiceDlg
                    .findViewById(R.id.play_voice_start_layout);
            LinearLayout play_voice_pause_layout = (LinearLayout) playVoiceDlg
                    .findViewById(R.id.play_voice_pause_layout);
            LinearLayout play_voice_stop_layout = (LinearLayout) playVoiceDlg.findViewById(R.id.play_voice_stop_layout);
            ImageView play_voice_iv = (ImageView) playVoiceDlg.findViewById(R.id.play_voice_iv);
            TextView play_voice_start_tv = (TextView) playVoiceDlg.findViewById(R.id.play_voice_start_tv);
            play_voice_start_layout.setVisibility(View.GONE);
            play_voice_pause_layout.setVisibility(View.VISIBLE);
            play_voice_stop_layout.setVisibility(View.VISIBLE);
            play_voice_iv.setBackgroundResource(R.drawable.voice_playing);
            AnimationDrawable pv_ad = (AnimationDrawable) play_voice_iv.getBackground();
            pv_ad.start();
            play_voice_start_tv.setText(R.string.voice_play);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放音频
     */
    public void playVoicePause() {
        try {
            LinearLayout play_voice_start_layout = (LinearLayout) playVoiceDlg
                    .findViewById(R.id.play_voice_start_layout);
            LinearLayout play_voice_pause_layout = (LinearLayout) playVoiceDlg
                    .findViewById(R.id.play_voice_pause_layout);
            LinearLayout play_voice_stop_layout = (LinearLayout) playVoiceDlg.findViewById(R.id.play_voice_stop_layout);
            ImageView play_voice_iv = (ImageView) playVoiceDlg.findViewById(R.id.play_voice_iv);
            TextView play_voice_start_tv = (TextView) playVoiceDlg.findViewById(R.id.play_voice_start_tv);
            play_voice_start_layout.setVisibility(View.VISIBLE);
            play_voice_pause_layout.setVisibility(View.GONE);
            play_voice_stop_layout.setVisibility(View.VISIBLE);
            play_voice_iv.setBackgroundResource(R.drawable.voice_pause);
            AnimationDrawable pv_ad = (AnimationDrawable) play_voice_iv.getBackground();
            pv_ad.start();
            play_voice_start_tv.setText(R.string.voice_play_continue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音频
     */
    public void playVoiceStop() {
        try {
            LinearLayout play_voice_start_layout = (LinearLayout) playVoiceDlg
                    .findViewById(R.id.play_voice_start_layout);
            LinearLayout play_voice_pause_layout = (LinearLayout) playVoiceDlg
                    .findViewById(R.id.play_voice_pause_layout);
            LinearLayout play_voice_stop_layout = (LinearLayout) playVoiceDlg.findViewById(R.id.play_voice_stop_layout);
            ImageView play_voice_iv = (ImageView) playVoiceDlg.findViewById(R.id.play_voice_iv);
            TextView play_voice_start_tv = (TextView) playVoiceDlg.findViewById(R.id.play_voice_start_tv);
            play_voice_start_layout.setVisibility(View.VISIBLE);
            play_voice_pause_layout.setVisibility(View.GONE);
            play_voice_stop_layout.setVisibility(View.VISIBLE);
            play_voice_iv.setBackgroundResource(R.drawable.voice_pause);
            AnimationDrawable pv_ad = (AnimationDrawable) play_voice_iv.getBackground();
            pv_ad.start();
            play_voice_start_tv.setText(R.string.voice_play);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示PopupMenu
     *
     * @param view {@code View} PopupMenu绑定的对象
     * @param type {@code String} 附件类型
     * @param pos  {@code int} 附件在列表中的索引
     */
    public void showPopupConfigMenu(View view, String type, int pos) {
        currentAttaType = type;
        currentAttaIndex = pos;

        popupMenu = new PopupMenu(this, view);
        // 强制显示PopupMenu图标
        forceShowPopupMenuIcon(popupMenu);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.rec_show_attach_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_atta_detail:
                        // 显示附件信息
                        showAttaDetail(currentAttaType, currentAttaIndex);
                        break;
                    default:
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu popup) {
            }
        });
        popupMenu.show();
    }

    /**
     * 检查未下载的附件信息 AsyncTask 类
     */
    private class CheckUnDownloadAttaListTask extends AsyncTask<Object, Integer, String> {
        /**
         * 需要下载的附件索引List
         */
        List<Integer> indexList = new ArrayList<Integer>();

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
            for (int i = 0, len = attaList.size(); i < len; i++) {
                LinearLayout atta_layout = (LinearLayout) attaListLayout.findViewWithTag("ATTA_" + i);
                if (atta_layout != null) {
                    // 附件状态
                    TextView infoStatus = (TextView) atta_layout.findViewById(R.id.infoStatus);
                    // 附件名称
                    TextView infoName = (TextView) atta_layout.findViewById(R.id.infoName);
                    // 信息Tag
                    HashMap<String, Object> tag = (HashMap<String, Object>) infoName.getTag();
                    HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
                    String file = (String) o.get("file");
                    if (getString(R.string.status_not_download).equals(infoStatus.getText().toString()) && !downloadAttaTaskMap.containsKey(file)) {
                        // 未下载，且不在待附件下载任务Map中
                        indexList.add(i);
                        tag.put("f", false);
                        downloadAttaList.add(tag);
                    }
                }
            }
            // 处理数据。结束============================================================================

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
            // 隐藏等待窗口
            unWait();
            continueDownloadAllBtn.setClickable(true);
            continueDownloadAllBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (indexList.size() > 0) {
                    SnackbarUtil.ShortSnackbar(contentView, indexList.size() + " 个附件已添加到下载计划中！", SnackbarUtil.Info).show();
                }
                if (!isDownloading) {
                    // 下载下一个附件
                    downloadNextAtta();
                }
            }
        }
    }

    /**
     * 重新下载所有附件 AsyncTask 类
     */
    private class ReDownloadAllAttaListTask extends AsyncTask<Object, Integer, String> {
        /**
         * 需要下载的附件索引List
         */
        List<Integer> indexList = new ArrayList<Integer>();

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
            if (downloadAttaList.size() > 1) {
                for (int i = 1, len = downloadAttaList.size() - 1; i <= len; i++) {
                    downloadAttaList.remove(1);
                }
            }
            for (int i = 0, len = attaList.size(); i < len; i++) {
                LinearLayout atta_layout = (LinearLayout) attaListLayout.findViewWithTag("ATTA_" + i);
                if (atta_layout != null) {
                    // 附件状态
                    TextView infoStatus = (TextView) atta_layout.findViewById(R.id.infoStatus);
                    // 附件名称
                    TextView infoName = (TextView) atta_layout.findViewById(R.id.infoName);
                    // 信息Tag
                    HashMap<String, Object> tag = (HashMap<String, Object>) infoName.getTag();
                    HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
                    String file = (String) o.get("file");
                    if (!downloadAttaTaskMap.containsKey(file)) {
                        // 未下载，且不在待附件下载任务Map中
                        indexList.add(i);
                        tag.put("f", true);
                        downloadAttaList.add(tag);
                    }
                }
            }
            // 处理数据。结束============================================================================

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
            // 隐藏等待窗口
            unWait();
            reDownloadAllBtn.setClickable(true);
            reDownloadAllBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (indexList.size() > 0) {
                    SnackbarUtil.ShortSnackbar(contentView, indexList.size() + " 个附件已添加到下载计划中！", SnackbarUtil.Info).show();
                }
                if (!isDownloading) {
                    // 下载下一个附件
                    downloadNextAtta();
                }
            }
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        contentView = (ScrollView) findViewById(R.id.contentView);
        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        titleTv = (TextView) findViewById(R.id.titleTv);
        stage1Tv = (TextView) findViewById(R.id.stage1Tv);
        stage2Tv = (TextView) findViewById(R.id.stage2Tv);
        cat1Tv = (TextView) findViewById(R.id.cat1Tv);
        cat2Tv = (TextView) findViewById(R.id.cat2Tv);
        lyTv = (TextView) findViewById(R.id.lyTv);
        skTv = (TextView) findViewById(R.id.skTv);
        infoTv = (TextView) findViewById(R.id.infoTv);
        ctimeTv = (TextView) findViewById(R.id.ctimeTv);
        unameTv = (TextView) findViewById(R.id.unameTv);
        photoTitleLayout = (LinearLayout) findViewById(R.id.photoTitleLayout);
        videoTitleLayout = (LinearLayout) findViewById(R.id.videoTitleLayout);
        audioTitleLayout = (LinearLayout) findViewById(R.id.audioTitleLayout);
        photoContentLayout = (LinearLayout) findViewById(R.id.photoContentLayout);
        videoContentLayout = (LinearLayout) findViewById(R.id.videoContentLayout);
        audioContentLayout = (LinearLayout) findViewById(R.id.audioContentLayout);
        photoNumTv = (TextView) findViewById(R.id.photoNumTv);
        photoGallery = (Gallery) findViewById(R.id.photoGallery);
        photoDescTv = (TextView) findViewById(R.id.photoDescTv);
        videoNumTv = (TextView) findViewById(R.id.videoNumTv);
        videoGallery = (Gallery) findViewById(R.id.videoGallery);
        videoDescTv = (TextView) findViewById(R.id.videoDescTv);
        audioNumTv = (TextView) findViewById(R.id.audioNumTv);
        audioGallery = (Gallery) findViewById(R.id.audioGallery);
        audioDescTv = (TextView) findViewById(R.id.audioDescTv);
        attaTitleLayout = (LinearLayout) findViewById(R.id.attaTitleLayout);
        attaNumTv = (TextView) findViewById(R.id.attaNumTv);
        continueDownloadAllBtn = (Button) findViewById(R.id.continueDownloadAllBtn);
        reDownloadAllBtn = (Button) findViewById(R.id.reDownloadAllBtn);
        attaHelpBtn = (PrintView) findViewById(R.id.attaHelpBtn);
        attaListLayout = (LinearLayout) findViewById(R.id.attaListLayout);
        // 界面相关参数。结束===============================
    }
}
