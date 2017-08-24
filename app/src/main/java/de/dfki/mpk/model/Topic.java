package de.dfki.mpk.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Olakunmi on 23/08/2017.
 */

public class Topic extends Content {
    String image;
    int[] reference;
    String text;

    public Topic(JSONObject object)
    {
        try {
            id = object.getInt("id");
            image = object.getString("image");
            JSONObject pixel = object.getJSONObject("pixel_coords");
            title = object.getString("title");
            text = object.getString("text");
            JSONArray ref = object.getJSONArray("references");
            reference = new int[ref.length()];
            for (int i=0; i<ref.length(); i++)
            {
                reference[i] =  Integer.parseInt(ref.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
