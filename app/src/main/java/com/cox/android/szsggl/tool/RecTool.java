/*
 * Copyright (c) 2014 山西考科思 版权所有
 */
package com.cox.android.szsggl.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.cox.android.szsggl.activity.DbActivity;
import com.cox.android.szsggl.activity.ShowImageActivity;
import com.cox.android.szsggl.activity.ShowVideoActivity;
import com.cox.android.szsggl.model.Attachment;
import com.cox.android.szsggl.model.Note;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;

/**
 * 操作证件信息的工具类
 * 
 * @author 乔勇(Jacky Qiao)
 * */
public class RecTool {
	Context context = null;
	DbTool dbTool = null;

	public RecTool(Context context, DbTool dbTool) {
		super();
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
	 * 查找指定 ids 的记录
	 * 
	 * @param {@code String} 记录 ids
	 * @return {@code Note} 记录对象
	 * */
	public Note getRecById(String ids) {
		// 记录对象
		Note note = null;
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(ids) && db != null) {
			Cursor cursor = db.rawQuery("SELECT * FROM note model WHERE model.ids=?", new String[] { ids });
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				note = getNoteWithCursor(cursor);
			}
			cursor.close();
		}
		return note;
	}

	/**
	 * 取得 cursor 当前的记录
	 * 
	 * @param cursor
	 *            {@code Cursor} Cursor
	 * @return {@code Note}
	 * */
	public Note getNoteWithCursor(Cursor cursor) {
		Note note = new Note();
		// ids
		note.setIds(cursor.getString(0));
		// 标题
		note.setTitle(cursor.getString(1));
		// 正文
		note.setInfodata(cursor.getString(2));
		// 创建日期
		note.setCreatedtime(cursor.getString(3));
		// 纬度
		note.setLat(cursor.getString(4));
		// 经度
		note.setLon(cursor.getString(5));
		// 纬度（百度）
		note.setLat_baidu(cursor.getString(6));
		// 经度（百度）
		note.setLon_baidu(cursor.getString(7));
		// 地址
		note.setAddress(cursor.getString(8));
		note.setValid(cursor.getString(9));
		return note;
	}

	/**
	 * 查找某个记录的所有附件
	 * 
	 * @param {@code String} 记录 ids
	 * @return {@code List<Attachment>} 附件List
	 * */
	public Map<String, Object> getAttachMapByNoteId(String ids) {
		Map<String, Object> attaMap = new HashMap<String, Object>();
		List<Attachment> photoList = new ArrayList<Attachment>();
		List<Attachment> videoList = new ArrayList<Attachment>();
		List<Attachment> audioList = new ArrayList<Attachment>();
		Integer total = 0;
		SQLiteDatabase db = dbTool.getDb();
		// 记录对象
		if (CommonUtil.checkNB(ids) && db != null) {
			Cursor cursor = db.rawQuery("SELECT * FROM attachment model WHERE model.noteids=? and model.valid='"
					+ CommonParam.VALID_YES + "' order by DATETIME(model.createdtime)", new String[] { ids });
			while (cursor.moveToNext()) {
				total++;
				Attachment atta = new Attachment();
				atta.setIds(cursor.getString(0));
				atta.setNoteids(cursor.getString(1));
				atta.setFiletype(cursor.getString(2));
				atta.setFilename(cursor.getString(3));
				atta.setFilesize(cursor.getString(4));
				atta.setCreatedtime(cursor.getString(5));
				atta.setLat(cursor.getString(6));
				atta.setLon(cursor.getString(7));
				atta.setLat_baidu(cursor.getString(8));
				atta.setLon_baidu(cursor.getString(9));
				atta.setValid(cursor.getString(10));
				atta.setMemo(cursor.getString(11));

				if (atta.getFiletype().equalsIgnoreCase(CommonParam.ATTA_TYPE_PHOTO)) {
					// 图片附件
					photoList.add(atta);
				}
			}

			cursor.close();
		}

		attaMap.put("total", total);
		attaMap.put("photoList", photoList);

		return attaMap;
	}

	/**
	 * 打开指定图片
	 * 
	 * @param filename
	 *            {@code String} 图片名称
	 * @param filepath
	 *            {@code String} 图片路径
	 * */
	public void openPicByFilename(String filename, String filepath) {
		openPicByFilename(filename, filepath, null);
	}

	/**
	 * 打开指定图片
	 * 
	 * @param filename
	 *            {@code String} 图片名称
	 * @param filepath
	 *            {@code String} 图片路径
	 * @param infoBundle
	 *            {@code Bundle} 附加信息
	 * */
	public void openPicByFilename(String filename, String filepath, Bundle infoBundle) {
		if (CommonUtil.checkNB(filename) && CommonUtil.checkNB(filepath)) {
			File file = new File(filepath);
			if (file.exists()) {
				// 创建信息传输Bundle
				Bundle data = new Bundle();
				data.putString("title", filename);
				data.putString("filepath", filepath);
				if (infoBundle != null) {
					data.putBundle("infoBundle", infoBundle);
				}
				// 创建启动 ShowImageActivity 的Intent
				Intent intent = new Intent(context, ShowImageActivity.class);
				// 将数据存入 Intent 中
				intent.putExtras(data);
				context.startActivity(intent);
			} else {
				((DbActivity) context).show("找不到该图片！");
			}
		}
	}

	/**
	 * 打开指定视频
	 * 
	 * @param filename
	 *            {@code String} 视频名称
	 * @param filepath
	 *            {@code String} 视频路径
	 * */
	public void openVideoByFilename(String filename, String filepath) {
		if (CommonUtil.checkNB(filename) && CommonUtil.checkNB(filepath)) {
			File file = new File(filepath);
			if (file.exists()) {
				// 创建信息传输Bundle
				Bundle data = new Bundle();
				data.putString("title", filename);
				data.putString("filepath", filepath);
				// 创建启动 ShowVideoActivity 的Intent
				Intent intent = new Intent(context, ShowVideoActivity.class);
				// 将数据存入 Intent 中
				intent.putExtras(data);
				context.startActivity(intent);
			} else {
				((DbActivity) context).show("找不到该视频！");
			}
		}
	}

	/**
	 * 新建附件记录
	 * */
	public void createAtta(Attachment o) {
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		ContentValues cv = new ContentValues();
		cv.put("ids", o.getIds());
		cv.put("noteids", o.getNoteids());
		cv.put("filetype", o.getFiletype());
		cv.put("filename", o.getFilename());
		cv.put("filesize", o.getFilesize());
		cv.put("createdtime", o.getCreatedtime());
		cv.put("lat", o.getLat());
		cv.put("lon", o.getLon());
		cv.put("lat_baidu", o.getLat_baidu());
		cv.put("lon_baidu", o.getLon_baidu());
		cv.put("valid", o.getValid());
		cv.put("memo", o.getMemo());

		db.insert("attachment", null, cv);
	}

	/**
	 * 新建记录
	 * */
	public void createNote(Note o) {
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		ContentValues cv = new ContentValues();
		cv.put("ids", o.getIds());
		cv.put("title", o.getTitle());
		cv.put("infodata", o.getInfodata());
		cv.put("createdtime", o.getCreatedtime());
		cv.put("lat", o.getLat());
		cv.put("lon", o.getLon());
		cv.put("lat_baidu", o.getLat_baidu());
		cv.put("lon_baidu", o.getLon_baidu());
		cv.put("address", o.getAddress());
		cv.put("valid", o.getValid());

		db.insert("note", null, cv);
	}

	/**
	 * 更新记录
	 * */
	public void updateNote(Note o) {
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		ContentValues cv = new ContentValues();
		cv.put("title", o.getTitle());
		cv.put("infodata", o.getInfodata());
		cv.put("address", o.getAddress());

		db.update("note", cv, "ids=?", new String[] { o.getIds() });
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
	 * 删除附件
	 * 
	 * @return {@code int} 受影响的行数
	 * */
	public int deleteAtta(String id) {
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();

		return db.delete("attachment", "ids=?", new String[] { id });
	}
}
