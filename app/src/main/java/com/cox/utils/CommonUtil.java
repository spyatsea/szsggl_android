/*
 * Copyright (c) www.spyatsea.com  2012
 */
package com.cox.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.R;
import com.cox.android.szsggl.activity.DbActivity;
import com.cox.android.szsggl.model.User;
import com.cox.dto.CodeNameDTO;
import com.cox.dto.CommonDTO;
import com.cox.utils.WpsParam.ClassName;
import com.cox.utils.WpsParam.PackageName;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 定义了一些通用的方法。
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CommonUtil {
    // 项目自定义参数区。开始====================================================
    /**
     * 日期是否晚：否
     */
    public static final int IS_LATER_NO = 0;
    /**
     * 日期是否晚：是
     */
    public static final int IS_LATER_YES = 1;
    /**
     * 日期是否晚：未知
     */
    public static final int IS_LATER_UNKNOW = 2;

    // 项目自定义参数区。结束====================================================

    // 项目自定义方法区。开始====================================================

    /**
     * 生成微信消息唯一标识符
     *
     * @return {@code String} 标识符
     */
    public static String buildTransaction() {
        return buildTransaction(null);
    }

    /**
     * 生成微信消息唯一标识符
     *
     * @param type {@code String} 消息类型
     * @return {@code String} 标识符
     */
    public static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 判断某日期比现在晚的毫秒数
     *
     * @param dateStr {@code String} 要比较的日期字符串
     * @return {@code long} 比今天晚的毫秒数，大于0时表示比现在晚。
     */
    public static long isLaterThanNow(String dateStr) {
        return isLaterThanNow(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 判断某日期比现在晚的毫秒数
     *
     * @param dateStr {@code String} 要比较的日期字符串
     * @param pattern {@code String} 日期模式字符串
     * @return {@code long} 比今天晚的毫秒数，大于0时表示比现在晚。
     */
    public static long isLaterThanNow(String dateStr, String pattern) {
        // 差值
        long laterTime = 0L;
        if (checkNB(dateStr) && checkNB(pattern)) {
            SimpleDateFormat dateFmt = new SimpleDateFormat(pattern, Locale.CHINA);

            try {
                Date date = dateFmt.parse(dateStr);
                Date now = new Date();
                laterTime = date.getTime() - now.getTime();
            } catch (ParseException e) {
                laterTime = -1L;
                e.printStackTrace();
            }
        }
        return laterTime;
    }

    /**
     * 获得 Gallery 小图合适的缩放倍数
     *
     * @param options   {@code BitmapFactory.Options}
     * @param reqWidth  {@code int} 图片的原始宽度
     * @param reqHeight {@code int} 图片的原始高度
     * @return {@code int} 缩放倍数
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 图片的原始宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        double dWidth = (double) width;
        double dHeight = (double) height;
        double sizeW = 1D, sizeH = 1D, size = 1D;
        if (width > reqWidth || height > reqHeight) {
            if (width > reqWidth) {
                sizeW = dWidth / (double) reqWidth;
            }
            if (height > reqHeight) {
                sizeH = dHeight / (double) reqHeight;
            }
            size = Math.min(sizeW, sizeH);
        }

        if (size > 1D) {
            // 实际方根
            double d = Math.sqrt(size);
            inSampleSize = (int) Math.pow(2D, Math.round(d));
        }
        return inSampleSize;
    }

    /**
     * 显示大图时，获得合适的尺寸
     *
     * @param realWidth  {@code int} 图片的原始宽度
     * @param realHeight {@code int} 图片的原始高度
     * @param maxWidth   {@code int} 图片最大的宽度
     * @param maxHeight  {@code int} 图片最大的高度
     * @return {@code Bundle} 缩放参数
     */
    public static Bundle calculateBigImageSize(int realWidth, int realHeight, int maxWidth, int maxHeight) {
        // 信息传输Bundle
        Bundle data = new Bundle();

        // 计算得出的宽度
        int calcWidth = 0;
        // 计算得出的高度
        int calcHeight = 0;
        // 宽度缩放比例
        float rateW = 0.0F;
        // 高度缩放比例
        float rateH = 0.0F;

        if (realWidth <= maxWidth && realHeight <= maxHeight) {
            calcWidth = realWidth;
            calcHeight = realHeight;
        } else if (realWidth > maxWidth && realHeight <= maxHeight) {
            rateW = (float) realWidth / (float) maxWidth;
            calcWidth = maxWidth;
            calcHeight = (int) ((float) realHeight / rateW);
        } else if (realWidth <= maxWidth && realHeight > maxHeight) {
            rateH = (float) realHeight / (float) maxHeight;
            calcHeight = maxHeight;
            calcWidth = (int) ((float) realWidth / rateH);
        } else if (realWidth > maxWidth && realHeight > maxHeight) {
            rateW = (float) realWidth / (float) maxWidth;
            rateH = (float) realHeight / (float) maxHeight;
            float rate = Math.max(rateW, rateH);
            calcHeight = (int) ((float) realHeight / rate);
            calcWidth = (int) ((float) realWidth / rate);
        }

        data.putInt("calcWidth", calcWidth);
        data.putInt("calcHeight", calcHeight);

        return data;
    }

    /**
     * 显示大图时，获得合适的尺寸
     * <p>
     * 使图片不小于屏幕的2/3
     *
     * @param realWidth  {@code int} 图片的原始宽度
     * @param realHeight {@code int} 图片的原始高度
     * @param maxWidth   {@code int} 图片最大的宽度
     * @param maxHeight  {@code int} 图片最大的高度
     * @return {@code Bundle} 缩放参数
     */
    public static Bundle calculateBigImageSizeWell(int realWidth, int realHeight, int maxWidth, int maxHeight) {
        // 信息传输Bundle
        Bundle data = new Bundle();

        // 计算得出的宽度
        int calcWidth = 0;
        // 计算得出的高度
        int calcHeight = 0;
        // 宽度缩放比例
        float rateW = 0.0F;
        // 高度缩放比例
        float rateH = 0.0F;
        float rate = 0.0F;

        if (realWidth > (maxWidth / 5.0F * 4.0F) || realHeight > (maxHeight / 5.0F * 4.0F)) {
            rateW = (float) realWidth / (float) maxWidth;
            rateH = (float) realHeight / (float) maxHeight;
            rate = Math.max(rateW, rateH);
            calcHeight = (int) ((float) realHeight / rate);
            calcWidth = (int) ((float) realWidth / rate);
        } else {
            rateW = (float) realWidth / ((float) maxWidth / 5.0F * 4.0F);
            rateH = (float) realHeight / ((float) maxHeight / 5.0F * 4.0F);
            rate = Math.max(rateW, rateH);
            calcHeight = (int) ((float) realHeight / rate);
            calcWidth = (int) ((float) realWidth / rate);
        }

        data.putInt("calcWidth", calcWidth);
        data.putInt("calcHeight", calcHeight);
        // Log.d("###realWidth:", "" + realWidth);
        // Log.d("###realHeight:", "" + realHeight);
        // Log.d("###maxWidth:", "" + maxWidth);
        // Log.d("###maxHeight:", "" + maxHeight);
        // Log.d("###calcWidth:", "" + calcWidth);
        // Log.d("###calcHeight:", "" + calcHeight);

        return data;
    }

    /**
     * 显示大图时，获得合适的尺寸
     * <p>
     * 使图片等比例缩放，并且宽（或高）刚好达到规定的最大宽（或高）。<br/>
     * 也就是将图片等比例缩放填充到某个尺寸（宽或高至少有一个值与规定的值相同）
     *
     * @param realWidth  {@code int} 图片的原始宽度
     * @param realHeight {@code int} 图片的原始高度
     * @param maxWidth   {@code int} 图片最大的宽度
     * @param maxHeight  {@code int} 图片最大的高度
     * @return {@code Bundle} 缩放参数
     */
    public static Bundle calculateBigImageSizeFix(int realWidth, int realHeight, int maxWidth, int maxHeight) {
        // 信息传输Bundle
        Bundle data = new Bundle();

        // 计算得出的宽度
        int calcWidth = 0;
        // 计算得出的高度
        int calcHeight = 0;
        // 缩放比例
        float rate = 0.0F;
        // 宽高比（原始）
        float scaleReal = 0.0F;
        // 宽高比（最大）
        float scaleMax = 0.0F;

        scaleReal = (float) realWidth / (float) realHeight;
        scaleMax = (float) maxWidth / (float) maxHeight;

        if (scaleReal > scaleMax) {
            // 图片宽。最终宽度不变，最终的高度会变小
            calcWidth = maxWidth;
            rate = (float) realWidth / (float) maxWidth;
            calcHeight = (int) ((float) realHeight / rate);
        } else if (scaleReal < scaleMax) {
            // 图片高。最终的宽度会变小，最终高度不变
            calcHeight = maxHeight;
            rate = (float) realHeight / (float) maxHeight;
            calcWidth = (int) ((float) realWidth / rate);
        } else {
            // 图片比例相同
            rate = 1.0f;
            calcWidth = maxWidth;
            calcHeight = maxHeight;
        }

        data.putInt("calcWidth", calcWidth);
        data.putInt("calcHeight", calcHeight);
        data.putFloat("rate", rate);

        return data;
    }

    /**
     * 将图片缩放为合适的大小
     *
     * @param filepath  {@code String} 图片资源路径
     * @param reqWidth  {@code int} 图片的预期宽度
     * @param reqHeight {@code int} 图片的预期高度
     * @return {@code Bitmap} 缩放后的图片
     */
    public static Bitmap decodeSampledBitmapFromResource(String filepath, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        try {
            // 是否有metadata信息
            boolean metadataFlag = false;
            // 旋转角度
            int degree = 0;
            // 图片宽度
            int w = 0;
            // 图片高度
            int h = 0;
            File capImageFile = new File(filepath);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(capImageFile);
            if (jpegMetadata != null) {
                metadataFlag = true;
                ArrayList<ImageMetadata.ImageMetadataItem> items = (ArrayList<ImageMetadata.ImageMetadataItem>) jpegMetadata.getItems();

                Log.d("###x1", items.toString());
                ImageInfo jpegInfo = Imaging.getImageInfo(capImageFile);
                Log.d("###x2", jpegInfo.toString());
                Log.d("###@width", "" + jpegInfo.getWidth());
                Log.d("###@height", "" + jpegInfo.getHeight());
                // 这里默认手机是竖屏
                w = Math.min(jpegInfo.getWidth(), jpegInfo.getHeight());
                h = Math.max(jpegInfo.getWidth(), jpegInfo.getHeight());
                for (int i = 0; i < items.size(); i++) {
                    TiffImageMetadata.TiffMetadataItem item = (TiffImageMetadata.TiffMetadataItem) items.get(i);
                    Log.d("###", item.toString() + "@" + item.getClass().getCanonicalName());
                    if ("orientation".equals(item.getKeyword().toLowerCase())) {
                        switch (item.getText()) {
                            case "6":
                                degree = 90;
                                break;
                            case "3":
                                degree = 180;
                                break;
                            case "8":
                                degree = -90;
                                break;
                            default:
                                degree = 0;
                        }
                    }
                }
            }

            // 解析图片时的参数
            BitmapFactory.Options _options = new BitmapFactory.Options();
            _options.outWidth = w;
            _options.outHeight = h;

            // 重设图片时的参数
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 计算 inSampleSize
            options.inSampleSize = calculateInSampleSize(_options, reqWidth, reqHeight);
            Log.d("###inSampleSize", "" + options.inSampleSize);
            options.inJustDecodeBounds = false;

            if (options.inSampleSize != 1) {
                // 对图片进行缩放
                bm = BitmapFactory.decodeFile(filepath, options);
            }

            if (metadataFlag && degree != 0) {
                Log.d("###", "==需要旋转" + degree + "°：" + filepath);

                // 先将缩放过的图片进行保存，这样才能保证旋转正常
                new FileUtil().saveBitmap(bm, capImageFile);
                // 旋转
                bm = RotateUtil.setRotate(filepath, degree);
            }

            if (bm == null) {
                bm = BitmapFactory.decodeFile(filepath, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 对于每个子对象中都有两个属性的list,根据第一个属性的的值key,返回第二个属性的值。
     */
    public static String GetTbKey(List<CommonDTO> list, String key) {
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).getP1().equals(key)) {
                return list.get(i).getP2();
            }
        }
        return "";
    }

    /**
     * 将Map的内容放入ContentValues
     *
     * @param map {@code Map<String, String>} 提供内容的map
     * @return {@code ContentValues}
     */
    public static ContentValues mapToCvS(Map<String, String> map) {
        // 键值对
        ContentValues cv = new ContentValues();
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> entry : set) {
            cv.put(entry.getKey(), (String) entry.getValue());
        }
        return cv;
    }

    /**
     * 将Map的内容放入ContentValues
     *
     * @param map {@code Map<String, Object>} 提供内容的map
     * @return {@code ContentValues}
     */
    public static ContentValues mapToCv(Map<String, Object> map) {
        return mapToCv(map, null);
    }

    /**
     * 将Map的内容放入ContentValues
     *
     * @param map {@code Map<String, Object>} 提供内容的map
     * @return {@code ContentValues}
     */
    public static ContentValues mapToCv(Map<String, Object> map, ContentValues cv) {
        // 键值对
        if (cv == null) {
            cv = new ContentValues();
        }
        Set<Entry<String, Object>> set = map.entrySet();
        for (Entry<String, Object> entry : set) {
            String key = entry.getKey();
            if (key.indexOf("V_") == 0) {
                // 如果key是系统保留值，就不会保存到表中
                continue;
            }
            Object o = entry.getValue();
            if (o instanceof Integer) {
                cv.put(key, (Integer) entry.getValue());
            } else {
                cv.put(key, CommonUtil.N2B((String) entry.getValue()));
            }
        }
        return cv;
    }

    /**
     * 将ContentValues的内容放入Map
     *
     * @param cv {@code ContentValues} 提供内容的cv
     * @return {@code HashMap<String, Object>}
     */
    public static HashMap<String, Object> cvToMap(ContentValues cv) {
        return cvToMap(cv, null);
    }

    /**
     * 将ContentValues的内容放入Map
     *
     * @param cv  {@code ContentValues} 提供内容的cv
     * @param map {@code HashMap<String, Object>} 接收内容的map
     * @return {@code HashMap<String, Object>}
     */
    public static HashMap<String, Object> cvToMap(ContentValues cv, HashMap<String, Object> map) {
        if (map == null) {
            map = new HashMap<String, Object>();
        }
        for (Entry<String, Object> entry : cv.valueSet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * 获得root权限。
     * <p>
     * 需要在Menifest.xml中增加 <code>&lt;uses-permission android:name="android.permission.ACCESS_SUPERUSER" /&gt;</code>
     */
    public void getRootPermission() {
        try {
            Process root = Runtime.getRuntime().exec("su");
            if (root != null) {
            }
            // DataOutputStream os = new
            // DataOutputStream(root.getOutputStream());
            // os.writeBytes("echo \"is root\" > /system/q.txt\n");
            // os.flush();
            // root.waitFor();

            // show("root");
            // try {
            // if (root.exitValue() != 255) {
            // } else {
            // show("not root");
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // show("not root");
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置用户权限
     *
     * @param user {@code BASIC_XZYQX系统用户} 用户
     */
    public static void processPermission(User user) {
        if (user != null) {
            // 权限Map
            Map<String, Boolean> permissionMap = user.getPermissionMap();
            if (permissionMap == null) {
                permissionMap = new HashMap<String, Boolean>();
            }

            user.setPermissionMap(permissionMap);
        }
    }

    /**
     * 获得用户的指定权限
     *
     * @param user           {@code BASIC_XZYQX系统用户} 用户
     * @param permissionName {@code String} 权限名
     * @return {@code boolean} 是否有该权限
     */
    public static boolean getUserPermission(User user, String permissionName) {
        // 是否有权限
        Boolean pFlag = false;
        if (user != null) {
            // 权限Map
            Map<String, Boolean> permissionMap = user.getPermissionMap();
            if (permissionMap != null) {
                pFlag = permissionMap.get(permissionName);
            }
            if (pFlag == null) {
                pFlag = false;
            }
        }

        return pFlag;
    }

    /**
     * 将数据库cursor当前的数据写入map中
     *
     * @param cursor {@code Cursor} 数据库指针
     * @param map    {@code Map<String, String>} 结果Map
     */
    public static Map<String, Object> cursorToMap(Cursor cursor, Map<String, Object> map) {
        for (int i = 0, columnCount = cursor.getColumnCount(); i < columnCount; i++) {
            int type = cursor.getType(i);
            String columnName = cursor.getColumnName(i);
            switch (type) {
//                case Cursor.FIELD_TYPE_STRING:
//                    map.put(columnName, N2B(cursor.getString(i)));
//                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    map.put(columnName, cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    map.put(columnName, cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    map.put(columnName, cursor.getBlob(i));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    map.put(columnName, null);
                    break;
                default:
                    map.put(columnName, N2B(cursor.getString(i)));
            }
        }
        return map;
    }

    /**
     * 将数据库cursor当前的数据写入map中
     * <p>使用自定义列名</p>
     *
     * @param cursor          {@code Cursor} 数据库指针
     * @param map             {@code Map<String, String>} 结果Map
     * @param columnNameArray {@code String[]} 自定义列名
     */
    public static Map<String, Object> cursorToCusMap(Cursor cursor, Map<String, Object> map, String[] columnNameArray) {
        for (int i = 0, columnCount = columnNameArray.length; i < columnCount; i++) {
            int type = cursor.getType(i);
            String columnName = columnNameArray[i];
            switch (type) {
                //case Cursor.FIELD_TYPE_STRING:
                //    map.put(columnName, N2B(cursor.getString(i)));
                //    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    map.put(columnName, cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    map.put(columnName, cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    map.put(columnName, cursor.getBlob(i));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    map.put(columnName, null);
                    break;
                default:
                    map.put(columnName, N2B(cursor.getString(i)));
            }
        }
        return map;
    }

    /**
     * 将数据库cursor当前的数据写入map中
     *
     * @param cursor {@code Cursor} 数据库指针
     * @param o      {@code JSONObject} 结果JsonObject
     */
    public static JSONObject cursorToJsonObject(Cursor cursor, JSONObject o) {
        for (int i = 0, columnCount = cursor.getColumnCount(); i < columnCount; i++) {
            int type = cursor.getType(i);
            String columnName = cursor.getColumnName(i);
            switch (type) {
//                case Cursor.FIELD_TYPE_STRING:
//                    o.put(columnName, N2B(cursor.getString(i)));
//                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    o.put(columnName, cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    o.put(columnName, cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    o.put(columnName, cursor.getBlob(i));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    o.put(columnName, null);
                    break;
                default:
                    o.put(columnName, N2B(cursor.getString(i)));
            }
        }
        return o;
    }

    /**
     * 将数据库cursor当前的数据写入map中
     * <p>使用自定义列名</p>
     *
     * @param cursor          {@code Cursor} 数据库指针
     * @param o               {@code JSONObject} 结果JsonObject
     * @param columnNameArray {@code String[]} 自定义列名
     */
    public static JSONObject cursorToCusJsonObject(Cursor cursor, JSONObject o, String[] columnNameArray) {
        for (int i = 0, columnCount = columnNameArray.length; i < columnCount; i++) {
            int type = cursor.getType(i);
            String columnName = columnNameArray[i];
            switch (type) {
                //case Cursor.FIELD_TYPE_STRING:
                //    map.put(columnName, N2B(cursor.getString(i)));
                //    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    o.put(columnName, cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    o.put(columnName, cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    o.put(columnName, cursor.getBlob(i));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    o.put(columnName, null);
                    break;
                default:
                    o.put(columnName, N2B(cursor.getString(i)));
            }
        }
        return o;
    }

    /**
     * 将数据库cursor当前的数据写入map中
     *
     * @param cursor {@code Cursor} 数据库指针
     * @param map    {@code Map<String, String} 结果Map
     */
    public static Map<String, String> cursorToMapS(Cursor cursor, Map<String, String> map) {
        for (int i = 0, columnCount = cursor.getColumnCount(); i < columnCount; i++) {
            map.put(cursor.getColumnName(i), N2B(cursor.getString(i)));
        }
        return map;
    }

    /**
     * 根据汉字返回对应的Gravity代码
     *
     * @param gravityStr {@code String} 对齐方式汉字
     * @return {@code int} Gravity代码
     */
    public static int getGravity(String gravityStr) {
        int gravityCode = Gravity.CENTER;
        if ("左".equals(gravityStr)) {
            gravityCode = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        } else if ("右".equals(gravityStr)) {
            gravityCode = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        }
        return gravityCode;
    }

    /**
     * 获得特定id的Map
     *
     * @param list {@code List<Map<String, Object>>) List
     * @param id   {@code String} Map 的 id
     * @return {@code Map<String, Object>} 指定id的Map
     */
    public static Map<String, Object> getTheMap(List<Map<String, Object>> list, String id) {
        Map<String, Object> map = null;
        for (Map<String, Object> m : list) {
            if (id.equals((String) m.get("IDS"))) {
                map = m;
                break;
            }
        }
        return map;
    }

    /**
     * 获得特定id的Map
     *
     * @param list {@code List<Map<String, String>>) List
     * @param id   {@code String} Map 的 id
     * @return {@code Map<String, String>} 指定id的Map
     */
    public static Map<String, String> getTheMapS(List<Map<String, String>> list, String id) {
        Map<String, String> map = null;
        for (Map<String, String> m : list) {
            if (id.equals((String) m.get("IDS"))) {
                map = m;
                break;
            }
        }
        return map;
    }

    /**
     * 对字符串进行编码
     *
     * @param str {@code String} 待编码字符串
     * @return {@code String} 编码后的字符串
     */
    public static String encBase64(String str) {
        String result = null;
        try {
            // Android
            result = Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
            // Server
            // result = new BASE64Encoder().encode(str.getBytes("UTF-8")).toString();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 对字符串进行解码
     *
     * @param str {@code String} 待解码字符串
     * @return {@code String} 解码后的字符串
     */
    public static String decBase64(String str) {
        String result = null;
        try {
            // Android
            result = new String(Base64.decode(str, Base64.DEFAULT), "UTF-8");
            // Server
            // result = new String(new BASE64Decoder().decodeBuffer(str), "UTF-8");
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static boolean makeRandomFlag() {
        boolean flag = false;

        return flag;
    }

    /**
     * 某个用户是否有指定的角色
     *
     * @param roles {@code String} 用户角色
     * @param role  {@code String} 指定的角色
     * @return {@code boolean} 存在标志
     */
    public static boolean hasRole(String roles, String role) {
        boolean flag = false;
        if (CommonUtil.checkNB(roles) && CommonUtil.checkNB(role)) {
            flag = roles.contains(role);
        }
        return flag;
    }

    /**
     * 打开附件
     *
     * @param attaFile {@code File} 附件
     * @param alias    {@code String} 别名
     * @param context  {@code Context} Context
     */
    public static void openAttaFile(File attaFile, String alias, Context context) {
        String fileName = attaFile.getName();
        Intent intent = null;
        if (attaFile.exists() && attaFile.isFile()) {
            try {
                if (checkEndsWithInStringArray(fileName, context.getResources().getStringArray(R.array.fileEndingImage))) {
                    intent = OpenAttachmentFiles.getImageFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingWebText))) {
                    intent = OpenAttachmentFiles.getHtmlFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingPackage))) {
                    intent = OpenAttachmentFiles.getApkFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingAudio))) {
                    intent = OpenAttachmentFiles.getAudioFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingVideo))) {
                    intent = OpenAttachmentFiles.getVideoFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingText))) {
                    intent = OpenAttachmentFiles.getTextFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, context.getResources()
                        .getStringArray(R.array.fileEndingPdf))) {
                    intent = OpenAttachmentFiles.getPdfFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingWord))) {
                    intent = OpenAttachmentFiles.getWordFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName,
                        context.getResources().getStringArray(R.array.fileEndingExcel))) {
                    intent = OpenAttachmentFiles.getExcelFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, context.getResources()
                        .getStringArray(R.array.fileEndingPPT))) {
                    intent = OpenAttachmentFiles.getPPTFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, context.getResources()
                        .getStringArray(R.array.fileEndingWps))) {
                    intent = OpenAttachmentFiles.getWpsFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, context.getResources()
                        .getStringArray(R.array.fileEndingEt))) {
                    intent = OpenAttachmentFiles.getEtFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, context.getResources()
                        .getStringArray(R.array.fileEndingDps))) {
                    intent = OpenAttachmentFiles.getDpsFileIntent(attaFile, context);
                    context.startActivity(intent);
                } else {
                    ((DbActivity) context).show("无法打开文件，请安装相应的软件！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((DbActivity) context).show("无法打开文件，请安装相应的软件！");
            }
        } else {
            ((DbActivity) context).show("文件不存在！");
        }
        // String postfix = CommonUtil.getPostfix(fileName);
        // if (CommonUtil.checkNB(postfix)) {
        // postfix = postfix.toLowerCase(Locale.CHINA);
        // // if (postfix.equals("doc"))
        // Intent intent = new Intent();
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.setAction(android.content.Intent.ACTION_VIEW);
        // intent.setClassName("cn.wps.moffice", "cn.wps.moffice.documentmanager.PreStartActivity");
        //
        // Uri uri = Uri.fromFile(attaFile);
        // intent.setData(uri);
        // try {
        // context.startActivity(intent);
        // } catch (ActivityNotFoundException e) {
        // e.printStackTrace();
        // }
        // }
    }

    /**
     * 检查后缀
     */
    public static boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    /**
     * 编辑Wps文件
     */
    public static boolean editWpsFile(File file, Context context) {
        boolean flag = false;

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        // 打开模式
        bundle.putString(WpsParam.OPEN_MODE, WpsParam.OpenMode.NORMAL);
        // 关闭时是否发送广播
        bundle.putBoolean(WpsParam.SEND_CLOSE_BROAD, true);
        // 保存时是否发送广播
        bundle.putBoolean(WpsParam.SEND_SAVE_BROAD, true);
        // 第三方应用的包名，用于对改应用合法性的验证
        bundle.putString(WpsParam.THIRD_PACKAGE, "com.cox.android.szsggl");
        // 清除打开记录
        bundle.putBoolean(WpsParam.CLEAR_TRACE, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(PackageName.NORMAL, ClassName.NORMAL);

        if (file == null || !file.exists()) {
            Toast toast = Toast.makeText(context, "文件为空或者不存在", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return flag;
        }

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            context.startActivity(intent);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 生成新行分隔符
     *
     * @param context {@code Context} 上下文
     * @return {@code View} 列分隔符View
     */
    public static View makeRowSpitter(Context context) {
        return makeRowSpitter(context, R.color.table_border_color);
    }

    /**
     * 生成新行分隔符
     *
     * @param context {@code Context} 上下文
     * @param color   {@code int} 颜色
     * @return {@code View} 列分隔符View
     */
    public static View makeRowSpitter(Context context, int color) {
        View splitter = new View(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        splitter.setLayoutParams(lp);
        splitter.setBackgroundColor(context.getResources().getColor(color));
        return splitter;
    }

    /**
     * 生成新列分隔符
     *
     * @param context {@code Context} 上下文
     * @return {@code View} 列分隔符View
     */
    public static View makeColumnSpitter(Context context) {
        return makeColumnSpitter(context, R.color.table_border_color);
    }

    /**
     * 生成新列分隔符
     *
     * @param context {@code Context} 上下文
     * @param color   {@code int} 颜色
     * @return {@code View} 列分隔符View
     */
    public static View makeColumnSpitter(Context context, int color) {
        View splitter = new View(context);
        LayoutParams lp = new LayoutParams(1, LayoutParams.MATCH_PARENT);
        splitter.setLayoutParams(lp);
        splitter.setBackgroundColor(context.getResources().getColor(color));
        return splitter;
    }

    /**
     * 查找信息在列表中的索引
     *
     * @param infoList {@code ArrayList<HashMap<String, Object>>} 信息List
     * @param value    {@code HashMap<String, Object>} 主字段值
     * @return {@code index} 信息索引
     */
    public static int getListItemIndex(ArrayList<HashMap<String, Object>> infoList, String value) {
        return getListItemIndex(infoList, value, "ids");
    }

    /**
     * 查找信息在列表中的索引
     *
     * @param infoList {@code ArrayList<HashMap<String, Object>>} 信息List
     * @param info     {@code HashMap<String, Object>} 信息
     * @return {@code index} 信息索引
     */
    public static int getListItemIndex(ArrayList<HashMap<String, Object>> infoList, HashMap<String, Object> info) {
        return getListItemIndex(infoList, info, "ids");
    }

    /**
     * 查找信息在列表中的索引
     *
     * @param infoList {@code ArrayList<HashMap<String, Object>>} 信息List
     * @param info     {@code HashMap<String, Object>} 信息
     * @param key      {@code String} 主字段名称。根据该字段的值来判断信息的索引。
     * @return {@code index} 信息索引
     */
    public static int getListItemIndex(ArrayList<HashMap<String, Object>> infoList, HashMap<String, Object> info, String key) {
        int index = -1;

        if (info != null && checkNB(key)) {
            index = getListItemIndex(infoList, (String) info.get(key), key);
        }

        return index;
    }

    /**
     * 查找信息在列表中的索引
     *
     * @param infoList {@code ArrayList<HashMap<String, Object>>} 信息List
     * @param value    {@code HashMap<String, Object>} 主字段值
     * @param key      {@code String} 主字段名称。根据该字段的值来判断信息的索引。
     * @return {@code index} 信息索引
     */
    public static int getListItemIndex(ArrayList<HashMap<String, Object>> infoList, String value, String key) {
        int index = -1;

        if (infoList != null && infoList.size() > 0 && value != null && checkNB(key)) {
            for (int i = 0, len = infoList.size(); i < len; i++) {
                HashMap<String, Object> vMap = (HashMap<String, Object>) infoList.get(i);
                HashMap<String, Object> o = (HashMap<String, Object>) vMap.get("info");

                String v_o = (String) o.get(key);
                if (CommonUtil.checkNB(v_o) && v_o.equals(value)) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    /**
     * 将View滚动到指定坐标
     *
     * @param scrollView {@code View} 待滚动的View
     * @param x          {@code int} x坐标
     * @param y          {@code int} y坐标
     */
    public static void scrollTo(View scrollView, int x, int y) {
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(scrollView, "scrollX", x);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", y);

        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(1000L);
        animators.playTogether(xTranslate, yTranslate);

        animators.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                // TODO Auto-generated method stub

            }
        });
        animators.start();
    }

    // 项目自定义方法区。结束====================================================

    /**
     * 删除第一个中文空格
     *
     * @param str {@code String } 要删除的字符串
     * @return {@code String} 删除后的字符串
     */
    public static String deleteFirstSpace(String str) {
        if (str != null && str.length() > 0) {
            str = str.replaceFirst("[　]", "");
        }
        return str;
    }

    /**
     * 删除字符串前面的中文空格
     *
     * @param str {@code String} 要删除的字符串
     * @return {@code String} 删除后的字符串
     */
    public static String deleteFrontSpace(String str) {
        if (str != null && str.length() > 0) {
            str = str.replaceFirst("[　]*", "");
        }
        return str;
    }

    /**
     * 根据阿拉伯数字来返回相应的中文数字
     *
     * @param i {@code int} 阿拉伯数字
     * @return {@code String} 中文数字
     */
    public static String getCNum(int i) {
        String[] cnum = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
        return cnum[i];
    }

    /**
     * 插入指定个数的中文空格
     *
     * @param num {@code int} 中文空格的个数
     * @return {@code String} 指定个数的中文空格
     */
    public static String insertCNSpace(int num) {
        String str = "";
        for (int i = 0; i < num; i++) {
            str = str + "　";
        }
        return str;
    }

    /**
     * 对errorMsg进行相应处理
     *
     * @param errorMsg {@code String} 错误信息
     * @return {@code String} 处理后的错误信息
     */
    public static String processErrMsg(String errorMsg) {
        boolean errFlag = false;
        if (CommonUtil.checkNB(errorMsg)) {
            try {
                if (errorMsg.indexOf("TSER") == 0) {
                    errorMsg = errorMsg.replaceAll("^.*?TSER", "").replaceAll("TSER.*\\s*", "");
                    // 如果数据库中的错误提示是以"ERR+数字"的形式给出的，
                    // 则使用反射机制查找已经赋值给静态字段的错误提示。
                    if (errorMsg.length() > 0 && errorMsg.indexOf("ERR") != -1) {
                        errorMsg = (String) getStaticFieldValue(errorMsg);
                    }
                    errFlag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!errFlag) {
            errorMsg = "保存失败，请检查后再保存！";
        }
        return errorMsg;
    }

    /**
     * 获得静态属性的值
     *
     * @param fieldName {@code String} 静态属性名称
     * @return {@code Object} 静态属性的值
     */
    public static Object getStaticFieldValue(String fieldName) {
        Object fieldValue = null;
        if (checkNB(fieldName)) {
            try {
                fieldValue = CommonParam.class.getField(fieldName).get(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldValue;
    }

    /**
     * 获得静态属性的值
     *
     * @param cls       {@code Class} 静态属性所在的类
     * @param fieldName {@code String} 静态属性名称
     * @return {@code Object} 静态属性的值
     */
    public static Object getStaticFieldValue(Class cls, String fieldName) {
        Object fieldValue = null;
        if (checkNB(fieldName)) {
            try {
                fieldValue = cls.getField(fieldName).get(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldValue;
    }

    /**
     * 获得对象属性的值
     *
     * @param cls       {@code Class} 属性所在的类
     * @param obj       {@code Object} 对象
     * @param fieldName {@code String} 属性名称
     * @return {@code Object} 属性的值
     */
    public static Object getFieldValue(Class cls, Object obj, String fieldName) {
        Object fieldValue = null;
        if (checkNB(fieldName)) {
            try {
                fieldValue = cls.getField(fieldName).get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldValue;
    }

    /**
     * 根据给定的类型名和字段名，返回R文件中的字段的值
     *
     * @param typeName  {@code String} 属于哪个类别的属性 （id,layout,drawable,string,color,attr......）
     * @param fieldName {@cdoe String}  字段名
     * @param context   {@code Context} 上下文
     * @return {@code int} 字段的值
     */
    public static int getFieldValue(String typeName, String fieldName, Context context) {
        int i = -1;
        try {
            Class<?> clazz = Class.forName(context.getPackageName() + ".R$" + typeName);
            i = clazz.getField(fieldName).getInt(null);
        } catch (Exception e) {
            Log.d("" + context.getClass(), "没有找到" + context.getPackageName() + ".R$" + typeName + "类型资源 " + fieldName + "请copy相应文件到对应的目录.");
            return -1;
        }
        return i;
    }

    /**
     * 获得静态方法的返回值
     *
     * @param methodName {@code String} 静态方法名称
     * @return {@code Object} 静态方法的返回值
     */
    public static Object getStaticMethodValue(String methodName) {
        Object fieldValue = null;
        if (checkNB(methodName)) {
            try {
                fieldValue = CommonParam.class.getMethod(methodName).invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldValue;
    }

    /**
     * 用反射的方法来调用CommonParam程序中的字典List
     *
     * @param codeType {@code String} 字典类型
     * @return {@code List<CodeNameDTO>} 字典List
     */
    public static List<CodeNameDTO> getCodeList(String codeType) {
        List<CodeNameDTO> codeList = null;
        if (CommonUtil.checkNB(codeType)) {
            try {
                codeList = (List<CodeNameDTO>) CommonUtil.getStaticMethodValue((String) CommonUtil
                        .getStaticFieldValue("CODE_" + codeType) + "_LIST");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return codeList;
    }

    /**
     * 用反射的方法来调用CommonParam程序中的字典Map
     *
     * @param codeType {@code String} 字典类型
     * @return {@code Map<String, String>} 字典Map
     */
    public static Map<String, String> getCodeMap(String codeType) {
        Map<String, String> codeMap = null;
        if (CommonUtil.checkNB(codeType)) {
            try {
                codeMap = (Map<String, String>) CommonUtil.getStaticMethodValue((String) CommonUtil
                        .getStaticFieldValue("CODE_" + codeType) + "_MAP");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return codeMap;
    }

    /**
     * 获得不重复的字符串
     *
     * @param targetStr    {@code String} 要处理的字符串
     * @param oldDelimiter {@code String} 旧分隔符（默认为英文逗号“,”）
     * @param newDelimiter {@code String} 新分隔符（默认为英文逗号“,”）
     * @return {@code String} 处理好的字符串
     */
    public static String getMonoString(String targetStr, String oldDelimiter, String newDelimiter) {
        String resultStr = "";
        if (targetStr != null && targetStr.length() > 0) {
            if (oldDelimiter == null) {
                oldDelimiter = ",";
            }
            if (newDelimiter == null) {
                newDelimiter = ",";
            }
            List<String> strList = Arrays.asList(targetStr.split("[" + oldDelimiter + "]"));
            Set<String> strSet = new HashSet<String>();
            for (String o : strList) {
                strSet.add(o);
            }
            for (String o : strList) {
                resultStr = resultStr + newDelimiter + o;
            }
            if (resultStr.length() > 0) {
                resultStr = resultStr.substring(1);
            }
        }
        return resultStr;
    }

    /**
     * 检查字符串是否为空(<span style="color:red;">N</span>ull)或为空字符串(<span style="color:red;">B</span>lank:"") <br />
     * <span style="color:red;font-weight:bold;font-size:15px;">如果为空或为空字符串，返回<span style="font-size:19px;">{@code false}
     * </span>，</span> <br />
     * <span style="color:green;font-weight:bold;font-size:15px;">如果不为空或不为空字符串，则返回 <span style="font-size:19px;">
     * {@code true}</span>。</span>
     *
     * @param str {@code String} 要检查的字符串
     * @return {@code Boolean} 检查结果
     */
    public static Boolean checkNB(String str) {
        Boolean flag = true;
        if (str == null || str.length() == 0) {
            flag = false;
        }
        return flag;
    }

    /**
     * 获得当前日期时间
     * <p>
     * 需要commons-lang3包
     *
     * @param pattern {@code String} 日期模式字符串
     * @return {@code String} 当前日期时间字符串
     */
    public static String getDT(String pattern) {
        String dtStr = null;
        if (CommonUtil.checkNB(pattern)) {
            // dtStr = DateFormatUtils.format(new Date(), pattern);
            SimpleDateFormat s = new SimpleDateFormat(pattern, Locale.CHINA);
            dtStr = s.format(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        } else {
            dtStr = "";
        }
        return dtStr;
    }

    /**
     * 获得当前日期时间
     *
     * @param pattern {@code String} 日期模式字符串
     * @param theTime {@code long} 时间的毫秒表示
     * @return {@code String} 当前日期时间字符串
     */
    public static String getDT(String pattern, long theTime) {
        String dtStr = null;
        if (CommonUtil.checkNB(pattern)) {
            SimpleDateFormat s = new SimpleDateFormat(pattern, Locale.CHINA);
            dtStr = s.format(new Timestamp(theTime));
        } else {
            dtStr = "";
        }
        return dtStr;
    }

    /**
     * 获得当前日期时间
     * <p>
     * 不需要commons-lang3包
     *
     * @return {@code String} 当前日期时间字符串
     */
    public static String getDTC() {
        StringBuilder dtSb = new StringBuilder("");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
        dtSb.append(cal.get(Calendar.YEAR)).append("-");
        int month = cal.get(Calendar.MONTH);
        if (month < 10) {
            dtSb.append("0");
        }
        dtSb.append(month).append("-");
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            dtSb.append("0");
        }
        dtSb.append(day);
        dtSb.append(" ");
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            dtSb.append("0");
        }
        dtSb.append(hour).append(":");
        int min = cal.get(Calendar.MINUTE);
        if (min < 10) {
            dtSb.append("0");
        }
        dtSb.append(min).append(":");
        int sec = cal.get(Calendar.SECOND);
        if (sec < 10) {
            dtSb.append("0");
        }
        dtSb.append(sec);

        return dtSb.toString();
    }

    /**
     * 获得当前日期时间
     * <p>
     * 日期模式字符串为"yyyy-MM-dd HH:mm:ss"。
     *
     * @return {@code String} 当前日期时间字符串
     */
    public static String getDT() {
        return getDT("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获得当前日期时间
     * <p>
     * 日期模式字符串为"yyyy-MM-dd HH:mm:ss"。
     *
     * @param theTime {@code long} 时间的毫秒表示
     * @return {@code String} 当前日期时间字符串
     */
    public static String getDT(long theTime) {
        return getDT("yyyy-MM-dd HH:mm:ss", theTime);
    }

    /**
     * 获得当前日期时间
     * <p>
     * 日期模式字符串为"yyyy-MM-dd HH:mm:ss.SSS"。
     *
     * @return {@code String} 当前日期时间字符串
     */
    public static String getDT_S() {
        return getDT("yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 获得当前日期时间（带毫秒）
     * <p>
     * 日期模式字符串为"yyyyMMd_HHmmss_SSS"。
     *
     * @return {@code String} 当前日期时间字符串
     */
    public static String getMDT() {
        return getDT("yyyyMMd_HHmmss_SSS");
    }

    /**
     * 对时间进行格式化处理
     *
     * @param timeStr {@code String} 时间字符串
     * @return {@code String} 格式化后的时间字符串
     */
    public static String getHMS(String timeStr) {
        StringBuilder resultSb = new StringBuilder("");
        if (checkNB(timeStr) && timeStr.indexOf(":") != -1) {
            timeStr = timeStr.replaceAll("[ 　：]", "");
            if (timeStr.indexOf(":") == timeStr.length() - 1) {
                timeStr = timeStr.substring(0, timeStr.length() - 1);
            }

            String[] timeArray = timeStr.split("[:]");
            if (timeArray.length >= 2) {
                if (timeArray[0].length() == 1) {
                    resultSb.append("0" + timeArray[0]);
                } else {
                    resultSb.append(timeArray[0]);
                }
                resultSb.append(":");
                if (timeArray[1].length() == 1) {
                    resultSb.append("0" + timeArray[1]);
                } else {
                    resultSb.append(timeArray[1]);
                }
                if (timeArray.length == 3) {
                    resultSb.append(":");
                    if (timeArray[2].length() == 1) {
                        resultSb.append("0" + timeArray[2]);
                    } else {
                        resultSb.append(timeArray[2]);
                    }
                }
            }

        }

        return resultSb.toString();
    }

    /**
     * 对日期进行格式化处理
     *
     * @param timeStr {@code String} 日期字符串
     * @return {@code String} 格式化后的日期字符串
     */
    public static String getYMD(String timeStr) {
        StringBuilder resultSb = new StringBuilder("");
        if (checkNB(timeStr) && timeStr.indexOf("-") != -1) {

            String[] dateArray = timeStr.split("[-]");
            ;
            if (dateArray.length == 3) {
                resultSb.append(dateArray[0] + "-");
                if (dateArray[1].length() == 1) {
                    resultSb.append("0" + dateArray[1]);
                } else {
                    resultSb.append(dateArray[1]);
                }
                resultSb.append("-");
                if (dateArray[2].length() == 1) {
                    resultSb.append("0" + dateArray[2]);
                } else {
                    resultSb.append(dateArray[2]);
                }
            }

        }

        return resultSb.toString();
    }

    /**
     * 根据模板文件名获得模板名称
     *
     * @param tmpName {@code String} 模板文件名
     * @return {@code String} 模板名称
     */
    public static String getTempleteName(String tmpName) {
        String nameStr = null;
        if (checkNB(tmpName)) {
            nameStr = tmpName.substring(0, tmpName.lastIndexOf("_"));
        }
        return nameStr;
    }

    /**
     * 进行转义，替换字符串中的一些字符
     *
     * @param str {@code String} 要进行替换的字符串
     * @return {@code String} 替换结果
     */
    public static String reEscape(String str) {
        if (checkNB(str)) {
            str = str.replaceAll("[&]", "&amp;").replaceAll("[<]", "&lt;").replaceAll("[>]", "&gt;")
                    .replaceAll("[\"]", "&quot;").replaceAll("[']", "\\\\\\'");
        }
        return str;
    }

    /**
     * 进行转义，替换字符串中的一些字符（仅替换英文单、双引号）
     *
     * @param str {@code String} 要进行替换的字符串
     * @return {@code String} 替换结果
     */
    public static String reEscapeQuot(String str) {
        if (checkNB(str)) {
            str = str.replaceAll("[']", "\\\\\\'");
        }
        return str;
    }

    public static void main(String[] args) {
        Q.p(getMDT());
    }

    /**
     * 进行转义，替换字符串中的一些字符，将“&”转为“&amp;”
     *
     * @param str {@code String} 要进行替换的字符串
     * @return {@code String} 替换结果
     */
    public static String reEscapeAmp(String str) {
        if (checkNB(str)) {
            str = str.replaceAll("[&]", "&amp;");
        }
        return str;
    }

    /**
     * 返回一个UUID。
     *
     * @return {@code String} UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("[-]", "");
    }

    /**
     * id种子
     */
    private static long IDSeed;
    private static String oldseedtime;

    /**
     * UUID生成方法，参考邢志云的 C# 方法
     */
    public static String GetNextID() {
        if (IDSeed > 99999)
            IDSeed = 0;
        String newseedtime = getDT("yyMMddHHmmssSSS");
        newseedtime = newseedtime.substring(0, newseedtime.length() - 1);
        if (IDSeed > 9999) {
            if (newseedtime.equals(oldseedtime)) {
                IDSeed = 0;
                oldseedtime = newseedtime;
            }
        } else {
            oldseedtime = newseedtime;
        }
        return newseedtime + new DecimalFormat("00000").format(IDSeed++)
                + ((("" + UUID.randomUUID().hashCode()) + "123456")).substring(2, 4);
    }

    /**
     * 将Map的key和value位置交换后返回。
     * <p>
     * 如果原Map的value为null或""，则将该条数据舍弃。
     *
     * @param oldMap {@code Map<String, String>} 原Map
     * @param {@code Map<String, String>} 新Map
     */
    public static Map<String, String> getRevMap(Map<String, String> oldMap) {
        Map<String, String> newMap = new HashMap<String, String>();
        if (oldMap.size() > 0) {
            Set<String> keySet = oldMap.keySet();
            for (String k : keySet) {
                String v = oldMap.get(k);
                if (checkNB(v)) {
                    newMap.put(v, k);
                }
            }
        }
        return newMap;
    }

    /**
     * 将List转变成Map后返回。
     *
     * @param oldList {@code List<CodeNameDTO>} 原List
     * @param {@code  Map<String, String>} 新Map
     */
    public static Map<String, String> getListMap(List<CodeNameDTO> oldList) {
        Map<String, String> newMap = new HashMap<String, String>();
        if (oldList != null && oldList.size() > 0) {
            for (CodeNameDTO c : oldList) {
                newMap.put(c.getCode(), c.getName());
            }
        }
        return newMap;
    }

    /**
     * 将sql语句进行转义，以供给Oracle使用
     *
     * @param sqlStr {@code String} sql语句
     * @return {@code String} 转义后的语句
     */
    public static String escapeOracle(String sqlStr) {
        if (CommonUtil.checkNB(sqlStr)) {
            sqlStr = sqlStr.replaceAll("'", "\\'").replaceAll("\"", "\\\"").replaceAll("%", "\\%")
                    .replaceAll("_", "\\_");
        }
        return sqlStr;
    }

    /**
     * 获得随机温度
     *
     * @param monthStr {@code String} 月份
     * @return {@code String} 温度
     */
    public static String getRandomTemp(String monthStr) {
        String temp = null;
        try {
            if (CommonUtil.checkNB(monthStr)) {
                Double maxT = null;
                Double minT = null;
                Integer month = Integer.valueOf(monthStr);
                if (month >= 12 || (month >= 1 && month <= 2)) {
                    // 冬天
                    minT = 21D;
                    maxT = 23D;
                } else if (month >= 3 && month <= 5) {
                    // 春天
                    minT = 22D;
                    maxT = 24D;
                } else if (month >= 6 && month <= 8) {
                    // 夏天
                    minT = 23D;
                    ;
                    maxT = 26D;
                } else if (month >= 9 && month <= 11) {
                    // 秋天
                    minT = 22D;
                    maxT = 24D;
                }
                boolean plus = (Integer.valueOf(new DecimalFormat("0").format(Math.random() * 100)) % 2) == 0 ? true
                        : false;
                if (plus) {
                    temp = new DecimalFormat("0.0").format((minT + maxT + Math.random()) / 2D);
                } else {
                    temp = new DecimalFormat("0.0").format((minT + maxT - Math.random()) / 2D);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (temp == null) {
            temp = "";
        }
        return temp;

    }

    /**
     * 获得随机时间(汇报时间，一天一次)
     *
     * @param timeStr {@code String} 时间字符串
     * @return {@code String} 随机时间
     */
    public static String getRandomTime(String timeStr) {
        String time = null;
        try {
            SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            if (timeStr.substring(11).equals("19:00:00")) {
                Date date = dateTimeFmt.parse(timeStr.substring(0, 11) + "08:30:00");
                boolean plus = (Integer.valueOf(new DecimalFormat("0").format(Math.random() * 100)) % 2) == 0 ? true
                        : false;
                long timeLong = (40 - 28) * 60 * 1000;
                long plusValue = Long.valueOf(new DecimalFormat("0").format(Math.random() * timeLong / 2));
                if (plus) {
                    timeLong = date.getTime() + 24 * 60 * 60 * 1000 + plusValue;
                } else {
                    timeLong = date.getTime() + 24 * 60 * 60 * 1000 - plusValue;
                }
                time = dateTimeFmt.format(new Date(timeLong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (time == null) {
            time = "";
        }
        return time;
    }

    /**
     * 获得随机时间（不带秒）(汇报时间，一天一次)
     *
     * @param timeStr {@code String} 时间字符串
     * @return {@code String} 随机时间
     */
    public static String getRandomTimeNoS(String timeStr) {
        String time = null;
        try {
            SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            if (timeStr.substring(11).equals("19:00")) {
                Date date = dateTimeFmt.parse(timeStr.substring(0, 11) + "08:30");
                boolean plus = (Integer.valueOf(new DecimalFormat("0").format(Math.random() * 100)) % 2) == 0 ? true
                        : false;
                long timeLong = (40 - 28) * 60 * 1000;
                long plusValue = Long.valueOf(new DecimalFormat("0").format(Math.random() * timeLong / 2));
                if (plus) {
                    timeLong = date.getTime() + 24 * 60 * 60 * 1000 + plusValue;
                } else {
                    timeLong = date.getTime() + 24 * 60 * 60 * 1000 - plusValue;
                }
                time = dateTimeFmt.format(new Date(timeLong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (time == null) {
            time = "";
        }
        return time;

    }

    public static StringComparator getStringComparator() {
        return new StringComparator();
    }

    /**
     * 返回某天是一周中的第几天。（周日为第一天）
     */
    public static int getDayOfWeek(String dateStr) {
        int dow = 2; // 默认是周一
        if (checkNB(dateStr)) {
            SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
            try {
                c.setTime(dateTimeFmt.parse(dateStr));
            } catch (ParseException e) {
                try {
                    c.setTime(dateTimeFmt.parse("2000-01-01"));
                } catch (ParseException e1) {

                }
            }

            dow = c.get(Calendar.DAY_OF_WEEK);

        }
        return dow;
    }

    /**
     * 检测某个时间是白班还是夜班
     *
     * @param timeStr {@code String} 待检测的时间
     * @return {@code Integer} 白班还是夜班 <br/>
     * 0：无效 <br/>
     * 1：白班 <br/>
     * 2：夜班
     */
    public static Integer checkJjb(String timeStr) {
        int flag = 0;
        if (checkNB(timeStr)) {
            try {
                // 日期时间格式
                SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
                // 如果结束时间在开始时间之后
                Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
                Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
                Calendar c3 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
                Calendar c4 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.CHINA);
                c.setTime(dateTimeFmt.parse(timeStr));
                c1 = (Calendar) c.clone();
                c1.set(Calendar.HOUR_OF_DAY, 7);
                c2 = (Calendar) c.clone();
                c2.set(Calendar.HOUR_OF_DAY, 9);
                c3 = (Calendar) c.clone();
                c3.set(Calendar.HOUR_OF_DAY, 18);
                c4 = (Calendar) c.clone();
                c4.set(Calendar.HOUR_OF_DAY, 20);

                if (c.getTimeInMillis() >= c1.getTimeInMillis() && c.getTimeInMillis() <= c2.getTimeInMillis()) {
                    // 白班
                    flag = 1;
                } else if (c.getTimeInMillis() >= c3.getTimeInMillis() && c.getTimeInMillis() <= c4.getTimeInMillis()) {
                    // 夜班
                    flag = 2;
                } else {
                    flag = 0;
                }

            } catch (ParseException e) {
                e.printStackTrace();
                flag = 0;
            }
        }
        return flag;
    }

    /**
     * 格式化数字并输出（用"0"补空）
     *
     * @param num    {@code Integer} 要格式化的数字
     * @param length {@code Integer} 结果长度
     * @param {@code String) 格式化后的数字
     */
    public static String getStringInt(Integer num, Integer length) {
        StringBuilder sb = new StringBuilder("");
        int len = num.toString().length();
        if (len < length) {
            for (int i = 1; i <= (length - len); i++) {
                sb.append("0");
            }
        }
        sb.append(num.toString());
        return sb.toString();
    }

    /**
     * 处理数字字符，去掉无用的0
     * <p>
     * 如果参数numStr为null或为空字符串，会返回字符"0"
     *
     * @param numStr {@code String} 待处理的字符
     * @return {@code String} 处理后的结果
     */
    public static String removeEndZero(String numStr) {
        return removeEndZero(numStr, true);
    }

    /**
     * 检查文件是否是指定的后缀
     *
     * @param fileName {@code String} 文件名
     * @param fileType {@code String} 后缀(带.)
     * @return boolean
     */
    public static boolean checkFileType(String fileName, String fileType) {
        boolean typeFlag = false;
        if (fileName.indexOf(fileType) != -1 && fileName.lastIndexOf(fileType) == (fileName.length() - 4)) {
            typeFlag = true;
        }
        return typeFlag;
    }

    /**
     * 处理数字字符，去掉无用的0
     *
     * <p>
     * 如果参数numStr为null或为空字符串，当numFlag为false时，会返回空字符串；当numFlag为true时，会返回字符"0"。
     *
     * @param numStr  {@code String} 待处理的字符
     * @param numFlag {@code boolean} 是否必须返回数字字符。
     * @return {@code String} 处理后的结果
     */
    public static String removeEndZero(String numStr, boolean numFlag) {
        if (CommonUtil.checkNB(numStr)) {
            if (numStr.contains(".")) {
                try {
                    // 小数
                    Double d = Double.valueOf(numStr);
                    if (d == 0D) {
                        // 0
                        numStr = "0";
                    } else if (d > -1D && d < 1D) {
                        // 绝对值小于1
                        numStr = d.toString();
                    } else {
                        // 绝对值大于1
                        if (d.doubleValue() == (double) d.intValue()) {
                            // 整数
                            numStr = "" + d.intValue();
                        } else {
                            // 有小数部分
                            numStr = d.toString();
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                // 整数
            }
        } else {
            if (numFlag) {
                numStr = "0";
            } else {
                numStr = "";
            }
        }
        return numStr;
    }

    /**
     * 获得文件名后缀
     *
     * @param fileName {@code String} 文件名
     * @return {@code String}
     */
    public static String getPostfix(String fileName) {
        String postfix;
        if (checkNB(fileName)) {
            postfix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        } else {
            postfix = "";
        }
        return postfix;
    }

    /**
     * 判断字符串是否为null。如果为null，将字符串设为""
     *
     * @param str {@code String} 待判断的字符串
     * @return {@code String} 处理后的字符串
     */
    public static String N2B(String str) {
        if (str == null) {
            str = "";
        } else if (str.toLowerCase().equals("null")) {
            str = "";
        }
        return str;
    }

    /**
     * 判断字符串是否为null。如果为null，将字符串设为默认值
     *
     * @param str          {@code String} 待判断的字符串
     * @param defaultValue {@code String} 默认值
     * @return {@code String} 处理后的字符串
     */
    public static String N2B(String str, String defaultValue) {
        if (str == null) {
            str = defaultValue;
        } else if (str.toLowerCase().equals("null")) {
            str = defaultValue;
        }
        return str;
    }

    /**
     * 将秒格式为x分x秒的形式
     *
     * @param s      {@code String} 秒数
     * @param {@code String} 格式化后的文本
     */
    public static String formatSeconds(String s) {
        StringBuffer sb = new StringBuffer("");
        int sec = 0;
        try {
            sec = Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        if (sec >= 60) {
            sb.append(sec / 60).append("'");
        }
        sb.append(sec % 60).append("\"");
        return sb.toString();
    }

    /**
     * 将毫秒格式为x分x秒的形式
     *
     * @param sec    {@code int} 毫秒数
     * @param {@code String} 格式化后的文本
     */
    public static String formatMSeconds(int sec) {
        sec = sec / 1000;
        return formatSeconds("" + sec);
    }

    /**
     * 检测日期格式是否有效
     * <p>
     * 格式为“yyyy-MM-dd”，位数不够时要用0补齐。
     *
     * @param str {@code String} 日期字符串
     * @return {@code boolean} 是否有效
     */
    public static boolean checkDateValidFull(String str) {
        // 结果
        boolean flag = false;
        if (CommonUtil.checkNB(str)) {
            String regex = "^(2[0-9]{3})\\-(0?[1-9]|1[0-2])\\-((0?[1-9])|((1|2)[0-9])|30|31)$";
            Pattern pat = Pattern.compile(regex);
            Matcher mat = pat.matcher(str);
            flag = mat.matches();
        }
        return flag;
    }

    /**
     * 检测时间格式是否有效
     * <p>
     * 格式为“HH:mm:ss”，位数不够时要用0补齐。
     *
     * @param str {@code String} 时间字符串
     * @return {@code boolean} 是否有效
     */
    public static boolean checkTimeValidFull(String str) {
        // 结果
        boolean flag = false;
        if (CommonUtil.checkNB(str)) {
            if (str.split(":").length == 2) {
                str = str + ":00";
            }
            String regex = "^(0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
            Pattern pat = Pattern.compile(regex);
            Matcher mat = pat.matcher(str);
            flag = mat.matches();
        }
        return flag;
    }

    /**
     * 从url中获得服务器地址
     *
     * @param url {@code String} url地址
     * @return {@code String} 服务器地址
     */
    public static String getServerAddress(String url) {
        if (CommonUtil.checkNB(url)) {
            int index = url.indexOf("/");
            if (index != -1) {
                url = url.substring(0, index);
            }
            index = url.indexOf(":");
            if (index != -1) {
                url = url.substring(0, index);
            }
        } else {
            url = "";
        }
        return url;
    }

    /**
     * 将JSONObject转变成Map后返回。
     *
     * @param json   {@code JSONObject} JSONObject
     * @param {@code Map<String, Object>} 新Map
     */
    public static HashMap<String, Object> jsonToMap(JSONObject json) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (json != null) {
            for (Entry<String, Object> e: json.entrySet()) {
                map.put(e.getKey(), e.getValue());
            }
        }

        return map;
    }

    /**
     * 将Map转变成JSONObject后返回。
     *
     * @param map   {@code Map<String, Object>} map
     * @param {@code JSONObject} 新JSONObject
     */
    public static JSONObject mapToJson(HashMap<String, Object> map) {
        JSONObject json = new JSONObject();
        if (map != null) {
            for (Entry<String, Object> e: map.entrySet()) {
                json.put(e.getKey(), e.getValue());
            }
        }

        return json;
    }
}

/**
 * 自定义比较类，用于将字符按数字格式排序
 */
class StringComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
        Integer i1 = null;
        Integer i2 = null;
        try {
            i1 = Integer.parseInt(s1);
        } catch (NumberFormatException e) {
            i1 = 0;
        }
        try {
            i2 = Integer.parseInt(s2);
        } catch (NumberFormatException e) {
            i2 = 0;
        }
        return i1 - i2;
    }
}

/**
 * 打开附件的类
 */
class OpenAttachmentFiles {
    /**
     * Android获取一个用于打开HTML文件的intent
     */
    public static Intent getHtmlFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider")
                    .scheme("content").encodedPath(file.toString()).build();
        }
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    /**
     * Android获取一个用于打开图片文件的intent
     */
    public static Intent getImageFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "image/*");

        return intent;
    }

    /**
     * Android获取一个用于打开PDF文件的intent
     */
    public static Intent getPdfFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/pdf");

        return intent;
    }

    /**
     * Android获取一个用于打开文本文件的intent
     */
    public static Intent getTextFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "text/plain");

        return intent;
    }

    /**
     * Android获取一个用于打开音频文件的intent
     */
    public static Intent getAudioFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        intent.setDataAndType(uri, "audio/*");

        return intent;
    }

    /**
     * Android获取一个用于打开视频文件的intent
     */
    public static Intent getVideoFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        intent.setDataAndType(uri, "video/*");

        return intent;
    }

    /**
     * Android获取一个用于打开CHM文件的intent
     */
    public static Intent getChmFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/x-chm");

        return intent;
    }

    /**
     * Android获取一个用于打开Word文件的intent
     */
    public static Intent getWordFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/msword");

        return intent;
    }

    /**
     * Android获取一个用于打开Excel文件的intent
     */
    public static Intent getExcelFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.ms-excel");

        return intent;
    }

    /**
     * Android获取一个用于打开PPT文件的intent
     */
    public static Intent getPPTFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

        return intent;
    }

    /**
     * Android获取一个用于打开APK文件的intent
     */
    public static Intent getApkFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            uri = Uri.fromFile(file);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        return intent;
    }

    /**
     * Android获取一个用于打开WPS文件的intent
     */
    public static Intent getWpsFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/kswps");

        return intent;
    }

    /**
     * Android获取一个用于打开ET文件的intent
     */
    public static Intent getEtFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/kset");

        return intent;
    }

    /**
     * Android获取一个用于打开Dps文件的intent
     */
    public static Intent getDpsFileIntent(File file, Context ctx) {
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, CommonParam.FILE_PROVIDER_NAME, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/ksdps");

        return intent;
    }
}