package com.cox.android.szsggl.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

/**
 * 显示网页信息详情的Activity（由程序内部调用）
 */
public class WebDetailActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    private final static int FILECHOOSER_RESULTCODE = 100;
    /**
     * 页面Handler
     */
    private final Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case 10:
                    makeWaitDialog();
                    break;
                case 11:
                    unWait();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    /**
     * 导航栏名称
     */
    TextView titleBarName;
    /**
     * 信息栏目类型
     * */
    // private String channelType;
    /**
     * 外部链接标志
     * <p>
     * 如果为true，代表是从Notification或者其他程序跳转过来的。
     * */
    // private boolean outterFlag;
    /**
     * 信息对象
     * */
    // private HashMap<String, Object> info;
    /**
     * 信息的url
     */
    private String pageUrl;
    /**
     * 信息加载容器
     * */
    // private LinearLayout webViewContainer;
    /**
     * 信息栏目名称
     */
    private String channelName;
    /**
     * 信息加载提示容器
     */
    private LinearLayout webViewLoadingContainer;
    /**
     * 浏览器容器Layout
     */
    private LinearLayout webLayout;
    /**
     * 浏览器View
     */
    private WebView webView;
    /**
     * 信息加载提示信息
     */
    private TextView loadingMsg;
    private WebAppInterface webAppInterface;
    /**
     * 是否正确加载网页
     */
    private boolean loadSuccessFlag = true;
    // 文件上传相关代码。开始===================================
    private ValueCallback<Uri> mUploadMessage;

    public WebView getWebView() {
        return webView;
    }

    // 文件上传相关代码。结束===================================

    /**
     * 历史记录List
     */
    // private ArrayList<String> loadHistoryUrls = new ArrayList<String>();
    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = WebDetailActivity.this;

        setContentView(R.layout.web_detail);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        // 记录 id
        pageUrl = data.getString("url");
        // 栏目名称
        channelName = data.getString("title");
        // 外部链接标志
        // outterFlag = data.getBoolean("outterFlag", false);

        findViews();

        titleBarName.setText(channelName);

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            // 关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }
        });

        webView.setDownloadListener(new MyDownloadListener());
        webAppInterface = new WebAppInterface(this);
        webView.addJavascriptInterface(webAppInterface, "QAndroid");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        // webSettings.setPluginState(PluginState.ON);

        webViewLoadingContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadSuccessFlag = true;
                loadingMsg.setText(R.string.alert_info_loading);
                webView.reload();
            }
        });
        webViewLoadingContainer.setClickable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // 返回
                goBack();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 执行主进程
        new MainTask().execute();
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
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webLayout.removeView(webView);
            webView.removeAllViews();
            webView.clearCache(true);
            webView.destroy();
        }
        super.onDestroy();
    }

    /**
     * 为url加上时间点标志
     */
    public String getUrl(String url) {
        // if (url.contains("?")) {
        // url = url + "&timeflag=" + Calendar.getInstance().getTimeInMillis();
        // } else {
        // url = url + "?timeflag=" + Calendar.getInstance().getTimeInMillis();
        // }
        // Log.d("###", "#" + baseApp.getChannelId());
        return url;
    }

    /**
     * 显示日期选择窗口
     */
    public void makeDateDlg(String dt) {
        //String dt = CommonUtil.getDT("yyyy-MM-dd");
        String year_str = dt.substring(0, 4);
        String month_str = dt.substring(5, 7);
        String day_str = dt.substring(8, 10);

        int year_int = Integer.parseInt(year_str);
        int month_int = Integer.parseInt(month_str) - 1;
        int day_int = Integer.parseInt(day_str);

        DatePickerDialog dateDlg = new DatePickerDialog(classThis, new DatePickerDialog.OnDateSetListener() {
            boolean mFire = false;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (!mFire) {
                    mFire = true;
                    monthOfYear = monthOfYear + 1;
                    String month = monthOfYear > 9 ? ("" + monthOfYear) : ("0" + monthOfYear);
                    String day = dayOfMonth > 9 ? ("" + dayOfMonth) : ("0" + dayOfMonth);
                    String dtStr = year + "-" + month + "-" + day;
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("javascript:getData('" + dtStr + "')");
                        }
                    });
                }
            }
        }, year_int, month_int, day_int);

        dateDlg.show();
    }

    /**
     * 查找view
     */
    public void findViews() {
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        // 界面相关参数。开始===============================
        webViewLoadingContainer = (LinearLayout) findViewById(R.id.webViewLoadingContainer);
        webLayout = (LinearLayout) findViewById(R.id.webLayout);
        webView = (WebView) findViewById(R.id.webView);
        loadingMsg = (TextView) findViewById(R.id.loadingMsg);
        // 界面相关参数。结束===============================
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
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            if (!CommonUtil.checkNB(pageUrl)) {
                pageUrl = "http://" + baseApp.serverAddr + "/" + CommonParam.URL_PRIVACY;
            }
            webView.loadUrl(pageUrl);
            Log.d("###", pageUrl);
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

            // if (info != null) {
            // // 信息有效
            // // 设置字段及按钮
            // publishProgress(PROGRESS_SET_FIELD);
            //
            // }
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
                // 返回
                goBack();
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            makeWaitDialog(R.string.alert_info_loading);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            unWait();
            if (loadSuccessFlag) {
                // 页面加载成功
                webViewLoadingContainer.setVisibility(View.GONE);
                webViewLoadingContainer.setClickable(false);
                // webViewContainer.setVisibility(View.VISIBLE);
                loadingMsg.setText(R.string.alert_info_loading);
            } else {
                // 页面加载失败
                webViewLoadingContainer.setVisibility(View.VISIBLE);
                webViewLoadingContainer.setClickable(true);
                // webViewContainer.setVisibility(View.GONE);
                loadingMsg.setText(R.string.alert_info_click_to_load);
                show(R.string.alert_info_load_error);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            unWait();
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadSuccessFlag = false;
        }
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            // Log.d("@JavascriptInterface", "showToast()");
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void alert(String toast) {
            // Log.d("@JavascriptInterface", "alert()");
            makeAlertDialog(toast);
        }

        @JavascriptInterface
        public void about() {
            // Log.d("@JavascriptInterface", "about()");
            makeAboutDialog();
        }

        @JavascriptInterface
        public void goBack() {
            // Log.d("@JavascriptInterface", "goBack()");
            classThis.goBack();
        }

        @JavascriptInterface
        public void showDateDlg(String dt) {
            // Log.d("@JavascriptInterface", "makeDateDlg()");
            makeDateDlg(dt);
        }

        @JavascriptInterface
        public void startWait() {
            // Log.d("@JavascriptInterface", "makeWaitDialog()");
            pageHandler.sendEmptyMessage(10);
        }

        @JavascriptInterface
        public void endWait() {
            // Log.d("@JavascriptInterface", "unWait()");
            pageHandler.sendEmptyMessage(11);
        }
    }

    public class MyDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    }
}
