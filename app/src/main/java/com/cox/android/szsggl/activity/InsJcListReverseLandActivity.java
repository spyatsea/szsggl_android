/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.handler.HtmlTagHandler;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsJcListAdapter;
import com.cox.android.uhf.Reader;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.rfid.InventoryTagMap;
import com.rfid.PowerUtil;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 巡视_现场检查记录_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsJcListReverseLandActivity extends DbActivity {
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
     * 主界面
     */
    LinearLayout contentView;
    /**
     * 巡视类型名称
     */
    TextView titleBarModeName;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
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
     * 临时退出
     */
    private Button pauseBtn;
    /**
     * 开始
     */
    private Button createBtn;
    /**
     * 结束
     */
    private Button finishBtn;
    // 界面相关参数。开始===============================
    private TextView titleTv;
    private TextView markTitleTv;
    private LinearLayout markContentLayout;
    private TextView markTv;
    private TextView cardMacTv;
    private ImageButton markChooseBtn;
    private ImageButton markDeleteBtn;
    private ImageButton addUhfBtn;
    private ImageButton editUhfBtn;
    private ImageButton scanUhfBtn;
    private TextView stdNumTv;
    private ImageView startTimeIv;
    private TextView startTimeTv;
    // 界面相关参数。结束===============================

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
    private InsJcListAdapter infoListAdapter;
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
     * 资源信息
     */
    private HashMap<String, Object> resInfo;
    /**
     * 资源类别信息
     */
    private HashMap<String, Object> categoryInfo;
    /**
     * 当前的区域标识信息
     */
    private HashMap<String, Object> areaSignInfo;
    /**
     * 编辑标志
     */
    private boolean editFlag;
    /**
     * 缺陷描述List
     */
    private JSONArray stdList;
    /**
     * 未完成的检查信息
     */
    private HashMap<String, Object> unfinishJcInfo;
    /**
     * 未完成的检查信息编号
     */
    private String unfinishJcId;
    /**
     * 检查结果List
     */
    private ArrayList<String> rList;
    /**
     * 检查结果Color List
     */
    private ArrayList<Integer> rColorList;
    /**
     * 检查结果Voice List
     */
    private ArrayList<String> rVoiceList;
    /**
     * 消缺结果List
     */
    private ArrayList<String> xqList;
    /**
     * 消缺结果Color List
     */
    private ArrayList<Integer> xqColorList;
    /**
     * 消缺结果Voice List
     */
    private ArrayList<String> xqVoiceList;
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
     * 巡视结束Dialog
     */
    private AlertDialog finishInsDlg;
    /**
     * 水工巡视检查记录结果Map
     */
    private HashMap<String, HashMap<String, Object>> insJcSubTableResultMap;
    /**
     * 新区域名称标志
     */
    private boolean newMarkFlag = false;

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

        classThis = InsJcListReverseLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");
        resInfo = (HashMap<String, Object>) data.getSerializable("resInfo");
        unfinishJcId = CommonUtil.N2B(data.getString("unfinishJcId"));

        setContentView(R.layout.ins_jc_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editFlag) {
                    new PauseInsTask().execute();
                } else {
                    // 返回
                    goBack();
                }
            }
        });
        goBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        pauseBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pauseBtn.setClickable(false);
                pauseBtn.setEnabled(false);
                if (editFlag) {
                    new PauseInsTask().execute();
                }
            }
        });
        createBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createBtn.setClickable(false);
                createBtn.setEnabled(false);
                new PrepareToJcTask().execute();
            }
        });
        finishBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finishBtn.setClickable(false);
                finishBtn.setEnabled(false);
                new CheckFinishTask().execute();
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
                if (editFlag) {
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
                markTv.setHint("请扫卡、输入或选择");
                areaSignInfo = null;
                newMarkFlag = false;
                markContentLayout.setBackgroundResource(R.drawable.border_trans_blue_grey);
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
        } else if (requestCode == CommonParam.REQUESTCODE_INS_DESC && resultCode == CommonParam.RESULTCODE_INS_DESC) {
            // 多媒体描述
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
        if (editFlag) {
            setResult(CommonParam.RESULTCODE_REFRESH_REC_LIST);
        }
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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
        if (searchTask != null) {
            searchTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (editFlag) {
                    new PauseInsTask().execute();
                } else {
                    // 返回
                    goBack();
                }
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
            listItems = new ArrayList<HashMap<String, Object>>();
            editFlag = false;
            rList = new ArrayList<String>(4);
            rList.add(getString(R.string.title_jc_result_yes));
            rList.add(getString(R.string.title_jc_result_no));
            rList.add(getString(R.string.title_jc_result_ignore));
            rList.add("");
            rColorList = new ArrayList<Integer>(4);
            rColorList.add(R.color.text_green_dark);
            rColorList.add(R.color.text_red);
            rColorList.add(R.color.normal_text_color_grey);
            rColorList.add(R.color.list_color_content_font_blue_01);
            rVoiceList = new ArrayList<String>(4);
            rVoiceList.add("ok.wav");
            rVoiceList.add("err.wav");
            rVoiceList.add("ignore.wav");
            rVoiceList.add("popup.wav");
            xqList = new ArrayList<String>(2);
            xqList.add(getString(R.string.title_jc_xq_yes));
            xqList.add(getString(R.string.title_jc_xq_no));
            xqColorList = new ArrayList<Integer>(2);
            xqColorList.add(R.color.text_green_dark);
            xqColorList.add(R.color.text_red);
            xqVoiceList = new ArrayList<String>(2);
            xqVoiceList.add("startup.wav");
            xqVoiceList.add("misrecognition.wav");
            insJcSubTableResultMap = new HashMap<String, HashMap<String, Object>>();

            // 区域标识信息
            areaSignList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                    "select * from t_szfgs_sgresareasign model where model.valid='1' and model.rid=? order by CAST(model.n as int) asc", new String[]{(String) resInfo.get("ids")});
            ArrayList<HashMap<String, Object>> categoryList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT * FROM t_szfgs_sgcategory model WHERE model.valid='1' and model.ids=?",
                            new String[]{(String) resInfo.get("cid")});
            if (categoryList.size() > 0) {
                categoryInfo = categoryList.get(0);
            }
            if (categoryInfo == null) {
                return result;
            }
            if (CommonUtil.checkNB(unfinishJcId)) {
                // 继续检查
                ArrayList<HashMap<String, Object>> jcList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT * FROM t_szfgs_sgxunsjcjl model WHERE model.valid='1' and model.ids=? and model.quid=?",
                                new String[]{unfinishJcId, (String) baseApp.getLoginUser().get("ids")});
                if (jcList.size() > 0) {
                    unfinishJcInfo = jcList.get(0);
                }
                if (unfinishJcInfo == null) {
                    return result;
                }
            }
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
                listTitleLayout.setVisibility(View.VISIBLE);
                if (unfinishJcInfo == null) {
                    // 新检查
                    titleTv.setText((String) resInfo.get("title"));
                    markTitleTv.setVisibility(View.GONE);
                    markContentLayout.setVisibility(View.GONE);
                    markTv.setText("");
                    markChooseBtn.setVisibility(View.GONE);
                    markDeleteBtn.setVisibility(View.GONE);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.GONE);
                    scanUhfBtn.setVisibility(View.GONE);
                    startTimeIv.setVisibility(View.GONE);
                    startTimeTv.setVisibility(View.GONE);
                    createBtn.setText(R.string.title_jc_start);
                } else {
                    // 继续检查
                    titleTv.setText((String) unfinishJcInfo.get("res_title"));
                    markTitleTv.setVisibility(View.VISIBLE);
                    markContentLayout.setVisibility(View.VISIBLE);
                    markTv.setText(CommonUtil.N2B((String) unfinishJcInfo.get("areasign")));
                    markTv.setHint("");
                    markChooseBtn.setVisibility(View.GONE);
                    markDeleteBtn.setVisibility(View.GONE);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.GONE);
                    scanUhfBtn.setVisibility(View.GONE);
                    startTimeIv.setVisibility(View.VISIBLE);
                    startTimeTv.setVisibility(View.VISIBLE);
                    startTimeTv.setText(CommonUtil.N2B((String) unfinishJcInfo.get("atime")));
                    createBtn.setText(R.string.title_jc_continue);
                }
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
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (InsJcListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoListAdapter = new InsJcListAdapter(getApplicationContext(), listItems, R.layout.ins_jc_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info"}, new int[]{R.id.tableRowLayout, R.id.info_c1,
                            R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.info_c5});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new InsJcListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof LinearLayout) {
                                LinearLayout layout = (LinearLayout) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (layout.getId() == R.id.tableRowLayout) {
                                    int n = (int) info.get("V_INFO_SN");
                                    layout.setTag("tableRowLayout_" + n);
                                    // 如果是第一行，要显示上边框
                                    if (n == 1) {
                                        View splitterView_1 = layout.findViewById(R.id.splitterView_1);
                                        splitterView_1.setVisibility(View.VISIBLE);
                                    }
                                } else if (layout.getId() == R.id.info_c1) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t1);
                                    textView.setText("" + info.get("V_INFO_SN"));
                                } else if (layout.getId() == R.id.info_c2) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t2);
                                    textView.setText((String) info.get("c"));
                                    // 这里是为了让表格行高一些，方便点击
                                    ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                                    mp.topMargin = 25;
                                    mp.bottomMargin = 25;
                                    mp.leftMargin = 10;
                                    mp.rightMargin = 10;
                                } else if (layout.getId() == R.id.info_c3) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t3);
                                    // 检查结果
                                    String r = (String) info.get("r");
                                    textView.setText(r);
                                    // 结果索引
                                    int rIndex = getRIndex(r);
                                    textView.setTextColor(getResources().getColor(rColorList.get(rIndex)));
                                    if (editFlag) {
                                        textView.setBackgroundColor(getResources().getColor(R.color.trans_blue_lighter));
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        textView.setBackground(null);
                                        layout.setBackground(null);
                                    }
                                    textView.setTag("info_c3_" + info.get("V_INFO_SN"));
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (editFlag) {
                                                LinearLayout layout = (LinearLayout) v;
                                                TextView textView_r = (TextView) layout.findViewById(R.id.info_t3);
                                                // 检查结果
                                                String rPre = textView_r.getText().toString();
                                                // 下一个检查结果索引
                                                int rIndex = getNextRIndex(rPre);
                                                // 下一个检查结果
                                                String r = rList.get(rIndex);
                                                textView_r.setText(r);
                                                textView_r.setTextColor(getResources().getColor(rColorList.get(rIndex)));

                                                // 设置info_t4值。开始=====================================
                                                LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + layout.getTag());
                                                TextView textView_xqf = (TextView) tableRowLayout.findViewById(R.id.info_t4);
                                                // 消缺否
                                                String xqf = null;
                                                if (getString(R.string.title_jc_result_no).equals(r)) {
                                                    // 异常
                                                    xqf = getString(R.string.title_jc_xq_no);
                                                    textView_xqf.setText(xqf);
                                                    textView_xqf.setTextColor(getResources().getColor(R.color.text_red));
                                                } else {
                                                    // 正常、忽略、未检查
                                                    xqf = getString(R.string.title_jc_xq_blank);
                                                    textView_xqf.setText("");
                                                    textView_xqf.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                                }
                                                // 设置info_t4值。结束=====================================

                                                if (baseApp.isAutoPlayInsAudio) {
                                                    playVoice(rVoiceList.get(rIndex));
                                                }
                                                // 序号
                                                int n = (Integer) layout.getTag();
                                                // 索引
                                                int index = -1;
                                                HashMap<String, Object> info = null;
                                                try {
                                                    index = n - 1;
                                                } catch (Exception e) {
                                                }
                                                if (index != -1) {
                                                    HashMap<String, Object> vMap = listItems.get(index);
                                                    if (vMap != null) {
                                                        info = (HashMap<String, Object>) vMap.get("info");
                                                    }
                                                }
                                                // 行结果Map
                                                HashMap<String, Object> subInfo = insJcSubTableResultMap.get((String) info.get("ids"));
                                                subInfo.put("r", r);
                                                subInfo.put("xqf", xqf);
                                                info.put("r", r);
                                                info.put("xqf", xqf);
                                                // 更新表
                                                new UpdateInsJcSubTableTask().execute((String) info.get("ids"), r, xqf);
                                            }
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c4) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t4);
                                    // 检查结果
                                    String r = (String) info.get("r");
                                    // 消缺否
                                    String xqf = (String) info.get("xqf");
                                    if (getString(R.string.title_jc_result_no).equals(r)) {
                                        // 异常
                                        textView.setText(xqf);
                                        if (getString(R.string.title_jc_xq_yes).equals(xqf)) {
                                            textView.setTextColor(getResources().getColor(R.color.text_green_dark));
                                        } else {
                                            textView.setTextColor(getResources().getColor(R.color.text_red));
                                        }
                                    } else {
                                        // 正常、忽略、未检查
                                        textView.setText("");
                                        textView.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                    }
                                    if (editFlag) {
                                        textView.setBackgroundColor(getResources().getColor(R.color.trans_blue_lighter));
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        textView.setBackground(null);
                                        layout.setBackground(null);
                                    }
                                    textView.setTag("info_c4_" + info.get("V_INFO_SN"));
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (editFlag) {
                                                LinearLayout layout = (LinearLayout) v;
                                                LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + layout.getTag());
                                                TextView textView_r = (TextView) tableRowLayout.findViewById(R.id.info_t3);
                                                TextView textView_xqf = (TextView) layout.findViewById(R.id.info_t4);
                                                // 检查结果
                                                String r = textView_r.getText().toString();
                                                // 消缺否
                                                String xqf = textView_xqf.getText().toString();

                                                if (getString(R.string.title_jc_result_no).equals(r)) {
                                                    // 异常
                                                    if (getString(R.string.title_jc_xq_no).equals(xqf)) {
                                                        xqf = xqList.get(0);
                                                        textView_xqf.setTextColor(getResources().getColor(xqColorList.get(0)));
                                                        if (baseApp.isAutoPlayInsAudio) {
                                                            playVoice(xqVoiceList.get(0));
                                                        }
                                                    } else {
                                                        xqf = xqList.get(1);
                                                        textView_xqf.setTextColor(getResources().getColor(xqColorList.get(1)));
                                                        if (baseApp.isAutoPlayInsAudio) {
                                                            playVoice(xqVoiceList.get(1));
                                                        }
                                                    }
                                                    textView_xqf.setText(xqf);

                                                    // 序号
                                                    int n = (Integer) layout.getTag();
                                                    // 索引
                                                    int index = -1;
                                                    HashMap<String, Object> info = null;
                                                    try {
                                                        index = n - 1;
                                                    } catch (Exception e) {
                                                    }
                                                    if (index != -1) {
                                                        HashMap<String, Object> vMap = listItems.get(index);
                                                        if (vMap != null) {
                                                            info = (HashMap<String, Object>) vMap.get("info");
                                                        }
                                                    }
                                                    // 行结果Map
                                                    HashMap<String, Object> subInfo = insJcSubTableResultMap.get((String) info.get("ids"));
                                                    subInfo.put("r", r);
                                                    subInfo.put("xqf", xqf);
                                                    info.put("r", r);
                                                    info.put("xqf", xqf);
                                                    // 更新表
                                                    new UpdateInsJcSubTableTask().execute((String) info.get("ids"), r, xqf);
                                                }
                                            }
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c5) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t5);
                                    ImageView textView_i1 = (ImageView) layout.findViewById(R.id.info_t5i1);
                                    ImageView textView_i2 = (ImageView) layout.findViewById(R.id.info_t5i2);
                                    // 检查描述
                                    String memo = CommonUtil.N2B((String) info.get("memo"));
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
                                    if ((memo_e_array != null && memo_e_array.size() > 0) || CommonUtil.checkNB(memo_d_str)) {
                                        textView_i1.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_i1.setVisibility(View.GONE);
                                    }
                                    // 是否有附件
                                    String infoAtta = (String) info.get("V_INFO_ATTA");
                                    if (CommonParam.YES.equals(infoAtta)) {
                                        textView_i2.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_i2.setVisibility(View.GONE);
                                    }

                                    if (editFlag) {
                                        textView.setBackgroundColor(getResources().getColor(R.color.trans_blue_lighter));
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        textView.setBackground(null);
                                        if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                            layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                        } else {
                                            layout.setBackground(null);
                                        }
                                    }
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImageView textView_i1 = (ImageView) v.findViewById(R.id.info_t5i1);
                                            ImageView textView_i2 = (ImageView) v.findViewById(R.id.info_t5i2);
                                            // 点击后是否打开缺陷描述页面
                                            boolean canClickFlag = false;
                                            if (editFlag) {
                                                canClickFlag = true;
                                            } else {
                                                if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                                    canClickFlag = true;
                                                }
                                            }
                                            if (canClickFlag) {
                                                // 序号
                                                int n = (Integer) layout.getTag();
                                                // 索引
                                                int index = -1;
                                                HashMap<String, Object> info = null;
                                                try {
                                                    index = n - 1;
                                                } catch (Exception e) {
                                                }
                                                if (index != -1) {
                                                    HashMap<String, Object> vMap = listItems.get(index);
                                                    if (vMap != null) {
                                                        info = (HashMap<String, Object>) vMap.get("info");
                                                    }
                                                }

                                                if (info != null) {
                                                    // 创建信息传输Bundle
                                                    Bundle data = new Bundle();
                                                    // 信息编号
                                                    data.putString("id", (String) info.get("ids"));

                                                    // 创建启动 Activity 的 Intent
                                                    Intent intent = null;
                                                    if (editFlag) {
                                                        if (!baseApp.isReverseRotate) {
                                                            intent = new Intent(classThis, InsJcDescEditLandActivity.class);
                                                        } else {
                                                            intent = new Intent(classThis, InsJcDescEditReverseLandActivity.class);
                                                        }
                                                    } else {
                                                        if (!baseApp.isReverseRotate) {
                                                            intent = new Intent(classThis, InsJcDescShowLandActivity.class);
                                                        } else {
                                                            intent = new Intent(classThis, InsJcDescShowReverseLandActivity.class);
                                                        }
                                                    }
                                                    // 将数据存入 Intent 中
                                                    intent.putExtras(data);
                                                    startActivityForResult(intent, CommonParam.REQUESTCODE_INS_DESC);
                                                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                                }
                                            }
                                        }
                                    });
                                }
                                return true;
                            } else if (view instanceof FrameLayout) {
                                FrameLayout layout = (FrameLayout) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (layout.getId() == R.id.tableRowLayout) {
                                    int n = (int) info.get("V_INFO_SN");
                                    layout.setTag("tableRowLayout_" + n);
                                    // 如果是第一行，要显示上边框
                                    if (n == 1) {
                                        View splitterView_1 = layout.findViewById(R.id.splitterView_1);
                                        splitterView_1.setVisibility(View.VISIBLE);
                                    }
                                } else if (layout.getId() == R.id.info_c1) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t1);
                                    textView.setText("" + info.get("V_INFO_SN"));
                                } else if (layout.getId() == R.id.info_c2) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t2);
                                    textView.setText((String) info.get("c"));
                                    // 这里是为了让表格行高一些，方便点击
                                    ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                                    mp.topMargin = 25;
                                    mp.bottomMargin = 25;
                                    mp.leftMargin = 10;
                                    mp.rightMargin = 10;
                                } else if (layout.getId() == R.id.info_c3) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t3);
                                    // 检查结果
                                    String r = (String) info.get("r");
                                    textView.setText(r);
                                    // 结果索引
                                    int rIndex = getRIndex(r);
                                    textView.setTextColor(getResources().getColor(rColorList.get(rIndex)));
                                    if (editFlag) {
                                        textView.setBackgroundColor(getResources().getColor(R.color.trans_blue_lighter));
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        textView.setBackground(null);
                                        layout.setBackground(null);
                                    }
                                    textView.setTag("info_c3_" + info.get("V_INFO_SN"));
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (editFlag) {
                                                LinearLayout layout = (LinearLayout) v;
                                                TextView textView_r = (TextView) layout.findViewById(R.id.info_t3);
                                                // 检查结果
                                                String rPre = textView_r.getText().toString();
                                                // 下一个检查结果索引
                                                int rIndex = getNextRIndex(rPre);
                                                // 下一个检查结果
                                                String r = rList.get(rIndex);
                                                textView_r.setText(r);
                                                textView_r.setTextColor(getResources().getColor(rColorList.get(rIndex)));

                                                // 设置info_t4值。开始=====================================
                                                LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + layout.getTag());
                                                TextView textView_xqf = (TextView) tableRowLayout.findViewById(R.id.info_t4);
                                                // 消缺否
                                                String xqf = null;
                                                if (getString(R.string.title_jc_result_no).equals(r)) {
                                                    // 异常
                                                    xqf = getString(R.string.title_jc_xq_no);
                                                    textView_xqf.setText(xqf);
                                                    textView_xqf.setTextColor(getResources().getColor(R.color.text_red));
                                                } else {
                                                    // 正常、忽略、未检查
                                                    xqf = getString(R.string.title_jc_xq_blank);
                                                    textView_xqf.setText("");
                                                    textView_xqf.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                                }
                                                // 设置info_t4值。结束=====================================

                                                if (baseApp.isAutoPlayInsAudio) {
                                                    playVoice(rVoiceList.get(rIndex));
                                                }
                                                // 序号
                                                int n = (Integer) layout.getTag();
                                                // 索引
                                                int index = -1;
                                                HashMap<String, Object> info = null;
                                                try {
                                                    index = n - 1;
                                                } catch (Exception e) {
                                                }
                                                if (index != -1) {
                                                    HashMap<String, Object> vMap = listItems.get(index);
                                                    if (vMap != null) {
                                                        info = (HashMap<String, Object>) vMap.get("info");
                                                    }
                                                }
                                                // 行结果Map
                                                HashMap<String, Object> subInfo = insJcSubTableResultMap.get((String) info.get("ids"));
                                                subInfo.put("r", r);
                                                subInfo.put("xqf", xqf);
                                                info.put("r", r);
                                                info.put("xqf", xqf);
                                                // 更新表
                                                new UpdateInsJcSubTableTask().execute((String) info.get("ids"), r, xqf);
                                            }
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c4) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t4);
                                    // 检查结果
                                    String r = (String) info.get("r");
                                    // 消缺否
                                    String xqf = (String) info.get("xqf");
                                    if (getString(R.string.title_jc_result_no).equals(r)) {
                                        // 异常
                                        textView.setText(xqf);
                                        if (getString(R.string.title_jc_xq_yes).equals(xqf)) {
                                            textView.setTextColor(getResources().getColor(R.color.text_green_dark));
                                        } else {
                                            textView.setTextColor(getResources().getColor(R.color.text_red));
                                        }
                                    } else {
                                        // 正常、忽略、未检查
                                        textView.setText("");
                                        textView.setTextColor(getResources().getColor(R.color.list_color_content_font_blue_01));
                                    }
                                    if (editFlag) {
                                        textView.setBackgroundColor(getResources().getColor(R.color.trans_blue_lighter));
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        textView.setBackground(null);
                                        layout.setBackground(null);
                                    }
                                    textView.setTag("info_c4_" + info.get("V_INFO_SN"));
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (editFlag) {
                                                LinearLayout layout = (LinearLayout) v;
                                                LinearLayout tableRowLayout = infoList.findViewWithTag("tableRowLayout_" + layout.getTag());
                                                TextView textView_r = (TextView) tableRowLayout.findViewById(R.id.info_t3);
                                                TextView textView_xqf = (TextView) layout.findViewById(R.id.info_t4);
                                                // 检查结果
                                                String r = textView_r.getText().toString();
                                                // 消缺否
                                                String xqf = textView_xqf.getText().toString();

                                                if (getString(R.string.title_jc_result_no).equals(r)) {
                                                    // 异常
                                                    if (getString(R.string.title_jc_xq_no).equals(xqf)) {
                                                        xqf = xqList.get(0);
                                                        textView_xqf.setTextColor(getResources().getColor(xqColorList.get(0)));
                                                        if (baseApp.isAutoPlayInsAudio) {
                                                            playVoice(xqVoiceList.get(0));
                                                        }
                                                    } else {
                                                        xqf = xqList.get(1);
                                                        textView_xqf.setTextColor(getResources().getColor(xqColorList.get(1)));
                                                        if (baseApp.isAutoPlayInsAudio) {
                                                            playVoice(xqVoiceList.get(1));
                                                        }
                                                    }
                                                    textView_xqf.setText(xqf);

                                                    // 序号
                                                    int n = (Integer) layout.getTag();
                                                    // 索引
                                                    int index = -1;
                                                    HashMap<String, Object> info = null;
                                                    try {
                                                        index = n - 1;
                                                    } catch (Exception e) {
                                                    }
                                                    if (index != -1) {
                                                        HashMap<String, Object> vMap = listItems.get(index);
                                                        if (vMap != null) {
                                                            info = (HashMap<String, Object>) vMap.get("info");
                                                        }
                                                    }
                                                    // 行结果Map
                                                    HashMap<String, Object> subInfo = insJcSubTableResultMap.get((String) info.get("ids"));
                                                    subInfo.put("r", r);
                                                    subInfo.put("xqf", xqf);
                                                    info.put("r", r);
                                                    info.put("xqf", xqf);
                                                    // 更新表
                                                    new UpdateInsJcSubTableTask().execute((String) info.get("ids"), r, xqf);
                                                }
                                            }
                                        }
                                    });
                                } else if (layout.getId() == R.id.info_c5) {
                                    TextView textView = (TextView) layout.findViewById(R.id.info_t5);
                                    ImageView textView_i1 = (ImageView) layout.findViewById(R.id.info_t5i1);
                                    ImageView textView_i2 = (ImageView) layout.findViewById(R.id.info_t5i2);
                                    // 检查描述
                                    String memo = CommonUtil.N2B((String) info.get("memo"));
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
                                    if ((memo_e_array != null && memo_e_array.size() > 0) || CommonUtil.checkNB(memo_d_str)) {
                                        textView_i1.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_i1.setVisibility(View.GONE);
                                    }
                                    // 是否有附件
                                    String infoAtta = (String) info.get("V_INFO_ATTA");
                                    if (CommonParam.YES.equals(infoAtta)) {
                                        textView_i2.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_i2.setVisibility(View.GONE);
                                    }

                                    if (editFlag) {
                                        textView.setBackgroundColor(getResources().getColor(R.color.trans_blue_lighter));
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        textView.setBackground(null);
                                        if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                            layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                        } else {
                                            layout.setBackground(null);
                                        }
                                    }
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImageView textView_i1 = (ImageView) v.findViewById(R.id.info_t5i1);
                                            ImageView textView_i2 = (ImageView) v.findViewById(R.id.info_t5i2);
                                            // 点击后是否打开缺陷描述页面
                                            boolean canClickFlag = false;
                                            if (editFlag) {
                                                canClickFlag = true;
                                            } else {
                                                if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                                    canClickFlag = true;
                                                }
                                            }
                                            if (canClickFlag) {
                                                // 序号
                                                int n = (Integer) layout.getTag();
                                                // 索引
                                                int index = -1;
                                                HashMap<String, Object> info = null;
                                                try {
                                                    index = n - 1;
                                                } catch (Exception e) {
                                                }
                                                if (index != -1) {
                                                    HashMap<String, Object> vMap = listItems.get(index);
                                                    if (vMap != null) {
                                                        info = (HashMap<String, Object>) vMap.get("info");
                                                    }
                                                }

                                                if (info != null) {
                                                    // 创建信息传输Bundle
                                                    Bundle data = new Bundle();
                                                    // 信息编号
                                                    data.putString("id", (String) info.get("ids"));

                                                    // 创建启动 Activity 的 Intent
                                                    Intent intent = null;
                                                    if (editFlag) {
                                                        if (!baseApp.isReverseRotate) {
                                                            intent = new Intent(classThis, InsJcDescEditLandActivity.class);
                                                        } else {
                                                            intent = new Intent(classThis, InsJcDescEditReverseLandActivity.class);
                                                        }
                                                    } else {
                                                        if (!baseApp.isReverseRotate) {
                                                            intent = new Intent(classThis, InsJcDescShowLandActivity.class);
                                                        } else {
                                                            intent = new Intent(classThis, InsJcDescShowReverseLandActivity.class);
                                                        }
                                                    }
                                                    // 将数据存入 Intent 中
                                                    intent.putExtras(data);
                                                    startActivityForResult(intent, CommonParam.REQUESTCODE_INS_DESC);
                                                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                                }
                                            }
                                        }
                                    });
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
         * 新增的信息
         */
        ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();
        /**
         * 总信息数
         */
        private int total;

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
            String dataStr;
            ArrayList<HashMap<String, Object>> jcSubList;

            if (unfinishJcInfo == null) {
                // 新检查
                // 巡视类别
                String ctype = (String) bizInfo.get("ctype");
                if ("2".equals(ctype)) {
                    // 停水检查
                    dataStr = (String) categoryInfo.get("x2");
                } else if ("3".equals(ctype)) {
                    // 特殊检查
                    dataStr = (String) categoryInfo.get("x3");
                } else {
                    // 日常检查
                    dataStr = (String) categoryInfo.get("x1");
                }
                if (CommonUtil.checkNB(dataStr)) {
                    stdList = JSONArray.parseArray(dataStr);
                } else {
                    stdList = new JSONArray();
                }

                for (int index = 0, len = stdList.size(); index < len; index++) {
                    // 存放信息的 Map
                    HashMap<String, Object> listItem = new HashMap<String, Object>();
                    HashMap<String, Object> info = new HashMap<String, Object>();

                    listItem.put("info", info);
                    info.put("V_INFO_SN", index + 1);

                    JSONObject std = stdList.getJSONObject(index);
                    // 检查结果
                    String r = "";
                    // 是否消缺
                    String xqf = "";

                    info.put("c", std.getString("t"));
                    info.put("r", r);
                    info.put("xqf", xqf);
                    info.put("V_INFO_ATTA", CommonParam.NO);

                    listItems_tmp.add(listItem);
                }
            } else {
                // 继续检查
                jcSubList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT * FROM t_szfgs_sgxunsjcjl_son model WHERE model.valid='1' and model.jcjl_id=? and model.quid=? ORDER BY model.xh ASC",
                                new String[]{unfinishJcId, (String) baseApp.getLoginUser().get("ids")});
                stdList = new JSONArray();

                // 处理检查结果。开始=======================================
                for (HashMap<String, Object> o : jcSubList) {
                    // 检查结果
                    String r = (String) o.get("r");
                    // 消缺否
                    String xqf = (String) o.get("xqf");
                    // 是否需要更新信息
                    boolean needUpdateFlag = false;

                    if (getString(R.string.title_jc_result_yes).equals(r) || getString(R.string.title_jc_result_ignore).equals(r) || "".equals(r)) {
                        // 正常、忽略，或者还没有进行检查时
                        if (!getString(R.string.title_jc_xq_blank).equals(xqf)) {
                            xqf = getString(R.string.title_jc_xq_blank);
                            needUpdateFlag = true;
                        }
                    } else if (getString(R.string.title_jc_result_no).equals(r)) {
                        // 异常
                        if (!getString(R.string.title_jc_xq_yes).equals(xqf) && !getString(R.string.title_jc_xq_no).equals(xqf)) {
                            xqf = getString(R.string.title_jc_xq_no);
                            needUpdateFlag = true;
                        }
                    }

                    // 需要更新
                    if (needUpdateFlag) {
                        o.put("xqf", xqf);
                        // 编号
                        String id = (String) o.get("ids");

                        ContentValues cv = new ContentValues();
                        cv.put("xqf", xqf);

                        infoTool.update("t_szfgs_sgxunsjcjl_son", cv, "ids=? and quid=?", new String[]{id, (String) baseApp.getLoginUser().get("ids")});
                    }
                }
                // 处理检查结果。结束=======================================

                for (HashMap<String, Object> o : jcSubList) {
                    JSONObject jo = new JSONObject();
                    jo.put("t", (String) o.get("c"));
                    jo.put("d", JSONArray.parseArray((String) o.get("d")));
                    stdList.add(jo);

                    // 存放信息的 Map
                    HashMap<String, Object> listItem = new HashMap<String, Object>();
                    HashMap<String, Object> info = o;

                    listItem.put("info", info);
                    info.put("V_INFO_SN", Integer.parseInt((String) info.get("xh")));

                    // 是否有附件
                    String infoAtta;
                    // 图片附件 Array
                    JSONArray photoArray = null;
                    // 视频附件 Array
                    JSONArray videoArray = null;
                    // 音频附件 Array
                    JSONArray audioArray = null;
                    // 图片信息
                    String photo = (String) info.get("photo");
                    // 视频信息
                    String video = (String) info.get("video");
                    // 音频信息
                    String audio = (String) info.get("audio");
                    if (CommonUtil.checkNB(photo)) {
                        photoArray = JSONArray.parseArray(photo);
                    }
                    if (CommonUtil.checkNB(video)) {
                        videoArray = JSONArray.parseArray(video);
                    }
                    if (CommonUtil.checkNB(audio)) {
                        audioArray = JSONArray.parseArray(audio);
                    }
                    // 如果至少有一个附件
                    if ((photoArray != null && photoArray.size() > 0) || (videoArray != null && videoArray.size() > 0) || (audioArray != null && audioArray.size() > 0)) {
                        infoAtta = CommonParam.YES;
                    } else {
                        infoAtta = CommonParam.NO;
                    }
                    info.put("V_INFO_ATTA", infoAtta);

                    listItems_tmp.add(listItem);

                    // 行结果Map
                    HashMap<String, Object> subInfo = new HashMap<String, Object>();
                    subInfo.put("r", (String) info.get("r"));
                    subInfo.put("xqf", (String) info.get("xqf"));
                    insJcSubTableResultMap.put((String) info.get("ids"), subInfo);
                }
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
                stdNumTv.setText("(" + stdList.size() + ")");

                if (editFlag) {
                    goBackBtn.setVisibility(View.GONE);
                    createBtn.setVisibility(View.GONE);
                    pauseBtn.setVisibility(View.VISIBLE);
                    finishBtn.setVisibility(View.VISIBLE);
                    listTitleTv.setText(R.string.msg_ins_jc_list_std_ins);
                    startTimeTv.setTextColor(getResources().getColor(R.color.text_green_darker));

                    markTitleTv.setVisibility(View.VISIBLE);
                    markContentLayout.setVisibility(View.VISIBLE);
                    markChooseBtn.setVisibility(View.VISIBLE);
                    markDeleteBtn.setVisibility(View.GONE);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.GONE);
                    if (baseApp.isUhfPda) {
                        markTv.setHint("请扫卡、输入或选择");
                        scanUhfBtn.setVisibility(View.VISIBLE);
                    } else {
                        markTv.setHint("请输入或选择");
                        scanUhfBtn.setVisibility(View.GONE);
                    }
                } else {
                    goBackBtn.setVisibility(View.VISIBLE);
                    createBtn.setVisibility(View.VISIBLE);
                    pauseBtn.setVisibility(View.GONE);
                    finishBtn.setVisibility(View.GONE);
                    listTitleTv.setText(R.string.msg_ins_jc_list_std);
                    startTimeTv.setTextColor(getResources().getColor(R.color.normal_text_color_grey));
                    markTv.setHint("");
                    markChooseBtn.setVisibility(View.GONE);
                    markDeleteBtn.setVisibility(View.GONE);
                    addUhfBtn.setVisibility(View.GONE);
                    editUhfBtn.setVisibility(View.GONE);
                    markTitleTv.setVisibility(View.GONE);
                    markContentLayout.setVisibility(View.GONE);
                }
                if (unfinishJcInfo == null) {
                    // 新检查
                    titleTv.setText((String) resInfo.get("title"));
                    markTitleTv.setVisibility(View.GONE);
                    markContentLayout.setVisibility(View.GONE);
                    markTv.setText("");
                    startTimeIv.setVisibility(View.GONE);
                    startTimeTv.setVisibility(View.GONE);
                } else {
                    // 继续检查
                    titleTv.setText((String) unfinishJcInfo.get("res_title"));
                    markTitleTv.setVisibility(View.VISIBLE);
                    markContentLayout.setVisibility(View.VISIBLE);
                    if (!CommonUtil.checkNB(markTv.getText().toString())) {
                        markTv.setText(CommonUtil.N2B((String) unfinishJcInfo.get("areasign")));
                    }
                    startTimeIv.setVisibility(View.VISIBLE);
                    startTimeTv.setVisibility(View.VISIBLE);
                    startTimeTv.setText(CommonUtil.N2B((String) unfinishJcInfo.get("atime")));
                }

                Log.d("####editFlag", editFlag + ":" + checkMarkDuplicate(markTv.getText().toString()) + ":" + CommonUtil.checkNB(cardMacTv.getText().toString()));
                if (editFlag) {
                    if (checkMarkDuplicate(markTv.getText().toString())) {
                        // 新建
                        addUhfBtn.setVisibility(View.VISIBLE);
                        editUhfBtn.setVisibility(View.GONE);
                    } else {
                        // 编辑
                        if (CommonUtil.checkNB(cardMacTv.getText().toString())) {
                            // 有新卡
                            addUhfBtn.setVisibility(View.GONE);
                            editUhfBtn.setVisibility(View.VISIBLE);
                        } else {
                            if (CommonUtil.checkNB(markTv.getText().toString())) {
                                addUhfBtn.setVisibility(View.GONE);
                                editUhfBtn.setVisibility(View.VISIBLE);
                            } else {
                                addUhfBtn.setVisibility(View.VISIBLE);
                                editUhfBtn.setVisibility(View.GONE);
                            }
                        }

                    }
                }
            } else {
                show("数据加载失败");
            }
            isConnecting = false;
        }
    }

    /**
     * 准备检查数据的 AsyncTask 类
     */
    private class PrepareToJcTask extends AsyncTask<Object, Integer, String> {
        /**
         * 检查信息编号
         */
        private String unfinishJcId_tmp;
        /**
         * 检查记录子表id List
         */
        private ArrayList<String> subIdList = new ArrayList<String>();
        /**
         * 方法
         */
        private String method;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在准备检查数据，请稍候…");
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
            if (unfinishJcInfo == null) {
                // 新检查
                method = CommonParam.METHOD_NEW;
                // 新建信息t_szfgs_sgxunsjcjl。开始=======================================
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

                unfinishJcId_tmp = CommonUtil.getUUID();

                // 键值对
                ContentValues cv = new ContentValues();
                cv.put("ids", unfinishJcId_tmp);
                cv.put("biz_id", (String) bizInfo.get("ids"));
                cv.put("biz_title", (String) bizInfo.get("title"));
                cv.put("res_id", (String) resInfo.get("ids"));
                cv.put("res_title", (String) resInfo.get("title"));
                cv.put("areasign", "");
                cv.put("ctype", (String) bizInfo.get("ctype"));
                cv.put("atime", CommonUtil.getDT());
                cv.put("btime", "");
                cv.put("fzr", fzr_id);
                cv.put("jlr", jlr_id);
                cv.put("user_name", fzr_name + "#" + jlr_name);
                cv.put("attachment", "");
                cv.put("quid", (String) baseApp.getLoginUser().get("ids"));
                cv.put("valid", CommonParam.YES);

                // ★☆
                long insResult = infoTool.insert("t_szfgs_sgxunsjcjl", cv);
                // 新建信息t_szfgs_sgxunsjcjl。结束=======================================
                if (insResult > -1L) {
                    result = CommonParam.RESULT_SUCCESS;
                }

                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    result = CommonParam.RESULT_ERROR;
                    ArrayList<HashMap<String, Object>> jcList = (ArrayList<HashMap<String, Object>>) infoTool
                            .getInfoMapList(
                                    "SELECT * FROM t_szfgs_sgxunsjcjl model WHERE model.valid='1' and model.ids=? and model.quid=?",
                                    new String[]{unfinishJcId_tmp, (String) baseApp.getLoginUser().get("ids")});
                    if (jcList.size() > 0) {
                        unfinishJcInfo = jcList.get(0);
                    }
                    if (unfinishJcInfo != null) {
                        result = CommonParam.RESULT_SUCCESS;
                    }
                }

                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    // 创建子记录
                    result = CommonParam.RESULT_ERROR;
                    boolean saveFlag = true;
                    for (int index = 0, len = stdList.size(); index < len; index++) {
                        JSONObject std = stdList.getJSONObject(index);
                        String subId = CommonUtil.getUUID();
                        subIdList.add(subId);

                        cv.clear();
                        cv.put("ids", subId);
                        cv.put("jcjl_id", unfinishJcId_tmp);
                        cv.put("xh", "" + (index + 1));
                        cv.put("c", CommonUtil.N2B(std.getString("t")));
                        JSONArray d = std.getJSONArray("d");
                        cv.put("d", d == null ? "" : d.toJSONString());
                        cv.put("r", "");
                        cv.put("xqf", "——");
                        cv.put("memo", "");
                        cv.put("attachment", "");
                        cv.put("valid", CommonParam.YES);
                        cv.put("photo", "");
                        cv.put("video", "");
                        cv.put("audio", "");
                        cv.put("quid", (String) baseApp.getLoginUser().get("ids"));

                        // ★☆
                        insResult = infoTool.insert("t_szfgs_sgxunsjcjl_son", cv);
                        // 新建信息t_szfgs_sgxunsjcjl。结束=======================================
                        if (insResult == -1L) {
                            saveFlag = false;
                        }
                    }

                    if (saveFlag) {
                        result = CommonParam.RESULT_SUCCESS;
                    }
                }

                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    unfinishJcId = unfinishJcId_tmp;
                } else {
                    // 删除子记录
                    infoTool.delete("t_szfgs_sgxunsjcjl", "ids=? and quid=?", new String[]{unfinishJcId_tmp, (String) baseApp.getLoginUser().get("ids")});
                    for (String subId : subIdList) {
                        infoTool.delete("t_szfgs_sgxunsjcjl_son", "ids=? and quid=?", new String[]{subId, (String) baseApp.getLoginUser().get("ids")});
                    }
                }
            } else {
                // 继续检查
                method = CommonParam.METHOD_EDIT;
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

            createBtn.setClickable(true);
            createBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                editFlag = true;
                if (!isConnecting) {
                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                }
                new UpdateStartTimeTask().execute();
            } else {
                unfinishJcId = null;
                unfinishJcInfo = null;
                show("信息错误，无法开启检查！");
            }
        }
    }

    /**
     * 获得判断结果的索引
     *
     * @param r {@code String} 提供的结果值
     * @return {@code String} 结果值索引
     */
    public int getRIndex(String r) {
        int index = rList.indexOf(r);
        if (index == -1) {
            index = 3;
        }

        return index;
    }

    /**
     * 获得下一个判断结果的索引
     *
     * @param r {@code String} 提供的结果值
     * @return {@code String} 下一个结果值索引
     */
    public int getNextRIndex(String r) {
        int index = rList.indexOf(r);
        if (index == -1) {
            index = rList.size() - 2;
        }
        if (index == (rList.size() - 1)) {
            index = -1;
        }
        index++;

        return index;
    }

    /**
     * 临时退出巡视 AsyncTask 类
     */
    private class PauseInsTask extends AsyncTask<Object, Integer, String> {
        private String mark;
        private String cardMac;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在保存，请稍候…");
            pauseBtn.setClickable(false);
            pauseBtn.setEnabled(false);

            mark = markTv.getText().toString();
            cardMac = cardMacTv.getText().toString();
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
            ContentValues cv = new ContentValues();
            cv.put("areasign", mark);

            // ★☆
            long insResult = infoTool.update("t_szfgs_sgxunsjcjl", cv, "ids=? and quid=?", new String[]{unfinishJcId, (String) baseApp.getLoginUser().get("ids")});
            if (insResult > -1L) {
                result = CommonParam.RESULT_SUCCESS;
            }

            if (newMarkFlag && CommonUtil.checkNB(mark)) {
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
                goBack();
            } else {
                show("保存失败。请检查表单内容，然后重新保存！");
                pauseBtn.setClickable(true);
                pauseBtn.setEnabled(true);
            }
        }
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
                    markTv.setHint("请扫卡、输入或选择");
                    areaSignInfo = info;
                    newMarkFlag = false;
                    markContentLayout.setBackgroundResource(R.drawable.border_trans_blue_grey);
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
     * 更新水工巡视检查记录子表 AsyncTask 类
     */
    private class UpdateInsJcSubTableTask extends AsyncTask<Object, Integer, String> {
        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            //makeWaitDialog();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 子表信息编号
            String id = (String) params[0];
            // 检查结果
            String r = (String) params[1];
            // 消缺否
            String xqf = (String) params[2];

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            ContentValues cv = new ContentValues();
            cv.put("r", r);
            cv.put("xqf", xqf);

            infoTool.update("t_szfgs_sgxunsjcjl_son", cv, "ids=? and quid=?", new String[]{id, (String) baseApp.getLoginUser().get("ids")});
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
            //unWait();
        }
    }

    /**
     * 检查是否可以结束巡视 AsyncTask 类
     */
    private class CheckFinishTask extends AsyncTask<Object, Integer, String> {
        /**
         * 出错序号
         */
        private int errorNum = 0;
        /**
         * 出错内容
         */
        private String errorC;

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
            for (int i = 0, len = listItems.size(); i < len; i++) {
                HashMap<String, Object> vMap = listItems.get(i);
                HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");
                // 行结果Map
                HashMap<String, Object> subInfo = insJcSubTableResultMap.get((String) info.get("ids"));
                // 检查结果
                String r = (String) subInfo.get("r");
                // 消缺否
                String xqf = (String) subInfo.get("xqf");
                if ("".equals(r)) {
                    // 未判定
                    errorNum = i + 1;
                    errorC = (String) info.get("c");
                    break;
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

            finishBtn.setClickable(true);
            finishBtn.setEnabled(true);
            if (errorNum == 0) {
                // 内容正常
                makeFinishInsDialog();
            } else {
                // 有错误内容
                makeAlertDialog("【" + errorNum + ". " + errorC + "】\n\n没有进行判定！");
            }
        }
    }

    /**
     * 显示巡检完成对话框
     */
    public void makeFinishInsDialog() {
        Builder dlgBuilder = new Builder(this);
        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_ins_jc_finish, null);
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
                new FinishInsTask().execute();
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
     * 巡检完成 AsyncTask 类
     */
    private class FinishInsTask extends AsyncTask<Object, Integer, String> {
        private String mark;
        private String cardMac;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog();

            mark = markTv.getText().toString();
            cardMac = cardMacTv.getText().toString();
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
            ContentValues cv = new ContentValues();
            cv.put("btime", CommonUtil.getDT());
            cv.put("areasign", mark);

            // ★☆
            long insResult = infoTool.update("t_szfgs_sgxunsjcjl", cv, "ids=? and quid=?", new String[]{unfinishJcId, (String) baseApp.getLoginUser().get("ids")});
            if (insResult > -1L) {
                result = CommonParam.RESULT_SUCCESS;
            }

            if (newMarkFlag && CommonUtil.checkNB(mark)) {
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
                if (baseApp.isAutoPlayInsAudio) {
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

                            // 声音响完后关闭本窗口
                            setResult(CommonParam.RESULTCODE_REFRESH_REC_LIST);
                            goBack();
                        }
                    });
                    show(getString(R.string.alert_inspect_over, (String) resInfo.get("title")));
                    playVoice("laba1.wav");
                } else {
                    // 声音响完后关闭本窗口
                    setResult(CommonParam.RESULTCODE_REFRESH_REC_LIST);
                    goBack();
                }
            } else {
                show("保存失败。请检查表单内容，然后重新保存！");
            }
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
            String realatime = infoTool.getSingleVal("SELECT model.realatime FROM t_biz_sgxuns model WHERE model.valid='1' and model.ids=? and model.quid=?",
                    new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            if (!CommonUtil.checkNB(realatime)) {
                // 已经没有设置开始时间，就需要设置
                ContentValues cv = new ContentValues();
                cv.put("realatime", CommonUtil.getDT());

                infoTool.update("t_biz_sgxuns", cv, "ids=? and quid=?", new String[]{(String) bizInfo.get("ids"), (String) baseApp.getLoginUser().get("ids")});
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
        }
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
                    markTv.setHint("请扫卡、输入或选择");
                    newMarkFlag = false;
                    markContentLayout.setBackgroundResource(R.drawable.border_trans_blue_grey);
                    addUhfBtn.setVisibility(View.VISIBLE);
                    editUhfBtn.setVisibility(View.GONE);
                    markChooseBtn.setVisibility(View.VISIBLE);
                    markDeleteBtn.setVisibility(View.GONE);
                } else {
                    markTv.setText("");
                    markTv.setHint("请输入区域名称");
                    newMarkFlag = true;
                    markContentLayout.setBackgroundResource(R.drawable.border_trans_blue_green);
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
                    markContentLayout.setBackgroundResource(R.drawable.border_trans_blue_green);
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
                    markContentLayout.setBackgroundResource(R.drawable.border_trans_blue_green);
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

    /**
     * 检查区域名称是否重复
     *
     * @param mark {@code String} mark值
     *
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
        contentView = (LinearLayout) findViewById(R.id.contentView);
        titleBarModeName = (TextView) findViewById(R.id.title_type_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        createBtn = (Button) findViewById(R.id.createBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        titleTv = (TextView) findViewById(R.id.titleTv);
        markTitleTv = (TextView) findViewById(R.id.markTitleTv);
        markContentLayout = (LinearLayout) findViewById(R.id.markContentLayout);
        markTv = (TextView) findViewById(R.id.markTv);
        cardMacTv = (TextView) findViewById(R.id.cardMacTv);
        markChooseBtn = (ImageButton) findViewById(R.id.markChooseBtn);
        markDeleteBtn = (ImageButton) findViewById(R.id.markDeleteBtn);
        addUhfBtn = (ImageButton) findViewById(R.id.addUhfBtn);
        editUhfBtn = (ImageButton) findViewById(R.id.editUhfBtn);
        scanUhfBtn = (ImageButton) findViewById(R.id.scanUhfBtn);
        stdNumTv = (TextView) findViewById(R.id.stdNumTv);
        startTimeIv = (ImageView) findViewById(R.id.startTimeIv);
        startTimeTv = (TextView) findViewById(R.id.startTimeTv);
        infoList = (ListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
