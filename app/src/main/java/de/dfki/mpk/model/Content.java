package de.dfki.mpk.model;

/**
 * Created by Olakunmi on 23/08/2017.
 */

public class Content {
    String id;
    String title;
    String json = "";
    String image;
    String[] reference;
    String text;

    public Content(String data)
    {
        json = data;
    }
    public Content(){}


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getJson() {
        return json;
    }
    public String[] getReference() {
        return reference;
    }


    public String getText(){
        return text;
    }
    public String getImage(){
        return image;
    }


}
