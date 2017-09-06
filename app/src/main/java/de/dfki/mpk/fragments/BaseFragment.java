package de.dfki.mpk.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;

/**
 * Created by Olakunmi on 14/08/2017.
 */

public class BaseFragment extends Fragment {
    public String title;
    public int id = -1;
    boolean visible= false;

    public void setSubtitle(String sub)
    {
        ((Home)getActivity()).setSubtitle(sub);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            visible = isVisibleToUser;
            setSubtitle(title);
            ((Home) getActivity()).passButton.setBackgroundColor(ContextCompat.
                    getColor(getActivity(),R.color.torquious_dark));
            ((Home) getActivity()).feedbackButton.setBackgroundColor(ContextCompat.
                    getColor(getActivity(),R.color.torquious_dark));
        }

        setHasOptionsMenu(true);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        if(isVisible()) {
            menu.clear();
            inflater.inflate(R.menu.empty, menu);
        }

    }
}
