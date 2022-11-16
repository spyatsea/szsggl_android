/*
 * Copyright (c) 2017 山西考科思 版权所有
 */
package com.cox.android.szsggl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cox.android.szsggl.application.BaseApplication;
import com.cox.utils.WpsParam.Receiver;

import java.util.Set;

/**
 * 
 * 
 * @author 乔勇(Jacky Qiao)
 */
public class MyFileBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("####", "@@@" + action);
		Bundle bundle = intent.getExtras();
		Set<String> set = bundle.keySet();
		for (String s : set) {
			Log.d("@", s + "#" + bundle.get(s));
		}
		if (Receiver.ACTION_BACK.equals(action)) {
			// 返回时广播
			Log.d("###", "#" + Receiver.ACTION_BACK);
		} else if (Receiver.ACTION_CLOSE.equals(action)) {
			// 关闭文件时候的广播
			((BaseApplication) context.getApplicationContext()).attaFileName = intent.getStringExtra("CloseFile");
			Log.d("###", "#" + Receiver.ACTION_CLOSE);
		} else if (Receiver.ACTION_HOME.equals(action)) {
			// HOME键广播
			Log.d("###", "#" + Receiver.ACTION_HOME);
		} else if (Receiver.ACTION_SAVE.equals(action)) {
			// 保存广播
			Log.d("###", "#" + Receiver.ACTION_SAVE);
		}
	}
}
