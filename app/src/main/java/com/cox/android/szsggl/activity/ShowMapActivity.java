package com.cox.android.szsggl.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.StatusBarUtil;

public class ShowMapActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    // DbActivity classThis;
    private BitmapDescriptor marker;
    /**
     * MapView 是地图主控件
     */
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    /**
     * 搜索模块，也可去掉地图模块独立使用
     */
    private GeoCoder mSearch = null;

    private Double mLat_baidu = 0D; // 37.8278739;// 39.9022; // point1纬度
    private Double mLon_baidu = 0D;// 112.5710392;// 116.3822; // point1经度
    // 地址名称
    private String address = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // classThis = ShowMapActivity.this;

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
        setContentView(R.layout.show_map);
        // 标题栏组件。开始========================================================
        titleText = (TextView) findViewById(R.id.titleText);
        // 标题栏组件。结束========================================================

        // 获取该Result上的Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        if (data != null) {
            Bundle infoBundle = data.getBundle("infoBundle");
            if (infoBundle != null) {
                // mLat = infoBundle.getDouble("lat");
                // mLon = infoBundle.getDouble("lon");
                mLat_baidu = infoBundle.getDouble("lat_baidu");
                mLon_baidu = infoBundle.getDouble("lon_baidu");
                setTitle("");
            } else {
                this.finish();
            }
        } else {
            this.finish();
        }
        setTitle(getString(R.string.info_loc_mapinfo_addr, (mLat_baidu.toString().length() >= 7 ? mLat_baidu.toString()
                .substring(0, 7) : mLat_baidu.toString()), (mLon_baidu.toString().length() >= 8 ? mLon_baidu.toString()
                .substring(0, 8) : mLon_baidu.toString()), ""));
        // 用给定的经纬度构造一个GeoPoint，单位是度
        final LatLng point = new LatLng(mLat_baidu, mLon_baidu);

        mMapView = (MapView) findViewById(R.id.bmapsView);
        mBaiduMap = mMapView.getMap();
        // 普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 17)); // 设置地图zoom级别
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new ReverseGeocodeTask().execute();
            }
        });
        // 准备overlay图像数据
        marker = BitmapDescriptorFactory.fromResource(R.drawable.iconmark); // 得到需要标在地图上的资源
        // marker.setBounds(0, 0, marker, marker.getIntrinsicHeight()); // 为maker定义位置和边界
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(marker);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        // 初始化搜索模块，注册事件监听
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
        }
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            /**
             * 反地理编码查询结果回调函数
             * */
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    address = getString(R.string.info_unknow);
                    return;
                } else {
                    AddressComponent ac = result.getAddressDetail();
                    // 反地理编码：通过坐标点检索详细地址及周边poi
                    address = ac.city + ac.district + ac.street + ac.streetNumber;
                    setTitle(getString(
                            R.string.info_loc_mapinfo_addr,
                            (mLat_baidu.toString().length() >= 7 ? mLat_baidu.toString().substring(0, 7) : mLat_baidu
                                    .toString()), (mLon_baidu.toString().length() >= 8 ? mLon_baidu.toString()
                                    .substring(0, 8) : mLon_baidu.toString()), address));
                }
            }

            /**
             * 地理编码查询结果回调函数
             * */
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        marker.recycle();
        mSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 不显示菜单
        return false;
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
        return true;
    }

    /**
     * 主进程 AsyncTask 类
     */
    private class ReverseGeocodeTask extends AsyncTask<Object, Integer, String> {

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
            String result = CommonParam.RESULT_SUCCESS;
            doWait(1000);
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
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(mLat_baidu, mLon_baidu)));// 逆地址解析
        }
    }
}
