package de.dfki.mpk.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Olakunmi on 23/08/2017.
 */

public class Exhibits extends Content {
    String icon;
    String image;
    int[] pixelCoord = new int[2];
    int[] reference;
    String text;

    public Exhibits(JSONObject object)
    {
        try {
            id = object.getInt("id");
            icon = object.getString("icon");
            image = object.getString("image");
            JSONObject pixel = object.getJSONObject("pixel_coords");
            pixelCoord[0] = pixel.getInt("x");
            pixelCoord[1] = pixel.getInt("y");
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
