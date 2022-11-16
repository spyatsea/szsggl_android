package com.cox.android.szsggl.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.MessageListAdapter;
import com.cox.android.szsggl.adapter.MessageListAdapter.ViewBinder;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * 消息类栏目：1
 * <p>
 * 下拉刷新时调用数据库数据
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressLint("DefaultLocale")
@SuppressWarnings({"unchecked"})
public class MessageChannelT1Fragment extends Fragment {
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
     * */
    // private String cname;
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
    private MessageListAdapter recListAdapter;

    private MessageMainActivity activity;

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
     * */
    // private static final int[] pics = { R.drawable.p1, R.drawable.p2, R.drawable.p3 };

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MessageMainActivity) getActivity();
        // 获取信息传输Bundle
        Bundle data = getArguments();
        // 布局尺寸
        // layoutWidth = data.getInt("layoutWidth");
        // 栏目编号
        cid = (String) data.getString("cid");
        // 栏目名称
        // cname = (String) data.getString("cname");
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
        LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_message_t1, container, false);

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
                            "select * from t_base_message model where model.valid='1' and model.biz_msg_cat=? order by DATETIME(model.createdtime) DESC LIMIT 0,"
                                    + ROWS_PER_PAGE, new String[]{cid});
            for (int index = 0, len = recList.size(); index < len; index++) {
                // 存放信息的 Map
                HashMap<String, Object> listItem = new HashMap<String, Object>();
                HashMap<String, Object> info = recList.get(index);
                if (CommonParam.MESSAGE_INFO_TYPE_NORMAL.equals("" + info.get("info_type"))) {
                    info.put("V_INFO_TYPE", CommonParam.MESSAGE_INFO_TYPE_NORMAL);
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
                recListAdapter = (MessageListAdapter) infoList.getAdapter();
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

                    recListAdapter = new MessageListAdapter(activity.getApplicationContext(), listItems,
                            R.layout.message_t1_list_normal_item, new String[]{"info"},
                            new int[]{R.id.newsItemTitle});
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
                                    textView.setText((String) info.get("description"));
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    Log.d("#size_3:" + cid, "" + infoList.getAdapter().getCount());
                    Log.d("###pos" + cid, "" + infoList.getLastVisiblePosition());
                    // 设置列表项点击事件
                    // infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    //
                    // @Override
                    // public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // // 列表项中的对象
                    // HashMap<String, Object> vMap = (HashMap<String, Object>) recListAdapter.getItem(position);
                    // HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");
                    //
                    // //Log.d("#", JSONObject.toJSONString(info));
                    // // // 创建信息传输Bundle
                    // // Bundle data = new Bundle();
                    // // // 信息id
                    // // data.putString("itemId", (String) info.get("ids"));
                    // // // 栏目名称
                    // // data.putString("channelName", cname);
                    // // // 创建启动 Activity 的
                    // // // Intent
                    // // Intent intent = new Intent(activity, MessageDetailT1Activity.class);
                    // // // 将数据存入 Intent 中
                    // // intent.putExtras(data);
                    // // startActivity(intent);
                    // // activity.overridePendingTransition(R.anim.activity_slide_left_in,
                    // // R.anim.activity_slide_left_out);
                    // }
                    // });
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
            MessageListAdapter recListAdapter = (MessageListAdapter) infoList.getAdapter();
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
         * */
        // private boolean resultFlag = false;
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

            searchType = (String) params[0];
            manualIndicatorFlag = (Boolean) params[1];
            // 显示数据加载指示器
            publishProgress(PROGRESS_SHOW_INDICATOR);

            if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                // 队首
                Log.d("@@@@@HEADER", "@@@@@@@@@@@@@@@@@@");

                // 删除多余的消息。开始============================
                if (activity.baseApp.remainMessageNum > 0) {
                    try {
                        activity.infoTool.delete("t_base_message",
                                "_id not in (SELECT _id FROM \"t_base_message\" where user_id=? order by _id desc LIMIT 0, "
                                        + activity.baseApp.remainMessageNum + ") and user_id=?", new String[]{(String) activity.baseApp.loginUser.get("ids"), (String) activity.baseApp.loginUser.get("ids")});
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                // 删除多余的消息。结束============================
                try {
                    // 查询信息。开始====================================================================
                    ArrayList<HashMap<String, Object>> recList = null;
                    if (cid == "1000") {
                        recList = (ArrayList<HashMap<String, Object>>) activity.infoTool
                                .getInfoMapList(
                                        "select * from t_base_message model where model.valid='1' and (model.biz_msg_cat=? or model.biz_msg_cat=? or model.biz_msg_cat=?) and model.user_id=? order by DATETIME(model.createdtime) DESC LIMIT 0,"
                                                + ROWS_PER_PAGE, new String[]{"1100", "1200", "1300", (String) activity.baseApp.loginUser.get("ids")});
                        total = activity.infoTool
                                .getCount(
                                        "select count(model.ids) from t_base_message model where model.valid='1' and (model.biz_msg_cat=? or model.biz_msg_cat=? or model.biz_msg_cat=?) and model.user_id=?",
                                        new String[]{"1100", "1200", "1300", (String) activity.baseApp.loginUser.get("ids")});
                    } else {
                        recList = (ArrayList<HashMap<String, Object>>) activity.infoTool
                                .getInfoMapList(
                                        "select * from t_base_message model where model.valid='1' and model.biz_msg_cat=? and model.user_id=? order by DATETIME(model.createdtime) DESC LIMIT 0,"
                                                + ROWS_PER_PAGE, new String[]{cid, (String) activity.baseApp.loginUser.get("ids")});
                        total = activity.infoTool
                                .getCount(
                                        "select count(model.ids) from t_base_message model where model.valid='1' and model.biz_msg_cat=? and model.user_id=?",
                                        new String[]{cid, (String) activity.baseApp.loginUser.get("ids")});
                    }
                    Log.d("##", "total:" + total);

                    listItems.clear();
                    ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();
                    for (int index = 0, len = recList.size(); index < len; index++) {
                        // 存放信息的 Map
                        HashMap<String, Object> listItem = new HashMap<String, Object>();
                        HashMap<String, Object> info = recList.get(index);

                        String infoType = info.get("info_type").toString();
                        if (!CommonUtil.checkNB(infoType)) {
                            infoType = CommonParam.MESSAGE_INFO_TYPE_NORMAL;
                        }
                        info.put("V_INFO_TYPE", infoType);

                        listItem.put("info", info);
                        listItems_tmp.add(listItem);
                    }

                    listItems.addAll(listItems_tmp);
                    result = CommonParam.RESULT_SUCCESS;

                    if (total <= ROWS_PER_PAGE) {
                        // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
                        infoList.setFooterListEnd();
                    } else {
                        // 启动队列下查功能
                        infoList.setFooterListContinue();
                    }
                    // 查询信息。结束====================================================================
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_FOOTER)) {
                // 队尾
                Log.d("@@@@@FOOTER", "@@@@@@@@@@@@@@@@@@");
                try {
                    if (cid == "1000") {
                        total = activity.infoTool
                                .getCount(
                                        "select count(model.ids) from t_base_message model where model.valid='1' and (model.biz_msg_cat=? or model.biz_msg_cat=? or model.biz_msg_cat=?) and model.user_id=?",
                                        new String[]{"1100", "1200", "1300", (String) activity.baseApp.loginUser.get("ids")});
                    } else {
                        total = activity.infoTool
                                .getCount(
                                        "select count(model.ids) from t_base_message model where model.valid='1' and model.biz_msg_cat=? and model.user_id=?",
                                        new String[]{cid, (String) activity.baseApp.loginUser.get("ids")});
                    }

                    // 当前信息数量
                    int infoCount = listItems.size();
                    // 当前页数
                    int page = (infoCount % ROWS_PER_PAGE == 0 ? (infoCount / ROWS_PER_PAGE) : (infoCount
                            / ROWS_PER_PAGE + 1));
                    // 总页数
                    int pageCount = (total % ROWS_PER_PAGE == 0 ? (total / ROWS_PER_PAGE) : (total / ROWS_PER_PAGE + 1));
                    if (page > pageCount && pageCount > 0) {
                        page = pageCount; // 如果当前页大于总页数，将当前页的值设为总页数
                    }
                    // 查询信息。开始====================================================================
                    ArrayList<HashMap<String, Object>> recList = null;

                    // 分页语句
                    String pagingStr = " LIMIT " + page * ROWS_PER_PAGE + "," + ROWS_PER_PAGE + " ";

                    if (cid == "1000") {
                        recList = (ArrayList<HashMap<String, Object>>) activity.infoTool
                                .getInfoMapList(
                                        "select * from t_base_message model where model.valid='1' and (model.biz_msg_cat=? or model.biz_msg_cat=? or model.biz_msg_cat=?) and model.user_id=? order by DATETIME(model.createdtime) DESC "
                                                + pagingStr, new String[]{"1100", "1200", "1300", (String) activity.baseApp.loginUser.get("ids")});
                    } else {
                        recList = (ArrayList<HashMap<String, Object>>) activity.infoTool
                                .getInfoMapList(
                                        "select * from t_base_message model where model.valid='1' and model.biz_msg_cat=? and model.user_id=? order by DATETIME(model.createdtime) DESC "
                                                + pagingStr, new String[]{cid, (String) activity.baseApp.loginUser.get("ids")});
                    }

                    ArrayList<HashMap<String, Object>> listItems_tmp = new ArrayList<HashMap<String, Object>>();

                    for (int index = 0, len = recList.size(); index < len; index++) {
                        // 存放信息的 Map
                        HashMap<String, Object> listItem = new HashMap<String, Object>();
                        HashMap<String, Object> info = recList.get(index);

                        String infoType = info.get("info_type").toString();
                        if (!CommonUtil.checkNB(infoType)) {
                            infoType = CommonParam.MESSAGE_INFO_TYPE_NORMAL;
                        }
                        info.put("V_INFO_TYPE", infoType);

                        listItem.put("info", info);

                        if (!listItems.contains(listItem)) {
                            listItems_tmp.add(listItem);
                        }
                    }

                    listItems.addAll(listItems_tmp);
                    result = CommonParam.RESULT_SUCCESS;

                    infoCount = listItems.size();

                    if (total <= infoCount) {
                        // 如果返回了所有信息，说明队列下面没有数据，停止队列下查功能
                        infoList.setFooterListEnd();
                    } else {
                        // 启动队列下查功能
                        infoList.setFooterListContinue();
                    }
                    // 查询信息。结束====================================================================
                } catch (Exception e) {
                    e.printStackTrace();
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
}