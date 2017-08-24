package de.dfki.mpk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.dfki.mpk.R;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by Olakunmi on 14/08/2017.
 */

public class FragmentPass extends BaseFragment {
    private static FragmentPass currentInstance = null;

    public static FragmentPass createInstance(){
        if(currentInstance == null)
        {
            currentInstance = new FragmentPass();
            currentInstance.title = "Pass";
        }

        return currentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pass,container,false);

        try {
            ((ImageView) v.findViewById(R.id.qrimagebox)).setImageBitmap(UtilsHelpers.TextToImageEncode("Hamburg", getActivity()));
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            UtilsHelpers.showErrorDialog(getActivity(),"Error Occurred","a Write exception occurred");
        }

        return v;
    }
}
