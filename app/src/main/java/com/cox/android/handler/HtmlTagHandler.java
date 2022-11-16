package com.cox.android.handler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.activity.DbActivity;
import com.cox.android.szsggl.activity.WebDetailActivity;
import com.cox.utils.CommonParam;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;

//import android.util.Log;

public class HtmlTagHandler implements Html.TagHandler {
    private int startIndex = 0;
    private int endIndex = 0;
    private final Context mContext;
    XMLReader mReader;

    public HtmlTagHandler(Context context) {
        mContext = context;
    }

    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // TODO Auto-generated method stub
        if (tag.toLowerCase().startsWith("coxa_")) {
            if (opening) {
                startCoxA(tag, output, xmlReader);
            } else {
                endCoxA(tag, output, xmlReader);
            }
        }
    }

    public void startCoxA(String tag, Editable output, XMLReader xmlReader) {
//        Log.d("##1", output.toString());
        startIndex = output.length();
    }

    public void endCoxA(String tag, Editable output, XMLReader xmlReader) {
        endIndex = output.length();
//        Log.d("##2", output.toString());
        Bundle data = null;
        if ("coxa_1".equals(tag)) {
            data = new Bundle();
            // 将数据存入Intent中
            data.putString("url", "http://" + ((DbActivity) mContext).getBaseApp().serverAddr + "/" + CommonParam.URL_PRIVACY);
            data.putString("title", ((DbActivity) mContext).getString(R.string.privacy));
        }
        if (data != null) {
            CoxASpan span = new CoxASpan(data);
            output.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

//        Log.d("##5", "#");
    }

    private class CoxASpan extends ClickableSpan implements DialogInterface.OnClickListener {
        private Bundle mData;

        public CoxASpan(Bundle data) {
            mData = data;
        }

        @Override
        public void onClick(View v) {
//            Log.d("###4", "#");
            // 创建启动 Activity 的Intent
            Intent intent = new Intent(v.getContext(), WebDetailActivity.class);
            // 将数据存入 Intent 中
            intent.putExtras(mData);
            v.getContext().startActivity(intent);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
//            Log.d("###5", "#");
        }
    }

    /**
     * 利用反射获取html标签的属性值
     *
     * @param xmlReader
     * @param property
     * @return
     */
    private String getProperty(XMLReader xmlReader, String property) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            for (int i = 0; i < len; i++) {
                // 这边的property换成你自己的属性名就可以了
                if (property.equals(data[i * 5 + 1])) {
                    return data[i * 5 + 4];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
