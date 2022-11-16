/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.alibaba.fastjson.JSONArray;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.InsJcHistoryListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡视_现场检查记录_历史记录_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class InsJcHistoryListReverseLandActivity extends DbActivity {
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
    private InsJcHistoryListAdapter infoListAdapter;
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
    /**
     * 任务信息
     */
    private HashMap<String, Object> bizInfo;
    /**
     * 资源信息
     */
    private HashMap<String, Object> resInfo;
    /**
     * 更新标志
     */
    private boolean updateFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = InsJcHistoryListReverseLandActivity.this;

        /// 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        fromFlag = data.getString("fromFlag", "edit");
        bizInfo = (HashMap<String, Object>) data.getSerializable("bizInfo");
        resInfo = (HashMap<String, Object>) data.getSerializable("resInfo");

        setContentView(R.layout.ins_jc_history_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("现场检查记录");

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
        if (infoListAdapter != null) {
            infoListAdapter.notifyDataSetChanged();
        }
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
                infoListAdapter = (InsJcHistoryListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoListAdapter = new InsJcHistoryListAdapter(getApplicationContext(), listItems, R.layout.ins_jc_history_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info", "info", "info"}, new int[]{R.id.infoSn, R.id.infoName,
                            R.id.info_c1, R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.showBtn, R.id.deleteBtn});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new InsJcHistoryListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof Button) {
                                Button btn = (Button) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (btn.getId() == R.id.showBtn) {
                                    btn.setVisibility(View.VISIBLE);
                                    btn.setTag("showBtn_" + info.get("ids"));
                                    btn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Button btn = (Button) v;
                                            btn.setClickable(false);
                                            btn.setEnabled(false);
                                            int index = CommonUtil.getListItemIndex(listItems, ((String) v.getTag()).replace("showBtn_", ""));
                                            HashMap<String, Object> vMap = (HashMap<String, Object>) listItems.get(index);
                                            HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");

                                            // 创建信息传输Bundle
                                            Bundle data = new Bundle();
                                            data.putSerializable("bizInfo", bizInfo);
                                            data.putSerializable("resInfo", resInfo);
                                            data.putSerializable("jcInfo", info);

                                            // 创建启动 Activity 的 Intent
                                            Intent intent = null;
                                            if (!baseApp.isReverseRotate) {
                                                intent = new Intent(classThis, InsJcShowListLandActivity.class);
                                            } else {
                                                intent = new Intent(classThis, InsJcShowListReverseLandActivity.class);
                                            }
                                            // 将数据存入 Intent 中
                                            intent.putExtras(data);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                        }
                                    });
                                } else if (btn.getId() == R.id.deleteBtn) {
                                    if ("edit".equals(fromFlag)) {
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
                                    } else {
                                        btn.setVisibility(View.GONE);
                                    }
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
                                    textView.setText(CommonUtil.N2B((String) info.get("res_title")));
                                } else if (textView.getId() == R.id.info_c1) {
                                    textView.setText(CommonUtil.N2B((String) info.get("areasign")));
                                } else if (textView.getId() == R.id.info_c2) {
                                    textView.setText(CommonUtil.N2B((String) info.get("atime")) + " 至 " + CommonUtil.N2B((String) info.get("btime")));
                                } else if (textView.getId() == R.id.info_c3) {
                                    textView.setText(CommonUtil.N2B((String) info.get("user_name")).split("#")[1]);
                                } else if (textView.getId() == R.id.info_c4) {
                                    // 任务状态
                                    String infoStatus = (String) info.get("V_INFO_STATUS");
                                    if ("0".equals(infoStatus)) {
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
            ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "select * from t_szfgs_sgxunsjcjl model where model.valid='1' and model.biz_id=? and model.res_id=? and model.quid=? order by model.atime desc",
                            new String[]{(String) bizInfo.get("ids"),
                                    (String) resInfo.get("ids"),
                                    (String) baseApp.getLoginUser().get("ids")});
            total = recList.size();
            for (int index = 0, len = recList.size(); index < len; index++) {
                // 存放信息的 Map
                HashMap<String, Object> listItem = new HashMap<String, Object>();
                HashMap<String, Object> info = recList.get(index);

                listItem.put("info", info);

                String atime = CommonUtil.N2B((String) info.get("atime"));
                String btime = CommonUtil.N2B((String) info.get("btime"));

                String infoStatus;
                if (CommonUtil.checkNB(atime) && CommonUtil.checkNB(btime)) {
                    // 已完成
                    infoStatus = "1";
                } else {
                    // 未完成
                    infoStatus = "0";
                }
                info.put("V_INFO_STATUS", infoStatus);

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
                titleText.setText("现场检查记录(" + listItems.size() + ")");
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
            // 附件List
            List<String> attaList = new ArrayList<String>();
            // 记录List
            ArrayList<HashMap<String, Object>> dataList = null;
            infoTool = getInfoTool();

            // 删除记录附件。开始=================================================================
            getAttaList(info, attaList);

            dataList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "select model.photo, model.video, model.audio from t_szfgs_sgxunsjcjl_son model where model.valid='1' and model.jcjl_id=? and model.quid=?",
                            new String[]{(String) info.get("ids"), (String) baseApp.getLoginUser().get("ids")});
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
            infoTool.delete("t_szfgs_sgxunsjcjl", "ids=? and quid=?", new String[]{(String) info.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            infoTool.delete("t_szfgs_sgxunsjcjl_son", "jcjl_id=? and quid=?", new String[]{(String) info.get("ids"), (String) baseApp.getLoginUser().get("ids")});
            // infoTool.delete("t_szfgs_sgxunsloc", "biz_id=? and res_id=?", new String[]{bizId, (String) info.get("res_id")});
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
                updateFlag = true;
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
        infoList = (ListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
