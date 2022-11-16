/*
 * Copyright (c) 2020 乔勇(Jacky Qiao) 版权所有
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
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.PopupMenu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡视_现场检查记录_缺陷描述_查阅页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsJcDescShowReverseLandActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 导航栏名称
     */
    TextView titleBarName;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    // 界面相关参数。开始===============================
    /**
     * 返回
     */
    private Button goBackBtn;

    private TextView titleTv;
    private TextView normalTv;
    private TextView descTv;
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
    // 界面相关参数。结束===============================
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 信息
     */
    private HashMap<String, Object> infoObj;
    /**
     * 信息编号
     */
    private String infoId;
    /**
     * 常见缺陷List
     */
    private List<String> memo_e_list;
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

        classThis = InsJcDescShowReverseLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");

        setContentView(R.layout.ins_jc_desc_show);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("多媒体描述");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
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
            ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT * FROM t_szfgs_sgxunsjcjl_son model WHERE model.valid='1' and model.ids=? and model.quid=?",
                            new String[]{infoId, (String) baseApp.getLoginUser().get("ids")});
            if (recList.size() > 0) {
                infoObj = recList.get(0);
            }
            if (infoObj == null) {
                return result;
            }

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
            // 处理数据。结束============================================================================

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);
            // 显示图片
            publishProgress(PROGRESS_SHOW_PHOTO);
            // 显示视频
            publishProgress(PROGRESS_SHOW_VIDEO);
            // 显示音频
            publishProgress(PROGRESS_SHOW_AUDIO);
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
                titleTv.setText(CommonUtil.N2B((String) infoObj.get("c")));

                // 处理检查描述。开始==============================================
                // 检查描述
                String memo = CommonUtil.N2B((String) infoObj.get("memo"));
                JSONObject memo_jo = JSONObject.parseObject(memo);
                // 常见缺陷
                JSONArray memo_e_array = null;
                // 检查描述文本
                String memo_d_str = null;
                if (memo_jo == null) {
                    memo_jo = new JSONObject();
                }
                memo_e_array = memo_jo.getJSONArray("e");
                memo_d_str = memo_jo.getString("d");
                if (memo_e_array != null) {
                    memo_e_list = memo_e_array.toJavaList(String.class);
                } else {
                    memo_e_list = new ArrayList<String>();
                }
                if (memo_e_list.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (String s : memo_e_list) {
                        sb.append(",").append(s);
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(0);
                    }
                    normalTv.setText(sb.toString());
                }
                descTv.setText(memo_d_str);
                // 处理检查描述。结束==============================================
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

            if (CommonParam.RESULT_ERROR.equals(result)) {
                show("信息出错！");
                goBack();
            }
        }
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
        Builder dlgBuilder = new Builder(this);
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
        photoGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

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
        photoGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了选中的图片
                if (photoSelPos == position) {
                    HashMap<String, Object> atta = photoList.get(position);
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");
                    // 屏幕方向
                    int screenOrientation = -1;
                    if (!baseApp.isReverseRotate) {
                        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else {
                        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    }
                    openPicByFilename((String) atta.get("alias"), filepath, screenOrientation);
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
        videoGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

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
        videoGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了选中的视频
                if (videoSelPos == position) {
                    Map<String, Object> atta = videoList.get(position);
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");
                    // 屏幕方向
                    int screenOrientation = -1;
                    if (!baseApp.isReverseRotate) {
                        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else {
                        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    }
                    openVideoByFilename((String) atta.get("alias"), filepath, screenOrientation);
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
        audioGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

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
        audioGallery.setOnItemClickListener(new OnItemClickListener() {

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
        Builder dlgBuilder = new Builder(this);

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
     * 查找view
     */
    public void findViews() {
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        // 界面相关参数。开始===============================
        titleTv = (TextView) findViewById(R.id.titleTv);
        normalTv = (TextView) findViewById(R.id.normalTv);
        descTv = (TextView) findViewById(R.id.descTv);

        photoNumTv = (TextView) findViewById(R.id.photoNumTv);
        photoGallery = (Gallery) findViewById(R.id.photoGallery);
        photoDescTv = (TextView) findViewById(R.id.photoDescTv);
        videoNumTv = (TextView) findViewById(R.id.videoNumTv);
        videoGallery = (Gallery) findViewById(R.id.videoGallery);
        videoDescTv = (TextView) findViewById(R.id.videoDescTv);
        audioNumTv = (TextView) findViewById(R.id.audioNumTv);
        audioGallery = (Gallery) findViewById(R.id.audioGallery);
        audioDescTv = (TextView) findViewById(R.id.audioDescTv);
        // 界面相关参数。结束===============================
    }

}
