package de.dfki.mpk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.dfki.mpk.Home;
import de.dfki.mpk.MPKApplication;
import de.dfki.mpk.R;
import de.dfki.mpk.model.ExhibitTimeWrapper;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by Olakunmi on 14/08/2017.
 */

public class FragmentPass extends BaseFragment {
    private static FragmentPass currentInstance = null;

    public static FragmentPass createInstance(){
        if(currentInstance == null)
        {
            currentInstance = new FragmentPass();
            currentInstance.title = "Pass";
        }

        return currentInstance;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            ((Home) getActivity()).passButton.setBackgroundColor(ContextCompat.
                    getColor(getActivity(),R.color.yellow));

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pass,container,false);

        try {
             JSONObject pass = generateUserPass();

            ImageView discountImg = v.findViewById(R.id.discountImage);
            if(pass.getBoolean("ed"))
                discountImg.setVisibility(View.VISIBLE);
            else
                discountImg.setVisibility(View.GONE);

            pass.put("vrc_1",isVoyager1Met());
            pass.put("vrc_2", isVoyager2Passed());

            //Visited more than 18 exhibition items
            int count2 = 0;

            //A visit is 5 seconds
            int visitTime = 5;

            Home activity = (Home) getActivity();
            MPKApplication application = (MPKApplication) activity.getApplication();

            for(ExhibitTimeWrapper items : activity.getExhibitTimeWrappers())
            {
                if(items.getTimeSpent() > visitTime)
                {
                    count2++;
                }
            }
            pass.put("vcc_2",count2>18);

            //they spent more than 10 minutes in area 34
            ExhibitTimeWrapper ex = activity.getExhibitTimeWrapper("34");
            if(ex!=null) {
                pass.put("vcc_3", ex.getTimeSpent() > 600);
            }
            else
            {
                pass.put("vcc_3", false);
            }

            //they pressed the like-button for at least 6 items from the
            // following list: 6, 10, 11, 13, 19, 20, 30, 31, 32, 38, 41
            String[] conditions = {"6", "10", "11", "13", "19", "20", "30", "31", "32", "38", "41"};

            int count4 = 0;
            for(String id : conditions)
            {
                if(application.isLiked(id)){
                    count4++;
                }
            }
            pass.put("vcc_4",count4>=6);

            //- they spent at least one minute in front of 10 or more items

            int count5 =0;
            for(ExhibitTimeWrapper items : activity.getExhibitTimeWrappers())
            {
                if(items.getTimeSpent() >= 60)
                {
                    count5++;
                }
                if(count5 >=10)
                    break;
            }

            pass.put("vcc_5",count5>=10);

            //- they spent at least two minutes in front of at least two items from the following list:
            // 35, 36, 41


            int count6 = 0;
            String ids[] = {"35", "36", "41"};
            for(String id : ids)
            {
                ExhibitTimeWrapper e = activity.getExhibitTimeWrapper(id);
                if(e!=null) {

                    if (e.getTimeSpent() >= 120) {
                        count6++;
                    }
                }
            }
            pass.put("vcc_6",count6>=2);


            //They visited at least three items from the following list: 43, 44, 45, 46, 47, 48

            int count7 = 0;
            String ids2[] = {"43", "44", "45", "46", "47", "48"};
            for(String id : ids2)
            {
                ExhibitTimeWrapper e = activity.getExhibitTimeWrapper(id);
                if(e!=null)
                if(e.getTimeSpent()>=visitTime)
                {
                    count7++;
                }
            }
            pass.put("vcc_7", count7>=3);


            ((ImageView) v.findViewById(R.id.qrimagebox)).setImageBitmap(UtilsHelpers.TextToImageEncode(pass.toString(), getActivity()));
        }
        catch (JSONException exp)
        {
            exp.printStackTrace();
            UtilsHelpers.showErrorDialog(getActivity(),"Error Occurred","a Write exception occurred");
        }
        catch (WriterException ex)
        {
            ex.printStackTrace();
        }

        return v;
    }

    public boolean isVoyager1Met(){
        boolean passed = false;
        JSONObject voyager1 = UtilsHelpers.fromRawToJson(getActivity(), R.raw.voyagercondition);

        Home activity = (Home) getActivity();

        try {
            double threshold = voyager1.getDouble("time_treshold");
            JSONArray array = voyager1.getJSONArray("single_conditions");
            for(int i=0 ; i< array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString("id");

                if(activity.containsExhibit(id))
                {
                    ExhibitTimeWrapper tw = activity.getExhibitTimeWrapper(id);
                    if(tw!=null) {
                        if (tw.getTimeSpent() > 60) {
                            passed = true;
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return passed;
    }
    public boolean isVoyager2Passed(){
        boolean passed = false;
        JSONObject voyager2 = UtilsHelpers.fromRawToJson(getActivity(), R.raw.voyagercondition);

        Home activity = (Home) getActivity();

        try {
            double threshold = voyager2.getDouble("time_treshold");
            JSONArray array = voyager2.getJSONArray("combined_conditions");
            for(int i=0 ; i< array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);
                JSONArray ids = object.getJSONArray("id");

                for(int y=0; y>ids.length(); y++) {
                    if (activity.containsExhibit(ids.getString(y))) {
                        ExhibitTimeWrapper tw = activity.getExhibitTimeWrapper(ids.getString(y));
                        if(tw!=null) {
                            if (tw.getTimeSpent() > 60) {
                                passed = true;
                            } else {
                                passed = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return passed;
    }

    public JSONObject generateUserPass() throws JSONException {
        JSONObject userDetail = UtilsHelpers.fromRawToJson(getActivity(), R.raw.pass);
        userDetail.put("uid",((MPKApplication) getActivity().getApplication()).getUserID());
        userDetail.put("ed",((MPKApplication) getActivity().getApplication()).getFacebookApproved());

        return userDetail;

    }

}
