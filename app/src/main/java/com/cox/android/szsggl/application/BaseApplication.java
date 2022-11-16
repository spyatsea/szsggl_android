package com.cox.android.szsggl.application;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.tool.DbTool;
import com.cox.android.szsggl.tool.InfoTool;
import com.cox.android.uhf.Reader;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.rfid.PowerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 存放变量的Application
 */
public class BaseApplication extends Application {
    private static BaseApplication mApplication = null;
    // 授权Key
    // 发布key
    // public static final String mStrKey = "GyjGGwpsh0Vpt6sTmY2ZOcnU";
    // 测试key
    // public static final String mStrKey = "mus21wgdxBS9bKIG28QbuQ5V";
    public String attaFileName = "";
    /**
     * 闪屏页面的进度条是否已经结束
     */
    private boolean splashProgressOver = false;
    /**
     * 当前地址
     * <p>
     * 由百度反查出
     */
    private String currentAddress;
    /**
     * 临时View
     */
    private View tempView;

    // 消息服务相关参数。开始================================================
    public String PUSH_USER_ID;
    // 消息服务相关参数。结束================================================

    // 统计相关参数。开始==========================================================

    // 统计相关参数。结束==========================================================
    // 图片缓存相关参数。开始================================================
    /**
     * 图片缓存目录
     */
    public File cacheDir;

    // 图片缓存相关参数。结束================================================

    // 登录信息。开始================================================
    // 是否已经登录
    public boolean isLogged;
    // 是否记住密码
    public boolean rememberFlag;
    // 已登录用户
    public HashMap<String, Object> loginUser;
    // 是否第一次打开APP
    public boolean isFirstOpenApp;
    // 是否自动下载附件
    public boolean isAutoDownloadAtta;
    // 是否自动播放巡检语音提示
    public boolean isAutoPlayInsAudio;
    // 是否第一次打开巡视界面
    public boolean needAlert;
    // UHF扫卡快签信息保存间隔
    public int autoDkOverTime;
    // UHF扫卡快签信息保留时间
    public int autoDkStayTime;
    // 是否反向旋转屏幕
    public boolean isReverseRotate;
    // 登录信息。结束================================================

    // 程序参数。开始================================================
    public SharedPreferences preferences = null;
    public SharedPreferences.Editor preferEditor = null;

    public String serverAddr;
    public String serverAddr_in;
    public String serverAddr_out;
    public boolean checkUpdateFlag = false;
    public Integer remainMessageNum;
    // 版本号
    public String versionCode;
    // 版本号数值
    public Integer versionCodeInt;
    // 版本名称
    public String versionName;
    /**
     * 数据库
     */
    SQLiteDatabase db = null;
    /**
     * 信息工具类
     */
    InfoTool infoTool;
    /**
     * 数据库类
     */
    DbTool dbTool;
    // 程序参数。结束================================================

    /**
     * 发布渠道，用来区分是从哪个渠道下载的app
     */
    public String publish_channel = CommonParam.PUBLISH_CHANNEL_1;

    /**
     * 临时计数器
     */
    public int tmpNum = 0;
    // 百度推送相关参数。开始============================================
    /**
     * 接收Notification标志
     */
    private boolean receiveNofifyFlag = true;
    /**
     * 百度推送的channel_id
     */
    private String channelId;

    // 百度推送相关参数。开始============================================

    // UHF相关参数。开始============================================
    /**
     * 设备型号
     */
    public String uhfModel;
    /**
     * 是否是 UHF PDA
     */
    public boolean isUhfPda;
    /**
     * UHF PDA信息
     */
    public Map<String, Object> uhfPdaInfo;
    /**
     * UHF功率
     */
    public int uhfPower;
    /**
     * UHF开关按键代码
     */
    public int uhfKeyCode;
    // UHF相关参数。结束============================================

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        cacheDir = StorageUtils.getOwnCacheDirectory(this, CommonParam.PROJECT_NAME + "/cache");
        initImageLoader(getApplicationContext());

        initPreferences();

        try {
            dbTool = getDbTool();
        } catch (Exception e) {
        }

