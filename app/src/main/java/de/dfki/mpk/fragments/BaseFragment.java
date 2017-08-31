package de.dfki.mpk.fragments;

import android.support.v4.app.Fragment;

import de.dfki.mpk.Home;

/**
 * Created by Olakunmi on 14/08/2017.
 */

public class BaseFragment extends Fragment {
    public String title;
    public int id = -1;

    public void setSubtitle(String sub)
    {
        ((Home)getActivity()).setSubtitle(sub);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
            setSubtitle(title);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
