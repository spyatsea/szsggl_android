/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.uhf.Reader;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.rfid.InventoryTagMap;
import com.rfid.PowerUtil;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * UHF_测试页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class UhfTestActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 主界面
     */
    ScrollView contentView;
    // 界面相关参数。开始===============================
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
    private TextView titleTv;
    private TextView memoTv;
    private TextView c1;
    private TextView c2;
    private EditText c3;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button scanBtn;
    private ImageView picIv;

    private TextView noticeTv;
    private SpringAnimation notice_springAni;
    private DynamicAnimation.OnAnimationEndListener noteice_springAni_onAnimationEndListener;
    private Animation notice_ani_in;
    private Animation notice_ani_out;
    float notice_ani_x = -1.0F, notice_ani_y = -1.0F;
    private Timer notice_aniTimer;
    private int[] NOTICE_COLOR = new int[] {R.color.text_color_orange_1, R.color.text_green, R.color.text_blue_light, R.color.text_color_yellow, R.color.text_pink};
    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 30:
                    notice_ani_out = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_out);
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
    // 界面相关参数。结束===============================

    // UHF参数。开始============================
    /**
     * 是否正在扫描
     */
    private boolean isUhfScaning = false;
    long uhfBeginTime = 0;
    private Timer uhfScanTimer;
    public Map<String, Integer> dtIndexMap = new LinkedHashMap<String, Integer>();
    private List<InventoryTagMap> uhfData = new ArrayList<InventoryTagMap>();
    private Handler uhfHandler;
    public boolean isUhfStopScanThread = false;
    public static int UhfErrorCount;
    public static int UhfErrorCRC;
    /**
     * 功率Dialog
     */
    private AlertDialog uhfPowerDlg;
    int i = 1;
    // UHF参数。结束============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = UhfTestActivity.this;

//        // 获取Intent
//        Intent intent = getIntent();
//        // 获取Intent上携带的数据
//        Bundle data = intent.getExtras();

        setContentView(R.layout.uhf_test);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("UHF测试");

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

        // UHF相关事件与方法。开始============================
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUhf(true);
                Log.d("###map", JSONObject.toJSONString(getUhfInfo()));
            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectUhf(true);
            }
        });
        btn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("##Connected", "" + Reader.rrlib.IsConnected());
            }
        });
        btn4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setUhfPower(Integer.parseInt(c3.getText().toString()));
            }
        });
        btn5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uhfSoundKey++;
                uhfSoundKey = uhfSoundKey % 3;
                Reader.rrlib.setsoundid(uhfSoundMap.get(uhfSoundKey), uhfSoundPool);
            }
        });
        btn6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.isUhfPda) {
                    makeSetUhfPowerDialog();
                } else {
                    makeSetUhfPowerDialogX();
                }
            }
        });
        btn7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeTv.setX(notice_ani_x);
                noticeTv.setY(notice_ani_y);
//                // 创建启动 Activity 的 Intent
//                Intent intent = new Intent(classThis, InsAutoDkListActivity.class);
//                // 信息传输Bundle
//                // Bundle data = new Bundle();
//                // data.putString("fromFlag", "main");
//                // // 将数据存入Intent中
//                // intent.putExtras(data);
//                startActivity(intent);
//                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });
        btn8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                final SpringAnimation anim = new SpringAnimation(picIv, DynamicAnimation.TRANSLATION_Y, 0);
