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
import java.util.HashMap;
import java.util.List;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;
import de.dfki.mpk.fragments.FragmentDetails;
import de.dfki.mpk.model.ExhibitTimeWrapper;
import de.dfki.mpk.model.Exhibits;

/**
 * Created by Olakunmi on 07/08/2017.
 */

public class PanZoomImageView extends SubsamplingScaleImageView {

    List<ExhibitTimeWrapper> exhibitsList = null;
    Paint paint = new Paint();

    public PanZoomImageView(Context context) {
        this(context, null);
    }

    public PanZoomImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    private void initialise() {
        paint.setAntiAlias(true);

    }

    public void setData(List<ExhibitTimeWrapper> ex)
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


        for(ExhibitTimeWrapper e : exhibitsList){
                PointF vPin = e.getExhibits().getPointF();
                PointF vCenter = sourceToViewCoord(vPin);
            Bitmap icon = e.getIcon();
                float vX = vCenter.x - (icon.getWidth()/2);
                float vY = vCenter.y - icon.getHeight();
                canvas.drawBitmap(icon, vX, vY, paint);

        }
    }

    public final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(isReady())
            {
                PointF tappedPoint = new PointF(e.getX(), e.getY());
                for(ExhibitTimeWrapper ex : exhibitsList)
                {
                    Bitmap pin = ex.getIcon();
                    PointF exCoord = sourceToViewCoord(ex.getExhibits().getPointF());
                    Float pictureStartX = exCoord.x - (pin.getWidth()/2);
                    Float pictureEndX = exCoord.x + (pin.getWidth()/2);

                    Float pictureStartY = exCoord.y - (pin.getHeight());
                    Float pictureEndY = exCoord.y;




                    if(     tappedPoint.x >= pictureStartX &&
                            tappedPoint.x <= pictureEndX &&
                            tappedPoint.y >= pictureStartY &&
                            tappedPoint.y<= pictureEndY)
                    {
                        //Toast.makeText(getContext(), "Captured "+ex.getExhibits().getTitle(),Toast.LENGTH_SHORT).show();
                        FragmentDetails fragmentDetails = FragmentDetails.createInstance();
                        Bundle b = new Bundle();
                        b.putString(FragmentDetails.key,ex.getExhibits().getJson());
                        fragmentDetails.setArguments(b);
                        ((Home)getContext()).switchFragment(fragmentDetails,ex.getExhibits().getTitle() );
                    }



                }
            }
            return super.onSingleTapConfirmed(e);
        }
    });
}

