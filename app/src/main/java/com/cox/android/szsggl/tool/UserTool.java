/*
 * Copyright (c) www.spyatsea.com  2013 
 */
package com.cox.android.szsggl.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cox.android.szsggl.model.User;
import com.cox.android.szsggl.model.Userinfo;
import com.cox.dto.CommonDTO;
import com.cox.utils.CommonUtil;
import com.cox.utils.DigestUtil;

/**
 * 操作人员信息的工具类
 * 
 * @author 乔勇(Jacky Qiao)
 * */
public class UserTool extends CommonTool {

	public UserTool(Context context, DbTool dbTool) {
		super(context, dbTool);
	}

	/**
	 * 查找用户
	 * 
	 * @return recList {@code List<Userinfo>} 记录List
	 * */
	public List<Userinfo> getRecList() {
		List<Userinfo> resultList = new ArrayList<Userinfo>();
		SQLiteDatabase db = dbTool.getDb();
		Cursor cursor = db.rawQuery("SELECT * FROM userinfo model WHERE model.valid='1' order by model.pxbh ASC",
				new String[] {});
		while (cursor.moveToNext()) {
			Userinfo o = getRecWithCursor(cursor);
			resultList.add(o);
		}
		cursor.close();
		return resultList;
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
		// 排序标志
		o.setPxbh(cursor.getInt(8));
		// 是否有效
		o.setValid(cursor.getString(9));
		return o;
	}

