/*
 * Copyright (c) www.spyatsea.com  2013 
 */
package com.cox.android.szsggl.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cox.utils.CommonUtil;

/**
 * 通用的信息操作工具类
 * 
 * @author 乔勇(Jacky Qiao)
 * */
public class CommonTool {
	Context context = null;
	DbTool dbTool = null;

	public CommonTool(Context context, DbTool dbTool) {
		this.context = context;
		this.dbTool = dbTool;
	}

	public DbTool getDbTool() {
		return dbTool;
	}

	public void setDbTool(DbTool dbTool) {
		this.dbTool = dbTool;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * 查找符合条件的记录总数
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code int} 记录总数
	 * */
	public int getCount(String sql, String[] selectionArgs) {
		int recordCount = 0;
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.moveToNext()) {
				// 查询出的信息数量
				recordCount = cursor.getInt(0);
			}
			cursor.close();
		}
		return recordCount;
	}

	/**
	 * 查找符合条件的单个字段值
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code String} 字段值
	 * */
	public String getSingleVal(String sql, String[] selectionArgs) {
		String result = null;
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.moveToNext()) {
				// 查询出的信息数量
				result = cursor.getString(0);
			}
			cursor.close();
		}
		return result;
	}

	/**
	 * 查找单字段 List
	 * 
	 * @param {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<String>} 字段值 List
	 * */
	public List<String> getValList(String sql, String[] selectionArgs) {
		List<String> resultList = new ArrayList<String>();
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				resultList.add(cursor.getString(0));
			}
			cursor.close();
		}
		return resultList;
	}

	/**
	 * 查找单字段 List
	 * 
	 * @param {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<Integer>} 字段值 List
	 * */
	public List<Integer> getIntList(String sql, String[] selectionArgs) {
		List<Integer> resultList = new ArrayList<Integer>();
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				resultList.add(cursor.getInt(0));
			}
			cursor.close();
		}
		return resultList;
	}

	/**
	 * 更新信息
	 * 
	 * @param tableName
	 *            {@code String} 表名
	 * @param keyColumn
	 *            {@code String} 关键列名
	 * @param keyValue
	 *            {@code String} 关键列值
	 * @param cv
	 *            {@code ContentValues} 更新值的信息组
	 * @return {@code int} 影响的行数
	 * */
	public int update(String tableName, String keyColumn, String keyValue, ContentValues cv) {
		int result = -1;
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(tableName) && CommonUtil.checkNB(keyColumn) && CommonUtil.checkNB(keyValue)
				&& cv != null && db != null) {
			result = db.update(tableName, cv, keyColumn + "=?", new String[] { keyValue });
		}
		return result;
	}

	/**
	 * 更新信息
	 * 
	 * @param tableName
	 *            {@code String} 表名
	 * @param cv
	 *            {@code ContentValues} 更新值的信息组
	 * @param whereClause
	 *            {@code String} where语句
	 * @param whereArgs
	 *            {@code String[]} 参数值
	 * @return {@code int} 影响的行数
	 * */
	public int update(String tableName, ContentValues cv, String whereClause, String[] whereArgs) {
		int result = -1;
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(tableName) && cv != null && db != null) {
			result = db.update(tableName, cv, whereClause, whereArgs);
		}
		return result;
	}

	/**
	 * 新建信息
	 * 
	 * @param tableName
	 *            {@code String} 表名
	 * @param cv
	 *            {@code ContentValues} 更新值的信息组
	 * @return {@code long} 行id
	 * */
	public long insert(String tableName, ContentValues cv) {
		return insert(null, tableName, cv);
	}

	/**
	 * 新建信息
	 * 
	 * @param db
	 *            {@code SQLiteDatabase} 数据库实例
	 * @param tableName
	 *            {@code String} 表名
	 * 
	 * @param cv
	 *            {@code ContentValues} 更新值的信息组
	 * @return {@code long} 行id
	 * */
	public long insert(SQLiteDatabase db, String tableName, ContentValues cv) {
		long result = -1;
		if (db == null) {
			// SQLite数据库变量
			db = dbTool.getDb();
		}
		if (db == null || !db.isOpen()) {
			db = dbTool.regetDb();
			dbTool.setDb(db);
		}
		if (CommonUtil.checkNB(tableName) && cv != null && db != null) {
			result = db.insert(tableName, "", cv);
		}
		return result;
	}

	/**
	 * 新建信息
	 * <p>
	 * 20141222修改。放入cv时要根据值的类型分别进行处理。
	 * @param tableName
	 *            {@code String} 表名
	 * @param map
	 *            {@code Map<String, Object>} 信息Map
	 * @return {@code long} 行id
	 * 
	 * */
	public long insert(String tableName, Map<String, Object> map) {
		long result = -1;
		// 键值对
		ContentValues cv = new ContentValues();
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(tableName) && map != null && db != null) {
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				if (key.indexOf("V_") == 0) {
					// 如果key是系统保留值，就不会保存到表中
					continue;
				}
				Object value = entry.getValue();
				if (value == null) {
					cv.put(key, "");
				} else if (value instanceof String) {
					cv.put(key, (String) value);
				} else if (value instanceof Integer) {
					cv.put(key, (Integer) value);
				} else {
					cv.put(key, value.toString());
				}
			}
			result = db.insert(tableName, "", cv);
		}
		return result;
	}

	/**
	 * 删除信息
	 * 
	 * @param tableName
	 *            {@code String} 表名
	 * @param whereClause
	 *            {@code String} where语句
	 * @param whereArgs
	 *            {@code String[]} 参数值
	 * @return {@code int} 影响的行数
	 * */
	public int delete(String tableName, String whereClause, String[] whereArgs) {
		int result = -1;
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(tableName) && db != null) {
			result = db.delete(tableName, whereClause, whereArgs);
		}
		return result;
	}

	/**
	 * 执行删除操作
	 * 
	 * */
	public void delInfo(String sql, String[] bindArgs) {
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && bindArgs != null && db != null) {
			db.execSQL(sql, bindArgs);
		}
	}

	/**
	 * 查找信息 Map
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<Map<String, Object>>} 信息 Map
	 * */
	public Map<String, String> getInfoKVMap(String sql, String[] selectionArgs) {
		Map<String, String> map = new HashMap<String, String>();
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(0), cursor.getString(1));
			}
			cursor.close();
		}
		return map;
	}
}
