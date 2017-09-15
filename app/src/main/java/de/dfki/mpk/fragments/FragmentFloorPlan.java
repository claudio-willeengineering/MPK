package de.dfki.mpk.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
    private String TAG = "FragmentFloorPlan";
    private Boolean DEBUG = true;

    private static FragmentFloorPlan currentInstance = null;
    PanZoomImageView imageView = null;

    public static FragmentFloorPlan createInstance() {
        if (currentInstance == null) {
            currentInstance = new FragmentFloorPlan();
            currentInstance.title = "Ausstellungsplan";
        }
        return currentInstance;
    }

    public static PanZoomImageView getFloorPlanImage() {
        if (currentInstance != null) {
            return currentInstance.imageView;
        }
        return null;
    }

    boolean visible = false;

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            visible = true;
        } else {
            visible = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_floorplan, container, false);

        imageView = v.findViewById(R.id.floorplan);
        imageView.setData(((Home) getActivity()).getExhibitTimeWrappers());
        imageView.setImage(ImageSource.resource(R.drawable.floorplan));

        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setMinimumDpi(450);

        Log.i(TAG, "onCreateView: IMAGE LOADED");

        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {


            @Override
            public void onReady() {
                if (DEBUG) Log.i(TAG, "onReady: READY");
                imageView.animateScaleAndCenter(0.3f, new PointF(5940, 2930))
                        .withDuration(2000)
                        .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                        .withInterruptible(false)
                        .start();

            }

            @Override
            public void onImageLoaded() {
                if (DEBUG) Log.i(TAG, "onImageLoaded: ");
            }

            @Override
            public void onPreviewLoadError(Exception e) {
                if (DEBUG) Log.i(TAG, "onPreviewLoadError: ");

            }

            @Override
            public void onImageLoadError(Exception e) {
                if (DEBUG) Log.i(TAG, "onImageLoadError: ");
            }

            @Override
            public void onTileLoadError(Exception e) {

                if (DEBUG) Log.i(TAG, "onTileLoadError: ");
            }

            @Override
            public void onPreviewReleased() {

                if (DEBUG) Log.i(TAG, "onPreviewReleased: ");
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
