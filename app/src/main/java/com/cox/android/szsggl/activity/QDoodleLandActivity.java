package com.cox.android.szsggl.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;
import com.cox.utils.FileUtil;
import com.cox.utils.StatusBarUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.Util;
import cn.hzw.doodle.DoodleBitmap;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodleOnTouchGestureListener;
import cn.hzw.doodle.DoodleParams;
import cn.hzw.doodle.DoodlePath;
import cn.hzw.doodle.DoodlePen;
import cn.hzw.doodle.DoodleShape;
import cn.hzw.doodle.DoodleText;
import cn.hzw.doodle.DoodleTouchDetector;
import cn.hzw.doodle.DoodleView;
import cn.hzw.doodle.IDoodleListener;
import cn.hzw.doodle.core.IDoodle;
import cn.hzw.doodle.core.IDoodleColor;
import cn.hzw.doodle.core.IDoodleItemListener;
import cn.hzw.doodle.core.IDoodlePen;
import cn.hzw.doodle.core.IDoodleSelectableItem;
import cn.hzw.doodle.core.IDoodleShape;
import cn.hzw.doodle.core.IDoodleTouchDetector;
import cn.hzw.doodle.dialog.ColorPickerDialog;
import cn.hzw.doodle.dialog.DialogController;
import cn.hzw.doodle.imagepicker.ImageSelectorView;

/**
 * 涂鸦界面，根据DoodleView的接口，提供页面交互
 * （这边代码和ui比较粗糙，主要目的是告诉大家DoodleView的接口具体能实现什么功能，实际需求中的ui和交互需另提别论）
 * Created by huangziwei(154330138@qq.com) on 2016/9/3.
 */
public class QDoodleLandActivity extends DbActivity {
    /**
     * 当前类对象
     * */
    DbActivity classThis;

    public static final String TAG = "QDoodle";
    public final static int DEFAULT_MOSAIC_SIZE = 20; // 默认马赛克大小
    public final static int DEFAULT_COPY_SIZE = 20; // 默认仿制大小
    public final static int DEFAULT_TEXT_SIZE = 18; // 默认文字大小
    public final static int DEFAULT_BITMAP_SIZE = 80; // 默认贴图大小

