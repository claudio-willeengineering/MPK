package de.dfki.mpk.model;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Olakunmi on 23/08/2017.
 */

public class Exhibits extends Content implements Comparable<Content>{
    String icon;
    PointF pointF;
    int[] pixelCoord = new int[2];

    public Exhibits(JSONObject object) throws JSONException
    {
        super(object.toString());

            id = object.getString("id");
            icon = object.getString("icon");
            image = object.getString("image");
            JSONObject pixel = object.getJSONObject("pixel_coords");
            pixelCoord[0] = pixel.getInt("x");
            pixelCoord[1] = pixel.getInt("y");
            pointF = new PointF(pixelCoord[0],pixelCoord[1]);
            title = object.getString("title");
            text = object.getString("text");
            JSONArray ref = object.getJSONArray("references");
            reference = new String[ref.length()];
            for (int i=0; i<ref.length(); i++)
            {
                reference[i] =  ref.get(i).toString();
            }


    }

    public int[] getPixelCoord() {
        return pixelCoord;
    }

    public PointF getPointF() {
        return pointF;
    }

    @Override
    public int compareTo(@NonNull Content t1) {
        return (getTitle().charAt(0)+"").toUpperCase().compareTo((t1.getTitle().charAt(0)+"").toUpperCase());
    }

}
