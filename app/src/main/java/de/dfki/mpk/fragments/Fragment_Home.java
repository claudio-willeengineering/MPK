package de.dfki.mpk.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;


public class Fragment_Home extends BaseFragment {

   public static Fragment_Home currentInstance = null;
    public static Fragment_Home createInstance()
    {
        if(Fragment_Home.currentInstance == null)
        {
            currentInstance = new Fragment_Home();
            currentInstance.title = "Hauptmenü";
        }

        return currentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);

        final Activity activity = getActivity();
        (v.findViewById(R.id.austellungButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Home)activity).switchFragment(FragmentFloorPlan.createInstance());
            }
        });

        (v.findViewById(R.id.ubersischtButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Home)activity).switchFragment(FragmentUbersischt.createInstance());
            }
        });

        return v;
    }


}
