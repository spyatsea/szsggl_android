package com.cox.android.szsggl.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.NewsListAdapter;
import com.cox.android.szsggl.adapter.NewsListAdapter.ViewBinder;
import com.cox.android.szsggl.model.Info;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 新闻类栏目：1
 * <p>
 * 下拉刷新时调用网站数据
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressLint("DefaultLocale")
@SuppressWarnings({"unchecked"})
public class NewsChannelT1Fragment extends Fragment {
    /**
     * 布局宽度
     * */
    // private int layoutWidth;
    /**
     * 栏目编号
     */
    private String cid;
    /**
     * 栏目名称
     */
    private String cname;
    /**
     * 信息列表
     */
    private ArrayList<HashMap<String, Object>> listItems;
    /**
     * 记录列表
     */
    private PullToRefreshListView infoList;
    /**
     * 列表Adapter
     */
    private NewsListAdapter recListAdapter;

    private NewsMainActivity activity;

    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 查询信息 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> searchTask;

    /**
     * 信息列表备用缩略图
     */
    private static final int[] pics = {R.drawable.p1, R.drawable.p2, R.drawable.p3};

    /**
     * 每页大小
     */
    int ROWS_PER_PAGE = CommonParam.RESULT_LIST_PER;
    // 网络连接相关参数。开始==========================================
    HttpResponse upResponse;
    DefaultHttpClient upHttpclient;
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    HttpPost upHttpPost = null;

    // 网络连接相关参数。结束==========================================
    // 图片显示相关参数。开始===============================================
    public static List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    // 图片显示相关参数。结束===============================================


