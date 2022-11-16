/*
 * Copyright (c) 2016 乔勇(Jacky Qiao) 版权所有
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.RemoteDataListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 动态搜索_信息列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class RemoteDataListActivity extends DbActivity {
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

    // 界面相关参数。开始===============================
    /**
     * 清除
     */
    private Button cleanBtn;
    /**
     * 搜索
     */
    private Button searchBtn;
    /**
     * 确定
     */
    private Button submitBtn;
    /**
     * 返回
     */
    private Button goBackBtn;

    private EditText searchTv;
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
    private RemoteDataListAdapter infoListAdapter;

    private String searchType;
    private String searchC;
    private String searchV;
    private String searchV_old;
    private String searchView;

    /**
     * 要返回值的Activity
     */
    private Class activityClass;

    /**
     * 定时查询的 AsyncTask 对象
     */
    private SearchTask searchTask;
    /**
     * 定时查询的 Timer 对象
     */
    private Timer searchTimer;

    // 界面相关参数。结束===============================
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;


    // 网络连接相关参数。开始==========================================
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    // 网络连接相关参数。结束==========================================
    // 查询参数。开始==========================================
    /**
     * 企业编号
     */
    private String eid;
    // 查询参数。结束==========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = RemoteDataListActivity.this;

        setContentView(R.layout.remote_data_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        fromFlag = data.getString("fromFlag", "search");
        searchType = data.getString("type", "ent");
        searchC = data.getString("c");
        searchV = data.getString("v", "");
        searchView = data.getString("view");
        eid = CommonUtil.N2B(data.getString("eid"));

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText("搜索");

        listItems = new ArrayList<HashMap<String, Object>>();

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
        cleanBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTv.setText("");
            }
        });
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isConnecting) {
                    (searchTask = new SearchTask()).execute();
                }
            }
        });
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
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
        StatusBarUtil.setStatusBarMode(this, false, R.color.background_title_green);
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
        finish();
    }

    @Override
    protected void onStop() {
        if (searchTimer != null) {
            searchTimer.cancel();
            searchTimer.purge();
            searchTimer = null;
        }
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchTimer == null) {
            searchTimer = new Timer();
        }
        if (searchTask == null) {
            searchTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (!isConnecting) {
                        (searchTask = new SearchTask()).execute();
                    }
                }
            }, 400);
        }
    }

    @Override
    protected void onDestroy() {
        if (mainTask != null) {
            mainTask.cancel(true);
        }
        if (searchTimer != null) {
            searchTimer.cancel();
            searchTimer.purge();
            searchTimer = null;
        }
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
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

            if ("sg_res_list".equals(fromFlag)) {
                activityClass = SgResListActivity.class;
            }

            // 处理数据。开始============================================================================
            listItems = new ArrayList<HashMap<String, Object>>();

            ArrayList<HashMap<String, Object>> recList = new ArrayList<HashMap<String, Object>>();
            for (int index = 0, len = recList.size(); index < len; index++) {
                // 存放信息的 Map
                HashMap<String, Object> listItem = new HashMap<String, Object>();
                HashMap<String, Object> info = recList.get(index);

                listItem.put("info", info);
                listItems.add(listItem);
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
                searchTv.setText(searchV);
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成列表
                infoListAdapter = (RemoteDataListAdapter) infoList.getAdapter();
                if (infoListAdapter != null) {
                    infoListAdapter.notifyDataSetChanged();
                } else {
                    infoListAdapter = new RemoteDataListAdapter(getApplicationContext(), listItems,
                            R.layout.remote_data_list_item, new String[]{"info"}, new int[]{
                            R.id.infoName});
                    infoList.setAdapter(infoListAdapter);

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new RemoteDataListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.infoName) {
                                    textView.setText(CommonUtil.N2B((String) info.get("v")));
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    // 设置列表项点击事件
                    infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // 列表项中的对象
                            HashMap<String, Object> vMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
                            HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");

                            // 创建信息传输Bundle
                            Bundle data = new Bundle();
                            if ("car_info".equals(searchType)) {
                                String v = (String) info.get("v");
                                data.putString("v", v);
                            }

                            data.putString("view", searchView);
                            data.putString("type", searchType);
                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, activityClass);
                            // 将数据存入Intent中
                            intent.putExtras(data);
                            // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                            setResult(CommonParam.RESULTCODE_REMOTE_DATA, intent);
                            goBack();
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
        }
    }

    /**
     * 查询信息 AsyncTask 类
     */
    private class SearchTask extends AsyncTask<Object, Integer, String> {
        private String keyWord;
        /**
         * 进度常量：生成列表
         * */
        // private static final int PROGRESS_MAKE_LIST = 1001;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            //makeWaitDialog();
            isConnecting = true;

            keyWord = searchTv.getText().toString();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            if (keyWord.equals(searchV_old)) {
                return result;
            }
            searchV_old = keyWord;

            // 服务器返回的文本
            String respStr = "";
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。结束=================
            try {
                // 查询信息。开始====================================================================
                String infoType = null;
                if ("car_info".equals(searchType)) {
                    infoType = "remote_car_info";
                }

                // 生成参数。开始======================================
                String userId = (String) baseApp.loginUser.get("ids");

                JSONObject queryParams = new JSONObject();
                if (CommonUtil.checkNB(eid)) {
                    queryParams.put("eid", eid);
                }
                queryParams.put("keyWord", keyWord);
                queryParams.put("searchC", searchC);
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("infoType", infoType)
                        .addFormDataPart("queryParams", queryParams.toJSONString());
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
                        ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();

                        for (int index = 0, len = data.size(); index < len; index++) {
                            // 存放信息的 Map
                            HashMap<String, Object> listItem = new HashMap<String, Object>();
                            HashMap<String, Object> info = null;
                            JSONObject json = data.getJSONObject(index);
                            info = CommonUtil.jsonToMap(json);

                            listItem.put("info", info);

                            listItems_tmp.add(listItem);
                        }
                        listItems.clear();
                        listItems.addAll(listItems_tmp);

                        result = CommonParam.RESULT_SUCCESS;
                    }
                } else {
                    // 服务器连接失败
                }
                // 查询信息。结束====================================================================
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            //unWait();
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (infoListAdapter != null) {
                    infoListAdapter.notifyDataSetChanged();
                }
            }
            isConnecting = false;

            if (searchTimer == null) {
                searchTimer = new Timer();
            }
            searchTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (!isConnecting) {
                        (searchTask = new SearchTask()).execute();
                    }
                }
            }, 400);
        }
    }

    /**
     * 提交信息
     */
    public void submit() {
        boolean submitFlag = false;
        String errorMsg = "";

        String v = searchTv.getText().toString();

        //if (!CommonUtil.checkNB(dateinfo)) {
        //    errorMsg = "请选择日期！";
        //} else {
        submitFlag = true;
        //}

        if (!submitFlag) {
            // 不能提交
            if (CommonUtil.checkNB(errorMsg)) {
                show(errorMsg);
            }
        } else {
            // 可以提交
            // 创建信息传输Bundle
            Bundle data = new Bundle();
            if ("car_info".equals(searchType)) {
                data.putString("v", v);
            }

            data.putString("view", searchView);
            data.putString("type", searchType);
            // 创建启动 Activity 的 Intent
            Intent intent = new Intent(classThis, activityClass);
            // 将数据存入Intent中
            intent.putExtras(data);
            // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
            setResult(CommonParam.RESULTCODE_REMOTE_DATA, intent);
            goBack();
        }
    }

    /**
     * 查找view
     */
    public void findViews() {
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        cleanBtn = (Button) findViewById(R.id.cleanBtn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        // 界面相关参数。开始===============================
        searchTv = (EditText) findViewById(R.id.searchTv);
        infoList = (ListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
