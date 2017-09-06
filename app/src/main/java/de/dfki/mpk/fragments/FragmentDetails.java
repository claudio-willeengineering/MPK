package de.dfki.mpk.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.dfki.mpk.Home;
import de.dfki.mpk.MPKApplication;
import de.dfki.mpk.R;
import de.dfki.mpk.model.Content;
import de.dfki.mpk.model.Exhibits;
import de.dfki.mpk.model.Topic;
import de.dfki.mpk.utils.NetworkUtils;
import de.dfki.mpk.utils.UtilsHelpers;

import static android.R.attr.content;
import static android.R.attr.key;

/**
 * Created by student on 29.08.17.
 */

public class FragmentDetails extends BaseFragment {

    public static String key = "KEY";
    //public static FragmentDetails currentInstance = null;
    Content content = null;
    boolean isVisibleToUser = false;
    public static FragmentDetails createInstance()
    {
        return new FragmentDetails();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        if(isVisibleToUser)
        {

            if(content!=null)
            {
                setSubtitle(content.getTitle());
            }
        }
        setHasOptionsMenu(true);
        //super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle b = this.getArguments();
        if (b != null) {
            content = getContent(b);
        }

        View v = inflater.inflate(R.layout.fragment_details,container,false);


        setHasOptionsMenu(true);


        ImageView imv = v.findViewById(R.id.exponatImage);
        TextView tx1 = v.findViewById(R.id.titleText);
        TextView tx2 = v.findViewById(R.id.bodytext);
        LinearLayout linearLayout = v.findViewById(R.id.references);

        setSubtitle(title);

        imv.setImageResource(R.drawable.placeholder);
        tx1.setText(content.getTitle());
        tx2.setText(content.getText());


        List<Content> rContent = new ArrayList<>();
            rContent.addAll(((Home)getActivity()).getExibitions(content.getReference()));
            rContent.addAll(((Home) getActivity()).getTopic(content.getReference()));

        if(rContent.size()==0)
            (v.findViewById(R.id.seeMoreText)).setVisibility(View.GONE);

        for(Content c: rContent)
        {
            final Content cx = c;
            LinearLayout rlv = (LinearLayout) inflater.inflate(R.layout.fragment_details_reference_item,null);
            ((TextView)rlv.findViewById(R.id.refTitle)).setText(c.getTitle());
            ((TextView)rlv.findViewById(R.id.refBody)).setText(c.getText());
            rlv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentDetails fragmentDetails = FragmentDetails.createInstance();
                    Bundle b = new Bundle();
                    b.putString(FragmentDetails.key,cx.getJson());
                    fragmentDetails.setArguments(b);
                    ((Home)getContext()).switchFragment(fragmentDetails, cx.getTitle());
                }
            });
            linearLayout.addView(rlv);
        }
        return v;
    }

    public Content getContent(Bundle b)
    {

        try {
            Content c =  new Exhibits(new JSONObject(b.getString(key, "")));
            return c;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            Content c = new Topic(new JSONObject(b.getString(key,"")));
            return c;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        if(((Home) getActivity()).amIVisible(this)) {
            menu.clear();
            inflater.inflate(R.menu.detail, menu);
            MenuItem item = menu.getItem(0);
            MPKApplication application = (MPKApplication) getActivity().getApplication();
            try {
                if (application.isLiked(content.getId())) {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.liked, null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(((Home) getActivity()).amIVisible(this)) {
            if (item.getItemId() == R.id.like)
            {
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.liked, null));
                try {
                    ((MPKApplication) getActivity().getApplication()).addtoLikes(content.getId());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = UtilsHelpers.fromRawToJson(getActivity(), R.raw.like);
                            object.put("uid", ((MPKApplication) getActivity().getApplication()).getUserID());
                            object.put("eid", content.getId());
                            object.put("timestamp", System.currentTimeMillis());
                            String resp = NetworkUtils.post("http://uni-data.wearcom.org/submit/mpk_likes/",
                                    object.toString(),
                                    "ec957d68a3c10d99a2455d4163869965");

                            Log.d("LIKES", resp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        }
        return super.onOptionsItemSelected(item);
    }


}
