package de.dfki.mpk.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Created by Olakunmi on 07/08/2017.
 */

public class PanZoomImageView extends SubsamplingScaleImageView {
    private int strokeWidth;

    public PanZoomImageView(Context context) {
        this(context, null);
    }

    public PanZoomImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        strokeWidth = (int)(density/60f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        PointF sCenter = new PointF(getSWidth()/2, getSHeight()/2);
        PointF vCenter = sourceToViewCoord(sCenter);
        float radius = (getScale() * getSWidth()) * 0.02f;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth * 2);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(vCenter.x, vCenter.y, radius, paint);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.argb(255, 51, 181, 229));
        canvas.drawCircle(vCenter.x, vCenter.y, radius, paint);
    }
}
