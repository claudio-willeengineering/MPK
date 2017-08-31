package de.dfki.mpk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;
import de.dfki.mpk.model.Content;
import de.dfki.mpk.model.Exhibits;
import de.dfki.mpk.model.Topic;

import static android.R.attr.content;
import static android.R.attr.key;

/**
 * Created by student on 29.08.17.
 */

public class FragmentDetails extends BaseFragment {
    public static String key = "KEY";
    public static FragmentDetails currentInstance = null;
    Content content = null;
    public static FragmentDetails createInstance()
    {
        if(FragmentFeedback.currentInstance == null)
        {
            currentInstance = new FragmentDetails();
            currentInstance.title = "Details";
        }

        return currentInstance;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser)
        {
            if(content!=null)
            {
                setSubtitle(content.getTitle());
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle b = this.getArguments();
        if (b != null) {
            content = getContent(b);
        }

        View v = inflater.inflate(R.layout.fragment_details,container,false);
        ImageView imv = v.findViewById(R.id.exponatImage);
        TextView tx1 = v.findViewById(R.id.titleText);
        TextView tx2 = v.findViewById(R.id.bodytext);
        LinearLayout linearLayout = v.findViewById(R.id.references);

        setSubtitle(title);

        imv.setImageResource(R.drawable.placeholder);
        tx1.setText(content.getTitle());
        tx2.setText(content.getText());


        List<Content> rContent = new ArrayList<>();
        if(content instanceof Exhibits)
        {
            rContent.addAll(((Home)getActivity()).getExibitions(content.getReference()));
        }
        else if(content instanceof Topic)
        {
            rContent.addAll(((Home) getActivity()).getTopic(content.getReference()));
        }

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
                    ((Home)getContext()).switchFragment(fragmentDetails);
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

        }

        try {
            Content c = new Topic(new JSONObject(b.getString(key,"")));
            return c;
        }
        catch (Exception e){}

        return null;

    }
}
