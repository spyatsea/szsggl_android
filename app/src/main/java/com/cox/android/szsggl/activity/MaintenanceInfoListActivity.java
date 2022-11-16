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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.MaintenanceInfoListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * 水工维修_项目基本信息_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MaintenanceInfoListActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 主界面
     */
    LinearLayout contentView;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 搜索图标按钮
     */
    ImageButton searchBarBtn;
    /**
     * 回到主页按钮
     */
    ImageButton homeBtn;
    /**
     * 搜索区
     */
    LinearLayout titleBarSearchLayout;
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
    private EditText searchTitleTv;
    private ImageButton searchTitleClearBtn;
    private Spinner stage1Spinner;
    private Spinner stage2Spinner;
    private Button searchBtn;
    // 界面相关参数。结束===============================

    /**
     * 信息列表
     */
    private ArrayList<HashMap<String, Object>> listItems;
    /**
     * 信息listview
     */
    private PullToRefreshListView infoList;
    /**
     * 列表Adapter
     */
    private MaintenanceInfoListAdapter infoListAdapter;
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
    int ROWS_PER_PAGE = CommonParam.RESULT_LIST_PER;

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

    // 网络连接相关参数。开始==========================================
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    // 网络连接相关参数。结束==========================================

    // 查询参数。开始==========================================
    // 查询参数。结束==========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MaintenanceInfoListActivity.this;

