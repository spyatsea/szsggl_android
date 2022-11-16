/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsAutoDkListAdapter;
import com.cox.android.uhf.Reader;
import com.cox.android.utils.SnackbarUtil;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.rfid.InventoryTagMap;
import com.rfid.PowerUtil;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.forward.androids.views.STextView;

/**
 * 扫卡快签_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsAutoDkListActivity extends DbActivity {
    // 常量。开始===============================
    /**
     * 读UHF卡类型：定位
     */
    private static final String SCAN_UHF_CARD_TYPE_LOC = "loc";
    /**
     * 读UHF卡类型：采集
     */
    private static final String SCAN_UHF_CARD_TYPE_ADD = "add";
    /**
     * 信息处理方式：不处理
     */
    public static final int INFO_PROCESS_TYPE_NO_ACTION = 0;
    /**
     * 信息处理方式：待增加
     */
    public static final int INFO_PROCESS_TYPE_ADD = 1;
    // 常量。结束===============================
    /**
     * 主界面
     */
    LinearLayout contentView;
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
    /**
     * 帮助按钮
     */
    ImageButton helpBtn;
    /**
     * 列表名称区
     */
    LinearLayout listTitleLayout;
    /**
     * 列表名称
     */
    TextView listTitleTv;

    // 界面相关参数。开始===============================
    private LinearLayout navNumContainer;
    private TextView currentNumTv;
    private TextView cardTitleTv;
    private TextView cardMacTv;
    private ImageButton addBtn;
    /**
     * 返回
     */
    private Button goBackBtn;
    // 界面相关参数。结束===============================
    /**
     * 任务信息
     */
    private HashMap<String, Object> bizInfo;
    /**
     * 资源信息
     */
    private HashMap<String, Object> resInfo;
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
    private InsAutoDkListAdapter infoListAdapter;
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 检查EPC AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> checkEpcTask;
    /**
     * 保存信息 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> submitTask;
    /**
     * 每页大小
     */
    int ROWS_PER_PAGE = CommonParam.RESULT_LIST_PER;
    /**
     * 当前的区域标识信息
     */
    private HashMap<String, Object> areaSignInfo;
    /**
     * 信息处理标志，表明是待处理还是不做处理
     */
    int infoProcessType = INFO_PROCESS_TYPE_NO_ACTION;
    /**
     * 经纬度
     */
    private String lngLat;
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    /**
     * 是否第一次打开
     */
    boolean isFirstStart = true;
    /**
     * 区域信息保留Timer
     */
    private Timer cardInfoStayTimer;
    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 10:
                    startScanUhf();
                    break;
                case 11:
                    stopScanUhf();
                    break;
                case 20:
                    infoProcessType = INFO_PROCESS_TYPE_NO_ACTION;
                    areaSignInfo = null;
                    cardTitleTv.setText("");
                    cardMacTv.setText("－－");
                    addBtn.setVisibility(View.INVISIBLE);
                    break;
                case 30:
                    notice_ani_out = AnimationUtils.loadAnimation(InsAutoDkListActivity.this, android.R.anim.fade_out);
                    notice_ani_out.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            noticeTv.setVisibility(View.INVISIBLE);
                            notice_ani_out = null;
                        }
                    });
                    // 运行动画
                    noticeTv.startAnimation(notice_ani_out);
                    break;
                default:
                    break;
            }
        }
    };

    // 网络连接相关参数。开始==========================================
    // 网络连接相关参数。结束==========================================

    // 提示信息参数。开始==========================================
    /**
     * 提示信息
     * */
    private TextView noticeTv;
    private SpringAnimation notice_springAni;
    private DynamicAnimation.OnAnimationEndListener noteice_springAni_onAnimationEndListener;
    private Animation notice_ani_in;
    private Animation notice_ani_out;
    private float notice_ani_x = -1.0F, notice_ani_y = -1.0F;
    private Timer notice_aniTimer;
    private int[] NOTICE_COLOR = new int[] {R.color.text_color_orange_1, R.color.text_green, R.color.text_blue_light, R.color.text_color_yellow, R.color.text_pink};
    // 提示信息参数。结束==========================================

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
    /**
     * UHF检查是否结束
     */
    private boolean isCheckingEpc = false;
    /**
     * UHF保存是否结束
     */
    private boolean isSavingEpc = false;
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

        setContentView(R.layout.ins_autodk_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");
        resInfo = (HashMap<String, Object>) data.getSerializable("resInfo");

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("扫卡快签");

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
        helpBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeHelpDialog(R.layout.dlg_help_autodk_list);
                TextView info_tv1 = (TextView) helpDlg.findViewById(R.id.info_tv1);
                TextView info_tv2 = (TextView) helpDlg.findViewById(R.id.info_tv2);
                info_tv1.setText(getString(R.string.text_scan_uhf_card_info_autodk_overtime, baseApp.autoDkOverTime, getString(R.string.autoDkOverTimeTitle).replace("（秒）", "")));
                info_tv2.setText(getString(R.string.text_scan_uhf_card_info_autodk_staytime, baseApp.autoDkStayTime, getString(R.string.autoDkStayTimeTitle).replace("（秒）", "")));
            }
        });
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoProcessType == INFO_PROCESS_TYPE_ADD && areaSignInfo != null && !isSavingEpc) {
                    addBtn.setClickable(false);
                    addBtn.setEnabled(false);
                    checkEpcTask.cancel(true);
                    isCheckingEpc = true;
                    submitTask = new SubmitTask().execute();
                }
            }
        });
        navNumContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanUhf();
                if (baseApp.uhfPdaInfo.size() > 0) {
                    makeSetUhfPowerDialog();
                }
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
                                Log.d("##@x", "UHF_MSG_UPDATE_LISTVIEW");
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
                                Log.d("##@x", "UHF_MSG_UPDATE_TIME");
                                //long endTime = System.currentTimeMillis();
                                break;
                            case CommonParam.UHF_MSG_UPDATE_ERROR:
                                Log.d("##@x", "UHF_MSG_UPDATE_ERROR");
                                break;
                            case CommonParam.UHF_MSG_UPDATE_STOP:
                                Log.d("##@x", "UHF_MSG_UPDATE_STOP");
                                // 当前状态：已停止；下一状态：启动
                                isUhfScanStop = true;
                                setUhfScanFieldStart();
                                break;
                            case CommonParam.UHF_MSG_UPDATE_EPC:
                                Log.d("##@x", "UHF_MSG_UPDATE_EPC");
                                // show((String) msg.obj);
                                Log.d("###epc", "#" + (String) msg.obj);
                                Log.d("###", isUhfActive + "" + isCheckingEpc + isSavingEpc);
                                if (isUhfActive && !isCheckingEpc && !isSavingEpc) {
                                    checkEpcTask = new CheckUhfCardTask().execute((String) msg.obj);
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
        StatusBarUtil.setStatusBarMode(this, false, R.color.cert_border_blue);
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
        if (listItems.size() > 0) {
            setResult(CommonParam.RESULTCODE_NEW_REC);
        }
        super.goBack();
    }

    @Override
    public void onResume() {
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            isUhfStopScanThread = false;
            if (!isFirstStart) {
                startScanUhf();
            }
        }
        // UHF相关事件与方法。结束============================
        super.onResume();
    }

    @Override
    public void onPause() {
        if (cardInfoStayTimer != null) {
            cardInfoStayTimer.cancel();
            cardInfoStayTimer = null;
        }
        if (checkEpcTask != null) {
            checkEpcTask.cancel(true);
        }

        // 提示信息相关事件与方法。开始============================
        if (notice_aniTimer != null) {
            notice_aniTimer.cancel();
            notice_aniTimer = null;
        }
        if (notice_ani_in != null) {
            notice_ani_in.cancel();
            notice_ani_in = null;
        }
        if (notice_ani_out != null) {
            notice_ani_out.cancel();
            notice_ani_out = null;
        }
        if (notice_springAni != null) {
            if (noteice_springAni_onAnimationEndListener != null) {
                notice_springAni.removeEndListener(noteice_springAni_onAnimationEndListener);
                noteice_springAni_onAnimationEndListener = null;
            }
            notice_springAni.cancel();
            notice_springAni = null;
        }
        if (noticeTv.getVisibility() == View.VISIBLE) {
            noticeTv.setVisibility(View.INVISIBLE);
        }
        // 提示信息相关事件与方法。结束============================

        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            if (isUhfScaning) {
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
            // disconnectUhf(false);
            // PowerUtil.power("0");
        }
        // UHF相关事件与方法。结束============================
        if (mainTask != null) {
            mainTask.cancel(true);
        }
        if (checkEpcTask != null) {
            checkEpcTask.cancel(true);
        }
        if (submitTask != null) {
            submitTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        } else if (keyCode == baseApp.uhfKeyCode) {
            Log.d("###307", "###########" + infoProcessType + (String) bizInfo.get("ids") + ":" + (String) resInfo.get("ids"));
            if (infoProcessType == INFO_PROCESS_TYPE_ADD && areaSignInfo != null && !isSavingEpc) {
                checkEpcTask.cancel(true);
                isCheckingEpc = true;
                submitTask = new SubmitTask().execute();
            }
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
         * 进度常量：开始扫描
         */
        private static final int PROGRESS_SCAN_START = 1003;
        /**
         * 进度常量：停止扫描
         */
        private static final int PROGRESS_SCAN_STOP = 1004;

        /**
         * 信息数量
         */
        int infoCount = 0;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在开启UHF模块，请稍候…");
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
            // 处理数据。结束============================================================================

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);
            // 生成信息列表
            publishProgress(PROGRESS_MAKE_LIST);

            // UHF相关事件与方法。开始============================
            if (baseApp.isUhfPda) {
                initUHFSound();
                // 如果没有连接UHF模块，需要先连接
                int tryCount = 10;
                for (int i = 0; i < tryCount; i++) {
                    Log.d("###IsConnected", "" + Reader.rrlib.IsConnected());
                    if (!Reader.rrlib.IsConnected()) {
                        connectUhf(false);
                        doWait(500);
                        Log.d("#x1", i + "###########");
                        getUhfInfo();
                    } else {
                        break;
                    }
                }
                doWait(1000);
                publishProgress(PROGRESS_SCAN_START);
            }
            // UHF相关事件与方法。结束============================

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
                homeBtn.setVisibility(View.GONE);
                listTitleLayout.setVisibility(View.VISIBLE);
                listTitleTv.setText("" + listItems.size());
                currentNumTv.setText("" + baseApp.uhfPower);

                ImageView animate_iv = (ImageView) findViewById(R.id.animate_iv);
                AnimationDrawable animate_ad = (AnimationDrawable) animate_iv.getBackground();
                animate_ad.start();
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (InsAutoDkListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoListAdapter = new InsAutoDkListAdapter(getApplicationContext(), listItems, R.layout.ins_autodk_list_item,
                            new String[]{"info", "info", "info", "info"}, new int[]{R.id.infoName,
                            R.id.info_c1, R.id.info_c2, R.id.info_c3});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new InsAutoDkListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.infoName) {
                                    textView.setText(CommonUtil.N2B((String) info.get("areasign")));
                                } else if (textView.getId() == R.id.info_c1) {
                                    textView.setText("卡　　号：" + CommonUtil.N2B((String) info.get("_loc")));
                                } else if (textView.getId() == R.id.info_c2) {
                                    textView.setText("签到时间：" + CommonUtil.N2B((String) info.get("ctime")));
                                } else if (textView.getId() == R.id.info_c3) {
                                    textView.setText("记录人员：" + CommonUtil.N2B((String) info.get("_uname")));
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

                    // 设置列表项点击事件
                    infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    });
                }
            } else if (progress[0] == PROGRESS_SCAN_START) {
                // 开始扫描
                startScanUhf();
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
            isFirstStart = false;
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                infoListAdapter.notifyDataSetChanged();
            } else {
                show("信息错误！");
                goBack();
            }
        }
    }

    /**
     * 检查UHF卡片编号 AsyncTask 类
     */
    private class CheckUhfCardTask extends AsyncTask<Object, Integer, String> {
        /**
         * 卡编号
         */
        private String cardMac;
        /**
         * 是否需要新增打卡信息
         */
        private boolean needAdd;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            //makeWaitDialog();

            // infoProcessType = INFO_PROCESS_TYPE_NO_ACTION;
            // areaSignInfo = null;
            isCheckingEpc = true;
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
            // 查询该卡片是否绑定到本巡视点的区域信息中
            //Log.d("###sql", (String) resInfo.get("ids"));
            List<HashMap<String, Object>> areaSignList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                    "select * from t_szfgs_sgresareasign model where model.valid='1' and model.loc=? and model.rid=? order by CAST(model.n as int) desc limit 1", new String[]{cardMac, (String) resInfo.get("ids")});
            //Log.d("###size1", "#" + areaSignList.size());
            if (areaSignList.size() > 0) {
                // 有区域信息
                HashMap<String, Object> _areaSignInfo = areaSignList.get(0);
                String areaSignTitle = (String) _areaSignInfo.get("title");
                // 签到数据表中是否有该卡片的签到记录
                //Log.d("###sql", (String) bizInfo.get("ids") + ":" + (String) resInfo.get("ids") + ":" + areaSignTitle);
                List<HashMap<String, Object>> dkList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                        "select * from t_szfgs_sgxunsqdjl model where model.valid='1' and model.biz_id=? and model.res_id=? and model.areasign=? order by DATETIME(model.ctime) DESC LIMIT 1", new String[]{(String) bizInfo.get("ids"), (String) resInfo.get("ids"), areaSignTitle});
                //Log.d("###size3", "#" + dkList.size());
                if (dkList.size() > 0) {
                    // 有打卡记录，需要先判断超时时间
                    HashMap<String, Object> latestDkInfo = dkList.get(0);
                    // 签到时间
                    String ctime = (String) latestDkInfo.get("ctime");
                    SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    Date cdate = null;
                    try {
                        cdate = dateTimeFmt.parse(ctime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (cdate != null) {
                        Date now = new Date();
                        long timeLong = now.getTime() - cdate.getTime();
                        if (timeLong > baseApp.autoDkOverTime * 1000) {
                            // 需要新增
                            needAdd = true;
                            areaSignInfo = _areaSignInfo;
                        }
                    }
                } else {
                    // 无打开记录，直接新增
                    needAdd = true;
                    areaSignInfo = _areaSignInfo;
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
            //unWait();

            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (needAdd) {
                    infoProcessType = INFO_PROCESS_TYPE_ADD;
                    cardTitleTv.setText((String) areaSignInfo.get("title"));
                    cardMacTv.setText(cardMac);
                    addBtn.setVisibility(View.VISIBLE);

                    if (cardInfoStayTimer != null) {
                        cardInfoStayTimer.cancel();
                    }
                    cardInfoStayTimer = new Timer();
                    cardInfoStayTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            pageHandler.sendEmptyMessage(20);
                        }
                    }, baseApp.autoDkStayTime * 1000);
                }
            }

            isCheckingEpc = false;
        }
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {
        /**
         * 打卡信息
         */
        private HashMap<String, Object> dkInfo;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            // makeWaitDialog("正在保存，请稍候…");
            isSavingEpc = true;
            isCheckingEpc = true;
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
            user_name = fzr_name + "#" + jlr_name;

            // 打卡信息编号
            String dkInfoId = CommonUtil.getUUID();

            // 键值对
            ContentValues cv = new ContentValues();
            cv.put("ids", dkInfoId);
            cv.put("ctime", CommonUtil.getDT());
            cv.put("biz_id", (String) bizInfo.get("ids"));
            cv.put("biz_title", (String) bizInfo.get("title"));
            cv.put("res_id", (String) resInfo.get("ids"));
            cv.put("res_title", (String) resInfo.get("title"));
            cv.put("areasign", (String) areaSignInfo.get("title"));
            cv.put("jingwd", longitude_baidu + "," + latitude_baidu);
            cv.put("fzr", CommonUtil.N2B((String) bizInfo.get("fzr")));
            cv.put("jlr", CommonUtil.N2B((String) baseApp.loginUser.get("ids")));
            cv.put("user_name", user_name);
            cv.put("valid", CommonParam.YES);
            cv.put("attachment", "[]");
            cv.put("photo", "[]");
            cv.put("video", "[]");
            cv.put("audio", "[]");
            cv.put("quid", (String) baseApp.getLoginUser().get("ids"));

            // ★☆
            long insResult = infoTool.insert("t_szfgs_sgxunsqdjl", cv);
            if (insResult > -1L) {
                dkInfo = CommonUtil.cvToMap(cv);
                result = CommonParam.RESULT_SUCCESS;
            }

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
            addBtn.setClickable(true);
            addBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                // 存放信息的 Map
                HashMap<String, Object> listItem = new HashMap<String, Object>();
                HashMap<String, Object> info = new HashMap<String, Object>();
                info.put("areasign", areaSignInfo.get("title"));
                info.put("_loc", areaSignInfo.get("loc"));
                info.put("ctime", dkInfo.get("ctime"));
                info.put("_uname", baseApp.loginUser.get("realname"));
                listItem.put("info", info);
                listItems.add(0, listItem);
                infoListAdapter.notifyDataSetChanged();
                listTitleTv.setText("" + listItems.size());

                infoProcessType = INFO_PROCESS_TYPE_NO_ACTION;
                areaSignInfo = null;
                cardTitleTv.setText("");
                cardMacTv.setText("－－");
                addBtn.setVisibility(View.INVISIBLE);

                isSavingEpc = false;
                isCheckingEpc = false;

                if (cardInfoStayTimer != null) {
                    cardInfoStayTimer.cancel();
                    cardInfoStayTimer = null;
                }

                // show("信息保存成功！");
                SnackbarUtil.ShortSnackbar(contentView, "信息保存成功！", SnackbarUtil.Confirm).show();

                // 显示提示信息。开始=====================================
                if (notice_aniTimer != null) {
                    notice_aniTimer.cancel();
                    notice_aniTimer = null;
                }
                if (notice_ani_in != null) {
                    notice_ani_in.cancel();
                    notice_ani_in = null;
                }
                if (notice_ani_out != null) {
                    notice_ani_out.cancel();
                    notice_ani_out = null;
                }
                if (notice_springAni != null) {
                    if (noteice_springAni_onAnimationEndListener != null) {
                        notice_springAni.removeEndListener(noteice_springAni_onAnimationEndListener);
                        noteice_springAni_onAnimationEndListener = null;
                    }
                    notice_springAni.cancel();
                    notice_springAni = null;
                }
                if (noticeTv.getVisibility() == View.VISIBLE) {
                    noticeTv.setVisibility(View.INVISIBLE);
                }

                notice_ani_in = AnimationUtils.loadAnimation(InsAutoDkListActivity.this, android.R.anim.fade_in);
                notice_ani_in.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        notice_springAni = new SpringAnimation(noticeTv, DynamicAnimation.TRANSLATION_Y, 0);
                        notice_springAni.addEndListener(noteice_springAni_onAnimationEndListener = new DynamicAnimation.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                notice_springAni = null;

                                notice_aniTimer = new Timer();
                                notice_aniTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        pageHandler.sendEmptyMessage(30);
                                    }
                                }, 100);
                            }
                        });
                        notice_springAni.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                        notice_springAni.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);
                        notice_springAni.animateToFinalPosition(-1 * screenHeight * 3 / 5);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        notice_ani_in = null;
                    }
                });
                noticeTv.setTextColor(getResources().getColor(NOTICE_COLOR[(int) (Math.random() * 100.0D) % NOTICE_COLOR.length]));
                noticeTv.setVisibility(View.VISIBLE);
                if (notice_ani_x == -1.0F) {
                    notice_ani_x = noticeTv.getX();
                    notice_ani_y = noticeTv.getY();
                } else {
                    noticeTv.setX(notice_ani_x);
                    noticeTv.setY(notice_ani_y);
                }
                // 运行动画
                noticeTv.startAnimation(notice_ani_in);
                // 显示提示信息。结束=====================================
            }
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
//        uhfSoundMap.put(0, uhfSoundPool.load(this, R.raw.blank, 1));
        uhfSoundMap.put(0, uhfSoundPool.load(this, R.raw.barcodebeep, 1));
        uhfSoundMap.put(1, uhfSoundPool.load(this, R.raw.readcard, 1));
        uhfSoundMap.put(2, uhfSoundPool.load(this, R.raw.gun, 1));
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
                int result_string_int = CommonUtil.getFieldValue("string", result_hex, InsAutoDkListActivity.this);
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
                    int result_string_int = CommonUtil.getFieldValue("string", result_hex, InsAutoDkListActivity.this);
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
        }
    }

    /**
     * 设置扫描相关字段状态：正在扫描
     */
    private void setUhfScanFieldScanning() {
        if (readUhfCardType == CommonParam.READ_UHF_CARD_TYPE_INSPECT) {

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
            Log.d("#x2", "###########");
            //show("功率修改成功");
            SnackbarUtil.ShortSnackbar(contentView, "功率修改成功", SnackbarUtil.Info).show();
        } else {
            //show("功率修改失败");
            SnackbarUtil.ShortSnackbar(contentView, "功率修改失败", SnackbarUtil.Info).show();
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
                try {
                    setUhfPower(currentNum);
                    Reader.rrlib.SetRfPower(currentNum);
                    getUhfInfo();
                    Log.d("#x3", "###########");
                    baseApp.uhfPower = (int) baseApp.uhfPdaInfo.get("power");
                    preferEditor.putInt("uhfPower", baseApp.uhfPower);
                    preferEditor.commit();
                    currentNumTv.setText("" + baseApp.uhfPower);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        uhfPowerDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startScanUhf();
            }
        });
    }
    // UHF相关事件与方法。结束========================================================


    /**
     * 查找view
     */
    public void findViews() {
        contentView = (LinearLayout) findViewById(R.id.contentView);
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        helpBtn = (ImageButton) findViewById(R.id.helpBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        noticeTv = (TextView) findViewById(R.id.noticeTv);
        // 界面相关参数。开始===============================
        navNumContainer = (LinearLayout) findViewById(R.id.navNumContainer);
        currentNumTv = (TextView) findViewById(R.id.currentNumTv);
        cardTitleTv = (TextView) findViewById(R.id.cardTitleTv);
        cardMacTv = (TextView) findViewById(R.id.cardMacTv);
        addBtn = (ImageButton) findViewById(R.id.addBtn);
        infoList = (ListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
