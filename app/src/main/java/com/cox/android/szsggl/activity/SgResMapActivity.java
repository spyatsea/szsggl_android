package com.cox.android.szsggl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.PopupMenu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 水工资源信息_地图页面
 *
 * @author 乔勇(Jacky Qiao)
 */
public class SgResMapActivity extends DbActivity {
    // 常量。开始============================================
    /**
     * 默认坐标图片
     */
    public static final int ICON_IMAGE_DEFAULT = R.drawable.bullet_x_red;
    /**
     * 默认缩放级别
     */
    public static final int ZOOM_LEVEL_DEFAULT = 17;

    /**
     * 图标Map
     */
    private static final LinkedHashMap<String, Integer> ICON_MAP = new LinkedHashMap<String, Integer>();

    public static LinkedHashMap<String, Integer> getIconMap() {
        if (ICON_MAP.size() == 0) {
            ICON_MAP.put("bullet_x_transparent.png", R.drawable.bullet_x_transparent);
            ICON_MAP.put("bullet_x_red.png", R.drawable.bullet_x_red);
            ICON_MAP.put("bullet_x_blue.png", R.drawable.bullet_x_blue);
            ICON_MAP.put("bullet_x_green.png", R.drawable.bullet_x_green);
            ICON_MAP.put("bullet_x_purple.png", R.drawable.bullet_x_purple);
            ICON_MAP.put("bullet_x_orange.png", R.drawable.bullet_x_orange);
            ICON_MAP.put("bullet_x_yellow.png", R.drawable.bullet_x_yellow);
            ICON_MAP.put("bullet_x_white.png", R.drawable.bullet_x_white);
            ICON_MAP.put("bullet_x_black.png", R.drawable.bullet_x_black);
            ICON_MAP.put("bullet_x_star.png", R.drawable.bullet_x_star);
            ICON_MAP.put("bullet_x_wrench.png", R.drawable.bullet_x_wrench);
            ICON_MAP.put("bullet_x_gear.png", R.drawable.bullet_x_gear);
            ICON_MAP.put("bullet_x_key.png", R.drawable.bullet_x_key);
            ICON_MAP.put("bullet_x_burn.png", R.drawable.bullet_x_burn);
            ICON_MAP.put("bullet_x_lightning.png", R.drawable.bullet_x_lightning);
            ICON_MAP.put("bullet_x_error.png", R.drawable.bullet_x_error);
            ICON_MAP.put("bullet_x_feed.png", R.drawable.bullet_x_feed);
            ICON_MAP.put("bullet_x_bulb_off.png", R.drawable.bullet_x_bulb_off);
            ICON_MAP.put("bullet_x_bulb_on.png", R.drawable.bullet_x_bulb_on);
            ICON_MAP.put("bullet_xx_red.png", R.drawable.bullet_xx_red);
            ICON_MAP.put("bullet_xx_blue.png", R.drawable.bullet_xx_blue);
            ICON_MAP.put("bullet_xx_green.png", R.drawable.bullet_xx_green);
            ICON_MAP.put("bullet_xx_purple.png", R.drawable.bullet_xx_purple);
            ICON_MAP.put("bullet_xx_orange.png", R.drawable.bullet_xx_orange);
            ICON_MAP.put("bullet_xx_yellow.png", R.drawable.bullet_xx_yellow);
            ICON_MAP.put("bullet_xx_white.png", R.drawable.bullet_xx_white);
            ICON_MAP.put("bullet_xx_black.png", R.drawable.bullet_xx_black);
        }
        return ICON_MAP;
    }

    /**
     * 颜色Map
     */
    private static final HashMap<String, String> COLOR_MAP = new HashMap<String, String>();

    public static HashMap<String, String> getColorMap() {
        if (COLOR_MAP.size() == 0) {
            COLOR_MAP.put("red", "#ff0000");
            COLOR_MAP.put("blue", "#0000ff");
            COLOR_MAP.put("green", "#008000");
            COLOR_MAP.put("purple", "#800080");
            COLOR_MAP.put("orange", "#ffa500");
            COLOR_MAP.put("yellow", "#ffff00");
            COLOR_MAP.put("white", "#ffffff");
            COLOR_MAP.put("black", "#000000");
        }
        return COLOR_MAP;
    }

    /**
     * 连接方式Map
     */
    private static final HashMap<String, String> OT_MAP = new HashMap<String, String>();

    public static HashMap<String, String> getOtMap() {
        if (OT_MAP.size() == 0) {
            OT_MAP.put("0", "点");
            OT_MAP.put("1", "折线");
            OT_MAP.put("2", "多边形");
        }
        return OT_MAP;
    }

    /**
     * 折线默认值
     */
    public class POLYLINE_OPTIONS_DEFAULT {
        /**
         * 颜色
         */
        public static final String STROKE_COLOR = "#FF0000";
        /**
         * 折线宽度
         */
        public static final int STROKE_WEIGHT = 3;
        /**
         * 折线透明度
         */
        public static final double STROKE_OPACITY = 0.5D;
    }

    /**
     * 多边形默认值
     */
    public class POLYGON_OPTIONS_DEFAULT {
        /**
         * 多边形边框颜色
         */
        public static final String STROKE_COLOR = "#0000FF";
        /**
         * 多边形边框宽度
         */
        public static final int STROKE_WEIGHT = 3;
        /**
         * 多边形边框透明度
         */
        public static final double STROKE_OPACITY = 0.5D;
        /**
         * 多边形填充颜色
         */
        public static final String FILL_COLOR = "#FFFFFF";
        /**
         * 多边形填充透明度
         */
        public static final double FILL_OPACITY = 0.5D;
    }
    // 常量。结束============================================
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
     * 菜单按钮
     */
    ImageButton menuBtn;
    /**
     * 列表名称区
     */
    LinearLayout listTitleLayout;
    /**
     * 底部返回按钮
     */
    private Button goBackBtn;
    private Button groupBtn;
    private Button infoListBtn;
    private TextView titleTv;
    private TextView lngLatTv;
    private TextView groupTv;

    /**
     * 弹出菜单
     */
    private PopupMenu mapPopupMenu;
    /**
     * 选择分组Dialog
     */
    private AlertDialog chooseGroupDlg;
    /**
     * 选择坐标Dialog
     */
    private AlertDialog chooseInfoDlg;
    /**
     * 地址定位Dialog
     */
    private AlertDialog locateDlg;

