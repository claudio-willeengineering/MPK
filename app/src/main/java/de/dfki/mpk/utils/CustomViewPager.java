package de.dfki.mpk.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Olakunmi on 08/08/2017.
 */

public class CustomViewPager extends ViewPager {
    boolean swipeEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
        swipeEnabled = true;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        swipeEnabled = true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipeEnabled) {
            return super.onTouchEvent(event);
        }

        return true;
    }



    public void enableSwipe(boolean enabled) {
        this.swipeEnabled = enabled;
    }
}
