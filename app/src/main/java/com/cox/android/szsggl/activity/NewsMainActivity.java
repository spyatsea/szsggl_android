/*
 * Copyright (c) 2016 山西考科思 版权所有
 */
package com.cox.android.szsggl.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBar.Tab;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.cox.android.szsggl.R;
import com.cox.android.ui.NewsViewPager;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 功能列表主页面
 *
 * @author 乔勇(Jacky Qiao)
 */
public class NewsMainActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 导航栏名称
     */
    TextView titleBarName;
    // 界面相关参数。开始===============================
    /**
     * 按钮：信息服务
     */
    private RadioButton nav_btn_news;
    /**
     * 按钮：信息查询
     */
    private Button nav_btn_baseinfo;
    /**
     * 按钮：信息录入
     */
    private Button nav_btn_editinfo;
    /**
     * 按钮：设置
     */
    private Button nav_btn_config;
    /**
     * Fragment名称前缀
     */
    public static String FRAGMENT_TAG = "FRAGMENT";
    private NewsViewPager viewPager;
    private ItemSectionsPagerAdapter pagerAdapter;

    /**
     * 新闻栏目信息List
     */
    private ArrayList<HashMap<String, Object>> channelList;

    static final int NOTIFICATION_ID = 0x123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = NewsMainActivity.this;

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_main);

        infoTool = getInfoTool();

        // 获取Intent
        // Intent intent = getIntent();
        // 获取Intent上携带的数据
        // Bundle data = intent.getExtras();
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        // 界面相关参数。开始===============================
        nav_btn_news = (RadioButton) findViewById(R.id.nav_btn_news);
        nav_btn_baseinfo = (Button) findViewById(R.id.nav_btn_baseinfo);
        nav_btn_editinfo = (Button) findViewById(R.id.nav_btn_editinfo);
        nav_btn_config = (Button) findViewById(R.id.nav_btn_config);

        // 显示等待窗口
        makeWaitDialog();
        titleBarName.setText(R.string.title_news);
        nav_btn_news.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        nav_btn_baseinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (baseApp.isLogged) {
                    // 用户角色
                    String roles = (String) baseApp.loginUser.get("roles");
                    if (CommonUtil.hasRole(roles, "[SYSADMIN]") || CommonUtil.hasRole(roles, "[ADMIN]")
                            || CommonUtil.hasRole(roles, "BIZ_A") || CommonUtil.hasRole(roles, "[LEADER]")) {
                        // 创建启动 Activity 的 Intent
                        // Intent intent = new Intent(classThis, BaseinfoMainActivity.class);
                        // // 信息传输Bundle
                        // // Bundle data = new Bundle();
                        // // data.putString("fromFlag", "store_main");
                        // // // 将数据存入Intent中
                        // // intent.putExtras(data);
                        // startActivity(intent);
                        // finish();
                        // overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                    } else {
                        show("您没有查询户籍信息的权限！");
                    }
                } else {
                    show("您需要登录后才能使用本功能！");
                }
            }
        });
        nav_btn_editinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (baseApp.isLogged) {
                    // 用户角色
                    String roles = (String) baseApp.loginUser.get("roles");
                    if (CommonUtil.hasRole(roles, "[SYSADMIN]") || CommonUtil.hasRole(roles, "[ADMIN]")
                            || CommonUtil.hasRole(roles, "BIZ_")) {
                        // 创建启动 Activity 的 Intent
                        // Intent intent = new Intent(classThis, EditinfoMainActivity.class);
                        // // 信息传输Bundle
                        // // Bundle data = new Bundle();
                        // // data.putString("fromFlag", "store_main");
                        // // // 将数据存入Intent中
                        // // intent.putExtras(data);
                        // startActivity(intent);
                        // finish();
                        // overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                    } else {
                        show("您没有录入信息的权限！");
                    }
                } else {
                    show("您需要登录后才能使用本功能！");
                }
            }
        });
        nav_btn_config.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, MeMainActivity.class);
                // 信息传输Bundle
                // Bundle data = new Bundle();
                // data.putString("fromFlag", "store_main");
                // // 将数据存入Intent中
                // intent.putExtras(data);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            }
        });

        // 生成channelList。开始=====================================
        channelList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = null;

        map = new HashMap<String, Object>();
        map.put("ids", "3b974df24a9f47bba22fb8efefca0344");
        map.put("title", "通知公告");
        channelList.add(map);

        map = new HashMap<String, Object>();
        map.put("ids", "196a4663793b42be868ef47007cec6c1");
        map.put("title", "工作动态");
        channelList.add(map);
        // 生成channelList。结束=====================================

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 获取ViewPager
        viewPager = (NewsViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(channelList.size());
        pagerAdapter = new ItemSectionsPagerAdapter(classThis, viewPager);

        // 遍历pagerAdapter对象所包含的全部Fragment。
        // 每个Fragment对应创建一个Tab标签
        for (int i = 0, len = channelList.size(); i < len; i++) {
            pagerAdapter.addTab(actionBar.newTab().setText((String) channelList.get(i).get("title")),
                    NewsChannelT1Fragment.class, null);
        }
        actionBar.hide();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 检查更新
        if (!isUpdating) {
            if (!baseApp.checkUpdateFlag) {
                baseApp.checkUpdateFlag = true;

                testUpdateApp("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUPDATE + "?token="
                        + CommonParam.APP_KEY + "&type=1", "0");
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color);
    }

    /**
     * 返回
     */
    @Override
    public void goBack() {
        makeExitDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
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

    public class ItemSectionsPagerAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public ItemSectionsPagerAdapter(AppCompatActivity activity, ViewPager pager) {
            super(((AppCompatActivity) activity).getSupportFragmentManager());
            mContext = (AppCompatActivity) activity;
            mActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        // 获取第position位置的Fragment
        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            TabInfo tabInfo = mTabs.get(position);
            // 信息传输Bundle
            Bundle data = new Bundle();
            // 栏目编号
            data.putString("cid", (String) channelList.get(position).get("ids"));
            // 栏目编号
            data.putString("cname", (String) channelList.get(position).get("title"));
            data.putInt("layoutWidth", screenWidth - CommonUtil.dip2px(classThis, 6f));
            data.putInt(CommonParam.FRAGMENT_INDEX, position + 1);
            data.putString("TAG", FRAGMENT_TAG + position);

            return Fragment.instantiate(mContext, tabInfo.clss.getName(), data);
        }

        // 该方法的返回值i表明该Adapter总共包括多少个Fragment
        @Override
        public int getCount() {
            return mTabs.size();
        }

        /**
         * 该方法的返回值决定每个Fragment的标题。
         * <p>
         * 该标题显示在fragment列表中或tab上。
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return getChannelName(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

    /**
     * 主进程 AsyncTask 类
     */
    private class MainTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：生成栏目Tab
         */
        private static final int PROGRESS_MAKE_CHANNEL_TAB = 1001;

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

            // 生成栏目Tab
            publishProgress(PROGRESS_MAKE_CHANNEL_TAB);

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
            if (progress[0] == PROGRESS_MAKE_CHANNEL_TAB) {
                // 生成栏目Tab

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
            // unWait();
        }
    }

    /**
     * 获得栏目名称
     *
     * @param i {@code int} tab索引
     * @return {@code String} 栏目名称
     */
    private String getChannelName(int i) {
        return (String) channelList.get(i).get("title");
    }
}
