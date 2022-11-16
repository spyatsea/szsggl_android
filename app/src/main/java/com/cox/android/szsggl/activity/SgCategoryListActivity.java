/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.SgCategoryListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 分类规范信息_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class SgCategoryListActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
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
     * 列表名称区
     */
    LinearLayout listTitleLayout;
    /**
     * 列表名称
     */
    TextView listTitleTv;

    // 界面相关参数。开始===============================
    /**
     * 返回
     */
    private Button goBackBtn;
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
    private SgCategoryListAdapter infoListAdapter;
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

        classThis = SgCategoryListActivity.this;

        setContentView(R.layout.info_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        //Intent intent = getIntent();
        // 获取Intent上携带的数据
        //Bundle data = intent.getExtras();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("水工资源类别和规范");

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
        mainTask = new MainTask().execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color);
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
            infoCount = infoTool.getCount("select count(model.ids) from t_szfgs_sgcategory model where model.valid='1'",
                    new String[]{});
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
                homeBtn.setVisibility(View.VISIBLE);
                listTitleLayout.setVisibility(View.VISIBLE);
                listTitleTv.setGravity(Gravity.RIGHT);
                listTitleTv.setText(getString(R.string.pagination_info, 0, 0, 0));
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (SgCategoryListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    // MANDATORY: Set the onRefreshListener on the list. You could also use
                    // listView.setOnRefreshListener(this); and let this Activity
                    // implement OnRefreshListener.
                    infoList.setOnRefreshListener(new OnRefreshListener() {

                        @Override
                        public void onRefresh() {
                            // Your code to refresh the list contents goes here

                            // for example:
                            // If this is a webservice call, it might be asynchronous so
                            // you would have to call listView.onRefreshComplete(); when
                            // the webservice returns the data
                            // infoListAdapter.loadData();

                            // Make sure you call listView.onRefreshComplete()
                            // when the loading is done. This can be done from here or any
                            // other place, like on a broadcast receive from your loading
                            // service or the onPostExecute of your AsyncTask.

                            // For the sake of this sample, the code will pause here to
                            // force a delay when invoking the refresh
                            // infoList.postDelayed(new Runnable() {
                            //
                            // @Override
                            // public void run() {
                            // infoList.onRefreshComplete();
                            // }
                            // }, 2000);
                            if (!infoList.isRefreshingEnd()) {
                                // 查询信息
                                if (!isConnecting) {
                                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                                }
                            }
                        }
                    });
                    infoListAdapter = new SgCategoryListAdapter(getApplicationContext(), listItems, R.layout.sg_category_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info"}, new int[]{R.id.infoSn, R.id.infoName,
                            R.id.info_c1, R.id.info_c2, R.id.info_c3, R.id.info_c4});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new SgCategoryListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.infoSn) {
                                    int index = CommonUtil.getListItemIndex(listItems, info);
                                    textView.setText("" + (index + 1));
                                } else if (textView.getId() == R.id.infoName) {
                                    textView.setText(CommonUtil.N2B((String) info.get("t")));
                                } else if (textView.getId() == R.id.info_c1) {
                                    textView.setText("排序编号：" + CommonUtil.N2B((String) info.get("p")));
                                } else if (textView.getId() == R.id.info_c2) {
                                    String x = CommonUtil.N2B((String) info.get("x1"));
                                    Drawable icon = null;
                                    if (CommonParam.YES.equals(x)) {
                                        icon = getResources().getDrawable(R.drawable.circle_checked);
                                    } else {
                                        icon = getResources().getDrawable(R.drawable.circle_normal);
                                    }
                                    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                                    textView.setCompoundDrawables(null, null, icon, null);
                                } else if (textView.getId() == R.id.info_c3) {
                                    String x = CommonUtil.N2B((String) info.get("x2"));
                                    Drawable icon = null;
                                    if (CommonParam.YES.equals(x)) {
                                        icon = getResources().getDrawable(R.drawable.circle_checked);
                                    } else {
                                        icon = getResources().getDrawable(R.drawable.circle_normal);
                                    }
                                    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                                    textView.setCompoundDrawables(null, null, icon, null);
                                } else if (textView.getId() == R.id.info_c4) {
                                    String x = CommonUtil.N2B((String) info.get("x3"));
                                    Drawable icon = null;
                                    if (CommonParam.YES.equals(x)) {
                                        icon = getResources().getDrawable(R.drawable.circle_checked);
                                    } else {
                                        icon = getResources().getDrawable(R.drawable.circle_normal);
                                    }
                                    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                                    textView.setCompoundDrawables(null, null, icon, null);
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
                            data.putString("id", (String) info.get("id"));
                            data.putString("fromFlag", "list");

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, SgCategoryShowActivity.class);

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
                if (infoCount == 0) {
                    makeAlertDialog("当前列表没有信息，请先同步基础信息（或下载任务）后再打开本界面。");
                }
                if (!isConnecting) {
                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                }
            } else {
                show("信息错误！\n请先同步基础信息（或下载任务）后再打开本界面。");
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
            searchType = (String) params[0];
            manualIndicatorFlag = (Boolean) params[1];
            // 显示数据加载指示器
            publishProgress(PROGRESS_SHOW_INDICATOR);

            if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                // 队首
                Log.d("@@HEADER", "@@@@@@@@@@@@@@@@@@");

                // 查询信息。开始====================================================================
                ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT model.ids id, model.title t, model.pxbh p, model.x1 x1, model.x2 x2, model.x3 x3 FROM v_szfgs_sgcategory model WHERE model.valid='1' ORDER BY model.pxbh ASC LIMIT 0," + ROWS_PER_PAGE,
                                new String[]{});
                total = infoTool.getCount("SELECT COUNT(model.ids) FROM t_szfgs_sgcategory model WHERE model.valid='1'", new String[]{});
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
                                "SELECT model.ids id, model.title t, model.pxbh p, model.x1 x1, model.x2 x2, model.x3 x3 FROM v_szfgs_sgcategory model WHERE model.valid='1' ORDER BY model.pxbh ASC LIMIT " + ((page - 1) * ROWS_PER_PAGE) + "," + ROWS_PER_PAGE,
                                new String[]{});
                total = infoTool.getCount("SELECT COUNT(model.ids) FROM t_szfgs_sgcategory model WHERE model.valid='1'", new String[]{});
                int count = listItems.size();
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
     * 查询信息 AsyncTask 类
     */
    private class SearchTaskNet extends AsyncTask<Object, Integer, String> {
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

            // 服务器返回的文本
            String respStr = "";
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。结束=================

            searchType = (String) params[0];
            manualIndicatorFlag = (Boolean) params[1];
            // 显示数据加载指示器
            publishProgress(PROGRESS_SHOW_INDICATOR);

            if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                // 队首
                Log.d("@@HEADER", "@@@@@@@@@@@@@@@@@@");

                try {
                    // 查询信息。开始====================================================================
                    // 生成参数。开始======================================
                    String userId = (String) baseApp.loginUser.get("ids");

                    JSONObject queryParams = new JSONObject();
                    // 生成参数。结束======================================

                    // 设置post值。开始=========================
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                            .addFormDataPart("userId", userId)
                            .addFormDataPart("infoType", "sql_sg_category")
                            .addFormDataPart("queryParams", queryParams.toJSONString())
                            .addFormDataPart("rows", "" + ROWS_PER_PAGE);
                    RequestBody requestBody = multipartBuilder.build();
                    // 设置post值。结束=========================

                    Request.Builder requestBuilder = new Request.Builder();
                    requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                    upHttpRequest = requestBuilder
                            .url("http://" + baseApp.serverAddr + "/"
                                    + CommonParam.URL_SEARCHTABLE)
                            .post(requestBody)
                            .build();
                    if (baseHttpClient == null) {
                        baseHttpClient = new OkHttpClient();
                    }
                    if (upHttpClient == null) {
                        upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                    }

                    upResponse = upHttpClient.newCall(upHttpRequest).execute();
                    if (upResponse.code() == 200) {
                        // 获取成功
                        byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                        // 如果返回的xml中有BOM头，要将其删除
                        if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0]
                                && respBytes[1] == FileUtil.UTF8BOM[1] && respBytes[2] == FileUtil.UTF8BOM[2]) {
                            respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                        } else {
                            respStr = new String(respBytes, "UTF-8");
                        }
                        Log.d("##", "#" + respStr);

                        JSONObject respJson = JSONObject.parseObject(respStr);
                        String resultStr = respJson.getString("result");
                        if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                            // 请求正确
                            JSONArray data = respJson.getJSONArray("data");
                            total = respJson.getIntValue("total");

                            for (int index = 0, len = data.size(); index < len; index++) {
                                // 存放信息的 Map
                                HashMap<String, Object> listItem = new HashMap<String, Object>();
                                HashMap<String, Object> info = null;
                                JSONObject json = data.getJSONObject(index);
                                info = CommonUtil.jsonToMap(json);

                                listItem.put("info", info);

                                listItems_tmp.add(listItem);
                            }
                            result = CommonParam.RESULT_SUCCESS;
                        }
                    } else {
                        // 服务器连接失败
                    }
                    // 查询信息。结束====================================================================
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            } else if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_FOOTER)) {
                // 队尾
                Log.d("@@FOOTER", "@@@@@@@@@@@@@@@@@@");
                try {
                    // 当前信息数量
                    int infoCount = listItems.size();
                    // 当前页数
                    int page = (infoCount % ROWS_PER_PAGE == 0 ? (infoCount / ROWS_PER_PAGE + 1)
                            : (infoCount / ROWS_PER_PAGE));
                    // 查询信息。开始====================================================================
                    // 生成参数。开始======================================
                    String userId = (String) baseApp.loginUser.get("ids");

                    JSONObject queryParams = new JSONObject();
                    // 生成参数。结束======================================

                    // 设置post值。开始=========================
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                            .addFormDataPart("userId", userId)
                            .addFormDataPart("infoType", "sql_sg_category")
                            .addFormDataPart("queryParams", queryParams.toJSONString())
                            .addFormDataPart("page", "" + page)
                            .addFormDataPart("rows", "" + ROWS_PER_PAGE);
                    RequestBody requestBody = multipartBuilder.build();
                    // 设置post值。结束=========================

                    Request.Builder requestBuilder = new Request.Builder();
                    requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                    upHttpRequest = requestBuilder
                            .url("http://" + baseApp.serverAddr + "/"
                                    + CommonParam.URL_SEARCHTABLE)
                            .post(requestBody)
                            .build();
                    if (baseHttpClient == null) {
                        baseHttpClient = new OkHttpClient();
                    }
                    if (upHttpClient == null) {
                        upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                    }

                    upResponse = upHttpClient.newCall(upHttpRequest).execute();
                    if (upResponse.code() == 200) {
                        // 获取成功
                        byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                        // 如果返回的xml中有BOM头，要将其删除
                        if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0]
                                && respBytes[1] == FileUtil.UTF8BOM[1] && respBytes[2] == FileUtil.UTF8BOM[2]) {
                            respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                        } else {
                            respStr = new String(respBytes, "UTF-8");
                        }
                        Log.d("##", "#" + respStr);

                        JSONObject respJson = JSONObject.parseObject(respStr);
                        String resultStr = respJson.getString("result");

                        if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                            // 请求正确
                            JSONArray data = respJson.getJSONArray("data");
                            total = respJson.getIntValue("total");
                            int count = listItems.size();
                            for (int index = 0, len = data.size(); index < len; index++) {
                                HashMap<String, Object> listItem = new HashMap<String, Object>();
                                HashMap<String, Object> info = null;
                                JSONObject json = data.getJSONObject(index);
                                info = CommonUtil.jsonToMap(json);

                                listItem.put("info", info);

                                if (!listItems.contains(listItem)) {
                                    listItems_tmp.add(listItem);
                                }
                            }
                            result = CommonParam.RESULT_SUCCESS;
                        }
                    } else {
                        // 服务器连接失败
                    }
                    // 查询信息。结束====================================================================
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
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
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        infoList = (PullToRefreshListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
