package de.dfki.mpk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.dfki.mpk.model.FacebookModel;

/**
 * Created by Olakunmi on 06/08/2017.
 */

public class MPKApplication extends android.app.Application {
     public static final String BEACON_TOKEN = "b0d0b32f022f2fc000d9e939413d8414";
     private static final String FIRST_RUN  = "first_run";
     private static final String EMAIL  = "email.json";
     private static final String UID = "UID";

    private static final String FACEBOOK_APPROVED = "fb";
    private static final String BEACON_PERMISSION = "sensor_perm";
    private static final String SCIENCE_APPROVAL = "scienceApproval";
    private static final String NAME = "user_name";
    private static final String AGE_RANGE = "agerange";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String GENDER = "gender";
    private static final String IMAGE_PREFERENCE = "image_pref";


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

    public void setBeaconPermission(boolean permitted) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(BEACON_PERMISSION,permitted);
        editor.apply();
        editor.commit();
    }

    public boolean getBeaconPermission() {
        boolean permitted = false;
        return preferences.getBoolean(BEACON_PERMISSION,false);
    }

    public void setFacebookApproved(boolean facebookApproved) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FACEBOOK_APPROVED,facebookApproved);
        editor.apply();
        editor.commit();
    }

    public boolean getFacebookApproved()
    {
        return preferences.getBoolean(FACEBOOK_APPROVED,false);
    }

    public void setScienceApproval(boolean emailApproval)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SCIENCE_APPROVAL,emailApproval);
        editor.apply();
        editor.commit();
    }

    public boolean getScienceApproved(){
        return preferences.getBoolean(SCIENCE_APPROVAL,false);
    }

    public void setUserFullName (String name)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME,name);
        editor.apply();
        editor.commit();
    }

    public String getUserFullName()
    {
        return preferences.getString(NAME,"");
    }


    public  void setEmail(String email)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL,email);
        editor.apply();
        editor.commit();
    }

    public String getEmail() {
        return preferences.getString(EMAIL,"");
    }

    public void setAgeRange(String ageRange)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AGE_RANGE, ageRange);
        editor.apply();
        editor.commit();
    }

    public String getAgeRange()
    {
        return preferences.getString(AGE_RANGE, "{}");
    }

    public void setFirstName(String firstName)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FIRST_NAME,firstName);
        editor.apply();
        editor.commit();
    }

    public String getFirstName()
    {
        return  preferences.getString(FIRST_NAME,"");
    }

    public void setLastName(String lastName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_NAME,lastName);
        editor.apply();
        editor.commit();
    }

    public  String getLastName() {
        return preferences.getString(LAST_NAME,"");
    }

    public void setGender (String gender)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(GENDER, gender);
        editor.apply();
        editor.commit();
    }

    public  String getGender() {
        return preferences.getString(GENDER,"");
    }

    public void setImagePreference(int imagePreference) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(IMAGE_PREFERENCE,imagePreference);
        editor.apply();
        editor.commit();
    }

    public int getImagePreference() {
        return preferences.getInt(IMAGE_PREFERENCE,-1);
    }

    public void setFbToken(final AccessToken token)
    {
        setFacebookApproved(true);
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

    }

    final static String LIKES = "likes";
    public void addtoLikes( String id) throws JSONException
    {
        String likes = preferences.getString(LIKES, "{}");
        JSONObject jsonObject = new JSONObject(likes);
        if(jsonObject.has(id))
        {
            return;
        }
        else
            jsonObject.put(id,"yes");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LIKES,jsonObject.toString());
        editor.apply();
        editor.commit();
    }

    public boolean isLiked(String id) throws JSONException
    {
        JSONObject object = new JSONObject(preferences.getString(LIKES,"{}"));
        return object.has(id);
    }

    public List<String> getAllLikes() throws JSONException
    {
        List<String> likes = new ArrayList<>();
        JSONObject object = new JSONObject(preferences.getString(LIKES,"{}"));
        if(object.names()!=null)
        {
            if(object.names().length()>0)
            {
                for(int i=0; i<object.names().length() ; i++)
                {
                    likes.add(object.names().getString(i));
                }
            }
        }
        return likes;
    }









}
