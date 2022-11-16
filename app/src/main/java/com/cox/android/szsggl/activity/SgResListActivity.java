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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.adapter.SgResListAdapter;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * 水工资源信息_列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class SgResListActivity extends DbActivity {
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
     * 搜索图标按钮
     */
    ImageButton searchBarBtn;
    /**
     * 回到主页按钮
     */
    ImageButton homeBtn;
    /**
     * 搜索区
     */
    LinearLayout titleBarSearchLayout;
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

    private EditText searchTitleTv;
    private ImageButton searchTitleClearBtn;
    private TextView searchPNameTv;
    private TextView searchPIdTv;
    private TextView searchBmNameTv;
    private TextView searchBmIdTv;
    private ImageButton searchBmNameClearBtn;
    private ImageButton searchPNameClearBtn;
    private LinearLayout searchPNameLayout;
    private LinearLayout searchBmNameLayout;
    private Spinner catSpinner;
    private Spinner scopeSpinner;
    private Spinner activeSpinner;
    private Button searchBtn;
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
    private SgResListAdapter infoListAdapter;
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

    /**
     * 类别List
     */
    private ArrayList<HashMap<String, Object>> catList = new ArrayList<HashMap<String, Object>>();
    /**
     * 范围List
     */
    private ArrayList<HashMap<String, Object>> scopeList = new ArrayList<HashMap<String, Object>>();
    /**
     * 激活List
     */
    private ArrayList<HashMap<String, Object>> activeList = new ArrayList<HashMap<String, Object>>();

    // 网络连接相关参数。开始==========================================
    /**
     * 是否正在传输数据
     */
    boolean isConnecting = false;
    // 网络连接相关参数。结束==========================================

    // 查询参数。开始==========================================
    // 查询参数。结束==========================================

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

        classThis = SgResListActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id", "ROOT");

        if ("ROOT".equals(infoId)) {
            setContentView(R.layout.sg_res_list_root);
        } else {
            setContentView(R.layout.sg_res_list);
        }

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleText.setSingleLine(true);
        titleText.setText("水工资源");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        searchBarBtn.setTag(false);
        searchBarBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = null;
                if (titleBarSearchLayout.getVisibility() == View.VISIBLE) {
                    searchBarBtn.setTag(false);
                    searchBarBtn.setImageResource(R.drawable.titlebar_btn_search_down);
                    titleBarSearchLayout.setVisibility(View.GONE);
                    animation = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_out);
                } else {
                    searchBarBtn.setTag(true);
                    searchBarBtn.setImageResource(R.drawable.titlebar_btn_search_up);
                    titleBarSearchLayout.setVisibility(View.VISIBLE);
                    animation = AnimationUtils.loadAnimation(classThis, android.R.anim.fade_in);
                }
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
                        // boolean flag = (Boolean) searchBarBtn.getTag();
                    }
                });
                // 运行动画
                titleBarSearchLayout.startAnimation(animation);
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
        searchTitleClearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTitleTv.setText("");
            }
        });
        searchPNameTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, TreeListActivity.class);
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putString("fromFlag", "sg_res_list");
                data.putString("type", "sg_res");
                data.putString("view", "searchPNameTv");
                data.putString("titleText", "水工资源导航");
                if (CommonUtil.checkNB(searchPIdTv.getText().toString())) {
                    HashMap<String, Object> data_map = new HashMap<String, Object>();
                    data_map.put("id", searchPIdTv.getText().toString());
                    data_map.put("text", searchPNameTv.getText().toString());
                    data.putSerializable("v", data_map);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_CHOOSE_DATA);
            }
        });
        searchPNameClearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPNameTv.setText("");
                searchPIdTv.setText("");
            }
        });
        searchBmNameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建启动 Activity 的 Intent
                Intent intent = new Intent(classThis, TreeListActivity.class);
                // 信息传输Bundle
                Bundle data = new Bundle();
                data.putString("fromFlag", "sg_res_list");
                data.putString("type", "dept");
                data.putString("view", "searchBmNameTv");
                data.putString("titleText", "组织导航");
                if (CommonUtil.checkNB(searchBmIdTv.getText().toString())) {
                    HashMap<String, Object> data_map = new HashMap<String, Object>();
                    data_map.put("id", searchBmIdTv.getText().toString());
                    data_map.put("text", searchBmNameTv.getText().toString());
                    data.putSerializable("v", data_map);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivityForResult(intent, CommonParam.REQUESTCODE_CHOOSE_DATA);
            }
        });
        searchBmNameClearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBmNameTv.setText("");
                searchBmIdTv.setText("");
            }
        });
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isConnecting) {
                    makeWaitDialog();
                    listItems.clear();
                    infoListAdapter.notifyDataSetChanged();
                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                }
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
        } else if (requestCode == CommonParam.REQUESTCODE_CHOOSE_DATA && resultCode == CommonParam.RESULTCODE_CHOOSE_DATA) {
            Bundle data = intent.getExtras();

            String searchType = (String) data.getString("type");

            if ("sg_res".equals(searchType)) {
                HashMap<String, String> data_map = (HashMap<String, String>) data.getSerializable("v");
                String view = (String) data.getString("view");

                if ("searchPNameTv".equals(view)) {
                    if (data_map != null) {
                        searchPIdTv.setText(CommonUtil.N2B((String) data_map.get("id")));
                        searchPNameTv.setText(CommonUtil.N2B((String) data_map.get("text")));
                    } else {
                        searchPIdTv.setText("");
                        searchPNameTv.setText("");
                    }
                }
            } else if ("dept".equals(searchType)) {
                HashMap<String, String> data_map = (HashMap<String, String>) data.getSerializable("v");
                String view = (String) data.getString("view");

                if ("searchBmNameTv".equals(view)) {
                    if (data_map != null) {
                        searchBmIdTv.setText(CommonUtil.N2B((String) data_map.get("id")));
                        searchBmNameTv.setText(CommonUtil.N2B((String) data_map.get("text")));
                    } else {
                        searchBmIdTv.setText("");
                        searchBmNameTv.setText("");
                    }
                }
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

            infoCount = infoTool.getCount("select count(model.ids) from t_szfgs_sgres model where model.valid='1'",
                    new String[]{});

            catList = infoTool
                    .getInfoMapList(
                            "SELECT model.ids code , model.title name FROM t_szfgs_sgcategory model WHERE model.valid='1' and model.ids<>'ROOT' ORDER BY model.pxbh ASC",
                            new String[]{});
            HashMap<String, Object> _catMap = new HashMap<String, Object>();
            _catMap.put("code", "");
            _catMap.put("name", "请选择…");
            catList.add(0, _catMap);

            scopeList = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> _scopeMap_1 = new HashMap<String, Object>();
            _scopeMap_1.put("code", "");
            _scopeMap_1.put("name", "本级");
            scopeList.add(_scopeMap_1);

            HashMap<String, Object> _scopeMap_2 = new HashMap<String, Object>();
            _scopeMap_2.put("code", "1");
            _scopeMap_2.put("name", "下级");
            scopeList.add(_scopeMap_2);

            activeList = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> _activeMap_1 = new HashMap<String, Object>();
            _activeMap_1.put("code", "");
            _activeMap_1.put("name", "全部");
            activeList.add(_activeMap_1);

            HashMap<String, Object> _activeMap_2 = new HashMap<String, Object>();
            _activeMap_2.put("code", "0");
            _activeMap_2.put("name", "否");
            activeList.add(_activeMap_2);

            HashMap<String, Object> _activeMap_3 = new HashMap<String, Object>();
            _activeMap_3.put("code", "1");
            _activeMap_3.put("name", "是");
            activeList.add(_activeMap_3);
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
                if ("ROOT".equals(infoId)) {
                    titleText.setText("水工资源");
                    backBtn.setImageDrawable(getResources().getDrawable(R.drawable.title_back_btn));
                    searchBarBtn.setVisibility(View.VISIBLE);
                } else {
                    titleText.setText((String) infoObj.get("title"));
                    backBtn.setImageDrawable(getResources().getDrawable(R.drawable.title_arrow_up_left_btn));
                    searchBarBtn.setVisibility(View.GONE);
                }
                homeBtn.setVisibility(View.VISIBLE);
                listTitleLayout.setVisibility(View.VISIBLE);
                listTitleTv.setGravity(Gravity.RIGHT);
                listTitleTv.setText(getString(R.string.pagination_info, 0, 0, 0));

                // 设置catSpinner。开始=================================================
                SimpleAdapter catAdapter = new SimpleAdapter(classThis, catList,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                catSpinner.setAdapter(catAdapter);
                catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // 设置catSpinner。结束=================================================

                // 设置scopeSpinner。开始=================================================
                SimpleAdapter scopeAdapter = new SimpleAdapter(classThis, scopeList,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                scopeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                scopeSpinner.setAdapter(scopeAdapter);
                scopeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // 设置scopeSpinner。结束=================================================

                // 设置activeSpinner。开始=================================================
                SimpleAdapter activeAdapter = new SimpleAdapter(classThis, activeList,
                        R.layout.simple_spinner_item, new String[]{"name"}, new int[]{android.R.id.text1});
                activeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                activeSpinner.setAdapter(activeAdapter);
                activeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // An item was selected. You can retrieve the selected item
                        // using
                        // parent.getItemAtPosition(pos)
                        // Map<String, String> m = (HashMap<String, String>) parent
                        // .getItemAtPosition(pos);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                });
                // 设置activeSpinner。结束=================================================
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                infoListAdapter = (SgResListAdapter) infoList.getAdapter();
                if (infoListAdapter == null) {
                    infoList.setOnRefreshListener(new OnRefreshListener() {

                        @Override
                        public void onRefresh() {
                            if (!infoList.isRefreshingEnd()) {
                                // 查询信息
                                if (!isConnecting) {
                                    searchTask = new SearchTask().execute(CommonParam.SEARCH_INFO_TYPE_HEADER, false);
                                }
                            }
                        }
                    });
                    infoListAdapter = new SgResListAdapter(getApplicationContext(), listItems, R.layout.sg_res_list_item,
                            new String[]{"info", "info", "info", "info", "info", "info", "info"}, new int[]{R.id.infoSn, R.id.infoName,
                            R.id.info_c1, R.id.info_c2, R.id.info_c3, R.id.info_c4, R.id.showButton});

                    // 对绑定的数据进行处理
                    infoListAdapter.setViewBinder(new SgResListAdapter.ViewBinder() {

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof ImageButton) {
                                ImageButton btn = (ImageButton) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (btn.getId() == R.id.showButton) {
                                    Integer n = (Integer) info.get("n");
                                    if (n > 0) {
                                        // 有子资源
                                        btn.setVisibility(View.VISIBLE);
                                        int index = CommonUtil.getListItemIndex(listItems, info);
                                        btn.setTag(index);
                                        btn.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int index = (int) v.getTag();
                                                // 列表项中的对象
                                                HashMap<String, Object> vMap = (HashMap<String, Object>) listItems.get(index);
                                                HashMap<String, Object> info = (HashMap<String, Object>) vMap.get("info");

                                                // 创建信息传输Bundle
                                                Bundle data = new Bundle();
                                                data.putString("id", (String) info.get("ids"));
                                                data.putString("fromFlag", "list");

                                                // 创建启动 Activity 的 Intent
                                                Intent intent = new Intent(classThis, SgResListActivity.class);

                                                // 将数据存入 Intent 中
                                                intent.putExtras(data);
                                                startActivityForResult(intent, CommonParam.REQUESTCODE_INFO);
                                                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
                                            }
                                        });
                                    } else {
                                        // 没有子资源
                                        btn.setVisibility(View.GONE);
                                    }
                                }
                                return true;
                            } else if (view instanceof TextView) {
                                TextView textView = (TextView) view;
                                // 记录信息
                                HashMap<String, Object> info = (HashMap<String, Object>) data;
                                if (textView.getId() == R.id.infoSn) {
                                    int infoIndex = CommonUtil.getListItemIndex(listItems, info);
                                    textView.setText("" + (infoIndex + 1));
                                } else if (textView.getId() == R.id.infoName) {
                                    textView.setText(CommonUtil.N2B((String) info.get("title")));
                                } else if (textView.getId() == R.id.info_c1) {
                                    textView.setText("编号：" + CommonUtil.N2B((String) info.get("pxbh")));
                                } else if (textView.getId() == R.id.info_c2) {
                                    textView.setText("类别：" + CommonUtil.N2B((String) info.get("ctitle")));
                                } else if (textView.getId() == R.id.info_c3) {
                                    textView.setText("组织：" + CommonUtil.N2B((String) info.get("bmtitle")));
                                } else if (textView.getId() == R.id.info_c4) {
                                    String active = CommonUtil.N2B((String) info.get("active"));
                                    Drawable icon = null;
                                    if (CommonParam.YES.equals(active)) {
                                        icon = getResources().getDrawable(R.drawable.circle_green);
                                    } else {
                                        icon = getResources().getDrawable(R.drawable.circle_red);
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
                            data.putString("id", (String) info.get("ids"));
                            data.putString("fromFlag", "list");

                            // 创建启动 Activity 的 Intent
                            Intent intent = new Intent(classThis, SgResShowActivity.class);

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

        // 查询参数。开始=========================
        // 标题
        private String title;
        // 父节点编号
        private String pid;
        // 组织编号
        private String bmid;
        // 类别编号
        private String cid;
        // 搜索范围
        private String scope;
        // 是否激活
        private String active;
        // 查询参数。结束========================

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

            title = searchTitleTv.getText().toString();
            if ("ROOT".equals(infoId)) {
                pid = searchPIdTv.getText().toString();
            } else {
                pid = infoId;
            }
            bmid = searchBmIdTv.getText().toString();
            cid = ((HashMap<String, String>) catSpinner
                    .getItemAtPosition(catSpinner.getFirstVisiblePosition())).get("code");
            scope = ((HashMap<String, String>) scopeSpinner
                    .getItemAtPosition(scopeSpinner.getFirstVisiblePosition())).get("code");
            active = ((HashMap<String, String>) activeSpinner
                    .getItemAtPosition(activeSpinner.getFirstVisiblePosition())).get("code");
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

            // 生成参数。开始======================================
            // 排序字段
            String orderStr = " model.pxbh ASC ";
            // 查询语句
            String queryStr = " WHERE model.valid='1' AND model.ids<>'ROOT' ";
            // 查询参数
            List<String> queryParamList = new ArrayList<String>();
            // 查询参数数组
            String[] queryParamArray;

            if (CommonUtil.checkNB(title)) {
                queryStr = queryStr + " AND UPPER(model.title) LIKE ?";
                queryParamList.add("%" + title.toUpperCase(Locale.CHINA) + "%");
            }
            if (CommonUtil.checkNB(active)) {
                queryStr = queryStr + " AND model.active=?";
                queryParamList.add(active);
            }
            if (!CommonUtil.checkNB(pid)) {
                pid = "ROOT";
            }
            if (CommonUtil.checkNB(scope)) {
                // 下级
                if (!CommonUtil.checkNB(pid) || "ROOT".equals(pid)) {

                } else {
                    queryStr = queryStr + " AND INSTR(model.fldh, ?)>0";
                    queryParamList.add("[" + pid + "]");
                }
            } else {
                // 本级
                queryStr = queryStr + " AND model.pid=?";
                queryParamList.add(pid);
            }
            if (CommonUtil.checkNB(cid)) {
                queryStr = queryStr + " AND model.cid=?";
                queryParamList.add(cid);
            }
            if (CommonUtil.checkNB(bmid)) {
                queryStr = queryStr + " and model.bmid=?";
                queryParamList.add(bmid);
            }

            queryParamArray = new String[queryParamList.size()];
            for (int i = 0, len = queryParamList.size(); i < len; i++) {
                queryParamArray[i] = queryParamList.get(i);
            }
            // 生成参数。结束======================================

            if (searchType.equals(CommonParam.SEARCH_INFO_TYPE_HEADER)) {
                // 队首
                Log.d("@@HEADER", "@@@@@@@@@@@@@@@@@@");

                // 查询信息。开始====================================================================
                ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool
                        .getInfoMapList(
                                "SELECT * FROM v_szfgs_sgres_list model " + queryStr + " ORDER BY " + orderStr + " LIMIT 0," + ROWS_PER_PAGE,
                                queryParamArray);
                total = infoTool.getCount("SELECT COUNT(model.ids) FROM t_szfgs_sgres model " + queryStr, queryParamArray);
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
                                "SELECT * FROM v_szfgs_sgres_list model " + queryStr + " ORDER BY " + orderStr + " LIMIT " + ((page - 1) * ROWS_PER_PAGE) + "," + ROWS_PER_PAGE,
                                queryParamArray);
                total = infoTool.getCount("SELECT COUNT(model.ids) FROM t_szfgs_sgres model " + queryStr, queryParamArray);
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
     * 查找view
     */
    public void findViews() {
        contentView = (LinearLayout) findViewById(R.id.contentView);
        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        searchBarBtn = (ImageButton) findViewById(R.id.searchBarBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        titleBarSearchLayout = (LinearLayout) findViewById(R.id.title_bar_search_layout);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        searchTitleTv = (EditText) findViewById(R.id.search_title_tv);
        searchTitleClearBtn = (ImageButton) findViewById(R.id.search_title_clear_btn);
        searchPNameLayout = (LinearLayout) findViewById(R.id.search_pname_layout);
        searchPNameTv = (TextView) findViewById(R.id.search_pname_tv);
        searchPIdTv = (TextView) findViewById(R.id.search_pid_tv);
        searchPNameClearBtn = (ImageButton) findViewById(R.id.search_pname_clear_btn);
        searchBmNameTv = (TextView) findViewById(R.id.search_bmname_tv);
        searchBmIdTv = (TextView) findViewById(R.id.search_bmid_tv);
        searchBmNameClearBtn = (ImageButton) findViewById(R.id.search_bmname_clear_btn);
        searchBmNameLayout = (LinearLayout) findViewById(R.id.search_bmname_layout);
        catSpinner = (Spinner) findViewById(R.id.search_cat_spinner);
        scopeSpinner = (Spinner) findViewById(R.id.search_scoper_spinner);
        activeSpinner = (Spinner) findViewById(R.id.search_active_spinner);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        // 界面相关参数。开始===============================
        infoList = (PullToRefreshListView) findViewById(R.id.infoList);
        // 界面相关参数。结束===============================
    }
}
