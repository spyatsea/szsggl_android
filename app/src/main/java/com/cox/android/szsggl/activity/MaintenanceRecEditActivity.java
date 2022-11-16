/*
 * Copyright (c) 2017 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hzw.doodle.DoodleParams;
import cn.hzw.doodle.DoodleView;

/**
 * 水工维修_项目记录信息_查阅_编辑页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MaintenanceRecEditActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 取消
     */
    private Button cancelBtn;
    /**
     * 提交
     */
    private Button submitBtn;
    // 界面相关参数。开始===============================
    private EditText titleTv;
    private Spinner stage1Spinner;
    private Spinner stage2Spinner;
    private Spinner cat1Spinner;
    private Spinner cat2Spinner;
    private EditText lyTv;
    private Spinner skSpinner;
    private EditText infoTv;
    /**
     * 拍照按钮
     */
    private Button photoBtn;
    /**
     * 摄像按钮
     */
    private Button videoBtn;
    /**
     * 录音按钮
     */
    private Button audioBtn;
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
     * 信息
     */
    private HashMap<String, Object> infoObj;
    /**
     * 信息编号
     */
    private String infoId;
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 进度List
     */
    private ArrayList<HashMap<String, Object>> stage1List;
    /**
     * 状态List
     */
    private ArrayList<HashMap<String, Object>> stage2List;
    /**
     * 状态Map
     */
    private HashMap<String, ArrayList<HashMap<String, Object>>> stage2Map;

    /**
     * 类别List
     */
    private ArrayList<HashMap<String, Object>> cat1List;
    /**
     * 类别List
     */
    private ArrayList<HashMap<String, Object>> cat2List;
    /**
     * 类别Map
     */
    private HashMap<String, ArrayList<HashMap<String, Object>>> cat2Map;
    /**
     * 受控List
     */
    private List<Map<String, Object>> skList = new ArrayList<Map<String, Object>>();
    /**
     * 基本信息编号
     */
    private String mInfoId;
    // 进度
    private String mStage1;
    // 状态
    private String mStage2;
    // 主类
    private String mCat1;
    // 子类
    private String mCat2;
    /**
     * 附件名称Dialog
     */
    private AlertDialog attaNameDlg;
    /**
     * 删除附件Dialog
     */
    private AlertDialog deleteAttaDlg;
    /**
     * 创建手绘图Dialog
     */
    private AlertDialog createDoodleDlg;
    /**
     * 弹出菜单
     */
    private PopupMenu popupMenu;
    /**
     * 录音Dialog
     */
    private AlertDialog recordVoiceDlg;
    /**
     * 播音Dialog
     */
    private AlertDialog playVoiceDlg;
    /**
     * 继续播放音频标记
     */
    private boolean playVoiceContinueFlag;
    /**
     * 录音器
     */
    private MediaRecorder mediaRecorder;
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
    /**
     * 拍照临时文件
     */
    private File capPhotoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
            + CommonParam.PROJECT_NAME + "/temp/a.jpg");
    /**
     * 摄像临时文件
     */
    private File capVideoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
            + CommonParam.PROJECT_NAME + "/temp/a.mp4");
    /**
     * 录音临时文件
     */
    private File capAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
            + CommonParam.PROJECT_NAME + "/temp/a.m4a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MaintenanceRecEditActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        mInfoId = data.getString("mInfoId");
        mStage1 = data.getString("stage1", "");
        mStage2 = data.getString("stage2", "");
        mCat1 = data.getString("cat1", "");
        mCat2 = data.getString("cat2", "");

        setContentView(R.layout.maintenance_rec_edit);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("项目记录：新建");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeCancelDialog();
            }
        });
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
        // 拍照
        photoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri capUri = FileProvider.getUriForFile(classThis, CommonParam.FILE_PROVIDER_NAME, capPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, capUri);
                startActivityForResult(intent, CommonParam.REQUESTCODE_CAMERA);
            }
        });
        // 摄像
        videoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri capUri = FileProvider.getUriForFile(classThis, CommonParam.FILE_PROVIDER_NAME, capVideoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, capUri);
                // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                startActivityForResult(intent, CommonParam.REQUESTCODE_VIDEO);
            }
        });
        // 录音
        audioBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示音频录制对话框
                makeRecVoiceDialog();
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
        } else if (resultCode != Activity.RESULT_OK) {
            return;
        }
        File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + CommonParam.PROJECT_NAME + "/ins");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        if (requestCode == CommonParam.REQUESTCODE_CAMERA_CROP) {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            intent.setData(Uri.parse(capPhotoFile.getAbsolutePath()));
            intent.putExtra("crop", true);
            intent.putExtra("outputX", CommonParam.SHOW_IMAGE_WIDTH);
            intent.putExtra("outputY", CommonParam.SHOW_IMAGE_HEIGHT);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CommonParam.REQUESTCODE_CAMERA_CROP);
        } else if (requestCode == CommonParam.REQUESTCODE_CAMERA) {
            // 拍照
            // 图片
            String attaName = CommonUtil.GetNextID() + ".jpg";
            File attaFile = new File(fileDir.getAbsolutePath() + "/" + attaName);
            boolean bFlag = false;
            try {
                // 方法一：保存原图
                //new FileUtil().copyFile(capImageFile.getAbsolutePath(), attaFile.getAbsolutePath());
                // 方法二：进行处理
                Bitmap bm = CommonUtil.decodeSampledBitmapFromResource(capPhotoFile.getAbsolutePath(),
                        CommonParam.SHOW_IMAGE_WIDTH, CommonParam.SHOW_IMAGE_HEIGHT);
                new FileUtil().saveBitmap(bm, attaFile);
                bFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                capPhotoFile.delete();
            }
            if (bFlag) {
                HashMap<String, Object> atta = new HashMap<String, Object>();
                atta.put("type", CommonParam.ATTA_TYPE_PHOTO);
                atta.put("name", attaName);
                atta.put("alias", attaName);
                atta.put("size", new FileUtil().getFileSize(attaFile));

                photoList.add(atta);
                BaseAdapter adapter = (BaseAdapter) photoGallery.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                // 设置图片数量
                photoNumTv.setText("(" + photoList.size() + ")");
            }
        } else if (requestCode == CommonParam.REQUESTCODE_VIDEO) {
            // 摄像
            // 视频
            String attaName = CommonUtil.GetNextID() + ".mp4";
            File attaFile = new File(fileDir.getAbsolutePath() + "/" + attaName);
            // 正常保存标志
            boolean bFlag = false;
            try {
                new FileUtil().copyFile(capVideoFile.getAbsolutePath(), attaFile.getAbsolutePath());
                bFlag = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                capVideoFile.delete();
            }
            if (bFlag) {
                HashMap<String, Object> atta = new HashMap<String, Object>();
                atta.put("type", CommonParam.ATTA_TYPE_VIDEO);
                atta.put("name", attaName);
                atta.put("alias", attaName);
                atta.put("size", new FileUtil().getFileSize(attaFile));

                videoList.add(atta);
                BaseAdapter adapter = (BaseAdapter) videoGallery.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                // 设置视频数量
                videoNumTv.setText("(" + videoList.size() + ")");
            }
        } else if (requestCode == CommonParam.REQUESTCODE_DOODLE) {
            Bundle data = intent.getExtras();

            HashMap<String, Object> newAtta = (HashMap<String, Object>) data.getSerializable("info");
            photoList.add(newAtta);
            BaseAdapter adapter = (BaseAdapter) photoGallery.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            // 设置图片数量
            photoNumTv.setText("(" + photoList.size() + ")");
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
            // 处理stageList。开始==========================================
            ArrayList<HashMap<String, Object>> stageList = infoTool
                    .getInfoMapList(
                            "SELECT model.c1 c1, model.c2 c2 FROM t_szfgs_sgwxstage model WHERE model.valid='1' ORDER BY model.pxbh ASC",
                            new String[]{});
            stage1List = new ArrayList<HashMap<String, Object>>();
            stage2List = new ArrayList<HashMap<String, Object>>();
            stage2Map = new HashMap<String, ArrayList<HashMap<String, Object>>>();
            for (HashMap<String, Object> o : stageList) {
                String c1 = (String) o.get("c1");
                String c2 = (String) o.get("c2");

                ArrayList<HashMap<String, Object>> list = stage2Map.get(c1);
                if (list == null) {
                    list = new ArrayList<HashMap<String, Object>>();
                    stage2Map.put(c1, list);

                    HashMap<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("code", c1);
                    map1.put("name", c1);
                    stage1List.add(map1);
                }
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("code", c2);
                map.put("name", c2);
                list.add(map);
            }
            // 处理stageList。结束==========================================

            // 处理catList。开始==========================================
            ArrayList<HashMap<String, Object>> catList = infoTool
                    .getInfoMapList(
                            "SELECT model.c1 c1, model.c2 c2 FROM t_szfgs_sgwxcat model WHERE model.valid='1' ORDER BY model.pxbh ASC",
                            new String[]{});
            cat1List = new ArrayList<HashMap<String, Object>>();
            cat2List = new ArrayList<HashMap<String, Object>>();
            cat2Map = new HashMap<String, ArrayList<HashMap<String, Object>>>();
            for (HashMap<String, Object> o : catList) {
                String c1 = (String) o.get("c1");
                String c2 = (String) o.get("c2");

                ArrayList<HashMap<String, Object>> list = cat2Map.get(c1);
                if (list == null) {
                    list = new ArrayList<HashMap<String, Object>>();
                    cat2Map.put(c1, list);

                    HashMap<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("code", c1);
                    map1.put("name", c1);
                    cat1List.add(map1);
                }
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("code", c2);
                map.put("name", c2);
                list.add(map);
            }
            // 处理catList。结束==========================================

            HashMap<String, Object> map_sk_0 = new HashMap<String, Object>();
            map_sk_0.put("code", "0");
            map_sk_0.put("name", "否");
            HashMap<String, Object> map_sk_1 = new HashMap<String, Object>();
            map_sk_1.put("code", "1");
            map_sk_1.put("name", "是");
            skList.add(map_sk_0);
            skList.add(map_sk_1);

            photoList = new ArrayList<HashMap>();
            videoList = new ArrayList<HashMap>();
            audioList = new ArrayList<HashMap>();
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
                // stage1Spinner。开始=================================================
                SimpleAdapter stage1Adapter = new SimpleAdapter(classThis, stage1List,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                stage1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stage1Spinner.setAdapter(stage1Adapter);
                stage1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                        Map<String, String> m = (HashMap<String, String>) parent.getItemAtPosition(pos);
                        stage2Spinner.setSelection(0);
                        SimpleAdapter stage2Adapter = (SimpleAdapter) stage2Spinner.getAdapter();
                        stage2List.clear();
                        stage2List.addAll(stage2Map.get(m.get("code")));
                        stage2Adapter.notifyDataSetChanged();

                        mStage1 = (String) m.get("code");
                        // mStage2 = (String) stage2List.get(0).get("code");
                        stage2Spinner.setSelection(0);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // stage1Spinner。结束=================================================

                // stage2Spinner。开始=================================================
                SimpleAdapter stage2Adapter = new SimpleAdapter(classThis, stage2List,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                stage2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stage2Spinner.setAdapter(stage2Adapter);
                stage2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                        Map<String, String> m = (HashMap<String, String>) parent.getItemAtPosition(pos);
                        mStage2 = (String) m.get("code");
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // stage2Spinner。结束=================================================

                // cat1Spinner。开始=================================================
                SimpleAdapter cat1Adapter = new SimpleAdapter(classThis, cat1List,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                cat1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cat1Spinner.setAdapter(cat1Adapter);
                cat1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                        Map<String, String> m = (HashMap<String, String>) parent.getItemAtPosition(pos);
                        cat2Spinner.setSelection(0);
                        SimpleAdapter cat2Adapter = (SimpleAdapter) cat2Spinner.getAdapter();
                        cat2List.clear();
                        cat2List.addAll(cat2Map.get(m.get("code")));
                        cat2Adapter.notifyDataSetChanged();

                        mCat1 = (String) m.get("code");
                        // mCat2 = (String) cat2List.get(0).get("code");
                        cat2Spinner.setSelection(0);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // cat1Spinner。结束=================================================

                // cat2Spinner。开始=================================================
                SimpleAdapter cat2Adapter = new SimpleAdapter(classThis, cat2List,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                cat2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cat2Spinner.setAdapter(cat2Adapter);
                cat2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                        Map<String, String> m = (HashMap<String, String>) parent.getItemAtPosition(pos);
                        mCat2 = (String) m.get("code");
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // cat2Spinner。结束=================================================

                // skSpinner。开始=================================================
                SimpleAdapter skAdapter = new SimpleAdapter(classThis, skList,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                skAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                skSpinner.setAdapter(skAdapter);
                skSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // skSpinner。结束=================================================

                if (CommonUtil.checkNB(mStage1)) {
                    int mIndex = 0;
                    for (int i = 0, len = stage1List.size(); i < len; i++) {
                        HashMap<String, Object> m = stage1List.get(i);
                        String code = (String) m.get("code");
                        if (code.equals(mStage1)) {
                            mIndex = i;
                            break;
                        }
                    }
                    if (mIndex > 0) {
                        stage1Spinner.setSelection(mIndex);
                    }
                }
                if (CommonUtil.checkNB(mStage2)) {
                    int mIndex = 0;
                    for (int i = 0, len = stage2List.size(); i < len; i++) {
                        HashMap<String, Object> m = stage2List.get(i);
                        String code = (String) m.get("code");
                        if (code.equals(mStage2)) {
                            mIndex = i;
                            break;
                        }
                    }
                    if (mIndex > 0) {
                        stage2Spinner.setSelection(mIndex);
                    }
                }
                if (CommonUtil.checkNB(mCat1)) {
                    int mIndex = 0;
                    for (int i = 0, len = cat1List.size(); i < len; i++) {
                        HashMap<String, Object> m = cat1List.get(i);
                        String code = (String) m.get("code");
                        if (code.equals(mCat1)) {
                            mIndex = i;
                            break;
                        }
                    }
                    if (mIndex > 0) {
                        cat1Spinner.setSelection(mIndex);
                    }
                }
                if (CommonUtil.checkNB(mCat2)) {
                    int mIndex = 0;
                    for (int i = 0, len = cat2List.size(); i < len; i++) {
                        HashMap<String, Object> m = cat2List.get(i);
                        String code = (String) m.get("code");
                        if (code.equals(mCat2)) {
                            mIndex = i;
                            break;
                        }
                    }
                    if (mIndex > 0) {
                        cat2Spinner.setSelection(mIndex);
                    }
                }
                lyTv.setText("PDA现场采集");
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
     * 编辑附件名称
     *
     * @param type {@code String} 附件类型
     * @param pos  {@code int} 附件在列表中的索引
     */
    public void editAttaName(String type, int pos) {
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
        makeEditAttaNameDialog(atta);
    }

    /**
     * 删除附件
     *
     * @param type {@code String} 附件类型
     * @param pos  {@code int} 附件在列表中的索引
     */
    public void deleteAtta(String type, int pos) {
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
        makeDeleteAttaDialog(atta);
    }

    /**
     * 创建手绘图
     *
     * @param type {@code String} 附件类型
     * @param pos  {@code int} 附件在列表中的索引
     */
    public void createDoodle(String type, int pos) {
        HashMap<String, Object> atta = null;
        if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
            // 图片
            atta = photoList.get(pos);
            makeCreateDoodleDialog(atta);
        }
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
                androidx.appcompat.app.AlertDialog dlg = (androidx.appcompat.app.AlertDialog) v.getTag();
                dlg.cancel();
            }
        });
    }

    /**
     * 显示编辑附件名称对话框
     *
     * @param atta {@code Map<String, Object>} 绑定的信息
     */
    public void makeEditAttaNameDialog(HashMap<String, Object> atta) {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_atta_edit_name, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.ins_title_atta_filename);
        dlgBuilder.setIcon(R.drawable.menu_comment_edit);
        dlgBuilder.setCancelable(true);

        // 附件名称
        final EditText attaNameTv = (EditText) layout.findViewById(R.id.attaNameTv);

        String name = CommonUtil.N2B((String) atta.get("alias"));
        name = name.substring(0, name.lastIndexOf("."));
        attaNameTv.setText(name);

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

        attaNameDlg = dlgBuilder.create();
        attaNameDlg.show();

        // 确定按钮
        Button confirmBtn = attaNameDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = attaNameDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 绑定数据
        confirmBtn.setTag(atta);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 附件名称
                String attaName = attaNameTv.getText().toString().trim();
                if (!CommonUtil.checkNB(attaName)) {
                    show("请输入文件名！");
                    return;
                }

                // 附件信息
                HashMap<String, Object> atta = (HashMap<String, Object>) v.getTag();

                // 附件类型
                String type = (String) atta.get("type");
                // 附件文件名
                String file = (String) atta.get("name");

                if (CommonUtil.checkNB(attaName)) {
                    attaName = attaName + file.substring(file.lastIndexOf("."));
                } else {
                    attaName = file;
                }

                int attaIndex = -1;
                atta.put("alias", attaName);
                if (type.equals(CommonParam.ATTA_TYPE_PHOTO)) {
                    attaIndex = photoList.indexOf(atta);
                    if (attaIndex == photoSelPos) {
                        photoDescTv.setText(attaName);
                    }
                } else if (type.equals(CommonParam.ATTA_TYPE_VIDEO)) {
                    attaIndex = videoList.indexOf(atta);
                    if (attaIndex == videoSelPos) {
                        videoDescTv.setText(attaName);
                    }
                } else if (type.equals(CommonParam.ATTA_TYPE_AUDIO)) {
                    attaIndex = audioList.indexOf(atta);
                    if (attaIndex == audioSelPos) {
                        audioDescTv.setText(attaName);
                    }
                }
                // else if (type.equals(CommonParam.FILE_TYPE_VIDEO)) {
                // if (CommonUtil.checkNB(attaName)) {
                // videoDescTv.setVisibility(View.VISIBLE);
                // } else {
                // videoDescTv.setVisibility(View.INVISIBLE);
                // }
                // videoDescTv.setText(attaName);
                // atta.put("备注", attaName);
                // }
                attaNameDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                attaNameDlg.cancel();
            }
        });
    }

    /**
     * 显示删除附件对话框
     *
     * @param atta {@code Map<String, Object>} 绑定的信息
     */
    public void makeDeleteAttaDialog(HashMap<String, Object> atta) {
        // 附件类型
        String type = (String) atta.get("type");

        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        if (type.equals(CommonParam.ATTA_TYPE_PHOTO)) {
            dlgBuilder.setMessage(R.string.alert_whether_delete_photo);
        } else if (type.equals(CommonParam.ATTA_TYPE_VIDEO)) {
            dlgBuilder.setMessage(R.string.alert_whether_delete_video);
        } else {
            dlgBuilder.setMessage(R.string.alert_whether_delete_audio);
        }
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(true);

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

        deleteAttaDlg = dlgBuilder.create();
        deleteAttaDlg.show();

        // 确定按钮
        Button confirmBtn = deleteAttaDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = deleteAttaDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 绑定数据
        confirmBtn.setTag(atta);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 附件信息
                HashMap<String, Object> atta = (HashMap<String, Object>) v.getTag();
                // 删除附件
                new DeleteAttaTask().execute(atta);
                deleteAttaDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteAttaDlg.cancel();
            }
        });
    }

    /**
     * 显示创建手绘图对话框
     *
     * @param atta {@code HashMap<String, Object>} 绑定的信息
     */
    public void makeCreateDoodleDialog(HashMap<String, Object> atta) {
        // 附件类型
        String type = (String) atta.get("type");

        Builder dlgBuilder = new Builder(this);
        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_atta_create_doodle, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.doodle_create);
        dlgBuilder.setIcon(R.drawable.menu_application_view_gallery);
        dlgBuilder.setCancelable(true);

        // 附件图片
        ImageView imageView = (ImageView) layout.findViewById(R.id.atta_image);
        // 附件新名称
        final TextView attaNameTv = (TextView) layout.findViewById(R.id.attaNameTv);
        // 附件后缀
        TextView attaPostFixTv = (TextView) layout.findViewById(R.id.attaPostFixTv);
        String attaName = CommonUtil.N2B((String) atta.get("alias"));
        String name = attaName.substring(0, attaName.lastIndexOf("."));
        String postFix = attaName.substring(attaName.lastIndexOf("."));

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
            imageView.setImageBitmap(bm);
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = getResources().getInteger(R.integer.gallery_thumbnail_height);
        layoutParams.width = getResources().getInteger(R.integer.gallery_thumbnail_width);
        imageView.setWillNotCacheDrawing(false);

        attaNameTv.setText(name + "_" + CommonUtil.getDT("yyyyMMddHHmmss"));
        attaPostFixTv.setText(postFix);

        dlgBuilder.setPositiveButton(R.string.doodle_start, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        createDoodleDlg = dlgBuilder.create();
        createDoodleDlg.show();

        // 确定按钮
        Button confirmBtn = createDoodleDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = createDoodleDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 绑定数据
        confirmBtn.setTag(atta);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 附件名称
                String attaName = attaNameTv.getText().toString().trim();
                if (!CommonUtil.checkNB(attaName)) {
                    show("请输入手绘图名称！");
                    return;
                }

                // 附件信息
                HashMap<String, Object> atta = (HashMap<String, Object>) v.getTag();
                File imageFileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins");
                if (!imageFileDir.exists()) {
                    imageFileDir.mkdir();
                }
                String srcFileName = (String) atta.get("name");
                String postfix = CommonUtil.getPostfix(srcFileName);
                String dstFileName = srcFileName.substring(0, srcFileName.lastIndexOf(".")) + "_" + CommonUtil.getDT("yyyyMMddHHmmss") + "." + postfix;
                String srcFilePath = imageFileDir.getAbsolutePath() + "/" + srcFileName;
                String dstFilePath = imageFileDir.getAbsolutePath() + "/" + dstFileName;
                // 涂鸦参数
                DoodleParams params = new DoodleParams();
                params.mIsFullScreen = true;
                // 源图片路径
                params.mImagePath = srcFilePath;
                // 图片保存路径
                params.mSavePath = dstFilePath;
                // 初始画笔大小
                params.mPaintUnitSize = DoodleView.DEFAULT_SIZE;
                // 画笔颜色
                params.mPaintColor = Color.RED;
                // 是否支持缩放item
                params.mSupportScaleItem = true;

                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("info", atta);
                data.putString("alias", attaName + "." + postfix);
                // 启动涂鸦页面
                QDoodleActivity.startActivityForResult(classThis, params, data, CommonParam.REQUESTCODE_DOODLE);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);

                createDoodleDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createDoodleDlg.cancel();
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
     * 删除附件的 AsyncTask 类
     */
    public class DeleteAttaTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：删除成功
         */
        private static final int PROGRESS_DELETE_SUCCESS = 1001;
        /**
         * 进度常量：删除失败
         */
        private static final int PROGRESS_DELETE_FAIL = 1002;
        /**
         * 进度常量：显示图片
         */
        private static final int PROGRESS_SHOW_PHOTO = 1004;
        /**
         * 进度常量：显示视频
         */
        private static final int PROGRESS_SHOW_VIDEO = 1005;
        /**
         * 进度常量：显示音频
         */
        private static final int PROGRESS_SHOW_AUDIO = 1003;

        /**
         * 附件信息
         */
        private HashMap<String, Object> atta;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 附件信息
            atta = (HashMap<String, Object>) params[0];

            // 删除文件
            new FileUtil().deleteFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/ins", (String) atta.get("name"));

            // 附件类型
            String type = (String) atta.get("type");
            if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
                // 显示图片
                publishProgress(PROGRESS_SHOW_PHOTO);
            } else if (CommonParam.ATTA_TYPE_VIDEO.equals(type)) {
                // 显示视频
                publishProgress(PROGRESS_SHOW_VIDEO);
            } else {
                // 显示音频
                publishProgress(PROGRESS_SHOW_AUDIO);
            }
            // 删除成功
            publishProgress(PROGRESS_DELETE_SUCCESS);

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
            if (progress[0] == PROGRESS_DELETE_SUCCESS) {
                // 删除成功
                show(R.string.alert_delete_success);
            } else if (progress[0] == PROGRESS_DELETE_FAIL) {
                // 删除失败
                show(R.string.alert_delete_fail);
            } else if (progress[0] == PROGRESS_SHOW_PHOTO) {
                // 生成图片 Gallery 列表
                // 要删除的图片索引
                int index = photoList.indexOf(atta);
                if (index < (photoList.size() - 1)) {
                    // 下一个图片
                    HashMap<String, Object> atta_next = photoList.get(index + 1);
                    photoDescTv.setText((String) atta_next.get("alias"));
                }
                photoList.remove(atta);
                BaseAdapter adapter = (BaseAdapter) photoGallery.getAdapter();
                adapter.notifyDataSetChanged();
                // 设置图片数量
                photoNumTv.setText("(" + photoList.size() + ")");
            } else if (progress[0] == PROGRESS_SHOW_VIDEO) {
                // 生成视频 Gallery 列表
                // 要删除的视频索引
                int index = videoList.indexOf(atta);
                if (index < (videoList.size() - 1)) {
                    // 下一个视频
                    HashMap<String, Object> atta_next = videoList.get(index + 1);
                    videoDescTv.setText((String) atta_next.get("alias"));
                }
                videoList.remove(atta);
                BaseAdapter adapter = (BaseAdapter) videoGallery.getAdapter();
                adapter.notifyDataSetChanged();
                // 设置视频数量
                videoNumTv.setText("(" + videoList.size() + ")");
            } else if (progress[0] == PROGRESS_SHOW_AUDIO) {
                // 生成音频 Gallery 列表
                // 要删除的音频索引
                int index = audioList.indexOf(atta);
                if (index < (audioList.size() - 1)) {
                    // 下一个视频
                    HashMap<String, Object> atta_next = audioList.get(index + 1);
                    audioDescTv.setText((String) atta_next.get("alias"));
                }
                audioList.remove(atta);
                BaseAdapter adapter = (BaseAdapter) audioGallery.getAdapter();
                adapter.notifyDataSetChanged();
                // 设置音频数量
                audioNumTv.setText("(" + audioList.size() + ")");
            }
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
        }
    }

    /**
     * 显示音频录制对话框
     */
    public void makeRecVoiceDialog() {
        Builder dlgBuilder = new Builder(this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_record_voice, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.voice_record);
        dlgBuilder.setIcon(R.drawable.menu_microphone);
        dlgBuilder.setCancelable(false);

        dlgBuilder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // 开始录制按钮
        ImageButton recordVoiceStartBtn = (ImageButton) layout.findViewById(R.id.record_voice_start);
        // 停止录制按钮
        ImageButton recordVoiceStopBtn = (ImageButton) layout.findViewById(R.id.record_voice_stop);

        recordVoiceStartBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
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
                    }
                });
                playVoice("sfx_click.wav");

                recordVoiceStart();
            }
        });

        recordVoiceStopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 停止录音
                recordVoiceStop();

                File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins");
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                String attaName = CommonUtil.GetNextID() + ".m4a";
                File attaFile = new File(fileDir.getAbsolutePath() + "/" + attaName);
                // 正常保存标志
                boolean bFlag = false;
                try {
                    new FileUtil().copyFile(capAudioFile.getAbsolutePath(), attaFile.getAbsolutePath());
                    bFlag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    capAudioFile.delete();
                }
                if (bFlag) {
                    HashMap<String, Object> atta = new HashMap<String, Object>();
                    atta.put("type", CommonParam.ATTA_TYPE_AUDIO);
                    atta.put("name", attaName);
                    atta.put("alias", attaName);
                    atta.put("size", new FileUtil().getFileSize(attaFile));

                    audioList.add(atta);
                    BaseAdapter adapter = (BaseAdapter) audioGallery.getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    // 设置音频数量
                    audioNumTv.setText("(" + audioList.size() + ")");
                }

                recordVoiceDlg.cancel();
            }
        });

        recordVoiceDlg = dlgBuilder.create();
        recordVoiceDlg.show();

        // 取消按钮
        Button cancelBtn = recordVoiceDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 停止录音
                recordVoiceStop();
                recordVoiceDlg.cancel();
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
     * 开始录音
     */
    public void recordVoiceStart() {
        try {
            recordVoiceDlg.setTitle(R.string.voice_recording);

            LinearLayout record_voice_start_layout = (LinearLayout) recordVoiceDlg
                    .findViewById(R.id.record_voice_start_layout);
            LinearLayout record_voice_stop_layout = (LinearLayout) recordVoiceDlg
                    .findViewById(R.id.record_voice_stop_layout);
            ImageView record_voice_iv = (ImageView) recordVoiceDlg.findViewById(R.id.record_voice_iv);
            record_voice_start_layout.setVisibility(View.GONE);
            record_voice_stop_layout.setVisibility(View.VISIBLE);
            record_voice_iv.setBackgroundResource(R.drawable.voice_recording);
            AnimationDrawable rv_ad = (AnimationDrawable) record_voice_iv.getBackground();
            rv_ad.start();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(capAudioFile.getAbsolutePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();

            mediaRecorder.start();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void recordVoiceStop() {
        recordVoiceDlg.setTitle(R.string.voice_record);
        LinearLayout record_voice_start_layout = (LinearLayout) recordVoiceDlg
                .findViewById(R.id.record_voice_start_layout);
        LinearLayout record_voice_stop_layout = (LinearLayout) recordVoiceDlg
                .findViewById(R.id.record_voice_stop_layout);
        ImageView record_voice_iv = (ImageView) recordVoiceDlg.findViewById(R.id.record_voice_iv);
        record_voice_start_layout.setVisibility(View.VISIBLE);
        record_voice_stop_layout.setVisibility(View.GONE);
        record_voice_iv.setBackgroundResource(R.drawable.voice_normal);

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
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
     * 提交信息
     */
    public void submit() {
        boolean submitFlag = false;
        String errorMsg = "";

        String title = titleTv.getText().toString();

        if (!CommonUtil.checkNB(title)) {
            errorMsg = "请输入标题！";
        } else if (!CommonUtil.checkNB(mStage1)) {
            errorMsg = "请选择项目进度！";
        } else if (!CommonUtil.checkNB(mStage2)) {
            errorMsg = "请选择项目状态！";
        } else if (!CommonUtil.checkNB(mCat1)) {
            errorMsg = "请选择记录主类！";
        } else if (!CommonUtil.checkNB(mCat2)) {
            errorMsg = "请选择记录子类！";
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
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {
        String title;
        String ly;
        String sk;
        String info;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在保存，请稍候…");
            submitBtn.setClickable(false);
            submitBtn.setEnabled(false);

            title = titleTv.getText().toString();
            ly = lyTv.getText().toString();
            sk = ((HashMap<String, String>) skSpinner
                    .getItemAtPosition(skSpinner.getFirstVisiblePosition())).get("code");
            info = infoTv.getText().toString();
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
            List<JSONObject> attaList = new ArrayList<JSONObject>();
            for (int i = 0, len = photoList.size(); i < len; i++) {
                HashMap<String, Object> m = photoList.get(i);
                JSONObject o = new JSONObject();
                o.put("name", (String) m.get("alias"));
                o.put("file", (String) m.get("name"));
                o.put("size", (String) m.get("size"));

                attaList.add(o);
            }
            for (int i = 0, len = videoList.size(); i < len; i++) {
                HashMap<String, Object> m = videoList.get(i);
                JSONObject o = new JSONObject();
                o.put("name", (String) m.get("alias"));
                o.put("file", (String) m.get("name"));
                o.put("size", (String) m.get("size"));

                attaList.add(o);
            }
            for (int i = 0, len = audioList.size(); i < len; i++) {
                HashMap<String, Object> m = audioList.get(i);
                JSONObject o = new JSONObject();
                o.put("name", (String) m.get("alias"));
                o.put("file", (String) m.get("name"));
                o.put("size", (String) m.get("size"));

                attaList.add(o);
            }

            // 键值对
            ContentValues cv = new ContentValues();
            cv.put("ids", CommonUtil.getUUID());
            cv.put("title", title);
            cv.put("pid", mInfoId);
            cv.put("stage1", mStage1);
            cv.put("stage2", mStage2);
            cv.put("cat1", mCat1);
            cv.put("cat2", mCat2);
            cv.put("ly", ly);
            cv.put("sk", sk);
            cv.put("info", info);
            cv.put("ctime", CommonUtil.getDT());
            cv.put("uid", CommonUtil.N2B((String) baseApp.loginUser.get("ids")));
            cv.put("uname", CommonUtil.N2B((String) baseApp.loginUser.get("realname")));
            cv.put("valid", CommonParam.YES);
            cv.put("attachment", JSONObject.toJSONString(attaList));
            cv.put("photo", JSONObject.toJSONString(photoList));
            cv.put("video", JSONObject.toJSONString(videoList));
            cv.put("audio", JSONObject.toJSONString(audioList));
            cv.put("quid", (String) baseApp.getLoginUser().get("ids"));

            // ★☆
            long insResult = infoTool.insert("t_szfgs_sgwxrec", cv);
            if (insResult > -1L) {
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
                show("信息保存成功！");
                setResult(CommonParam.RESULTCODE_NEW_REC);
                goBack();
            } else {
                show("保存失败。请检查表单内容，然后重新保存！");
                submitBtn.setClickable(true);
                submitBtn.setEnabled(true);
            }
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
        if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
            inflater.inflate(R.menu.rec_edit_attach_photo_menu, popupMenu.getMenu());
        } else {
            inflater.inflate(R.menu.rec_edit_attach_menu, popupMenu.getMenu());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_atta_detail:
                        // 显示附件信息
                        showAttaDetail(currentAttaType, currentAttaIndex);
                        break;
                    case R.id.menu_edit_atta_name:
                        // 编辑附件别名
                        editAttaName(currentAttaType, currentAttaIndex);
                        break;
                    case R.id.menu_delete:
                        // 删除附件
                        deleteAtta(currentAttaType, currentAttaIndex);
                    case R.id.menu_doodle:
                        // 手绘
                        createDoodle(currentAttaType, currentAttaIndex);
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
        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        // 界面相关参数。开始===============================
        titleTv = (EditText) findViewById(R.id.titleTv);
        stage1Spinner = (Spinner) findViewById(R.id.stage1Spinner);
        stage2Spinner = (Spinner) findViewById(R.id.stage2Spinner);
        cat1Spinner = (Spinner) findViewById(R.id.cat1Spinner);
        cat2Spinner = (Spinner) findViewById(R.id.cat2Spinner);
        lyTv = (EditText) findViewById(R.id.lyTv);
        skSpinner = (Spinner) findViewById(R.id.skSpinner);
        infoTv = (EditText) findViewById(R.id.infoTv);

        photoNumTv = (TextView) findViewById(R.id.photoNumTv);
        photoGallery = (Gallery) findViewById(R.id.photoGallery);
        photoDescTv = (TextView) findViewById(R.id.photoDescTv);
        videoNumTv = (TextView) findViewById(R.id.videoNumTv);
        videoGallery = (Gallery) findViewById(R.id.videoGallery);
        videoDescTv = (TextView) findViewById(R.id.videoDescTv);
        audioNumTv = (TextView) findViewById(R.id.audioNumTv);
        audioGallery = (Gallery) findViewById(R.id.audioGallery);
        audioDescTv = (TextView) findViewById(R.id.audioDescTv);

        photoBtn = (Button) findViewById(R.id.photoBtn);
        videoBtn = (Button) findViewById(R.id.videoBtn);
        audioBtn = (Button) findViewById(R.id.audioBtn);
        // 界面相关参数。结束===============================
    }
}
