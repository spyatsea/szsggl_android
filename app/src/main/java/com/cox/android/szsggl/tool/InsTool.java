/*
 * Copyright (c) 2013 山西考科思 版权所有
 */
package com.cox.android.szsggl.tool;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cox.utils.CommonUtil;

/**
 * 操作巡视信息的工具类
 * 
 * @author 乔勇(Jacky Qiao)
 * */
public class InsTool extends CommonTool {
	public InsTool(Context context, DbTool dbTool) {
		super(context, dbTool);
	}
	/**
	 * 获得结果list
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<Map<String, Object>>} 结果list
	 * */
	public ArrayList<HashMap<String, Object>> getInfoMapList(String sql, String[] selectionArgs) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);

			while (cursor.moveToNext()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				CommonUtil.cursorToMap(cursor, map);
				list.add(map);
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 获得结果list
	 * <p>使用自定义列名</p>
	 *
	 * @param sql             {@code String} 查询语句
	 * @param selectionArgs   {@code String[]} 查询参数
	 * @param columnNameArray {@code String[]} 自定义列名
	 * @return {@code List<Map<String, Object>>} 结果list
	 */
	public ArrayList<HashMap<String, Object>> getInfoMapCusList(String sql, String[] selectionArgs, String[] columnNameArray) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);

			while (cursor.moveToNext()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				CommonUtil.cursorToCusMap(cursor, map, columnNameArray);
				list.add(map);
			}
			cursor.close();
		}
		return list;
	}
}
