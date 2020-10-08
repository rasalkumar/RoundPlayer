package com.oculus.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by Prashant on 23-03-2017.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    Integer[] pics;
    String[] url;
    Context context ;

    public CustomAdapter (Context context, Integer[] pics, String[] url){

        super(context, R.layout.customadapter,url);

        this.pics=pics;
        this.context = context;
        this.url= url;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView =layoutInflater.inflate(R.layout.customadapter,null);
        ImageView imageView =(ImageView)convertView.findViewById(R.id.imageView);
        imageView.setBackgroundResource(pics[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }
}
