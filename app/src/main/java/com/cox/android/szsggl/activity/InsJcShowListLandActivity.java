/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsJcListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 巡视_现场检查记录_查阅列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsJcShowListLandActivity extends DbActivity {
    // 常量。开始===============================
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
    // 界面相关参数。开始===============================
    private TextView titleTv;
    private TextView markTitleTv;
    private LinearLayout markContentLayout;
    private EditText markTv;
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
     * 检查信息
     */
    private HashMap<String, Object> jcInfo;
    /**
     * 未完成的检查信息编号
     */
    private String jcId;
    /**
     * 缺陷描述List
     */
    private JSONArray stdList;
    /**
     * 检查结果List
     */
    private ArrayList<String> rList;
    /**
     * 检查结果Color List
     */
    private ArrayList<Integer> rColorList;
    /**
     * 消缺结果List
     */
    private ArrayList<String> xqList;
    /**
     * 消缺结果Color List
     */
    private ArrayList<Integer> xqColorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = InsJcShowListLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");
        resInfo = (HashMap<String, Object>) data.getSerializable("resInfo");
        jcInfo = (HashMap<String, Object>) data.getSerializable("jcInfo");
        jcId = (String) jcInfo.get("ids");

        setContentView(R.layout.ins_jc_show_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

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
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                // 返回
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
            listItems = new ArrayList<HashMap<String, Object>>();
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
            xqList = new ArrayList<String>(2);
            xqList.add(getString(R.string.title_jc_xq_yes));
            xqList.add(getString(R.string.title_jc_xq_no));
            xqColorList = new ArrayList<Integer>(2);
            xqColorList.add(R.color.text_green_dark);
            xqColorList.add(R.color.text_red);
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
                titleTv.setText((String) jcInfo.get("res_title"));
                markTitleTv.setVisibility(View.VISIBLE);
                markContentLayout.setVisibility(View.VISIBLE);
                markTv.setText(CommonUtil.N2B((String) jcInfo.get("areasign")));
                markTv.setHint("");
                markTv.setEnabled(false);
                startTimeIv.setVisibility(View.VISIBLE);
                startTimeTv.setVisibility(View.VISIBLE);
                startTimeTv.setText(CommonUtil.N2B((String) jcInfo.get("atime")));
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
                                    textView.setBackground(null);
                                    layout.setBackground(null);
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
                                    textView.setBackground(null);
                                    layout.setBackground(null);
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

                                    textView.setBackground(null);
                                    if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        layout.setBackground(null);
                                    }
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImageView textView_i1 = (ImageView) v.findViewById(R.id.info_t5i1);
                                            ImageView textView_i2 = (ImageView) v.findViewById(R.id.info_t5i2);
                                            // 点击后是否打开缺陷描述页面
                                            boolean canClickFlag = false;
                                            if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                                canClickFlag = true;
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
                                                    if (!baseApp.isReverseRotate) {
                                                        intent = new Intent(classThis, InsJcDescShowLandActivity.class);
                                                    } else {
                                                        intent = new Intent(classThis, InsJcDescShowReverseLandActivity.class);
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
                                    textView.setBackground(null);
                                    layout.setBackground(null);
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
                                    textView.setBackground(null);
                                    layout.setBackground(null);
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

                                    textView.setBackground(null);
                                    if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                        layout.setBackground(getResources().getDrawable(R.drawable.cell_bg_blue_selector));
                                    } else {
                                        layout.setBackground(null);
                                    }
                                    layout.setTag(info.get("V_INFO_SN"));
                                    layout.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImageView textView_i1 = (ImageView) v.findViewById(R.id.info_t5i1);
                                            ImageView textView_i2 = (ImageView) v.findViewById(R.id.info_t5i2);
                                            // 点击后是否打开缺陷描述页面
                                            boolean canClickFlag = false;
                                            if (textView_i1.getVisibility() == View.VISIBLE || textView_i2.getVisibility() == View.VISIBLE) {
                                                canClickFlag = true;
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
                                                    if (!baseApp.isReverseRotate) {
                                                        intent = new Intent(classThis, InsJcDescShowLandActivity.class);
                                                    } else {
                                                        intent = new Intent(classThis, InsJcDescShowReverseLandActivity.class);
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

            // 继续检查
            jcSubList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT * FROM t_szfgs_sgxunsjcjl_son model WHERE model.valid='1' and model.jcjl_id=? and model.quid=? ORDER BY model.xh ASC",
                            new String[]{jcId, (String) baseApp.getLoginUser().get("ids")});
            stdList = new JSONArray();

            // 处理检查结果。开始=======================================
            for (HashMap<String, Object> o : jcSubList) {
                // 检查结果
                String r = (String) o.get("r");
                // 消缺否
                String xqf = (String) o.get("xqf");

                if (getString(R.string.title_jc_result_yes).equals(r) || getString(R.string.title_jc_result_ignore).equals(r) || "".equals(r)) {
                    // 正常、忽略，或者还没有进行检查时
                    if (!getString(R.string.title_jc_xq_blank).equals(xqf)) {
                        xqf = getString(R.string.title_jc_xq_blank);
                    }
                } else if (getString(R.string.title_jc_result_no).equals(r)) {
                    // 异常
                    if (!getString(R.string.title_jc_xq_yes).equals(xqf) && !getString(R.string.title_jc_xq_no).equals(xqf)) {
                        xqf = getString(R.string.title_jc_xq_no);
                    }
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

                goBackBtn.setVisibility(View.VISIBLE);
                listTitleTv.setText(R.string.msg_ins_jc_list_std);
                markTv.setHint("");
                markTv.setEnabled(false);
                markTitleTv.setVisibility(View.GONE);
                markContentLayout.setVisibility(View.GONE);
                startTimeTv.setTextColor(getResources().getColor(R.color.normal_text_color_grey));

                titleTv.setText((String) jcInfo.get("res_title"));
                markTitleTv.setVisibility(View.VISIBLE);
                markContentLayout.setVisibility(View.VISIBLE);
                markTv.setText(CommonUtil.N2B((String) jcInfo.get("areasign")));
                startTimeIv.setVisibility(View.VISIBLE);
                startTimeTv.setVisibility(View.VISIBLE);
                startTimeTv.setText(CommonUtil.N2B((String) jcInfo.get("atime")));
            } else {
                show("数据加载失败");
            }
            isConnecting = false;
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
     * 查找view
     */
    public void findViews() {
        contentView = (LinearLayout) findViewById(R.id.contentView);
        titleBarModeName = (TextView) findViewById(R.id.title_type_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        titleTv = (TextView) findViewById(R.id.titleTv);
        markTitleTv = (TextView) findViewById(R.id.markTitleTv);
        markContentLayout = (LinearLayout) findViewById(R.id.markContentLayout);
        markTv = (EditText) findViewById(R.id.markTv);
        stdNumTv = (TextView) findViewById(R.id.stdNumTv);
        startTimeIv = (ImageView) findViewById(R.id.startTimeIv);
        startTimeTv = (TextView) findViewById(R.id.startTimeTv);
        infoList = (ListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
