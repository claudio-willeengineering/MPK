package de.dfki.mpk.model;

import android.content.Context;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Stack;

import de.dfki.mpk.R;
import de.dfki.mpk.utils.NetworkUtils;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by student on 31.08.17.
 */

public class FacebookModel {

    JSONObject fbModel;

    Stack<String> itemStack = new Stack<>();
    HashMap<String, JSONArray> otherItems = new HashMap<String, JSONArray>();

    AccessToken token;
    Context context;

    String currentItem = "";
    String maxFbFriends = "";


    public FacebookModel(String uniqueID, JSONObject json, Context context, AccessToken token) {
        fbModel = UtilsHelpers.fromRawToJson(context, R.raw.fb);

        this.token = token;
        this.context = context;

        try {
            fbModel.put("uid",uniqueID);

            if (json.has("id"))
                fbModel.put("id", json.getString("id"));
            if (json.has("email"))
                fbModel.put("email", json.getString("email"));
            if (json.has("name"))
                fbModel.put("name", json.getString("name"));

            JSONObject basicInfo = new JSONObject();
            basicInfo.put("last_name", json.has("last_name") ? json.getString("last_name") : "");
            basicInfo.put("first_name", json.has("first_name") ? json.getString("first_name") : "");
            basicInfo.put("middle_name", json.has("middle_name") ? json.getString("middle_name") : "");
            basicInfo.put("gender", json.has("gender") ? json.getString("gender") : "");

            JSONObject ageObj = null;
            if(json.has("age_range")) {
                ageObj = json.getJSONObject("age_range");
            }
            else
            {
                ageObj = new JSONObject();
            }
            {
                JSONObject tempObj = new JSONObject();
                tempObj.put("min", ageObj.has("min") ? ageObj.getString("min") : "");
                tempObj.put("max", ageObj.has("max") ? ageObj.getString("max") : "");
                basicInfo.put("age_range", tempObj);
            }

            basicInfo.put("birthday", json.has("birthday") ? json.getString("birthday") : "");


            basicInfo.put("home_town", json.has("home_town") ? json.getString("home_town") : "");
            basicInfo.put("religion", json.has("religion") ? json.getString("religion") : "");
            basicInfo.put("political", json.has("political") ? json.getString("political") : "");
            basicInfo.put("about", json.has("about") ? json.getString("about") : "");
            basicInfo.put("relationship_status", json.has("relationship_status") ? json.getString("relationship_status") : "");
            basicInfo.put("quotes", json.has("quotes") ? json.getString("quotes") : "");
            fbModel.put("basic_info", basicInfo);


            JSONArray education = new JSONArray();
            if(json.has("education"))
            {
                JSONArray edu = json.getJSONArray("education");
                try {
                    for (int i = 0; i < edu.length(); i++) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("id", edu.getJSONObject(i).has("school") ?
                                edu.getJSONObject(i).getJSONObject("school").getString("id") : "");
                        tempObj.put("name", edu.getJSONObject(i).has("school") ?
                                edu.getJSONObject(i).getJSONObject("school").getString("name") : "");
                        tempObj.put("type", edu.getJSONObject(i).has("type") ? edu.getJSONObject(i).getString("type") : "");
                        education.put(tempObj);
                    }
                }
                catch (Exception exp)
                {
                    exp.printStackTrace();
                }

            }

            fbModel.put("education",education);
            basicInfo.put("sports",json.has("sports")?json.getJSONArray("sports"):new JSONArray());
            JSONObject favourites = new JSONObject();
            favourites.put("favorite_athletes", json.has("favorite_athletes")?json.getJSONArray("favorite_athletes"):
                    new JSONArray());
            favourites.put("favorite_teams", json.has("favorite_teams")?json.getJSONArray("favorite_teams"):
                    new JSONArray());
            favourites.put("books",new JSONArray());
            favourites.put("movies", new JSONArray());
            favourites.put("music", new JSONArray());
            favourites.put("television", new JSONArray());

            fbModel.put("favourites",favourites);

            fbModel.put("likes",new JSONArray());
            fbModel.put("friends", new JSONObject());
            fbModel.getJSONObject("friends").put("no_friends","");
            fbModel.getJSONObject("friends").put("list",new JSONArray());



            itemStack.push("likes");
            itemStack.push("friends");
            itemStack.push("movies");
            itemStack.push("music");
            itemStack.push("television");

            queryFB();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        }

