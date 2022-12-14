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
 * ?????????????????????????????????????????????????????????
 *
 * @author ??????(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class DbActivity extends AppCompatActivity {
    /**
     * ?????????
     */
    SQLiteDatabase db = null;

    /**
     * ????????????
     */
    int screenWidth;
    /**
     * ????????????
     */
    int screenHeight;

    /**
     * ???????????????AlertDialog
     */
    boolean customizeAlertDlgFlag = false;
    // ?????????????????????==============================================
    SharedPreferences preferences = null;
    SharedPreferences.Editor preferEditor = null;
    /**
     * ?????????????????????
     */
    boolean exitAppFlag = false;
    /**
     * ????????????????????????????????????????????????
     */
    int readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
    /**
     * ???UHF?????????????????????????????????????????????
     */
    int readUhfCardType = CommonParam.READ_UHF_CARD_TYPE_NO_ACTION;
    /**
     * ???????????????Application
     */
    BaseApplication baseApp = null;
    /**
     * ??????????????????
     */
    boolean isSearching = false;
    /**
     * ???????????????????????? <p> ?????????INS????????????????????????????????????????????????????????????????????????????????????????????????
     */
    String fromFlag = "";
    /**
     * ?????????????????????????????? <p> ?????????INS???????????????????????????????????????
     */
    String fromFlagType = "";
    /**
     * ????????????????????????
     */
    String nowFlag = "";
    /**
     * ????????????
     */
    String userRoles;
    // ?????????????????????==============================================

    // ????????????????????????========================================================
    // ????????????????????????
    ActionBar actionBar;
    /**
     * ??????????????????
     */
    TextView titleText = null;
    // ????????????????????????========================================================

    // ?????????????????????????????????==========================================
    public static final int WAIT_SECONDS = 10;
    /**
     * ??????????????????????????????
     */
    static final int MESSAGE_CHECKUPDATE_APP = 0x010;
    /**
     * ??????????????????????????????
     */
    static final int MESSAGE_UPDATE_APP = 0x011;
    /**
     * ????????????????????????
     */
    boolean isUpdating = false;
    /**
     * ?????????????????????Dialog
     */
    AlertDialog updateDlg = null;
    /**
     * ???????????????ProgressDialog
     */
    private ProgressDialog updateProgressDlg = null;
    /**
     * ???????????????Call??????
     * */
    private Call upCall;
    /**
     * ???????????? AsyncTask ??????
     */
    private AsyncTask<String, Integer, String> testUpdateTask;
    // NFC?????????????????????========================================================
    NfcAdapter mAdapter;
    // ?????????????????????????????????==========================================
    PendingIntent mPendingIntent;
    IntentFilter[] mFilters;
    String[][] mTechLists;
    /**
     * ????????????
     */
    DbTool dbTool;
    // NFC?????????????????????========================================================
    /**
     * ???????????????
     */
    UserTool userTool;
    /**
     * ???????????????
     */
    InsTool insTool;
    /**
     * ???????????????
     */
    InfoTool infoTool;
    /**
     * ?????????????????????????????????
     */
    AlertDialog addrDlg = null;
    /**
     * ??????????????????????????????
     */
    AlertDialog locDlg = null;
    /**
     * ?????????????????????
     */
    AlertDialog userInfoDlg = null;
    /**
     * ??????Dialog
     */
    public AlertDialog helpDlg;
    // ?????????????????????????????????==========================================
    ProgressDialog upProDlg = null;
    int upProNum = 0;
    /**
     * ????????????????????????
     */
    boolean isUploading = false;
    /**
     * ????????????????????????
     */
    boolean isDownloading = false;
    OkHttpClient baseHttpClient = null;
    // ?????????????????????????????????==========================================
    LocationManager lm = null;

    // ?????????????????????????????????==========================================
    // ????????????
    Double latitude = 0.0D;
    // ????????????
    Double longitude = 0.0D;
    // ??????GPS??????
    Long gpsTime = 0L;
    // ????????????????????????
    Double latitude_baidu = 0.0D;
    // ????????????????????????
    Double longitude_baidu = 0.0D;
    // ????????????
    Double altitude = 0.0D;
    String locationProvider = "";
    // ????????????SDK????????????
    LocationClient locationClient_baidu = null;
    public BDLocationListener locListener_baidu = new MyLocationListener();
    public static final int THREE_SECONDS = 10000;
    public static final int TEN_METERS = 10;
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    /**
     * ???????????? GPS ??????
     */
    boolean locProviderFlag_GPS = false;
    /**
     * ????????????????????????
     */
    boolean locProviderFlag_Net = false;
    /**
     * ????????????????????? GPS ??????
     */
    boolean mUseGPS = false;
    /**
     * ?????????????????????????????????
     */
    boolean mUseNet = false;
    /**
     * ???????????????GPS ?????????
     */
    boolean mUseBoth = false;
    /**
     * ???????????????GPS ??? ??????
     */
    boolean mUseOne = false;
    /**
     * ????????????????????????????????????????????????
     */
    boolean needShowLocAlertFlag = true;
    /**
     * ??????????????????
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
     * ??????SDK????????????
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
     * ?????????????????????????????? SDK key ??????????????????????????????
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                // show("???????????? key ????????????!");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                // show("????????????");
            }
        }
    }

    private SDKReceiver mReceiver;
    // ???????????????????????????==========================================
    // Name for the SDP record when creating server socket
    public static final String NAME_SECURE = "BluetoothChatSecure";
    // ?????????????????????????????????==========================================
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
     * ?????????????????????ProgressDialog
     */
    ProgressDialog baseInfoSyncDlg = null;
    // ???????????????????????????==========================================

    // ???????????????????????????==========================================
    /**
     * ?????????ProgressDialog
     */
    ProgressDialog waitDlg = null;
    // ????????????????????????????????????Handler
    final Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // ???????????????????????????????????????
            switch (msg.what) {
                case MESSAGE_CHECKUPDATE_APP:
                    // ???????????????
                    // ??????????????????
                    makeWaitDialog(R.string.alert_check_update);
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * ?????????????????????
     */
    PopupWindow loadingWindow = null;
    /**
     * ?????????????????????
     */
    MediaPlayer mediaPlayer;
    // ???????????????????????????==========================================

    // ??????????????????????????????============================================
    /**
     * ????????????layout
     */
    FrameLayout parentFrameLayout;
    // ??????????????????????????????============================================

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

        // ?????????????????????
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();

        // NFC?????????========================================================
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
        // NFC?????????========================================================

        baseApp = getBaseApp();

        // ?????????????????????
        initPreferences();

        dbTool = getDbTool();
//		// db = baseApp.getDb();
//		// if (db == null || !db.isOpen()) {
//		// // ???????????????????????????
//		// db = dbTool.regetDb();
//		// baseApp.setDb(db);
//		// }
//		// dbTool.setDb(baseApp.getDb());
//
        // ?????????????????????
        initInfoConfig();
        if (baseApp.loginUser != null) {
            userRoles = CommonUtil.N2B((String) baseApp.loginUser.get("roles"));
        }

        // ????????????????????????????????????==================================================
        Intent postIntent = getIntent();
        Bundle postData = postIntent.getExtras();
        if (postData != null) {

        }
        // ????????????????????????????????????==================================================

        // ?????????????????????===========================================================
        // ?????? SDK ???????????????
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        if (lm == null) {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // ????????????????????? GPS ????????????
            if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {
                locProviderFlag_GPS = true;
            }
            // ???????????????????????????????????????
            if (lm.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                locProviderFlag_Net = true;
            }
        }
        if (locationClient_baidu == null) {
            // ??????LocationClient???
            locationClient_baidu = new LocationClient(getApplicationContext());
            // ??????????????????
            locationClient_baidu.registerLocationListener(locListener_baidu);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // ????????????
            option.setOpenGps(true); // ??????????????????gps?????????false
            option.setLocationNotify(true); // ???????????????GPS???????????????1S/1???????????????GPS???????????????false
            option.setIgnoreKillProcess(true); // ???????????????stop???????????????????????????????????????????????????????????????setIgnoreKillProcess(true)
            option.setIsNeedAddress(true);// ???????????????????????????????????????
            option.setNeedDeviceDirect(true);// ????????????????????????????????????????????????
            option.setScanSpan(1000);// ????????????????????????????????????int???????????????ms
            option.setWifiCacheTimeOut(5 * 60 * 1000); // ???????????????????????????????????????????????????????????????Wi-Fi???????????????????????????????????????????????????????????????Wi-Fi???????????????
            locationClient_baidu.setLocOption(option);
        }
        locationClient_baidu.start();
        // ?????????????????????===========================================================
    }

    /**
     * ??????????????????
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(this.getClass().getName() + ":log", "onCreateOptionsMenu()");
        return false;
    }

    /**
     * ??????????????????????????????????????????
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(this.getClass().getName() + ":log", "onPrepareOptionsMenu()");
        return false;
    }

    /**
     * ???????????????????????????????????????
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.d(this.getClass().getName() + ":log", "onMenuOpened()");

        return true;
    }

    /**
     * ?????????????????????????????????
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(this.getClass().getName() + ":log", "onOptionsItemSelected()");
        switch (item.getItemId()) {
            // case R.id.config:
            // show(R.string.config);
            // break;
            case R.id.menu_resetDB:
                // ?????????????????????????????????
                makeResetDbDialog();

                break;
            case R.id.menu_setLocService:
                // ???????????????????????????
                makeSetLocServiceDialog();

                break;
            case R.id.update:
                // ????????????
                if (!isUpdating) {
                    testUpdateApp("http://" + baseApp.serverAddr + "/" + CommonParam.URL_CHECKUPDATE + "?token="
                            + CommonParam.APP_KEY + "&type=1", "1");
                }
                break;
            //case R.id.about:
            // ?????????????????????
            //	makeAboutDialog();
            //	break;
            case R.id.logout:
                // ????????????????????????
                checkLogoutDialog();
                break;
            case R.id.exit:
                // ?????????????????????????????????
                makeExitDialog();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * ?????????????????????
     */
    public void changeDb() {
        if (db != null && db.isOpen()) {
            // ?????????????????????
            dbTool.closeDb();
        }
        // ???????????????????????????
        db = dbTool.regetDb();
        baseApp.setDb(db);
    }

    /**
     * ????????????
     */
    public void resetListData() {
    }

    /**
     * ??????????????????
     */
    public void resetFieldData() {

    }

    /**
     * ?????????????????????????????????
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
                // ????????????
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

        // ???????????????????????????======================================================
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
        // ???????????????????????????======================================================
    }

    /**
     * ?????????????????????????????????
     */
    public void makeResetDbDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.resetDBAsk);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ????????????
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
     * ?????????????????????????????????
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
                // ??????API<11
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
                    // ???????????? ShowImageActivity ???Intent
                    Intent intent = new Intent(DbActivity.this, ShowMapActivity.class);
                    Bundle bundle = new Bundle();
                    Bundle data = new Bundle();
                    data.putDouble("lat", latitude);
                    data.putDouble("lon", longitude);
                    data.putDouble("lat_baidu", latitude_baidu);
                    data.putDouble("lon_baidu", longitude_baidu);
                    bundle.putBundle("infoBundle", data);
                    // ??????????????? Intent ???
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

            // ????????????
            Button locRefreshBtn = locDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
            // ??????
            Button confirmBtn = locDlg.getButton(DialogInterface.BUTTON_POSITIVE);
            // ??????
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
     * ?????????????????????
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
        // + "\n" + "???????????????" + "\n???http://"
        // + preferences.getString("SERVER_ADDR", getString(R.string.url_upload_default_out)) + "????????????"
        // + "\n???http://" + baseApp.serverAddr + "????????????" + "\n\n"
        // + "\n" + "???????????????" + "\n???http://"
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
        // ???????????? SDK ??????
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
                    "id(??????):" + Util.toHexString(tag.getId(), 0, tag.getId().length));
            String cardId = Util.toHexString(tag.getId(), 0, tag.getId().length);

            if (!CommonUtil.checkNB(cardId)) {
                // ?????????id??????
                show(R.string.alert_card_mac_blank);
                return;
            }
            if (readCardType == CommonParam.READ_CARD_TYPE_PRE_READ) {
                // ????????????
                readCard_pre(cardId);
            } else if (readCardType == CommonParam.READ_CARD_TYPE_EXTRA_CARD) {
                // ?????????????????????
                readExtraCard(cardId);
            } else if (readCardType == CommonParam.READ_CARD_TYPE_INSPECT) {
                // ?????????
                readCard_ins_onBefore(cardId);
            } else {
                // ????????????
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
     * ???????????????
     *
     * @param tag {@code Tag} Tag??????
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
     * ???????????????????????????????????????????????????????????? Activity ??????????????????
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(this.getClass().getName() + ":log", "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CommonParam.REQUESTCODE_OPENCARD && resultCode == CommonParam.RESULTCODE_OPENCARD) {
            // ???????????? CardDialog ???????????????
            // ?????? Intent ?????? Extras ??????
            Bundle data = intent.getExtras();
            // ?????? ID
            String cardId = data.getString("cardId");
            if (CommonUtil.checkNB(cardId)) {
                // ??????
                Log.d(this.getClass().getName() + ":log", "????????????????????? ID:" + cardId + " ????????????");
            } else {
                show("???????????????");
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
     * ?????????????????????
     */
    public void initPreferences() {
        preferences = baseApp.preferences;
        preferEditor = baseApp.preferEditor;
    }

    /**
     * ????????????
     */
    public void finishApp() {
        baseApp.checkUpdateFlag = false;
        exitAppFlag = true;

        this.finish();
    }

    /**
     * ?????? Toast ??????
     *
     * @param text {@code String} ????????????
     */
    public void show(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * ?????? Toast ??????
     *
     * @param resId {@code int} ??????id
     */
    public void show(int resId) {
        show(getString(resId));
    }

    /**
     * ?????? Toast ??????
     *
     * @param text     {@code String} ????????????
     * @param duration {@code int} ????????????
     */
    public void show(String text, int duration) {
        Toast toast = Toast.makeText(this, text, duration);
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * ?????? Toast ??????
     *
     * @param resId    {@code int} ??????id
     * @param duration {@code int} ????????????
     */
    public void show(int resId, int duration) {
        show(getString(resId), duration);
    }

    /**
     * ?????? Note
     */
    public void editNote() {

    }

    /**
     * ?????? Note
     */
    public void saveNote() {

    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
    }

    /**
     * ??????????????????????????????
     *
     * @param {@code boolean} ??????????????????????????????
     * @return {@code boolean} ????????????????????????
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
            // ????????????????????????
            if (needAlert) {
                show(R.string.alert_loc_nolocservice);
            }
            needShowLocAlertFlag = true;
            return false;
        } else if (locProviderFlag_Net && !locProviderFlag_GPS && !mUseNet && needShowLocAlertFlag) {
            // ????????????????????????????????????
            if (needAlert) {
                show(R.string.alert_loc_need_net);
            }
            needShowLocAlertFlag = true;
            return false;
        } else if (locProviderFlag_GPS && !mUseGPS && needShowLocAlertFlag) {
            // ???GPS????????????????????????
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
            // ??????????????????????????????????????????????????????????????????
            lm.removeUpdates(locListener);
            if (mUseOne) {
                // ???????????????????????????
                if (mUseGPS) {
                    // GPS??????
                    locationProvider = LocationManager.GPS_PROVIDER;
                    lm.requestLocationUpdates(locationProvider, THREE_SECONDS, TEN_METERS, locListener);
                } else if (mUseNet) {
                    // ????????????
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                    lm.requestLocationUpdates(locationProvider, THREE_SECONDS, TEN_METERS, locListener);
                }
                updateLocation(lm.getLastKnownLocation(locationProvider));
            } else if (mUseBoth) {
                // ????????????????????????
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
     * ??????????????????????????????(??????????????????)
     *
     * @return {@code boolean} ????????????????????????
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
     * ??????????????????
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
     * ?????????????????? GPS
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
                startActivityForResult(intent, 0); // ??????????????????????????????????????????
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

        // ???????????????????????????======================================================
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
        // ???????????????????????????======================================================
    }

    /**
     * ??????????????????
     *
     * @param time {@code int} ????????????????????????
     */
    public void doWait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     *
     * @param needAlert {@code boolean} ??????????????????????????????
     * @return {@code boolean} ????????????
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
     * ??????????????????(??????????????????)
     *
     * @return {@code boolean} ????????????
     */
    public boolean checkNet() {
        return checkNet(true);
    }

    /**
     * ??????????????????????????????
     *
     * @param urlString {@code String} ?????? URL
     * @return {@code Map<String, Object>} ??????????????????
     */
    private Map<String, Object> downloadUrl(String urlString) {
        return downloadUrl(urlString, 10000, 15000);
    }

    /**
     * ??????????????????????????????
     *
     * @param urlString      {@code String} ?????? URL
     * @param readTimeout    {@code int} ????????????????????????
     * @param connectTimeout {@code int} ????????????????????????
     * @return {@code Map<String, Object>} ??????????????????
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
     * ??????????????????
     *
     * @param servAddr {@code String} ???????????????
     */
    public void testUpdateApp(String servAddr) {
        testUpdateApp(servAddr, "1");
    }

    /**
     * ??????????????????
     *
     * @param servAddr {@code String} ???????????????
     * @param waitFlag {@code String} ????????????????????????
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
     * ??????????????????
     *
     * @param servAddr  {@code String} ???????????????
     * @param waitFlag  {@code String} ????????????????????????
     * @param needAlert {@code boolean} ????????????????????????????????????
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
     * ??????????????? AsyncTask
     */
    private class TestUpdate_ok extends AsyncTask<String, Integer, String> {
        // ????????????????????????
        private String waitFlag = "1";
        /**
         * ????????????
         */
        private VerInfo verInfo = null;

        @Override
        protected void onPreExecute() {
            isUpdating  = true;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = CommonParam.RESULT_ERROR;
            // ????????????????????????
            waitFlag = params[2];
            // ????????????????????????
            String respStr = "";
            // ???????????????????????????=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // ???????????????????????????=================
            if (waitFlag.equals("1")) {
                // ??????????????? Handler
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
                    // ????????????
                    byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                    // ???????????????xml??????BOM?????????????????????
                    if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0]
                            && respBytes[1] == FileUtil.UTF8BOM[1] && respBytes[2] == FileUtil.UTF8BOM[2]) {
                        respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                    } else {
                        respStr = new String(respBytes, "UTF-8");
                    }

                    JSONObject respJson = JSONObject.parseObject(respStr);
                    String resultStr = respJson.getString("result");
                    if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                        // ????????????
                        verInfo = new VerInfo(respJson.getString("vercode"), respJson.getString("vername"),
                                respJson.getString("url"), respJson.getString("vercontent"));
                        int ver = Integer.parseInt(verInfo.getVercode());
                        if (baseApp.versionCodeInt < ver) {
                            // ????????????
                            result = CommonParam.UPDATETYPE_APP;
                        } else {
                            // ???????????????
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
            // ??????????????????
            unWait();
            upCall = null;
            isUpdating = false;

            // ??????
            if (result.equals(CommonParam.UPDATETYPE_APP)) {
                // ????????????
                makeUpdateDialog(verInfo);
            } else if (result.equals(CommonParam.RESULT_IS_LATESTVER)) {
                // ???????????????????????????
                if (waitFlag.equals("1")) {
                    show(R.string.alert_update_islatestver);
                }
            } else {
                // ??????
                if (waitFlag.equals("1")) {
                    show(R.string.alert_net_format_error);
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param url       {@code String} ???????????????
     * @param localPath {@code String} ???????????????????????????????????????
     * @param methodStr {@code String} ??????????????????????????????
     */
    public Map<String, Object> downloadFile(String url, String localPath, String methodStr) {
        return downloadFile(url, localPath, methodStr, true);
    }

    /**
     * ??????????????????
     *
     * @param url       {@code String} ???????????????
     * @param localPath {@code String} ???????????????????????????????????????
     * @param methodStr {@code String} ??????????????????????????????
     * @param needAlert {@code boolean} ????????????????????????????????????
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
     * ????????????????????? AsyncTask
     * <p>
     * ?????????????????????????????????????????????
     *
     * @deprecated
     */
    @Deprecated
    private class DownloadFileTask extends AsyncTask<Object, Integer, String> {
        /**
         * ???????????????
         */
        private String urlString;
        /**
         * ???????????????????????????????????????
         */
        private String localPath;
        /**
         * ??????????????????????????????
         */
        private String methodStr;
        /**
         * ??????????????????????????????
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

            // ?????????????????????===============================================
            if (checkNet(needShowAlert)) {
                if (needShowAlert) {
                    Message msg = basePageHandler.obtainMessage(11, getString(R.string.alert_data_download_file_message));
                    basePageHandler.sendMessage(msg);
                }

                // ????????????????????????
                int readTimeout = 3000;
                // ????????????????????????
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
            // ?????????????????????===============================================

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
            sb.append(line + "\n");// ?????????????????????????????????
            line = br.readLine();// ????????????????????????????????????
        }
        br.close();// ??????BufferedReader??????
        reader.close();// ??????Reader??????
        return sb.toString();
    }

    /**
     * ???????????????????????????
     *
     * @param verInfo {@code VerInfo} ????????????
     */
    public void makeUpdateDialog(VerInfo verInfo) {
        String title = getString(R.string.findUpdate) + " v" + verInfo.getVername();
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(title);
        dlgBuilder.setIcon(R.drawable.menu_update);
        if (CommonUtil.checkNB(verInfo.getVercontent().replaceAll("???", ""))) {
            dlgBuilder.setMessage(getString(R.string.ins_column_desc) + "???" + "\n"
                    + verInfo.getVercontent().replaceAll("???", "\n"));
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

        // ????????????
        Button updateBtn = updateDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // ????????????
        Button cancelBtn = updateDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        updateBtn.setTag(verInfo);
        updateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                VerInfo verInfo = (VerInfo) v.getTag();
                updateDlg.cancel();
                String url = verInfo.getUrl();
                Log.d("##", "??????" + url);

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
     * ???????????????????????????
     *
     * @param url       {@code String} ???????????????
     * @param methodStr {@code String} ??????????????????????????????
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
     * ????????????App??? AsyncTask ???
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

            // ?????????????????????
            String fileUrl = (String) params[0];
            methodStr = (String) params[1];
            // ????????????????????????
            String saveFileName = Calendar.getInstance().getTimeInMillis() + fileUrl.substring(fileUrl.lastIndexOf("."));
            saveFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/update/" + saveFileName;

            // ???????????????======================================
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
                //??????????????????
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
            // ???????????????======================================

            // ??????
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
                show("????????????");

                updateProgressDlg.setProgress(0);
                updateProgressDlg.dismiss();
            }
        }
    }

    /**
     * ????????????
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
     * ?????????????????????????????????Android P???
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
     * ??????????????????
     */
    public void checkBluetooth() {
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter == null) {
            // ???????????????
            show("???????????????");
        } else {
            // ????????????
            show("????????????");
            if (bAdapter.isEnabled()) {
                // ?????????????????????
                show("?????????????????????");
            } else {
                // ?????????????????????
                show("?????????????????????");
                makeOpenBluetoothDialog();
            }
        }
    }

    /**
     * ?????????????????????????????????
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
     * ????????????????????????
     */
    public void checkLogoutDialog() {
        checkLogoutDialog(true);
    }

    /**
     * ????????????????????????
     *
     * @param gotoLoginPageFlag {@code boolean} ??????????????????????????????
     */
    public void checkLogoutDialog(boolean gotoLoginPageFlag) {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.whetherLogout);
        dlgBuilder.setIcon(R.drawable.ic_dialog_info_blue_v);
        if (gotoLoginPageFlag) {
            // ????????????????????????
            dlgBuilder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout(true);
                }
            });
        } else {
            // ???????????????????????????
            dlgBuilder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // ??????????????????
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
     * ????????????
     *
     * @param gotoLoginPageFlag {@code boolean} ??????????????????????????????
     */
    public void logout(boolean gotoLoginPageFlag) {
        // ??????????????????
        makeLogout(false, gotoLoginPageFlag);
    }

    /**
     * ??????????????????
     *
     * @param needAlert         {@code boolean} ??????????????????????????????
     * @param gotoLoginPageFlag {@code boolean} ??????????????????????????????
     */
    public void makeLogout(boolean needAlert, boolean gotoLoginPageFlag) {
        makeLogout(needAlert, gotoLoginPageFlag, CommonParam.COMMANDCODE_NONE);
    }

    /**
     * ??????????????????
     *
     * @param needAlert         {@code boolean} ??????????????????????????????
     * @param gotoLoginPageFlag {@code boolean} ??????????????????????????????
     * @param commandCode       {@code commandCode} ??????????????????
     */
    public void makeLogout(boolean needAlert, boolean gotoLoginPageFlag, int commandCode) {
        // ??????Map
        HashMap<String, Object> restfullParamMap = new HashMap<String, Object>();
        restfullParamMap.put("method", "query_user_tags");
        // ????????????Bundle
        Bundle data = new Bundle();
        // ???????????????
        data.putInt("commandCode", commandCode);
        // ????????????Tags
        new MakeLogoutTask().execute(restfullParamMap, needAlert, gotoLoginPageFlag, data);
    }

    /**
     * ??????????????????
     *
     * @param needAlert         {@code boolean} ??????????????????????????????
     * @param gotoLoginPageFlag {@code boolean} ??????????????????????????????
     * @param commandCode       {@code commandCode} ??????????????????
     */
    public void makeLogout_old(boolean needAlert, boolean gotoLoginPageFlag, int commandCode) {
        // ??????Map
        HashMap<String, Object> restfullParamMap = new HashMap<String, Object>();
        restfullParamMap.put("method", "query_user_tags");
        // ????????????Bundle
        Bundle data = new Bundle();
        // ???????????????
        data.putInt("commandCode", commandCode);
        // ????????????Tags
        new RestfullDelTagsTask().execute(restfullParamMap, needAlert, gotoLoginPageFlag, data);
    }

    /**
     * ???????????????????????????
     *
     * @param dataset   {@code Map<String, Object>} ?????????
     * @param tableName {@code String} ??????
     * @return {@code boolean} ????????????
     */
    public boolean insertToTable(Map<String, Object> dataset, String tableName) {
        return insertToTable(dataset, tableName, true);
    }

    /**
     * ???????????????????????????
     *
     * @param dataset         {@code Map<String, Object>} ?????????
     * @param tableName       {@code String} ??????
     * @param deleteFirstFlag {@code boolean} ???????????????????????????????????????
     * @return {@code boolean} ????????????
     */
    public boolean insertToTable(Map<String, Object> dataset, String tableName, boolean deleteFirstFlag) {
        // ????????????
        boolean resultFlag = true;
        db = getDb();
        infoTool = getInfoTool();
        try {
            if (((Boolean) dataset.get("dataValidFlag"))) {
                // ????????????List
                List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) dataset.get("data");
                if (deleteFirstFlag) {
                    // ???????????????
                    db.delete(tableName, null, null);
                }
                if (data.size() > 0) {
                    // ????????????
                    for (HashMap<String, Object> record : data) {
                        // ???????????????
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
     * ??????????????????
     *
     * @param filename {@code String} ???????????????
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
     * ??????????????????????????????
     *
     * @param noteId   {@code String} note id
     * @param filename {@code String} ???????????????
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
     * ??????????????????
     *
     * @param filename {@code String} ???????????????
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
     * ??????????????????????????????????????????????????????????????????
     *
     * @return {@code MediaPlayer} ???????????????
     */
    public MediaPlayer getMediaplayer() {
        if (mediaPlayer == null) {
            mediaPlayer = getMediaplayer(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/voice/blank.wav");
        }
        return mediaPlayer;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param filePath {@code String} ???????????????
     * @return {@code MediaPlayer} ???????????????
     */
    public MediaPlayer getMediaplayer(String filePath) {
        // ?????????????????????????????????
        boolean newFlag = false;
        // ?????????????????????????????????
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
            // ??????
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
     * ???????????????
     *
     * @param cardMac {@code String} RFID??????MAC
     */
    public void readCard_pre(String cardMac) {

    }

    /**
     * ???????????????_??????
     *
     * @param cardMac {@code String} ?????????RFID??????MAC
     */
    public void readCard_ins_onBefore(String cardMac) {
        readCard_ins(cardMac);
    }

    /**
     * ???????????????
     *
     * @param cardMac {@code String} ?????????RFID??????MAC
     */
    public void readCard_ins(String cardMac) {
        readCard_ins(cardMac, null);
    }

    /**
     * ???????????????
     *
     * @param cardMac {@code String} ?????????RFID??????MAC
     * @param infoId  {@code String} ???????????????
     */
    public void readCard_ins(String cardMac, String infoId) {

    }


    /**
     * ????????????????????????
     *
     * @param cardMac {@code String} RFID??????MAC
     */
    public void readExtraCard(String cardMac) {

    }

    /**
     * ?????????????????????
     */
    public void makeWaitDialog() {
        makeWaitDialog(getString(R.string.alert_waiting));
    }

    /**
     * ?????????????????????
     *
     * @param resId {@code int} ??????id
     */
    public void makeWaitDialog(int resId) {
        makeWaitDialog(getString(resId));
    }

    /**
     * ?????????????????????
     *
     * @param waitMsg {@code String} ???????????????
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
     * ???????????????????????????
     */
    public void unWait() {
        if (waitDlg != null && waitDlg.isShowing()) {
            waitDlg.setProgress(waitDlg.getMax());
            waitDlg.dismiss();
        }
    }

    /**
     * ????????????????????????
     */
    public void loading() {
        loading(null);
    }

    /**
     * ????????????????????????
     *
     * @param waitMsg {@code String} ???????????????
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
     * ??????????????????????????????
     */
    public void unLoading() {
        if (loadingWindow != null && loadingWindow.isShowing()) {
            loadingWindow.dismiss();
        }
    }

    /**
     * ??????title
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
     * ??????title
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
     * ??????????????? AsyncTask ???
     */
    public class ResetDBTask extends AsyncTask<Object, Integer, String> {
        /**
         * ?????????????????????????????????
         */
        private static final int PROGRESS_COPY_FILE = 1001;
        /**
         * ??????????????????????????????
         */
        private static final int PROGRESS_CHANGE_DB = 1002;
        /**
         * ??????????????????????????????
         */
        private static final int PROGRESS_RESET_FIELD_DATA = 1003;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            if (db != null && db.isOpen()) {
                // ?????????????????????
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
            // ??????????????????????????????
            FileUtil.copyDefaultDB(DbActivity.this, true);
            // ?????????????????????
            publishProgress(PROGRESS_CHANGE_DB);
            // ??????????????????
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
                // ??????????????????
                makeWaitDialog(R.string.alert_wait_copy_file);
            } else if (progress[0] == PROGRESS_CHANGE_DB) {
                makeWaitDialog(R.string.alert_wait_resetDB);
                // ?????????????????????
                changeDb();
            } else if (progress[0] == PROGRESS_RESET_FIELD_DATA) {
                makeWaitDialog(R.string.alert_wait_reset_field_data);
                // ??????????????????
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
            // ??????????????????
            unWait();
            // ??????????????????
            show(R.string.resetDBOk);
        }

    }

    /**
     * ?????????????????????
     */
    public void makeAlertDialog() {
        makeAlertDialog("");
    }

    /**
     * ?????????????????????
     *
     * @param resId {@code int} ??????id
     */
    public void makeAlertDialog(int resId) {
        makeAlertDialog(getString(resId));
    }

    /**
     * ?????????????????????
     *
     * @param msg {@code String} ???????????????
     */
    public void makeAlertDialog(String msg) {
        makeAlertDialog(getString(R.string.alert_ts), msg);
    }

    /**
     * ?????????????????????
     *
     * @param title {@code String} ??????
     * @param msg   {@code String} ???????????????
     */
    public void makeAlertDialog(String title, String msg) {
        // ?????????????????????
        int readCardType_old = readCardType;
        // ????????????????????????
        readCardType = CommonParam.READ_CARD_TYPE_NO_ACTION;
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(title);
        dlgBuilder.setMessage(msg);
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ????????????
                Button confirmBtn = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                // ?????????????????????
                int readCardType_old = (Integer) confirmBtn.getTag();
                // ??????????????????????????????
                readCardType = readCardType_old;
            }
        });
        dlgBuilder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // ????????????
                Button confirmBtn = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                // ?????????????????????
                int readCardType_old = (Integer) confirmBtn.getTag();
                // ??????????????????????????????
                readCardType = readCardType_old;
            }
        });
        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // ???????????????????????????======================================================
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
        // ???????????????????????????======================================================

        // ????????????
        Button confirmBtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmBtn.setTag(readCardType_old);
    }

    /**
     * ??????????????????
     *
     * @param filename {@code String} ????????????
     * @param filepath {@code String} ????????????
     */
    public void openPicByFilename(String filename, String filepath) {
        openPicByFilename(filename, filepath, null, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * ??????????????????
     *
     * @param filename   {@code String} ????????????
     * @param filepath   {@code String} ????????????
     * @param infoBundle {@code Bundle} ????????????
     */
    public void openPicByFilename(String filename, String filepath, Bundle infoBundle) {
        openPicByFilename(filename, filepath, infoBundle, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * ??????????????????
     *
     * @param filename          {@code String} ????????????
     * @param filepath          {@code String} ????????????
     * @param screenOrientation {@code int} ????????????
     */
    public void openPicByFilename(String filename, String filepath, int screenOrientation) {
        openPicByFilename(filename, filepath, null, screenOrientation);
    }

    /**
     * ??????????????????
     *
     * @param filename          {@code String} ????????????
     * @param filepath          {@code String} ????????????
     * @param infoBundle        {@code Bundle} ????????????
     * @param screenOrientation {@code int} ????????????
     */
    public void openPicByFilename(String filename, String filepath, Bundle infoBundle, int screenOrientation) {
        if (CommonUtil.checkNB(filename) && CommonUtil.checkNB(filepath)) {
            File file = new File(filepath);
            if (file.exists()) {
                // ??????????????????Bundle
                Bundle data = new Bundle();
                data.putString("title", filename);
                data.putString("filepath", filepath);
                if (infoBundle != null) {
                    data.putBundle("infoBundle", infoBundle);
                }
                // ???????????? ShowImageActivity ???Intent
                Intent intent = null;
                if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    intent = new Intent(this, ShowImageLandActivity.class);
                } else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    intent = new Intent(this, ShowImageReverseLandActivity.class);
                } else {
                    intent = new Intent(this, ShowImageActivity.class);
                }
                // ??????????????? Intent ???
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            } else {
                show("?????????????????????");
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param filename {@code String} ????????????
     * @param filepath {@code String} ????????????
     */
    public void openVideoByFilename(String filename, String filepath) {
        openVideoByFilename(filename, filepath, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * ??????????????????
     *
     * @param filename          {@code String} ????????????
     * @param filepath          {@code String} ????????????
     * @param screenOrientation {@code int} ????????????
     */
    public void openVideoByFilename(String filename, String filepath, int screenOrientation) {
        if (CommonUtil.checkNB(filename) && CommonUtil.checkNB(filepath)) {
            File file = new File(filepath);
            if (file.exists()) {
                // ??????????????????Bundle
                Bundle data = new Bundle();
                data.putString("title", filename);
                data.putString("filepath", filepath);
                // ???????????? ShowVideoActivity ???Intent
                Intent intent = null;
                if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    intent = new Intent(this, ShowVideoLandActivity.class);
                } else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    intent = new Intent(this, ShowVideoReverseLandActivity.class);
                } else {
                    intent = new Intent(this, ShowVideoActivity.class);
                }
                // ??????????????? Intent ???
                intent.putExtras(data);
                this.startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out);
            } else {
                show("?????????????????????");
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    public void setDateTime() {
        startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
    }

    /**
     * ????????????????????????
     */
    public void setUpShortCut() {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        // ????????????????????????
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));

        // ????????????????????????
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

        // ?????????????????????????????????????????? false???????????????
        intent.putExtra("duplicate", true);

        // ????????????????????????????????????intent
        Intent targetIntent = new Intent(this, SplashActivity.class);

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, targetIntent);

        // ????????????
        sendBroadcast(intent);
    }

    /**
     * ?????????????????????
     *
     * @param context {@code Context} ?????????
     * @return {@code View} ????????????View
     */
    public View makeColumnSpitter(Context context) {
        View splitter = new View(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        splitter.setLayoutParams(lp);
        splitter.setBackgroundColor(context.getResources().getColor(R.color.table_border_color));
        return splitter;
    }

    /**
     * ?????????????????? AsyncTask ???
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
                    // ??????????????????????????????Map
                    Map<String, Object> tableMap = (HashMap<String, Object>) params[index];
                    // ????????????
                    String actionName = CommonUtil.N2B((String) tableMap.get("actionName"));
                    // ??????
                    String tableName = (String) tableMap.get("tableName");
                    // ????????????
                    String keyColumn = (String) tableMap.get("keyColumn");
                    // ????????????
                    String keyValue = (String) tableMap.get("keyValue");
                    // ?????????
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
     * ??????????????????
     *
     * @param imageId {@code int} ????????????id
     */
    public void addGuideImage(int imageId) {
        if (parentFrameLayout == null) {
            // ????????????setContentView???????????????
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
     * ?????????????????????
     *
     * @param imageId {@code int} ????????????id
     */
    public void addSmallGuideImage(int imageId) {
        if (parentFrameLayout == null) {
            // ????????????setContentView???????????????
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
     * ????????????????????????
     *
     * @param columnName  {@code String} ?????????
     * @param columnValue {@code String} ?????????
     */
    public void updateSysconfigValue(String columnName, String columnValue) {
        infoTool = getInfoTool();
        // ?????????
        ContentValues cv = new ContentValues();
        // ???????????????
        String tableName = "sysconfig";
        cv.put(CommonParam.SYSCONFIG_COLUMN_VALUE, columnValue);
        if (infoTool.getCount("select count(model." + CommonParam.SYSCONFIG_COLUMN_NAME
                        + ") from \"sysconfig\" model where model." + CommonParam.SYSCONFIG_COLUMN_NAME + "=?",
                new String[]{columnName}) == 0) {
            // ??????????????????????????????
            cv.put(CommonParam.SYSCONFIG_COLUMN_NAME, columnName);
            infoTool.insert(tableName, cv);
        } else {
            // ??????????????????????????????
            infoTool.update(tableName, CommonParam.SYSCONFIG_COLUMN_NAME, columnName, cv);
        }

    }

    /**
     * ????????????????????????
     *
     * @param columnName {@code String} ?????????
     */
    public String getSysconfigValue(String columnName) {
        infoTool = getInfoTool();
        return infoTool.getSingleVal("select model." + CommonParam.SYSCONFIG_COLUMN_VALUE
                        + " from \"sysconfig\" model where model." + CommonParam.SYSCONFIG_COLUMN_NAME + "=?",
                new String[]{columnName});

    }

    /**
     * ????????????overflow??????
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
     * ????????????PopupMenu??????
     */
    public void forceShowPopupMenuIcon(PopupMenu popup) {
        forceShowPopupMenuIcon(popup, true);
    }

    /**
     * ????????????PopupMenu??????
     *
     * @param flag {@code boolean} ??????????????????
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
     * ????????? AsyncTask ???
     */
    public class MakeLogoutTask extends AsyncTask<Object, Integer, String> {
        /**
         * ??????????????????????????????
         */
        private boolean needAlert;
        /**
         * ??????????????????????????????
         */
        private boolean gotoLoginPageFlag;

        /**
         * ????????????
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
                // ????????????
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
                // ?????????????????????
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    /**
     * ??????Restfull????????? AsyncTask ???
     */
    public class RestfullDelTagsTask extends AsyncTask<Object, Integer, String> {
        /**
         * Restfull ????????????
         */
        private static final String REST_URL = "http://channel.api.duapp.com/rest/2.0/channel/channel";

        private static final String HTTP_METHOD = "POST";
        /**
         * ??????????????????????????????
         */
        private boolean needAlert;
        /**
         * ??????????????????????????????
         */
        private boolean gotoLoginPageFlag;

        /**
         * ????????????
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

            // ??????Map
            HashMap<String, Object> restfullParamMap = (HashMap<String, Object>) params[0];
            needAlert = (Boolean) params[1];
            gotoLoginPageFlag = (Boolean) params[2];
            if (params.length >= 4) {
                // ????????????
                attatchData = (Bundle) params[3];
            }

            // ?????????
            String method = (String) restfullParamMap.get("method");
            // ????????????
            String apiKey = PNUtil.getMetaValue(DbActivity.this, PNUtil.API_KEY_NAME);
            // ????????????????????????unix?????????
            Long timestamp = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA)
                    .getTimeInMillis();

            if (!CommonUtil.checkNB(method)) {
                return result;
            }
            // ???????????????????????????=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // ???????????????????????????=================
            try {
                String signStr = HTTP_METHOD + REST_URL;
                String url = REST_URL;

                // ??????post????????????=========================
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
                // ??????post????????????=========================

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
                        // ????????????????????????
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
                // ?????????????????????
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    /**
     * ??????Restfull????????? AsyncTask ???
     */
    public class RestfullListTask extends AsyncTask<Object, Integer, String> {
        /**
         * Restfull ????????????
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

            // ??????Map
            HashMap<String, Object> restfullParamMap = (HashMap<String, Object>) params[0];

            // ?????????
            String method = (String) restfullParamMap.get("method");
            // ????????????
            String apiKey = PNUtil.getMetaValue(DbActivity.this, PNUtil.API_KEY_NAME);
            // ????????????????????????unix?????????
            Long timestamp = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA)
                    .getTimeInMillis();

            if (!CommonUtil.checkNB(method)) {
                return result;
            }
            // ???????????????????????????=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // ???????????????????????????=================
            try {
                String signStr = HTTP_METHOD + REST_URL;
                String url = REST_URL;

                // ??????post????????????=========================
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
                // ??????post????????????=========================

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
                        // ????????????????????????
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
     * ??????
     */
    public void goBack() {
        // ???????????? Activity ??? Intent
        // Intent intent = new Intent(DbActivity.this, ExerciseMainActivity.class);
        // ????????????Bundle
        // Bundle data = new Bundle();
        // ???????????????Intent???
        // intent.putExtras(data);
        // startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.activity_slide_right_out);
    }

    /**
     * ?????????????????????
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
     * ??????????????????url??????????????? AsyncTask
     */
    public class GetUrlInfoTask extends AsyncTask<String, Integer, String> {
        /**
         * ????????????????????????
         */
        private String waitFlag = "1";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String result = CommonParam.RESULT_ERROR;
            // ????????????????????????
            waitFlag = params[1];
            // ????????????????????????
            String respStr = "";
            // ???????????????????????????=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // ???????????????????????????=================

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
                    // ????????????
                    byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                    // ???????????????xml??????BOM?????????????????????
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
            // ??????????????????
            unWait();
        }
    }

    public void locationMethod(BDLocation location) {

    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param tableName   {@code String} ??????
     * @param queryParams {@code Map<String, Object>} ????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> serverTbToLocalTb(String tableName, Map<String, Object> queryParams) throws Exception {
        return serverTbToLocalTb(tableName, queryParams, tableName, true);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param remoteTbName {@code String} ????????????
     * @param remoteParams {@codeMap<String, Object>} ??????????????????
     * @param localTbName  {@code String} ????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> serverTbToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                 String localTbName) throws Exception {
        return serverTbToLocalTb(remoteTbName, remoteParams, localTbName, true);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param remoteTbName    {@code String} ????????????
     * @param remoteParams    {@code Map<String, Object>} ??????????????????
     * @param localTbName     {@code String} ????????????
     * @param deleteFirstFlag {@code boolean} ???????????????????????????????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> serverTbToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                 String localTbName, boolean deleteFirstFlag) throws Exception {
        // ?????????
        Map<String, Object> dataset = null;
        // ????????????????????????
        boolean dataValidFlag = false;
        // ????????????????????????
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
        Log.d("???????????????", localTbName);

        return dataset;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     * <p>??????????????????????????????</p>
     *
     * @param tableName   {@code String} ??????
     * @param queryParams {@code Map<String, Object>} ????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> serverTbFileToLocalTb(String tableName, Map<String, Object> queryParams) throws Exception {
        return serverTbFileToLocalTb(tableName, queryParams, tableName, true);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     * <p>??????????????????????????????</p>
     *
     * @param remoteTbName {@code String} ????????????
     * @param remoteParams {@codeMap<String, Object>} ??????????????????
     * @param localTbName  {@code String} ????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> serverTbFileToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                     String localTbName) throws Exception {
        return serverTbFileToLocalTb(remoteTbName, remoteParams, localTbName, true);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     * <p>??????????????????????????????</p>
     *
     * @param remoteTbName    {@code String} ????????????
     * @param remoteParams    {@code Map<String, Object>} ??????????????????
     * @param localTbName     {@code String} ????????????
     * @param deleteFirstFlag {@code boolean} ???????????????????????????????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> serverTbFileToLocalTb(String remoteTbName, Map<String, Object> remoteParams,
                                                     String localTbName, boolean deleteFirstFlag) throws Exception {
        // ?????????
        Map<String, Object> dataset = null;
        // ????????????????????????
        boolean dataValidFlag = false;
        // ????????????????????????
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
        Log.d("???????????????", localTbName);

        return dataset;
    }

    /**
     * ????????????????????????
     *
     * @param table  {@code String} ????????????
     * @param params {@code Map<String, Object>} ????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> getTbFromServer_ok(String table, Map<String, Object> params) {
        // ????????????????????????
        boolean dataValidFlag = false;
        // ?????????
        Map<String, Object> dataset = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        // ????????????????????????
        String respStr = "";
        // ???????????????????????????=================
        Request upHttpRequest = null;
        Response upResponse = null;
        OkHttpClient upHttpClient = null;
        // ???????????????????????????=================
        try {
            // ?????????????????????======================================
            // ?????????????????????======================================

            // ??????post????????????=========================
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                    .addFormDataPart("infoType", table);
            for (Entry<String, Object> e : params.entrySet()) {
                multipartBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
            }
            RequestBody requestBody = multipartBuilder.build();
            // ??????post????????????=========================

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
                // ????????????
                byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                // ???????????????xml??????BOM?????????????????????
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
                    // ????????????
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
     * ????????????????????????
     * <p>??????????????????????????????</p>
     *
     * @param table  {@code String} ????????????
     * @param params {@code Map<String, Object>} ????????????
     * @return {@code Map<String, Object>} ?????????
     */
    public Map<String, Object> getTbFileFromServer_ok(String table, Map<String, Object> params) {
        // ????????????????????????
        boolean dataValidFlag = false;
        // ?????????
        Map<String, Object> dataset = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        // ????????????????????????
        String respStr = "";
        // ???????????????????????????=================
        Request upHttpRequest = null;
        Response upResponse = null;
        OkHttpClient upHttpClient = null;
        // ???????????????????????????=================
        try {
            // ?????????????????????======================================
            // ?????????????????????======================================

            // ??????post????????????=========================
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY)
                    .addFormDataPart("infoType", table)
                    .addFormDataPart("dbSign", CommonParam.YES);
            for (Entry<String, Object> e : params.entrySet()) {
                multipartBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
            }
            RequestBody requestBody = multipartBuilder.build();
            // ??????post????????????=========================

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
                // ????????????
                byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                // ???????????????xml??????BOM?????????????????????
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
                    // ????????????
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
                            // ????????????
                            ArrayList<HashMap<String, Object>> resultInfoList = null;

                            if (downloadDb != null) {
                                downloadInsTool = new InsTool(DbActivity.this, new DbTool(DbActivity.this, downloadDb));

                                try {
                                    // ?????????????????????================================================================
                                    resultInfoList = downloadInsTool.getInfoMapCusList(
                                            "select * from tb model",
                                            new String[]{}, dbColumnArray);
                                    data = resultInfoList;
                                    resultInfoList = null;
                                    // ?????????????????????==============================================================

                                    dataValidFlag = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // ???????????????
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
     * ????????????
     * <p>
     * ???APP?????????????????????????????????????????????????????????
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
     * ????????????
     * <p>????????????????????????????????????????????????????????????????????????</p>
     *
     * @param m     {@code HashMap<String, Object>} ??????Map
     * @param c_new {@code String} ????????????}
     * @param c_old {@code String} ????????????}
     */
    public void pm(HashMap<String, Object> m, String c_new, String c_old) {
        if (!m.containsKey(c_new) && m.containsKey(c_old)) {
            m.put(c_new, m.get(c_old));
            m.remove(c_old);
        }
    }

    /**
     * ?????? FTP ??????????????????????????????
     *
     * @param url          {@code String} ftp??????????????? ?????? 192.168.1.110
     * @param port         {@code int} ????????? ??? 21
     * @param username     {@code String} ?????????
     * @param password     {@code String} ??????
     * @param remotePath   {@code String} ????????????????????????
     * @param fileName     {@code String} ?????????
     * @param fileSavePath {@code String} ?????????????????????????????????
     * @return {@code String} ????????????
     */
    public String ftpDownload(String url, int port, String username, String password, String remotePath,
                              String fileName, String fileSavePath) {
        // ????????????
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
            // ??????????????????
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // ????????????
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                fs = new BufferedOutputStream(new FileOutputStream(fileSavePath));
                boolean flag = ftpClient.retrieveFile(fileName, fs);
                if (flag) {
                    result = CommonParam.RESULT_SUCCESS;
                }
            } else {
                // ????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ftpDownload", "FTP??????????????????");
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
                Log.d("ftpDownload", "FTP????????????????????????");
            }
        }

        return result;
    }

    /**
     * ?????? FTP ????????????????????????????????????
     *
     * @param url           {@code String} ftp??????????????? ?????? 192.168.1.110
     * @param port          {@code int} ????????? ??? 21
     * @param username      {@code String} ?????????
     * @param password      {@code String} ??????
     * @param remotePath    {@code String} ????????????????????????
     * @param fileNames     {@code String[]} ?????????
     * @param fileSavePaths {@code String[]} ?????????????????????????????????
     * @return {@code Map<String, Object>} ????????????
     */
    public Map<String, Object> ftpDownload(String url, int port, String username, String password, String remotePath,
                                           String[] fileNames, String[] fileSavePaths) {
        return ftpDownload(url, port, username, password, remotePath, fileNames, fileSavePaths, false);
    }

    /**
     * ?????? FTP ????????????????????????????????????
     *
     * @param url               {@code String} ftp??????????????? ?????? 192.168.1.110
     * @param port              {@code int} ????????? ??? 21
     * @param username          {@code String} ?????????
     * @param password          {@code String} ??????
     * @param remotePath        {@code String} ????????????????????????
     * @param fileNames         {@code String[]} ?????????
     * @param fileSavePaths     {@code String[]} ?????????????????????????????????
     * @param errorContinueFlag {@code boolean} ?????????????????????????????????
     * @return {@code Map<String, Object>} ????????????
     */
    public Map<String, Object> ftpDownload(String url, int port, String username, String password, String remotePath,
                                           String[] fileNames, String[] fileSavePaths, boolean errorContinueFlag) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // ????????????
        String result = CommonParam.RESULT_SUCCESS;
        // ????????????
        int total = fileNames.length;
        // ?????????
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
            // ??????????????????
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // ????????????
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                for (int i = 0, len = fileNames.length; i < len; i++) {
                    try {
                        fs = new BufferedOutputStream(new FileOutputStream(fileSavePaths[i]));
                        Log.d("#??????", fileSavePaths[i]);
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
                // ????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = CommonParam.RESULT_ERROR;
            Log.d("ftpDownload", "FTP??????????????????");
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ftpDownload", "FTP????????????????????????");
            }
        }

        resultMap.put("result", result);
        resultMap.put("total", total);
        resultMap.put("done", done);
        return resultMap;
    }

    /**
     * ?????? FTP ????????????
     *
     * @param url        {@code String} ftp??????????????? ?????? 192.168.1.110
     * @param port       {@code int} ????????? ??? 21
     * @param username   {@code String} ?????????
     * @param password   {@code String} ??????
     * @param remotePath {@code String} ????????????????????????????????????
     * @param fileName   {@code String} ?????????
     * @param fileUpPath {@code String} ?????????????????????????????????
     * @return {@code String} ????????????
     */
    public String ftpUpload(String url, int port, String username, String password, String remotePath, String fileName,
                            String fileUpPath) {
        // ????????????
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
            // ??????????????????
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // ????????????
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
                // ????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ftpDownload", "FTP??????????????????");
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
                Log.d("ftpDownload", "FTP????????????????????????");
            }
        }

        return result;
    }

    /**
     * ?????? FTP ??????????????????
     *
     * @param url         {@code String} ftp??????????????? ?????? 192.168.1.110
     * @param port        {@code int} ????????? ??? 21
     * @param username    {@code String} ?????????
     * @param password    {@code String} ??????
     * @param remotePath  {@code String} ????????????????????????????????????
     * @param fileNames   {@code String[]} ?????????
     * @param fileUpPaths {@code String[]} ?????????????????????????????????
     * @return {@code String} ????????????
     */
    public String ftpUpload(String url, int port, String username, String password, String remotePath,
                            String[] fileNames, String[] fileUpPaths) {
        // ????????????
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
            // ??????????????????
            boolean loginResult = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (loginResult && FTPReply.isPositiveCompletion(replyCode)) {
                // ????????????
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();

                for (int i = 0, len = fileNames.length; i < len; i++) {
                    try {
                        fi = new BufferedInputStream(new FileInputStream(fileUpPaths[i]));
                        Log.d("#??????", fileUpPaths[i]);
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
                // ????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = CommonParam.RESULT_ERROR;
            Log.d("ftpDownload", "FTP??????????????????");
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ftpDownload", "FTP????????????????????????");
            }
        }

        return result;
    }

    /**
     * ????????????????????????????????????
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
     * ?????????????????? AsyncTask ???
     */
    public class UploadTestDataTask extends AsyncTask<Object, Integer, String> {
        /**
         * FTP??????????????????
         */
        public static final String FTP_USERNAME = "ftpuser";
        /**
         * FTP???????????????
         */
        public static final String FTP_PASSWORD = "Isdn1603!@#";
        /**
         * FTP???????????????
         */
        public static final int FTP_PORT = 21;
        /**
         * FTP????????????????????????????????????
         */
        public static final String FTP_REMOTE_UPFILE = "q";

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // ??????????????????
            makeWaitDialog("???????????????????????????");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // ???????????????????????????=================
            try {
                // ?????????????????????======================================
                // ?????????????????????======================================
                String fileSavePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/db/sys.db").getAbsolutePath();
                // Log.d("###", fileSavePath);
                // ????????????
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
            // ??????????????????
            unWait();
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                show("???????????????");
            } else {
                show("???????????????");
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void makeClearCacheDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.clearCacheAsk);
        dlgBuilder.setIcon(R.drawable.ic_dialog_broom_v);
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ????????????
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
     * ??????????????? AsyncTask ???
     */
    public class ClearCacheTask extends AsyncTask<Object, Integer, String> {
        /**
         * ???????????????????????????
         */
        private static final int PROGRESS_CLEAR_CACHE = 1001;

        /**
         * ????????????
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
            // ???????????????????????????=================================
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
            // ???????????????????????????=================================

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
            // ??????????????????
            unWait();
            // ??????????????????
            show(getString(R.string.clearCacheDetail, FileUtil.getFileSizeStatic((double) fileLength)));
        }
    }

    /**
     * ??????????????????????????????APP
     *
     * @param packageName {@code String} APP??????
     * @return {@code boolean} ????????????
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
     * ?????????????????????
     *
     * @param layoutRes {@code int} ??????????????????
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

    // UHF?????????????????????============================================

    /**
     * ??????UHF????????????
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
            // ????????????
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
    // UHF?????????????????????============================================

}
