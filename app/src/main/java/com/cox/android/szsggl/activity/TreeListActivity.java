/*
 * Copyright (c) 2020 乔勇(Jacky Qiao) 版权所有
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.holder.ArrowExpandSelectableHeaderHolder;
import com.cox.android.szsggl.holder.IconTreeItemHolder;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;
import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 树状列表页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class TreeListActivity extends DbActivity {
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
     * 回到顶部按钮
     */
    ImageButton topBtn;
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
    /**
     * 提交
     */
    private Button submitBtn;
    /**
     * 清空
     */
    private Button clearBtn;

    RelativeLayout treeContainer;
    // 界面相关参数。结束===============================
    /**
     * 主进程 AsyncTask 对象
     */
    AsyncTask<Object, Integer, String> mainTask;
    /**
     * 目录对象
     */
    private AndroidTreeView treeView;
    /**
     * 选中的节点
     */
    public TreeNode selectedNode;
    /**
     * 查询类别
     */
    private String searchType;
    /**
     * 引用页面的对应值域
     */
    private String searchView;
    /**
     * 之前选择的值对象
     */
    private HashMap<String, Object> searchV;
    /**
     * 要返回值的Activity
     */
    private Class activityClass;

    // 网络连接相关参数。开始==========================================
    // 网络连接相关参数。结束==========================================

    // 查询参数。开始==========================================
    // 查询参数。结束==========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = TreeListActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        fromFlag = data.getString("fromFlag", "search");
        searchType = data.getString("type", "info");
        searchView = data.getString("view");
        searchV = (HashMap<String, Object>) data.getSerializable("v");
        // 页面标题
        String titleText = data.getString("titleText", "导航");

        setContentView(R.layout.tree_list);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText(titleText);

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        topBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearLayout tree_items = findViewById(R.id.tree_items);
                FrameLayout scrollView = (FrameLayout) tree_items.getParent();
                CommonUtil.scrollTo(scrollView, 0, 0);
            }
        });
        goBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
        clearBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedNode != null) {
                    selectedNode.setSelected(false);
                    View arrow_layout = selectedNode.getViewHolder().getView().findViewById(R.id.arrow_layout);
                    PrintView iconView = (PrintView) arrow_layout.findViewById(R.id.icon);
                    arrow_layout.setBackgroundResource(R.drawable.color_grey_selector);
                    iconView.setIconText(getResources().getString(((IconTreeItemHolder.IconTreeItem) selectedNode.getValue()).icon));
                    iconView.setIconColor(R.color.background_yellow);
                    selectedNode = null;
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

        private TreeNode root;

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

            infoTool = getInfoTool();
            // 处理数据。开始============================================================================
            // 列表数据
            ArrayList<HashMap<String, Object>> list = null;
            if ("sg_res".equals(searchType)) {
                list = getTreeData_sg_res();
            } else if ("dept".equals(searchType)) {
                list = getTreeData_dept();
            } else {
                list = new ArrayList<HashMap<String, Object>>();
            }

            root = TreeNode.root();
            processNode(root, list);
            list = null;
            // 生成测试节点。开始============================
            // TreeNode s1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder with very long name ")).setViewHolder(
            //         new ArrowExpandSelectableHeaderHolder(classThis));
            // TreeNode s2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Another folder with very long name")).setViewHolder(
            //         new ArrowExpandSelectableHeaderHolder(classThis));
//
            // fillFolder(s1);
            // fillFolder(s2);
//
            // root.addChildren(s1, s2);
            // 生成测试节点。结束============================
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
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成信息列表
                treeView = new AndroidTreeView(classThis, root);
                treeView.setDefaultAnimation(true);
                treeView.setUse2dScroll(true);
                treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                treeView.setDefaultNodeClickListener(null);
                treeView.setDefaultViewHolder(ArrowExpandSelectableHeaderHolder.class);
                treeView.setSelectionModeEnabled(false);

                treeContainer.addView(treeView.getView());
                treeView.setUseAutoToggle(false);

                treeView.collapseAll();

                LinearLayout tree_items = findViewById(R.id.tree_items);
                // 这里将树状目录周围留出空白，这样目录不会离边缘太近。更方便查看和操作。
                tree_items.setPadding(20, 30, 20, 30);

                // 展开已选择的节点
                if (selectedNode != null) {
                    TreeNode pNode = selectedNode;
                    while (!pNode.isRoot()) {
                        Log.d("##p", "" + ((IconTreeItemHolder.IconTreeItem) pNode.getValue()).id);
                        pNode = pNode.getParent();
                        treeView.expandNode(pNode);
                    }
                    tree_items.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View v = selectedNode.getViewHolder().getView();
                            int location[] = new int[2];
                            v.getLocationOnScreen(location);
                            // Log.d("##xy1", "" + location[0] + "," + location[1]);

                            LinearLayout tree_items = findViewById(R.id.tree_items);
                            FrameLayout scrollView = (FrameLayout) tree_items.getParent();
                            scrollView.scrollTo(0, location[1] - screenHeight / 2 - 100);
                        }
                    }, 100);
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
     * 测试节点默认名称
     */
    private static final String TEST_NODE_NAME = "Very long name for folder";

    /**
     * 生成测试节点
     */
    private void fillFolder(TreeNode folder) {
        TreeNode currentNode = folder;
        for (int i = 0; i < 10; i++) {
            TreeNode file = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, TEST_NODE_NAME + " " + i));
            currentNode.addChild(file);
            currentNode = file;
        }
    }

    /**
     * 生成节点
     *
     * @param root {@code TreeNode} 根节点
     * @param list {@code ArrayList<HashMap<String, Object>>} 节点数据
     */
    private void processNode(TreeNode root, ArrayList<HashMap<String, Object>> list) {
        Log.d("####processNode", "=======");
        // 是否需要预先选中值
        boolean needPreSelectFlag = false;
        String pre_id = null;
        if (searchV != null) {
            needPreSelectFlag = true;
            pre_id = CommonUtil.N2B((String) searchV.get("id"));
            Log.d("#v", JSONObject.toJSONString(searchV));
        }
        HashMap<String, TreeNode> map = new HashMap<String, TreeNode>();
        for (int i = 0, len = list.size(); i < len; i++) {
            HashMap<String, Object> o = list.get(i);
            String id = (String) o.get("id");
            String pid = (String) o.get("p");
            String title = (String) o.get("t");
            TreeNode node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, title, id)).setViewHolder(new ArrowExpandSelectableHeaderHolder(classThis));
            map.put(id, node);

            if ("ROOT".equals(pid)) {
                root.addChild(node);
            }

            if (needPreSelectFlag && id.equals(pre_id)) {
                needPreSelectFlag = false;
                selectedNode = node;
                selectedNode.setSelected(true);
                View arrow_layout = selectedNode.getViewHolder().getView().findViewById(R.id.arrow_layout);
                PrintView iconView = (PrintView) arrow_layout.findViewById(R.id.icon);
                iconView.setIconText(getResources().getString(R.string.ic_check_circle));
                iconView.setIconColor(R.color.text_color_orange_1);
                arrow_layout.setBackgroundResource(R.drawable.border_yellow);
            }
        }

        for (int i = 0, len = list.size(); i < len; i++) {
            HashMap<String, Object> o = list.get(i);
            String id = (String) o.get("id");
            String pid = (String) o.get("p");

            TreeNode node = map.get(id);
            TreeNode pNode = map.get(pid);
            if (pNode != null) {
                pNode.addChild(node);
            }
        }
    }

    /**
     * 生成节点
     *
     * @param root {@code TreeNode} 根节点
     * @param s    {@code s} 节点数据
     */
    private void processNode_s(TreeNode root, String s) {
        Log.d("####processNode_s", "=======");
        // 是否需要预先选中值
        boolean needPreSelectFlag = false;
        String pre_id = null;
        if (searchV != null) {
            needPreSelectFlag = true;
            pre_id = CommonUtil.N2B((String) searchV.get("id"));
            Log.d("#v", JSONObject.toJSONString(searchV));
        }
        JSONArray array = JSONArray.parseArray(s);
        HashMap<String, TreeNode> map = new HashMap<String, TreeNode>();
        for (int i = 0, len = array.size(); i < len; i++) {
            JSONObject o = array.getJSONObject(i);
            String id = o.getString("id");
            String pid = o.getString("p");
            String title = o.getString("t");
            TreeNode node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, title, id)).setViewHolder(new ArrowExpandSelectableHeaderHolder(classThis));
            map.put(id, node);

            if ("ROOT".equals(pid)) {
                root.addChild(node);
            }

            if (needPreSelectFlag && id.equals(pre_id)) {
                needPreSelectFlag = false;
                selectedNode = node;
                selectedNode.setSelected(true);
                View arrow_layout = selectedNode.getViewHolder().getView().findViewById(R.id.arrow_layout);
                PrintView iconView = (PrintView) arrow_layout.findViewById(R.id.icon);
                iconView.setIconText(getResources().getString(R.string.ic_check_circle));
                iconView.setIconColor(R.color.text_color_orange_1);
                arrow_layout.setBackgroundResource(R.drawable.border_yellow);
            }
        }

        for (int i = 0, len = array.size(); i < len; i++) {
            JSONObject o = array.getJSONObject(i);
            String id = o.getString("id");
            String pid = o.getString("p");

            TreeNode node = map.get(id);
            TreeNode pNode = map.get(pid);
            if (pNode != null) {
                pNode.addChild(node);
            }
        }
    }

    /**
     * 提交信息
     */
    public void submit() {
        boolean submitFlag = false;
        String errorMsg = "";

//        if (selectedNode == null) {
//            errorMsg = "请选择日期！";
//        } else {
        submitFlag = true;
//        }

        if (!submitFlag) {
            // 不能提交
            if (CommonUtil.checkNB(errorMsg)) {
                show(errorMsg);
            }
        } else {
            // 可以提交
            // 创建信息传输Bundle
            Bundle data = new Bundle();
            if (selectedNode != null) {
                IconTreeItemHolder.IconTreeItem value = (IconTreeItemHolder.IconTreeItem) selectedNode.getValue();
                HashMap<String, Object> data_map = new HashMap<String, Object>();
                data_map.put("id", value.id);
                data_map.put("text", value.text);
                data.putSerializable("v", data_map);
            }
            data.putString("type", searchType);
            data.putString("view", searchView);
            // 创建启动 Activity 的 Intent
            Intent intent = new Intent(classThis, activityClass);
            // 将数据存入Intent中
            intent.putExtras(data);
            // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
            setResult(CommonParam.RESULTCODE_CHOOSE_DATA, intent);
            goBack();
        }
    }

    /**
     * 获取数据：sg_res
     *
     * @return {@code ArrayList<HashMap<String, Object>>} 数据
     */
    private ArrayList<HashMap<String, Object>> getTreeData_sg_res() {
        ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) infoTool
                .getInfoMapList(
                        "SELECT model.ids id, model.title t, model.pid p FROM t_szfgs_sgres model WHERE model.valid='1' AND model.ids<>? ORDER BY model.pxbh ASC",
                        new String[]{"ROOT"});

        return list;
    }

    /**
     * 获取数据：dept
     *
     * @return {@code ArrayList<HashMap<String, Object>>} 数据
     */
    private ArrayList<HashMap<String, Object>> getTreeData_dept() {
        // 这里将偏关分公司的编号做为ROOT
        ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) infoTool
                .getInfoMapList(
                        "SELECT model.ids id, model.title t, model.pids p FROM t_base_deptinfo model WHERE model.valid='1' AND model.ids<>? AND INSTR(model.fldh, ?)=1 ORDER BY model.pxbh ASC",
                        new String[]{"2aa2dc3166c140f3943c2708c84ae498", "{DEPTROOT}{x0000000000000000000000000000001}{2aa2dc3166c140f3943c2708c84ae498}"});
        // 重新设置根信息编号
        for (HashMap<String, Object> o : list) {
            String pid = (String) o.get("p");
            if ("2aa2dc3166c140f3943c2708c84ae498".equals(pid)) {
                o.put("p", "ROOT");
            }
        }
        return list;
    }

    /**
     * 查找view
     */
    public void findViews() {
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        topBtn = (ImageButton) findViewById(R.id.topBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        listTitleTv = (TextView) findViewById(R.id.listTitleTv);
        // 界面相关参数。开始===============================
        treeContainer = (RelativeLayout) findViewById(R.id.treeContainer);
        // 界面相关参数。结束===============================
    }

}
