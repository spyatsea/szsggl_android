/*
 * Copyright (c) www.spyatsea.com  2016
 */
package com.cox.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.LocationManager;

import com.cox.android.szsggl.R;
import com.cox.dto.CodeNameDTO;

import okhttp3.MediaType;

/**
 * 定义了一些静态字段和方法。
 *
 * @author 乔勇(Jacky Qiao)
 */
public class CommonParam {
    // 项目自定义参数区。开始====================================================
    /**
     * 项目名称
     */
    public static final String PROJECT_NAME = "szsggl";
    /**
     * 是否为测试模式
     */
    public static final boolean TEST_MODE = false;
    /**
     * 消息中心最多保存的消息数量
     */
    public static final int REMAIN_MESSAGE_NUM_DEFAULT = 1000;
    /**
     * 扫码类型：信息
     */
    public static final int SCAN_TYPE_INFO = 0;
    /**
     * 扫码类型：产品签收
     */
    public static final int SCAN_TYPE_IN = 1;
    /**
     * 扫码类型：产品归还
     */
    public static final int SCAN_TYPE_OUT = 2;

    /**
     * UHF天线功率：最大值
     */
    public static final int UHF_ANTENNA_POWER_MAX = 30;
    /**
     * UHF天线功率：最小值
     */
    public static final int UHF_ANTENNA_POWER_MIN = 10;
    /**
     * UHF天线功率：扫描卡片用的值
     */
    public static final int UHF_ANTENNA_POWER_SCAN = 30;
    /**
     * UHF天线功率：绑定卡片用的值
     */
    public static final int UHF_ANTENNA_POWER_BIND = 12;

    /**
     * UHF停止间隔：最小值
     */
    public static final int UHF_SLEEP_MIN = 1;
    /**
     * UHF停止间隔：扫描卡片用的值
     */
    public static final int UHF_SLEEP_SCAN = 50;
    /**
     * UHF停止间隔：绑定卡片用的值
     */
    public static final int UHF_SLEEP_BIND = 30;
    /**
     * UHF常量：波特率
     */
    public static final int UHF_BAUD = 115200;
    /**
     * UHF设备型号：PDA
     */
    public static final String UHF_MODEL_PDA = "PDA";
    /**
     * UHF设备型号：UHFPDA
     * <p>这是一个自定义型号</p>
     */
    public static final String UHF_MODEL_UHFPDA = "UHFPDA";
    /**
     * UHF扫描读取间隔（毫秒）
     */
    public static final int UHF_SCAN_INTERVAL = 100;
    /**
     * UHF扫卡快签信息保存间隔（秒）
     */
    public static final int UHF_AUTO_DK_OVER_TIME = 30;
    /**
     * UHF扫卡快签信息保留时间（秒）
     */
    public static final int UHF_AUTO_DK_STAY_TIME = 15;
    /**
     * UHF状态常量s
     */
    public static final int UHF_MSG_UPDATE_LISTVIEW = 0;
    public static final int UHF_MSG_UPDATE_TIME = 1;
    public static final int UHF_MSG_UPDATE_ERROR = 2;
    public static final int UHF_MSG_UPDATE_START = 3;
    public static final int UHF_MSG_UPDATE_STOP = 4;
    public static final int UHF_MSG_UPDATE_STOPPING = 5;
    public static final int UHF_MSG_UPDATE_EPC = 6;
    /**
     * UHF检查关闭间隔（毫秒）
     */
    public static final int UHF_CHECK_STOP_INTERVAL = 100;
    /**
     * UHF检查关闭次数
     */
    public static final int UHF_CHECK_STOP_TOTAL = 30;
    /**
     * UHF开关按键代码默认值
     * <p>默认是红色扳机键的代码：307</p>
     */
    public static final int UHF_KEY_CODE_DEFAULT = 307;
    /**
     * 项目的FileProvider名称
     */
    public static final String FILE_PROVIDER_NAME = "com.cox.android.szsggl.fileprovider";
    // KEY常量。开始================================================================
    /**
     * 程序识别码，用于校验客户端有效性
     */
    public static final String APP_KEY = "13f5eee7fe1b4ef7a917212a4cfad907";
	// 测试
	// public static final String BAIDU_SECRET_KEY = "KTYtuvC8M6INIvEHULsBeNIhWgFVI5ee";
	// 发布
	public static final String BAIDU_SECRET_KEY = "KTYtuvC8M6INIvEHULsBeNIhWgFVI5ee";

    /**
     * 腾讯appid
     */
    public static final String TENCENT_APPID = "";

    /**
     * 微信开放平台的APP_ID（发布）
     */
    public static final String WEIXIN_OPEN_APP_ID = "";
    /**
     * 微信开放平台的AppSecret（发布）
     */
    public static final String WEIXIN_OPEN_SECRET_KEY = "";

    /**
     * 微博app_key
     */
    public static final String WEIBO_APP_KEY = "";

    /**
     * 当前微博应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI 关于 Scope
     * 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    // KEY常量。结束================================================================

    /**
     * 账号：游客
     */
    public static final String ACCOUNT_VISITOR = "visitor";

    /**
     * 图片缩放后的最大高度
     */
    public static final int PIC_MAX_HEIGHT = 210;
    /**
     * sqlite数据库根目录
     */
    public static final String DB_MODEL_DIR = "E:\\sqlite";
    /**
     * 触屏点击感应距离：水平
     */
    public static final int TOUCH_ACTIVE_H = 150;

    /**
     * 列表中最大显示的结果条数
     * <p>
     * 0 表示不限制条数, RESULT_MAX > 0 表示限制条数
     */
    public static final int RESULT_MAX = 20;
    /**
     * 记录列表中最大显示的结果条数
     * <p>
     * 0 表示不限制条数, RESULT_MAX > 0 表示限制条数
     */
    public static final int RESULT_MAX_REC = 300;
    /**
     * 记录列表中最大显示的结果条数
     * <p>
     * 0 表示不限制条数, RESULT_MAX > 0 表示限制条数
     */
    public static final int RESULT_MAX_INS = 50;
    /**
     * 记录列表中最大显示的结果条数
     * <p>
     * 0 表示不限制条数, RESULT_MAX > 0 表示限制条数
     */
    public static final int RESULT_MAX_INS_POINT = 0;

