package de.dfki.mpk.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;
import de.dfki.mpk.utils.PanZoomImageView;

/**
 * Created by Olakunmi on 13/08/2017.
 */

public class FragmentFloorPlan extends BaseFragment {
    private static FragmentFloorPlan currentInstance = null;

    public static FragmentFloorPlan createInstance()
    {
        if(currentInstance == null)
        {
            currentInstance = new FragmentFloorPlan();
            currentInstance.title = "Ausstellungsplan";
        }
        return currentInstance;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_floorplan,container,false);


        final PanZoomImageView imageView = (PanZoomImageView) v.findViewById(R.id.floorplan);
        imageView.setData(((Home)getActivity()).getExhibitTimeWrappers());
        ((SubsamplingScaleImageView)imageView).setImage(ImageSource.resource(R.drawable.floorplan));

        //imageView.setMinimumTileDpi(540);

        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        //imageView.setMaximumDpi(840);
        imageView.setMinimumDpi(450);

        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener(){

            @Override
            public void onReady() {

                imageView.animateScaleAndCenter(0.3f, new PointF(1500, 1000))
                        .withDuration(2000)
                        .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                        .withInterruptible(false)
                        .start();

            }

            @Override
            public void onImageLoaded() {

            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }

            @Override
            public void onPreviewReleased() {

            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return imageView.gestureDetector.onTouchEvent(motionEvent);
            }
        });



        return v;
    }

}
