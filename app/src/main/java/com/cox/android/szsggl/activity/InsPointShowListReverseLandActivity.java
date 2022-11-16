/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsPointShowListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡视_巡视点_编辑状态_列表页面
 * <p>这里使用了<i>SearchTask</i>生成巡视点列表</p>
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsPointShowListReverseLandActivity extends DbActivity {
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
     * 读卡类型：定位
     */
    private static final String SCAN_CARD_TYPE_LOC = "loc";
    /**
     * 读卡类型：采集
     */
    private static final String SCAN_CARD_TYPE_ADD = "add";
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
    // 界面相关参数。开始===============================
    private TextView totalNumTv;
    private TextView currentNumTv;
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
    private InsPointShowListAdapter infoListAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = InsPointShowListReverseLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");

        setContentView(R.layout.ins_point_show_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("水工巡视");

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
                helpBtn.setVisibility(View.GONE);
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
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (InsPointShowListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoListAdapter = new InsPointShowListAdapter(getApplicationContext(), listItems, R.layout.ins_point_show_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info"}, new int[]{R.id.tableRowLayout, R.id.info_c1,
                            R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.info_c5});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new InsPointShowListAdapter.ViewBinder() {

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
                                                data.putString("fromFlag", "show");
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
                                                data.putString("fromFlag", "show");
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
                                                data.putString("fromFlag", "show");
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
                                                data.putString("fromFlag", "show");
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
                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    makeAlertDialog("任务中待检查的巡视点数量为" + total_needToIns + "，现在手机中只找到" + total + "个巡视点的信息，有的巡视点无法查阅。您可以退回到主界面，然后同步基础信息。");
                } else {
                    show("数据加载失败");
                }
            }
            isConnecting = false;
        }
    }

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
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        infoList = (ListView) findViewById(R.id.infoList);
        totalNumTv = (TextView) findViewById(R.id.totalNumTv);
        currentNumTv = (TextView) findViewById(R.id.currentNumTv);
        // 界面相关参数。结束===============================
    }
}
