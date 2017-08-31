package de.dfki.mpk.model;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Olakunmi on 23/08/2017.
 */

public class Topic extends Content implements Comparable<Content>{

    public Topic(JSONObject object)
    {
        super(object.toString());
        try {
            title = object.getString("title");
            id = object.getString("id");
            image = object.getString("image");
            title = object.getString("title");
            text = object.getString("text");
            JSONArray ref = object.getJSONArray("references");
            reference = new String[ref.length()];
            for (int i=0; i<ref.length(); i++)
            {
                reference[i] =  ref.get(i).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int compareTo(@NonNull Content t1) {
        return (getTitle().charAt(0)+"").toUpperCase().compareTo((t1.getTitle().charAt(0)+"").toUpperCase());
    }

}
