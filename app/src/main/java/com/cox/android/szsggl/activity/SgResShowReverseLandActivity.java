/*
 * Copyright (c) 2021 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.handler.HtmlTagHandler;
import com.cox.android.szsggl.R;
import com.cox.android.utils.SnackbarUtil;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.github.johnkil.print.PrintView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * 水工资源信息_查阅页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class SgResShowReverseLandActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 表格总数
     */
    private final int TABLE_TOTAL = 2;
    /**
     * 主界面
     */
    ScrollView contentView;
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
    // 界面相关参数。开始===============================
    private TextView pResNameTv;
    private TextView titleTv;
    private ImageButton showCatButton;
    private TextView catNameTv;
    private TextView pxbhTv;
    private TextView bmNameTv;
    private TextView activeTv;
    private TextView loctypeTv;
    private TextView locTv;
    private TextView z01Tv;
    private TextView z02Tv;
    private TextView z03Tv;
    private TextView z04Tv;
    private TextView z05Tv;
    private TextView z06Tv;
    private TextView z07Tv;
    private TextView z08Tv;
    private TextView z09Tv;
    private TextView z10Tv;
    private TextView z11Tv;
    private TextView z12Tv;
    private TextView z13Tv;
    private TextView z14Tv;
    private TextView z15Tv;
    ImageButton lnglatMapButton;
    private TextView memoTv;
    private Button memoTableBtn;
    private LinearLayout areaSignTableLayout;
    private LinearLayout memoTableLayout;
    private LinearLayout attaTitleLayout;
    private TextView attaNumTv;
    private Button continueDownloadAllBtn;
    private Button reDownloadAllBtn;
    private PrintView attaHelpBtn;
    private LinearLayout attaListLayout;
    /**
     * 弹出菜单
     */
    private PopupMenu attaPopupMenu;
    // 界面相关参数。结束===============================

    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;

    // 子列表相关参数。开始===============================
    /**
     * 已经生成的表格数量
     */
    private int table_count = 1;
    /**
     * 表格中的单选框
     */
    private List<RadioButton> table_radioButtonList;
    // 子列表相关参数。结束===============================

    // 附件相关参数。开始===============================
    /**
     * 附件 List
     */
    private List<HashMap<String, Object>> attaList;
    /**
     * 存放下载附件任务的Map
     */
    HashMap<String, AsyncTask<Object, Integer, String>> downloadAttaTaskMap;
    /**
     * 待下载附件的List
     */
    ArrayList<HashMap<String, Object>> downloadAttaList;
    /**
     * 当前正在下载的文件
     */
    File currentDownloadingFile;
    // 附件相关参数。结束===============================

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

        classThis = SgResShowReverseLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");
        fromFlagType = data.getString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        setContentView(R.layout.sg_res_show);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("水工资源：查阅");

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
        showCatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String cid = (String) infoObj.get("cid");
                if (CommonUtil.checkNB(cid)) {
                    // 创建信息传输Bundle
                    Bundle data = new Bundle();
                    data.putString("id", cid);
                    data.putString("fromFlag", "sgres");
                    data.putString("fromFlagType", fromFlagType);

                    // 创建启动 Activity 的 Intent
                    Intent intent = null;
                    if (Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).equals(fromFlagType)) {
                        intent = new Intent(classThis, SgCategoryShowActivity.class);
                    } else if (Integer.toString(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE).equals(fromFlagType)) {
                        intent = new Intent(classThis, SgCategoryShowReverseLandActivity.class);
                    } else {
                        intent = new Intent(classThis, SgCategoryShowLandActivity.class);
                    }

                    // 将数据存入 Intent 中
                    intent.putExtras(data);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                }
            }
        });
        lnglatMapButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet(false)) {
                    // 创建信息传输Bundle
                    Bundle data = new Bundle();
                    data.putString("id", infoId);
                    data.putSerializable("info", infoObj);
                    data.putString("fromFlag", "sgres");
                    data.putString("fromFlagType", fromFlagType);

                    // 创建启动 Activity 的 Intent
                    Intent intent = null;
                    if (Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).equals(fromFlagType)) {
                        intent = new Intent(classThis, SgResMapActivity.class);
                    } else if (Integer.toString(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE).equals(fromFlagType)) {
                        intent = new Intent(classThis, SgResMapReverseLandActivity.class);
                    } else {
                        intent = new Intent(classThis, SgResMapLandActivity.class);
                    }

                    // 将数据存入 Intent 中
                    intent.putExtras(data);
                    startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                    overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                } else {
                    makeAlertDialog("当前没有网络连接，无法加载地图！");
                }
            }
        });
        memoTableBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                Animation animation = null;
                if (memoTableLayout.getVisibility() == View.VISIBLE) {
                    btn.setTag(false);
                    animation = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_out);
                } else {
                    btn.setTag(true);
                    memoTableLayout.setVisibility(View.VISIBLE);
                    animation = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_in);
                }
                btn.setClickable(false);
                btn.setEnabled(false);
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
                        Button btn = memoTableBtn;
                        btn.setClickable(true);
                        btn.setEnabled(true);
                        boolean flag = (Boolean) btn.getTag();
                        if (flag) {
                            btn.setText("∧收起");
                        } else {
                            btn.setText("∨展开");
                            memoTableLayout.setVisibility(View.GONE);
                        }
                    }
                });
                // 运行动画
                memoTableLayout.startAnimation(animation);
            }
        });
        continueDownloadAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                continueDownloadAllBtn.setClickable(false);
                continueDownloadAllBtn.setEnabled(false);
                AsyncTask<Object, Integer, String> checkUnDownloadAttaListTask = new CheckUnDownloadAttaListTask();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        checkUnDownloadAttaListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        checkUnDownloadAttaListTask.execute();
                    }
                } catch (RejectedExecutionException ree) {
                }
            }
        });
        reDownloadAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reDownloadAllBtn.setClickable(false);
                reDownloadAllBtn.setEnabled(false);
                AsyncTask<Object, Integer, String> reDownloadAllAttaListTask = new ReDownloadAllAttaListTask();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        reDownloadAllAttaListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        reDownloadAllAttaListTask.execute();
                    }
                } catch (RejectedExecutionException ree) {
                }
            }
        });
        attaHelpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeHelpDialog(R.layout.dlg_help_download_all);
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
        if (downloadAttaTaskMap != null) {
            for (Map.Entry<String, AsyncTask<Object, Integer, String>> e : downloadAttaTaskMap.entrySet()) {
                AsyncTask<Object, Integer, String> task = e.getValue();
                task.cancel(true);
            }
        }
        downloadAttaList.clear();
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

        // 父资源名称
        private String pResName;
        // 分类名称
        private String catName;
        // 组织名称
        private String bmName;
        // 定位方式
        private String loctypeName;

        /**
         * 子表格数据包
         */
        private Map<String, Object> areaSign_dataPack;
        private Map<String, Object> memo_dataPack;

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
            downloadAttaTaskMap = new HashMap<String, AsyncTask<Object, Integer, String>>();
            downloadAttaList = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT * FROM t_szfgs_sgres model WHERE model.valid='1' and model.ids=?",
                            new String[]{infoId});
            if (recList.size() > 0) {
                infoObj = recList.get(0);
            }
            if (infoObj == null) {
                return result;
            }

            // 父资源编号
            String pid = (String) infoObj.get("pid");
            if (CommonUtil.checkNB(pid)) {
                pResName = infoTool
                        .getSingleVal(
                                "SELECT model.title FROM t_szfgs_sgres model WHERE model.valid='1' and model.ids=?",
                                new String[]{pid});
            }

            // 分类编号
            String cid = (String) infoObj.get("cid");
            if (CommonUtil.checkNB(cid)) {
                catName = infoTool
                        .getSingleVal(
                                "SELECT model.title FROM t_szfgs_sgcategory model WHERE model.valid='1' and model.ids=?",
                                new String[]{cid});
            }

            // 组织编号
            String bmid = (String) infoObj.get("bmid");
            if (CommonUtil.checkNB(cid)) {
                bmName = infoTool
                        .getSingleVal(
                                "SELECT model.title FROM t_base_deptinfo model WHERE model.valid='1' and model.ids=?",
                                new String[]{bmid});
            }

            // 定位方式
            loctypeName = infoTool.getSingleVal("SELECT model.zdname FROM t_base_code model WHERE model.valid='1' and model.type=? and model.zdcode=?",
                    new String[]{"朔州分公司_水工资源定位方式", (String) infoObj.get("loctype")});


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

            // ◇areaSign。开始================================================================
            ArrayList<HashMap<String, Object>> areaSignList = (ArrayList<HashMap<String, Object>>) infoTool
                    .getInfoMapList(
                            "SELECT model.n, model.title, model.loc FROM t_szfgs_sgresareasign model WHERE model.valid='1' and model.rid=?",
                            new String[]{infoId});
            if (areaSignList.size() > 0) {
                dataList = new JSONArray();
                // 处理信息
                for (HashMap<String, Object> o : areaSignList) {
                    o.put("_n", o.get("n"));
                    o.remove("n");
                    dataList.add(CommonUtil.mapToJson(o));
                }
                areaSignList = null;

                columnNameArray = new String[]{" ", "区域名称", "定位参数"};
                columnFieldArray = new String[]{"_n", "title", "loc"};
                tableViewId = R.id.areaSignTableLayout;
                listDataType = CommonParam.LIST_DATA_TYPE_MAP;
                tableStyle = CommonParam.LIST_STYLE.BLUE_01();

                areaSign_dataPack = new HashMap<String, Object>();
                areaSign_dataPack.put("dataList", dataList);
                areaSign_dataPack.put("columnNameArray", columnNameArray);
                areaSign_dataPack.put("columnFieldArray", columnFieldArray);
                areaSign_dataPack.put("columnImageList", columnImageList);
                areaSign_dataPack.put("tableViewId", tableViewId);
                areaSign_dataPack.put("listDataType", listDataType);
                areaSign_dataPack.put("tableStyle", tableStyle);
                areaSign_dataPack.put("headerGravity", Gravity.CENTER);
                areaSign_dataPack.put("columnGravity", Gravity.LEFT | Gravity.CENTER_VERTICAL);
                areaSign_dataPack.put("propertyFlag", false);
            }
            // ◇areaSign。结束================================================================

            // ◇memo。开始================================================================
            dataStr = (String) infoObj.get("history");
            if (CommonUtil.checkNB(dataStr)) {
                dataStr = dataStr.replaceAll("<br>", "\n").replaceAll("<BR>", "\n").replaceAll("<br/>", "\n")
                        .replaceAll("<BR/>", "\n");
                dataList = JSONArray.parseArray(dataStr);

                columnNameArray = new String[]{"№"};
                columnFieldArray = new String[]{};
                tableViewId = R.id.memoTableLayout;
                listDataType = CommonParam.LIST_DATA_TYPE_STRING_ARRAY;
                tableStyle = CommonParam.LIST_STYLE.GREY_01();

                memo_dataPack = new HashMap<String, Object>();
                memo_dataPack.put("dataList", dataList);
                memo_dataPack.put("columnNameArray", columnNameArray);
                memo_dataPack.put("columnFieldArray", columnFieldArray);
                memo_dataPack.put("columnImageList", columnImageList);
                memo_dataPack.put("tableViewId", tableViewId);
                memo_dataPack.put("listDataType", listDataType);
                memo_dataPack.put("tableStyle", tableStyle);
                memo_dataPack.put("headerGravity", Gravity.CENTER);
                memo_dataPack.put("columnGravity", Gravity.LEFT | Gravity.TOP);
                memo_dataPack.put("propertyFlag", false);
            }
            // ◇memo。结束================================================================

            // 下载附件。开始================================================================