    // 地图相关参数。开始=================================================
    /**
     * 地图图标：默认
     */
    private BitmapDescriptor marker_icon_normal = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding_new);
    /**
     * 地图图标：被选中
     */
    private BitmapDescriptor marker_icon_now = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
    /**
     * MapView 是地图主控件
     */
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    /**
     * 缩放级别
     */
    int zoomLevel = ZOOM_LEVEL_DEFAULT;
    /**
     * 搜索模块，也可去掉地图模块独立使用
     */
    private GeoCoder mSearch;

    // 初始经度
    private Double mLon_now = 0D;
    // 初始纬度
    private Double mLat_now = 0D;
    // point经度
    private Double mLon_baidu = 0D;// 112.5710392;// 116.3822;
    // point纬度
    private Double mLat_baidu = 0D; // 37.8278739;// 39.9022;

    /**
     * 地图类型
     * <p>0:普通地图<br/>
     * 1:卫星图</p>
     */
    int mapType = BaiduMap.MAP_TYPE_NORMAL;
    /**
     * 经纬度信息
     */
    private JSONArray mapDataList;
    /**
     * 组索引
     */
    private int currentGroupIndex = -1;
    /**
     * 信息索引
     */
    private int currentInfoIndex = -1;
    /**
     * 首次加载地图标志
     */
    boolean firstLoadMapFlag = true;
    /**
     * 临时坐标
     */
    LatLng tempPoint;
    /**
     * 临时坐标编号
     */
    //String tempPointId;
    /**
     * 临时Marker
     */
    //Marker tempMarker;
    /**
     * 临时Label
     */
    //Marker tempLabel;
    /**
     * 闪烁的Marker
     */
    Marker flashMarker;
    /**
     * 标记List
     */
    ArrayList<ArrayList<Marker>> markerList;
    /**
     * 文本List
     */
    ArrayList<ArrayList<Text>> labelList;
    /**
     * 文本标记List
     */
    ArrayList<ArrayList<Marker>> labelMarkerList;
    /**
     * 坐标Map
     */
    LinkedHashMap<String, LatLng> pointMap;
    /**
     * 坐标图片资源Map
     * <p>本页中用到的坐标图片资源都存放到这个Map中。<br/>
     * key:# + 图片id<br/>
     * value: 图片BitmapDescriptor</p>
     */
    HashMap<String, BitmapDescriptor> iconMap;
    // 地图相关参数。开始=================================================
    /**
     * 信息
     */
    private HashMap<String, Object> infoObj;
    /**
     * 信息编号
     */
    private String infoId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = SgResMapActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoId = data.getString("id");
        infoObj = (HashMap<String, Object>) data.getSerializable("info");
        fromFlagType = data.getString("fromFlagType", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        setContentView(R.layout.sg_res_map);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        findViews();

        titleBarName.setSingleLine(true);
        titleBarName.setText((String) infoObj.get("title"));

        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 返回
                goBack();
            }
        });
        menuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMapPopupMenu(menuBtn);
            }
        });
        goBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeWaitDialog();