        // 获得设备信息
        getDeviceInfo();
    }

    /**
     * 初始化Android-Universal-Image-Loader
     * <p>
     * String imageUri = "http://site.com/image.png"; // from Web<br/>
     * String imageUri = "file:///mnt/sdcard/image.png" // from SD card String imageUri = "file:///mnt/sdcard/video.mp4"
     * // from SD card (video thumbnail) String imageUri = "content://media/external/audio/albumart/13"; // from content
     * provider<br/>
     * String imageUri = "content://media/external/video/media/13" // from content provider (video thumbnail) String
     * imageUri = "assets://image.png"; // from assets<br/>
     * String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     */
    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.transparent_pic) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.transparent_pic) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.transparent_pic) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                // .displayer(new RoundedBitmapDisplayer(30))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(720, 800)
                // max width, max height，即保存的每个缓存文件的最大长宽
                // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it
                // carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                // .memoryCacheSize(2 * 1024 * 1024)
                // .diskCacheSize(50 * 1024 * 1024)
                // .diskCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5 加密
                // .diskCacheFileCount(300)
                // 缓存的文件数量
                .diskCache(new UnlimitedDiskCache(cacheDir))
                // 自定义缓存路径
                .defaultDisplayImageOptions(options)
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s),
                // readTimeout (30 s)超时时间
                .imageDecoder(new BaseImageDecoder(true)).writeDebugLogs() // Remove for release app
                .build();// 开始构建
        // Initialize ImageLoader with configuration.
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化设置参数
     */
    @SuppressWarnings({"unchecked"})
    public void initPreferences() {
        // 设置参数。开始==============================================
        // 本程序的SharedPreferences对象
        if (preferences == null) {
            preferences = getSharedPreferences(CommonParam.PROJECT_NAME, MODE_PRIVATE);
            preferEditor = preferences.edit();
        }
        rememberFlag = preferences.getBoolean("rememberFlag", false);
        if (rememberFlag) {
            String userStr = preferences.getString("loginUser", "");
            if (CommonUtil.checkNB(userStr)) {
                JSONObject userObj = JSONObject.parseObject(userStr);
                loginUser = CommonUtil.jsonToMap(userObj);
                isLogged = true;

                PUSH_USER_ID = preferences.getString("PUSH_USER_ID", "");
            } else {
                rememberFlag = false;
                loginUser = null;
                isLogged = false;
            }
        } else {
            loginUser = null;
            isLogged = false;
        }

        serverAddr_in = preferences.getString("SERVER_ADDR_IN", "");
        // 还未写入服务器地址
        if (serverAddr_in.equals("")) {
            // 设置默认服务器地址
            serverAddr_in = getString(R.string.url_upload_default_in);
            // 写入服务器地址
            preferEditor.putString("SERVER_ADDR_IN", serverAddr_in);
            preferEditor.commit();
        }
        serverAddr_out = preferences.getString("SERVER_ADDR_OUT", "");
        // 还未写入服务器地址
        if (serverAddr_out.equals("")) {
            // 设置默认服务器地址
            serverAddr_out = getString(R.string.url_upload_default_out);
            // 写入服务器地址
            preferEditor.putString("SERVER_ADDR_OUT", serverAddr_out);
            preferEditor.commit();
        }
        serverAddr = preferences.getString("SERVER_ADDR", "");
        // 还未写入服务器地址
        if (serverAddr.equals("")) {
            // 设置默认服务器地址
            serverAddr = getString(R.string.url_upload_default);
            // 写入服务器地址
            preferEditor.putString("SERVER_ADDR", serverAddr);
            preferEditor.commit();
        }

        int sysconfig_value_ins_distance = preferences.getInt("SYSCONFIG_VALUE_INS_DISTANCE", -1);
        if (sysconfig_value_ins_distance == -1) {
            sysconfig_value_ins_distance = CommonParam.SYSCONFIG_VALUE_INS_DISTANCE;
            preferEditor.putInt("SYSCONFIG_VALUE_INS_DISTANCE", sysconfig_value_ins_distance);
            preferEditor.commit();
        } else {
            CommonParam.SYSCONFIG_VALUE_INS_DISTANCE = sysconfig_value_ins_distance;
        }

//		remainMessageNum = preferences.getInt("REMAIN_MESSAGE_NUM", 0);
//		// 还未写入服务器地址
//		if (remainMessageNum == 0) {
//			// 设置默认
//			remainMessageNum = CommonParam.REMAIN_MESSAGE_NUM_DEFAULT;
//			// 写入
//			preferEditor.putInt("REMAIN_MESSAGE_NUM", CommonParam.REMAIN_MESSAGE_NUM_DEFAULT);
//			preferEditor.commit();
//		}

        versionCode = getString(R.string.app_versionCode);
        versionCodeInt = Integer.parseInt(versionCode);
        versionName = getString(R.string.app_versionName);

        isFirstOpenApp = preferences.getBoolean("isFirstOpenApp", true);
        if (isFirstOpenApp) {
            preferEditor.putBoolean("isFirstOpenApp", isFirstOpenApp);
            preferEditor.commit();
        }

        isAutoDownloadAtta = preferences.getBoolean("isAutoDownloadAtta", false);
        if (isAutoDownloadAtta) {
            preferEditor.putBoolean("isAutoDownloadAtta", isAutoDownloadAtta);
            preferEditor.commit();
        }

        isAutoPlayInsAudio = preferences.getBoolean("isAutoPlayInsAudio", false);
        if (isAutoPlayInsAudio) {
            preferEditor.putBoolean("isAutoPlayInsAudio", isAutoPlayInsAudio);
            preferEditor.commit();
        }

        isReverseRotate = preferences.getBoolean("isReverseRotate", false);
        if (isReverseRotate) {
            preferEditor.putBoolean("isReverseRotate", isReverseRotate);
            preferEditor.commit();
        }

        uhfPower = preferences.getInt("uhfPower", CommonParam.UHF_ANTENNA_POWER_MAX);
        preferEditor.putInt("uhfPower", uhfPower);
        preferEditor.commit();

        autoDkOverTime = preferences.getInt("autoDkOverTime", CommonParam.UHF_AUTO_DK_OVER_TIME);
        preferEditor.putInt("autoDkOverTime", autoDkOverTime);
        preferEditor.commit();

        autoDkStayTime = preferences.getInt("autoDkStayTime", CommonParam.UHF_AUTO_DK_STAY_TIME);
        preferEditor.putInt("autoDkStayTime", autoDkStayTime);
        preferEditor.commit();

        uhfKeyCode = preferences.getInt("uhfKeyCode", CommonParam.UHF_KEY_CODE_DEFAULT);
        preferEditor.putInt("uhfKeyCode", uhfKeyCode);
        preferEditor.commit();

        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,"api_key")
        // 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
        // if (!PNUtil.hasBind(getApplicationContext()) || (isLogged && !CommonUtil.checkNB(PUSH_USER_ID))) {
        // PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
        // PNUtil.getMetaValue(this, PNUtil.API_KEY_NAME));
        // // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // // PushManager.enableLbs(getApplicationContext());
        // }
        // if (isLogged && loginUser != null) {
        // // 设置push服务的tag
        // PushManager.resumeWork(this);
        // PushManager.setTags(this, Arrays.asList(new String[] { PUSH_USER_ID }));
        // }

        // 开启调试模式
        // PushSettings.enableDebugMode(this, true);
        // -------------
        // isLogged = true;
        // String userStr =
        // "{\"ids\":\"0c43bb1af72f43e69b010e1ebdad4c5b\",\"account\":\"jacky12\",\"password\":\"E04755387E5B5968EC213E41F70C1D46\",\"type\":\"0\",\"realname\":\"乔勇\",\"nickname\":\"乔勇\",\"birthday\":\"\",\"gender\":\"1\",\"address\":\"山西太原\",\"phone\":\"\",\"mobilephone\":\"13834564234\",\"pxbh\":0,\"valid\":\"1\",\"active\":\"1\",\"createdtime\":\"2015-04-16 17:45:51\",\"latestlogintime\":\"2015-05-25 16:50:43\",\"modifiedtime\":\"2015-12-22 03:35:04\",\"loginnum\":96,\"idsn\":\"\",\"totalscore\":0,\"wxid\":\"o6GMjuEhB0-Bb3SHeBrfASH3T2_4\",\"location\":\"山西省太原市\",\"plantFav\":\"0ae8478831fc484b8e146f27e7f45c76,0ae8478831fc484b8e146f27e7f45c76,0ae8478831fc484b8e146f27e7f45c76,0ae8478831fc484b8e146f27e7f45c76,d4755fa5203540d18d1a2ca5fe09d7a7,300f8f3c66224de490ecd28afb29445a\",\"lastAddress\":\"山西省太原市小店区学府街106号\",\"lastLng\":\"112.56884488062\",\"lastLat\":\"37.813561373344\",\"lastLocation\":\"{\\\"distance\\\":\\\"51\\\",\\\"direction\\\":\\\"西\\\",\\\"street\\\":\\\"学府街\\\",\\\"province\\\":\\\"山西省\\\",\\\"street_number\\\":\\\"106号\\\",\\\"district\\\":\\\"小店区\\\",\\\"country_code\\\":0,\\\"country\\\":\\\"中国\\\",\\\"city\\\":\\\"太原市\\\"}\",\"subscribe\":\"1\",\"picture\":\"user2.jpg\",\"dw\":\"11\",\"bm\":\"474\",\"zw\":\"4\",\"zc\":\"8\",\"memo\":\"4\",\"subproStarttime\":\"\",\"subproPasstime\":\"\",\"goodat\":\"44\"}";

        // loginUser = (HashMap<String, Object>) JSONObject.parseObject(userStr, HashMap.class);
        // loginUser = new HashMap<String, Object>();
        // setObject("0c43bb1af72f43e69b010e1ebdad4c5b", "ids");
        // setObject("0c43bb1af72f43e69b010e1ebdad4c5b", "deptId");
        // setObject("20131111", "account");
        // setObject("bcbe3365e6ac95ea2c0343a2395834dd", "password");
        // setObject("A01", "project");
        // setObject("乔勇", "realname");
        // setObject("1", "gender");
        // setObject("山西太原", "address");
        // setObject("1", "valid");
        // setObject("1", "active");
        // setObject("阿乔", "nickname");
        // setObject("山西省太原市小店区学府街106号", "location");
        // setObject(
        // "0ae8478831fc484b8e146f27e7f45c76,0ae8478831fc484b8e146f27e7f45c76,0ae8478831fc484b8e146f27e7f45c76,0ae8478831fc484b8e146f27e7f45c76",
        // "plantfav");
        // setObject("user2.jpg", "picture");
        // -------------
        // 设置参数。结束==============================================
    }

    /**
     * 删除百度推送绑定的Tag
     */
    //public void delBdPushTags() {
    //PushManager.listTags(getApplicationContext());
    //}
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        // MultiDex.install(context);
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }

    public boolean isSplashProgressOver() {
        return splashProgressOver;
    }

    public void setSplashProgressOver(boolean splashProgressOver) {
        this.splashProgressOver = splashProgressOver;
    }

    public View getTempView() {
        return tempView;
    }

    public void setTempView(View tempView) {
        this.tempView = tempView;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public boolean isRememberFlag() {
        return rememberFlag;
    }

    public void setRememberFlag(boolean rememberFlag) {
        this.rememberFlag = rememberFlag;
    }

    public HashMap<String, Object> getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(HashMap<String, Object> loginUser) {
        this.loginUser = loginUser;
    }

    public void setObject(Object obj, String key) {
        loginUser.put(key, obj);
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public boolean getReceiveNofifyFlag() {
        return receiveNofifyFlag;
    }

    public void setReceiveNofifyFlag(boolean receiveNofifyFlag) {
        this.receiveNofifyFlag = receiveNofifyFlag;
    }

    public String getChannelId() {
        if (channelId == null) {
            channelId = "";
        }
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public SQLiteDatabase getDb() {
        if (db == null || !db.isOpen()) {
            db = dbTool.regetDb();
        }
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public void closeDb() {
        if (db != null && db.isOpen()) {
            // 关闭数据库连接
            dbTool.closeDb();
        }

    }

    public DbTool getDbTool() {
        if (dbTool == null) {
            dbTool = new DbTool(this);
        }
        return dbTool;
    }

    public void setDbTool(DbTool dbTool) {
        this.dbTool = dbTool;
    }

    public void setInfoTool(InfoTool infoTool) {
        this.infoTool = infoTool;
    }

    public InfoTool getInfoTool() {
        if (infoTool == null) {
            if (dbTool != null) {
                infoTool = new InfoTool(this, dbTool);
            }
        }
        return infoTool;
    }

    // UHF相关方法。开始============================================

    /**
     * 获得读卡器信息
     */
    public String getDeviceInfo() {
        try {
            uhfModel = SystemProperties.get("ro.product.model");
            if (CommonParam.UHF_MODEL_PDA.equals(uhfModel) && (new File(PowerUtil.s2).exists())) {
                uhfModel = CommonParam.UHF_MODEL_UHFPDA;
            }
            if (CommonParam.UHF_MODEL_UHFPDA.equals(uhfModel)) {
                isUhfPda = true;
            } else {
                isUhfPda = false;
            }
            if (isUhfPda) {
                PowerUtil.power("1");
                connectUHF();
                Reader.rrlib.SetRfPower(uhfPower);
                getUhfInfo();
                uhfPower = (int) uhfPdaInfo.get("power");
                preferEditor.putInt("uhfPower", uhfPower);
                preferEditor.commit();
                disConnectUHF();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uhfModel;
    }

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

        if (isUhfPda) {
            int result = Reader.rrlib.GetUHFInformation(Version, Power, band, MaxFre, MinFre, BeepEn, Ant);
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
        uhfPdaInfo = map;

        return map;
    }

    /**
     * 连接UHF模块
     */
    private void connectUHF() {
        try {
            int result = Reader.rrlib.Connect("/dev/ttyS2", CommonParam.UHF_BAUD);
            if (result == 0) {
            } else {
                String result_hex = Integer.toHexString(result).toUpperCase(Locale.CHINA);
                result_hex = String.format(result_hex.length() > 1 ? "RV_%s" : "RV_0%s", result_hex);
                int result_string_int = CommonUtil.getFieldValue("string", result_hex, this);
                if (result_string_int != -1) {

                } else {

                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 断开UHF模块
     */
    private void disConnectUHF() {
        try {
            if (Reader.rrlib.IsConnected()) {
                int result = Reader.rrlib.DisConnect();
                if (result == 0) {

                } else {
                    String result_hex = Integer.toHexString(result).toUpperCase(Locale.CHINA);
                    result_hex = String.format(result_hex.length() > 1 ? "RV_%s" : "RV_0%s", result_hex);
                    int result_string_int = CommonUtil.getFieldValue("string", result_hex, this);
                    if (result_string_int != -1) {

                    } else {

                    }
                }
            }
        } catch (Exception e) {

        }
    }
    // UHF相关方法。结束============================================

    // 权限相关的属性与方法。开始=============================================

    /**
     * 获得APP需要的权限数组
     *
     * @return {@code String[]} 权限数组
     */
    @TargetApi(23)
    public String[] getNecessaryPermissions() {
        ArrayList<String> permissionList = new ArrayList<String>();
        String[] permissions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            // 读写权限
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            // 拍照权限
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CAMERA);
            }

            // 录音权限
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.RECORD_AUDIO);
            }
        }

        permissions = permissionList.toArray(new String[permissionList.size()]);

        return permissions;
    }

    /**
     * 检查APP需要获得的权限
     *
     * @return {@code HashMap<String, Object>} 检查结果信息
     */
    @TargetApi(23)
    public HashMap<String, Object> checkPermissions(Activity activity) {
        // 获取权限的结果信息
        HashMap<String, Object> permissionResultMap = new HashMap<String, Object>();
        // 是否已经获得了所需的权限
        boolean havePermission = false;
        // 是否需要在应用信息中设置权限。当弹出权限窗口，用户拒绝后，本参数就为true。
        // 这时如果想获取权限，就需要进入应用信息界面，手工来获取权限。
        boolean needShowDetailsSettings = false;
        // 需要获得的权限数组
        String[] permissions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions = getNecessaryPermissions();

            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    // 无法弹出权限窗口，说明用户拒绝过。那么本参数就要设置为true。
                    // 这时如果想获取权限，就需要进入应用信息界面，手工来获取权限。
                    needShowDetailsSettings = true;
                }
            }

            if (permissions.length == 0) {
                havePermission = true;
            }
//			else {
//				// 需要获得权限
//				if (needShowDetailsSettings) {
//					// 需要打开应用信息界面
//					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//					Uri uri = Uri.fromParts("package", getPackageName(), null);
//					intent.setData(uri);
//					activity.startActivityForResult(intent, CommonParam.REQUESTCODE_PERMISSION);
//				} else {
//					// 打开授权窗口
//					activity.requestPermissions(permissions, CommonParam.REQUESTCODE_PERMISSION);
//				}
//			}
        } else {
            havePermission = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 访问所有存储空间权限
            if (!Environment.isExternalStorageManager()) {
                havePermission = false;
                needShowDetailsSettings = true;
            }
        }

        permissionResultMap.put("havePermission", havePermission);
        if (!havePermission) {
            permissionResultMap.put("needShowDetailsSettings", needShowDetailsSettings);
            permissionResultMap.put("permissions", permissions);
            Log.d("#havePermission", "#" + havePermission);
            Log.d("#showDetailsSettings", "#" + needShowDetailsSettings);
            Log.d("#permissions", "#" + JSONObject.toJSONString(permissions));
        }

        return permissionResultMap;
    }
    // 权限相关的属性与方法。结束=============================================
}