//            // 待下载的附件List
//            List<String> attaFileList = new ArrayList<String>();
//            // 附件
//            String _attachment = (String) infoObj.get("attachment");
//
//            if (CommonUtil.checkNB(_attachment)) {
//                JSONArray _aArray = JSONArray.parseArray(_attachment);
//                for (int i = 0, len = _aArray.size(); i < len; i++) {
//                    String fileName = _aArray.getJSONObject(i).getString("file");
//                    if (CommonUtil.checkNB(fileName)) {
//                        attaFileList.add(fileName);
//                    }
//                }
//            }
//            // 附件保存目录
//            File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
//                    + CommonParam.PROJECT_NAME + "/atta");
//            for (int i = 0, len = attaFileList.size(); i < len; i++) {
//                String _file = attaFileList.get(i);
//                File attaFile = new File(saveDir_atta, _file);
//
//                if (!attaFile.exists()) {
//                    Map<String, Object> downloadResult = downloadFile("http://" + baseApp.serverAddr
//                            + "/UploadFiles/SgResAtta/" + _file, attaFile.getAbsolutePath(), null);
//                }
//            }
            // 下载附件。结束================================================================
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
                if (Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).equals(fromFlagType)) {
                    homeBtn.setVisibility(View.VISIBLE);
                } else {
                    homeBtn.setVisibility(View.GONE);
                }
                pResNameTv.setText(CommonUtil.N2B(pResName));
                titleTv.setText(CommonUtil.N2B((String) infoObj.get("title")));
                catNameTv.setText(CommonUtil.N2B(catName));
                pxbhTv.setText(CommonUtil.N2B((String) infoObj.get("pxbh")));
                bmNameTv.setText(CommonUtil.N2B(bmName));
                activeTv.setText(CommonParam.YES.equals((String) infoObj.get("active")) ? "是" : "否");
                loctypeTv.setText(CommonUtil.N2B(loctypeName));
                locTv.setText(CommonUtil.N2B((String) infoObj.get("loc")));

                z01Tv.setText(CommonUtil.N2B((String) infoObj.get("z01")));
                z02Tv.setText(CommonUtil.N2B((String) infoObj.get("z02")));
                z03Tv.setText(CommonUtil.N2B((String) infoObj.get("z03")));
                z04Tv.setText(CommonUtil.N2B((String) infoObj.get("z04")));
                z05Tv.setText(CommonUtil.N2B((String) infoObj.get("z05")));
                z06Tv.setText(CommonUtil.N2B((String) infoObj.get("z06")));
                z07Tv.setText(CommonUtil.N2B((String) infoObj.get("z07")));
                z08Tv.setText(CommonUtil.N2B((String) infoObj.get("z08")));
                z09Tv.setText(CommonUtil.N2B((String) infoObj.get("z09")));
                z10Tv.setText(CommonUtil.N2B((String) infoObj.get("z10")));
                z11Tv.setText(CommonUtil.N2B((String) infoObj.get("z11")));
                z12Tv.setText(CommonUtil.N2B((String) infoObj.get("z12")));
                z13Tv.setText(CommonUtil.N2B((String) infoObj.get("z13")));
                z14Tv.setText(CommonUtil.N2B((String) infoObj.get("z14")));
                z15Tv.setText(CommonUtil.N2B((String) infoObj.get("z15")));

                memoTv.setText(CommonUtil.N2B((String) infoObj.get("memo")));
                TextView z06TitleTv = (TextView) findViewById(R.id.z06TitleTv);
                z06TitleTv.setText(Html.fromHtml("面　　积(m<sup>2</sup>):", null, new HtmlTagHandler(classThis)));
                TextView z11TitleTv = (TextView) findViewById(R.id.z11TitleTv);
                z11TitleTv.setText(Html.fromHtml("设计流量(m<sup>3</sup>/s):", null, new HtmlTagHandler(classThis)));
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
            } else if (progress[0] == PROGRESS_MAKE_SUB_LIST) {
                // 生成子列表
                new MakeListTableTask().execute(areaSign_dataPack);
                JSONArray dataList = null;
                if (memo_dataPack != null) {
                    dataList = (JSONArray) memo_dataPack.get("dataList");
                    if (dataList.size() > 0) {
                        memoTableBtn.setVisibility(View.VISIBLE);
                    }
                }
                if (dataList == null || dataList.size() == 0) {
                    memoTableBtn.setVisibility(View.GONE);
                }
                new MakeListTableTask().execute(memo_dataPack);
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
                new MakeAttaListTask().execute();
            } else {
                show("信息错误！");
                // goBack();
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
                        // 列字段
                        String columnField = null;
                        if (columnFieldArray.length > 0) {
                            if (columnWeightArray.length > 0) {
                                float columnWeight = columnWeightArray[headerIndex];
                                lp.weight = columnWeight;
                            } else {
                                columnField = columnFieldArray[headerIndex];
                                if ("_n".equals(columnField)) {
                                    // 序号列
                                    lp.weight = 1.0f;
                                } else {
                                    lp.weight = 5.0f;
                                }
                            }
                        } else {
                            lp.weight = 1.0f;
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
                        // 列字段
                        String columnField = null;
                        if (columnFieldArray.length > 0) {
                            if (columnWeightArray.length > 0) {
                                float columnWeight = columnWeightArray[cIndex];
                                lp.weight = columnWeight;
                            } else {
                                columnField = columnFieldArray[cIndex];
                                if ("_n".equals(columnField)) {
                                    // 序号列
                                    lp.weight = 1.0f;
                                } else {
                                    lp.weight = 5.0f;
                                }
                            }
                        } else {
                            lp.weight = 1.0f;
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
                                // 如果是序号列，就按标题列的对齐方式
                                if (CommonUtil.checkNB(columnField) && "_n".equals(columnField) && headerGravity != null) {
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
     * 生成附件列表的 AsyncTask 类
     */
    private class MakeAttaListTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：显示附件列表
         */
        private static final int PROGRESS_SHOW_ATTA_LIST = 1001;

        /**
         * 进度常量：建立附件列表
         */
        private static final int PROGRESS_MAKE_ATTA_LIST = 1002;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在生成附件列表，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            attaList = new ArrayList<HashMap<String, Object>>();
            String attachment = (String) infoObj.get("attachment");
            if (CommonUtil.checkNB(attachment)) {
                JSONArray ps = JSONArray.parseArray(attachment);
                for (int i = 0, len = ps.size(); i < len; i++) {
                    JSONObject o = ps.getJSONObject(i);
                    attaList.add(CommonUtil.jsonToMap(o));
                }
            }

            publishProgress(PROGRESS_SHOW_ATTA_LIST);

            if (attaList.size() > 0) {
                // 有附件
                publishProgress(PROGRESS_MAKE_ATTA_LIST);
            }

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
            if (progress[0] == PROGRESS_SHOW_ATTA_LIST) {
                // 显示附件列表
                if (attaList.size() == 0) {
                    attaTitleLayout.setVisibility(View.GONE);
                    attaListLayout.setVisibility(View.GONE);
                } else {
                    attaTitleLayout.setVisibility(View.VISIBLE);
                    attaListLayout.setVisibility(View.VISIBLE);
                    attaNumTv.setText("(" + attaList.size() + ")");
                }
            } else if (progress[0] == PROGRESS_MAKE_ATTA_LIST) {
                // 建立附件列表
                // 附件保存目录
                File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/atta");
                for (int i = 0, len = attaList.size(); i < len; i++) {
                    HashMap<String, Object> o = attaList.get(i);

                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.atta_list_item, null);
                    // 附件名称
                    TextView infoName = (TextView) layout.findViewById(R.id.infoName);
                    // 附件大小
                    TextView infoSize = (TextView) layout.findViewById(R.id.infoSize);
                    // 行分隔符
                    View splitterView = (View) layout.findViewById(R.id.splitterView);
                    // 图标
                    // ImageView infoIcon = (ImageView) layout.findViewById(R.id.infoIcon);
                    // 附件状态
                    TextView infoStatus = (TextView) layout.findViewById(R.id.infoStatus);
                    // 下载状态
                    ProgressBar infoStatusBar = (ProgressBar) layout.findViewById(R.id.infoStatusBar);

                    infoName.setText((String) o.get("name"));
                    infoSize.setText((String) o.get("size"));
                    // 信息Tag
                    HashMap<String, Object> tag = new HashMap<String, Object>();
                    tag.put("i", i);
                    tag.put("o", o);
                    // 是否强制下载
                    tag.put("f", false);
                    layout.setTag("ATTA_" + i);
                    infoName.setTag(tag);
                    infoStatusBar.setVisibility(View.GONE);

                    layout.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> tag = (HashMap<String, Object>) v.findViewById(R.id.infoName).getTag();
                            HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");

                            String fileName = (String) o.get("file");
                            String fileAlias = (String) o.get("name");
                            if (CommonUtil.checkNB(fileName)) {
                                File attaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/" + CommonParam.PROJECT_NAME + "/atta/" + fileName);
                                if (!(currentDownloadingFile != null && currentDownloadingFile.getAbsolutePath().equals(attaFile.getAbsolutePath()))) {
                                    // 如果没有正在下载该文件，才可以继续后面的操作
                                    if (CommonUtil.checkEndsWithInStringArray(fileName,
                                            getResources().getStringArray(R.array.fileEndingWps))
                                            || CommonUtil.checkEndsWithInStringArray(fileName, getResources()
                                            .getStringArray(R.array.fileEndingEt))
                                            || CommonUtil.checkEndsWithInStringArray(fileName, getResources()
                                            .getStringArray(R.array.fileEndingDps))) {
                                        CommonUtil.editWpsFile(attaFile, classThis);
                                    } else {
                                        CommonUtil.openAttaFile(attaFile, fileAlias, classThis);
                                    }
                                }
                            }
                        }
                    });
                    layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showPopupMenuAtta(v);
                            return true;
                        }
                    });

                    if (i < (len - 1)) {
                        splitterView.setVisibility(View.VISIBLE);
                    }
                    attaListLayout.addView(layout);

                    // 处理附件相关信息。开始=====================================
                    String file = (String) o.get("file");
                    File attaFile = new File(saveDir_atta, file);
                    if (attaFile.exists()) {
                        // 如果附件已经存在
                        infoStatus.setText(R.string.status_already_downloaded);
                        infoStatus.setTextColor(getResources().getColor(R.color.text_green_darker));
                    } else {
                        infoStatus.setText(R.string.status_not_download);
                        infoStatus.setTextColor(getResources().getColor(R.color.text_brown));

                        // 将待下载的附件添加到列表中
                        downloadAttaList.add(tag);
                    }
                    // 处理附件相关信息。结束=====================================
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

            // 自动下载附件
            if (baseApp.isAutoDownloadAtta) {
                // 下载下一个附件
                downloadNextAtta();
            } else {
                downloadAttaList.clear();
            }
        }
    }

    /**
     * 下载下一个附件
     */
    private void downloadNextAtta() {
        if (downloadAttaList.size() > 0) {
            isDownloading = true;
            // 信息Tag
            HashMap<String, Object> tag = (HashMap<String, Object>) downloadAttaList.get(0);
            HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
            int o_index = (int) tag.get("i");
            boolean o_force = (boolean) tag.get("f");
            // 创建附件下载任务
            createDownloadAttaTask(o, o_index, o_force);
        } else {
            isDownloading = false;
        }
    }

    /**
     * 创建附件下载任务
     *
     * @param o                 {@code HashMap<String, Object>} 附件对象
     * @param o_index           {@code int} 附件索引
     * @param forceDownloadFlag {@code boolean} 强制下载标志
     */
    private void createDownloadAttaTask(HashMap<String, Object> o, int o_index, boolean forceDownloadFlag) {
        createDownloadAttaTask(o, o_index, forceDownloadFlag, false);
    }

    /**
     * 创建附件下载任务
     *
     * @param o                 {@code HashMap<String, Object>} 附件对象
     * @param o_index           {@code int} 附件索引
     * @param forceDownloadFlag {@code boolean} 强制下载标志
     * @param needShowNetAlert  {@code boolean} 是否需要发出网络提示信息
     */
    private void createDownloadAttaTask(HashMap<String, Object> o, int o_index, boolean forceDownloadFlag, boolean needShowNetAlert) {
        DownloadAttaTask downloadAttaTask = new DownloadAttaTask();
        downloadAttaTaskMap.put((String) o.get("file"), downloadAttaTask);
        downloadAttaTask.execute(o, o_index, forceDownloadFlag, needShowNetAlert);
    }

    /**
     * 下载附件 AsyncTask 类
     */
    private class DownloadAttaTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：开始下载
         */
        private static final int PROGRESS_START = 1001;

        /**
         * 附件对象
         */
        HashMap<String, Object> o;
        /**
         * 附件索引
         */
        int o_index;
        /**
         * 强制下载标志
         */
        boolean forceDownloadFlag = false;
        /**
         * 是否需要发出网络提示信息
         */
        boolean needShowNetAlert = false;
        /**
         * 是否需要下载附件
         */
        boolean needDownloadFlag = false;
        /**
         * 本地保存的附件文件
         */
        File attaFile;
        /**
         * 下载连接
         */
        HttpURLConnection conn;
        InputStream is;
        FileOutputStream fs;

        LinearLayout atta_layout;
        TextView infoStatus;
        ProgressBar infoStatusBar;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            currentDownloadingFile = null;
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 处理数据。开始============================================================================
            o = (HashMap<String, Object>) params[0];
            o_index = (int) params[1];
            if (params.length > 2) {
                forceDownloadFlag = (boolean) params[2];
            }
            if (params.length > 3) {
                needShowNetAlert = (boolean) params[3];
            }

            String remoteFileName = null;
            if (o != null && o_index > -1) {
                atta_layout = (LinearLayout) attaListLayout.findViewWithTag("ATTA_" + o_index);
                if (atta_layout != null) {
                    // 附件状态
                    infoStatus = (TextView) atta_layout.findViewById(R.id.infoStatus);
                    // 下载状态
                    infoStatusBar = (ProgressBar) atta_layout.findViewById(R.id.infoStatusBar);
                }

                // 附件保存目录
                File saveDir_atta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/atta");
                remoteFileName = (String) o.get("file");
                attaFile = new File(saveDir_atta, remoteFileName);
                currentDownloadingFile = attaFile;
                if (forceDownloadFlag) {
                    // 强制下载，先删除后下载
                    if (attaFile.exists()) {
                        attaFile.delete();
                    }
                    needDownloadFlag = true;
                } else {
                    // 非强制下载
                    if (!attaFile.exists()) {
                        needDownloadFlag = true;
                    } else {
                        result = CommonParam.RESULT_SUCCESS;
                    }
                }
            }

            if (needDownloadFlag) {
                // 需要下载
                publishProgress(PROGRESS_START);
                // 下载文件。开始===============================================
                if (checkNet(needShowNetAlert)) {
                    // 读取超时（毫秒）
                    int readTimeout = 3000;
                    // 连接超时（毫秒）
                    int connectTimeout = 3000;
                    // 下载文件名
                    String urlString = "http://" + baseApp.serverAddr + "/UploadFiles/SgResAtta/" + remoteFileName;

                    try {
                        URL url = new URL(urlString);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                        conn.setReadTimeout(readTimeout);
                        conn.setConnectTimeout(connectTimeout);
                        conn.setDoInput(true);
                        conn.connect();
                        int response = conn.getResponseCode();
                        if (response == 200) {
                            is = conn.getInputStream();
                            if (is != null) {
                                fs = new FileOutputStream(attaFile);
                                byte[] buffer = new byte[1444];
                                int byteRead = 0;
                                while ((byteRead = is.read(buffer)) != -1) {
                                    fs.write(buffer, 0, byteRead);
                                }
                                fs.flush();
                                result = CommonParam.RESULT_SUCCESS;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                            conn = null;
                        }
                        if (fs != null) {
                            try {
                                fs.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("#successFlag", result + ":" + urlString);
                }
                // 下载文件。结束===============================================
//                Map<String, Object> downloadResult = downloadFile("http://" + baseApp.serverAddr
//                        + "/UploadFiles/SgResAtta/" + remoteFileName, attaFile.getAbsolutePath(), null, needShowNetAlert);
//                result = (String) downloadResult.get("result");
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
            if (progress[0] == PROGRESS_START) {
                // 开始下载
                if (atta_layout != null) {
                    infoStatus.setText(R.string.status_downloading);
                    infoStatus.setTextColor(getResources().getColor(R.color.text_color_subline_blue));
                    infoStatusBar.setVisibility(View.VISIBLE);
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
            if (atta_layout != null) {
                if (CommonParam.RESULT_SUCCESS.equals(result)) {
                    // 下载成功
                    infoStatus.setText(R.string.status_already_downloaded);
                    infoStatus.setTextColor(getResources().getColor(R.color.text_green_darker));
                } else {
                    // 下载失败
                    infoStatus.setText(R.string.status_not_download);
                    infoStatus.setTextColor(getResources().getColor(R.color.text_brown));
                }
                infoStatusBar.setVisibility(View.GONE);
            }

            // 去掉该附件的下载计划
            downloadAttaTaskMap.remove((String) o.get("file"));
            if (downloadAttaList.size() > 0) {
                downloadAttaList.remove(0);
            }
            currentDownloadingFile = null;

            // 下载下一个附件
            downloadNextAtta();
        }

        @Override
        protected void onCancelled(String result) {
            currentDownloadingFile = null;
            if (needDownloadFlag && isCancelled() && CommonParam.RESULT_ERROR.equals(result)) {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
                if (fs != null) {
                    try {
                        fs.flush();
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 删除没下载完成的文件
                if (attaFile.exists()) {
                    attaFile.delete();
                }
            }

            super.onCancelled(result);
        }
    }

    /**
     * 显示附件PopupMenu
     *
     * @param view {@code View} PopupMenu绑定的对象
     */
    public void showPopupMenuAtta(View view) {
        if (attaPopupMenu != null) {
            Menu menu = attaPopupMenu.getMenu();
            menu.close();
            attaPopupMenu = null;
        }
        attaPopupMenu = new PopupMenu(this, view);
        // 强制显示PopupMenu图标
        forceShowPopupMenuIcon(attaPopupMenu);
        MenuInflater inflater = attaPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.sgres_atta_menu, attaPopupMenu.getMenu());
        attaPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_download:
                        // 下载附件
                        // 附件名称
                        TextView infoName = (TextView) view.findViewById(R.id.infoName);
                        // 信息Tag
                        HashMap<String, Object> tag = (HashMap<String, Object>) infoName.getTag();
                        HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
                        int o_index = (int) tag.get("i");
                        boolean o_force = (boolean) tag.get("f");
                        String file = (String) o.get("file");

                        if (downloadAttaTaskMap.containsKey(file)) {
                            // 正在下载
                            show("正在下载该文件，请稍后再试…");
                        } else {
                            // 是否需要将附件添加到下载队列中
                            boolean needAddToListFlag = true;
                            for (HashMap<String, Object> downloadAtta : downloadAttaList) {
                                int download_index = (int) downloadAtta.get("i");
                                if (o_index == download_index) {
                                    needAddToListFlag = false;
                                    downloadAtta.put("f", true);
                                    break;
                                }
                            }
                            if (needAddToListFlag) {
                                // 将待下载的附件添加到列表中
                                // 强制下载
                                tag.put("f", true);
                                downloadAttaList.add(tag);
                                if (!isDownloading) {
                                    downloadNextAtta();
                                }
                            }
                            SnackbarUtil.ShortSnackbar(contentView, "附件已添加到下载计划中！", SnackbarUtil.Info).show();
                        }

                        break;
                    default:
                }
                return true;
            }
        });
        attaPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu popup) {
            }
        });
        attaPopupMenu.show();
    }

    /**
     * 检查未下载的附件信息 AsyncTask 类
     */
    private class CheckUnDownloadAttaListTask extends AsyncTask<Object, Integer, String> {
        /**
         * 需要下载的附件索引List
         */
        List<Integer> indexList = new ArrayList<Integer>();

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
            for (int i = 0, len = attaList.size(); i < len; i++) {
                LinearLayout atta_layout = (LinearLayout) attaListLayout.findViewWithTag("ATTA_" + i);
                if (atta_layout != null) {
                    // 附件状态
                    TextView infoStatus = (TextView) atta_layout.findViewById(R.id.infoStatus);
                    // 附件名称
                    TextView infoName = (TextView) atta_layout.findViewById(R.id.infoName);
                    // 信息Tag
                    HashMap<String, Object> tag = (HashMap<String, Object>) infoName.getTag();
                    HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
                    String file = (String) o.get("file");
                    if (getString(R.string.status_not_download).equals(infoStatus.getText().toString()) && !downloadAttaTaskMap.containsKey(file)) {
                        // 未下载，且不在待附件下载任务Map中
                        indexList.add(i);
                        tag.put("f", false);
                        downloadAttaList.add(tag);
                    }
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
            continueDownloadAllBtn.setClickable(true);
            continueDownloadAllBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (indexList.size() > 0) {
                    SnackbarUtil.ShortSnackbar(contentView, indexList.size() + " 个附件已添加到下载计划中！", SnackbarUtil.Info).show();
                }
                if (!isDownloading) {
                    // 下载下一个附件
                    downloadNextAtta();
                }
            }
        }
    }

    /**
     * 重新下载所有附件 AsyncTask 类
     */
    private class ReDownloadAllAttaListTask extends AsyncTask<Object, Integer, String> {
        /**
         * 需要下载的附件索引List
         */
        List<Integer> indexList = new ArrayList<Integer>();

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
            if (downloadAttaList.size() > 1) {
                for (int i = 1, len = downloadAttaList.size() - 1; i <= len; i++) {
                    downloadAttaList.remove(1);
                }
            }
            for (int i = 0, len = attaList.size(); i < len; i++) {
                LinearLayout atta_layout = (LinearLayout) attaListLayout.findViewWithTag("ATTA_" + i);
                if (atta_layout != null) {
                    // 附件状态
                    TextView infoStatus = (TextView) atta_layout.findViewById(R.id.infoStatus);
                    // 附件名称
                    TextView infoName = (TextView) atta_layout.findViewById(R.id.infoName);
                    // 信息Tag
                    HashMap<String, Object> tag = (HashMap<String, Object>) infoName.getTag();
                    HashMap<String, Object> o = (HashMap<String, Object>) tag.get("o");
                    String file = (String) o.get("file");
                    if (!downloadAttaTaskMap.containsKey(file)) {
                        // 未下载，且不在待附件下载任务Map中
                        indexList.add(i);
                        tag.put("f", true);
                        downloadAttaList.add(tag);
                    }
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
            reDownloadAllBtn.setClickable(true);
            reDownloadAllBtn.setEnabled(true);
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (indexList.size() > 0) {
                    SnackbarUtil.ShortSnackbar(contentView, indexList.size() + " 个附件已添加到下载计划中！", SnackbarUtil.Info).show();
                }
                if (!isDownloading) {
                    // 下载下一个附件
                    downloadNextAtta();
                }
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
        pResNameTv = (TextView) findViewById(R.id.pResNameTv);
        titleTv = (TextView) findViewById(R.id.titleTv);
        showCatButton = (ImageButton) findViewById(R.id.showCatButton);
        catNameTv = (TextView) findViewById(R.id.catNameTv);
        pxbhTv = (TextView) findViewById(R.id.pxbhTv);
        bmNameTv = (TextView) findViewById(R.id.bmNameTv);
        activeTv = (TextView) findViewById(R.id.activeTv);
        loctypeTv = (TextView) findViewById(R.id.loctypeTv);
        locTv = (TextView) findViewById(R.id.locTv);
        z01Tv = (TextView) findViewById(R.id.z01Tv);
        z02Tv = (TextView) findViewById(R.id.z02Tv);
        z03Tv = (TextView) findViewById(R.id.z03Tv);
        z04Tv = (TextView) findViewById(R.id.z04Tv);
        z05Tv = (TextView) findViewById(R.id.z05Tv);
        z06Tv = (TextView) findViewById(R.id.z06Tv);
        z07Tv = (TextView) findViewById(R.id.z07Tv);
        z08Tv = (TextView) findViewById(R.id.z08Tv);
        z09Tv = (TextView) findViewById(R.id.z09Tv);
        z10Tv = (TextView) findViewById(R.id.z10Tv);
        z11Tv = (TextView) findViewById(R.id.z11Tv);
        z12Tv = (TextView) findViewById(R.id.z12Tv);
        z13Tv = (TextView) findViewById(R.id.z13Tv);
        z14Tv = (TextView) findViewById(R.id.z14Tv);
        z15Tv = (TextView) findViewById(R.id.z15Tv);
        lnglatMapButton = (ImageButton) findViewById(R.id.lnglatMapButton);
        memoTv = (TextView) findViewById(R.id.memoTv);
        memoTableBtn = (Button) findViewById(R.id.memoTableBtn);
        memoTableLayout = (LinearLayout) findViewById(R.id.memoTableLayout);
        areaSignTableLayout = (LinearLayout) findViewById(R.id.areaSignTableLayout);
        attaTitleLayout = (LinearLayout) findViewById(R.id.attaTitleLayout);
        attaNumTv = (TextView) findViewById(R.id.attaNumTv);
        continueDownloadAllBtn = (Button) findViewById(R.id.continueDownloadAllBtn);
        reDownloadAllBtn = (Button) findViewById(R.id.reDownloadAllBtn);
        attaHelpBtn = (PrintView) findViewById(R.id.attaHelpBtn);
        attaListLayout = (LinearLayout) findViewById(R.id.attaListLayout);
        // 界面相关参数。结束===============================
    }
}
