package de.dfki.mpk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;

import de.dfki.mpk.utils.NetworkUtils;

/**
 * Created by Olakunmi on 06/08/2017.
 */

public class MPKApplication extends android.app.Application {
     private static final String FIRST_RUN  = "first_run";
     private static final String EMAIL  = "email";

    static AccessToken fbToken = null;

    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

    public void setFbToken(AccessToken token)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
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
                                            String response = NetworkUtils.post(NetworkUtils.FB_POST_URL,object.toString());
                                        Log.d("Kunmi",response);
                                        }
                                        catch (IOException e)
                                        {e.printStackTrace();}
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
        parameters.putString("fields", "id,about,email,age_range,birthday,education," +
                "first_name,last_name,middle_name,name,gender,hometown,inspirational_people,political,public_key,quotes," +
                "relationship_status,religion,books,friends,likes,live_videos,movies," +
                "music");
        request.setParameters(parameters);
        request.executeAsync();


    }
}
