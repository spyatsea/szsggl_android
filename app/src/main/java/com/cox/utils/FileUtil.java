/*
 * Copyright (c) www.spyatsea.com  2011 
 */
package com.cox.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

/**
 * 定义了一些操作文件的方法
 * 
 * @author 乔勇(Jacky Qiao)
 * */
@SuppressWarnings({})
public class FileUtil {
	/**
	 * BOM标记
	 * */
	public static byte[] UTF8BOM = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

	/**
	 * 获得文件大小
	 * <p>
	 * 这里的容量计算单位是1024。
	 * 
	 * @param file
	 *            {@code File} 文件对象
	 * @return {@code String} 文件大小。单位有四种，分别为{@code B、KB、MB、GB}。
	 * */
	public String getFileSize(File file) {
		String fileSize = null;
		try {
			if (file != null) {
				fileSize = getFileSize((double) file.length());
			} else {
				fileSize = "0 B";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileSize;
	}

	/**
	 * 获得文件大小（静态方法）
	 * <p>
	 * 这里的容量计算单位是1024。
	 * 
	 * @param file
	 *            {@code File} 文件对象
	 * @return {@code String} 文件大小。单位有四种，分别为{@code B、KB、MB、GB}。
	 * */
	public static String getFileSizeStatic(File file) {
		String fileSize = null;
		try {
			if (file != null) {
				fileSize = getFileSizeStatic((double) file.length());
			} else {
				fileSize = "0 B";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileSize;
	}

	/**
	 * 获得文件大小
	 * <p>
	 * 这里的容量计算单位是1024。
	 * 
	 * @param size
	 *            {@code Double} 文件对象
	 * @return {@code String} 文件大小。单位有四种，分别为{@code B、KB、MB、GB}。
	 * */
	public String getFileSize(Double size) {
		String fileSize = null;
		if (size > 0D) {
			if (size / 1024D < 1) {
				fileSize = new DecimalFormat("0").format(size) + " B";
			} else if (size / 1024D < 1024D) {
				fileSize = new DecimalFormat("0.0").format(size / 1024D) + " KB";
			} else if (size / (1024D * 1024D) < 1024D) {
				fileSize = new DecimalFormat("0.0").format(size / (1024D * 1024D)) + " MB";
			} else {
				fileSize = new DecimalFormat("0.0").format(size / (1024D * 1024D * 1024D)) + " GB";
			}
		} else {
			fileSize = "0 B";
		}

		return fileSize;
	}

	/**
	 * 获得文件大小（静态方法）
	 * <p>
	 * 这里的容量计算单位是1024。
	 * 
	 * @param size
	 *            {@code Double} 文件对象
	 * @return {@code String} 文件大小。单位有四种，分别为{@code B、KB、MB、GB}。
	 * */
	public static String getFileSizeStatic(Double size) {
		String fileSize = null;
		if (size > 0D) {
			if (size / 1024D < 1) {
				fileSize = new DecimalFormat("0").format(size) + " B";
			} else if (size / 1024D < 1024D) {
				fileSize = new DecimalFormat("0.0").format(size / 1024D) + " KB";
			} else if (size / (1024D * 1024D) < 1024D) {
				fileSize = new DecimalFormat("0.0").format(size / (1024D * 1024D)) + " MB";
			} else {
				fileSize = new DecimalFormat("0.0").format(size / (1024D * 1024D * 1024D)) + " GB";
			}
		} else {
			fileSize = "0 B";
		}

		return fileSize;
	}

	/**
	 * 将内容写入存储器的文件中
	 * 
	 * @param file
	 *            {@code File} 要写入的文件
	 * @param content
	 *            {@code String} 要写入的内容
	 * @return boolean 写入是否成功
	 * */
	public synchronized boolean writeFile(File file, String content) throws IOException {

		// StringReader sr = new StringReader(content);
		// FileWriter fw = null;
		try {
			FileOutputStream fouts = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fouts, "UTF-8"));
			bw.write(content);
			bw.close();
			fouts.close();
			// fw = new FileWriter(file);
			// char[] buffer = new char[1024];
			// int hasRead = 0;
			// while ((hasRead = sr.read(buffer)) > 0) {
			// fw.write(new String(new String(buffer, 0,
			// hasRead).getBytes("UTF-8"), "UTF-8"));
			// Q.p(content);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			// if (sr != null) {
			// sr.close();
			// }
			// if (fw != null) {
			// fw.close();
			// }
		}
		Q.p("文件\"" + file + "\"写入成功！");
		return true;
	}

	/**
	 * 将内容写入存储器的文件中
	 * 
	 * @param file
	 *            {@code File} 要写入的文件
	 * @param content
	 *            {@code String} 要写入的内容
	 * @param bomFlag
	 *            {@code boolean} 是否添加BOM标记
	 * @return boolean 写入是否成功
	 * */
	public synchronized static boolean writeFile(File file, String content, boolean bomFlag) throws IOException {

		FileOutputStream fouts = null;
		BufferedWriter bw = null;
		try {
			fouts = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(fouts, "UTF-8"));
			if (bomFlag) {
				String utf8BomStr = new String(UTF8BOM, "UTF-8");// 定义BOM标记
				bw.write(utf8BomStr);
			}
			bw.write(content);
			bw.close();
			fouts.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (bw != null) {
				bw.close();
			}
			if (fouts != null) {
				fouts.close();
			}
		}
		Q.p("文件\"" + file + "\"写入成功！");
		return true;
	}

	/**
	 * 取得类所在的路径
	 * */
	public static String getFilePath() {
		String spath = FileUtil.class.getResource("").getPath();
		spath = spath.substring(1, spath.indexOf("/WEB-INF/classes/"));
		return spath;
	}

	/**
	 * 删除文件
	 *
	 * @param filename
	 */
	public void deleteFile(String filename) {
		File f = new File(filename);
		// out.println(f);
		// out.println(f.exists());

		if (f.exists()) {// 检查File.txt是否存在
			f.delete();// 删除File.txt文件
		}

	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 * @param filename
	 */
	public void deleteFile(String path, String filename) {
		File f = new File(path, filename);
		// out.println(f);
		// out.println(f.exists());

		if (f.exists()) {// 检查File.txt是否存在
			f.delete();// 删除File.txt文件
		}

	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 *            {@code String} 完整文件名
	 * @return {@code String} 文件内容
	 * */
	public String readFile(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder("");
		InputStreamReader read = null;// 建立FileReader对象，并实例化为fr
		BufferedReader br = null; // 建立BufferedReader对象，并实例化为br
		try {
			read = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
			br = new BufferedReader(read);

			String line;
			line = br.readLine();
			// 从文件读取一行字符串
			// ss+=Line;
			// 判断读取到的字符串是否不为空
			while (line != null) {
				sb.append(line + "\n");// 输出从文件中读取的数据
				line = br.readLine();// 从文件中继续读取一行数据
			}
			br.close();// 关闭BufferedReader对象
			read.close();// 关闭文件
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public void copyFile(String oldPath, String newPath) throws IOException {
		try {
			// int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					// bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.flush();
				fs.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 * @throws IOException
	 */
	public void copyFolder(String oldPath, String newPath) throws IOException {

		(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
		File a = new File(oldPath);
		String[] file = a.list();
		File temp = null;
		for (int i = 0; i < file.length; i++) {
			if (oldPath.endsWith(File.separator)) {
				temp = new File(oldPath + file[i]);
			} else {
				temp = new File(oldPath + File.separator + file[i]);
			}

			if (temp.isFile()) {
				FileInputStream input = new FileInputStream(temp);
				FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = input.read(b)) != -1) {
					output.write(b, 0, len);
				}
				output.flush();
				output.close();
				input.close();
			}
			if (temp.isDirectory()) {// 如果是子文件夹
				copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
			}
		}
	}

	/**
	 * 生成zip文件
	 * 
	 * @param ftlmc
	 *            {@code Sting} 自定义模板文件名
	 * @param attamc
	 *            {@code String} 附属文件名称
	 * @return {@code String} 临时压缩文件前缀名，如果出错则返回{@code null}。
	 * */
	// public String makeAttaZip(String ftlmc, String attamc) throws Exception {
	// String uuid = null;
	// boolean zipFlag = false; // 是否压缩成功
	// if (CommonUtil.checkNB(ftlmc) && CommonUtil.checkNB(getFilePath())) {
	// String ftlname = null;
	// if (ftlmc.lastIndexOf(".ftl") != -1) {
	// ftlname = ftlmc.substring(0, ftlmc.lastIndexOf(".ftl"));
	// } else {
	// ftlname = ftlmc;
	// }
	// File attaFile = new File(getFilePath() + "/templates/customize/"
	// + ftlname + "/images/" + attamc); // 代表附属文件的File对象
	// if (attaFile.exists() && attaFile.isFile()) {
	// BufferedInputStream fins = null;
	// ZipArchiveOutputStream zouts = null;
	// File zipFile = null;
	// try {
	// uuid = UUID.randomUUID().toString().replaceAll("[-]", ""); // 临时文件名
	// zipFile = new File(getFilePath() + "/tempdir/" + uuid
	// + ".zip");
	// if (zipFile.exists()) {
	// zipFile.delete();
	// }
	// ZipArchiveEntry entry = null;
	// File inFile = attaFile;
	// fins = new BufferedInputStream(
	// new FileInputStream(attaFile));
	// zouts = new ZipArchiveOutputStream(new FileOutputStream(
	// zipFile));
	// entry = new ZipArchiveEntry(inFile.getName());
	// entry.setSize(0L);
	// zouts.putArchiveEntry(entry);
	// byte[] buff = new byte[1024];
	// int hasRead = 0;
	// while ((hasRead = fins.read(buff)) > 0) {
	// zouts.write(buff, 0, hasRead);
	// }
	// zipFlag = true;
	// Q.p("成功生成" + zipFile + "！");
	// } catch (Exception e) {
	// e.printStackTrace();
	// if (zipFile != null && zipFile.exists()) {
	// zipFile.delete();
	// }
	// Q.p("生成Zip文件出错。请检查文件的属性。");
	// } finally {
	// if (fins != null) {
	// fins.close();
	// }
	// if (zouts != null) {
	// zouts.closeArchiveEntry();
	// zouts.close();
	// }
	// }
	// }
	// }
	// if (!zipFlag) {
	// uuid = null;
	// }
	// return uuid;
	// }

	/**
	 * 将assets目录的数据库文件拷贝到SD卡上
	 * 
	 * @param activity
	 *            {@code Activity} 调用本程序的Activity
	 * */
	public static void copyDefaultDB(AppCompatActivity activity) {
		copyDefaultDB(activity, false);
	}

	/**
	 * 将assets目录的数据库文件拷贝到SD卡上
	 * 
	 * @param activity
	 *            {@code Activity} 调用本程序的Activity
	 * @param forceDelete
	 *            {@code Boolean} 强制删除标志
	 * */
	public static void copyDefaultDB(AppCompatActivity activity, Boolean forceDelete) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File sdcard = Environment.getExternalStorageDirectory();
				File pkgFile = new File(sdcard.getAbsolutePath() + "/" + CommonParam.PROJECT_NAME);
				if (!pkgFile.exists()) {
					boolean pkgFlag = pkgFile.mkdir();
					Log.i("***", "新建了" + pkgFlag + pkgFile.getAbsolutePath());
				}
				File db = new File(pkgFile.getAbsolutePath() + "/db");
				if (!db.exists()) {
					db.mkdir();
					Log.i("***", "新建了" + db.getAbsolutePath());
				}
				File cache = new File(pkgFile.getAbsolutePath() + "/cache");
				if (!cache.exists()) {
					cache.mkdir();
					Log.i("***", "新建了" + cache.getAbsolutePath());
				}
				File model = new File(pkgFile.getAbsolutePath() + "/model");
				if (!model.exists()) {
					model.mkdir();
					Log.i("***", "新建了" + model.getAbsolutePath());
				}
				File font = new File(pkgFile.getAbsolutePath() + "/font");
				if (!font.exists()) {
					font.mkdir();
					Log.i("***", "新建了" + font.getAbsolutePath());
				}
				File record = new File(pkgFile.getAbsolutePath() + "/record");
				if (!record.exists()) {
					record.mkdir();
					Log.i("***", "新建了" + record.getAbsolutePath());
				}
				File temp = new File(pkgFile.getAbsolutePath() + "/temp");
				if (!temp.exists()) {
					temp.mkdir();
					Log.i("***", "新建了" + temp.getAbsolutePath());
				}
				File update = new File(pkgFile.getAbsolutePath() + "/update");
				if (!update.exists()) {
					update.mkdir();
					Log.i("***", "新建了" + update.getAbsolutePath());
				}
				File upload = new File(pkgFile.getAbsolutePath() + "/upload");
				if (!upload.exists()) {
					upload.mkdir();
					Log.i("***", "新建了" + upload.getAbsolutePath());
				}
				File voice = new File(pkgFile.getAbsolutePath() + "/voice");
				if (!voice.exists()) {
					voice.mkdir();
					Log.i("***", "新建了" + voice.getAbsolutePath());
				}
				File ins = new File(pkgFile.getAbsolutePath() + "/ins");
				if (!ins.exists()) {
					ins.mkdir();
					Log.i("***", "新建了" + ins.getAbsolutePath());
				}
				File atta = new File(pkgFile.getAbsolutePath() + "/atta");
				if (!atta.exists()) {
					atta.mkdir();
					Log.i("***", "新建了" + atta.getAbsolutePath());
				}

				AssetManager assets = activity.getAssets();

				// Log.d("size", "" + assets.list("db").length);
				// 拷贝assets-db目录下的文件。开始===============================================
				for (String dbFileName : assets.list("db")) {
					File dbFile = new File(db.getAbsolutePath() + "/" + dbFileName);
					if (dbFile.exists() && forceDelete) {
						// 先将数据库删除
						dbFile.delete();
						Log.i("***", "删除了" + dbFile.getAbsolutePath());
					}
					if (!dbFile.exists()) {
						InputStream in = assets.open("db/" + dbFileName);
						FileOutputStream fs = new FileOutputStream(dbFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + dbFileName);
					}
				}
				// 拷贝assets-db目录下的文件。结束===============================================

				// 拷贝assets-model目录下的文件。开始===============================================
				for (String modelFileName : assets.list("model")) {
					File modelFile = new File(model.getAbsolutePath() + "/" + modelFileName);
					if (modelFile.exists() && forceDelete) {
						// 先将数据库删除
						modelFile.delete();
						Log.i("***", "删除了" + modelFile.getAbsolutePath());
					}
					if (!modelFile.exists()) {
						InputStream in = assets.open("model/" + modelFileName);
						FileOutputStream fs = new FileOutputStream(modelFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + modelFileName);
					}
				}
				// 拷贝assets-model目录下的文件。结束===============================================

				// 拷贝assets-font目录下的文件。开始===============================================
				for (String fontFileName : assets.list("font")) {
					File fontFile = new File(font.getAbsolutePath() + "/" + fontFileName);
					if (fontFile.exists() && forceDelete) {
						// 先将文件删除
						fontFile.delete();
						Log.i("***", "删除了" + fontFile.getAbsolutePath());
					}
					if (!fontFile.exists()) {
						InputStream in = assets.open("font/" + fontFileName);
						FileOutputStream fs = new FileOutputStream(fontFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + fontFileName);
					}
				}
				// 拷贝assets-font目录下的文件。结束===============================================
				
				// 拷贝assets-record目录下的文件。开始===============================================
				for (String recordFileName : assets.list("record")) {
					File recordFile = new File(record.getAbsolutePath() + "/" + recordFileName);
					if (recordFile.exists() && forceDelete) {
						// 先将数据库删除
						recordFile.delete();
						Log.i("***", "删除了" + recordFile.getAbsolutePath());
					}
					if (!recordFile.exists()) {
						InputStream in = assets.open("record/" + recordFileName);
						FileOutputStream fs = new FileOutputStream(recordFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + recordFileName);
					}
				}
				// 拷贝assets-record目录下的文件。结束===============================================

				// 拷贝assets-ins目录下的文件。开始===============================================
				for (String insFileName : assets.list("ins")) {
					File insFile = new File(ins.getAbsolutePath() + "/" + insFileName);
					if (insFile.exists() && forceDelete) {
						// 先将文件删除
						insFile.delete();
						Log.i("***", "删除了" + insFile.getAbsolutePath());
					}
					if (!insFile.exists()) {
						InputStream in = assets.open("ins/" + insFileName);
						FileOutputStream fs = new FileOutputStream(insFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + insFileName);
					}
				}
				// 拷贝assets-ins目录下的文件。结束===============================================

				// 拷贝assets-atta目录下的文件。开始===============================================
				for (String attaFileName : assets.list("atta")) {
					File attaFile = new File(atta.getAbsolutePath() + "/" + attaFileName);
					if (attaFile.exists() && forceDelete) {
						// 先将文件删除
						attaFile.delete();
						Log.i("***", "删除了" + attaFile.getAbsolutePath());
					}
					if (!attaFile.exists()) {
						InputStream in = assets.open("atta/" + attaFileName);
						FileOutputStream fs = new FileOutputStream(attaFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + attaFileName);
					}
				}
				// 拷贝assets-atta目录下的文件。结束===============================================

				// 拷贝assets-update目录下的文件。开始===============================================
				// if (forceDelete) {
				// for (String updateFileName : assets.list("update")) {
				// InputStream in = assets.open("update/" + updateFileName);
				// File updateFile = new File(update.getAbsolutePath() + "/" + updateFileName);
				// FileOutputStream fs = new FileOutputStream(updateFile);
				// byte[] buffer = new byte[1444];
				// int byteread = 0;
				// while ((byteread = in.read(buffer)) != -1) {
				// fs.write(buffer, 0, byteread);
				// }
				// in.close();
				// fs.flush();
				// fs.close();
				// Log.i("***", "拷贝了" + updateFileName);
				// }
				// }
				// 拷贝assets-update目录下的文件。结束===============================================

				// 拷贝assets-voice目录下的文件。开始===============================================
				for (String voiceFileName : assets.list("voice")) {
					File voiceFile = new File(voice.getAbsolutePath() + "/" + voiceFileName);
					if (voiceFile.exists() && forceDelete) {
						// 先将文件删除
						voiceFile.delete();
						Log.i("***", "删除了" + voiceFile.getAbsolutePath());
					}
					if (!voiceFile.exists()) {
						InputStream in = assets.open("voice/" + voiceFileName);
						FileOutputStream fs = new FileOutputStream(voiceFile);
						byte[] buffer = new byte[1444];
						int byteread = 0;
						while ((byteread = in.read(buffer)) != -1) {
							fs.write(buffer, 0, byteread);
						}
						in.close();
						fs.flush();
						fs.close();
						Log.i("***", "拷贝了" + voiceFileName);
					}
				}
				// 拷贝assets-voice目录下的文件。结束===============================================

				// Log.i("***", "all");

				// File[] updateFiles = update.listFiles();
				// for (File updateFile : updateFiles) {
				//
				// }
			}
			// Log.i("Hb", "数据库拷贝成功！");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将update目录的数据库文件拷贝到SD卡上
	 * 
	 * @param activity
	 *            {@code Activity} 调用本程序的Activity
	 * */
	public static void copyUpdateDB(AppCompatActivity activity) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File sdcard = Environment.getExternalStorageDirectory();
				File pkgFile = new File(sdcard.getAbsolutePath() + "/" + CommonParam.PROJECT_NAME);
				if (!pkgFile.exists()) {
					pkgFile.mkdir();
					Log.i("***", "新建了" + pkgFile.getAbsolutePath());
				}
				File update = new File(pkgFile.getAbsolutePath() + "/update");
				if (!update.exists()) {
					update.mkdir();
					Log.i("***", "新建了" + update.getAbsolutePath());
				}
				AssetManager assets = activity.getAssets();

				// 拷贝assets-update目录下的文件。开始===============================================
				File dbFile = new File(update.getAbsolutePath() + "/update.db");
				if (dbFile.exists()) {
					// 先将数据库删除
					dbFile.delete();
					Log.i("***", "删除了" + dbFile.getAbsolutePath());
				}
				if (!dbFile.exists()) {
					InputStream in = assets.open("db/sys.db");
					FileOutputStream fs = new FileOutputStream(dbFile);
					byte[] buffer = new byte[1444];
					int byteread = 0;
					while ((byteread = in.read(buffer)) != -1) {
						fs.write(buffer, 0, byteread);
					}
					in.close();
					fs.flush();
					fs.close();
					Log.i("***", "拷贝了" + dbFile.getAbsolutePath());
				}
				// 拷贝assets-update目录下的文件。结束===============================================
			}
			// Log.i("Hb", "数据库拷贝成功！");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将 Bitmap 保存在文件中
	 * 
	 * @param bm
	 *            {@code Bitmap} 图片资源
	 * @param saveFile
	 *            {@code File} 准备保存成的文件
	 * */
	public void saveBitmap(Bitmap bm, File saveFile) throws Exception {
		FileOutputStream iStream = null;
		try {
			iStream = new FileOutputStream(saveFile);

			bm.compress(CompressFormat.JPEG, 100, iStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (iStream != null) {
					iStream.flush();
					iStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 测试方法
	 * */
	public static void main(String[] args) {
	}
}
