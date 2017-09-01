package de.dfki.mpk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import java.util.UUID;

import de.dfki.mpk.model.FacebookModel;

/**
 * Created by Olakunmi on 06/08/2017.
 */

public class MPKApplication extends android.app.Application {
     private static final String FIRST_RUN  = "first_run";
     private static final String EMAIL  = "email";
     private static final String UID = "UID";


    static AccessToken fbToken = null;

    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public String getUserID()
    {
        synchronized (this){
            if (!preferences.contains(UID)) {
                String uniqueID = UUID.randomUUID().toString();

                SharedPreferences.Editor editor = preferences.edit();

                editor.putString(UID, uniqueID);
                editor.apply();
                editor.commit();
                return uniqueID;
            } else {
                return preferences.getString(UID, null);
            }
        }
    }


    public boolean isFirstRun()
    {
        return preferences.getBoolean(FIRST_RUN,true);
    }

    public void setHasRunOnce()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_RUN,false);
        editor.apply();
        editor.commit();
    }

    public  void setEmail(String email)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL,email);
        editor.apply();
        editor.commit();
    }

    public void setFbToken(final AccessToken token)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, final GraphResponse response) {
                        Log.v("MPKApplication", response.toString());

                        // Application code
                        try{

                            int code = response.getConnection().getResponseCode();
                            if(code == 200)
                            {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            if(object != null)
                                            {
                                                FacebookModel model = new FacebookModel(getUserID(),object,getApplicationContext(), token);
                                            }
                                            //Log.d("Kunmi",response);
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        Log.d("Result",object.toString());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "" +
                "id," +
                "about," +
                "email," +
                "age_range," +
                "birthday," +
                "first_name,last_name,middle_name,name,gender," +
                "hometown,inspirational_people,political,quotes," +
                "relationship_status,religion," +
                "education," +
                "favorite_athletes," +
                "favorite_teams," +
                "sports");
        request.setParameters(parameters);
        request.executeAsync();


/*        new GraphRequest(
                token,
                "me/education",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            if (response.getConnection().getResponseCode() == 200) {
                                if (response.getJSONObject() != null) {
                                    //fbResultCollection.add(res);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
        ).executeAsync();

*/
    }



}
