/*
 * Copyright (c) www.spyatsea.com  2014 
 */
package com.cox.android.szsggl.tool;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.cox.utils.CommonParam;

/**
 * 操作数据库的工具类
 * 
 * @author 乔勇(Jacky Qiao)
 * */
public class DbTool {
	Context context = null;
	SQLiteDatabase db = null;

	public DbTool(Context context) {
		super();
		this.context = context;
	}

	public DbTool(Context context, SQLiteDatabase db) {
		super();
		this.context = context;
		this.db = db;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * 重新获得数据库连接
	 * */
	public SQLiteDatabase regetDb() {
		// 打开数据库
		db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ CommonParam.PROJECT_NAME + "/db/sys.db", null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return db;
	}

	/**
	 * 重新获得数据库连接
	 * */
	public SQLiteDatabase regetDb(int flag) {
		// 打开数据库
		db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ CommonParam.PROJECT_NAME + "/db/sys.db", null, flag);
		return db;
	}
	
	/**
	 * 获得某个数据库连接
	 * */
	public static SQLiteDatabase getDb(File dbFile, int flag) {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null,
				flag);
		return db;
	}
	
	/**
	 * 获得某个数据库连接
	 * */
	public static SQLiteDatabase getDb(File dbFile) {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return db;
	}

	/**
	 * 关闭数据库连接
	 * */
	public void closeDb() {
		if (db != null) {
			db.close();
			SQLiteDatabase.releaseMemory();
		}
	}

	/**
	 * 关闭指定的数据库连接
	 * */
	public static void closeDb(SQLiteDatabase db) {
		if (db != null) {
			db.close();
			SQLiteDatabase.releaseMemory();
		}
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

}
