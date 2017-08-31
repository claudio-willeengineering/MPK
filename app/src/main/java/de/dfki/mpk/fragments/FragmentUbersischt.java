package de.dfki.mpk.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.dfki.mpk.Home;
import de.dfki.mpk.R;
import de.dfki.mpk.model.Content;
import de.dfki.mpk.model.Exhibits;
import de.dfki.mpk.model.Topic;
import de.dfki.mpk.utils.UtilsHelpers;

/**
 * Created by Olakunmi on 21/08/2017.
 */

public class FragmentUbersischt extends BaseFragment{
    private static FragmentUbersischt currentInstance = null;



    public static FragmentUbersischt createInstance(){
        if(currentInstance == null)
        {
            currentInstance = new FragmentUbersischt();
            currentInstance.title = "Übersicht";
        }

        return currentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ubersischt,container,false);



        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        final ExhibitionAdapter adapter = new ExhibitionAdapter(((Home)getActivity()).getExhibits(),getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        TabLayout layout = v.findViewById(R.id.tabLayout);
        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition())
                {
                    case 0:
                        adapter.setData(((Home) getActivity()).getExhibits());
                        break;
                    case 1:
                        adapter.setData(((Home) getActivity()).getTopics());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





        return v;
    }

    class ExhibitionAdapter<T extends Content> extends RecyclerView.Adapter<ExhibitionViewHolder>{

        public List<UbersichtItem> data = null;
        public Activity activity = null;

        public ExhibitionAdapter(List<T> items, Activity context)
        {
            activity = context;
            setData(items);
        }

        public void setData(List<T> items)
        {
            data = new ArrayList<>();
            String lastLetterStart = "";

            for(Content c : items)
            {
                if(lastLetterStart.compareTo("")==0)
                {
                    lastLetterStart = c.getTitle().charAt(0)+"";
                    data.add(new UbersichtItem(lastLetterStart.toUpperCase()));
                }
                else
                {
                    if(lastLetterStart.compareTo(c.getTitle().charAt(0)+"")!=0)
                    {
                        lastLetterStart = c.getTitle().charAt(0)+"";
                        data.add(new UbersichtItem(lastLetterStart.toUpperCase()));
                    }
                }
                data.add(new UbersichtItem(c));
            }
            notifyDataSetChanged();
        }


        @Override
        public ExhibitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = null;
            switch (viewType)
            {
                case 0:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_letter_header, parent, false);
                    break;
                case 1:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                    break;
            }

            return new ExhibitionViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ExhibitionViewHolder holder, final int position) {
            holder.text.setText(data.get(position).getText());

            if(data.get(position).type != 0)
                holder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentDetails fragmentDetails = FragmentDetails.createInstance();
                        Bundle b = new Bundle();
                        b.putString(FragmentDetails.key,data.get(position).content.getJson());
                        fragmentDetails.setArguments(b);
                        ((Home)getActivity()).switchFragment(fragmentDetails);
                    }
                });

        }
        @Override
        public int getItemViewType(int position) {
            return data.get(position).type;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }



    public static class ExhibitionViewHolder extends RecyclerView.ViewHolder {

        public TextView text;

        public ExhibitionViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }


    class UbersichtItem
    {
        //Header = 0; Text = 1
        private int type;
        private Content content;
        String text;


        public UbersichtItem(Content c)
        {
            content = c;
            type = 1;
            text = content.getTitle();
        }

        public UbersichtItem(String text)
        {
            this.text = text;
            type = 0;
        }

        public String getText() {
            return text;
        }
    }


}
