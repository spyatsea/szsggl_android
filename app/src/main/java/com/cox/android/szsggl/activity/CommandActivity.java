/*
 * Copyright (c) 2014 山西考科思 版权所有
 */
package com.cox.android.szsggl.activity;

import android.os.Bundle;
import android.util.Log;

import com.cox.android.szsggl.R;
import com.cox.utils.StatusBarUtil;

/**
 * 执行指令的Activity
 *
 * @author 乔勇(Jacky Qiao)
 */
public class CommandActivity extends DbActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("################", "[" + baseApp.cacheDir.getAbsolutePath());
        finish();
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
}