//                anim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
//                    @Override
//                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
//
//                    }
//                });
//                anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
//                anim.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM);
//                anim.animateToFinalPosition(-500);
                i++;

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

                notice_ani_in = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_in);
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
            }
        });
        scanBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUhfScaning) {
                    startScanUhf();
                } else {
                    stopScanUhf();
                }
            }
        });
        if (baseApp.isUhfPda) {
            uhfHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {

                    try {
                        switch (msg.what) {
                            case CommonParam.UHF_MSG_UPDATE_LISTVIEW:
                                uhfData = Reader.rrlib.getInventoryTagMapList();
                                c1.setText(String.valueOf(uhfData.size()));
                                if (uhfData.size() > 0) {
                                    StringBuffer sb = new StringBuffer();
                                    for (InventoryTagMap m : uhfData) {
                                        sb.append(m.strEPC).append("\n");
                                    }
                                    memoTv.setText(sb.toString());
                                }
                                break;
                            case CommonParam.UHF_MSG_UPDATE_TIME:
                                long endTime = System.currentTimeMillis();
                                c2.setText(String.valueOf(endTime - uhfBeginTime));
                                break;
                            case CommonParam.UHF_MSG_UPDATE_ERROR:
                                break;
                            case CommonParam.UHF_MSG_UPDATE_STOP:
                                setScanButtonStart();
                                break;
                            case CommonParam.UHF_MSG_UPDATE_EPC:
                                titleTv.setText((String) msg.obj);
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
        new MainTask().execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            isUhfStopScanThread = false;
        }
        // UHF相关事件与方法。结束============================
    }

    @Override
    public void onPause() {
        super.onPause();

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
            stopScanUhf();
        }
        // UHF相关事件与方法。结束============================
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // UHF相关事件与方法。开始============================
        if (baseApp.isUhfPda) {
            disconnectUhf(true);
            PowerUtil.power("0");
        }
        // UHF相关事件与方法。结束============================
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        } else if (keyCode == baseApp.uhfKeyCode) {
            if (baseApp.isUhfPda) {
                if (isUhfScaning) {
                    stopScanUhf();
                } else {
                    startScanUhf();
                }
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
                show("信息错误！");
                goBack();
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
        uhfSoundMap.put(0, uhfSoundPool.load(this, R.raw.barcodebeep, 1));
        uhfSoundMap.put(1, uhfSoundPool.load(this, R.raw.readcard, 1));
        uhfSoundMap.put(2, uhfSoundPool.load(this, R.raw.gun, 1));
        uhfAm = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        // 实例化AudioManager对象
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
                    setScanButtonStop();
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
        Reader.rrlib.StopRead();
        isUhfScaning = false;
        if (uhfScanTimer != null) {
            uhfScanTimer.cancel();
            uhfScanTimer = null;
            setScanButtonStopping();
        }
    }

    /**
     * 设置扫描按钮状态：开始扫描
     */
    private void setScanButtonStart() {
        scanBtn.setText(R.string.title_card_scan_start);
        scanBtn.setBackgroundResource(R.drawable.custom_btn_card_green);
    }

    /**
     * 设置扫描按钮状态：正在停止
     */
    private void setScanButtonStopping() {
        scanBtn.setText(R.string.title_card_scan_stopping);
    }

    /**
     * 设置扫描按钮状态：停止扫描
     */
    private void setScanButtonStop() {
        scanBtn.setText(R.string.title_card_scan_stop);
        scanBtn.setBackgroundResource(R.drawable.custom_btn_card_red);
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
                show("" + currentNum);
//                // 功率按钮
//                Button powerBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEUTRAL);
//                powerBtn.setText("功率：" + currentNum);
                setUhfPower(currentNum);
            }
        });
    }

    /**
     * 显示设置UHF功率对话框
     */
    public void makeSetUhfPowerDialogX() {
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
        int currentNum = 30;

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
                show("" + currentNum);
//                // 功率按钮
//                Button powerBtn = readUhfCardDlg.getButton(DialogInterface.BUTTON_NEUTRAL);
//                powerBtn.setText("功率：" + currentNum);
//                setUhfPower(currentNum);
            }
        });
    }
    // UHF相关事件与方法。结束========================================================

    /**
     * 查找view
     */
    public void findViews() {
        contentView = (ScrollView) findViewById(R.id.contentView);
        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        // 界面相关参数。开始===============================
        titleTv = (TextView) findViewById(R.id.titleTv);
        memoTv = (TextView) findViewById(R.id.memoTv);
        c1 = (TextView) findViewById(R.id.c1);
        c2 = (TextView) findViewById(R.id.c2);
        c3 = (EditText) findViewById(R.id.c3);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        picIv = (ImageView) findViewById(R.id.picIv);
        noticeTv = (TextView) findViewById(R.id.noticeTv);
        // 界面相关参数。结束===============================
    }
}
