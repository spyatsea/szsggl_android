/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.application.BaseApplication;
import com.cox.android.szsggl.model.VerInfo;
import com.cox.android.szsggl.tool.DbTool;
import com.cox.android.szsggl.tool.InfoTool;
import com.cox.android.szsggl.tool.InsTool;
import com.cox.android.szsggl.tool.UserTool;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.DigestUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.PNUtil;
import com.sinpo.xnfc.Util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 与数据库相关的父类，其他类可以继承该类
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class DbActivity extends AppCompatActivity {
    /**
     * 数据库
     */
    SQLiteDatabase db = null;

    /**
     * 屏幕宽度
     */
    int screenWidth;
    /**
     * 屏幕高度
     */
    int screenHeight;

    /**
     * 是否个性化AlertDialog
     */
    boolean customizeAlertDlgFlag = false;
    // 设置参数。开始==============================================
    SharedPreferences preferences = null;
    SharedPreferences.Editor preferEditor = null;
    /**
     * 是否要退出程序
     */
    boolean exitAppFlag = false;
    /**
     * 读卡标志，表明是读卡还是不做处理
     */
    int readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
    /**
     * 读UHF卡标志，表明是读卡还是不做处理
     */
    int readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_NO_ACTION;
    /**
     * 存放变量的Application
     */
    BaseApplication baseApp = null;
    /**
     * 是否正在搜索
     */
    boolean isSearching = false;
    /**
     * 之前的界面标志。 <p> 例如“INS”，表示要访问的是巡视界面，但没有登录，登录后就要转到巡视界面。
     */
    String fromFlag = "";
    /**
     * 之前的界面标志类型。 <p> 例如“INS”，用来补充界面标志信息。
     */
    String fromFlagType = "";
    /**
     * 当前的界面标志。
     */
    String nowFlag = "";
    /**
     * 用户角色
     */
    String userRoles;
    // 设置参数。结束==============================================

    // 标题栏组件。开始========================================================
    // ▲△▲△▲△▲△
    ActionBar actionBar;
    /**
     * 标题栏：标题
     */
    TextView titleText = null;
    // 标题栏组件。结束========================================================

    // 更新程序相关参数。开始==========================================
    public static final int WAIT_SECONDS = 10;
    /**
     * 信息代码：检查主程序
     */
    static final int MESSAGE_CHECKUPDATE_APP = 0x010;
    /**
     * 信息代码：升级主程序
     */
    static final int MESSAGE_UPDATE_APP = 0x011;
    /**
     * 是否正在升级程序
     */
    boolean isUpdating = false;
    /**
     * 更新程序的提示Dialog
     */
    AlertDialog updateDlg = null;
    /**
     * 更新程序的ProgressDialog
     */
    private ProgressDialog updateProgressDlg = null;
    /**
     * 查检更新的Call对象
     * */
    private Call upCall;
    /**
     * 查检更新 AsyncTask 对象
     */
    private AsyncTask<String, Integer, String> testUpdateTask;
    // NFC相关参数。开始========================================================
    NfcAdapter mAdapter;
    // 更新程序相关参数。结束==========================================
    PendingIntent mPendingIntent;
    IntentFilter[] mFilters;
    String[][] mTechLists;
    /**
     * 数据库类
     */
    DbTool dbTool;
    // NFC相关参数。结束========================================================
    /**
     * 人员工具类
     */
    UserTool userTool;
    /**
     * 巡视工具类
     */
    InsTool insTool;
    /**
     * 信息工具类
     */
    InfoTool infoTool;
    /**
     * 设置服务器地址的对话框
     */
    AlertDialog addrDlg = null;
    /**
     * 设置定位方式的对话框
     */
    AlertDialog locDlg = null;
    /**
     * 用户信息对话框
     */
    AlertDialog userInfoDlg = null;
    /**
     * 帮助Dialog
     */
    public AlertDialog helpDlg;
    // 上传下载相关参数。开始==========================================
    ProgressDialog upProDlg = null;
    int upProNum = 0;
    /**
     * 是否正在上传数据
     */
    boolean isUploading = false;
    /**
     * 是否正在下载数据
     */
    boolean isDownloading = false;
    OkHttpClient baseHttpClient = null;
    // 地理信息相关参数。开始==========================================
    LocationManager lm = null;

    // 上传下载相关参数。结束==========================================
    // 当前纬度
    Double latitude = 0.0D;
    // 当前经度
    Double longitude = 0.0D;
    // 当前GPS时间
    Long gpsTime = 0L;
    // 当前纬度（百度）
    Double latitude_baidu = 0.0D;
    // 当前经度（百度）
    Double longitude_baidu = 0.0D;
    // 当前高度
    Double altitude = 0.0D;
    String locationProvider = "";
    // 百度定位SDK的核心类
    LocationClient locationClient_baidu = null;
    public BDLocationListener locListener_baidu = new MyLocationListener();
    public static final int THREE_SECONDS = 10000;
    public static final int TEN_METERS = 10;
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    /**
     * 是否支持 GPS 定位
     */
    boolean locProviderFlag_GPS = false;
    /**
     * 是否支持网络定位
     */
    boolean locProviderFlag_Net = false;
    /**
     * 定位方式：使用 GPS 定位
     */
    boolean mUseGPS = false;
    /**
     * 定位方式：使用网络定位
     */
    boolean mUseNet = false;
    /**
     * 定位方式：GPS 和网络
     */
    boolean mUseBoth = false;
    /**
     * 定位方式：GPS 或 网络
     */
    boolean mUseOne = false;
    /**
     * 是否需要提示用户打开打开位置服务
     */
    boolean needShowLocAlertFlag = true;
    /**
     * 是否首次定位
     */
    boolean isFirstLoc = true;
    public LocationListener locListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        @SuppressLint("NewApi")
        public void onProviderEnabled(String provider) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(baseApp, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(baseApp, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                baseApp.checkPermissions(DbActivity.this);
            } else {
                updateLocation(lm.getLastKnownLocation(provider));
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
    };

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                latitude_baidu = location.getLatitude();
                longitude_baidu = location.getLongitude();
                altitude = location.getAltitude();

                locationMethod(location);
            }
        }
    }

    Criteria locCri = new Criteria();

    static {
        // locCri.setAccuracy(Criteria.ACCURACY_FINE);
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                // show("百度地图 key 验证出错!");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                // show("网络出错");
            }
        }
    }

    private SDKReceiver mReceiver;
    // 蓝牙相关参数。开始==========================================
    // Name for the SDP record when creating server socket
    public static final String NAME_SECURE = "BluetoothChatSecure";
    // 地理信息相关参数。结束==========================================
    public static final String NAME_INSECURE = "BluetoothChatInsecure";
    // Unique UUID for this application
    public static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device
    /**
     * 基础数据同步的ProgressDialog
     */
    ProgressDialog baseInfoSyncDlg = null;
    // 蓝牙相关参数。结束==========================================

    // 巡视相关参数。开始==========================================
    /**
     * 等待的ProgressDialog
     */
    ProgressDialog waitDlg = null;
    // 创建一个负责更新的进度的Handler
    final Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 表明消息是由该程序发送的。
            switch (msg.what) {
                case MESSAGE_CHECKUPDATE_APP:
                    // 检查主程序
                    // 显示等待窗口
                    makeWaitDialog(R.string.alert_check_update);
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 正在加载提示框
     */
    PopupWindow loadingWindow = null;
    /**
     * 背景音乐播放器
     */
    MediaPlayer mediaPlayer;
    // 巡视相关参数。结束==========================================

    // 覆盖层相关参数。开始============================================
    /**
     * 覆盖层根layout
     */
    FrameLayout parentFrameLayout;
    // 覆盖层相关参数。结束============================================

    private final Handler basePageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.d("##msg", msg.what + ":" + msg.obj);
            switch (msg.what) {
                case 10:
                    show((String) msg.obj);
                    break;
                case 11:
                    makeWaitDialog((String) msg.obj);
                    break;
                case 12:
                    unWait();
                    break;
                default:
                    break;
            }
        }
    };

    public BaseApplication getBaseApp() {
        if (baseApp == null) {
            baseApp = (BaseApplication) getApplication();
        }
        return baseApp;
    }

    public void setBaseApp(BaseApplication baseApp) {
        this.baseApp = baseApp;
    }

    public SQLiteDatabase getDb() {
        return baseApp.getDb();
    }

    public void setDb(SQLiteDatabase db) {
        baseApp.setDb(db);
        this.db = db;
    }

    public DbTool getDbTool() {
        dbTool = baseApp.getDbTool();
        return dbTool;
    }

    public void setDbTool(DbTool dbTool) {
        baseApp.setDbTool(dbTool);
        this.dbTool = dbTool;
    }

    public UserTool getUserTool() {
        if (userTool == null) {
            userTool = new UserTool(this, dbTool);
        }
        return userTool;
    }

    public void setUserTool(UserTool userTool) {
        this.userTool = userTool;
    }

    public InfoTool getInfoTool() {
        db = getDb();
        // dbTool.setDb(db);
        infoTool = baseApp.getInfoTool();
        // if (infoTool == null) {
        // infoTool = new InfoTool(this, dbTool);
        // }
        return infoTool;
    }

    public void setInfoTool(InfoTool infoTool) {
        this.infoTool = infoTool;
    }

    public InsTool getInsTool() {
        if (insTool == null) {
            insTool = new InsTool(this, dbTool);
        }
        return insTool;
    }

    public void setInsTool(InsTool insTool) {
        this.insTool = insTool;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName() + ":log", "onCreate()");
        super.onCreate(savedInstanceState);

        // 使设备屏幕长亮
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();

        // NFC。开始========================================================
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        // Create a generic PendingIntent that will be deliver to this activity.
        // The NFC stack
        // will fill in the intent with the details of the discovered tag before
        // delivering to
        // this activity.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        } else {
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        }
        // Setup an intent filter for all MIME based dispatches
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        // try {
        // ndef.addDataType("*/*");
        // } catch (MalformedMimeTypeException e) {
        // throw new RuntimeException("fail", e);
        // }
        mFilters = new IntentFilter[]{ndef, tech, tag};

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][]{new String[]{NfcF.class.getName()}};
        // NFC。结束========================================================

        baseApp = getBaseApp();

        // 初始化设置参数
        initPreferences();

        dbTool = getDbTool();
