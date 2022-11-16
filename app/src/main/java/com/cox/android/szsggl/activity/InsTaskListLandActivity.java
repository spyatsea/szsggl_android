/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsTaskListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * 巡视_巡视任务_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsTaskListLandActivity extends DbActivity {
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
    /**
     * 返回
     */
    private Button goBackBtn;

    LottieAnimationView animationView;
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
    private InsTaskListAdapter infoListAdapter;
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
    /**
     * 删除信息Dialog
     */
    private AlertDialog deleteInfoDlg;

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

        classThis = InsTaskListLandActivity.this;

        // 获取Intent
        //Intent intent = getIntent();
        // 获取Intent上携带的数据
        //Bundle data = intent.getExtras();

        setContentView(R.layout.ins_task_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("请选择巡视任务");

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
                makeHelpDialog(R.layout.dlg_help_ins_task_list);
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
        } else if (requestCode == CommonParam.REQUESTCODE_LIST && resultCode == CommonParam.RESULTCODE_REFRESH_REC_LIST) {
            if (intent != null) {
                // 创建信息传输Bundle
                Bundle data = intent.getExtras();
                if (data != null) {
                    String msg = data.getString("msg");
                    if (CommonUtil.checkNB(msg)) {
                        show(msg);
                    }
                    Boolean finishFlag = data.getBoolean("finishFlag", false);
                    if (finishFlag) {
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        animationView.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animationView.setVisibility(View.GONE);
                                animationView.setProgress(0.0F);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        if (baseApp.isAutoPlayInsAudio) {
                            if (mediaPlayer == null) {
                                mediaPlayer = getMediaplayer();
                            }
                            playVoice("laba2.wav");
                        }
                    }
                }
            }
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

            // 处理数据。开始============================================================================
            listItems = new ArrayList<HashMap<String, Object>>();
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
                //listTitleTv.setGravity(Gravity.RIGHT);
                //listTitleTv.setText(getString(R.string.pagination_info, 0, 0, 0));
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (InsTaskListAdapter) infoList.getAdapter();
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
                    infoListAdapter = new InsTaskListAdapter(getApplicationContext(), listItems, R.layout.ins_task_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info", "info", "info", "info"}, new int[]{R.id.infoSn, R.id.infoName,
                            R.id.info_c1, R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.info_c5, R.id.insBtn, R.id.deleteBtn});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new InsTaskListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof Button) {
                                Button btn = (Button) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (btn.getId() == R.id.insBtn) {
                                    // 任务状态
                                    String infoStatus = (String) info.get("V_INFO_STATUS");
                                    if ("0".equals(infoStatus)) {
                                        // 未巡视
                                        btn.setText("开始巡视");
                                        btn.setBackgroundResource(R.drawable.custom_btn_card_blue);
                                    } else if ("1".equals(infoStatus)) {
                                        // 未完成
                                        btn.setText("继续巡视");
                                        btn.setBackgroundResource(R.drawable.custom_btn_card_purple);
                                    } else {
                                        // 已完成
                                        btn.setText("查阅");
                                        btn.setBackgroundResource(R.drawable.custom_btn_card_green);
                                    }
                                    btn.setTag("insBtn_" + info.get("ids"));
                                    btn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Button btn = (Button) v;
                                            btn.setClickable(false);
                                            btn.setEnabled(false);
                                            String id = ((String) v.getTag()).replace("insBtn_", "");
                                            int index = CommonUtil.getListItemIndex(listItems, id);
                                            HashMap<String, Object> vMap = (HashMap<String, Object>) listItems.get(index);
                                            // 任务信息
                                            HashMap<String, Object> bizInfo = (HashMap<String, Object>) vMap.get("info");
                                            // 任务状态
                                            String infoStatus = (String) bizInfo.get("V_INFO_STATUS");
                                            if ("0".equals(infoStatus)) {
                                                // 未巡视
                                                // 准备巡视数据
                                                new PrepareEditInsPointDataTask().execute(id, bizInfo);
                                            } else if ("1".equals(infoStatus)) {
                                                // 未完成
                                                // 准备巡视数据
                                                new PrepareEditInsPointDataTask().execute(id, bizInfo);
                                            } else {
                                                // 已完成
                                                // 准备巡视数据
                                                new PrepareShowInsPointDataTask().execute(id, bizInfo);
                                            }

                                        }
                                    });
                                } else if (btn.getId() == R.id.deleteBtn) {
                                    btn.setVisibility(View.VISIBLE);
                                    btn.setTag("deleteBtn_" + info.get("ids"));
                                    btn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Button btn = (Button) v;
                                            btn.setClickable(false);
                                            btn.setEnabled(false);
                                            makeDeleteInfoDialog(((String) v.getTag()).replace("deleteBtn_", ""));
                                        }
                                    });
                                }
                                return true;
                            } else if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.infoSn) {
                                    int index = CommonUtil.getListItemIndex(listItems, info);
                                    textView.setText("" + (index + 1));
                                } else if (textView.getId() == R.id.infoName) {
                                    textView.setText(CommonUtil.N2B((String) info.get("title")));
                                } else if (textView.getId() == R.id.info_c1) {
                                    // 检查类别
                                    String ctype = (String) info.get("ctype");
                                    String ctype_name = null;
                                    if ("2".equals(ctype)) {
                                        ctype_name = "停水检查";
                                    } else if ("3".equals(ctype)) {
                                        ctype_name = "特殊检查";
                                    } else {
                                        ctype_name = "日常检查";
                                    }
                                    textView.setText(ctype_name);
                                } else if (textView.getId() == R.id.info_c2) {
                                    textView.setText(CommonUtil.N2B((String) info.get("planatime")) + " 至 " + CommonUtil.N2B((String) info.get("planbtime")));
                                } else if (textView.getId() == R.id.info_c3) {
                                    textView.setText(CommonUtil.N2B((String) info.get("user_name")).replace("#", ","));
                                } else if (textView.getId() == R.id.info_c4) {
                                    textView.setText(CommonUtil.N2B((String) info.get("renw_desc")));
                                } else if (textView.getId() == R.id.info_c5) {
                                    // 任务状态
                                    String infoStatus = (String) info.get("V_INFO_STATUS");
                                    if ("0".equals(infoStatus)) {
                                        textView.setText("未巡视");
                                        textView.setTextColor(getResources().getColor(R.color.text_red));
                                    } else if ("1".equals(infoStatus)) {
                                        textView.setText("未完成");
                                        textView.setTextColor(getResources().getColor(R.color.text_purple));
                                    } else {
                                        textView.setText("已完成");
                                        textView.setTextColor(getResources().getColor(R.color.text_green_dark));
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
//                                    .getItemAtPosition(position + 1);
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
                                "select * from t_biz_sgxuns model where model.valid='1' and (model.fzr=? or INSTR(model.ryap, ?)>0) and model.quid=? order by model.planatime desc",
                                new String[]{(String) baseApp.getLoginUser().get("ids"),
                                        (String) baseApp.getLoginUser().get("ids"),
                                        (String) baseApp.getLoginUser().get("ids")});
                total = recList.size();
                for (int index = 0, len = recList.size(); index < len; index++) {
                    // 存放信息的 Map
                    HashMap<String, Object> listItem = new HashMap<String, Object>();
                    HashMap<String, Object> info = recList.get(index);

                    listItem.put("info", info);

                    String realatime = CommonUtil.N2B((String) info.get("realatime"));
                    String realbtime = CommonUtil.N2B((String) info.get("realbtime"));

                    String infoStatus;
                    if (CommonUtil.checkNB(realatime) && CommonUtil.checkNB(realbtime)) {
                        // 已完成
                        infoStatus = "2";
                    } else if (CommonUtil.checkNB(realatime) && !CommonUtil.checkNB(realbtime)) {
                        // 未完成
                        infoStatus = "1";
                    } else {
                        // 未巡视
                        infoStatus = "0";
                    }
                    info.put("V_INFO_STATUS", infoStatus);

                    listItems_tmp.add(listItem);

                    // 检查附件目录。开始=========================
                    String temp_save = (String) info.get("temp_save");
                    File temp_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins/" + temp_save);
                    if (!temp_dir.exists()) {
                        temp_dir.mkdir();
                    }
                    // 检查附件目录。结束=========================
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
                    //listTitleTv.setText(getString(R.string.pagination_info, (listItems.size() > 0 ? 1 : 0), listItems.size(), total));
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
                    //listTitleTv.setText(getString(R.string.pagination_info, (listItems.size() > 0 ? 1 : 0), listItems.size(), total));
                }
            } else {
                show("数据加载失败");
            }
            isConnecting = false;
        }
    }

    /**
     * 显示删除信息对话框
     *
     * @param id {@code String} 信息编号
     */
    public void makeDeleteInfoDialog(String id) {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.alert_whether_delete_info_and_atta);
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

        deleteInfoDlg = dlgBuilder.create();
        deleteInfoDlg.show();
        // 确定按钮
        Button confirmBtn = deleteInfoDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = deleteInfoDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        // 存放Dialog所需信息的Map
        Map<String, Object> tag = new HashMap<String, Object>();
        tag.put("id", id);
        // 绑定数据
        confirmBtn.setTag(tag);
        Button deleteBtn = infoList.findViewWithTag("deleteBtn_" + id);
        deleteBtn.setClickable(true);
        deleteBtn.setEnabled(true);

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 存放Dialog所需信息的Map
                Map<String, Object> tag = (HashMap<String, Object>) v.getTag();
                // 信息编号
                String id = (String) tag.get("id");
                // 删除信息
                new DeleteInfoTask().execute(id);
                deleteInfoDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteInfoDlg.cancel();
            }
        });
    }

    /**
     * 删除信息的 AsyncTask 类
     */
    public class DeleteInfoTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：删除成功
         */
        private static final int PROGRESS_DELETE_SUCCESS = 1001;
        /**
         * 进度常量：删除失败
         */
        private static final int PROGRESS_DELETE_FAIL = 1002;
        /**
         * 信息
         */
        private HashMap<String, Object> info;
        /**
         * 信息编号
         */
        private String id;
        /**
         * 信息索引
         */
        private int position;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            makeWaitDialog("正在删除，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            id = (String) params[0];
            position = CommonUtil.getListItemIndex(listItems, id);
            HashMap<String, Object> vMap = (HashMap<String, Object>) listItems.get(position);
            info = (HashMap<String, Object>) vMap.get("info");
            String bizId = (String) info.get("ids");
            // 附件List
            List<String> attaList = new ArrayList<String>();
            // 记录List
            ArrayList<HashMap<String, Object>> dataList = null;
            infoTool = getInfoTool();

            // 删除语音附件。开始=================================================================
            String atta = (String) info.get("atta");
            if (CommonUtil.checkNB(atta)) {
                JSONObject audioInfo = JSONObject.parseObject(atta);
                if (audioInfo != null) {
                    String audioName = audioInfo.getString("name");
                    if (CommonUtil.checkNB(audioName)) {
                        File attaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                                + CommonParam.PROJECT_NAME + "/ins/" + audioName);
                        if (attaFile.exists() && attaFile.isFile()) {
                            attaFile.delete();
                        }
                    }
                }
            }
            // 删除语音附件。结束=================================================================

            // 删除业务附件。开始=================================================================
            String attachment = (String) info.get("attachment");
            if (CommonUtil.checkNB(attachment)) {
                String temp_save = (String) info.get("temp_save");
                File temp_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/" + temp_save);
                if (temp_dir.exists() && temp_dir.isDirectory()) {
                    File[] attaFiles = temp_dir.listFiles();
                    for (File attaFile : attaFiles) {
                        if (attaFile.exists() && attaFile.isFile()) {
                            attaFile.delete();
                        }
                    }
                    temp_dir.delete();
                }
            }
            // 删除业务附件。结束=================================================================

            // 删除记录附件。开始=================================================================
            dataList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "select model.ids, model.photo, model.video, model.audio from t_szfgs_sgxunsjcjl model where model.valid='1' and model.biz_id=? and model.quid=?",
                            new String[]{bizId, (String) baseApp.getLoginUser().get("ids")});
            // 水工巡视检查记录编号
            StringBuffer jcIdSb = new StringBuffer();
            for (HashMap<String, Object> o : dataList) {
                getAttaList(o, attaList);
                jcIdSb.append(",'" + o.get("ids") + "'");
            }
            if (jcIdSb.length() > 0) {
                jcIdSb.delete(0, 1);
            }

            dataList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "select model.photo, model.video, model.audio from t_szfgs_sgxunsjcjl_son model where model.valid='1' and model.jcjl_id in(" + jcIdSb.toString() + ") and model.quid=?",
                            new String[]{(String) baseApp.getLoginUser().get("ids")});
            for (HashMap<String, Object> o : dataList) {
                getAttaList(o, attaList);
            }

            dataList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "select model.photo, model.video, model.audio from t_szfgs_sgxunsqdjl model where model.valid='1' and model.biz_id=? and model.quid=?",
                            new String[]{bizId, (String) baseApp.getLoginUser().get("ids")});
            for (HashMap<String, Object> o : dataList) {
                getAttaList(o, attaList);
            }

            for (String file_name : attaList) {
                File attaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/" + file_name);
                if (attaFile.exists() && attaFile.isFile()) {
                    attaFile.delete();
                }
            }
            // 删除记录附件。结束=================================================================

            // 删除信息。开始=================================================================
            infoTool.delete("t_biz_sgxuns", "ids=? and quid=?", new String[]{bizId, (String) baseApp.getLoginUser().get("ids")});
            infoTool.delete("t_szfgs_sgxunsjcjl", "biz_id=? and quid=?", new String[]{bizId, (String) baseApp.getLoginUser().get("ids")});
            infoTool.delete("t_szfgs_sgxunsjcjl_son", "jcjl_id in(" + jcIdSb.toString() + ") and quid=?", new String[]{(String) baseApp.getLoginUser().get("ids")});
            infoTool.delete("t_szfgs_sgxunsqdjl", "biz_id=? and quid=?", new String[]{bizId, (String) baseApp.getLoginUser().get("ids")});
            infoTool.delete("t_szfgs_sgxunsloc", "biz_id=? and quid=?", new String[]{bizId, (String) baseApp.getLoginUser().get("ids")});
            // 删除信息。结束=================================================================

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

            // 更新列表
            if (result.equals(CommonParam.RESULT_SUCCESS)) {
                listItems.remove(position);
                infoListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 获得附件信息
     */
    public void getAttaList(HashMap<String, Object> info, List<String> attaList) {
        // 图片
        String photo = (String) info.get("photo");
        // 视频
        String video = (String) info.get("video");
        // 音频
        String audio = (String) info.get("audio");

        if (CommonUtil.checkNB(photo)) {
            JSONArray ps = JSONArray.parseArray(photo);
            for (int i = 0, len = ps.size(); i < len; i++) {
                String fileName = ps.getJSONObject(i).getString("name");
                if (CommonUtil.checkNB(fileName)) {
                    attaList.add(fileName);
                }
            }
        }

        if (CommonUtil.checkNB(video)) {
            JSONArray ps = JSONArray.parseArray(video);
            for (int i = 0, len = ps.size(); i < len; i++) {
                String fileName = ps.getJSONObject(i).getString("name");
                if (CommonUtil.checkNB(fileName)) {
                    attaList.add(fileName);
                }
            }
        }

        if (CommonUtil.checkNB(audio)) {
            JSONArray ps = JSONArray.parseArray(audio);
            for (int i = 0, len = ps.size(); i < len; i++) {
                String fileName = ps.getJSONObject(i).getString("name");
                if (CommonUtil.checkNB(fileName)) {
                    attaList.add(fileName);
                }
            }
        }
    }

    /**
     * 准备编辑状态巡视点数据的 AsyncTask 类
     */
    public class PrepareEditInsPointDataTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：资源不足
         */
        private static final int PROGRESS_RES_LESS = 1001;
        /**
         * 信息编号
         */
        private String id;
//        /**
//         * 信息索引
//         */
//        private int position;
        /**
         * 任务信息
         */
        private HashMap<String, Object> bizInfo;
        /**
         * 需要巡视的资源数量
         */
        private int res_total = 0;
        /**
         * 设备数据库中能找到的资源数量
         */
        private int res_found_total = 0;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            makeWaitDialog("正在准备巡视数据，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            id = (String) params[0];
//            position = CommonUtil.getListItemIndex(listItems, id);
            bizInfo = (HashMap<String, Object>) params[1];
            // 资源编号
            String res_id = (String) bizInfo.get("res_id");
            StringBuffer sb = new StringBuffer();
            if (CommonUtil.checkNB(res_id)) {
                String[] res_array = res_id.split(",");
                for (String _id : res_array) {
                    if (CommonUtil.checkNB(_id)) {
                        res_total++;
                        sb.append(",'" + _id + "'");
                    }
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(0);
                }
            }

            infoTool = getInfoTool();
            res_found_total = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1' and model.ids in ("
                    + sb.toString() + ")", new String[]{});

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
            if (progress[0] == PROGRESS_RES_LESS) {
                // 资源不足
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

            // 巡视按钮
            Button btn = infoList.findViewWithTag("insBtn_" + id);
            if (btn != null) {
                btn.setClickable(true);
                btn.setEnabled(true);
            }

            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (res_found_total < res_total) {
                    makeAlertDialog("本次任务需要巡视 " + res_total + " 个巡视点，本设备中只有 " + res_found_total
                            + " 个巡视点的信息。请先同步基础信息！");
                } else {
                    // 创建信息传输Bundle
                    Bundle data = new Bundle();
                    data.putSerializable("bizInfo", bizInfo);

                    // 创建启动 Activity 的 Intent
                    Intent intent = null;
                    if (!baseApp.isReverseRotate) {
                        intent = new Intent(classThis, InsPointEditListLandActivity.class);
                    } else {
                        intent = new Intent(classThis, InsPointEditListReverseLandActivity.class);
                    }
                    // 将数据存入 Intent 中
                    intent.putExtras(data);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                    //finish();
                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                }
            }
        }
    }

    /**
     * 准备查阅状态巡视点数据的 AsyncTask 类
     */
    public class PrepareShowInsPointDataTask extends AsyncTask<Object, Integer, String> {
        /**
         * 信息编号
         */
        private String id;
//        /**
//         * 信息索引
//         */
//        private int position;
        /**
         * 任务信息
         */
        private HashMap<String, Object> bizInfo;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            makeWaitDialog("正在准备巡视数据，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            id = (String) params[0];
//            position = CommonUtil.getListItemIndex(listItems, id);
            bizInfo = (HashMap<String, Object>) params[1];

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

            // 巡视按钮
            Button btn = infoList.findViewWithTag("insBtn_" + id);
            if (btn != null) {
                btn.setClickable(true);
                btn.setEnabled(true);
            }

            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("bizInfo", bizInfo);

                // 创建启动 Activity 的 Intent
                Intent intent = null;
                if (!baseApp.isReverseRotate) {
                    intent = new Intent(classThis, InsPointShowListLandActivity.class);
                } else {
                    intent = new Intent(classThis, InsPointShowListReverseLandActivity.class);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                //finish();
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        contentView = (LinearLayout) findViewById(R.id.contentView);
        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        helpBtn = (ImageButton) findViewById(R.id.helpBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        infoList = (PullToRefreshListView) findViewById(R.id.infoList);
        animationView = (LottieAnimationView) findViewById(R.id.animationView);
        // 界面相关参数。结束===============================
    }
}
