/*
 * Copyright (c) www.spyatsea.com  2014
 */
package com.cox.android.szsggl.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonUtil;

/**
 * 显示大图片
 *
 * @author 乔勇(Jacky Qiao)
 */
public class ShowBigImageDialogFragment extends DialogFragment {

    /**
     * 图片
     */
    private ImageView picView = null;

    /**
     * The system calls this to get the DialogFragment's layout, regardless of whether it's being displayed as a dialog
     * or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局
        LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.show_big_image, container, false);
        picView = (ImageView) rootLayout.findViewById(R.id.picView);

        // 获取信息传输 Bundle
        Bundle data = getArguments();

        // String filepath = data.getString("filepath");
        int imageId = data.getInt("imageId");
        byte[] imageBytes = data.getByteArray("imageBytes");
        int maxWidth = data.getInt("maxWidth");
        int maxHeight = data.getInt("maxHeight");

        Bitmap bitmap = null;
        if (imageId != 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        } else if (imageBytes != null && imageBytes.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        // 缩放的参数 Bundle
        Bundle calcBundle = CommonUtil
                .calculateBigImageSize(bitmap.getWidth(), bitmap.getHeight(), maxWidth, maxHeight);

        picView.setImageBitmap(bitmap);
        ViewGroup.LayoutParams layoutparams = picView.getLayoutParams();
        layoutparams.width = calcBundle.getInt("calcWidth");
        layoutparams.height = calcBundle.getInt("calcHeight");
        picView.setLayoutParams(layoutparams);
        // Inflate the layout to use as dialog or embedded fragment
        return rootLayout;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using
        // onCreateView() is
        // to modify any dialog characteristics. For example, the dialog
        // includes a
        // title by default, but your custom layout might not need it. So here
        // you can
        // remove the dialog title, but you must call the superclass to get the
        // Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}