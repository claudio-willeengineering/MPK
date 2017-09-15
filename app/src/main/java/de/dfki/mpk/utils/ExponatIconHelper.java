package de.dfki.mpk.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dfki.mpk.R;

/**
 * Created by student on 05.09.17.
 */

public class ExponatIconHelper {
    Activity activity = null;

    List<Bitmap> exponat = new ArrayList<>();
    List<Bitmap> empfehlung = new ArrayList<>();
    List<Bitmap> nearby = new ArrayList<>();

    private int strokeWidth;

    public ExponatIconHelper(Activity a){
        activity = a;
        float density = activity.getResources().getDisplayMetrics().densityDpi;
        float w = 120,h = 120;
        strokeWidth = (int)(density/60f);


        //exponat
        Resources resources = activity.getResources();

        exponat.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_codierung_exponat)
                , (int)w, (int)h, true));
        exponat.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_datenerfassung_exponat)
                , (int)w, (int)h, true));
        exponat.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_geheimnis_exponat)
                , (int)w, (int)h, true));
        exponat.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_schluessel_exponat)
                , (int)w, (int)h, true));
        exponat.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_werte_exponat)
                , (int)w, (int)h, true));

        //Exmpfehlung
        empfehlung.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_codierung_empfehlung)
                , (int)w, (int)h, true));
        empfehlung.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_datenerfassung_empfehlung)
                , (int)w, (int)h, true));
        empfehlung.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_geheimnis_empfehlung)
                , (int)w, (int)h, true));
        empfehlung.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_schluessel_empfehlung)
                , (int)w, (int)h, true));
        empfehlung.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_werte_empfehlung)
                , (int)w, (int)h, true));


        nearby.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.mpk_icon_codierung_position)
                , (int)w, (int)h, true));
        nearby.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.mpk_icon_datenerfassung_position)
                , (int)w, (int)h, true));
        nearby.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.mpk_icon_geheimnis_position)
                , (int)w, (int)h, true));
        nearby.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.mpk_icon_schluessel_position)
                , (int)w, (int)h, true));
        nearby.add(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.mpk_icon_werte_position)
                , (int)w, (int)h, true));

    }

    public Bitmap normalIcon(int type)
    {
        return exponat.get(type-1);
    }

    public Bitmap getRecommendedIcon(int type){
        return empfehlung.get(type-1);
    }

    public Bitmap getNearbyIcon(int type)
    {
        return nearby.get(type-1);
    }





}
