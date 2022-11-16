package com.cox.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.cox.android.szsggl.R;

public class MessageViewPager extends ViewPager {

	public MessageViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MessageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Message);
		// String name = ta.getString(R.styleable.News_treeid);
		// Log.d("id=", name);
		ta.recycle();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		super.addView(child, index, params);
	}

}
