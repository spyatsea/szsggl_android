/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.PopupMenu;

import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cox.android.handler.HtmlTagHandler;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsPointEditListAdapter;
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

/**
 * 巡视_巡视点_编辑状态_列表页面
 * <p>这里使用了<i>SearchTask</i>生成巡视点列表</p>
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsPointEditListLandActivity extends DbActivity {
    // 常量。开始===============================
    /**
     * 定位状态：不需要定位
     */
    private static final String LOC_STATUS_NONE = "";
    /**
     * 定位状态：未定位
     */
    private static final String LOC_STATUS_NO = "--";
    /**
     * 定位状态：已定位
     */
    private static final String LOC_STATUS_YES = "√";
    /**
     * 定位方式：不定位
     */
    private static final String LOC_TYPE_NONE = "0";
    /**
     * 定位方式：RFID
     */
    private static final String LOC_TYPE_RFID = "1";
    /**
     * 定位方式：GPS
     */
    private static final String LOC_TYPE_GPS = "2";
    /**
     * 定位方式：UHF
     */
    private static final String LOC_TYPE_UHF = "3";
    /**
     * 读卡类型：定位
     */
    private static final String SCAN_CARD_TYPE_LOC = "loc";
    /**
     * 读卡类型：采集
     */
    private static final String SCAN_CARD_TYPE_ADD = "add";
    /**
     * 读UHF卡类型：定位
     */
    private static final String SCAN_UHF_CARD_TYPE_LOC = "loc";
    /**
     * 读UHF卡类型：采集
     */
    private static final String SCAN_UHF_CARD_TYPE_ADD = "add";
    /**
     * 位置类型：定位
     */
    private static final String LOCATION_TYPE_LOC = "loc";
    /**
     * 位置类型：采集
     */
    private static final String LOCATION_TYPE_ADD = "add";
    // 常量。结束===============================

    /**
     * 当前类对象
     */
    DbActivity classThis;
    /**
     * 主界面
     */
    LinearLayout contentView;
    /**
     * 巡视类型名称
     */
    TextView titleBarModeName;
    /**
     * 巡视状态
     */
    TextView infoStatusTv;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 回到主页按钮
     */
    ImageButton homeBtn;
    /**
     * 帮助按钮
     */
    ImageButton helpBtn;
    /**
     * 显示任务详情按钮
     */
    Button insTaskShowBtn;
    /**
     * 列表名称区
     */
    LinearLayout listTitleLayout;
    /**
     * 列表名称
     */
    TextView listTitleTv;
    /**
     * 返回
     */
    private Button goBackBtn;
    /**
     * 检查
     */
    private Button jcBtn;
    /**
     * 打卡
     */
    private Button dkBtn;
    /**
     * 扫卡快签
     */
    private Button autoDkBtn;
    /**
     * 定位
     */
    private Button locBtn;
    /**
     * 采集
     */
    private Button locAddBtn;
    /**
     * 结束巡视
     */
    private Button finishBtn;
    // 界面相关参数。开始===============================
    private TextView totalNumTv;
    private TextView currentNumTv;
    /**
     * 读卡Dialog
     */
    private AlertDialog readCardDlg;
    /**
     * 读UHF卡Dialog
     */
    private AlertDialog readUhfCardDlg;
    /**
     * 弹出菜单
     */
    private PopupMenu popupMenu;
    /**
     * 获取位置Dialog
     */
    private AlertDialog getLocationDlg;
    /**
     * 巡视结束Dialog
     */
    private AlertDialog finishInsDlg;
    /**
     * 巡视结束总结Dialog
     */
    private AlertDialog summaryDlg;
    /**
     * 是否需要提示用户旋转屏幕
     */
    boolean needShowRotatePortraitAlertFlag = true;
    /**
     * 提示用户旋转屏幕Dialog
     */
    private AlertDialog rotateAlertDlg;

    /**
     * 继续播放音频标记
     */
    private boolean playVoiceContinueFlag;
    /**
     * 音频文件信息
     */
    private HashMap<String, Object> audioInfo;
    /**
     * 录音器
     */
    private MediaRecorder mediaRecorder;
    /**
     * 录音临时文件
     */
    private File capAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
            + CommonParam.PROJECT_NAME + "/temp/a.m4a");
    // 界面相关参数。结束===============================

    // 子列表相关参数。开始===============================
    /**
     * 表格中的复选框
     */
    private List<CheckBox> table_checkBoxButtonList;
    // 子列表相关参数。结束===============================

    /**
     * 信息列表
     */
    private ArrayList<HashMap<String, Object>> listItems;
    /**
     * 信息listview
     */
    private ListView infoList;
    /**
     * 列表Adapter
     */
    private InsPointEditListAdapter infoListAdapter;
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 查询信息 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> searchTask;
    /**
     * 每页大小
     */
    int ROWS_PER_PAGE = CommonParam.RESULT_LIST_PER * 100;
    // 网络连接相关参数。开始==========================================
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    // 网络连接相关参数。结束==========================================

    // 查询参数。开始==========================================
    // 查询参数。结束==========================================
    /**
     * 任务信息
     */
    private HashMap<String, Object> bizInfo;
    /**
     * 选中的资源编号
     */
    private String currentResId;
    /**
     * 选中的资源信息
     */
    private HashMap<String, Object> currentResInfo;
    /**
     * 未完成检查的信息Map
     * <p>key: 巡视点编号<br/>
     * value: 检查记录编号</p>
     */
    HashMap<String, String> unfinishJcMap;
    /**
     * 水工资源采集定位记录
     * <p>key: 资源编号<br/>
     * value: 定位参数</p>
     */
    Map<String, String> resLocMap;
    /**
     * 更新标志
     */
    private boolean updateFlag;
    /**
     * 扫描卡片类型
     */
    private String scanCardType;
    /**
     * 扫描UHF卡片类型
     */
    private String scanUhfCardType;
    /**
     * 位置类型
     */
    private String locationType;
    /**
     * 页面Handler
     */
    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 10:
                    getLocationDlg.cancel();
                    break;
                case 20:
                    scanCardType = SCAN_CARD_TYPE_LOC;
                    break;
                case 30:
                    scanUhfCardType = SCAN_UHF_CARD_TYPE_LOC;
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

        classThis = InsPointEditListLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");

        setContentView(R.layout.ins_point_edit_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("请选择巡视点");

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
        helpBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeHelpDialog(R.layout.dlg_help_ins_point_list);
            }
        });
        insTaskShowBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("bizInfo", bizInfo);

                // 创建启动 Activity 的 Intent
                Intent intent = null;
                if (!baseApp.isReverseRotate) {
                    intent = new Intent(classThis, InsTaskShowLandActivity.class);
                } else {
                    intent = new Intent(classThis, InsTaskShowReverseLandActivity.class);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivity(intent);
                //finish();
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });
        goBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        // 检查登记
        jcBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jcBtn.setClickable(false);
                jcBtn.setEnabled(false);
                prepareToJc();
            }
        });
        // 打卡
        dkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dkBtn.setClickable(false);
                dkBtn.setEnabled(false);
                prepareToDk();
            }
        });
        // 扫卡快签
        autoDkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                autoDkBtn.setClickable(false);
                autoDkBtn.setEnabled(false);
                prepareToAutoDk();
            }
        });
        // 定位
        locBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prepareToLoc();
            }
        });
        // 采集
        locAddBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prepareToLocAdd();
            }
        });
        // 结束巡视
        finishBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckFinishTask().execute();
            }
        });

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
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            initUHFData();
        }
        // UHF相关事件与方法。结束============================
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
        } else if (requestCode == CommonParam.REQUESTCODE_LIST && (resultCode == CommonParam.RESULTCODE_REFRESH_REC_LIST || resultCode == CommonParam.RESULTCODE_NEW_REC)) {
            updateFlag = true;
            if (!isConnecting) {
                searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
            }
        }
    }

    /**
     * 返回
     */
    @Override
    public void goBack() {
        if (updateFlag) {
            setResult(CommonParam.RESULTCODE_REFRESH_REC_LIST);
        }
        super.goBack();
    }

    @Override
    public void onResume() {
        super.onResume();
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            isUhfStopScanThread = false;
        }
        // UHF相关事件与方法。结束============================
    }

    @Override
    public void onPause() {
        super.onPause();
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            if (isUhfScaning) {
                stopScanUhf();
            }
        }
        // UHF相关事件与方法。结束============================
    }

    @Override
    protected void onDestroy() {
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            disconnectUhf(false);
            PowerUtil.power("0");
        }
        // UHF相关事件与方法。结束============================
        if (mainTask != null) {
            mainTask.cancel(true);
        }
        if (searchTask != null) {
            searchTask.cancel(true);
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
            listItems = new ArrayList<HashMap<String, Object>>();
            table_checkBoxButtonList = new ArrayList<CheckBox>();
            unfinishJcMap = new HashMap<String, String>();
            needShowRotatePortraitAlertFlag = preferences.getBoolean("needShowRotatePortraitAlertFlag", true);
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
                listTitleLayout.setVisibility(View.GONE);
                // 巡视类别
                String ctype = (String) bizInfo.get("ctype");
                String ctype_name = null;
                if ("2".equals(ctype)) {
                    ctype_name = "停水检查";
                } else if ("3".equals(ctype)) {
                    ctype_name = "特殊检查";
                } else {
                    ctype_name = "日常检查";
                }
                titleBarModeName.setText(ctype_name);

                String realatime = (String) bizInfo.get("realatime");
                String realbtime = (String) bizInfo.get("realbtime");
                if (CommonUtil.checkNB(realatime)) {
                    if (CommonUtil.checkNB(realbtime)) {
                        infoStatusTv.setText("已完成");
                        infoStatusTv.setTextColor(getResources().getColor(R.color.text_green_dark));
                    } else {
                        infoStatusTv.setText("未完成");
                        infoStatusTv.setTextColor(getResources().getColor(R.color.text_purple));
                    }
                } else {
                    infoStatusTv.setText("未巡视");
                    infoStatusTv.setTextColor(getResources().getColor(R.color.text_orange_dark));
                }

                RelativeLayout bottomBar = findViewById(R.id.bottomBar);
                LinearLayout bottomBarLeft = findViewById(R.id.bottomBarLeft);
                LinearLayout bottomBarRight = findViewById(R.id.bottomBarRight);
                if (baseApp.isUhfPda) {
                    // 设备支持UHF
                    if (bottomBar.getWidth() < (bottomBarLeft.getWidth() + bottomBarRight.getWidth() + jcBtn.getWidth() + dkBtn.getWidth() + CommonUtil.dip2px(classThis, 35.0f))) {
                        // 如果按钮的总宽度超过工具栏的宽度
                        locAddBtn.setCompoundDrawables(null, null, null, null);
                        locBtn.setCompoundDrawables(null, null, null, null);
                        autoDkBtn.setCompoundDrawables(null, null, null, null);
                        dkBtn.setCompoundDrawables(null, null, null, null);

                        int btnWidth = CommonUtil.dip2px(classThis, 70.0f);
                        int btnMargin = CommonUtil.dip2px(classThis, 10.0f);
                        ViewGroup.LayoutParams locAddBtn_lp = locAddBtn.getLayoutParams();
                        locAddBtn_lp.width = btnWidth;
                        ViewGroup.LayoutParams locBtn_lp = locBtn.getLayoutParams();
                        locBtn_lp.width = btnWidth;
                        ViewGroup.LayoutParams dkBtn_lp = dkBtn.getLayoutParams();
                        dkBtn_lp.width = btnWidth;

                        ViewGroup.MarginLayoutParams locAddBtn_mp = (ViewGroup.MarginLayoutParams) locAddBtn.getLayoutParams();
                        locAddBtn_mp.rightMargin = btnMargin;
                        ViewGroup.MarginLayoutParams locBtn_mp = (ViewGroup.MarginLayoutParams) locBtn.getLayoutParams();
                        locBtn_mp.rightMargin = btnMargin;
                        ViewGroup.MarginLayoutParams dkBtn_mp = (ViewGroup.MarginLayoutParams) dkBtn.getLayoutParams();
                        dkBtn_mp.rightMargin = btnMargin;
                        ViewGroup.MarginLayoutParams autoDkBtn_mp = (ViewGroup.MarginLayoutParams) autoDkBtn.getLayoutParams();
                        autoDkBtn_mp.rightMargin = btnMargin;
                    }
                    autoDkBtn.setVisibility(View.VISIBLE);
                }
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (InsPointEditListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoListAdapter = new InsPointEditListAdapter(getApplicationContext(), listItems, R.layout.ins_point_edit_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info", "info"}, new int[]{R.id.tableRowLayout, R.id.info_c1, R.id.info_cr,
                            R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.info_c5});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new InsPointEditListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof LinearLayout) {
                                LinearLayout layout = (LinearLayout) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (layout.getId() == R.id.tableRowLayout) {
                                    String id = (String) info.get("ids");
                                    layout.setTag("tableRowLayout_" + id);
                                    int n = (int) info.get("V_INFO_SN");
                                    // 如果是第一行，要显示上边框
                                    if (n == 1) {
                                        View splitterView_1 = layout.findViewById(R.id.splitterView_1);
                                        splitterView_1.setVisibility(View.VISIBLE);
                                    }
                                    // 如果之前选中了某行，就要在刷新列表后重新选择该行
                                    if (currentResId != null && id.equals(currentResId)) {
                                        CheckBox btn = (CheckBox) layout.findViewById(R.id.info_r);
                                        btn.setChecked(true);
                                        layout.setBackgroundColor(getResources().getColor(R.color.list_color_content_background_even_blue_01));

                                        // 未完成的检查信息编号
                                        String unfinishJcId = unfinishJcMap.get((String) info.get("ids"));
                                        Drawable jcBtn_icon = null;
                                        if (!CommonUtil.checkNB(unfinishJcId)) {
                                            // 已完成
                                            jcBtn.setText(R.string.title_jc_new);
                                            jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_create_normal);
                                        } else {
                                            // 未完成
                                            jcBtn.setText(R.string.title_jc_continue);
                                            jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_edit_normal);
                                        }
                                        jcBtn_icon.setBounds(0, 0, jcBtn_icon.getMinimumWidth(), jcBtn_icon.getMinimumHeight());
                                        jcBtn.setCompoundDrawables(jcBtn_icon, null, null, null);

                                        // 定位方式
                                        String loctype = (String) info.get("loctype");
                                        if (!LOC_TYPE_NONE.equals(loctype)) {
                                            if (CommonUtil.checkNB((String) info.get("loc"))) {
                                                // 有定位参数
                                                locAddBtn.setVisibility(View.GONE);
                                            } else {
                                                // 没有定位参数
                                                locAddBtn.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            locAddBtn.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (layout.getId() == R.id.info_cr) {
                                    CheckBox btn = (CheckBox) layout.findViewById(R.id.info_r);
                                    btn.setTag((String) info.get("ids"));
                                    btn.setClickable(false);
                                    table_checkBoxButtonList.add(btn);
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onClickCheckBoxColumn((LinearLayout) v);
                                            //titleText.setText(CommonUtil.N2B(currentResId));
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c1) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t1);
                                    textView.setText("" + info.get("V_INFO_SN"));
                                } else if (layout.getId() == R.id.info_c2) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t2);
                                    // textView.setText(CommonUtil.N2B((String) info.get("_t")) + "#" + CommonUtil.N2B((String) info.get("loctype")) + "#" + CommonUtil.N2B((String) info.get("loc")));
                                    textView.setText(CommonUtil.N2B((String) info.get("_t")));
                                    // 这里是为了让表格行高一些，方便点击
                                    ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                                    mp.topMargin = 25;
                                    mp.bottomMargin = 25;
                                    mp.leftMargin = 10;
                                    mp.rightMargin = 10;
                                    layout.setTag((String) info.get("ids"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 创建信息传输Bundle
                                            Bundle data = new Bundle();
                                            data.putString("id", (String) v.getTag());
                                            data.putString("fromFlag", "list");
                                            if (!baseApp.isReverseRotate) {
                                                data.putString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
                                            } else {
                                                data.putString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
                                            }

                                            // 创建启动 Activity 的 Intent
                                            Intent intent = null;
                                            if (!baseApp.isReverseRotate) {
                                                intent = new Intent(classThis, SgResShowLandActivity.class);
                                            } else {
                                                intent = new Intent(classThis, SgResShowReverseLandActivity.class);
                                            }

                                            // 将数据存入 Intent 中
                                            intent.putExtras(data);
                                            startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c3) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t3);
                                    TextView textView_x = (TextView) layout.findViewById(R.id.info_t3x);
                                    // 定位
                                    String _v = null;
                                    // 定位方式
                                    String loctype = (String) info.get("loctype");
                                    // 定位方式名称
                                    String loctype_name = "";
                                    // 是否已定位
                                    String _loc = (String) info.get("_loc");
                                    // 资源定位参数
                                    String resLoc = null;
                                    if (LOC_TYPE_NONE.equals(loctype)) {
                                        // 如果不需要定位
                                        _v = LOC_STATUS_NONE;
                                        loctype_name = "";
                                        textView_x.setVisibility(View.GONE);
                                        textView_x.setText(loctype_name);
                                        textView_x.setBackgroundColor(getResources().getColor(R.color.background_grey_dark));
                                        textView.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                    } else {
                                        // 如果需要定位
                                        if (CommonParam.YES.equals(_loc)) {
                                            _v = LOC_STATUS_YES;
                                        } else {
                                            _v = LOC_STATUS_NO;
                                        }
                                        if (LOC_TYPE_RFID.equals(loctype)) {
                                            loctype_name = "RFID";
                                        } else if (LOC_TYPE_UHF.equals(loctype)) {
                                            loctype_name = "UHF";
                                        } else {
                                            loctype_name = "GPS";
                                        }
                                        textView_x.setVisibility(View.VISIBLE);
                                        textView_x.setText(loctype_name);

                                        if (LOC_STATUS_YES.equals(_v)) {
                                            // 已定位
                                            textView.setTextColor(getResources().getColor(R.color.text_green_dark));
                                            textView_x.setBackgroundColor(getResources().getColor(R.color.ade_dark_green));
                                        } else {
                                            // 未定位
                                            // 分为两种情况：1、有定位参数，但尚未定位；2、没有定位参数，需要先采集定位信息，再进行定位。
                                            textView.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                            if (CommonUtil.checkNB((String) info.get("loc"))) {
                                                // 有定位参数
                                                textView_x.setBackgroundColor(getResources().getColor(R.color.background_title_blue));
                                            } else {
                                                if (CommonUtil.checkNB(resLocMap.get((String) info.get("ids")))) {
                                                    // 资源采集定位表中有定位参数
                                                    textView_x.setBackgroundColor(getResources().getColor(R.color.background_title_blue));
                                                } else {
                                                    // 资源采集定位表中没有定位参数
                                                    textView_x.setBackgroundColor(getResources().getColor(R.color.background_grey_dark));
                                                }
                                            }
                                        }
                                    }
                                    textView.setText(_v);
                                    layout.setTag((String) info.get("ids"));
                                    if (CommonParam.TEST_MODE) {
                                        // 在测试模式下，长按单元格，如果该巡视点是RFID/UHF定位，且尚未定位，则选中该巡视点，并且模拟进行定位
                                        layout.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                // 当前行的巡视点编号
                                                String _id = (String) v.getTag();
                                                LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + _id);
                                                if (tableRowLayout != null) {
                                                    TextView textView_t1 = (TextView) tableRowLayout.findViewById(R.id.info_t1);
                                                    if (textView_t1 != null) {
                                                        HashMap<String, Object> _currentResInfo = null;
                                                        // 序号
                                                        String n = textView_t1.getText().toString();
                                                        // 索引
                                                        int index = -1;
                                                        try {
                                                            index = Integer.parseInt(n) - 1;
                                                        } catch (Exception e) {
                                                        }
                                                        if (index != -1) {
                                                            HashMap<String, Object> vMap = listItems.get(index);
                                                            if (vMap != null) {
                                                                _currentResInfo = (HashMap<String, Object>) vMap.get("info");
                                                            }
                                                        }
                                                        // 定位方式
                                                        String loctype = (String) _currentResInfo.get("loctype");
                                                        // 是否已定位
                                                        String _loc = (String) _currentResInfo.get("_loc");
                                                        checkTableRow(tableRowLayout, true);
                                                        if (LOC_TYPE_NONE.equals(loctype)) {
                                                            // 不需要定位
                                                            show("本巡视点无需定位！");
                                                        } else {
                                                            // 需要定位
                                                            if (CommonParam.YES.equals(_loc)) {
                                                                // 已定位
                                                                show("本巡视点已定位！");
                                                            } else {
                                                                // 未定位
                                                                // 定位参数
                                                                String loc = (String) _currentResInfo.get("loc");
                                                                if (!CommonUtil.checkNB(loc)) {
                                                                    loc = resLocMap.get((String) _currentResInfo.get("ids"));
                                                                }
                                                                if (LOC_TYPE_RFID.equals(loctype)) {
                                                                    // RFID定位
                                                                    if (CommonUtil.checkNB(loc)) {
                                                                        // 有定位参数
                                                                        readCard_ins(loc);
                                                                    } else {
                                                                        // 没有定位参数
                                                                        show("请先采集定位信息！");
                                                                    }
                                                                } else if (LOC_TYPE_UHF.equals(loctype)) {
                                                                    // UHF定位
                                                                    if (CommonUtil.checkNB(loc)) {
                                                                        // 有定位参数
                                                                        readUhfCard_ins(loc);
                                                                    } else {
                                                                        // 没有定位参数
                                                                        show("请先采集定位信息！");
                                                                    }
                                                                } else {
                                                                    // GPS定位
                                                                    if (CommonUtil.checkNB(loc)) {
                                                                        // 有定位参数
                                                                        if (setupLocationFunc()) {
                                                                            String[] locArray = loc.split(",");
                                                                            String lng_loc = locArray[0];
                                                                            String lat_loc = locArray[1];
                                                                            Double lng_loc_double = 0.0D;
                                                                            Double lat_loc_double = 0.0D;
                                                                            try {
                                                                                lng_loc_double = Double.parseDouble(lng_loc);
                                                                            } catch (Exception e) {
                                                                            }
                                                                            try {
                                                                                lat_loc_double = Double.parseDouble(lat_loc);
                                                                            } catch (Exception e) {
                                                                            }

                                                                            locationType = LOCATION_TYPE_LOC;
                                                                            location_ins(new LatLng(lat_loc_double, lng_loc_double));
                                                                        }
                                                                    } else {
                                                                        // 没有定位参数
                                                                        show("请先采集定位信息！");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                return true;
                                            }
                                        });
                                    }
                                } else if (layout.getId() == R.id.info_c4) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t4);
                                    textView.setText(Integer.toString((Integer) info.get("_dkNum")));
                                    layout.setTag((String) info.get("ids"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 当前行的巡视点编号
                                            String _id = (String) v.getTag();
                                            LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + _id);
                                            // 打卡信息数量
                                            Integer _dkNum = 0;
                                            HashMap<String, Object> _currentResInfo = null;
                                            if (tableRowLayout != null) {
                                                TextView textView_t1 = (TextView) tableRowLayout.findViewById(R.id.info_t1);
                                                if (textView_t1 != null) {
                                                    // 序号
                                                    String n = textView_t1.getText().toString();
                                                    // 索引
                                                    int index = -1;
                                                    try {
                                                        index = Integer.parseInt(n) - 1;
                                                    } catch (Exception e) {
                                                    }
                                                    if (index != -1) {
                                                        HashMap<String, Object> vMap = listItems.get(index);
                                                        if (vMap != null) {
                                                            _currentResInfo = (HashMap<String, Object>) vMap.get("info");
                                                            if (_currentResInfo != null) {
                                                                _dkNum = (Integer) _currentResInfo.get("_dkNum");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (_dkNum > 0) {
                                                // 创建信息传输Bundle
                                                Bundle data = new Bundle();
                                                data.putSerializable("bizInfo", bizInfo);
                                                data.putSerializable("resInfo", _currentResInfo);

                                                // 创建启动 Activity 的 Intent
                                                Intent intent = null;
                                                if (!baseApp.isReverseRotate) {
                                                    intent = new Intent(classThis, InsDkHistoryListLandActivity.class);
                                                } else {
                                                    intent = new Intent(classThis, InsDkHistoryListReverseLandActivity.class);
                                                }
                                                // 将数据存入 Intent 中
                                                intent.putExtras(data);
                                                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                                                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                            } else {
                                                show("该巡视点没有签到打卡记录！");
                                            }
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c5) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t5);
                                    textView.setText(Integer.toString((Integer) info.get("_jcNum")));
                                    layout.setTag((String) info.get("ids"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 当前行的巡视点编号
                                            String _id = (String) v.getTag();
                                            LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + _id);
                                            // 检查信息数量
                                            Integer _jcNum = 0;
                                            HashMap<String, Object> _currentResInfo = null;
                                            if (tableRowLayout != null) {
                                                TextView textView_t1 = (TextView) tableRowLayout.findViewById(R.id.info_t1);
                                                if (textView_t1 != null) {
                                                    // 序号
                                                    String n = textView_t1.getText().toString();
                                                    // 索引
                                                    int index = -1;
                                                    try {
                                                        index = Integer.parseInt(n) - 1;
                                                    } catch (Exception e) {
                                                    }
                                                    if (index != -1) {
                                                        HashMap<String, Object> vMap = listItems.get(index);
                                                        if (vMap != null) {
                                                            _currentResInfo = (HashMap<String, Object>) vMap.get("info");
                                                            if (_currentResInfo != null) {
                                                                _jcNum = (Integer) _currentResInfo.get("_jcNum");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (_jcNum > 0) {
                                                // 创建信息传输Bundle
                                                Bundle data = new Bundle();
                                                data.putSerializable("bizInfo", bizInfo);
                                                data.putSerializable("resInfo", _currentResInfo);

                                                // 创建启动 Activity 的 Intent
                                                Intent intent = null;
                                                if (!baseApp.isReverseRotate) {
                                                    intent = new Intent(classThis, InsJcHistoryListLandActivity.class);
                                                } else {
                                                    intent = new Intent(classThis, InsJcHistoryListReverseLandActivity.class);
                                                }
                                                // 将数据存入 Intent 中
                                                intent.putExtras(data);
                                                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                                                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                            } else {
                                                show("该巡视点没有现场检查记录！");
                                            }
                                        }
                                    });
                                    // 未完成的检查信息编号
                                    String unfinishJcId = unfinishJcMap.get((String) info.get("ids"));
                                    TextView textView_x = (TextView) layout.findViewById(R.id.info_t5x);
                                    if (CommonUtil.checkNB(unfinishJcId)) {
                                        textView_x.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_x.setVisibility(View.GONE);
                                    }
                                }
                                return true;
                            } else if (view instanceof FrameLayout) {
                                FrameLayout layout = (FrameLayout) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (layout.getId() == R.id.tableRowLayout) {
                                    String id = (String) info.get("ids");
                                    layout.setTag("tableRowLayout_" + id);
                                    int n = (int) info.get("V_INFO_SN");
                                    // 如果是第一行，要显示上边框
                                    if (n == 1) {
                                        View splitterView_1 = layout.findViewById(R.id.splitterView_1);
                                        splitterView_1.setVisibility(View.VISIBLE);
                                    }
                                    // 如果之前选中了某行，就要在刷新列表后重新选择该行
                                    if (currentResId != null && id.equals(currentResId)) {
                                        CheckBox btn = (CheckBox) layout.findViewById(R.id.info_r);
                                        btn.setChecked(true);
                                        layout.setBackgroundColor(getResources().getColor(R.color.list_color_content_background_even_blue_01));

                                        // 未完成的检查信息编号
                                        String unfinishJcId = unfinishJcMap.get((String) info.get("ids"));
                                        Drawable jcBtn_icon = null;
                                        if (!CommonUtil.checkNB(unfinishJcId)) {
                                            // 已完成
                                            jcBtn.setText(R.string.title_jc_new);
                                            jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_create_normal);
                                        } else {
                                            // 未完成
                                            jcBtn.setText(R.string.title_jc_continue);
                                            jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_edit_normal);
                                        }
                                        jcBtn_icon.setBounds(0, 0, jcBtn_icon.getMinimumWidth(), jcBtn_icon.getMinimumHeight());
                                        jcBtn.setCompoundDrawables(jcBtn_icon, null, null, null);

                                        // 定位方式
                                        String loctype = (String) info.get("loctype");
                                        if (!LOC_TYPE_NONE.equals(loctype)) {
                                            if (CommonUtil.checkNB((String) info.get("loc"))) {
                                                // 有定位参数
                                                locAddBtn.setVisibility(View.GONE);
                                            } else {
                                                // 没有定位参数
                                                locAddBtn.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            locAddBtn.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (layout.getId() == R.id.info_cr) {
                                    CheckBox btn = (CheckBox) layout.findViewById(R.id.info_r);
                                    btn.setTag((String) info.get("ids"));
                                    btn.setClickable(false);
                                    table_checkBoxButtonList.add(btn);
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onClickCheckBoxColumn((LinearLayout) v);
                                            //titleText.setText(CommonUtil.N2B(currentResId));
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c1) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t1);
                                    textView.setText("" + info.get("V_INFO_SN"));
                                } else if (layout.getId() == R.id.info_c2) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t2);
                                    // textView.setText(CommonUtil.N2B((String) info.get("_t")) + "#" + CommonUtil.N2B((String) info.get("loctype")) + "#" + CommonUtil.N2B((String) info.get("loc")));
                                    textView.setText(CommonUtil.N2B((String) info.get("_t")));
                                    // 这里是为了让表格行高一些，方便点击
                                    ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                                    mp.topMargin = 25;
                                    mp.bottomMargin = 25;
                                    mp.leftMargin = 10;
                                    mp.rightMargin = 10;
                                    layout.setTag((String) info.get("ids"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 创建信息传输Bundle
                                            Bundle data = new Bundle();
                                            data.putString("id", (String) v.getTag());
                                            data.putString("fromFlag", "list");
                                            if (!baseApp.isReverseRotate) {
                                                data.putString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
                                            } else {
                                                data.putString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
                                            }

                                            // 创建启动 Activity 的 Intent
                                            Intent intent = null;
                                            if (!baseApp.isReverseRotate) {
                                                intent = new Intent(classThis, SgResShowLandActivity.class);
                                            } else {
                                                intent = new Intent(classThis, SgResShowReverseLandActivity.class);
                                            }

                                            // 将数据存入 Intent 中
                                            intent.putExtras(data);
                                            startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c3) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t3);
                                    TextView textView_x = (TextView) layout.findViewById(R.id.info_t3x);
                                    // 定位
                                    String _v = null;
                                    // 定位方式
                                    String loctype = (String) info.get("loctype");
                                    // 定位方式名称
                                    String loctype_name = "";
                                    // 是否已定位
                                    String _loc = (String) info.get("_loc");
                                    // 资源定位参数
                                    String resLoc = null;
                                    if (LOC_TYPE_NONE.equals(loctype)) {
                                        // 如果不需要定位
                                        _v = LOC_STATUS_NONE;
                                        loctype_name = "";
                                        textView_x.setVisibility(View.GONE);
                                        textView_x.setText(loctype_name);
                                        textView_x.setBackgroundColor(getResources().getColor(R.color.background_grey_dark));
                                        textView.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                    } else {
                                        // 如果需要定位
                                        if (CommonParam.YES.equals(_loc)) {
                                            _v = LOC_STATUS_YES;
                                        } else {
                                            _v = LOC_STATUS_NO;
                                        }
                                        if (LOC_TYPE_RFID.equals(loctype)) {
                                            loctype_name = "RFID";
                                        } else if (LOC_TYPE_UHF.equals(loctype)) {
                                            loctype_name = "UHF";
                                        } else {
                                            loctype_name = "GPS";
                                        }
                                        textView_x.setVisibility(View.VISIBLE);
                                        textView_x.setText(loctype_name);

                                        if (LOC_STATUS_YES.equals(_v)) {
                                            // 已定位
                                            textView.setTextColor(getResources().getColor(R.color.text_green_dark));
                                            textView_x.setBackgroundColor(getResources().getColor(R.color.ade_dark_green));
                                        } else {
                                            // 未定位
                                            // 分为两种情况：1、有定位参数，但尚未定位；2、没有定位参数，需要先采集定位信息，再进行定位。
                                            textView.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                            if (CommonUtil.checkNB((String) info.get("loc"))) {
                                                // 有定位参数
                                                textView_x.setBackgroundColor(getResources().getColor(R.color.background_title_blue));
                                            } else {
                                                if (CommonUtil.checkNB(resLocMap.get((String) info.get("ids")))) {
                                                    // 资源采集定位表中有定位参数
                                                    textView_x.setBackgroundColor(getResources().getColor(R.color.background_title_blue));
                                                } else {
                                                    // 资源采集定位表中没有定位参数
                                                    textView_x.setBackgroundColor(getResources().getColor(R.color.background_grey_dark));
                                                }
                                            }
                                        }
                                    }
                                    textView.setText(_v);
                                    layout.setTag((String) info.get("ids"));
                                    if (CommonParam.TEST_MODE) {
                                        // 在测试模式下，长按单元格，如果该巡视点是RFID/UHF定位，且尚未定位，则选中该巡视点，并且模拟进行定位
                                        layout.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                // 当前行的巡视点编号
                                                String _id = (String) v.getTag();
                                                LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + _id);
                                                if (tableRowLayout != null) {
                                                    TextView textView_t1 = (TextView) tableRowLayout.findViewById(R.id.info_t1);
                                                    if (textView_t1 != null) {
                                                        HashMap<String, Object> _currentResInfo = null;
                                                        // 序号
                                                        String n = textView_t1.getText().toString();
                                                        // 索引
                                                        int index = -1;
                                                        try {
                                                            index = Integer.parseInt(n) - 1;
                                                        } catch (Exception e) {
                                                        }
                                                        if (index != -1) {
                                                            HashMap<String, Object> vMap = listItems.get(index);
                                                            if (vMap != null) {
                                                                _currentResInfo = (HashMap<String, Object>) vMap.get("info");
                                                            }
                                                        }
                                                        // 定位方式
                                                        String loctype = (String) _currentResInfo.get("loctype");
                                                        // 是否已定位
                                                        String _loc = (String) _currentResInfo.get("_loc");
                                                        checkTableRow(tableRowLayout, true);
                                                        if (LOC_TYPE_NONE.equals(loctype)) {
                                                            // 不需要定位
                                                            show("本巡视点无需定位！");
                                                        } else {
                                                            // 需要定位
                                                            if (CommonParam.YES.equals(_loc)) {
                                                                // 已定位
                                                                show("本巡视点已定位！");
                                                            } else {
                                                                // 未定位
                                                                // 定位参数
                                                                String loc = (String) _currentResInfo.get("loc");
                                                                if (!CommonUtil.checkNB(loc)) {
                                                                    loc = resLocMap.get((String) _currentResInfo.get("ids"));
                                                                }
                                                                if (LOC_TYPE_RFID.equals(loctype)) {
                                                                    // RFID定位
                                                                    if (CommonUtil.checkNB(loc)) {
                                                                        // 有定位参数
                                                                        readCard_ins(loc);
                                                                    } else {
                                                                        // 没有定位参数
                                                                        show("请先采集定位信息！");
                                                                    }
                                                                } else if (LOC_TYPE_UHF.equals(loctype)) {
                                                                    // UHF定位
                                                                    if (CommonUtil.checkNB(loc)) {
                                                                        // 有定位参数
                                                                        readUhfCard_ins(loc);
                                                                    } else {
                                                                        // 没有定位参数
                                                                        show("请先采集定位信息！");
                                                                    }
                                                                } else {
                                                                    // GPS定位
                                                                    if (CommonUtil.checkNB(loc)) {
                                                                        // 有定位参数
                                                                        if (setupLocationFunc()) {
                                                                            String[] locArray = loc.split(",");
                                                                            String lng_loc = locArray[0];
                                                                            String lat_loc = locArray[1];
                                                                            Double lng_loc_double = 0.0D;
                                                                            Double lat_loc_double = 0.0D;
                                                                            try {
                                                                                lng_loc_double = Double.parseDouble(lng_loc);
                                                                            } catch (Exception e) {
                                                                            }
                                                                            try {
                                                                                lat_loc_double = Double.parseDouble(lat_loc);
                                                                            } catch (Exception e) {
                                                                            }

                                                                            locationType = LOCATION_TYPE_LOC;
                                                                            location_ins(new LatLng(lat_loc_double, lng_loc_double));
                                                                        }
                                                                    } else {
                                                                        // 没有定位参数
                                                                        show("请先采集定位信息！");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                return true;
                                            }
                                        });
                                    }
                                } else if (layout.getId() == R.id.info_c4) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t4);
                                    textView.setText(Integer.toString((Integer) info.get("_dkNum")));
                                    layout.setTag((String) info.get("ids"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 当前行的巡视点编号
                                            String _id = (String) v.getTag();
                                            LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + _id);
                                            // 打卡信息数量
                                            Integer _dkNum = 0;
                                            HashMap<String, Object> _currentResInfo = null;
                                            if (tableRowLayout != null) {
                                                TextView textView_t1 = (TextView) tableRowLayout.findViewById(R.id.info_t1);
                                                if (textView_t1 != null) {
                                                    // 序号
                                                    String n = textView_t1.getText().toString();
                                                    // 索引
                                                    int index = -1;
                                                    try {
                                                        index = Integer.parseInt(n) - 1;
                                                    } catch (Exception e) {
                                                    }
                                                    if (index != -1) {
                                                        HashMap<String, Object> vMap = listItems.get(index);
                                                        if (vMap != null) {
                                                            _currentResInfo = (HashMap<String, Object>) vMap.get("info");
                                                            if (_currentResInfo != null) {
                                                                _dkNum = (Integer) _currentResInfo.get("_dkNum");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (_dkNum > 0) {
                                                // 创建信息传输Bundle
                                                Bundle data = new Bundle();
                                                data.putSerializable("bizInfo", bizInfo);
                                                data.putSerializable("resInfo", _currentResInfo);

                                                // 创建启动 Activity 的 Intent
                                                Intent intent = null;
                                                if (!baseApp.isReverseRotate) {
                                                    intent = new Intent(classThis, InsDkHistoryListLandActivity.class);
                                                } else {
                                                    intent = new Intent(classThis, InsDkHistoryListReverseLandActivity.class);
                                                }
                                                // 将数据存入 Intent 中
                                                intent.putExtras(data);
                                                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                                                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                            } else {
                                                show("该巡视点没有签到打卡记录！");
                                            }
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c5) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t5);
                                    textView.setText(Integer.toString((Integer) info.get("_jcNum")));
                                    layout.setTag((String) info.get("ids"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 当前行的巡视点编号
                                            String _id = (String) v.getTag();
                                            LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + _id);
                                            // 检查信息数量
                                            Integer _jcNum = 0;
                                            HashMap<String, Object> _currentResInfo = null;
                                            if (tableRowLayout != null) {
                                                TextView textView_t1 = (TextView) tableRowLayout.findViewById(R.id.info_t1);
                                                if (textView_t1 != null) {
                                                    // 序号
                                                    String n = textView_t1.getText().toString();
                                                    // 索引
                                                    int index = -1;
                                                    try {
                                                        index = Integer.parseInt(n) - 1;
                                                    } catch (Exception e) {
                                                    }
                                                    if (index != -1) {
                                                        HashMap<String, Object> vMap = listItems.get(index);
                                                        if (vMap != null) {
                                                            _currentResInfo = (HashMap<String, Object>) vMap.get("info");
                                                            if (_currentResInfo != null) {
                                                                _jcNum = (Integer) _currentResInfo.get("_jcNum");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (_jcNum > 0) {
                                                // 创建信息传输Bundle
                                                Bundle data = new Bundle();
                                                data.putSerializable("bizInfo", bizInfo);
                                                data.putSerializable("resInfo", _currentResInfo);

                                                // 创建启动 Activity 的 Intent
                                                Intent intent = null;
                                                if (!baseApp.isReverseRotate) {
                                                    intent = new Intent(classThis, InsJcHistoryListLandActivity.class);
                                                } else {
                                                    intent = new Intent(classThis, InsJcHistoryListReverseLandActivity.class);
                                                }
                                                // 将数据存入 Intent 中
                                                intent.putExtras(data);
                                                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                                                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                            } else {
                                                show("该巡视点没有现场检查记录！");
                                            }
                                        }
                                    });
                                    // 未完成的检查信息编号
                                    String unfinishJcId = unfinishJcMap.get((String) info.get("ids"));
                                    TextView textView_x = (TextView) layout.findViewById(R.id.info_t5x);
                                    if (CommonUtil.checkNB(unfinishJcId)) {
                                        textView_x.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_x.setVisibility(View.GONE);
                                    }
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                    infoList.setAdapter(infoListAdapter);
                    // 更新列表
                    infoListAdapter.notifyDataSetChanged();

//                    // 设置列表项点击事件
//                    infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            // 列表项中的对象
//                            HashMap<String, Object> vMap = (HashMap<String, Object>) parent
//                                    .getItemAtPosition(position);
//                            HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");
//
//                            show((String) info.get("ids"));
////                            // 创建信息传输Bundle
////                            Bundle data = new Bundle();
////                            data.putString("id", (String) info.get("id"));
////                            data.putString("fromFlag", "list");
////
////                            // 创建启动 Activity 的 Intent
////                            Intent intent = new Intent(InsListActivity.this, SgCategoryShowActivity.class);
////
////                            // 将数据存入 Intent 中
////                            intent.putExtras(data);
////                            startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
////                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
//                        }
//                    });
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
                if (!isConnecting) {
                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                }
            } else {
                show("信息错误！");
                goBack();
            }
        }
    }

    /**
     * 查询信息 AsyncTask 类
     */
    private class SearchTask extends AsyncTask<Object, Integer, String> {
        /**
         * 结果：数据库中找不到所有待巡视的巡视点信息
         */
        public static final String RESULT_INS_POINT_NOT_ENOUGH = "ins_point_less";
        /**
         * 新增的信息
         */
        ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();
        /**
         * 可巡视的巡视点数量
         */
        private int total;
        /**
         * 待巡视的巡视点数量
         */
        private int total_needToIns;
        /**
         * 已经巡视的巡视点数量
         * <p>这里指已有检查记录的巡视点数量</p>
         */
        private int donePointNum = 0;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            // makeWaitDialog();
            Log.d("bsearch##########", "#");
            isConnecting = true;
            table_checkBoxButtonList.clear();
            unfinishJcMap.clear();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            infoTool = getInfoTool();
            // 查询信息。开始====================================================================
            // 资源编号
            String res_id = (String) bizInfo.get("res_id");
            StringBuffer sb = new StringBuffer();
            String[] res_array = null;
            if (CommonUtil.checkNB(res_id)) {
                res_array = res_id.split(",");
                for (String _id : res_array) {
                    if (CommonUtil.checkNB(_id)) {
                        sb.append(",'" + _id + "'");
                    }
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(0);
                }
            }
            if (res_array == null) {
                res_array = new String[]{};
            }
            total_needToIns = res_array.length;

            // 水工资源
            ArrayList<HashMap<String, Object>> resList_tmp = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT * FROM t_szfgs_sgres model WHERE model.valid='1' and model.ids in ("
                                    + sb.toString() + ") ORDER BY model.pxbh ASC",
                            new String[]{});
            // 水工资源
            ArrayList<HashMap<String, Object>> resList = new ArrayList<HashMap<String, Object>>();
            HashMap<String, HashMap<String, Object>> resMap = new HashMap<String, HashMap<String, Object>>();
            for (HashMap<String, Object> o : resList_tmp) {
                resMap.put((String) o.get("ids"), o);
            }
            for (String rid : res_array) {
                HashMap<String, Object> o = resMap.get(rid);
                if (o != null) {
                    resList.add(o);
                }
            }
            total = resList.size();
            if (total_needToIns > total) {
                result = RESULT_INS_POINT_NOT_ENOUGH;
                return result;
            }

            // 水工巡视检查记录
            ArrayList<HashMap<String, Object>> jcList = infoTool
                    .getInfoMapList(
                            "SELECT model.ids, model.res_id, model.atime, model.btime FROM t_szfgs_sgxunsjcjl model WHERE model.valid='1' and model.biz_id=? and model.quid=? ORDER BY model.atime DESC",
                            new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            HashMap<String, Object> jcInfoMap = new HashMap<String, Object>();
            for (HashMap<String, Object> o : jcList) {
                String rid = (String) o.get("res_id");
                String btime = (String) o.get("btime");

                HashMap<String, Object> map = (HashMap<String, Object>) jcInfoMap.get(rid);
                if (map == null) {
                    map = new HashMap<String, Object>();
                    jcInfoMap.put(rid, map);
                }
                // 已完成数量
                Integer doneNum = (Integer) map.get("doneNum");
                if (doneNum == null) {
                    doneNum = 0;
                }
                // 未完成数量
                Integer unfinishNum = (Integer) map.get("unfinishNum");
                if (unfinishNum == null) {
                    unfinishNum = 0;
                }
                if (CommonUtil.checkNB(btime)) {
                    // 已完成
                    doneNum++;
                } else {
                    // 未完成
                    unfinishNum++;
                    // 未完成检查的编号保存在unfinishJcMap中
                    if (unfinishJcMap.get(rid) == null) {
                        unfinishJcMap.put(rid, (String) o.get("ids"));
                    }
                }

                map.put("doneNum", doneNum);
                map.put("unfinishNum", unfinishNum);
            }

            // 水工巡视签到记录
            Map<String, String> dkMap = infoTool
                    .getInfoKVMap(
                            "SELECT model.res_id, count(model.ids) FROM t_szfgs_sgxunsqdjl model WHERE model.valid='1' and model.biz_id=? and model.quid=? GROUP BY model.res_id",
                            new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            // 水工巡视定位记录
            Map<String, String> locMap = infoTool
                    .getInfoKVMap(
                            "SELECT model.res_id, model.loc FROM t_szfgs_sgxunsloc model WHERE model.valid='1' and model.biz_id=? and model.quid=?",
                            new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            // 水工资源采集定位记录
            resLocMap = infoTool
                    .getInfoKVMap(
                            "SELECT model.res_id, model.loc FROM t_szfgs_sgresloc model WHERE model.valid='1' and model.res_id in ("
                                    + sb.toString() + ")", new String[]{});

            for (int index = 0, len = resList.size(); index < len; index++) {
                // 存放信息的 Map
                HashMap<String, Object> listItem = new HashMap<String, Object>();
                HashMap<String, Object> info = resList.get(index);

                listItem.put("info", info);
                info.put("V_INFO_SN", index + 1);

                // 巡视点编号
                String id = CommonUtil.N2B((String) info.get("ids"));
                String fldh = (String) info.get("fldh");
                // 缩进空格数量
                int space_total = (fldh.length() - 6) / 34 - 1;
                info.put("_t", CommonUtil.insertCNSpace(space_total) + info.get("title"));

                // 检查信息数量
                int jcNum = 0;
                // 打卡记录数量
                int dkNum = 0;
                String dkNum_str = dkMap.get(id);

                HashMap<String, Object> jcMap = (HashMap<String, Object>) jcInfoMap.get(id);
                // 已完成数量
                Integer doneNum = 0;
                // 未完成数量
                Integer unfinishNum = 0;
                if (jcMap == null) {
                    jcNum = 0;
                } else {
                    // 已完成数量
                    doneNum = (Integer) jcMap.get("doneNum");
                    // 未完成数量
                    unfinishNum = (Integer) jcMap.get("unfinishNum");
                    if (doneNum == null) {
                        doneNum = 0;
                    }
                    if (unfinishNum == null) {
                        unfinishNum = 0;
                    }

                    jcNum = doneNum + unfinishNum;
                }
                if (doneNum > 0) {
                    donePointNum++;
                }

                if (CommonUtil.checkNB(dkNum_str)) {
                    try {
                        dkNum = Integer.parseInt(dkNum_str);
                    } catch (Exception e) {
                    }
                }
                info.put("_jcNum", jcNum);
                info.put("_dkNum", dkNum);

                // 是否已定位
                String loc = locMap.get(id);
                if (loc == null) {
                    loc = CommonParam.NO;
                }
                info.put("_loc", loc);

                listItems_tmp.add(listItem);
            }
            result = CommonParam.RESULT_SUCCESS;
            // 查询信息。结束====================================================================

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
                listItems.clear();
                listItems.addAll(listItems_tmp);
                infoListAdapter.notifyDataSetChanged();
                totalNumTv.setText("总数：" + listItems.size());
                currentNumTv.setText("已巡：" + donePointNum);
            } else {
                goBackBtn.setVisibility(View.VISIBLE);
                finishBtn.setVisibility(View.GONE);
                jcBtn.setVisibility(View.GONE);
                dkBtn.setVisibility(View.GONE);
                autoDkBtn.setVisibility(View.GONE);
                locBtn.setVisibility(View.GONE);
                locAddBtn.setVisibility(View.GONE);
                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    makeAlertDialog("任务中待检查的巡视点数量为" + total_needToIns + "，现在手机中只找到" + total + "个巡视点的信息，无法进行巡视。请退回到主界面，并重新下载任务（或同步基础信息）。");
                } else {
                    show("数据加载失败");
                }
            }
            isConnecting = false;
        }
    }

    /**
     * 点击CheckBox所在单元格时的事件
     *
     * @param tableRowColumn {@code LinearLayout} 单元格
     */
    public void onClickCheckBoxColumn(LinearLayout tableRowColumn) {
        CheckBox btn = (CheckBox) tableRowColumn.findViewById(R.id.info_r);
        String t = (String) btn.getTag();
        LinearLayout tableRowLayout_now = infoList.findViewWithTag("tableRowLayout_" + t);
        if (tableRowLayout_now != null) {
            if (btn.isChecked()) {
                // 点击复选框后的一些操作。开始=====================
                currentResId = null;
                currentResInfo = null;
                btn.setChecked(false);
                tableRowLayout_now.setBackgroundColor(getResources().getColor(R.color.solid_plain));

                jcBtn.setText(R.string.title_jc_new);
                Drawable jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_create_normal);
                jcBtn_icon.setBounds(0, 0, jcBtn_icon.getMinimumWidth(), jcBtn_icon.getMinimumHeight());
                jcBtn.setCompoundDrawables(jcBtn_icon, null, null, null);
                locAddBtn.setVisibility(View.GONE);
                // 点击复选框后的一些操作。结束=====================
            } else {
                // 点击复选框后的一些操作。开始=====================
                currentResId = t;

                TextView textView_t1 = (TextView) tableRowLayout_now.findViewById(R.id.info_t1);
                if (textView_t1 != null) {
                    // 序号
                    String n = textView_t1.getText().toString();
                    // 索引
                    int index = -1;
                    try {
                        index = Integer.parseInt(n) - 1;
                    } catch (Exception e) {
                    }
                    if (index != -1) {
                        HashMap<String, Object> vMap = listItems.get(index);
                        if (vMap != null) {
                            currentResInfo = (HashMap<String, Object>) vMap.get("info");
                        }
                    }
                }

                btn.setChecked(true);
                tableRowLayout_now.setBackgroundColor(getResources().getColor(R.color.list_color_content_background_even_blue_01));

                // 未完成的检查信息编号
                String unfinishJcId = unfinishJcMap.get(t);
                Drawable jcBtn_icon = null;
                if (!CommonUtil.checkNB(unfinishJcId)) {
                    // 已完成
                    jcBtn.setText(R.string.title_jc_new);
                    jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_create_normal);
                } else {
                    // 未完成
                    jcBtn.setText(R.string.title_jc_continue);
                    jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_edit_normal);
                }
                jcBtn_icon.setBounds(0, 0, jcBtn_icon.getMinimumWidth(), jcBtn_icon.getMinimumHeight());
                jcBtn.setCompoundDrawables(jcBtn_icon, null, null, null);

                // 定位方式
                String loctype = (String) currentResInfo.get("loctype");
                if (!LOC_TYPE_NONE.equals(loctype)) {
                    if (CommonUtil.checkNB((String) currentResInfo.get("loc"))) {
                        // 有定位参数
                        locAddBtn.setVisibility(View.GONE);
                    } else {
                        // 没有定位参数
                        locAddBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    locAddBtn.setVisibility(View.GONE);
                }
                // 点击复选框后的一些操作。结束=====================
                for (CheckBox r : table_checkBoxButtonList) {
                    String t_tmp = (String) r.getTag();
                    if (!t.equals(t_tmp)) {
                        if (r.isChecked()) {
                            r.setChecked(false);
                            LinearLayout tableRowLayout_tmp = infoList.findViewWithTag("tableRowLayout_" + t_tmp);
                            if (tableRowLayout_tmp != null) {
                                tableRowLayout_tmp.setBackgroundColor(getResources().getColor(R.color.solid_plain));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 选中某行的事件
     *
     * @param tableRowLayout {@code LinearLayout} 行
     * @param checkFlag      {@code boolean} 是否选中
     */
    public void checkTableRow(LinearLayout tableRowLayout, boolean checkFlag) {
        CheckBox btn = (CheckBox) tableRowLayout.findViewById(R.id.info_r);
        String t = (String) btn.getTag();
        LinearLayout tableRowLayout_now = tableRowLayout;
        if (tableRowLayout_now != null) {
            if (btn.isChecked() != checkFlag) {
                // 只有当行的选中状态不同于isChecked参数时，才需要进行改变
                if (!checkFlag) {
                    // 点击复选框后的一些操作。开始=====================
                    currentResId = null;
                    currentResInfo = null;
                    btn.setChecked(false);
                    tableRowLayout_now.setBackgroundColor(getResources().getColor(R.color.solid_plain));

                    jcBtn.setText(R.string.title_jc_new);
                    Drawable jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_create_normal);
                    jcBtn_icon.setBounds(0, 0, jcBtn_icon.getMinimumWidth(), jcBtn_icon.getMinimumHeight());
                    jcBtn.setCompoundDrawables(jcBtn_icon, null, null, null);
                    locAddBtn.setVisibility(View.GONE);
                    // 点击复选框后的一些操作。结束=====================
                } else {
                    // 点击复选框后的一些操作。开始=====================
                    currentResId = t;

                    TextView textView_t1 = (TextView) tableRowLayout_now.findViewById(R.id.info_t1);
                    if (textView_t1 != null) {
                        // 序号
                        String n = textView_t1.getText().toString();
                        // 索引
                        int index = -1;
                        try {
                            index = Integer.parseInt(n) - 1;
                        } catch (Exception e) {
                        }
                        if (index != -1) {
                            HashMap<String, Object> vMap = listItems.get(index);
                            if (vMap != null) {
                                currentResInfo = (HashMap<String, Object>) vMap.get("info");
                            }
                        }
                    }

                    btn.setChecked(true);
                    tableRowLayout_now.setBackgroundColor(getResources().getColor(R.color.list_color_content_background_even_blue_01));

                    // 未完成的检查信息编号
                    String unfinishJcId = unfinishJcMap.get(t);
                    Drawable jcBtn_icon = null;
                    if (!CommonUtil.checkNB(unfinishJcId)) {
                        // 已完成
                        jcBtn.setText(R.string.title_jc_new);
                        jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_create_normal);
                    } else {
                        // 未完成
                        jcBtn.setText(R.string.title_jc_continue);
                        jcBtn_icon = getResources().getDrawable(R.drawable.table_icon_edit_normal);
                    }
                    jcBtn_icon.setBounds(0, 0, jcBtn_icon.getMinimumWidth(), jcBtn_icon.getMinimumHeight());
                    jcBtn.setCompoundDrawables(jcBtn_icon, null, null, null);

                    // 定位方式
                    String loctype = (String) currentResInfo.get("loctype");
                    if (!LOC_TYPE_NONE.equals(loctype)) {
                        if (CommonUtil.checkNB((String) currentResInfo.get("loc"))) {
                            // 有定位参数
                            locAddBtn.setVisibility(View.GONE);
                        } else {
                            // 没有定位参数
                            locAddBtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                        locAddBtn.setVisibility(View.GONE);
                    }
                    // 点击复选框后的一些操作。结束=====================
                    for (CheckBox r : table_checkBoxButtonList) {
                        String t_tmp = (String) r.getTag();
                        if (!t.equals(t_tmp)) {
                            if (r.isChecked()) {
                                r.setChecked(false);
                                LinearLayout tableRowLayout_tmp = infoList.findViewWithTag("tableRowLayout_" + t_tmp);
                                if (tableRowLayout_tmp != null) {
                                    tableRowLayout_tmp.setBackgroundColor(getResources().getColor(R.color.solid_plain));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查状态：定位
     *
     * @return {@code boolean} 状态是否有效
     */
    public boolean checkStatus_loc() {
        return checkStatus_loc(true);
    }

    /**
     * 检查状态：定位
     *
     * @param needAlert {@code boolean} 是否需要发出提示信息
     * @return {@code boolean} 状态是否有效
     */
    public boolean checkStatus_loc(boolean needAlert) {
        boolean submitFlag = false;
        String errorMsg = "";

        if (!CommonUtil.checkNB(currentResId)) {
            errorMsg = "请选择巡视点！";
        } else {
            submitFlag = true;
        }

        if (!submitFlag) {
            // 不能提交
            if (CommonUtil.checkNB(errorMsg)) {
                show(errorMsg);
            }
        } else {

        }

        return submitFlag;
    }

    /**
     * 准备定位
     */
    public void prepareToLoc() {
        if (currentResInfo == null) {
            show("请选择巡视点！");
            return;
        }

        // 定位方式
        String loctype = (String) currentResInfo.get("loctype");
        // 是否已定位
        String _loc = (String) currentResInfo.get("_loc");

        if (LOC_TYPE_NONE.equals(loctype)) {
            // 不需要定位
            show("本巡视点无需定位！");
        } else {
            // 需要定位
            if (CommonParam.YES.equals(_loc)) {
                // 已定位
                show("本巡视点已定位！");
            } else {
                // 未定位
                // 定位参数
                String loc = (String) currentResInfo.get("loc");
                if (!CommonUtil.checkNB(loc)) {
                    loc = resLocMap.get((String) currentResInfo.get("ids"));
                }
                if (LOC_TYPE_RFID.equals(loctype)) {
                    // RFID定位
                    if (CommonUtil.checkNB(loc)) {
                        // 有定位参数
                        scanCardType = SCAN_CARD_TYPE_LOC;
                        makeReadCardDialog();
                    } else {
                        // 没有定位参数
                        show("请先采集定位信息！");
                    }
                } else if (LOC_TYPE_UHF.equals(loctype)) {
                    // UHF定位
                    if (!baseApp.isUhfPda) {
                        makeAlertDialog("您的设备不支持UHF功能，无法定位！");
                        return;
                    }
                    if (CommonUtil.checkNB(loc)) {
                        // 有定位参数
                        scanUhfCardType = SCAN_UHF_CARD_TYPE_LOC;
                        makeReadUhfCardDialog();
                    } else {
                        // 没有定位参数
                        show("请先采集定位信息！");
                    }
                } else if (LOC_TYPE_GPS.equals(loctype)) {
                    // GPS定位
                    if (CommonUtil.checkNB((String) currentResInfo.get("loc")) || CommonUtil.checkNB(resLocMap.get((String) currentResInfo.get("ids")))) {
                        // 有定位参数
                        if (setupLocationFunc()) {
                            locationType = LOCATION_TYPE_LOC;
                            new GetLocationTask().execute();
                        }
                    } else {
                        // 没有定位参数
                        show("请先采集定位信息！");
                    }
                }
            }
        }
    }

    /**
     * 准备采集
     */
    public void prepareToLocAdd() {
        if (currentResInfo == null) {
            show("请选择巡视点！");
            return;
        }

        // 定位方式
        String loctype = (String) currentResInfo.get("loctype");
        // 是否已定位
        String _loc = (String) currentResInfo.get("_loc");

        if (LOC_TYPE_NONE.equals(loctype)) {
            // 不需要定位
            show("本巡视点无需定位！");
        } else {
            // 需要定位
            if (CommonParam.YES.equals(_loc)) {
                // 已定位
                show("本巡视点已定位，不可再次采集定位信息！", Toast.LENGTH_LONG);
            } else {
                // 未定位
                if (LOC_TYPE_RFID.equals(loctype)) {
                    // RFID定位
                    scanCardType = SCAN_CARD_TYPE_ADD;
                    makeReadCardDialog();
                } else if (LOC_TYPE_UHF.equals(loctype)) {
                    // UHF定位
                    if (!baseApp.isUhfPda) {
                        makeAlertDialog("您的设备不支持UHF功能，无法采集！");
                        return;
                    }
                    scanUhfCardType = SCAN_UHF_CARD_TYPE_ADD;
                    makeReadUhfCardDialog();
                } else if (LOC_TYPE_GPS.equals(loctype)) {
                    // GPS定位
                    if (setupLocationFunc()) {
                        locationType = LOCATION_TYPE_ADD;
                        new GetLocationTask().execute();
                    }
                }
            }
        }
    }

    /**
     * 准备打卡
     */
    public void prepareToDk() {
        if (currentResInfo == null) {
            show("请选择巡视点！");
            dkBtn.setClickable(true);
            dkBtn.setEnabled(true);
            return;
        }

        // 定位方式
        String loctype = (String) currentResInfo.get("loctype");
        // 是否已定位
        String _loc = (String) currentResInfo.get("_loc");

        if (LOC_TYPE_NONE.equals(loctype)) {
            // 不需要定位
            new PrepareToDkTask().execute();
        } else {
            // 需要定位
            if (CommonParam.YES.equals(_loc)) {
                // 已定位
//                if (!baseApp.isUhfPda) {
//                    makeAlertDialog("您的设备不支持UHF功能，无法打卡！");
//                    return;
//                }
                new PrepareToDkTask().execute();
            } else {
                // 未定位
                show(R.string.alert_inspect_dk_need_loc);
                dkBtn.setClickable(true);
                dkBtn.setEnabled(true);
            }
        }
    }

    /**
     * 准备打卡
     */
    public void prepareToAutoDk() {
        if (currentResInfo == null) {
            show("请选择巡视点！");
            autoDkBtn.setClickable(true);
            autoDkBtn.setEnabled(true);
            return;
        }

        // 定位方式
        String loctype = (String) currentResInfo.get("loctype");
        // 是否已定位
        String _loc = (String) currentResInfo.get("_loc");

        if (LOC_TYPE_NONE.equals(loctype)) {
            // 不需要定位
            new PrepareToAutoDkTask().execute();
        } else {
            // 需要定位
            if (CommonParam.YES.equals(_loc)) {
                // 已定位
                if (!baseApp.isUhfPda) {
                    makeAlertDialog("您的设备不支持UHF功能，无法扫卡快签！");
                    return;
                }
                new PrepareToAutoDkTask().execute();
            } else {
                // 未定位
                show(R.string.alert_inspect_autodk_need_loc);
                autoDkBtn.setClickable(true);
                autoDkBtn.setEnabled(true);
            }
        }
    }

    /**
     * 准备检查
     */
    public void prepareToJc() {
        if (currentResInfo == null) {
            show("请选择巡视点！");
            jcBtn.setClickable(true);
            jcBtn.setEnabled(true);
            return;
        }

        // 定位方式
        String loctype = (String) currentResInfo.get("loctype");
        // 是否已定位
        String _loc = (String) currentResInfo.get("_loc");

        if (LOC_TYPE_NONE.equals(loctype)) {
            // 不需要定位
            new PrepareToJcTask().execute();
        } else {
            // 需要定位
            if (CommonParam.YES.equals(_loc)) {
                // 已定位
                new PrepareToJcTask().execute();
            } else {
                // 未定位
                show(R.string.alert_inspect_jc_need_loc);
                jcBtn.setClickable(true);
                jcBtn.setEnabled(true);
            }
        }
    }

    /**
     * 准备现场检查数据的 AsyncTask 类
     */
    private class PrepareToJcTask extends AsyncTask<Object, Integer, String> {
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

            result = CommonParam.RESULT_SUCCESS;

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

            jcBtn.setClickable(true);
            jcBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("bizInfo", bizInfo);
                data.putSerializable("resInfo", currentResInfo);
                // 未完成的检查信息编号
                data.putString("unfinishJcId", CommonUtil.N2B(unfinishJcMap.get(currentResId)));

                // 创建启动 Activity 的 Intent
                Intent intent = null;
                if (!baseApp.isReverseRotate) {
                    intent = new Intent(classThis, InsJcListLandActivity.class);
                } else {
                    intent = new Intent(classThis, InsJcListReverseLandActivity.class);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            } else {

            }
        }
    }

    /**
     * 准备签到打卡数据的 AsyncTask 类
     */
    private class PrepareToDkTask extends AsyncTask<Object, Integer, String> {
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

            result = CommonParam.RESULT_SUCCESS;

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

            dkBtn.setClickable(true);
            dkBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("bizInfo", bizInfo);
                data.putSerializable("resInfo", currentResInfo);

                // 创建启动 Activity 的 Intent
                Intent intent = null;
                if (!baseApp.isReverseRotate) {
                    intent = new Intent(classThis, InsDkEditLandActivity.class);
                } else {
                    intent = new Intent(classThis, InsDkEditReverseLandActivity.class);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            } else {

            }
        }
    }

    /**
     * 准备扫卡快签数据的 AsyncTask 类
     */
    private class PrepareToAutoDkTask extends AsyncTask<Object, Integer, String> {
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

            // 处理数据。开始============================================================================

            // 处理数据。结束============================================================================

            result = CommonParam.RESULT_SUCCESS;

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
            // unWait();

            autoDkBtn.setClickable(true);
            autoDkBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                // 创建信息传输Bundle
                if (needShowRotatePortraitAlertFlag) {
                    makeShowRotateAlertDialog();
                } else {
                    // 创建启动 Activity 的 Intent
                    Intent intent = new Intent(classThis, InsAutoDkListActivity.class);
                    // 信息传输Bundle
                    Bundle data = new Bundle();
                    data.putSerializable("bizInfo", bizInfo);
                    data.putSerializable("resInfo", currentResInfo);
                    // 将数据存入Intent中
                    intent.putExtras(data);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                }
            } else {

            }
        }
    }

    /**
     * 显示读卡对话框
     */
    public void makeReadCardDialog() {
        Builder dlgBuilder = new Builder(this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_read_ins_card, null);
        dlgBuilder.setView(layout);
        if (scanCardType.equals(SCAN_CARD_TYPE_ADD)) {
            // 采集
            dlgBuilder.setTitle(getString(R.string.text_scan_card_data));
        } else {
            dlgBuilder.setTitle(getString(R.string.text_scan_card));
        }
        dlgBuilder.setIcon(R.drawable.ic_dialog_nfc_v);
        dlgBuilder.setCancelable(true);

        readCardType = CommonParam.READ_CARD_TYPE_INSPECT;
        readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_NO_ACTION;

        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        readCardDlg = dlgBuilder.create();
        readCardDlg.show();

        ImageView animate_iv = (ImageView) readCardDlg.findViewById(R.id.animate_iv);
        AnimationDrawable animate_ad = (AnimationDrawable) animate_iv.getBackground();
        animate_ad.start();

        // 取消按钮
        Button cancelBtn = readCardDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                readCardDlg.cancel();
            }
        });
        readCardDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
                readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_NO_ACTION;
                pageHandler.sendEmptyMessageDelayed(20, 1000);
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

        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setNeutralButton("功率：" + (int) baseApp.uhfPdaInfo.get("power"), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        readUhfCardDlg = dlgBuilder.create();
        readUhfCardDlg.show();

        setUhfScanFieldStart();

        // 功率按钮
        Button powerBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEUTRAL);
        // 取消按钮
        Button cancelBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        powerBtn.setTextColor(getResources().getColor(R.color.text_blue));
        powerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopScanUhf();
                makeSetUhfPowerDialog();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                readUhfCardDlg.cancel();
            }
        });

        readUhfCardDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                stopScanUhf();
                if (Reader.rrlib.IsConnected()) {
                    disconnectUhf(false);
                }
                readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
                readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_NO_ACTION;
                pageHandler.sendEmptyMessageDelayed(30, 1000);
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
                    if (baseApp.isUhfPda) {
                        if (readUhfCardType == CommonParam.READ_UHF_CARD_TYPE_INSPECT) {
                            // 读巡视卡
                            if (isUhfScaning) {
                                stopScanUhf();
                            } else {
                                startScanUhf();
                            }
                            flag = true;
                        }
                    }
                }
                return flag;
            }
        });
    }

    /**
     * 读卡：巡视_之前
     *
     * @param cardMac {@code String} 巡视点RFID卡的MAC
     */
    @Override
    public void readCard_ins_onBefore(String cardMac) {
        new CheckCardTask().execute(cardMac);
    }

    /**
     * 读卡：巡视
     *
     * @param cardMac {@code String} 巡视点RFID卡的MAC
     */
    @Override
    public void readCard_ins(String cardMac) {
        if (SCAN_CARD_TYPE_LOC.equals(scanCardType)) {
            // 定位
            // 定位参数
            String loc = (String) currentResInfo.get("loc");
            if (!CommonUtil.checkNB(loc)) {
                // 资源信息中没有定位参数，就要使用水工巡视定位记录（t_szfgs_sgxunsloc）中的定位参数
                loc = resLocMap.get((String) currentResInfo.get("ids"));
            }
            if (cardMac.equals(loc)) {
                // 卡编号有效
                if (readCardDlg != null && readCardDlg.isShowing()) {
                    readCardDlg.cancel();
                }

                infoTool = getInfoTool();
                // 删除旧信息
                infoTool.delete("t_szfgs_sgxunsloc", "biz_id=? and res_id=? and quid=?", new String[]{(String) bizInfo.get("ids"), currentResId, (String) baseApp.getLoginUser().get("ids")});
                // 保存信息。开始=======================================
                // 键值对
                ContentValues cv = new ContentValues();
                cv.put("ids", CommonUtil.getUUID());
                cv.put("ctime", CommonUtil.getDT());
                cv.put("biz_id", (String) bizInfo.get("ids"));
                cv.put("res_id", currentResId);
                cv.put("loc", CommonParam.YES);
                cv.put("x1", "");
                cv.put("x2", "");
                cv.put("x3", "");
                cv.put("quid", (String) baseApp.getLoginUser().get("ids"));
                cv.put("valid", "1");

                // ★☆
                long insResult = infoTool.insert("t_szfgs_sgxunsloc", cv);
                // 保存信息。结束=======================================
                if (insResult > -1L) {
                    if (baseApp.isAutoPlayInsAudio) {
                        playVoice("di.wav");
                    }
                    show(R.string.alert_inspect_loc_rfid_success);
                    currentResInfo.put("_loc", CommonParam.YES);

                    // 设置定位状态
                    LinearLayout tableRowLayout_now = infoList.findViewWithTag("tableRowLayout_" + currentResId);
                    if (tableRowLayout_now != null) {
                        TextView textView_t3 = (TextView) tableRowLayout_now.findViewById(R.id.info_t3);
                        TextView textView_t3_x = (TextView) tableRowLayout_now.findViewById(R.id.info_t3x);
                        if (textView_t3 != null) {
                            textView_t3.setText(LOC_STATUS_YES);
                            textView_t3.setTextColor(getResources().getColor(R.color.text_green_dark));
                            textView_t3_x.setBackgroundColor(getResources().getColor(R.color.ade_dark_green));
                        }
                    }

                    updateFlag = true;
                    new UpdateStartTimeTask().execute();
                } else {
                    if (baseApp.isAutoPlayInsAudio) {
                        playVoice("nocard.wav");
                    }
                    show(R.string.alert_inspect_loc_save_fail);
                }
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("nocard.wav");
                }
                show(R.string.alert_inspect_card_incorrect);
            }
        } else {
            // 采集
            if (readCardDlg != null && readCardDlg.isShowing()) {
                readCardDlg.cancel();
            }

            resLocMap.put((String) currentResInfo.get("ids"), cardMac);

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

            infoTool = getInfoTool();
            // 删除旧信息
            infoTool.delete("t_szfgs_sgresloc", "res_id=?", new String[]{currentResId});
            // 保存信息。开始=======================================
            // 键值对
            ContentValues cv = new ContentValues();
            cv.put("ids", CommonUtil.getUUID());
            cv.put("ctime", CommonUtil.getDT());
            cv.put("uid", jlr_id);
            cv.put("uname", jlr_name);
            cv.put("res_id", currentResId);
            cv.put("loctype", (String) currentResInfo.get("loctype"));
            cv.put("loc", cardMac);
            cv.put("valid", "1");

            // ★☆
            long insResult = infoTool.insert("t_szfgs_sgresloc", cv);
            // 保存信息。结束=======================================
            if (insResult > -1L) {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("di.wav");
                }
                show(R.string.alert_inspect_loc_add_rfid_success);

                infoListAdapter.notifyDataSetChanged();

                updateFlag = true;
                new UpdateStartTimeTask().execute();
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("nocard.wav");
                }
                show(R.string.alert_inspect_loc_save_fail);
            }
        }
        scanCardType = SCAN_CARD_TYPE_LOC;
    }

    /**
     * 读UHF卡：巡视
     *
     * @param cardMac {@code String} 巡视点UHF卡的MAC
     */
    public void readUhfCard_ins(String cardMac) {
        if (SCAN_UHF_CARD_TYPE_LOC.equals(scanUhfCardType)) {
            // 定位
            // 定位参数
            String loc = (String) currentResInfo.get("loc");
            if (!CommonUtil.checkNB(loc)) {
                // 资源信息中没有定位参数，就要使用水工巡视定位记录（t_szfgs_sgxunsloc）中的定位参数
                loc = resLocMap.get((String) currentResInfo.get("ids"));
            }
            if (cardMac.equals(loc)) {
                // 卡编号有效
                if (readUhfCardDlg != null && readUhfCardDlg.isShowing()) {
                    readUhfCardDlg.cancel();
                }

                infoTool = getInfoTool();
                // 删除旧信息
                infoTool.delete("t_szfgs_sgxunsloc", "biz_id=? and res_id=? and quid=?", new String[]{(String) bizInfo.get("ids"), currentResId, (String) baseApp.getLoginUser().get("ids")});
                // 保存信息。开始=======================================
                // 键值对
                ContentValues cv = new ContentValues();
                cv.put("ids", CommonUtil.getUUID());
                cv.put("ctime", CommonUtil.getDT());
                cv.put("biz_id", (String) bizInfo.get("ids"));
                cv.put("res_id", currentResId);
                cv.put("loc", CommonParam.YES);
                cv.put("x1", "");
                cv.put("x2", "");
                cv.put("x3", "");
                cv.put("quid", (String) baseApp.getLoginUser().get("ids"));
                cv.put("valid", "1");

                // ★☆
                long insResult = infoTool.insert("t_szfgs_sgxunsloc", cv);
                // 保存信息。结束=======================================
                if (insResult > -1L) {
                    if (baseApp.isAutoPlayInsAudio) {
                        playVoice("di.wav");
                    }
                    show(R.string.alert_inspect_loc_uhf_success);
                    currentResInfo.put("_loc", CommonParam.YES);

                    // 设置定位状态
                    LinearLayout tableRowLayout_now = infoList.findViewWithTag("tableRowLayout_" + currentResId);
                    if (tableRowLayout_now != null) {
                        TextView textView_t3 = (TextView) tableRowLayout_now.findViewById(R.id.info_t3);
                        TextView textView_t3_x = (TextView) tableRowLayout_now.findViewById(R.id.info_t3x);
                        if (textView_t3 != null) {
                            textView_t3.setText(LOC_STATUS_YES);
                            textView_t3.setTextColor(getResources().getColor(R.color.text_green_dark));
                            textView_t3_x.setBackgroundColor(getResources().getColor(R.color.ade_dark_green));
                        }
                    }

                    updateFlag = true;
                    new UpdateStartTimeTask().execute();
                } else {
                    if (baseApp.isAutoPlayInsAudio) {
                        playVoice("nocard.wav");
                    }
                    show(R.string.alert_inspect_loc_save_fail);
                }
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("nocard.wav");
                }
                show(R.string.alert_inspect_card_incorrect);
            }
        } else {
            // 采集
            if (readUhfCardDlg != null && readUhfCardDlg.isShowing()) {
                readUhfCardDlg.cancel();
            }

            resLocMap.put((String) currentResInfo.get("ids"), cardMac);

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

            infoTool = getInfoTool();
            // 删除旧信息
            infoTool.delete("t_szfgs_sgresloc", "res_id=?", new String[]{currentResId});
            // 保存信息。开始=======================================
            // 键值对
            ContentValues cv = new ContentValues();
            cv.put("ids", CommonUtil.getUUID());
            cv.put("ctime", CommonUtil.getDT());
            cv.put("uid", jlr_id);
            cv.put("uname", jlr_name);
            cv.put("res_id", currentResId);
            cv.put("loctype", (String) currentResInfo.get("loctype"));
            cv.put("loc", cardMac);
            cv.put("valid", "1");

            // ★☆
            long insResult = infoTool.insert("t_szfgs_sgresloc", cv);
            // 保存信息。结束=======================================
            if (insResult > -1L) {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("di.wav");
                }
                show(R.string.alert_inspect_loc_add_uhf_success);

                infoListAdapter.notifyDataSetChanged();

                updateFlag = true;
                new UpdateStartTimeTask().execute();
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("nocard.wav");
                }
                show(R.string.alert_inspect_loc_save_fail);
            }
        }
        scanUhfCardType = SCAN_UHF_CARD_TYPE_LOC;
    }

    /**
     * 定位：巡视
     */
    public void location_ins() {
        location_ins(null);
    }

    /**
     * 定位：巡视
     *
     * @param latLng {@code LatLng} 用户当前坐标
     */
    public void location_ins(LatLng latLng) {
        if (latLng == null) {
            latLng = new LatLng(latitude_baidu, longitude_baidu);
        }
        if (LOCATION_TYPE_LOC.equals(locationType)) {
            // 定位
            // 定位参数
            String loc = (String) currentResInfo.get("loc");
            if (!CommonUtil.checkNB(loc)) {
                // 资源信息中没有定位参数，就要使用水工巡视定位记录（t_szfgs_sgxunsloc）中的定位参数
                loc = resLocMap.get((String) currentResInfo.get("ids"));
            }
            String[] locArray = loc.split(",");
            String lng_loc = locArray[0];
            String lat_loc = locArray[1];
            Double lng_loc_double = 0.0D;
            Double lat_loc_double = 0.0D;
            try {
                lng_loc_double = Double.parseDouble(lng_loc);
            } catch (Exception e) {
            }
            try {
                lat_loc_double = Double.parseDouble(lat_loc);
            } catch (Exception e) {
            }
            // 当前位置与预设定位点之间的距离
            double distance = DistanceUtil.getDistance(new LatLng(lat_loc_double, lng_loc_double), latLng);
            if (distance <= CommonParam.SYSCONFIG_VALUE_INS_DISTANCE) {
                // 在有效距离之内
                infoTool = getInfoTool();
                // 删除旧信息
                infoTool.delete("t_szfgs_sgxunsloc", "biz_id=? and res_id=? and quid=?", new String[]{(String) bizInfo.get("ids"), currentResId, (String) baseApp.getLoginUser().get("ids")});
                // 保存信息。开始=======================================
                // 键值对
                ContentValues cv = new ContentValues();
                cv.put("ids", CommonUtil.getUUID());
                cv.put("ctime", CommonUtil.getDT());
                cv.put("biz_id", (String) bizInfo.get("ids"));
                cv.put("res_id", currentResId);
                cv.put("loc", CommonParam.YES);
                cv.put("x1", "");
                cv.put("x2", "");
                cv.put("x3", "");
                cv.put("quid", (String) baseApp.getLoginUser().get("ids"));
                cv.put("valid", "1");

                // ★☆
                long insResult = infoTool.insert("t_szfgs_sgxunsloc", cv);
                // 保存信息。结束=======================================
                if (insResult > -1L) {
                    if (baseApp.isAutoPlayInsAudio) {
                        playVoice("di.wav");
                    }
                    show(R.string.alert_inspect_loc_gps_success);
                    currentResInfo.put("_loc", CommonParam.YES);

                    // 设置定位状态
                    LinearLayout tableRowLayout_now = infoList.findViewWithTag("tableRowLayout_" + currentResId);
                    if (tableRowLayout_now != null) {
                        TextView textView_t3 = (TextView) tableRowLayout_now.findViewById(R.id.info_t3);
                        TextView textView_t3_x = (TextView) tableRowLayout_now.findViewById(R.id.info_t3x);
                        if (textView_t3 != null) {
                            textView_t3.setText(LOC_STATUS_YES);
                            textView_t3.setTextColor(getResources().getColor(R.color.text_green_dark));
                            textView_t3_x.setBackgroundColor(getResources().getColor(R.color.ade_dark_green));
                        }
                    }

                    updateFlag = true;
                    new UpdateStartTimeTask().execute();
                } else {
                    if (baseApp.isAutoPlayInsAudio) {
                        playVoice("nocard.wav");
                    }
                    show(R.string.alert_inspect_loc_save_fail);
                }
            } else {
                // 超出有效距离
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("nocard.wav");
                }
                makeAlertDialog(String.format("您当前所在位置与巡视点预设的GPS位置相差过大，超过%s米。请尝试重新定位，或者靠近巡视点后再定位。", CommonParam.SYSCONFIG_VALUE_INS_DISTANCE));
            }
        } else {
            // 采集
            String location_str = latLng.longitude + "," + latLng.latitude;
            resLocMap.put((String) currentResInfo.get("ids"), location_str);

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

            infoTool = getInfoTool();
            // 删除旧信息
            infoTool.delete("t_szfgs_sgresloc", "res_id=?", new String[]{currentResId});
            // 保存信息。开始=======================================
            // 键值对
            ContentValues cv = new ContentValues();
            cv.put("ids", CommonUtil.getUUID());
            cv.put("ctime", CommonUtil.getDT());
            cv.put("uid", jlr_id);
            cv.put("uname", jlr_name);
            cv.put("res_id", currentResId);
            cv.put("loctype", (String) currentResInfo.get("loctype"));
            cv.put("loc", location_str);
            cv.put("valid", "1");

            // ★☆
            long insResult = infoTool.insert("t_szfgs_sgresloc", cv);
            // 保存信息。结束=======================================
            if (insResult > -1L) {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("di.wav");
                }
                show(R.string.alert_inspect_loc_add_gps_success);

                infoListAdapter.notifyDataSetChanged();

                updateFlag = true;
                new UpdateStartTimeTask().execute();
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("nocard.wav");
                }
                show(R.string.alert_inspect_loc_save_fail);
            }
        }
        locationType = LOCATION_TYPE_LOC;
    }

    /**
     * 显示获取位置对话框
     */
    public void makeGetLocationDialog() {
        Builder dlgBuilder = new Builder(this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_locate, null);
        dlgBuilder.setView(layout);
        if (locationType.equals(LOCATION_TYPE_ADD)) {
            // 采集
            dlgBuilder.setTitle(getString(R.string.text_get_location_data));
        } else {
            dlgBuilder.setTitle(getString(R.string.text_get_location));
        }
        dlgBuilder.setIcon(R.drawable.ic_dialog_place_v);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        getLocationDlg = dlgBuilder.create();
        getLocationDlg.show();

        ImageView animate_iv = (ImageView) getLocationDlg.findViewById(R.id.animate_iv);
        AnimationDrawable animate_ad = (AnimationDrawable) animate_iv.getBackground();
        animate_ad.start();

        // 取消按钮
        Button cancelBtn = getLocationDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getLocationDlg.cancel();
            }
        });
        getLocationDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                locationType = LOCATION_TYPE_LOC;
            }
        });
    }

    /**
     * 获取位置 AsyncTask 类
     */
    private class GetLocationTask extends AsyncTask<Object, Integer, String> {
        /**
         * 用户当前坐标
         */
        private LatLng latLng = null;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            if (getLocationDlg == null || !getLocationDlg.isShowing()) {
                makeGetLocationDialog();
            }
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            if (params.length > 0) {
                latLng = (LatLng) params[0];
            }
            // 处理数据。开始============================================================================
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            location_ins(latLng);

            pageHandler.sendEmptyMessage(10);
        }
    }

    /**
     * 更新实际开始时间 AsyncTask 类
     */
    private class UpdateStartTimeTask extends AsyncTask<Object, Integer, String> {
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

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            String realatime = (String) bizInfo.get("realatime");
            if (!CommonUtil.checkNB(realatime)) {
                // 已经没有设置开始时间，就需要设置
                realatime = CommonUtil.getDT();
                ContentValues cv = new ContentValues();
                cv.put("realatime", realatime);

                infoTool.update("t_biz_sgxuns", cv, "ids=? and quid=?", new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
                bizInfo.put("realatime", realatime);
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
            infoStatusTv.setText("未完成");
            infoStatusTv.setTextColor(getResources().getColor(R.color.text_purple));
        }
    }

    /**
     * 检查是否可以结束巡视 AsyncTask 类
     */
    private class CheckFinishTask extends AsyncTask<Object, Integer, String> {
        /**
         * 记录总数
         */
        private int total = 0;
        /**
         * 是否可以结束
         */
        boolean finishFlag = false;
        /**
         * 错误信息
         */
        String errorMsg = "";

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
            infoTool = getInfoTool();

            // 现场检查记录
            int jcNum = 0;
            // 打卡记录
            int dkNum = 0;

            jcNum = infoTool.getCount("select count(model.ids) from t_szfgs_sgxunsjcjl model where model.biz_id=? and model.quid=?",
                    new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            dkNum = infoTool.getCount("select count(model.ids) from t_szfgs_sgxunsqdjl model where model.biz_id=? and model.quid=?",
                    new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            total = jcNum + dkNum;


            if (unfinishJcMap.size() > 0) {
                errorMsg = "有未完成的现场检查记录！";
            } else if (total == 0) {
                errorMsg = "您还没有进行巡视，无法结束！";
            } else {
                finishFlag = true;
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

            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (!finishFlag) {
                    // 不能提交
                    if (CommonUtil.checkNB(errorMsg)) {
                        show(errorMsg);
                    } else {
                        show("信息错误，无法结束！");
                    }
                } else {
                    makeFinishInsDialog();
                }
            } else {

            }
        }
    }

    /**
     * 显示巡检完成对话框
     */
    public void makeFinishInsDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_ins_finish, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        dlgBuilder.setTitle(getString(R.string.alert_inspect_finish_ins_title));
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
        finishInsDlg = dlgBuilder.create();
        finishInsDlg.show();

        // 确定按钮
        Button confirmBtn = finishInsDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = finishInsDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeSetSummaryDialog();
                finishInsDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finishInsDlg.cancel();
            }
        });
    }

    /**
     * 显示巡视结束总结对话框
     */
    public void makeSetSummaryDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_edit_summary, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("巡视结束总结");
        dlgBuilder.setIcon(R.drawable.table_icon_pass_normal);


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
        dlgBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // 停止录音
                recordVoiceStop();

                playVoiceContinueFlag = false;

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                playVoiceStop();
            }
        });

        EditText textView = (EditText) layout.findViewById(R.id.textView);
        // 录音layout
        LinearLayout recordLayout = (LinearLayout) layout.findViewById(R.id.record_voice_layout);
        // 放音layout
        LinearLayout playLayout = (LinearLayout) layout.findViewById(R.id.play_voice_layout);
        // 开始录制按钮
        ImageButton recordVoiceStartBtn = (ImageButton) layout.findViewById(R.id.record_voice_start);
        // 停止录制按钮
        ImageButton recordVoiceStopBtn = (ImageButton) layout.findViewById(R.id.record_voice_stop);
        // 播放按钮
        ImageButton startBtn = (ImageButton) layout.findViewById(R.id.play_voice_start);
        // 暂停播放按钮
        ImageButton pauseBtn = (ImageButton) layout.findViewById(R.id.play_voice_pause);
        // 停止播放按钮
        ImageButton stopBtn = (ImageButton) layout.findViewById(R.id.play_voice_stop);
        // 删除音频按钮
        ImageButton deleteBtn = (ImageButton) layout.findViewById(R.id.play_voice_delete);

        textView.setText((String) bizInfo.get("memo"));
        textView.setHint("请输入巡视结束总结…");

        recordLayout.setVisibility(View.VISIBLE);
        playLayout.setVisibility(View.GONE);

        recordVoiceStartBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins");
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                String attaName = CommonUtil.GetNextID() + ".m4a";
                File attaFile = new File(fileDir.getAbsolutePath() + "/" + attaName);
                if (attaFile.exists() && attaFile.isFile()) {
                    attaFile.delete();
                }

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
                    audioInfo = new HashMap<String, Object>();
                    audioInfo.put("type", CommonParam.ATTA_TYPE_AUDIO);
                    audioInfo.put("name", attaName);
                    audioInfo.put("alias", "巡视结束总结_" + baseApp.loginUser.get("realname") + ".m4a");
                    audioInfo.put("size", new FileUtil().getFileSize(attaFile));

                    // 录音layout
                    LinearLayout recordLayout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_layout);
                    // 放音layout
                    LinearLayout playLayout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_layout);

                    recordLayout.setVisibility(View.GONE);
                    playLayout.setVisibility(View.VISIBLE);
                    playVoiceContinueFlag = false;
                }
            }
        });
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!playVoiceContinueFlag) {
                    // 重新开始播放
                    if (audioInfo != null) {
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
                        classThis.playAudio(Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/" + CommonParam.PROJECT_NAME + "/ins/" + audioInfo.get("name"));
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
        deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                playVoiceContinueFlag = false;

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                new DeleteAudioTask().execute();
                playVoiceStop();
            }
        });

        summaryDlg = dlgBuilder.create();
        summaryDlg.show();
        // 确定按钮
        Button confirmBtn = summaryDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = summaryDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        confirmBtn.setTag(textView);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button confirmBtn = (Button) v;
                // 停止录音。开始================================================================
                boolean bFlag = false;
                if (mediaRecorder != null) {
                    recordVoiceStop();

                    File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins");
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    String attaName = CommonUtil.GetNextID() + ".m4a";
                    File attaFile = new File(fileDir.getAbsolutePath() + "/" + attaName);
                    try {
                        new FileUtil().copyFile(capAudioFile.getAbsolutePath(), attaFile.getAbsolutePath());
                        bFlag = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        capAudioFile.delete();
                    }
                    if (bFlag) {
                        audioInfo = new HashMap<String, Object>();
                        audioInfo.put("type", CommonParam.ATTA_TYPE_AUDIO);
                        audioInfo.put("name", attaName);
                        audioInfo.put("alias", "巡视结束总结_" + baseApp.loginUser.get("realname") + ".m4a");
                        audioInfo.put("size", new FileUtil().getFileSize(attaFile));

                        // 录音layout
                        LinearLayout recordLayout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_layout);
                        // 放音layout
                        LinearLayout playLayout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_layout);

                        recordLayout.setVisibility(View.GONE);
                        playLayout.setVisibility(View.VISIBLE);
                        playVoiceContinueFlag = false;
                    }
                } else {
                    bFlag = true;
                }
                // 停止录音。结束================================================================
                EditText t = (EditText) v.getTag();
                String s = CommonUtil.N2B(t.getText().toString());
                if (CommonUtil.checkNB(s)) {
                    s = baseApp.loginUser.get("realname") + "的巡视结束总结：" + s;
                }

                infoTool = getInfoTool();
                ContentValues cv = new ContentValues();
                cv.put("memo", s);
                String starttime = (String) bizInfo.get("realatime");
                if (!CommonUtil.checkNB(starttime)) {
                    cv.put("realatime", CommonUtil.getDT());
                }
                String endtime = CommonUtil.getDT();
                cv.put("realbtime", endtime);

                if (bFlag) {
                    cv.put("atta", JSONObject.toJSONString(audioInfo));
                }

                infoTool.update("t_biz_sgxuns", cv, "ids=? and quid=?", new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});

                bizInfo.put("memo", s);
                updateFlag = true;
                infoStatusTv.setText("已完成");
                infoStatusTv.setTextColor(getResources().getColor(R.color.text_green_dark));

                confirmBtn.setClickable(false);
                confirmBtn.setEnabled(false);
                summaryDlg.cancel();

                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putString("msg", getString(R.string.alert_inspect_over, (String) bizInfo.get("title")));
                data.putBoolean("finishFlag", true);
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, InsTaskListLandActivity.class);

                // 将数据存入Intent中
                intent.putExtras(data);
                setResult(CommonParam.RESULTCODE_REFRESH_REC_LIST, intent);
                InsPointEditListLandActivity.super.goBack();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                summaryDlg.cancel();
            }
        });
    }

    /**
     * 开始录音
     */
    public void recordVoiceStart() {
        try {
            // 录音layout
            LinearLayout recordLayout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_layout);
            // 放音layout
            LinearLayout playLayout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_layout);
            LinearLayout record_voice_start_layout = (LinearLayout) summaryDlg
                    .findViewById(R.id.record_voice_start_layout);
            LinearLayout record_voice_stop_layout = (LinearLayout) summaryDlg
                    .findViewById(R.id.record_voice_stop_layout);
            ImageView record_voice_iv = (ImageView) summaryDlg.findViewById(R.id.record_voice_iv);

            recordLayout.setVisibility(View.VISIBLE);
            playLayout.setVisibility(View.GONE);
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
        // 录音layout
        LinearLayout recordLayout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_layout);
        // 放音layout
        LinearLayout playLayout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_layout);
        LinearLayout record_voice_start_layout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_start_layout);
        LinearLayout record_voice_stop_layout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_stop_layout);
        ImageView record_voice_iv = (ImageView) summaryDlg.findViewById(R.id.record_voice_iv);

        recordLayout.setVisibility(View.VISIBLE);
        playLayout.setVisibility(View.GONE);
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
            LinearLayout play_voice_start_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_start_layout);
            LinearLayout play_voice_pause_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_pause_layout);
            LinearLayout play_voice_stop_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_stop_layout);
            LinearLayout play_voice_delete_layout = (LinearLayout) summaryDlg
                    .findViewById(R.id.play_voice_delete_layout);
            ImageView play_voice_iv = (ImageView) summaryDlg.findViewById(R.id.play_voice_iv);
            TextView play_voice_start_tv = (TextView) summaryDlg.findViewById(R.id.play_voice_start_tv);
            play_voice_start_layout.setVisibility(View.GONE);
            play_voice_pause_layout.setVisibility(View.VISIBLE);
            play_voice_stop_layout.setVisibility(View.VISIBLE);
            play_voice_delete_layout.setVisibility(View.VISIBLE);
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
            LinearLayout play_voice_start_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_start_layout);
            LinearLayout play_voice_pause_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_pause_layout);
            LinearLayout play_voice_stop_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_stop_layout);
            LinearLayout play_voice_delete_layout = (LinearLayout) summaryDlg
                    .findViewById(R.id.play_voice_delete_layout);
            ImageView play_voice_iv = (ImageView) summaryDlg.findViewById(R.id.play_voice_iv);
            TextView play_voice_start_tv = (TextView) summaryDlg.findViewById(R.id.play_voice_start_tv);
            play_voice_start_layout.setVisibility(View.VISIBLE);
            play_voice_pause_layout.setVisibility(View.GONE);
            play_voice_stop_layout.setVisibility(View.VISIBLE);
            play_voice_delete_layout.setVisibility(View.VISIBLE);
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
            LinearLayout play_voice_start_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_start_layout);
            LinearLayout play_voice_pause_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_pause_layout);
            LinearLayout play_voice_stop_layout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_stop_layout);
            LinearLayout play_voice_delete_layout = (LinearLayout) summaryDlg
                    .findViewById(R.id.play_voice_delete_layout);
            ImageView play_voice_iv = (ImageView) summaryDlg.findViewById(R.id.play_voice_iv);
            TextView play_voice_start_tv = (TextView) summaryDlg.findViewById(R.id.play_voice_start_tv);
            play_voice_start_layout.setVisibility(View.VISIBLE);
            play_voice_pause_layout.setVisibility(View.GONE);
            play_voice_stop_layout.setVisibility(View.VISIBLE);
            play_voice_delete_layout.setVisibility(View.VISIBLE);
            play_voice_iv.setBackgroundResource(R.drawable.voice_pause);
            AnimationDrawable pv_ad = (AnimationDrawable) play_voice_iv.getBackground();
            pv_ad.start();
            play_voice_start_tv.setText(R.string.voice_play);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除附件的 AsyncTask 类
     */
    public class DeleteAudioTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：删除成功
         */
        private static final int PROGRESS_DELETE_SUCCESS = 1001;
        /**
         * 进度常量：删除失败
         */
        private static final int PROGRESS_DELETE_FAIL = 1002;
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
            atta = audioInfo;

            if (atta != null) {
                // 删除文件
                new FileUtil().deleteFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins", (String) atta.get("name"));
                // 删除成功
                publishProgress(PROGRESS_DELETE_SUCCESS);
            }
            // 显示音频
            publishProgress(PROGRESS_SHOW_AUDIO);

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
                // show(R.string.alert_delete_success);
            } else if (progress[0] == PROGRESS_DELETE_FAIL) {
                // 删除失败
                // show(R.string.alert_delete_fail);
            } else if (progress[0] == PROGRESS_SHOW_AUDIO) {
                // 显示音频
                // 录音layout
                LinearLayout recordLayout = (LinearLayout) summaryDlg.findViewById(R.id.record_voice_layout);
                // 放音layout
                LinearLayout playLayout = (LinearLayout) summaryDlg.findViewById(R.id.play_voice_layout);

                recordLayout.setVisibility(View.VISIBLE);
                playLayout.setVisibility(View.GONE);

                playVoiceStop();
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
     * 检查RFID卡片编号 AsyncTask 类
     */
    private class CheckCardTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：水工资源表中已绑定该卡
         */
        private static final int PROGRESS_RES_BIND = 1001;
        /**
         * 进度常量：水工资源区域标识表已绑定该卡
         */
        private static final int PROGRESS_AREASIGN_BIND = 1002;
        /**
         * 进度常量：所有表都绑定该卡
         */
        private static final int PROGRESS_ALL_BIND = 1003;
        /**
         * 进度常量：错误的卡
         */
        private static final int PROGRESS_INCORRECT_CARD = 1004;

        /**
         * 卡编号
         */
        private String cardMac;

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

            // 卡编号
            cardMac = (String) params[0];

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            // 水工资源表中该卡号的数量
            int resTotal = 0;
            // 水工资源区域标识表中该卡号的数量
            int areaSignTotal = 0;

            if (SCAN_CARD_TYPE_LOC.equals(scanCardType)) {
                // 定位
                // 检查该卡片是否绑定到了其他资源
                resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1' and model.loctype='1' and model.loc=? and model.ids=?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                if (resTotal == 0) {
                    // 如果在水工资源表中找不到该卡，就到水工资源采集定位记录表中找
                    resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresloc model where model.valid='1' and model.loctype='1' and model.loc=? and model.res_id=?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                }
                if (resTotal == 0) {
                    publishProgress(PROGRESS_INCORRECT_CARD);
                } else {
                    result = CommonParam.RESULT_SUCCESS;
                }
            } else {
                // 采集
                // 检查该卡片是否绑定到了其他资源
                resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1' and model.loctype='1' and model.loc=? and model.ids<>?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                if (resTotal == 0) {
                    // 如果在水工资源表中找不到该卡，就到水工资源采集定位记录表中找
                    resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresloc model where model.valid='1' and model.loctype='1' and model.loc=? and model.res_id<>?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                }
                if (resTotal == 0) {
                    // 检查该卡片是否绑定到了其他资源的区域中
                    areaSignTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresareasign model where model.valid='1' and model.loctype='1' and model.loc=? and model.rid<>?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                }
                if (resTotal > 0 && areaSignTotal == 0) {
                    publishProgress(PROGRESS_RES_BIND);
                } else if (resTotal == 0 && areaSignTotal > 0) {
                    publishProgress(PROGRESS_AREASIGN_BIND);
                } else if (resTotal > 0 && areaSignTotal > 0) {
                    publishProgress(PROGRESS_ALL_BIND);
                } else {
                    result = CommonParam.RESULT_SUCCESS;
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
            if (progress[0] == PROGRESS_RES_BIND) {
                // 水工资源表中已绑定该卡
                setScanFieldStart("<span style=\"color:#ed6c26\">该卡已绑定到其他巡视点！</span>");
            } else if (progress[0] == PROGRESS_AREASIGN_BIND) {
                // 水工资源区域标识表已绑定该卡
                setScanFieldStart("<span style=\"color:#ed6c26\">该卡已绑定到其他区域！</span>");
            } else if (progress[0] == PROGRESS_ALL_BIND) {
                // 所有表都绑定该卡
                setScanFieldStart("<span style=\"color:#ed6c26\">该卡已同时绑定到其他巡视点和区域！</span>");
            } else if (progress[0] == PROGRESS_INCORRECT_CARD) {
                // 错误的卡
                setScanFieldStart("<span style=\"color:#ed6c26\">该卡片不属于本巡视点，请重新扫描！</span>");
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
                readCard_ins(cardMac);
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("du.wav");
                }
            }
        }
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
         * 进度常量：水工资源区域标识表已绑定该卡
         */
        private static final int PROGRESS_AREASIGN_BIND = 1002;
        /**
         * 进度常量：所有表都绑定该卡
         */
        private static final int PROGRESS_ALL_BIND = 1003;
        /**
         * 进度常量：错误的卡
         */
        private static final int PROGRESS_INCORRECT_CARD = 1004;

        /**
         * 卡编号
         */
        private String cardMac;

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

            // 卡编号
            cardMac = (String) params[0];

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            // 水工资源表中该卡号的数量
            int resTotal = 0;
            // 水工资源区域标识表中该卡号的数量
            int areaSignTotal = 0;

            if (SCAN_UHF_CARD_TYPE_LOC.equals(scanUhfCardType)) {
                // 定位
                // 检查该卡片是否绑定到了其他资源
                resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1' and model.loctype='3' and model.loc=? and model.ids=?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                if (resTotal == 0) {
                    // 如果在水工资源表中找不到该卡，就到水工资源采集定位记录表中找
                    resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresloc model where model.valid='1' and model.loctype='3' and model.loc=? and model.res_id=?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                }
                for (int i = 0; i < CommonParam.UHF_CHECK_STOP_TOTAL; i++) {
                    Log.d("@@@" + i, "###" + isUhfScanStop);
                    if (!isUhfScanStop) {
                        doWait(CommonParam.UHF_CHECK_STOP_INTERVAL);
                    } else {
                        break;
                    }
                }
                if (resTotal == 0) {
                    publishProgress(PROGRESS_INCORRECT_CARD);
                } else {
                    result = CommonParam.RESULT_SUCCESS;
                }
            } else {
                // 采集
                // 检查该卡片是否绑定到了其他资源
                resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1' and model.loctype='3' and model.loc=? and model.ids<>?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                if (resTotal == 0) {
                    // 如果在水工资源表中找不到该卡，就到水工资源采集定位记录表中找
                    resTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresloc model where model.valid='1' and model.loctype='3' and model.loc=? and model.res_id<>?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                }
                if (resTotal == 0) {
                    // 检查该卡片是否绑定到了其他资源的区域中
                    areaSignTotal = infoTool.getCount("select count(model.ids) from t_szfgs_sgresareasign model where model.valid='1' and model.loctype='3' and model.loc=? and model.rid<>?", new String[]{cardMac, (String) currentResInfo.get("ids")});
                }
                for (int i = 0; i < CommonParam.UHF_CHECK_STOP_TOTAL; i++) {
                    Log.d("@@@" + i, "###" + isUhfScanStop);
                    if (!isUhfScanStop) {
                        doWait(CommonParam.UHF_CHECK_STOP_INTERVAL);
                    } else {
                        break;
                    }
                }
                if (resTotal > 0 && areaSignTotal == 0) {
                    publishProgress(PROGRESS_RES_BIND);
                } else if (resTotal == 0 && areaSignTotal > 0) {
                    publishProgress(PROGRESS_AREASIGN_BIND);
                } else if (resTotal > 0 && areaSignTotal > 0) {
                    publishProgress(PROGRESS_ALL_BIND);
                } else {
                    result = CommonParam.RESULT_SUCCESS;
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
            if (progress[0] == PROGRESS_RES_BIND) {
                // 水工资源表中已绑定该卡
                setUhfScanFieldStart(String.format("%s%s", "<span style=\"color:#ed6c26\">该卡已绑定到其他巡视点！</span><br/><br/>", getString(R.string.text_scan_uhf_card_info_not_start)));
            } else if (progress[0] == PROGRESS_AREASIGN_BIND) {
                // 水工资源区域标识表已绑定该卡
                setUhfScanFieldStart(String.format("%s%s", "<span style=\"color:#ed6c26\">该卡已绑定到其他区域！</span><br/><br/>", getString(R.string.text_scan_uhf_card_info_not_start)));
            } else if (progress[0] == PROGRESS_ALL_BIND) {
                // 所有表都绑定该卡
                setUhfScanFieldStart(String.format("%s%s", "<span style=\"color:#ed6c26\">该卡已同时绑定到其他巡视点和区域！</span><br/><br/>", getString(R.string.text_scan_uhf_card_info_not_start)));
            } else if (progress[0] == PROGRESS_INCORRECT_CARD) {
                // 错误的卡
                setUhfScanFieldStart("<span style=\"color:#ed6c26\">该卡片不属于本巡视点，请重新扫描！</span>");
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
                readUhfCard_ins(cardMac);
            } else {
                if (baseApp.isAutoPlayInsAudio) {
                    playVoice("du.wav");
                }
            }
        }
    }

    /**
     * 显示旋转屏幕对话框
     */
    public void makeShowRotateAlertDialog() {
        if (rotateAlertDlg == null) {
            Builder dlgBuilder = new Builder(this);

            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_rotate_screen_portrait, null);
            dlgBuilder.setView(layout);
            dlgBuilder.setTitle(R.string.alert_ts);
            if (!baseApp.isReverseRotate) {
                dlgBuilder.setIcon(R.drawable.to_portrait_blue);
            } else {
                dlgBuilder.setIcon(R.drawable.to_rev_portrait_blue);
            }
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
            dlgBuilder.setNeutralButton(R.string.info_neveralert, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            rotateAlertDlg = dlgBuilder.create();
            rotateAlertDlg.show();

            ImageView animate_iv = (ImageView) rotateAlertDlg.findViewById(R.id.animate_iv);
            if (baseApp.isReverseRotate) {
                animate_iv.setBackground(getResources().getDrawable(R.drawable.waiting_rotate_rev_portrait));
            }
            AnimationDrawable animate_ad = (AnimationDrawable) animate_iv.getBackground();
            animate_ad.start();

            // 确定按钮
            Button confirmBtn = rotateAlertDlg.getButton(DialogInterface.BUTTON_POSITIVE);
            // 取消按钮
            Button cancelBtn = rotateAlertDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
            // 不再提醒按钮
            Button neverAlerttn = rotateAlertDlg.getButton(DialogInterface.BUTTON_NEUTRAL);

            confirmBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 创建启动 Activity 的 Intent
                    Intent intent = new Intent(classThis, InsAutoDkListActivity.class);
                    // 信息传输Bundle
                    Bundle data = new Bundle();
                    data.putSerializable("bizInfo", bizInfo);
                    data.putSerializable("resInfo", currentResInfo);
                    // 将数据存入Intent中
                    intent.putExtras(data);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                    rotateAlertDlg.cancel();
                }
            });
            cancelBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    rotateAlertDlg.cancel();
                }
            });
            neverAlerttn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    needShowRotatePortraitAlertFlag = false;
                    preferEditor.putBoolean("needShowRotatePortraitAlertFlag", needShowRotatePortraitAlertFlag);
                    preferEditor.commit();
                    rotateAlertDlg.cancel();
                }
            });
        } else {
            rotateAlertDlg.show();
        }
    }

    /**
     * 设置扫描相关字段状态：准备开始
     *
     * @param msg {@code String} 提示文本
     */
    private void setScanFieldStart(String msg) {
        if (readCardType == CommonParam.READ_CARD_TYPE_INSPECT) {
            // 读巡视卡
            TextView info_tv = (TextView) readCardDlg.findViewById(R.id.info_tv);
            info_tv.setText(Html.fromHtml(msg, null, new HtmlTagHandler(classThis)));
            info_tv.setTextSize(16.0F);
        }
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

    public class UhfScanMsgCallback implements TagCallback {

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
                UhfScanMsgCallback callback = new UhfScanMsgCallback();
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
                stopScanUhf();
            }
        } catch (Exception e) {
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
        contentView = (LinearLayout) findViewById(R.id.contentView);
        titleText = (TextView) findViewById(R.id.title_text_view);
        titleBarModeName = (TextView) findViewById(R.id.title_type_text_view);
        infoStatusTv = (TextView) findViewById(R.id.infoStatusTv);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        helpBtn = (ImageButton) findViewById(R.id.helpBtn);
        insTaskShowBtn = (Button) findViewById(R.id.insTaskShowBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        jcBtn = (Button) findViewById(R.id.jcBtn);
        dkBtn = (Button) findViewById(R.id.dkBtn);
        autoDkBtn = (Button) findViewById(R.id.autoDkBtn);
        locBtn = (Button) findViewById(R.id.locBtn);
        locAddBtn = (Button) findViewById(R.id.locAddBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        infoList = (ListView) findViewById(R.id.infoList);
        totalNumTv = (TextView) findViewById(R.id.totalNumTv);
        currentNumTv = (TextView) findViewById(R.id.currentNumTv);
        // 界面相关参数。结束===============================
    }
}
