/*
 * Copyright (c) 2017 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 水工维修_项目基本信息_查阅页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MaintenanceInfoShowActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    // DbActivity classThis;
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
     * 底部按钮：返回
     */
    private Button goBackBtn;
    // 界面相关参数。开始===============================
    private TextView bhTv;
    private TextView titleTv;
    private TextView bmTv;
    private TextView stage1Tv;
    private TextView stage2Tv;
    private TextView xzTv;
    private TextView lxTv;
    private TextView infoTv;
    private TextView patimeTv;
    private TextView pbtimeTv;
    private TextView gqTv;
    private TextView htdwTv;
    private TextView htbhTv;
    private TextView htjeTv;
    private TextView ratimeTv;
    private TextView rbtimeTv;
    private TextView ctimeTv;
    private TextView htzfTv;
    private TextView zlyjqkTv;
    private TextView fzrTv;
    private TextView zxrTv;
    // 界面相关参数。结束===============================

    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;

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

        // classThis = MaintenanceInfoShowActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");
        infoObj = (HashMap<String, Object>) data.getSerializable("info");

        setContentView(R.layout.maintenance_info_show);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("项目基本信息");

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
            if (infoObj == null && CommonUtil.checkNB(infoId)) {
                infoTool = getInfoTool();
                ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT * FROM t_szfgs_sgwxinfo model WHERE model.valid='1' and model.ids=? and model.quid=?",
                                new String[]{infoId, (String) baseApp.getLoginUser().get("ids")});
                if (recList.size() > 0) {
                    infoObj = recList.get(0);
                    infoId = (String) infoObj.get("ids");
                }
            }
            if (infoObj == null) {
                return result;
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
                homeBtn.setVisibility(View.VISIBLE);
                bhTv.setText(CommonUtil.N2B((String) infoObj.get("bh")));
                titleTv.setText(CommonUtil.N2B((String) infoObj.get("title")));
                bmTv.setText(CommonUtil.N2B((String) infoObj.get("bm")));
                stage1Tv.setText(CommonUtil.N2B((String) infoObj.get("stage1")));
                stage2Tv.setText(CommonUtil.N2B((String) infoObj.get("stage2")));
                xzTv.setText(CommonUtil.N2B((String) infoObj.get("xz")));
                lxTv.setText(CommonUtil.N2B((String) infoObj.get("lx")));
                infoTv.setText(CommonUtil.N2B((String) infoObj.get("info")));
                patimeTv.setText(CommonUtil.N2B((String) infoObj.get("patime")));
                pbtimeTv.setText(CommonUtil.N2B((String) infoObj.get("pbtime")));
                gqTv.setText(CommonUtil.N2B((String) infoObj.get("gq")));
                htdwTv.setText(CommonUtil.N2B((String) infoObj.get("htdw")));
                htbhTv.setText(CommonUtil.N2B((String) infoObj.get("htbh")));
                htjeTv.setText(CommonUtil.N2B((String) infoObj.get("htje")));
                ratimeTv.setText(CommonUtil.N2B((String) infoObj.get("ratime")));
                rbtimeTv.setText(CommonUtil.N2B((String) infoObj.get("rbtime")));
                ctimeTv.setText(CommonUtil.N2B((String) infoObj.get("ctime")));
                htzfTv.setText(CommonUtil.N2B((String) infoObj.get("htzf")).equals("1") ? "是" : "否");
                zlyjqkTv.setText(CommonUtil.N2B((String) infoObj.get("zlyjqk")));
                fzrTv.setText(CommonUtil.N2B((String) infoObj.get("fzrname")));
                zxrTv.setText(CommonUtil.N2B((String) infoObj.get("zxrname")));
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成列表
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
            } else {
                show("信息错误！");
                goBack();
            }
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);

        bhTv = (TextView) findViewById(R.id.bhTv);
        titleTv = (TextView) findViewById(R.id.titleTv);
        bmTv = (TextView) findViewById(R.id.bmTv);
        stage1Tv = (TextView) findViewById(R.id.stage1Tv);
        stage2Tv = (TextView) findViewById(R.id.stage2Tv);
        xzTv = (TextView) findViewById(R.id.xzTv);
        lxTv = (TextView) findViewById(R.id.lxTv);
        infoTv = (TextView) findViewById(R.id.infoTv);
        patimeTv = (TextView) findViewById(R.id.patimeTv);
        pbtimeTv = (TextView) findViewById(R.id.pbtimeTv);
        gqTv = (TextView) findViewById(R.id.gqTv);
        htdwTv = (TextView) findViewById(R.id.htdwTv);
        htbhTv = (TextView) findViewById(R.id.htbhTv);
        htjeTv = (TextView) findViewById(R.id.htjeTv);
        ratimeTv = (TextView) findViewById(R.id.ratimeTv);
        rbtimeTv = (TextView) findViewById(R.id.rbtimeTv);
        ctimeTv = (TextView) findViewById(R.id.ctimeTv);
        htzfTv = (TextView) findViewById(R.id.htzfTv);
        zlyjqkTv = (TextView) findViewById(R.id.zlyjqkTv);
        fzrTv = (TextView) findViewById(R.id.fzrTv);
        zxrTv = (TextView) findViewById(R.id.zxrTv);
        // 界面相关参数。结束===============================
    }
}