    public static final int RESULT_ERROR = -111; // 出现错误

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param params      涂鸦参数
     * @param requestCode startActivityForResult的请求码
     * @see DoodleParams
     */
    public static void startActivityForResult(Activity activity, DoodleParams params, int requestCode) {
        Intent intent = new Intent(activity, QDoodleLandActivity.class);
        intent.putExtra(QDoodleLandActivity.KEY_PARAMS, params);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param params      涂鸦参数
     * @param data        附加信息
     * @param requestCode startActivityForResult的请求码
     * @see DoodleParams
     */
    public static void startActivityForResult(Activity activity, DoodleParams params, Bundle data, int requestCode) {
        Intent intent = new Intent(activity, QDoodleLandActivity.class);
        intent.putExtra(QDoodleLandActivity.KEY_PARAMS, params);
        intent.putExtras(data);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param imagePath   　图片路径
     * @param savePath    　保存路径
     * @param isDir       　保存路径是否为目录
     * @param requestCode 　startActivityForResult的请求码
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, String savePath, boolean isDir, int requestCode) {
        DoodleParams params = new DoodleParams();
        params.mImagePath = imagePath;
        params.mSavePath = savePath;
        params.mSavePathIsDir = isDir;
        startActivityForResult(activity, params, requestCode);
    }

    /**
     * {@link QDoodleLandActivity#startActivityForResult(Activity, String, String, boolean, int)}
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, int requestCode) {
        DoodleParams params = new DoodleParams();
        params.mImagePath = imagePath;
        startActivityForResult(activity, params, requestCode);
    }

    public static final String KEY_PARAMS = "key_doodle_params";
    public static final String KEY_IMAGE_PATH = "key_image_path";

    private String mImagePath;

    private FrameLayout mFrameLayout;
    private IDoodle mDoodle;
    private DoodleView mDoodleView;

    private TextView mPaintSizeView;

    private View mBtnHidePanel, mSettingsPanel;
    private View mSelectedEditContainer;
    private TextView mItemScaleTextView;
    private View mBtnColor, mColorContainer;
    private SeekBar mEditSizeSeekBar;
    private View mShapeContainer, mPenContainer, mSizeContainer;
    private View mBtnUndo;
    private View mMosaicMenu;
    private View mEditBtn;

    private AlphaAnimation mViewShowAnimation, mViewHideAnimation; // view隐藏和显示时用到的渐变动画

    private DoodleParams mDoodleParams;

    // 触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mHideDelayRunnable;
    // 触摸屏幕超过一定时间才判断为需要显示设置面板
    private Runnable mShowDelayRunnable;

    private DoodleOnTouchGestureListener mTouchGestureListener;
    private Map<IDoodlePen, Float> mPenSizeMap = new HashMap<>(); //保存每个画笔对应的最新大小

    private int mMosaicLevel = -1;

    /**
     * 保存图片Dialog
     */
    private AlertDialog savePhotoDlg;
    /**
     * 信息
     */
    private HashMap<String, Object> infoObj;
    /**
     * 新文件名
     */
    private String aliasName;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PARAMS, mDoodleParams);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        mDoodleParams = savedInstanceState.getParcelable(KEY_PARAMS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classThis = QDoodleLandActivity.this;

        // 获取Intent
        Intent intent = getIntent();
        // 获取Intent上携带的数据
        Bundle data = intent.getExtras();
        infoObj = (HashMap<String, Object>) data.getSerializable("info");
        aliasName = data.getString("alias");

        if (mDoodleParams == null) {
            mDoodleParams = getIntent().getExtras().getParcelable(KEY_PARAMS);
        }
        if (mDoodleParams == null) {
            LogUtil.e("TAG", "mDoodleParams is null!");
            goBack();
            return;
        }

        mImagePath = mDoodleParams.mImagePath;
        if (mImagePath == null) {
            LogUtil.e("TAG", "mImagePath is null!");
            goBack();
            return;
        }

        LogUtil.d("TAG", mImagePath);
        if (mDoodleParams.mIsFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        Bitmap bitmap = ImageUtils.createBitmapFromPath(mImagePath, this);
        if (bitmap == null) {
            LogUtil.e("TAG", "bitmap is null!");
            goBack();
            return;
        }

        setContentView(R.layout.q_doodle_land_layout);

        // 获得ActionBar
        actionBar = getSupportActionBar();
        // 隐藏ActionBar
        actionBar.hide();

        mFrameLayout = (FrameLayout) findViewById(R.id.doodle_container);

        /*
        Whether or not to optimize drawing, it is suggested to open, which can optimize the drawing speed and performance.
        Note: When item is selected for editing after opening, it will be drawn at the top level, and not at the corresponding level until editing is completed.
        是否优化绘制，建议开启，可优化绘制速度和性能.
        注意：开启后item被选中编辑时时会绘制在最上面一层，直到结束编辑后才绘制在相应层级
         */
        mDoodle = mDoodleView = new DoodleViewWrapper(this, bitmap, mDoodleParams.mOptimizeDrawing, new IDoodleListener() {
            @Override
            public void onSaved(IDoodle doodle, Bitmap bitmap, Runnable callback) {
                new SaveImageTask().execute(bitmap);
                // 保存图片为jpg格式
//                File doodleFile = null;
//                File file = null;
//                String savePath = mDoodleParams.mSavePath;
//                boolean isDir = mDoodleParams.mSavePathIsDir;
//                if (TextUtils.isEmpty(savePath)) {
//                    File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
//                    doodleFile = new File(dcimFile, "Doodle");
//                    //　保存的路径
//                    file = new File(doodleFile, System.currentTimeMillis() + ".jpg");
//                } else {
//                    if (isDir) {
//                        doodleFile = new File(savePath);
//                        //　保存的路径
//                        file = new File(doodleFile, System.currentTimeMillis() + ".jpg");
//                    } else {
//                        file = new File(savePath);
//                        doodleFile = file.getParentFile();
//                    }
//                }
//                doodleFile.mkdirs();
//
//                FileOutputStream outputStream = null;
//                boolean saveFlag = false;
//                try {
//                    outputStream = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
//                    ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());
//
//                    // 创建信息传输Bundle
//                    Bundle data = new Bundle();
//                    data.putString("v", "");
//                    data.putString(KEY_IMAGE_PATH, file.getAbsolutePath());
//
//                    // 创建启动 Activity 的 Intent
//                    Intent intent = new Intent();
//                    // 将数据存入Intent中
//                    intent.putExtras(data);
//                    // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
//                    setResult(Activity.RESULT_OK, intent);
//
////                    Toast toast = Toast.makeText(classThis, "保存成功", Toast.LENGTH_SHORT);
////                    toast.setGravity(Gravity.CENTER, 0, 0);
////                    toast.show();
//
//                    saveFlag = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    onError(DoodleView.ERROR_SAVE, e.getMessage());
//                } finally {
//                    Util.closeQuietly(outputStream);
//                    if (saveFlag) {
//                        mDoodleView.clear();
//                        goBack();
//                    }
//                }
            }

            public void onError(int i, String msg) {
                setResult(RESULT_ERROR);
                goBack();
            }

            @Override
            public void onReady(IDoodle doodle) {
                mEditSizeSeekBar.setMax(100);
                //mEditSizeSeekBar.setMax(Math.min(mDoodleView.getWidth(), mDoodleView.getHeight()));

                float size = mDoodleParams.mPaintUnitSize > 0 ? mDoodleParams.mPaintUnitSize * mDoodle.getUnitSize() : 0;
                if (size <= 0) {
                    size = mDoodleParams.mPaintPixelSize > 0 ? mDoodleParams.mPaintPixelSize : mDoodle.getSize();
                }
                if (size >= 100) {
                    size = 100;
                }
                // 设置初始值
                mDoodle.setSize(size);
                // 选择画笔
                mDoodle.setPen(DoodlePen.BRUSH);
                mDoodle.setShape(DoodleShape.HAND_WRITE);
                mDoodle.setColor(new DoodleColor(mDoodleParams.mPaintColor));
                if (mDoodleParams.mZoomerScale <= 0) {
                    findViewById(R.id.btn_zoomer).setVisibility(View.GONE);
                }
                mDoodle.setZoomerScale(mDoodleParams.mZoomerScale);
                mTouchGestureListener.setSupportScaleItem(mDoodleParams.mSupportScaleItem);

                // 每个画笔的初始值
                mPenSizeMap.put(DoodlePen.BRUSH, mDoodle.getSize());
                mPenSizeMap.put(DoodlePen.MOSAIC, DEFAULT_MOSAIC_SIZE * mDoodle.getUnitSize());
                mPenSizeMap.put(DoodlePen.COPY, DEFAULT_COPY_SIZE * mDoodle.getUnitSize());
                mPenSizeMap.put(DoodlePen.ERASER, mDoodle.getSize());
                mPenSizeMap.put(DoodlePen.TEXT, DEFAULT_TEXT_SIZE * mDoodle.getUnitSize());
                mPenSizeMap.put(DoodlePen.BITMAP, DEFAULT_BITMAP_SIZE * mDoodle.getUnitSize());
            }
        }, null);

        mTouchGestureListener = new DoodleOnTouchGestureListener(mDoodleView, new DoodleOnTouchGestureListener.ISelectionListener() {
            // save states before being selected
            IDoodlePen mLastPen = null;
            IDoodleColor mLastColor = null;
            Float mSize = null;

            IDoodleItemListener mIDoodleItemListener = new IDoodleItemListener() {
                @Override
                public void onPropertyChanged(int property) {
                    if (mTouchGestureListener.getSelectedItem() == null) {
                        return;
                    }
                    if (property == IDoodleItemListener.PROPERTY_SCALE) {
                        mItemScaleTextView.setText(
                                (int) (mTouchGestureListener.getSelectedItem().getScale() * 100 + 0.5f) + "%");
                    }
                }
            };

            @Override
            public void onSelectedItem(IDoodle doodle, IDoodleSelectableItem selectableItem, boolean selected) {
                if (selected) {
                    if (mLastPen == null) {
                        mLastPen = mDoodle.getPen();
                    }
                    if (mLastColor == null) {
                        mLastColor = mDoodle.getColor();
                    }
                    if (mSize == null) {
                        mSize = mDoodle.getSize();
                    }
                    mDoodleView.setEditMode(true);
                    mDoodle.setPen(selectableItem.getPen());
                    mDoodle.setColor(selectableItem.getColor());
                    mDoodle.setSize(selectableItem.getSize());
                    mEditSizeSeekBar.setProgress((int) selectableItem.getSize());
                    mSelectedEditContainer.setVisibility(View.VISIBLE);
                    mSizeContainer.setVisibility(View.VISIBLE);
                    mItemScaleTextView.setText((int) (selectableItem.getScale() * 100 + 0.5f) + "%");
                    selectableItem.addItemListener(mIDoodleItemListener);
                } else {
                    selectableItem.removeItemListener(mIDoodleItemListener);

                    if (mTouchGestureListener.getSelectedItem() == null) { // nothing is selected. 当前没有选中任何一个item
                        if (mLastPen != null) {
                            mDoodle.setPen(mLastPen);
                            mLastPen = null;
                        }
                        if (mLastColor != null) {
                            mDoodle.setColor(mLastColor);
                            mLastColor = null;
                        }
                        if (mSize != null) {
                            mDoodle.setSize(mSize);
                            mSize = null;
                        }
                        mSelectedEditContainer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCreateSelectableItem(IDoodle doodle, float x, float y) {
                if (mDoodle.getPen() == DoodlePen.TEXT) {
                    createDoodleText(null, x, y);
                } else if (mDoodle.getPen() == DoodlePen.BITMAP) {
                    createDoodleBitmap(null, x, y);
                }
            }
        }) {
            @Override
            public void setSupportScaleItem(boolean supportScaleItem) {
                super.setSupportScaleItem(supportScaleItem);
                if (supportScaleItem) {
                    mItemScaleTextView.setVisibility(View.VISIBLE);
                } else {
                    mItemScaleTextView.setVisibility(View.GONE);
                }
            }
        };

        IDoodleTouchDetector detector = new DoodleTouchDetector(getApplicationContext(), mTouchGestureListener);
        mDoodleView.setDefaultTouchDetector(detector);

        mDoodle.setIsDrawableOutside(mDoodleParams.mIsDrawableOutside);
        mFrameLayout.addView(mDoodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDoodle.setDoodleMinScale(mDoodleParams.mMinScale);
        mDoodle.setDoodleMaxScale(mDoodleParams.mMaxScale);

        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarUtil.setStatusBarMode(this, false, R.color.window_background);
    }

    private boolean canChangeColor(IDoodlePen pen) {
        return pen != DoodlePen.ERASER
                && pen != DoodlePen.BITMAP
                && pen != DoodlePen.COPY
                && pen != DoodlePen.MOSAIC;
    }

    // 添加文字
    private void createDoodleText(final DoodleText doodleText, final float x, final float y) {
        if (isFinishing()) {
            return;
        }

        DialogController.showInputTextDialog(this, doodleText == null ? null : doodleText.getText(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = (v.getTag() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (doodleText == null) {
                    IDoodleSelectableItem item = new DoodleText(mDoodle, text, mDoodle.getSize(), mDoodle.getColor().copy(), x, y);
                    mDoodle.addItem(item);
                    mTouchGestureListener.setSelectedItem(item);
                } else {
                    doodleText.setText(text);
                }
                mDoodle.refresh();
            }
        }, null);
        if (doodleText == null) {
            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
        }
    }

    // 添加贴图
    private void createDoodleBitmap(final DoodleBitmap doodleBitmap, final float x, final float y) {
        DialogController.showSelectImageDialog(this, new ImageSelectorView.ImageSelectorListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onEnter(List<String> pathList) {
                Bitmap bitmap = ImageUtils.createBitmapFromPath(pathList.get(0), mDoodleView.getWidth() / 4, mDoodleView.getHeight() / 4);

                if (doodleBitmap == null) {
                    IDoodleSelectableItem item = new DoodleBitmap(mDoodle, bitmap, mDoodle.getSize(), x, y);
                    mDoodle.addItem(item);
                    mTouchGestureListener.setSelectedItem(item);
                } else {
                    doodleBitmap.setBitmap(bitmap);
                }
                mDoodle.refresh();
            }
        });
    }

    //++++++++++++++++++以下为一些初始化操作和点击监听+++++++++++++++++++++++++++++++++++++++++

    //
    private void initView() {
        mBtnUndo = findViewById(R.id.btn_undo);
        mBtnUndo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!(DoodleParams.getDialogInterceptor() != null
                        && DoodleParams.getDialogInterceptor().onShow(classThis, mDoodle, DoodleParams.DialogType.CLEAR_ALL))) {
                    DialogController.showEnterCancelDialog(classThis,
                            getString(R.string.doodle_clear_screen), getString(R.string.doodle_cant_undo_after_clearing),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDoodle.clear();
                                }
                            }, null
                    );
                }
                return true;
            }
        });
        mSelectedEditContainer = findViewById(R.id.doodle_selectable_edit_container);
        mSelectedEditContainer.setVisibility(View.GONE);
        mItemScaleTextView = (TextView) findViewById(R.id.item_scale);
        mItemScaleTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mTouchGestureListener.getSelectedItem() != null) {
                    mTouchGestureListener.getSelectedItem().setScale(1);
                }
                return true;
            }
        });

        mSettingsPanel = findViewById(R.id.doodle_panel);

        mBtnHidePanel = findViewById(R.id.doodle_btn_hide_panel);

        mPaintSizeView = (TextView) findViewById(R.id.paint_size_text);
        mShapeContainer = findViewById(R.id.shape_container);
        mPenContainer = findViewById(R.id.pen_container);
        mSizeContainer = findViewById(R.id.size_container);
        mMosaicMenu = findViewById(R.id.mosaic_menu);
        mEditBtn = findViewById(R.id.doodle_selectable_edit);

        mBtnColor = classThis.findViewById(R.id.btn_set_color);
        mColorContainer = classThis.findViewById(R.id.btn_set_color_container);
        mEditSizeSeekBar = (SeekBar) findViewById(R.id.doodle_seekbar_size);
        mEditSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 0) {
                    mEditSizeSeekBar.setProgress(1);
                    return;
                }
                if ((int) mDoodle.getSize() == progress) {
                    return;
                }
                mDoodle.setSize(progress);
                if (mTouchGestureListener.getSelectedItem() != null) {
                    mTouchGestureListener.getSelectedItem().setSize(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mDoodleView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏设置面板
                if (!mBtnHidePanel.isSelected()  // 设置面板没有被隐藏
                        && mDoodleParams.mChangePanelVisibilityDelay > 0) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                            mSettingsPanel.removeCallbacks(mShowDelayRunnable);
                            //触摸屏幕超过一定时间才判断为需要隐藏设置面板
                            mSettingsPanel.postDelayed(mHideDelayRunnable, mDoodleParams.mChangePanelVisibilityDelay);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                            mSettingsPanel.removeCallbacks(mShowDelayRunnable);
                            //离开屏幕超过一定时间才判断为需要显示设置面板
                            mSettingsPanel.postDelayed(mShowDelayRunnable, mDoodleParams.mChangePanelVisibilityDelay);
                            break;
                    }
                }

                return false;
            }
        });

        // 长按标题栏显示原图
        findViewById(R.id.doodle_txt_title).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        mDoodle.setShowOriginal(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        mDoodle.setShowOriginal(false);
                        break;
                }
                return true;
            }
        });

        mViewShowAnimation = new AlphaAnimation(0, 1);
        mViewShowAnimation.setDuration(150);
        mViewHideAnimation = new AlphaAnimation(1, 0);
        mViewHideAnimation.setDuration(150);
        mHideDelayRunnable = new Runnable() {
            public void run() {
                hideView(mSettingsPanel);
            }

        };
        mShowDelayRunnable = new Runnable() {
            public void run() {
                showView(mSettingsPanel);
            }
        };
    }

    private ValueAnimator mRotateAnimator;

    public void onClick(final View v) {
        if (v.getId() == R.id.btn_pen_hand) {
            mDoodle.setPen(DoodlePen.BRUSH);
        } else if (v.getId() == R.id.btn_pen_mosaic) {
            mDoodle.setPen(DoodlePen.MOSAIC);
        } else if (v.getId() == R.id.btn_pen_copy) {
            mDoodle.setPen(DoodlePen.COPY);
        } else if (v.getId() == R.id.btn_pen_eraser) {
            mDoodle.setPen(DoodlePen.ERASER);
        } else if (v.getId() == R.id.btn_pen_text) {
            mDoodle.setPen(DoodlePen.TEXT);
        } else if (v.getId() == R.id.btn_pen_bitmap) {
            mDoodle.setPen(DoodlePen.BITMAP);
        } else if (v.getId() == R.id.doodle_btn_brush_edit) {
            mDoodleView.setEditMode(!mDoodleView.isEditMode());
        } else if (v.getId() == R.id.btn_undo) {
            mDoodle.undo();
        } else if (v.getId() == R.id.btn_zoomer) {
            mDoodleView.enableZoomer(!mDoodleView.isEnableZoomer());
        } else if (v.getId() == R.id.btn_set_color_container) {
            DoodleColor color = null;
            if (mDoodle.getColor() instanceof DoodleColor) {
                color = (DoodleColor) mDoodle.getColor();
            }
            if (color == null) {
                return;
            }
            if (!(DoodleParams.getDialogInterceptor() != null
                    && DoodleParams.getDialogInterceptor().onShow(classThis, mDoodle, DoodleParams.DialogType.COLOR_PICKER))) {
                boolean fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
                int themeId;
                if (fullScreen) {
                    themeId = android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
                } else {
                    themeId = android.R.style.Theme_Translucent_NoTitleBar;
                }
                new ColorPickerDialog(classThis,
                        new ColorPickerDialog.OnColorChangedListener() {
                            public void colorChanged(int color, int size) {
                                mDoodle.setColor(new DoodleColor(color));
                                mDoodle.setSize(size);
                            }

                            @Override
                            public void colorChanged(Drawable color, int size) {
                                Bitmap bitmap = ImageUtils.getBitmapFromDrawable(color);
                                mDoodle.setColor(new DoodleColor(bitmap));
                                mDoodle.setSize(size);
                            }
                        }, themeId).show(mDoodleView, mBtnColor.getBackground(), 100);
                // }, themeId).show(mDoodleView, mBtnColor.getBackground(), Math.min(mDoodleView.getWidth(), mDoodleView.getHeight()));
            }
        } else if (v.getId() == R.id.doodle_btn_hide_panel) {
            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
            mSettingsPanel.removeCallbacks(mShowDelayRunnable);
            v.setSelected(!v.isSelected());
            if (!mBtnHidePanel.isSelected()) {
                Toast.makeText(classThis, "显示工具", Toast.LENGTH_SHORT).show();
                showView(mSettingsPanel);
            } else {
                Toast.makeText(classThis, "隐藏工具", Toast.LENGTH_SHORT).show();
                hideView(mSettingsPanel);
            }
        } else if (v.getId() == R.id.doodle_btn_finish) {
            mDoodle.save();
        } else if (v.getId() == R.id.backBtn) {
            if (mDoodleView.isEditMode()) {
                mDoodleView.setEditMode(false);
            }
            if (mDoodle.getAllItem() == null || mDoodle.getItemCount() == 0) {
                goBack();
                return;
            }
            if (!(DoodleParams.getDialogInterceptor() != null
                    && DoodleParams.getDialogInterceptor().onShow(classThis, mDoodle, DoodleParams.DialogType.SAVE))) {
                makeExitDialog();
            }
        } else if (v.getId() == R.id.doodle_btn_rotate) {
            // 旋转图片
            if (mRotateAnimator == null) {
                mRotateAnimator = new ValueAnimator();
                mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        mDoodle.setDoodleRotation(value);
                    }
                });
                mRotateAnimator.setDuration(250);
            }
            if (mRotateAnimator.isRunning()) {
                return;
            }
            mRotateAnimator.setIntValues(mDoodle.getDoodleRotation(), mDoodle.getDoodleRotation() + 90);
            mRotateAnimator.start();
        } else if (v.getId() == R.id.doodle_selectable_edit) {
            if (mTouchGestureListener.getSelectedItem() instanceof DoodleText) {
                createDoodleText((DoodleText) mTouchGestureListener.getSelectedItem(), -1, -1);
            } else if (mTouchGestureListener.getSelectedItem() instanceof DoodleBitmap) {
                createDoodleBitmap((DoodleBitmap) mTouchGestureListener.getSelectedItem(), -1, -1);
            }
        } else if (v.getId() == R.id.doodle_selectable_remove) {
            mDoodle.removeItem(mTouchGestureListener.getSelectedItem());
            mTouchGestureListener.setSelectedItem(null);
        } else if (v.getId() == R.id.doodle_selectable_top) {
            mDoodle.topItem(mTouchGestureListener.getSelectedItem());
        } else if (v.getId() == R.id.doodle_selectable_bottom) {
            mDoodle.bottomItem(mTouchGestureListener.getSelectedItem());
        } else if (v.getId() == R.id.btn_hand_write) {
            mDoodle.setShape(DoodleShape.HAND_WRITE);
        } else if (v.getId() == R.id.btn_arrow) {
            mDoodle.setShape(DoodleShape.ARROW);
        } else if (v.getId() == R.id.btn_line) {
            mDoodle.setShape(DoodleShape.LINE);
        } else if (v.getId() == R.id.btn_holl_circle) {
            mDoodle.setShape(DoodleShape.HOLLOW_CIRCLE);
        } else if (v.getId() == R.id.btn_fill_circle) {
            mDoodle.setShape(DoodleShape.FILL_CIRCLE);
        } else if (v.getId() == R.id.btn_holl_rect) {
            mDoodle.setShape(DoodleShape.HOLLOW_RECT);
        } else if (v.getId() == R.id.btn_fill_rect) {
            mDoodle.setShape(DoodleShape.FILL_RECT);
        } else if (v.getId() == R.id.btn_mosaic_level1) {
            if (v.isSelected()) {
                return;
            }

            mMosaicLevel = DoodlePath.MOSAIC_LEVEL_1;
            mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, mMosaicLevel));
            v.setSelected(true);
            mMosaicMenu.findViewById(R.id.btn_mosaic_level2).setSelected(false);
            mMosaicMenu.findViewById(R.id.btn_mosaic_level3).setSelected(false);
            if (mTouchGestureListener.getSelectedItem() != null) {
                mTouchGestureListener.getSelectedItem().setColor(mDoodle.getColor().copy());
            }
        } else if (v.getId() == R.id.btn_mosaic_level2) {
            if (v.isSelected()) {
                return;
            }

            mMosaicLevel = DoodlePath.MOSAIC_LEVEL_2;
            mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, mMosaicLevel));
            v.setSelected(true);
            mMosaicMenu.findViewById(R.id.btn_mosaic_level1).setSelected(false);
            mMosaicMenu.findViewById(R.id.btn_mosaic_level3).setSelected(false);
            if (mTouchGestureListener.getSelectedItem() != null) {
                mTouchGestureListener.getSelectedItem().setColor(mDoodle.getColor().copy());
            }
        } else if (v.getId() == R.id.btn_mosaic_level3) {
            if (v.isSelected()) {
                return;
            }

            mMosaicLevel = DoodlePath.MOSAIC_LEVEL_3;
            mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, mMosaicLevel));
            v.setSelected(true);
            mMosaicMenu.findViewById(R.id.btn_mosaic_level1).setSelected(false);
            mMosaicMenu.findViewById(R.id.btn_mosaic_level2).setSelected(false);
            if (mTouchGestureListener.getSelectedItem() != null) {
                mTouchGestureListener.getSelectedItem().setColor(mDoodle.getColor().copy());
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                findViewById(R.id.backBtn).performClick();
                return true;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.clearAnimation();
        view.startAnimation(mViewShowAnimation);
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        view.clearAnimation();
        view.startAnimation(mViewHideAnimation);
        view.setVisibility(View.GONE);
    }

    /**
     * 包裹DoodleView，监听相应的设置接口，以改变UI状态
     */
    private class DoodleViewWrapper extends DoodleView {

        public DoodleViewWrapper(Context context, Bitmap bitmap, boolean optimizeDrawing, IDoodleListener listener, IDoodleTouchDetector defaultDetector) {
            super(context, bitmap, optimizeDrawing, listener, defaultDetector);
        }

        private Map<IDoodlePen, Integer> mBtnPenIds = new HashMap<>();

        {
            mBtnPenIds.put(DoodlePen.BRUSH, R.id.btn_pen_hand);
            mBtnPenIds.put(DoodlePen.MOSAIC, R.id.btn_pen_mosaic);
            mBtnPenIds.put(DoodlePen.COPY, R.id.btn_pen_copy);
            mBtnPenIds.put(DoodlePen.ERASER, R.id.btn_pen_eraser);
            mBtnPenIds.put(DoodlePen.TEXT, R.id.btn_pen_text);
            mBtnPenIds.put(DoodlePen.BITMAP, R.id.btn_pen_bitmap);
        }

        @Override
        public void setPen(IDoodlePen pen) {
            IDoodlePen oldPen = getPen();
            super.setPen(pen);

            mMosaicMenu.setVisibility(GONE);
            mEditBtn.setVisibility(View.GONE); // edit btn
            if (pen == DoodlePen.BITMAP || pen == DoodlePen.TEXT) {
                mEditBtn.setVisibility(View.VISIBLE); // edit btn
                mShapeContainer.setVisibility(GONE);
                if (pen == DoodlePen.BITMAP) {
                    mColorContainer.setVisibility(GONE);
                } else {
                    mColorContainer.setVisibility(VISIBLE);
                }
            } else if (pen == DoodlePen.MOSAIC) {
                mMosaicMenu.setVisibility(VISIBLE);
                mShapeContainer.setVisibility(VISIBLE);
                mColorContainer.setVisibility(GONE);
            } else {
                mShapeContainer.setVisibility(VISIBLE);
                if (pen == DoodlePen.COPY || pen == DoodlePen.ERASER) {
                    mColorContainer.setVisibility(GONE);
                } else {
                    mColorContainer.setVisibility(VISIBLE);
                }
            }
            setSingleSelected(mBtnPenIds.values(), mBtnPenIds.get(pen));

            if (mTouchGestureListener.getSelectedItem() == null) {
                mPenSizeMap.put(oldPen, getSize()); // save
                Float size = mPenSizeMap.get(pen); // restore
                if (size != null) {
                    mDoodle.setSize(size);
                }
                if (isEditMode()) {
                    mShapeContainer.setVisibility(GONE);
                    mColorContainer.setVisibility(GONE);
                    mMosaicMenu.setVisibility(GONE);
                }
            } else {
                mShapeContainer.setVisibility(GONE);
                return;
            }

            if (pen == DoodlePen.BRUSH) {
                Drawable colorBg = mBtnColor.getBackground();
                if (colorBg instanceof ColorDrawable) {
                    mDoodle.setColor(new DoodleColor(((ColorDrawable) colorBg).getColor()));
                } else {
                    mDoodle.setColor(new DoodleColor(((BitmapDrawable) colorBg).getBitmap()));
                }
            } else if (pen == DoodlePen.MOSAIC) {
                if (mMosaicLevel <= 0) {
                    mMosaicMenu.findViewById(R.id.btn_mosaic_level2).performClick();
                } else {
                    mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, mMosaicLevel));
                }
            } else if (pen == DoodlePen.COPY) {

            } else if (pen == DoodlePen.ERASER) {

            } else if (pen == DoodlePen.TEXT) {
                Drawable colorBg = mBtnColor.getBackground();
                if (colorBg instanceof ColorDrawable) {
                    mDoodle.setColor(new DoodleColor(((ColorDrawable) colorBg).getColor()));
                } else {
                    mDoodle.setColor(new DoodleColor(((BitmapDrawable) colorBg).getBitmap()));
                }
            } else if (pen == DoodlePen.BITMAP) {
                Drawable colorBg = mBtnColor.getBackground();
                if (colorBg instanceof ColorDrawable) {
                    mDoodle.setColor(new DoodleColor(((ColorDrawable) colorBg).getColor()));
                } else {
                    mDoodle.setColor(new DoodleColor(((BitmapDrawable) colorBg).getBitmap()));
                }
            }
        }

        private Map<IDoodleShape, Integer> mBtnShapeIds = new HashMap<>();

        {
            mBtnShapeIds.put(DoodleShape.HAND_WRITE, R.id.btn_hand_write);
            mBtnShapeIds.put(DoodleShape.ARROW, R.id.btn_arrow);
            mBtnShapeIds.put(DoodleShape.LINE, R.id.btn_line);
            mBtnShapeIds.put(DoodleShape.HOLLOW_CIRCLE, R.id.btn_holl_circle);
            mBtnShapeIds.put(DoodleShape.FILL_CIRCLE, R.id.btn_fill_circle);
            mBtnShapeIds.put(DoodleShape.HOLLOW_RECT, R.id.btn_holl_rect);
            mBtnShapeIds.put(DoodleShape.FILL_RECT, R.id.btn_fill_rect);

        }

        @Override
        public void setShape(IDoodleShape shape) {
            super.setShape(shape);
            setSingleSelected(mBtnShapeIds.values(), mBtnShapeIds.get(shape));
        }

        TextView mPaintSizeView = (TextView) classThis.findViewById(R.id.paint_size_text);

        @Override
        public void setSize(float paintSize) {
            super.setSize(paintSize);
            mEditSizeSeekBar.setProgress((int) paintSize);
            mPaintSizeView.setText("" + (int) paintSize);

            if (mTouchGestureListener.getSelectedItem() != null) {
                mTouchGestureListener.getSelectedItem().setSize(getSize());
            }
        }

        @Override
        public void setColor(IDoodleColor color) {
            IDoodlePen pen = getPen();
            super.setColor(color);

            DoodleColor doodleColor = null;
            if (color instanceof DoodleColor) {
                doodleColor = (DoodleColor) color;
            }
            if (doodleColor != null
                    && canChangeColor(pen)) {
                if (doodleColor.getType() == DoodleColor.Type.COLOR) {
                    mBtnColor.setBackgroundColor(doodleColor.getColor());
                } else if (doodleColor.getType() == DoodleColor.Type.BITMAP) {
                    mBtnColor.setBackgroundDrawable(new BitmapDrawable(doodleColor.getBitmap()));
                }

                if (mTouchGestureListener.getSelectedItem() != null) {
                    mTouchGestureListener.getSelectedItem().setColor(getColor().copy());
                }
            }

            if (doodleColor != null && pen == DoodlePen.MOSAIC
                    && doodleColor.getLevel() != mMosaicLevel) {
                switch (doodleColor.getLevel()) {
                    case DoodlePath.MOSAIC_LEVEL_1:
                        classThis.findViewById(R.id.btn_mosaic_level1).performClick();
                        break;
                    case DoodlePath.MOSAIC_LEVEL_2:
                        classThis.findViewById(R.id.btn_mosaic_level2).performClick();
                        break;
                    case DoodlePath.MOSAIC_LEVEL_3:
                        classThis.findViewById(R.id.btn_mosaic_level3).performClick();
                        break;
                }
            }
        }

        @Override
        public void enableZoomer(boolean enable) {
            super.enableZoomer(enable);
            classThis.findViewById(R.id.btn_zoomer).setSelected(enable);
            if (enable) {
                Toast.makeText(classThis, "x" + mDoodleParams.mZoomerScale, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean undo() {
            mTouchGestureListener.setSelectedItem(null);
            return super.undo();
        }

        @Override
        public void clear() {
            super.clear();
            mTouchGestureListener.setSelectedItem(null);
        }

        View mBtnEditMode = classThis.findViewById(R.id.doodle_btn_brush_edit);
        Boolean mLastIsDrawableOutside = null;

        @Override
        public void setEditMode(boolean editMode) {
            if (editMode == isEditMode()) {
                return;
            }

            super.setEditMode(editMode);
            mBtnEditMode.setSelected(editMode);
            if (editMode) {
                Toast.makeText(classThis, R.string.doodle_move_mode, Toast.LENGTH_SHORT).show();
                mLastIsDrawableOutside = mDoodle.isDrawableOutside(); // save
                mDoodle.setIsDrawableOutside(true);
                mPenContainer.setVisibility(GONE);
                mShapeContainer.setVisibility(GONE);
                mSizeContainer.setVisibility(GONE);
                mColorContainer.setVisibility(GONE);
                mBtnUndo.setVisibility(GONE);
                mMosaicMenu.setVisibility(GONE);
            } else {
                Toast.makeText(classThis, R.string.doodle_edit_mode, Toast.LENGTH_SHORT).show();
                if (mLastIsDrawableOutside != null) { // restore
                    mDoodle.setIsDrawableOutside(mLastIsDrawableOutside);
                }
                mTouchGestureListener.center(); // center picture
                if (mTouchGestureListener.getSelectedItem() == null) { // restore
                    setPen(getPen());
                }
                mTouchGestureListener.setSelectedItem(null);
                mPenContainer.setVisibility(VISIBLE);
                mSizeContainer.setVisibility(VISIBLE);
                mBtnUndo.setVisibility(VISIBLE);
            }
        }

        private void setSingleSelected(Collection<Integer> ids, int selectedId) {
            for (int id : ids) {
                if (id == selectedId) {
                    classThis.findViewById(id).setSelected(true);
                } else {
                    classThis.findViewById(id).setSelected(false);
                }
            }
        }
    }

    /**
     * 显示退出对话框
     */
    public void makeExitDialog() {
        Builder dlgBuilder = new Builder(this);
        dlgBuilder.setTitle(R.string.alert_ts);
        dlgBuilder.setMessage(R.string.alert_whether_save_photo);
        dlgBuilder.setIcon(R.drawable.ic_dialog_alert_blue_v);
        dlgBuilder.setCancelable(true);

        dlgBuilder.setPositiveButton(R.string.record_save, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setNegativeButton(R.string.record_not_save, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        savePhotoDlg = dlgBuilder.create();
        savePhotoDlg.show();

        // 保存按钮
        Button confirmBtn = savePhotoDlg.getButton(DialogInterface.BUTTON_POSITIVE);
        // 放弃按钮
        Button discardBtn = savePhotoDlg.getButton(DialogInterface.BUTTON_NEGATIVE);
        // 取消按钮
        Button cancelBtn = savePhotoDlg.getButton(DialogInterface.BUTTON_NEUTRAL);
        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDoodle.save();
                savePhotoDlg.cancel();
            }
        });
        discardBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goBack();
                savePhotoDlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                savePhotoDlg.cancel();
            }
        });
    }

    /**
     * 保存图片 AsyncTask 类
     */
    private class SaveImageTask extends AsyncTask<Object, Integer, String> {
        /**
         * 图片文件
         */
        private File imageFile;

        /**
         * invoked on the UI thread before the task is executed. This step is normally used to setup the task, for
         * instance by showing a progress bar in the user interface.
         */
        @Override
        protected void onPreExecute() {
            // 显示等待窗口
            makeWaitDialog("正在保存，请稍候…");
        }

        /**
         * The system calls this to perform work in a worker thread and delivers it the parameters given to
         * AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Object... params) {
            String result = CommonParam.RESULT_ERROR;

            Bitmap bitmap = (Bitmap) params[0];
            // 处理数据。开始============================================================================
            File doodleFile = null;
            String savePath = mDoodleParams.mSavePath;
            boolean isDir = mDoodleParams.mSavePathIsDir;
            if (TextUtils.isEmpty(savePath)) {
                File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
                doodleFile = new File(dcimFile, "Doodle");
                //　保存的路径
                imageFile = new File(doodleFile, System.currentTimeMillis() + ".jpg");
            } else {
                if (isDir) {
                    doodleFile = new File(savePath);
                    //　保存的路径
                    imageFile = new File(doodleFile, System.currentTimeMillis() + ".jpg");
                } else {
                    imageFile = new File(savePath);
                    doodleFile = imageFile.getParentFile();
                }
            }
            doodleFile.mkdirs();

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                ImageUtils.addImage(getContentResolver(), imageFile.getAbsolutePath());

                result = CommonParam.RESULT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Util.closeQuietly(outputStream);
            }
            // 处理数据。结束============================================================================

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
                mDoodleView.clear();

                HashMap<String, Object> atta = new HashMap<String, Object>();
                atta.put("type", CommonParam.ATTA_TYPE_PHOTO);
                atta.put("name", imageFile.getName());
                atta.put("alias", aliasName);
                atta.put("size", new FileUtil().getFileSize(imageFile));


                // 创建信息传输Bundle
                Bundle data = new Bundle();
                data.putSerializable("info", atta);

                // 创建启动 Activity 的 Intent
                Intent intent = new Intent();
                // 将数据存入Intent中
                intent.putExtras(data);
                // 设置该 Activity 的结果码，并设置结束之后返回的 Activity
                setResult(Activity.RESULT_OK, intent);

                show("保存成功");

                goBack();
            }
        }
    }
}
