package com.cox.android.szsggl.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cox.android.szsggl.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为ListView的Item设置不同的布局. 例如在该例子中ListView的第一个Item显示一张 图片,其余的Item都显示文字.
 * <p>
 * 为了达到此目的需要重写BaseAdapter中的 1 getViewTypeCount()和getItemViewType(int position)方法. 1.1在getViewTypeCount中指定一共有几种不同的item
 * 在此返回2即可. 1.2在getItemViewType(int position)中需要依据position的不同 返回不同的Type. 2 在getView()方法中针对不同的Type为Item设置布局
 * 2.1得到当前位置(position)时的Type即代码: currentType= getItemViewType(position); 2.2依据Type的不同为Item设置布局
 * <p>
 * 参考资料: 1、http://www.2cto.com/kf/201310/248500.html 2、http://www.cnblogs.com/devinzhang/archive/2012/07/02/2573554.html
 * 3、http://blog.csdn.net/yueyue369/article/details/6115552 4、http://blog.sina.com.cn/s/blog_5da93c8f0100wx4v.html Thank
 * you very much
 */
@SuppressLint("DefaultLocale")
@SuppressWarnings({"unchecked", "rawtypes"})
public class MaintenanceRecListAdapter extends BaseAdapter implements Filterable {

    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;

    private SimpleFilter mFilter;
    private ArrayList<Map<String, ?>> mUnfilteredData;

    // ===================================================
    private final int VIEW_TYPE_COUNT = 1;
    private final int TYPE_NORMAL = 0;

    // ===================================================

    public MaintenanceRecListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    // ===================================================
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, Object> data = (HashMap<String, Object>) mData.get(position);
        Integer viewType = TYPE_NORMAL;
        return viewType;
    }

    // ===================================================

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取到当前位置所对应的Type
        View view = null;
        // HashMap<String, Object> info = (HashMap<String, Object>) getItem(position);
        // 普通信息
        mDropDownResource = R.layout.maintenance_rec_list_item;
        view = createViewFromResource(position, convertView, parent, R.layout.maintenance_rec_list_item);
        convertView = view;

        return convertView;
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View v;
        // if (convertView == null) {
        v = mInflater.inflate(resource, parent, false);
        // } else {
        // v = convertView;
        // }

        bindView(position, v);

        return v;
    }

    /**
     * <p>
     * Sets the layout resource to create the drop down views.
     * </p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, View, ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName()
                                    + " should be bound to a Boolean, not a "
                                    + (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a "
                                + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link ViewBinder} used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     */
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to remove the existing binder
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if there is no existing ViewBinder or if the
     * existing ViewBinder cannot handle binding to an ImageView.
     * <p>
     * This method is called instead of {@link #setViewImage(ImageView, String)} if the supplied data is an int or
     * Integer.
     *
     * @param v     ImageView to receive an image
     * @param value the value retrieved from the data set
     * @see #setViewImage(ImageView, String)
     */
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if there is no existing ViewBinder or if the
     * existing ViewBinder cannot handle binding to an ImageView.
     * <p>
     * By default, the value will be treated as an image resource. If the value cannot be used as an image resource, the
     * value is used as an image Uri.
     * <p>
     * This method is called instead of {@link #setViewImage(ImageView, int)} if the supplied data is not an int or
     * Integer.
     *
     * @param v     ImageView to receive an image
     * @param value the value retrieved from the data set
     * @see #setViewImage(ImageView, int)
     */
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }

    /**
     * Called by bindView() to set the text for a TextView but only if there is no existing ViewBinder or if the
     * existing ViewBinder cannot handle binding to a TextView.
     *
     * @param v    TextView to receive text
     * @param text the text to be set for the TextView
     */
    public void setViewText(TextView v, String text) {
        v.setText(text);
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    /**
     * This class can be used by external clients of SimpleAdapter to bind values to views.
     * <p>
     * You should use this class to bind values to views that are not directly supported by SimpleAdapter or to change
     * the way binding occurs for views supported by SimpleAdapter.
     *
     * @see SimpleAdapter#setViewImage(ImageView, int)
     * @see SimpleAdapter#setViewImage(ImageView, String)
     * @see SimpleAdapter#setViewText(TextView, String)
     */
    public static interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         * <p>
         * When binding is handled by this ViewBinder, this method must return true. If this method returns false,
         * SimpleAdapter will attempts to handle the binding on its own.
         *
         * @param view               the view to bind the data to
         * @param data               the data to bind to the view
         * @param textRepresentation a safe String representation of the supplied data: it is either the result of data.toString() or
         *                           an empty String but it is never null
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

    /**
     * <p>
     * An array filters constrains the content of the array adapter with a prefix. Each item that does not start with
     * the supplied prefix is removed from the list.
     * </p>
     */
    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<Map<String, ?>>(mData);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Map<String, ?>> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<Map<String, ?>> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<Map<String, ?>> newValues = new ArrayList<Map<String, ?>>(count);

                for (int i = 0; i < count; i++) {
                    Map<String, ?> h = unfilteredValues.get(i);
                    if (h != null) {

                        int len = mTo.length;

                        for (int j = 0; j < len; j++) {
                            String str = (String) h.get(mFrom[j]);

                            String[] words = str.split(" ");
                            int wordCount = words.length;

                            for (int k = 0; k < wordCount; k++) {
                                String word = words[k];

                                if (word.toLowerCase().startsWith(prefixString)) {
                                    newValues.add(h);
                                    break;
                                }
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // noinspection unchecked
            mData = (List<Map<String, ?>>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
