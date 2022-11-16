/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

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
 * 巡视_巡视任务_查阅页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsTaskShowReverseLandActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 主界面
     */
    ScrollView contentView;
    /**
     * 导航栏名称
     */
    TextView titleBarName;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 回到主页按钮
     */
    ImageButton homeBtn;
    // 界面相关参数。开始===============================
    /**
     * 返回
     */
    private Button goBackBtn;

    private TextView infoStatusTv;
    private TextView titleTv;
    private TextView ctypeNameTv;
    private TextView planatimeTv;
    private TextView planbtimeTv;
    private TextView resTitleTv;
    private TextView resNumTv;
    private TextView renwDescTv;
    private TextView fzrNameTv;
    private TextView ryapNameTv;
    private TextView ryapNumTv;
    private TextView zhunbDescTv;
    private TextView realatimeTv;
    private TextView realbtimeTv;
    private TextView memoTv;
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

    // 音频相关参数。开始================================
    private LinearLayout play_voice_layout;
    private LinearLayout play_voice_start_layout;
    private LinearLayout play_voice_pause_layout;
    private LinearLayout play_voice_stop_layout;
    // 播放按钮
    private ImageButton startBtn;
    // 暂停播放按钮
    private ImageButton pauseBtn;
    // 停止播放按钮
    private ImageButton stopBtn;
    private ImageView play_voice_iv;
    private TextView play_voice_start_tv;
    /**
     * 继续播放音频标记
     */
    private boolean playVoiceContinueFlag;
    // 音频相关参数。结束================================

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
     * 任务信息
     */
    private HashMap<String, Object> bizInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = InsTaskShowReverseLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");

        setContentView(R.layout.ins_task_show);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("任务详情");

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
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!playVoiceContinueFlag) {
                    // 重新开始播放
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
                    String attachment = (String) bizInfo.get("atta");
                    if (CommonUtil.checkNB(attachment)) {
                        JSONObject audioInfo = JSONObject.parseObject(attachment);
                        if (audioInfo != null) {
                            String audioName = audioInfo.getString("name");

                            classThis.playAudio(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/" + CommonParam.PROJECT_NAME + "/ins/" + audioName);
                            playVoiceStart();
                        }
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
        StatusBarUtil.setStatusBarMode(this, false, R.color.background_title_blue_dark);
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
        playVoiceContinueFlag = false;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        playVoiceStop();

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

            // 下载附件。开始================================================================
//            // 待下载的附件List
//            List<String> attaFileList = new ArrayList<String>();
//            // 附件
//            String _attachment = (String) bizInfo.get("attachment");
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
//            // 业务对应的绑定模板编号
//            String temp_save = (String) bizInfo.get("temp_save");
//            // 附件保存目录
//            File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
//                    + CommonParam.PROJECT_NAME + "/ins/" + temp_save);
//            for (int i = 0, len = attaFileList.size(); i < len; i++) {
//                String _file = attaFileList.get(i);
//                File attaFile = new File(saveDir_atta, _file);
//
//                if (!attaFile.exists()) {
//                    Map<String, Object> downloadResult = downloadFile("http://" + baseApp.serverAddr
//                            + "/UploadFiles/" + temp_save + "/" + _file, attaFile.getAbsolutePath(), null);
//                }
//            }
            // 下载附件。结束================================================================
            // 处理数据。结束============================================================================

            // 设置字段及按钮
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
                // 任务状态
                String infoStatus = (String) bizInfo.get("V_INFO_STATUS");
                if ("0".equals(infoStatus)) {
                    infoStatusTv.setText("未巡视");
                    infoStatusTv.setTextColor(getResources().getColor(R.color.text_red));
                } else if ("1".equals(infoStatus)) {
                    infoStatusTv.setText("未完成");
                    infoStatusTv.setTextColor(getResources().getColor(R.color.text_purple));
                } else {
                    infoStatusTv.setText("已完成");
                    infoStatusTv.setTextColor(getResources().getColor(R.color.text_green_dark));
                }
                titleTv.setText(CommonUtil.N2B((String) bizInfo.get("title")));

                String ctype = (String) bizInfo.get("ctype");
                String ctype_name = null;
                if ("2".equals(ctype)) {
                    ctype_name = "停水检查";
                } else if ("3".equals(ctype)) {
                    ctype_name = "特殊检查";
                } else {
                    ctype_name = "日常检查";
                }
                ctypeNameTv.setText(ctype_name);
                planatimeTv.setText(CommonUtil.N2B((String) bizInfo.get("planatime")));
                planbtimeTv.setText(CommonUtil.N2B((String) bizInfo.get("planbtime")));

                String res_title = CommonUtil.N2B((String) bizInfo.get("res_title"));
                int resNum = 0;
                resTitleTv.setText(res_title);
                if (res_title.length() > 0) {
                    if (res_title.indexOf(",") == -1) {
                        resNum = 1;
                    } else {
                        resNum = res_title.split(",").length;
                    }
                }
                resNumTv.setText("(" + resNum + ")");
                renwDescTv.setText(CommonUtil.N2B((String) bizInfo.get("renw_desc")));

                // 用户名字
                String user_name = CommonUtil.N2B((String) bizInfo.get("user_name"));
                int ryapNum = 0;
                if (user_name.contains("#")) {
                    String[] user_name_array = user_name.split("#");

                    fzrNameTv.setText(user_name_array[0]);
                    if (user_name_array.length > 1) {
                        String ryap = CommonUtil.N2B(user_name_array[1]);
                        ryapNameTv.setText(ryap);
                        if (ryap.length() > 0) {
                            if (ryap.indexOf(",") == -1) {
                                ryapNum = 1;
                            } else {
                                ryapNum = ryap.split(",").length;
                            }
                        }
                    }
                }
                ryapNumTv.setText("(" + +ryapNum + ")");

                zhunbDescTv.setText(CommonUtil.N2B((String) bizInfo.get("zhunb_desc")));
                realatimeTv.setText(CommonUtil.N2B((String) bizInfo.get("realatime")));
                realbtimeTv.setText(CommonUtil.N2B((String) bizInfo.get("realbtime")));
                String memo = CommonUtil.N2B((String) bizInfo.get("memo"));
                if (memo.contains("：")) {
                    memo = memo.substring(memo.indexOf("：") + 1);
                }
                memoTv.setText(memo);

                String starttime = (String) bizInfo.get("realatime");
                String endtime = (String) bizInfo.get("realbtime");
                if (CommonUtil.checkNB(starttime) && CommonUtil.checkNB(endtime)) {
                    File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins");
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    String attachment = (String) bizInfo.get("atta");
                    if (CommonUtil.checkNB(attachment)) {
                        JSONObject audioInfo = JSONObject.parseObject(attachment);
                        if (audioInfo != null) {
                            String audioName = audioInfo.getString("name");
                            File audioFile = new File(fileDir.getAbsolutePath() + "/" + audioName);
                            if (audioFile.exists() && audioFile.isFile()) {
                                play_voice_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
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
                // goBack();
            }
        }
    }

    /**
     * 开始播放音频
     */
    public void playVoiceStart() {
        try {
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
            String attachment = (String) bizInfo.get("attachment");
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
                // 业务对应的绑定模板编号
                String temp_save = (String) bizInfo.get("temp_save");
                // 附件保存目录
                File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/" + temp_save);
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
                                // 业务对应的绑定模板编号
                                String temp_save = (String) bizInfo.get("temp_save");
                                // 附件文件
                                File attaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/" + CommonParam.PROJECT_NAME + "/ins/" + temp_save + "/" + fileName);
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
                    // 附件文件
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

                // 业务对应的绑定模板编号
                String temp_save = (String) bizInfo.get("temp_save");
                // 附件保存目录
                File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/" + temp_save);
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
                    // 业务对应的绑定模板编号
                    String temp_save = (String) bizInfo.get("temp_save");
                    // 下载文件名
                    String urlString = "http://" + baseApp.serverAddr + "/UploadFiles/" + temp_save + "/" + remoteFileName;

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
//                        + "/UploadFiles/" + temp_save + "/" + remoteFileName, attaFile.getAbsolutePath(), null, needShowNetAlert);
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
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        // 界面相关参数。开始===============================
        infoStatusTv = (TextView) findViewById(R.id.infoStatusTv);
        titleTv = (TextView) findViewById(R.id.titleTv);
        ctypeNameTv = (TextView) findViewById(R.id.ctypeNameTv);
        planatimeTv = (TextView) findViewById(R.id.planatimeTv);
        planbtimeTv = (TextView) findViewById(R.id.planbtimeTv);
        resTitleTv = (TextView) findViewById(R.id.resTitleTv);
        resNumTv = (TextView) findViewById(R.id.resNumTv);
        renwDescTv = (TextView) findViewById(R.id.renwDescTv);
        fzrNameTv = (TextView) findViewById(R.id.fzrNameTv);
        ryapNameTv = (TextView) findViewById(R.id.ryapNameTv);
        ryapNumTv = (TextView) findViewById(R.id.ryapNumTv);
        zhunbDescTv = (TextView) findViewById(R.id.zhunbDescTv);
        realatimeTv = (TextView) findViewById(R.id.realatimeTv);
        realbtimeTv = (TextView) findViewById(R.id.realbtimeTv);
        memoTv = (TextView) findViewById(R.id.memoTv);
        attaTitleLayout = (LinearLayout) findViewById(R.id.attaTitleLayout);
        attaNumTv = (TextView) findViewById(R.id.attaNumTv);
        continueDownloadAllBtn = (Button) findViewById(R.id.continueDownloadAllBtn);
        reDownloadAllBtn = (Button) findViewById(R.id.reDownloadAllBtn);
        attaHelpBtn = (PrintView) findViewById(R.id.attaHelpBtn);
        attaListLayout = (LinearLayout) findViewById(R.id.attaListLayout);
        play_voice_layout = (LinearLayout) findViewById(R.id.play_voice_layout);
        play_voice_start_layout = (LinearLayout) findViewById(R.id.play_voice_start_layout);
        play_voice_pause_layout = (LinearLayout) findViewById(R.id.play_voice_pause_layout);
        play_voice_stop_layout = (LinearLayout) findViewById(R.id.play_voice_stop_layout);
        startBtn = (ImageButton) findViewById(R.id.play_voice_start);
        pauseBtn = (ImageButton) findViewById(R.id.play_voice_pause);
        stopBtn = (ImageButton) findViewById(R.id.play_voice_stop);
        play_voice_iv = (ImageView) findViewById(R.id.play_voice_iv);
        play_voice_start_tv = (TextView) findViewById(R.id.play_voice_start_tv);
        // 界面相关参数。结束===============================
    }
}
