package de.dfki.mpk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;
import de.dfki.mpk.fragments.FragmentDetails;
import de.dfki.mpk.model.Exhibits;

/**
 * Created by Olakunmi on 07/08/2017.
 */

public class PanZoomImageView extends SubsamplingScaleImageView {
    private Bitmap pin;
    private int strokeWidth;
    List<Exhibits> exhibitsList = new ArrayList<>();



    public PanZoomImageView(Context context) {
        this(context, null);
    }

    public PanZoomImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin);
        //float w = (density/420f) * pin.getWidth();
        //float h = (density/420f) * pin.getHeight();

        strokeWidth = (int)(density/60f);

        float w = 220,h = 220;
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);

    }

    public void setData(List<Exhibits> ex)
    {
        exhibitsList = ex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        for(Exhibits e : exhibitsList){
            if (pin != null) {
                PointF vPin = e.getPointF();
                PointF vCenter = sourceToViewCoord(vPin);
                float vX = vCenter.x - (pin.getWidth()/2);
                float vY = vCenter.y - pin.getHeight();
                canvas.drawBitmap(pin, vX, vY, paint);
            }
        }
    }

    public final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(isReady())
            {
                PointF tappedPoint = new PointF(e.getX(), e.getY());
                for(Exhibits ex : exhibitsList)
                {
                    PointF exCoord = sourceToViewCoord(ex.getPointF());
                    Float pictureStartX = exCoord.x - (pin.getWidth()/2);
                    Float pictureEndX = exCoord.x + (pin.getWidth()/2);

                    Float pictureStartY = exCoord.y - (pin.getHeight());
                    Float pictureEndY = exCoord.y;




                    if(     tappedPoint.x >= pictureStartX &&
                            tappedPoint.x <= pictureEndX &&
                            tappedPoint.y >= pictureStartY &&
                            tappedPoint.y<= pictureEndY)
                    {
                        Toast.makeText(getContext(), "Captured "+ex.getTitle(),Toast.LENGTH_SHORT).show();
                        FragmentDetails fragmentDetails = FragmentDetails.createInstance();
                        Bundle b = new Bundle();
                        b.putString(FragmentDetails.key,ex.getJson());
                        fragmentDetails.setArguments(b);
                        ((Home)getContext()).switchFragment(fragmentDetails);
                    }



                }
            }
            return super.onSingleTapConfirmed(e);
        }
    });
}

