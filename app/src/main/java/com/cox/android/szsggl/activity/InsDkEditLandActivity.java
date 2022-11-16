/*
 * Copyright (c) 2020 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.handler.HtmlTagHandler;
import com.cox.android.szsggl.R;
import com.cox.android.uhf.Reader;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;
import com.rfid.InventoryTagMap;
import com.rfid.PowerUtil;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.hzw.doodle.DoodleParams;
import cn.hzw.doodle.DoodleView;

/**
 * 巡视_签到打卡记录_编辑页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsDkEditLandActivity extends DbActivity {
    // 常量。开始===============================
    /**
     * 读UHF卡类型：定位
     */
    private static final String SCAN_UHF_CARD_TYPE_LOC = "loc";
    /**
     * 读UHF卡类型：采集
     */
    private static final String SCAN_UHF_CARD_TYPE_ADD = "add";
    // 常量。结束===============================
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
    private TextView bizTitleTv;
    private TextView ctimeTv;
    private TextView resTitleTv;
    private LinearLayout markContentLayout;
    private TextView markTv;
    private TextView cardMacTv;
    private ImageButton markChooseBtn;
    private ImageButton markDeleteBtn;
    private ImageButton addUhfBtn;
    private ImageButton editUhfBtn;
    private Button scanUhfBtn;
    private TextView lngLatTv;
    private Button getLnglatBtn;
    private TextView jlrNameTv;
    private TextView fzrNameTv;

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
     * 任务信息
     */
    private HashMap<String, Object> bizInfo;
    /**
     * 资源信息
     */
    private HashMap<String, Object> resInfo;
    /**
     * 当前的区域标识信息
     */
    private HashMap<String, Object> areaSignInfo;
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 选择区域标识Dialog
     */
    private AlertDialog chooseAreaSignDlg;
    /**
     * 新建区域标识Dialog
     */
    private AlertDialog createAreaSignInfoDlg;
    /**
     * 新建区域标识Dialog
     */
    private AlertDialog createAreaSignTitleDlg;
    /**
     * 区域标识List
     */
    private List<HashMap<String, Object>> areaSignList;
    /**
     * 读UHF卡Dialog
     */
    private AlertDialog readUhfCardDlg;
    /**
     * 扫描UHF卡片类型
     */
    private String scanUhfCardType;
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
     * 新区域名称标志
     */
    private boolean newMarkFlag = false;
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

    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 0:
                    // 更新经纬度
                    lngLatTv.setText(longitude_baidu + "," + latitude_baidu);
                    break;
                default:
                    break;
            }
        }
    };

    // UHF参数。开始============================
    /**
     * 是否正在扫描
     */
    private boolean isUhfScaning = false;
    /**
     * UHF扫描结果是否有效
     */
    private boolean isUhfActive = false;
    /**
     * UHF扫描是否结束
     */
    private boolean isUhfScanStop = true;
    long uhfBeginTime = 0;
    private Timer uhfScanTimer;
    public Map<String, Integer> dtIndexMap = new LinkedHashMap<String, Integer>();
    private List<InventoryTagMap> uhfData = new ArrayList<InventoryTagMap>();
    private Handler uhfHandler;
    public boolean isUhfStopScanThread = false;
    public static int UhfErrorCount;
    public static int UhfErrorCRC;
    /**
     * UHF功率Dialog
     */
    private AlertDialog uhfPowerDlg;
    // UHF参数。结束============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = InsDkEditLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");
        resInfo = (HashMap<String, Object>) data.getSerializable("resInfo");

        setContentView(R.layout.ins_dk_edit);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("签到打卡记录");

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
        // 获取经纬度
        getLnglatBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pageHandler.sendEmptyMessage(0);
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
        addUhfBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editUhfBtn.performClick();
            }
        });
        editUhfBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String mark = markTv.getText().toString();
                if (checkMarkDuplicate(mark)) {
                    // 如果是旧区域
                    makeCreateAreaSignTitleDialog();
                } else {
                    if (CommonUtil.checkNB(cardMacTv.getText().toString())) {
                        // 新扫卡
                        makeCreateAreaSignInfoDialog();
                    } else {
                        // 新建区域（未扫卡）
                        makeCreateAreaSignTitleDialog();
                    }
                }
            }
        });
        markTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String mark = markTv.getText().toString();
                if (checkMarkDuplicate(mark)) {
                    // 如果是旧区域
                    markChooseBtn.performClick();
                } else {
                    if (!CommonUtil.checkNB(mark) && !CommonUtil.checkNB(cardMacTv.getText().toString())) {
                        // 手动选择的区域
                        markChooseBtn.performClick();
                    } else {
                        editUhfBtn.performClick();
                    }
                }
            }
        });
        markChooseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                markChooseBtn.setClickable(false);
                markChooseBtn.setEnabled(false);
                makeChooseAreaSignDialog();
            }
        });
        markDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                markTv.setText("");
                cardMacTv.setText("");
                markTv.setHint(R.string.hint_jc_mark_select);
                areaSignInfo = null;
                newMarkFlag = false;
                markContentLayout.setBackgroundResource(R.drawable.border_grey_readonly);
                addUhfBtn.setVisibility(View.VISIBLE);
                editUhfBtn.setVisibility(View.GONE);
                markChooseBtn.setVisibility(View.VISIBLE);
                markDeleteBtn.setVisibility(View.GONE);
            }
        });
        if (baseApp.isUhfPda) {
            // 扫UHF卡定位
            scanUhfBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanUhfCardType = SCAN_UHF_CARD_TYPE_LOC;
                    makeReadUhfCardDialog();
                }
            });
        }

        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            uhfHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {

                    try {
                        switch (msg.what) {
                            case CommonParam.UHF_MSG_UPDATE_LISTVIEW:
                                uhfData = Reader.rrlib.getInventoryTagMapList();
                                if (uhfData.size() > 0) {
                                    StringBuffer sb = new StringBuffer();
                                    for (InventoryTagMap m : uhfData) {
                                        sb.append(m.strEPC).append("\n");
                                    }
                                    Log.d("###sb", "#" + sb.toString());
                                }
                                break;
                            case CommonParam.UHF_MSG_UPDATE_TIME:
                                //long endTime = System.currentTimeMillis();
                                break;
                            case CommonParam.UHF_MSG_UPDATE_ERROR:
                                break;
                            case CommonParam.UHF_MSG_UPDATE_STOP:
                                // 当前状态：已停止；下一状态：启动
                                isUhfScanStop = true;
                                setUhfScanFieldStart();
                                break;
                            case CommonParam.UHF_MSG_UPDATE_EPC:
                                // show((String) msg.obj);
                                Log.d("###epc", "#" + (String) msg.obj);
                                if (isUhfActive) {
                                    Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
                                    confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                                    confirmBtn.setText("开始扫描");
                                    stopScanUhf(false);
                                    new CheckUhfCardTask().execute((String) msg.obj);
                                }
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        ex.toString();
                    }

                    super.handleMessage(msg);
                }

            };
        }
        // UHF相关事件与方法。结束============================
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
            intent.putExtra("outputX", CommonParam.SHOW_IMAGE_HEIGHT);
            intent.putExtra("outputY", CommonParam.SHOW_IMAGE_WIDTH);
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
                        CommonParam.SHOW_IMAGE_HEIGHT, CommonParam.SHOW_IMAGE_WIDTH);
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
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            isUhfStopScanThread = false;
        }
        // UHF相关事件与方法。结束============================
        super.onResume();
    }

    @Override
    public void onPause() {
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            if (isUhfScaning) {
                Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
                confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                confirmBtn.setText("开始扫描");
                stopScanUhf();
            }
        }
        // UHF相关事件与方法。结束============================
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
//            disconnectUhf(false);
//            PowerUtil.power("0");
        }
        // UHF相关事件与方法。结束============================
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
            // 区域标识信息
            areaSignList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                    "select * from t_szfgs_sgresareasign model where model.valid='1' and model.rid=? order by CAST(model.n as int) asc", new String[]{(String) resInfo.get("ids")});

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
                // 用户名字
                String user_name = CommonUtil.N2B((String) bizInfo.get("user_name"));
                // 负责人编号
                String fzr_id = CommonUtil.N2B((String) bizInfo.get("fzr"));
                // 记录人编号
                String jlr_id = CommonUtil.N2B((String) baseApp.loginUser.get("ids"));
                String fzr_name = null, jlr_name = null;
                if (user_name.contains("#")) {
                    String[] user_name_array = user_name.split("#");

                    fzr_name = user_name_array[0];

                    if (fzr_id.equals(jlr_id)) {
                        // 负责人与记录人相同
                        jlr_name = fzr_name;
                    } else {
                        // 负责人与记录人不相同
                        String ryap = (CommonUtil.N2B((String) bizInfo.get("ryap")));
                        try {
                            String[] ryap_array = ryap.split(",");
                            String[] ryap_name_array = user_name_array[1].split(",");
                            int ryap_index = ArrayUtils.indexOf(ryap_array, jlr_id);
                            jlr_name = ryap_name_array[ryap_index];
                        } catch (Exception e) {
                        }
                    }
                }
                if (!CommonUtil.checkNB(fzr_name)) {
                    fzr_name = "未知人员";
                }
                if (!CommonUtil.checkNB(jlr_name)) {
                    jlr_name = "未知人员";
                }

                bizTitleTv.setText(CommonUtil.N2B((String) bizInfo.get("title")));
                ctimeTv.setText(CommonUtil.getDT());
                resTitleTv.setText(CommonUtil.N2B((String) resInfo.get("title")));
                jlrNameTv.setText(jlr_name);
                fzrNameTv.setText(fzr_name);

                addUhfBtn.setVisibility(View.VISIBLE);
                editUhfBtn.setVisibility(View.GONE);
                markChooseBtn.setVisibility(View.VISIBLE);
                if (baseApp.isUhfPda) {
                    markTv.setHint(R.string.hint_jc_mark_select);
                    scanUhfBtn.setVisibility(View.VISIBLE);
                } else {
                    markTv.setHint(R.string.hint_jc_mark_select_areasign);
                    scanUhfBtn.setVisibility(View.GONE);
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
                pageHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
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
                AlertDialog dlg = (AlertDialog) v.getTag();
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
                if (!baseApp.isReverseRotate) {
                    QDoodleLandActivity.startActivityForResult(classThis, params, data, CommonParam.REQUESTCODE_DOODLE);
                } else {
                    QDoodleReverseLandActivity.startActivityForResult(classThis, params, data, CommonParam.REQUESTCODE_DOODLE);
                }
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

        String mark = markTv.getText().toString();

        if (baseApp.isUhfPda) {
            if (!CommonUtil.checkNB(mark)) {
                errorMsg = "请输入区域名称！";
            } else if (newMarkFlag && checkMarkDuplicate(mark)) {
                errorMsg = "已有该区域名称！";
            } else {
                submitFlag = true;
            }
        } else {
            if (!CommonUtil.checkNB(mark)) {
                errorMsg = "请选择区域标识！";
            } else {
                submitFlag = true;
            }
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
     * 检查区域名称是否重复
     *
     * @param mark {@code String} mark值
     * @return {@code boolean} 是否重复
     */
    public boolean checkMarkDuplicate(String mark) {
        boolean flag = false;

        for (HashMap<String, Object> o : areaSignList) {
            if (mark.equals(o.get("title"))) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {
        String ctime;
        String mark;
        String cardMac;
        String lngLat;
        String user_name;

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

            ctime = ctimeTv.getText().toString();
            mark = markTv.getText().toString();
            cardMac = cardMacTv.getText().toString();
            lngLat = lngLatTv.getText().toString();
            user_name = fzrNameTv.getText().toString() + "#" + jlrNameTv.getText().toString();
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

            // 打卡信息编号
            String dkInfoId = CommonUtil.getUUID();

            // 键值对
            ContentValues cv = new ContentValues();
            cv.put("ids", dkInfoId);
            cv.put("ctime", ctime);
            cv.put("biz_id", (String) bizInfo.get("ids"));
            cv.put("biz_title", (String) bizInfo.get("title"));
            cv.put("res_id", (String) resInfo.get("ids"));
            cv.put("res_title", (String) resInfo.get("title"));
            cv.put("areasign", mark);
            cv.put("jingwd", lngLat);
            cv.put("fzr", CommonUtil.N2B((String) bizInfo.get("fzr")));
            cv.put("jlr", CommonUtil.N2B((String) baseApp.loginUser.get("ids")));
            cv.put("user_name", user_name);
            cv.put("valid", CommonParam.YES);
            cv.put("attachment", JSONObject.toJSONString(attaList));
            cv.put("photo", JSONObject.toJSONString(photoList));
            cv.put("video", JSONObject.toJSONString(videoList));
            cv.put("audio", JSONObject.toJSONString(audioList));
            cv.put("quid", (String) baseApp.getLoginUser().get("ids"));

            // ★☆
            long insResult = infoTool.insert("t_szfgs_sgxunsqdjl", cv);
            if (insResult > -1L) {
                result = CommonParam.RESULT_SUCCESS;
            }

            if (baseApp.isUhfPda) {
                if (newMarkFlag) {
                    // 区域标识编号
                    String areaSignId = CommonUtil.getUUID();
                    String _n = null;
                    if (CommonParam.RESULT_SUCCESS.equals(result)) {
                        // 如果有新区域，要保存
                        result = CommonParam.RESULT_ERROR;

                        // 键值对
                        cv.clear();
                        cv.put("ids", areaSignId);
                        cv.put("title", mark);
                        cv.put("loctype", "3");
                        cv.put("loc", cardMac);
                        cv.put("rid", (String) resInfo.get("ids"));
                        cv.put("n", "" + (areaSignList.size() + 1));
                        cv.put("valid", CommonParam.YES);

                        // ★☆
                        insResult = infoTool.insert("t_szfgs_sgresareasign", cv);
                        if (insResult > -1L) {
                            result = CommonParam.RESULT_SUCCESS;
                            _n = cv.getAsString("n");
                        }
                    }

                    // 区域标识定位编号
                    String areaSignLocId = CommonUtil.getUUID();
                    if (CommonParam.RESULT_SUCCESS.equals(result)) {
                        // 如果有新区域，要保存
                        result = CommonParam.RESULT_ERROR;

                        // 键值对
                        cv.clear();
                        cv.put("ids", areaSignLocId);
                        cv.put("ctime", CommonUtil.getDT());
                        cv.put("uid", (String) baseApp.loginUser.get("ids"));
                        cv.put("title", mark);
                        cv.put("loctype", "3");
                        cv.put("loc", cardMac);
                        cv.put("rid", (String) resInfo.get("ids"));
                        cv.put("n", _n);
                        cv.put("valid", CommonParam.YES);

                        // ★☆
                        insResult = infoTool.insert("t_szfgs_sgresareasignloc", cv);
                        if (insResult > -1L) {
                            result = CommonParam.RESULT_SUCCESS;
                        }
                    }
                }
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
     * 选择区域标识
     */
    public void makeChooseAreaSignDialog() {
        if (areaSignList.size() == 0) {
            makeAlertDialog("您当前没有区域标识可以选择！");
            markChooseBtn.setClickable(true);
            markChooseBtn.setEnabled(true);
            return;
        }
        // 名称数组
        String[] nameArray = new String[areaSignList.size()];
        int checkedIndex = -1;
        for (int i = 0, len = areaSignList.size(); i < len; i++) {
            HashMap<String, Object> info = areaSignList.get(i);
            String name = (String) info.get("title");
            nameArray[i] = name;
            if (markTv.getText().toString().equals(name)) {
                checkedIndex = i;
            }
        }

        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle("选择区域标识");
        dlgBuilder.setIcon(R.drawable.cat_type_normal);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setSingleChoiceItems(nameArray, checkedIndex, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) ((AlertDialog) dialog).getButton(
                        DialogInterface.BUTTON_POSITIVE).getTag();
                // 选择的索引
                dlgTag.put("which", which);
            }
        });
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

        chooseAreaSignDlg = dlgBuilder.create();
        chooseAreaSignDlg.show();

        // 确定按钮
        Button confirmBtn = chooseAreaSignDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = chooseAreaSignDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        // 存放Dialog所需信息的Map
        Map<String, Object> dlgTag = new HashMap<String, Object>();
        dlgTag.put("which", checkedIndex);
        // 绑定数据
        confirmBtn.setTag(dlgTag);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) v.getTag();
                // 选择的索引
                Integer which = (Integer) dlgTag.get("which");
                if (which != null && which > -1) {
                    HashMap<String, Object> info = areaSignList.get(which);
                    markTv.setText((String) info.get("title"));
                    cardMacTv.setText((String) info.get("loc"));
                    markTv.setHint(R.string.hint_jc_mark_select);
                    areaSignInfo = info;
                    newMarkFlag = false;
                    markContentLayout.setBackgroundResource(R.drawable.border_grey_readonly);
                    addUhfBtn.setVisibility(View.VISIBLE);
                    editUhfBtn.setVisibility(View.GONE);
                    markChooseBtn.setVisibility(View.VISIBLE);
                    markDeleteBtn.setVisibility(View.GONE);
                }
                chooseAreaSignDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseAreaSignDlg.cancel();
            }
        });
        chooseAreaSignDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                markChooseBtn.setClickable(true);
                markChooseBtn.setEnabled(true);
            }
        });
    }

    /**
     * 显示读UHF卡对话框
     */
    public void makeReadUhfCardDialog() {
        // 如果没有连接UHF模块，需要先连接
        if (!Reader.rrlib.IsConnected()) {
            connectUhf(false);
            getUhfInfo();
        }

        Builder dlgBuilder = new Builder(this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_read_ins_uhf_card, null);
        dlgBuilder.setView(layout);
        if (scanUhfCardType.equals(SCAN_UHF_CARD_TYPE_ADD)) {
            // 采集
            dlgBuilder.setTitle(getString(R.string.text_scan_uhf_card_data));
        } else {
            dlgBuilder.setTitle(getString(R.string.text_scan_uhf_card));
        }
        dlgBuilder.setIcon(R.drawable.ic_dialog_uhf_v);
        dlgBuilder.setCancelable(true);

        readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
        readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_INSPECT;

        dlgBuilder.setPositiveButton("开始扫描", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setNeutralButton("功率：" + (int) baseApp.uhfPdaInfo.get("power"), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        readUhfCardDlg = dlgBuilder.create();
        readUhfCardDlg.show();

        setUhfScanFieldStart();

        // 确定按钮
        Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 功率按钮
        Button powerBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEUTRAL);

        confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                if (readUhfCardType == CommonParam.READ_UHF_CARD_TYPE_INSPECT) {
                    // 读巡视卡
                    if (isUhfScaning) {
                        confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                        btn.setText("开始扫描");
                        stopScanUhf();
                    } else {
                        confirmBtn.setTextColor(getResources().getColor(R.color.text_orange_dark));
                        btn.setText("停止扫描");
                        startScanUhf();
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                readUhfCardDlg.cancel();
            }
        });
        powerBtn.setTextColor(getResources().getColor(R.color.text_blue));
        powerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
                confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                confirmBtn.setText("开始扫描");
                stopScanUhf();
                makeSetUhfPowerDialog();
            }
        });

        readUhfCardDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
                confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                confirmBtn.setText("开始扫描");
                stopScanUhf();
                if (Reader.rrlib.IsConnected()) {
                    disconnectUhf(false);
                }
                readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
                readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_NO_ACTION;
                scanUhfCardType = SCAN_UHF_CARD_TYPE_LOC;
            }
        });
        readUhfCardDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                boolean flag = false;
                // 点击PDA上的红色按钮会开始扫描/停止扫描
                // 这里加上了event.getAction() == KeyEvent.ACTION_UP。如果不加，会响应两次
                // 开关的监听设置在这里，是因为弹出AlertDialog后，由AlertDialog来监听按键，Activity是监听不到的。
                if (keyCode == baseApp.uhfKeyCode && event.getAction() == KeyEvent.ACTION_UP) {
                    if (readUhfCardType == CommonParam.READ_UHF_CARD_TYPE_INSPECT) {
                        // 读巡视卡
                        Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
                        if (isUhfScaning) {
                            confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                            confirmBtn.setText("开始扫描");
                            stopScanUhf();
                        } else {
                            confirmBtn.setTextColor(getResources().getColor(R.color.text_orange_dark));
                            confirmBtn.setText("停止扫描");
                            startScanUhf();
                        }
                        flag = true;
                    }
                }
                return flag;
            }
        });
    }

    /**
     * 检查UHF卡片编号 AsyncTask 类
     */
    private class CheckUhfCardTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：水工资源表中已绑定该卡
         */
        private static final int PROGRESS_RES_BIND = 1001;
        /**
         * 进度常量：水工资源区域标识表其他巡视点的区域已绑定该卡
         */
        private static final int PROGRESS_AREASIGN_OTHER_BIND = 1002;
        /**
         * 进度常量：所有其他表都绑定该卡
         */
        private static final int PROGRESS_ALL_OTHER_BIND = 1003;

        /**
         * 卡编号
         */
        private String cardMac;
        /**
         * 水工资源表中该卡号的数量
         */
        private int resTotal = 0;
        /**
         * 水工资源区域标识表中其他巡视点该卡号的数量
         */
        private int areaSignOtherTotal = 0;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog();

            areaSignInfo = null;
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 卡编号
            cardMac = (String) params[0];

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            // 检查该卡片是否绑定到了其他资源
            resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1' and model.loctype='3' and model.loc=? and model.ids<>?", new String[]{cardMac, (String) resInfo.get("ids")});
            if (resTotal == 0) {
                // 如果在水工资源表中找不到该卡，就到水工资源采集定位记录表中找
                resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresloc model where model.valid='1' and model.loctype='3' and model.loc=? and model.res_id<>?", new String[]{cardMac, (String) resInfo.get("ids")});
            }
            if (resTotal == 0) {
                // 检查该卡片是否绑定到了其他资源的区域中
                areaSignOtherTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresareasign model where model.valid='1' and model.loctype='3' and model.loc=? and model.rid<>?", new String[]{cardMac, (String) resInfo.get("ids")});
            }
            for (int i = 0; i < CommonParam.UHF_CHECK_STOP_TOTAL; i++) {
                if (!isUhfScanStop) {
                    doWait(CommonParam.UHF_CHECK_STOP_INTERVAL);
                } else {
                    break;
                }
            }
            if (resTotal > 0 && areaSignOtherTotal == 0) {
                publishProgress(PROGRESS_RES_BIND);
            } else if (resTotal == 0 && areaSignOtherTotal > 0) {
                publishProgress(PROGRESS_AREASIGN_OTHER_BIND);
            } else if (resTotal > 0 && areaSignOtherTotal > 0) {
                publishProgress(PROGRESS_ALL_OTHER_BIND);
            } else {
                List<HashMap<String, Object>> _areaSignList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                        "select * from t_szfgs_sgresareasign model where model.valid='1' and model.loc=? and model.rid=? order by CAST(model.n as int) desc", new String[]{cardMac, (String) resInfo.get("ids")});
                if (_areaSignList.size() > 0) {
                    areaSignInfo = _areaSignList.get(0);
                }

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
            if (progress[0] == PROGRESS_RES_BIND) {
                // 水工资源表中已绑定该卡
                setUhfScanFieldStart(String.format("%s%s", "<span style=\"color:#ed6c26\">该卡已绑定到其他巡视点！</span><br/><br/>", getString(R.string.text_scan_uhf_card_info_not_start)));
            } else if (progress[0] == PROGRESS_AREASIGN_OTHER_BIND) {
                // 水工资源区域标识表其他巡视点的区域已绑定该卡
                setUhfScanFieldStart(String.format("%s%s", "<span style=\"color:#ed6c26\">该卡已绑定到其他巡视点的区域！</span><br/><br/>", getString(R.string.text_scan_uhf_card_info_not_start)));
            } else if (progress[0] == PROGRESS_ALL_OTHER_BIND) {
                // 所有其他表都绑定该卡
                setUhfScanFieldStart(String.format("%s%s", "<span style=\"color:#ed6c26\">该卡已同时绑定到其他巡视点和区域！</span><br/><br/>", getString(R.string.text_scan_uhf_card_info_not_start)));
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
                cardMacTv.setText(cardMac);
                if (areaSignInfo != null) {
                    markTv.setText((String) areaSignInfo.get("title"));
                    markTv.setHint(R.string.hint_jc_mark_select);
                    newMarkFlag = false;
                    markContentLayout.setBackgroundResource(R.drawable.border_grey_readonly);
                    addUhfBtn.setVisibility(View.VISIBLE);
                    editUhfBtn.setVisibility(View.GONE);
                    markChooseBtn.setVisibility(View.VISIBLE);
                    markDeleteBtn.setVisibility(View.GONE);
                } else {
                    markTv.setText("");
                    markTv.setHint("请输入区域名称");
                    newMarkFlag = true;
                    markContentLayout.setBackgroundResource(R.drawable.border_green);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.VISIBLE);
                    markChooseBtn.setVisibility(View.GONE);
                    markDeleteBtn.setVisibility(View.VISIBLE);
                }
                if (readUhfCardDlg != null && readUhfCardDlg.isShowing()) {
                    readUhfCardDlg.cancel();
                }
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("di.wav");
                }
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("du.wav");
                }
            }
        }
    }

    /**
     * 显示新建区域标识对话框
     */
    public void makeCreateAreaSignTitleDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_areasign_title_create, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("新建区域标识");
        dlgBuilder.setIcon(R.drawable.comment_edit);
        dlgBuilder.setCancelable(true);

        // 区域名称
        TextView info_tv = (TextView) layout.findViewById(R.id.info_tv);
        if (checkMarkDuplicate(markTv.getText().toString())) {
            // 新建
            info_tv.setText("");
        } else {
            // 编辑
            info_tv.setText(markTv.getText().toString());
        }

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

        createAreaSignTitleDlg = dlgBuilder.create();
        createAreaSignTitleDlg.show();

        // 确定按钮
        Button confirmBtn = createAreaSignTitleDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = createAreaSignTitleDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean submitFlag = false;
                String errorMsg = "";

                TextView info_tv = (TextView) createAreaSignTitleDlg.findViewById(R.id.info_tv);
                String mark = info_tv.getText().toString();
                boolean _newMarkFlag = true;

                if (!CommonUtil.checkNB(mark)) {
                    errorMsg = "请输入区域名称！";
                } else if (_newMarkFlag && checkMarkDuplicate(mark)) {
                    errorMsg = "已有该区域名称！";
                } else {
                    submitFlag = true;
                }

                if (!submitFlag) {
                    // 不能提交
                    if (CommonUtil.checkNB(errorMsg)) {
                        show(errorMsg);
                    }
                } else {
                    markTv.setText(mark);
                    cardMacTv.setText("");
                    areaSignInfo = null;
                    newMarkFlag = true;
                    markContentLayout.setBackgroundResource(R.drawable.border_green);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.VISIBLE);
                    markChooseBtn.setVisibility(View.GONE);
                    markDeleteBtn.setVisibility(View.VISIBLE);
                    createAreaSignTitleDlg.cancel();
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createAreaSignTitleDlg.cancel();
            }
        });
    }

    /**
     * 显示编辑区域标识对话框
     */
    public void makeCreateAreaSignInfoDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_areasign_info_edit, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("新建区域标识");
        dlgBuilder.setIcon(R.drawable.comment_edit);
        dlgBuilder.setCancelable(true);

        // 卡号
        TextView mac_tv = (TextView) layout.findViewById(R.id.mac_tv);
        mac_tv.setText(cardMacTv.getText().toString());
        // 区域名称
        TextView info_tv = (TextView) layout.findViewById(R.id.info_tv);
        info_tv.setText(markTv.getText().toString());

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

        createAreaSignInfoDlg = dlgBuilder.create();
        createAreaSignInfoDlg.show();

        // 确定按钮
        Button confirmBtn = createAreaSignInfoDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = createAreaSignInfoDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean submitFlag = false;
                String errorMsg = "";

                TextView info_tv = (TextView) createAreaSignInfoDlg.findViewById(R.id.info_tv);
                String mark = info_tv.getText().toString();
                String cardMac = cardMacTv.getText().toString();

                if (!CommonUtil.checkNB(mark)) {
                    errorMsg = "请输入区域名称！";
                } else if (!CommonUtil.checkNB(cardMac)) {
                    errorMsg = "请扫描UHF卡片！";
                } else if (newMarkFlag && checkMarkDuplicate(mark)) {
                    errorMsg = "已有该区域名称！";
                } else {
                    submitFlag = true;
                }

                if (!submitFlag) {
                    // 不能提交
                    if (CommonUtil.checkNB(errorMsg)) {
                        show(errorMsg);
                    }
                } else {
                    markTv.setText(mark);
                    newMarkFlag = true;
                    markContentLayout.setBackgroundResource(R.drawable.border_green);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.VISIBLE);
                    markChooseBtn.setVisibility(View.GONE);
                    markDeleteBtn.setVisibility(View.VISIBLE);
                    createAreaSignInfoDlg.cancel();
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createAreaSignInfoDlg.cancel();
            }
        });
    }

    // UHF相关事件与方法。开始========================================================
    static HashMap<Integer, Integer> uhfSoundMap = new HashMap<Integer, Integer>();
    private static SoundPool uhfSoundPool;
    private static AudioManager uhfAm;

