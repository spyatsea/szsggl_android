/*
 * Copyright (c) 2017 乔勇(Jacky Qiao) 版权所有
 */
package com.cox.android.szsggl.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.CommonUtil;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 反馈页面
 *
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({"unchecked"})
public class MeFeedbackEditActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;
    /**
     * 返回按钮
     */
    ImageButton backBtn;
    // 界面相关参数。开始===============================
    /**
     * 提交
     */
    private Button submitBtn;

    private EditText titleTv;
    private EditText userNameTv;
    private EditText telTv;
    private EditText emailTv;
    private EditText qqTv;
    private EditText descriptionTv;
    /**
     * 拍照按钮
     */
    private Button cameraBtn;

    /**
     * 照片列表
     */
    private Gallery photoGallery;
    /**
     * 删除附件Dialog
     */
    private AlertDialog deleteAttaDlg;
    /**
     * 图片附件 List
     */
    private List<HashMap<String, Object>> photoList;
    /**
     * 图片数量
     */
    private TextView photoNum;
    /**
     * 选中图片的索引
     */
    private int picSelPos = -1;
    /**
     * 搜索模块，也可去掉地图模块独立使用
     */
    private GeoCoder mSearch = null;
    /**
     * 拍照临时文件
     */
    private File capImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
            + CommonParam.PROJECT_NAME + "/temp/a.jpg");

    // 界面相关参数。结束===============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = MeFeedbackEditActivity.this;

        setContentView(R.layout.me_feedback_edit);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        // 获取Intent
        // Intent intent = getIntent();
        // // 获取Intent上携带的数据
        // Bundle data = intent.getExtras();

        titleText = (TextView) findViewById(R.id.title_text_view);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        // 界面相关参数。开始===============================
        titleTv = (EditText) findViewById(R.id.titleTv);
        userNameTv = (EditText) findViewById(R.id.userNameTv);
        telTv = (EditText) findViewById(R.id.telTv);
        emailTv = (EditText) findViewById(R.id.emailTv);
        qqTv = (EditText) findViewById(R.id.qqTv);
        descriptionTv = (EditText) findViewById(R.id.descriptionTv);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        photoGallery = (Gallery) findViewById(R.id.photoGallery);
        photoNum = (TextView) findViewById(R.id.photoNum);
        // 界面相关参数。结束===============================

        titleText.setSingleLine(true);
        titleText.setText("民生留言");

        registerForContextMenu(photoGallery);

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                makeCancelDialog();
            }
        });
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
        cameraBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (setupLocationFunc()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri capUri = FileProvider.getUriForFile(classThis, CommonParam.FILE_PROVIDER_NAME, capImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, capUri);
                startActivityForResult(intent, CommonParam.REQUESTCODE_CAMERA);
                // }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 执行主进程
        new MainTask().execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.title_bar_backgroud_color);
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定 Activity 返回的结果。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        File noteFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + CommonParam.PROJECT_NAME + "/ins");
        if (!noteFile.exists()) {
            noteFile.mkdir();
        }

        if (requestCode == CommonParam.REQUESTCODE_CAMERA_CROP) {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            intent.setData(Uri.parse(capImageFile.getAbsolutePath()));
            intent.putExtra("crop", true);
            intent.putExtra("outputX", 1024);
            intent.putExtra("outputY", 768);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CommonParam.REQUESTCODE_CAMERA_CROP);
        } else if (requestCode == CommonParam.REQUESTCODE_CAMERA) {
            // 拍照
            // 取出 Intent 里的 Extras 数据
            // 照片
            String attaName = CommonUtil.GetNextID() + ".jpg";
            File attaFile = new File(noteFile.getAbsolutePath() + "/" + attaName);
            // Bundle data = intent.getExtras();
            // Bitmap bitmap = data.getParcelable("data");
            boolean bFlag = false;
            // FileOutputStream b = null;
            // try {
            // b = new FileOutputStream(attaFile);
            // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            // bFlag = true;
            // } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // } finally {
            // try {
            // if (b != null) {
            // b.flush();
            // b.close();
            // }
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // }
            try {
                // new FileUtil().copyFile(capImageFile.getAbsolutePath(),
                // attaFile.getAbsolutePath());
                Bitmap bm = CommonUtil.decodeSampledBitmapFromResource(capImageFile.getAbsolutePath(),
                        CommonParam.SHOW_IMAGE_WIDTH, CommonParam.SHOW_IMAGE_HEIGHT);
                new FileUtil().saveBitmap(bm, attaFile);
                bFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                capImageFile.delete();
            }
            if (bFlag) {
                HashMap<String, Object> atta = new HashMap<String, Object>();
                atta.put("type", CommonParam.ATTA_TYPE_PHOTO);
                atta.put("name", attaName);
                atta.put("size", new FileUtil().getFileSize(attaFile));
                atta.put("ct", CommonUtil.getDT());
                atta.put(
                        "lng",
                        longitude.toString().substring(0,
                                longitude.toString().length() > 15 ? 15 : longitude.toString().length()));
                atta.put(
                        "lat",
                        latitude.toString().substring(0,
                                latitude.toString().length() > 15 ? 15 : latitude.toString().length()));
                atta.put(
                        "lngb",
                        longitude_baidu.toString().substring(0,
                                longitude_baidu.toString().length() > 15 ? 15 : longitude_baidu.toString().length()));
                atta.put(
                        "latb",
                        latitude_baidu.toString().substring(0,
                                latitude_baidu.toString().length() > 15 ? 15 : latitude_baidu.toString().length()));
                atta.put("memo", "");

                photoList.add(atta);
                BaseAdapter adapter = (BaseAdapter) photoGallery.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                // 设置图片数量
                photoNum.setText("(" + photoList.size() + ")");
            }
        }
    }

    /**
     * 返回
     */
    @Override
    public void goBack() {
        super.goBack();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSearch == null) {
            // 初始化MKSearch
            initMSearch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                makeCancelDialog();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * 在菜单显示之前对菜单进行操作
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * 创建上下文菜单
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rec_edit_attach_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        // 附件类型
        String type = (String) info.targetView.getTag();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                // 删除附件
                deleteAtta(type, info.position);
                break;
            default:
        }
        return true;
    }

    /**
     * 主进程 AsyncTask 类
     */
    private class MainTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：设置字段及按钮
         */
        private static final int PROGRESS_SET_FIELD = 1001;
        /**
         * 进度常量：生成信息列表
         */
        private static final int PROGRESS_MAKE_LIST = 1002;
        /**
         * 进度常量：显示图片
         */
        private static final int PROGRESS_SHOW_PHOTO = 1003;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 设置字段及按钮
            publishProgress(PROGRESS_SET_FIELD);

            // 处理数据。开始============================================================================
            // infoTool = getInfoTool();

            photoList = new ArrayList<HashMap<String, Object>>();
            // String picture = (String) point.get("picture");
            // JSONArray js = JSONArray.parseArray(picture);
            // for (int i = 0, len = js.size(); i < len; i++) {
            // JSONObject o = js.getJSONObject(i);
            // HashMap<String, Object> m = new HashMap<String, Object>();
            // m.put("type", o.get("type"));
            // m.put("name", o.get("name"));
            // m.put("size", o.get("size"));
            // m.put("ct", o.get("ct"));
            // m.put("lng", o.get("lng"));
            // m.put("lat", o.get("lat"));
            // m.put("lngb", o.get("lngb"));
            // m.put("latb", o.get("latb"));
            // m.put("memo", o.get("memo"));
            //
            // photoList.add(m);
            // }
            // 显示图片
            publishProgress(PROGRESS_SHOW_PHOTO);
            // 处理数据。结束============================================================================

            // 生成信息列表
            publishProgress(PROGRESS_MAKE_LIST);

            result = CommonParam.RESULT_SUCCESS;
            return result;
        }

        /**
         * invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is
         * undefined. This method is used to display any form of progress in the user interface while the background
         * computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text
         * field.
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == PROGRESS_SET_FIELD) {
                // 设置字段及按钮
                if (baseApp.isLogged) {
                    String userName = (String) baseApp.loginUser.get("realname");
                    userNameTv.setText(userName);
                    userNameTv.setTextColor(getResources().getColor(R.color.normal_text_color_grey));
                    userNameTv.setEnabled(false);
                }
            } else if (progress[0] == PROGRESS_MAKE_LIST) {
            } else if (progress[0] == PROGRESS_SHOW_PHOTO) {
                // 生成图片 Gallery 列表
                // 设置图片数量
                photoNum.setText("(" + photoList.size() + ")");
                makePhotoGallery();
            }

        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // 隐藏等待窗口
            unWait();
        }
    }

    /**
     * 提交信息
     */
    public void submit() {
        boolean submitFlag = false;
        String errorMsg = "";

        String title = titleTv.getText().toString();
        String userName = userNameTv.getText().toString();
        String tel = telTv.getText().toString();
        String email = emailTv.getText().toString();
        String qq = qqTv.getText().toString();
        String desc = descriptionTv.getText().toString();

        if (!CommonUtil.checkNB(title)) {
            errorMsg = "请输入标题！";
        } else if (!CommonUtil.checkNB(userName)) {
            errorMsg = "请输入姓名！";
        } else if (!CommonUtil.checkNB(tel) && !CommonUtil.checkNB(email) && !CommonUtil.checkNB(qq)) {
            errorMsg = "请至少输入一项联系方式（手机号、电子邮件、QQ号）！";
        } else if (!CommonUtil.checkNB(desc)) {
            errorMsg = "请输入留言内容！";
        } else {
            submitFlag = true;
        }

        if (!submitFlag) {
            // 不能提交
            if (CommonUtil.checkNB(errorMsg)) {
                makeAlertDialog(errorMsg);
            }
        } else {
            // 可以提交
            new SubmitTask().execute();
        }
    }

    /**
     * 提交信息 AsyncTask 类
     */
    private class SubmitTask extends AsyncTask<Object, Integer, String> {
        String userId;
        String userName;
        String title;
        String description;
        String tel;
        String email;
        String qq;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在提交，请稍候…");
            submitBtn.setClickable(false);
            submitBtn.setEnabled(false);

            if (baseApp.isLogged) {
                userId = (String) baseApp.loginUser.get("ids");
                userName = (String) baseApp.loginUser.get("realname");
            } else {
                userId = "";
                userName = userNameTv.getText().toString();
            }
            title = titleTv.getText().toString();
            description = descriptionTv.getText().toString();
            tel = telTv.getText().toString();
            email = emailTv.getText().toString();
            qq = qqTv.getText().toString();
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 服务器返回的文本
            String respStr = "";
            // 网络连接对象。开始=================
            Request upHttpRequest = null;
            Response upResponse = null;
            OkHttpClient upHttpClient = null;
            // 网络连接对象。开始=================
            try {
                // 查询信息。开始====================================================================
                // 生成参数。开始======================================
                String ids = CommonUtil.getUUID();
                String createdtime = CommonUtil.getDT();
                String pictures = null;

                JSONObject data = new JSONObject();
                data.put("ids", ids);
                data.put("title", title);
                data.put("description", description);
                data.put("userId", userId);
                data.put("userName", userName);
                data.put("tel", tel);
                data.put("email", email);
                data.put("qq", qq);
                data.put("createdtime", createdtime);
                // 生成参数。结束======================================

                // 设置post值。开始=========================
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                multipartBuilder.addFormDataPart("token", CommonParam.APP_KEY);
                String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/";
                JSONArray pictureArray = new JSONArray();
                for (int i = 0, len = photoList.size(); i < len; i++) {
                    HashMap<String, Object> info = (HashMap<String, Object>) photoList.get(i);
                    // 图片名称
                    String imageName = (String) info.get("name");
                    JSONObject obj = new JSONObject();
                    obj.put("name", imageName);

                    pictureArray.add(obj);

                    multipartBuilder.addFormDataPart("filedata", imageName, RequestBody.create(CommonParam.MEDIA_TYPE_BIN, new File(dirName + imageName)));
                }
                pictures = pictureArray.toJSONString();

                data.put("pictures", pictures);
                multipartBuilder.addFormDataPart("datainfo", data.toJSONString());

                RequestBody requestBody = multipartBuilder.build();
                // 设置post值。结束=========================

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", String.format("%s/%s", CommonParam.CLIENT_USER_AGENT_ANDROID_DEFAULT, getString(R.string.app_versionName)));
                upHttpRequest = requestBuilder
                        .url("http://" + baseApp.serverAddr + "/" + CommonParam.URL_SUBMITFEEDBACK)
                        .post(requestBody)
                        .build();
                if (baseHttpClient == null) {
                    baseHttpClient = new OkHttpClient();
                }
                if (upHttpClient == null) {
                    upHttpClient = baseHttpClient.newBuilder().connectTimeout(WAIT_SECONDS, TimeUnit.SECONDS).build();
                }

                upResponse = upHttpClient.newCall(upHttpRequest).execute();
                Log.d("#succ", "#" + upResponse.isSuccessful());
                if (upResponse.code() == 200) {
                    // 获取成功
                    byte[] respBytes = upResponse.body().string().getBytes("UTF-8");

                    // 如果返回的xml中有BOM头，要将其删除
                    if (respBytes.length >= 3 && respBytes[0] == FileUtil.UTF8BOM[0]
                            && respBytes[1] == FileUtil.UTF8BOM[1] && respBytes[2] == FileUtil.UTF8BOM[2]) {
                        respStr = new String(respBytes, 3, respBytes.length - 3, "UTF-8");
                    } else {
                        respStr = new String(respBytes, "UTF-8");
                    }
                    Log.d("##", "#" + respStr);

                    JSONObject respJson = JSONObject.parseObject(respStr);
                    String resultStr = respJson.getString("result");
                    if (CommonParam.RESPONSE_SUCCESS.equals(resultStr)) {
                        // 请求正确
                        result = CommonParam.RESULT_SUCCESS;
                    }
                } else {
                    // 服务器连接失败
                }
                // 查询信息。结束====================================================================
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

            return result;
        }

        /**
         * invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is
         * undefined. This method is used to display any form of progress in the user interface while the background
         * computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text
         * field.
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // 隐藏等待窗口
            unWait();
            if (CommonParam.RESULT_SUCCESS.equals(result)) {
                show("信息提交成功，非常感谢您的宝贵建议！");
                goBack();
            } else {
                show("提交失败，请检查网络连接！");
                submitBtn.setClickable(true);
                submitBtn.setEnabled(true);
            }
        }
    }

    /**
     * 显示取消Dialog
     */
    public void makeCancelDialog() {
        Builder dlgBuilder = new Builder(this);

        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage("信息还没有保存，确定退出吗？");
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                goBack();
            }
        });

        AlertDialog dlg = dlgBuilder.create();
        dlg.show();
    }

    /**
     * 显示附件信息
     */
    public void showAttaDetail(String type, int pos) {
        HashMap<String, Object> atta = null;
        if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
            // 图片
            atta = photoList.get(pos);
        }
        makeAttaDetailDialog(atta);
    }

    /**
     * 删除附件
     */
    public void deleteAtta(String type, int pos) {
        HashMap<String, Object> atta = null;
        if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
            // 图片
            atta = photoList.get(pos);
        }
        makeDeleteAttaDialog(atta);
    }

    /**
     * 显示编辑附件备注对话框
     *
     * @param atta {@code HashMap<String, Object>} 绑定的信息
     */
    public void makeAttaDetailDialog(HashMap<String, Object> atta) {
        Builder dlgBuilder = new Builder(this);
        ScrollView layout = null;
        // 附件类型
        String type = (String) atta.get("type");
        layout = (ScrollView) getLayoutInflater().inflate(R.layout.dlg_atta_detail, null);
        dlgBuilder.setView(layout);
        dlgBuilder.setTitle(R.string.ins_column_atta_detail);
        dlgBuilder.setIcon(R.drawable.menu_document_info);
        dlgBuilder.setCancelable(true);

        // 附件图片
        ImageView imageView = (ImageView) layout.findViewById(R.id.atta_image);
        // 附件名称
        TextView attaFileName = (TextView) layout.findViewById(R.id.atta_filename);
        // 附件类型
        TextView attaFileType = (TextView) layout.findViewById(R.id.atta_filetype);
        // 附件大小
        TextView attaFileSize = (TextView) layout.findViewById(R.id.atta_filesize);
        // 创建时间
        TextView attaCreatedtime = (TextView) layout.findViewById(R.id.atta_createdtime);

        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CommonParam.PROJECT_NAME
                + "/ins/" + atta.get("name");

        File attaFile = new File(filepath);
        if (type.equals(CommonParam.ATTA_TYPE_PHOTO)) {
            // 图片
            // 照片
            Bitmap bm = null;
            if (attaFile.exists()) {
                // 图片文件存在
                bm = CommonUtil.decodeSampledBitmapFromResource(filepath, getResources().getInteger(R.integer.gallery_thumbnail_width),
                        getResources().getInteger(R.integer.gallery_thumbnail_height));
            } else {
                // 图片文件不存在
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.thumbnail);
            }
            attaFileSize.setText((String) atta.get("size"));
            attaCreatedtime.setText((String) atta.get("ct"));
            imageView.setImageBitmap(bm);
        }
        LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = getResources().getInteger(R.integer.gallery_thumbnail_height);
        layoutParams.width = getResources().getInteger(R.integer.gallery_thumbnail_width);
        imageView.setWillNotCacheDrawing(false);

        attaFileName.setText((String) atta.get("name"));
        attaFileType.setText(CommonUtil.N2B(CommonParam.CODE_ATTA_TYPE().get(type)));

        dlgBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dlg = dlgBuilder.create();
        dlg.show();

        // 确定按钮
        Button confirmBtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmBtn.setTag(dlg);

        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dlg = (AlertDialog) v.getTag();
                dlg.cancel();
            }
        });
    }

    /**
     * 显示删除附件对话框
     *
     * @param atta {@code HashMap<String, Object>} 绑定的信息
     */
    public void makeDeleteAttaDialog(HashMap<String, Object> atta) {
        // 附件类型
        String type = (String) atta.get("type");
        Builder dlgBuilder = new Builder(this);

        dlgBuilder.setTitle(R.string.alert_ts);
        if (type.equals(CommonParam.ATTA_TYPE_PHOTO)) {
            dlgBuilder.setMessage(R.string.alert_whether_delete_photo);
        }
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        deleteAttaDlg = dlgBuilder.create();
        deleteAttaDlg.show();
        // 取消按钮
        Button cancelBtn = deleteAttaDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 确定按钮
        Button confirmBtn = deleteAttaDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 绑定数据
        confirmBtn.setTag(atta);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteAttaDlg.cancel();
            }
        });
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 附件信息
                HashMap<String, Object> atta = (HashMap<String, Object>) v.getTag();
                // 删除附件
                new DeleteAttaTask().execute(atta);
                deleteAttaDlg.cancel();
            }
        });
    }

    /**
     * 生成图片 Gallery 列表
     */
    public void makePhotoGallery() {
        // 为 Gallery 设置 adapter
        BaseAdapter adapter = new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 照片的 ImageView
                ImageView imageView = new ImageView(classThis);
                imageView.setTag(CommonParam.ATTA_TYPE_PHOTO);
                HashMap<String, Object> atta = photoList.get(position);
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");

                File attaFile = new File(filepath);
                // 照片
                Bitmap bm = null;
                if (attaFile.exists()) {
                    // 图片文件存在
                    bm = CommonUtil.decodeSampledBitmapFromResource(filepath, getResources().getInteger(R.integer.gallery_thumbnail_width),
                            getResources().getInteger(R.integer.gallery_thumbnail_height));
                } else {
                    // 图片文件不存在
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.thumbnail);
                }

                imageView.setImageBitmap(bm);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new Gallery.LayoutParams(getResources().getInteger(R.integer.gallery_thumbnail_width),
                        getResources().getInteger(R.integer.gallery_thumbnail_height)));
                TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
                imageView.setBackgroundResource(typedArray.getResourceId(
                        R.styleable.Gallery_android_galleryItemBackground, 0));
                imageView.setWillNotCacheDrawing(false);
                return imageView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public int getCount() {
                return photoList.size();
            }
        };
        photoGallery.setAdapter(adapter);
        photoGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                picSelPos = position;
                // HashMap<String, Object> atta = photoList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        photoGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了选中的图片
                if (picSelPos == position) {
                    HashMap<String, Object> atta = photoList.get(position);
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + CommonParam.PROJECT_NAME + "/ins/" + atta.get("name");
                    // Double lon = Double.parseDouble((String) atta.get("lng"));
                    // Double lat = Double.parseDouble((String) atta.get("lat"));
                    // Double lon_baidu = Double.parseDouble((String) atta.get("lngb"));
                    // Double lat_baidu = Double.parseDouble((String) atta.get("latb"));
                    // Bundle infoBundle = null;
                    // if ((lon_baidu != 0D && lat_baidu != 0D) || (lon != 0D && lat != 0D)) {
                    // infoBundle = new Bundle();
                    // infoBundle.putDouble("lon", lon);
                    // infoBundle.putDouble("lat", lat);
                    // infoBundle.putDouble("lon_baidu", lon_baidu);
                    // infoBundle.putDouble("lat_baidu", lat_baidu);
                    // }
                    // 创建信息传输Bundle
                    Bundle data = new Bundle();
                    data.putString("title", (String) atta.get("name"));
                    data.putString("filepath", filepath);
                    // 创建启动 Activity 的 Intent
                    Intent intent = new Intent(classThis, ShowImageActivity.class);
                    // 将数据存入 Intent 中
                    intent.putExtras(data);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 初始化MKSearch
     */
    public void initMSearch() {
        // 定位相关参数。开始=================================================
        // 初始化搜索模块，注册事件监听
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
        }
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            /**
             * 反地理编码查询结果回调函数
             * */
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                // String address = "";
                // AddressComponent ac = null;
                // if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // address = getString(R.string.info_unknow);
                // } else {
                // ac = result.getAddressDetail();
                // 反地理编码：通过坐标点检索详细地址及周边poi
                // address = ac.city + ac.district + ac.street + ac.streetNumber;
                // }
            }

            /**
             * 地理编码查询结果回调函数
             * */
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }
        });
        // 定位相关参数。结束=================================================
    }

    /**
     * 删除附件的 AsyncTask 类
     */
    public class DeleteAttaTask extends AsyncTask<Object, Integer, String> {
        /**
         * 进度常量：删除成功
         */
        private static final int PROGRESS_DELETE_SUCCESS = 1001;
        /**
         * 进度常量：删除失败
         */
        private static final int PROGRESS_DELETE_FAIL = 1002;
        /**
         * 进度常量：显示音频
         */
        private static final int PROGRESS_SHOW_AUDIO = 1003;
        /**
         * 进度常量：显示图片
         */
        private static final int PROGRESS_SHOW_PHOTO = 1004;
        /**
         * 进度常量：显示视频
         */
        private static final int PROGRESS_SHOW_VIDEO = 1005;

        /**
         * 附件信息
         */
        private HashMap<String, Object> atta;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            // 附件信息
            atta = (HashMap<String, Object>) params[0];

            // 删除成功
            new FileUtil().deleteFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + CommonParam.PROJECT_NAME + "/ins", (String) atta.get("name"));

            // 附件类型
            String type = (String) atta.get("type");
            if (CommonParam.ATTA_TYPE_PHOTO.equals(type)) {
                // 显示图片
                publishProgress(PROGRESS_SHOW_PHOTO);
            }
            // 删除成功
            publishProgress(PROGRESS_DELETE_SUCCESS);

            result = CommonParam.RESULT_SUCCESS;
            return result;
        }

        /**
         * invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is
         * undefined. This method is used to display any form of progress in the user interface while the background
         * computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text
         * field.
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == PROGRESS_DELETE_SUCCESS) {
                // 删除成功
                show(R.string.alert_delete_success);
            }
            if (progress[0] == PROGRESS_DELETE_FAIL) {
                // 删除失败
                show(R.string.alert_delete_fail);
            } else if (progress[0] == PROGRESS_SHOW_PHOTO) {
                // 生成图片 Gallery 列表
                photoList.remove(atta);
                BaseAdapter adapter = (BaseAdapter) photoGallery.getAdapter();
                adapter.notifyDataSetChanged();
                // 设置图片数量
                photoNum.setText("(" + photoList.size() + ")");
            }
        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
        }

    }

    /**
     * 主进程 AsyncTask 类
     *
     * @deprecated
     */
    @Deprecated
    private class ReverseGeocodeTask extends AsyncTask<Object, Integer, String> {

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_SUCCESS;
            doWait(1000);
            return result;
        }

        /**
         * invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is
         * undefined. This method is used to display any form of progress in the user interface while the background
         * computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text
         * field.
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        /**
         * invoked on the UI thread after the background computation finishes. The result of the background computation
         * is passed to this step as a parameter. The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude_baidu,
            // longitude_baidu)));// 逆地址解析
        }
    }
}