//		// db = baseApp.getDb();
//		// if (db == null || !db.isOpen()) {
//		// // 重新获得数据库连接
//		// db = dbTool.regetDb();
//		// baseApp.setDb(db);
//		// }
//		// dbTool.setDb(baseApp.getDb());
//
        // 初始化信息参数
        initInfoConfig();
        if (baseApp.loginUser != null) {
            userRoles = CommonUtil.N2B((String) baseApp.loginUser.get("roles"));
        }

        // 处理外部传入的数据。开始==================================================
        Intent postIntent = getIntent();
        Bundle postData = postIntent.getExtras();
        if (postData != null) {

        }
        // 处理外部传入的数据。结束==================================================

        // 地理信息。开始===========================================================
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        if (lm == null) {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // 检查是否提供了 GPS 定位功能
            if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {
                locProviderFlag_GPS = true;
            }
            // 检查是否提供了网络定位功能
            if (lm.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                locProviderFlag_Net = true;
            }
        }
        if (locationClient_baidu == null) {
            // 声明LocationClient类
            locationClient_baidu = new LocationClient(getApplicationContext());
            // 注册监听函数
            locationClient_baidu.registerLocationListener(locListener_baidu);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 定位模式
            option.setOpenGps(true); // 设置是否使用gps，默认false
            option.setLocationNotify(true); // 设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
            option.setIgnoreKillProcess(true); // 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
            option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
            option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
            option.setScanSpan(1000);// 设置发起定位请求的间隔，int类型，单位ms
            option.setWifiCacheTimeOut(5 * 60 * 1000); // 设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
            locationClient_baidu.setLocOption(option);
        }
        locationClient_baidu.start();
        // 地理信息。结束===========================================================
    }

    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(this.getClass().getName() + ":log", "onCreateOptionsMenu()");
        return false;
    }

    /**
     * 在菜单显示之前对菜单进行操作
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(this.getClass().getName() + ":log", "onPrepareOptionsMenu()");
        return false;
    }

    /**
     * 打开菜单时对菜单项进行处理
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.d(this.getClass().getName() + ":log", "onMenuOpened()");

        return true;
    }

    /**
     * 菜单被点击后的回调方法
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(this.getClass().getName() + ":log", "onOptionsItemSelected()");
        switch (item.getItemId()) {
            // case R.id.config:
            // show(R.string.config);
            // break;
            case R.id.menu_resetDB:
                // 显示重置数据提示对话框
                makeResetDbDialog();

                break;
            case R.id.menu_setLocService:
                // 位置服务设置对话框
                makeSetLocServiceDialog();

                break;
            case R.id.update:
                // 检查更新
                if (!isUpdating) {
                    testUpdateApp("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUPDATE + "?token="
                            + CommonParam.APP_KEY + "&type=1", "1");
                }
                break;
            //case R.id.about:
            // 显示关于对话框
            //	makeAboutDialog();
            //	break;
            case R.id.logout:
                // 提示用户是否注销
                checkLogoutDialog();
                break;
            case R.id.exit:
                // 显示退出程序提示对话框
                makeExitDialog();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 更改数据库连接
     */
    public void changeDb() {
        if (db != null && db.isOpen()) {
            // 关闭数据库连接
            dbTool.closeDb();
        }
        // 重新获得数据库连接
        db = dbTool.regetDb();
        baseApp.setDb(db);
    }

    /**
     * 重置列表
     */
    public void resetListData() {
    }

    /**
     * 重置相关区域
     */
    public void resetFieldData() {

    }

    /**
     * 显示退出程序提示对话框
     */
    public void makeExitDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.alert_askquit);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // show(R.string.alert_quited);
                // 退出程序
                finishApp();
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // 改变一些样式。开始======================================================
        if (customizeAlertDlgFlag) {
            int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId != 0) {
                TextView alertTitle = (TextView) dlg.findViewById(titleId);
                if (alertTitle != null) {
                    alertTitle.setTextColor(getResources().getColor(R.color.text_color_orange));
                }
            }

            int titleDividerId = dlgBuilder.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            if (titleDividerId != 0) {
                View titleDivider = (View) dlg.findViewById(titleDividerId);
                if (titleDivider != null) {
                    titleDivider.setBackgroundColor(getResources().getColor(R.color.text_color_orange));
                }
            }
        }
        // 改变一些样式。结束======================================================
    }

    /**
     * 显示重置数据提示对话框
     */
    public void makeResetDbDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.resetDBAsk);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 重置数据
                new ResetDBTask().execute();
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();
    }

    /**
     * 显示位置服务设置对话框
     */
    public void makeSetLocServiceDialog() {
        if (setupLocationFunc()) {
            Builder dlgBuilder = new Builder(this);

            // Get the layout inflater
            final LinearLayout setServlerlayout = (LinearLayout) getLayoutInflater().inflate(
                    R.layout.dlg_set_loc_service, null);
            dlgBuilder.setView(setServlerlayout);
            dlgBuilder.setTitle(R.string.setLocService);
            dlgBuilder.setIcon(R.drawable.ic_dialog_gps_v);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                // 如果API<11
                TextView t1 = (TextView) setServlerlayout.findViewById(R.id.t1);
                TextView t2 = (TextView) setServlerlayout.findViewById(R.id.t2);
                TextView t3 = (TextView) setServlerlayout.findViewById(R.id.t3);
                t1.setTextColor(getResources().getColor(R.color.solid_plain));
                t2.setTextColor(getResources().getColor(R.color.solid_plain));
                t3.setTextColor(getResources().getColor(R.color.solid_plain));
            }
            final TextView loc_type = (TextView) setServlerlayout.findViewById(R.id.loc_type);
            final TextView loc_lat = (TextView) setServlerlayout.findViewById(R.id.loc_lat);
            final TextView loc_lon = (TextView) setServlerlayout.findViewById(R.id.loc_lon);
            Button locMapBtn = (Button) setServlerlayout.findViewById(R.id.locMapBtn);

            loc_type.setText(CommonParam.LOC_TYPE_MAP().get(locationProvider));
            loc_lat.setText(latitude_baidu.toString());
            loc_lon.setText(longitude_baidu.toString());

            locMapBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 创建启动 ShowImageActivity 的Intent
                    Intent intent = new Intent(DbActivity.this, ShowMapActivity.class);
                    Bundle bundle = new Bundle();
                    Bundle data = new Bundle();
                    data.putDouble("lat", latitude);
                    data.putDouble("lon", longitude);
                    data.putDouble("lat_baidu", latitude_baidu);
                    data.putDouble("lon_baidu", longitude_baidu);
                    bundle.putBundle("infoBundle", data);
                    // 将数据存入 Intent 中
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            dlgBuilder.setNegativeButton(R.string.btn_loc_refresh, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            locDlg = dlgBuilder.create();
            locDlg.show();

            // 刷新状态
            Button locRefreshBtn = locDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
            // 确定
            Button confirmBtn = locDlg.getButton(DialogInterface.BUTTON_POSITIVE);
            // 确定
            locRefreshBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    loc_type.setText(CommonParam.LOC_TYPE_MAP().get(locationProvider));
                    loc_lat.setText(latitude_baidu.toString());
                    loc_lon.setText(longitude_baidu.toString());
                }
            });
            confirmBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    locDlg.cancel();
                }
            });
        }
    }

    /**
     * 显示关于对话框
     */
    public void makeAboutDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.about);
        dlgBuilder.setIcon(R.drawable.menu_info);
        dlgBuilder.setMessage(getString(R.string.app_name_display_format) + "\nV"
                + getString(R.string.app_versionName) + "\n" + getString(R.string.dpi_type));
        // + "  Build"
        // + getString(R.string.app_versionBuild);
        // + getString(R.string.dpi_type)
        // + "\n\n"
        // + getString(R.string.default_account_n_password,
        // getString(R.string.default_account),
        // getString(R.string.default_password))
        // + "\n"
        // + String.format(getString(R.string.devidinfo),
        // CommonUtil.N2B(((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId()))
        // + "\n\n"
        // + "\n" + "下载地址：" + "\n　http://"
        // + preferences.getString("SERVER_ADDR", getString(R.string.url_upload_default_out)) + "（外网）"
        // + "\n　http://" + baseApp.serverAddr + "（内网）" + "\n\n"
        // + "\n" + "下载地址：" + "\n　http://"
        // + preferences.getString("SERVER_ADDR", getString(R.string.url_upload_default_in)) + "\n\n"
        // + getString(R.string.copyright));
        dlgBuilder.setPositiveButton(R.string.confirm, null);
        dlgBuilder.create().show();
    }

    @Override
    protected void onDestroy() {
        Log.d(this.getClass().getName() + ":log", "onDestroy()");
        unWait();

        baseApp.closeDb();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (locationClient_baidu != null) {
            locationClient_baidu.stop();
        }
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
        super.onDestroy();
        if (exitAppFlag) {
            System.exit(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName() + ":log", "onPostCreate()");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getName() + ":log", "onResume()");
        super.onResume();
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
        setupLocationFunc(false);

        // MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        Log.d(this.getClass().getName() + ":log", "onPause()");
        baseApp.closeDb();
        if (upCall != null) {
            upCall.cancel();
            upCall = null;
        }
        if (testUpdateTask != null) {
            testUpdateTask.cancel(true);
            testUpdateTask = null;
        }
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
        // MobclickAgent.onPause(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(this.getClass().getName() + ":log", "onNewIntent(Intent intent)");
        Log.d(this.getClass().getName() + ":" + "Foreground dispatch", "Discovered tag with intent: " + intent);
        this.resolveIntent(intent);
    }

    void resolveIntent(Intent intent) {
        Log.d(this.getClass().getName() + ":log", "resolveIntent(Intent intent)");
        // Parse the intent
        String action = intent.getAction();
        Log.d(this.getClass().getName() + ":log", "action=" + action);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.d(this.getClass().getName() + ":log", getString(R.string.title_scanned_tag));
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techArray = tag.getTechList();
            StringBuilder sb = new StringBuilder();

            Log.d(this.getClass().getName() + ":log", "id:" + tag.getId());
            Log.d(this.getClass().getName() + ":log",
                    "id(解码):" + Util.toHexString(tag.getId(), 0, tag.getId().length));
            String cardId = Util.toHexString(tag.getId(), 0, tag.getId().length);

            if (!CommonUtil.checkNB(cardId)) {
                // 如果卡id为空
                show(R.string.alert_card_mac_blank);
                return;
            }
            if (readCardType == CommonParam.READ_CARD_TYPE_PRE_READ) {
                // 预读卡片
                readCard_pre(cardId);
            } else if (readCardType == CommonParam.READ_CARD_TYPE_EXTRA_CARD) {
                // 读取额外的卡片
                readExtraCard(cardId);
            } else if (readCardType == CommonParam.READ_CARD_TYPE_INSPECT) {
                // 巡视卡
                readCard_ins_onBefore(cardId);
            } else {
                // 其他方式
            }

            for (int i = 0, len = techArray.length; i < len; i++) {
                sb.append("tech" + i + ":" + techArray[i]).append("\n");
                Log.d(this.getClass().getName() + ":log", "tech" + i + ":" + techArray[i]);
            }
            Log.d(this.getClass().getName() + ":log", "toString:" + tag.toString());

            readTag(tag);
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d(this.getClass().getName() + ":log", getString(R.string.title_scanned_tech));
        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d(this.getClass().getName() + ":log", getString(R.string.title_scanned_ndef));
            // When a tag is discovered we send it to the service to be save. We
            // include a PendingIntent for the service to call back onto. This
            // will cause this activity to be restarted with onNewIntent(). At
            // that time we read it from the database and view it.
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }
            // Setup the views
        } else {
            finish();
            return;
        }
    }

    /**
     * 读卡的内容
     *
     * @param tag {@code Tag} Tag对象
     */
    public void readTag(Tag tag) {
        Log.d(this.getClass().getName() + ":log", "readTag(Tag tag)");
        if (tag != null) {
            String tagStr = tag.toString();
            if (tagStr != null) {
                if (tagStr.contains("MifareUltralight")) {
                    Log.d(this.getClass().getName() + ":log", "MifareUltralight");
                } else if (tagStr.contains("MifareClassic")) {
                    Log.d(this.getClass().getName() + ":log", "MifareClassic");
                }
            }
        }
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定 Activity 返回的结果。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(this.getClass().getName() + ":log", "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CommonParam.REQUESTCODE_OPENCARD && resultCode == CommonParam.RESULTCODE_OPENCARD) {
            // 如果是由 CardDialog 返回的结果
            // 取出 Intent 里的 Extras 数据
            Bundle data = intent.getExtras();
            // 卡片 ID
            String cardId = data.getString("cardId");
            if (CommonUtil.checkNB(cardId)) {
                // 显示
                Log.d(this.getClass().getName() + ":log", "传来指令，打开 ID:" + cardId + " 的卡片！");
            } else {
                show("该卡无效！");
            }
        }
    }

    @Override
    protected void onRestart() {
        Log.d(this.getClass().getName() + ":log", "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d(this.getClass().getName() + ":log", "onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(this.getClass().getName() + ":log", "onStop()");
        super.onStop();
        if (lm != null) {
            lm.removeUpdates(locListener);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化设置参数
     */
    public void initPreferences() {
        preferences = baseApp.preferences;
        preferEditor = baseApp.preferEditor;
    }

    /**
     * 退出程序
     */
    public void finishApp() {
        baseApp.checkUpdateFlag = false;
        exitAppFlag = true;

        this.finish();
    }

    /**
     * 显示 Toast 提示
     *
     * @param text {@code String} 提示信息
     */
    public void show(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示 Toast 提示
     *
     * @param resId {@code int} 资源id
     */
    public void show(int resId) {
        show(getString(resId));
    }

    /**
     * 显示 Toast 提示
     *
     * @param text     {@code String} 提示信息
     * @param duration {@code int} 提示时间
     */
    public void show(String text, int duration) {
        Toast toast = Toast.makeText(this, text, duration);
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示 Toast 提示
     *
     * @param resId    {@code int} 资源id
     * @param duration {@code int} 提示时间
     */
    public void show(int resId, int duration) {
        show(getString(resId), duration);
    }

    /**
     * 修改 Note
     */
    public void editNote() {

    }

    /**
     * 保存 Note
     */
    public void saveNote() {

    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
    }

    /**
     * 检测并初始化位置服务
     *
     * @param {@code boolean} 是否需要发出提示信息
     * @return {@code boolean} 位置服务是否可用
     */
    @TargetApi(23)
    public boolean setupLocationFunc(boolean needAlert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(baseApp, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(baseApp, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // getPersimmions();
                return false;
            }
        }
        if (locProviderFlag_GPS && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mUseGPS = true;
        } else {
            mUseGPS = false;
        }
        if (locProviderFlag_Net && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mUseNet = true;
        } else {
            mUseNet = false;
        }

        if (mUseGPS && mUseNet) {
            mUseBoth = true;
            mUseOne = false;
        } else if (!mUseGPS && !mUseNet) {
            mUseBoth = false;
            mUseOne = false;
        } else {
            mUseBoth = false;
            mUseOne = true;
        }

        if (!locProviderFlag_GPS && !locProviderFlag_Net && !mUseGPS && !mUseNet && !mUseBoth && !mUseOne
                && needShowLocAlertFlag) {
            // 没有提供位置服务
            if (needAlert) {
                show(R.string.alert_loc_nolocservice);
            }
            needShowLocAlertFlag = true;
            return false;
        } else if (locProviderFlag_Net && !locProviderFlag_GPS && !mUseNet && needShowLocAlertFlag) {
            // 仅有网络服务，但没有打开
            if (needAlert) {
                show(R.string.alert_loc_need_net);
            }
            needShowLocAlertFlag = true;
            return false;
        } else if (locProviderFlag_GPS && !mUseGPS && needShowLocAlertFlag) {
            // 有GPS功能，但没有打开
            latitude = 0D;
            longitude = 0D;
            gpsTime = 0L;
            latitude_baidu = 0D;
            longitude_baidu = 0D;

            if (needAlert) {
                openGPSSettings();
            }

            return false;
        } else {
            // 提供了位置服务，并且至少有一个服务已经打开了
            lm.removeUpdates(locListener);
            if (mUseOne) {
                // 仅提供一种定位方式
                if (mUseGPS) {
                    // GPS定位
                    locationProvider = LocationManager.GPS_PROVIDER;
                    lm.requestLocationUpdates(locationProvider, THREE_SECONDS, TEN_METERS, locListener);
                } else if (mUseNet) {
                    // 网络定位
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                    lm.requestLocationUpdates(locationProvider, THREE_SECONDS, TEN_METERS, locListener);
                }
                updateLocation(lm.getLastKnownLocation(locationProvider));
            } else if (mUseBoth) {
                // 提供两种定位方式
                Location gpsLocation = null;
                Location networkLocation = null;
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, THREE_SECONDS, TEN_METERS, locListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, THREE_SECONDS, TEN_METERS, locListener);

                gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // If both providers return last known locations, compare the
                // two and use the better
                // one to update the UI. If only one provider returns a
                // location, use it.
                if (gpsLocation != null && networkLocation != null) {
                    Location bestLocation = getBetterLocation(gpsLocation, networkLocation);
                    locationProvider = bestLocation.getProvider();
                    updateLocation(bestLocation);
                } else if (gpsLocation != null) {
                    locationProvider = LocationManager.GPS_PROVIDER;
                    updateLocation(gpsLocation);
                } else if (networkLocation != null) {
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                    updateLocation(networkLocation);
                }
            }

            if (locationClient_baidu != null && locationClient_baidu.isStarted()) {
                locationClient_baidu.requestLocation();
            }
            needShowLocAlertFlag = false;
            return true;
        }
    }

    /**
     * 检测并初始化位置服务(发出提示信息)
     *
     * @return {@code boolean} 位置服务是否可用
     */
    public boolean setupLocationFunc() {
        return setupLocationFunc(true);
    }

    /**
     * Determines whether one Location reading is better than the current Location fix. Code taken from
     * http://developer.android.com/guide/topics/location /obtaining-user-location.html
     *
     * @param gpsLocation
     * @param networkLocation
     * @return The better Location object based on recency and accuracy.
     */
    protected Location getBetterLocation(Location gpsLocation, Location networkLocation) {
        if (networkLocation == null) {
            // A new location is always better than no location
            return gpsLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = gpsLocation.getTime() - networkLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return gpsLocation;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return networkLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (gpsLocation.getAccuracy() - networkLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(gpsLocation.getProvider(), networkLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return gpsLocation;
        } else if (isNewer && !isLessAccurate) {
            return gpsLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return gpsLocation;
        }
        return networkLocation;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * 更新位置信息
     *
     * @param location {@code Location}
     */
    private void updateLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            gpsTime = location.getTime();
        }
    }

    /**
     * 提示用户打开 GPS
     */
    public void openGPSSettings() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_needgps);
        dlgBuilder.setMessage(getString(R.string.alert_needgpsinfo));
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        dlgBuilder.setPositiveButton(R.string.info_setnow, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                needShowLocAlertFlag = true;
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
            }
        });
        dlgBuilder.setNeutralButton(R.string.info_notset, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                needShowLocAlertFlag = true;
                dialog.cancel();
            }
        });
        dlgBuilder.setNegativeButton(R.string.info_neveralert, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                needShowLocAlertFlag = false;
                dialog.cancel();
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // 改变一些样式。开始======================================================
        if (customizeAlertDlgFlag) {
            int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId != 0) {
                TextView alertTitle = (TextView) dlg.findViewById(titleId);
                if (alertTitle != null) {
                    alertTitle.setTextColor(getResources().getColor(R.color.text_color_orange));
                }
            }

            int titleDividerId = dlgBuilder.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            if (titleDividerId != 0) {
                View titleDivider = (View) dlg.findViewById(titleDividerId);
                if (titleDivider != null) {
                    titleDivider.setBackgroundColor(getResources().getColor(R.color.text_color_orange));
                }
            }
        }
        // 改变一些样式。结束======================================================
    }

    /**
     * 等待一段时间
     *
     * @param time {@code int} 等待时间（毫秒）
     */
    public void doWait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查网络状态
     *
     * @param needAlert {@code boolean} 是否需要发出提示信息
     * @return {@code boolean} 网络状态
     */
    public boolean checkNet(boolean needAlert) {
        boolean netFlag;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) {
            netFlag = true;
        } else {
            netFlag = false;
            if (needAlert) {
                Message msg = basePageHandler.obtainMessage(10, getString(R.string.alert_nonet));
                basePageHandler.sendMessage(msg);
            }
        }
        return netFlag;
    }

    /**
     * 检查网络状态(发出提示信息)
     *
     * @return {@code boolean} 网络状态
     */
    public boolean checkNet() {
        return checkNet(true);
    }

    /**
     * 取得某个页面的输入流
     *
     * @param urlString {@code String} 页面 URL
     * @return {@code Map<String, Object>} 页面的输入流
     */
    private Map<String, Object> downloadUrl(String urlString) {
        return downloadUrl(urlString, 10000, 15000);
    }

    /**
     * 取得某个页面的输入流
     *
     * @param urlString      {@code String} 页面 URL
     * @param readTimeout    {@code int} 读取超时（毫秒）
     * @param connectTimeout {@code int} 连接超时（毫秒）
     * @return {@code Map<String, Object>} 页面的输入流
     */
    public Map<String, Object> downloadUrl(String urlString, int readTimeout, int connectTimeout) {
        // Log.d("#urlString", "#" + urlString);
        InputStream is = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean successFlag = false;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectTimeout);
            // conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if (response == 200) {
                is = conn.getInputStream();
                resultMap.put("is", is);
                successFlag = true;
            }
        } catch (IOException e) {
            // Log.d("#e", "#" + JSONObject.toJSONString(e));
            e.printStackTrace();
            successFlag = false;
        }
        Log.d("#successFlag", successFlag + ":" + urlString);
        if (successFlag) {
            resultMap.put("result", CommonParam.RESULT_SUCCESS);
        } else {
            resultMap.put("result", CommonParam.RESULT_NET_ERROR);
        }
        return resultMap;
    }

    /**
     * 检查程序更新
     *
     * @param servAddr {@code String} 服务器地址
     */
    public void testUpdateApp(String servAddr) {
        testUpdateApp(servAddr, "1");
    }

    /**
     * 检查程序更新
     *
     * @param servAddr {@code String} 服务器地址
     * @param waitFlag {@code String} 是否显示等待提示
     */
    public void testUpdateApp(String servAddr, String waitFlag) {
        if (checkNet()) {
            if (testUpdateTask != null) {
                testUpdateTask.cancel(true);
                testUpdateTask = null;
            }
            testUpdateTask = new TestUpdate_ok().execute(servAddr, CommonParam.UPDATETYPE_APP, waitFlag);
        }
    }

    /**
     * 检查程序更新
     *
     * @param servAddr  {@code String} 服务器地址
     * @param waitFlag  {@code String} 是否显示等待提示
     * @param needAlert {@code boolean} 是否需要发出网络提示信息
     */
    public void testUpdateApp(String servAddr, String waitFlag, boolean needAlert) {
        if (checkNet(needAlert)) {
            if (testUpdateTask != null) {
                testUpdateTask.cancel(true);
                testUpdateTask = null;
            }
            testUpdateTask = new TestUpdate_ok().execute(servAddr, CommonParam.UPDATETYPE_APP, waitFlag);
        }
    }

    /**
     * 用来升级的 AsyncTask
     */
    private class TestUpdate_ok extends AsyncTask<String, Integer, String> {
        // 是否显示等待提示
        private String waitFlag = "1";
        /**
         * 升级信息
         */
        private VerInfo verInfo = null;

        @Override
        protected void onPreExecute() {
            isUpdating  = true;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = CommonParam.RESULT_ERROR;
            // 是否显示等待提示
            waitFlag = params[2];
            // 服务器返回的文本
            String respStr = "";
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。开始=================
            if (waitFlag.equals("1")) {
                // 发送消息到 Handler
                updateHandler.sendEmptyMessage(MESSAGE_CHECKUPDATE_APP);
            }

            try {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url(params[0])
                        .build();
                if (baseHttpClient == null) {
                    baseHttpClient = new OkHttpClient();
                }
                if (upHttpClient == null) {
                    upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                }

                upCall = upHttpClient.newCall(upHttpRequest);
                upResponse = upCall.execute();

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

                    JSONObject respJson = JSONObject.parseObject(respStr);
                    String resultStr = respJson.getString("result");
                    if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                        // 请求正确
                        verInfo = new VerInfo(respJson.getString("vercode"), respJson.getString("vername"),
                                respJson.getString("url"), respJson.getString("vercontent"));
                        int ver = Integer.parseInt(verInfo.getVercode());
                        if (baseApp.versionCodeInt < ver) {
                            // 需要升级
                            result = CommonParam.UPDATETYPE_APP;
                        } else {
                            // 不需要升级
                            result = CommonParam.RESULT_IS_LATESTVER;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // 隐藏等待窗口
            unWait();
            upCall = null;
            isUpdating = false;

            // 成功
            if (result.equals(CommonParam.UPDATETYPE_APP)) {
                // 升级程序
                makeUpdateDialog(verInfo);
            } else if (result.equals(CommonParam.RESULT_IS_LATESTVER)) {
                // 程序已经是最新版本
                if (waitFlag.equals("1")) {
                    show(R.string.alert_update_islatestver);
                }
            } else {
                // 失败
                if (waitFlag.equals("1")) {
                    show(R.string.alert_net_format_error);
                }
            }
        }
    }

    /**
     * 下载文件程序
     *
     * @param url       {@code String} 下载文件名
     * @param localPath {@code String} 保存在本地的文件名绝对地址
     * @param methodStr {@code String} 下载完毕后执行的方法
     */
    public Map<String, Object> downloadFile(String url, String localPath, String methodStr) {
        return downloadFile(url, localPath, methodStr, true);
    }

    /**
     * 下载文件程序
     *
     * @param url       {@code String} 下载文件名
     * @param localPath {@code String} 保存在本地的文件名绝对地址
     * @param methodStr {@code String} 下载完毕后执行的方法
     * @param needAlert {@code boolean} 是否需要发出网络提示信息
     */
    public Map<String, Object> downloadFile(String url, String localPath, String methodStr, boolean needAlert) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (checkNet(needAlert)) {
            Map<String, Object> rMap = downloadUrl(url);

            if (((String) rMap.get("result")).equals(CommonParam.RESULT_SUCCESS)) {
                InputStream is = (InputStream) rMap.get("is");
                try {
                    FileOutputStream fs = new FileOutputStream(localPath);
                    byte[] buffer = new byte[1444];
                    int byteRead = 0;
                    while ((byteRead = is.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteRead);
                    }
                    is.close();
                    fs.flush();
                    fs.close();
                    resultMap.put("fileName", localPath);
                    resultMap.put("methodStr", methodStr);
                    resultMap.put("result", CommonParam.RESULT_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    resultMap.put("result", CommonParam.RESULT_ERROR);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    rMap.clear();
                }
            } else {
                resultMap.put("result", (String) rMap.get("result"));
            }
        } else {
            resultMap.put("result", CommonParam.RESULT_ERROR);
        }
        return resultMap;
    }

    /**
     * 用来下载文件的 AsyncTask
     * <p>
     * 一般由其他方法调用，或单独使用
     *
     * @deprecated
     */
    @Deprecated
    private class DownloadFileTask extends AsyncTask<Object, Integer, String> {
        /**
         * 下载文件名
         */
        private String urlString;
        /**
         * 保存在本地的文件名绝对地址
         */
        private String localPath;
        /**
         * 下载完毕后执行的方法
         */
        private String methodStr;
        /**
         * 是否需要发出提示信息
         */
        private boolean needShowAlert;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            urlString = (String) params[0];
            localPath = (String) params[1];
            methodStr = (String) params[2];
            needShowAlert = (boolean) params[3];

            // 下载文件。开始===============================================
            if (checkNet(needShowAlert)) {
                if (needShowAlert) {
                    Message msg = basePageHandler.obtainMessage(11, getString(R.string.alert_data_download_file_message));
                    basePageHandler.sendMessage(msg);
                }

                // 读取超时（毫秒）
                int readTimeout = 3000;
                // 连接超时（毫秒）
                int connectTimeout = 3000;
                InputStream is = null;
                FileOutputStream fs = null;

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                    conn.setReadTimeout(readTimeout);
                    conn.setConnectTimeout(connectTimeout);
                    conn.setDoInput(true);
                    conn.connect();
                    int response = conn.getResponseCode();
                    if (response == 200) {
                        is = conn.getInputStream();
                        if (is != null) {
                            fs = new FileOutputStream(localPath);
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
                    try {
                        if (fs != null) {
                            fs.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("#successFlag", result + ":" + localPath);
            }
            // 下载文件。结束===============================================

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
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (needShowAlert) {
                    unWait();
                    show(R.string.alert_download_success);
                }
            } else {
                if (needShowAlert) {
                    unWait();
                    show(R.string.alert_download_fail);
                }
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder("");
        Reader reader = null;
        BufferedReader br = null;
        reader = new InputStreamReader(stream, "UTF-8");
        br = new BufferedReader(reader);
        String line = br.readLine();
        while (line != null) {
            sb.append(line + "\n");// 输出从文件中读取的数据
            line = br.readLine();// 从文件中继续读取一行数据
        }
        br.close();// 关闭BufferedReader对象
        reader.close();// 关闭Reader对象
        return sb.toString();
    }

    /**
     * 显示提示升级对话框
     *
     * @param verInfo {@code VerInfo} 升级信息
     */
    public void makeUpdateDialog(VerInfo verInfo) {
        String title = getString(R.string.findUpdate) + " v" + verInfo.getVername();
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(title);
        dlgBuilder.setIcon(R.drawable.menu_update);
        if (CommonUtil.checkNB(verInfo.getVercontent().replaceAll("★", ""))) {
            dlgBuilder.setMessage(getString(R.string.ins_column_desc) + "：" + "\n"
                    + verInfo.getVercontent().replaceAll("★", "\n"));
        } else {
            dlgBuilder.setMessage(R.string.whether_update);
        }
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton(R.string.btn_update_now, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNegativeButton(R.string.btn_update_later, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        updateDlg = dlgBuilder.create();
        updateDlg.show();

        // 下载按钮
        Button updateBtn = updateDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = updateDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        updateBtn.setTag(verInfo);
        updateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                VerInfo verInfo = (VerInfo) v.getTag();
                updateDlg.cancel();
                String url = verInfo.getUrl();
                Log.d("##", "下载" + url);

                makeAppDownloadDialog(url, CommonParam.UPDATETYPE_APP);
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                updateDlg.cancel();
            }
        });
    }

    /**
     * 显示升级进度对话框
     *
     * @param url       {@code String} 下载文件名
     * @param methodStr {@code String} 下载完毕后执行的方法
     */
    public void makeAppDownloadDialog(String url, String methodStr) {
        if (!checkNet()) {
            return;
        }

        if (updateProgressDlg == null) {
            updateProgressDlg = new ProgressDialog(this);
            updateProgressDlg.setTitle(R.string.update_prodlg_title);
            updateProgressDlg.setMax(100);
            updateProgressDlg.setCancelable(false);
            updateProgressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            updateProgressDlg.setProgress(0);
            updateProgressDlg.setIndeterminate(false);
            updateProgressDlg.setIcon(R.drawable.menu_download);

            updateProgressDlg.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            updateProgressDlg.cancel();
                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });
        } else {
            updateProgressDlg.setProgress(0);
        }
        updateProgressDlg.show();

        new AppDownloadTask().execute(url, methodStr);
    }

    /**
     * 下载更新App的 AsyncTask 类
     */
    private class AppDownloadTask extends AsyncTask<Object, Integer, String> {
        private String methodStr;
        private String saveFilePath;

        @Override
        protected void onPreExecute() {

        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 待下载的文件名
            String fileUrl = (String) params[0];
            methodStr = (String) params[1];
            // 要保存成的文件名
            String saveFileName = Calendar.getInstance().getTimeInMillis() + fileUrl.substring(fileUrl.lastIndexOf("."));
            saveFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/update/" + saveFileName;

            // 下载。开始======================================
            InputStream is = null;
            FileOutputStream fs = null;
            Map<String, Object> resultMap = new HashMap<String, Object>();
            boolean successFlag = false;

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);
                conn.connect();
                //获取内容长度
                int contentLength = conn.getContentLength();
                int response = conn.getResponseCode();
                if (response == 200) {
                    is = conn.getInputStream();
                    if (is != null) {
                        fs = new FileOutputStream(saveFilePath);
                        byte[] buffer = new byte[1444];
                        long totalReaded = 0;
                        int temp_Len;

                        while ((temp_Len = is.read(buffer)) != -1) {
                            totalReaded += temp_Len;
                            //Log.i("XXXX", "run: totalReaded:" + totalReaded);
                            long progress = totalReaded * 100 / contentLength;
                            //Log.i("XXXX", "run: progress:" + progress);
                            publishProgress((int) progress);

                            fs.write(buffer, 0, temp_Len);
                        }
                        resultMap.put("is", is);
                        successFlag = true;
                    }
                }
                fs.flush();
                result = CommonParam.RESULT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                successFlag = false;
            } finally {
                try {
                    if (fs != null) {
                        fs.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("#successFlag", successFlag + ":" + fileUrl);
            if (successFlag) {
                resultMap.put("result", CommonParam.RESULT_SUCCESS);
            } else {
                resultMap.put("result", CommonParam.RESULT_NET_ERROR);
            }
            // 下载。结束======================================

            // 关闭
            publishProgress(updateProgressDlg.getMax() + 1);
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
            if (progress[0] <= updateProgressDlg.getMax()) {
                updateProgressDlg.setProgress(progress[0]);
            } else if (progress[0] > (updateProgressDlg.getMax())) {
                updateProgressDlg.setProgress(updateProgressDlg.getMax());
                updateProgressDlg.dismiss();
            }
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                if (CommonParam.UPDATETYPE_APP.equals(methodStr)) {
                    if (CommonUtil.checkNB(saveFilePath)) {
                        installApk(saveFilePath);
                    }
                }
            } else {
                show("下载失败");

                updateProgressDlg.setProgress(0);
                updateProgressDlg.dismiss();
            }
        }
    }

    /**
     * 安装程序
     */
    public void installApk(String apkFileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installApk_new(apkFileName);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(apkFileName)), "application/vnd.android.package-archive");
            startActivity(intent);
        }
        // System.exit(0);
    }

    /**
     * 安装程序（新方法，支持Android P）
     */
    public void installApk_new(String apkFileName) {
        File file = new File(apkFileName);

        Uri apkUri = FileProvider.getUriForFile(this, CommonParam.FILE_PROVIDER_NAME, file);
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setData(apkUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    /**
     * 检测蓝牙功能
     */
    public void checkBluetooth() {
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter == null) {
            // 不支持蓝牙
            show("不支持蓝牙");
        } else {
            // 支持蓝牙
            show("支持蓝牙");
            if (bAdapter.isEnabled()) {
                // 蓝牙功能已开启
                show("蓝牙功能已开启");
            } else {
                // 蓝牙功能未开启
                show("蓝牙功能未开启");
                makeOpenBluetoothDialog();
            }
        }
    }

    /**
     * 显示打开蓝牙提示对话框
     */
    public void makeOpenBluetoothDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setMessage(R.string.whetherOpenBluetooth);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, CommonParam.REQUESTCODE_ENABLE_BT);
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.create().show();
    }

    /**
     * 提示用户是否注销
     */
    public void checkLogoutDialog() {
        checkLogoutDialog(true);
    }

    /**
     * 提示用户是否注销
     *
     * @param gotoLoginPageFlag {@code boolean} 是否需要返回登录界面
     */
    public void checkLogoutDialog(boolean gotoLoginPageFlag) {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.whetherLogout);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        if (gotoLoginPageFlag) {
            // 需要返回登录界面
            dlgBuilder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout(true);
                }
            });
        } else {
            // 不需要返回登录界面
            dlgBuilder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 注销用户信息
                    makeLogout(true, false);
                }
            });
        }
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();
    }

    /**
     * 注销用户
     *
     * @param gotoLoginPageFlag {@code boolean} 是否需要返回登录界面
     */
    public void logout(boolean gotoLoginPageFlag) {
        // 注销用户信息
        makeLogout(false, gotoLoginPageFlag);
    }

    /**
     * 注销用户信息
     *
     * @param needAlert         {@code boolean} 是否需要发出提示信息
     * @param gotoLoginPageFlag {@code boolean} 是否需要返回登录界面
     */
    public void makeLogout(boolean needAlert, boolean gotoLoginPageFlag) {
        makeLogout(needAlert, gotoLoginPageFlag, CommonParam.COMMANDCODE_NONE);
    }

    /**
     * 注销用户信息
     *
     * @param needAlert         {@code boolean} 是否需要发出提示信息
     * @param gotoLoginPageFlag {@code boolean} 是否需要返回登录界面
     * @param commandCode       {@code commandCode} 附加的指令码
     */
    public void makeLogout(boolean needAlert, boolean gotoLoginPageFlag, int commandCode) {
        // 参数Map
        HashMap<String, Object> restfullParamMap = new HashMap<String, Object>();
        restfullParamMap.put("method", "query_user_tags");
        // 信息传输Bundle
        Bundle data = new Bundle();
        // 请求指令码
        data.putInt("commandCode", commandCode);
        // 删除用户Tags
        new MakeLogoutTask().execute(restfullParamMap, needAlert, gotoLoginPageFlag, data);
    }

    /**
     * 注销用户信息
     *
     * @param needAlert         {@code boolean} 是否需要发出提示信息
     * @param gotoLoginPageFlag {@code boolean} 是否需要返回登录界面
     * @param commandCode       {@code commandCode} 附加的指令码
     */
    public void makeLogout_old(boolean needAlert, boolean gotoLoginPageFlag, int commandCode) {
        // 参数Map
        HashMap<String, Object> restfullParamMap = new HashMap<String, Object>();
        restfullParamMap.put("method", "query_user_tags");
        // 信息传输Bundle
        Bundle data = new Bundle();
        // 请求指令码
        data.putInt("commandCode", commandCode);
        // 删除用户Tags
        new RestfullDelTagsTask().execute(restfullParamMap, needAlert, gotoLoginPageFlag, data);
    }

    /**
     * 将信息写入数据库表
     *
     * @param dataset   {@code Map<String, Object>} 结果集
     * @param tableName {@code String} 表名
     * @return {@code boolean} 成功标志
     */
    public boolean insertToTable(Map<String, Object> dataset, String tableName) {
        return insertToTable(dataset, tableName, true);
    }

    /**
     * 将信息写入数据库表
     *
     * @param dataset         {@code Map<String, Object>} 结果集
     * @param tableName       {@code String} 表名
     * @param deleteFirstFlag {@code boolean} 写入之前是否清空表中的信息
     * @return {@code boolean} 成功标志
     */
    public boolean insertToTable(Map<String, Object> dataset, String tableName, boolean deleteFirstFlag) {
        // 成功标志
        boolean resultFlag = true;
        db = getDb();
        infoTool = getInfoTool();
        try {
            if (((Boolean) dataset.get("dataValidFlag"))) {
                // 数据内容List
                List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) dataset.get("data");
                if (deleteFirstFlag) {
                    // 删除旧数据
                    db.delete(tableName, null, null);
                }
                if (data.size() > 0) {
                    // 有新数据
                    for (HashMap<String, Object> record : data) {
                        // 插入新数据
                        if (infoTool.insert(tableName, processRec(tableName, record)) == -1) {
                            resultFlag = false;
                            break;
                        }
                    }
                }
            } else {
                resultFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultFlag = false;
        }
        return resultFlag;
    }

    /**
     * 播放音频文件
     *
     * @param filename {@code String} 音频文件名
     */
    public void playVoice(String filename) {
        if (mediaPlayer == null) {
            mediaPlayer = getMediaplayer(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/voice/" + filename);
        } else {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();

                File voiceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/voice/" + filename);
                if (voiceFile.exists()) {
                    mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                } else {
                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/voice/" + "novoice.wav");
                }
                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
    }

    /**
     * 播放随手拍的音频文件
     *
     * @param noteId   {@code String} note id
     * @param filename {@code String} 音频文件名
     */
    public void playRecVoice(String noteId, String filename) {
        if (mediaPlayer == null) {
            mediaPlayer = getMediaplayer(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/record/" + noteId + "/" + filename);
        } else {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();

                File voiceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/record/" + noteId + "/" + filename);
                if (voiceFile.exists()) {
                    mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                } else {
                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/voice/" + "novoice.wav");
                }
                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
    }

    /**
     * 播放音频文件
     *
     * @param filename {@code String} 音频文件名
     */
    public void playAudio(String filename) {
        if (mediaPlayer == null) {
            mediaPlayer = getMediaplayer(filename);
        } else {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();

                File voiceFile = new File(filename);
                if (voiceFile.exists()) {
                    mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                } else {
                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/voice/" + "novoice.wav");
                }
                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
    }

    /**
     * 获得当前播放器实例（如果没有就创建新的实例）
     *
     * @return {@code MediaPlayer} 播放器实例
     */
    public MediaPlayer getMediaplayer() {
        if (mediaPlayer == null) {
            mediaPlayer = getMediaplayer(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/voice/blank.wav");
        }
        return mediaPlayer;
    }

    /**
     * 获得新播放器实例（总是返回新实例）
     *
     * @param filePath {@code String} 多媒体文件
     * @return {@code MediaPlayer} 播放器实例
     */
    public MediaPlayer getMediaplayer(String filePath) {
        // 是否是新建的播放器实例
        boolean newFlag = false;
        // 指定的音频文件是否存在
        boolean voiceExistsFlag = false;
        if (mediaPlayer == null) {
            newFlag = true;
        } else {
            newFlag = false;
        }
        File voiceFile = new File(filePath);
        if (voiceFile.exists()) {
            voiceExistsFlag = true;
        } else {
            voiceExistsFlag = false;
        }

        if (newFlag) {
            // 新建
            if (voiceExistsFlag) {
                mediaPlayer = MediaPlayer.create(this, FileProvider.getUriForFile(this, CommonParam.FILE_PROVIDER_NAME, new File(filePath)));
            } else {
                mediaPlayer = MediaPlayer.create(
                        this,
                        FileProvider.getUriForFile(this, CommonParam.FILE_PROVIDER_NAME, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                                + CommonParam.PROJECT_NAME + "/voice/novoice.wav")));
            }
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            });
            mediaPlayer.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {

                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                }
            });
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // mp.start();
                }
            });
            mediaPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    return true;
                }
            });
            mediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                }
            });
        } else {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();

                // Log.d("file", voiceFile.getAbsolutePath());
                if (voiceExistsFlag) {
                    mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                } else {
                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/voice/" + "novoice.wav");
                }
                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaPlayer;
    }

    /**
     * 读卡：预读
     *
     * @param cardMac {@code String} RFID卡的MAC
     */
    public void readCard_pre(String cardMac) {

    }

    /**
     * 读卡：巡视_之前
     *
     * @param cardMac {@code String} 巡视点RFID卡的MAC
     */
    public void readCard_ins_onBefore(String cardMac) {
        readCard_ins(cardMac);
    }

    /**
     * 读卡：巡视
     *
     * @param cardMac {@code String} 巡视点RFID卡的MAC
     */
    public void readCard_ins(String cardMac) {
        readCard_ins(cardMac, null);
    }

    /**
     * 读卡：巡视
     *
     * @param cardMac {@code String} 巡视点RFID卡的MAC
     * @param infoId  {@code String} 巡视点编号
     */
    public void readCard_ins(String cardMac, String infoId) {

    }


    /**
     * 读卡：额外的卡片
     *
     * @param cardMac {@code String} RFID卡的MAC
     */
    public void readExtraCard(String cardMac) {

    }

    /**
     * 显示等待对话框
     */
    public void makeWaitDialog() {
        makeWaitDialog(getString(R.string.alert_waiting));
    }

    /**
     * 显示等待对话框
     *
     * @param resId {@code int} 资源id
     */
    public void makeWaitDialog(int resId) {
        makeWaitDialog(getString(resId));
    }

    /**
     * 显示等待对话框
     *
     * @param waitMsg {@code String} 等待提示语
     */
    @SuppressLint("ResourceType")
    public void makeWaitDialog(String waitMsg) {
        if (waitDlg == null) {
            waitDlg = new ProgressDialog(this);
            final LinearLayout waitLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.wait, null);
            waitDlg.setView(waitLayout);
            waitDlg.setMessage(waitMsg);
            waitDlg.setMax(10);
            waitDlg.setCancelable(false);
            waitDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            waitDlg.setProgress(0);
            waitDlg.setIndeterminate(true);
            waitDlg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.waiting_yh));
            // waitDlg.getWindow().setDimAmount(0);

            waitDlg.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            waitDlg.cancel();
                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });
        } else {
            waitDlg.setMessage(waitMsg);
        }
        waitDlg.show();
    }

    /**
     * 取消显示等待对话框
     */
    public void unWait() {
        if (waitDlg != null && waitDlg.isShowing()) {
            waitDlg.setProgress(waitDlg.getMax());
            waitDlg.dismiss();
        }
    }

    /**
     * 显示正在加载窗口
     */
    public void loading() {
        loading(null);
    }

    /**
     * 显示正在加载窗口
     *
     * @param waitMsg {@code String} 等待提示语
     */
    public void loading(String waitMsg) {
        View root = getLayoutInflater().inflate(R.layout.waiting, null);
        if (loadingWindow == null) {
            loadingWindow = new PopupWindow(root, CommonUtil.dip2px(this, 140f), CommonUtil.dip2px(this, 36f));
        }
        TextView waitingText = (TextView) root.findViewById(R.id.waiting);
        waitingText.setText(waitMsg);
        loadingWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    /**
     * 取消显示正在加载窗口
     */
    public void unLoading() {
        if (loadingWindow != null && loadingWindow.isShowing()) {
            loadingWindow.dismiss();
        }
    }

    /**
     * 设置title
     */
    @Override
    public void setTitle(CharSequence title) {
        if (titleText != null) {
            titleText.setText(title);
        } else {
            super.setTitle(title);
        }
    }

    /**
     * 设置title
     */
    @Override
    public void setTitle(int titleId) {
        if (titleText != null) {
            titleText.setText(getString(titleId));
        } else {
            super.setTitle(titleId);
        }
    }

    /**
     * 重置数据的 AsyncTask 类
     */
    public class ResetDBTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：拷贝相关文件
         */
        private static final int PROGRESS_COPY_FILE = 1001;
        /**
         * 进度常量：重置数据库
         */
        private static final int PROGRESS_CHANGE_DB = 1002;
        /**
         * 进度常量：初始化数据
         */
        private static final int PROGRESS_RESET_FIELD_DATA = 1003;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            if (db != null && db.isOpen()) {
                // 关闭数据库连接
                dbTool.closeDb();
            }
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;
            publishProgress(PROGRESS_COPY_FILE);
            // 删除并重新拷贝数据库
            FileUtil.copyDefaultDB(DbActivity.this, true);
            // 打开当前数据库
            publishProgress(PROGRESS_CHANGE_DB);
            // 重置相关区域
            publishProgress(PROGRESS_RESET_FIELD_DATA);

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
            if (progress[0] == PROGRESS_COPY_FILE) {
                // 显示等待窗口
                makeWaitDialog(R.string.alert_wait_copy_file);
            } else if (progress[0] == PROGRESS_CHANGE_DB) {
                makeWaitDialog(R.string.alert_wait_resetDB);
                // 打开当前数据库
                changeDb();
            } else if (progress[0] == PROGRESS_RESET_FIELD_DATA) {
                makeWaitDialog(R.string.alert_wait_reset_field_data);
                // 重置相关区域
                resetFieldData();
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
            // 显示完成提示
            show(R.string.resetDBOk);
        }

    }

    /**
     * 显示等待对话框
     */
    public void makeAlertDialog() {
        makeAlertDialog("");
    }

    /**
     * 显示提示对话框
     *
     * @param resId {@code int} 资源id
     */
    public void makeAlertDialog(int resId) {
        makeAlertDialog(getString(resId));
    }

    /**
     * 显示提示对话框
     *
     * @param msg {@code String} 等待提示语
     */
    public void makeAlertDialog(String msg) {
        makeAlertDialog(getString(R.string.alert_ts), msg);
    }

    /**
     * 显示提示对话框
     *
     * @param title {@code String} 标题
     * @param msg   {@code String} 等待提示语
     */
    public void makeAlertDialog(String title, String msg) {
        // 之前的读卡方式
        int readCardType_old = readCardType;
        // 读卡方式为不读卡
        readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(title);
        dlgBuilder.setMessage(msg);
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确定按钮
                Button confirmBtn = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                // 之前的读卡方式
                int readCardType_old = (Integer) confirmBtn.getTag();
                // 设置为之前的读卡方式
                readCardType = readCardType_old;
            }
        });
        dlgBuilder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // 确定按钮
                Button confirmBtn = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                // 之前的读卡方式
                int readCardType_old = (Integer) confirmBtn.getTag();
                // 设置为之前的读卡方式
                readCardType = readCardType_old;
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // 改变一些样式。开始======================================================
        if (customizeAlertDlgFlag) {
            int titleId = dlgBuilder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId != 0) {
                TextView alertTitle = (TextView) dlg.findViewById(titleId);
                if (alertTitle != null) {
                    alertTitle.setTextColor(getResources().getColor(R.color.text_color_orange));
                }
            }

            int titleDividerId = dlgBuilder.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            if (titleDividerId != 0) {
                View titleDivider = (View) dlg.findViewById(titleDividerId);
                if (titleDivider != null) {
                    titleDivider.setBackgroundColor(getResources().getColor(R.color.text_color_orange));
                }
            }
        }
        // 改变一些样式。结束======================================================

        // 确定按钮
        Button confirmBtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmBtn.setTag(readCardType_old);
    }

    /**
     * 打开指定图片
     *
     * @param filename {@code String} 图片名称
     * @param filepath {@code String} 图片路径
     */
    public void openPicByFilename(String filename, String filepath) {
        openPicByFilename(filename, filepath, null, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 打开指定图片
     *
     * @param filename   {@code String} 图片名称
     * @param filepath   {@code String} 图片路径
     * @param infoBundle {@code Bundle} 附加信息
     */
    public void openPicByFilename(String filename, String filepath, Bundle infoBundle) {
        openPicByFilename(filename, filepath, infoBundle, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 打开指定图片
     *
     * @param filename          {@code String} 图片名称
     * @param filepath          {@code String} 图片路径
     * @param screenOrientation {@code int} 屏幕方向
     */
    public void openPicByFilename(String filename, String filepath, int screenOrientation) {
        openPicByFilename(filename, filepath, null, screenOrientation);
    }

    /**
     * 打开指定图片
     *
     * @param filename          {@code String} 图片名称
     * @param filepath          {@code String} 图片路径
     * @param infoBundle        {@code Bundle} 附加信息
     * @param screenOrientation {@code int} 屏幕方向
     */
    public void openPicByFilename(String filename, String filepath, Bundle infoBundle, int screenOrientation) {
        if (CommonUtil.checkNB(filename) && CommonUtil.checkNB(filepath)) {
            File file = new File(filepath);
            if (file.exists()) {
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putString("title", filename);
                data.putString("filepath", filepath);
                if (infoBundle != null) {
                    data.putBundle("infoBundle", infoBundle);
                }
                // 创建启动 ShowImageActivity 的Intent
                Intent intent = null;
                if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    intent = new Intent(this, ShowImageLandActivity.class);
                } else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    intent = new Intent(this, ShowImageReverseLandActivity.class);
                } else {
                    intent = new Intent(this, ShowImageActivity.class);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            } else {
                show("找不到该图片！");
            }
        }
    }

    /**
     * 打开指定视频
     *
     * @param filename {@code String} 视频名称
     * @param filepath {@code String} 视频路径
     */
    public void openVideoByFilename(String filename, String filepath) {
        openVideoByFilename(filename, filepath, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 打开指定视频
     *
     * @param filename          {@code String} 视频名称
     * @param filepath          {@code String} 视频路径
     * @param screenOrientation {@code int} 屏幕方向
     */
    public void openVideoByFilename(String filename, String filepath, int screenOrientation) {
        if (CommonUtil.checkNB(filename) && CommonUtil.checkNB(filepath)) {
            File file = new File(filepath);
            if (file.exists()) {
                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putString("title", filename);
                data.putString("filepath", filepath);
                // 创建启动 ShowVideoActivity 的Intent
                Intent intent = null;
                if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    intent = new Intent(this, ShowVideoLandActivity.class);
                } else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    intent = new Intent(this, ShowVideoReverseLandActivity.class);
                } else {
                    intent = new Intent(this, ShowVideoActivity.class);
                }
                // 将数据存入 Intent 中
                intent.putExtras(data);
                this.startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            } else {
                show("找不到该视频！");
            }
        }
    }

    /**
     * 打开修改日期时间窗口
     */
    public void setDateTime() {
        startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
    }

    /**
     * 创建程序快捷方式
     */
    public void setUpShortCut() {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        // 设置快捷方式图片
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));

        // 设置快捷方式名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

        // 设置是否允许重复创建快捷方式 false表示不允许
        intent.putExtra("duplicate", true);

        // 创建快捷方式要打开的目标intent
        Intent targetIntent = new Intent(this, SplashActivity.class);

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, targetIntent);

        // 发送广播
        sendBroadcast(intent);
    }

    /**
     * 生成新列分隔符
     *
     * @param context {@code Context} 上下文
     * @return {@code View} 列分隔符View
     */
    public View makeColumnSpitter(Context context) {
        View splitter = new View(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        splitter.setLayoutParams(lp);
        splitter.setBackgroundColor(context.getResources().getColor(R.color.table_border_color));
        return splitter;
    }

    /**
     * 操作数据库的 AsyncTask 类
     */
    public class DbTask extends AsyncTask<Object, Integer, String> {

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;
            infoTool = getInfoTool();
            try {
                for (int index = 0, len = params.length; index < len; index++) {
                    // 操作数据库所需信息的Map
                    Map<String, Object> tableMap = (HashMap<String, Object>) params[index];
                    // 操作类型
                    String actionName = CommonUtil.N2B((String) tableMap.get("actionName"));
                    // 表名
                    String tableName = (String) tableMap.get("tableName");
                    // 关键列名
                    String keyColumn = (String) tableMap.get("keyColumn");
                    // 关键列值
                    String keyValue = (String) tableMap.get("keyValue");
                    // 键值对
                    ContentValues cv = (ContentValues) tableMap.get("cv");

                    if ("update".equals(actionName)) {
                        infoTool.update(tableName, keyColumn, keyValue, cv);
                    }
                }
                result = CommonParam.RESULT_SUCCESS;
            } catch (Exception e) {
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
        }

    }

    /**
     * 增加引导图片
     *
     * @param imageId {@code int} 引导图片id
     */
    public void addGuideImage(int imageId) {
        if (parentFrameLayout == null) {
            // 查找通过setContentView上的根布局
            View rootLayout = getWindow().getDecorView().findViewById(R.id.rootLayout);
            if (rootLayout == null)
                return;
            ViewParent viewParent = rootLayout.getParent();
            if (viewParent instanceof FrameLayout && getResources().getDrawable(imageId) != null) {
                parentFrameLayout = (FrameLayout) viewParent;
            }
        }
        if (parentFrameLayout != null) {
            LayoutInflater inflater = getLayoutInflater();
            final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.show_cover_image, null);
            ImageView picView = (ImageView) layout.findViewById(R.id.picView);
            picView.setImageResource(imageId);
            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    parentFrameLayout.removeView(layout);
                }
            });
            parentFrameLayout.addView(layout);
        }
    }

    /**
     * 增加小引导图片
     *
     * @param imageId {@code int} 引导图片id
     */
    public void addSmallGuideImage(int imageId) {
        if (parentFrameLayout == null) {
            // 查找通过setContentView上的根布局
            View rootLayout = getWindow().getDecorView().findViewById(R.id.rootLayout);
            if (rootLayout == null)
                return;
            ViewParent viewParent = rootLayout.getParent();
            if (viewParent instanceof FrameLayout && getResources().getDrawable(imageId) != null) {
                parentFrameLayout = (FrameLayout) viewParent;
            }
        }
        if (parentFrameLayout != null) {
            LayoutInflater inflater = getLayoutInflater();
            final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.show_cover_small_image, null);
            ImageView picView = (ImageView) layout.findViewById(R.id.picView);
            picView.setImageResource(imageId);
            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    parentFrameLayout.removeView(layout);
                }
            });
            parentFrameLayout.addView(layout);
        }
    }

    /**
     * 更新系统配置信息
     *
     * @param columnName  {@code String} 字段名
     * @param columnValue {@code String} 字段值
     */
    public void updateSysconfigValue(String columnName, String columnValue) {
        infoTool = getInfoTool();
        // 键值对
        ContentValues cv = new ContentValues();
        // 操作的表名
        String tableName = "sysconfig";
        cv.put(CommonParam.SYSCONFIG_COLUMN_VALUE, columnValue);
        if (infoTool.getCount("select count(model." + CommonParam.SYSCONFIG_COLUMN_NAME
                        + ") from \"sysconfig\" model where model." + CommonParam.SYSCONFIG_COLUMN_NAME + "=?",
                new String[]{columnName}) == 0) {
            // 不存在该值，要新增。
            cv.put(CommonParam.SYSCONFIG_COLUMN_NAME, columnName);
            infoTool.insert(tableName, cv);
        } else {
            // 已存在该值，要更新。
            infoTool.update(tableName, CommonParam.SYSCONFIG_COLUMN_NAME, columnName, cv);
        }

    }

    /**
     * 获得系统配置信息
     *
     * @param columnName {@code String} 字段名
     */
    public String getSysconfigValue(String columnName) {
        infoTool = getInfoTool();
        return infoTool.getSingleVal("select model." + CommonParam.SYSCONFIG_COLUMN_VALUE
                        + " from \"sysconfig\" model where model." + CommonParam.SYSCONFIG_COLUMN_NAME + "=?",
                new String[]{columnName});

    }

    /**
     * 强制显示overflow图标
     */
    @SuppressLint("SoonBlockedPrivateApi")
    public void forceShowOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制显示PopupMenu图标
     */
    public void forceShowPopupMenuIcon(PopupMenu popup) {
        forceShowPopupMenuIcon(popup, true);
    }

    /**
     * 强制显示PopupMenu图标
     *
     * @param flag {@code boolean} 强制显示标志
     */
    public void forceShowPopupMenuIcon(PopupMenu popup, boolean flag) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, flag);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销的 AsyncTask 类
     */
    public class MakeLogoutTask extends AsyncTask<Object, Integer, String> {
        /**
         * 是否需要发出提示信息
         */
        private boolean needAlert;
        /**
         * 是否需要返回登录界面
         */
        private boolean gotoLoginPageFlag;

        /**
         * 附加数据
         */
        private Bundle attatchData;

        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            needAlert = (Boolean) params[1];
            gotoLoginPageFlag = (Boolean) params[2];
            if (params.length >= 4) {
                // 附加数据
                attatchData = (Bundle) params[3];
            }

            try {
                baseApp.isLogged = false;
                // baseApp.delBdPushTags();
                result = CommonParam.RESULT_SUCCESS;
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
            baseApp.rememberFlag = false;
            preferEditor.putBoolean("rememberFlag", false);
            preferEditor.commit();

            baseApp.isLogged = false;

            baseApp.loginUser = null;
            preferEditor.putString("loginUser", "");
            preferEditor.commit();

            preferEditor.putString("PUSH_USER_ID", "");
            preferEditor.commit();
            baseApp.PUSH_USER_ID = "";

            if (needAlert) {
                show(R.string.alert_logout_success);
            }

            if (gotoLoginPageFlag) {
                Intent intent = new Intent(DbActivity.this, MeLoginActivity.class);

                if (attatchData != null) {
                    intent.putExtras(attatchData);
                }
                // 没有附加结果码
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    /**
     * 调用Restfull接口的 AsyncTask 类
     */
    public class RestfullDelTagsTask extends AsyncTask<Object, Integer, String> {
        /**
         * Restfull 服务地址
         */
        private static final String REST_URL = "http://channel.api.duapp.com/rest/2.0/channel/channel";

        private static final String HTTP_METHOD = "POST";
        /**
         * 是否需要发出提示信息
         */
        private boolean needAlert;
        /**
         * 是否需要返回登录界面
         */
        private boolean gotoLoginPageFlag;

        /**
         * 附加数据
         */
        private Bundle attatchData;

        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 参数Map
            HashMap<String, Object> restfullParamMap = (HashMap<String, Object>) params[0];
            needAlert = (Boolean) params[1];
            gotoLoginPageFlag = (Boolean) params[2];
            if (params.length >= 4) {
                // 附加数据
                attatchData = (Bundle) params[3];
            }

            // 方法名
            String method = (String) restfullParamMap.get("method");
            // 访问令牌
            String apiKey = PNUtil.getMetaValue(DbActivity.this, PNUtil.API_KEY_NAME);
            // 用户发起请求时的unix时间戳
            Long timestamp = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA)
                    .getTimeInMillis();

            if (!CommonUtil.checkNB(method)) {
                return result;
            }
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。结束=================
            try {
                String signStr = HTTP_METHOD + REST_URL;
                String url = REST_URL;

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                multipartBuilder.addFormDataPart("apikey", apiKey);
                signStr = signStr + "apikey=" + URLEncoder.encode(apiKey, "UTF-8");
                url = url + "?apikey=" + apiKey;

                multipartBuilder.addFormDataPart("method", method);
                signStr = signStr + "method=" + method;
                url = url + "&method=" + URLEncoder.encode(method, "UTF-8");

                multipartBuilder.addFormDataPart("timestamp", "" + timestamp);
                signStr = signStr + "timestamp=" + timestamp;
                url = url + "&timestamp=" + timestamp;

                multipartBuilder.addFormDataPart("user_id", baseApp.PUSH_USER_ID);
                signStr = signStr + "user_id=" + URLEncoder.encode(baseApp.PUSH_USER_ID, "UTF-8");
                url = url + "&user_id=" + baseApp.PUSH_USER_ID;

                signStr = signStr + CommonParam.BAIDU_SECRET_KEY;
                Log.d("#signStr", "[" + signStr + "]");
                String sign = DigestUtil.md5(URLEncoder.encode(signStr, "UTF-8"));
                multipartBuilder.addFormDataPart("sign", sign);
                url = url + "&sign=" + sign;
                Log.d("#url", "[" + url + "]");

                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url(REST_URL)
                        .post(requestBody)
                        .build();
                if (baseHttpClient == null) {
                    baseHttpClient = new OkHttpClient();
                }
                if (upHttpClient == null) {
                    upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                }

                upResponse = upHttpClient.newCall(upHttpRequest).execute();
                Log.d("#succ", "#" + upResponse.isSuccessful());
                if (upResponse.code() == 200) {
                    String respStr = upResponse.body().string();
                    Log.d("#respStr", "[" + respStr + "]");
                    if (CommonUtil.checkNB(respStr)) {
                        HashMap<String, Object> resultMap = JSONObject.parseObject(respStr, HashMap.class);
                        Log.d("#map", resultMap.get("response_params").toString());
                        // 指令是否执行成功
                        // HashMap<String, Object> response_paramsMap = (HashMap<String, Object>)
                        // resultMap.get("response_params");
                        LinkedHashMap<String, Object> response_paramsMap = (LinkedHashMap<String, Object>) resultMap
                                .get("response_params");
                        if (response_paramsMap != null) {
                            Integer tag_num = ((Double) response_paramsMap.get("tag_num")).intValue();
                            Log.d("#tag_num", "" + tag_num);

                            ArrayList<LinkedHashMap<String, Object>> tagsOs = (ArrayList<LinkedHashMap<String, Object>>) response_paramsMap
                                    .get("tags");
                            if (tagsOs != null) {
                                ArrayList<String> tidList = new ArrayList<String>();
                                for (LinkedHashMap<String, Object> tag : tagsOs) {
                                    String name = (String) tag.get("name");
                                    tidList.add(name);
                                }
                                // PushManager.delTags(DbActivity.this, tidList);
                            }
                        }
                    }

                    result = CommonParam.RESULT_SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
            baseApp.rememberFlag = false;
            preferEditor.putBoolean("rememberFlag", false);
            preferEditor.commit();

            baseApp.isLogged = false;

            baseApp.loginUser = null;
            preferEditor.putString("loginUser", "");
            preferEditor.commit();

            preferEditor.putString("PUSH_USER_ID", "");
            preferEditor.commit();
            baseApp.PUSH_USER_ID = "";

            if (needAlert) {
                show(R.string.alert_logout_success);
            }

            if (gotoLoginPageFlag) {
                Intent intent = new Intent(DbActivity.this, MeLoginActivity.class);

                if (attatchData != null) {
                    intent.putExtras(attatchData);
                }
                // 没有附加结果码
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    /**
     * 调用Restfull接口的 AsyncTask 类
     */
    public class RestfullListTask extends AsyncTask<Object, Integer, String> {
        /**
         * Restfull 服务地址
         */
        private static final String REST_URL = "http://channel.api.duapp.com/rest/2.0/channel/channel";

        private static final String HTTP_METHOD = "POST";

        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 参数Map
            HashMap<String, Object> restfullParamMap = (HashMap<String, Object>) params[0];

            // 方法名
            String method = (String) restfullParamMap.get("method");
            // 访问令牌
            String apiKey = PNUtil.getMetaValue(DbActivity.this, PNUtil.API_KEY_NAME);
            // 用户发起请求时的unix时间戳
            Long timestamp = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA)
                    .getTimeInMillis();

            if (!CommonUtil.checkNB(method)) {
                return result;
            }
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。结束=================
            try {
                String signStr = HTTP_METHOD + REST_URL;
                String url = REST_URL;

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                multipartBuilder.addFormDataPart("apikey", apiKey);
                signStr = signStr + "apikey=" + URLEncoder.encode(apiKey, "UTF-8");
                url = url + "?apikey=" + apiKey;

                multipartBuilder.addFormDataPart("method", method);
                signStr = signStr + "method=" + method;
                url = url + "&method=" + URLEncoder.encode(method, "UTF-8");

                multipartBuilder.addFormDataPart("timestamp", "" + timestamp);
                signStr = signStr + "timestamp=" + timestamp;
                url = url + "&timestamp=" + timestamp;

                multipartBuilder.addFormDataPart("user_id", baseApp.PUSH_USER_ID);
                signStr = signStr + "user_id=" + URLEncoder.encode(baseApp.PUSH_USER_ID, "UTF-8");
                url = url + "&user_id=" + baseApp.PUSH_USER_ID;

                signStr = signStr + CommonParam.BAIDU_SECRET_KEY;
                Log.d("#signStr", "[" + signStr + "]");
                String sign = DigestUtil.md5(URLEncoder.encode(signStr, "UTF-8"));
                multipartBuilder.addFormDataPart("sign", sign);
                url = url + "&sign=" + sign;
                Log.d("#url", "[" + url + "]");

                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url(REST_URL)
                        .post(requestBody)
                        .build();
                if (baseHttpClient == null) {
                    baseHttpClient = new OkHttpClient();
                }
                if (upHttpClient == null) {
                    upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                }

                upResponse = upHttpClient.newCall(upHttpRequest).execute();
                Log.d("#succ", "#" + upResponse.isSuccessful());
                if (upResponse.code() == 200) {
                    String respStr = upResponse.body().string();
                    Log.d("#respStr", "[" + respStr + "]");
                    if (CommonUtil.checkNB(respStr)) {
                        HashMap<String, Object> resultMap = JSONObject.parseObject(respStr, HashMap.class);
                        Log.d("#map", resultMap.get("response_params").toString());
                        // 指令是否执行成功
                        // HashMap<String, Object> response_paramsMap = (HashMap<String, Object>)
                        // resultMap.get("response_params");
                        LinkedHashMap<String, Object> response_paramsMap = (LinkedHashMap<String, Object>) resultMap
                                .get("response_params");
                        if (response_paramsMap != null) {
                            Integer tag_num = ((Double) response_paramsMap.get("tag_num")).intValue();
                            Log.d("#tag_num", "" + tag_num);

                            ArrayList<LinkedHashMap<String, Object>> tagsOs = (ArrayList<LinkedHashMap<String, Object>>) response_paramsMap
                                    .get("tags");
                            if (tagsOs != null) {
                                for (LinkedHashMap<String, Object> tag : tagsOs) {
                                    String name = (String) tag.get("name");
                                    Log.d("name", "[" + name);
                                    Log.d("tid", "[" + tag.get("tid"));
                                }
                            }
                        }
                    }

                    result = CommonParam.RESULT_SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
        }

    }

    /**
     * 返回
     */
    public void goBack() {
        // 创建启动 Activity 的 Intent
        // Intent intent = new Intent(DbActivity.this, ExerciseMainActivity.class);
        // 信息传输Bundle
        // Bundle data = new Bundle();
        // 将数据存入Intent中
        // intent.putExtras(data);
        // startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.activity_slide_right_out);
    }

    /**
     * 初始化信息参数
     */
    public void initInfoConfig() {
        HashMap<String, Object> infoConfig = CommonParam.infoConfig;
        if (infoConfig == null || infoConfig.size() == 0) {
            if (infoConfig == null) {
                infoConfig = new HashMap<String, Object>();
            }
            infoTool = getInfoTool();
            ArrayList<HashMap<String, Object>> recList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(
                    "select * from sysconfig model", new String[]{});
            for (HashMap<String, Object> map : recList) {
                infoConfig.put((String) map.get("paramname"), (String) map.get("paramvalue"));
            }
            CommonParam.infoConfig = infoConfig;
        }
    }

    /**
     * 用来获得某个url地址信息的 AsyncTask
     */
    public class GetUrlInfoTask extends AsyncTask<String, Integer, String> {
        /**
         * 是否显示等待提示
         */
        private String waitFlag = "1";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String result = CommonParam.RESULT_ERROR;
            // 是否显示等待提示
            waitFlag = params[1];
            // 服务器返回的文本
            String respStr = "";
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。开始=================

            try {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url(params[0])
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
                    result = CommonParam.RESULT_SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // 隐藏等待窗口
            unWait();
        }
    }

    public void locationMethod(BDLocation location) {

    }

    /**
     * 从服务器查询数据库信息，并将信息写入本地数据库中
     *
     * @param tableName   {@code String} 表名
     * @param queryParams {@code Map<String, Object>} 查询参数
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> serverTbToLocalTb(String tableName, Map<String, Object> queryParams) throws Exception {
        return serverTbToLocalTb(tableName, queryParams, tableName, true);
    }

    /**
     * 从服务器查询数据库信息，并将信息写入本地数据库中
     *
     * @param remoteTbName {@code String} 远程表名
     * @param remoteParams {@codeMap<String, Object>} 远程查询参数
     * @param localTbName  {@code String} 本地表名
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> serverTbToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                 String localTbName) throws Exception {
        return serverTbToLocalTb(remoteTbName, remoteParams, localTbName, true);
    }

    /**
     * 从服务器查询数据库信息，并将信息写入本地数据库中
     *
     * @param remoteTbName    {@code String} 远程表名
     * @param remoteParams    {@code Map<String, Object>} 远程查询参数
     * @param localTbName     {@code String} 本地表名
     * @param deleteFirstFlag {@code boolean} 写入之前是否清空表中的信息
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> serverTbToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                 String localTbName, boolean deleteFirstFlag) throws Exception {
        // 结果集
        Map<String, Object> dataset = null;
        // 返回结果是否正常
        boolean dataValidFlag = false;
        // 数据写入是否正常
        boolean resultFlag = false;

        dataset = getTbFromServer_ok(remoteTbName, remoteParams);
        dataValidFlag = (Boolean) dataset.get("dataValidFlag");
        if (!dataValidFlag) {
            throw new Exception();
        }
        resultFlag = insertToTable(dataset, localTbName, deleteFirstFlag);
        if (!resultFlag) {
            throw new Exception();
        }
        Log.d("写入数据库", localTbName);

        return dataset;
    }

    /**
     * 从服务器查询数据库信息，并将信息写入本地数据库中
     * <p>服务器返回数据库文件</p>
     *
     * @param tableName   {@code String} 表名
     * @param queryParams {@code Map<String, Object>} 查询参数
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> serverTbFileToLocalTb(String tableName, Map<String, Object> queryParams) throws Exception {
        return serverTbFileToLocalTb(tableName, queryParams, tableName, true);
    }

    /**
     * 从服务器查询数据库信息，并将信息写入本地数据库中
     * <p>服务器返回数据库文件</p>
     *
     * @param remoteTbName {@code String} 远程表名
     * @param remoteParams {@codeMap<String, Object>} 远程查询参数
     * @param localTbName  {@code String} 本地表名
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> serverTbFileToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                     String localTbName) throws Exception {
        return serverTbFileToLocalTb(remoteTbName, remoteParams, localTbName, true);
    }

    /**
     * 从服务器查询数据库信息，并将信息写入本地数据库中
     * <p>服务器返回数据库文件</p>
     *
     * @param remoteTbName    {@code String} 远程表名
     * @param remoteParams    {@code Map<String, Object>} 远程查询参数
     * @param localTbName     {@code String} 本地表名
     * @param deleteFirstFlag {@code boolean} 写入之前是否清空表中的信息
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> serverTbFileToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                     String localTbName, boolean deleteFirstFlag) throws Exception {
        // 结果集
        Map<String, Object> dataset = null;
        // 返回结果是否正常
        boolean dataValidFlag = false;
        // 数据写入是否正常
        boolean resultFlag = false;

        dataset = getTbFileFromServer_ok(remoteTbName, remoteParams);
        dataValidFlag = (Boolean) dataset.get("dataValidFlag");
        if (!dataValidFlag) {
            throw new Exception();
        }
        resultFlag = insertToTable(dataset, localTbName, deleteFirstFlag);
        if (!resultFlag) {
            throw new Exception();
        }
        Log.d("写入数据库", localTbName);

        return dataset;
    }

    /**
     * 从服务器获得信息
     *
     * @param table  {@code String} 查询类型
     * @param params {@code Map<String, Object>} 查询参数
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> getTbFromServer_ok(String table, Map<String, Object> params) {
        // 返回结果是否正常
        boolean dataValidFlag = false;
        // 结果集
        Map<String, Object> dataset = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        // 服务器返回的文本
        String respStr = "";
        // 网络连接对象。开始=================
        Request upHttpRequest = null;
        Response upResponse = null;
        OkHttpClient upHttpClient = null;
        // 网络连接对象。开始=================
        try {
            // 生成参数。开始======================================
            // 生成参数。结束======================================

            // 设置post值。开始=========================
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                    .addFormDataPart("infoType", table);
            for (Entry<String, Object> e : params.entrySet()) {
                multipartBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
            }
            RequestBody requestBody = multipartBuilder.build();
            // 设置post值。结束=========================

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
            upHttpRequest = requestBuilder
                    .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_SEARCHTABLE)
                    .post(requestBody)
                    .build();
            if (baseHttpClient == null) {
                baseHttpClient = new OkHttpClient();
            }
            if (upHttpClient == null) {
                upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
            }

            upResponse = upHttpClient.newCall(upHttpRequest).execute();
            // Log.d("#succ", "#" + upResponse.isSuccessful());
            if (upResponse.code() == 200) {
                // 获取成功
                byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                // 如果返回的xml中有BOM头，要将其删除
                if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0] && respBytes[1] == FileUtil.UTF8BOM[1]
                        && respBytes[2] == FileUtil.UTF8BOM[2]) {
                    respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                } else {
                    respStr = new String(respBytes, "UTF-8");
                }
                // Log.d("##", "#" + respStr);

                JSONObject respJson = JSONObject.parseObject(respStr);
                String resultStr = respJson.getString("result");
                if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                    // 请求正确
                    JSONArray dataJson = respJson.getJSONArray("data");
                    for (int i = 0, len = dataJson.size(); i < len; i++) {
                        JSONObject o = dataJson.getJSONObject(i);
                        HashMap<String, Object> m = new HashMap<String, Object>();
                        for (Entry<String, Object> e : o.entrySet()) {
                            m.put(e.getKey(), e.getValue());
                        }
                        data.add(m);
                    }
                    dataValidFlag = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        if (dataset == null) {
            dataset = new HashMap<String, Object>();
        }
        dataset.put("dataValidFlag", dataValidFlag);
        if (dataValidFlag) {
            dataset.put("data", data);
        }

        return dataset;
    }

    /**
     * 从服务器获得信息
     * <p>服务器返回数据库文件</p>
     *
     * @param table  {@code String} 查询类型
     * @param params {@code Map<String, Object>} 查询参数
     * @return {@code Map<String, Object>} 结果集
     */
    public Map<String, Object> getTbFileFromServer_ok(String table, Map<String, Object> params) {
        // 返回结果是否正常
        boolean dataValidFlag = false;
        // 结果集
        Map<String, Object> dataset = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        // 服务器返回的文本
        String respStr = "";
        // 网络连接对象。开始=================
        Request upHttpRequest = null;
        Response upResponse = null;
        OkHttpClient upHttpClient = null;
        // 网络连接对象。开始=================
        try {
            // 生成参数。开始======================================
            // 生成参数。结束======================================

            // 设置post值。开始=========================
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                    .addFormDataPart("infoType", table)
                    .addFormDataPart("dbSign", CommonParam.YES);
            for (Entry<String, Object> e : params.entrySet()) {
                multipartBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
            }
            RequestBody requestBody = multipartBuilder.build();
            // 设置post值。结束=========================

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
            upHttpRequest = requestBuilder
                    .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_SEARCHTABLE)
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
                if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0] && respBytes[1] == FileUtil.UTF8BOM[1]
                        && respBytes[2] == FileUtil.UTF8BOM[2]) {
                    respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                } else {
                    respStr = new String(respBytes, "UTF-8");
                }
                // Log.d("##", "#" + respStr);

                JSONObject respJson = JSONObject.parseObject(respStr);
                String resultStr = respJson.getString("result");
                if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                    // 请求正确
                    JSONObject dataJson = respJson.getJSONObject("data");
                    boolean fileFlag = dataJson.getBoolean("flag");
                    if (fileFlag) {
                        String dbFileName = dataJson.getString("db");
                        JSONArray dbColumnArray_jo = dataJson.getJSONArray("cs");
                        String[] dbColumnArray = new String[dbColumnArray_jo.size()];
                        for (int i = 0, len = dbColumnArray_jo.size(); i < len; i++) {
                            dbColumnArray[i] = dbColumnArray_jo.getString(i);
                        }

                        File downloadDbFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                                + CommonParam.PROJECT_NAME + "/temp/" + dbFileName);
                        Map<String, Object> downloadResult = downloadFile("http://" + baseApp.serverAddr
                                + "/temp/" + dbFileName, downloadDbFile.getAbsolutePath(), null);
                        String r = (String) downloadResult.get("result");
                        if (CommonParam.RESULT_SUCCESS.equals(r)) {
                            SQLiteDatabase downloadDb;
                            InsTool downloadInsTool;

                            downloadDb = DbTool.getDb(downloadDbFile);
                            // 结果信息
                            ArrayList<HashMap<String, Object>> resultInfoList = null;

                            if (downloadDb != null) {
                                downloadInsTool = new InsTool(DbActivity.this, new DbTool(DbActivity.this, downloadDb));

                                try {
                                    // 读取数据。开始================================================================
                                    resultInfoList = downloadInsTool.getInfoMapCusList(
                                            "select * from tb model",
                                            new String[]{}, dbColumnArray);
                                    data = resultInfoList;
                                    resultInfoList = null;
                                    // 读取数据。结束==============================================================

                                    dataValidFlag = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // 关闭上传库
                                DbTool.closeDb(downloadDb);
                            }

                            if (downloadDbFile.exists()) {
                                downloadDbFile.delete();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        if (dataset == null) {
            dataset = new HashMap<String, Object>();
        }
        dataset.put("dataValidFlag", dataValidFlag);
        if (dataValidFlag) {
            dataset.put("data", data);
        }

        return dataset;
    }

    /**
     * 处理信息
     * <p>
     * 当APP从服务器获取信息后，会在这里进行处理。
     */
    public HashMap<String, Object> processRec(String table, HashMap<String, Object> m) {
        if ("t_base_role".equals(table)) {
        } else if ("t_base_deptinfo".equals(table)) {
        } else if ("t_base_userinfo".equals(table)) {
            pm(m, "dept_id", "deptId");
            pm(m, "position_id", "positionId");
            pm(m, "msg_cfg", "msgCfg");
            m.remove("deptId");
            m.remove("positionId");
            m.remove("msgCfg");
        } else if ("t_base_code".equals(table)) {
        } else if ("t_biz_sgxuns".equals(table)) {
            m.put("realatime", "");
            m.put("realbtime", "");
            m.put("memo", "");
        } else if ("t_szfgs_sgwxrec".equals(table)) {
            m.put("up", "1");
        }

        return m;
    }

    /**
     * 处理信息
     * <p>将指定字段的值设置到新的字段中，并把旧字段删除。</p>
     *
     * @param m     {@code HashMap<String, Object>} 信息Map
     * @param c_new {@code String} 新字段名}
     * @param c_old {@code String} 旧字段名}
     */
    public void pm(HashMap<String, Object> m, String c_new, String c_old) {
        if (!m.containsKey(c_new) && m.containsKey(c_old)) {
            m.put(c_new, m.get(c_old));
            m.remove(c_old);
        }
    }

    /**
     * 通过 FTP 下载文件并保存到本地
     *
     * @param url          {@code String} ftp服务器地址 如： 192.168.1.110
     * @param port         {@code int} 端口如 ： 21
     * @param username     {@code String} 登录名
     * @param password     {@code String} 密码
     * @param remotePath   {@code String} 要下载文件的路径
     * @param fileName     {@code String} 文件名
     * @param fileSavePath {@code String} 保存到本地的路径和名称
     * @return {@code String} 下载结果
     */
    public String ftpDownload(String url, int port, String username, String password, String remotePath,
                              String fileName, String fileSavePath) {
        // 下载结果
        String result = CommonParam.RESULT_ERROR;

        FTPClient ftpClient = new FTPClient();
        BufferedOutputStream fs = null;
        try {
            // ftpClient.setBufferSize(1024);
            ftpClient.setControlKeepAliveTimeout(1000L);
            FTPClientConfig ftpConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
            ftpConfig.setServerTimeZoneId("Asia/Shanghai");
            ftpConfig.setServerLanguageCode("zh");
            ftpClient.configure(ftpConfig);
            ftpClient.connect(url, port);
            // 登录成功标志
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // 登录成功
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                fs = new BufferedOutputStream(new FileOutputStream(fileSavePath));
                boolean flag = ftpClient.retrieveFile(fileName, fs);
                if (flag) {
                    result = CommonParam.RESULT_SUCCESS;
                }
            } else {
                // 登录失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ftpDownload", "FTP客户端出错！");
        } finally {
            if (fs != null) {
                try {
                    fs.flush();
                    fs.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ftpDownload", "FTP客户端关闭出错！");
            }
        }

        return result;
    }

    /**
     * 通过 FTP 下载多个文件并保存到本地
     *
     * @param url           {@code String} ftp服务器地址 如： 192.168.1.110
     * @param port          {@code int} 端口如 ： 21
     * @param username      {@code String} 登录名
     * @param password      {@code String} 密码
     * @param remotePath    {@code String} 要下载文件的路径
     * @param fileNames     {@code String[]} 文件名
     * @param fileSavePaths {@code String[]} 保存到本地的路径和名称
     * @return {@code Map<String, Object>} 下载结果
     */
    public Map<String, Object> ftpDownload(String url, int port, String username, String password, String remotePath,
                                           String[] fileNames, String[] fileSavePaths) {
        return ftpDownload(url, port, username, password, remotePath, fileNames, fileSavePaths, false);
    }

    /**
     * 通过 FTP 下载多个文件并保存到本地
     *
     * @param url               {@code String} ftp服务器地址 如： 192.168.1.110
     * @param port              {@code int} 端口如 ： 21
     * @param username          {@code String} 登录名
     * @param password          {@code String} 密码
     * @param remotePath        {@code String} 要下载文件的路径
     * @param fileNames         {@code String[]} 文件名
     * @param fileSavePaths     {@code String[]} 保存到本地的路径和名称
     * @param errorContinueFlag {@code boolean} 出错后是否继续下载标志
     * @return {@code Map<String, Object>} 下载结果
     */
    public Map<String, Object> ftpDownload(String url, int port, String username, String password, String remotePath,
                                           String[] fileNames, String[] fileSavePaths, boolean errorContinueFlag) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 下载结果
        String result = CommonParam.RESULT_SUCCESS;
        // 文件总数
        int total = fileNames.length;
        // 完成数
        int done = 0;

        FTPClient ftpClient = new FTPClient();
        BufferedOutputStream fs = null;
        try {
            // ftpClient.setBufferSize(1024);
            ftpClient.setControlKeepAliveTimeout(300L);
            FTPClientConfig ftpConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
            ftpConfig.setServerTimeZoneId("Asia/Shanghai");
            ftpConfig.setServerLanguageCode("zh");
            ftpClient.configure(ftpConfig);
            ftpClient.connect(url, port);
            // 登录成功标志
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // 登录成功
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                for (int i = 0, len = fileNames.length; i < len; i++) {
                    try {
                        fs = new BufferedOutputStream(new FileOutputStream(fileSavePaths[i]));
                        Log.d("#下载", fileSavePaths[i]);
                        boolean flag = ftpClient.retrieveFile(fileNames[i], fs);
                        Log.d("#", "" + flag);
                        if (!flag) {
                            result = CommonParam.RESULT_ERROR;
                            break;
                        }
                        done++;
                    } catch (Exception e) {
                        result = CommonParam.RESULT_ERROR;
                        if (!errorContinueFlag) {
                            throw e;
                        }
                    } finally {
                        if (fs != null) {
                            fs.flush();
                            fs.close();
                        }
                    }
                }
            } else {
                // 登录失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = CommonParam.RESULT_ERROR;
            Log.d("ftpDownload", "FTP客户端出错！");
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ftpDownload", "FTP客户端关闭出错！");
            }
        }

        resultMap.put("result", result);
        resultMap.put("total", total);
        resultMap.put("done", done);
        return resultMap;
    }

    /**
     * 通过 FTP 上传文件
     *
     * @param url        {@code String} ftp服务器地址 如： 192.168.1.110
     * @param port       {@code int} 端口如 ： 21
     * @param username   {@code String} 登录名
     * @param password   {@code String} 密码
     * @param remotePath {@code String} 服务器要要上传文件的路径
     * @param fileName   {@code String} 文件名
     * @param fileUpPath {@code String} 要上传文件的路径和名称
     * @return {@code String} 上传结果
     */
    public String ftpUpload(String url, int port, String username, String password, String remotePath, String fileName,
                            String fileUpPath) {
        // 下载结果
        String result = CommonParam.RESULT_ERROR;

        FTPClient ftpClient = new FTPClient();
        BufferedInputStream fi = null;
        try {
            ftpClient.setBufferSize(1024);
            ftpClient.setControlKeepAliveTimeout(300L);
            FTPClientConfig ftpConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
            ftpConfig.setServerTimeZoneId("Asia/Shanghai");
            ftpConfig.setServerLanguageCode("zh");
            ftpClient.configure(ftpConfig);
            ftpClient.connect(url, port);
            // 登录成功标志
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // 登录成功
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();

                fi = new BufferedInputStream(new FileInputStream(fileUpPath));
                boolean flag = ftpClient.storeFile(fileName, fi);
                if (flag) {
                    result = CommonParam.RESULT_SUCCESS;
                }
            } else {
                // 登录失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ftpDownload", "FTP客户端出错！");
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ftpDownload", "FTP客户端关闭出错！");
            }
        }

        return result;
    }

    /**
     * 通过 FTP 上传多个文件
     *
     * @param url         {@code String} ftp服务器地址 如： 192.168.1.110
     * @param port        {@code int} 端口如 ： 21
     * @param username    {@code String} 登录名
     * @param password    {@code String} 密码
     * @param remotePath  {@code String} 服务器要要上传文件的路径
     * @param fileNames   {@code String[]} 文件名
     * @param fileUpPaths {@code String[]} 要上传文件的路径和名称
     * @return {@code String} 上传结果
     */
    public String ftpUpload(String url, int port, String username, String password, String remotePath,
                            String[] fileNames, String[] fileUpPaths) {
        // 上传结果
        String result = CommonParam.RESULT_SUCCESS;

        FTPClient ftpClient = new FTPClient();
        BufferedInputStream fi = null;
        try {
            ftpClient.setBufferSize(1024);
            ftpClient.setControlKeepAliveTimeout(300L);
            FTPClientConfig ftpConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
            ftpConfig.setServerTimeZoneId("Asia/Shanghai");
            ftpConfig.setServerLanguageCode("zh");
            ftpClient.configure(ftpConfig);
            ftpClient.connect(url, port);
            // 登录成功标志
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // 登录成功
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();

                for (int i = 0, len = fileNames.length; i < len; i++) {
                    try {
                        fi = new BufferedInputStream(new FileInputStream(fileUpPaths[i]));
                        Log.d("#上传", fileUpPaths[i]);
                        boolean flag = ftpClient.storeFile(fileNames[i], fi);
                        Log.d("#", "" + flag);
                        if (!flag) {
                            result = CommonParam.RESULT_ERROR;
                            break;
                        }
                    } catch (Exception e) {
                        result = CommonParam.RESULT_ERROR;
                        throw e;
                    } finally {
                        if (fi != null) {
                            fi.close();
                        }
                    }
                }
            } else {
                // 登录失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = CommonParam.RESULT_ERROR;
            Log.d("ftpDownload", "FTP客户端出错！");
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ftpDownload", "FTP客户端关闭出错！");
            }
        }

        return result;
    }

    /**
     * 显示服务器地址设置对话框
     */
    public void makeSetServerDialog() {
        Builder dlgBuilder = new Builder(this);

        // Get the layout inflater
        final LinearLayout setServlerlayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dlg_set_server, null);
        dlgBuilder.setView(setServlerlayout);
        dlgBuilder.setTitle(R.string.setServer);
        dlgBuilder.setIcon(R.drawable.menu_server);
        final EditText servAddrView = (EditText) setServlerlayout.findViewById(R.id.serveraddr);

        servAddrView.setText(CommonUtil.N2B(baseApp.serverAddr));
        Button confirmBtn = (Button) setServlerlayout.findViewById(R.id.confirmUrlBtn);
        Button defaultBtn = (Button) setServlerlayout.findViewById(R.id.urlDefaultBtn);
        Button urlDefaultOtherBtn = (Button) setServlerlayout.findViewById(R.id.urlDefaultOtherBtn);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String servAddr_tmp = servAddrView.getText().toString();
                if (CommonUtil.checkNB(servAddr_tmp) && servAddr_tmp.lastIndexOf("/") == servAddr_tmp.length() - 1) {
                    while (servAddr_tmp.length() > 0 && servAddr_tmp.lastIndexOf("/") == servAddr_tmp.length() - 1) {
                        servAddr_tmp = servAddr_tmp.substring(0, servAddr_tmp.length() - 1);
                    }
                    servAddrView.setText(servAddr_tmp);
                }

                if (CommonUtil.checkNB(servAddr_tmp) && servAddr_tmp.length() > 1) {
                    baseApp.serverAddr = servAddr_tmp;
                    preferEditor.putString("SERVER_ADDR", baseApp.serverAddr);
                    preferEditor.commit();

                    addrDlg.cancel();
                } else {
                    show(R.string.show_setServer_invalid);
                }
            }
        });
        defaultBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                servAddrView.setText(baseApp.serverAddr_out);
            }
        });
        urlDefaultOtherBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                servAddrView.setText(baseApp.serverAddr_in);
            }
        });
        addrDlg = dlgBuilder.create();
        addrDlg.show();
    }

    /**
     * 上传测试信息 AsyncTask 类
     */
    public class UploadTestDataTask extends AsyncTask<Object, Integer, String> {
        /**
         * FTP常量：用户名
         */
        public static final String FTP_USERNAME = "ftpuser";
        /**
         * FTP常量：密码
         */
        public static final String FTP_PASSWORD = "Isdn1603!@#";
        /**
         * FTP常量：端口
         */
        public static final int FTP_PORT = 21;
        /**
         * FTP常量：服务器缺陷文件目录
         */
        public static final String FTP_REMOTE_UPFILE = "q";

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在上传，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 网络连接对象。开始=================
            try {
                // 生成参数。开始======================================
                // 生成参数。结束======================================
                String fileSavePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/db/sys.db").getAbsolutePath();
                // Log.d("###", fileSavePath);
                // 上传结果
                String uploadResult = ftpUpload("scgl.spyatsea.com", FTP_PORT, FTP_USERNAME, FTP_PASSWORD,
                        FTP_REMOTE_UPFILE, CommonParam.PROJECT_NAME + "_" + (CommonUtil.checkNB((String) baseApp.loginUser.get("ids")) ? ((String) baseApp.loginUser.get("ids")).substring(0, 8) : "anonymous") + "_" + CommonUtil.getDT("yyyyMMddHHmmssSSS") + "_"
                                + "sys.db", fileSavePath);
                if (uploadResult.equals(CommonParam.RESULT_SUCCESS)) {
                    result = CommonParam.RESULT_SUCCESS;
                }
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
            unWait();
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                show("上传成功！");
            } else {
                show("上传失败！");
            }
        }
    }

    /**
     * 显示清理缓存提示对话框
     */
    public void makeClearCacheDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.clearCacheAsk);
        dlgBuilder.setIcon(R.drawable.ic_dialog_broom_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 清理缓存
                new ClearCacheTask().execute();
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();
    }

    /**
     * 清理缓存的 AsyncTask 类
     */
    public class ClearCacheTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：清理缓存
         */
        private static final int PROGRESS_CLEAR_CACHE = 1001;

        /**
         * 文件容量
         */
        private long fileLength = 0L;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            publishProgress(PROGRESS_CLEAR_CACHE);
            // 统计缓存文件。开始=================================
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File pkgDir = new File(sdcard.getAbsolutePath() + "/" + CommonParam.PROJECT_NAME);
                    String[] dirNameArray = new String[]{"atta", "cache", "temp", "update", "upload"};
                    for (String dirName : dirNameArray) {
                        File dir = new File(pkgDir, dirName);
                        for (File file : dir.listFiles()) {
                            if (file.exists() && file.isFile()) {
                                fileLength = fileLength + file.length();
                                file.delete();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 统计缓存文件。结束=================================

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
            if (progress[0] == PROGRESS_CLEAR_CACHE) {
                makeWaitDialog(R.string.alert_wait_clear_cache_start_clear);
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
            // 显示完成提示
            show(getString(R.string.clearCacheDetail, FileUtil.getFileSizeStatic((double) fileLength)));
        }
    }

    /**
     * 检测是否安装了指定的APP
     *
     * @param packageName {@code String} APP包名
     * @return {@code boolean} 是否安装
     */
    public boolean isAppInstalled(String packageName) {
        boolean flag = false;
        PackageManager manager = getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0, len = pkgList.size(); i < len; i++) {
            PackageInfo pInfo = pkgList.get(i);
            if (pInfo.packageName.equalsIgnoreCase(packageName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 显示帮助对话框
     *
     * @param layoutRes {@code int} 布局文件资源
     */
    public void makeHelpDialog(int layoutRes) {
        Builder dlgBuilder = new Builder(this);
        ScrollView layout = (ScrollView) getLayoutInflater().inflate(layoutRes, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setIcon(R.drawable.help);
        dlgBuilder.setTitle(R.string.help);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        helpDlg = dlgBuilder.create();
        helpDlg.show();
    }

    // UHF相关方法。开始============================================

    /**
     * 获得UHF基本信息
     */
    public Map<String, Object> getUhfInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        byte[] Version = new byte[2];
        byte[] Power = new byte[1];
        byte[] band = new byte[1];
        byte[] MaxFre = new byte[1];
        byte[] MinFre = new byte[1];
        byte[] BeepEn = new byte[1];
        byte[] Ant = new byte[1];

        if (baseApp.isUhfPda) {
            // 尝试次数
            int tryCount = 10;
            int result = -1;
//            for (int i = 0; i < tryCount; i++) {
//                result = com.cox.android.uhf.Reader.rrlib.GetUHFInformation(Version, Power, band, MaxFre, MinFre, BeepEn, Ant);
//                if (result == 0) {
//                    String hvn = String.valueOf(Version[0]);
//                    if (hvn.length() == 1) hvn = "0" + hvn;
//                    String lvn = String.valueOf(Version[1]);
//                    if (lvn.length() == 1) lvn = "0" + lvn;
//                    int bandindex = band[0];
//                    if (bandindex == 8) {
//                        bandindex = bandindex - 4;
//                    } else {
//                        bandindex = bandindex - 1;
//                    }
//
//                    map.put("version", hvn + "." + lvn);
//                    map.put("power", (int) Power[0]);
//                    map.put("fre", (int) band[0]);
//                    map.put("band", bandindex);
//                    map.put("minFrm", (int) MinFre[0]);
//                    map.put("maxFrm", (int) MaxFre[0]);
//
//                    break;
//                } else {
//                    Log.d("###result", i + "#" + result);
//                    doWait(100);
//                }
//            }
            result = com.cox.android.uhf.Reader.rrlib.GetUHFInformation(Version, Power, band, MaxFre, MinFre, BeepEn, Ant);
            if (result == 0) {
                String hvn = String.valueOf(Version[0]);
                if (hvn.length() == 1) hvn = "0" + hvn;
                String lvn = String.valueOf(Version[1]);
                if (lvn.length() == 1) lvn = "0" + lvn;
                int bandindex = band[0];
                if (bandindex == 8) {
                    bandindex = bandindex - 4;
                } else {
                    bandindex = bandindex - 1;
                }

                map.put("version", hvn + "." + lvn);
                map.put("power", (int) Power[0]);
                map.put("fre", (int) band[0]);
                map.put("band", bandindex);
                map.put("minFrm", (int) MinFre[0]);
                map.put("maxFrm", (int) MaxFre[0]);
            }
        }
        baseApp.uhfPdaInfo = map;

        Log.d("###uhfPdaInfo", JSONObject.toJSONString(baseApp.uhfPdaInfo));
        return map;
    }
    // UHF相关方法。结束============================================

}
