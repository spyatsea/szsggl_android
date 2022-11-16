package com.cox.android.ui;

import android.app.Activity;
import android.content.Context;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.cox.android.szsggl.R;
import com.cox.utils.CommonParam;

public class TouchListView extends ListView {
	/**
	 * 手势水平座标
	 * */
	private int motionX = 0;

	public TouchListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TouchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchListView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			motionX = (int) event.getX();
			break;
		case (MotionEvent.ACTION_UP):
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