    /**
     * 列表中最大显示的结果条数：不限量
     * <p>
     * 0 表示不限制条数
     */
    public static final int RESULT_UNLIMITED = 0;
    /**
     * 列表中最大显示的结果条数
     * <p>
     * RESULT_MAX = 0 表示不限制条数 RESULT_MAX > 0 表示限制条数
     */
    public static final int RESULT_REC_MAX = 10000;
    /**
     * 信息列表中每次显示的信息数量
     */
    public static final int RESULT_LIST_PER = 15;
    /**
     * 默认进度
     */
    public static final int PROGRESS_MAX = 10;
    /**
     * 附件类型：图片
     */
    public static final String ATTA_TYPE_PHOTO = "PHOTO";
    /**
     * 附件类型：图片
     */
    public static final String ATTA_TYPE_VIDEO = "VIDEO";
    /**
     * 附件类型：音频
     */
    public static final String ATTA_TYPE_AUDIO = "AUDIO";
    /**
     * 附件目录：图片
     */
    public static final String ATTA_DIR_PHOTO = "photo";

    /**
     * 附件类型名称Map
     *
     * @return {@code Map} 允许Map
     */
    public static final Map<String, String> CODE_ATTA_TYPE() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("PHOTO", "图片");
        map.put("VIDEO", "视频");
        map.put("AUDIO", "音频");
        return map;
    }

    /**
     * 方法名：列表
     */
    public static final String METHOD_LIST = "LIST";
    /**
     * 方法名：新建
     */
    public static final String METHOD_NEW = "NEW";
    /**
     * 方法名：编辑
     */
    public static final String METHOD_EDIT = "EDIT";
    /**
     * 方法名：保存
     */
    public static final String METHOD_SAVE = "SAVE";
    /**
     * 方法名：删除
     */
    public static final String METHOD_DELETE = "DELETE";
    /**
     * 是否：否
     */
    public static final String NO = "0";
    /**
     * 是否：是
     */
    public static final String YES = "1";
    /**
     * 有无附件：无
     */
    public static final String ATTACH_NO = "0";
    /**
     * 有无附件：有
     */
    public static final String ATTACH_YES = "1";
    /**
     * 是否激活：无
     */
    public static final String ACTIVE_NO = "0";
    /**
     * 是否激活：有
     */
    public static final String ACTIVE_YES = "1";
    /**
     * 是否有效：无
     */
    public static final String VALID_NO = "0";
    /**
     * 是否有效：有
     */
    public static final String VALID_YES = "1";
    /**
     * 记录状态：正常
     */
    public static final String STATUS_NORMAL = "0";
    /**
     * 记录状态：正在上传
     */
    public static final String STATUS_UPLOADING = "1";
    /**
     * 记录状态：已上传
     */
    public static final String STATUS_UPLOADED = "2";
    /**
     * 处理状态：未接收
     */
    public static final String PROCESS_STATUS_未接收 = "未接收";
    /**
     * 处理状态：已接收
     */
    public static final String PROCESS_STATUS_已接收 = "已接收";
    /**
     * 处理状态：已接收
     */
    public static final String PROCESS_STATUS_已处理 = "已处理";
    /**
     * 结果：成功
     */
    public static final String RESULT_SUCCESS = "success";
    /**
     * 结果：出错
     */
    public static final String RESULT_ERROR = "error";
    /**
     * 结果：显示错误信息
     */
    public static final String RESULT_SHOW_ERRORS = "showerrors";
    /**
     * 结果：结果格式错误
     */
    public static final String RESULT_FORMAT_ERROR = "formaterror";
    /**
     * 结果：错误的程序识别码
     */
    public static final String RESULT_INVALIDKEY = "invalidkey";
    /**
     * 结果：错误的程序版本号
     */
    public static final String RESULT_INVALIDVER1 = "invalidver";
    /**
     * 结果：程序已经是最近版本
     */
    public static final String RESULT_IS_LATESTVER = "islatestver";
    /**
     * 结果：网络错误
     */
    public static final String RESULT_NET_ERROR = "neterror";
    /**
     * 结果：开始
     */
    public static final String RESULT_START = "start";
    /**
     * 结果：登录
     */
    public static final String RESULT_LOGIN = "login";
    /**
     * 进度常量：开始
     */
    public static final int PROGRESS_START = 0;
    /**
     * 进度常量：重置数据
     */
    public static final int PROGRESS_RESET_LIST_DATA = 1;
    /**
     * 进度常量：处理数据
     */
    public static final int PROGRESS_PROCESS_DATA = 2;
    /**
     * 进度常量：结果正常
     */
    public static final int PROGRESS_OK = 3;
    /**
     * 进度常量：结果异常
     */
    public static final int PROGRESS_ERROR = 4;
    /**
     * 进度常量：显示错误信息
     */
    public static final int PROGRESS_SHOW_ERRORS = 5;
    /**
     * 进度常量：结束
     */
    public static final int PROGRESS_END = 100;
    /**
     * 字段类型常量：非备注字段，只读
     */
    public static final int COLUMN_TYPE_1 = 1;
    /**
     * 字段类型常量：非备注文本字段，可编辑，仅文本
     */
    public static final int COLUMN_TYPE_2 = 2;
    /**
     * 字段类型常量：非备注文本字段，可编辑，有备选值
     */
    public static final int COLUMN_TYPE_3 = 3;
    /**
     * 字段类型常量：备注文本字段，可编辑，仅文本
     */
    public static final int COLUMN_TYPE_4 = 4;
    /**
     * 字段类型常量：备注文本字段，可编辑，有备选值
     */
    public static final int COLUMN_TYPE_5 = 5;
    /**
     * 字段类型常量：备注字段，内部包含多种控件
     */
    public static final int COLUMN_TYPE_6 = 6;
    /**
     * 字段结果类型常量：√|×|/
     */
    public static final int COLUMN_RESULT_TYPE_1 = 1;
    /**
     * 字段结果类型常量：有|无|是|否
     */
    public static final int COLUMN_RESULT_TYPE_2 = 2;
    /**
     * 结果类型常量：正常
     */
    public static final String RESULT_TYPE_NORMAL = "0";
    /**
     * 结果类型常量：异常
     */
    public static final String RESULT_TYPE_ABNORMAL = "1";
    /**
     * 结果类型常量：不适用
     */
    public static final String RESULT_TYPE_NOTFIT = "2";
    /**
     * 客户端默认User-Agent:Android
     */
    public static final String CLIENT_USER_AGENT_ANDROID_DEFAULT = "CoxAppAndroid";
    /**
     * URL：静态文件地址
     */
    public static final String URL_UPLOADFILES = "UploadFiles";
    /**
     * URL：上传数据库文件
     */
    public static final String URL_UPLOAD_INS_DB = "fileUp_upload_sgins_dbfile.action";

    /**
     * URL：上传巡视附件
     */
    public static final String URL_UPLOAD_INS_ATTACHMENT = "fileUp_upload_szsgins_attachfile.action";
    /**
     * URL指令：完成上传巡检信息
     */
    public static final String URLCOMMAND_FINISH_UP_INS = "finishupins";
    // URL常量。开始=======================================================
    public static final String URL_SEARCHTABLE = "szss_searchtable.action";
    public static final String URL_EXECSQL = "szss_execsql.action";
    public static final String URL_COMMAND = "szss_command.action";
    public static final String URL_CHANGEUSERPWD = "szss_changeuserpwd.action";
    public static final String URL_CHECKUPDATE = "szss_checkupdate.action";
    public static final String URL_CHECKUSER = "szss_checkuser.action";
    public static final String URL_CHECKUSERDETAIL = "szss_checkuserdetail.action";
    public static final String URL_SUBMITINFO = "szss_submitinfo.action";
    public static final String URL_PRIVACY = "privacy.html";

    public static final String URL_SAVEINFO = "szss_saveinfo.action";
    public static final String URL_GETNEWSINFOLIST = "szss_getnewsinfolist.action";
    public static final String URL_NEWSINFODETAIL = "info0002_detail.action";
    public static final String URL_SUBMITFEEDBACK = "fileUp_submitfeedback.action";
    public static final String URL_UPDATEUSERINFO = "fileUp_updateuserinfo.action";
    // public static final String URL_CHECKSTUDENTDETAIL = "s_checkstudentdetail.action";
    // public static final String URL_FEEDBACK = "s_feedback.action";
    // public static final String URL_CHECKLISTUPDATE = "s_checklistupdate.action";
    // public static final String URL_SETPLANTFAVSTATUS = "s_setplantfavstatus.action";
    // public static final String URL_CHECKPLANTFAVSTATUS = "s_checkplantfavstatus.action";
    // public static final String URL_SUBMITSTUDENT = "s_submitstudent.action";
    // public static final String URL_CHANGEPWD = "s_changepwd.action";
    // public static final String URL_SENDACTIVEMAIL = "s_sendactivemail.action";
    // public static final String URL_PLANTINFODETAIL_TEST = "hhccplant0002_appdetail.action";
    // public static final String URL_GETFAVPLANTLIST = "s_getfavplantlist.action";
    // public static final String URL_PLANTSHARE = "plant_share.action";

    // URL常量。结束=======================================================

    // FTP常量。开始=======================================================
    /**
     * FTP常量：用户名
     */
    public static final String FTP_USERNAME = "ftp";
    /**
     * FTP常量：密码
     */
    public static final String FTP_PASSWORD = "Aa123456";
    /**
     * FTP常量：端口
     */
    public static final int FTP_PORT = 21;
    /**
     * FTP常量：本地图片下载目录
     */
    public static final String FTP_LOCAL_RECORDS = "record";
    /**
     * FTP常量：服务器文件目录
     */
    public static final String FTP_REMOTE_RECORDS = "szsggl/image";
    // FTP常量。结束=======================================================
    /**
     * 服务器响应值：请求正确
     */
    public static final String RESPONSE_SUCCESS = "1";
    /**
     * 服务器响应值：请求出错
     */
    public static final String RESPONSE_ERROR = "0";
    /**
     * 查询资讯类型：查询信息总数
     */
    public static final String SEARCH_INFO_TYPE_TOTAL = "total";
    /**
     * 查询资讯类型：查询队列前的信息
     */
    public static final String SEARCH_INFO_TYPE_HEADER = "header";
    /**
     * 查询资讯类型：查询队列后的信息
     */
    public static final String SEARCH_INFO_TYPE_FOOTER = "footer";
    /**
     * GALLERY 中缩略图的宽度
     *
     * @deprecated 已被integers.xml中的gallery_thumbnail_width代替
     */
    @Deprecated
    public static final int GALLERY_THUMBNAIL_WIDTH = 280;
    /**
     * GALLERY 中缩略图的高度
     *
     * @deprecated 已被integers.xml中的gallery_thumbnail_height代替
     */
    @Deprecated
    public static final int GALLERY_THUMBNAIL_HEIGHT = 210;

    /**
     * 读卡方式：不处理卡
     */
    public static final int READ_CARD_TYPE_NO_ACTION = 0;
    /**
     * 读卡方式：预读卡片
     */
    public static final int READ_CARD_TYPE_PRE_READ = 1;
    /**
     * 读卡方式：读取卡片
     */
    public static final int READ_CARD_TYPE_READ = 2;
    /**
     * 读卡方式：带密码读取卡片
     */
    public static final int READ_CARD_TYPE_READ_WITH_PASSWORD = 3;
    /**
     * 读卡方式：写入卡片
     */
    public static final int READ_CARD_TYPE_WRITE = 4;
    /**
     * 读卡方式：加密卡片
     */
    public static final int READ_CARD_TYPE_WRITE_WITH_PASSWORD = 5;
    /**
     * 读卡方式：读取额外的卡片
     */
    public static final int READ_CARD_TYPE_EXTRA_CARD = 6;
    /**
     * 读卡方式：巡视卡
     */
    public static final int READ_CARD_TYPE_INSPECT = 7;
    /**
     * 读UHF卡方式：不处理卡
     */
    public static final int READ_UHF_CARD_TYPE_NO_ACTION = 0;
    /**
     * 读UHF卡方式：巡视卡
     */
    public static final int READ_UHF_CARD_TYPE_INSPECT = 7;
    /**
     * 导航标志：previous(前一个)
     */
    public static final int NAV_FLAG_PRE = 0;
    /**
     * 导航标志：next(后一个)
     */
    public static final int NAV_FLAG_NEXT = 1;
    /**
     * 导航标志：no
     */
    public static final int NAV_FLAG_NO = 100;
    /**
     * 导航标志：yes
     */
    public static final int NAV_FLAG_YES = 101;
    /**
     * 图片查看窗口中图片的宽度
     */
    public static final int SHOW_IMAGE_WIDTH = 1536;
    /**
     * 图片查看窗口中图片的高度
     */
    public static final int SHOW_IMAGE_HEIGHT = 2048;
    /**
     * 缩放图片的宽度
     */
    public static final int RESIZE_IMAGE_WIDTH = 1024;
    /**
     * 缩放缩略图片的宽度
     */
    public static final int RESIZE_THUMB_IMAGE_WIDTH = 120;
    /**
     * 更新类型：主程序
     */
    public static final String UPDATETYPE_APP = "UPDATEAPP";
    /**
     * 更新类型：启动画面
     */
    public static final String UPDATETYPE_SPPIC = "UPDATESPPIC";
    /**
     * 更新类型：类型
     */
    public static final String UPDATETYPE_TYPES = "UPDATETYPES";

    /**
     * 存放数据的目录名称
     */
    public static final String INS_FOLDER_NAME = "szsggl";
    /**
     * 默认打包名
     */
    public static final String APK_PACKAGE_DEFAULT = "szsggl.apk";
    /**
     * 系统设置信息：定位距离
     */
    public static int SYSCONFIG_VALUE_INS_DISTANCE = 500;
    /**
     * 通知类型：资讯更新
     */
    public static final int NM_TYPE_NEWS_UPDATE = 0;
    /**
     * 通知类型：资讯信息
     */
    public static final int NM_TYPE_NEWS = 1;
    /**
     * 通知类型：业务信息
     */
    public static final int NM_TYPE_BIZ = 2;
    /**
     * 通知类型：程序更新
     */
    public static final int NM_TYPE_APP_UPDATE = 3;
    /**
     * 通知类型：数据更新
     */
    public static final int NM_TYPE_DATA_UPDATE = 4;
    /**
     * 通知类型：远程指令
     */
    public static final int NM_TYPE_COMMAND = 5;
    /**
     * 指令：无指令
     */
    public static final int COMMANDCODE_NONE = 0;
    /**
     * 指令：退出程序
     */
    public static final int COMMANDCODE_EXIT = 1;
    /**
     * 指令：重置数据
     */
    public static final int COMMANDCODE_RESETDB = 2;
    /**
     * 用户注册结果：用户已经存在
     */
    public static final String USER_REGISTER_STATUS_EXISTS = "0";
    /**
     * 用户登录结果：用户不存在
     */
    public static final String USER_CHECK_STATUS_NOT_EXISTS = "0";
    /**
     * 用户登录结果：密码错误
     */
    public static final String USER_CHECK_STATUS_PASSWORD_ERROR = "1";
    /**
     * 用户登录结果：未激活
     */
    public static final String USER_CHECK_STATUS_INACTIVE = "2";
    /**
     * 新闻信息类型：普通信息
     */
    public static final String NEWS_INFO_TYPE_NORMAL = "0";
    /**
     * 新闻信息类型：小图信息
     */
    public static final String NEWS_INFO_TYPE_SMALL_PIC = "1";
    /**
     * 新闻信息类型：大图信息
     */
    public static final String NEWS_INFO_TYPE_BIG_PIC = "2";
    /**
     * 业务消息类型：普通信息
     */
    public static final String MESSAGE_INFO_TYPE_NORMAL = "1";
    /**
     * 新闻栏目类型：新闻类
     */
    public static final String NEWS_CHANNEL_TYPE_NEWS = "1";
    /**
     * 新闻栏目类型：产品展示类
     */
    public static final String NEWS_CHANNEL_TYPE_PRODUCT = "2";
    /**
     * 新闻栏目类型：规章类
     */
    public static final String NEWS_CHANNEL_TYPE_RULE = "3";
    /**
     * 新闻栏目类型：专题类
     */
    public static final String NEWS_CHANNEL_TYPE_COLUMN = "4";
    /**
     * 参数配置信息
     */
    public static HashMap<String, Object> infoConfig;

    /**
     * 更新类型List
     *
     * @return {@code List} 更新类型List
     */
    public static final List<String> CODE_UPDATETYPE_LIST() {
        List<String> list = new ArrayList<String>();
        list.add(UPDATETYPE_APP);
        list.add(UPDATETYPE_SPPIC);
        list.add(UPDATETYPE_TYPES);
        return list;
    }

    /**
     * 权限标志位：查看执法监察
     */
    public static int PERMISSION_POS_INS_VIEW = 11;
    /**
     * 权限标志位：查看随手拍信息
     */
    public static int PERMISSION_POS_JC_VIEW = 12;
    /**
     * 权限标志位：执法监察
     */
    public static int PERMISSION_POS_INS = 13;
    /**
     * 权限标志位：随手拍
     */
    public static int PERMISSION_POS_JC = 14;
    /**
     * 排序标志：升序
     */
    public static final String ORDER_ASC = "ASC";
    /**
     * 排序标志：降序
     */
    public static final String ORDER_DESC = "DESC";
    /**
     * 巡视点状态：正常
     */
    public static final String INS_POINT_STATUS_NORMAL = "0";
    /**
     * 巡视点状态：下一个
     */
    public static final String INS_POINT_STATUS_NEXT = "1";
    /**
     * 巡视点状态：已巡视
     */
    public static final String INS_POINT_STATUS_DONE = "2";
    /**
     * 巡视点状态：忽略
     */
    public static final String INS_POINT_STATUS_IGNORE = "3";
    /**
     * 正则表达式：浮点数
     */
    public static final String REGEX_DOUBLE = "[+-]?\\d*\\.?\\d*";
    /**
     * 系统设置字段：设置名称
     */
    public static final String SYSCONFIG_COLUMN_NAME = "paramname";
    /**
     * 系统设置字段：设置值
     */
    public static final String SYSCONFIG_COLUMN_VALUE = "paramvalue";
    /**
     * 系统设置名：版本号
     */
    public static final String SYSCONFIG_VER = "VER";
    /**
     * Fragment索引
     */
    public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";

    /**
     * 是否正常List
     *
     * @return {@code List} 是否List
     */
    public static final List<Map<String, String>> CODENAME_NORMAL_LIST() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List<CodeNameDTO> regionList = CODE_NORMAL_LIST();
        for (CodeNameDTO dto : regionList) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("code", dto.getCode());
            m.put("name", dto.getName());
            list.add(m);
        }
        return list;
    }

    /**
     * 是否正常List
     *
     * @return {@code List} 是否List
     */
    public static final List<CodeNameDTO> CODE_NORMAL_LIST() {
        List<CodeNameDTO> list = new ArrayList<CodeNameDTO>();
        list.add(new CodeNameDTO("1", "正常"));
        list.add(new CodeNameDTO("0", "异常"));
        return list;
    }

    /**
     * 是否正常Map
     *
     * @return {@code Map} 是否Map
     */
    public static final Map<String, String> CODE_NORMAL_MAP() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "正常");
        map.put("0", "异常");
        return map;
    }

    /**
     * GPS 经纬度中的原点
     */
    public static final String GPS_0 = "0";

    /**
     * 定位方式 Map
     *
     * @return {@code Map} 定位方式 Map
     */
    public static final Map<String, String> LOC_TYPE_MAP() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("", "无法定位");
        map.put("BOTH", "GPS + 网络");
        map.put(LocationManager.GPS_PROVIDER, "GPS 定位");
        map.put(LocationManager.NETWORK_PROVIDER, "网络定位");
        return map;
    }

    /**
     * 字母表List
     *
     * @return {@code List} 字母表List
     */
    public static final List<String> CHAR_LIST() {
        List<String> list = new ArrayList<String>();
        for (char c = 'A', len = c; c < (len + 26); c++) {
            list.add(Character.valueOf(c).toString());
        }
        return list;
    }

    /**
     * 自定义MediaType
     */
    public static final MediaType MEDIA_TYPE_BIN = MediaType.parse("application/octet-stream");
    // 项目自定义参数区。结束============== ======================================

    /**
     * 搜索种类：不限
     */
    public static final int SEARCH_TYPE_ALL = -1;

    /**
     * RequestCode:打开卡片
     */
    public static final int REQUESTCODE_OPENCARD = 10;
    /**
     * RequestCode:拍照
     */
    public static final int REQUESTCODE_CAMERA = 11;
    /**
     * RequestCode:拍照修改
     */
    public static final int REQUESTCODE_CAMERA_CROP = 12;
    /**
     * RequestCode:摄像
     */
    public static final int REQUESTCODE_VIDEO = 13;
    /**
     * RequestCode:登录
     */
    public static final int REQUESTCODE_LOGIN = 14;
    /**
     * RequestCode:我
     */
    public static final int REQUESTCODE_ME = 15;
    /**
     * RequestCode:复位数据
     */
    public static final int REQUESTCODE_RESETDB1 = 16;
    /**
     * RequestCode:修改密码
     */
    public static final int REQUESTCODE_CHANGEPWD = 17;
    /**
     * RequestCode:蓝牙
     */
    public static final int REQUESTCODE_ENABLE_BT = 18;
    /**
     * RequestCode:设置
     */
    public static final int REQUESTCODE_CONFIG = 19;
    /**
     * RequestCode:新建记录
     */
    public static final int REQUESTCODE_NEW_REC = 20;
    /**
     * RequestCode:编辑记录
     */
    public static final int REQUESTCODE_EDIT_REC = 21;
    /**
     * RequestCode:信息
     */
    public static final int REQUESTCODE_INFO = 22;
    /**
     * RequestCode:主页
     */
    public static final int REQUESTCODE_MAIN = 23;
    /**
     * RequestCode:设备识别
     */
    public static final int REQUESTCODE_INDICATE = 24;
    /**
     * RequestCode:设备检索
     */
    public static final int REQUESTCODE_SEARCH = 25;
    /**
     * RequestCode:绑定卡片
     */
    public static final int REQUESTCODE_BIND = 26;
    /**
     * RequestCode:设备巡检
     */
    public static final int REQUESTCODE_INS = 27;
    /**
     * RequestCode:信息详情
     */
    public static final int REQUESTCODE_DETAIL = 28;
    /**
     * RequestCode:信息列表
     */
    public static final int REQUESTCODE_LIST = 29;
    /**
     * RequestCode:获得权限
     */
    public static final int REQUESTCODE_PERMISSION = 30;
    /**
     * RequestCode:动态查询
     */
    public static final int REQUESTCODE_REMOTE_DATA = 31;
    /**
     * RequestCode:选择结果
     */
    public static final int REQUESTCODE_CHOOSE_DATA = 32;
    /**
     * RequestCode:Doodle图片编辑
     */
    public static final int REQUESTCODE_DOODLE = 33;
    /**
     * RequestCode:缺陷
     */
    public static final int REQUESTCODE_INS_E = 34;
    /**
     * RequestCode:消缺
     */
    public static final int REQUESTCODE_INS_DE = 35;
    /**
     * RequestCode:缺陷说明
     */
    public static final int REQUESTCODE_INS_DESC = 36;
    /**
     * RequestCode:选择常见缺陷
     */
    public static final int REQUESTCODE_CHOOSE_COMMON_E = 37;
    /**
     * RequestCode:权限:ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
     */
    public static final int REQUESTCODE_PERMISSION_ALL_FILES = 38;
    /**
     * ResultCode:打开卡片
     */
    public static final int RESULTCODE_OPENCARD = 10;
    /**
     * ResultCode:刷新记录列表
     */
    public static final int RESULTCODE_REFRESH_REC_LIST = 11;
    /**
     * ResultCode:注销
     */
    public static final int RESULTCODE_LOGOUT = 12;
    /**
     * ResultCode:登录
     */
    public static final int RESULTCODE_LOGIN = 13;
    /**
     * ResultCode:退出
     */
    public static final int RESULTCODE_EXIT = 14;
    /**
     * ResultCode:复位数据
     */
    public static final int RESULTCODE_RESETDB = 15;
    /**
     * ResultCode:修改密码
     */
    public static final int RESULTCODE_CHANGEPWD = 16;
    /**
     * ResultCode:设置
     */
    public static final int RESULTCODE_CONFIG = 17;
    /**
     * ResultCode:新建记录
     */
    public static final int RESULTCODE_NEW_REC = 18;
    /**
     * ResultCode:编辑记录
     */
    public static final int RESULTCODE_EDIT_REC = 19;
    /**
     * ResultCode:我
     */
    public static final int RESULTCODE_ME = 20;
    /**
     * ResultCode:信息
     */
    public static final int RESULTCODE_INFO = 21;
    /**
     * ResultCode:主页
     */
    public static final int RESULTCODE_MAIN = 22;
    /**
     * ResultCode:需要激活用户
     */
    public static final int RESULTCODE_NEED_ACTIVE_USER = 23;
    /**
     * ResultCode:保存信息后退出
     */
    public static final int RESULTCODE_INFO_EXIT = 24;
    /**
     * ResultCode:动态查询
     */
    public static final int RESULTCODE_REMOTE_DATA = 25;
    /**
     * ResultCode:选择结果
     */
    public static final int RESULTCODE_CHOOSE_DATA = 26;
    /**
     * ResultCode:缺陷
     */
    public static final int RESULTCODE_INS_E = 27;
    /**
     * ResultCode:消缺
     */
    public static final int RESULTCODE_INS_DE = 28;
    /**
     * ResultCode:缺陷说明
     */
    public static final int RESULTCODE_INS_DESC = 29;
    /**
     * ResultCode:选择常见缺陷
     */
    public static final int RESULTCODE_CHOOSE_COMMON_E = 30;
    /**
     * 每页的记录显示条数。
     */
    public static final int PERPAGE = 18;
    /**
     * 页面访问方式：完全
     */
    public static final String ACCESSMODE_FULL = "full";
    /**
     * 页面访问方式：只读
     */
    public static final String ACCESSMODE_READONLY = "readonly";
    /**
     * 页面访问方式：默认
     */
    public static final String ACCESSMODE_DEFAULT = "readonly";
    /**
     * 对文件进行操作：出错
     */
    public static final String PROCESSFILE_ERROR = "0";
    /**
     * 对文件进行操作：成功
     */
    public static final String PROCESSFILE_SUCCESS = "1";
    /**
     * 对文件进行操作：失败，文件已经存在
     */
    public static final String PROCESSFILE_FILEEXISTS = "2";

    /**
     * 性别Map
     *
     * @return {@code Map} 性别Map
     */
    public static final Map<String, String> CODE_GENDER_MAP() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("男", "男");
        map.put("女", "女");
        return map;
    }

    /**
     * 性别List
     *
     * @return {@code List} 性别List
     */
    public static final List<Map<String, String>> CODENAME_GENDER_LIST() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List<CodeNameDTO> cList = CODE_GENDER_LIST();
        Map<String, String> _m = new HashMap<String, String>();
        _m.put("code", "");
        _m.put("name", "全部");
        list.add(_m);
        for (CodeNameDTO dto : cList) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("code", dto.getCode());
            m.put("name", dto.getName());
            list.add(m);
        }
        return list;
    }

    /**
     * 性别List
     *
     * @return {@code List} 性别List
     */
    public static final List<CodeNameDTO> CODE_GENDER_LIST() {
        List<CodeNameDTO> list = new ArrayList<CodeNameDTO>();
        list.add(new CodeNameDTO("男", "男"));
        list.add(new CodeNameDTO("女", "女"));
        return list;
    }

    /**
     * 允许Map
     *
     * @return {@code Map} 允许Map
     */
    public static final Map<String, String> CODE_ALLOW_MAP() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("0", "不允许");
        map.put("1", "允许");
        return map;
    }

    /**
     * 允许List
     *
     * @return {@code List} 允许List
     */
    public static final List<CodeNameDTO> CODE_ALLOW_LIST() {
        List<CodeNameDTO> list = new ArrayList<CodeNameDTO>();
        list.add(new CodeNameDTO("0", "不允许"));
        list.add(new CodeNameDTO("1", "允许"));
        return list;
    }

    /**
     * 锁定Map return {@code Map} 锁定Map
     */
    public static final Map<String, String> CODE_LOCK_MAP() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("0", "不锁定");
        map.put("1", "锁定");
        return map;
    }

    /**
     * 锁定List
     *
     * @return {@code List} 锁定List
     */
    public static final List<CodeNameDTO> CODE_LOCK_LIST() {
        List<CodeNameDTO> list = new ArrayList<CodeNameDTO>();
        list.add(new CodeNameDTO("0", "不锁定"));
        list.add(new CodeNameDTO("1", "锁定"));
        return list;
    }

    // 是否常量开始====================================================
    public static final String SF_否 = "0";
    public static final String SF_是 = "1";

    // 是否常量结束====================================================

    /**
     * 是否类型List
     *
     * @return {@code List} 是否List
     */
    public static final List<Map<String, String>> CODENAME_SF_LIST() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List<CodeNameDTO> regionList = CODE_SF_LIST();
        for (CodeNameDTO dto : regionList) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("code", dto.getCode());
            m.put("name", dto.getName());
            list.add(m);
        }
        return list;
    }

    /**
     * 是否类型List
     *
     * @return {@code List} 是否类型List
     */
    public static final List<CodeNameDTO> CODE_SF_LIST() {
        List<CodeNameDTO> list = new ArrayList<CodeNameDTO>();
        list.add(new CodeNameDTO("0", "否"));
        list.add(new CodeNameDTO("1", "是"));
        return list;
    }

    /**
     * 是否类型Map
     *
     * @return {@code Map} 是否类型Map
     */
    public static final Map<String, String> CODE_SF_MAP() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("0", "否");
        map.put("1", "是");
        return map;
    }

    // 自定义常量。开始=====================================================

    /**
     * 领用类别List
     *
     * @return {@code List} 领用类别List
     */
    public static final List<Map<String, String>> CODENAME_REQTYPE_LIST() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List<CodeNameDTO> regionList = CODE_REQTYPE_LIST();
        for (CodeNameDTO dto : regionList) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("code", dto.getCode());
            m.put("name", dto.getName());
            list.add(m);
        }
        return list;
    }

    /**
     * 领用类别List
     *
     * @return {@code List} 领用类别List
     */
    public static final List<CodeNameDTO> CODE_REQTYPE_LIST() {
        List<CodeNameDTO> list = new ArrayList<CodeNameDTO>();
        list.add(new CodeNameDTO("0", "领出"));
        list.add(new CodeNameDTO("1", "领入"));
        return list;
    }

    /**
     * 列表数据类型：key-value
     */
    public static final int LIST_DATA_TYPE_MAP = 0;
    /**
     * 列表数据类型：array
     */
    public static final int LIST_DATA_TYPE_ARRAY = 1;
    /**
     * 列表数据类型：string
     */
    public static final int LIST_DATA_TYPE_STRING_ARRAY = 2;
    // 自定义常量。结束=====================================================

    // 发布渠道常量。开始====================================================
    /**
     * 发布渠道：测试
     */
    public static final String PUBLISH_CHANNEL_0 = "test";
    /**
     * 发布渠道：官网
     */
    public static final String PUBLISH_CHANNEL_1 = "default";
    /**
     * 发布渠道：安智
     */
    public static final String PUBLISH_CHANNEL_2 = "anzhi";
    /**
     * 发布渠道：豌豆荚
     */
    public static final String PUBLISH_CHANNEL_3 = "wandoujia";
    /**
     * 发布渠道：百度
     */
    public static final String PUBLISH_CHANNEL_4 = "baidu";
    /**
     * 发布渠道：360
     */
    public static final String PUBLISH_CHANNEL_5 = "a360";
    /**
     * 发布渠道：魅族
     */
    public static final String PUBLISH_CHANNEL_6 = "meizu";
    /**
     * 发布渠道：机锋
     */
    public static final String PUBLISH_CHANNEL_7 = "gfan";
    /**
     * 发布渠道：应用宝
     */
    public static final String PUBLISH_CHANNEL_8 = "tencent";
    /**
     * 发布渠道：小米
     */
    public static final String PUBLISH_CHANNEL_9 = "xiaomi";
    /**
     * 发布渠道：华为
     */
    public static final String PUBLISH_CHANNEL_10 = "huawei";

    // 发布渠道常量。结束====================================================

    // 应用市场包名常量。开始====================================================
    /**
     * 应用市场：腾讯应用宝
     */
    public static final String MARKET_PACKAGE_TENCENT = "com.tencent.android.qqdownloader";
    /**
     * 应用市场：华为应用市场
     */
    public static final String MARKET_PACKAGE_HUAWEI = "com.huawei.appmarket";
    // 应用市场包名常量。结束====================================================

    /**
     * 推送消息类
     */
    public static class PUSH {
        /**
         * 消息类型：百度推送
         */
        public static final int MESSAGE_TYPE_BAIDUPUSH = 1;
        /**
         * 消息类型：普通短信
         */
        public static final int MESSAGE_TYPE_SMS = 2;
        /**
         * 消息类型：微信模板消息
         */
        public static final int MESSAGE_TYPE_WECHAT_TEMPLATE_INFO = 3;
        /**
         * 消息推送类型：单个人
         */
        public static final int PUSH_TYPE_UNICAST = 1;
        /**
         * 消息推送类型：一群人
         */
        public static final int PUSH_TYPE_TAG = 2;
        /**
         * 消息推送类型：所有人
         */
        public static final int PUSH_TYPE_BROADCAST = 3;
        /**
         * 接收者类型：用户
         * <p>
         * 来自userinfo表
         */
        public static final int USER_TYPE_USER = 1;
        /**
         * 接收者类型：客户
         * <p>
         * 来自student表
         */
        public static final int USER_TYPE_STUDENT = 2;
        /**
         * 消息接收设备类型：手机
         */
        public static final int DEVICE_TYPE_MOBILE = 0;
        /**
         * 消息接收设备类型：浏览器设备
         */
        public static final int DEVICE_TYPE_BROWSER = 1;
        /**
         * 消息接收设备类型：PC设备
         */
        public static final int DEVICE_TYPE_PC = 2;
        /**
         * 消息接收设备类型：Android设备
         */
        public static final int DEVICE_TYPE_ANDROID = 3;
        /**
         * 消息接收设备类型：iOS设备
         */
        public static final int DEVICE_TYPE_IOS = 4;
        /**
         * 消息接收设备类型：Windows Phone设备
         */
        public static final int DEVICE_TYPE_WINDOWSPHONE = 5;
        /**
         * 推送消息类型：消息
         */
        public static final int PUSH_MESSAGE_TYPE_MESSAGE = 0;

        /**
         * 推送消息类型：通知
         */
        public static final int PUSH_MESSAGE_TYPE_NOTIFICATION = 1;
    }

    /**
     * 自定义表格样式类
     */
    public static class LIST_STYLE {
        /**
         * 蓝色01
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_01() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_01);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_01);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_blue_01);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_blue_01);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_01);
            map.put("list_color_split_row", R.color.list_color_split_row_blue_01);
            map.put("list_color_split_column", R.color.list_color_split_column_blue_01);
            return map;
        }

        /**
         * 蓝色02
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_02() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_02);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_02);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_blue_02);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_blue_02);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_02);
            map.put("list_color_split_row", R.color.list_color_split_row_blue_02);
            map.put("list_color_split_column", R.color.list_color_split_column_blue_02);
            return map;
        }

        /**
         * 蓝色03
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_03() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_01);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_01);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_01);
            map.put("list_color_split_row", R.color.list_color_header_background_blue_01);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }

        /**
         * 蓝色04
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_04() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_02);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_02);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_02);
            map.put("list_color_split_row", R.color.list_color_header_background_blue_02);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }

        /**
         * 蓝色05
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_05() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_01);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_01);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_01);
            map.put("list_color_split_row", R.color.background_bubble_dark);
            map.put("list_color_split_column", R.color.background_bubble_dark);
            return map;
        }

        /**
         * 蓝色11
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_11() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_11);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_11);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_blue_11);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_blue_11);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_11);
            map.put("list_color_split_row", R.color.list_color_split_row_blue_11);
            map.put("list_color_split_column", R.color.list_color_split_column_blue_11);
            return map;
        }

        /**
         * 蓝色12
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> BLUE_12() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_blue_12);
            map.put("list_color_header_font", R.color.list_color_header_font_blue_12);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_blue_12);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_blue_12);
            map.put("list_color_content_font", R.color.list_color_content_font_blue_12);
            map.put("list_color_split_row", R.color.list_color_split_row_blue_12);
            map.put("list_color_split_column", R.color.list_color_split_column_blue_12);
            return map;
        }

        /**
         * 橙色01
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> ORANGE_01() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_orange_01);
            map.put("list_color_header_font", R.color.list_color_header_font_orange_01);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_orange_01);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_orange_01);
            map.put("list_color_content_font", R.color.list_color_content_font_orange_01);
            map.put("list_color_split_row", R.color.list_color_split_row_orange_01);
            map.put("list_color_split_column", R.color.list_color_split_column_orange_01);
            return map;
        }

        /**
         * 橙色02
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> ORANGE_02() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_orange_02);
            map.put("list_color_header_font", R.color.list_color_header_font_orange_02);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_orange_02);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_orange_02);
            map.put("list_color_content_font", R.color.list_color_content_font_orange_02);
            map.put("list_color_split_row", R.color.list_color_split_row_orange_02);
            map.put("list_color_split_column", R.color.list_color_split_column_orange_02);
            return map;
        }

        /**
         * 橙色03
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> ORANGE_03() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_orange_01);
            map.put("list_color_header_font", R.color.list_color_header_font_orange_01);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_orange_01);
            map.put("list_color_split_row", R.color.list_color_header_background_orange_01);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }

        /**
         * 橙色04
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> ORANGE_04() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_orange_02);
            map.put("list_color_header_font", R.color.list_color_header_font_orange_02);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_orange_02);
            map.put("list_color_split_row", R.color.list_color_header_background_orange_02);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }

        /**
         * 橙色11
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> ORANGE_11() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_orange_11);
            map.put("list_color_header_font", R.color.list_color_header_font_orange_11);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_orange_11);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_orange_11);
            map.put("list_color_content_font", R.color.list_color_content_font_orange_11);
            map.put("list_color_split_row", R.color.list_color_split_row_orange_11);
            map.put("list_color_split_column", R.color.list_color_split_column_orange_11);
            return map;
        }

        /**
         * 橙色12
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> ORANGE_12() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_orange_12);
            map.put("list_color_header_font", R.color.list_color_header_font_orange_12);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_orange_12);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_orange_12);
            map.put("list_color_content_font", R.color.list_color_content_font_orange_12);
            map.put("list_color_split_row", R.color.list_color_split_row_orange_12);
            map.put("list_color_split_column", R.color.list_color_split_column_orange_12);
            return map;
        }

        /**
         * 绿色01
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> GREEN_01() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_green_01);
            map.put("list_color_header_font", R.color.list_color_header_font_green_01);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_green_01);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_green_01);
            map.put("list_color_content_font", R.color.list_color_content_font_green_01);
            map.put("list_color_split_row", R.color.list_color_split_row_green_01);
            map.put("list_color_split_column", R.color.list_color_split_column_green_01);
            return map;
        }

        /**
         * 绿色03
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> GREEN_03() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_green_01);
            map.put("list_color_header_font", R.color.list_color_header_font_green_01);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_green_01);
            map.put("list_color_split_row", R.color.list_color_header_background_green_01);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }

        /**
         * 绿色11
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> GREEN_11() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_green_11);
            map.put("list_color_header_font", R.color.list_color_header_font_green_11);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_green_11);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_green_11);
            map.put("list_color_content_font", R.color.list_color_content_font_green_11);
            map.put("list_color_split_row", R.color.list_color_split_row_green_11);
            map.put("list_color_split_column", R.color.list_color_split_column_green_11);
            return map;
        }

        /**
         * 灰色01
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> GREY_01() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_grey_01);
            map.put("list_color_header_font", R.color.list_color_header_font_grey_01);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_grey_01);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_grey_01);
            map.put("list_color_content_font", R.color.list_color_content_font_grey_01);
            map.put("list_color_split_row", R.color.list_color_split_row_grey_01);
            map.put("list_color_split_column", R.color.list_color_split_column_grey_01);
            return map;
        }

        /**
         * 灰色03
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> GREY_03() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_grey_01);
            map.put("list_color_header_font", R.color.list_color_header_font_grey_01);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_grey_01);
            map.put("list_color_split_row", R.color.list_color_header_background_grey_01);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }

        /**
         * 灰色11
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> GREY_11() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_grey_11);
            map.put("list_color_header_font", R.color.list_color_header_font_grey_11);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_grey_11);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_grey_11);
            map.put("list_color_content_font", R.color.list_color_content_font_grey_11);
            map.put("list_color_split_row", R.color.list_color_split_row_grey_11);
            map.put("list_color_split_column", R.color.list_color_split_column_grey_11);
            return map;
        }

        /**
         * 紫色01
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> PURPLE_01() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_purple_01);
            map.put("list_color_header_font", R.color.list_color_header_font_purple_01);
            map.put("list_color_content_background_odd", R.color.list_color_content_background_odd_purple_01);
            map.put("list_color_content_background_even", R.color.list_color_content_background_even_purple_01);
            map.put("list_color_content_font", R.color.list_color_content_font_purple_01);
            map.put("list_color_split_row", R.color.list_color_split_row_purple_01);
            map.put("list_color_split_column", R.color.list_color_split_column_purple_01);
            return map;
        }

        /**
         * 紫色03
         *
         * @return {@code Map} 颜色定义
         */
        public static final Map<String, Integer> PURPLE_03() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("list_color_header_background", R.color.list_color_header_background_purple_01);
            map.put("list_color_header_font", R.color.list_color_header_font_purple_01);
            map.put("list_color_content_background_odd", R.color.solid_plain);
            map.put("list_color_content_background_even", R.color.solid_plain);
            map.put("list_color_content_font", R.color.list_color_content_font_purple_01);
            map.put("list_color_split_row", R.color.list_color_header_background_purple_01);
            map.put("list_color_split_column", R.color.transparent);
            return map;
        }
    }
}