//                HashMap<String, Object> info = null;
//                if (CommonUtil.checkNB(mEqId)) {
//                    infoTool = getInfoTool();
//                    String queryStr = "select model.ids ids, model.title title, model.nfc_code nfc_code, model.sn_code sn_code, model.code code from v_res_equipment model where model.valid='1' and model.ids=?";
//                    ArrayList<HashMap<String, Object>> dataList = (ArrayList<HashMap<String, Object>>) infoTool.getInfoMapList(queryStr,
//                            new String[]{mEqId});
//                    if (dataList.size() > 0) {
//                        info = dataList.get(0);
//                    }
//                }

                unWait();
            }
        });
        groupBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeChooseGroupDialog();
            }
        });
        infoListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeChooseInfoDialog();
            }
        });

        mMapView = (MapView) findViewById(R.id.bdMapView);
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        // 普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(zoomLevel));
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                // TODO Auto-generated method stub
                zoomLevel = (int) status.zoom;
            }

            @Override
            public void onMapStatusChange(MapStatus status) {
                // TODO Auto-generated method stub

            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }

            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                JSONArray info = (JSONArray) bundle.get("info");
                String type = bundle.getString("type");
                if (info != null && ("mk".equals(type) || "lb".equals(type))) {
                    currentGroupIndex = info.getIntValue(2);
                    currentInfoIndex = info.getIntValue(3);
                    String lnglat = CommonUtil.N2B(info.getString(0));
                    String title = CommonUtil.N2B(info.getString(1));
                    titleTv.setText(title.length() > 0 ? title : "　");
                    lngLatTv.setText(lnglat);
                    // 组对象
                    JSONObject group_o = mapDataList.getJSONObject(currentGroupIndex);
                    groupTv.setText(CommonUtil.N2B(group_o.getString("m")));
                } else {
                    // 这种情况应该不会出现
                    titleTv.setText(" ");
                    lngLatTv.setText("－－");
                    groupTv.setText("－－");
                }

                return true;
            }
        });
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        mBaiduMap.setMyLocationEnabled(true);
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
        StatusBarUtil.setStatusBarMode(this, false, R.color.cert_border_blue);
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定 Activity 返回的结果。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == CommonParam.RESULTCODE_EXIT) {
            setResult(CommonParam.RESULTCODE_EXIT);
            super.goBack();
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
        if (mSearch == null) {
            // 初始化MKSearch
            initMSearch();
        }
        mMapView.onResume();
        unWait();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        if (flashMarker != null) {
            flashMarker.cancelAnimation();
        }
        mMapView.onDestroy();
        mMapView = null;
        if (marker_icon_normal != null) {
            marker_icon_normal.recycle();
        }
        if (mSearch != null) {
            mSearch.destroy();
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
        return true;
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

    @Override
    public void locationMethod(BDLocation location) {
        if (mBaiduMap != null) {
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(latitude_baidu)
                    .longitude(longitude_baidu).build();
            mBaiduMap.setMyLocationData(locData);
        }
        // 首次定位
        if (firstLoadMapFlag && mBaiduMap != null && latitude_baidu != 0.0D && latitude_baidu < 180.0D) {
            firstLoadMapFlag = false;
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(latitude_baidu, longitude_baidu), 17));
        }
        mLon_now = longitude_baidu;
        mLat_now = latitude_baidu;
    }

    /**
     * 获得距离
     */
    public int getDistance() {
        // 距离
        int n = -1;

        if (longitude_baidu > 50.0D && longitude_baidu < 180.0D && latitude_baidu > 0.0D && latitude_baidu < 90.0D) {
            // 两点间的距离
            double distance = DistanceUtil.getDistance(new LatLng(mLat_baidu, mLon_baidu), new LatLng(latitude_baidu, longitude_baidu));
            n = (int) distance;
        }
        return n;
    }

    /**
     * 显示地图PopupMenu
     *
     * @param view {@code View} PopupMenu绑定的对象
     */
    public void showMapPopupMenu(View view) {
        if (mapPopupMenu == null) {
            mapPopupMenu = new PopupMenu(this, view);
            // 强制显示PopupMenu图标
            forceShowPopupMenuIcon(mapPopupMenu);
            MenuInflater inflater = mapPopupMenu.getMenuInflater();
            inflater.inflate(R.menu.sgres_map_menu, mapPopupMenu.getMenu());
            mapPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_map_type:
                            if (mapType == BaiduMap.MAP_TYPE_SATELLITE) {
                                mapType = BaiduMap.MAP_TYPE_NORMAL;
                            } else {
                                mapType = BaiduMap.MAP_TYPE_SATELLITE;
                            }
                            mBaiduMap.setMapType(mapType);
                            break;
                        case R.id.menu_map_redraw:
                            makeWaitDialog("正在绘制标点，请稍候…");
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    drawOverlay(true);
                                    unWait();
                                }
                            }, 200);
                            break;
                        case R.id.menu_map_locate:
                            makeMapLocateDialog();
                            break;
                        case R.id.menu_map_scale:
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(ZOOM_LEVEL_DEFAULT));
                            break;
                        case R.id.menu_map_info_status:
                            if (listTitleLayout.getVisibility() == View.VISIBLE) {
                                listTitleLayout.setVisibility(View.GONE);
                            } else {
                                listTitleLayout.setVisibility(View.VISIBLE);
                            }
                            break;
                        case R.id.menu_map_home:
                            setResult(CommonParam.RESULTCODE_EXIT);
                            goBack();
                            break;
                        default:
                    }
                    return true;
                }
            });
            mapPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

                @Override
                public void onDismiss(PopupMenu popup) {
                }
            });
            mapPopupMenu.show();
        } else {
            Menu menu = mapPopupMenu.getMenu();
            menu.close();
            mapPopupMenu.show();
        }
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

            // 处理数据。开始============================================================================
            markerList = new ArrayList<ArrayList<Marker>>();
            labelList = new ArrayList<ArrayList<Text>>();
            labelMarkerList = new ArrayList<ArrayList<Marker>>();
            pointMap = new LinkedHashMap<String, LatLng>();
            iconMap = new HashMap<String, BitmapDescriptor>();
            getIconMap();
            getColorMap();
            getOtMap();

            // 坐标点icon
            BitmapDescriptor xx_icon = iconMap.get("#xx");
            if (xx_icon == null) {
                xx_icon = BitmapDescriptorFactory.fromResource(R.drawable.bullet_xx);
                iconMap.put("#xx", xx_icon);
            }

            String lnglat = (String) infoObj.get("lnglat");
            if (CommonUtil.checkNB(lnglat)) {
                mapDataList = JSONArray.parseArray(lnglat);
            }
            if (mapDataList == null) {
                mapDataList = new JSONArray();
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
                // 设置字段及按钮
                drawOverlay(true);
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
                // 生成列表
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
     * 绘制覆盖层
     *
     * @param clearFlag {@code boolean} 是否需要清空地图覆盖层，默认为true
     */
    public void drawOverlay(boolean clearFlag) {
        if (clearFlag) {
            mBaiduMap.clear();

            markerList.clear();
            labelList.clear();
            labelMarkerList.clear();
            pointMap.clear();
        }

        for (int i_group = 0, len_group = mapDataList.size(); i_group < len_group; i_group++) {
            // 组对象
            JSONObject group_o = mapDataList.getJSONObject(i_group);
            // 属性对象
            JSONObject p_o = group_o.getJSONObject("p");
            // 坐标数组
            JSONArray p_d_array = p_o.getJSONArray("d");
            for (int i = 0, len = p_d_array.size(); i < len; i++) {
                JSONArray _o = p_d_array.getJSONArray(i);
                String lnglat = _o.getString(0);
                String[] lnglatArray = lnglat.split(",");
                String lng_str = (String) lnglatArray[0];
                String lat_str = (String) lnglatArray[1];
                if (CommonUtil.checkNB(lng_str) && CommonUtil.checkNB(lat_str)) {
                    Double lng = 0.0D, lat = 0.0D;
                    try {
                        lng = Double.parseDouble(lng_str);
                        lat = Double.parseDouble(lat_str);
                    } catch (Exception e) {
                        lng = 0.0D;
                        lat = 0.0D;
                    }
                    if (lng != 0.0D && lat != 0.0D) {
                        // 用给定的经纬度构造一个GeoPoint，单位是度
                        LatLng point = new LatLng(lat, lng);
                        pointMap.put(i_group + "#" + i, point);

                        // 设置索引值
                        _o.add(i_group);
                        _o.add(i);
                    }
                }
            }
        }

        for (int i_group = 0, len_group = mapDataList.size(); i_group < len_group; i_group++) {
            // 组对象
            JSONObject group_o = mapDataList.getJSONObject(i_group);
            // 属性对象
            JSONObject p_o = group_o.getJSONObject("p");
            // 连接方式
            String p_ot = p_o.getString("ot");

            if ("1".equals(p_ot)) {
                // 折线
                drawPolyline(i_group);
            } else if ("2".equals(p_ot)) {
                // 多边形
                drawPolygon(i_group);
            }

            drawMarker(i_group);
            drawLabel(i_group);
        }

        if (pointMap.size() > 0) {
            LatLng point = null;
            for (Map.Entry<String, LatLng> e : pointMap.entrySet()) {
                point = e.getValue();
                break;
            }
            if (firstLoadMapFlag) {
                firstLoadMapFlag = false;
            }
            tempPoint = point;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(tempPoint.latitude, tempPoint.longitude), zoomLevel));
                }
            }, 500);
        }
    }

    /**
     * 绘制点标记
     *
     * @param groupIndex {@code int} 组索引
     */
    public void drawMarker(int groupIndex) {
        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(groupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        // 坐标点图片
        int icon_image = 0;
        try {
            icon_image = ICON_MAP.get(p_o.getString("icon"));
        } catch (Exception e) {
        }
        if (icon_image == 0) {
            icon_image = ICON_IMAGE_DEFAULT;
        }
        // 坐标点icon
        BitmapDescriptor icon = iconMap.get("#" + icon_image);
        if (icon == null) {
            icon = BitmapDescriptorFactory.fromResource(icon_image);
            iconMap.put("#" + icon_image, icon);
        }
        // 图标高度
        int icon_height = icon.getBitmap().getHeight();

        ArrayList<Marker> mkList = new ArrayList<Marker>();
        markerList.add(mkList);

        // 坐标数组
        JSONArray p_d_array = p_o.getJSONArray("d");
        for (int i = 0, len = p_d_array.size(); i < len; i++) {
            JSONArray _o = p_d_array.getJSONArray(i);
            // 坐标点
            LatLng point = pointMap.get(groupIndex + "#" + i);
            // 构建MarkerOptions，用于在地图上添加Marker
            MarkerOptions options = new MarkerOptions().position(point).icon(icon).yOffset(icon_height / 2);
            // 在地图上添加Marker
            Marker marker = (Marker) mBaiduMap.addOverlay(options);
            mkList.add(marker);

            Bundle bundle = new Bundle();
            bundle.putSerializable("info", _o);
            bundle.putString("type", "mk");
            marker.setExtraInfo(bundle);
        }
    }

    /**
     * 绘制文本标记
     *
     * @param groupIndex {@code int} 组索引
     */
    public void drawLabel(int groupIndex) {
        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(groupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        // 坐标点图片
        int icon_image = 0;
        try {
            icon_image = ICON_MAP.get(p_o.getString("icon"));
        } catch (Exception e) {
        }
        if (icon_image == 0) {
            icon_image = ICON_IMAGE_DEFAULT;
        }
        // 坐标点icon
        BitmapDescriptor icon = iconMap.get("#" + icon_image);
        if (icon == null) {
            icon = BitmapDescriptorFactory.fromResource(icon_image);
            iconMap.put("#" + icon_image, icon);
        }
        // 图标高度
        int icon_height = 0;
        // 如果是小图，就需要重计算Marker高度
        if (icon_image != ICON_IMAGE_DEFAULT && p_o.getString("icon").contains("_xx_")) {
            icon_height = iconMap.get("#xx").getBitmap().getHeight();
        } else {
            icon_height = icon.getBitmap().getHeight();
        }

        ArrayList<Marker> lbList = new ArrayList<Marker>();
        labelMarkerList.add(lbList);

        // 坐标数组
        JSONArray p_d_array = p_o.getJSONArray("d");
        for (int i = 0, len = p_d_array.size(); i < len; i++) {
            JSONArray _o = p_d_array.getJSONArray(i);
            String title = CommonUtil.N2B(_o.getString(1));
            // 坐标点
            LatLng point = pointMap.get(groupIndex + "#" + i);
            // 构建文字Option对象，用于在地图上添加文字
//            TextOptions textOptions = new TextOptions().bgColor(0x25FFFFFF).fontSize(25)
//                    .fontColor(0xFFFF0000).text(CommonUtil.N2B(title)).rotate(0).position(point);
            // 在地图上添加该文字对象
            //Text marker_text = null;//(Text) mBaiduMap.addOverlay(textOptions);
            //lbList.add(marker_text);

            BitmapDescriptor bd = null;
            int labelYOffset = 0;
            if (title.length() > 0) {
                TextView textView = new TextView(classThis);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                textView.setTextColor(Color.BLACK);
                textView.setShadowLayer(3, 0, 0, Color.WHITE);
                textView.setText(title.length() > 0 ? title : "　　");
                textView.setBackground(getResources().getDrawable(R.drawable.border_white_red));
                textView.setPadding(3, 3, 3, 3);
                textView.destroyDrawingCache();
                textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
                textView.setDrawingCacheEnabled(true);
                Bitmap bitmapText = textView.getDrawingCache(true);
                if (icon_image != R.drawable.bullet_x_transparent) {
                    // 坐标不是透明图
                    labelYOffset = icon_height / 2 + bitmapText.getHeight() + 4;
                } else {
                    labelYOffset = bitmapText.getHeight() + 6;
                }
                bd = BitmapDescriptorFactory.fromBitmap(bitmapText);
            } else {
                if (icon_image != R.drawable.bullet_x_transparent) {
                    // 坐标不是透明图
                    labelYOffset = icon_height / 2 + getResources().getDrawable(R.drawable.bullet_x_transparent).getIntrinsicHeight() + 4;
                } else {
                    labelYOffset = getResources().getDrawable(R.drawable.bullet_x_transparent).getIntrinsicHeight() + 6;
                }
                bd = iconMap.get("#" + R.drawable.bullet_x_transparent);
                if (bd == null) {
                    bd = BitmapDescriptorFactory.fromResource(R.drawable.bullet_x_transparent);
                    iconMap.put("#" + R.drawable.bullet_x_transparent, bd);
                }
            }

            // 构建MarkerOptions，用于在地图上添加Marker
            // MarkerOptions options = new MarkerOptions().position(point).icon(bd).yOffset(icon_height / 2 + icon_height / 8 * 7);
            MarkerOptions options = new MarkerOptions().position(point).icon(bd).yOffset(labelYOffset);
            // 在地图上添加Marker
            Marker marker = (Marker) mBaiduMap.addOverlay(options);
            if (title.length() > 0) {
                bd.recycle();
            }
            lbList.add(marker);

            Bundle bundle = new Bundle();
            bundle.putSerializable("info", _o);
            bundle.putString("type", "lb");
            marker.setExtraInfo(bundle);
        }
    }

    /**
     * 绘制连线
     *
     * @param groupIndex {@code int} 组索引
     */
    public void drawPolyline(int groupIndex) {
        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(groupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        JSONObject p_op_o = p_o.getJSONObject("op");
        // 坐标数组
        JSONArray p_d_array = p_o.getJSONArray("d");
        List<LatLng> pointList = new ArrayList<LatLng>();
        for (int i = 0, len = p_d_array.size(); i < len; i++) {
            JSONArray _o = p_d_array.getJSONArray(i);
            // 坐标点
            LatLng point = pointMap.get(groupIndex + "#" + i);
            pointList.add(point);
        }

        //设置折线的属性
//        PolylineOptions options = new PolylineOptions()
//                .width(10)
//                .color(0xAAFF0000)
//                .points(pointList);
        PolylineOptions options = makePolylineOptions(p_op_o);
        options.points(pointList);
        // 在地图上添加Polyline
        Polyline polyline = (Polyline) mBaiduMap.addOverlay(options);
    }

    /**
     * 生成折线属性
     *
     * @param op {@code JSONObject} 原始属性对象
     * @return {@code PolylineOptions} 属性对象
     */
    public PolylineOptions makePolylineOptions(JSONObject op) {
        PolylineOptions options = new PolylineOptions();

        Integer strokeWeight = op.getInteger("strokeWeight");
        if (strokeWeight == null) {
            strokeWeight = POLYLINE_OPTIONS_DEFAULT.STROKE_WEIGHT;
        }
        options.width(strokeWeight);

        String strokeColor = op.getString("strokeColor");
        if (!CommonUtil.checkNB(strokeColor)) {
            strokeColor = POLYLINE_OPTIONS_DEFAULT.STROKE_COLOR;
        }
        if (COLOR_MAP.get(strokeColor) != null) {
            strokeColor = COLOR_MAP.get(strokeColor);
        }

        Double strokeOpacity = op.getDouble("strokeOpacity");
        if (strokeOpacity == null) {
            strokeOpacity = POLYLINE_OPTIONS_DEFAULT.STROKE_OPACITY;
        }

        options.color((((int) (((double) 0XFF) * strokeOpacity)) << 24) + Integer.parseInt(strokeColor.replace("#", ""), 16));

        return options;
    }

    /**
     * 删除连线
     */
    public void removePolyline() {

    }

    /**
     * 绘制多边形
     *
     * @param groupIndex {@code int} 组索引
     */
    public void drawPolygon(int groupIndex) {
        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(groupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        JSONObject p_op_o = p_o.getJSONObject("op");
        // 坐标数组
        JSONArray p_d_array = p_o.getJSONArray("d");
        List<LatLng> pointList = new ArrayList<LatLng>();
        for (int i = 0, len = p_d_array.size(); i < len; i++) {
            JSONArray _o = p_d_array.getJSONArray(i);
            // 坐标点
            LatLng point = pointMap.get(groupIndex + "#" + i);
            pointList.add(point);
        }

        //设置折线的属性
//        PolylineOptions options = new PolylineOptions()
//                .width(10)
//                .color(0xAAFF0000)
//                .points(pointList);
        PolygonOptions options = makePolygonOptions(p_op_o);
        options.points(pointList);
        // 在地图上添加Polygon
        Polygon polygon = (Polygon) mBaiduMap.addOverlay(options);
    }

    /**
     * 生成多边形属性
     *
     * @param op {@code JSONObject} 原始属性对象
     * @return {@code PolygonOptions} 属性对象
     */
    public PolygonOptions makePolygonOptions(JSONObject op) {
        PolygonOptions options = new PolygonOptions();
        // 边框对象
        Stroke stroke = null;

        Integer strokeWeight = op.getInteger("strokeWeight");
        if (strokeWeight == null) {
            strokeWeight = POLYGON_OPTIONS_DEFAULT.STROKE_WEIGHT;
        }

        String strokeColor = op.getString("strokeColor");
        if (!CommonUtil.checkNB(strokeColor)) {
            strokeColor = POLYGON_OPTIONS_DEFAULT.STROKE_COLOR;
        }
        if (COLOR_MAP.get(strokeColor) != null) {
            strokeColor = COLOR_MAP.get(strokeColor);
        }

        Double strokeOpacity = op.getDouble("strokeOpacity");
        if (strokeOpacity == null) {
            strokeOpacity = POLYGON_OPTIONS_DEFAULT.STROKE_OPACITY;
        }
        stroke = new Stroke(strokeWeight, (((int) (((double) 0XFF) * strokeOpacity)) << 24) + Integer.parseInt(strokeColor.replace("#", ""), 16));
        options.stroke(stroke);

        String fillColor = op.getString("fillColor");
        if (!CommonUtil.checkNB(fillColor)) {
            fillColor = POLYGON_OPTIONS_DEFAULT.FILL_COLOR;
        }
        if (COLOR_MAP.get(fillColor) != null) {
            fillColor = COLOR_MAP.get(fillColor);
        }

        Double fillOpacity = op.getDouble("fillOpacity");
        if (fillOpacity == null) {
            fillOpacity = POLYGON_OPTIONS_DEFAULT.FILL_OPACITY;
        }
        options.fillColor((((int) (((double) 0XFF) * fillOpacity)) << 24) + Integer.parseInt(fillColor.replace("#", ""), 16));

        return options;
    }

    /**
     * 删除多边形
     */
    public void removePolygon() {

    }

    /**
     * 定位分组
     *
     * @param groupIndex {@code int} 组索引
     */
    public void findGroup(int groupIndex) {
        if (groupIndex > -1 && groupIndex < mapDataList.size()) {
            // 坐标点
            LatLng point = pointMap.get(groupIndex + "#" + 0);
            if (point != null) {
                // 组对象
                JSONObject group_o = mapDataList.getJSONObject(groupIndex);
                // 属性对象
                JSONObject p_o = group_o.getJSONObject("p");
                // 坐标数组
                JSONArray p_d_array = p_o.getJSONArray("d");
                JSONArray _o = p_d_array.getJSONArray(0);
                if (_o != null) {
                    String lnglat = CommonUtil.N2B(_o.getString(0));
                    String title = CommonUtil.N2B(_o.getString(1));
                    titleTv.setText(title.length() > 0 ? title : "　");
                    lngLatTv.setText(lnglat);
                    groupTv.setText(CommonUtil.N2B(group_o.getString("m")));

                    locatePoint(new LatLng(point.latitude, point.longitude));
                }
            }
        }
    }

    /**
     * 将标记居中显示
     *
     * @param point {@code Latlng} 要居中的坐标
     */
    public void locatePoint(LatLng point) {
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), zoomLevel));
        flashPoint(point);
    }

    /**
     * 使标记闪烁
     *
     * @param point {@code Latlng} 要闪烁的坐标
     */
    public void flashPoint(LatLng point) {
        if (flashMarker != null) {
            flashMarker.cancelAnimation();
        }

        //通过LatLng列表构造ScaleAnimation对象
        ScaleAnimation mTransforma = new ScaleAnimation(1.0f, 2.0f, 3.0f, 1.0f);
        //动画执行时间
        mTransforma.setDuration(500);
        //动画重复模式
        mTransforma.setRepeatMode(Animation.RepeatMode.RESTART);
        //动画重复次数
        mTransforma.setRepeatCount(2);
        //根据开发需要设置动画监听
        mTransforma.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                if (flashMarker != null) {
                    flashMarker.remove();
                    flashMarker = null;
                }
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

        // 构建MarkerOptions，用于在地图上添加Marker
        MarkerOptions options = new MarkerOptions().position(point).icon(marker_icon_now).zIndex(0);
        // 在地图上添加Marker
        flashMarker = (Marker) mBaiduMap.addOverlay(options);
        //设置动画
        flashMarker.setAnimation(mTransforma);
        //开启动画
        flashMarker.startAnimation();
    }

    /**
     * 选择分组
     */
    public void makeChooseGroupDialog() {
        if (mapDataList.size() == 0) {
            makeAlertDialog("您当前没有分组可以选择！");
            return;
        }
        // 名称数组
        String[] nameArray = new String[mapDataList.size()];
        int checkedIndex = -1;
        for (int i = 0, len = mapDataList.size(); i < len; i++) {
            // 组对象
            JSONObject group_o = mapDataList.getJSONObject(i);
            nameArray[i] = CommonUtil.N2B(group_o.getString("t"));
        }
        if (currentGroupIndex > -1) {
            checkedIndex = currentGroupIndex;
        }

        Builder dlgBuilder = new Builder(this);

        dlgBuilder.setTitle("选择分组");
        dlgBuilder.setIcon(R.drawable.cat_type_normal);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setSingleChoiceItems(nameArray, checkedIndex, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) ((AlertDialog) dialog).getButton(
                        DialogInterface.BUTTON_POSITIVE).getTag();

                Integer which_old = (Integer) dlgTag.get("which");
                // 是否需要重设信息索引
                boolean needResetInfoIndexFlag = false;
                if (which_old == null || which_old == -1) {
                    // 没有选择过
                    needResetInfoIndexFlag = true;
                } else {
                    // 已经选择过
                    if (which != which_old) {
                        // 选择了不同的选项
                        needResetInfoIndexFlag = true;
                    }
                }
                // 选择的索引
                dlgTag.put("which", which);
                dlgTag.put("needReset", needResetInfoIndexFlag);

                dlgTag = (HashMap<String, Object>) ((AlertDialog) dialog).getButton(
                        DialogInterface.BUTTON_NEUTRAL).getTag();
                // 选择的索引
                dlgTag.put("which", which);
                dlgTag.put("needReset", needResetInfoIndexFlag);
            }
        });
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNeutralButton(R.string.detail, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        chooseGroupDlg = dlgBuilder.create();
        chooseGroupDlg.show();

        // 确定按钮
        Button confirmBtn = chooseGroupDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = chooseGroupDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 详情按钮
        Button detailBtn = chooseGroupDlg.getButton(DialogInterface.BUTTON_NEUTRAL);

        // 存放Dialog所需信息的Map
        Map<String, Object> dlgTag = new HashMap<String, Object>();
        dlgTag.put("which", checkedIndex);
        // 绑定数据
        confirmBtn.setTag(dlgTag);
        detailBtn.setTag(dlgTag);

        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) v.getTag();
                // 选择的索引
                Integer which = (Integer) dlgTag.get("which");
                // 是否需要重设信息索引
                Boolean needResetInfoIndexFlag = (Boolean) dlgTag.get("needReset");

                if (needResetInfoIndexFlag != null && needResetInfoIndexFlag) {
                    currentInfoIndex = -1;
                }

                if (which != null && which > -1) {
                    currentGroupIndex = which;
                    findGroup(currentGroupIndex);
                }

                chooseGroupDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseGroupDlg.cancel();
            }
        });
        detailBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) v.getTag();
                // 选择的索引
                Integer which = (Integer) dlgTag.get("which");

                if (which != null && which > -1) {
                    makeGroupDetailDialog(which);
                } else {
                    makeAlertDialog("请选择分组！");
                }
            }
        });
    }

    /**
     * 选择信息
     */
    public void makeChooseInfoDialog() {
        if (currentGroupIndex == -1) {
            makeAlertDialog("请先选择分组！");
            return;
        }

        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(currentGroupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        // 坐标数组
        JSONArray p_d_array = p_o.getJSONArray("d");

        // 名称数组
        String[] nameArray = new String[p_d_array.size()];
        int checkedIndex = -1;
        for (int i = 0, len = p_d_array.size(); i < len; i++) {
            // 组对象
            JSONArray _o = p_d_array.getJSONArray(i);
            String lnglat = CommonUtil.N2B(_o.getString(0));
            String memo = CommonUtil.N2B(_o.getString(1));
            String title = null;
            if (memo.length() > 0) {
                title = memo;
            } else {
                title = "(" + CommonUtil.N2B(_o.getString(0)) + ")";
            }
            nameArray[i] = title;
        }
        if (currentInfoIndex > -1) {
            checkedIndex = currentInfoIndex;
        }

        Builder dlgBuilder = new Builder(this);

        dlgBuilder.setTitle("经纬度信息列表");
        dlgBuilder.setIcon(R.drawable.menu_list);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setSingleChoiceItems(nameArray, checkedIndex, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) ((AlertDialog) dialog).getButton(
                        DialogInterface.BUTTON_POSITIVE).getTag();
                // 选择的索引
                dlgTag.put("which", which);

                dlgTag = (HashMap<String, Object>) ((AlertDialog) dialog).getButton(
                        DialogInterface.BUTTON_NEUTRAL).getTag();
                // 选择的索引
                dlgTag.put("which", which);
            }
        });
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgBuilder.setNeutralButton(R.string.detail, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        chooseInfoDlg = dlgBuilder.create();
        chooseInfoDlg.show();

        // 确定按钮
        Button confirmBtn = chooseInfoDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = chooseInfoDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 详情按钮
        Button detailBtn = chooseInfoDlg.getButton(DialogInterface.BUTTON_NEUTRAL);

        // 存放Dialog所需信息的Map
        Map<String, Object> dlgTag = new HashMap<String, Object>();
        dlgTag.put("which", checkedIndex);
        // 绑定数据
        confirmBtn.setTag(dlgTag);
        detailBtn.setTag(dlgTag);

        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) v.getTag();
                // 选择的索引
                Integer which = (Integer) dlgTag.get("which");

                if (which != null && which > -1) {
                    currentInfoIndex = which;
                    // 坐标点
                    LatLng point = pointMap.get(currentGroupIndex + "#" + currentInfoIndex);
                    if (point != null) {
                        // 组对象
                        JSONObject group_o = mapDataList.getJSONObject(currentGroupIndex);
                        // 属性对象
                        JSONObject p_o = group_o.getJSONObject("p");
                        // 坐标数组
                        JSONArray p_d_array = p_o.getJSONArray("d");
                        JSONArray _o = p_d_array.getJSONArray(currentInfoIndex);
                        if (_o != null) {
                            String lnglat = CommonUtil.N2B(_o.getString(0));
                            String title = CommonUtil.N2B(_o.getString(1));
                            titleTv.setText(title.length() > 0 ? title : "　");
                            lngLatTv.setText(lnglat);
                            groupTv.setText(CommonUtil.N2B(group_o.getString("m")));

                            locatePoint(new LatLng(point.latitude, point.longitude));
                        }
                    }
                }

                chooseInfoDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseInfoDlg.cancel();
            }
        });
        detailBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 存放Dialog所需信息的Map
                Map<String, Object> dlgTag = (HashMap<String, Object>) v.getTag();
                // 选择的索引
                Integer which = (Integer) dlgTag.get("which");

                if (which != null && which > -1) {
                    makeInfoDetailDialog(currentGroupIndex, which);
                } else {
                    makeAlertDialog("请选择信息！");
                }
            }
        });
    }

    /**
     * 显示分组详情对话框
     *
     * @param groupIndex {@code int} 组索引
     */
    public void makeGroupDetailDialog(int groupIndex) {
        if (groupIndex == -1 || groupIndex >= mapDataList.size()) {
            makeAlertDialog("请选择分组！");
            return;
        }

        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(groupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        // 形状属性
        JSONObject p_op_o = p_o.getJSONObject("op");

        String title = CommonUtil.N2B(group_o.getString("t"));
        String memo = CommonUtil.N2B(group_o.getString("m"));

        // 坐标点图片
        int icon_image = 0;
        try {
            icon_image = ICON_MAP.get(p_o.getString("icon"));
            if (icon_image == R.drawable.bullet_x_transparent) {
                icon_image = R.drawable.bullet_x_transparent_logo;
            }
        } catch (Exception e) {
        }
        if (icon_image == 0) {
            icon_image = ICON_IMAGE_DEFAULT;
        }

        // 连接方式
        String ot = p_o.getString("ot");

        String strokeColor = p_op_o.getString("strokeColor");
        if (!CommonUtil.checkNB(strokeColor)) {
            strokeColor = POLYGON_OPTIONS_DEFAULT.STROKE_COLOR;
        }
        if (COLOR_MAP.get(strokeColor) != null) {
            strokeColor = COLOR_MAP.get(strokeColor);
        }
        Integer strokeWeight = p_op_o.getInteger("strokeWeight");
        Double strokeOpacity = p_op_o.getDouble("strokeOpacity");
        if (strokeOpacity == null) {
            strokeOpacity = POLYGON_OPTIONS_DEFAULT.STROKE_OPACITY;
        }

        String fillColor = p_op_o.getString("fillColor");
        if (!CommonUtil.checkNB(fillColor)) {
            fillColor = POLYGON_OPTIONS_DEFAULT.FILL_COLOR;
        }
        if (COLOR_MAP.get(fillColor) != null) {
            fillColor = COLOR_MAP.get(fillColor);
        }
        Double fillOpacity = p_op_o.getDouble("fillOpacity");
        if (fillOpacity == null) {
            fillOpacity = POLYGON_OPTIONS_DEFAULT.FILL_OPACITY;
        }

        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_sgres_group_detail, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("分组信息");
        dlgBuilder.setIcon(R.drawable.menu_document_info);
        dlgBuilder.setCancelable(true);

        TextView group_title_tv = (TextView) layout.findViewById(R.id.group_title_tv);
        TextView group_memo_tv = (TextView) layout.findViewById(R.id.group_memo_tv);
        TextView group_ot_tv = (TextView) layout.findViewById(R.id.group_ot_tv);
        TextView group_icon_tv = (TextView) layout.findViewById(R.id.group_icon_tv);
        TextView group_stroke_color_pad_tv = (TextView) layout.findViewById(R.id.group_stroke_color_pad_tv);
        TextView group_stroke_color_tv = (TextView) layout.findViewById(R.id.group_stroke_color_tv);
        TextView group_stroke_weight_tv = (TextView) layout.findViewById(R.id.group_stroke_weight_tv);
        TextView group_stroke_opacity_tv = (TextView) layout.findViewById(R.id.group_stroke_opacity_tv);
        TextView group_fill_color_pad_tv = (TextView) layout.findViewById(R.id.group_fill_color_pad_tv);
        TextView group_fill_color_tv = (TextView) layout.findViewById(R.id.group_fill_color_tv);
        TextView group_fill_opacity_tv = (TextView) layout.findViewById(R.id.group_fill_opacity_tv);
        LinearLayout group_stroke_layout = (LinearLayout) layout.findViewById(R.id.group_stroke_layout);
        LinearLayout group_fill_layout = (LinearLayout) layout.findViewById(R.id.group_fill_layout);

        group_title_tv.setText(title);
        group_memo_tv.setText(memo);

        group_ot_tv.setText(OT_MAP.get(ot));
        Drawable ot_icon = null;
        if ("1".equals(ot)) {
            ot_icon = getResources().getDrawable(R.drawable.map_overlay_polyline);
        } else if ("2".equals(ot)) {
            ot_icon = getResources().getDrawable(R.drawable.map_overlay_polygon);
        } else {
            ot_icon = getResources().getDrawable(R.drawable.map_overlay_point);
        }
        group_ot_tv.setCompoundDrawablesWithIntrinsicBounds(ot_icon, null, null, null);

        // group_icon_tv.setText(p_o.getString("icon"));
        Drawable icon_icon = getResources().getDrawable(icon_image);
        group_icon_tv.setCompoundDrawablesWithIntrinsicBounds(icon_icon, null, null, null);

        if ("1".equals(ot) || "2".equals(ot)) {
            // 折线或多边形
            group_stroke_layout.setVisibility(View.VISIBLE);
            group_stroke_color_tv.setText(strokeColor);
            group_stroke_color_pad_tv.setBackgroundColor(Color.parseColor(strokeColor));
            group_stroke_weight_tv.setText("" + strokeWeight);
            group_stroke_opacity_tv.setText("" + strokeOpacity);

            if ("2".equals(ot)) {
                // 多边形
                group_fill_layout.setVisibility(View.VISIBLE);
                group_fill_color_tv.setText(fillColor);
                group_fill_color_pad_tv.setBackgroundColor(Color.parseColor(fillColor));
                group_fill_opacity_tv.setText("" + fillOpacity);
            } else {
                group_fill_layout.setVisibility(View.GONE);
            }
        } else {
            group_stroke_layout.setVisibility(View.GONE);
            group_fill_layout.setVisibility(View.GONE);
        }

        dlgBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dlgBuilder.create().show();
    }

    /**
     * 显示信息详情对话框
     *
     * @param groupIndex {@code int} 组索引
     * @param infoIndex  {@code int} 信息索引
     */
    public void makeInfoDetailDialog(int groupIndex, int infoIndex) {
        if (infoIndex == -1) {
            makeAlertDialog("请选择信息！");
            return;
        }

        // 组对象
        JSONObject group_o = mapDataList.getJSONObject(groupIndex);
        // 属性对象
        JSONObject p_o = group_o.getJSONObject("p");
        // 坐标数组
        JSONArray p_d_array = p_o.getJSONArray("d");// 组对象
        JSONArray _o = p_d_array.getJSONArray(infoIndex);

        String loc = CommonUtil.N2B(CommonUtil.N2B(_o.getString(0)));
        String memo = CommonUtil.N2B(CommonUtil.N2B(_o.getString(1)));

        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_sgres_info_detail, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("经纬度信息");
        dlgBuilder.setIcon(R.drawable.menu_document_info);
        dlgBuilder.setCancelable(true);

        TextView lnglat_loc_tv = (TextView) layout.findViewById(R.id.lnglat_loc_tv);
        TextView lnglat_memo_tv = (TextView) layout.findViewById(R.id.lnglat_memo_tv);

        lnglat_loc_tv.setText(loc);
        lnglat_memo_tv.setText(memo);

        dlgBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dlgBuilder.create().show();
    }

    /**
     * 显示地址定位对话框
     */
    public void makeMapLocateDialog() {
        Builder dlgBuilder = new Builder(this);

        ScrollView layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_locate_edit, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle("地址定位");
        dlgBuilder.setIcon(R.drawable.menu_map_pin);

        dlgBuilder.setPositiveButton(R.string.location, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        locateDlg = dlgBuilder.create();
        locateDlg.show();

        // 定位按钮
        Button locateBtn = locateDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 取消按钮
        Button cancelBtn = locateDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

        locateBtn.setTag(layout);

        locateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean flag = false;

                ScrollView view = (ScrollView) v.getTag();
                EditText tv1 = (EditText) view.findViewById(R.id.tv1);
                EditText tv2 = (EditText) view.findViewById(R.id.tv2);

                String s1 = CommonUtil.N2B(tv1.getText().toString());
                String s2 = CommonUtil.N2B(tv2.getText().toString());

                if (s1.length() == 0) {
                    makeAlertDialog("请输入城市名称！");
                } else {
                    flag = true;
                }

                if (flag) {
                    if (s2.length() == 0) {
                        s2 = s1;
                    }

                    makeWaitDialog("正在查询，请稍候…");
                    mSearch.geocode(new GeoCodeOption()
                            .city(s1)
                            .address(s2));
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                locateDlg.cancel();
            }
        });
    }

    /**
     * 初始化MKSearch
     */
    public void initMSearch() {
        // 定位相关参数。开始=================================================
        // 初始化搜索模块，注册事件监听
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
        }
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            /**
             * 反地理编码查询结果回调函数
             * */
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult geoCodeResult) {
                // String address = "";
                // AddressComponent ac = null;
                // if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // address = getString(R.string.info_unknow);
                // } else {
                // ac = result.getAddressDetail();
                // 反地理编码：通过坐标点检索详细地址及周边poi
                // address = ac.city + ac.district + ac.street + ac.streetNumber;
                // }
            }

            /**
             * 地理编码查询结果回调函数
             * */
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                unWait();
                if (null != geoCodeResult && null != geoCodeResult.getLocation()) {
                    if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        //没有检索到结果
                        makeAlertDialog("找不到该地址！");
                    } else {
                        if (locateDlg != null && locateDlg.isShowing()) {
                            locateDlg.cancel();
                        }
                        double latitude = geoCodeResult.getLocation().latitude;
                        double longitude = geoCodeResult.getLocation().longitude;
                        locatePoint(new LatLng(latitude, longitude));
                    }
                }
            }
        });
        // 定位相关参数。结束=================================================
    }

    /**
     * 查找view
     */
    public void findViews() {
        titleBarName = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        groupBtn = (Button) findViewById(R.id.groupBtn);
        infoListBtn = (Button) findViewById(R.id.infoListBtn);
        listTitleLayout = (LinearLayout) findViewById(R.id.listTitleLayout);
        // 界面相关参数。开始===============================
        titleTv = (TextView) findViewById(R.id.titleTv);
        lngLatTv = (TextView) findViewById(R.id.lngLatTv);
        groupTv = (TextView) findViewById(R.id.groupTv);
        // 界面相关参数。结束===============================
    }
}