    OkHttpClient baseHttpClient = null;
    /**
     * 页面Handler
     */
    private final Handler infoListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 10:
                    infoList.setFooterListEnd();
                    break;
                case 20:
                    infoList.setFooterListContinue();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (NewsMainActivity) getActivity();
        // 获取信息传输Bundle
        Bundle data = getArguments();
        // 布局尺寸
        // layoutWidth = data.getInt("layoutWidth");
        // 栏目编号
        cid = (String) data.getString("cid");
        // 栏目名称
        cname = (String) data.getString("cname");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mainTask != null) {
            mainTask.cancel(true);
        }
        if (searchTask != null) {
            searchTask.cancel(true);
        }
        super.onDestroy();
    }

    // 该方法的返回值就是该Fragment显示的View组件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局
        LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_news_t1, container, false);

        infoList = (PullToRefreshListView) rootLayout.findViewById(R.id.infoList);
        return rootLayout;
    }

    //@Override
    //public void onAttach(AppCompatActivity activity) {
    //	super.onAttach(activity);
    //}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 执行主进程
        mainTask = new MainTask().execute();
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定 Activity 返回的结果。
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }

    /**
     * 主进程 AsyncTask 类
     */
    private class MainTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：生成列表
         */
        private static final int PROGRESS_MAKE_LIST = 1001;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 重置列表
            resetListData();
            // 显示等待窗口
            // activity.makeWaitDialog();
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

            if (activity.db == null || !activity.db.isOpen()) {
                activity.db = activity.dbTool.regetDb();
                activity.baseApp.setDb(activity.db);
            }

            listItems = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) activity.infoTool
                    .getInfoMapList(
                            "select * from t_news_info model where model.treeid=? order by model.topflag DESC,DATETIME(model.publishtime) DESC LIMIT 0,"
                                    + ROWS_PER_PAGE, new String[]{cid});
            for (int index = 0, len = recList.size(); index < len; index++) {
                // 存放信息的 Map
                HashMap<String, Object> listItem = new HashMap<String, Object>();
                HashMap<String, Object> info = recList.get(index);
                if (CommonParam.NEWS_INFO_TYPE_BIG_PIC.equals((String) info.get("infotype"))) {
                    info.put("V_INFO_TYPE", CommonParam.NEWS_INFO_TYPE_BIG_PIC);
                } else if (CommonParam.NEWS_INFO_TYPE_SMALL_PIC.equals((String) info.get("infotype"))) {
                    info.put("V_INFO_TYPE", CommonParam.NEWS_INFO_TYPE_SMALL_PIC);
                } else {
                    info.put("V_INFO_TYPE", CommonParam.NEWS_INFO_TYPE_NORMAL);
                }
                listItem.put("info", info);
                listItems.add(listItem);
            }
            // if (total <= ROWS_PER_PAGE) {
            // // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
            // infoList.setFooterListEnd();
            // }

            // 生成列表
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
            if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成列表
                recListAdapter = (NewsListAdapter) infoList.getAdapter();
                if (recListAdapter != null) {
                    recListAdapter.notifyDataSetChanged();
                } else {
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
                            // recListAdapter.loadData();

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
                    // LayoutInflater inflater = activity.getLayoutInflater();
                    // 设置列表的首行视图。开始=========================================================
                    // RelativeLayout topImageLayout = (RelativeLayout)
                    // inflater.inflate(R.layout.news_t1_list_image_item,
                    // null);
                    // ImageView topItemImage = (ImageView) topImageLayout.findViewById(R.id.newsItemImage);
                    // TextView topItemTitle = (TextView) topImageLayout.findViewById(R.id.newsItemTitle);
                    // if ("201823b12ccf45da8cd37fb4d0bef153".equals(cid)) {
                    // // 信息快递
                    // topItemImage.setImageResource(R.drawable.p_note);
                    // } else if ("3b974df24a9f47bba22fb8efefca0344".equals(cid)) {
                    // // 新闻动态
                    // topItemImage.setImageResource(R.drawable.p_news);
                    // } else {
                    // topItemImage.setImageResource(pics[(int) Math.round(Math.random() * (pics.length - 1))]);
                    // }
                    // topItemTitle.setText((String) listItems.get(0).get("title"));
                    // infoList.addHeaderView(topImageLayout, listItems.get(0), true);
                    // 设置列表的首行视图。结束=========================================================

                    recListAdapter = new NewsListAdapter(activity.getApplicationContext(), listItems,
                            R.layout.news_t1_list_normal_item, new String[]{"info", "info"}, new int[]{
                            R.id.newsItemTitle, R.id.newsItemDate});
                    infoList.setAdapter(recListAdapter);

                    // 对绑定的数据进行处理
                    recListAdapter.setViewBinder(new ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.newsItemTitle) {
                                    textView.setText((String) info.get("title"));
                                } else if (textView.getId() == R.id.newsItemDate) {
                                    String user = CommonUtil.N2B((String) info.get("f2"));
                                    String date = CommonUtil.N2B((String) info.get("publishtime"));
                                    if (date.length() == 19) {
                                        date = date.substring(5, 16);
                                    }
                                    textView.setText(user + "　" + date);
                                }
                                return true;
                            } else if (view instanceof ImageView) {
                                ImageView imageView = (ImageView) view;
                                // 记录信息
                                // String str = (String) data;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                // 信息类型
                                String infoType = (String) info.get("V_INFO_TYPE");
                                // 图片名称
                                String imageName = (String) info.get("f1");
                                // if (CommonParam.NEWS_INFO_TYPE_BIGPIC.equals(infoType)) {
                                // // 大图信息
                                // if ("3a25925506c04e17893f3f952cbe3b77".equals((String) info.get("ids"))) {
                                // imageView.setImageResource(R.drawable.client_pic);
                                // } else if ("201823b12ccf45da8cd37fb4d0bef153".equals(cid)) {
                                // // 信息快递
                                // imageView.setImageResource(R.drawable.p_note);
                                // } else if ("3b974df24a9f47bba22fb8efefca0344".equals(cid)) {
                                // // 新闻动态
                                // imageView.setImageResource(R.drawable.p_news);
                                // } else {
                                // imageView.setImageResource(pics[(int) Math.round(Math.random()
                                // * (pics.length - 1))]);
                                // }
                                // } else {
                                // 普通信息
                                if (CommonUtil.checkNB(imageName)) {
                                    ImageLoader imageLoader = ImageLoader.getInstance();
                                    imageLoader.displayImage(
                                            "http://"
                                                    + activity.preferences.getString("SERVER_ADDR",
                                                    activity.baseApp.serverAddr)
                                                    + CommonParam.URL_UPLOADFILES + "/" + imageName, imageView,
                                            new ImageLoadingListener() {

                                                @Override
                                                public void onLoadingStarted(String imageUri, View view) {
                                                }

                                                @Override
                                                public void onLoadingFailed(String imageUri, View view,
                                                                            FailReason failReason) {
                                                    ImageView imageView = (ImageView) view;
                                                    imageView.setImageResource(R.drawable.transparent_pic);
                                                }

                                                @Override
                                                public void onLoadingComplete(String imageUri, View view,
                                                                              Bitmap loadedImage) {
                                                    ImageView imageView = (ImageView) view;
                                                    if (loadedImage != null) {
                                                        boolean firstDisplay = !displayedImages.contains(imageUri);
                                                        if (firstDisplay) {
                                                            FadeInBitmapDisplayer.animate(imageView, 500);
                                                            displayedImages.add(imageUri);
                                                        }
                                                    } else {
                                                        imageView.setImageResource(R.drawable.transparent_pic);
                                                    }
                                                }

                                                @Override
                                                public void onLoadingCancelled(String imageUri, View view) {
                                                }
                                            });
                                } else {
                                    imageView.setImageResource(pics[(int) Math.round(Math.random() * (pics.length - 1))]);
                                }
                                // }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    Log.d("#size_3:" + cid, "" + infoList.getAdapter().getCount());
                    Log.d("###pos" + cid, "" + infoList.getLastVisiblePosition());
                    // 设置列表项点击事件
                    infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // 列表项中的对象
                            HashMap<String, Object> vMap = (HashMap<String, Object>) recListAdapter.getItem(position);
                            HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");

                            // 创建信息传输Bundle
                            Bundle data = new Bundle();
                            // 信息id
                            data.putString("itemId", (String) info.get("ids"));
                            // 栏目名称
                            data.putString("channelName", cname);
                            // 创建启动 Activity 的
                            // Intent
                            Intent intent = new Intent(activity, NewsDetailT1Activity.class);
                            // 将数据存入 Intent 中
                            intent.putExtras(data);
                            startActivity(intent);
                            activity.overridePendingTransition(R.anim.activity_slide_left_in,
                                    R.anim.activity_slide_left_out);
                        }
                    });
                    // 设置列表滚动事件
                    infoList.setOnScrollListener(new OnScrollListener() {

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            Log.d("a@@@scrollState", "" + scrollState);
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                             int totalItemCount) {
                            if (!infoList.isFooterRefreshing() && !infoList.isFooterListEnd()
                                    && (firstVisibleItem + visibleItemCount) == totalItemCount
                                    && infoList.getAdapter() != null && infoList.getAdapter().getCount() > 0) {
                                // 滚动到底部
                                if (!isConnecting) {
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
            // 更新列表
            recListAdapter.notifyDataSetChanged();

            // 隐藏等待窗口
            activity.unWait();
            infoList.onRefreshComplete();
            if (activity.checkNet()) {
                searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
            }
        }
    }

    /**
     * 重置列表
     */
    public void resetListData() {
        if (infoList != null) {
            NewsListAdapter recListAdapter = (NewsListAdapter) infoList.getAdapter();
            if (recListAdapter != null) {
                recListAdapter.notifyDataSetInvalidated();
            }
        }
        if (listItems != null) {
            // 清空信息列表
            listItems.clear();
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
         * 搜索类型
         */
        private String searchType;
        /**
         * 是否手动显示数据加载指示器
         */
        private boolean manualIndicatorFlag = false;
        /**
         * 结果是否成功的标志
         */
        private boolean resultFlag = false;
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
            // activity.makeWaitDialog();
            Log.d("bsearch##########", "#");
            isConnecting = true;
            if (!activity.checkNet()) {
                return;
            }
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            if (activity.db == null || !activity.db.isOpen()) {
                activity.db = activity.dbTool.regetDb();
                activity.baseApp.setDb(activity.db);
            }

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
                Log.d("@@@@@HEADER", "@@@@@@@@@@@@@@@@@@");
                try {
                    // 查询信息。开始====================================================================
                    // 生成参数。开始======================================
                    String userId = null;
                    if (activity.baseApp.isLogged) {
                        userId = (String) activity.baseApp.loginUser.get("ids");
                    } else {
                        userId = "★";
                    }
                    // 生成参数。结束======================================

                    // 设置post值。开始=========================
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                            .addFormDataPart("userId", userId)
                            .addFormDataPart("treeId", cid)
                            .addFormDataPart("rows", "" + ROWS_PER_PAGE);
                    RequestBody requestBody = multipartBuilder.build();
                    // 设置post值。结束=========================

                    Request.Builder requestBuilder = new Request.Builder();
                    requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                    upHttpRequest = requestBuilder
                            .url("http://" + activity.baseApp.serverAddr + "/"
                                    + CommonParam.URL_GETNEWSINFOLIST)
                            .post(requestBody)
                            .build();
                    if (baseHttpClient == null) {
                        baseHttpClient = new OkHttpClient();
                    }
                    if (upHttpClient == null) {
                        upHttpClient = baseHttpClient.newBuilder().connectTimeout(activity.WAIT_SECONDS, TimeUnit.SECONDS).build();
                    }

                    upResponse = upHttpClient.newCall(upHttpRequest).execute();
                    Log.d("#succ", "#" + upResponse.isSuccessful());
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

                            listItems.clear();
                            ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();

                            for (int index = 0, len = data.size(); index < len; index++) {
                                HashMap<String, Object> listItem = new HashMap<String, Object>();
                                // 存放信息的 Map
                                HashMap<String, Object> info = null;
                                JSONObject json = data.getJSONObject(index);
                                HashMap<String, Object> info_tmp = JSONObject.parseObject(json.toJSONString(),
                                        HashMap.class);

                                info = mapToInfoMap(info_tmp);

                                String infoType = (String) info.get("infotype");
                                if (!CommonUtil.checkNB(infoType)) {
                                    infoType = CommonParam.NEWS_INFO_TYPE_NORMAL;
                                }
                                info.put("V_INFO_TYPE", infoType);

                                listItem.put("info", info);
                                listItems_tmp.add(listItem);

                                // 更新数据库。开始========================================
                                activity.infoTool.delete("t_news_info", "ids = ?",
                                        new String[]{(String) info.get("ids")});
                                // ★☆
                                long inResult = activity.infoTool.insert("t_news_info", info);
                                // 更新数据库。结束========================================
                            }
                            listItems.addAll(listItems_tmp);
                            result = CommonParam.RESULT_SUCCESS;
                        }

                        if (total <= ROWS_PER_PAGE) {
                            // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
                            infoListHandler.sendEmptyMessage(10);
                        } else {
                            // 启动队列下查功能
                            infoListHandler.sendEmptyMessage(20);
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
                Log.d("@@@@@FOOTER", "@@@@@@@@@@@@@@@@@@");
                try {
                    // 查询信息。开始====================================================================
                    // 生成参数。开始======================================
                    // 当前信息数量
                    int infoCount = listItems.size();
                    // 当前页数
                    int page = (infoCount % ROWS_PER_PAGE == 0 ? (infoCount / ROWS_PER_PAGE + 1)
                            : (infoCount / ROWS_PER_PAGE));
                    // 生成参数。结束======================================

                    // 设置post值。开始=========================
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                            .addFormDataPart("treeId", cid)
                            .addFormDataPart("page", "" + page)
                            .addFormDataPart("rows", "" + ROWS_PER_PAGE);
                    RequestBody requestBody = multipartBuilder.build();
                    // 设置post值。结束=========================

                    Request.Builder requestBuilder = new Request.Builder();
                    requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                    upHttpRequest = requestBuilder
                            .url("http://" + activity.baseApp.serverAddr + "/"
                                    + CommonParam.URL_GETNEWSINFOLIST)
                            .post(requestBody)
                            .build();
                    if (baseHttpClient == null) {
                        baseHttpClient = new OkHttpClient();
                    }
                    if (upHttpClient == null) {
                        upHttpClient = baseHttpClient.newBuilder().connectTimeout(activity.WAIT_SECONDS, TimeUnit.SECONDS).build();
                    }

                    upResponse = upHttpClient.newCall(upHttpRequest).execute();
                    Log.d("#succ", "#" + upResponse.isSuccessful());
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

                            ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();

                            for (int index = 0, len = data.size(); index < len; index++) {
                                HashMap<String, Object> listItem = new HashMap<String, Object>();
                                // 存放信息的 Map
                                HashMap<String, Object> info = null;
                                JSONObject json = data.getJSONObject(index);
                                HashMap<String, Object> info_tmp = JSONObject.parseObject(json.toJSONString(),
                                        HashMap.class);

                                info = mapToInfoMap(info_tmp);

                                String infoType = (String) info.get("infotype");
                                if (!CommonUtil.checkNB(infoType)) {
                                    infoType = CommonParam.NEWS_INFO_TYPE_NORMAL;
                                }
                                info.put("V_INFO_TYPE", infoType);

                                listItem.put("info", info);

                                if (!listItems.contains(listItem)) {
                                    listItems_tmp.add(listItem);
                                    // 更新数据库。开始========================================
                                    activity.infoTool.delete("t_news_info", "ids = ?",
                                            new String[]{(String) info.get("ids")});
                                    // ★☆
                                    long inResult = activity.infoTool.insert("t_news_info", info);
                                    // 更新数据库。结束========================================
                                }
                            }
                            listItems.addAll(listItems_tmp);
                            result = CommonParam.RESULT_SUCCESS;
                        }

                        infoCount = listItems.size();
                        if (total <= infoCount) {
                            // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
                            infoListHandler.sendEmptyMessage(10);
                        } else {
                            // 启动队列下查功能
                            infoListHandler.sendEmptyMessage(20);
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
            // activity.unWait();
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                Log.d("b#############" + CommonUtil.getDTC(), "加载完成！");
            } else {
                activity.show("数据加载失败");
            }
            recListAdapter.notifyDataSetChanged();
            isConnecting = false;
        }
    }

    /**
     * 将Map对象转化为Info对象
     *
     * @param m {@code HashMap<String, Object>} Map对象
     * @return {@code Info}
     */
    public Info mapToInfo(HashMap<String, Object> m) {
        Info o = new Info();
        o.setIds((String) m.get("id"));
        o.setTitle((String) m.get("title"));
        o.setInfotype((String) m.get("infoType"));
        o.setTreeid((String) m.get("treeId"));
        o.setPublishtime((String) m.get("publishtime"));
        o.setPublisher_id((String) m.get("publisherId"));
        o.setF2((String) m.get("pubshsherName"));

        return o;
    }

    /**
     * 将Map对象转化为Info的Map对象
     *
     * @param m {@code HashMap<String, Object>} Map对象
     * @return {@code HashMap<String, Object>}
     */
    public HashMap<String, Object> mapToInfoMap(HashMap<String, Object> m) {
        Info o = new Info();
        HashMap<String, Object> _m = JSONObject.parseObject(JSONObject.toJSONString(o), HashMap.class);

        _m.put("ids", m.get("id"));
        _m.put("title", m.get("title"));
        _m.put("infotype", m.get("infoType"));
        _m.put("treeid", m.get("treeId"));
        _m.put("publishtime", m.get("publishtime"));
        _m.put("publisher_id", m.get("publisherId"));
        _m.put("f2", m.get("pubshsherName"));

        return _m;
    }
}