/*
 * Copyright (c) www.spyatsea.com  2013 
 */
package com.cox.android.szsggl.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cox.android.szsggl.model.Userinfo;
import com.cox.utils.CommonUtil;

/**
 * 操作考试信息的工具类
 * 
 * @author 乔勇(Jacky Qiao)
 * */
public class InfoTool extends CommonTool {
	public InfoTool(Context context, DbTool dbTool) {
		super(context, dbTool);
	}

	/**
	 * 查找指定 ids 的记录
	 * 
	 * @param {@code String} 记录 ids
	 * @return {@code Userinfo} 记录对象
	 * */
	public Userinfo getRecById(String ids) {
		// 记录对象
		Userinfo o = null;
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(ids) && db != null) {
			Cursor cursor = db.rawQuery("SELECT * FROM userinfo model WHERE model.ids=?", new String[] { ids });
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				o = getRecWithCursor(cursor);
			}
			cursor.close();
		}
		return o;
	}

	/**
	 * 查找指定 ids 的信息记录
	 * 
	 * @param {@code String} 记录 ids
	 * @return {@code HashMap<String, Object>} 记录对象
	 * */
	public HashMap<String, Object> getInfoById(String ids) {
		// 记录对象
		HashMap<String, Object> o = null;
		if (CommonUtil.checkNB(ids)) {
			ArrayList<HashMap<String, Object>> list = getInfoMapList("SELECT * FROM info model WHERE model.ids=?",
					new String[] { ids });
			if (list.size() > 0) {
				o = list.get(0);
			}
		}
		return o;
	}

	/**
	 * 取得 cursor 当前的记录
	 * 
	 * @param cursor
	 *            {@code Cursor} Cursor
	 * @return {@code Userinfo}
	 * */
	public Userinfo getRecWithCursor(Cursor cursor) {
		Userinfo o = new Userinfo();
		// ids
		o.setIds(cursor.getString(0));
		// 类型
		o.setType(cursor.getString(1));
		// 账号
		o.setAccount(cursor.getString(2));
		// 密码
		o.setPassword(cursor.getString(3));
		// 姓名
		o.setRealname(cursor.getString(4));
		// 组织编码
		o.setDeptids(cursor.getString(5));
		// 组织名称
		o.setDeptname(cursor.getString(6));
		// 岗位编码
		o.setPosition(cursor.getString(7));
		// 是否有效
		o.setValid(cursor.getString(8));
		return o;
	}

	/**
	 * 更新记录
	 * */
	public void updateNote1(Userinfo o) {
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		ContentValues cv = new ContentValues();

		db.update("note", cv, "ids=?", new String[] { o.getIds() });
	}

	/**
	 * 检查用户是否有效（与数据库中的数据比较）
	 * <p>
	 * 如果有效，将返回数据库中保存的用户对象，否则返回null。
	 * 
	 * @param u
	 *            {@code Userinfo} 用来检查的Userinfo对象
	 *
	 * @return {@code Userinfo} 数据库中保存的用户对象。
	 * */
	public Userinfo checkDBUser(Userinfo u) {
		boolean flag = false;
		// 临时用户对象
		Userinfo user_tmp = null;
		if (u != null) {
			if (CommonUtil.checkNB(u.getAccount()) && CommonUtil.checkNB(u.getPassword())) {
				SQLiteDatabase db = dbTool.getDb();
				if (db != null) {
					Cursor cursor = db
							.rawQuery(
									"SELECT * FROM userinfo model WHERE UPPER(model.account)=? and model.password=? and model.valid='1'",
									new String[] { u.getAccount().toUpperCase(Locale.CHINA), u.getPassword() });
					if (cursor.getCount() == 1) {
						cursor.moveToNext();
						user_tmp = getRecWithCursor(cursor);
						flag = true;
					}
					cursor.close();
				}

			}
		}
		if (!flag) {
			user_tmp = null;
		}
		return user_tmp;
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
	 *
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<Map<String, Object>>} 结果list
	 * */
	public JSONArray getJsonArray(String sql, String[] selectionArgs) {
		JSONArray array = new JSONArray();
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);

			while (cursor.moveToNext()) {
				JSONObject o = new JSONObject();
				CommonUtil.cursorToJsonObject(cursor, o);
				array.add(o);
			}
			cursor.close();
		}
		return array;
	}

}
