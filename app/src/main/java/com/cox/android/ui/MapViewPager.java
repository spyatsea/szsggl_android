/*
 * Copyright (c) www.spyatsea.com  2014 
 */
package com.cox.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.cox.android.szsggl.R;

public class MapViewPager extends ViewPager {

	public MapViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MapViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.News);
		// String name = ta.getString(R.styleable.News_id);
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

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item);
	}

}
