/*
 * Copyright (c) 2017 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 水工维修_项目基本信息_信息主页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MaintenanceInfoMainActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
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
    /**
     * 底部按钮：新建
     */
    private Button addBtn;
    // 界面相关参数。开始===============================
    private TextView infoTitleTv;
    private TextView c1Tv;
    private TextView c2Tv;
    private TextView c3Tv;
    private TextView c4Tv;
    private TextView c5Tv;
    private Button info_btn;
    private Button rec_list_btn;
    /**
     * 阶段列表
     */
    private LinearLayout stageListLayout;
    /**
     * 分类列表
     */
    private LinearLayout catListLayout;
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
    /**
     * 阶段list
     */
    private List<String> stageList;
    /**
     * 分类list
     */
    private List<String> catList;
    /**
     * 页面Handler
     */
    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 10:
                    addBtn.setClickable(false);
                    addBtn.setEnabled(false);
                    break;
                case 11:
                    addBtn.setClickable(true);
                    addBtn.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MaintenanceInfoMainActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");

        setContentView(R.layout.maintenance_info_main);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("水工维修项目");

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
        info_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("info", infoObj);

                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MaintenanceInfoShowActivity.class);
                // 将数据存入Intent中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });
        rec_list_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("mInfoId", infoId);

                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MaintenanceRecListActivity.class);
                // 将数据存入Intent中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });
        addBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pageHandler.sendEmptyMessage(10);

                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putString("mInfoId", infoId);

                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MaintenanceRecEditActivity.class);
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_NEW_REC);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);

                pageHandler.sendEmptyMessageDelayed(11, 3000);
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
         * 进度常量：建立阶段列表
         */
        private static final int PROGRESS_MAKE_STAGE_LIST = 1003;
        /**
         * 进度常量：建立分类列表
         */
        private static final int PROGRESS_MAKE_CAT_LIST = 1004;

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
                            "SELECT * FROM t_szfgs_sgwxinfo model WHERE model.valid='1' and model.ids=? and model.quid=?",
                            new String[]{infoId, (String) baseApp.getLoginUser().get("ids")});
            if (recList.size() > 0) {
                infoObj = recList.get(0);
            }
            if (infoObj == null) {
                return result;
            }
            stageList = infoTool
                    .getValList(
                            "SELECT DISTINCT(model.c1) c1 FROM t_szfgs_sgwxstage model WHERE model.valid='1' GROUP BY model.c1 ORDER BY model.pxbh ASC",
                            new String[]{});
            catList = infoTool
                    .getValList(
                            "SELECT DISTINCT(model.c1) c1 FROM t_szfgs_sgwxcat model WHERE model.valid='1' GROUP BY model.c1 ORDER BY model.pxbh ASC",
                            new String[]{});
            // 处理数据。结束============================================================================

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);
            // 生成信息列表
            publishProgress(PROGRESS_MAKE_LIST);
            if (stageList.size() > 0) {
                // 建立阶段列表
                publishProgress(PROGRESS_MAKE_STAGE_LIST);
            }
            if (catList.size() > 0) {
                // 建立分类列表
                publishProgress(PROGRESS_MAKE_CAT_LIST);
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
            if (progress[0] == PROGRESS_SET_FIELD) {
                // 设置字段及按钮
                homeBtn.setVisibility(View.VISIBLE);
                infoTitleTv.setText(CommonUtil.N2B((String) infoObj.get("title")));
                c1Tv.setText(CommonUtil.N2B((String) infoObj.get("bh")));
                c2Tv.setText(CommonUtil.N2B((String) infoObj.get("bm")));
                c3Tv.setText(CommonUtil.N2B((String) infoObj.get("stage1")));
                c4Tv.setText(CommonUtil.N2B((String) infoObj.get("stage2")));
                c5Tv.setText(CommonUtil.N2B((String) infoObj.get("patime")) + " 至 " + CommonUtil.N2B((String) infoObj.get("pbtime")));
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成列表
            } else if (progress[0] == PROGRESS_MAKE_STAGE_LIST) {
                // 建立阶段列表
                for (int i = 0, len = stageList.size(); i < len; i++) {
                    String c = stageList.get(i);

                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                            R.layout.ins_choose_stage_list_item, null);
                    // 列表名称
                    Button list_btn = (Button) layout.findViewById(R.id.list_btn);
                    // 新建按钮
                    Button add_btn = (Button) layout.findViewById(R.id.add_btn);
                    list_btn.setText((i + 1) + "、" + c);
                    add_btn.setVisibility(View.VISIBLE);

                    // 信息Tag
                    Map<String, Object> tag = new HashMap<String, Object>();
                    tag.put("c", c);
                    list_btn.setTag(tag);
                    list_btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> map = (HashMap<String, Object>) v.getTag();
                            String c = (String) map.get("c");

                            // 信息传输Bundle
                            Bundle data = new Bundle();
                            data.putSerializable("mInfoId", infoId);
                            data.putString("stage1", c);

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, MaintenanceRecListActivity.class);
                            // 将数据存入Intent中
                            intent.putExtras(data);
                            startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                        }
                    });
                    add_btn.setTag(tag);
                    add_btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> map = (HashMap<String, Object>) v.getTag();
                            String c = (String) map.get("c");

                            // 创建信息传输Bundle
                            Bundle data = new Bundle();
                            data.putString("mInfoId", infoId);
                            data.putString("stage1", c);

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, MaintenanceRecEditActivity.class);
                            // 将数据存入 Intent 中
                            intent.putExtras(data);
                            startActivityForResult(intent, CommonParam.REQUESTCODE_NEW_REC);
                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                        }
                    });

                    stageListLayout.addView(layout);
                }
            } else if (progress[0] == PROGRESS_MAKE_CAT_LIST) {
                // 建立分类列表
                for (int i = 0, len = catList.size(); i < len; i++) {
                    String c = catList.get(i);

                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                            R.layout.ins_choose_stage_list_item, null);
                    // 列表名称
                    Button list_btn = (Button) layout.findViewById(R.id.list_btn);
                    // 新建按钮
                    Button add_btn = (Button) layout.findViewById(R.id.add_btn);
                    list_btn.setText((i + 1) + "、" + c);
                    add_btn.setVisibility(View.VISIBLE);

                    // 信息Tag
                    Map<String, Object> tag = new HashMap<String, Object>();
                    tag.put("c", c);
                    list_btn.setTag(tag);
                    list_btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 信息传输Bundle
                            Bundle data = new Bundle();
                            data.putSerializable("mInfoId", infoId);
                            data.putString("cat1", c);

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, MaintenanceRecListActivity.class);
                            // 将数据存入Intent中
                            intent.putExtras(data);
                            startActivityForResult(intent, CommonParam.REQUESTCODE_LIST);
                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                        }
                    });

                    add_btn.setTag(tag);
                    add_btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> map = (HashMap<String, Object>) v.getTag();
                            String c = (String) map.get("c");

                            // 创建信息传输Bundle
                            Bundle data = new Bundle();
                            data.putString("mInfoId", infoId);
                            data.putString("cat1", c);

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, MaintenanceRecEditActivity.class);
                            // 将数据存入 Intent 中
                            intent.putExtras(data);
                            startActivityForResult(intent, CommonParam.REQUESTCODE_NEW_REC);
                            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                        }
                    });

                    catListLayout.addView(layout);
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
        addBtn = (Button) findViewById(R.id.addBtn);

        infoTitleTv = (TextView) findViewById(R.id.infoTitleTv);
        c1Tv = (TextView) findViewById(R.id.c1Tv);
        c2Tv = (TextView) findViewById(R.id.c2Tv);
        c3Tv = (TextView) findViewById(R.id.c3Tv);
        c4Tv = (TextView) findViewById(R.id.c4Tv);
        c5Tv = (TextView) findViewById(R.id.c5Tv);
        info_btn = (Button) findViewById(R.id.info_btn);
        rec_list_btn = (Button) findViewById(R.id.rec_list_btn);
        stageListLayout = (LinearLayout) findViewById(R.id.stageListLayout);
        catListLayout = (LinearLayout) findViewById(R.id.catListLayout);
        // 界面相关参数。结束===============================
    }
}
