package com.cox.android.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;

public class TouchScrollView extends ScrollView {
	/**
	 * 手势水平座标
	 * */
	private int motionX = 0;

	public TouchScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TouchScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchScrollView(Context context) {
		super(context);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			motionX = (int) event.getX();
			break;
		case MotionEvent.ACTION_UP:
			if ((event.getX() - motionX) > CommonParam.TOUCH_ACTIVE_H) {
				((Activity) getContext()).finish();
				((Activity) getContext()).overridePendingTransition(R.anim.activity_slide_right_in,
						R.anim.activity_slide_right_out);
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