//        // 获取Intent
//        Intent intent = getIntent();
//        // 获取Intent上携带的数据
//        Bundle data = intent.getExtras();

        setContentView(R.layout.maintenance_info_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("请选择水工维修项目");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        searchBarBtn.setTag(false);
        searchBarBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = null;
                if (titleBarSearchLayout.getVisibility() == View.VISIBLE) {
                    searchBarBtn.setTag(false);
                    searchBarBtn.setImageResource(R.drawable.titlebar_btn_search_down);
                    titleBarSearchLayout.setVisibility(View.GONE);
                    animation = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_out);
                } else {
                    searchBarBtn.setTag(true);
                    searchBarBtn.setImageResource(R.drawable.titlebar_btn_search_up);
                    titleBarSearchLayout.setVisibility(View.VISIBLE);
                    animation = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_in);
                }
                animation.setAnimationListener(new Animation.AnimationListener() {

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
                        // boolean flag = (Boolean) searchBarBtn.getTag();
                    }
                });
                // 运行动画
                titleBarSearchLayout.startAnimation(animation);
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
        searchTitleClearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTitleTv.setText("");
            }
        });
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isConnecting) {
                    makeWaitDialog();
                    listItems.clear();
                    infoListAdapter.notifyDataSetChanged();
                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                }
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
            infoList.setRefreshing();
            infoList.setFooterListEnd();
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

            ArrayList<HashMap<String, Object>> stageList = infoTool
                    .getInfoMapList(
                            "SELECT model.c1 c1, model.c2 c2 FROM t_szfgs_sgwxstage model WHERE model.valid='1' ORDER BY model.pxbh ASC",
                            new String[]{});
            HashMap<String, Object> map_empty = new HashMap<String, Object>();
            map_empty.put("code", "");
            map_empty.put("name", "请选择…");
            stage1List = new ArrayList<HashMap<String, Object>>();
            stage2List = new ArrayList<HashMap<String, Object>>();
            stage2Map = new HashMap<String, ArrayList<HashMap<String, Object>>>();
            stage1List.add(map_empty);
            ArrayList<HashMap<String, Object>> list_empty = new ArrayList<HashMap<String, Object>>();
            list_empty.add(map_empty);
            stage2Map.put("", list_empty);
            stage2List.addAll(list_empty);
            for (HashMap<String, Object> o : stageList) {
                String c1 = (String) o.get("c1");
                String c2 = (String) o.get("c2");

                ArrayList<HashMap<String, Object>> list = stage2Map.get(c1);
                if (list == null) {
                    list = new ArrayList<HashMap<String, Object>>();
                    stage2Map.put(c1, list);
                    list.add(map_empty);

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
                searchBarBtn.setVisibility(View.VISIBLE);
                homeBtn.setVisibility(View.VISIBLE);
                listTitleLayout.setVisibility(View.VISIBLE);
                listTitleTv.setText(getString(R.string.pagination_info, 0, 0, 0));

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
                        Log.d("###", JSONObject.toJSONString(m));
                        Log.d("###", JSONObject.toJSONString(stage2List));
                        stage2Spinner.setSelection(0);
                        SimpleAdapter stage2Adapter = (SimpleAdapter) stage2Spinner.getAdapter();
                        stage2List.clear();
                        stage2List.addAll(stage2Map.get(m.get("code")));
                        stage2Adapter.notifyDataSetChanged();
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
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // stage2Spinner。结束=================================================
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (MaintenanceInfoListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoList.setOnRefreshListener(new OnRefreshListener() {

                        @Override
                        public void onRefresh() {
                            if (!infoList.isRefreshingEnd()) {
                                // 查询信息
                                if (!isConnecting) {
                                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                                }
                            }
                        }
                    });
                    infoListAdapter = new MaintenanceInfoListAdapter(getApplicationContext(), listItems, R.layout.maintenance_info_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info", "info", "info", "info"}, new int[]{R.id.infoSn, R.id.infoName,
                            R.id.info_c1, R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.info_c5, R.id.showBtn, R.id.addBtn});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new MaintenanceInfoListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof LinearLayout) {
                                LinearLayout btn = (LinearLayout) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (btn.getId() == R.id.showBtn) {
                                    btn.setTag("showBtn_" + (String) info.get("ids"));
                                    btn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 信息传输Bundle
                                            Bundle data = new Bundle();
                                            data.putSerializable("id", ((String) v.getTag()).replace("showBtn_", ""));

                                            // 创建启动 Activity 的 Intent
                                            Intent intent = new Intent(classThis, MaintenanceInfoShowActivity.class);
                                            // 将数据存入Intent中
                                            intent.putExtras(data);
                                            startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                        }
                                    });
                                } else if (btn.getId() == R.id.addBtn) {
                                    btn.setTag("addBtn_" + (String) info.get("ids"));
                                    btn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 创建信息传输Bundle
                                            Bundle data = new Bundle();
                                            data.putString("mInfoId", ((String) v.getTag()).replace("addBtn_", ""));

                                            // 创建启动 Activity 的 Intent
                                            Intent intent = new Intent(classThis, MaintenanceRecEditActivity.class);
                                            // 将数据存入 Intent 中
                                            intent.putExtras(data);
                                            startActivityForResult(intent, CommonParam.REQUESTCODE_NEW_REC);
                                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                        }
                                    });
                                }
                                return true;
                            }
                            if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.infoSn) {
                                    int index = CommonUtil.getListItemIndex(listItems, info);
                                    textView.setText("" + (index + 1));
                                } else if (textView.getId() == R.id.infoName) {
                                    textView.setText(CommonUtil.N2B((String) info.get("title")));
                                } else if (textView.getId() == R.id.info_c1) {
                                    textView.setText("项目编号：" + CommonUtil.N2B((String) info.get("bh")));
                                } else if (textView.getId() == R.id.info_c2) {
                                    textView.setText("责任部门：" + CommonUtil.N2B((String) info.get("bm")));
                                } else if (textView.getId() == R.id.info_c3) {
                                    textView.setText("项目进度：" + CommonUtil.N2B((String) info.get("stage1")));
                                } else if (textView.getId() == R.id.info_c4) {
                                    textView.setText("项目状态：" + CommonUtil.N2B((String) info.get("stage2")));
                                } else if (textView.getId() == R.id.info_c5) {
                                    textView.setText("计划时间：" + CommonUtil.N2B((String) info.get("patime")) + " 至 " + CommonUtil.N2B((String) info.get("pbtime")));
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
                            // 列表项中的对象
                            HashMap<String, Object> vMap = (HashMap<String, Object>) parent
                                    .getItemAtPosition(position + 1);
                            HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");

                            // 创建信息传输Bundle
                            Bundle data = new Bundle();
                            data.putString("id", (String) info.get("ids"));
                            data.putString("fromFlag", "list");

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, MaintenanceInfoMainActivity.class);

                            // 将数据存入 Intent 中
                            intent.putExtras(data);
                            startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                        }
                    });
                    // 设置列表滚动事件
                    infoList.setOnScrollListener(new OnScrollListener() {

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            Log.d("@@@scrollState", "" + scrollState);
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                             int totalItemCount) {
                            if (!isConnecting) {
                                if (!infoList.isFooterRefreshing() && !infoList.isFooterListEnd()
                                        && (firstVisibleItem + visibleItemCount) == totalItemCount
                                        && infoList.getAdapter() != null && infoList.getAdapter().getCount() > 0) {
                                    // 滚动到底部
                                    infoList.setFooterRefreshing();
                                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_FOOTER, false);
                                }
                            }
                        }
                    });
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
                infoList.onRefreshComplete();
                if (!isConnecting) {
                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                }
            } else {
                show("信息错误！\n请先同步基础信息后再打开本界面。");
                goBack();
            }
        }
    }

    /**
     * 查询信息 AsyncTask 类
     */
    private class SearchTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：显示数据加载指示器
         */
        private static final int PROGRESS_SHOW_INDICATOR = 1001;
        /**
         * 进度常量：隐藏数据加载指示器
         */
        private static final int PROGRESS_HIDE_INDICATOR = 1002;
        /**
         * 进度常量：生成列表
         * */
        // private static final int PROGRESS_MAKE_LIST = 1003;
        /**
         * 新增的信息
         */
        ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();
        /**
         * 搜索类型
         */
        private String searchType;
        /**
         * 结果是否成功的标志
         * */
        // private boolean resultFlag = false;
        /**
         * 是否手动显示数据加载指示器
         */
        private boolean manualIndicatorFlag = false;
        /**
         * 总信息数
         */
        private int total;

        // 查询参数。开始=========================
        // 标题
        private String title;
        // 进度
        private String stage1;
        // 状态
        private String stage2;
        // 查询参数。结束========================

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

            title = searchTitleTv.getText().toString();
            stage1 = ((HashMap<String, String>) stage1Spinner
                    .getItemAtPosition(stage1Spinner.getFirstVisiblePosition())).get("code");
            stage2 = ((HashMap<String, String>) stage2Spinner
                    .getItemAtPosition(stage2Spinner.getFirstVisiblePosition())).get("code");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            infoTool = getInfoTool();
            searchType = (String) params[0];
            manualIndicatorFlag = (Boolean) params[1];
            // 显示数据加载指示器
            publishProgress(PROGRESS_SHOW_INDICATOR);

            // 生成参数。开始======================================
            // 排序字段
            String orderStr = " DATETIME(model.patime) DESC ";
            // 查询语句
            String queryStr = " WHERE model.valid='1' and (model.fqr=? or INSTR(model.fzr, ?)>0 or INSTR(model.zxr, ?)>0) and model.quid=?";
            // 查询参数
            List<String> queryParamList = new ArrayList<String>();
            // 查询参数数组
            String[] queryParamArray;

            queryParamList.add((String) baseApp.getLoginUser().get("ids"));
            queryParamList.add((String) baseApp.getLoginUser().get("ids"));
            queryParamList.add((String) baseApp.getLoginUser().get("ids"));
            queryParamList.add((String) baseApp.getLoginUser().get("ids"));
            if (CommonUtil.checkNB(title)) {
                queryStr = queryStr + " AND UPPER(model.title) LIKE ?";
                queryParamList.add("%" + title.toUpperCase(Locale.CHINA) + "%");
            }
            if (CommonUtil.checkNB(stage1)) {
                queryStr = queryStr + " AND stage1=?";
                queryParamList.add(stage1);
            }
            if (CommonUtil.checkNB(stage2)) {
                queryStr = queryStr + " AND stage2=?";
                queryParamList.add(stage2);
            }

            queryParamArray = new String[queryParamList.size()];
            for (int i = 0, len = queryParamList.size(); i < len; i++) {
                queryParamArray[i] = queryParamList.get(i);
            }
            Log.d("##", JSONObject.toJSONString(queryParamList));
            // 生成参数。结束======================================

            if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                // 队首
                Log.d("@@HEADER", "@@@@@@@@@@@@@@@@@@");

                // 查询信息。开始====================================================================
                ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT model.ids ids, model.bh bh, model.title title, model.bm bm, model.stage1 stage1, model.stage2 stage2, model.patime patime, model.pbtime pbtime FROM t_szfgs_sgwxinfo model " + queryStr + " ORDER BY " + orderStr + " LIMIT 0," + ROWS_PER_PAGE,
                                queryParamArray);
                total = infoTool.getCount("SELECT COUNT(model.ids) FROM t_szfgs_sgwxinfo model " + queryStr, queryParamArray);
                for (int index = 0, len = recList.size(); index < len; index++) {
                    // 存放信息的 Map
                    HashMap<String, Object> listItem = new HashMap<String, Object>();
                    HashMap<String, Object> info = recList.get(index);

                    listItem.put("info", info);

                    listItems_tmp.add(listItem);
                }
                result = CommonParam.RESULT_SUCCESS;
                // 查询信息。结束====================================================================
            } else if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_FOOTER)) {
                // 队尾
                Log.d("@@FOOTER", "@@@@@@@@@@@@@@@@@@");

                // 当前信息数量
                int infoCount = listItems.size();
                // 当前页数
                int page = (infoCount % ROWS_PER_PAGE == 0 ? (infoCount / ROWS_PER_PAGE + 1)
                        : (infoCount / ROWS_PER_PAGE));
                // 查询信息。开始====================================================================
                ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT model.ids ids, model.bh bh, model.title title, model.bm bm, model.stage1 stage1, model.stage2 stage2, model.patime patime, model.pbtime pbtime FROM t_szfgs_sgwxinfo model " + queryStr + " ORDER BY " + orderStr + " LIMIT " + ((page - 1) * ROWS_PER_PAGE) + "," + ROWS_PER_PAGE,
                                queryParamArray);
                total = infoTool.getCount("SELECT COUNT(model.ids) FROM t_szfgs_sgwxinfo model " + queryStr, queryParamArray);
                for (int index = 0, len = recList.size(); index < len; index++) {
                    // 存放信息的 Map
                    HashMap<String, Object> listItem = new HashMap<String, Object>();
                    HashMap<String, Object> info = recList.get(index);

                    listItem.put("info", info);

                    if (!listItems.contains(listItem)) {
                        listItems_tmp.add(listItem);
                    }
                }
                result = CommonParam.RESULT_SUCCESS;
                // 查询信息。结束====================================================================
            }
            // 隐藏数据加载指示器
            publishProgress(PROGRESS_HIDE_INDICATOR);

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
            if (progress[0] == PROGRESS_SHOW_INDICATOR) {
                // 显示数据加载指示器
                if (manualIndicatorFlag) {
                    if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                        // 队首
                        infoList.setRefreshing();
                    } else if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_FOOTER)) {
                        // 队尾
                        infoList.setFooterRefreshing();
                    }
                }
            } else if (progress[0] == PROGRESS_HIDE_INDICATOR) {
                // 隐藏数据加载指示器
                if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                    // 队首
                    infoList.onRefreshComplete();
                } else if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_FOOTER)) {
                    // 队尾
                    infoList.onFooterRefreshComplete();
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
                if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                    // 队首
                    if (total <= ROWS_PER_PAGE) {
                        // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
                        infoList.setFooterListEnd();
                    } else {
                        // 启动队列下查功能
                        infoList.setFooterListContinue();
                    }

                    listItems.clear();
                    listItems.addAll(listItems_tmp);
                    infoListAdapter.notifyDataSetChanged();
                    listTitleTv.setText(getString(R.string.pagination_info, (listItems.size() > 0 ? 1 : 0), listItems.size(), total));
                } else if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_FOOTER)) {
                    // 队尾
                    // 当前信息数量
                    int infoCount = listItems.size();
                    if (total <= infoCount || (listItems_tmp != null && listItems_tmp.size() == 0)) {
                        // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
                        infoList.setFooterListEnd();
                    } else {
                        // 启动队列下查功能
                        infoList.setFooterListContinue();
                    }

                    if (listItems_tmp.size() > 0) {
                        listItems.addAll(listItems_tmp);
                        infoListAdapter.notifyDataSetChanged();
                    }
                    listTitleTv.setText(getString(R.string.pagination_info, (listItems.size() > 0 ? 1 : 0), listItems.size(), total));
                }
            } else {
                show("数据加载失败");
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
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        searchBarBtn = (ImageButton) findViewById(R.id.searchBarBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        titleBarSearchLayout = (LinearLayout) findViewById(R.id.title_bar_search_layout);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        searchTitleTv = (EditText) findViewById(R.id.search_title_tv);
        searchTitleClearBtn = (ImageButton) findViewById(R.id.search_title_clear_btn);
        stage1Spinner = (Spinner) findViewById(R.id.search_stage1_spinner);
        stage2Spinner = (Spinner) findViewById(R.id.search_stage2_spinner);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        // 界面相关参数。开始===============================
        infoList = (PullToRefreshListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
