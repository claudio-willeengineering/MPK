package de.dfki.mpk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.dfki.mpk.R;

/**
 * Created by Olakunmi on 21/08/2017.
 */

public class FragmentFeedback extends BaseFragment {

    public static FragmentFeedback currentInstance = null;
    public static FragmentFeedback createInstance()
    {
        if(FragmentFeedback.currentInstance == null)
        {
            currentInstance = new FragmentFeedback();
            currentInstance.title = "Feedback";
        }

        return currentInstance;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback,container,false);

        final ImageView recorder = (ImageView) v.findViewById(R.id.recordAudio);
        recorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    recorder.setImageResource(R.drawable.recordaudio_active);
                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    recorder.setImageResource(R.drawable.recordaudio_normal);

                return true;
            }
        });


        return v;


    }
}