    public void queryFB()
    {

        if(!itemStack.isEmpty())
        {
            currentItem = itemStack.pop();

            new GraphRequest(
                    token,
                    "me/"+currentItem,
                    null,
                    HttpMethod.GET,
                    fbOtherCallBack
            ).executeAsync();

        }
        else {
            try {
                sendWhatWeHaveSoFar();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }


    }


    GraphRequest.Callback fbOtherCallBack = new GraphRequest.Callback(){
        @Override
        public void onCompleted(GraphResponse response) {
            try {

                if(response.getConnection().getResponseCode() == 200)
                {
                    if(response.getJSONObject() != null) {

                        consume(response.getJSONObject());


                        GraphRequest nextResultsRequests = response.
                                getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);

                        if (nextResultsRequests != null) {
                            nextResultsRequests.setCallback(fbOtherCallBack);
                            nextResultsRequests.executeAsync();
                        }
                        else
                        {
                            if(!itemStack.isEmpty()){
                                queryFB();
                            }
                            else
                            {
                                sendWhatWeHaveSoFar();
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                sendWhatWeHaveSoFar();
                e.printStackTrace();
            }

        }
    };


    public void consume(JSONObject object) throws JSONException{
        JSONArray data = object.getJSONArray("data");


        switch (currentItem) {
            case "likes":
                for(int i=0; i< data.length(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",data.getJSONObject(i).getString("id"));
                    jsonObject.put("name",data.getJSONObject(i).getString("name"));
                    fbModel.getJSONArray("likes").put(jsonObject);
                }
                break;
            case "friends":
                try {

                    if(object.has("summary"))
                    {
                        fbModel.getJSONObject("friends").put("no_friends",
                                object.getJSONObject("summary").getString("total_count"));
                    }
                }
                catch (Exception e) {
                e.printStackTrace();
                }

                for(int i=0; i< data.length(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",data.getJSONObject(i).getString("id"));
                    fbModel.getJSONObject("friends").getJSONArray("list").put(jsonObject);
                }
                break;
            case "movies":
                for(int i=0; i< data.length(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",data.getJSONObject(i).has("id")?data.getJSONObject(i).getString("id"):"");
                    jsonObject.put("name",data.getJSONObject(i).has("name")?data.getJSONObject(i).getString("name"):"");
                    jsonObject.put("genre",data.getJSONObject(i).has("genre")?data.getJSONObject(i).getString("genre"):"");
                }
                break;
            case "music":
                for(int i=0; i< data.length(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",data.getJSONObject(i).has("id")?data.getJSONObject(i).getString("id"):"");
                    jsonObject.put("name",data.getJSONObject(i).has("name")?data.getJSONObject(i).getString("name"):"");
                    jsonObject.put("genre",data.getJSONObject(i).has("genre")?data.getJSONObject(i).getString("genre"):"");
                }
                break;
            case "television":
                for(int i=0; i< data.length(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",data.getJSONObject(i).has("id")?data.getJSONObject(i).getString("id"):"");
                    jsonObject.put("name",data.getJSONObject(i).has("name")?data.getJSONObject(i).getString("name"):"");
                    jsonObject.put("genre",data.getJSONObject(i).has("genre")?data.getJSONObject(i).getString("genre"):"");
                }
                break;

        }

    }


    public void sendWhatWeHaveSoFar()
    {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        String response = NetworkUtils.post(NetworkUtils.FB_POST_URL,fbModel.toString());
                    Log.d(FacebookModel.class.getSimpleName(),response);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }
            }).start();

    }
}
