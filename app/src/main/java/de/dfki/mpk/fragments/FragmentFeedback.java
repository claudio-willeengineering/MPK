package de.dfki.mpk.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.maps.android.PolyUtil;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.dfki.mpk.Home;
import de.dfki.mpk.MPKApplication;
import de.dfki.mpk.R;
import de.dfki.mpk.utils.NetworkUtils;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by Olakunmi on 21/08/2017.
 */

public class FragmentFeedback extends BaseFragment {


    static String LOG_TAG = FragmentFeedback.class.getSimpleName();
    public static FragmentFeedback currentInstance = null;
    Timer timer = null;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    long startTime = -1;
    long endTime = -1;


    ProgressBar progressBar = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private MediaPlayer   mPlayer = null;





    public static FragmentFeedback createInstance()
    {
        if(FragmentFeedback.currentInstance == null)
        {
            currentInstance = new FragmentFeedback();
            currentInstance.title = "Feedback";
        }

        return currentInstance;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            ((Home) getActivity()).feedbackButton.setBackgroundColor(ContextCompat.
                    getColor(getActivity(),R.color.yellow));

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback,container,false);



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

                    double durr = (endTime - startTime)/1000;
                    if( durr < 2)
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("Feedback");
                        dialog.setMessage("Zu kurz!");
                        dialog.show();
                        return true;
                    }

                    startPlaying();

                    AlertDialog.Builder sendThisAudio = new AlertDialog.Builder(getActivity());
                    sendThisAudio.setTitle("Feedback");
                    sendThisAudio.setMessage("schick das Audio?");
                    sendThisAudio.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopPlaying();


                            if(((Home) getActivity()).isRecordingLocation())
                            {
                                try {
                                    if(((Home) getActivity()).isInFence())
                                    {
                                        final JSONObject gpsJson = UtilsHelpers.fromRawToJson(getActivity(), R.raw.feedback_gps);
                                        MPKApplication application = (MPKApplication) getActivity().getApplication();
                                        gpsJson.put("uid",application.getUserID());
                                        gpsJson.put("timestamp",System.currentTimeMillis());

                                        File file = new File(mFileName);

                                        byte[] bytes = FileUtils.readFileToByteArray(file);

                                        String encoded = Base64.encodeToString(bytes, 0);

                                        gpsJson.put("audio",  encoded);
                                        JSONObject location = gpsJson.getJSONObject("location");
                                        Location currentLocation = ((Home) getActivity()).getMostRecentLocation();
                                        location.getJSONObject("position").put("lon",currentLocation.getLongitude());
                                        location.getJSONObject("position").put("lat", currentLocation.getLatitude());
                                        gpsJson.put("location",location);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String resp = NetworkUtils.post("http://uni-data.wearcom.org/submit/mpk_audio_feedback/",
                                                            gpsJson.toString(),
                                                            "5866070cdadcd1082ede0012a86eb2e0");
                                                    Log.d("NET", resp);
                                                }
                                                catch (IOException exp)
                                                {
                                                    exp.printStackTrace();
                                                }
                                            }
                                        }).start();

                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "Ein Fehler ist aufgetreten, bitte wenden Sie sich an admin", Toast.LENGTH_SHORT).show();
                                }
                            }

                            if(((Home) getActivity()).getMostRecentBeacon()!= null) {
                                try {
                                    final JSONObject gpsJson = UtilsHelpers.fromRawToJson(getActivity(), R.raw.feedback_beacon);
                                    MPKApplication application = (MPKApplication) getActivity().getApplication();
                                    gpsJson.put("uid", application.getUserID());
                                    gpsJson.put("timestamp", System.currentTimeMillis());
                                    File file = new File(mFileName);
                                    byte[] bytes = FileUtils.readFileToByteArray(file);
                                    String encoded = Base64.encodeToString(bytes, 0);
                                    gpsJson.put("audio", encoded);
                                    JSONObject location = gpsJson.getJSONObject("location");
                                    Location currentLocation = ((Home) getActivity()).getMostRecentLocation();
                                    location.put("position",((Home) getActivity()).getMostRecentBeacon());
                                    gpsJson.put("location", location);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String resp = NetworkUtils.post("http://uni-data.wearcom.org/submit/mpk_audio_feedback/",
                                                        gpsJson.toString(),
                                                        "5866070cdadcd1082ede0012a86eb2e0");
                                                Log.d("NET", resp);
                                            } catch (IOException exp) {
                                                exp.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                                catch (Exception exp)
                                {
                                    exp.printStackTrace();
                                }
                            }



                        }
                    });

                    sendThisAudio.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopPlaying();
                        }
                    });

                    sendThisAudio.show();

                }

                return true;
            }
        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
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
        startTime = System.currentTimeMillis();
        mRecorder.start();
    }

    private void stopRecording() {
        endTime = System.currentTimeMillis();
        if(mRecorder!=null) {
        try {
            endTime = System.currentTimeMillis();
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
        }
        }
    }






}

