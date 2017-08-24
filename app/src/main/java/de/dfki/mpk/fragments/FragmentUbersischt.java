package de.dfki.mpk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    public JSONObject jsonData = null;
    List<Exhibits> exhibits = new ArrayList<>();
    List<Topic> topics = new ArrayList<>();


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
        jsonData = UtilsHelpers.fromRawToJson(getActivity(),R.raw.content);

        try {
            JSONArray exibs = jsonData.getJSONArray("exhibits");
            JSONArray tops = jsonData.getJSONArray("topics");
            for(int i=0; i<exibs.length(); i++)
            {
                exhibits.add(new Exhibits(exibs.getJSONObject(i)));
            }
            for (int i=0; i<tops.length(); i++)
            {
                topics.add(new Topic(tops.getJSONObject(i)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        exhibits.sort(new Comparator<Exhibits>() {
            @Override
            public int compare(Exhibits to, Exhibits t1) {
                return  (to.getTitle().charAt(0)+"").compareTo(t1.getTitle().charAt(0)+"");
            }
        });

        topics.sort(new Comparator<Topic>() {
            @Override
            public int compare(Topic to, Topic t1) {
                return  (to.getTitle().charAt(0)+"").compareTo(t1.getTitle().charAt(0)+"");
            }
        });


        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        






        return v;
    }

    class ExhibitionAdapter extends RecyclerView.Adapter<ExhibitionViewHolder>{

        List<UbersichtItem> data = null;

        public ExhibitionAdapter(List<Content> items)
        {

            data = new ArrayList<>();
            String lastLetterStart = "";

            for(Content c : items)
            {
                if(lastLetterStart.compareTo("")==0)
                {
                    lastLetterStart = c.getTitle().charAt(0)+"";
                    data.add(new UbersichtItem(lastLetterStart));
                }
                else
                {
                    if(lastLetterStart.compareTo(c.getTitle().charAt(0)+"")!=0)
                    {
                        lastLetterStart = c.getTitle().charAt(0)+"";
                        data.add(new UbersichtItem(lastLetterStart));
                    }
                }
                data.add(new UbersichtItem(c));
            }
        }


        @Override
        public ExhibitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = null;
            switch (viewType)
            {
                case 0:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                    break;
                case 1:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_letter_header, parent, false);
                    break;
            }

            return new ExhibitionViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ExhibitionViewHolder holder, int position) {
            holder.text.setText(data.get(position).getText());
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