//    private void initUHFSound() {
//        uhfSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
//        uhfSoundMap.put(0, uhfSoundPool.load(this, R.raw.barcodebeep, 1));
//        uhfAm = (AudioManager) this.getSystemService(AUDIO_SERVICE);
//        实例化AudioManager对象
//        Reader.rrlib.setsoundid(uhfSoundMap.get(0), uhfSoundPool);
//    }

    private int uhfSoundKey = 0;

    private void initUHFSound() {
        uhfSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        uhfSoundMap.put(0, uhfSoundPool.load(this, R.raw.blank, 1));
//        uhfSoundMap.put(0, uhfSoundPool.load(this, R.raw.barcodebeep, 1));
//        uhfSoundMap.put(1, uhfSoundPool.load(this, R.raw.readcard, 1));
//        uhfSoundMap.put(2, uhfSoundPool.load(this, R.raw.gun, 1));
        uhfAm = (AudioManager) this.getSystemService(AUDIO_SERVICE);
//        // 实例化AudioManager对象
        Reader.rrlib.setsoundid(uhfSoundMap.get(uhfSoundKey), uhfSoundPool);
    }

    /**
     * 初始化UHF信息
     */
    private void initUHFData() {
        PowerUtil.power("1");
        initUHFSound();
    }

    public class UhfUhfScanMsgCallback implements TagCallback {

        @Override
        public void tagCallback(ReadTag arg0) {
            String epc = arg0.epcId.toUpperCase();
            InventoryTagMap m;
            Reader.rrlib.beginSound(true);
            Integer findIndex = dtIndexMap.get(epc);
            if (findIndex == null) {
                dtIndexMap.put(epc, dtIndexMap.size());
                m = new InventoryTagMap();
                m.strEPC = epc;
                m.antenna = arg0.antId;
                m.strRSSI = String.valueOf(arg0.rssi);
                m.nReadCount = 1;
                //dtIndexMa
                Reader.rrlib.getInventoryTagMapList().add(m);
            } else {
                m = Reader.rrlib.getInventoryTagMapList().get(findIndex);
                m.antenna |= arg0.antId;
                m.nReadCount++;
                m.strRSSI = String.valueOf(arg0.rssi);
            }
            uhfHandler.removeMessages(CommonParam.UHF_MSG_UPDATE_LISTVIEW);
            uhfHandler.sendEmptyMessage(CommonParam.UHF_MSG_UPDATE_LISTVIEW);

            Message msg = uhfHandler.obtainMessage(CommonParam.UHF_MSG_UPDATE_EPC, epc);
            uhfHandler.sendMessage(msg);
        }

        @Override
        public int CRCErrorCallBack(int reason) {
            if (reason == 1) {
                UhfErrorCRC += 1;
            }
            UhfErrorCount += 1;
            uhfHandler.removeMessages(CommonParam.UHF_MSG_UPDATE_ERROR);
            uhfHandler.sendEmptyMessage(CommonParam.UHF_MSG_UPDATE_ERROR);
            return 0;
        }

        @Override
        public void FinishCallBack() {
            isUhfStopScanThread = false;
            uhfHandler.removeMessages(CommonParam.UHF_MSG_UPDATE_STOP);
            uhfHandler.sendEmptyMessage(CommonParam.UHF_MSG_UPDATE_STOP);
        }

        @Override
        public int tagCallbackFailed(int reason) {
            return 0;
        }
    }

    /**
     * 连接UHF模块
     */
    private void connectUhf() {
        connectUhf(false);
    }

    /**
     * 连接UHF模块
     *
     * @param needAlert {@code boolean} 是否需要发出提示信息
     */
    private void connectUhf(boolean needAlert) {
        try {
            int result = Reader.rrlib.Connect("/dev/ttyS2", CommonParam.UHF_BAUD);
            if (result == 0) {
                if (needAlert) {
                    show(R.string.DEV_OPEN_SUC);
                }
            } else {
                String result_hex = Integer.toHexString(result).toUpperCase(Locale.CHINA);
                result_hex = String.format(result_hex.length() > 1 ? "RV_%s" : "RV_0%s", result_hex);
                int result_string_int = CommonUtil.getFieldValue("string", result_hex, classThis);
                if (result_string_int != -1) {
                    if (needAlert) {
                        show(result_string_int);
                    }
                } else {
                    if (needAlert) {
                        show(R.string.DEV_OPEN_ERR);
                    }
                }
            }
        } catch (Exception e) {
            if (needAlert) {
                show(R.string.DEV_OPEN_ERR);
            }
        }
    }

    /**
     * 断开UHF模块
     */
    private void disconnectUhf() {
        disconnectUhf(false);
    }

    /**
     * 断开UHF模块
     *
     * @param needAlert {@code boolean} 是否需要发出提示信息
     */
    private void disconnectUhf(boolean needAlert) {
        try {
            if (Reader.rrlib.IsConnected()) {
                int result = Reader.rrlib.DisConnect();
                if (result == 0) {
                    if (needAlert) {
                        show(R.string.DEV_CLOSE_SUC);
                    }
                } else {
                    String result_hex = Integer.toHexString(result).toUpperCase(Locale.CHINA);
                    result_hex = String.format(result_hex.length() > 1 ? "RV_%s" : "RV_0%s", result_hex);
                    int result_string_int = CommonUtil.getFieldValue("string", result_hex, classThis);
                    if (result_string_int != -1) {
                        if (needAlert) {
                            show(result_string_int);
                        }
                    } else {
                        if (needAlert) {
                            show(R.string.DEV_CLOSE_ERR);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (needAlert) {
                show(R.string.DEV_CLOSE_ERR);
            }
        }
    }

    /**
     * 扫描UHF卡片
     */
    private void startScanUhf() {
        try {
            if (uhfScanTimer == null) {
                if (isUhfStopScanThread) return;
                isUhfStopScanThread = true;
                isUhfActive = true;
                isUhfScanStop = false;
                Reader.rrlib.getInventoryTagMapList().clear();
                Reader.rrlib.getInventoryTagResultList().clear();
                dtIndexMap = new LinkedHashMap<String, Integer>();
                UhfUhfScanMsgCallback callback = new UhfUhfScanMsgCallback();
                Reader.rrlib.SetCallBack(callback);
                UhfErrorCount = 0;
                UhfErrorCRC = 0;
                if (Reader.rrlib.StartRead() == 0) {
                    isUhfScaning = true;
                    uhfBeginTime = System.currentTimeMillis();
                    uhfScanTimer = new Timer();
                    uhfScanTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            uhfHandler.removeMessages(CommonParam.UHF_MSG_UPDATE_TIME);
                            uhfHandler.sendEmptyMessage(CommonParam.UHF_MSG_UPDATE_TIME);
                        }
                    }, 0, CommonParam.UHF_SCAN_INTERVAL);
                    setUhfScanFieldScanning();
                }
            } else {
                Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
                confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
                confirmBtn.setText("开始扫描");
                stopScanUhf();
            }
        } catch (Exception e) {
            Button confirmBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_POSITIVE);
            confirmBtn.setTextColor(getResources().getColor(R.color.text_green_dark));
            confirmBtn.setText("开始扫描");
            stopScanUhf();
        }
    }

    /**
     * 停止扫描UHF卡片
     */
    private void stopScanUhf() {
        stopScanUhf(true);
    }

    /**
     * 停止扫描UHF卡片
     *
     * @param needAlert {@code boolean} 是否需要发出提示信息
     */
    private void stopScanUhf(boolean needAlert) {
        isUhfActive = false;
        Reader.rrlib.StopRead();
        isUhfScaning = false;
        if (uhfScanTimer != null) {
            uhfScanTimer.cancel();
            uhfScanTimer = null;
            if (needAlert) {
                setUhfScanFieldStart();
            }
        }
    }

    /**
     * 设置扫描相关字段状态：准备开始
     */
    private void setUhfScanFieldStart() {
        setUhfScanFieldStart(getString(R.string.text_scan_uhf_card_info_not_start));
    }

    /**
     * 设置扫描相关字段状态：准备开始
     *
     * @param msg {@code String} 提示文本
     */
    private void setUhfScanFieldStart(String msg) {
        if (readUhfCardType == CommonParam.READ_UHF_CARD_TYPE_INSPECT) {
            // 读巡视卡
            ImageView animate_iv = (ImageView) readUhfCardDlg.findViewById(R.id.animate_iv);
            AnimationDrawable animate_ad = (AnimationDrawable) animate_iv.getBackground();
            animate_ad.stop();
            animate_ad.selectDrawable(0);
            TextView info_tv = (TextView) readUhfCardDlg.findViewById(R.id.info_tv);
            info_tv.setText(Html.fromHtml(msg, null, new HtmlTagHandler(classThis)));
            info_tv.setTextSize(16.0F);
        }
    }

    /**
     * 设置扫描相关字段状态：正在扫描
     */
    private void setUhfScanFieldScanning() {
        if (readUhfCardType == CommonParam.READ_UHF_CARD_TYPE_INSPECT) {
            ImageView animate_iv = (ImageView) readUhfCardDlg.findViewById(R.id.animate_iv);
            AnimationDrawable animate_ad = (AnimationDrawable) animate_iv.getBackground();
            animate_ad.start();
            TextView info_tv = (TextView) readUhfCardDlg.findViewById(R.id.info_tv);
            info_tv.setText(Html.fromHtml(getString(R.string.text_scan_uhf_card_info_scanning), null, new HtmlTagHandler(classThis)));
            info_tv.setTextSize(15.0F);
        }
    }

    /**
     * 设置扫描相关字段状态：正在停止
     */
    private void setUhfScanFieldStopping() {
    }

    /**
     * 设置扫描相关字段状态：停止扫描
     */
    private void setUhfScanFieldStop() {
    }

    /**
     * 设置UHF功率
     * <p>功率范围0-30(dbm)</p>
     */
    private void setUhfPower(int powerValue) {
        int result = Reader.rrlib.SetRfPower(powerValue);
        if (result == 0) {
            getUhfInfo();
            show("功率修改成功");
        } else {
            show("功率修改失败");
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
                int currentNum = Integer.parseInt(currentNumTv_nav.getText().toString());
                // 功率按钮
                Button powerBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEUTRAL);
                powerBtn.setText("功率：" + currentNum);
                try {
                    setUhfPower(currentNum);
                    Reader.rrlib.SetRfPower(currentNum);
                    getUhfInfo();
                    baseApp.uhfPower = (int) baseApp.uhfPdaInfo.get("power");
                    preferEditor.putInt("uhfPower", baseApp.uhfPower);
                    preferEditor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // UHF相关事件与方法。结束========================================================

    /**
     * 查找view
     */
    public void findViews() {
        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        // 界面相关参数。开始===============================
        bizTitleTv = (TextView) findViewById(R.id.bizTitleTv);
        ctimeTv = (TextView) findViewById(R.id.ctimeTv);
        resTitleTv = (TextView) findViewById(R.id.resTitleTv);
        markContentLayout = (LinearLayout) findViewById(R.id.markContentLayout);
        markTv = (TextView) findViewById(R.id.markTv);
        cardMacTv = (TextView) findViewById(R.id.cardMacTv);
        markChooseBtn = (ImageButton) findViewById(R.id.markChooseBtn);
        markDeleteBtn = (ImageButton) findViewById(R.id.markDeleteBtn);
        addUhfBtn = (ImageButton) findViewById(R.id.addUhfBtn);
        editUhfBtn = (ImageButton) findViewById(R.id.editUhfBtn);
        scanUhfBtn = (Button) findViewById(R.id.scanUhfBtn);
        lngLatTv = (TextView) findViewById(R.id.lngLatTv);
        getLnglatBtn = (Button) findViewById(R.id.getLnglatBtn);
        jlrNameTv = (TextView) findViewById(R.id.jlrNameTv);
        fzrNameTv = (TextView) findViewById(R.id.fzrNameTv);

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
