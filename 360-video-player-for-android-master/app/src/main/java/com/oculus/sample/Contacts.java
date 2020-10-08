package com.oculus.sample;



/**
 * Created by HP on 28-01-2017.
 */

public class Contacts {

    private String url;

    public Contacts(String url){
        this.setItem(url);
    }

    public void setItem(String url) {
        this.url = url;
    }


    public String getItem() {
        return url;
    }
}
