package de.dfki.mpk.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import de.dfki.mpk.Home;
import de.dfki.mpk.MPKApplication;
import de.dfki.mpk.R;
import de.dfki.mpk.utils.ExponatIconHelper;
import de.dfki.mpk.utils.NetworkUtils;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by student on 04.09.17.
 */

public class ExhibitTimeWrapper {
    List<Long[]> durations = new ArrayList<>();
    Exhibits exhibits = null;
    long entrytime = -1;
    Activity activity;

    public  ExhibitTimeWrapper(Exhibits e, Activity a)
    {
        exhibits = e;
        activity = a;
        resetTime();
    }

    public long getTimeSpent()
    {
        long d = 0;
        for(Long[] l : durations)
        {
            d += (l[1]-l[0]);
        }
        if(entrytime>0)
        {
            d += System.currentTimeMillis() - entrytime;
        }
        return d/1000;
    }

    public Exhibits getExhibits(){
        return exhibits;
    }

    public void Enter()
    {
        if(entrytime<0) {
            entrytime = System.currentTimeMillis();
            updateMap();
        }
    }


    public void Leave()
    {
            if (entrytime > 0) {
                final Long[] visit = new Long[]{entrytime, System.currentTimeMillis()};
                durations.add(visit);
                resetTime();
                updateMap();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObject = UtilsHelpers.fromRawToJson(activity, R.raw.visits);
                            jsonObject.put("uid", ((MPKApplication) activity.getApplication()).getUserID());
                            jsonObject.put("eid",Integer.parseInt(exhibits.getId()));
                            jsonObject.put("sequence_number",durations.size());
                            jsonObject.put("checkin_time",visit[0]);
                            jsonObject.put("checkout_time",visit[1]);


                            String response = NetworkUtils.post("http://uni-data.wearcom.org/submit/mpk_visitor_journey/",
                                    jsonObject.toString(),
                                    "50f6c92f9e6ce44582f273de8ca1245f");

                            Log.d(Exhibits.class.getSimpleName(),response);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                ((Home) activity).computeThreshHoldRecommendations();

            }

    }

    void resetTime(){
        entrytime = -1;
    }
    void updateMap()
    {

        ((Home)activity).updateMapView();

    }

    public boolean isNearBy()
    {
        return entrytime > 0;
    }
    public boolean isRecommended(){
        if(((Home) activity).getRecommendedExponats().containsKey(exhibits.getId()))
            return true;
        else
            return false;
    }

    public Bitmap getIcon(){
        ExponatIconHelper helper = ((Home)activity).getIconHelper();
        int icon = exhibits.icon;

        if(isNearBy()){
            return helper.getNearbyIcon(icon);
        }
        else if(isRecommended())
        {
            return helper.getRecommendedIcon(icon);
        }
        else
        {
            return helper.normalIcon(icon);
        }
    }

}
