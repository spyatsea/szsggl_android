/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类规范信息_查阅页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class SgCategoryShowLandActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 表格总数
     */
    private final int TABLE_TOTAL = 3;
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
    private TextView pxbhTv;
    private TextView memoTv;
    // 界面相关参数。结束===============================

    // 子列表相关参数。开始===============================
    /**
     * 已经生成的表格数量
     */
    private int table_count = 0;
    /**
     * 表格中的单选框
     */
    private List<RadioButton> table_radioButtonList;
    // 子列表相关参数。结束===============================

    /**
     * 信息
     */
    private HashMap<String, Object> infoObj;
    /**
     * 信息编号
     */
    private String infoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = SgCategoryShowLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        fromFlag = data.getString("fromFlag", "list");
        infoId = data.getString("id");
        fromFlagType = data.getString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        setContentView(R.layout.sg_category_show);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("水工资源类别和规范：查阅");

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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 执行主进程
        new MainTask().execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ("sgres".equals(fromFlag)) {
            RelativeLayout title_bar_layout = (RelativeLayout) findViewById(R.id.title_bar_layout);
            title_bar_layout.setBackgroundColor(getResources().getColor(R.color.background_title_green));
            StatusBarUtil.setStatusBarMode(this, false, R.color.background_title_green);
        } else {
            StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color);
        }
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
         * 进度常量：生成子列表
         */
        private static final int PROGRESS_MAKE_SUB_LIST = 1003;

        /**
         * 子表格数据包
         */
        private Map<String, Object> x1_dataPack;
        private Map<String, Object> x2_dataPack;
        private Map<String, Object> x3_dataPack;

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
                            "SELECT * FROM t_szfgs_sgcategory model WHERE model.valid='1' and model.ids=?",
                            new String[]{infoId});
            if (recList.size() > 0) {
                infoObj = recList.get(0);
            }

            if (infoObj == null) {
                return result;
            }

            String dataStr;
            JSONArray dataList;
            // "§"表示本列不显示
            // "№"表示不显示标题行
            // "я"表示要用图片显示
            // "∮"表示要用单选框
            String[] columnNameArray = null;
            String[] columnFieldArray = null;
            ArrayList<HashMap<String, Integer>> columnImageList = null;
            int tableViewId = 0;
            int listDataType = CommonParam.LIST_DATA_TYPE_ARRAY;
            Map<String, Integer> tableStyle = null;

            // ◇x1。开始================================================================
            dataStr = (String) infoObj.get("x1");
            if (CommonUtil.checkNB(dataStr)) {
                dataList = JSONArray.parseArray(dataStr);

                // 处理信息
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    JSONObject o = dataList.getJSONObject(i);
                    String d = CommonUtil.N2B(o.getString("d"));
                    List<String> dList = JSONArray.parseArray(d, String.class);
                    StringBuffer dSb = new StringBuffer();
                    for (String _d : dList) {
                        dSb.append("· ").append(_d).append("\n");
                    }
                    if (dSb.length() > 0) {
                        dSb.deleteCharAt(dSb.length() - 1);
                    }
                    o.put("_d", dSb.toString());
                    o.put("_n", (i + 1) + "");
                }

                columnNameArray = new String[]{" ", "名称", "执行结果描述"};
                columnFieldArray = new String[]{"_n", "t", "_d"};
                tableViewId = R.id.x1TableLayout;
                listDataType = CommonParam.LIST_DATA_TYPE_MAP;
                tableStyle = CommonParam.LIST_STYLE.BLUE_01();

                x1_dataPack = new HashMap<String, Object>();
                x1_dataPack.put("dataList", dataList);
                x1_dataPack.put("columnNameArray", columnNameArray);
                x1_dataPack.put("columnFieldArray", columnFieldArray);
                x1_dataPack.put("columnImageList", columnImageList);
                x1_dataPack.put("tableViewId", tableViewId);
                x1_dataPack.put("listDataType", listDataType);
                x1_dataPack.put("tableStyle", tableStyle);
                x1_dataPack.put("headerGravity", Gravity.CENTER);
                x1_dataPack.put("columnGravity", Gravity.LEFT | Gravity.CENTER_VERTICAL);
                x1_dataPack.put("propertyFlag", false);
            }
            // ◇x1。结束================================================================

            // ◇x2。开始================================================================
            dataStr = (String) infoObj.get("x2");
            if (CommonUtil.checkNB(dataStr)) {
                dataList = JSONArray.parseArray(dataStr);

                // 处理信息
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    JSONObject o = dataList.getJSONObject(i);
                    String d = CommonUtil.N2B(o.getString("d"));
                    List<String> dList = JSONArray.parseArray(d, String.class);
                    StringBuffer dSb = new StringBuffer();
                    for (String _d : dList) {
                        dSb.append("· ").append(_d).append("\n");
                    }
                    if (dSb.length() > 0) {
                        dSb.deleteCharAt(dSb.length() - 1);
                    }
                    o.put("_d", dSb.toString());
                    o.put("_n", (i + 1) + "");
                }

                columnNameArray = new String[]{" ", "名称", "执行结果描述"};
                columnFieldArray = new String[]{"_n", "t", "_d"};
                tableViewId = R.id.x2TableLayout;
                listDataType = CommonParam.LIST_DATA_TYPE_MAP;
                tableStyle = CommonParam.LIST_STYLE.GREEN_01();

                x2_dataPack = new HashMap<String, Object>();
                x2_dataPack.put("dataList", dataList);
                x2_dataPack.put("columnNameArray", columnNameArray);
                x2_dataPack.put("columnFieldArray", columnFieldArray);
                x2_dataPack.put("columnImageList", columnImageList);
                x2_dataPack.put("tableViewId", tableViewId);
                x2_dataPack.put("listDataType", listDataType);
                x2_dataPack.put("tableStyle", tableStyle);
                x2_dataPack.put("headerGravity", Gravity.CENTER);
                x2_dataPack.put("columnGravity", Gravity.LEFT | Gravity.CENTER_VERTICAL);
                x2_dataPack.put("propertyFlag", false);
            }
            // ◇x2。结束================================================================

            // ◇x3。开始================================================================
            dataStr = (String) infoObj.get("x3");
            if (CommonUtil.checkNB(dataStr)) {
                dataList = JSONArray.parseArray(dataStr);

                // 处理信息
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    JSONObject o = dataList.getJSONObject(i);
                    String d = CommonUtil.N2B(o.getString("d"));
                    List<String> dList = JSONArray.parseArray(d, String.class);
                    StringBuffer dSb = new StringBuffer();
                    for (String _d : dList) {
                        dSb.append("· ").append(_d).append("\n");
                    }
                    if (dSb.length() > 0) {
                        dSb.deleteCharAt(dSb.length() - 1);
                    }
                    o.put("_d", dSb.toString());
                    o.put("_n", (i + 1) + "");
                }

                columnNameArray = new String[]{" ", "名称", "执行结果描述"};
                columnFieldArray = new String[]{"_n", "t", "_d"};
                tableViewId = R.id.x3TableLayout;
                listDataType = CommonParam.LIST_DATA_TYPE_MAP;
                tableStyle = CommonParam.LIST_STYLE.ORANGE_01();

                x3_dataPack = new HashMap<String, Object>();
                x3_dataPack.put("dataList", dataList);
                x3_dataPack.put("columnNameArray", columnNameArray);
                x3_dataPack.put("columnFieldArray", columnFieldArray);
                x3_dataPack.put("columnImageList", columnImageList);
                x3_dataPack.put("tableViewId", tableViewId);
                x3_dataPack.put("listDataType", listDataType);
                x3_dataPack.put("tableStyle", tableStyle);
                x3_dataPack.put("headerGravity", Gravity.CENTER);
                x3_dataPack.put("columnGravity", Gravity.LEFT | Gravity.CENTER_VERTICAL);
                x3_dataPack.put("propertyFlag", false);
            }
            // ◇x3。结束================================================================
            // 处理数据。结束============================================================================

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);
            // 生成信息列表
            publishProgress(PROGRESS_MAKE_LIST);
            // 生成子信息列表
            publishProgress(PROGRESS_MAKE_SUB_LIST);

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
                if ("sgres".equals(fromFlag)) {

                } else {
                    homeBtn.setVisibility(View.VISIBLE);
                }
                if (!Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).equals(fromFlagType)) {
                    homeBtn.setVisibility(View.GONE);
                }
                titleTv.setText(CommonUtil.N2B((String) infoObj.get("title")));
                pxbhTv.setText(CommonUtil.N2B((String) infoObj.get("pxbh")));
                memoTv.setText(CommonUtil.N2B((String) infoObj.get("memo")));
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
            } else if (progress[0] == PROGRESS_MAKE_SUB_LIST) {
                // 生成子列表
                new MakeListTableTask().execute(x1_dataPack);
                new MakeListTableTask().execute(x2_dataPack);
                new MakeListTableTask().execute(x3_dataPack);
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

    /**
     * 生成表格 AsyncTask 类
     */
    private class MakeListTableTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：生成信息列表
         */
        private static final int PROGRESS_MAKE_LIST = 1001;
        /**
         * 数据包
         */
        private Map<String, Object> dataPack;
        /**
         * 数据信息
         */
        private JSONArray dataList;
        /**
         * 列名
         */
        private String[] columnNameArray;
        /**
         * 列数据key
         */
        private String[] columnFieldArray;
        /**
         * 列内容对应的图片
         * <p>List中的元素数量，要与列数量一致。如果只有特定的几个列是图片列。其他非图片列也要以null来占用这个元素。这样才能保证列数量一致。<br/>
         * 如果某列的内容以"я"开始，表示该列是图片列。具体的内容对应的图片，要在本元素中定义。<br/>
         * 这样可以保证，即使值相同，不同的图片列中的图片可以不同。<br/>
         * 需要注意的是，这里的图片调用时用的是drawable的id，也就是说只能使用资源中的图片。</p>
         */
        private ArrayList<HashMap<String, Integer>> columnImageList;
        /**
         * 列宽
         */
        private float[] columnWeightArray;
        /**
         * 列表View的id
         */
        private int tableViewId;

        /**
         * 列表数据类型
         */
        private int listDataType;

        /**
         * 列表样式
         */
        private Map<String, Integer> tableStyle;

        /**
         * 标题位置
         */
        private Integer headerGravity;

        /**
         * 内容位置
         */
        private Integer columnGravity;

        /**
         * 属性表标志
         */
        private Boolean propertyFlag;

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

            if (table_radioButtonList == null) {
                table_radioButtonList = new ArrayList<RadioButton>();
            }
            // 处理数据。开始============================================================================
            dataPack = (HashMap<String, Object>) params[0];

            if (dataPack != null) {
                dataList = (JSONArray) dataPack.get("dataList");
                columnNameArray = (String[]) dataPack.get("columnNameArray");
                columnFieldArray = (String[]) dataPack.get("columnFieldArray");
                columnImageList = (ArrayList<HashMap<String, Integer>>) dataPack.get("columnImageList");
                if (columnImageList == null) {
                    columnImageList = new ArrayList<HashMap<String, Integer>>();
                }
                columnWeightArray = (float[]) dataPack.get("columnWeightArray");
                if (columnWeightArray == null) {
                    columnWeightArray = new float[]{};
                }
                tableViewId = (Integer) dataPack.get("tableViewId");
                listDataType = (Integer) dataPack.get("listDataType");
                tableStyle = (HashMap<String, Integer>) dataPack.get("tableStyle");
                headerGravity = (Integer) dataPack.get("headerGravity");
                columnGravity = (Integer) dataPack.get("columnGravity");
                propertyFlag = (Boolean) dataPack.get("propertyFlag");
                if (propertyFlag == null) {
                    propertyFlag = false;
                }

                // 生成信息列表
                publishProgress(PROGRESS_MAKE_LIST);
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
            if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                LayoutInflater inflater = getLayoutInflater();
                final LinearLayout tableListLayout = (LinearLayout) findViewById(tableViewId);

                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_list_item_c1_table, null);

                // 生成表格内容。开始========================================================================
                String columnName = columnNameArray[0];
                if (!"№".equals(columnName)) {
                    // 生成表头。开始========================================================================
                    // 表头Layout
                    LinearLayout tableHeaderLayout = (LinearLayout) layout.findViewById(R.id.tableHeaderLayout);
                    // 表头行
                    LinearLayout tableHeaderRowLayout = (LinearLayout) inflater.inflate(
                            R.layout.fragment_list_item_c1_table_header, null);
                    // 表头行内容容器Layout
                    LinearLayout tableHeaderContentLayout = (LinearLayout) tableHeaderRowLayout
                            .findViewById(R.id.tableHeaderContentLayout);
                    boolean tableHeaderColumnLayout_tag_flag = false;
                    for (int headerIndex = 0, headerTotal = columnNameArray.length; headerIndex < headerTotal; headerIndex++) {
                        columnName = columnNameArray[headerIndex];
                        if ("§".equals(columnName)) {
                            continue;
                        }
                        // 表头列
                        LinearLayout tableHeaderColumnLayout = (LinearLayout) inflater.inflate(
                                R.layout.fragment_list_item_c1_table_header_item, null);
                        if (!tableHeaderColumnLayout_tag_flag) {
                            tableHeaderColumnLayout_tag_flag = true;
                            tableHeaderColumnLayout.setTag("tableColumnLayout_0");
                        }
                        // 表头列文本
                        TextView tableHeaderColumn = (TextView) tableHeaderColumnLayout.findViewById(R.id.tableColumn);
                        tableHeaderColumn.setText(CommonUtil.N2B(columnNameArray[headerIndex]));

                        // 如果是第一列，要在列前添加列分隔符
                        if (headerIndex == 0) {
                            tableHeaderContentLayout.addView(CommonUtil.makeColumnSpitter(classThis,
                                    tableStyle.get("list_color_split_column")));
                        }
                        // 将列添加到行中
                        tableHeaderContentLayout.addView(tableHeaderColumnLayout);
                        // 布局参数
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tableHeaderColumnLayout
                                .getLayoutParams();
                        lp.width = 0;
                        if (columnWeightArray.length > 0) {
                            float columnWeight = columnWeightArray[headerIndex];
                            lp.weight = columnWeight;
                        } else {
                            String columnField = columnFieldArray[headerIndex];
                            if ("_n".equals(columnField)) {
                                // 序号列
                                lp.weight = 1.0f;
                            } else {
                                lp.weight = 5.0f;
                            }
                        }
                        //tableHeaderColumnLayout.setLayoutParams(lp);
                        // 添加列分隔符
                        tableHeaderContentLayout.addView(CommonUtil.makeColumnSpitter(classThis,
                                tableStyle.get("list_color_split_column")));
                        // Styler。开始=====================================================
                        tableHeaderRowLayout.setBackgroundColor(getResources().getColor(
                                tableStyle.get("list_color_header_background")));
                        tableHeaderColumn.setTextColor(getResources()
                                .getColor(tableStyle.get("list_color_header_font")));
                        if (headerGravity != null) {
                            tableHeaderColumn.setGravity(headerGravity);
                        } else if (columnGravity != null) {
                            tableHeaderColumn.setGravity(columnGravity);
                        }
                        // Styler。结束=====================================================
                    }
                    // 将表头列添加到表头
                    tableHeaderLayout.addView(tableHeaderRowLayout);
                    // Styler。开始=====================================================
                    tableHeaderRowLayout.findViewById(R.id.splitterView_1).setBackgroundColor(
                            getResources().getColor(tableStyle.get("list_color_split_row")));
                    // tableHeaderRowLayout.findViewById(R.id.splitterView_2).setBackgroundColor(
                    // getResources().getColor(tableStyle.get("list_color_split_row")));
                    // Styler。结束=====================================================
                    // 生成表头。结束========================================================================
                }

                // 生成表格行。开始========================================================================
                // 表格Body Layout
                LinearLayout tableBodyLayout = (LinearLayout) layout.findViewById(R.id.tableBodyLayout);

                for (int dataIndex = 0, dataTotle = dataList.size(); dataIndex < dataTotle; dataIndex++) {
                    // 表格行
                    LinearLayout tableRowLayout = (LinearLayout) inflater.inflate(
                            R.layout.fragment_list_item_c1_table_row, null);
                    // 表格行内容容器Layout
                    LinearLayout tableRowContentLayout = (LinearLayout) tableRowLayout
                            .findViewById(R.id.tableRowContentLayout);

                    Object info = null;
                    if (listDataType == CommonParam.LIST_DATA_TYPE_ARRAY) {
                        info = (JSONArray) dataList.getJSONArray(dataIndex);
                    } else if (listDataType == CommonParam.LIST_DATA_TYPE_MAP) {
                        info = (JSONObject) dataList.getJSONObject(dataIndex);
                    } else if (listDataType == CommonParam.LIST_DATA_TYPE_STRING_ARRAY) {
                        info = CommonUtil.N2B(dataList.getString(dataIndex));
                    }
                    for (int cIndex = 0, cTotal = columnNameArray.length; cIndex < cTotal; cIndex++) {
                        columnName = columnNameArray[cIndex];
                        if ("§".equals(columnName)) {
                            continue;
                        }
                        String v = null;
                        if (listDataType == CommonParam.LIST_DATA_TYPE_ARRAY) {
                            v = "" + ((JSONArray) info).get(cIndex);
                        } else if (listDataType == CommonParam.LIST_DATA_TYPE_MAP) {
                            v = "" + ((JSONObject) info).get(columnFieldArray[cIndex]);
                        } else if (listDataType == CommonParam.LIST_DATA_TYPE_STRING_ARRAY) {
                            v = (String) info;
                        }

                        // 内容列
                        LinearLayout tableRowColumnLayout = null;
                        // 内容列文本
                        View tableRowColumn = null;
                        // 内容列
                        if (v.indexOf("я") != -1) {
                            // 图片
                            HashMap<String, Integer> columnImageMap_c = columnImageList.get(cIndex);
                            Integer tableRowColumn_drawable_id = columnImageMap_c.get(v.replaceAll("я", ""));
                            if (tableRowColumn_drawable_id != null) {
                                tableRowColumnLayout = (LinearLayout) inflater.inflate(
                                        R.layout.fragment_list_item_c1_table_row_column_image, null);
                                tableRowColumn = (ImageView) tableRowColumnLayout.findViewById(R.id.tableColumn);
                                ((ImageView) tableRowColumn).setImageResource(tableRowColumn_drawable_id.intValue());
                            }
                        } else if (v.indexOf("∮") != -1) {
                            // 单选框
                            tableRowColumnLayout = (LinearLayout) inflater.inflate(
                                    R.layout.fragment_list_item_c1_table_row_column_radio, null);
                            // 内容列文本
                            tableRowColumn = (RadioButton) tableRowColumnLayout.findViewById(R.id.tableButtonRadio);
                            tableRowColumn.setTag(v);
                            table_radioButtonList.add((RadioButton) tableRowColumn);
                            ((RadioButton) tableRowColumn).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        String tag = (String) buttonView.getTag();
                                        String t = tag.substring(tag.indexOf("∮") + 1);
                                        // 点击单选框后的一些操作。开始=====================
                                        // 点击单选框后的一些操作。结束=====================
                                        for (RadioButton r : table_radioButtonList) {
                                            String r_tag = (String) r.getTag();
                                            if (!tag.equals(r_tag)) {
                                                if (r.isChecked()) {
                                                    r.setChecked(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            // 正常文本
                            tableRowColumnLayout = (LinearLayout) inflater.inflate(
                                    R.layout.fragment_list_item_c1_table_row_column_textview, null);
                            // 内容列文本
                            tableRowColumn = (TextView) tableRowColumnLayout.findViewById(R.id.tableColumn);
                            ((TextView) tableRowColumn).setText(v);
                        }

                        // 如果是第一列，要在列前添加列分隔符
                        if (cIndex == 0) {
                            tableRowContentLayout.addView(CommonUtil.makeColumnSpitter(classThis,
                                    tableStyle.get("list_color_split_column")));
                        }
                        // 将列添加到行中
                        tableRowContentLayout.addView(tableRowColumnLayout);

                        // 布局参数
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tableRowColumnLayout
                                .getLayoutParams();
                        lp.width = 0;
                        String columnField = columnFieldArray[cIndex];
                        if (columnWeightArray.length > 0) {
                            float columnWeight = columnWeightArray[cIndex];
                            lp.weight = columnWeight;
                        } else {
                            if ("_n".equals(columnField)) {
                                // 序号列
                                lp.weight = 1.0f;
                            } else {
                                lp.weight = 5.0f;
                            }
                        }
                        //tableRowColumnLayout.setLayoutParams(lp);
                        // 添加列分隔符
                        tableRowContentLayout.addView(CommonUtil.makeColumnSpitter(classThis,
                                tableStyle.get("list_color_split_column")));

                        // Styler。开始=====================================================
                        if (propertyFlag) {
                            if (cIndex == 0) {
                                if (tableRowColumn instanceof TextView) {
                                    ((TextView) tableRowColumn).setTextColor(getResources().getColor(
                                            tableStyle.get("list_color_content_font")));
                                }
                            } else {
                                if (tableRowColumn instanceof TextView) {
                                    ((TextView) tableRowColumn).setTextColor(getResources().getColor(R.color.normal_text_color_grey));
                                }
                            }
                        } else {
                            if (tableRowColumn instanceof TextView) {
                                ((TextView) tableRowColumn).setTextColor(getResources().getColor(
                                        tableStyle.get("list_color_content_font")));
                            }
                        }
                        if (columnGravity != null) {
                            if (tableRowColumn instanceof TextView) {
                                // ★☆特殊处理，标准列表中需删除
                                if ("_n".equals(columnField) && headerGravity != null) {
                                    ((TextView) tableRowColumn).setGravity(headerGravity);
                                } else {
                                    ((TextView) tableRowColumn).setGravity(columnGravity);
                                }
                            } else if (tableRowColumn instanceof ImageView) {
                                tableRowColumnLayout.setGravity(columnGravity);
                            }
                        }
                        // Styler。结束=====================================================
                    }
                    // 将本行数据添加到表格
                    tableBodyLayout.addView(tableRowLayout);
                    // Styler。开始=====================================================
                    if (dataIndex % 2 == 1) {
                        tableRowLayout.setBackgroundColor(getResources().getColor(
                                tableStyle.get("list_color_content_background_odd")));
                    } else {
                        tableRowLayout.setBackgroundColor(getResources().getColor(
                                tableStyle.get("list_color_content_background_even")));
                    }
                    if (dataIndex == 0) {
                        View splitterView_1 = tableRowLayout.findViewById(R.id.splitterView_1);
                        splitterView_1.setVisibility(View.VISIBLE);
                        splitterView_1.setBackgroundColor(getResources().getColor(
                                tableStyle.get("list_color_split_row")));
                    }
                    tableRowLayout.findViewById(R.id.splitterView_2).setBackgroundColor(
                            getResources().getColor(tableStyle.get("list_color_split_row")));
                    // Styler。结束=====================================================
                }
                // 生成表格行。结束========================================================================

                tableListLayout.addView(layout);

                // 处理表头行高。开始========================================================================
                // 在有的设备上，会出现表头行过高的情况，下面的语句专门用来处理这种情况
                tableListLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        String columnName = columnNameArray[0];
                        Log.d("#x5", "#" + tableListLayout.getWidth() + ":" + tableListLayout.getHeight());
                        if (!"№".equals(columnName)) {
                            LinearLayout tableHeaderContentLayout = tableListLayout.findViewById(R.id.tableHeaderLayout).findViewById(R.id.tableHeaderContentLayout);
                            if (tableHeaderContentLayout != null) {
                                LinearLayout tableColumnLayout = (LinearLayout) tableHeaderContentLayout.findViewWithTag("tableColumnLayout_0");
                                if (tableColumnLayout != null) {
                                    Log.d("#x6", "#" + tableHeaderContentLayout.getHeight() + "#" + tableColumnLayout.getHeight());
                                    int tableHeaderContentLayout_height = tableHeaderContentLayout.getHeight();
                                    int tableColumnLayout_height = tableColumnLayout.getHeight();
                                    if (tableHeaderContentLayout_height > (tableColumnLayout_height + 5)) {
                                        // 布局参数
                                        LinearLayout.LayoutParams tableHeaderContentLayout_lp = (LinearLayout.LayoutParams) tableHeaderContentLayout
                                                .getLayoutParams();
                                        tableHeaderContentLayout_lp.height = tableColumnLayout_height;
                                        Log.d("#x7", "#" + tableHeaderContentLayout.getHeight() + "#" + tableColumnLayout.getHeight());
                                        tableHeaderContentLayout.setLayoutParams(tableHeaderContentLayout_lp);
                                        Log.d("#x8", "#" + tableHeaderContentLayout.getHeight() + "#" + tableColumnLayout.getHeight());
                                    }

                                }
                            }
                        }
                    }
                });
                // 处理表头行高。结束========================================================================
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
            table_count++;
            if (table_count == TABLE_TOTAL) {
                unWait();
            }
        }
    }

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
        pxbhTv = (TextView) findViewById(R.id.pxbhTv);
        memoTv = (TextView) findViewById(R.id.memoTv);
        // 界面相关参数。结束===============================
    }
}
