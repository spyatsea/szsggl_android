/*
 * Copyright (c) 2011 山西考科思 版权所有
 */
package com.cox.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;

import java.io.File;

/**
 * 旋转图片的工具类
 *
 * @author 乔勇(Jacky Qiao)
 */
public class RotateUtil {
    /**
     * 最大高度
     */
    public static int FIX_HEIGHT;
    /**
     * 最大宽度
     */
    public static int FIX_WIDTH;

    /**
     * 将图片旋转一定的角度后保存
     * <p>
     * <b>本方法保存的源图片名与目标图片名相同。</b>
     * <p>
     * 只能旋转并保存后缀为jpg、jpeg、png、gif的图片。
     *
     * @param sourceFileName {@code String} 源图片名
     * @param degree         {@code int} 旋转角度，可以是负值（逆时针旋转）
     */
    public static void rotate(String sourceFileName, int degree) {
        rotate(sourceFileName, sourceFileName, degree);
    }

    /**
     * 将图片旋转一定的角度后保存
     * <p>
     * 只能旋转并保存后缀为jpg、jpeg、png、gif的图片。
     * <p>
     * 源图片与目标图片的后缀可以不同。例如：可以将“1.jpg”转换为“1.png”。
     *
     * @param sourceFileName {@code String} 源图片名
     * @param targetFileName {@code String} 目标图片名
     * @param degree         {@code int} 旋转角度，可以是负值（逆时针旋转）
     */
    public static void rotate(String sourceFileName, String targetFileName, int degree) {
        try {
            Bitmap resizedBitmap = setRotate(sourceFileName, degree);
            if (resizedBitmap != null) {
                // 目标文件
                File fOut = new File(targetFileName);
                new FileUtil().saveBitmap(resizedBitmap, fOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将图片旋转一定的角度后保存
     * <p>
     * 只能旋转并保存后缀为jpg、jpeg、png、gif的图片。
     * <p>
     * 源图片与目标图片的后缀可以不同。例如：可以将“1.jpg”转换为“1.png”。
     *
     * @param sourceFileName {@code String} 源图片名
     * @param targetFileName {@code String} 目标图片名
     * @param degree         {@code int} 旋转角度，可以是负值（逆时针旋转）
     */
    public static void rotate_old(String sourceFileName, String targetFileName, int degree) {
        try {
            // 源文件
            //File fIn = new File(sourceFileName);
            // 目标文件
            File fOut = new File(targetFileName);
            Bitmap bitmap = BitmapFactory.decodeFile(sourceFileName);
            // 图片宽度
            int w = bitmap.getWidth();
            // 图片高度
            int h = bitmap.getHeight();
            // 新图片的高和宽
            int width = 0, height = 0;
            Log.d("###@@w", "" + w);
            Log.d("###@@h", "" + h);
            // 如果角度是90的倍数
            if (degree % 180 == 0) {
                // 如果角度是180的倍数，如0、180。目标图片的宽和高与原图片相同。
                width = w;
                height = h;
            } else {
                // 如果角度不是180的倍数，如90、270。目标图片的宽和高与原图片相反。
                width = h;
                height = w;
            }
            Log.d("###xxzwh", "" + width + ":" + height);

            // 方法1：直接旋转（首选方法）
            Matrix matrix = new Matrix();
            matrix.setRotate(degree);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            // 方法2：使用Canvas重绘
            // 新Bitmap
//            Bitmap resizedBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
//            Canvas canvas = new Canvas(resizedBitmap);
//            // 主要以这个对象调用旋转方法
//            Matrix matrix = new Matrix();
//            // 计算旋转中心点
//            int x = Math.min(w, h);
//            // 进行旋转
//            matrix.setRotate(degree, x / 2, x / 2);
//            Paint paint = new Paint();
//            // 设置抗锯齿,防止过多的失真
//            paint.setAntiAlias(true);
//            canvas.drawBitmap(bitmap, matrix, paint);

            new FileUtil().saveBitmap(resizedBitmap, fOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将图片旋转一定的角度后保存
     * <p>
     * 只能旋转并保存后缀为jpg、jpeg、png、gif的图片。
     * <p>
     * 源图片与目标图片的后缀可以不同。例如：可以将“1.jpg”转换为“1.png”。
     *
     * @param sourceFileName {@code String} 源图片名
     * @param degree         {@code int} 旋转角度，可以是负值（逆时针旋转）
     *
     * @return {@code Bitmap} 转换后的Bitmap
     */
    public static Bitmap setRotate(String sourceFileName, int degree) {
        Bitmap resizedBitmap = null;
        try {
            // 源文件
            //File fIn = new File(sourceFileName);
            Bitmap bitmap = BitmapFactory.decodeFile(sourceFileName);
            // 图片宽度
            int w = bitmap.getWidth();
            // 图片高度
            int h = bitmap.getHeight();
            // 新图片的高和宽
            int width = 0, height = 0;
            Log.d("###@@w", "" + w);
            Log.d("###@@h", "" + h);
            // 如果角度是90的倍数
            if (degree % 180 == 0) {
                // 如果角度是180的倍数，如0、180。目标图片的宽和高与原图片相同。
                width = w;
                height = h;
            } else {
                // 如果角度不是180的倍数，如90、270。目标图片的宽和高与原图片相反。
                width = h;
                height = w;
            }
            Log.d("###@wh", "" + width + ":" + height);

            // 方法1：直接旋转（首选方法）
            Matrix matrix = new Matrix();
            matrix.setRotate(degree);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            // 方法2：使用Canvas重绘
            // 新Bitmap
//            Bitmap resizedBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
//            Canvas canvas = new Canvas(resizedBitmap);
//            // 主要以这个对象调用旋转方法
//            Matrix matrix = new Matrix();
//            // 计算旋转中心点
//            int x = Math.min(w, h);
//            // 进行旋转
//            matrix.setRotate(degree, x / 2, x / 2);
//            Paint paint = new Paint();
//            // 设置抗锯齿,防止过多的失真
//            paint.setAntiAlias(true);
//            canvas.drawBitmap(bitmap, matrix, paint);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resizedBitmap;
    }

    /**
     * 将图片按一定的高度缩放后保存
     * <p>
     * 只能旋转并保存后缀为jpg、jpeg、png、gif的图片。
     * <p>
     * 源图片与目标图片的后缀可以不同。例如：可以将“1.jpg”转换为“1.png”。
     *
     * @param sourceFileName {@code String} 源图片名
     * @param targetFileName {@code String} 目标图片名
     */
    public static void zoom(String sourceFileName, String targetFileName) {
        try {
            // 源文件
            File fIn = new File(sourceFileName);
            // 目标文件
            File fOut = new File(targetFileName);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
