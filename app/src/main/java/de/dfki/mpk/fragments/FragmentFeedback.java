package de.dfki.mpk.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by Olakunmi on 21/08/2017.
 */

public class FragmentFeedback extends BaseFragment {

    static String LOG_TAG = FragmentFeedback.class.getSimpleName();
    public static FragmentFeedback currentInstance = null;
    Timer timer = null;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    ProgressBar progressBar = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private MediaPlayer   mPlayer = null;



    //Location Update
    Location mostRecentLocation = null;

    private FusedLocationProviderClient mFusedLocationClient = null;
    boolean mRequestingLocationUpdates = false;


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


        mFusedLocationClient = new FusedLocationProviderClient(getActivity());

        mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName += "/mpkfeedback.mp3";


        if(!((Home)getActivity()).hasAudioPermission())
        {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        }


        progressBar = v.findViewById(R.id.progress_bar);

        final ImageView recorder = (ImageView) v.findViewById(R.id.recordAudio);
        recorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(((Home) getActivity()).hasAudioPermission())
                    {

                    if(timer == null)
                    {
                        startRecording();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(progressBar.getProgress() >= progressBar.getMax())
                                {

                                    progressBar.setProgress(0);

                                    stopRecording();
                                    timer.cancel();
                                    return;
                                }
                                progressBar.incrementProgressBy(1);
                            }
                        }, 0, 10);


                    }
                    recorder.setImageResource(R.drawable.recordaudio_active);
                    }
                    else
                    {
                        UtilsHelpers.showErrorDialog(getActivity(),getString(R.string.error_audio_dialog_title),
                                getString(R.string.error_audio_dialog_body));
                        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);


                    }

                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    timer.cancel();
                    timer = null;
                    progressBar.setProgress(0);
                    recorder.setImageResource(R.drawable.recordaudio_normal);
                    stopRecording();
                }

                return true;
            }
        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        if(mRecorder!=null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

           LocationRequest mLocationRequest = new LocationRequest();


            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);

        }


    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

            }
        };
    };


    private void stopLocationUpdates() {

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


}