	/**
	 * 更新记录
	 * */
	public void updateNote(Userinfo o) {
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

	// XZY相关库方法。开始=============================================================
	/**
	 * 查找指定 ids 的记录
	 * 
	 * @param {@code String} 记录 ids
	 * @return {@code BASIC_XZYQX系统用户} 记录对象
	 * */
	public User getBASIC_XZYQX系统用户ById(String ids) {
		// 记录对象
		User o = null;
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(ids) && db != null) {
			Cursor cursor = db.rawQuery("SELECT * FROM BASIC_XZYQX系统用户 model WHERE model.ids=?", new String[] { ids });
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				o = getUserWithCursor(cursor);
			}
			cursor.close();
		}
		return o;
	}

	/**
	 * 取得 cursor 当前的记录
	 * 
	 * @param cursor
	 *            {@code Cursor} Cursor
	 *
	 * @return {@code BASIC_XZYQX系统用户} 记录对象
	 * */
	public User getUserWithCursor(Cursor cursor) {
		User o = new User();
		o.setIds(cursor.getString(0));
		o.setAccount(cursor.getString(2));
		o.setPassword(cursor.getString(3));
		o.setRealname(cursor.getString(4));
		o.setGender(cursor.getInt(6));
		o.setMobilephone(cursor.getString(9));
		o.setDeptid(cursor.getString(17));
		o.setIdsn(cursor.getString(21));
		o.setCategoryIds(cursor.getString(22));
		o.setLoginnum(cursor.getInt(16));
		o.setLatestlogintime(cursor.getString(11));
		return o;
	}

	/**
	 * 查找指定 ids 的记录
	 * 
	 * @param id
	 *            {@code String} 记录 id
	 *
	 * @return {@code User} 数据库中保存的用户对象。
	 * */
	public User getUserById(String id) {
		// 临时用户对象
		User user_tmp = null;
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(id) && db != null) {
			Cursor cursor = db.rawQuery(
					"SELECT * FROM student model WHERE model.IDS=?",
					new String[] { id });
			if (cursor.getCount() == 1) {
				cursor.moveToNext();
				user_tmp = getUserWithCursor(cursor);
			}
			cursor.close();

		}
		if (user_tmp != null) {
			String deptname = getSingleVal("SELECT model.title FROM unitinfo model WHERE model.ids=?",
					new String[] { user_tmp.getDeptid() });
			user_tmp.setDeptname(deptname);
		} else {
			user_tmp = null;
		}
		return user_tmp;
	}

	/**
	 * 检查用户是否有效（与数据库中的数据比较）
	 * <p>
	 * 如果有效，将返回数据库中保存的用户对象，否则返回null。
	 * 
	 * @param u
	 *            {@code User} 用来检查的用户对象
	 *
	 * @return {@code User} 数据库中保存的用户对象。
	 * */
	@SuppressLint("DefaultLocale")
	public User checkUser(User u) {
		boolean flag = false;
		// 临时用户对象
		User user_tmp = null;
		if (u != null) {
			if (CommonUtil.checkNB(u.getAccount()) && CommonUtil.checkNB(u.getPassword())) {
				SQLiteDatabase db = dbTool.getDb();
				if (db != null) {
					Cursor cursor = db
							.rawQuery(
									"SELECT * FROM student model WHERE UPPER(model.\"account\")=? and UPPER(model.\"password\")=? and valid=? and active=?",
									new String[] { u.getAccount().toUpperCase(Locale.CHINA),
											DigestUtil.md5(u.getPassword()).toUpperCase(Locale.CHINA) , "1", "1"});
					if (cursor.getCount() == 1) {
						cursor.moveToNext();
						user_tmp = getUserWithCursor(cursor);
						flag = true;
					}
					cursor.close();
				}

			}
		}
		if (flag) {
			String deptname = getSingleVal("select model.title from \"unitinfo\" model where model.ids=?",
					new String[] { user_tmp.getDeptid() });
			user_tmp.setDeptname(deptname);
		} else {
			user_tmp = null;
		}
		return user_tmp;
	}

	/**
	 * 获得巡视时用户可见的部门列表
	 * 
	 * @param userId
	 *            {@code String} 当前登录用户id
	 * @param deptId
	 *            {@code String} 用户部门id
	 * @return {@code List<Map<String, String>>} 可见的部门列表
	 * */
	public List<Map<String, String>> getXZYInsDeptlist(String userId, String deptId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(userId) && CommonUtil.checkNB(deptId) && db != null) {
			Cursor cursor = db
					.rawQuery(
							"select model.部门名称,model.IDS from BASIC_部门设置  model where model.部门类别='泵站' and (model.IDS in (select 部门IDS from BASIC_XZYQX系统用户可见部门 where 用户IDS=?) or model.IDS=?) order by model.部门序号 ASC",
							new String[] { userId, deptId });
			while (cursor.moveToNext()) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("code", cursor.getString(1));
				m.put("name", cursor.getString(0));
				list.add(m);
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 获得涉源单位列表
	 * 
	 * @return {@code List<Map<String, String>>} 涉源单位列表
	 * */
	public List<Map<String, String>> getInsTargetDeptlist() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase db = dbTool.getDb();
		if (db != null) {
			Cursor cursor = db.rawQuery("select model.IDS, model.单位名称 from A030_单位基本情况  model order by model.IDS ASC",
					new String[] {});
			while (cursor.moveToNext()) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("code", cursor.getString(0));
				m.put("name", cursor.getString(1));
				list.add(m);
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 查找XS_XZYQX系统用户dto List
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<CommonDTO>} 记录对象
	 * */
	public List<CommonDTO> getXS_XZYQX系统用户DtoList(String sql, String[] selectionArgs) {
		// 记录对象
		List<CommonDTO> resultList = new ArrayList<CommonDTO>();
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				CommonDTO c = new CommonDTO();
				// id
				c.setP1(cursor.getString(0));
				// 姓名
				c.setP2(cursor.getString(1));

				resultList.add(c);
			}
			cursor.close();
		}
		return resultList;
	}

	/**
	 * 查找XSNR_巡视S2执行人dto List
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<CommonDTO>} 记录对象
	 * */
	public List<CommonDTO> getXSNR_巡视S2执行人DtoList(String sql, String[] selectionArgs) {
		// 记录对象
		List<CommonDTO> resultList = new ArrayList<CommonDTO>();
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				CommonDTO c = new CommonDTO();
				c.setP1(cursor.getString(0));
				c.setP2(cursor.getString(1));

				resultList.add(c);
			}
			cursor.close();
		}
		return resultList;
	}

	/**
	 * 获得巡视人Dto List
	 * */
	public List<CommonDTO> getInsPeopleDtoList(String deptId, String userId) {
		return getXS_XZYQX系统用户DtoList(
				"select model.IDS, model.姓名 from \"BASIC_XZYQX系统用户\" model where model.部门IDS=? and model.IDS<>? order by model.序号 ASC",
				new String[] { deptId, userId });
	}

	/**
	 * 获得巡视人Map List
	 * 
	 * @param sql
	 *            {@code String} 查询语句
	 * @param selectionArgs
	 *            {@code String[]} 查询参数
	 * @return {@code List<Map<String, Object>>} 巡视人信息List
	 * @deprecated
	 * */
	@Deprecated
	public ArrayList<HashMap<String, Object>> getInsPeopleMapList(String sql, String[] selectionArgs) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		// SQLite数据库变量
		SQLiteDatabase db = dbTool.getDb();
		if (CommonUtil.checkNB(sql) && selectionArgs != null && db != null) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				for (int i = 0, len = cursor.getColumnCount(); i < len; i++) {
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}
				list.add(map);
			}
			cursor.close();
		}
		return list;
	}
	// XZY相关库方法。结束=============================================================
}
